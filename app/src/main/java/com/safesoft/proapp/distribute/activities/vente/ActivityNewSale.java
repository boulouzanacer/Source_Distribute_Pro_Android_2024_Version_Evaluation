package com.safesoft.proapp.distribute.activities.vente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DecimalFormat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.vision.barcode.Barcode;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEvent2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.activities.ActivityNewClient;
import com.safesoft.proapp.distribute.activities.ActivityVersement;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanier;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.FragmentListClient;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import com.safesoft.proapp.distribute.R;

import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.safesoft.proapp.distribute.R.id.timbre;

public class ActivityNewSale extends AppCompatActivity {


    ////////////////////////////////////////
    private static final int REQUEST_ACTIVITY_ADDPRODUCT = 5000;
    private static final int REQUEST_ACTIVITY_NEW_CLIENT = 5001;
    private static final int ACCES_FINE_LOCATION = 2;
    private Intent intent_location;
    private Boolean checkPermission = false;

    private TextView client_name;
    private ListViewAdapterPanier PanierAdapter;
    private Button addProduct;
    private TextView client, txv_remise_btn;
    private DATABASE controller;
    private TextView mode_tarif;
    private  ArrayList<PostData_Bon2> final_panier;
    private TextView total_ht, tva, txv_timbre, txv_remise, total_ttc, total_ttc_remise;
    private CheckBox  checkBox_timbre;
    private TableRow tr_item_3, tr_item_5, tr_item_4;
    private  Double val_total_ht = 0.00, val_tva = 0.00, val_timbre = 0.00, val_remise = 0.00, val_total_ttc = 0.00, val_total_ttc_remise = 0.00;

    private EventBus bus;
    private String CODE_DEPOT;
    private PostData_Client client_selected;
    private PostData_Bon1 bon1;


    private ExpandableHeightListView expandableListView;

    private NumberFormat nf;

    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityNewSale.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        bon1 = new PostData_Bon1();
        final_panier = new ArrayList<>();

        //get num bon
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String NUM_BON = Get_Digits_String();
        if(NUM_BON != null) {
            getSupportActionBar().setTitle("Bon de vente N°: " + NUM_BON);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                    .getColor(R.color.black)));
        }

        bon1.num_bon = NUM_BON;

        // get date and time
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formattedDate_Show = df_show.format(c.getTime());
        String formattedDate = df_save.format(c.getTime());

        if(formattedDate_Show != null){
            getSupportActionBar().setSubtitle(formattedDate_Show);
        }
        String currentTime = sdf.format(c.getTime());
        if(currentTime != null){
            getSupportActionBar().setSubtitle(formattedDate_Show + " "  + currentTime);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bon1.date_bon = formattedDate;
        bon1.heure = currentTime;

        initViews();

        bon1.code_depot = CODE_DEPOT;

        // Register as a subscriber
        bus.register(this);

        client_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addProduct.setEnabled(!client_name.getText().toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        checkBox_timbre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(client_selected == null){
                Toast.makeText(ActivityNewSale.this, "You have to select client first", Toast.LENGTH_SHORT).show();
                checkBox_timbre.setChecked(false);
            }else{
                checkBox_timbre.setChecked(isChecked);
                calcule();
                sauvegarder();
            }

        });

    }

    public String Get_Digits_String(){
        String _number = controller.Select_max_num_bon_vente("Bon1");
        while(_number.length() < 6){
            _number = "0" + _number;
        }
        return _number;
    }
    public String Get_Digitss_String(String number, Integer length) {
        String _number = number;
        while (_number.length() < length) {
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }


    @Override
    protected void onStart() {

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }




    @SuppressLint("CutPasteId")
    private void initViews() {

        //  recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RelativeLayout rtvl_client = findViewById(R.id.relativeLayout);
        client = findViewById(R.id.client);
        addProduct = findViewById(R.id.addProduct);
        Button addClient = findViewById(R.id.btn_create);
        Button valideFacture = findViewById(R.id.valide_facture);

        mode_tarif = findViewById(R.id.mode_tarif);
        client_name = findViewById(R.id.client);

        //textview
        total_ht = findViewById(R.id.total_ht);
        tva = findViewById(R.id.tva);
        total_ttc = findViewById(R.id.total_ttc);
        txv_timbre = findViewById(timbre);
        total_ttc_remise = findViewById(R.id.total_ttc_remise);
        TextView observation = findViewById(R.id.observation_value);
        txv_remise_btn = findViewById(R.id.txv_remise_btn);
        txv_remise = findViewById(R.id.txv_remise);

        //checkbox
        checkBox_timbre = findViewById(R.id.checkbox_timbre);

        //TablRow
        tr_item_3 = findViewById(R.id.tr_item_3);
        tr_item_4 = findViewById(R.id.tr_item_4);
        tr_item_5 = findViewById(R.id.tr_item_5);

        String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
        SharedPreferences prefs = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        intent_location = new Intent(this, ServiceLocation.class);
        String PREFS_AUTRE = "ConfigAutre";
        SharedPreferences prefs1 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if(prefs1.getBoolean("GPS_LOCALISATION", false)){
            requestPermission();
            if(checkPermission){
                startService(intent_location);
            }
        }else {
            stopService(intent_location);
        }

    }


    @SuppressLint("NonConstantResourceId")
    public void onClickEvent(View v){
        switch (v.getId()){
            case R.id.relativeLayout:
                showListClient();
                break;
            case R.id.addProduct:
                if(client.getText().length() <1){

                    Crouton.makeText(ActivityNewSale.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }
                Intent intentAddProduct = new Intent(ActivityNewSale.this, ActivityAddProduct.class);
                intentAddProduct.putExtra("MODE_TARIFF", client_selected.mode_tarif);
                intentAddProduct.putExtra("NUM_BON", bon1.num_bon);
                intentAddProduct.putExtra("CODE_DEPOT", CODE_DEPOT);

                startActivityForResult(intentAddProduct, REQUEST_ACTIVITY_ADDPRODUCT);
                break;
            case R.id.btn_create:
                Intent intentAddClient = new Intent(ActivityNewSale.this, ActivityNewClient.class);
                startActivityForResult(intentAddClient, REQUEST_ACTIVITY_NEW_CLIENT);
                break;
            case R.id.valide_facture:
                if(client.getText().length() <1){

                    Crouton.makeText(ActivityNewSale.this, "Vous devez Séléctionner un client", Style.ALERT).show();
                    return;
                }
                if(final_panier.size() <1){
                    Crouton.makeText(ActivityNewSale.this, "Panier Vide! Séléctionner un produit", Style.INFO).show();
                    return;
                }

                  Intent versement_intent = new Intent(ActivityNewSale.this, ActivityVersement.class);
                  versement_intent.putExtra("NUM_BON", bon1.num_bon);
                   versement_intent.putExtra("CODE_DEPOT", bon1.code_depot);
                 versement_intent.putExtra("LATITUDE", bon1.latitude);
                  versement_intent.putExtra("LONGITUDE", bon1.longitude);
                  startActivityForResult(versement_intent, 55555);

                break;
            case R.id.txv_remise_btn:

                // Initialize activity
                Activity activity;

                // define activity of this class//
                activity = ActivityNewSale.this;

                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(activity, val_total_ttc, val_remise);
                break;
        }
    }


    protected void showListClient()
    {
        FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentListClient(); // creating new object
        Bundle args = new Bundle();
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent){

        onClientSelected(clientEvent.getClient());

    }

    protected void onClientSelected(PostData_Client client_s){

        client_selected = client_s;
        client.setText(client_selected.client);

        bon1.code_client = client_selected.code_client;
        bon1.solde_ancien = client_selected.solde_montant;
        bon1.verser = client_selected.verser_montant;



        if(client_selected.mode_tarif != null){
            if(client_selected.mode_tarif.equals("2")){
                // tariff 2
                final BadgeDrawable drawable4 =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                                .badgeColor(0xffCC9999)
                                .text1("TARRIF 2")
                                .build();
                SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
                mode_tarif.setText(spannableString1);

            }else if(client_selected.mode_tarif.equals("3")){
                // tarrif 3
                final BadgeDrawable drawable4 =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                                .badgeColor(0xffCC9999)
                                .text1("TARRIF 3")
                                .build();

                SpannableString spannableString2 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
                mode_tarif.setText(spannableString2);
            }
            else{
                // tariff 1
                final BadgeDrawable drawable4 =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                                .badgeColor(0xffCC9999)
                                .text1("TARRIF 1")
                                .build();
                SpannableString spannableString3 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
                mode_tarif.setText(spannableString3);
            }
        }else{
            // tariff 1
            final BadgeDrawable drawable4 =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                            .badgeColor(0xffCC9999)
                            .text1("TARRIF 1")
                            .build();
            SpannableString spannableString4 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
            mode_tarif.setText(spannableString4);
        }

        if(!controller.Insert_into_bon1("Bon1",bon1)){
            finish();
        }
    }
    @Subscribe
    public void onEvent(CheckedPanierEvent2 panier){

        // initData(panier.getData());
    }

    protected void initData(){


        final_panier = controller.select_bon2_from_database("" +
                "SELECT " +
                "Bon2.RECORDID, " +
                "Bon2.CODE_BARRE, " +
                "Bon2.NUM_BON, " +
                "Bon2.PRODUIT, " +
                "Bon2.QTE, " +
                "Bon2.PV_HT, " +
                "Bon2.TVA, " +
                "Bon2.CODE_DEPOT, " +
                "Bon2.PA_HT, " +
                "Produit.STOCK " +
                "FROM Bon2 " +
                "INNER JOIN " +
                "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                "WHERE Bon2.NUM_BON = '" + bon1.num_bon + "'" );
        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanier(this, R.layout.transfert2_items, final_panier);

        expandableListView = findViewById(R.id.expandable_listview);

        expandableListView.setAdapter(PanierAdapter);

        // This actually does the magic
        expandableListView.setExpanded(true);
        registerForContextMenu(expandableListView);

        calcule();

        sauvegarder();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()== R.id.expandable_listview) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_listv, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete:
                try{
                    // remove stuff here
                    controller.delete_from_bon2(false, final_panier.get(info.position).recordid, bon1.num_bon ,final_panier.get(info.position));
                    final_panier.remove(info.position);
                    //PanierAdapter.RefrechPanier(final_panier);
                    PanierAdapter = new ListViewAdapterPanier(ActivityNewSale.this, R.layout.transfert2_items, final_panier);
                    expandableListView.setAdapter(PanierAdapter);
                    calcule();

                }catch (Exception e){

                    new SweetAlertDialog(ActivityNewSale.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention!")
                            .setContentText("problème lors suppression produits! : " + e.getMessage())
                            .show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    public void calcule(){

        val_total_ht = 0.00;
        val_tva = 0.00;
        val_timbre = 0.00;
       // val_remise = 0.00;
        val_total_ttc = 0.00;
        val_total_ttc_remise = 0.00;

        for(int k = 0; k< final_panier.size(); k++){

            Double total_montant_produit = (Double.parseDouble(final_panier.get(k).p_u) * Double.parseDouble(final_panier.get(k).qte));
            Double montant_tva_produit = total_montant_produit  * ((Double.parseDouble(final_panier.get(k).tva)) / 100);
            val_total_ht = val_total_ht + total_montant_produit;
            val_tva = val_tva + montant_tva_produit;

        }


        if(checkBox_timbre.isChecked()){
            val_total_ttc =  (val_total_ht) +  val_tva;
            if((val_total_ttc * 0.01) >= 2500){
                val_timbre = 2500.00;
            }else{
                //float nnn = (val_total_ttc * (1/100));
                val_timbre = (val_total_ttc * 0.01);
            }
        }else {
            val_timbre = 0.0;
        }

        val_total_ttc =  (val_total_ht) +  val_tva + val_timbre;
        val_total_ttc_remise = val_total_ttc - val_remise;

        final BadgeDrawable drawable1 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xff303F9F)
                .text1(nf.format(val_total_ht))
                .text2(" DA ")
                .build();
        SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));
        total_ht.setText(spannableString1);

        final BadgeDrawable drawable2 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xffE74C3C)
                .text1(nf.format(val_tva))
                .text2(" DA ")
                .build();
        SpannableString spannableString2 = new SpannableString(TextUtils.concat(drawable2.toSpannable()));
        tva.setText(spannableString2);

        final BadgeDrawable drawable3 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xffE74C3C)
                        .text1(nf.format(val_timbre))
                        .text2(" DA")
                        .build();
        SpannableString spannableString3 = new SpannableString(TextUtils.concat(drawable3.toSpannable()));
        txv_timbre.setText(spannableString3);

        final BadgeDrawable drawable4 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xff303F9F)
                .text1(nf.format(val_total_ttc))
                .text2(" DA ")
                .build();
        SpannableString spannableString4 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
        total_ttc.setText(spannableString4);

        final BadgeDrawable drawable5 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xff303F9F)
                .text1(nf.format(val_remise))
                .text2(" DA ")
                .build();
        SpannableString spannableString5 = new SpannableString(TextUtils.concat(drawable5.toSpannable()));
        txv_remise.setText(spannableString5);


        final BadgeDrawable drawable6 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xff303F9F)
                        .text1(nf.format(val_total_ttc_remise))
                        .text2(" DA")
                        .build();
        SpannableString spannableString6 = new SpannableString(TextUtils.concat(drawable6.toSpannable()));
        total_ttc_remise.setText(spannableString6);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bon_vente_commande, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.menu_scan){

            startScan();
        }
        return super.onOptionsItemSelected(item);
    }


    //Versement
    protected void sauvegarder(){

        bon1.code_client = client_selected.code_client;
        bon1.client = client.getText().toString();
        bon1.tot_ttc =  String.valueOf(total_ttc);
        bon1.tot_ttc_remise =  String.valueOf(total_ttc_remise);
        bon1.code_vendeur = "000000";
        bon1.mode_tarif = client_selected.mode_tarif;
        bon1.solde_ancien = client_selected.solde_montant;
        bon1.nbr_p = String.valueOf(final_panier.size());
        bon1.montant_bon = String.valueOf(val_total_ttc_remise);


        if(checkBox_timbre.isChecked()){
            bon1.timbre_ckecked = true;
        }else{
            bon1.timbre_ckecked = false;
        }

        bon1.tot_ht = String.valueOf(val_total_ht);
        bon1.tot_tva = String.valueOf(val_tva);
        bon1.timbre =  String.valueOf(val_timbre);
        bon1.tot_ttc = String.valueOf(val_total_ttc);
        bon1.remise =  String.valueOf(val_remise);
        bon1.tot_ttc_remise = String.valueOf(val_total_ttc_remise);

        //update current bon1
        controller.update_bon1("Bon1",bon1.num_bon, bon1);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 55555){
            finish();
        }else if(requestCode == REQUEST_ACTIVITY_ADDPRODUCT){
            initData();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            checkPermission = false;
        }else{
            checkPermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCES_FINE_LOCATION){
            startService(new Intent(this, ServiceLocation.class));
        }
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();

    }

    @Override
    public void onBackPressed() {
        Sound( R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int resid){
        MediaPlayer mp = MediaPlayer.create(this, resid);
        mp.start();
    }

    @Subscribe
    public void onEvent(LocationEvent event){

        Log.e("TRACKKK", "Recieved location vente : " +  event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

        bon1.latitude = event.getLocationData().getLatitude();
        bon1.longitude = event.getLocationData().getLongitude();
    }

    @Subscribe
    public void onRemiseReceived(RemiseEvent remise){

        val_remise = remise.getRemise();

        final BadgeDrawable drawable5 = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xff303F9F)
                .text1(nf.format(val_remise))
                .text2(" DA ")
                .build();
        SpannableString spannableString5 = new SpannableString(TextUtils.concat(drawable5.toSpannable()));
        txv_remise.setText(spannableString5);


        total_ttc_remise.setText(remise.getApresRemise().toString());

        calcule();
        sauvegarder();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityNewSale.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    Sound(R.raw.bleep);
                    barcodeResult = barcode;
                    // result.setText(barcode.rawValue);
                    //Toast.makeText(ActivityNewSale.this, ""+barcode.rawValue, Toast.LENGTH_SHORT).show();
                    // Do search after barcode scanned
                    //setRecycle(barcode.rawValue, true);

                    PostData_Client client = new PostData_Client();
                    client = controller.select_client_from_database(barcode.rawValue);
                    if(client.client != null){
                        onClientSelected(client);
                    }else {
                        Crouton.makeText(ActivityNewSale.this, "Client non exist sur le terminal", Style.ALERT).show();
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        stopService(intent_location);
        super.onDestroy();
    }


    private Double getValueFromString(String value){
        Pattern p = Pattern.compile("(\\d+)");
        Matcher m = p.matcher(value); //The string you should get from your TextView
        Double number = 0.0;
        if(m.find()) { //If a number in the string is found
            number = Double.parseDouble(m.group()); //Sets number to first found number group
        }
        return number;
    }

}

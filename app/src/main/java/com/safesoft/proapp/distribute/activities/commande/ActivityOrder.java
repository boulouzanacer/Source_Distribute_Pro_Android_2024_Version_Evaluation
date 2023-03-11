package com.safesoft.proapp.distribute.activities.commande;

import static com.safesoft.proapp.distribute.R.id.timbre;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierVente;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEvent2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQteVente;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.fragments.FragmentSelectClient;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.fragments.FragmentValider;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.printing.PrinterVente;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityOrder extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick{



    ////////////////////////////////////////
    private static final int ACCES_FINE_LOCATION = 2;
    private static final int CAMERA_PERMISSION = 5;

    private Intent intent_location;

    private ListViewAdapterPanierVente PanierAdapter;
    private Button btn_select_client,btn_mode_tarif;
    private DATABASE controller;
    private  ArrayList<PostData_Bon2> final_panier;
    private TextView total_ht, tva, txv_timbre, txv_remise, total_ttc, total_ttc_remise;
    private CheckBox  checkBox_timbre;
    private Double val_total_ht = 0.00;
    private Double val_tva = 0.00;
    private Double val_timbre = 0.00;
    private Double val_total_ttc = 0.00;
    private Double val_remise = 0.00;
    private Double val_total_ttc_remise = 0.00;

    private EventBus bus;

    private String NUM_BON;
    private String CODE_DEPOT;
    private PostData_Client client_selected;
    private PostData_Bon1 bon1_temp;
    private String SOURCE;


    private ExpandableHeightListView expandableListView;

    private NumberFormat nf;

    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;
    final String PREFS_AUTRE = "ConfigAutre";

    String TYPE_ACTIVITY = "";
    String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityOrder.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        bon1_temp = new PostData_Bon1();
        final_panier = new ArrayList<>();


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String date_time_sub_title = null;
        String formattedDate = null;


        SharedPreferences prefs = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        initViews();

        //get num bon
        if(getIntent() !=null){
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
        }else {
            Crouton.makeText(ActivityOrder.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        if(TYPE_ACTIVITY.equals("NEW_ORDER")){
            //get num bon
            NUM_BON = Get_Digits_String();
            // get date and time
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String formattedDate_Show = date_format.format(c.getTime());
           // formattedDate = df_save.format(c.getTime());
            String currentTime = sdf.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            bon1_temp.date_bon = formattedDate_Show;
            bon1_temp.heure = currentTime;
            bon1_temp.num_bon = NUM_BON;
            bon1_temp.blocage = "";
            bon1_temp.client = "";
            bon1_temp.code_depot = CODE_DEPOT;


        }else if(TYPE_ACTIVITY.equals("EDIT_ORDER")){
            //get num bon
            if(getIntent() !=null){
                NUM_BON = getIntent().getStringExtra("NUM_BON");
            }

            String querry = "SELECT " +
                    "Bon1_Temp.RECORDID, " +
                    "Bon1_Temp.NUM_BON, " +
                    "Bon1_Temp.DATE_BON, " +
                    "Bon1_Temp.HEURE, " +
                    "Bon1_Temp.MODE_RG, " +
                    "Bon1_Temp.MODE_TARIF, " +

                    "Bon1_Temp.NBR_P, " +
                    "Bon1_Temp.TOT_QTE, " +

                    "Bon1_Temp.TOT_HT, " +
                    "Bon1_Temp.TOT_TVA, " +
                    "Bon1_Temp.TIMBRE, " +
                    "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE AS TOT_TTC, " +
                    "Bon1_Temp.REMISE, " +
                    "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE AS MONTANT_BON, " +

                    "Bon1_Temp.ANCIEN_SOLDE, " +
                    "Bon1_Temp.VERSER, " +
                    "Bon1_Temp.ANCIEN_SOLDE + (Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE) - Bon1_Temp.VERSER AS RESTE, " +

                    "Bon1_Temp.CODE_CLIENT, " +
                    "Client.CLIENT, " +
                    "Client.ADRESSE, " +
                    "Client.TEL, " +
                    "Client.RC, " +
                    "Client.IFISCAL, " +
                    "Client.AI, " +
                    "Client.NIS, " +

                    "Client.LATITUDE as LATITUDE_CLIENT, " +
                    "Client.LONGITUDE as LONGITUDE_CLIENT, " +

                    "Client.SOLDE AS SOLDE_CLIENT, " +
                    "Client.CREDIT_LIMIT, " +

                    "Bon1_Temp.LATITUDE, " +
                    "Bon1_Temp.LONGITUDE, " +

                    "Bon1_Temp.CODE_DEPOT, " +
                    "Bon1_Temp.CODE_VENDEUR, " +
                    "Bon1_Temp.EXPORTATION, " +
                    "Bon1_Temp.BLOCAGE " +
                    "FROM Bon1_Temp " +
                    "LEFT JOIN Client ON Bon1_Temp.CODE_CLIENT = Client.CODE_CLIENT " +
                    "WHERE Bon1_Temp.NUM_BON ='"+NUM_BON+"'";
            ///////////////////////////////////
            bon1_temp = controller.select_bon1_from_database2(querry);


            final_panier =  controller.select_bon2_from_database("" +
                    "SELECT " +
                    "Bon2_Temp.RECORDID, " +
                    "Bon2_Temp.CODE_BARRE, " +
                    "Bon2_Temp.NUM_BON, " +
                    "Bon2_Temp.PRODUIT, " +
                    "Bon2_Temp.NBRE_COLIS, " +
                    "Bon2_Temp.COLISSAGE, " +
                    "Bon2_Temp.QTE, " +
                    "Bon2_Temp.QTE_GRAT, " +
                    "Bon2_Temp.PV_HT, " +
                    "Bon2_Temp.TVA, " +
                    "Bon2_Temp.CODE_DEPOT, " +
                    "Bon2_Temp.PA_HT, " +
                    "Bon2_Temp.DESTOCK_TYPE, " +
                    "Bon2_Temp.DESTOCK_CODE_BARRE, " +
                    "Bon2_Temp.DESTOCK_QTE, " +
                    "Produit.STOCK " +
                    "FROM Bon2_Temp " +
                    "LEFT JOIN Produit ON (Bon2_Temp.CODE_BARRE = Produit.CODE_BARRE) " +
                    "WHERE Bon2_Temp.NUM_BON = '" + NUM_BON+ "'" );

            //private String formattedDate;
            date_time_sub_title = bon1_temp.date_bon + " " + bon1_temp.heure;

            client_selected = controller.select_client_from_database(bon1_temp.code_client);
            onClientSelected(client_selected);

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier);

            expandableListView = findViewById(R.id.expandable_listview);

            expandableListView.setAdapter(PanierAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);

            registerForContextMenu(expandableListView);

            val_timbre = bon1_temp.timbre;
            val_remise = bon1_temp.remise;

            checkBox_timbre.setChecked(false);
            if(val_timbre != 0) checkBox_timbre.setChecked(true);

            calcule();


        }else {
            Crouton.makeText(ActivityOrder.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }




        if(NUM_BON != null) {
            getSupportActionBar().setTitle("Bon de commande N°: " + NUM_BON);
        }
        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();



        // Register as a subscriber
        bus.register(this);


        checkBox_timbre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(client_selected == null){
                Toast.makeText(ActivityOrder.this, "Veuilez sélectionner un Client", Toast.LENGTH_SHORT).show();
                checkBox_timbre.setChecked(false);
            }else{
                checkBox_timbre.setChecked(isChecked);
                calcule();
                sauvegarder();
            }

        });

    }

    public String Get_Digits_String(){
        String _number = controller.Select_max_num_bon_vente("Bon1_Temp");
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
        return _number;
    }




    @SuppressLint({"CutPasteId", "WrongViewCast"})
    private void initViews() {

        btn_select_client = findViewById(R.id.btn_select_client);
        btn_mode_tarif = findViewById(R.id.btn_mode_tarif);

        //textview
        total_ht = findViewById(R.id.total_ht);
        tva = findViewById(R.id.tva);
        total_ttc = findViewById(R.id.total_ttc);
        txv_timbre = findViewById(timbre);
        total_ttc_remise = findViewById(R.id.total_ttc_remise);
        //TextView observation = findViewById(R.id.observation_value);
        txv_remise = findViewById(R.id.txv_remise);

        //checkbox
        checkBox_timbre = findViewById(R.id.checkbox_timbre);

        //TablRow
        TableRow tr_total_ht = findViewById(R.id.tr_total_ht);
        TableRow tr_total_tva = findViewById(R.id.tr_total_tva);
        TableRow tr_total_timbre = findViewById(R.id.tr_total_timbre);


        prefs = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if(prefs.getBoolean("AFFICHAGE_HT", false)){
            tr_total_ht.setVisibility(View.VISIBLE);
            tr_total_tva.setVisibility(View.VISIBLE);
            tr_total_timbre.setVisibility(View.VISIBLE);
        }else{
            tr_total_ht.setVisibility(View.GONE);
            tr_total_tva.setVisibility(View.GONE);
            tr_total_timbre.setVisibility(View.GONE);
        }

        intent_location = new Intent(this, ServiceLocation.class);

        SharedPreferences prefs1 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if(prefs1.getBoolean("GPS_LOCALISATION", false)){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            }else{
                startService(intent_location);
            }
        }else {
            stopService(intent_location);
        }


        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

    }


    @SuppressLint("NonConstantResourceId")
    public void onClickEvent(View v) throws UnsupportedEncodingException, ParseException {
        switch (v.getId()){
            case R.id.btn_select_client:
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                showListClient();
                break;

            case R.id.btn_mode_tarif:
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                if( bon1_temp.client.length() <1){

                    Crouton.makeText(ActivityOrder.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }

                if(client_selected.mode_tarif.equals("0")){

                    if(btn_mode_tarif.getText().toString().equals("Tarif 1")){
                        bon1_temp.mode_tarif = "2";
                        btn_mode_tarif.setText("Tarif 2");
                    }else if(btn_mode_tarif.getText().toString().equals("Tarif 2")){
                        bon1_temp.mode_tarif = "3";
                        btn_mode_tarif.setText("Tarif 3");
                    }else if(btn_mode_tarif.getText().toString().equals("Tarif 3")) {
                        bon1_temp.mode_tarif = "1";
                        btn_mode_tarif.setText("Tarif 1");
                    }
                }else {
                    return;
                }

                break;
            case R.id.addProduct:
                if(bon1_temp.blocage.equals("F")){
                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
                }
                if( bon1_temp.client.length() <1){

                    Crouton.makeText(ActivityOrder.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }

                // Initialize activity
                Activity activity;
                // define activity of this class//
                activity = ActivityOrder.this;

                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(),  bon1_temp.mode_tarif, "ORDER");

                break;

            case R.id.valide_facture:

                if( bon1_temp.client.length() <1){

                    Crouton.makeText(ActivityOrder.this, "Vous devez Séléctionner un client", Style.ALERT).show();
                    return;
                }
                if(final_panier.size() <1){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                //FragmentValider fragmentvalider = new FragmentValider();
                //fragmentvalider.showDialogbox(ActivityOrder.this, bon1_temp.solde_ancien, bon1_temp.montant_bon, bon1_temp.verser);
                onVersementReceived(null);

                break;
            case R.id.txv_remise_btn:
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                Activity mactivity;

                // define activity of this class//
                mactivity = ActivityOrder.this;

                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);
                break;
            case R.id.btn_scan_produit:
                if (ContextCompat.checkSelfPermission(ActivityOrder.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityOrder.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                }else{
                    if(bon1_temp.blocage.equals("F")){
                        new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon est déja validé")
                                .show();
                        return;
                    }
                    if( bon1_temp.client.length() < 1){

                        Crouton.makeText(ActivityOrder.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                        return;
                    }

                    startScanProduct();
                }
                break;
            case R.id.btn_mofifier_bon:
            if(!bon1_temp.blocage.equals("F")){
                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas encore validé")
                        .show();
                return;

            } else  {
                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Modification")
                        .setContentText("Voulez-vous vraiment Modifier ce Bon ?")
                        .setCancelText("Non")
                        .setConfirmText("Oui")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try{
                                if (controller.modifier_bon1_sql("Bon1_Temp",bon1_temp.num_bon, bon1_temp) ) {
                                    bon1_temp.blocage = "M";
                                    validate_theme();
                                }
                            }catch (Exception e){

                                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors de Modification de Bon : " + e.getMessage())
                                        .show();
                            }
                            sDialog.dismiss();
                        }).show();
            }
            break;
            case R.id.btn_imp_bon:
                if(!bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon n'est pas encore validé")
                            .show();
                    return;
                }
                Activity bactivity;
                bactivity = ActivityOrder.this;

                PrinterVente printer = new PrinterVente();
                printer.start_print_order_bon(bactivity, final_panier, bon1_temp);

                break;

        }
    }



    protected void showListClient()
    {
        // Initialize activity
        Activity activity;

        // define activity of this class//
        activity = ActivityOrder.this;

        FragmentSelectClient fragmentSelectClient = new FragmentSelectClient();
        fragmentSelectClient.showDialogbox(activity, getBaseContext());

    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent){

        onClientSelected(clientEvent.getClient());

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


    protected void onClientSelected(PostData_Client client_s){

        client_selected = client_s;
        bon1_temp.client = client_s.client;
        btn_select_client.setText(client_selected.client);

        bon1_temp.code_client = client_selected.code_client;
        bon1_temp.client = client_selected.client;
        bon1_temp.adresse = client_selected.adresse;
        bon1_temp.tel = client_selected.tel;
        bon1_temp.rc = client_selected.rc;
        bon1_temp.ifiscal = client_selected.ifiscal;
        bon1_temp.ai = client_selected.ai;
        bon1_temp.nis = client_selected.nis;

        if(TYPE_ACTIVITY.equals("NEW_ORDER")){

            bon1_temp.solde_ancien = client_selected.solde_montant;  // Modifier le 03/02/2023

            bon1_temp.mode_tarif = "1";
            btn_mode_tarif.setText("Tarif 1");

            if(client_selected.mode_tarif != null){
                if(client_selected.mode_tarif.equals("2")){
                    // tarif 2
                    btn_mode_tarif.setText("Tarif 2");
                    bon1_temp.mode_tarif = "2";

                }else if(client_selected.mode_tarif.equals("3")){
                    // tarif 3
                    btn_mode_tarif.setText("Tarif 3");
                    bon1_temp.mode_tarif = "3";
                }
                else{
                    // tarif 1
                    btn_mode_tarif.setText("Tarif 1");
                    bon1_temp.mode_tarif = "1";
                }
            }else{
                // tarif 1
                btn_mode_tarif.setText("Tarif 1");
                bon1_temp.mode_tarif = "1";
            }
        }else {

            if(bon1_temp.mode_tarif.equals("2")){
                // tarif 2
                btn_mode_tarif.setText("Tarif 2");

            }else if(bon1_temp.mode_tarif.equals("3")){
                // tarif 3
                btn_mode_tarif.setText("Tarif 3");
            }
            else{
                // tarif 1
                btn_mode_tarif.setText("Tarif 1");
            }
        }

        if(!controller.Insert_into_bon1("Bon1_Temp",bon1_temp)){
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
                "Bon2_Temp.RECORDID, " +
                "Bon2_Temp.CODE_BARRE, " +
                "Bon2_Temp.NUM_BON, " +
                "Bon2_Temp.PRODUIT, " +
                "Bon2_Temp.NBRE_COLIS, " +
                "Bon2_Temp.COLISSAGE, " +
                "Bon2_Temp.QTE, " +
                "Bon2_Temp.QTE_GRAT, " +
                "Bon2_Temp.PV_HT, " +
                "Bon2_Temp.TVA, " +
                "Bon2_Temp.CODE_DEPOT, " +
                "Bon2_Temp.PA_HT, " +
                "Bon2_Temp.DESTOCK_TYPE, " +
                "Bon2_Temp.DESTOCK_CODE_BARRE, " +
                "Bon2_Temp.DESTOCK_QTE, " +
                "Produit.STOCK " +
                "FROM Bon2_Temp " +
                "LEFT JOIN Produit ON (Bon2_Temp.CODE_BARRE = Produit.CODE_BARRE) " +
                "WHERE Bon2_Temp.NUM_BON = '" + bon1_temp.num_bon + "'" );

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier);

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
            case R.id.delete_produit:
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Suppression")
                        .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                        .setCancelText("Anuuler")
                        .setConfirmText("Supprimer")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try{
                                SOURCE = "BON2_TEMP_DELETE";
                                controller.delete_from_bon2("Bon2_Temp", final_panier.get(info.position).recordid, bon1_temp.num_bon ,final_panier.get(info.position));
                                initData();
                                //PanierAdapter.RefrechPanier(final_panier);
                                PanierAdapter = new ListViewAdapterPanierVente(ActivityOrder.this, R.layout.transfert2_items, final_panier);
                                expandableListView.setAdapter(PanierAdapter);

                            }catch (Exception e){

                                new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors suppression produits! : " + e.getMessage())
                                        .show();
                            }

                            sDialog.dismiss();
                        }).show();
                return true;

            case R.id.edit_produit:
                if(bon1_temp.blocage.equals("F")){
                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true ;
                }
                try{
                    SOURCE = "BON2_TEMP_EDIT";
                    Activity activity;
                    activity = ActivityOrder.this;
                    FragmentQteVente fragmentqte = new FragmentQteVente();
                    fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position) , final_panier.get(info.position).p_u * (1 + (final_panier.get(info.position).tva/ 100)));

                }catch (Exception e){

                    new SweetAlertDialog(ActivityOrder.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Attention!")
                            .setContentText("Error : " + e.getMessage())
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
        //val_timbre = 0.00;
       // val_remise = 0.00;
        val_total_ttc = 0.00;
        val_total_ttc_remise = 0.00;

        for(int k = 0; k< final_panier.size(); k++){

            Double total_montant_produit = final_panier.get(k).p_u * final_panier.get(k).qte;
            Double montant_tva_produit = total_montant_produit  * ((final_panier.get(k).tva) / 100);
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
       // inflater.inflate(R.menu.menu_bon_vente_commande, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    //Versement
    protected void sauvegarder(){

        bon1_temp.code_client = client_selected.code_client;
        bon1_temp.client = client_selected.code_client;
        bon1_temp.code_vendeur = "000000";
        //bon1_temp.mode_tarif = client_selected.mode_tarif;
        //bon1_temp.solde_ancien = client_selected.solde_montant;
        bon1_temp.nbr_p = final_panier.size();


        bon1_temp.tot_ht = val_total_ht;
        bon1_temp.tot_tva = val_tva;
        bon1_temp.timbre =  val_timbre;
        bon1_temp.tot_ttc = val_total_ttc;
        bon1_temp.remise =  val_remise;
        bon1_temp.montant_bon = val_total_ttc_remise;
        //update current bon1
        controller.update_bon1("Bon1_Temp",bon1_temp.num_bon, bon1_temp);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ACCES_FINE_LOCATION){
            startService(new Intent(this, ServiceLocation.class));
        }
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

        bon1_temp.latitude = event.getLocationData().getLatitude();
        bon1_temp.longitude = event.getLocationData().getLongitude();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }


    private void startScanProduct() {
        /**
         * Build a new MaterialBarcodeScanner
         */

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityOrder.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        // Sound( R.raw.bleep);
                       // setRecycle(barcode.rawValue, true);
                        selectProductFromScan(barcode.rawValue);
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


    @Override
    public void onClick(View v, int position, PostData_Produit item, String SOURCE_ACTIVITY) {

        PostData_Bon2 bon2_temp = new PostData_Bon2();
        bon2_temp.produit = item.produit;
        bon2_temp.codebarre = item.code_barre;
        bon2_temp.stock_produit = item.stock;
        bon2_temp.destock_type = item.destock_type;
        bon2_temp.destock_code_barre = item.destock_code_barre;
        bon2_temp.destock_qte = item.destock_qte;
        bon2_temp.pa_ht = item.pa_ht;
        bon2_temp.tva = item.tva;
        bon2_temp.colissage = item.colissage;
        if(bon1_temp.mode_tarif.equals("3")){
            bon2_temp.p_u = item.pv3_ht;
        }else if(bon1_temp.mode_tarif.equals("2")){
            bon2_temp.p_u = item.pv2_ht;
        } else
            bon2_temp.p_u = item.pv1_ht;

        bon2_temp.num_bon = NUM_BON;
        bon2_temp.code_depot = CODE_DEPOT;
        SOURCE = "BON2_TEMP_INSERT";
        Activity activity = ActivityOrder.this;
        FragmentQteVente fragmentqte = new FragmentQteVente();
        fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(),  bon2_temp , bon2_temp.p_u * (1 + (bon2_temp.tva / 100)));

    }

    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventBon2 item_panier){

           try {
               if(SOURCE.equals("BON2_TEMP_INSERT")){
                   controller.Insert_into_bon2("Bon2_Temp", NUM_BON, CODE_DEPOT,  item_panier.getData());
               }else if(SOURCE.equals("BON2_TEMP_EDIT")){
                   controller.Update_into_bon2("Bon2_Temp", NUM_BON, item_panier.getData(), item_panier.getQteOld(),item_panier.getGratuitOld());
               }

               initData();

           }catch (Exception e){
               Crouton.makeText(ActivityOrder.this, "Erreur in produit" + e.getMessage(), Style.ALERT).show();
           }

    }


    @Subscribe
    public void onVersementReceived(ValidateFactureEvent versement){

        bon1_temp.verser = 0.0;

       /* if (bon1_temp.verser != 0 ) {
            bon1_temp.mode_rg = "ESPECE";
        } else{
            bon1_temp.mode_rg = "A TERME";
        }*/

       // bon1_temp.reste = bon1_temp.solde_ancien + (bon1_temp.tot_ht + bon1_temp.tot_tva + bon1_temp.timbre - bon1_temp.remise) - bon1_temp.verser;

        if (controller.validate_bon1_sql("Bon1_Temp", bon1_temp.num_bon, bon1_temp)) {
            bon1_temp.blocage = "F";
        }

        validate_theme();

    }
    public void validate_theme(){
        if (bon1_temp.blocage.equals("F")) {
            //findViewById(R.id.client).setBackgroundColor(Color.LTGRAY);
            //findViewById(R.id.LayoutButton).setBackgroundColor(Color.LTGRAY);
            findViewById(R.id.Linear_Layout_Header_bon).setBackgroundColor(Color.LTGRAY);
            findViewById(R.id.tl).setBackgroundColor(Color.LTGRAY);
            getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);

        } else {
            //findViewById(R.id.client).setBackgroundColor(Color.WHITE);
            //findViewById(R.id.LayoutButton).setBackgroundColor(Color.WHITE);
            findViewById(R.id.Linear_Layout_Header_bon).setBackgroundColor(Color.WHITE);
            findViewById(R.id.tl).setBackgroundColor(Color.WHITE);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        }
    }

    private void selectProductFromScan(String resultscan){
        ArrayList<PostData_Produit> produits;
        PostData_Bon2 bon2_temp = new PostData_Bon2();

            String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                    "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
            produits = controller.select_produits_from_database(querry);

            if(produits.size() == 0){
                String querry1 = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '"+resultscan+"'";
                String code_barre = controller.select_codebarre_from_database(querry1);

                String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                produits = controller.select_produits_from_database(querry2);
            }

        if(produits.size() == 1){
            bon2_temp.num_bon = NUM_BON;
            bon2_temp.code_depot = CODE_DEPOT;
            bon2_temp.produit = produits.get(0).produit;
            bon2_temp.codebarre = produits.get(0).code_barre;
            bon2_temp.stock_produit = produits.get(0).stock;
            bon2_temp.destock_type = produits.get(0).destock_type;
            bon2_temp.destock_code_barre = produits.get(0).destock_code_barre;
            bon2_temp.destock_qte = produits.get(0).destock_qte;
            bon2_temp.pa_ht = produits.get(0).pa_ht;
            bon2_temp.tva = produits.get(0).tva;
            bon2_temp.colissage = produits.get(0).colissage;
            if(bon1_temp.mode_tarif.equals("3")){
                bon2_temp.p_u = produits.get(0).pv3_ht;
            }else if(bon1_temp.mode_tarif.equals("2")){
                bon2_temp.p_u = produits.get(0).pv2_ht;
            } else
                bon2_temp.p_u = produits.get(0).pv1_ht;


            SOURCE = "BON2_TEMP_INSERT";
            Activity activity = ActivityOrder.this;
            FragmentQteVente fragmentqte = new FragmentQteVente();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(),  bon2_temp , bon2_temp.p_u * (1 + (bon2_temp.tva / 100)));

        }else{
            Crouton.makeText(ActivityOrder.this, "Produit introuvable !", Style.ALERT).show();
        }
    }
}

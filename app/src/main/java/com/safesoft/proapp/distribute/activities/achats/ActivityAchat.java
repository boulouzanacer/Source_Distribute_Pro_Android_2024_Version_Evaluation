package com.safesoft.proapp.distribute.activities.achats;

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
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanier;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.eventsClasses.TimbreEvent;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQte;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.fragments.FragmentSelectFournisseur;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.fragments.FragmentTimbre;
import com.safesoft.proapp.distribute.fragments.FragmentValideBon;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.printing.Printing;

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

public class ActivityAchat extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick{

    ////////////////////////////////////////
    private static final int ACCES_FINE_LOCATION = 2;
    private static final int CAMERA_PERMISSION = 5;
    private Intent intent_location;
    private ListViewAdapterPanier PanierAdapter;
    private Button btn_select_fournisseur;
    private DATABASE controller;
    private  ArrayList<PostData_Bon2> final_panier;
    private TextView total_ht, tva, txv_timbre, txv_remise, total_ttc, total_ttc_remise;
    private double val_total_ht = 0.00;
    private double val_tva = 0.00;
    private double val_timbre = 0.00;
    private double val_total_ttc = 0.00;
    private double val_remise = 0.00;
    private double val_total_ttc_remise = 0.00;
    private EventBus bus;
    private String NUM_BON;
    private String CODE_DEPOT;
    private PostData_Fournisseur fournisseur_selected;
    private PostData_Achat1 achat1;
    private String SOURCE;
    private ExpandableHeightListView expandableListView;
    private NumberFormat nf;
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;
    final String PREFS = "ALL_PREFS";
    String TYPE_ACTIVITY = "";
    String SOURCE_EXPORT = "";
    String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
    SharedPreferences prefs;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format;
    @SuppressLint("SimpleDateFormat") SimpleDateFormat heure_format;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achat);

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityAchat.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        achat1 = new PostData_Achat1();
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
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        }else {
            Crouton.makeText(ActivityAchat.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        date_format = new SimpleDateFormat("dd/MM/yyyy");
        heure_format = new SimpleDateFormat("HH:mm:ss");

        if(TYPE_ACTIVITY.equals("NEW_ACHAT")){
            //get num bon
            String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM ACHAT1 WHERE NUM_BON IS NOT NULL";
            NUM_BON = controller.select_max_num_bon(selectQuery);
            // get date and time
            Calendar c = Calendar.getInstance();
            String formattedDate_Show = date_format.format(c.getTime());
           // formattedDate = df_save.format(c.getTime());
            String currentTime = heure_format.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            achat1.date_bon = formattedDate_Show;
            achat1.heure = currentTime;
            achat1.num_bon = NUM_BON;
            achat1.blocage = "";
            achat1.fournis = "";
            achat1.code_depot = CODE_DEPOT;


        }else if(TYPE_ACTIVITY.equals("EDIT_ACHAT")){
            //get num bon
            if(getIntent() !=null){
                NUM_BON = getIntent().getStringExtra("NUM_BON");
            }

            String querry = "SELECT " +
                    "ACHAT1.RECORDID, " +
                    "ACHAT1.NUM_BON, " +
                    "ACHAT1.CODE_FRS, " +
                    "ACHAT1.DATE_BON, " +
                    "ACHAT1.HEURE, " +
                    "ACHAT1.NBR_P, " +
                    "ACHAT1.TOT_QTE, " +
                    "ACHAT1.EXPORTATION, " +
                    "ACHAT1.BLOCAGE, " +
                    "ACHAT1.CODE_DEPOT, " +

                    "ACHAT1.NBR_P, " +
                    "ACHAT1.TOT_QTE, " +

                    "ACHAT1.TOT_HT, " +
                    "ACHAT1.TOT_TVA, " +
                    "ACHAT1.TIMBRE, " +
                    "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE AS TOT_TTC, " +
                    "ACHAT1.REMISE, " +
                    "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE AS MONTANT_BON, " +

                    "ACHAT1.ANCIEN_SOLDE, " +
                    "ACHAT1.VERSER, " +
                    "ACHAT1.ANCIEN_SOLDE + (ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE) - ACHAT1.VERSER AS RESTE, " +

                    "FOURNIS.FOURNIS, " +
                    "FOURNIS.ADRESSE, " +
                    "FOURNIS.TEL " +
                    "FROM ACHAT1 " +
                    "LEFT JOIN FOURNIS ON (ACHAT1.CODE_FRS = FOURNIS.CODE_FRS) " +
                    " WHERE ACHAT1.NUM_BON ='"+ NUM_BON +"'";
            ///////////////////////////////////
            achat1 = controller.select_one_acha1_from_database(querry);


            final_panier =  controller.select_all_achat2_from_database("" +
                    "SELECT " +
                    "ACHAT2.RECORDID, " +
                    "ACHAT2.CODE_BARRE, " +
                    "ACHAT2.NUM_BON, " +
                    "ACHAT2.PRODUIT, " +
                    "ACHAT2.NBRE_COLIS, " +
                    "ACHAT2.COLISSAGE, " +
                    "ACHAT2.QTE, " +
                    "ACHAT2.QTE_GRAT, " +
                    "ACHAT2.PU, " +
                    "ACHAT2.TVA, " +
                    "ACHAT2.CODE_DEPOT, " +
                    "ACHAT2.QTE_GRAT, " +
                    "PRODUIT.STOCK " +
                    "FROM ACHAT2 " +
                    "LEFT JOIN PRODUIT ON (ACHAT2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE ACHAT2.NUM_BON = '" + NUM_BON + "'" );


            //private String formattedDate;
            date_time_sub_title = achat1.date_bon + " " + achat1.heure;

            fournisseur_selected = controller.select_fournisseur_from_database(achat1.code_frs);
            onFournisseurSelected(fournisseur_selected);

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanier(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

            expandableListView = findViewById(R.id.expandable_listview);

            expandableListView.setAdapter(PanierAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);

            registerForContextMenu(expandableListView);

            val_timbre = achat1.timbre;
            val_remise = achat1.remise;


            calcule();

        }else {
            Crouton.makeText(ActivityAchat.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }


        if(NUM_BON != null) {
            getSupportActionBar().setTitle("Bon d'achat N°: " + NUM_BON);
        }
        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();


        // Register as a subscriber
        bus.register(this);

    }

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    private void initViews() {

        btn_select_fournisseur = findViewById(R.id.btn_select_fournisseur);

        //textview
        total_ht = findViewById(R.id.total_ht);
        tva = findViewById(R.id.tva);
        total_ttc = findViewById(R.id.total_ttc);
        txv_timbre = findViewById(timbre);
        total_ttc_remise = findViewById(R.id.total_ttc_remise);
        //TextView observation = findViewById(R.id.observation_value);
        txv_remise = findViewById(R.id.txv_remise);
        TableRow tbr_remise = (TableRow) findViewById(R.id.tbr_remise);

        //TablRow
        TableRow tr_total_ht = findViewById(R.id.tr_total_ht);
        TableRow tr_total_tva = findViewById(R.id.tr_total_tva);
        TableRow tr_total_timbre = findViewById(R.id.tr_total_timbre);


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(prefs.getBoolean("AFFICHAGE_HT", false)){
            tr_total_ht.setVisibility(View.VISIBLE);
            tr_total_tva.setVisibility(View.VISIBLE);
            tr_total_timbre.setVisibility(View.VISIBLE);
        }else{
            tr_total_ht.setVisibility(View.GONE);
            tr_total_tva.setVisibility(View.GONE);
            tr_total_timbre.setVisibility(View.GONE);
        }


        if(prefs.getBoolean("AFFICHAGE_REMISE", true)){
            tbr_remise.setVisibility(View.VISIBLE);
        }else{
            tbr_remise.setVisibility(View.GONE);
        }

        intent_location = new Intent(this, ServiceLocation.class);

        SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
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
            case R.id.btn_select_fournisseur:
                if(achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                showListFournisseur();
                break;
            case R.id.addProduct:
                if(achat1.blocage.equals("F")){
                new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
                }
                if( achat1.fournis.length() <1){

                    Crouton.makeText(ActivityAchat.this, "Vous devez Séléctionner un fournisseur tout d'abord", Style.ALERT).show();
                    return;
                }

                // Initialize activity
                Activity activity;
                activity = ActivityAchat.this;
                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(),  "0", "ACHAT");

                break;

            case R.id.valide_facture:
                if( final_panier.size() <1){
                    Crouton.makeText(ActivityAchat.this, "Votre panier est vide !", Style.ALERT).show();
                    return;
                }
                if(achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                FragmentValideBon fragmentvalider = new FragmentValideBon();
                fragmentvalider.showDialogbox(ActivityAchat.this, achat1.solde_ancien, achat1.montant_bon, achat1.verser);

                break;

            case R.id.txv_remise_btn:
                if(achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                Activity mactivity;

                // define activity of this class//
                mactivity = ActivityAchat.this;

                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);
                break;

            case R.id.txv_timbre_btn:
                if(achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                Activity tactivity;
                tactivity = ActivityAchat.this;
                FragmentTimbre fragmentTimbre = new FragmentTimbre();
                fragmentTimbre.showDialogbox(tactivity, val_timbre);

                break;
            case R.id.btn_scan_produit:
                if (ContextCompat.checkSelfPermission(ActivityAchat.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityAchat.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                }else{
                    if(achat1.blocage.equals("F")){
                        new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon est déja validé")
                                .show();
                        return;
                    }
                    if( achat1.fournis.length() <1){

                        Crouton.makeText(ActivityAchat.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                        return;
                    }
                    startScanProduct();
                }
                break;
            case R.id.btn_mofifier_bon:

                if(!achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon n'est pas encore validé")
                            .show();
                    return;
                }
                
                if(prefs.getBoolean("AUTORISE_MODIFY_BON", true)){
                    if(!SOURCE_EXPORT.equals("EXPORTED")){

                        new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Modification")
                                .setContentText("Voulez-vous vraiment Modifier ce Bon ?")
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    try{

                                        if (controller.modifier_achat1_sql("ACHAT1", achat1) ) {
                                            achat1.blocage = "M";
                                            validate_theme();
                                        }
                                    }catch (Exception e){
                                        new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Attention!")
                                                .setContentText("problème lors de Modification de Bon : " + e.getMessage())
                                                .show();
                                    }
                                    sDialog.dismiss();
                                }).show();
                    }else {
                        new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon est déja exporté")
                                .show();
                    }
                }else{
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention!!")
                            .setContentText("Vous n'avez pas l'autorisation de modifier, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                            .show();                 }


            break;
            case R.id.btn_imp_bon:
                if(!achat1.blocage.equals("F")){
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon n'est pas encore validé")
                            .show();
                    return;
                }

                Activity bactivity;
                bactivity = ActivityAchat.this;

                Printing printer = new Printing();
                printer.start_print_bon(bactivity, "ACHAT", final_panier, null, achat1);

                break;


        }
    }

    protected void showListFournisseur()
    {
        // Initialize activity
        Activity activity;
        // define activity of this class//
        activity = ActivityAchat.this;
        FragmentSelectFournisseur fragmentSelectFournisseur = new FragmentSelectFournisseur();
        fragmentSelectFournisseur.showDialogbox(activity, getBaseContext());

    }

    @Subscribe
    public void onFournisseurSelected(SelectedFournisseurEvent fournisseurEvent){
        onFournisseurSelected(fournisseurEvent.getFournisseur());
    }

    @Subscribe
    public void onRemiseReceived(RemiseEvent remise){

        val_remise = remise.getRemise();
        calcule();
        sauvegarder();
    }

    @Subscribe
    public void onTimbreReceived(TimbreEvent timbre){
        val_timbre = timbre.getTimbre();
        calcule();
        sauvegarder();
    }
    protected void onFournisseurSelected(PostData_Fournisseur fournisseur_s){

        fournisseur_selected = fournisseur_s;
        achat1.fournis = fournisseur_s.fournis;
        btn_select_fournisseur.setText(fournisseur_selected.fournis);

        achat1.code_frs = fournisseur_selected.code_frs;
        achat1.fournis = fournisseur_selected.fournis;
        achat1.tel = fournisseur_selected.tel;
        achat1.adresse = fournisseur_selected.adresse;

        if(!controller.insert_into_achat1("ACHAT1", achat1)){
            finish();
        }
    }


    protected void initData(){
        final_panier = controller.select_all_achat2_from_database("" +
                "SELECT " +
                "ACHAT2.RECORDID, " +
                "ACHAT2.CODE_BARRE, " +
                "ACHAT2.NUM_BON, " +
                "ACHAT2.PRODUIT, " +
                "ACHAT2.NBRE_COLIS, " +
                "ACHAT2.COLISSAGE, " +
                "ACHAT2.QTE, " +
                "ACHAT2.QTE_GRAT, " +
                "ACHAT2.PU, " +
                "ACHAT2.TVA, " +
                "ACHAT2.CODE_DEPOT, " +
                "PRODUIT.STOCK " +
                "FROM ACHAT2 " +
                "LEFT JOIN PRODUIT ON (ACHAT2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                "WHERE ACHAT2.NUM_BON = '" + NUM_BON + "'" );

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanier(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

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
        switch (item.getItemId()) {
            case R.id.delete_produit -> {
                if (achat1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Suppression")
                        .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                        .setCancelText("Anuuler")
                        .setConfirmText("Supprimer")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try {

                                SOURCE = "ACHAT2_DELETE";
                                assert info != null;
                                controller.delete_from_achat2("ACHAT2", final_panier.get(info.position).recordid, final_panier.get(info.position));
                                initData();
                                //PanierAdapter.RefrechPanier(final_panier);
                                PanierAdapter = new ListViewAdapterPanier(ActivityAchat.this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);
                                expandableListView.setAdapter(PanierAdapter);

                            } catch (Exception e) {

                                new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors suppression produits! : " + e.getMessage())
                                        .show();
                            }

                            sDialog.dismiss();
                        }).show();
                return true;
            }
            case R.id.edit_produit -> {
                if (achat1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                try {
                    SOURCE = "ACHAT2_EDIT";
                    Activity activity;
                    activity = ActivityAchat.this;
                    FragmentQte fragmentqte = new FragmentQte();
                    assert info != null;
                    fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position));

                } catch (Exception e) {

                    new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Attention!")
                            .setContentText("Error : " + e.getMessage())
                            .show();
                }
                return true;
            }
            default -> {
                return super.onContextItemSelected(item);
            }
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

            double total_montant_produit = final_panier.get(k).p_u * final_panier.get(k).qte;
            double montant_tva_produit = total_montant_produit  * ((final_panier.get(k).tva) / 100);
            val_total_ht = val_total_ht + total_montant_produit;
            val_tva = val_tva + montant_tva_produit;

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
        getMenuInflater().inflate(R.menu.sale_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if (id == R.id.generate_pdf) {

            if(!achat1.blocage.equals("F")){
                new SweetAlertDialog(ActivityAchat.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas validé")
                        .show();
            }else{
                Activity mActivity;
                mActivity = ActivityAchat.this;

                GeneratePDF generate_pdf = new GeneratePDF();
                generate_pdf.startPDFAchat(mActivity, achat1, final_panier, "FROM_ACHAT");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //Versement
    protected void sauvegarder(){
        //update current bon1
        achat1.code_frs = fournisseur_selected.code_frs;
        achat1.fournis = fournisseur_selected.code_frs;
        //bon1_a_com.code_vendeur = "000000";
        //bon1_temp.mode_tarif = client_selected.mode_tarif;
        //achat1.solde_ancien = fournisseur_selected.solde_montant;
        achat1.nbr_p = final_panier.size();


        achat1.tot_ht = val_total_ht;
        achat1.tot_tva = val_tva;
        achat1.timbre =  val_timbre;
        achat1.tot_ttc = val_total_ttc;
        achat1.remise =  val_remise;
        achat1.montant_bon = val_total_ttc_remise;
        //update current bon1
        controller.update_achat1("ACHAT1",achat1.num_bon, achat1);

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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int resid){
        MediaPlayer mp = MediaPlayer.create(this, resid);
        mp.start();
    }

    @Subscribe
    public void onEventLocation(LocationEvent event){

        Log.e("TRACKKK", "Recieved location vente : " +  event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

        //achat1.latitude = event.getLocationData().getLatitude();
        //achat1.longitude = event.getLocationData().getLongitude();
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
                .withActivity(ActivityAchat.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) throws ParseException {
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
    public void onClick(View v, int position, PostData_Produit item) throws ParseException {

            PostData_Bon2 bon2 = new PostData_Bon2();
            bon2.produit = item.produit;
            bon2.codebarre = item.code_barre;
            bon2.stock_produit = item.stock;
            bon2.destock_type = item.destock_type;
            bon2.destock_code_barre = item.destock_code_barre;
            bon2.destock_qte = item.destock_qte;
            bon2.tva = item.tva;
            bon2.colissage = item.colissage;
            bon2.p_u = item.pa_ht;
            bon2.promo = item.promo;
            bon2.d1 = item.d1;
            bon2.d2 = item.d2;
            bon2.pp1_ht = item.pp1_ht;

            bon2.num_bon = NUM_BON;
            bon2.code_depot = CODE_DEPOT;
            SOURCE = "ACHAT2_INSERT";
            Activity activity = ActivityAchat.this;
            FragmentQte fragmentqte = new FragmentQte();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(),  bon2);

        //Save clicked item position in list
        //save permanently
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", position).apply();
    }

    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventBon2 item_panier){

           try {
               if(SOURCE.equals("ACHAT2_INSERT")){
                   if(item_panier.getIfExist()){
                       controller.update_into_achat2("ACHAT2", NUM_BON, item_panier.getData(), item_panier.getQteOld(),item_panier.getGratuitOld());
                   }else {
                       controller.insert_into_achat2("ACHAT2",  item_panier.getData());
                   }

               }else if(SOURCE.equals("ACHAT2_EDIT")){
                   controller.update_into_achat2("ACHAT2", NUM_BON, item_panier.getData(), item_panier.getQteOld(),item_panier.getGratuitOld());
               }

               initData();
               Sound(R.raw.cashier_quotka);

           }catch (Exception e){
               Crouton.makeText(ActivityAchat.this, "Erreur in produit" + e.getMessage(), Style.ALERT).show();
           }
    }


    @Subscribe
    public void onVersementReceived(ValidateFactureEvent versement){

        achat1.verser = versement.getVersement();
        if (achat1.verser != 0 ) {
            achat1.mode_rg = "ESPECE";
        } else{
            achat1.mode_rg = "A TERME";
        }

        achat1.reste = achat1.solde_ancien + (achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise) - achat1.verser;

        Calendar c = Calendar.getInstance();
        achat1.date_f = date_format.format(c.getTime());
        achat1.heure_f = heure_format.format(c.getTime());

        //bon1.diff_time =
        if (controller.validate_achat1_sql("ACHAT1", achat1)) {
            achat1.blocage = "F";
        }

        validate_theme();
    }
    public void validate_theme(){
        if (achat1.blocage.equals("F")) {
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

    private void selectProductFromScan(String resultscan) throws ParseException {
        ArrayList<PostData_Produit> produits;
        PostData_Bon2 bon2 = new PostData_Bon2();

            String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
            produits = controller.select_produits_from_database(querry);

            if(produits.size() == 0){
                String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+resultscan+"'";
                String code_barre = controller.select_codebarre_from_database(querry1);

                String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                produits = controller.select_produits_from_database(querry2);
            }

        if(produits.size() == 1){
            bon2.num_bon = NUM_BON;
            bon2.code_depot = CODE_DEPOT;
            bon2.produit = produits.get(0).produit;
            bon2.codebarre = produits.get(0).code_barre;
            bon2.stock_produit = produits.get(0).stock;
            bon2.destock_type = produits.get(0).destock_type;
            bon2.destock_code_barre = produits.get(0).destock_code_barre;
            bon2.destock_qte = produits.get(0).destock_qte;
            bon2.tva = produits.get(0).tva;
            bon2.colissage = produits.get(0).colissage;
            bon2.p_u = produits.get(0).pa_ht;
            bon2.promo = produits.get(0).promo;
            bon2.d1 = produits.get(0).d1;
            bon2.d2 = produits.get(0).d2;
            bon2.pp1_ht = produits.get(0).pp1_ht;


            SOURCE = "ACHAT2_INSERT";
            Activity activity = ActivityAchat.this;
            FragmentQte fragmentqte = new FragmentQte();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(),  bon2);

        }else if(produits.size() > 1){
            Crouton.makeText(ActivityAchat.this, "Attention il y a 2 produits avec le meme code !", Style.ALERT).show();
        }else{
            Crouton.makeText(ActivityAchat.this, "Produit introuvable !", Style.ALERT).show();
        }
    }

}

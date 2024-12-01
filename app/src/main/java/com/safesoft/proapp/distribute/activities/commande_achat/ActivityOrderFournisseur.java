package com.safesoft.proapp.distribute.activities.commande_achat;

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
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierAchat;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierVente;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventAchat2;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQteAchat;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.fragments.FragmentSelectFournisseur;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.libs.expandableheightlistview.ExpandableHeightListView;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.utils.Env;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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

public class ActivityOrderFournisseur extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick {


    ////////////////////////////////////////
    private static final int ACCES_FINE_LOCATION = 2;
    private static final int CAMERA_PERMISSION = 5;

    private Intent intent_location;

    private ListViewAdapterPanierAchat PanierAdapter;
    private Button btn_select_fournisseur, btn_mode_tarif;
    private DATABASE controller;
    private ArrayList<PostData_Achat2> final_panier;
    private TextView total_ht, tva, txv_timbre, txv_remise, total_ttc, total_ttc_remise;
    private double val_total_ht = 0.00;
    private double val_tva = 0.00;
    private double val_total_ttc = 0.00;
    private double val_timbre = 0.00;
    private double val_remise = 0.00;
    private double val_total_ttc_remise = 0.00;

    private EventBus bus;
    private String NUM_BON;
    private String CODE_DEPOT;
    private PostData_Fournisseur fournisseur_selected;
    private PostData_Achat1 achat1_com;
    private String SOURCE;


    private ExpandableHeightListView expandableListView;

    private NumberFormat nf;

    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;
    final String PREFS = "ALL_PREFS";
    String TYPE_ACTIVITY = "";
    String SOURCE_EXPORT = "";
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achat);

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityOrderFournisseur.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        achat1_com = new PostData_Achat1();
        final_panier = new ArrayList<>();


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String date_time_sub_title = null;
        String formattedDate = null;


        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        initViews();

        //get num bon
        if (getIntent() != null) {
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        } else {
            Crouton.makeText(ActivityOrderFournisseur.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        if (TYPE_ACTIVITY.equals("NEW_ORDER_ACHAT")) {
            //get num bon
            NUM_BON = controller.select_max_num_bon("SELECT MAX(NUM_BON) AS max_id FROM ACHAT1_TEMP WHERE NUM_BON IS NOT NULL");
            // get date and time
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String formattedDate_Show = date_format.format(c.getTime());
            // formattedDate = df_save.format(c.getTime());
            String currentTime = sdf.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            achat1_com.date_bon = formattedDate_Show;
            achat1_com.heure = currentTime;
            achat1_com.num_bon = NUM_BON;
            achat1_com.blocage = "";
            achat1_com.code_depot = CODE_DEPOT;


        } else if (TYPE_ACTIVITY.equals("EDIT_ORDER_ACHAT")) {
            //get num bon
            if (getIntent() != null) {
                NUM_BON = getIntent().getStringExtra("NUM_BON");
            }

            String querry = "SELECT " +
                    "ACHAT1_TEMP.RECORDID, " +
                    "ACHAT1_TEMP.NUM_BON, " +
                    "ACHAT1_TEMP.CODE_FRS, " +
                    "ACHAT1_TEMP.DATE_BON, " +
                    "ACHAT1_TEMP.HEURE, " +
                    "ACHAT1_TEMP.NBR_P, " +
                    "ACHAT1_TEMP.TOT_QTE, " +
                    "ACHAT1_TEMP.EXPORTATION, " +
                    "ACHAT1_TEMP.BLOCAGE, " +
                    "ACHAT1_TEMP.CODE_DEPOT, " +

                    "ACHAT1_TEMP.NBR_P, " +
                    "ACHAT1_TEMP.TOT_QTE, " +

                    "ACHAT1_TEMP.TOT_HT, " +
                    "ACHAT1_TEMP.TOT_TVA, " +
                    "ACHAT1_TEMP.TIMBRE, " +
                    "ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE AS TOT_TTC, " +
                    "ACHAT1_TEMP.REMISE, " +
                    "ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE - ACHAT1_TEMP.REMISE AS MONTANT_BON, " +

                    "ACHAT1_TEMP.ANCIEN_SOLDE, " +
                    "ACHAT1_TEMP.VERSER, " +
                    "ACHAT1_TEMP.ANCIEN_SOLDE + (ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE - ACHAT1_TEMP.REMISE) - ACHAT1_TEMP.VERSER AS RESTE, " +

                    "FOURNIS.FOURNIS, " +
                    "FOURNIS.ADRESSE, " +
                    "FOURNIS.TEL " +
                    "FROM ACHAT1_TEMP " +
                    "LEFT JOIN FOURNIS ON (ACHAT1_TEMP.CODE_FRS = FOURNIS.CODE_FRS) " +
                    " WHERE ACHAT1_TEMP.NUM_BON ='" + NUM_BON + "'";

            ///////////////////////////////////
            achat1_com = controller.select_one_acha1_from_database(querry);


            final_panier = controller.select_all_achat2_from_database("SELECT " +
                    "ACHAT2_TEMP.RECORDID, " +
                    "ACHAT2_TEMP.CODE_BARRE, " +
                    "ACHAT2_TEMP.NUM_BON, " +
                    "ACHAT2_TEMP.PRODUIT, " +
                    "ACHAT2_TEMP.NBRE_COLIS, " +
                    "ACHAT2_TEMP.COLISSAGE, " +
                    "ACHAT2_TEMP.QTE, " +
                    "ACHAT2_TEMP.QTE_GRAT, " +
                    "ACHAT2_TEMP.PA_HT, " +
                    "ACHAT2_TEMP.TVA, " +
                    "ACHAT2_TEMP.CODE_DEPOT, " +
                    "ACHAT2_TEMP.QTE_GRAT, " +
                    "PRODUIT.PAMP, " +
                    "PRODUIT.STOCK " +
                    "FROM ACHAT2_TEMP " +
                    "LEFT JOIN PRODUIT ON (ACHAT2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE ACHAT2_TEMP.NUM_BON = '" + NUM_BON + "'");


            //private String formattedDate;
            date_time_sub_title = achat1_com.date_bon + " " + achat1_com.heure;

            fournisseur_selected = controller.select_fournisseur_from_database(achat1_com.code_frs);
            onFournisseurSelected(fournisseur_selected);

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanierAchat(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

            expandableListView = findViewById(R.id.expandable_listview);

            expandableListView.setAdapter(PanierAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);

            registerForContextMenu(expandableListView);

            calcule();

        } else {
            Crouton.makeText(ActivityOrderFournisseur.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }


        if (NUM_BON != null) {
            getSupportActionBar().setTitle("Bon de commande N°: " + NUM_BON);
        }

        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();

        // Register as a subscriber
        bus.register(this);

    }

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    private void initViews() {

        btn_select_fournisseur = findViewById(R.id.btn_select_fournisseur);
        btn_mode_tarif = findViewById(R.id.btn_mode_tarif);

        //textview
        total_ht = findViewById(R.id.total_ht);
        tva = findViewById(R.id.tva);
        total_ttc = findViewById(R.id.total_ttc);
        txv_timbre = findViewById(timbre);
        total_ttc_remise = findViewById(R.id.total_ttc_remise);
        //TextView observation = findViewById(R.id.observation_value);
        txv_remise = findViewById(R.id.txv_remise);

        //TablRow
        TableRow tr_total_ht = findViewById(R.id.tr_total_ht);
        TableRow tr_total_tva = findViewById(R.id.tr_total_tva);
        TableRow tr_total_timbre = findViewById(R.id.tr_total_timbre);


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
            tr_total_ht.setVisibility(View.VISIBLE);
            tr_total_tva.setVisibility(View.VISIBLE);
            tr_total_timbre.setVisibility(View.VISIBLE);
        } else {
            tr_total_ht.setVisibility(View.GONE);
            tr_total_tva.setVisibility(View.GONE);
            tr_total_timbre.setVisibility(View.GONE);
        }

        intent_location = new Intent(this, ServiceLocation.class);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (prefs.getBoolean("GPS_LOCALISATION", false)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            } else {
                startService(intent_location);
            }
        } else {
            stopService(intent_location);
        }


        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

    }


    @SuppressLint("NonConstantResourceId")
    public void onClickEvent(View v) {
        switch (v.getId()) {
            case R.id.btn_select_fournisseur:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                showListFournisseur();
                break;

            case R.id.btn_mode_tarif:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                if (achat1_com.fournis.isEmpty()) {
                    Crouton.makeText(ActivityOrderFournisseur.this, "Vous devez Séléctionner un fournisseur tout d'abord", Style.ALERT).show();
                    return;
                }

                break;
            case R.id.addProduct:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                if (achat1_com.fournis.isEmpty()) {

                    Crouton.makeText(ActivityOrderFournisseur.this, "Vous devez Séléctionner un fournisseur tout d'abord", Style.ALERT).show();
                    return;
                }

                if (!prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Important !")
                            .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                            .show();

                    return;
                }
                // Initialize activity
                Activity activity;
                activity = ActivityOrderFournisseur.this;
                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(), "0", "ACHAT");

                break;

            case R.id.valide_facture:

                if (achat1_com.fournis.isEmpty()) {

                    Crouton.makeText(ActivityOrderFournisseur.this, "Vous devez Séléctionner un fournisseur", Style.ALERT).show();
                    return;
                }
                if (final_panier.isEmpty()) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                //FragmentValider fragmentvalider = new FragmentValider();
                //fragmentvalider.showDialogbox(ActivityOrder.this, bon1_temp.solde_ancien, bon1_temp.montant_bon, bon1_temp.verser);
                onVersementReceived(null);

                break;
            case R.id.txv_timbre_btn:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                /*Activity mactivity;
                mactivity = ActivityOrderFournisseur.this;
                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);*/

                break;
            case R.id.txv_remise_btn:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                Activity mactivity;
                mactivity = ActivityOrderFournisseur.this;
                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);

                break;
            case R.id.btn_scan_produit:
                if (ContextCompat.checkSelfPermission(ActivityOrderFournisseur.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityOrderFournisseur.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                } else {
                    if (achat1_com.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon est déja validé")
                                .show();
                        return;
                    }

                    if (achat1_com.fournis.isEmpty()) {

                        Crouton.makeText(ActivityOrderFournisseur.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                        return;
                    }

                    startScanProduct();
                }
                break;
            case R.id.btn_mofifier_bon:
                if (!SOURCE_EXPORT.equals("EXPORTED")) {
                    if (!achat1_com.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon n'est pas encore validé")
                                .show();
                        return;

                    } else {
                        new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Modification")
                                .setContentText("Voulez-vous vraiment Modifier ce Bon ?")
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    try {
                                        if (controller.modifier_achat1_sql("ACHAT1_TEMP", achat1_com)) {
                                            achat1_com.blocage = "M";
                                            validate_theme();
                                        }
                                    } catch (Exception e) {

                                        new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Attention!")
                                                .setContentText("problème lors de Modification de Bon : " + e.getMessage())
                                                .show();
                                    }
                                    sDialog.dismiss();
                                }).show();
                    }
                } else {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja exporté")
                            .show();
                }

                break;
            case R.id.btn_imp_bon:
                if (!achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon n'est pas encore validé")
                            .show();
                    return;
                }

                /*Activity bactivity;
                bactivity = ActivityOrderFournisseur.this;
                PrinterVente printer = new PrinterVente();
                printer.start_print_order_bon(bactivity, final_panier, bon1_a_com);*/

                break;

        }
    }

    protected void showListFournisseur() {
        // Initialize activity
        Activity activity;
        // define activity of this class//
        activity = ActivityOrderFournisseur.this;
        FragmentSelectFournisseur fragmentSelectFournisseur = new FragmentSelectFournisseur();
        fragmentSelectFournisseur.showDialogbox(activity, getBaseContext());

    }


    @Subscribe
    public void onFournisseurSelected(SelectedFournisseurEvent fournisseurEvent) {
        onFournisseurSelected(fournisseurEvent.getFournisseur());
    }

    @Subscribe
    public void onRemiseReceived(RemiseEvent remise) {

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


    protected void onFournisseurSelected(PostData_Fournisseur fournisseur_s) {

        fournisseur_selected = fournisseur_s;
        achat1_com.fournis = fournisseur_s.fournis;
        btn_select_fournisseur.setText(fournisseur_selected.fournis);

        achat1_com.code_frs = fournisseur_selected.code_frs;
        achat1_com.fournis = fournisseur_selected.fournis;
        achat1_com.tel = fournisseur_selected.tel;
        achat1_com.adresse = fournisseur_selected.adresse;

        if (!controller.insert_into_achat1("ACHAT1_TEMP", achat1_com)) {
            finish();
        }
    }

    protected void initData() {
        final_panier = controller.select_all_achat2_from_database("SELECT " +
                "ACHAT2_TEMP.RECORDID, " +
                "ACHAT2_TEMP.CODE_BARRE, " +
                "ACHAT2_TEMP.NUM_BON, " +
                "ACHAT2_TEMP.PRODUIT, " +
                "ACHAT2_TEMP.NBRE_COLIS, " +
                "ACHAT2_TEMP.COLISSAGE, " +
                "ACHAT2_TEMP.QTE, " +
                "ACHAT2_TEMP.QTE_GRAT, " +
                "ACHAT2_TEMP.PA_HT, " +
                "ACHAT2_TEMP.TVA, " +
                "ACHAT2_TEMP.CODE_DEPOT, " +
                "PRODUIT.PAMP, " +
                "PRODUIT.STOCK " +
                "FROM ACHAT2_TEMP " +
                "LEFT JOIN PRODUIT ON (ACHAT2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                "WHERE ACHAT2_TEMP.NUM_BON = '" + achat1_com.num_bon + "'");

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanierAchat(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

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
        if (v.getId() == R.id.expandable_listview) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_listv, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete_produit:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Suppression")
                        .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                        .setCancelText("Anuuler")
                        .setConfirmText("Supprimer")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try {
                                SOURCE = "BON2_TEMP_DELETE";
                                controller.delete_from_achat2("ACHAT2_TEMP", final_panier.get(info.position).recordid, final_panier.get(info.position));
                                initData();
                                //PanierAdapter.RefrechPanier(final_panier);
                                PanierAdapter = new ListViewAdapterPanierAchat(ActivityOrderFournisseur.this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);
                                expandableListView.setAdapter(PanierAdapter);

                            } catch (Exception e) {

                                new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors suppression produits! : " + e.getMessage())
                                        .show();
                            }

                            sDialog.dismiss();
                        }).show();
                return true;

            case R.id.edit_produit:
                if (achat1_com.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                try {
                    SOURCE = "BON2_TEMP_EDIT";
                    Activity activity;
                    activity = ActivityOrderFournisseur.this;
                    FragmentQteAchat fragmentqte = new FragmentQteAchat();
                    fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position), 0);

                } catch (Exception e) {

                    new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Attention!")
                            .setContentText("Error : " + e.getMessage())
                            .show();
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void calcule() {

        val_total_ht = 0.00;
        val_tva = 0.00;
        val_timbre = 0.00;
        val_remise = 0.00;
        val_total_ttc = 0.00;
        val_total_ttc_remise = 0.00;

        for (int k = 0; k < final_panier.size(); k++) {

            double total_montant_produit = final_panier.get(k).pa_ht * final_panier.get(k).qte;
            double montant_tva_produit = total_montant_produit * ((final_panier.get(k).tva) / 100);
            val_total_ht = val_total_ht + total_montant_produit;
            val_tva = val_tva + montant_tva_produit;

        }


       /* if(checkBox_timbre.isChecked()){
            val_total_ttc =  (val_total_ht) +  val_tva;
            if((val_total_ttc * 0.01) >= 2500){
                val_timbre = 2500.00;
            }else{
                //float nnn = (val_total_ttc * (1/100));
                val_timbre = (val_total_ttc * 0.01);
            }
        }else {
            val_timbre = 0.0;
        }*/

        val_total_ttc = (val_total_ht) + val_tva + val_timbre;
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

        /*final BadgeDrawable drawable3 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                        .badgeColor(0xffE74C3C)
                        .text1(nf.format(val_timbre))
                        .text2(" DA")
                        .build();
        SpannableString spannableString3 = new SpannableString(TextUtils.concat(drawable3.toSpannable()));
        txv_timbre.setText(spannableString3);*/

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
        getMenuInflater().inflate(R.menu.menu_sale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.generate_pdf) {

            if (!achat1_com.blocage.equals("F")) {
                new SweetAlertDialog(ActivityOrderFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas validé")
                        .show();
            } else {
                /*Activity mActivity;
                mActivity = ActivityOrderFournisseur.this;
                GeneratePDF generate_pdf = new GeneratePDF();
                generate_pdf.startPDF(mActivity, bon1_a_com, final_panier, "FROM_ORDER");*/
            }
        }

        return super.onOptionsItemSelected(item);
    }


    //Versement
    protected void sauvegarder() {

        achat1_com.code_frs = fournisseur_selected.code_frs;
        achat1_com.fournis = fournisseur_selected.fournis;
        //bon1_a_com.code_vendeur = "000000";
        //bon1_temp.mode_tarif = client_selected.mode_tarif;
        //bon1_temp.solde_ancien = client_selected.solde_montant;
        achat1_com.nbr_p = final_panier.size();


        //bon1_a_com.tot_ht = val_total_ht;
        // bon1_a_com.tot_tva = val_tva;
        //bon1_a_com.timbre =  val_timbre;
        //bon1_a_com.tot_ttc = val_total_ttc;
        achat1_com.remise = val_remise;
        achat1_com.montant_bon = val_total_ttc_remise;
        //update current bon1
        controller.update_achat1("ACHAT1_TEMP", achat1_com.num_bon, achat1_com);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACCES_FINE_LOCATION) {
            startService(new Intent(this, ServiceLocation.class));
        }
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.back);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int resid) {
        MediaPlayer mp = MediaPlayer.create(this, resid);
        mp.start();
    }

    @Subscribe
    public void onEvent(LocationEvent event) {

        Log.e("TRACKKK", "Recieved location vente : " + event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

        //  bon1_a_com.latitude = event.getLocationData().getLatitude();
        //  bon1_a_com.longitude = event.getLocationData().getLongitude();
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
                .withActivity(ActivityOrderFournisseur.this)
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


        PostData_Achat2 achat2_com = new PostData_Achat2();
        achat2_com.produit = item.produit;
        achat2_com.codebarre = item.code_barre;
        achat2_com.pa_ht = item.pa_ht;
        achat2_com.pa_ht_produit = item.pa_ht;
        achat2_com.tva = item.tva;
        achat2_com.colissage = item.colissage;
        achat2_com.num_bon = NUM_BON;
        achat2_com.code_depot = CODE_DEPOT;
        achat2_com.stock_produit = item.stock;

        SOURCE = "ACHAT2_TEMP_INSERT";
        Activity activity = ActivityOrderFournisseur.this;
        FragmentQteAchat fragmentachat = new FragmentQteAchat();
        fragmentachat.showDialogbox(SOURCE, activity, getBaseContext(), achat2_com, 0);

    }

    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventAchat2 item_panier) {

        try {
            if (SOURCE.equals("BON2_TEMP_INSERT")) {
                controller.insert_into_achat2("ACHAT2_TEMP", item_panier.getData());
            } else if (SOURCE.equals("BON2_TEMP_EDIT")) {
                controller.update_into_achat2("ACHAT2_TEMP", NUM_BON, item_panier.getData(), item_panier.getQteOld(), item_panier.getGratuitOld());
            }

            initData();
            if (prefs.getBoolean("ENABLE_SOUND", false)) {
                Sound(R.raw.cashier_quotka);
            }

        } catch (Exception e) {
            Crouton.makeText(ActivityOrderFournisseur.this, "Erreur in produit" + e.getMessage(), Style.ALERT).show();
        }

    }


    @Subscribe
    public void onVersementReceived(ValidateFactureEvent versement) {

        // achat1_com.verser = 0.0;

       /* if (bon1_temp.verser != 0 ) {
            bon1_temp.mode_rg = "ESPECE";
        } else{
            bon1_temp.mode_rg = "A TERME";
        }*/

        // bon1_temp.reste = bon1_temp.solde_ancien + (bon1_temp.tot_ht + bon1_temp.tot_tva + bon1_temp.timbre - bon1_temp.remise) - bon1_temp.verser;

        if (controller.validate_achat1_sql("ACHAT1_TEMP", achat1_com)) {
            achat1_com.blocage = "F";
        }

        validate_theme();

    }

    public void validate_theme() {
        if (achat1_com.blocage.equals("F")) {
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
        PostData_Achat2 bon2_temp = new PostData_Achat2();

        String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
        produits = controller.select_produits_from_database(querry);


        if (produits.isEmpty()) {
            String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + resultscan + "'";
            String code_barre = controller.select_codebarre_from_database(querry1);

            String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
            produits = controller.select_produits_from_database(querry2);
        }

        if (produits.size() == 1) {

            bon2_temp.num_bon = NUM_BON;
            bon2_temp.code_depot = CODE_DEPOT;
            bon2_temp.produit = produits.get(0).produit;
            bon2_temp.codebarre = produits.get(0).code_barre;
            bon2_temp.stock_produit = produits.get(0).stock;
            bon2_temp.pa_ht = produits.get(0).pa_ht;
            bon2_temp.pa_ht_produit = produits.get(0).pa_ht;
            bon2_temp.tva = produits.get(0).tva;
            bon2_temp.colissage = produits.get(0).colissage;

            SOURCE = "BON2_TEMP_INSERT";
            Activity activity = ActivityOrderFournisseur.this;
            FragmentQteAchat fragmentqte = new FragmentQteAchat();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), bon2_temp, 0);

        } else if (produits.size() > 1) {
            Crouton.makeText(ActivityOrderFournisseur.this, "Attention il y a 2 produits avec le meme code !", Style.ALERT).show();
        } else {
            Crouton.makeText(ActivityOrderFournisseur.this, "Produit introuvable !", Style.ALERT).show();
        }
    }
}

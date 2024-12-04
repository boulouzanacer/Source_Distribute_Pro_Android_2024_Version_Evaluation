package com.safesoft.proapp.distribute.activities.inventaire;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputEditText;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityOrderClient;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierInventaire;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventInventaire2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQteInventaire;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.libs.expandableheightlistview.ExpandableHeightListView;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.printing.PrinterInventaire;
import com.safesoft.proapp.distribute.utils.Env;

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

public class ActivityInventaire extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick {


    ////////////////////////////////////////
    private static final int ACCES_FINE_LOCATION = 2;
    private static final int CAMERA_PERMISSION = 5;

    private Intent intent_location;

    private ListViewAdapterPanierInventaire PanierAdapter;
    private DATABASE controller;
    private ArrayList<PostData_Inv2> final_panier;
    private TextView nbr_produit;
    private int val_nbr_produit = 0;
    private TextInputEditText edt_nom_inventaire;
    private ImageButton btn_validate_inv_name;
    private boolean btn_nom_inv_state_isactive = true;

    private EventBus bus;

    private String NUM_INV;
    private PostData_Inv1 inv1;
    private String SOURCE;
    private String CODE_DEPOT;


    private ExpandableHeightListView expandableListView;

    private NumberFormat nf;

    public static final String BARCODE_KEY = "BARCODE";
    private final static String SCAN_ACTION = "safesoft.barcode.signal";
    private final static String BROADCAST_KEYBOARD = "com.scanner.broadcast";

    private Barcode barcodeResult;
    final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    String TYPE_ACTIVITY = "";
    String SOURCE_EXPORT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventaire);

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityInventaire.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        inv1 = new PostData_Inv1();
        final_panier = new ArrayList<>();


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String date_time_sub_title = null;
        String formattedDate = null;


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        initViews();

        //get num bon
        if (getIntent() != null) {
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        } else {
            Crouton.makeText(ActivityInventaire.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        if (TYPE_ACTIVITY.equals("NEW_INV")) {
            //get num bon
            String selectQuery = "SELECT MAX(NUM_INV) AS max_id FROM INV1 WHERE NUM_INV IS NOT NULL";
            NUM_INV = controller.select_max_num_bon(selectQuery);
            // get date and time
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String formattedDate_Show = date_format.format(c.getTime());
            // formattedDate = df_save.format(c.getTime());
            String currentTime = sdf.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            inv1.date_inv = formattedDate_Show;
            inv1.heure_inv = currentTime;
            inv1.num_inv = NUM_INV;
            inv1.blocage = "M";
            inv1.nom_inv = "";
            inv1.code_depot = CODE_DEPOT;


        } else if (TYPE_ACTIVITY.equals("EDIT_INV")) {
            //get num bon
            if (getIntent() != null) {
                NUM_INV = getIntent().getStringExtra("NUM_INV");
            }

            String querry = "SELECT " +
                    "INV1.NUM_INV, " +
                    "INV1.DATE_INV, " +
                    "INV1.HEURE_INV, " +
                    "INV1.LIBELLE, " +
                    "INV1.NBR_PRODUIT, " +

                    "INV1.UTILISATEUR, " +
                    "INV1.CODE_DEPOT, " +
                    "INV1.EXPORTATION, " +
                    "INV1.IS_EXPORTED, " +
                    "INV1.DATE_EXPORT_INV, " +
                    "INV1.BLOCAGE " +

                    "FROM INV1 " +
                    "WHERE INV1.NUM_INV ='" + NUM_INV + "'";

            ///////////////////////////////////
            inv1 = controller.select_inventaire_from_database(querry);

            final_panier = controller.select_inventaire2_from_database("SELECT " +
                    "INV2.RECORDID, " +
                    "INV2.CODE_BARRE, " +
                    "INV2.NUM_INV, " +
                    "INV2.PRODUIT, " +
                    "INV2.NBRE_COLIS, " +
                    "INV2.COLISSAGE, " +
                    "INV2.PA_HT, " +
                    "INV2.QTE, " +
                    "INV2.QTE_TMP, " +
                    "INV2.QTE_NEW, " +
                    "INV2.TVA, " +
                    "INV2.VRAC, " +
                    "INV2.CODE_DEPOT " +
                    "FROM INV2 " +
                    "WHERE INV2.NUM_INV = '" + NUM_INV + "'");
            //private String formattedDate;
            date_time_sub_title = inv1.date_inv + " " + inv1.heure_inv;

            edt_nom_inventaire.setText(inv1.nom_inv);
            btn_validate_inv_name.setBackgroundResource(R.drawable.baseline_edit_24);
            edt_nom_inventaire.setEnabled(false);
            btn_nom_inv_state_isactive = false;

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanierInventaire(this, R.layout.transfert2_items, final_panier);

            expandableListView = findViewById(R.id.expandable_listview);

            expandableListView.setAdapter(PanierAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);

            registerForContextMenu(expandableListView);

            calcule();

        } else {
            Crouton.makeText(ActivityInventaire.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }


        if (NUM_INV != null) {
            getSupportActionBar().setTitle("Inventaire N°: " + NUM_INV);
        }
        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();


        // Register as a subscriber
        bus.register(this);

    }

    @Override
    protected void onResume() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        filter.addAction(BROADCAST_KEYBOARD);
        registerReceiver(mScanReceiver , filter, RECEIVER_EXPORTED);

        super.onResume();
    }

    @SuppressLint({"CutPasteId", "WrongViewCast"})
    private void initViews() {

        edt_nom_inventaire = findViewById(R.id.edt_nom_inventaire);
        btn_validate_inv_name = findViewById(R.id.btn_valide_inv_name);

        //textview
        nbr_produit = findViewById(R.id.total_nbr_produit);
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
    public void onClickEvent(View v) throws UnsupportedEncodingException, ParseException {
        switch (v.getId()) {
            case R.id.addProduct:
                if (inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est déja validé")
                            .show();
                    return;
                }

                if (Objects.requireNonNull(edt_nom_inventaire.getText()).length() < 1) {
                    Crouton.makeText(ActivityInventaire.this, "Vous devez saissir le nom d'inventaire", Style.ALERT).show();
                    return;
                }
                if (btn_nom_inv_state_isactive) {
                    Crouton.makeText(ActivityInventaire.this, "Vous devez valider le nom d'inventaire", Style.ALERT).show();
                    return;
                }

                if (!prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Important !")
                            .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                            .show();

                    return;
                }

                // Initialize activity
                Activity activity;
                // define activity of this class//
                activity = ActivityInventaire.this;

                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(), "1", "VENTE");
                break;

            case R.id.valide_inventaire:

                if (Objects.requireNonNull(edt_nom_inventaire.getText()).length() < 1) {

                    Crouton.makeText(ActivityInventaire.this, "Vous devez saissir le nom d'inventaire", Style.ALERT).show();
                    return;
                }
                if (btn_nom_inv_state_isactive) {
                    Crouton.makeText(ActivityInventaire.this, "Vous devez valider le nom d'inventaire", Style.ALERT).show();
                    return;
                }
                if (final_panier.size() < 1) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est vide")
                            .show();
                    return;
                }
                if (inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est déja validé")
                            .show();
                    return;
                }

                if (controller.validate_inv1_sql("INV1", inv1.num_inv)) {
                    inv1.blocage = "F";
                    validate_theme();
                }

                break;
            case R.id.btn_scan_produit:
                if (ContextCompat.checkSelfPermission(ActivityInventaire.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityInventaire.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                } else {
                    if (inv1.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Cet inventaire est déja validé")
                                .show();
                        return;
                    }
                    if (Objects.requireNonNull(edt_nom_inventaire.getText()).length() < 1) {

                        Crouton.makeText(ActivityInventaire.this, "Vous devez saissir le nom d'inventaire", Style.ALERT).show();
                        return;
                    }
                    if (btn_nom_inv_state_isactive) {
                        Crouton.makeText(ActivityInventaire.this, "Vous devez valider le nom d'inventaire", Style.ALERT).show();
                        return;
                    }
                    startScanProduct();
                }
                break;
            case R.id.btn_mofifier_bon:

                if (!SOURCE_EXPORT.equals("EXPORTED")) {
                    if (inv1.is_exported == 1) {
                        new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Cet inventaire est déja exporté, modification impossible")
                                .show();
                        return;
                    }
                    if (!inv1.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Cet inventaire n'est pas encore validé")
                                .show();
                        return;

                    } else {
                        new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Modification")
                                .setContentText("Voulez-vous vraiment Modifier cet inventaire ?")
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    try {

                                        if (controller.modifier_inv1_sql("INV1", inv1.num_inv)) {
                                            inv1.blocage = "M";
                                            validate_theme();
                                        }


                                    } catch (Exception e) {

                                        new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Attention!")
                                                .setContentText("problème lors de Modification d'inventaire : " + e.getMessage())
                                                .show();
                                    }
                                    sDialog.dismiss();
                                }).show();
                    }
                } else {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja exporté")
                            .show();
                }

                break;
            case R.id.btn_imp_bon:
                if (!inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire n'est pas encore validé")
                            .show();
                    return;
                }
                Activity bactivity;
                bactivity = ActivityInventaire.this;

                PrinterInventaire printer = new PrinterInventaire();
                printer.start_print_inv(bactivity, final_panier, inv1);

                break;
            case R.id.btn_valide_inv_name:

                if (inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est déja validé")
                            .show();
                    return;
                }

                if (Objects.requireNonNull(edt_nom_inventaire.getText()).length() < 1) {
                    Crouton.makeText(ActivityInventaire.this, "Vous devez saissir le nom d'inventaire", Style.ALERT).show();
                    return;
                }

                inv1.nom_inv = edt_nom_inventaire.getText().toString();

                if (btn_nom_inv_state_isactive) {
                    if (controller.insert_into_Inv1("INV1", inv1)) {
                        btn_validate_inv_name.setBackgroundResource(R.drawable.baseline_edit_24);
                        edt_nom_inventaire.setEnabled(false);
                        btn_nom_inv_state_isactive = false;
                    }
                } else {
                    btn_validate_inv_name.setBackgroundResource(R.drawable.ic_baseline_done_24);
                    edt_nom_inventaire.setEnabled(true);
                    btn_nom_inv_state_isactive = true;
                }

                break;

        }
    }

    protected void initData() {
        final_panier = controller.select_inventaire2_from_database("SELECT " +
                "INV2.RECORDID, " +
                "INV2.CODE_BARRE, " +
                "INV2.NUM_INV, " +
                "INV2.PRODUIT, " +
                "INV2.NBRE_COLIS, " +
                "INV2.COLISSAGE, " +
                "INV2.PA_HT, " +
                "INV2.QTE, " +
                "INV2.QTE_TMP, " +
                "INV2.QTE_NEW, " +
                "INV2.TVA, " +
                "INV2.VRAC, " +
                "INV2.CODE_DEPOT " +
                "FROM INV2 " +
                "WHERE INV2.NUM_INV = '" + NUM_INV + "'");

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanierInventaire(this, R.layout.transfert2_items, final_panier);

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
                if (inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est déja validé")
                            .show();
                    return true;
                }
                new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Suppression")
                        .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                        .setCancelText("Anuuler")
                        .setConfirmText("Supprimer")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try {
                                SOURCE = "INV2_DELETE";
                                controller.delete_from_inv2("INV2", final_panier.get(info.position).recordid);
                                initData();
                                //PanierAdapter.RefrechPanier(final_panier);
                                PanierAdapter = new ListViewAdapterPanierInventaire(ActivityInventaire.this, R.layout.transfert2_items, final_panier);
                                expandableListView.setAdapter(PanierAdapter);

                            } catch (Exception e) {

                                new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors suppression produits! : " + e.getMessage())
                                        .show();
                            }

                            sDialog.dismiss();
                        }).show();
                return true;

            case R.id.edit_produit:
                if (inv1.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Cet inventaire est déja validé")
                            .show();
                    return true;
                }
                try {
                    SOURCE = "INV2_EDIT";
                    Activity activity;
                    activity = ActivityInventaire.this;
                    FragmentQteInventaire fragmentQteInventaire = new FragmentQteInventaire();
                    fragmentQteInventaire.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position));

                } catch (Exception e) {

                    new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.ERROR_TYPE)
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

        val_nbr_produit = final_panier.size();

        final BadgeDrawable drawable = new BadgeDrawable.Builder()
                .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                .badgeColor(0xff303F9F)
                .text1(String.valueOf(val_nbr_produit))
                .text2(" PRODUIT")
                .build();
        SpannableString spannableString = new SpannableString(TextUtils.concat(drawable.toSpannable()));
        nbr_produit.setText(spannableString);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.menu_bon_vente_commande, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    //Versement
    protected void sauvegarder() {

        inv1.nbr_produit = final_panier.size();
        //update current inv1
        controller.update_inv1_nbr_produit("INV1", inv1);

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

        //bon1.latitude = event.getLocationData().getLatitude();
        //bon1.longitude = event.getLocationData().getLongitude();
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
                .withActivity(ActivityInventaire.this)
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
    public void onClick(View v, int position, PostData_Produit item) {

        PostData_Inv2 inv2 = new PostData_Inv2();
        inv2.produit = item.produit;
        inv2.codebarre = item.code_barre;
        inv2.code_depot = CODE_DEPOT;
        inv2.qte_theorique = item.stock;
        inv2.pa_ht = item.pamp;
        inv2.colissage = item.colissage;

        inv2.num_inv = NUM_INV;
        // inv2.code_depot = CODE_DEPOT;
        SOURCE = "INV2_INSERT";
        Activity activity = ActivityInventaire.this;
        FragmentQteInventaire fragmentqteinventaire = new FragmentQteInventaire();
        fragmentqteinventaire.showDialogbox(SOURCE, activity, getBaseContext(), inv2);

    }

    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventInventaire2 item_panier) {

        try {
            if (SOURCE.equals("INV2_INSERT")) {
                if (item_panier.getIfExist()) {
                    controller.Update_inventaire2(item_panier.getData());
                } else {
                    controller.insert_into_inventaire2(item_panier.getData());
                }
            } else if (SOURCE.equals("INV2_EDIT")) {
                controller.Update_inventaire2(item_panier.getData());
            }
            initData();
        } catch (Exception e) {
            Crouton.makeText(ActivityInventaire.this, "Erreur dans produit" + e.getMessage(), Style.ALERT).show();
        }
    }

    public void validate_theme() {
        if (inv1.blocage.equals("F")) {
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

    private void selectProductFromScan(String resultscan) {
        ArrayList<PostData_Produit> produits;

        PostData_Inv2 inv2 = new PostData_Inv2();

        String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
        produits = controller.select_produits_from_database(querry);

        if (produits.size() == 0) {
            String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + resultscan + "'";
            String code_barre = controller.select_codebarre_from_database(querry1);

            String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
            produits = controller.select_produits_from_database(querry2);
        }

        if (produits.size() == 1) {

            inv2.produit = produits.get(0).produit;
            inv2.codebarre = produits.get(0).code_barre;
            inv2.qte_theorique = produits.get(0).stock;
            inv2.pa_ht = produits.get(0).pamp;
            inv2.tva = produits.get(0).tva;
            inv2.colissage = produits.get(0).colissage;
            inv2.num_inv = NUM_INV;
            inv2.code_depot = CODE_DEPOT;

            SOURCE = "INV2_INSERT";
            Activity activity = ActivityInventaire.this;
            FragmentQteInventaire fragmentqteinventaire = new FragmentQteInventaire();
            fragmentqteinventaire.showDialogbox(SOURCE, activity, getBaseContext(), inv2);

        } else if (produits.size() > 1) {
            Crouton.makeText(ActivityInventaire.this, "Attention il y a 2 produits avec le meme code !", Style.ALERT).show();
        } else {
            Crouton.makeText(ActivityInventaire.this, "Produit introuvable !", Style.ALERT).show();
        }
    }

    private final BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

                if (Objects.requireNonNull(intent.getAction()).equals(BROADCAST_KEYBOARD)){

                    String barcode2 = intent.getStringExtra("com.symbol.datawedge.data_string");
                    Log.v("TEST", "code_barre : " + barcode2);
                    selectProductFromScan(barcode2);

                }else {
                    String barcode = intent.getStringExtra("barcode");
                    //byte[] barcode = intent.getByteArrayExtra("barcode");
                    int barocodelen = intent.getIntExtra("length", 0);
                    byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                    //android.util.Log.i("debug", "----codetype--" + temp);
                    selectProductFromScan(barcode);
                }
        }
    };

    @Override
    protected void onStop() {
        unregisterReceiver(mScanReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        stopService(intent_location);
        super.onDestroy();
    }

}

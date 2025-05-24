package com.safesoft.proapp.distribute.activities.commande_vente;

import static com.safesoft.proapp.distribute.R.id.timbre;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierVente;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ByteDataEvent;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQteVente;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.fragments.FragmentSelectClient;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.libs.expandableheightlistview.ExpandableHeightListView;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.utils.Env;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityOrderClient extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick {


    ////////////////////////////////////////
    private static final int ACCES_FINE_LOCATION = 2;
    private static final int CAMERA_PERMISSION = 5;

    private Intent intent_location;

    private ListViewAdapterPanierVente PanierAdapter;
    private Button btn_select_client, btn_mode_tarif;
    private DATABASE controller;
    private ArrayList<PostData_Bon2> final_panier;
    private TextView total_ht, tva, txv_timbre, txv_remise, total_ttc, total_ttc_remise;
    private CheckBox checkBox_timbre;
    private double val_total_ht = 0.00;
    private double val_tva = 0.00;
    private double val_timbre = 0.00;
    private double val_total_ttc = 0.00;
    private double val_remise = 0.00;
    private double val_total_ttc_remise = 0.00;
    private double val_total_achat_ht = 0.00;

    private EventBus bus;
    private boolean show_picture_prod;
    private String NUM_BON;
    private String CODE_DEPOT;
    private PostData_Client client_selected;
    private PostData_Bon1 bon1_temp;
    private String SOURCE;


    private ExpandableHeightListView expandableListView;

    private NumberFormat nf;

    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;
    final String PREFS = "ALL_PREFS";

    String TYPE_ACTIVITY = "";
    String SOURCE_EXPORT = "";
    SharedPreferences prefs;

    private PostData_Params params;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date_format;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat heure_format;

    private boolean is_app_synchronised_mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vente);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityOrderClient.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        bon1_temp = new PostData_Bon1();
        final_panier = new ArrayList<>();


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        String date_time_sub_title = null;
        String formattedDate = null;


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);

        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        show_picture_prod = prefs.getBoolean("SHOW_PROD_PIC", false);

        initViews();

        //get num bon
        if (getIntent() != null) {
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        } else {
            Crouton.makeText(ActivityOrderClient.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        date_format = new SimpleDateFormat("dd/MM/yyyy");
        heure_format = new SimpleDateFormat("HH:mm:ss");

        if (TYPE_ACTIVITY.equals("NEW_ORDER_CLIENT")) {
            //get num bon
            String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM BON1_TEMP WHERE NUM_BON IS NOT NULL";
            NUM_BON = controller.select_max_num_bon(selectQuery);
            // get date and time
            Calendar c = Calendar.getInstance();
            String formattedDate_Show = date_format.format(c.getTime());
            // formattedDate = df_save.format(c.getTime());
            String currentTime = heure_format.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            bon1_temp.date_bon = formattedDate_Show;
            bon1_temp.heure = currentTime;
            bon1_temp.num_bon = NUM_BON;
            bon1_temp.blocage = "";
            bon1_temp.client = "";
            bon1_temp.code_depot = CODE_DEPOT;


        } else if (TYPE_ACTIVITY.equals("EDIT_ORDER_CLIENT")) {
            //get num bon
            if (getIntent() != null) {
                NUM_BON = getIntent().getStringExtra("NUM_BON");
            }

            String querry = "SELECT " +
                    "BON1_TEMP.RECORDID, " +
                    "BON1_TEMP.NUM_BON, " +
                    "BON1_TEMP.DATE_BON, " +
                    "BON1_TEMP.HEURE, " +
                    "BON1_TEMP.MODE_RG, " +
                    "BON1_TEMP.MODE_TARIF, " +

                    "BON1_TEMP.NBR_P, " +
                    "BON1_TEMP.TOT_QTE, " +

                    "BON1_TEMP.TOT_HT, " +
                    "BON1_TEMP.TOT_TVA, " +
                    "BON1_TEMP.TIMBRE, " +
                    "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE AS TOT_TTC, " +
                    "BON1_TEMP.REMISE, " +
                    "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE AS MONTANT_BON, " +
                    "BON1_TEMP.MONTANT_ACHAT, " +

                    "BON1_TEMP.ANCIEN_SOLDE, " +
                    "BON1_TEMP.VERSER, " +
                    "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

                    "BON1_TEMP.CODE_CLIENT, " +
                    "CLIENT.CLIENT, " +
                    "CLIENT.ADRESSE, " +
                    "CLIENT.TEL, " +
                    "CLIENT.RC, " +
                    "CLIENT.IFISCAL, " +
                    "CLIENT.AI, " +
                    "CLIENT.NIS, " +

                    "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                    "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                    "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                    "CLIENT.CREDIT_LIMIT, " +

                    "BON1_TEMP.LATITUDE, " +
                    "BON1_TEMP.LONGITUDE, " +
                    "BON1_TEMP.LIVRER, " +
                    "BON1_TEMP.DATE_LIV, " +
                    "BON1_TEMP.IS_IMPORTED, " +

                    "BON1_TEMP.CODE_DEPOT, " +
                    "BON1_TEMP.CODE_VENDEUR, " +
                    "BON1_TEMP.EXPORTATION, " +
                    "BON1_TEMP.BLOCAGE " +
                    "FROM BON1_TEMP " +
                    "LEFT JOIN CLIENT ON BON1_TEMP.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                    "WHERE BON1_TEMP.NUM_BON ='" + NUM_BON + "'";
            ///////////////////////////////////
            bon1_temp = controller.select_bon1_from_database2(querry);


            final_panier = controller.select_bon2_from_database("SELECT " +
                    "BON2_TEMP.RECORDID, " +
                    "BON2_TEMP.CODE_BARRE, " +
                    "BON2_TEMP.NUM_BON, " +
                    "BON2_TEMP.PRODUIT, " +
                    "BON2_TEMP.NBRE_COLIS, " +
                    "BON2_TEMP.COLISSAGE, " +
                    "BON2_TEMP.QTE, " +
                    "BON2_TEMP.QTE_GRAT, " +
                    "BON2_TEMP.PV_HT, " +
                    "BON2_TEMP.PA_HT, " +
                    "BON2_TEMP.TVA, " +
                    "BON2_TEMP.CODE_DEPOT, " +
                    "BON2_TEMP.DESTOCK_TYPE, " +
                    "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                    "BON2_TEMP.DESTOCK_QTE, " +

                    "PRODUIT.ISNEW, " +
                    "PRODUIT.PV_LIMITE, " +
                    "PRODUIT.STOCK, " +
                    "PRODUIT.PROMO, " +
                    "PRODUIT.QTE_PROMO, " +
                    "PRODUIT.D1, " +
                    "PRODUIT.D2, " +
                    "PRODUIT.PP1_HT " +

                    "FROM BON2_TEMP " +
                    "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE BON2_TEMP.NUM_BON = '" + NUM_BON + "'");

            //private String formattedDate;
            date_time_sub_title = bon1_temp.date_bon + " " + bon1_temp.heure;

            client_selected = controller.select_client_from_database(bon1_temp.code_client);
            onClientSelected(client_selected, false);

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

            expandableListView = findViewById(R.id.expandable_listview);

            expandableListView.setAdapter(PanierAdapter);

            // This actually does the magic
            expandableListView.setExpanded(true);

            registerForContextMenu(expandableListView);

            val_timbre = bon1_temp.timbre;
            val_remise = bon1_temp.remise;

            checkBox_timbre.setChecked(false);
            if (val_timbre != 0) checkBox_timbre.setChecked(true);

            calcule();


        } else {
            Crouton.makeText(ActivityOrderClient.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }


        if (NUM_BON != null) {
            getSupportActionBar().setTitle("Bon de commande N°: " + NUM_BON);
        }
        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();


        // Register as a subscriber
        bus.register(this);


        checkBox_timbre.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (client_selected == null) {
                Toast.makeText(ActivityOrderClient.this, "Veuilez sélectionner un Client", Toast.LENGTH_SHORT).show();
                checkBox_timbre.setChecked(false);
            } else {
                checkBox_timbre.setChecked(isChecked);
                calcule();
                sauvegarder();
            }

        });

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
        TableRow tbr_remise = findViewById(R.id.tbr_remise);

        //checkbox
        checkBox_timbre = findViewById(R.id.checkbox_timbre);

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

        if (prefs.getBoolean("AFFICHAGE_REMISE", true)) {
            tbr_remise.setVisibility(View.VISIBLE);
        } else {
            tbr_remise.setVisibility(View.GONE);
        }

       /* intent_location = new Intent(this, ServiceLocation.class);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (prefs.getBoolean("GPS_LOCALISATION", false)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            } else {
                startService(intent_location);
            }
        } else {
            stopService(intent_location);
        }*/


        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        params = new PostData_Params();
        params = controller.select_params_from_database("SELECT * FROM PARAMS");
    }


    @SuppressLint("NonConstantResourceId")
    public void onClickEvent(View v) throws UnsupportedEncodingException, ParseException {
        switch (v.getId()) {
            case R.id.btn_select_client:
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                showListClient();
                break;

            case R.id.btn_mode_tarif:
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                if (bon1_temp.client.isEmpty()) {

                    Crouton.makeText(ActivityOrderClient.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }

                String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

                if (selectedTitle.equals("Libre")) {

                    if (client_selected.mode_tarif.equals("0")) {

                        if (btn_mode_tarif.getText().toString().equals("Tarif 1")) {
                            if (params.prix_2 == 1 && is_app_synchronised_mode) {
                                bon1_temp.mode_tarif = "2";
                                btn_mode_tarif.setText("Tarif 2");
                            } else {
                                bon1_temp.mode_tarif = "1";
                                btn_mode_tarif.setText("Tarif 1");
                            }
                        } else if (btn_mode_tarif.getText().toString().equals("Tarif 2")) {
                            if (params.prix_3 == 1 && is_app_synchronised_mode) {
                                bon1_temp.mode_tarif = "3";
                                btn_mode_tarif.setText("Tarif 3");
                            } else {
                                bon1_temp.mode_tarif = "1";
                                btn_mode_tarif.setText("Tarif 1");
                            }
                        } else if (btn_mode_tarif.getText().toString().equals("Tarif 3")) {
                            if (params.prix_4 == 1) {
                                bon1_temp.mode_tarif = "4";
                                btn_mode_tarif.setText("Tarif 4");
                            } else {
                                bon1_temp.mode_tarif = "1";
                                btn_mode_tarif.setText("Tarif 1");
                            }
                        } else if (btn_mode_tarif.getText().toString().equals("Tarif 4")) {
                            if (params.prix_5 == 1) {
                                bon1_temp.mode_tarif = "5";
                                btn_mode_tarif.setText("Tarif 5");
                            } else {
                                bon1_temp.mode_tarif = "1";
                                btn_mode_tarif.setText("Tarif 1");
                            }
                        } else if (btn_mode_tarif.getText().toString().equals("Tarif 5")) {
                            if (params.prix_6 == 1) {
                                bon1_temp.mode_tarif = "6";
                                btn_mode_tarif.setText("Tarif 6");
                            } else {
                                bon1_temp.mode_tarif = "1";
                                btn_mode_tarif.setText("Tarif 1");
                            }
                        } else if (btn_mode_tarif.getText().toString().equals("Tarif 6")) {
                            bon1_temp.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }

                        sauvegarder();

                    } else {
                        return;
                    }

                }else{

                    if(selectedTitle.equals(params.pv1_titre)){
                        btn_mode_tarif.setText("Tarif 1");
                        bon1_temp.mode_tarif = "1";
                    }else if(selectedTitle.equals(params.pv2_titre)){
                        btn_mode_tarif.setText("Tarif 2");
                        bon1_temp.mode_tarif = "2";
                    }else if(selectedTitle.equals(params.pv3_titre)){
                        btn_mode_tarif.setText("Tarif 3");
                        bon1_temp.mode_tarif = "3";
                    } else if(selectedTitle.equals(params.pv4_titre)){
                        btn_mode_tarif.setText("Tarif 4");
                        bon1_temp.mode_tarif = "4";
                    } else if(selectedTitle.equals(params.pv5_titre)){
                        btn_mode_tarif.setText("Tarif 5");
                        bon1_temp.mode_tarif = "5";
                    } else if(selectedTitle.equals(params.pv6_titre)){
                        btn_mode_tarif.setText("Tarif 6");
                        bon1_temp.mode_tarif = "6";
                    }

                }



                break;
            case R.id.addProduct:
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                if (bon1_temp.client.isEmpty()) {

                    Crouton.makeText(ActivityOrderClient.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }

                if (!prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Important !")
                            .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                            .show();

                    return;
                }


                // Initialize activity
                Activity activity;
                // define activity of this class//
                activity = ActivityOrderClient.this;
                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(), bon1_temp.mode_tarif, "VENTE");

                break;

            case R.id.valide_facture:

                if (bon1_temp.client.isEmpty()) {

                    Crouton.makeText(ActivityOrderClient.this, "Vous devez Séléctionner un client", Style.ALERT).show();
                    return;
                }
                if (final_panier.isEmpty()) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
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
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return;
                }

                // Initialize activity
                Activity mactivity;

                // define activity of this class//
                mactivity = ActivityOrderClient.this;

                FragmentRemise fragmentRemise = new FragmentRemise();
                fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);
                break;
            case R.id.btn_scan_produit:
                if (ContextCompat.checkSelfPermission(ActivityOrderClient.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ActivityOrderClient.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                } else {
                    if (bon1_temp.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon est déja validé")
                                .show();
                        return;
                    }
                    if (bon1_temp.client.isEmpty()) {
                        Crouton.makeText(ActivityOrderClient.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                        return;
                    }

                    startScanProduct();
                }
                break;
            case R.id.btn_mofifier_bon:
                if (!SOURCE_EXPORT.equals("EXPORTED")) {
                    if (!bon1_temp.blocage.equals("F")) {
                        new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon n'est pas encore validé")
                                .show();
                        return;

                    } else {
                        new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Modification")
                                .setContentText("Voulez-vous vraiment Modifier ce Bon ?")
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    try {
                                        if (controller.modifier_bon1_sql("BON1_TEMP", bon1_temp.num_bon, bon1_temp)) {
                                            bon1_temp.blocage = "M";
                                            validate_theme();
                                        }
                                    } catch (Exception e) {

                                        new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("Attention!")
                                                .setContentText("problème lors de Modification de Bon : " + e.getMessage())
                                                .show();
                                    }
                                    sDialog.dismiss();
                                }).show();
                    }
                } else {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja exporté")
                            .show();
                }

                break;
            case R.id.btn_imp_bon:
                if (!bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon n'est pas encore validé")
                            .show();
                    return;
                }

                if (Objects.equals(prefs.getString("LANGUE_TICKET", "LATIN"), "LATIN")) {
                    Activity bactivity;
                    bactivity = ActivityOrderClient.this;

                    Printing printer = new Printing();
                    printer.start_print_bon_vente(bactivity, "ORDER", final_panier, bon1_temp);
                } else {
                    Intent html_intent = new Intent(this, ActivityHtmlView.class);
                    html_intent.putExtra("TYPE_BON", "COMMANDE");
                    html_intent.putExtra("BON1", bon1_temp);
                    html_intent.putExtra("BON2", final_panier);
                    startActivity(html_intent);
                }


                break;

        }
    }


    protected void showListClient() {
        // Initialize activity
        Activity activity;
        // define activity of this class//
        activity = ActivityOrderClient.this;
        FragmentSelectClient fragmentSelectClient = new FragmentSelectClient();
        fragmentSelectClient.showDialogbox(activity, getBaseContext(), "FROM_ORDER_CLIENT");

    }

    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent) {

        onClientSelected(clientEvent.getClient(), true);

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

    protected void onClientSelected(PostData_Client client_s, boolean isUpdate) {
        client_selected = client_s;
        btn_select_client.setText(client_selected.client);

        String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

        if (TYPE_ACTIVITY.equals("NEW_SALE") || isUpdate) {
            // Remplir bon1 avec les données du client
            bon1_temp.ancien_solde = client_selected.solde_montant;
            bon1_temp.code_client = client_selected.code_client;
            bon1_temp.client = client_selected.client;
            bon1_temp.adresse = client_selected.adresse;
            bon1_temp.tel = client_selected.tel;
            bon1_temp.rc = client_selected.rc;
            bon1_temp.ifiscal = client_selected.ifiscal;
            bon1_temp.ai = client_selected.ai;
            bon1_temp.nis = client_selected.nis;

            bon1_temp.blocage = "M";
            bon1_temp.mode_tarif = "1";
            btn_mode_tarif.setText("Tarif 1");
        }

        appliquerTarif(selectedTitle, client_selected.mode_tarif);

        if (!controller.insert_into_bon1("BON1_TEMP", bon1_temp)) {
            finish();
        }
    }

    // Méthode de gestion du tarif
    private void appliquerTarif(String selectedTitle, String modeTarifClient) {
        Map<String, String> titresMap = new HashMap<>();
        titresMap.put(params.pv1_titre, "1");
        titresMap.put(params.pv2_titre, "2");
        titresMap.put(params.pv3_titre, "3");
        titresMap.put(params.pv4_titre, "4");
        titresMap.put(params.pv5_titre, "5");
        titresMap.put(params.pv6_titre, "6");

        String tarif = "1"; // par défaut

        if ("Libre".equals(selectedTitle)) {
            if (modeTarifClient != null && modeTarifClient.matches("[2-6]")) {
                tarif = modeTarifClient;
            }
        } else if (titresMap.containsKey(selectedTitle)) {
            tarif = titresMap.get(selectedTitle);
        }

        bon1_temp.mode_tarif = tarif;
        btn_mode_tarif.setText("Tarif " + tarif);
    }

    protected void initData() {
        final_panier = controller.select_bon2_from_database("SELECT " +
                "BON2_TEMP.RECORDID, " +
                "BON2_TEMP.CODE_BARRE, " +
                "BON2_TEMP.NUM_BON, " +
                "BON2_TEMP.PRODUIT, " +
                "BON2_TEMP.NBRE_COLIS, " +
                "BON2_TEMP.COLISSAGE, " +
                "BON2_TEMP.QTE, " +
                "BON2_TEMP.QTE_GRAT, " +
                "BON2_TEMP.PV_HT, " +
                "BON2_TEMP.PA_HT, " +
                "BON2_TEMP.TVA, " +
                "BON2_TEMP.CODE_DEPOT, " +
                "BON2_TEMP.DESTOCK_TYPE, " +
                "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                "BON2_TEMP.DESTOCK_QTE, " +

                "PRODUIT.ISNEW, " +
                "PRODUIT.PV_LIMITE, " +
                "PRODUIT.STOCK, " +
                "PRODUIT.PROMO, " +
                "PRODUIT.QTE_PROMO, " +
                "PRODUIT.D1, " +
                "PRODUIT.D2, " +
                "PRODUIT.PP1_HT " +

                "FROM BON2_TEMP " +
                "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                "WHERE BON2_TEMP.NUM_BON = '" + bon1_temp.num_bon + "'");

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

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
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Suppression")
                        .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                        .setCancelText("Anuuler")
                        .setConfirmText("Supprimer")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            try {
                                SOURCE = "BON2_TEMP_DELETE";
                                controller.delete_from_bon2("BON2_TEMP", final_panier.get(info.position).recordid, final_panier.get(info.position));
                                initData();
                                //PanierAdapter.RefrechPanier(final_panier);
                                PanierAdapter = new ListViewAdapterPanierVente(ActivityOrderClient.this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);
                                expandableListView.setAdapter(PanierAdapter);

                            } catch (Exception e) {

                                new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention!")
                                        .setContentText("problème lors suppression produits! : " + e.getMessage())
                                        .show();
                            }

                            sDialog.dismiss();
                        }).show();
                return true;

            case R.id.edit_produit:
                if (bon1_temp.blocage.equals("F")) {
                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja validé")
                            .show();
                    return true;
                }
                try {
                    SOURCE = "BON2_TEMP_EDIT";
                    Activity activity;
                    double last_price = controller.select_last_price_from_database("BON1_TEMP", bon1_temp.code_client, final_panier.get(info.position).codebarre);
                    activity = ActivityOrderClient.this;
                    FragmentQteVente fragmentqte = new FragmentQteVente();
                    fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position), last_price);

                } catch (Exception e) {

                    new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.ERROR_TYPE)
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
        //val_timbre = 0.00;
        // val_remise = 0.00;
        val_total_ttc = 0.00;
        val_total_ttc_remise = 0.00;
        val_total_achat_ht = 0.00;

        for (int k = 0; k < final_panier.size(); k++) {

            double total_montant_produit = final_panier.get(k).pv_ht * final_panier.get(k).qte;
            double montant_tva_produit = total_montant_produit * ((final_panier.get(k).tva) / 100);
            val_total_ht = val_total_ht + total_montant_produit;
            val_tva = val_tva + montant_tva_produit;

            val_total_achat_ht = val_total_achat_ht + (final_panier.get(k).gratuit + final_panier.get(k).qte) * final_panier.get(k).pa_ht;

        }


        if (checkBox_timbre.isChecked()) {
            val_total_ttc = (val_total_ht) + val_tva;
            if ((val_total_ttc * 0.01) >= 2500) {
                val_timbre = 2500.00;
            } else {
                //float nnn = (val_total_ttc * (1/100));
                val_timbre = (val_total_ttc * 0.01);
            }
        } else {
            val_timbre = 0.0;
        }

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

            if (!bon1_temp.blocage.equals("F")) {
                new SweetAlertDialog(ActivityOrderClient.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas validé")
                        .show();
            } else {
                Activity mActivity;
                mActivity = ActivityOrderClient.this;

                GeneratePDF generate_pdf = new GeneratePDF();
                try {
                    generate_pdf.startPDFVente(mActivity, bon1_temp, final_panier, "FROM_ORDER");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }


    //Versement
    protected void sauvegarder() {

        bon1_temp.code_client = client_selected.code_client;
        bon1_temp.client = client_selected.client;
        bon1_temp.code_vendeur = "000000";
        //bon1_temp.mode_tarif = client_selected.mode_tarif;
        //bon1_temp.solde_ancien = client_selected.solde_montant;
        bon1_temp.nbr_p = final_panier.size();


        bon1_temp.tot_ht = val_total_ht;
        bon1_temp.tot_tva = val_tva;
        bon1_temp.timbre = val_timbre;
        bon1_temp.tot_ttc = val_total_ttc;
        bon1_temp.remise = val_remise;
        bon1_temp.montant_bon = val_total_ttc_remise;
        bon1_temp.montant_achat = val_total_achat_ht;
        //update current bon1
        controller.update_bon1("BON1_TEMP", bon1_temp.num_bon, bon1_temp);

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
    }

    public void Sound(int resid) {
        MediaPlayer mp = MediaPlayer.create(this, resid);
        mp.start();
    }

    @Subscribe
    public void onEvent(LocationEvent event) {

        Log.e("TRACKKK", "Recieved location vente : " + event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

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
                .withActivity(ActivityOrderClient.this)
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
        //stopService(intent_location);
        super.onDestroy();
    }


    @Override
    public void onClick(View v, int position, PostData_Produit item) throws ParseException {

        PostData_Bon2 bon2_temp = new PostData_Bon2();
        bon2_temp.produit = item.produit;
        bon2_temp.codebarre = item.code_barre;
        bon2_temp.stock_produit = item.stock;
        bon2_temp.destock_type = item.destock_type;
        bon2_temp.destock_code_barre = item.destock_code_barre;
        bon2_temp.destock_qte = item.destock_qte;
        bon2_temp.pa_ht = item.pamp;

        bon2_temp.tva = item.tva;
        bon2_temp.colissage = item.colissage;
        bon2_temp.promo = item.promo;
        bon2_temp.d1 = item.d1;
        bon2_temp.d2 = item.d2;
        bon2_temp.pp1_ht = item.pp1_ht;
        bon2_temp.qte_promo = item.qte_promo;

        switch (bon1_temp.mode_tarif) {
            case "6" -> bon2_temp.pv_ht = item.pv6_ht;
            case "5" -> bon2_temp.pv_ht = item.pv5_ht;
            case "4" -> bon2_temp.pv_ht = item.pv4_ht;
            case "3" -> bon2_temp.pv_ht = item.pv3_ht;
            case "2" -> bon2_temp.pv_ht = item.pv2_ht;
            default -> bon2_temp.pv_ht = item.pv1_ht;
        }

        bon2_temp.num_bon = NUM_BON;
        bon2_temp.code_depot = CODE_DEPOT;
        SOURCE = "BON2_TEMP_INSERT";
        double last_price = controller.select_last_price_from_database("BON1_TEMP", bon1_temp.code_client, bon2_temp.codebarre);
        Activity activity = ActivityOrderClient.this;
        FragmentQteVente fragmentqte = new FragmentQteVente();
        fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), bon2_temp, last_price);

        //Save clicked item position in list
        //save permanently
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", position).apply();
    }

    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventBon2 item_panier) {

        try {
            if (SOURCE.equals("BON2_TEMP_INSERT")) {
                if (item_panier.getIfExist()) {
                    controller.update_into_bon2("BON2_TEMP", NUM_BON, item_panier.getData(), item_panier.getQteOld(), item_panier.getGratuitOld());
                } else {
                    controller.insert_into_bon2("BON2_TEMP", NUM_BON, CODE_DEPOT, item_panier.getData());
                }
            } else if (SOURCE.equals("BON2_TEMP_EDIT")) {
                controller.update_into_bon2("BON2_TEMP", NUM_BON, item_panier.getData(), item_panier.getQteOld(), item_panier.getGratuitOld());
            }

            initData();
            if (prefs.getBoolean("ENABLE_SOUND", false)) {
                Sound(R.raw.cashier_quotka);
            }

            if (prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                // Initialize activity
                Activity activity;
                // define activity of this class//
                activity = ActivityOrderClient.this;
                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(), bon1_temp.mode_tarif, "VENTE");
            }

        } catch (Exception e) {
            Crouton.makeText(ActivityOrderClient.this, "Erreur : " + e.getMessage(), Style.ALERT).show();
        }



    }


    @Subscribe
    public void onVersementReceived(ValidateFactureEvent versement) {

        bon1_temp.verser = 0.0;
        bon1_temp.mode_rg = "A TERME";

        bon1_temp.reste = bon1_temp.ancien_solde + (bon1_temp.tot_ht + bon1_temp.tot_tva + bon1_temp.timbre - bon1_temp.remise) - bon1_temp.verser;

        Calendar c = Calendar.getInstance();
        bon1_temp.date_f = date_format.format(c.getTime());
        bon1_temp.heure_f = heure_format.format(c.getTime());

        //bon1_temp.diff_time =
        if (controller.validate_bon1_sql("BON1_TEMP", bon1_temp)) {
            bon1_temp.blocage = "F";
        }

        validate_theme();

    }

    public void validate_theme() {
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

    private void selectProductFromScan(String resultscan) throws ParseException {
        ArrayList<PostData_Produit> produits;
        PostData_Bon2 bon2_temp = new PostData_Bon2();

        ///////////////////////////////////CODE BARRE //////////////////////////////////////
        ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

        String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN FROM CODEBARRE WHERE CODE_BARRE != '" + resultscan + "' AND CODE_BARRE_SYN = '" + resultscan + "' ";
        codebarres = controller.select_all_codebarre_from_database(querry_codebarre);
        if(!codebarres.isEmpty()){
            resultscan = codebarres.get(0).code_barre;
        }
        ///////////////////////////////////CODE BARRE //////////////////////////////////////

        String querry = "";

        if(show_picture_prod){
            querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PTODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
        }else{
            querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PTODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";
        }

        produits = controller.select_produits_from_database(querry, show_picture_prod);

        if (produits.size() == 1) {
            bon2_temp.num_bon = NUM_BON;
            bon2_temp.code_depot = CODE_DEPOT;
            bon2_temp.produit = produits.get(0).produit;
            bon2_temp.codebarre = produits.get(0).code_barre;
            bon2_temp.stock_produit = produits.get(0).stock;
            bon2_temp.destock_type = produits.get(0).destock_type;
            bon2_temp.destock_code_barre = produits.get(0).destock_code_barre;
            bon2_temp.destock_qte = produits.get(0).destock_qte;
            //bon2_temp.pa_ht = produits.get(0).pa_ht;
            bon2_temp.tva = produits.get(0).tva;
            bon2_temp.colissage = produits.get(0).colissage;
            bon2_temp.promo = produits.get(0).promo;
            bon2_temp.d1 = produits.get(0).d1;
            bon2_temp.d2 = produits.get(0).d2;
            bon2_temp.pp1_ht = produits.get(0).pp1_ht;
            bon2_temp.qte_promo = produits.get(0).qte_promo;

            switch (bon1_temp.mode_tarif) {
                case "6" -> bon2_temp.pv_ht = produits.get(0).pv6_ht;
                case "5" -> bon2_temp.pv_ht = produits.get(0).pv5_ht;
                case "4" -> bon2_temp.pv_ht = produits.get(0).pv4_ht;
                case "3" -> bon2_temp.pv_ht = produits.get(0).pv3_ht;
                case "2" -> bon2_temp.pv_ht = produits.get(0).pv2_ht;
                default -> bon2_temp.pv_ht = produits.get(0).pv1_ht;
            }


            SOURCE = "BON2_TEMP_INSERT";
            Activity activity = ActivityOrderClient.this;
            double last_price = controller.select_last_price_from_database("BON1_TEMP", bon1_temp.code_client, bon2_temp.codebarre);
            FragmentQteVente fragmentqte = new FragmentQteVente();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), bon2_temp, last_price);

        } else if (produits.size() > 1) {
            Crouton.makeText(ActivityOrderClient.this, "Attention il y a 2 produits avec le meme code !", Style.ALERT).show();
        } else {
            Crouton.makeText(ActivityOrderClient.this, "Produit introuvable !", Style.ALERT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    assert imageBitmap != null;
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100 /* Ignored for PNGs */, blob);
                    byte[] inputData = blob.toByteArray();
                    bus.post(new ByteDataEvent(inputData));
                }
            }
        } else if (requestCode == 4000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    InputStream iStream;
                    try {
                        iStream = getContentResolver().openInputStream(selectedImage);
                        byte[] inputData = getBytes(iStream);
                        bus.post(new ByteDataEvent(inputData));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}

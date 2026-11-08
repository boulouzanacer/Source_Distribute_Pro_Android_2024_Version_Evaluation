package com.safesoft.proapp.distribute.activities.vente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.text.InputType;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.adapters.note_adapter.NotePickerAdapter;
import com.safesoft.proapp.distribute.eventsClasses.ByteDataEvent;
import com.safesoft.proapp.distribute.eventsClasses.PrintEvent;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterPanierVente;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;
import com.safesoft.proapp.distribute.fragments.FragmentQteVente;
import com.safesoft.proapp.distribute.fragments.FragmentRemise;
import com.safesoft.proapp.distribute.fragments.FragmentSelectClient;
import com.safesoft.proapp.distribute.fragments.FragmentSelectProduct;
import com.safesoft.proapp.distribute.fragments.FragmentValideBon;
import com.safesoft.proapp.distribute.gps.service_location.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.utils.Env;
import com.safesoft.proapp.distribute.utils.StandardFunctions;

import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

import static com.safesoft.proapp.distribute.R.id.timbre;

public class ActivityVente extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick {


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
    private String NUM_BON;
    private String CODE_DEPOT;
    private PostData_Client client_selected;
    private PostData_Bon1 bon1;
    private String SOURCE;
    //private ExpandableHeightListView expandableListView;
    private ListView expandableListView;
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
    private Toolbar toolbar;
    private PopupWindow notePopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vente);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            //  WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityVente.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        bus = EventBus.getDefault();
        controller = new DATABASE(this);
        bon1 = new PostData_Bon1();
        final_panier = new ArrayList<>();

        toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        String date_time_sub_title = null;

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);

        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

        initViews();

        //get num bon
        if (getIntent() != null) {
            TYPE_ACTIVITY = getIntent().getStringExtra("TYPE_ACTIVITY");
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        } else {
            Crouton.makeText(ActivityVente.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }

        date_format = new SimpleDateFormat("dd/MM/yyyy");
        heure_format = new SimpleDateFormat("HH:mm:ss");

        if (TYPE_ACTIVITY.equals("NEW_SALE")) {
            //get num bon
            String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM BON1 WHERE NUM_BON IS NOT NULL";
            NUM_BON = controller.select_max_num_bon(selectQuery);
            // get date and time
            Calendar c = Calendar.getInstance();
            String formattedDate_Show = date_format.format(c.getTime());
            // formattedDate = df_save.format(c.getTime());
            String currentTime = heure_format.format(c.getTime());

            date_time_sub_title = formattedDate_Show + " " + currentTime;

            bon1.date_bon = formattedDate_Show;
            bon1.heure = currentTime;
            bon1.num_bon = NUM_BON;
            bon1.blocage = "";
            bon1.client = "";
            bon1.code_depot = CODE_DEPOT;


        } else if (TYPE_ACTIVITY.equals("EDIT_SALE")) {
            //get num bon
            if (getIntent() != null) {
                NUM_BON = getIntent().getStringExtra("NUM_BON");
            }

            String querry = "SELECT " +
                    "BON1.RECORDID, " +
                    "BON1.NUM_BON, " +
                    "BON1.DATE_BON, " +
                    "BON1.HEURE, " +
                    "BON1.MODE_RG, " +
                    "BON1.MODE_TARIF, " +

                    "BON1.NBR_P, " +
                    "BON1.TOT_QTE, " +

                    "BON1.TOT_HT, " +
                    "BON1.TOT_TVA, " +
                    "BON1.TIMBRE, " +
                    "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE AS TOT_TTC, " +
                    "BON1.REMISE, " +
                    "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE AS MONTANT_BON, " +
                    "BON1.MONTANT_ACHAT, " +
                    "BON1.REMARQUE, " +

                    "BON1.ANCIEN_SOLDE, " +
                    "BON1.VERSER, " +
                    "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                    "BON1.CODE_CLIENT, " +
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

                    "BON1.LATITUDE, " +
                    "BON1.LONGITUDE, " +
                    "BON1.LIVRER, " +
                    "BON1.DATE_LIV, " +
                    "BON1.IS_IMPORTED, " +

                    "BON1.CODE_DEPOT, " +
                    "BON1.CODE_VENDEUR, " +
                    "BON1.EXPORTATION, " +
                    "BON1.BLOCAGE " +
                    "FROM BON1 " +
                    "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                    "WHERE BON1.NUM_BON ='" + NUM_BON + "'";
            ///////////////////////////////////
            bon1 = controller.select_bon1_from_database2(querry);


            final_panier = controller.select_bon2_from_database("SELECT " +
                    "BON2.RECORDID, " +
                    "BON2.CODE_BARRE, " +
                    "BON2.NUM_BON, " +
                    "BON2.PRODUIT, " +
                    "BON2.NBRE_COLIS, " +
                    "BON2.COLISSAGE, " +
                    "BON2.QTE, " +
                    "BON2.QTE_GRAT, " +
                    "BON2.PV_HT, " +
                    "BON2.PA_HT, " +
                    "BON2.TVA, " +
                    "BON2.CODE_DEPOT, " +
                    "BON2.DESTOCK_TYPE, " +
                    "BON2.DESTOCK_CODE_BARRE, " +
                    "BON2.DESTOCK_QTE, " +

                    "PRODUIT.ISNEW, " +
                    "PRODUIT.PV_LIMITE, " +
                    "PRODUIT.STOCK, " +
                    "PRODUIT.PROMO, " +
                    "PRODUIT.QTE_PROMO, " +
                    "PRODUIT.D1, " +
                    "PRODUIT.D2, " +
                    "PRODUIT.PP1_HT " +

                    "FROM BON2 " +
                    "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE BON2.NUM_BON = '" + NUM_BON + "'");
            //private String formattedDate;
            date_time_sub_title = bon1.date_bon + " " + bon1.heure;

            client_selected = controller.select_client_from_database(bon1.code_client);
            onClientSelected(client_selected, false);

            // Create the adapter to convert the array to views
            PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

            expandableListView = findViewById(R.id.expandable_listview);
            expandableListView.setAdapter(PanierAdapter);
            registerForContextMenu(expandableListView);

            registerForContextMenu(expandableListView);

            val_timbre = bon1.timbre;
            val_remise = bon1.remise;

            checkBox_timbre.setChecked(false);
            if (val_timbre != 0) checkBox_timbre.setChecked(true);

            calcule();


        } else {
            Crouton.makeText(ActivityVente.this, "Erreur séléction activity !", Style.ALERT).show();
            return;
        }


        if (NUM_BON != null) {
            getSupportActionBar().setTitle("Vente N°: " + NUM_BON);
        }
        getSupportActionBar().setSubtitle(date_time_sub_title);
        validate_theme();


        // Register as a subscriber
        bus.register(this);


        checkBox_timbre.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (bon1.blocage.equals("F")) {
                checkBox_timbre.setChecked(!isChecked);
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
            }

            if (client_selected == null) {
                Toast.makeText(ActivityVente.this, "Veuilez sélectionner un Client", Toast.LENGTH_SHORT).show();
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

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
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
        int viewId = v.getId();

        if (viewId == R.id.btn_select_client) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return;
            }
            showListClient();

        } else if (viewId == R.id.btn_mode_tarif) {

            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return;
            }

            if (bon1.client.isEmpty()) {
                Crouton.makeText(ActivityVente.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                return;
            }

            String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

            if (selectedTitle.equals("Libre")) {

                if (client_selected.mode_tarif.equals("0")) {

                    if (btn_mode_tarif.getText().toString().equals("Tarif 1")) {
                        if (params.prix_2 == 1) {
                            bon1.mode_tarif = "2";
                            btn_mode_tarif.setText("Tarif 2");
                        } else {
                            bon1.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }
                    } else if (btn_mode_tarif.getText().toString().equals("Tarif 2")) {
                        if (params.prix_3 == 1) {
                            bon1.mode_tarif = "3";
                            btn_mode_tarif.setText("Tarif 3");
                        } else {
                            bon1.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }
                    } else if (btn_mode_tarif.getText().toString().equals("Tarif 3")) {
                        if (params.prix_4 == 1) {
                            bon1.mode_tarif = "4";
                            btn_mode_tarif.setText("Tarif 4");
                        } else {
                            bon1.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }
                    } else if (btn_mode_tarif.getText().toString().equals("Tarif 4")) {
                        if (params.prix_5 == 1) {
                            bon1.mode_tarif = "5";
                            btn_mode_tarif.setText("Tarif 5");
                        } else {
                            bon1.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }
                    } else if (btn_mode_tarif.getText().toString().equals("Tarif 5")) {
                        if (params.prix_6 == 1) {
                            bon1.mode_tarif = "6";
                            btn_mode_tarif.setText("Tarif 6");
                        } else {
                            bon1.mode_tarif = "1";
                            btn_mode_tarif.setText("Tarif 1");
                        }
                    } else if (btn_mode_tarif.getText().toString().equals("Tarif 6")) {
                        bon1.mode_tarif = "1";
                        btn_mode_tarif.setText("Tarif 1");
                    }

                    sauvegarder();
                }

            } else {

                if (selectedTitle.equals(params.pv1_titre)) {
                    btn_mode_tarif.setText("Tarif 1");
                    bon1.mode_tarif = "1";
                } else if (selectedTitle.equals(params.pv2_titre)) {
                    btn_mode_tarif.setText("Tarif 2");
                    bon1.mode_tarif = "2";
                } else if (selectedTitle.equals(params.pv3_titre)) {
                    btn_mode_tarif.setText("Tarif 3");
                    bon1.mode_tarif = "3";
                } else if (selectedTitle.equals(params.pv4_titre)) {
                    btn_mode_tarif.setText("Tarif 4");
                    bon1.mode_tarif = "4";
                } else if (selectedTitle.equals(params.pv5_titre)) {
                    btn_mode_tarif.setText("Tarif 5");
                    bon1.mode_tarif = "5";
                } else if (selectedTitle.equals(params.pv6_titre)) {
                    btn_mode_tarif.setText("Tarif 6");
                    bon1.mode_tarif = "6";
                }
            }

        } else if (viewId == R.id.addProduct) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return;
            }

            if (bon1.client.isEmpty()) {
                Crouton.makeText(ActivityVente.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                return;
            }

            if (!prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Important !")
                        .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                        .show();

                return;
            }

            Activity activity;
            activity = ActivityVente.this;
            FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
            fragmentSelectProduct.showDialogbox(activity, getBaseContext(), bon1.mode_tarif, "VENTE");

        } else if (viewId == R.id.valide_facture) {
            if (bon1.client.isEmpty()) {
                Crouton.makeText(ActivityVente.this, "Vous devez Séléctionner un client", Style.ALERT).show();
                return;
            }
            if (final_panier.isEmpty()) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
            }
            if (bon1.blocage.equals("F")) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
            }
            if (isBonAnnule()) {
                showBonLockedAlert(true);
                return;
            }
            FragmentValideBon fragmentvalider = new FragmentValideBon();
            fragmentvalider.showDialogbox(ActivityVente.this, bon1.ancien_solde, bon1.montant_bon, bon1.verser, "VALIDATE_VENTE");

        } else if (viewId == R.id.txv_remise_btn) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return;
            }

            Activity mactivity;
            mactivity = ActivityVente.this;
            FragmentRemise fragmentRemise = new FragmentRemise();
            fragmentRemise.showDialogbox(mactivity, val_total_ttc, val_remise);

        } else if (viewId == R.id.btn_scan_produit) {
            if (ContextCompat.checkSelfPermission(ActivityVente.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityVente.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

            } else {
                if (isBonVerrouille()) {
                    showBonLockedAlert(isBonAnnule());
                    return;
                }
                if (bon1.client.isEmpty()) {

                    Crouton.makeText(ActivityVente.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
                    return;
                }
                startScanProduct();
            }

        } else if (viewId == R.id.btn_mofifier_bon) {
            if (isBonAnnule()) {
                showBonLockedAlert(true);
                return;
            }
            if (!bon1.blocage.equals("F")) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas encore validé")
                        .show();
                return;

            }
            if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
                if (!SOURCE_EXPORT.equals("EXPORTED")) {

                    new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Modification")
                            .setContentText("Voulez-vous vraiment Modifier ce Bon ?")
                            .setCancelText("Non")
                            .setConfirmText("Oui")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                try {

                                    if (controller.modifier_bon1_sql("BON1", bon1.num_bon, bon1)) {
                                        bon1.blocage = "M";
                                        validate_theme();
                                    }


                                } catch (Exception e) {

                                    new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("Attention!")
                                            .setContentText("problème lors de Modification de Bon : " + e.getMessage())
                                            .show();
                                }
                                sDialog.dismiss();
                            }).show();
                } else {
                    new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Information!")
                            .setContentText("Ce bon est déja exporté")
                            .show();
                }
            } else {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention!!")
                        .setContentText("Vous n'avez pas l'autorisation de modifier, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                        .show();
            }

        } else if (viewId == R.id.btn_imp_bon) {
            if (!bon1.blocage.equals("F")) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas encore validé")
                        .show();
                return;
            }
            if (Objects.equals(prefs.getString("LANGUE_TICKET", "LATIN"), "LATIN")) {
                Activity bactivity;
                bactivity = ActivityVente.this;

                Printing printer = new Printing();
                printer.start_print_bon_vente(bactivity, "VENTE", final_panier, bon1);
            } else {
                Intent html_intent = new Intent(this, ActivityHtmlView.class);
                html_intent.putExtra("TYPE_BON", "VENTE");
                html_intent.putExtra("BON1", bon1);
                html_intent.putExtra("BON2", final_panier);
                startActivity(html_intent);
            }
        }
    }





    protected void showListClient() {
        // Initialize activity
        Activity activity;

        // define activity of this class//
        activity = ActivityVente.this;

        FragmentSelectClient fragmentSelectClient = new FragmentSelectClient();
        fragmentSelectClient.showDialogbox(activity, getBaseContext(), "FROM_VENTE");

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

        if (TYPE_ACTIVITY.equals("NEW_SALE") || isUpdate) {

            bon1.ancien_solde = client_selected.solde_client;  // Modifier le 03/02/2023
            bon1.code_client = client_selected.code_client;
            bon1.client = client_selected.client;
            bon1.adresse = client_selected.adresse;
            bon1.tel = client_selected.tel;
            bon1.rc = client_selected.rc;
            bon1.ifiscal = client_selected.ifiscal;
            bon1.ai = client_selected.ai;
            bon1.nis = client_selected.nis;

            bon1.blocage = "M";
            bon1.mode_tarif = "1";
            btn_mode_tarif.setText("Tarif 1");

            String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

            if (selectedTitle.equals("Libre")) {

                if (client_selected.mode_tarif != null) {
                    switch (client_selected.mode_tarif) {
                        case "2" -> {
                            btn_mode_tarif.setText("Tarif 2");
                            bon1.mode_tarif = "2";
                        }
                        case "3" -> {
                            btn_mode_tarif.setText("Tarif 3");
                            bon1.mode_tarif = "3";
                        }
                        case "4" -> {
                            btn_mode_tarif.setText("Tarif 4");
                            bon1.mode_tarif = "4";
                        }
                        case "5" -> {
                            btn_mode_tarif.setText("Tarif 5");
                            bon1.mode_tarif = "5";
                        }
                        case "6" -> {
                            btn_mode_tarif.setText("Tarif 6");
                            bon1.mode_tarif = "6";
                        }
                        default -> {
                            btn_mode_tarif.setText("Tarif 1");
                            bon1.mode_tarif = "1";
                        }
                    }
                } else {
                    // tarif 1
                    btn_mode_tarif.setText("Tarif 1");
                    bon1.mode_tarif = "1";
                }

            }else{
                if(selectedTitle.equals(params.pv1_titre)){
                    btn_mode_tarif.setText("Tarif 1");
                    bon1.mode_tarif = "1";
                }else if(selectedTitle.equals(params.pv2_titre)){
                    btn_mode_tarif.setText("Tarif 2");
                    bon1.mode_tarif = "2";
                }else if(selectedTitle.equals(params.pv3_titre)){
                    btn_mode_tarif.setText("Tarif 3");
                    bon1.mode_tarif = "3";
                } else if(selectedTitle.equals(params.pv4_titre)){
                    btn_mode_tarif.setText("Tarif 4");
                    bon1.mode_tarif = "4";
                } else if(selectedTitle.equals(params.pv5_titre)){
                    btn_mode_tarif.setText("Tarif 5");
                    bon1.mode_tarif = "5";
                } else if(selectedTitle.equals(params.pv6_titre)){
                    btn_mode_tarif.setText("Tarif 6");
                    bon1.mode_tarif = "6";
                }
            }

        } else {

            String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

            if (selectedTitle.equals("Libre")) {
                switch (client_selected.mode_tarif) {
                    case "2" -> {
                        btn_mode_tarif.setText("Tarif 2");
                        bon1.mode_tarif = "2";
                    }
                    case "3" -> {
                        btn_mode_tarif.setText("Tarif 3");
                        bon1.mode_tarif = "3";
                    }
                    case "4" -> {
                        btn_mode_tarif.setText("Tarif 4");
                        bon1.mode_tarif = "4";
                    }
                    case "5" -> {
                        btn_mode_tarif.setText("Tarif 5");
                        bon1.mode_tarif = "5";
                    }
                    case "6" -> {
                        btn_mode_tarif.setText("Tarif 6");
                        bon1.mode_tarif = "6";
                    }
                    default -> {
                        btn_mode_tarif.setText("Tarif 1");
                        bon1.mode_tarif = "1";
                    }
                }
            }else{

                if(selectedTitle.equals(params.pv1_titre)){
                    btn_mode_tarif.setText("Tarif 1");
                    bon1.mode_tarif = "1";
                }else if(selectedTitle.equals(params.pv2_titre)){
                    btn_mode_tarif.setText("Tarif 2");
                    bon1.mode_tarif = "2";
                }else if(selectedTitle.equals(params.pv3_titre)){
                    btn_mode_tarif.setText("Tarif 3");
                    bon1.mode_tarif = "3";
                } else if(selectedTitle.equals(params.pv4_titre)){
                    btn_mode_tarif.setText("Tarif 4");
                    bon1.mode_tarif = "4";
                } else if(selectedTitle.equals(params.pv5_titre)){
                    btn_mode_tarif.setText("Tarif 5");
                    bon1.mode_tarif = "5";
                } else if(selectedTitle.equals(params.pv6_titre)){
                    btn_mode_tarif.setText("Tarif 6");
                    bon1.mode_tarif = "6";
                }
            }

        }


        if (!controller.insert_into_bon1("BON1", bon1)) {
            finish();
        }
    }

    protected void initData() {
        final_panier = controller.select_bon2_from_database("SELECT " +
                "BON2.RECORDID, " +
                "BON2.CODE_BARRE, " +
                "BON2.NUM_BON, " +
                "BON2.PRODUIT, " +
                "BON2.NBRE_COLIS, " +
                "BON2.COLISSAGE, " +
                "BON2.QTE, " +
                "BON2.QTE_GRAT, " +
                "BON2.PV_HT, " +
                "BON2.PA_HT, " +
                "BON2.TVA, " +
                "BON2.CODE_DEPOT, " +
                "BON2.DESTOCK_TYPE, " +
                "BON2.DESTOCK_CODE_BARRE, " +
                "BON2.DESTOCK_QTE, " +

                "PRODUIT.ISNEW, " +
                "PRODUIT.PV_LIMITE, " +
                "PRODUIT.STOCK, " +
                "PRODUIT.PROMO, " +
                "PRODUIT.QTE_PROMO, " +
                "PRODUIT.D1, " +
                "PRODUIT.D2, " +
                "PRODUIT.PP1_HT " +

                "FROM BON2 " +
                "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                "WHERE BON2.NUM_BON = '" + bon1.num_bon + "'");

        // Create the adapter to convert the array to views
        PanierAdapter = new ListViewAdapterPanierVente(this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);

        expandableListView = findViewById(R.id.expandable_listview);
        expandableListView.setAdapter(PanierAdapter);
        registerForContextMenu(expandableListView);

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
        int itemId = item.getItemId();
        if (itemId == R.id.delete_produit) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return true;
            }
            new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer le produit sélectionner ?")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        try {
                            SOURCE = "BON2_DELETE";
                            controller.delete_from_bon2("BON2", final_panier.get(info.position).recordid, final_panier.get(info.position));
                            initData();
                            PanierAdapter = new ListViewAdapterPanierVente(ActivityVente.this, R.layout.transfert2_items, final_panier, TYPE_ACTIVITY);
                            expandableListView.setAdapter(PanierAdapter);

                        } catch (Exception e) {

                            new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!")
                                    .setContentText("problème lors suppression produits! : " + e.getMessage())
                                    .show();
                        }

                        sDialog.dismiss();
                    }).show();
            return true;

        } else if (itemId == R.id.edit_produit) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return true;
            }
            try {
                SOURCE = "BON2_EDIT";
                Activity activity;
                activity = ActivityVente.this;
                double last_price = controller.select_last_price_from_database("BON1", bon1.code_client, final_panier.get(info.position).codebarre);
                FragmentQteVente fragmentqte = new FragmentQteVente();
                fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), final_panier.get(info.position), last_price);

            } catch (Exception e) {

                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Attention!")
                        .setContentText("Error : " + e.getMessage())
                        .show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    public void calcule() {

        val_total_ht = 0.00;
        val_tva = 0.00;
        //val_timbre = 0.00;
        //val_remise = 0.00;
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
            val_timbre = StandardFunctions.getTimbreValue(val_total_ttc);
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
        if (id == R.id.action_note) {
            if (isBonVerrouille()) {
                showBonLockedAlert(isBonAnnule());
                return true;
            }
            String note = bon1.remarque == null ? "" : bon1.remarque.trim();
            if (note.isEmpty()) {
                showNotesPickerDialog();
            } else {
                showSelectedNotePopup();
            }
            return true;
        }
        if (id == R.id.generate_pdf) {

            if (!bon1.blocage.equals("F")) {
                new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon n'est pas validé")
                        .show();
            } else {
                Activity mActivity;
                mActivity = ActivityVente.this;

                GeneratePDF generate_pdf = new GeneratePDF();
                try {
                    generate_pdf.startPDFVente(mActivity, bon1, final_panier, "FROM_SALE");
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    //Versement
    protected void sauvegarder() {
        if (isBonAnnule()) {
            return;
        }

        bon1.code_client = client_selected.code_client;
        bon1.client = client_selected.client;
        bon1.code_vendeur = "000000";
        //bon1.mode_tarif = client_selected.mode_tarif;
        // bon1.solde_ancien = client_selected.solde_montant;  // Modifier le 03/02/2023
        bon1.nbr_p = final_panier.size();


        bon1.tot_ht = val_total_ht;
        bon1.tot_tva = val_tva;
        bon1.timbre = val_timbre;
        bon1.tot_ttc = val_total_ttc;
        bon1.remise = val_remise;
        bon1.montant_bon = val_total_ttc_remise;
        bon1.montant_achat = val_total_achat_ht;
        //update current bon1
        controller.update_bon1("BON1", bon1.num_bon, bon1);

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
        super.onBackPressed();
    }

    @Subscribe
    public void onLocationEvent(LocationEvent event) {

        Log.e("TRACKKK", "Recieved location vente : " + event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

        bon1.latitude = event.getLocationData().getLatitude();
        bon1.longitude = event.getLocationData().getLongitude();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }


    private void startScanProduct() {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityVente.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    // Sound( R.raw.bleep);
                    // setRecycle(barcode.rawValue, true);
                    selectProductFromScan(barcode.rawValue);
                }).build();
        materialBarcodeScanner.startScan();
    }


    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        //stopService(intent_location);
        if (notePopupWindow != null) {
            notePopupWindow.dismiss();
            notePopupWindow = null;
        }
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
        bon2.pa_ht = item.pamp;

        bon2.promo = item.promo;
        bon2.d1 = item.d1;
        bon2.d2 = item.d2;
        bon2.pp1_ht = item.pp1_ht;
        bon2.qte_promo = item.qte_promo;

        switch (bon1.mode_tarif) {
            case "6" -> bon2.pv_ht = item.pv6_ht;
            case "5" -> bon2.pv_ht = item.pv5_ht;
            case "4" -> bon2.pv_ht = item.pv4_ht;
            case "3" -> bon2.pv_ht = item.pv3_ht;
            case "2" -> bon2.pv_ht = item.pv2_ht;
            default -> bon2.pv_ht = item.pv1_ht;
        }
        bon2.pv_limite = item.pv_limite;

        bon2.num_bon = NUM_BON;
        bon2.code_depot = CODE_DEPOT;
        SOURCE = "BON2_INSERT";
        double last_price = controller.select_last_price_from_database("BON1", bon1.code_client, bon2.codebarre);
        Activity activity = ActivityVente.this;
        FragmentQteVente fragmentqte = new FragmentQteVente();
        fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), bon2, last_price);

        //Save clicked item position in list
        //save permanently
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", position).apply();

    }


    @Subscribe
    public void onItemPanierReceive(CheckedPanierEventBon2 item_panier) {

        try {
            if (SOURCE.equals("BON2_INSERT")) {
                if (item_panier.getIfExist()) {
                    controller.update_into_bon2("BON2", NUM_BON, item_panier.getData(), item_panier.getQteOld(), item_panier.getGratuitOld());
                } else {
                    controller.insert_into_bon2("BON2", NUM_BON, CODE_DEPOT, item_panier.getData());
                }

            } else if (SOURCE.equals("BON2_EDIT")) {
                controller.update_into_bon2("BON2", NUM_BON, item_panier.getData(), item_panier.getQteOld(), item_panier.getGratuitOld());
            }

            initData();
            if (prefs.getBoolean("APP_ACTIVATED", false) && final_panier.size() >= 2) {
                // Initialize activity
                Activity activity;
                // define activity of this class//
                activity = ActivityVente.this;
                FragmentSelectProduct fragmentSelectProduct = new FragmentSelectProduct();
                fragmentSelectProduct.showDialogbox(activity, getBaseContext(), bon1.mode_tarif, "VENTE");
            }

        } catch (Exception e) {
            Crouton.makeText(ActivityVente.this, "Erreur : " + e.getMessage(), Style.ALERT).show();
        }
        
    }


    @Subscribe
    public void onVersementReceived(ValidateFactureEvent versement) {

        bon1.verser = versement.getVersement();
        if (bon1.verser != 0) {
            bon1.mode_rg = "ESPECE";
        } else {
            bon1.mode_rg = "A TERME";
        }

        bon1.reste = bon1.ancien_solde + (bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise) - bon1.verser;

        Calendar c = Calendar.getInstance();
        bon1.date_f = date_format.format(c.getTime());
        bon1.heure_f = heure_format.format(c.getTime());

        //bon1.diff_time =
        if (controller.validate_bon1_sql("BON1", bon1)) {
            bon1.blocage = "F";
        }

        validate_theme();
    }


    public void validate_theme() {
        if (bon1.blocage.equals("F")) {
            findViewById(R.id.Linear_Layout_Header_bon).setBackgroundColor(Color.LTGRAY);
            findViewById(R.id.tl).setBackgroundColor(Color.LTGRAY);
            getWindow().getDecorView().setBackgroundColor(Color.LTGRAY);
        } else if (bon1.blocage.equals("A")) {
            int slateGray = Color.parseColor("#475569");
            int paleBg = Color.parseColor("#94A3B8");
            findViewById(R.id.Linear_Layout_Header_bon).setBackgroundColor(slateGray);
            findViewById(R.id.tl).setBackgroundColor(paleBg);
            getWindow().getDecorView().setBackgroundColor(paleBg);
            View root = findViewById(android.R.id.content);
            if (root != null) root.setAlpha(0.58f);
            if (expandableListView != null) expandableListView.setEnabled(false);
            btn_select_client.setEnabled(false);
            btn_mode_tarif.setEnabled(false);
        } else {
            findViewById(R.id.Linear_Layout_Header_bon).setBackgroundColor(Color.WHITE);
            findViewById(R.id.tl).setBackgroundColor(Color.WHITE);
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            View root = findViewById(android.R.id.content);
            if (root != null) root.setAlpha(1.0f);
            if (expandableListView != null) expandableListView.setEnabled(true);
            btn_select_client.setEnabled(true);
            btn_mode_tarif.setEnabled(true);
        }
    }

    private boolean isBonVerrouille() {
        return "F".equals(bon1.blocage) || "A".equals(bon1.blocage);
    }

    private boolean isBonAnnule() {
        return "A".equals(bon1.blocage);
    }

    private void showBonLockedAlert(boolean isAnnule) {
        String msg = isAnnule ? "Ce bon est annulé, aucune modification n'est autorisée." : "Ce bon est déja validé";
        new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Information!")
                .setContentText(msg)
                .show();
    }


    private void selectProductFromScan(String resultscan) throws ParseException {
        ArrayList<PostData_Produit> produits;
        PostData_Bon2 bon2 = new PostData_Bon2();

        ///////////////////////////////////CODE BARRE //////////////////////////////////////
        ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

        String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN FROM CODEBARRE WHERE CODE_BARRE != '" + resultscan + "' AND CODE_BARRE_SYN = '" + resultscan + "' ";
        codebarres = controller.select_all_codebarre_from_database(querry_codebarre);
        if(!codebarres.isEmpty()){
            resultscan = codebarres.get(0).code_barre;
        }
        ///////////////////////////////////CODE BARRE //////////////////////////////////////

        String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, DETAILLE, ISNEW, IS_IMPORTED, FAMILLE, DESTOCK_TYPE, " +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                    "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                    "FROM PRODUIT  WHERE CODE_BARRE = '" + resultscan + "' OR REF_PRODUIT = '" + resultscan + "'";

        produits = controller.select_produits_from_database(querry);


        if (produits.size() == 1) {

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
            bon2.promo = produits.get(0).promo;
            bon2.d1 = produits.get(0).d1;
            bon2.d2 = produits.get(0).d2;
            bon2.pp1_ht = produits.get(0).pp1_ht;
            bon2.qte_promo = produits.get(0).qte_promo;

            switch (bon1.mode_tarif) {
                case "6" -> bon2.pv_ht = produits.get(0).pv6_ht;
                case "5" -> bon2.pv_ht = produits.get(0).pv5_ht;
                case "4" -> bon2.pv_ht = produits.get(0).pv4_ht;
                case "3" -> bon2.pv_ht = produits.get(0).pv3_ht;
                case "2" -> bon2.pv_ht = produits.get(0).pv2_ht;
                default -> bon2.pv_ht = produits.get(0).pv1_ht;
            }

            bon2.pv_limite = produits.get(0).pv_limite;


            SOURCE = "BON2_INSERT";
            Activity activity = ActivityVente.this;
            double last_price = controller.select_last_price_from_database("BON1", bon1.code_client, bon2.codebarre);
            FragmentQteVente fragmentqte = new FragmentQteVente();
            fragmentqte.showDialogbox(SOURCE, activity, getBaseContext(), bon2, last_price);

        } else if (produits.size() > 1) {
            Crouton.makeText(ActivityVente.this, "Attention il y a 2 produits avec le meme code !", Style.ALERT).show();
        } else {
            Crouton.makeText(ActivityVente.this, "Produit introuvable !", Style.ALERT).show();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PrintEvent event) {
        // Do something
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

    private void showNotesPickerDialog() {
        ArrayList<DATABASE.NoteRow> notes = controller.selectNotes();
        if (notes == null) notes = new ArrayList<>();

        Dialog notesDialog = new Dialog(ActivityVente.this);
        LinearLayout root = new LinearLayout(ActivityVente.this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        float density = getResources().getDisplayMetrics().density;
        int padding16 = (int) (16 * density);
        int padding12 = (int) (12 * density);
        int radius12 = (int) (12 * density);

        RelativeLayout topBar = new RelativeLayout(ActivityVente.this);
        topBar.setBackgroundColor(getResources().getColor(R.color.blue));
        topBar.setPadding(padding16, padding12, padding16, padding12);

        TextView title = new TextView(ActivityVente.this);
        title.setText("Notes");
        title.setTextColor(Color.WHITE);
        title.setTextSize(18);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
        RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.addRule(RelativeLayout.CENTER_VERTICAL);
        title.setLayoutParams(titleLp);

        ImageButton btnClose = new ImageButton(ActivityVente.this);
        btnClose.setImageResource(R.drawable.met_ic_clear);
        btnClose.setBackgroundColor(Color.TRANSPARENT);
        btnClose.setColorFilter(Color.WHITE);
        RelativeLayout.LayoutParams closeLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        closeLp.addRule(RelativeLayout.CENTER_VERTICAL);
        btnClose.setLayoutParams(closeLp);

        topBar.addView(title);
        topBar.addView(btnClose);

        LinearLayout editorWrap = new LinearLayout(ActivityVente.this);
        editorWrap.setOrientation(LinearLayout.VERTICAL);
        editorWrap.setPadding(padding16, padding12, padding16, 0);

        TextInputLayout tilNew = new TextInputLayout(ActivityVente.this);
        tilNew.setHint("Nouvelle note ou modifier une existante…");
        tilNew.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        tilNew.setBoxCornerRadii(radius12, radius12, radius12, radius12);
        tilNew.setPadding(0, padding12, 0, 0);
        LinearLayout.LayoutParams tilLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tilNew.setLayoutParams(tilLp);

        TextInputEditText etNew = new TextInputEditText(tilNew.getContext());
        etNew.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        etNew.setMaxLines(3);
        etNew.setMinLines(2);
        etNew.setImeOptions(EditorInfo.IME_ACTION_NONE);
        etNew.setTextSize(15);
        String currentNote = bon1.remarque == null ? "" : bon1.remarque;
        if (!currentNote.isEmpty()) {
            etNew.setText(currentNote);
            etNew.setSelection(currentNote.length());
        }
        tilNew.addView(etNew, new TextInputLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editorWrap.addView(tilNew);

        LinearLayout btnRow = new LinearLayout(ActivityVente.this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.END);
        LinearLayout.LayoutParams btnRowLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnRowLp.topMargin = padding12;
        btnRow.setLayoutParams(btnRowLp);

        GradientDrawable btnOutlinedBg = new GradientDrawable();
        btnOutlinedBg.setColor(Color.WHITE);
        btnOutlinedBg.setCornerRadius(radius12);
        btnOutlinedBg.setStroke(Math.max(1, (int)(1.5 * density)), getResources().getColor(R.color.blue));

        Button btnUseDirect = new Button(ActivityVente.this);
        btnUseDirect.setText("Utiliser");
        btnUseDirect.setTextColor(getResources().getColor(R.color.blue));
        btnUseDirect.setTextSize(14);
        btnUseDirect.setTypeface(btnUseDirect.getTypeface(), android.graphics.Typeface.BOLD);
        btnUseDirect.setBackground(btnOutlinedBg);
        LinearLayout.LayoutParams btnDirectLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(44 * density));
        btnDirectLp.setMarginEnd(padding12);
        btnUseDirect.setLayoutParams(btnDirectLp);
        btnUseDirect.setPadding((int)(16 * density), 0, (int)(16 * density), 0);

        GradientDrawable btnSaveBg = new GradientDrawable();
        btnSaveBg.setColor(getResources().getColor(R.color.blue));
        btnSaveBg.setCornerRadius(radius12);

        Button btnSaveUse = new Button(ActivityVente.this);
        btnSaveUse.setText("Enregistrer & choisir");
        btnSaveUse.setTextColor(Color.WHITE);
        btnSaveUse.setTextSize(14);
        btnSaveUse.setTypeface(btnSaveUse.getTypeface(), android.graphics.Typeface.BOLD);
        btnSaveUse.setBackground(btnSaveBg);
        LinearLayout.LayoutParams btnSaveLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)(44 * density));
        btnSaveUse.setLayoutParams(btnSaveLp);
        btnSaveUse.setPadding((int)(16 * density), 0, (int)(16 * density), 0);

        btnRow.addView(btnUseDirect);
        btnRow.addView(btnSaveUse);
        editorWrap.addView(btnRow);

        TextView tvSubList = new TextView(ActivityVente.this);
        tvSubList.setText("Ou choisir parmi les notes enregistrées :");
        tvSubList.setTextColor(getResources().getColor(R.color.mainTextColor));
        tvSubList.setTextSize(13);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = padding16;
        subLp.setMarginStart(padding16);
        tvSubList.setLayoutParams(subLp);

        FrameLayout frameRv = new FrameLayout(ActivityVente.this);
        LinearLayout.LayoutParams frameLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        frameLp.topMargin = padding12 / 2;
        frameLp.bottomMargin = padding12;
        frameRv.setLayoutParams(frameLp);

        RecyclerView rv = new RecyclerView(ActivityVente.this);
        rv.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        rv.setLayoutManager(new LinearLayoutManager(ActivityVente.this));
        frameRv.addView(rv);

        TextView tvEmpty = new TextView(ActivityVente.this);
        tvEmpty.setText("Aucune note enregistrée");
        tvEmpty.setTextColor(0xFF94A3B8);
        tvEmpty.setTextSize(14);
        tvEmpty.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams emptyLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        emptyLp.gravity = Gravity.CENTER;
        tvEmpty.setLayoutParams(emptyLp);
        if (notes.isEmpty()) {
            frameRv.addView(tvEmpty);
        }

        final ArrayList<DATABASE.NoteRow> finalNotes = notes;
        NotePickerAdapter adapter = new NotePickerAdapter(finalNotes, noteText -> {
            if (noteText == null) return;
            etNew.setText(noteText);
            etNew.setSelection(noteText.length());
            bon1.remarque = noteText;
            if (bon1.num_bon != null) {
                controller.update_bon1("BON1", bon1.num_bon, bon1);
            }
            notesDialog.dismiss();
            showSelectedNotePopup();
        }, (noteId, position) -> {
            new SweetAlertDialog(ActivityVente.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Supprimer la note ?")
                    .setContentText("Cette action est irréversible.")
                    .setConfirmText("Supprimer")
                    .setConfirmClickListener(sDialog -> {
                        controller.deleteNote(noteId);
                        finalNotes.remove(position);
                        NotePickerAdapter ad = (NotePickerAdapter) rv.getAdapter();
                        if (ad != null) ad.notifyItemRemoved(position);
                        if (finalNotes.isEmpty() && frameRv.indexOfChild(tvEmpty) < 0) {
                            frameRv.addView(tvEmpty);
                        }
                        sDialog.dismissWithAnimation();
                    })
                    .setCancelText("Annuler")
                    .setCancelClickListener(sd -> sd.dismissWithAnimation())
                    .show();
        });
        rv.setAdapter(adapter);

        btnUseDirect.setOnClickListener(v -> {
            String txt = etNew.getText() == null ? "" : etNew.getText().toString().trim();
            if (txt.isEmpty()) {
                Crouton.makeText(ActivityVente.this, "Saisissez une note ou choisissez-en une.", Style.ALERT).show();
                return;
            }
            bon1.remarque = txt;
            if (bon1.num_bon != null) {
                controller.update_bon1("BON1", bon1.num_bon, bon1);
            }
            notesDialog.dismiss();
            showSelectedNotePopup();
        });

        btnSaveUse.setOnClickListener(v -> {
            String txt = etNew.getText() == null ? "" : etNew.getText().toString().trim();
            if (txt.isEmpty()) {
                Crouton.makeText(ActivityVente.this, "Saisissez une note pour l'enregistrer.", Style.ALERT).show();
                return;
            }
            boolean alreadySaved = false;
            for (DATABASE.NoteRow n : finalNotes) {
                if (txt.equalsIgnoreCase(n.text == null ? "" : n.text.trim())) {
                    alreadySaved = true;
                    break;
                }
            }
            if (!alreadySaved) {
                controller.insertNote(txt);
            }
            bon1.remarque = txt;
            if (bon1.num_bon != null) {
                controller.update_bon1("BON1", bon1.num_bon, bon1);
            }
            notesDialog.dismiss();
            showSelectedNotePopup();
        });

        root.addView(topBar);
        root.addView(editorWrap);
        root.addView(tvSubList);
        root.addView(frameRv);

        notesDialog.setContentView(root);
        notesDialog.setCancelable(true);
        btnClose.setOnClickListener(v -> notesDialog.dismiss());

        if (notesDialog.getWindow() != null) {
            notesDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            notesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        notesDialog.show();
    }

    private void showSelectedNotePopup() {
        if (toolbar == null) return;

        if (notePopupWindow != null && notePopupWindow.isShowing()) {
            notePopupWindow.dismiss();
        }

        float density = getResources().getDisplayMetrics().density;
        int padding12 = (int) (12 * density);
        int padding16 = (int) (16 * density);
        int radius12 = (int) (12 * density);
        int elevation8 = (int) (8 * density);
        int stroke1 = Math.max(1, (int) (1 * density));
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9f);

        LinearLayout root = new LinearLayout(ActivityVente.this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(padding16, padding12, padding16, padding16);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(radius12);
        bg.setStroke(stroke1, 0x22000000);
        root.setBackground(bg);
        root.setElevation(elevation8);

        RelativeLayout header = new RelativeLayout(ActivityVente.this);
        header.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(ActivityVente.this);
        title.setText("Note");
        title.setTextColor(getResources().getColor(R.color.mainTextColor));
        title.setTextSize(16);
        title.setTypeface(title.getTypeface(), android.graphics.Typeface.BOLD);
        RelativeLayout.LayoutParams titleLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLp.addRule(RelativeLayout.ALIGN_PARENT_START);
        title.setLayoutParams(titleLp);

        ImageButton btnClose = new ImageButton(ActivityVente.this);
        int closeId = View.generateViewId();
        btnClose.setId(closeId);
        btnClose.setImageResource(R.drawable.met_ic_clear);
        btnClose.setBackgroundColor(Color.TRANSPARENT);
        btnClose.setColorFilter(getResources().getColor(R.color.mainTextColor));
        RelativeLayout.LayoutParams closeLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        closeLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        btnClose.setLayoutParams(closeLp);

        ImageButton btnChange = new ImageButton(ActivityVente.this);
        int changeId = View.generateViewId();
        btnChange.setId(changeId);
        btnChange.setImageResource(R.drawable.baseline_edit_24);
        btnChange.setBackgroundColor(Color.TRANSPARENT);
        btnChange.setColorFilter(getResources().getColor(R.color.mainTextColor));
        RelativeLayout.LayoutParams changeLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        changeLp.addRule(RelativeLayout.LEFT_OF, closeId);
        changeLp.setMarginEnd(padding12);
        btnChange.setLayoutParams(changeLp);

        header.addView(title);
        header.addView(btnChange);
        header.addView(btnClose);

        String note = bon1.remarque == null ? "" : bon1.remarque.trim();
        TextView tvNote = new TextView(ActivityVente.this);
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvLp.topMargin = padding12;
        tvNote.setLayoutParams(tvLp);
        tvNote.setTextColor(getResources().getColor(R.color.mainTextColor));
        tvNote.setTextSize(14);
        tvNote.setMaxLines(8);
        tvNote.setText(note.isEmpty() ? "Aucune note sélectionnée" : note);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(getResources().getColor(R.color.blue));
        btnBg.setCornerRadius(radius12);

        Button btnEditDirect = new Button(ActivityVente.this);
        btnEditDirect.setText("Modifier la note");
        btnEditDirect.setTextColor(Color.WHITE);
        btnEditDirect.setTextSize(14);
        btnEditDirect.setTypeface(btnEditDirect.getTypeface(), android.graphics.Typeface.BOLD);
        btnEditDirect.setBackground(btnBg);
        LinearLayout.LayoutParams btnEditLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(44 * density));
        btnEditLp.topMargin = padding16;
        btnEditDirect.setLayoutParams(btnEditLp);

        root.addView(header);
        root.addView(tvNote);
        root.addView(btnEditDirect);

        notePopupWindow = new PopupWindow(root, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        notePopupWindow.setOutsideTouchable(true);
        notePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notePopupWindow.setClippingEnabled(true);

        btnClose.setOnClickListener(v -> notePopupWindow.dismiss());
        btnChange.setOnClickListener(v -> {
            notePopupWindow.dismiss();
            showNotesPickerDialog();
        });
        btnEditDirect.setOnClickListener(v -> {
            notePopupWindow.dismiss();
            showNotesPickerDialog();
        });

        notePopupWindow.showAsDropDown(toolbar, 0, 0, Gravity.END);
    }

}

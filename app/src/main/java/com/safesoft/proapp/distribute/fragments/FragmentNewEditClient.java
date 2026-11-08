package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputEditText;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.AdapterCommune;
import com.safesoft.proapp.distribute.adapters.AdapterWilaya;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_commune;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;
import com.safesoft.proapp.distribute.utils.ToggleButtonGroupTableLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.function.Consumer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditClient {

    private static final int REQUEST_LOCATION_PERMISSION = 1001;
    private static final int REQUEST_CAMERA_PERMISSION = 1002;

    Button btn_valider, btn_cancel;
    TextInputEditText edt_client_code,
            edt_client_name,
            edt_client_adress,
            edt_client_telephone,
            edt_client_registre,
            edt_client_nif,
            edt_client_nis,
            edt_client_ai,
            edt_client_solde_init,
            edt_client_latitude,
            edt_client_longitude;
    Spinner wilayaSpinner, communeSpinner;
    ImageButton scan_codeclient, btn_get_gps, btn_view_map;
    ToggleButtonGroupTableLayout radioGroup_mode_tarif;
    private Context mContext;

    private FusedLocationProviderClient fusedLocationClient;
    @SuppressWarnings("deprecation")
    private ProgressDialog progressGps;
    private LocationCallback locationCallback;
    private final java.text.DecimalFormat coordFormat =
            (java.text.DecimalFormat) java.text.NumberFormat.getNumberInstance(java.util.Locale.US);
    {
        coordFormat.applyPattern("##0.000000");
        coordFormat.setGroupingUsed(false);
    }
    private final java.text.DecimalFormatSymbols dfsUS = new java.text.DecimalFormatSymbols(java.util.Locale.US);

    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;

    private PostData_Client created_client;
    private DATABASE controller;

    private ArrayList<PostData_wilaya> wilayas = new ArrayList<>();
    private ArrayList<PostData_commune> communes = new ArrayList<>();
    boolean shouldWork = true;
    // Tracking sélections MANUELLES UTILISATEUR Wilaya / Commune
    // (évite la validation de laisser passer la 1ère auto-sélection Android pos=0)
    private boolean userHasSelectedWilaya = false;
    private boolean userHasSelectedCommune = false;
    private boolean communeSpinnerFirstAutoFire = true;
    private String old_code_client;
    private boolean savedSuccessfully = false;
    private Consumer<Boolean> onClosedCallback = null;

    //PopupWindow display method (compatibilité : appel historique SANS callback)
    public void showDialogbox(Activity activity, Context context, String SOURCE_ACTIVITY, PostData_Client old_client) {
        showDialogbox(activity, context, SOURCE_ACTIVITY, old_client, null);
    }

    // Nouvelle version AVEC callback : onClosedCallback.accept(true) si save OK, false sinon
    @SuppressLint("SetTextI18n")
    public void showDialogbox(Activity activity, Context context, String SOURCE_ACTIVITY, PostData_Client old_client, Consumer<Boolean> onClosed) {

        this.mContext = context;
        this.activity = activity;
        this.controller = DATABASE.getInstance(activity);
        this.onClosedCallback = onClosed;
        this.savedSuccessfully = false;

        created_client = new PostData_Client();

        SharedPreferences prefs = mContext.getSharedPreferences("ALL_PREFS", MODE_PRIVATE);
        boolean is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);
        String CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");
        String CODE_VENDEUR = prefs.getString("CODE_VENDEUR", "000000");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_client, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes(layoutParams);

            // Apply immersive mode (hide navigation bar) to the dialog window if enabled.
            com.safesoft.proapp.distribute.app.BaseApplication.applyImmersiveToDialog(dialog.getWindow());
        }

        dialog.setOnDismissListener(dlg -> {
            cleanupGpsResources();
            if (onClosedCallback != null) {
                final boolean didSave = savedSuccessfully;
                activity.runOnUiThread(() -> onClosedCallback.accept(didSave));
            }
        });


        btn_valider = dialogview.findViewById(R.id.btn_valider);
        btn_cancel = dialogview.findViewById(R.id.btn_cancel);

        edt_client_code = dialogview.findViewById(R.id.edt_client_code);
        edt_client_name = dialogview.findViewById(R.id.edt_client_name);
        wilayaSpinner = dialogview.findViewById(R.id.wilaya_spinner);
        communeSpinner = dialogview.findViewById(R.id.commune_spinner);

        TextView title_client = dialogview.findViewById(R.id.title_client);

        scan_codeclient = dialogview.findViewById(R.id.scan_codeclient);
        btn_get_gps = dialogview.findViewById(R.id.btn_get_gps);
        btn_view_map = dialogview.findViewById(R.id.btn_view_map);

        edt_client_adress = dialogview.findViewById(R.id.edt_client_adress);
        edt_client_telephone = dialogview.findViewById(R.id.edt_client_telephone);
        edt_client_registre = dialogview.findViewById(R.id.edt_client_registre);
        edt_client_nif = dialogview.findViewById(R.id.edt_client_nif);
        edt_client_nis = dialogview.findViewById(R.id.edt_client_nis);
        edt_client_ai = dialogview.findViewById(R.id.edt_client_ai);
        edt_client_solde_init = dialogview.findViewById(R.id.edt_client_solde_init);
        edt_client_latitude = dialogview.findViewById(R.id.edt_client_latitude);
        edt_client_longitude = dialogview.findViewById(R.id.edt_client_longitude);

        edt_client_registre.setVisibility(GONE);
        edt_client_ai.setVisibility(GONE);
        edt_client_nif.setVisibility(GONE);
        edt_client_nis.setVisibility(GONE);

        radioGroup_mode_tarif = dialogview.findViewById(R.id.rd_mode_tarif);

        RadioButton  rb0= dialogview.findViewById(R.id.rb_0);
        RadioButton rb1 = dialogview.findViewById(R.id.rb_1);
        RadioButton rb2 = dialogview.findViewById(R.id.rb_2);
        RadioButton rb3 = dialogview.findViewById(R.id.rb_3);
        RadioButton rb4 = dialogview.findViewById(R.id.rb_4);
        RadioButton rb5 = dialogview.findViewById(R.id.rb_5);
        RadioButton rb6 = dialogview.findViewById(R.id.rb_6);


        Resources res = dialogview.getResources();
        wilayas = controller.select_wilayas_from_database("SELECT * FROM WILAYAS ORDER BY ID");
        AdapterWilaya adapterwilaya = new AdapterWilaya(mContext, R.layout.dropdown_wilaya_commune_item, wilayas, res);
        wilayaSpinner.setAdapter(adapterwilaya);

        wilayaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (shouldWork) {
                    created_client.wilaya = wilayas.get(position).wilaya;
                    // Sélection MANUELLE utilisateur (pas le premier auto-fire ignoré via shouldWork)
                    userHasSelectedWilaya = true;
                    // Lorsque la Wilaya change, le jeu de Communes est REMPLACÉ :
                    // il faut redemander une sélection explicite Commune ET reset le 1er auto-fire
                    userHasSelectedCommune = false;
                    communeSpinnerFirstAutoFire = true;
                    setRecyleCommune(res, wilayas.get(position).id);
                } else {
                    shouldWork = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        communeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (communeSpinnerFirstAutoFire) {
                    // 1er appel = auto-sélection Android pos=0 du nouveau jeu de Communes
                    // Ne PAS compter comme sélection utilisateur.
                    communeSpinnerFirstAutoFire = false;
                    if (!communes.isEmpty() && position >= 0 && position < communes.size()) {
                        created_client.commune = communes.get(position).commune;
                    }
                } else {
                    created_client.commune = communes.get(position).commune;
                    userHasSelectedCommune = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scan_codeclient.setOnClickListener(this::startScan);

        btn_get_gps.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                if (isGpsEnabled()) {
                    getCurrentLocation();
                } else {
                    showGpsEnableDialog();
                }
            } else {
                requestLocationPermission();
            }
        });

        btn_view_map.setOnClickListener(v -> openMapPreview());

        PostData_Params dbParams = controller.select_params_from_database("SELECT * FROM PARAMS");
        PostData_Params params = (dbParams != null) ? dbParams : new PostData_Params();

        rb1.setText(params.pv1_titre);
        rb2.setText(params.pv2_titre);
        rb3.setText(params.pv3_titre);
        rb4.setText(params.pv4_titre);
        rb5.setText(params.pv5_titre);
        rb6.setText(params.pv6_titre);

        rb0.setVisibility(GONE);
        rb1.setVisibility(GONE);
        rb2.setVisibility(GONE);
        rb3.setVisibility(GONE);
        rb4.setVisibility(GONE);
        rb5.setVisibility(GONE);
        rb6.setVisibility(GONE);


        if (is_app_synchronised_mode) {

            if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Libre")){

                rb0.setVisibility(View.VISIBLE);
                rb1.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_0);

                if (params.prix_2 == 1) {
                    rb2.setVisibility(View.VISIBLE);
                } else {
                    rb2.setVisibility(GONE);
                }

                if (params.prix_3 == 1) {
                    rb3.setVisibility(View.VISIBLE);
                } else {
                    rb3.setVisibility(GONE);
                }

                if (params.prix_4 == 1) {
                    rb4.setVisibility(View.VISIBLE);
                } else {
                    rb4.setVisibility(GONE);
                }

                if (params.prix_5 == 1) {
                    rb5.setVisibility(View.VISIBLE);
                } else {
                    rb5.setVisibility(GONE);
                }

                if (params.prix_6 == 1) {
                    rb6.setVisibility(View.VISIBLE);
                } else {
                    rb6.setVisibility(GONE);
                }

            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv1_titre)) {
                rb1.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_1);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv2_titre)) {
                rb2.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_2);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv3_titre)) {
                rb3.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_3);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv4_titre)) {
                rb4.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_4);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv5_titre)) {
                rb5.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_5);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv6_titre)) {
                rb6.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_6);
            }
        }else{
            if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Libre")){
                rb0.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_0);

                rb1.setVisibility(View.VISIBLE);
                rb2.setVisibility(View.VISIBLE);
                rb3.setVisibility(View.VISIBLE);

            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 1")) {
                rb1.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_1);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 2")) {
                rb2.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_2);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 3")) {
                rb3.setVisibility(View.VISIBLE);
                radioGroup_mode_tarif.check(R.id.rb_3);
            }
        }

        edt_client_solde_init.setEnabled(prefs.getBoolean("CAN_EDIT_SOLDE_INI", false));

        if(SOURCE_ACTIVITY.equals("NEW_CLIENT")){

            title_client.setText("Nouveau client");
            created_client.solde_client = created_client.solde_ini;
            if (CODE_DEPOT.equals("000000")) {
                created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "_" + CODE_VENDEUR;

            } else {
                created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "_" + CODE_DEPOT;
            }

            edt_client_code.setText(created_client.code_client);
            edt_client_latitude.setText("0.0");
            edt_client_longitude.setText("0.0");
            created_client.latitude = 0.0;
            created_client.longitude = 0.0;
            refreshMapButtonState();

        }else {

            title_client.setText("Modifier client");

            edt_client_name.setText(old_client.client);
            edt_client_adress.setText(old_client.adresse);
            scan_codeclient.setVisibility(GONE);
            created_client.wilaya = old_client.wilaya;
            created_client.commune = old_client.commune;
            old_code_client = old_client.code_client;
            created_client.code_client = old_client.code_client;

            created_client.latitude = old_client.latitude;
            created_client.longitude = old_client.longitude;
            if (old_client.latitude != 0.0 || old_client.longitude != 0.0) {
                edt_client_latitude.setText(coordFormat.format(old_client.latitude));
                edt_client_longitude.setText(coordFormat.format(old_client.longitude));
            } else {
                edt_client_latitude.setText("0.0");
                edt_client_longitude.setText("0.0");
            }
            refreshMapButtonState();

            ArrayList<PostData_wilaya> wilayas_temp = controller.select_wilayas_from_database("SELECT * FROM WILAYAS WHERE NAME = '" + old_client.wilaya + "' ORDER BY ID");

            if (wilayas_temp.size() > 1) {
                shouldWork = false;
                wilayaSpinner.setSelection(wilayas_temp.get(1).id);
                setRecyleCommune(res, wilayas_temp.get(1).id);
                //communes_temp = controller.select_communes_from_database("SELECT * FROM COMMUNES WHERE NAME = '" + old_client.commune + "' ORDER BY ID");
                int position = 0;
                for (int i = 0; i < communes.size(); i++) {
                    if (Objects.equals(communes.get(i).commune, old_client.commune)) {
                        position = i;
                        break;
                    }
                }
                communeSpinner.setSelection(position);
            }


            edt_client_telephone.setText(old_client.tel);
            edt_client_registre.setText(old_client.rc);
            edt_client_nif.setText(old_client.ifiscal);
            edt_client_nis.setText(old_client.nis);
            edt_client_ai.setText(old_client.ai);
            edt_client_code.setText(old_client.code_client);

            //edt_client_solde_init.setText(new DecimalFormat("####0.00").format(old_client.solde_ini));
            edt_client_solde_init.setText(String.valueOf(old_client.solde_ini));


            radioGroup_mode_tarif.clearCheck();
            if (old_client.mode_tarif.equals("0")) {
                radioGroup_mode_tarif.check(R.id.rb_0);
            }
            if (old_client.mode_tarif.equals("1")) {
                radioGroup_mode_tarif.check(R.id.rb_1);
            }
            if (old_client.mode_tarif.equals("2")) {
                radioGroup_mode_tarif.check(R.id.rb_2);
            }
            if (old_client.mode_tarif.equals("3")) {
                radioGroup_mode_tarif.check(R.id.rb_3);
            }
            if (old_client.mode_tarif.equals("4")) {
                radioGroup_mode_tarif.check(R.id.rb_4);
            }
            if (old_client.mode_tarif.equals("5")) {
                radioGroup_mode_tarif.check(R.id.rb_5);
            }
            if (old_client.mode_tarif.equals("6")) {
                radioGroup_mode_tarif.check(R.id.rb_6);
            }
        }

        btn_valider.setOnClickListener(v -> {
            boolean hasError = false;
            CharSequence nomCs = edt_client_name.getText();
            CharSequence telCs = edt_client_telephone.getText();
            CharSequence soldeCs = edt_client_solde_init.getText();

            if (nomCs == null || nomCs.length() <= 0) {

                edt_client_name.setError("Nom client est obligatoire!!");
                hasError = true;
            }

            if (telCs == null || telCs.length() <= 0) {
                edt_client_telephone.setError("Telephone est obligatoire!!");
                hasError = true;
            }

            if (soldeCs == null || soldeCs.length() <= 0) {
                edt_client_solde_init.setError("Solde initial est obligatoire!!");
                hasError = true;
            }

            // —— Validation optionnelle Wilaya / Commune (Paramètres Divers > Clients) ——
            // RÈGLE : Seulement pour un NOUVEAU client (pas édition)
            // Et demande une SÉLECTION MANUELLE des 2 spinners par l'utilisateur
            // (interdiction de compter l'auto-sélection Android pos=0 au démarrage)
            if (!SOURCE_ACTIVITY.equals("EDIT_CLIENT")) {
                if (prefs.getBoolean("WILAYA_COMMUNE_OBLIGATOIRE_CLIENT", false)) {
                    int posW = wilayaSpinner.getSelectedItemPosition();
                    boolean wilayaVide = (!userHasSelectedWilaya
                            || posW < 0
                            || wilayas.isEmpty()
                            || wilayas.get(posW) == null
                            || wilayas.get(posW).wilaya == null
                            || wilayas.get(posW).wilaya.trim().isEmpty());

                    int posC = communeSpinner.getSelectedItemPosition();
                    boolean communeVide = (!userHasSelectedCommune
                            || posC < 0
                            || communes.isEmpty()
                            || communes.get(posC) == null
                            || communes.get(posC).commune == null
                            || communes.get(posC).commune.trim().isEmpty());

                    if (wilayaVide) {
                        Crouton.makeText(activity, "Veuillez sélectionner une Wilaya (paramètre obligatoire activé)", Style.ALERT).show();
                        hasError = true;
                    }
                    if (communeVide) {
                        Crouton.makeText(activity, "Veuillez sélectionner une Commune (paramètre obligatoire activé)", Style.ALERT).show();
                        hasError = true;
                    }
                }
            }

            if (!hasError) {

                created_client.client = (edt_client_name.getText() != null) ? edt_client_name.getText().toString() : "";
                created_client.adresse = (edt_client_adress.getText() != null) ? edt_client_adress.getText().toString() : "";
                created_client.tel = (edt_client_telephone.getText() != null) ? edt_client_telephone.getText().toString() : "";
                created_client.rc = (edt_client_registre.getText() != null) ? edt_client_registre.getText().toString() : "";
                created_client.ifiscal = (edt_client_nif.getText() != null) ? edt_client_nif.getText().toString() : "";
                created_client.nis = (edt_client_nis.getText() != null) ? edt_client_nis.getText().toString() : "";
                created_client.ai = (edt_client_ai.getText() != null) ? edt_client_ai.getText().toString() : "";
                String soldeStr = (edt_client_solde_init.getText() != null) ? edt_client_solde_init.getText().toString().trim() : "";
                if (soldeStr.isEmpty()) soldeStr = "0";
                try {
                    created_client.solde_ini = Double.parseDouble(soldeStr);
                } catch (NumberFormatException nfe) {
                    created_client.solde_ini = 0.0;
                    edt_client_solde_init.setError("Solde invalide");
                    return;
                }

                try {
                    String latStr = (edt_client_latitude.getText() != null) ? edt_client_latitude.getText().toString().trim() : "";
                    String lngStr = (edt_client_longitude.getText() != null) ? edt_client_longitude.getText().toString().trim() : "";
                    java.text.DecimalFormat parseFmt = new java.text.DecimalFormat("##0.000000", dfsUS);
                    parseFmt.setParseBigDecimal(true);
                    Number latNum = latStr.isEmpty() ? null : parseFmt.parse(latStr.replace(',', '.'));
                    Number lngNum = lngStr.isEmpty() ? null : parseFmt.parse(lngStr.replace(',', '.'));
                    created_client.latitude = (latNum != null) ? latNum.doubleValue() : 0.0;
                    created_client.longitude = (lngNum != null) ? lngNum.doubleValue() : 0.0;
                } catch (Exception e) {
                    created_client.latitude = 0.0;
                    created_client.longitude = 0.0;
                }

                created_client.mode_tarif = "0";
                created_client.isNew = 1;


                int selectedRadioButtonId = radioGroup_mode_tarif.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_0).getId()) {
                        created_client.mode_tarif = "0";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_1).getId()) {
                        created_client.mode_tarif = "1";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_2).getId()) {
                        created_client.mode_tarif = "2";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_3).getId()) {
                        created_client.mode_tarif = "3";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_4).getId()) {
                        created_client.mode_tarif = "4";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_5).getId()) {
                        created_client.mode_tarif = "5";
                    }
                    if (selectedRadioButtonId == dialogview.findViewById(R.id.rb_6).getId()) {
                        created_client.mode_tarif = "6";
                    }
                }


                if (SOURCE_ACTIVITY.equals("EDIT_CLIENT")) {

                    try {

                        created_client.code_client = edt_client_code.getEditableText().toString();
                        controller.update_client(created_client, old_code_client);
                        Crouton.makeText(activity, "Client bien modifier", Style.INFO).show();
                        SelectedClientEvent added_client = new SelectedClientEvent(created_client);
                        bus.post(added_client);
                        savedSuccessfully = true;
                        dialog.dismiss();

                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème de mise à jour client : " + e.getMessage())
                                .show();
                    }
                    //Insert client into database,

                } else {

                    created_client.solde_client = created_client.solde_ini;

                    try {

                        created_client.code_client = edt_client_code.getEditableText().toString();

                         // Check if client exist in database
                         if(controller.checkClientExists(created_client.code_client)){
                             new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Ce client existe déjà")
                                    .show();
                             return;
                         }
                         //Insert client into database,
                         controller.insert_into_client(created_client);
                         Crouton.makeText(activity, "Client bien ajouté", Style.INFO).show();
                         SelectedClientEvent added_client = new SelectedClientEvent(created_client);
                         bus.post(added_client);
                         savedSuccessfully = true;
                         dialog.dismiss();

                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème insertion : " + e.getMessage())
                                .show();
                    }

                }

            }

        });

        btn_cancel.setOnClickListener(v -> {
            cleanupGpsResources();
            dialog.dismiss();
        });
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_LOCATION_PERMISSION);
        Toast.makeText(mContext, "Activez la permission Localisation puis réessayez", Toast.LENGTH_LONG).show();
    }

    private boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) return false;
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ignored) {}
        try {
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ignored) {}
        return gpsEnabled || networkEnabled;
    }

    private void showGpsEnableDialog() {
        new AlertDialog.Builder(activity)
                .setTitle("Localisation désactivée")
                .setMessage("Veuillez activer la localisation (GPS ou Réseau) pour récupérer votre position.")
                .setCancelable(false)
                .setPositiveButton("Activer", (dlg, which) ->
                        activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Annuler", (dlg, which) -> dlg.dismiss())
                .show();
    }

    @SuppressWarnings({"MissingPermission", "deprecation"})
    private void getCurrentLocation() {
        progressGps = new ProgressDialog(activity);
        progressGps.setTitle("Position GPS");
        progressGps.setMessage("Récupération de la position en cours...");
        progressGps.setIndeterminate(true);
        progressGps.setCancelable(true);
        progressGps.setOnCancelListener(dlg -> cleanupGpsResources());
        progressGps.show();

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, location -> {
                        if (location != null) {
                            applyLocation(location);
                            dismissProgressGps();
                        } else {
                            requestNewLocation();
                        }
                    })
                    .addOnFailureListener(activity, e -> requestNewLocation());
        } catch (SecurityException e) {
            dismissProgressGps();
            Toast.makeText(mContext, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void requestNewLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000L)
                .setMaxUpdates(1)
                .setWaitForAccurateLocation(false)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                cleanupGpsResources();
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    applyLocation(location);
                } else {
                    Toast.makeText(mContext, "Position introuvable, réessayez.", Toast.LENGTH_SHORT).show();
                }
                dismissProgressGps();
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, activity.getMainLooper());
        } catch (SecurityException e) {
            dismissProgressGps();
            Toast.makeText(mContext, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyLocation(@NonNull Location location) {
        created_client.latitude = location.getLatitude();
        created_client.longitude = location.getLongitude();
        edt_client_latitude.setText(coordFormat.format(created_client.latitude));
        edt_client_longitude.setText(coordFormat.format(created_client.longitude));
        refreshMapButtonState();
        Crouton.makeText(activity, "Position récupérée avec succès !", Style.CONFIRM).show();
    }

    private void refreshMapButtonState() {
        boolean hasPosition = created_client.latitude != 0.0 || created_client.longitude != 0.0;
        if (btn_view_map != null) {
            btn_view_map.setEnabled(hasPosition);
            btn_view_map.setAlpha(hasPosition ? 1.0f : 0.3f);
        }
    }

    private void openMapPreview() {
        try {
            double lat = created_client.latitude;
            double lng = created_client.longitude;
            if (lat == 0.0 && lng == 0.0) return;
            String label = Uri.encode(
                    (created_client.client != null && !created_client.client.trim().isEmpty())
                            ? created_client.client : "Position client");
            Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng
                    + "?q=" + lat + "," + lng + "(" + label + ")&z=16");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(mapIntent);
            } else {
                Intent fallback = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng));
                activity.startActivity(fallback);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "Impossible d'ouvrir la carte", Toast.LENGTH_SHORT).show();
        }
    }

    private void dismissProgressGps() {
        if (progressGps != null && progressGps.isShowing()) {
            try {
                progressGps.dismiss();
            } catch (Exception ignored) {}
        }
        progressGps = null;
    }

    private void cleanupGpsResources() {
        if (fusedLocationClient != null && locationCallback != null) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback);
            } catch (Exception ignored) {}
        }
        locationCallback = null;
        dismissProgressGps();
    }

    private void startScan(View view) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Permission caméra requise")
                        .setContentText("L'application a besoin d'accéder à la caméra pour scanner les codes-barres.")
                        .setConfirmText("Autoriser")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        })
                        .setCancelText("Annuler")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            } else {
                boolean deniedPermanently = !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA);
                if (deniedPermanently) {
                    new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Caméra indisponible")
                            .setContentText("Vous avez refusé définitivement la permission caméra. Pour activer le scanner, allez dans Paramètres > Applications > Distribute Pro > Autorisations et activez Caméra.")
                            .setConfirmText("Aller aux paramètres")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                activity.startActivity(intent);
                            })
                            .setCancelText("Annuler")
                            .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                            .show();
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_PERMISSION);
                }
            }
            return;
        }

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    if (view.getId() == R.id.scan_codeclient && barcode != null) {
                        // check if barcode is exist in database
                        edt_client_code.setText(barcode.rawValue);
                        created_client.code_client = barcode.rawValue;
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    public void setRecyleCommune(Resources res, int wilaya_id) {
        communes.clear();
        communes = controller.select_communes_from_database("SELECT * FROM COMMUNES WHERE CODE_WILAYA = " + wilaya_id + " ORDER BY COMMUNE");
        AdapterCommune adaptercommune = new AdapterCommune(mContext, R.layout.dropdown_wilaya_commune_item, communes, res);
        communeSpinner.setAdapter(adaptercommune);
    }
}
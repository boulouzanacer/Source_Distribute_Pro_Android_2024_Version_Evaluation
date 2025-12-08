package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;
import static android.view.View.GONE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
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

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditClient {

    Button btn_valider, btn_cancel;
    TextInputEditText edt_client_code, edt_client_name, edt_client_adress, edt_client_telephone, edt_client_registre, edt_client_nif, edt_client_nis, edt_client_ai, edt_client_solde_init;
    Spinner wilayaSpinner, communeSpinner;
    private TextView title_client;
    ImageButton scan_codeclient;
    ToggleButtonGroupTableLayout radioGroup_mode_tarif;
    private Context mContext;
    private Barcode barcodeResult;

    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;

    private final String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;
    private String CODE_DEPOT, CODE_VENDEUR;

    private PostData_Client created_client;
    private DATABASE controller;
    private PostData_Client old_client;
    private AdapterWilaya adapterwilaya;
    private AdapterCommune adaptercommune;

    private Resources res;
    private ArrayList<PostData_wilaya> wilayas = new ArrayList<>();
    private ArrayList<PostData_wilaya> wilayas_temp = new ArrayList<>();
    private ArrayList<PostData_commune> communes = new ArrayList<>();
    boolean shouldWork = true;
    private boolean is_app_synchronised_mode = false;
    private String old_code_client;

    //PopupWindow display method

    public void showDialogbox(Activity activity, Context context, String SOURCE_ACTIVITY, PostData_Client old_client) {

        this.mContext = context;
        this.activity = activity;
        this.controller = new DATABASE(activity);
        this.old_client = old_client;

        created_client = new PostData_Client();

        prefs = mContext.getSharedPreferences(PREFS, MODE_PRIVATE);
        is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);
        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");
        CODE_VENDEUR = prefs.getString("CODE_VENDEUR", "000000");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_client, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


        btn_valider = dialogview.findViewById(R.id.btn_valider);
        btn_cancel = dialogview.findViewById(R.id.btn_cancel);

        edt_client_code = dialogview.findViewById(R.id.edt_client_code);
        edt_client_name = dialogview.findViewById(R.id.edt_client_name);
        wilayaSpinner = dialogview.findViewById(R.id.wilaya_spinner);
        communeSpinner = dialogview.findViewById(R.id.commune_spinner);

        title_client = dialogview.findViewById(R.id.title_client);

        scan_codeclient = dialogview.findViewById(R.id.scan_codeclient);

        edt_client_adress = dialogview.findViewById(R.id.edt_client_adress);
        edt_client_telephone = dialogview.findViewById(R.id.edt_client_telephone);
        edt_client_registre = dialogview.findViewById(R.id.edt_client_registre);
        edt_client_nif = dialogview.findViewById(R.id.edt_client_nif);
        edt_client_nis = dialogview.findViewById(R.id.edt_client_nis);
        edt_client_ai = dialogview.findViewById(R.id.edt_client_ai);
        edt_client_solde_init = dialogview.findViewById(R.id.edt_client_solde_init);

        radioGroup_mode_tarif = dialogview.findViewById(R.id.rd_mode_tarif);

        RadioButton  rb0= dialogview.findViewById(R.id.rb_0);
        RadioButton rb1 = dialogview.findViewById(R.id.rb_1);
        RadioButton rb2 = dialogview.findViewById(R.id.rb_2);
        RadioButton rb3 = dialogview.findViewById(R.id.rb_3);
        RadioButton rb4 = dialogview.findViewById(R.id.rb_4);
        RadioButton rb5 = dialogview.findViewById(R.id.rb_5);
        RadioButton rb6 = dialogview.findViewById(R.id.rb_6);


        res = dialogview.getResources();
        wilayas = controller.select_wilayas_from_database("SELECT * FROM WILAYAS ORDER BY ID");
        adapterwilaya = new AdapterWilaya(mContext, R.layout.dropdown_wilaya_commune_item, wilayas, res);
        wilayaSpinner.setAdapter(adapterwilaya);

        wilayaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (shouldWork) {
                    created_client.wilaya = wilayas.get(position).wilaya;
                    setRecyleCommune(res, wilayas.get(position).id);
                } else
                    shouldWork = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        communeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                created_client.commune = communes.get(position).commune;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        scan_codeclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(view);
            }
        });

        PostData_Params params = new PostData_Params();
        params = controller.select_params_from_database("SELECT * FROM PARAMS");

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

        if(SOURCE_ACTIVITY.equals("NEW_CLIENT")){

            title_client.setText("Nouveau client");
            created_client.solde_montant = created_client.solde_ini;
            if (CODE_DEPOT.equals("000000")) {
                created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "_" + CODE_VENDEUR;

            } else {
                created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "_" + CODE_DEPOT;
            }

            edt_client_code.setText(created_client.code_client);

        }else {

            title_client.setText("Modifier client");

            edt_client_name.setText(old_client.client);
            edt_client_adress.setText(old_client.adresse);
            scan_codeclient.setVisibility(GONE);
            created_client.wilaya = old_client.wilaya;
            created_client.commune = old_client.commune;
            old_code_client = old_client.code_client;
            created_client.code_client = old_client.code_client;

            wilayas_temp = controller.select_wilayas_from_database("SELECT * FROM WILAYAS WHERE NAME = '" + old_client.wilaya + "' ORDER BY ID");

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
            edt_client_solde_init.setEnabled(false);

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

            if (edt_client_name.getText().length() <= 0) {

                edt_client_name.setError("Nom client est obligatoire!!");
                hasError = true;
            }
            if (edt_client_adress.getText().length() <= 0) {
                edt_client_adress.setError("Adresse est obligatoire!!");
                hasError = true;
            }

            if (edt_client_telephone.getText().length() <= 0) {
                edt_client_telephone.setError("Telephone est obligatoire!!");
                hasError = true;
            }

            if (edt_client_solde_init.getText().length() <= 0) {
                edt_client_solde_init.setError("Solde initial est obligatoire!!");
                hasError = true;
            }

            if (!hasError) {

                created_client.client = edt_client_name.getText().toString();
                created_client.adresse = edt_client_adress.getText().toString();
                created_client.tel = edt_client_telephone.getText().toString();
                created_client.rc = Objects.requireNonNull(edt_client_registre.getText()).toString();
                created_client.ifiscal = Objects.requireNonNull(edt_client_nif.getText()).toString();
                created_client.nis = Objects.requireNonNull(edt_client_nis.getText()).toString();
                created_client.ai = Objects.requireNonNull(edt_client_ai.getText()).toString();
                created_client.solde_ini = Double.parseDouble(edt_client_solde_init.getText().toString());

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
                        dialog.dismiss();

                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème de mise à jour client : " + e.getMessage())
                                .show();
                    }
                    //Insert client into database,

                } else {

                    created_client.solde_montant = created_client.solde_ini;

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
            dialog.dismiss();
        });
    }

    private void startScan(View view) {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    barcodeResult = barcode;

                    if (view.getId() == R.id.scan_codeclient) {
                        // check if barcode is exist in database
                        edt_client_code.setText(barcodeResult.rawValue);
                        created_client.code_client = barcodeResult.rawValue;

                    }

                })
                .build();
        materialBarcodeScanner.startScan();
    }

    public void setRecyleCommune(Resources res, int wilaya_id) {
        communes.clear();
        communes = controller.select_communes_from_database("SELECT * FROM COMMUNES WHERE WILAYA_ID = " + wilaya_id + " ORDER BY NAME");
        adaptercommune = new AdapterCommune(mContext, R.layout.dropdown_wilaya_commune_item, communes, res);
        communeSpinner.setAdapter(adaptercommune);
    }
}
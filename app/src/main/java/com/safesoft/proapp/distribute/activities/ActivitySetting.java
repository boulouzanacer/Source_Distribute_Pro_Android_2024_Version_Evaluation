package com.safesoft.proapp.distribute.activities;

import android.Manifest;
//import android.content.Context;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
//import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
//import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.github.ybq.android.spinkit.style.Circle;
//import com.safesoft.proapp.distribute.activation.ActivityActivation;
import com.safesoft.proapp.distribute.activities.login.ActivityChangePwd;
//import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.PasswordResetDialogFragment;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.util.ScalingActivityAnimator;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import pl.coreorb.selectiondialogs.data.SelectableIcon;
import pl.coreorb.selectiondialogs.dialogs.IconSelectDialog;
import pl.coreorb.selectiondialogs.views.SelectedItemView;

public class ActivitySetting extends AppCompatActivity implements IconSelectDialog.OnIconSelectedListener {

    private static final String TAG_SELECT_COLOR_DIALOG = "TAG_SELECT_COLOR_DIALOG";

    public Circle circle;

    private EditText ip, pathdatabase ,nom_serveur_ftp, num_port_ftp ,nom_utilisateur_ftp ,password_ftp ,ch_exp_ftp ,ch_imp_ftp;
    private EditText comapny_name, activity, adresse, merci, encas;

    private TextView textView, code_depot, code_vendeur;
    private EasyTextInputLayout edt_objectif;

    private LinearLayout editBloc;
    private LinearLayout editFooter;

    private SelectedItemView mode_tarif;

    private Button btntest;

    private final String PREFS_CONNEXION = "ConfigNetwork";
    private final String PREFS_PRINTER = "ConfigPrinter";
    private final String PREFS_FOOTER = "Footer";

    private final String PREFS_SCANNER = "ConfigScanner";
    private final String PREFS_AUTRE = "ConfigAutre";
    private final String PARAMS_PREFS_IMPORT_EXPORT = "IMPORT_EXPORT";
    private final String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
    private final String PARAMS_PREFS_FTP = "ConfigServeurFtp";

    private final Boolean[] isRunning = {false};
    private String username;
    private String password;

    private RadioButton rdb_integrete;
    private RadioButton rdb_bleutooth;
    private RadioButton rdb_scanner_integrete;
    private RadioButton rdb_scanner_camera;

    private LinearLayout param_co, param_ftp, param_impr, param_backup, param_reset, param_divers;

    Button bt1, bt2, bt3, bt4, bt10, bt11;
    RelativeLayout param_pda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //DATABASE controller = new DATABASE(this);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Parametres");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        //Context mContext = this;
        initViews();
    }

    private void initViews() {

        //EditText
        ip = findViewById(R.id.ip);

        pathdatabase = findViewById(R.id.database);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);

        bt10 = findViewById(R.id.bt10);
        bt11 = findViewById(R.id.bt11);


        merci = findViewById(R.id.merci);
        encas = findViewById(R.id.encas);
        comapny_name = findViewById(R.id.company_name);
        activity = findViewById(R.id.activity_name);
        adresse = findViewById(R.id.adresse);

        param_co = findViewById(R.id.param_co);
        param_ftp = findViewById(R.id.param_ftp);
        param_impr = findViewById(R.id.param_impr);
        param_pda = findViewById(R.id.param_pda);
        param_backup = findViewById(R.id.param_backup);
        param_reset = findViewById(R.id.param_reset);
        param_divers = findViewById(R.id.param_divers);


        bt1.setOnClickListener(view -> {

            if (param_co.isShown())
                param_co.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.VISIBLE);
                param_ftp.setVisibility(View.GONE);
                param_impr.setVisibility(View.GONE);
                param_reset.setVisibility(View.GONE);
                param_backup.setVisibility(View.GONE);
                param_divers.setVisibility(View.GONE); }
        });

        bt4.setOnClickListener(view -> {

            if (param_ftp.isShown())
                param_ftp.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.GONE);
                param_ftp.setVisibility(View.VISIBLE);
                param_impr.setVisibility(View.GONE);
                param_reset.setVisibility(View.GONE);
                param_backup.setVisibility(View.GONE);
                param_divers.setVisibility(View.GONE); }
        });

        bt2.setOnClickListener(view -> {

            if (param_divers.isShown())
                param_divers.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.GONE);
                param_ftp.setVisibility(View.GONE);
                param_impr.setVisibility(View.GONE);
                param_reset.setVisibility(View.GONE);
                param_backup.setVisibility(View.GONE);
                param_divers.setVisibility(View.VISIBLE); }
        });
        bt3.setOnClickListener(view -> {

            if (param_impr.isShown())
                param_impr.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.GONE);
                param_ftp.setVisibility(View.GONE);
                param_impr.setVisibility(View.VISIBLE);
                param_reset.setVisibility(View.GONE);
                param_backup.setVisibility(View.GONE);
                param_divers.setVisibility(View.GONE); }
        });

        bt10.setOnClickListener(view -> {

            if (param_backup.isShown())
                param_backup.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.GONE);
                param_ftp.setVisibility(View.GONE);
                param_impr.setVisibility(View.GONE);
                param_reset.setVisibility(View.GONE);
                param_backup.setVisibility(View.VISIBLE);
                param_divers.setVisibility(View.GONE); }
        });
        bt11.setOnClickListener(view -> {

            if (param_reset.isShown())
                param_reset.setVisibility(View.GONE);
            else {
                param_co.setVisibility(View.GONE);
                param_ftp.setVisibility(View.GONE);
                param_impr.setVisibility(View.GONE);
                param_reset.setVisibility(View.VISIBLE);
                param_backup.setVisibility(View.GONE);
                param_divers.setVisibility(View.GONE); }
        });
        // Chekbox
        CheckBox chkbx_stock_moins = findViewById(R.id.chkbx_stock_moins);
        CheckBox chkbx_achats_show = findViewById(R.id.chkbx_achats_show);
        CheckBox chkbx_produit = findViewById(R.id.chkbx_photo_pr);


        // checkbox stock moins
        SharedPreferences prefs3 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);

        chkbx_stock_moins.setChecked(prefs3.getBoolean("STOCK_MOINS", false));

        chkbx_stock_moins.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();

            editor.putBoolean("STOCK_MOINS", isChecked);
            editor.apply();   // editor.commit();
        });


        // checkbox achats show
        prefs3 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);

        chkbx_achats_show.setChecked(prefs3.getBoolean("ACHATS_SHOW", false));

        chkbx_achats_show.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
            editor.putBoolean("ACHATS_SHOW", isChecked);
            editor.apply();
        });

        chkbx_produit.setChecked(prefs3.getBoolean("PR_PRO", false));

        chkbx_produit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
            editor.putBoolean("PR_PRO", isChecked);
            editor.apply();
        });

        mode_tarif = findViewById(R.id.mode_tarif_select);
        mode_tarif.setOnClickListener(v -> showIconSelectDialog());

        edt_objectif = findViewById(R.id.edit_objectif);

        SharedPreferences prefs = getSharedPreferences(PREFS_CONNEXION, MODE_PRIVATE);
        ip.setText(prefs.getString("ip", "192.168.1.10"));
        pathdatabase.setText(prefs.getString("path", "D:/P-VENTE/DATA/PME PRO/PMEPRO"));
        username = prefs.getString("username", "SYSDBA");
        password = prefs.getString("password", "masterkey");

        prefs = getSharedPreferences(PARAMS_PREFS_FTP,MODE_PRIVATE);

        nom_serveur_ftp = findViewById(R.id.serveur_ftp);
        nom_serveur_ftp.setText(prefs.getString("serveur_ftp",""));

        num_port_ftp = findViewById(R.id.port_ftp);
        num_port_ftp.setText(prefs.getString("port_ftp","21"));

        nom_utilisateur_ftp =  findViewById(R.id.utilisateur_ftp);
        nom_utilisateur_ftp.setText(prefs.getString("utilisateur_ftp",""));

        password_ftp =  findViewById(R.id.pwd_ftp);
        password_ftp.setText(prefs.getString("pwd_ftp",""));

        ch_exp_ftp =  findViewById(R.id.exp_ftp);
        ch_exp_ftp.setText(prefs.getString("exp_ftp","IMP"));

        ch_imp_ftp =  findViewById(R.id.imp_ftp);
        ch_imp_ftp.setText(prefs.getString("imp_ftp","EXP"));

        prefs = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        edt_objectif.getEditText().setText(prefs.getString("OBJECTIF_MONTANT", "0.00"));

        if (Objects.equals(prefs.getString("PV_ID", "PV1"), "PV1")) {
            mode_tarif.setSelectedName("Prix de vente 1");
        } else if (Objects.equals(prefs.getString("PV_ID", "PV1"), "PV2")) {
            mode_tarif.setSelectedName("Prix de vente 2");
        } else {
            mode_tarif.setSelectedName("Prix de vente 3");
        }



        //Radio button
        rdb_integrete = findViewById(R.id.radioButton_integrer);
        rdb_bleutooth = findViewById(R.id.radioButton_bluetooth);
        rdb_scanner_integrete = findViewById(R.id.radioScanner_integrer);
        rdb_scanner_camera = findViewById(R.id.radioScanner_camera);

        // Shared preference
        SharedPreferences prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if (Objects.equals(prefs1.getString("PRINTER", "INTEGRATE"), "INTEGRATE")) {
            rdb_integrete.setChecked(true);
            rdb_bleutooth.setChecked(false);
        } else {
            rdb_integrete.setChecked(false);
            rdb_bleutooth.setChecked(true);
        }

        SharedPreferences prefs_scanner = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE);
        if (Objects.equals(prefs_scanner.getString("SCANNER", "INTEGRATE"), "INTEGRATE")) {
            rdb_scanner_integrete.setChecked(true);
            rdb_scanner_camera.setChecked(false);
        } else {
            rdb_scanner_integrete.setChecked(false);
            rdb_scanner_camera.setChecked(true);
        }


        //Button
        btntest = findViewById(R.id.check);
        btntest.setOnClickListener(v -> {
            if (!isRunning[0]) {
                new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(),
                        username, password).execute();
                isRunning[0] = true;
            } else {
                Crouton.showText(ActivitySetting.this, "Test Connection is running !", Style.INFO);
            }
        });

        Button btn_save_ftp = findViewById(R.id.save_ftp);
        btn_save_ftp.setOnClickListener(v -> {

        //// sauvegaure de parametre de serveur FTP

            SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_FTP, MODE_PRIVATE).edit();
            editor.putString("serveur_ftp", nom_serveur_ftp.getText().toString());
            editor.putString("port_ftp", num_port_ftp.getText().toString());
            editor.putString("utilisateur_ftp", nom_utilisateur_ftp.getText().toString());
            editor.putString("pwd_ftp", password_ftp.getText().toString());
            editor.putString("exp_ftp", ch_exp_ftp.getText().toString());
            editor.putString("imp_ftp", ch_imp_ftp.getText().toString());
            editor.apply();
            new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!")
                    .setContentText("Paramètred FTP sauvegarder!")
                    .show();

        });

        Button enregistrer_footer = findViewById(R.id.enregistrer_footer);
        Button enregistrer = findViewById(R.id.enregistrer);

        enregistrer.setOnClickListener(v -> {
            if (comapny_name.getText().toString().length() <= 0 || activity.getText().toString().length() <= 0 || adresse.getText().toString().length() <= 0) {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oups!")
                        .setContentText("Tous les champs sont obligatoire")
                        .show();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
                editor.putString("COMPANY_NAME", comapny_name.getText().toString());
                editor.putString("ACTIVITY_NAME", activity.getText().toString());
                editor.putString("ADRESSE", adresse.getText().toString());
                editor.apply();
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Les données sauvegarder!")
                        .show();
            }
        });
        enregistrer_footer.setOnClickListener(v -> {
            if (merci.getText().toString().length() <= 0 && encas.getText().toString().length() <= 0) {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Oups!")
                        .setContentText("Tous les champs sont obligatoire")
                        .show();
            } else {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE).edit();
                editor.putString("MERCI", merci.getText().toString());
                editor.putString("ENCAS", encas.getText().toString());
                editor.apply();
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Les données sauvegarder!")
                        .show();
            }
        });

        Button btnobjectif = findViewById(R.id.BtnObjectif);
        btnobjectif.setOnClickListener(v -> {

            if (edt_objectif.getEditText().getText().toString() != null) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                editor.putString("OBJECTIF_MONTANT", edt_objectif.getEditText().getText().toString());
                editor.apply();

                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Montant objectif sauvegardé!")
                        .show();
            } else {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Opss!")
                        .setContentText("Montant vide!")
                        .show();
            }

        });

        //TextView
        code_depot = findViewById(R.id.code_depot);
        code_vendeur = findViewById(R.id.code_vendeur);
        textView = findViewById(R.id.progress);
        circle = new Circle();
        circle.setBounds(0, 0, 60, 60);

        circle.setColor(getResources().getColor(R.color.colorAccent));
        textView.setCompoundDrawables(null, null, circle, null);
        textView.setVisibility(View.GONE);

        //Relative layout
        editBloc = findViewById(R.id.edit_bloc);
        editFooter = findViewById(R.id.edit_footer);

        ///////////////////
        SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
        code_depot.setText(prefs2.getString("CODE_DEPOT", "000000"));
        code_vendeur.setText(prefs2.getString("CODE_VENDEUR", "000000"));


        rdb_integrete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
            if (isChecked) {
                rdb_bleutooth.setChecked(false);
                editor.putString("PRINTER", "INTEGRATE");
            } else {
                rdb_bleutooth.setChecked(true);
                editor.putString("PRINTER", "BLEUTOOTH");
            }
            editor.apply();
        });

        rdb_bleutooth.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
            if (isChecked) {
                rdb_integrete.setChecked(false);
                editor.putString("PRINTER", "BLEUTOOTH");
            } else {
                rdb_integrete.setChecked(true);
                editor.putString("PRINTER", "INTEGRATE");
            }
            editor.apply();
        });

        rdb_scanner_integrete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE).edit();
            if (isChecked) {
                rdb_scanner_camera.setChecked(false);
                editor.putString("SCANNER", "INTEGRATE");
            } else {
                rdb_scanner_camera.setChecked(true);
                editor.putString("SCANNER", "CAMERA");
            }
            editor.apply();
        });

        rdb_scanner_camera.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE).edit();
            if (isChecked) {
                rdb_scanner_integrete.setChecked(false);
                editor.putString("SCANNER", "INTEGRATE");
            } else {
                rdb_scanner_integrete.setChecked(true);
                editor.putString("SCANNER", "CAMERA");
            }
            editor.apply();
        });

        //Switch
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_importer_online = findViewById(R.id.switch_import);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_exporter_online = findViewById(R.id.switch_export);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_gps = findViewById(R.id.switch_gps);

        /////////////////////////////////// SWITCH IMPORTATION /////////////////////////////////////

        prefs3 = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE);
        switch_importer_online.setChecked(prefs3.getBoolean("IMPORT_ONLINE", false));

        switch_importer_online.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
            editor.putBoolean("IMPORT_ONLINE", isChecked);
            editor.apply();

        });

        //////////////////////////////////// SWITCH EXPORTATION ////////////////////////////////////

        switch_exporter_online.setChecked(prefs3.getBoolean("EXPORT_ONLINE", false));
        switch_exporter_online.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
            editor.putBoolean("EXPORT_ONLINE", isChecked);
            editor.apply();
        });


        //////////////////////////////// SWITCH GPS LOCALISATION ///////////////////////////////////

        SharedPreferences prefs4 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        switch_gps.setChecked(prefs4.getBoolean("GPS_LOCALISATION", false));

        switch_gps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
            editor.putBoolean("GPS_LOCALISATION", isChecked);
            editor.apply();
        });


        // Reset database


        // Backup database


        // Header company setting
        CheckBox title_bon = findViewById(R.id.title_bon);
        CheckBox title_footer = findViewById(R.id.title_footer);


        prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if (prefs1.getBoolean("ENTETE_SHOW", false)) {
            editBloc.setVisibility(View.VISIBLE);
            title_bon.setChecked(true);
            comapny_name.setText(prefs1.getString("COMPANY_NAME", ""));
            activity.setText(prefs1.getString("ACTIVITY_NAME", ""));
            adresse.setText(prefs1.getString("ADRESSE", ""));
        } else {
            editBloc.setVisibility(View.GONE);
            title_bon.setChecked(false);
        }

        title_bon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
            if (isChecked) {
                editBloc.setVisibility(View.VISIBLE);
                editor.putBoolean("ENTETE_SHOW", true);
            } else {
                editBloc.setVisibility(View.GONE);
                editor.putBoolean("ENTETE_SHOW", false);
            }
            editor.apply();
        });

        prefs1 = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE);
        if (prefs1.getBoolean("FOOTER", false)) {
            editFooter.setVisibility(View.VISIBLE);
            title_footer.setChecked(true);
            merci.setText(prefs1.getString("MERCI", ""));
            encas.setText(prefs1.getString("ENCAS", ""));
        } else {
            editBloc.setVisibility(View.GONE);
            title_bon.setChecked(false);
        }

        title_footer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE).edit();
            if (isChecked) {
                editFooter.setVisibility(View.VISIBLE);
                editor.putBoolean("FOOTER", true);
            } else {
                editFooter.setVisibility(View.GONE);
                editor.putBoolean("FOOTER", false);
            }
            editor.apply();
        });
    }


    /**
     * Shows icon selection dialog with sample icons.
     */
    private void showIconSelectDialog() {
        new IconSelectDialog.Builder(ActivitySetting.this)
                .setIcons(sampleIcons())
                .setTitle("Séléctionner mode tarif")
                .setSortIconsByName(true)
                .setOnIconSelectedListener(this)
                .build().show(getSupportFragmentManager(), TAG_SELECT_COLOR_DIALOG);
    }


    /**
     * Creates sample ArrayList of icons to display in dialog.
     *
     * @return sample icons
     */
    private static ArrayList<SelectableIcon> sampleIcons() {
        ArrayList<SelectableIcon> selectionDialogsColors = new ArrayList<>();
        selectionDialogsColors.add(new SelectableIcon("PV1", "Prix vente 1", R.drawable.pv1));
        selectionDialogsColors.add(new SelectableIcon("PV2", "Prix vente 2", R.drawable.pv2));
        selectionDialogsColors.add(new SelectableIcon("PV3", "Prix vente 3", R.drawable.pv3));
        return selectionDialogsColors;
    }


    @Override
    public void onIconSelected(SelectableIcon selectedItem) {
        mode_tarif.setSelectedIcon(selectedItem);
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
        String TAG = "1";
        switch (selectedItem.getId()) {

            case "PV1":
                editor.putString("PV_ID", "PV1");
                TAG = "1";
                break;
            case "PV2":
                editor.putString("PV_ID", "PV2");
                TAG = "2";
                break;
            case "PV3":
                editor.putString("PV_ID", "PV3");
                TAG = "3";
                break;
        }
        editor.apply();
        new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success!")
                .setContentText("Prix de vente " + TAG + " bien séléctionné !")
                .show();
    }

    @SuppressLint("NonConstantResourceId")
    public void onClickListener(View v) throws IOException {

        switch (v.getId()) {

            case R.id.code_depot_lnr:
                final ScalingActivityAnimator mScalingActivityAnimator = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView = mScalingActivityAnimator.start();
                final EditText edited_prix = popView.findViewById(R.id.edited_prix);
                Button mButtonSure = popView.findViewById(R.id.btn_sure);
                Button mButtonBack = popView.findViewById(R.id.btn_cancel);

                edited_prix.setText(code_depot.getText().toString());
                mButtonBack.setOnClickListener(v1 -> mScalingActivityAnimator.resume());

                mButtonSure.setOnClickListener(v12 -> {
                    code_depot.setText(edited_prix.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE).edit();
                    editor.putString("CODE_DEPOT", edited_prix.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator.resume();
                });
                break;

            case R.id.code_vendeur_lnr:
                final ScalingActivityAnimator mScalingActivityAnimator1 = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView1 = mScalingActivityAnimator1.start();
                final EditText edited_code_vendeur = popView1.findViewById(R.id.edited_prix);
                Button mButtonSure1 = popView1.findViewById(R.id.btn_sure);
                Button mButtonBack1 = popView1.findViewById(R.id.btn_cancel);

                edited_code_vendeur.setText(code_vendeur.getText().toString());
                mButtonBack1.setOnClickListener(v13 -> mScalingActivityAnimator1.resume());

                mButtonSure1.setOnClickListener(v14 -> {
                    code_vendeur.setText(edited_code_vendeur.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE).edit();
                    editor.putString("CODE_VENDEUR", edited_code_vendeur.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator1.resume();
                });
                break;
            case R.id.mode_tarif_lnr:

                break;

            case R.id.reset_pda:

                ResetDialog();
                break;

            case R.id.backup:

                Backup_db_to_ftp();

                break;
            case R.id.password_change:
                // Start change pwd  activity
                Intent intent = new Intent(getApplicationContext(), ActivityChangePwd.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


    private void ResetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PasswordResetDialogFragment editNameDialogFragment = PasswordResetDialogFragment.newInstance("Reinitialiser");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    public void lunch_back_up() {

        //TelephonyManager telephonyManager = (TelephonyManager) getSystemService(ActivityActivation.TELEPHONY_SERVICE);

        //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
        //}
       //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return;
       // }


/*
        backup_database.getName();
        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("files.000webhost.com");

            if (con.login("getwhatyouwant-all", "Boulouza1111"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
               // String data = "/sdcard/vivekm4a.m4a";
                String data = backup_database.getAbsolutePath();

                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("/"+backup_database.getName(), in);
                in.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            Log.e("TRACKKK", "gggggg" +  e.getMessage());
        }


        */
    }

    //Backup ftp function
    private void Backup_db_to_ftp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 454545);
                //return;
            } else {
                lunch_back_up();
            }
        } else {
            lunch_back_up();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 454545) {
            lunch_back_up();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //class Insert Data into FireBird Database
    //====================================
    @SuppressLint("StaticFieldLeak")
    public class TestConnection_Setting extends AsyncTask<Void, Void, Boolean> {

        Boolean executed = false;
        String _Server;
        String _pathDatabase;
        String _Username;
        String _Password;
        Connection con = null;

        public TestConnection_Setting(String server, String database, String username, String password) {
            super();
            // do stuff
            _Server = server;
            _pathDatabase = database;
            _Username = username;
            _Password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setVisibility(View.VISIBLE);
            circle.start();

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql://" + _Server + "/" + _pathDatabase + ".FDB?encoding=ISO8859_1";
                Log.d("TAG", "doInBackground: " + sCon);
                con = DriverManager.getConnection(sCon, _Username, _Password);
                executed = true;
                con = null;

            } catch (Exception ex) {
                con = null;
                Log.e("TRACKKK", "FAILED TO CONNECT WITH SERVER " + ex.getMessage());
            }
            return executed;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            circle.stop();
            isRunning[0] = false;
            textView.setVisibility(View.GONE);

            SharedPreferences.Editor editor = getSharedPreferences(PREFS_CONNEXION, MODE_PRIVATE).edit();
            editor.putString("ip", _Server);
            editor.putString("path", _pathDatabase);
            editor.putString("username", _Username);
            editor.putString("password", _Password);
            editor.apply();

            if (aBoolean) {
                @SuppressLint("InflateParams")
                View customView = getLayoutInflater().inflate(R.layout.custom_cruton_style, null);
                Crouton.show(ActivitySetting.this, customView);
                Sound(R.raw.login);
            } else {
                Animation shake = AnimationUtils.loadAnimation(ActivitySetting.this, R.anim.shakanimation);
                btntest.startAnimation(shake);
                Sound(R.raw.error);
                // Crouton.showText(_context , " Failed to Connect !", Style.ALERT);
            }
            super.onPostExecute(aBoolean);
        }
    }
    //==================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

}

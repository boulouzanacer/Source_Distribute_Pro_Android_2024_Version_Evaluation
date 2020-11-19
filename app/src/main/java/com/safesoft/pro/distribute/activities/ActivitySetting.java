package com.safesoft.pro.distribute.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.github.ybq.android.spinkit.style.Circle;
import com.safesoft.pro.distribute.activation.ActivityActivation;
import com.safesoft.pro.distribute.activities.login.ActivityChangePwd;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.fragments.PasswordResetDialogFragment;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.util.ScalingActivityAnimator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import pl.coreorb.selectiondialogs.data.SelectableIcon;
import pl.coreorb.selectiondialogs.dialogs.IconSelectDialog;
import pl.coreorb.selectiondialogs.views.SelectedItemView;

public class ActivitySetting extends AppCompatActivity implements IconSelectDialog.OnIconSelectedListener{

    private static final String TAG_SELECT_COLOR_DIALOG = "TAG_SELECT_COLOR_DIALOG";

    private DATABASE controller;
    public Circle circle;
    private MediaPlayer mp;

    private EditText ip, pathdatabase;
    private EditText comapny_name, activity, adresse,merci,encas;

    private TextView textView, code_depot, code_vendeur;
    private EasyTextInputLayout edt_objectif;

    private LinearLayout code_depot_lnr, editBloc , mode_tarif_lnr, code_vendeur_lnr,editFooter;

    private SelectedItemView mode_tarif;

    private Button btntest,enregistrer, btnobjectif,enregistrer_footer;

    private CheckBox title_bon,title_footer;

    private String PREFS_CONNEXION = "ConfigNetwork";
    private String PREFS_PRINTER = "ConfigPrinter";
    private String PREFS_FOOTER = "Footer";

    private String PREFS_SCANNER = "ConfigScanner";
    private String PREFS_AUTRE = "ConfigAutre";
    private String PARAMS_PREFS_IMPORT_EXPORT = "IMPORT_EXPORT";
    private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";

    private final Boolean[] isRunning = {false};
    private String username;
    private String password;

    private RadioButton rdb_integrete, rdb_bleutooth, rdb_scanner_integrete, rdb_scanner_camera;
    private Switch switch_importer_online, switch_exporter_online, switch_gps;
    private CheckBox chkbx_stock_moins, chkbx_achats_show,chkbx_produit;


    private LinearLayout reset_pda, change_pwd, backup_db,param_co,param_impr,param_scan,param_import,param_sound,param_gps,param_objectif,param_backup,param_secure,param_reset,param_divers;
    Button bt1,bt2,bt3,bt4,bt5,bt6,bt7,bt8,bt9,bt10,bt11;
RelativeLayout param_pda;
    private TelephonyManager telephonyManager;

    private Context mContext;

    private boolean read_write_permission = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        controller = new DATABASE(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Parametres");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        mContext = this;
        initViews();
    }

    private void initViews() {

        //EditText
        ip = (EditText) findViewById(R.id.ip);
        pathdatabase = (EditText) findViewById(R.id.database);
bt1=findViewById(R.id.bt1);
        bt2=findViewById(R.id.bt2);
        bt3=findViewById(R.id.bt3);

        bt10=findViewById(R.id.bt10);
        bt11=findViewById(R.id.bt11);



        merci = (EditText) findViewById(R.id.merci);
        encas = (EditText) findViewById(R.id.encas);
        comapny_name = (EditText) findViewById(R.id.company_name);
        activity = (EditText) findViewById(R.id.activity_name);
        adresse = (EditText) findViewById(R.id.adresse);
param_co=(LinearLayout)findViewById(R.id.param_co);
        param_pda=(RelativeLayout)findViewById(R.id.param_pda);
        param_impr=(LinearLayout)findViewById(R.id.param_impr);
        param_import=(LinearLayout)findViewById(R.id.param_import);
        param_secure=(LinearLayout)findViewById(R.id.param_secure);
        param_gps=(LinearLayout)findViewById(R.id.param_gps);
        param_objectif=(LinearLayout)findViewById(R.id.param_objectif);
        param_sound=(LinearLayout)findViewById(R.id.param_sound);
        param_backup=(LinearLayout)findViewById(R.id.param_backup);
        param_reset=(LinearLayout)findViewById(R.id.param_reset);
        param_scan=(LinearLayout)findViewById(R.id.param_scan);

        param_divers=(LinearLayout)findViewById(R.id.param_divers);




        bt1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {

        if(param_co.isShown())
        param_co.setVisibility(View.GONE);
        else
            param_co.setVisibility(View.VISIBLE);
    }
});
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(param_divers.isShown())
                    param_divers.setVisibility(View.GONE);
                else
                    param_divers.setVisibility(View.VISIBLE);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(param_impr.isShown())
                    param_impr.setVisibility(View.GONE);
                else
                    param_impr.setVisibility(View.VISIBLE);
            }
        });

        bt10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(param_backup.isShown())
                    param_backup.setVisibility(View.GONE);
                else
                    param_backup.setVisibility(View.VISIBLE);
            }
        });
        bt11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(param_reset.isShown())
                    param_reset.setVisibility(View.GONE);
                else
                    param_reset.setVisibility(View.VISIBLE);
            }
        });
        // Chekbox
        chkbx_stock_moins = (CheckBox) findViewById(R.id.chkbx_stock_moins);
        chkbx_achats_show = (CheckBox) findViewById(R.id.chkbx_achats_show);
        chkbx_produit = (CheckBox) findViewById(R.id.chkbx_photo_pr);




        // checkbox stock moins
        SharedPreferences prefs3 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if (prefs3.getBoolean("STOCK_MOINS", false)) {
            chkbx_stock_moins.setChecked(true);
        } else {
            chkbx_stock_moins.setChecked(false);
        }

        chkbx_stock_moins.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                if (isChecked) {
                    editor.putBoolean("STOCK_MOINS", true);
                } else {
                    editor.putBoolean("STOCK_MOINS", false);
                }
                editor.commit();
            }
        });


        // checkbox achats show
        prefs3 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if (prefs3.getBoolean("ACHATS_SHOW", false)) {
            chkbx_achats_show.setChecked(true);
        } else {
            chkbx_achats_show.setChecked(false);
        }

        chkbx_achats_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                if (isChecked) {
                    editor.putBoolean("ACHATS_SHOW", true);
                } else {
                    editor.putBoolean("ACHATS_SHOW", false);
                }
                editor.commit();
            }
        });

        if (prefs3.getBoolean("PR_PRO", false)) {
            chkbx_produit.setChecked(true);
        } else {
            chkbx_produit.setChecked(false);
        }

        chkbx_produit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                if (isChecked) {
                    editor.putBoolean("PR_PRO", true);
                } else {
                    editor.putBoolean("PR_PRO", false);
                }
                editor.commit();
            }
        });

        mode_tarif = (SelectedItemView) findViewById(R.id.mode_tarif_select);
        mode_tarif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showIconSelectDialog();
            }
        });

        edt_objectif = (EasyTextInputLayout) findViewById(R.id.edit_objectif);

        SharedPreferences prefs = getSharedPreferences(PREFS_CONNEXION, MODE_PRIVATE);
        ip.setText(prefs.getString("ip", "192.168.1.93"));
        pathdatabase.setText(prefs.getString("path", "C:/PMEPRO"));
        username = prefs.getString("username", "SYSDBA");
        password = prefs.getString("password", "masterkey");


        prefs = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        edt_objectif.getEditText().setText(prefs.getString("OBJECTIF_MONTANT", "0.00"));

        if(prefs.getString("PV_ID", "PV1").equals("PV1")){
            mode_tarif.setSelectedName("Prix de vente 1");
        }else if(prefs.getString("PV_ID", "PV1").equals("PV2")){
            mode_tarif.setSelectedName("Prix de vente 2");
        }else{
            mode_tarif.setSelectedName("Prix de vente 3");
        }

        //Radio button
        rdb_integrete = (RadioButton) findViewById(R.id.radioButton_integrer);
        rdb_bleutooth = (RadioButton) findViewById(R.id.radioButton_bluetooth);
        rdb_scanner_integrete = (RadioButton) findViewById(R.id.radioScanner_integrer);
        rdb_scanner_camera = (RadioButton) findViewById(R.id.radioScanner_camera);

        // Shared preference
        SharedPreferences prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if (prefs1.getString("PRINTER", "INTEGRATE").toString().equals("INTEGRATE")) {
            rdb_integrete.setChecked(true);
            rdb_bleutooth.setChecked(false);
        } else {
            rdb_integrete.setChecked(false);
            rdb_bleutooth.setChecked(true);
        }

        SharedPreferences prefs_scanner = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE);
        if (prefs_scanner.getString("SCANNER", "INTEGRATE").toString().equals("INTEGRATE")) {
            rdb_scanner_integrete.setChecked(true);
            rdb_scanner_camera.setChecked(false);
        } else {
            rdb_scanner_integrete.setChecked(false);
            rdb_scanner_camera.setChecked(true);
        }


        //Button
        btntest = (Button) findViewById(R.id.check);
        btntest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRunning[0]) {
                    new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(),
                            username.toString(), password.toString()).execute();
                    isRunning[0] = true;
                } else {
                    Crouton.showText(ActivitySetting.this, "Test Connection is running !", Style.INFO);
                }
            }
        });


        enregistrer_footer = (Button) findViewById(R.id.enregistrer_footer);
        enregistrer = (Button) findViewById(R.id.enregistrer);

        enregistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    editor.commit();
                    new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success!")
                            .setContentText("Les données sauvegarder!")
                            .show();
                }
            }
        });
        enregistrer_footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (merci.getText().toString().length() <= 0 && encas.getText().toString().length() <= 0 ) {
                    new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Oups!")
                            .setContentText("Tous les champs sont obligatoire")
                            .show();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE).edit();
                    editor.putString("MERCI", merci.getText().toString());
                    editor.putString("ENCAS", encas.getText().toString());
                    editor.commit();
                    new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success!")
                            .setContentText("Les données sauvegarder!")
                            .show();
                }
            }
        });

        btnobjectif = (Button) findViewById(R.id.BtnObjectif);
        btnobjectif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_objectif.getEditText().getText().toString() != null) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                    editor.putString("OBJECTIF_MONTANT", edt_objectif.getEditText().getText().toString());
                    editor.commit();

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

            }
        });

        //TextView
        code_depot = (TextView) findViewById(R.id.code_depot);
        code_vendeur = (TextView) findViewById(R.id.code_vendeur);
        textView = (TextView) findViewById(R.id.progress);
        circle = new Circle();
        circle.setBounds(0, 0, 60, 60);
        //noinspection deprecation
        circle.setColor(getResources().getColor(R.color.colorAccent));
        textView.setCompoundDrawables(null, null, circle, null);
        textView.setVisibility(View.GONE);

        //Relative layout
        code_depot_lnr = (LinearLayout) findViewById(R.id.code_depot_lnr);
        code_vendeur_lnr = (LinearLayout) findViewById(R.id.code_vendeur_lnr);
        editBloc = (LinearLayout) findViewById(R.id.edit_bloc);
        editFooter = (LinearLayout) findViewById(R.id.edit_footer);

        mode_tarif_lnr = (LinearLayout) findViewById(R.id.mode_tarif_lnr);

        ///////////////////
        SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
        code_depot.setText(prefs2.getString("CODE_DEPOT", "000000"));
        code_vendeur.setText(prefs2.getString("CODE_VENDEUR", "000000"));


        rdb_integrete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
                if (isChecked) {
                    rdb_bleutooth.setChecked(false);
                    editor.putString("PRINTER", "INTEGRATE");
                } else {
                    rdb_bleutooth.setChecked(true);
                    editor.putString("PRINTER", "BLEUTOOTH");
                }
                editor.commit();
            }
        });

        rdb_bleutooth.setOnCheckedChangeListener(                                                                    new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
                if (isChecked) {
                    rdb_integrete.setChecked(false);
                    editor.putString("PRINTER", "BLEUTOOTH");
                } else {
                    rdb_integrete.setChecked(true);
                    editor.putString("PRINTER", "INTEGRATE");
                }
                editor.commit();
            }
        });

        rdb_scanner_integrete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE).edit();
                if (isChecked) {
                    rdb_scanner_camera.setChecked(false);
                    editor.putString("SCANNER", "INTEGRATE");
                } else {
                    rdb_scanner_camera.setChecked(true);
                    editor.putString("SCANNER", "CAMERA");
                }
                editor.commit();
            }
        });

        rdb_scanner_camera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_SCANNER, MODE_PRIVATE).edit();
                if (isChecked) {
                    rdb_scanner_integrete.setChecked(false);
                    editor.putString("SCANNER", "INTEGRATE");
                } else {
                    rdb_scanner_integrete.setChecked(true);
                    editor.putString("SCANNER", "CAMERA");
                }
                editor.commit();
            }
        });

        //Switch
        switch_importer_online = (Switch) findViewById(R.id.switch_import);
        switch_exporter_online = (Switch) findViewById(R.id.switch_export);
        switch_gps = (Switch) findViewById(R.id.switch_gps);

        /////////////////////////////////// SWITCH IMPORTATION /////////////////////////////////////

        prefs3 = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE);
        if (prefs3.getBoolean("IMPORT_ONLINE", false)) {
            switch_importer_online.setChecked(true);
        } else {
            switch_importer_online.setChecked(false);
        }

        switch_importer_online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
                    editor.putBoolean("IMPORT_ONLINE", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
                    editor.putBoolean("IMPORT_ONLINE", false);
                    editor.commit();
                }

            }
        });

        //////////////////////////////////// SWITCH EXPORTATION ////////////////////////////////////

        if (prefs3.getBoolean("EXPORT_ONLINE", false)) {
            switch_exporter_online.setChecked(true);
        } else {
            switch_exporter_online.setChecked(false);
        }
        switch_exporter_online.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
                    editor.putBoolean("EXPORT_ONLINE", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE).edit();
                    editor.putBoolean("EXPORT_ONLINE", false);
                    editor.commit();
                }
            }
        });


        //////////////////////////////// SWITCH GPS LOCALISATION ///////////////////////////////////

        SharedPreferences prefs4 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if (prefs4.getBoolean("GPS_LOCALISATION", false)) {
            switch_gps.setChecked(true);
        } else {
            switch_gps.setChecked(false);
        }

        switch_gps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                    editor.putBoolean("GPS_LOCALISATION", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE).edit();
                    editor.putBoolean("GPS_LOCALISATION", false);
                    editor.commit();
                }
            }
        });


        // Reset database
        reset_pda = (LinearLayout) findViewById(R.id.reset_pda);


        // Backup database
        backup_db = (LinearLayout) findViewById(R.id.backup);


        // Header company setting
        title_bon = (CheckBox) findViewById(R.id.title_bon);
        title_footer = (CheckBox) findViewById(R.id.title_footer);


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

        title_bon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE).edit();
                if (isChecked) {
                    editBloc.setVisibility(View.VISIBLE);
                    editor.putBoolean("ENTETE_SHOW", true);
                } else {
                    editBloc.setVisibility(View.GONE);
                    editor.putBoolean("ENTETE_SHOW", false);
                }
                editor.commit();
            }
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

        title_footer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE).edit();
                if (isChecked) {
                    editFooter.setVisibility(View.VISIBLE);
                    editor.putBoolean("FOOTER", true);
                } else {
                    editFooter.setVisibility(View.GONE);
                    editor.putBoolean("FOOTER", false);
                }
                editor.commit();
            }
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
                .build().show(getSupportFragmentManager(), TAG_SELECT_COLOR_DIALOG );
    }


    /**
     * Creates sample ArrayList of icons to display in dialog.
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
        switch(selectedItem.getId()){

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
        editor.commit();
        new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success!")
                .setContentText("Prix de vente "+TAG+" bien séléctionné !")
                .show();
    }
    public void onClickListener(View v) throws IOException {

        switch (v.getId()) {

            case R.id.code_depot_lnr:
                final ScalingActivityAnimator mScalingActivityAnimator = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView = mScalingActivityAnimator.start();
                final EditText edited_prix = (EditText) popView.findViewById(R.id.edited_prix);
                Button mButtonSure = (Button) popView.findViewById(R.id.btn_sure);
                Button mButtonBack = (Button) popView.findViewById(R.id.btn_cancel);

                edited_prix.setText(code_depot.getText().toString());
                mButtonBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mScalingActivityAnimator.resume();
                    }
                });

                mButtonSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        code_depot.setText(edited_prix.getText().toString());
                        SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE).edit();
                        editor.putString("CODE_DEPOT", edited_prix.getText().toString());
                        editor.commit();
                        mScalingActivityAnimator.resume();
                    }
                });
                break;

            case R.id.code_vendeur_lnr:
                final ScalingActivityAnimator mScalingActivityAnimator1 = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView1 = mScalingActivityAnimator1.start();
                final EditText edited_code_vendeur = (EditText) popView1.findViewById(R.id.edited_prix);
                Button mButtonSure1 = (Button) popView1.findViewById(R.id.btn_sure);
                Button mButtonBack1 = (Button) popView1.findViewById(R.id.btn_cancel);

                edited_code_vendeur.setText(code_vendeur.getText().toString());
                mButtonBack1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mScalingActivityAnimator1.resume();
                    }
                });

                mButtonSure1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        code_vendeur.setText(edited_code_vendeur.getText().toString());
                        SharedPreferences.Editor editor = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE).edit();
                        editor.putString("CODE_VENDEUR", edited_code_vendeur.getText().toString());
                        editor.commit();
                        mScalingActivityAnimator1.resume();
                    }
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
        telephonyManager = (TelephonyManager) getSystemService(ActivityActivation.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


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
                return;
            }else {
                lunch_back_up();
            }
        }else{
            lunch_back_up();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 454545){
            lunch_back_up();

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //class Insert Data into FireBird Database
    //====================================
    public class TestConnection_Setting extends AsyncTask<Void,Void,Boolean> {

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
                String sCon = "jdbc:firebirdsql:" + _Server + ":" + _pathDatabase + ".FDB";
                con = DriverManager.getConnection(sCon, _Username, _Password);
                executed = true;
                con = null;

            }catch (Exception ex ){
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
            editor.commit();

            if(aBoolean){
                View customView = getLayoutInflater().inflate(R.layout.custom_cruton_style, null);
                Crouton.show(ActivitySetting.this, customView);
                Sound(R.raw.login);
            }else{
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
        if(item.getItemId() == android.R.id.home){
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int SourceSound){
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

}

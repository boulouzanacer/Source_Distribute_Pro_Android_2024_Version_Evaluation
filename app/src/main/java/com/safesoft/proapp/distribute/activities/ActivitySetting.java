package com.safesoft.proapp.distribute.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.rt.printerlibrary.enumerate.BarcodeType.CODE128;
import static com.safesoft.proapp.distribute.MainActivity.getAndroidID;

import android.Manifest;
//import android.content.Context;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
//import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mhadikz.toaster.Toaster;
import com.rt.printerlibrary.bean.BluetoothEdrConfigBean;
import com.rt.printerlibrary.bean.LableSizeBean;
import com.rt.printerlibrary.bean.Position;
import com.rt.printerlibrary.bean.UsbConfigBean;
import com.rt.printerlibrary.bean.WiFiConfigBean;
import com.rt.printerlibrary.cmd.Cmd;
import com.rt.printerlibrary.cmd.EscFactory;
import com.rt.printerlibrary.cmd.TscFactory;
import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.BarcodeStringPosition;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.enumerate.ConnectStateEnum;
import com.rt.printerlibrary.enumerate.PrintDirection;
import com.rt.printerlibrary.enumerate.PrintRotation;
import com.rt.printerlibrary.exception.SdkException;
import com.rt.printerlibrary.factory.cmd.CmdFactory;
import com.rt.printerlibrary.factory.connect.BluetoothFactory;
import com.rt.printerlibrary.factory.connect.PIFactory;
import com.rt.printerlibrary.factory.connect.UsbFactory;
import com.rt.printerlibrary.factory.connect.WiFiFactory;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.observer.PrinterObserver;
import com.rt.printerlibrary.observer.PrinterObserverManager;
import com.rt.printerlibrary.printer.RTPrinter;
import com.rt.printerlibrary.setting.BarcodeSetting;
import com.rt.printerlibrary.setting.CommonSetting;
import com.rt.printerlibrary.setting.TextSetting;
import com.safesoft.proapp.distribute.activation.NetClient;
import com.safesoft.proapp.distribute.activities.login.ActivityChangePwd;
import com.safesoft.proapp.distribute.app.BaseActivity;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.cloud.DownloadBackupTask;
import com.safesoft.proapp.distribute.cloud.FetchFilesTask;
import com.safesoft.proapp.distribute.cloud.FileUploader;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.dialog.BluetoothDeviceChooseDialog;
import com.safesoft.proapp.distribute.dialog.UsbDeviceChooseDialog;
import com.safesoft.proapp.distribute.eventsClasses.AddedEmailEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedBackupEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedDepotEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedVendeurEvent;
import com.safesoft.proapp.distribute.fragments.FragmentCloudAccount;
import com.safesoft.proapp.distribute.fragments.FragmentListDatabases;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedDepot;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedVendeur;
import com.safesoft.proapp.distribute.fragments.PasswordResetDialogFragment;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Depot;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Vendeur;
import com.safesoft.proapp.distribute.utils.BaseEnum;
import com.safesoft.proapp.distribute.utils.ImageUtils;
import com.safesoft.proapp.distribute.utils.SPUtils;
import com.safesoft.proapp.distribute.utils.ScalingActivityAnimator;
import com.safesoft.proapp.distribute.utils.TonyUtils;
import com.safesoft.proapp.distribute.view.FlowRadioGroup;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class ActivitySetting extends BaseActivity implements View.OnClickListener, PrinterObserver {

    public Circle circle;
    private EditText ip, pathdatabase, nom_serveur_ftp, num_port_ftp, nom_utilisateur_ftp, password_ftp, ch_exp_ftp, ch_imp_ftp;
    private TextInputLayout username_safe_event_text, password_safe_event_text;
    private EditText username_safe_event, password_safe_event;
    private Spinner type_logiciel_dropdown;
    private TextView textView, code_depot, nom_depot, code_vendeur, nom_vendeur, langue_ticket_title, model_ticket_title;
    private EditText edt_objectif;
    private Button btntest_connection,
            btn_get_all_depot,
            btn_get_all_vendeur,
            btn_disConnect_printer,
            btn_connect_printer,
            btn_connected_list,
            btn_test_impression,
            btn_clear_logo;
    //////////////////////// SharedPreferences /////////////////////
    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    /////////////////// CONFIG BLUETOOTH ///////////////////////
    private String BT_VALUE_MAC = "00:00:00:00";
    private String BT_VALUE_NAME = "Aucune imprimante";
    /////////////////// CONFIG WIFI ///////////////////////
    private String WIFI_VALUE_IP = "127.0.0.1";
    private String WIFI_VALUE_PORT = "9100";
    //////////////////////// SharedPreferences /////////////////////
    private Boolean isRunning = false;
    private String username;
    private String password;

    private ImageView company_logo;
    private FlowRadioGroup rg_connect;
    private FlowRadioGroup rg_type_imprimente;
    private FlowRadioGroup rg_langue_ticket;
    private FlowRadioGroup rg_model_latin_ticket;
    private FlowRadioGroup rg_model_arabe_ticket;
    private TextView tv_device_selected;
    private ProgressBar pb_connect;

    private RadioButton rdb_scanner_integrete;
    private RadioButton rdb_scanner_camera;

    private LinearLayout param_co, param_ftp, param_impr, param_backup, param_reset, param_divers;
    private LinearLayout lnr_back_up;

    Button bt1, bt2, bt3, bt4, bt10, bt11;
    RelativeLayout param_pda;
    private ProgressDialog mProgressDialog_Free;
    private Context mContext;

    private final String[] NEED_PERMISSION = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
    };

    private TextSetting textSetting;

    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> pairedDeviceList;

    @BaseEnum.ConnectType
    private int checkedConType = BaseEnum.CON_WIFI;
    private int checkedImpType = BaseEnum.IMP_TYPE_TICKET;
    private int checkedTicketModel = BaseEnum.TICKET_LANGUE_LATIN;
    private RTPrinter rtPrinter = null;
    private PrinterFactory printerFactory;

    private final String SP_KEY_IP = "ip";
    private final String SP_KEY_PORT = "port";
    private Object configObj;
    private final ArrayList<PrinterInterface> printerInterfaceArrayList = new ArrayList<>();


    private final List<String> NO_PERMISSION = new ArrayList<>();
    private static final int REQUEST_CAMERA = 0;
    private DATABASE controller;
    private PostData_Params params;

    private PowerSpinnerView amazingSpinner;
    private IconSpinnerItem selected_prix_revendeur;

    private TextView txtv_email_cloud, txtv_password_cloud;
    private Button btn_add_update_cloud,btn_delete_cloud;

    private boolean is_app_synchronised_mode = false;

    private void CheckAllPermission() {
        NO_PERMISSION.clear();
        for (String s : NEED_PERMISSION) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                NO_PERMISSION.add(s);
            }
        }
        if (NO_PERMISSION.isEmpty()) {

        } else {
            requestPermissions(NO_PERMISSION.toArray(new String[0]), REQUEST_CAMERA);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        CheckAllPermission();

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Paramétres");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        controller = new DATABASE(this);

        params = new PostData_Params();
        try {
            params = controller.select_params_from_database("SELECT * FROM PARAMS");
        } catch (Exception e) {

        }

        mContext = this;
        initView();
        init();
        addListener();

    }


    @Override
    public void initView() {

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (String split : splits) {
                            if (Integer.parseInt(split) > 255) {
                                return "";
                            }
                        }
                    }
                }
                return null;
            }
        };

        amazingSpinner = findViewById(R.id.amazingspinner);
        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();

        PostData_Params params = new PostData_Params();
        params = controller.select_params_from_database("SELECT * FROM PARAMS");

        iconSpinnerItems.add(new IconSpinnerItem("Libre"));

        if (is_app_synchronised_mode) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv1_titre));
        }else {
            iconSpinnerItems.add(new IconSpinnerItem("Prix 1"));
        }

        if (params.prix_2 == 1 && is_app_synchronised_mode) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv2_titre));
        }else {
            iconSpinnerItems.add(new IconSpinnerItem("Prix 2"));
        }

        if (params.prix_3 == 1 && is_app_synchronised_mode) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv3_titre));
        }else {
            iconSpinnerItems.add(new IconSpinnerItem("Prix 3"));
        }

        if (params.prix_4 == 1) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv4_titre));
        }

        if (params.prix_5 == 1) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv5_titre));
        }

        if (params.prix_6 == 1) {
            iconSpinnerItems.add(new IconSpinnerItem(params.pv6_titre));
        }


        //ArrayAdapter<String> adapter_prix_revendeurs = new ArrayAdapter<>(this, R.layout.dropdown_famille_item, prix_revendeurs);
        //amazingSpinner.setSpinnerAdapter(adapter_prix_revendeurs);

        lnr_back_up = findViewById(R.id.backup);

        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(amazingSpinner);
        amazingSpinner.setSpinnerAdapter(iconSpinnerAdapter);
        amazingSpinner.setItems(iconSpinnerItems);
        //amazingSpinner.selectItemByIndex(0);
        amazingSpinner.setLifecycleOwner(this);



        if (is_app_synchronised_mode) {
            if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Libre")){
                amazingSpinner.selectItemByIndex(0);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv1_titre)) {
                amazingSpinner.selectItemByIndex(1);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv2_titre)) {
                amazingSpinner.selectItemByIndex(2);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv3_titre)) {
                amazingSpinner.selectItemByIndex(3);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv4_titre)) {
                amazingSpinner.selectItemByIndex(4);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv5_titre)) {
                amazingSpinner.selectItemByIndex(5);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals(params.pv6_titre)) {
                amazingSpinner.selectItemByIndex(6);
            }
        }else{
            if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Libre")){
                amazingSpinner.selectItemByIndex(0);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 1")) {
                amazingSpinner.selectItemByIndex(1);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 2")) {
                amazingSpinner.selectItemByIndex(2);
            }else if(prefs.getString("PRIX_REVENDEUR", "Libre").equals("Prix 3")) {
                amazingSpinner.selectItemByIndex(3);
            }
        }


        amazingSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override public void onItemSelected(int oldIndex, @Nullable Object oldItem, int newIndex, Object newItem) {

                selected_prix_revendeur = (IconSpinnerItem) newItem;
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("PRIX_REVENDEUR", selected_prix_revendeur.getText().toString());
                editor.apply();

            }
        });


        txtv_email_cloud = findViewById(R.id.txtv_email_cloud);
        txtv_password_cloud = findViewById(R.id.txtv_password_cloud);
        btn_add_update_cloud = findViewById(R.id.btn_add_update_cloud);
        btn_delete_cloud = findViewById(R.id.btn_delete_cloud);

        txtv_email_cloud.setText(prefs.getString("EMAIL_COMPTE_CLOUD", ""));
        txtv_password_cloud.setText(prefs.getString("PASSWORD_COMPTE_CLOUD", ""));
        if(txtv_email_cloud.getText().equals("")){
            lnr_back_up.setEnabled(false);
        }
        btn_add_update_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentCloudAccount fragmentCloudAccount = new FragmentCloudAccount();
                fragmentCloudAccount.showDialogbox(ActivitySetting.this, txtv_email_cloud.getText().toString(), txtv_password_cloud.getText().toString());
            }
        });

        btn_delete_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Compte cloud")
                        .setContentText("Voulez-vous vraiment supprimer le compte cloud maintenant ?")
                        .setCancelText("Non")
                        .setConfirmText("Oui")
                        .showCancelButton(true)
                        .setCancelClickListener(Dialog::dismiss)
                        .setConfirmClickListener(sDialog -> {

                            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                            editor.putString("EMAIL_COMPTE_CLOUD", "");
                            editor.putString("PASSWORD_COMPTE_CLOUD", "");
                            editor.apply();

                            txtv_email_cloud.setText("");
                            txtv_password_cloud.setText("");

                            lnr_back_up.setEnabled(false);

                            sDialog.dismiss();
                        })
                        .setCancelClickListener(Dialog::dismiss)
                        .show();


            }
        });

        //EditText
        ip = findViewById(R.id.ip);
        ip.setFilters(filters);
        pathdatabase = findViewById(R.id.database);
        type_logiciel_dropdown = findViewById(R.id.type_logiciel);

        username_safe_event_text = findViewById(R.id.username_safe_event_text);
        password_safe_event_text = findViewById(R.id.password_safe_event_text);

        username_safe_event = findViewById(R.id.username_event);
        password_safe_event = findViewById(R.id.password_event);


        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);

        bt10 = findViewById(R.id.bt10);
        bt11 = findViewById(R.id.bt11);


        company_logo = findViewById(R.id.comapny_logo);
        TextInputEditText comapny_name = findViewById(R.id.edt_company_name);
        TextInputEditText activity = findViewById(R.id.edt_activity_name);
        TextInputEditText adresse = findViewById(R.id.edt_company_adresse);
        TextInputEditText tel = findViewById(R.id.edt_company_tel);
        TextInputEditText footer = findViewById(R.id.edt_pied_de_page);

        company_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserGallery();
            }
        });


        comapny_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("COMPANY_NAME", s.toString());
                editor.apply();   // editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        activity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("ACTIVITY_NAME", s.toString());
                editor.apply();   // editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        adresse.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("COMPANY_ADRESSE", s.toString());
                editor.apply();   // editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("COMPANY_TEL", s.toString());
                editor.apply();   // editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        footer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("COMPANY_FOOTER", s.toString());
                editor.apply();   // editor.commit();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        param_co = findViewById(R.id.param_co);
        param_ftp = findViewById(R.id.param_ftp);
        param_impr = findViewById(R.id.param_impr);
        param_pda = findViewById(R.id.param_pda);
        param_backup = findViewById(R.id.param_backup);
        param_reset = findViewById(R.id.param_reset);
        param_divers = findViewById(R.id.param_divers);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.type_logiciel_list, android.R.layout.simple_spinner_dropdown_item);
        type_logiciel_dropdown.setAdapter(adapter);


        String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");
        type_logiciel_dropdown.setSelection(adapter.getPosition(TYPE_LOGICIEL));
        type_logiciel_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                editor.putString("TYPE_LOGICIEL", (String) parent.getItemAtPosition(position));
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        bt1.setOnClickListener(view -> {

            if (param_co.isShown())
                param_co.setVisibility(GONE);
            else {
                param_co.setVisibility(VISIBLE);
                param_ftp.setVisibility(GONE);
                param_impr.setVisibility(GONE);
                param_reset.setVisibility(GONE);
                param_backup.setVisibility(GONE);
                param_divers.setVisibility(GONE);
            }
        });

        bt4.setOnClickListener(view -> {

            if (param_ftp.isShown())
                param_ftp.setVisibility(GONE);
            else {
                param_co.setVisibility(GONE);
                param_ftp.setVisibility(VISIBLE);
                param_impr.setVisibility(GONE);
                param_reset.setVisibility(GONE);
                param_backup.setVisibility(GONE);
                param_divers.setVisibility(GONE);
            }
        });

        bt2.setOnClickListener(view -> {

            if (param_divers.isShown())
                param_divers.setVisibility(GONE);
            else {
                param_co.setVisibility(GONE);
                param_ftp.setVisibility(GONE);
                param_impr.setVisibility(GONE);
                param_reset.setVisibility(GONE);
                param_backup.setVisibility(GONE);
                param_divers.setVisibility(VISIBLE);
            }
        });
        bt3.setOnClickListener(view -> {

            if (param_impr.isShown())
                param_impr.setVisibility(GONE);
            else {
                param_co.setVisibility(GONE);
                param_ftp.setVisibility(GONE);
                param_impr.setVisibility(VISIBLE);
                param_reset.setVisibility(GONE);
                param_backup.setVisibility(GONE);
                param_divers.setVisibility(GONE);
            }
        });

        bt10.setOnClickListener(view -> {

            if (param_backup.isShown())
                param_backup.setVisibility(GONE);
            else {
                param_co.setVisibility(GONE);
                param_ftp.setVisibility(GONE);
                param_impr.setVisibility(GONE);
                param_reset.setVisibility(GONE);
                param_backup.setVisibility(VISIBLE);
                param_divers.setVisibility(GONE);
            }
        });

        bt11.setOnClickListener(view -> {

            if (param_reset.isShown())
                param_reset.setVisibility(GONE);
            else {
                param_co.setVisibility(GONE);
                param_ftp.setVisibility(GONE);
                param_impr.setVisibility(GONE);
                param_reset.setVisibility(VISIBLE);
                param_backup.setVisibility(GONE);
                param_divers.setVisibility(GONE);
            }
        });

        // Chekbox
        CheckBox chkbx_safe_event = findViewById(R.id.chkbx_safe_event);

        // checkbox stock moins
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        chkbx_safe_event.setChecked(prefs.getBoolean("USE_SAFE_EVENT", false));
        username_safe_event.setText(prefs.getString("SAFE_EVENT_USER", ""));
        password_safe_event.setText(prefs.getString("SAFE_EVENT_PASS", ""));

        chkbx_safe_event.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("USE_SAFE_EVENT", isChecked);
            editor.apply();   // editor.commit();

            if (isChecked) {
                username_safe_event_text.setVisibility(VISIBLE);
                password_safe_event_text.setVisibility(VISIBLE);
            } else {
                ip.setText("192.168.1.6");
                username_safe_event_text.setVisibility(GONE);
                password_safe_event_text.setVisibility(GONE);
            }

        });

        if (prefs.getBoolean("USE_SAFE_EVENT", false)) {
            username_safe_event_text.setVisibility(VISIBLE);
            password_safe_event_text.setVisibility(VISIBLE);
        } else {
            username_safe_event_text.setVisibility(GONE);
            password_safe_event_text.setVisibility(GONE);
        }

        edt_objectif = findViewById(R.id.edit_objectif);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        ip.setText(prefs.getString("ip", "192.168.1.6"));
        pathdatabase.setText(prefs.getString("path", "D:/P-VENTE/DATA/PME PRO/DISTRIBUTE"));
        username = prefs.getString("username", "SYSDBA");
        password = prefs.getString("password", "masterkey");

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        nom_serveur_ftp = findViewById(R.id.serveur_ftp);


        num_port_ftp = findViewById(R.id.port_ftp);
        nom_utilisateur_ftp = findViewById(R.id.utilisateur_ftp);
        password_ftp = findViewById(R.id.pwd_ftp);
        ch_exp_ftp = findViewById(R.id.exp_ftp);
        ch_imp_ftp = findViewById(R.id.imp_ftp);


        if (params != null) {
            nom_serveur_ftp.setText(params.ftp_server);
            num_port_ftp.setText(params.ftp_port);
            nom_utilisateur_ftp.setText(params.ftp_user);
            password_ftp.setText(params.ftp_pass);
            ch_exp_ftp.setText(params.ftp_imp);
            ch_imp_ftp.setText(params.ftp_exp);
        }


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        edt_objectif.setText(prefs.getString("OBJECTIF_MONTANT", "0.00"));


        rg_connect = findViewById(R.id.rg_connect);
        rg_type_imprimente = findViewById(R.id.rg_type_imprimente);
        rg_langue_ticket = findViewById(R.id.rg_langue_ticket);
        rg_model_latin_ticket = findViewById(R.id.rg_model_latin_ticket);
        rg_model_arabe_ticket = findViewById(R.id.rg_model_arabe_ticket);

        btn_connect_printer = findViewById(R.id.btn_connect_printer);
        btn_disConnect_printer = findViewById(R.id.btn_disConnect_printer);
        tv_device_selected = findViewById(R.id.tv_device_selected);
        btn_connected_list = findViewById(R.id.btn_connected_list);
        pb_connect = findViewById(R.id.pb_connect);
        btn_test_impression = findViewById(R.id.btn_test);
        btn_clear_logo = findViewById(R.id.btn_clear_logo);


        //Radio button
        rdb_scanner_integrete = findViewById(R.id.radioScanner_integrer);
        rdb_scanner_camera = findViewById(R.id.radioScanner_camera);


        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("SCANNER", "INTEGRATE"), "INTEGRATE")) {
            rdb_scanner_integrete.setChecked(true);
            rdb_scanner_camera.setChecked(false);
        } else {
            rdb_scanner_integrete.setChecked(false);
            rdb_scanner_camera.setChecked(true);
        }

        //Button
        btntest_connection = findViewById(R.id.btntest_connection);
        btn_get_all_depot = findViewById(R.id.get_all_depot);
        btn_get_all_vendeur = findViewById(R.id.get_all_vendeur);

        btntest_connection.setOnClickListener(v -> {
            // SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (!isRunning) {
                if (prefs.getBoolean("USE_SAFE_EVENT", false)) {

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("SAFE_EVENT_USER", username_safe_event.getText().toString());
                    editor.putString("SAFE_EVENT_PASS", password_safe_event.getText().toString());
                    editor.apply();   // editor.commit();

                    textView.setVisibility(VISIBLE);
                    circle.start();
                    sendRequest(username_safe_event.getText().toString(), password_safe_event.getText().toString(), "TEST_CONNEXION");
                } else {
                    textView.setVisibility(VISIBLE);
                    circle.start();
                    new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(), username, password, "TEST_CONNEXION").execute();
                }
                isRunning = true;
            } else {
                Crouton.showText(ActivitySetting.this, "Demande de connexion est en cours!", Style.INFO);
            }
        });

        btn_get_all_depot.setOnClickListener(v -> {
            // SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (!isRunning) {
                if (prefs.getBoolean("USE_SAFE_EVENT", false)) {

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("SAFE_EVENT_USER", username_safe_event.getText().toString());
                    editor.putString("SAFE_EVENT_PASS", password_safe_event.getText().toString());
                    editor.apply();   // editor.commit();

                    textView.setVisibility(VISIBLE);
                    circle.start();
                    sendRequest(username_safe_event.getText().toString(), password_safe_event.getText().toString(), "GET_ALL_DEPOT");
                } else {
                    textView.setVisibility(VISIBLE);
                    circle.start();
                    new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(), username, password, "GET_ALL_DEPOT").execute();
                }
                isRunning = true;
            } else {
                Crouton.showText(ActivitySetting.this, "Demande de connexion est en cours!", Style.INFO);
            }
        });

        btn_get_all_vendeur.setOnClickListener(v -> {
            // SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (!isRunning) {
                if (prefs.getBoolean("USE_SAFE_EVENT", false)) {

                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("SAFE_EVENT_USER", username_safe_event.getText().toString());
                    editor.putString("SAFE_EVENT_PASS", password_safe_event.getText().toString());
                    editor.apply();   // editor.commit();

                    textView.setVisibility(VISIBLE);
                    circle.start();
                    sendRequest(username_safe_event.getText().toString(), password_safe_event.getText().toString(), "GET_ALL_VENDEUR");
                } else {
                    textView.setVisibility(VISIBLE);
                    circle.start();
                    new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(), username, password, "GET_ALL_VENDEUR").execute();
                }
                isRunning = true;
            } else {
                Crouton.showText(ActivitySetting.this, "Demande de connexion est en cours!", Style.INFO);
            }
        });

        btn_clear_logo.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putString("COMPANY_LOGO", "");
            editor.apply();

            company_logo.setImageResource(R.mipmap.user_profile);
        });

        Button btn_save_ftp = findViewById(R.id.save_ftp);
        btn_save_ftp.setOnClickListener(v -> {

            //// sauvegaure de parametre de serveur FTP

            //====================== FTP =================================
            PostData_Params params2 = new PostData_Params();

            params2.ftp_server = nom_serveur_ftp.getText().toString();
            params2.ftp_port = num_port_ftp.getText().toString();
            params2.ftp_user = nom_utilisateur_ftp.getText().toString();
            params2.ftp_pass = password_ftp.getText().toString();
            params2.ftp_imp = ch_exp_ftp.getText().toString();
            params2.ftp_exp = ch_imp_ftp.getText().toString();

            controller.update_ftp_params(params2);

            new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!")
                    .setContentText("Paramètred FTP sauvegarder!")
                    .show();

        });

        Button btnobjectif = findViewById(R.id.BtnObjectif);
        btnobjectif.setOnClickListener(v -> {

            edt_objectif.getText().toString();
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putString("OBJECTIF_MONTANT", edt_objectif.getText().toString());
            editor.apply();

            new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Success!")
                    .setContentText("Montant objectif sauvegardé!")
                    .show();

        });

        //TextView
        code_depot = findViewById(R.id.code_depot);
        nom_depot = findViewById(R.id.nom_depot);
        code_vendeur = findViewById(R.id.code_vendeur);
        nom_vendeur = findViewById(R.id.nom_vendeur);

        langue_ticket_title = findViewById(R.id.langue_ticket_title);
        model_ticket_title = findViewById(R.id.model_ticket_title);

        textView = findViewById(R.id.progress);
        circle = new Circle();
        circle.setBounds(0, 0, 60, 60);

        circle.setColor(getResources().getColor(R.color.colorAccent));
        textView.setCompoundDrawables(null, null, circle, null);
        textView.setVisibility(GONE);


        ///////////////////
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot.setText(prefs.getString("CODE_DEPOT", "000000"));
        nom_depot.setText(prefs.getString("NOM_DEPOT", "..."));
        code_vendeur.setText(prefs.getString("CODE_VENDEUR", "000000"));
        nom_vendeur.setText(prefs.getString("NOM_VENDEUR", "..."));

        rdb_scanner_integrete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
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
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
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
        Switch switch_module_achat = findViewById(R.id.switch_module_achat);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_module_vente = findViewById(R.id.switch_module_vente);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_module_commande = findViewById(R.id.switch_module_commande);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_module_inventaire = findViewById(R.id.switch_module_inventaire);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_ht = findViewById(R.id.switch_ht);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_pa_ht = findViewById(R.id.switch_pa_ht);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_sychroniser_client = findViewById(R.id.switch_sychroniser_client);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_achat_client = findViewById(R.id.switch_achat_client);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_benifice = findViewById(R.id.switch_benifice);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_stock_moins = findViewById(R.id.switch_stock_moins);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_solde_client = findViewById(R.id.switch_solde_client);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_remise = findViewById(R.id.switch_remise);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_modifier_bon = findViewById(R.id.switch_modifier_bon);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_edit_prix = findViewById(R.id.switch_edit_prix);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_famille = findViewById(R.id.switch_famille);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_filtre_recherche = findViewById(R.id.switch_filtre_recherche);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_gps = findViewById(R.id.switch_gps);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_son = findViewById(R.id.switch_son);

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_vente_stock_negatif = findViewById(R.id.switch_vente_stock_negatif);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch switch_show_photo_produit = findViewById(R.id.switch_show_photo_produit);


        //////////////////////////////// SWITCH ///////////////////////////////////
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        switch_module_achat.setChecked(prefs.getBoolean("MODULE_ACHAT", true));
        switch_module_vente.setChecked(prefs.getBoolean("MODULE_VENTE", true));
        switch_module_commande.setChecked(prefs.getBoolean("MODULE_COMMANDE", true));
        switch_module_inventaire.setChecked(prefs.getBoolean("MODULE_INVENTAIRE", true));

        switch_ht.setChecked(prefs.getBoolean("AFFICHAGE_HT", false));
        switch_pa_ht.setChecked(prefs.getBoolean("AFFICHAGE_PA_HT", false));
        switch_sychroniser_client.setChecked(prefs.getBoolean("SYCHRONISER_TOUS_CLIENT", false));
        switch_achat_client.setChecked(prefs.getBoolean("SHOW_ACHAT_CLIENT", false));
        switch_benifice.setChecked(prefs.getBoolean("AFFICHAGE_BENIFICE", false));
        switch_stock_moins.setChecked(prefs.getBoolean("AFFICHAGE_STOCK_MOINS", false));
        switch_solde_client.setChecked(prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true));
        switch_remise.setChecked(prefs.getBoolean("AFFICHAGE_REMISE", true));
        switch_modifier_bon.setChecked(prefs.getBoolean("AUTORISE_MODIFY_BON", true));
        switch_edit_prix.setChecked(prefs.getBoolean("CAN_EDIT_PRICE", false));
        switch_famille.setChecked(prefs.getBoolean("MODE_FAMILLE", false));
        switch_filtre_recherche.setChecked(prefs.getBoolean("FILTRE_SEARCH", false));

        switch_gps.setChecked(prefs.getBoolean("GPS_LOCALISATION", false));
        switch_son.setChecked(prefs.getBoolean("ENABLE_SOUND", false));

        switch_vente_stock_negatif.setChecked(prefs.getBoolean("STOCK_MOINS", false));
        switch_show_photo_produit.setChecked(prefs.getBoolean("SHOW_PROD_PIC", false));


        switch_module_achat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("MODULE_ACHAT", isChecked);
            editor.apply();
        });

        switch_module_vente.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("MODULE_VENTE", isChecked);
            editor.apply();
        });

        switch_module_commande.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("MODULE_COMMANDE", isChecked);
            editor.apply();
        });

        switch_module_inventaire.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("MODULE_INVENTAIRE", isChecked);
            editor.apply();
        });


        switch_ht.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_HT", isChecked);
            editor.apply();
        });

        switch_pa_ht.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_PA_HT", isChecked);
            editor.apply();
        });

        switch_sychroniser_client.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("SYCHRONISER_TOUS_CLIENT", isChecked);
            editor.apply();
        });

        switch_achat_client.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("SHOW_ACHAT_CLIENT", isChecked);
            editor.apply();
        });

        switch_benifice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_BENIFICE", isChecked);
            editor.apply();
        });

        switch_stock_moins.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_STOCK_MOINS", isChecked);
            editor.apply();
        });

        switch_solde_client.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_SOLDE_CLIENT", isChecked);
            editor.apply();
        });

        switch_remise.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AFFICHAGE_REMISE", isChecked);
            editor.apply();
        });

        switch_modifier_bon.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("AUTORISE_MODIFY_BON", isChecked);
            editor.apply();
        });

        switch_edit_prix.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("CAN_EDIT_PRICE", isChecked);
            editor.apply();
        });

        switch_famille.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("MODE_FAMILLE", isChecked);
            editor.apply();
        });

        switch_filtre_recherche.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("FILTRE_SEARCH", isChecked);
            editor.apply();
        });

        switch_gps.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("GPS_LOCALISATION", isChecked);
            editor.apply();
        });

        switch_son.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("ENABLE_SOUND", isChecked);
            editor.apply();
        });


        switch_vente_stock_negatif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("STOCK_MOINS", isChecked);
            editor.apply();
        });

        switch_show_photo_produit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putBoolean("SHOW_PROD_PIC", isChecked);
            editor.apply();
        });

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        comapny_name.setText(prefs.getString("COMPANY_NAME", ""));
        activity.setText(prefs.getString("ACTIVITY_NAME", ""));
        adresse.setText(prefs.getString("COMPANY_ADRESSE", ""));
        tel.setText(prefs.getString("COMPANY_TEL", ""));
        footer.setText(prefs.getString("COMPANY_FOOTER", ""));

        String img_str = prefs.getString("COMPANY_LOGO", "");
        if (!img_str.equals("")) {
            //decode string to image
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
            company_logo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            // company_logo.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length) );
        }
    }


    public void init() {

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        // rtPrinter.setPrinterInterface(curPrinterInterface);
        PrinterObserverManager.getInstance().add(this);
        textSetting = new TextSetting();
        setPrintEnable(BaseApplication.getInstance().getIsConnected());
        if (BaseApplication.getInstance().getIsConnected()) {
            tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
        } else {
            tv_device_selected.setTag(BaseEnum.NO_DEVICE);
        }
    }

    public void addListener() {

        btn_connect_printer.setOnClickListener(this);
        btn_disConnect_printer.setOnClickListener(this);
        tv_device_selected.setOnClickListener(this);
        btn_connected_list.setOnClickListener(this);
        btn_test_impression.setOnClickListener(this);

        radioButtonCheckListener();//single button listener
        // Shared preference
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            rg_connect.check(R.id.rb_connect_bluetooth);
            tv_device_selected.setText(prefs.getString("PRINTER_NAME", "Aucune imprimante"));
            if (Objects.equals(prefs.getString("PRINTER_NAME", "Aucune imprimante"), "Aucune imprimante")) {
                tv_device_selected.setTag(BaseEnum.NO_DEVICE);
            } else {
                tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {
            rg_connect.check(R.id.rb_connect_wifi);
            WIFI_VALUE_IP = prefs.getString("PRINTER_IP", "127.0.0.1");
            WIFI_VALUE_PORT = prefs.getString("PRINTER_PORT", "9100");
            tv_device_selected.setText(WIFI_VALUE_IP + ":" + WIFI_VALUE_PORT);
            if (Objects.equals(prefs.getString("PRINTER_IP", "127.0.0.1"), "127.0.0.1")) {
                tv_device_selected.setTag(BaseEnum.NO_DEVICE);
            } else {
                tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
            }
        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "USB")) {
            rg_connect.check(R.id.rb_connect_usb);
        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "COM")) {
            rg_connect.check(R.id.rb_connect_com);
        }

        if (Objects.equals(prefs.getString("PRINTER_TYPE", "IMP_TICKET"), "IMP_TICKET")) {
            rg_type_imprimente.check(R.id.rb_type_ticket);
        } else if (Objects.equals(prefs.getString("PRINTER_TYPE", "IMP_TICKET"), "IMP_CODEBARRE")) {
            rg_type_imprimente.check(R.id.rb_type_codebarre);
        }

        if (Objects.equals(prefs.getString("LANGUE_TICKET", "LATIN"), "LATIN")) {
            rg_langue_ticket.check(R.id.rb_langue_latin);
            rg_model_latin_ticket.setVisibility(VISIBLE);
            rg_model_arabe_ticket.setVisibility(GONE);
        } else if (Objects.equals(prefs.getString("LANGUE_TICKET", "ARABE"), "ARABE")) {
            rg_langue_ticket.check(R.id.rb_langue_arabe);
            rg_model_latin_ticket.setVisibility(GONE);
            rg_model_arabe_ticket.setVisibility(VISIBLE);
        }

        if (Objects.equals(prefs.getString("MODEL_TICKET_LATIN", "MODEL 1"), "MODEL 1")) {
            rg_model_latin_ticket.check(R.id.rb_model_latin_1);
        } else if (Objects.equals(prefs.getString("MODEL_TICKET_LATIN", "MODEL 1"), "MODEL 2")) {
            rg_model_latin_ticket.check(R.id.rb_model_latin_2);
        }

        if (Objects.equals(prefs.getString("MODEL_TICKET_ARABE", "MODEL 3"), "MODEL 3")) {
            rg_model_arabe_ticket.check(R.id.rb_model_arabe_1);
        } else if (Objects.equals(prefs.getString("MODEL_TICKET_ARABE", "MODEL 3"), "MODEL 4")) {
            rg_model_arabe_ticket.check(R.id.rb_model_arabe_2);
        }

        //EventBus listener
        EventBus.getDefault().register(this);
    }

    private void radioButtonCheckListener() {

        rg_connect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // doDisConnect();
                switch (i) {
                    case R.id.rb_connect_wifi ->//WiFi
                            checkedConType = BaseEnum.CON_WIFI;
                    case R.id.rb_connect_bluetooth ->//bluetooth
                            checkedConType = BaseEnum.CON_BLUETOOTH;
                    case R.id.rb_connect_usb ->//usb
                            checkedConType = BaseEnum.CON_USB;
                    case R.id.rb_connect_com ->//串口-AP02
                            checkedConType = BaseEnum.CON_COM;
                }
            }
        });

        rg_type_imprimente.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // doDisConnect();
                switch (i) {
                    case R.id.rb_type_ticket ->//Ticket
                    {
                        langue_ticket_title.setVisibility(VISIBLE);
                        rg_langue_ticket.setVisibility(VISIBLE);
                        checkedImpType = BaseEnum.IMP_TYPE_TICKET;
                    }
                    case R.id.rb_type_codebarre ->//codebarre
                    {
                        langue_ticket_title.setVisibility(GONE);
                        rg_langue_ticket.setVisibility(GONE);
                        checkedImpType = BaseEnum.IMP_TYPE_CODEBARRE;
                        checkedTicketModel = BaseEnum.TICKET_LANGUE_LATIN;
                        saveConfigTicketLangue(checkedTicketModel);
                    }
                }
                saveConfigImpType(checkedImpType);

            }
        });

        rg_langue_ticket.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // doDisConnect();
                switch (i) {
                    case R.id.rb_langue_latin ->//Ticket latin
                    {
                        //model_ticket_title.setVisibility(View.VISIBLE);
                        rg_model_latin_ticket.setVisibility(VISIBLE);
                        rg_model_arabe_ticket.setVisibility(GONE);
                        checkedTicketModel = BaseEnum.TICKET_LANGUE_LATIN;
                    }
                    case R.id.rb_langue_arabe ->//ticket arabe
                    {
                        //model_ticket_title.setVisibility(View.GONE);
                        rg_model_arabe_ticket.setVisibility(VISIBLE);
                        rg_model_latin_ticket.setVisibility(GONE);
                        checkedTicketModel = BaseEnum.TICKET_LANGUE_ARABE;
                    }
                }
                saveConfigTicketLangue(checkedTicketModel);
            }
        });


        rg_model_latin_ticket.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // doDisConnect();
                switch (i) {
                    case R.id.rb_model_latin_1 ->//Ticket latin
                            checkedTicketModel = BaseEnum.TICKET_LATIN_MODEL_1;
                    case R.id.rb_model_latin_2 ->//ticket arabe
                            checkedTicketModel = BaseEnum.TICKET_LATIN_MODEL_2;
                }
                saveConfigTicketLangueLatin(checkedTicketModel);
            }
        });

        rg_model_arabe_ticket.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                // doDisConnect();
                switch (i) {
                    case R.id.rb_model_arabe_1 ->//Ticket latin
                            checkedTicketModel = BaseEnum.TICKET_ARABE_MODEL_1;
                    case R.id.rb_model_arabe_2 ->//ticket arabe
                            checkedTicketModel = BaseEnum.TICKET_ARABE_MODEL_2;
                }
                saveConfigTicketLangueArabe(checkedTicketModel);
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_disConnect_printer:
                doDisConnect();
                break;
            case R.id.btn_connect_printer:
                doConnect();
                break;
            case R.id.tv_device_selected:
                //showConnectDialog();
                break;
            case R.id.btn_connected_list://显示多连接
                //showConnectedListDialog();
                showConnectDialog();
                break;
            case R.id.btn_test:
                try {
                    textPrint();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (SdkException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void printerObserverCallback(final PrinterInterface printerInterface, final int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb_connect.setVisibility(GONE);
                switch (state) {
                    case CommonEnum.CONNECT_STATE_SUCCESS:
                        tv_device_selected.setText(printerInterface.getConfigObject().toString());
                        tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                        setPrintEnable(true);
                        saveConfigConx(checkedConType);
                        break;
                    case CommonEnum.CONNECT_STATE_INTERRUPTED:

                        //tv_device_selected.setText("Aucune imprimante" +printerInterface.getConnectState().toString());
                        //tv_device_selected.setTag(BaseEnum.NO_DEVICE);
                        setPrintEnable(false);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void printerReadMsgCallback(PrinterInterface printerInterface, byte[] bytes) {

    }


    private void saveConfigConx(int conType) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        switch (conType) {
            case BaseEnum.CON_WIFI -> {//WiFi
                editor.putString("PRINTER_CONX", "WIFI");
                editor.putString("PRINTER_IP", WIFI_VALUE_IP);
                editor.putString("PRINTER_PORT", WIFI_VALUE_PORT);
            }
            case BaseEnum.CON_BLUETOOTH -> {//bluetooth
                editor.putString("PRINTER_CONX", "BLUETOOTH");
                editor.putString("PRINTER_MAC", BT_VALUE_MAC);
                editor.putString("PRINTER_NAME", BT_VALUE_NAME);
            }
            case BaseEnum.CON_USB ->//usb
                    editor.putString("PRINTER_CONX", "USB");

            //editor.putString("PRINTER_MAC", "USB");
            case BaseEnum.CON_COM ->//串口-AP02
                    editor.putString("PRINTER_CONX", "COM");

            //editor.putString("PRINTER_MAC", "COM");
        }
        editor.apply();
    }

    private void saveConfigImpType(int impType) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        switch (impType) {
            case BaseEnum.IMP_TYPE_TICKET -> {//ticket
                editor.putString("PRINTER_TYPE", "IMP_TICKET");
            }
            case BaseEnum.IMP_TYPE_CODEBARRE -> {//codebarre
                editor.putString("PRINTER_TYPE", "IMP_CODEBARRE");
            }
        }
        editor.apply();
    }

    private void saveConfigTicketLangue(int ticketLangue) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        switch (ticketLangue) {
            case BaseEnum.TICKET_LANGUE_LATIN -> {//ticket latine
                editor.putString("LANGUE_TICKET", "LATIN");
            }
            case BaseEnum.TICKET_LANGUE_ARABE -> {//ticket arabe
                editor.putString("LANGUE_TICKET", "ARABE");
            }
        }
        editor.apply();
    }

    private void saveConfigTicketLangueLatin(int ticketLangueLatin) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        switch (ticketLangueLatin) {
            case BaseEnum.TICKET_LATIN_MODEL_1 -> //ticket model 1
                    editor.putString("MODEL_TICKET_LATIN", "MODEL 1");
            case BaseEnum.TICKET_LATIN_MODEL_2 -> //ticket model 2
                    editor.putString("MODEL_TICKET_LATIN", "MODEL 2");
        }
        editor.apply();
    }

    private void saveConfigTicketLangueArabe(int ticketLangueArabe) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        switch (ticketLangueArabe) {
            case BaseEnum.TICKET_ARABE_MODEL_1 -> //ticket model 1
                    editor.putString("MODEL_TICKET_ARABE", "MODEL 3");
            case BaseEnum.TICKET_ARABE_MODEL_2 -> //ticket model 2
                    editor.putString("MODEL_TICKET_ARABE", "MODEL 4");
        }
        editor.apply();
    }

    /**
     * wifi 连接信息填写
     */
    private void showWifiChooseDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_tip);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_wifi_config, null);
        final EditText et_wifi_ip = view.findViewById(R.id.et_wifi_ip);
        final EditText et_wifi_port = view.findViewById(R.id.et_wifi_port);

        String spIp = SPUtils.get(ActivitySetting.this, SP_KEY_IP, "192.168.").toString();
        String spPort = SPUtils.get(ActivitySetting.this, SP_KEY_PORT, "9100").toString();

        et_wifi_ip.setText(spIp);
        et_wifi_ip.setSelection(spIp.length());
        et_wifi_port.setText(spPort);

        dialog.setView(view);
        dialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String ip = et_wifi_ip.getText().toString();
                String strPort = et_wifi_port.getText().toString();
                if (TextUtils.isEmpty(strPort)) {
                    strPort = "9100";
                }
                if (!TextUtils.isEmpty(ip)) {
                    SPUtils.put(ActivitySetting.this, SP_KEY_IP, ip);
                    WIFI_VALUE_IP = ip;
                }
                if (!TextUtils.isEmpty(strPort)) {
                    SPUtils.put(ActivitySetting.this, SP_KEY_PORT, strPort);
                    WIFI_VALUE_PORT = strPort;
                }
                configObj = new WiFiConfigBean(ip, Integer.parseInt(strPort));
                tv_device_selected.setText(configObj.toString());
                tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                isConfigPrintEnable(configObj);
            }
        });
        dialog.setNegativeButton(R.string.dialog_cancel, null);
        dialog.show();

    }

    private void showConnectedListDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_title_connected_devlist);
        String[] devList = new String[printerInterfaceArrayList.size()];
        for (int i = 0; i < devList.length; i++) {
            devList[i] = printerInterfaceArrayList.get(i).getConfigObject().toString();
        }
        if (devList.length > 0) {
            dialog.setItems(devList, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    tv_device_selected.setText(printerInterfaceArrayList.get(i).getConfigObject().toString());
                    rtPrinter.setPrinterInterface(printerInterfaceArrayList.get(i));//设置连接方式 Connection port settings
                    tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                    BaseApplication.getInstance().setRtPrinter(rtPrinter);//设置全局RTPrinter
                    setPrintEnable(printerInterfaceArrayList.get(i).getConnectState() == ConnectStateEnum.Connected);
                }
            });
        } else {
            dialog.setMessage(R.string.pls_connect_printer_first);
        }
        dialog.setNegativeButton(R.string.dialog_cancel, null);
        dialog.show();
    }

    private void doConnect() {

        if (Integer.parseInt(tv_device_selected.getTag().toString()) == BaseEnum.NO_DEVICE) {//未选择设备
            showAlertDialog(getString(R.string.main_pls_choose_device));
            return;
        }
        pb_connect.setVisibility(VISIBLE);

        switch (checkedConType) {
            case BaseEnum.CON_WIFI -> {
                if (configObj != null) {
                    WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;
                    connectWifi(wiFiConfigBean);
                } else {
                    WIFI_VALUE_IP = prefs.getString("PRINTER_IP", "127.0.0.1");
                    WIFI_VALUE_PORT = prefs.getString("PRINTER_PORT", "9100");
                    assert WIFI_VALUE_PORT != null;
                    configObj = new WiFiConfigBean(WIFI_VALUE_IP, Integer.parseInt(WIFI_VALUE_PORT));
                    WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;
                    connectWifi(wiFiConfigBean);
                    tv_device_selected.setText(configObj.toString());
                    tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                    isConfigPrintEnable(configObj);
                }
            }
            case BaseEnum.CON_BLUETOOTH -> {
                if (configObj != null) {
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    connectBluetooth(bluetoothEdrConfigBean);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(ActivitySetting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(ActivitySetting.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                            return;
                        }
                    }
                    BluetoothDevice device = getDevice();
                    BT_VALUE_MAC = device.getAddress();
                    BT_VALUE_NAME = device.getName();

                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    connectBluetooth(bluetoothEdrConfigBean);
                    tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                }
            }
            case BaseEnum.CON_USB -> {
                UsbConfigBean usbConfigBean = (UsbConfigBean) configObj;
                connectUSB(usbConfigBean);
            }
            default -> pb_connect.setVisibility(GONE);
        }

    }

    private BluetoothDevice getDevice() {
        BluetoothDevice device = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(ActivitySetting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivitySetting.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return null;
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
        boolean isfound = false;
        Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
        for (int i = 0; i < pairedDeviceList.size(); i++) {
            if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                device = pairedDeviceList.get(i);
            }
        }

        return device;
    }

    private void doDisConnect() {

        if (Integer.parseInt(tv_device_selected.getTag().toString()) == BaseEnum.NO_DEVICE) {
//            showAlertDialog(getString(R.string.main_discon_click_repeatedly));
            return;
        }
        rtPrinter = BaseApplication.getInstance().getRtPrinter();

        if (rtPrinter != null && rtPrinter.getPrinterInterface() != null) {
            rtPrinter.disConnect();
        }
        // tv_device_selected.setText(getString(R.string.connectez_svp));
        //tv_device_selected.setTag(BaseEnum.NO_DEVICE);
        setPrintEnable(false);
    }

    private void showConnectDialog() {
        switch (checkedConType) {
            case BaseEnum.CON_WIFI -> showWifiChooseDialog();
            case BaseEnum.CON_BLUETOOTH -> showBluetoothDeviceChooseDialog();
            case BaseEnum.CON_USB -> showUSBDeviceChooseDialog();
            case BaseEnum.CON_COM, BaseEnum.NONE -> {
            }
        }
    }


    private void textPrint() throws UnsupportedEncodingException, SdkException {
        switch (checkedImpType) {
            case BaseEnum.IMP_TYPE_TICKET -> {
                BaseApplication.getInstance().setCurrentCmdType(BaseEnum.CMD_ESC);
                escPrint();
            }
            case BaseEnum.IMP_TYPE_CODEBARRE -> {
                BaseApplication.getInstance().setCurrentCmdType(BaseEnum.CMD_TSC);
                tscPrintBarCode("123456789");
            }
        }
    }


    private void tscPrintBarCode(String barcodeContent) {

        try {
            int labelWidth = 80;
            int labelHeight = 40;

            CmdFactory tscFac = new TscFactory();
            Cmd tscCmd = tscFac.create();

            tscCmd.append(tscCmd.getHeaderCmd());
            CommonSetting commonSetting = new CommonSetting();
            commonSetting.setLableSizeBean(new LableSizeBean(labelWidth, labelHeight));
            commonSetting.setLabelGap(3);
            commonSetting.setPrintDirection(PrintDirection.NORMAL);
            tscCmd.append(tscCmd.getCommonSettingCmd(commonSetting));


            String strPrintTxtproduit = TonyUtils.printText("20", "20", "TSS24.BF2", "0", "1", "1", "TEST PRODUIT;");
            rtPrinter.writeMsg(strPrintTxtproduit.getBytes("GBK"));
            String strPrintTxtPrix = TonyUtils.printText("20", "60", "TSS24.BF2", "0", "1", "1", "300.00 DA");
            rtPrinter.writeMsg(strPrintTxtPrix.getBytes("GBK"));
            //  String strPrint = TonyUtils.setPRINT("1", "1");
            //   rtPrinter.writeMsg(strPrint.getBytes());

            // tscCmd.append(tscCmd.getLFCRCmd()); // one line space


            BarcodeSetting barcodeSetting = new BarcodeSetting();
            barcodeSetting.setNarrowInDot(2);//narrow bar setting, bar width
            barcodeSetting.setWideInDot(4);
            barcodeSetting.setHeightInDot(48);//bar height setting
            barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
            barcodeSetting.setPrintRotation(PrintRotation.Rotate0);
            int x = 20, y = 100;
            barcodeSetting.setPosition(new Position(x, y));


            byte[] barcodeCmd = tscCmd.getBarcodeCmd(CODE128, barcodeSetting, barcodeContent);
            tscCmd.append(barcodeCmd);

            tscCmd.append(tscCmd.getPrintCopies(1));
            tscCmd.append(tscCmd.getEndCmd());
            if (rtPrinter != null) {
                rtPrinter.writeMsgAsync(tscCmd.getAppendCmds());
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    private void escPrint() throws UnsupportedEncodingException {
        /*Intent html_intent = new Intent(ActivitySetting.this, ActivityHtmlView.class);
        startActivity(html_intent);*/

        if (BaseApplication.getInstance().getIsConnected()) {
            rtPrinter = BaseApplication.getInstance().getRtPrinter();
            if (rtPrinter != null) {
                CmdFactory escFac = new EscFactory();
                Cmd escCmd = escFac.create();
                escCmd.append(escCmd.getHeaderCmd());//初始化, Initial

                escCmd.setChartsetName("utf-8");

                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                escCmd.append(escCmd.getCommonSettingCmd(commonSetting));

                escCmd.append(escCmd.getTextCmd(textSetting, "(this is juste a print test)"));
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getTextCmd(textSetting, "العربية"));
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());
                escCmd.append(escCmd.getLFCRCmd());

                rtPrinter.writeMsgAsync(escCmd.getAppendCmds());

            }
        } else {

        }
 /*
        try {
            String fileName = Environment.getExternalStorageDirectory().getPath()+"/webview_capture1.jpg";
            bitmap = BitmapFactory.decodeFile(fileName);

        } catch (Exception e) {
            Log.e("eeeeee", e.getMessage());
        }


      new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Loading...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());

                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));

                BitmapSetting bitmapSetting = new BitmapSetting();

                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);
//                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_MULTI_COLOR);

                //bitmapSetting.setBimtapLimitWidth(bmpPrintWidth * 8);
                try {
                    cmd.append(cmd.getBitmapCmd(bitmapSetting, bitmap));
                } catch (SdkException e) {
                    e.printStackTrace();
                }
                cmd.append(cmd.getLFCRCmd());
                cmd.append(cmd.getLFCRCmd());
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }

                hideProgressDialog();
            }
        }).start();*/

    }


    private void connectBluetooth(BluetoothEdrConfigBean bluetoothEdrConfigBean) {
        PIFactory piFactory = new BluetoothFactory();
        PrinterInterface printerInterface = piFactory.create();
        printerInterface.setConfigObject(bluetoothEdrConfigBean);
        rtPrinter.setPrinterInterface(printerInterface);
        try {
            rtPrinter.connect(bluetoothEdrConfigBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectWifi(WiFiConfigBean wiFiConfigBean) {
        PIFactory piFactory = new WiFiFactory();
        PrinterInterface printerInterface = piFactory.create();
        printerInterface.setConfigObject(wiFiConfigBean);
        rtPrinter.setPrinterInterface(printerInterface);
        try {
            rtPrinter.connect(wiFiConfigBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectUSB(UsbConfigBean usbConfigBean) {
        UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        PIFactory piFactory = new UsbFactory();
        PrinterInterface printerInterface = piFactory.create();
        printerInterface.setConfigObject(usbConfigBean);
        rtPrinter.setPrinterInterface(printerInterface);

        if (mUsbManager.hasPermission(usbConfigBean.usbDevice)) {
            try {
                rtPrinter.connect(usbConfigBean);
                BaseApplication.instance.setRtPrinter(rtPrinter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mUsbManager.requestPermission(usbConfigBean.usbDevice, usbConfigBean.pendingIntent);
        }

    }

    private void showBluetoothDeviceChooseDialog() {
        BluetoothDeviceChooseDialog bluetoothDeviceChooseDialog = new BluetoothDeviceChooseDialog();
        bluetoothDeviceChooseDialog.setOnDeviceItemClickListener(new BluetoothDeviceChooseDialog.onDeviceItemClickListener() {
            @Override
            public void onDeviceItemClick(BluetoothDevice device) {
                //doDisConnect();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(ActivitySetting.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ActivitySetting.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                        return;
                    }
                }
                if (TextUtils.isEmpty(device.getName())) {
                    tv_device_selected.setText(device.getAddress());
                } else {
                    tv_device_selected.setText(device.getName());
                }
                BT_VALUE_MAC = device.getAddress();
                BT_VALUE_NAME = device.getName();
                configObj = new BluetoothEdrConfigBean(device);
                tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                isConfigPrintEnable(configObj);

            }
        });

        bluetoothDeviceChooseDialog.show(ActivitySetting.this.getFragmentManager(), null);
    }

    /**
     * usb设备选择
     */
    private void showUSBDeviceChooseDialog() {
        final UsbDeviceChooseDialog usbDeviceChooseDialog = new UsbDeviceChooseDialog();
        usbDeviceChooseDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UsbDevice mUsbDevice = (UsbDevice) parent.getAdapter().getItem(position);
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ActivitySetting.this, 0, new Intent(ActivitySetting.this.getApplicationInfo().packageName), PendingIntent.FLAG_IMMUTABLE);
                tv_device_selected.setText(getString(R.string.adapter_usbdevice) + mUsbDevice.getDeviceId()); //+ (position + 1));
                configObj = new UsbConfigBean(BaseApplication.getInstance(), mUsbDevice, mPermissionIntent);
                tv_device_selected.setTag(BaseEnum.HAS_DEVICE);
                isConfigPrintEnable(configObj);
                usbDeviceChooseDialog.dismiss();
            }
        });
        usbDeviceChooseDialog.show(getFragmentManager(), null);
    }


    private void setPrintEnable(boolean isEnable) {
        btn_connect_printer.setEnabled(!isEnable);
        btn_disConnect_printer.setEnabled(isEnable);
        btn_test_impression.setEnabled(isEnable);

    }

    private void isConfigPrintEnable(Object configObj) {
        setPrintEnable(isInConnectList(configObj));
    }

    private boolean isInConnectList(Object configObj) {
        boolean isInList = false;
        for (int i = 0; i < printerInterfaceArrayList.size(); i++) {
            PrinterInterface printerInterface = printerInterfaceArrayList.get(i);
            if (configObj.toString().equals(printerInterface.getConfigObject().toString())) {
                if (printerInterface.getConnectState() == ConnectStateEnum.Connected) {
                    isInList = true;
                    break;
                }
            }
        }
        return isInList;
    }


    @SuppressLint("NonConstantResourceId")
    public void onClickListener(View v) throws IOException {

        switch (v.getId()) {
            case R.id.code_depot_lnr -> {
                final ScalingActivityAnimator mScalingActivityAnimator = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView = mScalingActivityAnimator.start();
                final EditText edited_code_depot = popView.findViewById(R.id.edited_prix);
                edited_code_depot.setInputType(InputType.TYPE_CLASS_NUMBER);
                Button mButtonSure = popView.findViewById(R.id.btn_sure);
                Button mButtonBack = popView.findViewById(R.id.btn_cancel);
                edited_code_depot.setText(code_depot.getText().toString());
                mButtonBack.setOnClickListener(v1 -> mScalingActivityAnimator.resume());
                mButtonSure.setOnClickListener(v12 -> {
                    code_depot.setText(edited_code_depot.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("CODE_DEPOT", edited_code_depot.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator.resume();
                });
            }
            case R.id.nom_depot_lnr -> {
                final ScalingActivityAnimator mScalingActivityAnimator2 = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView2 = mScalingActivityAnimator2.start();
                final EditText edited_nom_depot = popView2.findViewById(R.id.edited_prix);
                edited_nom_depot.setInputType(InputType.TYPE_CLASS_TEXT);
                Button mButtonSure2 = popView2.findViewById(R.id.btn_sure);
                Button mButtonBack2 = popView2.findViewById(R.id.btn_cancel);
                edited_nom_depot.setText(nom_depot.getText().toString());
                mButtonBack2.setOnClickListener(v1 -> mScalingActivityAnimator2.resume());
                mButtonSure2.setOnClickListener(v12 -> {
                    nom_depot.setText(edited_nom_depot.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("NOM_DEPOT", edited_nom_depot.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator2.resume();
                });
            }
            case R.id.code_vendeur_lnr -> {
                final ScalingActivityAnimator mScalingActivityAnimator1 = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView1 = mScalingActivityAnimator1.start();
                final EditText edited_code_vendeur = popView1.findViewById(R.id.edited_prix);
                edited_code_vendeur.setInputType(InputType.TYPE_CLASS_NUMBER);
                Button mButtonSure1 = popView1.findViewById(R.id.btn_sure);
                Button mButtonBack1 = popView1.findViewById(R.id.btn_cancel);
                edited_code_vendeur.setText(code_vendeur.getText().toString());
                mButtonBack1.setOnClickListener(v13 -> mScalingActivityAnimator1.resume());
                mButtonSure1.setOnClickListener(v14 -> {
                    code_vendeur.setText(edited_code_vendeur.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("CODE_VENDEUR", edited_code_vendeur.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator1.resume();
                });
            }
            case R.id.nom_vendeur_lnr -> {
                final ScalingActivityAnimator mScalingActivityAnimator3 = new ScalingActivityAnimator(this, this, R.id.root_view, R.layout.pop_view);
                View popView3 = mScalingActivityAnimator3.start();
                final EditText edited_nom_vendeur = popView3.findViewById(R.id.edited_prix);
                edited_nom_vendeur.setInputType(InputType.TYPE_CLASS_TEXT);
                Button mButtonSure3 = popView3.findViewById(R.id.btn_sure);
                Button mButtonBack3 = popView3.findViewById(R.id.btn_cancel);
                edited_nom_vendeur.setText(nom_vendeur.getText().toString());
                mButtonBack3.setOnClickListener(v13 -> mScalingActivityAnimator3.resume());
                mButtonSure3.setOnClickListener(v14 -> {
                    nom_vendeur.setText(edited_nom_vendeur.getText().toString());
                    SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                    editor.putString("NOM_VENDEUR", edited_nom_vendeur.getText().toString());
                    editor.apply();
                    mScalingActivityAnimator3.resume();
                });
            }
            case R.id.reset_pda -> ResetDialog();
            case R.id.backup -> backup_db();
            case R.id.restore -> restore_db();
            case R.id.password_change -> {
                // Start change pwd  activity
                Intent intent = new Intent(getApplicationContext(), ActivityChangePwd.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                finish();
            }
        }
    }


    private void ResetDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PasswordResetDialogFragment editNameDialogFragment = PasswordResetDialogFragment.newInstance("Reinitialiser");
        editNameDialogFragment.show(fm, "fragment_edit_name");
    }

    private void backup_db() {
        String currentDBPath = "//data//data//" + mContext.getPackageName() + "//databases//safe_distribute_pro";

        String current_version = "0";
        String android_unique_id = "0";
        String seriel_number = "0";

        try {

            PackageManager pm = getApplicationContext().getPackageManager();
            String packageName =getApplicationContext().getPackageName();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            current_version = String.valueOf(packageInfo.versionName);
            android_unique_id = getAndroidID(getApplicationContext());

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        new UploadFileTask().execute(currentDBPath, seriel_number, android_unique_id, current_version, txtv_email_cloud.getText().toString());

    }

    private void restore_db() {
        //localBackup.performRestore(controller);
        /*new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Info...")
                .setContentText("Cette operation necessite l'intervention de l'administrateur, Veuillez contacter le fournisseur !")
                .show();*/

        new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Restoration de la base de données")
                .setContentText("Vous etes responsable de cette opération si il y a des perdes des données, Voulez-vous vraiment restorer vos données ?")
                .setCancelText("Non")
                .setConfirmText("Oui")
                .showCancelButton(true)
                .setCancelClickListener(Dialog::dismiss)
                .setConfirmClickListener(sDialog -> {

                    new GetFileTask().execute(txtv_email_cloud.getText().toString());
                    sDialog.dismiss();
                }).show();

    }


    private class GetFileTask extends AsyncTask<String, Integer, JSONArray> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ActivitySetting.this);
            mProgressDialog.setMessage("Récupération list des bases de données ...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            return FetchFilesTask.getListFile(params[0]);
        }

        @Override
        protected void onPostExecute(JSONArray result) {

            mProgressDialog.dismiss();
           try {
               if(result.length() > 0){
                   ArrayList<String> list_databases = new ArrayList<>();
                   for (int i = 0; i < result.length(); i++) {
                       list_databases.add(result.getString(i));
                       String fileName = result.getString(i);
                       Log.d("File", "File: " + fileName);
                   }
                   showListBackup(list_databases, "Sauvegarde list");
               }else{
                   new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.NORMAL_TYPE)
                           .setTitleText("Info...")
                           .setContentText("Aucune sauvegarde base de données disponible sur le serveur !")
                           .show();
               }

           } catch (Exception e) {
             Log.e("FetchFilesTask", "Error parsing JSON", e);
               new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                       .setTitleText("Erreur...")
                       .setContentText("Probleme au moment du récuperation la liste des bases de donnees")
                       .show();
           }

        }
    }


    private class GetDownloadTask extends AsyncTask<String, Integer, Boolean> {

        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ActivitySetting.this);
            mProgressDialog.setMessage("Téléchargement backup (bases de données)");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            return DownloadBackupTask.downloadBackupFile(getApplicationContext(), params[0], params[1], percentage -> publishProgress(percentage));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            mProgressDialog.setTitle(progress + "%");
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            mProgressDialog.dismiss();

            if(result){
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Succès...")
                        .setContentText("Téléchargement terminé")
                        .show();
            }else{
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme au moment du téléchargement la base de données")
                        .show();
            }
        }
    }


    public void sendRequest(String username_safe_event, String password_safe_event, String typeConnection) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    NetClient nc = new NetClient("105.96.9.62", 1209); // ip adress and port

                    String message_online = username_safe_event + ":" + password_safe_event + ":" + ":" + "cccc";
                    nc.sendDataWithString(message_online);
                    String token = nc.receiveDataFromServer();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (token.length() <= 15) {
                                ip.setText(token);
                                new TestConnection_Setting(ip.getText().toString(), pathdatabase.getText().toString(), username, password, typeConnection).execute();
                            } else {

                                terminateProcessing();

                                new Toaster.Builder(ActivitySetting.this)
                                        .setTitle("Attention")
                                        .setDescription(token)
                                        .setDuration(Toaster.LENGTH_LONG)
                                        .setStatus(Toaster.Status.WARNING)
                                        .show();
                            }

                        }
                    });

                    Log.e("TRACKKK", "==========> " + token);

                } catch (Exception e) {

                    runOnUiThread(() -> {
                        terminateProcessing();
                        Log.e("TRACKKK", e.getMessage());

                        new Toaster.Builder(ActivitySetting.this)
                                .setTitle("Erreur")
                                .setDescription("Problème au niveau de serveur")
                                .setDuration(Toaster.LENGTH_LONG)
                                .setStatus(Toaster.Status.ERROR)
                                .show();
                    });
                }
            }
        }).start();
    }

    private void terminateProcessing() {
        isRunning = false;
        circle.stop();
        textView.setVisibility(GONE);
    }

    //====================================
    @SuppressLint("StaticFieldLeak")
    public class TestConnection_Setting extends AsyncTask<Void, Void, Boolean> {

        Boolean executed = false;
        String _Server;
        String _pathDatabase;
        String _Username;
        String _Password;
        String _TypeConnection;
        Connection con;
        String errorMessage;

        public TestConnection_Setting(String server, String database, String username, String password, String typeConnection) {
            super();
            // do stuff
            _Server = server;
            _pathDatabase = database;
            _pathDatabase = _pathDatabase.toUpperCase().replace(".FDB", "");
            _Username = username;
            _Password = password;
            _TypeConnection = typeConnection;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setVisibility(VISIBLE);
            circle.start();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + _Server + ":" + _pathDatabase + ".FDB?encoding=WIN1256";
                Log.d("TAG", "doInBackground: " + sCon);
                con = DriverManager.getConnection(sCon, _Username, _Password);
                executed = true;

            } catch (Exception ex) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                Log.e("TRACKKK", "FAILED TO CONNECT WITH SERVER " + ex.getMessage());
                errorMessage = ex.getMessage();
            }finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return executed;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            circle.stop();
            isRunning = false;
            textView.setVisibility(GONE);
            if(_TypeConnection.equals("GET_ALL_DEPOT")){

                if (aBoolean) {
                    //connected to server call get all depot
                    getAllDepotFromserver getAllDepotFromserver = new getAllDepotFromserver(_Server, _pathDatabase, _Username, _Password);
                    getAllDepotFromserver.execute();
                } else {
                    Animation shake = AnimationUtils.loadAnimation(ActivitySetting.this, R.anim.shakanimation);
                    btn_get_all_depot.startAnimation(shake);
                    Sound(R.raw.error);
                    new Toaster.Builder(ActivitySetting.this)
                            .setTitle("Erreur de connexion")
                            .setDescription(errorMessage)
                            .setDuration(Toaster.LENGTH_LONG)
                            .setStatus(Toaster.Status.ERROR)
                            .show();
                }

            }else if( _TypeConnection.equals("GET_ALL_VENDEUR")){
                if (aBoolean) {
                    //connected to server call get all vendeur
                    getAllVendeurFromserver getAllVendeurFromserver = new getAllVendeurFromserver(_Server, _pathDatabase, _Username, _Password);
                    getAllVendeurFromserver.execute();
                } else {
                    Animation shake = AnimationUtils.loadAnimation(ActivitySetting.this, R.anim.shakanimation);
                    btn_get_all_vendeur.startAnimation(shake);
                    Sound(R.raw.error);
                    new Toaster.Builder(ActivitySetting.this)
                            .setTitle("Erreur de connexion")
                            .setDescription(errorMessage)
                            .setDuration(Toaster.LENGTH_LONG)
                            .setStatus(Toaster.Status.ERROR)
                            .show();
                }
            }else{
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
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
                    btntest_connection.startAnimation(shake);
                    Sound(R.raw.error);
                    new Toaster.Builder(ActivitySetting.this)
                            .setTitle("Erreur de connexion")
                            .setDescription(errorMessage)
                            .setDuration(Toaster.LENGTH_LONG)
                            .setStatus(Toaster.Status.ERROR)
                            .show();
                }
            }

            super.onPostExecute(aBoolean);
        }
    }
    //==================================================


    public class getAllDepotFromserver extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        ArrayList<PostData_Depot> depots;
        String messageError = "";


        String _Server;
        String _pathDatabase;
        String _Username;
        String _Password;

        public getAllDepotFromserver(String server, String database, String username, String password) {
            super();
            // do stuff
            _Server = server;
            _pathDatabase = database;
            _pathDatabase = _pathDatabase.toUpperCase().replace(".FDB", "");
            _Username = username;
            _Password = password;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivitySetting.this);
            mProgressDialog_Free.setMessage("Collection des informations ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {

            try {

                depots = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + _Server + ":" + _pathDatabase + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, _Username, _Password);

                Statement stmt = con.createStatement();

                //============================ GET all depot ===========================================

                String sql1 = "SELECT * FROM DEPOT1";

                ResultSet rs1 = stmt.executeQuery(sql1);

                while (rs1.next()) {
                    PostData_Depot depot = new PostData_Depot();
                    depot.RECORDID = rs1.getInt("RECORDID");
                    depot.nom_depot = rs1.getString("NOM_DEPOT");
                    depot.code_depot = rs1.getString("CODE_DEPOT");
                    depots.add(depot);
                }

                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD" + e.getMessage());
                    flag = 3;
                }
                messageError = e.getMessage();
            }finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                if (!((Activity) mContext).isFinishing()) {
                    //propose list
                    showListDepot(depots, "List Depots");

                }
            } else if (integer == 2) {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }


    public class getAllVendeurFromserver extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        ArrayList<PostData_Vendeur> vendeurs;
        String messageError = "";


        String _Server;
        String _pathDatabase;
        String _Username;
        String _Password;

        public getAllVendeurFromserver(String server, String database, String username, String password) {
            super();
            // do stuff
            _Server = server;
            _pathDatabase = database;
            _pathDatabase = _pathDatabase.toUpperCase().replace(".FDB", "");
            _Username = username;
            _Password = password;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivitySetting.this);
            mProgressDialog_Free.setMessage("Collection des informations ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {

            try {

                vendeurs = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + _Server + ":" + _pathDatabase + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, _Username, _Password);

                Statement stmt = con.createStatement();

                //============================ GET all depot ===========================================

                String sql1 = "SELECT * FROM VENDEUR";

                ResultSet rs1 = stmt.executeQuery(sql1);

                while (rs1.next()) {
                    PostData_Vendeur vendeur = new PostData_Vendeur();
                    vendeur.RECORDID = rs1.getInt("RECORDID");
                    vendeur.nom_vendeur = rs1.getString("NOM_VENDEUR");
                    vendeur.code_vendeur = rs1.getString("CODE_VENDEUR");
                    vendeurs.add(vendeur);
                }

                flag = 1;

            } catch (Exception e) {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD" + e.getMessage());
                    flag = 3;
                }
                messageError = e.getMessage();
            }finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                if (!((Activity) mContext).isFinishing()) {
                    //propose list
                    showListVendeur(vendeurs, "List Vendeurs");

                }
            } else if (integer == 2) {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }

    protected void showListDepot(ArrayList<PostData_Depot> depots, String title) {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentSelectedDepot(); // creating new object
        Bundle args = new Bundle();
        args.putParcelableArrayList("LIST_SELECTED_DEPOTS",depots);
        args.putString("TITLE", title);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }

    protected void showListVendeur(ArrayList<PostData_Vendeur> vendeurs, String title) {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentSelectedVendeur(); // creating new object
        Bundle args = new Bundle();
        args.putParcelableArrayList("LIST_SELECTED_VENDEURS", vendeurs);
        args.putString("TITLE", title);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }

    protected void showListBackup(ArrayList<String> backups, String title) {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentListDatabases(); // creating new object
        Bundle args = new Bundle();
        args.putStringArrayList("LIST_DATABASES", backups);
        args.putString("TITLE", title);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }

    @Subscribe
    public void onDepotSelected(SelectedDepotEvent event){
        code_depot.setText(event.getCode_depot());
        nom_depot.setText(event.getNom_depot());

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("CODE_DEPOT", event.getCode_depot());
        editor.putString("NOM_DEPOT", event.getNom_depot());
        editor.apply();
    }

    @Subscribe
    public void onVendeurSelected(SelectedVendeurEvent event){

        code_vendeur.setText(event.getCode_vendeur());
        nom_vendeur.setText(event.getNom_vendeur());

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putString("CODE_VENDEUR", event.getCode_vendeur());
        editor.putString("NOM_VENDEUR", event.getNom_vendeur());
        editor.apply();
    }

    @Subscribe
    public void onBackupSelected(SelectedBackupEvent event){
        String fileName = event.getNom_database();
        new GetDownloadTask().execute(fileName, txtv_email_cloud.getText().toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddedEmailEvent(AddedEmailEvent event) {

        if(event.getIsAdded()){

            SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
            editor.putString("EMAIL_COMPTE_CLOUD", event.getEmailCloud());
            editor.putString("PASSWORD_COMPTE_CLOUD", event.getPaswordCloud());
            editor.apply();

            txtv_email_cloud.setText(event.getEmailCloud());
            txtv_password_cloud.setText(event.getPaswordCloud());

            new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Compte cloud")
                    .setContentText("Compte cloud bien crée, Voulez-vous vraiment faire un backup cloud maintenant ?")
                    .setCancelText("Non")
                    .setConfirmText("Oui")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        String currentDBPath = "//data//data//" + mContext.getPackageName() + "//databases//safe_distribute_pro";

                        String current_version = "0";
                        String android_unique_id = "0";
                        String seriel_number = "0";

                        try {

                            PackageManager pm = getApplicationContext().getPackageManager();
                            String packageName =getApplicationContext().getPackageName();
                            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

                            current_version = String.valueOf(packageInfo.versionName);
                            android_unique_id = getAndroidID(getApplicationContext());

                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                        new UploadFileTask().execute(currentDBPath, seriel_number, android_unique_id, current_version, event.getEmailCloud());

                        sDialog.dismiss();
                    })
                    .setCancelClickListener(Dialog::dismiss)
                    .show();

            lnr_back_up.setEnabled(true);

        }else{
            new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Erreur")
                    .setContentText("Erreur lors de l'ajout du compte cloud : " + event.getMessage())
                    .show();
        }

    }


    private class UploadFileTask extends AsyncTask<String, Integer, String> {

        ProgressDialog mProgressDialog;
        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(ActivitySetting.this);
            mProgressDialog.setMessage("Sauvegarde cloud ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String filePath = params[0];
            String seriel_number = params[1];
            String android_id = params[2];
            String version = params[3];
            String email = params[4];

            return FileUploader.uploadFile(filePath, seriel_number, android_id, version, email, percentage -> publishProgress(percentage));
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            mProgressDialog.setTitle(progress + "%");
            mProgressDialog.setProgress(progress);
        }

        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(result);
                if(jsonObject.getString("status").equals("success")){
                    new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Information...")
                            .setContentText(jsonObject.getString("message"))
                            .show();
                }else{
                    new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Erreur...")
                            .setContentText(jsonObject.getString("message"))
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                new SweetAlertDialog(ActivitySetting.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error...")
                        .setContentText(e.getMessage())
                        .show();
            }
        }
    }

    void imageChooserGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 4000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

      if (requestCode == 4000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                        company_logo.setImageBitmap(bitmap);
                        //company_logo.setRotation(90);
                        InputStream iStream = getContentResolver().openInputStream(selectedImage);
                        byte[] image = ImageUtils.getBytes(iStream);

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                        String img_str = Base64.encodeToString(image, 0);
                        editor.putString("COMPANY_LOGO", img_str);
                        editor.apply();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.back);
        }
        super.onBackPressed();
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    protected void onDestroy() {
        PrinterObserverManager.getInstance().remove(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

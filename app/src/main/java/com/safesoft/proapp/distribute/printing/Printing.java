package com.safesoft.proapp.distribute.printing;


import static android.content.Context.MODE_PRIVATE;

import static com.rt.printerlibrary.enumerate.BarcodeType.CODE128;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.rt.printerlibrary.bean.BluetoothEdrConfigBean;
import com.rt.printerlibrary.bean.Position;
import com.rt.printerlibrary.bean.WiFiConfigBean;
import com.rt.printerlibrary.cmd.Cmd;
import com.rt.printerlibrary.cmd.EscFactory;
import com.rt.printerlibrary.cmd.TscFactory;
import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.BarcodeStringPosition;
import com.rt.printerlibrary.enumerate.BarcodeType;
import com.rt.printerlibrary.enumerate.BmpPrintMode;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.enumerate.PrintRotation;
import com.rt.printerlibrary.enumerate.SettingEnum;
import com.rt.printerlibrary.exception.SdkException;
import com.rt.printerlibrary.factory.cmd.CmdFactory;
import com.rt.printerlibrary.factory.connect.BluetoothFactory;
import com.rt.printerlibrary.factory.connect.PIFactory;
import com.rt.printerlibrary.factory.connect.WiFiFactory;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.printer.RTPrinter;
import com.rt.printerlibrary.setting.BarcodeSetting;
import com.rt.printerlibrary.setting.BitmapSetting;
import com.rt.printerlibrary.setting.CommonSetting;
import com.rt.printerlibrary.setting.TextSetting;
import com.safesoft.proapp.distribute.adapters.model.MyDataObject;
import com.safesoft.proapp.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_f;
import com.safesoft.proapp.distribute.postData.PostData_Etatv;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.utils.BaseEnum;
import com.safesoft.proapp.distribute.utils.TonyUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Printing {
    Bitmap bitmap;
    private ProgressDialog progressDialog;
    private final String PREFS = "ALL_PREFS";
    private Activity mActivity;
    SharedPreferences prefs;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> pairedDeviceList;
    private RTPrinter rtPrinter;
    private PrinterFactory printerFactory;
    private TextSetting textSetting;
    private final String mChartsetName = "UTF-8";
    private Object configObj;
    private ArrayList<PostData_Bon2> final_panier_vente;
    private ArrayList<PostData_Achat2> final_panier_achat;
    private PostData_Produit produit;
    private PostData_Bon1 bon1;
    private PostData_Achat1 achat1;
    private PostData_Carnet_c carnet_c_print;
    private PostData_Carnet_f carnet_f_print;
    private ArrayList<PostData_Etatv> etats;
    private String code_client_etat, client_etat, date_debut_etat, date_fin_etat;
    private String type_print;

    /////////////////////////////////////// IMPRIMER BON ///////////////////////////////////////////
    public void start_print_bon_vente(Activity activity, String type_print, ArrayList<PostData_Bon2> final_panier, PostData_Bon1 bon1) throws UnsupportedEncodingException {

        mActivity = activity;
        this.final_panier_vente = final_panier;
        this.bon1 = bon1;
        this.type_print = type_print;

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }

        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }
            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {

            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }


    public void start_print_bon_achat(Activity activity, String type_print, ArrayList<PostData_Achat2> final_panier, PostData_Achat1 achat1) throws UnsupportedEncodingException {

        mActivity = activity;
        this.final_panier_achat = final_panier;
        this.achat1 = achat1;
        this.type_print = type_print;

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }

        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }
            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {

            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }

    /////////////////////////////////// IMPRIMER ETIQUETTE /////////////////////////////////////////
    public void start_print_etiquette(Activity activity, PostData_Produit produit) {

        mActivity = activity;
        this.produit = produit;
        this.type_print = "ETIQUETTE";

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {

            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }


    public void start_print_etat(Activity activity, String Typeprint, ArrayList<PostData_Etatv> etats, String code_client_etat, String client_etat, String date_debut_etat, String date_fin_etat) {

        mActivity = activity;
        this.etats = etats;
        this.code_client_etat = code_client_etat;
        this.client_etat = client_etat;
        this.date_debut_etat = date_debut_etat;
        this.date_fin_etat = date_fin_etat;
        this.type_print = Typeprint;

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {

            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }

    /////////////////////////////////// IMPRIMER ETIQUETTE /////////////////////////////////////////
    public void start_print_etiquette_code_barre(Activity activity, PostData_Produit produit) {

        mActivity = activity;
        this.produit = produit;
        this.type_print = "ETIQUETTE_CODEBARRE";

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }

        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();


        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {


            // WIFI_VALUE_IP = prefs.getString("PRINTER_IP", "127.0.0.1");
            //WIFI_VALUE_PORT = prefs.getString("PRINTER_PORT", "9100");
            // assert WIFI_VALUE_PORT != null;
            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }


    /////////////////////////////////// IMPRIMER VERSEMENT CLIENT/////////////////////////////////////////
    public void start_print_versement_client(Activity activity, PostData_Carnet_c carnet_c_print) {

        mActivity = activity;
        this.carnet_c_print = carnet_c_print;

        this.type_print = "VERSEMENT_CLIENT";

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {


            // WIFI_VALUE_IP = prefs.getString("PRINTER_IP", "127.0.0.1");
            //WIFI_VALUE_PORT = prefs.getString("PRINTER_PORT", "9100");
            // assert WIFI_VALUE_PORT != null;
            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }


    /////////////////////////////////// IMPRIMER VERSEMENT FOURNISSEUR /////////////////////////////////////////
    public void start_print_versement_fournisseur(Activity activity, PostData_Carnet_f carnet_f_print) {

        mActivity = activity;
        this.carnet_f_print = carnet_f_print;

        this.type_print = "VERSEMENT_FOURNISSEUR";

        AsyncTask<Void, Void, Boolean> runningTask;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                return;
            }
        }

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();
        textSetting = new TextSetting();

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = null;
            pairedDeviceList = new ArrayList<>(mBluetoothAdapter.getBondedDevices());
            boolean isfound = false;
            Log.v("PRINTER_CONX", Objects.requireNonNull(prefs.getString("PRINTER_MAC", "00:00:00:00")));
            for (int i = 0; i < pairedDeviceList.size(); i++) {
                if (pairedDeviceList.get(i).getAddress().equals(prefs.getString("PRINTER_MAC", "00:00:00:00"))) {
                    isfound = true;
                    device = pairedDeviceList.get(i);

                }
            }

            if (isfound) {
                Log.v("PRINTER_CONX", "Device found");
                if (device != null) {
                    configObj = new BluetoothEdrConfigBean(device);
                    BluetoothEdrConfigBean bluetoothEdrConfigBean = (BluetoothEdrConfigBean) configObj;
                    runningTask = new LongOperation(bluetoothEdrConfigBean);
                    runningTask.execute();
                }
            } else {
                Log.v("PRINTER_CONX", "Device not found");
                Crouton.makeText(mActivity, "Aucune imprimante est connecté", Style.ALERT).show();
            }

        } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {


            // WIFI_VALUE_IP = prefs.getString("PRINTER_IP", "127.0.0.1");
            //WIFI_VALUE_PORT = prefs.getString("PRINTER_PORT", "9100");
            // assert WIFI_VALUE_PORT != null;
            configObj = new WiFiConfigBean(prefs.getString("PRINTER_IP", "127.0.0.1"), Integer.parseInt(prefs.getString("PRINTER_PORT", "9100")));
            WiFiConfigBean wiFiConfigBean = (WiFiConfigBean) configObj;

            runningTask = new LongOperation(wiFiConfigBean);
            runningTask.execute();

        }
    }

    private final class LongOperation extends AsyncTask<Void, Void, Boolean> {
        BluetoothEdrConfigBean bluetoothEdrConfigBean;
        WiFiConfigBean wiFiConfigBean;
        ProgressDialog mProgressDialog;

        public LongOperation(BluetoothEdrConfigBean bluetoothEdrConfigBean) {
            this.bluetoothEdrConfigBean = bluetoothEdrConfigBean;
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Preparation...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }


        public LongOperation(WiFiConfigBean wiFiConfigBean) {
            this.wiFiConfigBean = wiFiConfigBean;
            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Preparation...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            try {

                if (!BaseApplication.getInstance().getIsConnected()) {

                    if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "BLUETOOTH")) {
                        PIFactory piFactory = new BluetoothFactory();
                        PrinterInterface printerInterface = piFactory.create();
                        printerInterface.setConfigObject(bluetoothEdrConfigBean);
                        rtPrinter.setPrinterInterface(printerInterface);
                        rtPrinter.connect(bluetoothEdrConfigBean);

                    } else if (Objects.equals(prefs.getString("PRINTER_CONX", "BLUETOOTH"), "WIFI")) {

                        PIFactory piFactory = new WiFiFactory();
                        PrinterInterface printerInterface = piFactory.create();
                        printerInterface.setConfigObject(wiFiConfigBean);
                        rtPrinter.setPrinterInterface(printerInterface);
                        rtPrinter.connect(wiFiConfigBean);
                    }

                    Thread.sleep(1500);

                } else {
                    rtPrinter = BaseApplication.getInstance().getRtPrinter();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {

            mProgressDialog.dismiss();

            if (result) {
                try {
                    switch (type_print) {
                        case "VENTE", "ORDER" -> {
                            if (prefs.getString("MODEL_TICKET_LATIN", "MODEL 1").equals("MODEL 1")) {
                                print_bon_with_colisage();
                            } else {
                                print_bon_without_colisage();
                            }
                        }
                        case "ACHAT" -> print_achat();
                        case "ETIQUETTE" -> print_etiquette();
                        case "ETAT_VENTE", "ETAT_COMMANDE" -> print_etat_vente();
                        case "ETIQUETTE_CODEBARRE" -> print_etiquette_code_barre(produit.code_barre, produit.produit, produit.pv1_ht * (1 + (produit.tva / 100)));
                        case "VERSEMENT_CLIENT" -> print_versement_client();
                        case "VERSEMENT_FOURNISSEUR" -> print_versement_fournisseur();
                        case "ARABIC" -> print_arabic();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    void print_achat() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    ////////////////////////////////////// INFO CLIENT ///////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_RIGHT);
                    //textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "Date :" + achat1.date_bon + " " + achat1.heure));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "FOURNISSEUR  :" + achat1.fournis));
                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "ADRESSE :" + achat1.adresse));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "TEL     :" + achat1.tel));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    if (type_print.equals("ACHAT")) {
                        cmd.append(cmd.getTextCmd(textSetting, "ACHAT N :" + achat1.num_bon));
                    } else if (type_print.equals("ACHAT_ORDER")) {
                        cmd.append(cmd.getTextCmd(textSetting, "COMMANDE.A N :" + achat1.num_bon));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    //cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setBold(SettingEnum.Enable);
                    String format0 = "%1$-19s %2$-9s %3$-5s %4$12s";
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format0, "PRODUIT", StringUtils.center("QTE", 9), StringUtils.center("U.G", 5), "P.U")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    double nbr_colis, colissage, qte, gte_gratuit, prix_unit;
                    String nbr_colis_Str, colissage_Str, qte_Str, gte_gratuit_Str, prix_unit_Str, X1_Str, X2_Str, eq1_Str, plus_Str;
                    //nbr_colis = 10.0; colissage =23.0 ; qte = 120.00 ; gte_gratuit = 1.0;  prix_unit = 12345.33 ;


                    for (int i = 0; i < final_panier_achat.size(); i++) {


                        cmd.append(cmd.getTextCmd(textSetting, final_panier_achat.get(i).produit));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        nbr_colis = final_panier_achat.get(i).nbr_colis;
                        nbr_colis_Str = new DecimalFormat("####0.##").format(nbr_colis);

                        colissage = final_panier_achat.get(i).colissage;
                        colissage_Str = new DecimalFormat("####0.##").format(colissage);

                        qte = final_panier_achat.get(i).qte;
                        qte_Str = new DecimalFormat("####0.##").format(qte);

                        gte_gratuit = final_panier_achat.get(i).gratuit;
                        gte_gratuit_Str = new DecimalFormat("####0.##").format(gte_gratuit);

                        prix_unit = final_panier_achat.get(i).pa_ht;
                        prix_unit_Str = new DecimalFormat("####0.00").format(prix_unit);


                        X1_Str = "X";
                        eq1_Str = "=";
                        plus_Str = "+";

                        if (gte_gratuit == 0.0) {
                            gte_gratuit_Str = "";
                            plus_Str = " ";
                        }

                        if (colissage == 0.0) {
                            nbr_colis_Str = "";
                            colissage_Str = "";
                            X1_Str = " ";
                            eq1_Str = " ";
                        }
                        String format1 = "  %1$-6s " + X1_Str + " %2$-6s " + eq1_Str + " %3$-9s" + plus_Str + "%4$-5sX%5$12s";
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format1, StringUtils.center(nbr_colis_Str, 6), StringUtils.center(colissage_Str, 6), StringUtils.center(qte_Str, 9), StringUtils.center(gte_gratuit_Str, 5), prix_unit_Str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space

                       /* if(i<final_panier.size()-1){
                            textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                            cmd.append(cmd.getTextCmd(textSetting, "------------------------"));
                            textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }*/


                    }
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    int nbr_produit;
                    Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
                    String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

                    nbr_produit = final_panier_achat.size();
                    nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

                    total_ht_bon = achat1.tot_ht;
                    total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

                    tva_bon = achat1.tot_tva;
                    tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

                    timbre_bon = achat1.timbre;
                    timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

                    total_bon = achat1.tot_ttc;
                    total_bon_str = new DecimalFormat("####0.00").format(total_bon);

                    remise_bon = achat1.remise;
                    remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

                    ancien_solde = achat1.solde_ancien;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

                    total_a_payer = achat1.montant_bon;
                    total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

                    versement = achat1.verser;
                    versement_str = new DecimalFormat("####0.00").format(versement);

                    nouveau_solde = achat1.reste;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

                    String format2 = "%1$13s%2$-9s%3$13s%4$13s";
                    textSetting.setBold(SettingEnum.Enable);

                    if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                        if (tva_bon != 0 && timbre_bon != 0) {
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        } else if (tva_bon != 0) {

                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space

                        } else if (timbre_bon != 0) {
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }
                    }


                    if (remise_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL :", total_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "REMISE :", remise_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "NBR PRODUIT :", StringUtils.center(nbr_produit_str, 9), "TTC A PAYER :", total_a_payer_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION TOTAL///////////////////////////////////
                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    String format3 = "%1$16s%2$14s";

                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));


                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    if (type_print.equals("ACHAT")) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "TOTAL BON :", total_a_payer_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "VERSEMENT :", versement_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_FOOTER", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, achat1.num_bon));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }


                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }


    void print_bon_with_colisage() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    ////////////////////////////////////// INFO CLIENT ///////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_RIGHT);
                    //textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "Date :" + bon1.date_bon + " " + bon1.heure));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "CLIENT  :" + bon1.client));
                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "ADRESSE :" + bon1.adresse));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "TEL     :" + bon1.tel));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    if (type_print.equals("VENTE")) {
                        cmd.append(cmd.getTextCmd(textSetting, "BL N :" + bon1.num_bon));
                    } else if (type_print.equals("ORDER")) {
                        cmd.append(cmd.getTextCmd(textSetting, "COMMANDE N :" + bon1.num_bon));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    //cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setBold(SettingEnum.Enable);
                    String format0 = "%1$-19s %2$-9s %3$-5s %4$12s";
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format0, "PRODUIT", StringUtils.center("QTE", 9), StringUtils.center("U.G", 5), "P.U")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    double nbr_colis, colissage, qte, gte_gratuit, prix_unit;
                    String nbr_colis_Str, colissage_Str, qte_Str, gte_gratuit_Str, prix_unit_Str, X1_Str, X2_Str, eq1_Str, plus_Str;
                    //nbr_colis = 10.0; colissage =23.0 ; qte = 120.00 ; gte_gratuit = 1.0;  prix_unit = 12345.33 ;


                    for (int i = 0; i < final_panier_vente.size(); i++) {

                        cmd.append(cmd.getTextCmd(textSetting, final_panier_vente.get(i).produit));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        nbr_colis = final_panier_vente.get(i).nbr_colis;
                        nbr_colis_Str = new DecimalFormat("####0.##").format(nbr_colis);

                        colissage = final_panier_vente.get(i).colissage;
                        colissage_Str = new DecimalFormat("####0.##").format(colissage);

                        qte = final_panier_vente.get(i).qte;
                        qte_Str = new DecimalFormat("####0.##").format(qte);

                        gte_gratuit = final_panier_vente.get(i).gratuit;
                        gte_gratuit_Str = new DecimalFormat("####0.##").format(gte_gratuit);

                        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                            prix_unit = final_panier_vente.get(i).pv_ht;
                        }else{
                            prix_unit = final_panier_vente.get(i).pv_ht * (1 + (final_panier_vente.get(i).tva/100));
                        }

                        prix_unit_Str = new DecimalFormat("####0.00").format(prix_unit);


                        X1_Str = "X";
                        eq1_Str = "=";
                        plus_Str = "+";

                        if (gte_gratuit == 0.0) {
                            gte_gratuit_Str = "";
                            plus_Str = " ";
                        }

                        if (colissage == 0.0) {
                            nbr_colis_Str = "";
                            colissage_Str = "";
                            X1_Str = " ";
                            eq1_Str = " ";
                        }
                        String format1 = "  %1$-6s " + X1_Str + " %2$-6s " + eq1_Str + " %3$-9s" + plus_Str + "%4$-5sX%5$12s";
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format1, StringUtils.center(nbr_colis_Str, 6), StringUtils.center(colissage_Str, 6), StringUtils.center(qte_Str, 9), StringUtils.center(gte_gratuit_Str, 5), prix_unit_Str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space

                       /* if(i<final_panier.size()-1){
                            textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                            cmd.append(cmd.getTextCmd(textSetting, "------------------------"));
                            textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }*/


                    }
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    int nbr_produit;
                    Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
                    String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

                    nbr_produit = final_panier_vente.size();
                    nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

                    total_ht_bon = bon1.tot_ht;
                    total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

                    tva_bon = bon1.tot_tva;
                    tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

                    timbre_bon = bon1.timbre;
                    timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

                    total_bon = bon1.tot_ttc;
                    total_bon_str = new DecimalFormat("####0.00").format(total_bon);

                    remise_bon = bon1.remise;
                    remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

                    ancien_solde = bon1.solde_ancien;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

                    total_a_payer = bon1.montant_bon;
                    total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

                    versement = bon1.verser;
                    versement_str = new DecimalFormat("####0.00").format(versement);

                    nouveau_solde = bon1.reste;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

                    String format2 = "%1$13s%2$-9s%3$13s%4$13s";
                    textSetting.setBold(SettingEnum.Enable);


                    if (prefs.getBoolean("AFFICHAGE_HT", false)) {

                        if (tva_bon != 0 && timbre_bon != 0) {

                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space

                        } else if (tva_bon != 0) {

                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space

                        } else if (timbre_bon != 0) {
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }
                    }

                    if (remise_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL TTC :", total_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "REMISE :", remise_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "NBR PRODUIT :", StringUtils.center(nbr_produit_str, 9), "TTC A PAYER :", total_a_payer_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION TOTAL///////////////////////////////////
                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    String format3 = "%1$16s%2$14s";

                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));


                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    if (type_print.equals("VENTE")) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "TOTAL BON :", total_a_payer_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "VERSEMENT :", versement_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_FOOTER", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, bon1.num_bon));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }


                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }


    void print_bon_without_colisage() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    ////////////////////////////////////// INFO CLIENT ///////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_RIGHT);
                    //textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "Date :" + bon1.date_bon + " " + bon1.heure));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "CLIENT  :" + bon1.client));
                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "ADRESSE :" + bon1.adresse));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "TEL     :" + bon1.tel));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    if (type_print.equals("VENTE")) {
                        cmd.append(cmd.getTextCmd(textSetting, "BL N :" + bon1.num_bon));
                    } else if (type_print.equals("ORDER")) {
                        cmd.append(cmd.getTextCmd(textSetting, "COMMANDE N :" + bon1.num_bon));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    //cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setBold(SettingEnum.Enable);
                    String format0 = "%1$-25s %2$-4s %3$-8s %4$8s";
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format0, "PRODUIT", StringUtils.center("QTE", 4), StringUtils.center("P.U", 8), "TOTAL")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    double nbr_colis, colissage, qte, gte_gratuit, prix_unit;
                    String nbr_colis_Str, colissage_Str, qte_Str, gte_gratuit_Str, prix_unit_Str, X1_Str, X2_Str, eq1_Str, plus_Str, total_par_produit_str;
                    //nbr_colis = 10.0; colissage =23.0 ; qte = 120.00 ; gte_gratuit = 1.0;  prix_unit = 12345.33 ;


                    for (int i = 0; i < final_panier_vente.size(); i++) {

                        double total_par_produit = 0.0;

                        //cmd.append(cmd.getTextCmd(textSetting, final_panier_vente.get(i).produit));
                        //cmd.append(cmd.getLFCRCmd()); // one line space
                        nbr_colis = final_panier_vente.get(i).nbr_colis;
                        nbr_colis_Str = new DecimalFormat("####0.##").format(nbr_colis);

                        colissage = final_panier_vente.get(i).colissage;
                        colissage_Str = new DecimalFormat("####0.##").format(colissage);

                        qte = final_panier_vente.get(i).qte;
                        qte_Str = new DecimalFormat("####0.##").format(qte);

                        gte_gratuit = final_panier_vente.get(i).gratuit;
                        gte_gratuit_Str = new DecimalFormat("####0.##").format(gte_gratuit);

                        prix_unit = final_panier_vente.get(i).pv_ht;
                        prix_unit_Str = new DecimalFormat("####0.00").format(prix_unit);

                        total_par_produit = final_panier_vente.get(i).pv_ht * final_panier_vente.get(i).qte;
                        total_par_produit_str = new DecimalFormat("####0.00").format(total_par_produit);


                        X1_Str = "X";
                        eq1_Str = "=";
                        plus_Str = "+";

                        if (gte_gratuit == 0.0) {
                            gte_gratuit_Str = "";
                            plus_Str = " ";
                        }

                        if (colissage == 0.0) {
                            nbr_colis_Str = "";
                            colissage_Str = "";
                            X1_Str = " ";
                            eq1_Str = " ";
                        }

                        //String format0 = "%1$-25s %2$-4s %3$-8s %4$8s";
                        String format1 = "%1$-23s%2$-1s"+plus_Str+"%3$-5sX%4$-9s%5$8s";
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format1, final_panier_vente.get(i).produit, StringUtils.center(gte_gratuit_Str, 1), StringUtils.center(qte_Str, 5), StringUtils.center(prix_unit_Str, 9), total_par_produit_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space

                       /* if(i<final_panier.size()-1){
                            textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                            cmd.append(cmd.getTextCmd(textSetting, "------------------------"));
                            textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }*/


                    }
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    int nbr_produit;
                    Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
                    String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

                    nbr_produit = final_panier_vente.size();
                    nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

                    total_ht_bon = bon1.tot_ht;
                    total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

                    tva_bon = bon1.tot_tva;
                    tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

                    timbre_bon = bon1.timbre;
                    timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

                    total_bon = bon1.tot_ttc;
                    total_bon_str = new DecimalFormat("####0.00").format(total_bon);

                    remise_bon = bon1.remise;
                    remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

                    ancien_solde = bon1.solde_ancien;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

                    total_a_payer = bon1.montant_bon;
                    total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

                    versement = bon1.verser;
                    versement_str = new DecimalFormat("####0.00").format(versement);

                    nouveau_solde = bon1.reste;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

                    String format2 = "%1$13s%2$-9s%3$13s%4$13s";
                    textSetting.setBold(SettingEnum.Enable);


                    if (tva_bon != 0 && timbre_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    } else if (tva_bon != 0) {
                        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }
                    } else if (timbre_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    if (remise_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL TTC :", total_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "REMISE :", remise_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "NBR PRODUIT :", StringUtils.center(nbr_produit_str, 9), "TTC A PAYER :", total_a_payer_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION TOTAL///////////////////////////////////
                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    String format3 = "%1$16s%2$14s";

                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));


                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    if (type_print.equals("VENTE")) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "TOTAL BON :", total_a_payer_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "VERSEMENT :", versement_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_FOOTER", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, bon1.num_bon));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }


                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }


    void print_etiquette() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));
                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    double prix_vente = produit.pv1_ht * (1 + (produit.tva / 100));
                    String prix_vente_str = new DecimalFormat("####0.00").format(prix_vente);


                    cmd.append(cmd.getTextCmd(textSetting, produit.produit));
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setDoubleHeight(SettingEnum.Enable);
                    textSetting.setDoublePrinting(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, prix_vente_str + " DA"));
                    cmd.append(cmd.getLFCRCmd()); // one line space


                   /* //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, produit.code_barre));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }
                    */
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////

                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space


                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }


    void print_etat_vente() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    if(type_print.equals("ETAT_VENTE")){
                        cmd.append(cmd.getTextCmd(textSetting, "Etat De Vente"));
                    }else{
                        cmd.append(cmd.getTextCmd(textSetting, "Etat De Commande"));
                    }

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    cmd.append(cmd.getTextCmd(textSetting, "CODE CLIENT : " + code_client_etat));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "CLIENT : " + client_etat));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    ////////////////////////////////////// INFO DATES ///////////////////////////////////////////
                    //textSetting.setIsEscSmallCharactor(SettingEnum.Enable);
                    //cmd.append(cmd.getTextCmd(textSetting, "De :" + date_debut_etat));
                    //textSetting.setAlign(CommonEnum.ALIGN_RIGHT);
                    //format dates
                    String format_date = "%1$-3s %2$-26s %3$-5s %4$10s";
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format_date, "De:", date_debut_etat, "Vers:", date_fin_etat)));
                    //cmd.append(cmd.getTextCmd(textSetting, "Vers :" + date_fin_etat));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    //cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    //////////////////////////////////////////////////////////////////////////////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);
                    textSetting.setBold(SettingEnum.Enable);
                    String format0 = "%1$-24s%2$-5s%3$-9s%4$10s";
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format0, "PRODUIT", StringUtils.center("QTE", 4), StringUtils.center("P.U", 9), "TOTAL")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    double total_montant, total_remise, total_verser, credit_total, benefice_total, qte, chiffre_affaire, prix_unit;
                    String total_montant_Str, total_remise_Str, qte_Str, total_verser_Str, prix_unit_Str, credit_total_Str, benefice_total_Str, chiffre_affaire_Str, plus_Str, total_par_produit_str;
                    //nbr_colis = 10.0; colissage =23.0 ; qte = 120.00 ; gte_gratuit = 1.0;  prix_unit = 12345.33 ;


                    for (int i = 0; i < etats.size(); i++) {

                        double total_par_produit = 0.0;

                        qte = etats.get(i).quantite;
                        qte_Str = new DecimalFormat("####0.##").format(qte);

                        prix_unit = etats.get(i).pv_ht;
                        prix_unit_Str = new DecimalFormat("####0.##").format(prix_unit);

                        total_par_produit = etats.get(i).pv_ht * etats.get(i).quantite;
                        total_par_produit_str = new DecimalFormat("####0.##").format(total_par_produit);


                        //String format0 = "%1$-25s %2$-4s %3$-8s %4$8s";
                        String format1 = "%1$-24s%2$-4sX%3$-9s%4$10s";

                        String format_conclusion = "%1$-34s %2$12s";

                        switch (etats.get(i).code_parent) {
                            case "1" ->{
                                 textSetting.setBold(SettingEnum.Disable);
                                 cmd.append(cmd.getTextCmd(textSetting, String.format(format1, etats.get(i).produit, StringUtils.center(qte_Str, 4), StringUtils.center(prix_unit_Str, 9), total_par_produit_str)));
                                 cmd.append(cmd.getLFCRCmd()); // one line space
                            }
                            case "-6" -> {

                                textSetting.setBold(SettingEnum.Disable);
                                cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                                cmd.append(cmd.getTextCmd(textSetting, "Conclusion Total :"));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                                cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                                cmd.append(cmd.getLFCRCmd()); // one line space

                                for (int k = i, b = 1; k < etats.size() - 1; k++, b++) {

                                    i = k;
                                    textSetting.setBold(SettingEnum.Enable);

                                    qte = etats.get(i).quantite;
                                    qte_Str = new DecimalFormat("####0.##").format(qte);

                                    total_montant = etats.get(i).montant;
                                    total_montant_Str = new DecimalFormat("####0.00").format(total_montant);



                                    if(b == 1){
                                        cmd.append(cmd.getTextCmd(textSetting, String.format(format_conclusion, etats.get(i).produit, qte_Str)));
                                        cmd.append(cmd.getLFCRCmd()); // one line space
                                    }else if(b == 7){
                                        if(prefs.getBoolean("AFFICHAGE_BENIFICE", false)){
                                            cmd.append(cmd.getTextCmd(textSetting, String.format(format_conclusion, etats.get(i).produit, total_montant_Str)));
                                            cmd.append(cmd.getLFCRCmd()); // one line space
                                        }
                                    }else{
                                        cmd.append(cmd.getTextCmd(textSetting, String.format(format_conclusion, etats.get(i).produit, total_montant_Str)));
                                        cmd.append(cmd.getLFCRCmd()); // one line space
                                    }

                                }
                            }
                            case "-8" -> {

                                total_montant = etats.get(i).montant;
                                total_montant_Str = new DecimalFormat("####0.00").format(total_montant);

                                textSetting.setBold(SettingEnum.Disable);
                                cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                                cmd.append(cmd.getTextCmd(textSetting, "Objectif : "));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                                cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                                cmd.append(cmd.getTextCmd(textSetting, String.format(format_conclusion, "Objectif : ", total_montant_Str)));
                                cmd.append(cmd.getLFCRCmd()); // one line space
                            }
                        }
                    }

                    /////////////////////////////IMPRESSION BON2////////////////////////////////////

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    int nbr_produit;
                    Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
                    String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

                    nbr_produit = etats.size();
                    nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

                    /*total_ht_bon = bon1.tot_ht;
                    total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

                    tva_bon = bon1.tot_tva;
                    tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

                    timbre_bon = bon1.timbre;
                    timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

                    total_bon = bon1.tot_ttc;
                    total_bon_str = new DecimalFormat("####0.00").format(total_bon);

                    remise_bon = bon1.remise;
                    remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

                    ancien_solde = bon1.solde_ancien;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

                    total_a_payer = bon1.montant_bon;
                    total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

                    versement = bon1.verser;
                    versement_str = new DecimalFormat("####0.00").format(versement);

                    nouveau_solde = bon1.reste;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

                    String format2 = "%1$13s%2$-9s%3$13s%4$13s";
                    textSetting.setBold(SettingEnum.Enable);


                    if (tva_bon != 0 && timbre_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    } else if (tva_bon != 0) {
                        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                            cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TVA :", tva_bon_str)));
                            cmd.append(cmd.getLFCRCmd()); // one line space
                        }
                    } else if (timbre_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL HT :", total_ht_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TIMBRE :", timbre_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    if (remise_bon != 0) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "TOTAL TTC :", total_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "", "", "REMISE :", remise_bon_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format2, "NBR PRODUIT :", StringUtils.center(nbr_produit_str, 9), "TTC A PAYER :", total_a_payer_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////////////IMPRESSION TOTAL///////////////////////////////////
                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    //textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    String format3 = "%1$16s%2$14s";

                    //cmd.append(cmd.getTextCmd(textSetting, "123456789.123456789.123456789.123456789.123456789.123456789.123456789."));


                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    if (type_print.equals("VENTE")) {
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "TOTAL BON :", total_a_payer_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "VERSEMENT :", versement_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                        cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                        cmd.append(cmd.getLFCRCmd()); // one line space
                    }

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_FOOTER", "")));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////
                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setHeightInDot(50);//accept value:1~255
                    barcodeSetting.setBarcodeWidth(3);//accept value:2~6
                    barcodeSetting.setQrcodeDotSize(5);//accept value: Esc(1~15), Tsc(1~10);

                    try {
                        cmd.append(cmd.getBarcodeCmd(BarcodeType.CODE39, barcodeSetting, bon1.num_bon));
                    } catch (SdkException e) {
                        e.printStackTrace();
                    }
*/

                    //////////////////////////////// IMPRESSION CODE BARRE// ///////////////////////


                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getHeaderCmd());//初始化, Initial
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }

    void print_etiquette_code_barre(String barcodeContent, String produit, double prix_vente_ttc) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
                try {


                    CmdFactory tscFac = new TscFactory();
                    Cmd tscCmd = tscFac.create();

                    tscCmd.append(tscCmd.getHeaderCmd());

                    rtPrinter.writeMsg(TonyUtils.InitPrinter());
                    TonyUtils.Tsc_InitLabelPrint(rtPrinter);
                    ///rtPrinter.writeMsg(TonyUtils.SetSize("80", "40").getBytes());
                    String strPrintTxtproduit = TonyUtils.printText("20", "20", "TSS24.BF2", "0", "1", "1", produit + ";");
                    rtPrinter.writeMsg(strPrintTxtproduit.getBytes("GBK"));

                    String prix_vente_str = new DecimalFormat("####0.00").format(prix_vente_ttc) + " DA";
                    String strPrintTxtPrix = TonyUtils.printText("80", "80", "TSS24.BF2", "0", "2", "2", prix_vente_str + ";");
                    rtPrinter.writeMsg(strPrintTxtPrix.getBytes("GBK"));
                    // String strPrint = TonyUtils.setPRINT("1", "1");
                    // rtPrinter.writeMsg(strPrint.getBytes());

                    // tscCmd.append(tscCmd.getLFCRCmd()); // one line space


                    BarcodeSetting barcodeSetting = new BarcodeSetting();
                    barcodeSetting.setNarrowInDot(2);//narrow bar setting, bar width
                    barcodeSetting.setWideInDot(4);
                    barcodeSetting.setHeightInDot(48);//bar height setting
                    barcodeSetting.setBarcodeStringPosition(BarcodeStringPosition.BELOW_BARCODE);
                    barcodeSetting.setPrintRotation(PrintRotation.Rotate0);
                    int x = 80, y = 130;
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
                hideProgressDialog();
            }
        }).start();

    }


    void print_versement_client() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    cmd.append(cmd.getTextCmd(textSetting, "RECU DE PAIMENT CLIENT"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "N : " + carnet_c_print.carnet_num_bon));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "Nous soussignes,"));
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Certifions avoir recu en date du : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_c_print.carnet_date));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "la somme de : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, new DecimalFormat("####0.00").format(carnet_c_print.carnet_versement)));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Mode de paiment : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_c_print.carnet_mode_rg));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "de la part de : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_c_print.client));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Adresse: "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_c_print.adresse));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Observation: "));
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    Double ancien_solde, nouveau_solde;
                    String ancien_solde_str, nouveau_solde_str;


                    ancien_solde = carnet_c_print.carnet_achats;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);


                    nouveau_solde = carnet_c_print.carnet_achats - carnet_c_print.carnet_versement;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);
                    textSetting.setBold(SettingEnum.Enable);


                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Disable);

                    String format3 = "%1$16s%2$14s";

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "Ce recu est etabli pour servir et valoir ce que de droit."));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "Signature"));

                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }



    void print_versement_fournisseur() throws UnsupportedEncodingException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                showProgressDialog("Impression...");

                CmdFactory cmdFactory = new EscFactory();
                Cmd cmd = cmdFactory.create();
                cmd.append(cmd.getHeaderCmd());
                cmd.setChartsetName(mChartsetName);
                //cmd.setPrinterCharacterTable(22);
                CommonSetting commonSetting = new CommonSetting();
                commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                cmd.append(cmd.getCommonSettingCmd(commonSetting));
                BitmapSetting bitmapSetting = new BitmapSetting();
                bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);

                prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);


                try {

                    Bitmap mBitmap = null;
                    String preBlank = "        ";

                    String img_str = prefs.getString("COMPANY_LOGO", "");
                    if (!img_str.equals("")) {
                        //decode string to image
                        byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
                        mBitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                        cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap));
                        cmd.append(cmd.getLFCRCmd());
                    }

                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    textSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setDoubleWidth(SettingEnum.Disable);

                    if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("ACTIVITY_NAME", "")));
                    }
                    if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    }
                    if (!prefs.getString("COMPANY_TEL", "").equals("")) {
                        cmd.append(cmd.getLFCRCmd());
                        cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_TEL", "")));
                    }

                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    /////////////////////info bon //////////////////////////////////////////////////
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_B_9x24);

                    cmd.append(cmd.getTextCmd(textSetting, "RECU DE PAIMENT FOURNISSEUR"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "N : " + carnet_f_print.carnet_num_bon));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);
                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    textSetting.setAlign(CommonEnum.ALIGN_LEFT);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, "Nous soussignes,"));
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, carnet_f_print.fournis));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Certifions avoir recu en date du : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_f_print.carnet_date));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "la somme de : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, new DecimalFormat("####0.00").format(carnet_f_print.carnet_versement)));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Mode de paiment : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, carnet_f_print.carnet_mode_rg));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "de la part de : "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_NAME", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Adresse: "));
                    textSetting.setBold(SettingEnum.Enable);

                    cmd.append(cmd.getTextCmd(textSetting, prefs.getString("COMPANY_ADRESSE", "")));
                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    cmd.append(cmd.getTextCmd(textSetting, "Observation: "));
                    textSetting.setBold(SettingEnum.Enable);
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);
                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    /////////////////////////////IMPRESSION TOTAL////////////////////////////////////

                    Double ancien_solde, nouveau_solde;
                    String ancien_solde_str, nouveau_solde_str;


                    ancien_solde = carnet_f_print.carnet_achats;
                    ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);


                    nouveau_solde = carnet_f_print.carnet_achats - carnet_f_print.carnet_versement;
                    nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);
                    textSetting.setBold(SettingEnum.Enable);


                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////
                    textSetting.setBold(SettingEnum.Disable);

                    String format3 = "%1$16s%2$14s";

                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "ANCIEN SOLDE :", ancien_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getTextCmd(textSetting, String.format(format3, "NOUVEAU SOLDE :", nouveau_solde_str)));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    textSetting.setBold(SettingEnum.Disable);
                    textSetting.setDoubleWidth(SettingEnum.Disable);
                    //textSetting.setEscFontType(ESCFontTypeEnum.FONT_A_12x24);

                    cmd.append(cmd.getTextCmd(textSetting, "------------------------------------------------"));
                    cmd.append(cmd.getLFCRCmd()); // one line space

                    //////////////////////////////// IMPRESSION ANCIEN SOLDE ///////////////////////

                    cmd.append(cmd.getTextCmd(textSetting, "Ce recu est etabli pour servir et valoir ce que de droit."));
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    cmd.append(cmd.getLFCRCmd()); // one line space
                    textSetting.setBold(SettingEnum.Enable);
                    textSetting.setDoubleWidth(SettingEnum.Enable);
                    cmd.append(cmd.getTextCmd(textSetting, "Signature"));

                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space
                    cmd.append(cmd.getLFCRCmd());  // one line space

                } catch (SdkException | IOException e) {
                    e.printStackTrace();
                }
                if (rtPrinter != null) {
                    rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                }
                hideProgressDialog();
            }
        }).start();

    }

    private void print_arabic() throws UnsupportedEncodingException {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String fileName = Objects.requireNonNull(mActivity.getExternalCacheDir()).getPath() + "/webview_capture1.jpg";
                    bitmap = BitmapFactory.decodeFile(fileName);

                } catch (Exception e) {
                    Log.e("eeeeee", e.getMessage());
                }

                try {
                    Thread.sleep(1000);
                    showProgressDialog("Impression...");

                    CmdFactory cmdFactory = new EscFactory();
                    Cmd cmd = cmdFactory.create();
                    cmd.append(cmd.getHeaderCmd());

                    CommonSetting commonSetting = new CommonSetting();
                    commonSetting.setAlign(CommonEnum.ALIGN_MIDDLE);
                    cmd.append(cmd.getCommonSettingCmd(commonSetting));

                    BitmapSetting bitmapSetting = new BitmapSetting();

                    bitmapSetting.setBmpPrintMode(BmpPrintMode.MODE_SINGLE_COLOR);
                    //bitmapSetting.setBimtapLimitWidth(bmpPrintWidth * 8);
                    cmd.append(cmd.getBitmapCmd(bitmapSetting, bitmap));

                    cmd.append(cmd.getLFCRCmd());
                    cmd.append(cmd.getLFCRCmd());
                    if (rtPrinter != null) {
                        rtPrinter.writeMsg(cmd.getAppendCmds());//Sync Write
                    }

                    hideProgressDialog();

                } catch (SdkException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();

    }

    static class StringUtils {

        public static String center(String s, int size) {
            return center(s, size, ' ');
        }

        public static String center(String s, int size, char pad) {
            if (s == null || size <= s.length())
                return s;

            StringBuilder sb = new StringBuilder(size);
            for (int i = 0; i < (size - s.length()) / 2; i++) {
                sb.append(pad);
            }
            sb.append(s);
            while (sb.length() < size) {
                sb.append(pad);
            }
            return sb.toString();
        }
    }


    public void showProgressDialog(final String str) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(mActivity);
                }
                if (!TextUtils.isEmpty(str)) {
                    progressDialog.setMessage(str);
                } else {
                    progressDialog.setMessage("Impression...");
                }
                progressDialog.show();
            }
        });

    }

    public void hideProgressDialog() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });

    }
}

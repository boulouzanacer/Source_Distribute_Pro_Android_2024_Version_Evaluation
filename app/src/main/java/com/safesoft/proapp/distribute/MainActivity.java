package com.safesoft.proapp.distribute;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowInsetsController;
import android.widget.Toast;

import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.observer.PrinterObserver;
import com.rt.printerlibrary.observer.PrinterObserverManager;
import com.rt.printerlibrary.printer.RTPrinter;
import com.safesoft.proapp.distribute.activities.ActivityInfo;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.appUpdate.APKUtils;
import com.safesoft.proapp.distribute.appUpdate.CheckVerRequestTask;
import com.safesoft.proapp.distribute.appUpdate.UpdateApp;
import com.safesoft.proapp.distribute.eventsClasses.CheckVersionEvent;
import com.safesoft.proapp.distribute.eventsClasses.GetServerHashEvent;
import com.safesoft.proapp.distribute.fragments.FragmentMain;
import com.safesoft.proapp.distribute.utils.BaseEnum;
import com.safesoft.proapp.distribute.utils.Env;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends AppCompatActivity implements PrinterObserver {
    Fragment objFrgment;
    FragmentManager fragmentManager;
    private RTPrinter rtPrinter = null;
    private PrinterFactory printerFactory;
    final String PREFS = "ALL_PREFS";
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        Toolbar toolbar = findViewById(R.id.toolbardrawer);
        pref = getSharedPreferences(PREFS, 0);
        if (pref.getBoolean("APP_ACTIVATED", false)) {
            toolbar.setSubtitle(Env.APP_VERION_LABEL);
        } else {
            toolbar.setSubtitle(Env.APP_VERION_LABEL + " (Version évaluation)");
        }

        setSupportActionBar(toolbar);

        objFrgment = new FragmentMain();
        fragmentManager = getSupportFragmentManager();
        if (objFrgment != null) {
            fragmentManager.beginTransaction().replace(R.id.drawer_layoutt, objFrgment).commit();
        }


        if (pref.getString("date_time", null) == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date currentDateTime = Calendar.getInstance().getTime();
            String currentDateTimeString = sdf.format(currentDateTime);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("date_time", currentDateTimeString);
            editor.apply();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
        printerFactory = new ThermalPrinterFactory();
        rtPrinter = printerFactory.create();

        //add observer listen for connexion(bluetooth, wifi, usb)
        PrinterObserverManager.getInstance().add(this);

        EventBus.getDefault().register(this);

    }


   /* public void startActivity(Class clss, int request) {
        Intent intent = new Intent(this, clss);
        intent.putExtra("SOURCE_EXPORT", "NOTEXPORTED");
        startActivityForResult(intent, request);
    }*/

    @Override
    public void printerObserverCallback(PrinterInterface printerInterface, int state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (state) {
                    case CommonEnum.CONNECT_STATE_SUCCESS -> {
                        Toast.makeText(MainActivity.this, printerInterface.getConfigObject().toString() + " Connecté", Toast.LENGTH_SHORT).show();
                        BaseApplication.getInstance().setIsConnected(true);
                        rtPrinter.setPrinterInterface(printerInterface);
                        BaseApplication.getInstance().setRtPrinter(rtPrinter);
                    }
                    case CommonEnum.CONNECT_STATE_INTERRUPTED -> {
                        Toast.makeText(MainActivity.this, getString(R.string._main_disconnect), Toast.LENGTH_SHORT).show();
                        BaseApplication.getInstance().setIsConnected(false);
                    }
                    //BaseApplication.getInstance().setRtPrinter(null);
                    default -> {
                    }
                }
            }
        });
    }

    @Override
    public void printerReadMsgCallback(PrinterInterface printerInterface, byte[] bytes) {
        //byte[] bbytes = bytes;
        //PrinterInterface printerInterfacebb = printerInterface;
        //String a = "dddd";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.info) {
            Intent info_intent = new Intent(this, ActivityInfo.class);
            startActivity(info_intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            //DATABASE_OLD controller = new DATABASE_OLD(this);
            //controller.ResetPda();
            //controller.hasUniqueConstraint("PRODUIT");

        } else if(item.getItemId() == R.id.show_seriel_number){
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Numéro de série")
                    .setContentText(pref.getString("NUM_SERIE", "0"))
                    .show();
        } else if (item.getItemId() == R.id.update) {

            openPlayStoreApp("com.safesoft.proapp.distribute");

            /*String current_version = "0";
            String versionCode = "0";
            String android_unique_id = "0";
            String seriel_number = "0";
            int activation_code = 0;
            String revendeur = "";

            try {

                PackageManager pm = getPackageManager();
                String packageName = getPackageName();
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

                current_version = String.valueOf(packageInfo.versionName);
                versionCode = String.valueOf(packageInfo.versionCode);
                android_unique_id = getAndroidID(MainActivity.this);
                seriel_number = pref.getString("NUM_SERIE", "0");
                activation_code = pref.getInt("CODE_ACTIVATION", 0);
                revendeur = pref.getString("REVENDEUR", "0");


                //int dowloaded_version = getLocalVersion(getExternalCacheDir().getPath());
                new CheckVerRequestTask().execute(Env.URL_CHECK_VERSION, current_version, versionCode, android_unique_id, seriel_number, String.valueOf(activation_code), revendeur);

               *//* if (dowloaded_version > Integer.parseInt(current_version)) {
                    new GetServeHashRequestTask().execute();
                } else {
                    new CheckVerRequestTask().execute(Env.URL_CHECK_VERSION, current_version, versionCode, android_unique_id, seriel_number, String.valueOf(activation_code), revendeur);
                }*//*

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }*/


        }
        return super.onOptionsItemSelected(item);
    }

    public void openPlayStoreApp(String packageName) {
        try {
            // Open the app directly in the Play Store
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // Fallback: Open the Play Store in a browser if the Play Store app is unavailable
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public int getLocalVersion(String path) {
        String dowloaded_version = "0";
        // Create a File object
        File directory = new File(path);
        // Get all the files in the directory
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                // Check if it is a file or directory
                if (file.isFile() && file.getName().endsWith(".apk")) {

                    dowloaded_version = file.getName().substring(14);
                    dowloaded_version = dowloaded_version.replace(".apk", "");

                    break;
                }
            }
        }
        return Integer.parseInt(dowloaded_version);
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetHashServerEvent(GetServerHashEvent event) {

        String current_version = "0";
        String versionCode = "0";
        String android_unique_id = "0";
        String seriel_number = "0";
        int activation_code = 0;
        String revendeur = "";

        try {
            PackageManager pm = getPackageManager();
            String packageName = getPackageName();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

            current_version = String.valueOf(packageInfo.versionName);
            versionCode = String.valueOf(packageInfo.versionCode);
            android_unique_id = getAndroidID(MainActivity.this);
            seriel_number = pref.getString("NUM_SERIE", "0");
            activation_code = pref.getInt("CODE_ACTIVATION", 0);
            revendeur = pref.getString("REVENDEUR", "0");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        int dowloaded_version = getLocalVersion(getExternalCacheDir().getPath());
        String server_hash = event.getHash();
        String doanload_hash = APKUtils.getApkHash(getExternalCacheDir() + "/distribute_pro"+dowloaded_version+".apk", Env.hashAlgorithm);


        if (doanload_hash.equalsIgnoreCase(server_hash)) {
            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Mise a jour disponible")
                    .setContentText("Voulez-vous vraiment Installer la mise à jour " + dowloaded_version + " ?")
                    .setCancelText("Non")
                    .setConfirmText("Installer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(FileProvider.getUriForFile(MainActivity.this, getApplicationContext().getPackageName() + ".provider", new File(getExternalCacheDir() + "/distribute_pro" + dowloaded_version + ".apk")), "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);

                        sDialog.dismiss();

                    }).show();
        } else {
            new CheckVerRequestTask().execute(Env.URL_CHECK_VERSION, current_version, versionCode, android_unique_id, seriel_number, String.valueOf(activation_code), revendeur);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCheckVerReplayEvent(CheckVersionEvent event) {

        if (event.getCode() == 200) {

            new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Mise a jour disponible")
                    .setContentText("Voulez-vous vraiment Télécharger la mise à jour " + event.getVersion() + " ?")
                    .setCancelText("Non")
                    .setConfirmText("Télécharger")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        UpdateApp atualizaApp = new UpdateApp(this, event.getVersion());
                        atualizaApp.setContext(getApplicationContext());
                        atualizaApp.execute("http://144.91.122.24/apk/distribute/distribute_pro" + event.getVersion() + ".apk");

                        sDialog.dismiss();

                    }).show();


        } else if (event.getCode() == 201) {
            Crouton.makeText(MainActivity.this, event.getMessage(), Style.CONFIRM).show();
        } else if (event.getCode() == 202) {
            Crouton.makeText(MainActivity.this, event.getMessage(), Style.CONFIRM).show();
        } else if (event.getCode() == 203) {
            Crouton.makeText(MainActivity.this, event.getMessage(), Style.ALERT).show();
        } else if (event.getCode() == 204) {
            Crouton.makeText(MainActivity.this, event.getMessage(), Style.ALERT).show();
        } else {
            Crouton.makeText(MainActivity.this, event.getMessage(), Style.ALERT).show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}


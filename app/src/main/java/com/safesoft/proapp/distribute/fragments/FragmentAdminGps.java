package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.MainActivity;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.gps.service_location.LocationServerConnect;
import com.safesoft.proapp.distribute.gps.service_start.RestartBroadcastReceiver;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentAdminGps {

    public static final String ACTION_LOCATION_SERVICE_STATE = "LOCATION_SERVICE_STATE";
    public static final String PARAM_LOCATION_SERVICE_STATE = "PARAM_LOCATION_SERVICE_STATE";
    MaterialFancyButton btn_valider, btn_cancel;
    Button btn_check_connection;
    private EditText edt_device_name, edt_email, edt_password;
    private String CODE_CLIENT;
    private Boolean IS_EDIT;

    public static final String PREFS = "ALL_PREFS";
    private final EventBus bus = EventBus.getDefault();
    Activity mActivity;
    AlertDialog dialog;
    private NumberFormat nf;

    private ProgressDialog progressDialog;
    private DATABASE controller;
    private SharedPreferences prefs;
    private String deviceId = "123456789";
    private TextView device_id_txtv ;
    private static final int REQUEST_LOCATION_PERMISSION = 100;

    private boolean is_connected = false;


    //PopupWindow display method
    public void showDialogbox(Activity activity, String deviceId) {

        this.mActivity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");
        controller = new DATABASE(activity);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_admin_gps, null);
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
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_valider.setTextSize(15);
        btn_valider.setIconPosition(POSITION_LEFT);
        btn_valider.setFontIconSize(30);

        btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);

        btn_check_connection  = dialogview.findViewById(R.id.btn_check_connection);

        device_id_txtv = dialogview.findViewById(R.id.device_id);
        edt_device_name = dialogview.findViewById(R.id.edt_device_name);
        edt_email = dialogview.findViewById(R.id.edt_email);
        edt_password = dialogview.findViewById(R.id.edt_password);


        this.deviceId = deviceId;

        prefs = this.mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);

        device_id_txtv.setText(deviceId);
        edt_device_name.setText(prefs.getString("DEVICE_NAME", ""));
        edt_email.setText(prefs.getString("USER_EMAIL", ""));
        edt_password.setText(prefs.getString("USER_PASSWORD", ""));


        btn_check_connection.setOnClickListener(v -> {

            prefs.edit()
                    .putString("DEVICE_ID", device_id_txtv.getText().toString())
                    .putString("DEVICE_NAME", edt_device_name.getText().toString())
                    .putString("USER_EMAIL", edt_email.getText().toString())
                    .putString("USER_PASSWORD", edt_password.getText().toString())
                    .apply();

            checkAndRequestPermissions();
        });

        btn_valider.setOnClickListener(v -> {

            if(is_connected){
                prefs.edit()
                        .putString("DEVICE_ID", device_id_txtv.getText().toString())
                        .putString("DEVICE_NAME", edt_device_name.getText().toString())
                        .putString("USER_EMAIL", edt_email.getText().toString())
                        .putString("USER_PASSWORD", edt_password.getText().toString())
                        .putBoolean("PHONE_LOCATION_SERVICE", true)
                        .apply();

                launcheLocationService();

                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Succès ...!!")
                        .setContentText("Système de localisation activé !")
                        .show();

                broadcastActionBaz(mActivity,true);
            }else{

                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention ...!!")
                        .setContentText("Votre system de localisation n'est pas activé !")
                        .show();

                broadcastActionBaz(mActivity,false);
            }

            dialog.dismiss();

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });





    }

    public static void broadcastActionBaz(Context context, boolean param) {
        Intent intent = new Intent(ACTION_LOCATION_SERVICE_STATE);
        intent.putExtra(PARAM_LOCATION_SERVICE_STATE, param);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(context);
        bm.sendBroadcast(intent);
    }


    private void checkAndRequestPermissions() {

        if (edt_device_name.getText().toString().isEmpty()) {
            edt_device_name.setError("Ce champ est obligatoire");
            return;
        }

        if (edt_email.getText().toString().isEmpty()) {
            edt_email.setError("Ce champ est obligatoire");
            return;
        }

        if (edt_password.getText().toString().isEmpty()) {
            edt_password.setError("Ce champ est obligatoire");
            return;
        }

        // -------------------------
        // SHOW PROGRESS DIALOG HERE
        // -------------------------
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        // -------------------------

        boolean fineLocationGranted = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean foregroundServiceGranted = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            foregroundServiceGranted = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.FOREGROUND_SERVICE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        if (!fineLocationGranted || !foregroundServiceGranted) {
            ActivityCompat.requestPermissions(mActivity, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.FOREGROUND_SERVICE_LOCATION }, REQUEST_LOCATION_PERMISSION);
            progressDialog.dismiss(); // dismiss immediately because permission dialog interrupts flow
            return;
        }

        LocationServerConnect.ConnectServer(deviceId, edt_device_name.getText().toString(), edt_email.getText().toString(), edt_password.getText().toString(), new LocationServerConnect.Callback() {
                    @Override
                    public void onResult(int responseCode, String responseMessage) {

                            progressDialog.dismiss();  // HIDE PROGRESS

                            Log.e("LocationServer", "Success: " + responseCode + " - " + responseMessage);

                            if (responseCode == 200) {

                                mActivity.runOnUiThread(() -> {
                                    View customView = mActivity.getLayoutInflater().inflate(R.layout.cruton_style_loca_connected, null);
                                    Crouton.show(mActivity, customView);
                                });

                                is_connected = true;

                            } else if (responseCode == 401) {
                                mActivity.runOnUiThread(() -> {
                                    Crouton.makeText(mActivity, "Compte n'exist pas ou mot de passe incorrect!", Style.ALERT).show();
                                });
                            } else if (responseCode == 403) {
                                mActivity.runOnUiThread(() -> {
                                    Crouton.makeText(mActivity, "Ce téléphone est associé à un autre utilisateur.", Style.ALERT).show();
                                });
                            } else {
                                mActivity.runOnUiThread(() -> {
                                    Crouton.makeText(mActivity, "Problème de lancement du service localisation!", Style.ALERT).show();
                                });
                            }
                    }

                    @Override
                    public void onError(Exception e) {

                        progressDialog.dismiss();  // HIDE PROGRESS
                        Log.e("LocationServer", "Error", e);
                        mActivity.runOnUiThread(() -> {
                            Crouton.makeText(mActivity, "Erreur: " + e.getMessage(), Style.ALERT).show();
                        });

                    }
                }
        );
    }

    private void launcheLocationService() {
        LocationManager locationManager = (LocationManager) mActivity.getSystemService(LOCATION_SERVICE);

        if (locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            mActivity.runOnUiThread(() -> {
                new androidx.appcompat.app.AlertDialog.Builder(mActivity)
                        .setTitle("GPS désactivé")
                        .setMessage("Votre GPS est désactivé. Voulez-vous l’activer ?")
                        .setPositiveButton("Oui", (d, w) -> {
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mActivity.startActivity(i);
                        })
                        .setNegativeButton("Non", (d, w) -> d.dismiss())
                        .show();
            });
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PowerManager pm = (PowerManager) mActivity.getSystemService(Context.POWER_SERVICE);
            boolean ignoring = pm.isIgnoringBatteryOptimizations(mActivity.getPackageName());

            if (!ignoring) {
                mActivity.runOnUiThread(() -> {
                    new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Distribute pro batterie optimization")
                            .setContentText(
                                    "Le service de localisation est activé. Toutefois, l’optimisation de la batterie " +
                                            "peut empêcher son bon fonctionnement. Souhaitez-vous l’autoriser ?"
                            )
                            .setCancelText("Non")
                            .setConfirmText("Oui")
                            .showCancelButton(true)
                            .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                            .setConfirmClickListener(sDialog -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + mActivity.getPackageName()));
                                mActivity.startActivity(intent);
                                sDialog.dismissWithAnimation();
                            })
                            .show();
                });
            }
        }

        RestartBroadcastReceiver.scheduleJob(mActivity);

    }

}
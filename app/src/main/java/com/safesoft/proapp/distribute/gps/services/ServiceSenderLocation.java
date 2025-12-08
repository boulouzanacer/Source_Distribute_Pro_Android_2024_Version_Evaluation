package com.safesoft.proapp.distribute.gps.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.gps.LocationSender;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ServiceSenderLocation extends Service {
    private static final String TAG = "TRACKKK";
    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler;
    private Runnable locationRunnable;
    private String deviceId = "123456789";
    private String device_name = "";
    private String email = "";
    private String password = "";
    private DATABASE controller;
    private String querry_bon1 = "SELECT " +
            "BON1.RECORDID, " +
            "BON1.NUM_BON, " +
            "BON1.DATE_BON, " +
            "BON1.HEURE, " +
            "BON1.DATE_F, " +
            "BON1.HEURE_F, " +
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
            "BON1.TOT_HT - BON1.REMISE - BON1.MONTANT_ACHAT AS BENIFICE_BON, " +

            "BON1.ANCIEN_SOLDE, " +
            "BON1.VERSER, " +
            "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

            "BON1.CODE_CLIENT, " +
            "Client.CLIENT, " +
            "Client.ADRESSE, " +
            "Client.WILAYA, " +
            "Client.COMMUNE, " +
            "Client.TEL, " +
            "Client.RC, " +
            "Client.IFISCAL, " +
            "Client.AI, " +
            "Client.NIS, " +

            "Client.LATITUDE as LATITUDE_CLIENT, " +
            "Client.LONGITUDE as LONGITUDE_CLIENT, " +

            "Client.SOLDE AS SOLDE_CLIENT, " +
            "Client.CREDIT_LIMIT, " +

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
            "LEFT JOIN Client ON BON1.CODE_CLIENT = Client.CODE_CLIENT " +
            "WHERE IS_EXPORTED = 0 ORDER BY BON1.NUM_BON";

    String querry_bon1_temp = "SELECT " +
            "BON1_TEMP.RECORDID, " +
            "BON1_TEMP.NUM_BON, " +
            "BON1_TEMP.DATE_BON, " +
            "BON1_TEMP.HEURE, " +
            "BON1_TEMP.DATE_F, " +
            "BON1_TEMP.HEURE_F, " +
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
            "BON1_TEMP.TOT_HT - BON1_TEMP.REMISE - BON1_TEMP.MONTANT_ACHAT AS BENIFICE_BON, " +

            "BON1_TEMP.ANCIEN_SOLDE, " +
            "BON1_TEMP.VERSER, " +
            "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

            "BON1_TEMP.CODE_CLIENT, " +
            "CLIENT.CLIENT, " +
            "CLIENT.ADRESSE, " +
            "CLIENT.WILAYA, " +
            "CLIENT.COMMUNE, " +
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
            "WHERE IS_EXPORTED = 0 ORDER BY BON1_TEMP.NUM_BON";


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");

        // Gérer action de redémarrage depuis l'Activity
        if (intent != null && "RESTART_SERVICE".equals(intent.getAction())) {
            Log.i(TAG, "Received RESTART_SERVICE action.");
            restartServiceLogic();
            return START_STICKY;
        }

        startForegroundServiceLogic();
        return START_STICKY;
    }

    private void startForegroundServiceLogic() {
        NotificationChannel channel = new NotificationChannel("location_channel", "Location Service", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);

        Notification notification = new NotificationCompat.Builder(this, "location_channel")
                .setContentTitle("Tracking location")
                .setContentText("Location tracking is active")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        startForeground(1, notification);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        deviceId = prefs.getString("DEVICE_ID", "12345678");
        device_name = prefs.getString("DEVICE_NAME", device_name);
        email = prefs.getString("USER_EMAIL", email);
        password = prefs.getString("USER_PASSWORD", password);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        handler = new Handler();
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                getLocationAndSend();
                handler.postDelayed(this, 1000 * 60); // 15s interval
            }
        };
        handler.post(locationRunnable);
    }

    private void restartServiceLogic() {
        if (handler != null && locationRunnable != null) {
            handler.removeCallbacks(locationRunnable);
        }
        startForegroundServiceLogic();
    }

    private void getLocationAndSend() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                Log.e("LOCATION", location.getLatitude() + " / " + location.getLongitude());
                ArrayList<PostData_Bon1> bon1List = new ArrayList<>();
                ArrayList<PostData_Bon1> bon1TempList = new ArrayList<>();
                try {
                    controller = new DATABASE(this);
                    bon1List = controller.select_all_bon1_from_database(querry_bon1);
                    bon1TempList = controller.select_all_bon1_from_database(querry_bon1_temp);

                }catch (Exception e){
                    Log.e("LOCATION", "bon 1 / bon1_temp error : " + e.getMessage());
                }
                LocationSender.sendLocation(deviceId, location.getLatitude(), location.getLongitude(), device_name, 0, 0,0,0, bon1List, bon1TempList, this);
            }
        });
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");

        if (handler != null && locationRunnable != null) {
            handler.removeCallbacks(locationRunnable);
        }

        stopForeground(true);
        super.onDestroy();
    }
}

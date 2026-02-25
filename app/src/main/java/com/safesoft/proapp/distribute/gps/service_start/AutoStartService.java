package com.safesoft.proapp.distribute.gps.service_start;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.safesoft.proapp.distribute.gps.LocationSender;

import java.util.Timer;
import java.util.TimerTask;

public class AutoStartService extends Service {
    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler;
    private Runnable locationRunnable;
    private String deviceId = "123456789";
    private String device_name = "";
    private String email = "";
    private String password = "";

    private static boolean isRunning = false;
    private static final String TAG = "AutoService";

    public int counter = 0;
    private Timer timer;
    private TimerTask timerTask;



    public static final String ACTION_FOO = "com.safesoft.proapp.distribute.ACTION_FOO";
    public static final String EXTRA_PARAM_A = "com.safesoft.proapp.distribute.PARAM_A";
    private static final String CHANNEL_ID = "minute_service_channel";

    public AutoStartService(Context context) {
        Log.i(TAG, "AutoStartService: Here we Go!!!!!");
    }

    public AutoStartService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (isRunning) {
            Log.w(TAG, "Service already running, ignoring start");
            return START_STICKY;
        }

        isRunning = true;

        createNotificationChannel();

        startForeground(1337, createNotification());

        startJob(this);
        return START_STICKY;
    }



    public void startJob(Context context) {
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
                //getLocationAndSend();
                getCurrentLocation(context);
                handler.postDelayed(this, 1000 * 60); // 15s interval
            }
        };
        handler.post(locationRunnable);
    }


    private void getCurrentLocation(Context context) {

        // Vérifie la permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).setMaxUpdates(1).setWaitForAccurateLocation(true).build();

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                fusedLocationClient.removeLocationUpdates(this);

                if (locationResult.getLastLocation() == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                if (location != null) {
                    Log.e("LOCATION 2", location.getLatitude() + " / " + location.getLongitude());
                    new Thread(() -> {
                        LocationSender.sendLocation(deviceId, location.getLatitude(), location.getLongitude(), device_name, 0, 0,0,0,  context);
                    }).start();
                }

            }
        }, getMainLooper());
    }


    public static void broadcastActionBaz(Context context, String param) {
        Intent intent = new Intent(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM_A, param);
        LocalBroadcastManager bm = LocalBroadcastManager.getInstance(context);
        bm.sendBroadcast(intent);
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Distribute Pro")
                .setContentText("Tache executes chaque 1 minute")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Minute Foreground Service",
                NotificationManager.IMPORTANCE_HIGH
        );

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: Service is destroyed :( ");
        //Intent broadcastIntent = new Intent(this, RestartBroadcastReceiver.class);
        //sendBroadcast(broadcastIntent);
        isRunning = false;
        // Stop handler callbacks
        if (handler != null && locationRunnable != null) {
            handler.removeCallbacks(locationRunnable);
            handler.removeCallbacksAndMessages(null);
        }

    }
}

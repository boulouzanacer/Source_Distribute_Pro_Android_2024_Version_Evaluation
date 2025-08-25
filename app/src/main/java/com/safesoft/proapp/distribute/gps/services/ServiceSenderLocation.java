package com.safesoft.proapp.distribute.gps.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
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
import com.safesoft.proapp.distribute.gps.LocationSender;

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
                LocationSender.sendLocation(deviceId, location.getLatitude(), location.getLongitude());
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

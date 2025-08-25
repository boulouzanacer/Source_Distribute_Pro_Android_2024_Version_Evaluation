package com.safesoft.proapp.distribute.gps;

import android.os.StrictMode;

import com.safesoft.proapp.distribute.eventsClasses.SendLocationEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationSender {

    public static void sendLocation(String phoneId, double latitude, double longitude) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // for testing only
        StrictMode.setThreadPolicy(policy);

        try {
            URL url = new URL("https://ebwbrjkqrsgumlwvhrhb.supabase.co/functions/v1/location-api"); // For emulator use 10.0.2.2 instead of localhost
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Build JSON body
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("phone_id", phoneId);
            jsonParam.put("latitude", latitude);
            jsonParam.put("longitude", longitude);

            // Write body
            OutputStream os = conn.getOutputStream();
            os.write(jsonParam.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();
            if(responseCode == 200){
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "connected", "Service running... ok"));
            }else if(responseCode == 404){
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "server not found"));
            }else if(responseCode == 403){
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "blocked", "user disabled"));
            }else{
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "Problème fatal : " + conn.getResponseMessage()));
            }

            System.out.println("Response Code: " + conn.getResponseCode());
            System.out.println("Response Message : " + conn.getResponseMessage());

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendLocationEvent(0, "not_connected", "Problème fatal : " + e.getMessage()));
        }
    }
}

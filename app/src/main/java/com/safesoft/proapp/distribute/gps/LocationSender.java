package com.safesoft.proapp.distribute.gps;

import android.content.Context;
import android.os.StrictMode;

import com.safesoft.proapp.distribute.eventsClasses.SendLocationEvent;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LocationSender {

    public static void sendLocation(
            String deviceId,
            double latitude,
            double longitude,
            String deviceName,
            int nbrBon1,
            int nbrBon2,
            int nbrBon1Temp,
            int nbrBon2Temp,
            List<PostData_Bon1> bon1List,
            List<PostData_Bon1> bon1TempList,
            Context rContext) {

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        try {
            URL url = new URL("https://geo-track.onrender.com/api/bon/sync");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Main JSON payload
            JSONObject json = new JSONObject();

            // DEVICE
            JSONObject device = new JSONObject();
            device.put("device_id", deviceId);
            device.put("latitude", latitude);
            device.put("longitude", longitude);
            device.put("timestamp", java.time.LocalDateTime.now().toString());
            device.put("name", deviceName);

            json.put("device", device);

            // STATS
            JSONObject stats = new JSONObject();
            stats.put("nbr_bon1", nbrBon1);
            stats.put("nbr_bon2", nbrBon2);
            stats.put("nbr_bon1_temp", nbrBon1Temp);
            stats.put("nbr_bon2_temp", nbrBon2Temp);

            json.put("stats", stats);

            // BON 1
            json.put("bon1", BonJsonBuilder.convertBon1List(bon1List, rContext));

            // BON 1 TEMP
            json.put("bon1_temp", BonJsonBuilder.convertBon1_Temp_List(bon1TempList, rContext));

            // Send
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "connected", "Service running... ok"));
            } else if (responseCode == 404) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "server not found"));
            } else if (responseCode == 403) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "blocked", "user disabled"));
            } else {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "Fatal Error : " + conn.getResponseMessage()));
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendLocationEvent(0, "not_connected", "Fatal error : " + e.getMessage()));
        }
    }
}

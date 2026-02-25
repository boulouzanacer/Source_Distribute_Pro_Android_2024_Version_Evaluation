package com.safesoft.proapp.distribute.gps.service_location;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationServerConnect {

    public interface Callback {
        void onResult(int responseCode, String responseMessage);
        void onError(Exception e);
    }

    public static void ConnectServer(String phoneId, String device_name, String email, String password, Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://geo-track.onrender.com/api/phone-auth");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("phone_id", phoneId);
                jsonParam.put("device_name", device_name);
                jsonParam.put("email", email);
                jsonParam.put("username", email);
                jsonParam.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                String responseMessage = conn.getResponseMessage();

                conn.disconnect();

                if (callback != null) {
                    callback.onResult(responseCode, responseMessage);
                }

            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        }).start();
    }
}

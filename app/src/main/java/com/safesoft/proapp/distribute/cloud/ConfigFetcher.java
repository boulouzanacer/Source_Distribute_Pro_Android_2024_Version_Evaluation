package com.safesoft.proapp.distribute.cloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.safesoft.proapp.distribute.utils.Env;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

public class ConfigFetcher {

    private static final String TAG = "ConfigFetcher";
    private static final String PREF_NAME = "ALL_PREFS";
    private static final String API_URL = Env.URL_GET_CONFIG;

    public static boolean fetchAndSave(
            Context context,
            String emailCloud,
            String passwordCloud
    ) {

        HttpURLConnection conn = null;

        try {
            // -------- OPEN CONNECTION --------
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setDoOutput(true);
            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
            );

            // -------- POST BODY --------
            String postData = "EMAIL_CLOUD=" + URLEncoder.encode(emailCloud, "UTF-8") + "&PASSWORD_CLOUD=" + URLEncoder.encode(passwordCloud, "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            // -------- READ RESPONSE --------
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // -------- PARSE JSON --------
            JSONObject json = new JSONObject(response.toString());

            if (json.getInt("code") != 200) {
                Log.e(TAG, "API error: " + json.optString("message"));
                return false;
            }

            JSONObject data = json.getJSONObject("data");

            // -------- SAVE TO PREFS --------
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Iterator<String> keys = data.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = data.get(key);

                if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else {
                    editor.putString(key, String.valueOf(value));
                }
            }

            editor.apply();
            return true;

        } catch (Exception e) {
            Log.e(TAG, "Fetch failed", e);
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}

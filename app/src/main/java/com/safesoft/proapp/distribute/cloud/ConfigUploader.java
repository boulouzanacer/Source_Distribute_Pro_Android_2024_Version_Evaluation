package com.safesoft.proapp.distribute.cloud;

import android.util.Log;

import com.safesoft.proapp.distribute.utils.Env;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class ConfigUploader {

    private static final String TAG = "ConfigUploader";
    private static final String API_URL = Env.URL_SAVE_CONFIG;

    public static boolean postConfig(Map<String, String> params) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded"
            );

            // Build POST body
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                postData.append('=');
                postData.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8")
            );
            writer.write(postData.toString());
            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "HTTP Response: " + responseCode);

            return responseCode == HttpURLConnection.HTTP_OK;

        } catch (Exception e) {
            Log.e(TAG, "Upload failed", e);
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}

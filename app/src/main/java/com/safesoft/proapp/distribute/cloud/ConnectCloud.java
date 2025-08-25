package com.safesoft.proapp.distribute.cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.eventsClasses.onConnectCloudEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConnectCloud extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0]; // URL to call
        String param_email = params[1]; // parameter email
        String param_password = params[2]; // parameter geneated_number


        String resultToDisplay = "";
        InputStream in = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            // Set POST request body
            String postParameters = "email=" + param_email + "& password=" + param_password;

            byte[] postData = postParameters.getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = urlConnection.getOutputStream()) {
                os.write(postData);
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = inReader.readLine()) != null) {
                    response.append(inputLine);
                }
                inReader.close();

                resultToDisplay = response.toString();
            } else {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", 202);
                jsonObject.put("is_connect", false);
                jsonObject.put("message", "Probleme de connexion avec le serveur");

                // Convert JSON object to string and log it
                String jsonString = jsonObject.toString();
                resultToDisplay = jsonString;
            }

        } catch (Exception e) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", 204);
                jsonObject.put("is_connect", false);
                jsonObject.put("message", "Failed to connect to server");
                String jsonString = jsonObject.toString();
                e.printStackTrace();
                resultToDisplay = jsonString;
            } catch (Exception ignored) {
            }

        }

        return resultToDisplay;
    }

    @Override
    protected void onPostExecute(String result) {
        // Process the result here (e.g., update UI)
        // e.g., textView.setText(result);
        Log.e("result", result);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(result);
            EventBus.getDefault().post(new onConnectCloudEvent(jsonObject.getInt("code"), jsonObject.getBoolean("is_connect"), jsonObject.getString("message")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
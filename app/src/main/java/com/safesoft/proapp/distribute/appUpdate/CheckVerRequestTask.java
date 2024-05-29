package com.safesoft.proapp.distribute.appUpdate;

import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.eventsClasses.CheckVersionEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.opencensus.trace.MessageEvent;

public class CheckVerRequestTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0]; // URL to call
        String param1 = params[1]; // parameter 1
        String param2 = params[2]; // parameter 2
        String param3 = params[3]; // parameter 3
        String param4 = params[4]; // parameter 4
        String param5 = params[5]; // parameter 5
        String param6 = params[6]; // parameter 6

        String resultToDisplay = "";
        InputStream in = null;

        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);

            // Set POST request body
            String postParameters = "old_version=" + param1 +
                    "& version_code=" + param3+
                    "& android_unique_id=" + param3+
                    "& seriel_number=" + param4+
                    "& activation_code=" + param5+
                    "& revendeur=" + param6;

            byte[] postData = postParameters.getBytes("UTF-8");

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
                jsonObject.put("code", responseCode);
                jsonObject.put("message", "Probleme de connexion avec le serveur");
                jsonObject.put("version", "");

                // Convert JSON object to string and log it
                String jsonString = jsonObject.toString();
                resultToDisplay = jsonString;
            }

        } catch (Exception e) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("code", 204);
                jsonObject.put("message", "Failed to connect to server");
                jsonObject.put("version", "");
                String jsonString = jsonObject.toString();
                e.printStackTrace();
                resultToDisplay = jsonString;
            }catch (Exception ignored){}

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
            EventBus.getDefault().post(new CheckVersionEvent(jsonObject.getInt("code"), jsonObject.getString("message"), jsonObject.getString("version")));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
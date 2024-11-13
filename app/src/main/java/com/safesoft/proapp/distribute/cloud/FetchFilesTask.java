package com.safesoft.proapp.distribute.cloud;

import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.utils.Env;

import org.json.JSONArray;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FetchFilesTask{

    public static JSONArray getListFile(String email_cloud){

        JSONArray fileList = null;
        try {
            URL url = new URL(Env.URL_GET_FILES);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.connect();

            // Send the filename in the POST request
            String postData = "email_cloud=" + email_cloud;
            try (OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream())) {
                outputStream.write(postData.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            // Check if connection was successful
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                fileList = new JSONArray(response.toString());

            }

            return fileList;

        } catch (Exception e) {
            Log.e("FetchFilesTask", "Error fetching files", e);
            return new JSONArray();
        }
    }
}
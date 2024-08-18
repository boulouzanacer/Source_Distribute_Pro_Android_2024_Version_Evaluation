package com.safesoft.proapp.distribute.appUpdate;

import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.eventsClasses.CheckVersionEvent;
import com.safesoft.proapp.distribute.eventsClasses.GetServerHashEvent;
import com.safesoft.proapp.distribute.utils.Env;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetServeHashRequestTask extends AsyncTask<String, Void, String> {

    private static OkHttpClient client = new OkHttpClient();
    
    @Override
    protected String doInBackground(String... params) {
        
            Request request = new Request.Builder()
                    .url(Env.URL_GET_APK_HASH)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                return response.body().string().trim();  // Assuming the server returns the hash as plain text
            } catch (IOException e) {
                return e.getMessage();
            }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.e("result", result);
        EventBus.getDefault().post(new GetServerHashEvent(result));
    }
}
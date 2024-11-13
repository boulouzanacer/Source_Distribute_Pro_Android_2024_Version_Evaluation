package com.safesoft.proapp.distribute.cloud;

import android.content.Context;
import android.util.Log;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.utils.Env;

import org.json.JSONArray;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class DownloadBackupTask {

    public static boolean downloadBackupFile(Context context, String backup_filename, String email_cloud, FileUploader.ProgressListener listener){
        HttpURLConnection connection = null;

        try {

            URL url = new URL(Env.URL_DOWNLOAD_FILES);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // Send the filename in the POST request
            String postData = "filename=" + URLEncoder.encode(backup_filename, "UTF-8") + "&email_cloud="+ email_cloud;
            try (OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream())) {
                outputStream.write(postData.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            int fileLength = connection.getContentLength();

            File dbFile = context.getDatabasePath(DATABASE.DATABASE_NAME);
            File tempFile = new File(context.getCacheDir(), DATABASE.DATABASE_NAME);

            try (InputStream input = connection.getInputStream(); FileOutputStream output = new FileOutputStream(tempFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = input.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    if (fileLength > 0) {
                        int progress = (int) (totalBytesRead * 100 / fileLength);
                        listener.onProgressUpdate(progress);
                    }
                    output.write(buffer, 0, bytesRead);
                }
            }

            if (dbFile.exists()) {
                dbFile.delete();
            }
            tempFile.renameTo(dbFile);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
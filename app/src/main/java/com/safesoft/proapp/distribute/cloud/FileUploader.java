package com.safesoft.proapp.distribute.cloud;

import android.util.Log;

import com.safesoft.proapp.distribute.utils.Env;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileUploader {

    public interface ProgressListener {
        void onProgressUpdate(int percentage);
    }

    public static String uploadFile(String sourceFileUri, String seriel_number, String android_id, String version, String email, ProgressListener listener) {
        String upLoadServerUri = Env.URL_UPLOAD_BDD; // replace with your server URL
        String fileName = sourceFileUri;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        try {
            File sourceFile = new File(sourceFileUri);
            if (!sourceFile.isFile()) {
                return "File not found: " + sourceFileUri;
            }

            long totalFileSize = sourceFile.length();
            long totalBytesUploaded = 0;

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
           // conn.setRequestProperty("file", fileName);


            dos = new DataOutputStream(conn.getOutputStream());

            // Send additional data
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"seriel_number\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(seriel_number + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"android_id\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(android_id + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"version\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(version + lineEnd);

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"email\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(email + lineEnd);

            // Send file data
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                totalBytesUploaded += bufferSize;

                // Calculate upload progress and notify listener
                if (listener != null) {
                    int progress = (int) ((totalBytesUploaded * 100) / totalFileSize);
                    listener.onProgressUpdate(progress);
                }

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            int serverResponseCode = conn.getResponseCode();
            fileInputStream.close();
            dos.flush();
            dos.close();
            //Log.v("DATAA", conn.);
            if(serverResponseCode == HttpURLConnection.HTTP_OK){
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                // Close the BufferedReader
                in.close();

                // Print or use the response content
                System.out.println("Response: " + response);

                return response.toString();
            }else{
                return "{\"status\": \"error\", \"message\": \"Le téléchargement de la sauvegarde a échoué.\"}";
            }


        } catch (IOException e) {
            e.printStackTrace();
            //return "Exception: " + e.getMessage();
            return "{\"status\": \"error\", \"message\": \"Échec de la vérification la connexion au serveur a échoué \"}";
        }
    }
}

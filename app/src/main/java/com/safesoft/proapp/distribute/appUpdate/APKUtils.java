package com.safesoft.proapp.distribute.appUpdate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class APKUtils {

    public static String getApkHash(String apkFilePath, String hashAlgorithm) {
        try {
            // Open the APK file
            File apkFile = new File(apkFilePath);
            FileInputStream fis = new FileInputStream(apkFile);

            // Create MessageDigest instance for the chosen algorithm
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);

            // Read the APK file and update the digest
            byte[] byteBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(byteBuffer)) != -1) {
                digest.update(byteBuffer, 0, bytesRead);
            }
            fis.close();

            // Get the hash's bytes
            byte[] hashBytes = digest.digest();

            // Convert the hash bytes to a hexadecimal string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }

            // Return the computed hash
            return sb.toString();

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

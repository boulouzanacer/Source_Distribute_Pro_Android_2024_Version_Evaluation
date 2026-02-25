package com.safesoft.proapp.distribute.exportdata;

import android.app.Activity;

import com.safesoft.proapp.distribute.postData.PostData_Produit;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
public class CsvExporter {

    public static File exportProductsToCache(Activity activity, ArrayList<PostData_Produit> products) {
        File file = new File(activity.getCacheDir(), "EXPORTED_PRODUCTS_DATA.csv");

        try (FileOutputStream fos = new FileOutputStream(file)) {

            // CSV Header
            String header = "CODEBARRE,PRODUIT,PA_TTC,PV1_TTC,PV2_TTC, PV3_TTC, QTE\n";
            fos.write(header.getBytes(StandardCharsets.UTF_8));

            // CSV Rows
            for (PostData_Produit p : products) {
                String row = p.code_barre + ","
                        + escape(p.produit) + ","
                        + p.pa_ht * (1 + p.tva / 100) + ","
                        + p.pv1_ht * (1 + p.tva / 100) + ","
                        + p.pv2_ht * (1 + p.tva / 100) + ","
                        + p.pv3_ht * (1 + p.tva / 100) + ","
                        + p.stock + "\n";
                fos.write(row.getBytes(StandardCharsets.UTF_8));
            }

            fos.flush();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}

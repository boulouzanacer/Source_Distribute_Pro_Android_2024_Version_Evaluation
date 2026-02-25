package com.safesoft.proapp.distribute.gps;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SendLocationEvent;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocationSender {

    public static void sendLocation(
            String deviceId,
            double latitude,
            double longitude,
            String deviceName,
            int nbrBon1,
            int nbrBon2,
            int nbrBon1Temp,
            int nbrBon2Temp,
            Context rContext) {

        //StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        DATABASE controller = new DATABASE(rContext);

        try {

            URL url = new URL("https://geo-track.onrender.com/api/bon/sync");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            // Main JSON payload
            JSONObject json = new JSONObject();

            // DEVICE
            JSONObject device = new JSONObject();
            device.put("device_id", deviceId);
            device.put("latitude", latitude);
            device.put("longitude", longitude);
            device.put("timestamp", java.time.LocalDateTime.now().toString());
            device.put("name", deviceName);

            json.put("device", device);

            // STATS
            JSONObject stats = new JSONObject();
            stats.put("nbr_bon1", nbrBon1);
            stats.put("nbr_bon2", nbrBon2);
            stats.put("nbr_bon1_temp", nbrBon1Temp);
            stats.put("nbr_bon2_temp", nbrBon2Temp);

            json.put("stats", stats);

            // BON 1
            try {
                 String querry_bon1 = "SELECT " +
                        "BON1.RECORDID, " +
                        "BON1.NUM_BON, " +
                        "BON1.DATE_BON, " +
                        "BON1.HEURE, " +
                        "BON1.DATE_F, " +
                        "BON1.HEURE_F, " +
                        "BON1.MODE_RG, " +
                        "BON1.MODE_TARIF, " +

                        "BON1.NBR_P, " +
                        "BON1.TOT_QTE, " +

                        "BON1.TOT_HT, " +
                        "BON1.TOT_TVA, " +
                        "BON1.TIMBRE, " +
                        "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE AS TOT_TTC, " +
                        "BON1.REMISE, " +
                        "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE AS MONTANT_BON, " +
                        "BON1.MONTANT_ACHAT, " +
                        "BON1.TOT_HT - BON1.REMISE - BON1.MONTANT_ACHAT AS BENIFICE_BON, " +

                        "BON1.ANCIEN_SOLDE, " +
                        "BON1.VERSER, " +
                        "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                        "BON1.CODE_CLIENT, " +
                        "Client.CLIENT, " +
                        "Client.ADRESSE, " +
                        "Client.WILAYA, " +
                        "Client.COMMUNE, " +
                        "Client.TEL, " +
                        "Client.RC, " +
                        "Client.IFISCAL, " +
                        "Client.AI, " +
                        "Client.NIS, " +

                        "Client.LATITUDE as LATITUDE_CLIENT, " +
                        "Client.LONGITUDE as LONGITUDE_CLIENT, " +

                        "Client.SOLDE AS SOLDE_CLIENT, " +
                        "Client.CREDIT_LIMIT, " +

                        "BON1.LATITUDE, " +
                        "BON1.LONGITUDE, " +
                        "BON1.LIVRER, " +
                        "BON1.DATE_LIV, " +
                        "BON1.IS_IMPORTED, " +

                        "BON1.CODE_DEPOT, " +
                        "BON1.CODE_VENDEUR, " +
                        "BON1.EXPORTATION, " +
                        "BON1.BLOCAGE " +
                        "FROM BON1 " +
                        "LEFT JOIN Client ON BON1.CODE_CLIENT = Client.CODE_CLIENT " +
                        "WHERE IS_EXPORTED = 0 ORDER BY BON1.NUM_BON";
                ArrayList<PostData_Bon1> bon1List = controller.select_all_bon1_from_database(querry_bon1);
                json.put("bon1", BonJsonBuilder.convertBon1List(bon1List, rContext));
            }catch (Exception e){
                Log.e("LOCATION", "bon 1 : " + e.getMessage());
            }


            // BON 1 TEMP
            try {
                String querry_bon1_temp = "SELECT " +
                        "BON1_TEMP.RECORDID, " +
                        "BON1_TEMP.NUM_BON, " +
                        "BON1_TEMP.DATE_BON, " +
                        "BON1_TEMP.HEURE, " +
                        "BON1_TEMP.DATE_F, " +
                        "BON1_TEMP.HEURE_F, " +
                        "BON1_TEMP.MODE_RG, " +
                        "BON1_TEMP.MODE_TARIF, " +

                        "BON1_TEMP.NBR_P, " +
                        "BON1_TEMP.TOT_QTE, " +

                        "BON1_TEMP.TOT_HT, " +
                        "BON1_TEMP.TOT_TVA, " +
                        "BON1_TEMP.TIMBRE, " +
                        "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE AS TOT_TTC, " +
                        "BON1_TEMP.REMISE, " +
                        "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE AS MONTANT_BON, " +
                        "BON1_TEMP.MONTANT_ACHAT, " +
                        "BON1_TEMP.TOT_HT - BON1_TEMP.REMISE - BON1_TEMP.MONTANT_ACHAT AS BENIFICE_BON, " +

                        "BON1_TEMP.ANCIEN_SOLDE, " +
                        "BON1_TEMP.VERSER, " +
                        "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

                        "BON1_TEMP.CODE_CLIENT, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.ADRESSE, " +
                        "CLIENT.WILAYA, " +
                        "CLIENT.COMMUNE, " +
                        "CLIENT.TEL, " +
                        "CLIENT.RC, " +
                        "CLIENT.IFISCAL, " +
                        "CLIENT.AI, " +
                        "CLIENT.NIS, " +

                        "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                        "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                        "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                        "CLIENT.CREDIT_LIMIT, " +

                        "BON1_TEMP.LATITUDE, " +
                        "BON1_TEMP.LONGITUDE, " +
                        "BON1_TEMP.LIVRER, " +
                        "BON1_TEMP.DATE_LIV, " +
                        "BON1_TEMP.IS_IMPORTED, " +

                        "BON1_TEMP.CODE_DEPOT, " +
                        "BON1_TEMP.CODE_VENDEUR, " +
                        "BON1_TEMP.EXPORTATION, " +
                        "BON1_TEMP.BLOCAGE " +
                        "FROM BON1_TEMP " +
                        "LEFT JOIN CLIENT ON BON1_TEMP.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                        "WHERE IS_EXPORTED = 0 ORDER BY BON1_TEMP.NUM_BON";
                ArrayList<PostData_Bon1> bon1TempList = controller.select_all_bon1_from_database(querry_bon1_temp);
                json.put("bon1_temp", BonJsonBuilder.convertBon1_Temp_List(bon1TempList, rContext));
            }catch (Exception e){
                Log.e("LOCATION", "bon1_temp error : " + e.getMessage());
            }


            // Send
            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "connected", "Service running... ok"));
            } else if (responseCode == 404) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "server not found"));
            } else if (responseCode == 403) {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "blocked", "user disabled"));
            } else {
                EventBus.getDefault().post(new SendLocationEvent(responseCode, "not_connected", "Fatal Error : " + conn.getResponseMessage()));
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            EventBus.getDefault().post(new SendLocationEvent(0, "not_connected", "Fatal error : " + e.getMessage()));
        }
    }
}

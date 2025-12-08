package com.safesoft.proapp.distribute.gps;

import android.content.Context;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class BonJsonBuilder {

    // Convert list of Bon1 → JSON Array
    public static JSONArray convertBon1List(List<PostData_Bon1> bonList, Context context) throws Exception {

        DATABASE controller = new DATABASE(context);

        JSONArray array = new JSONArray();

        for (int i = 0; i < bonList.size(); i++) {
            PostData_Bon1 bon = bonList.get(i);

            String querry_bon2 = "SELECT " +
                    "BON2.RECORDID, " +
                    "BON2.CODE_BARRE, " +
                    "BON2.NUM_BON, " +
                    "BON2.PRODUIT, " +
                    "BON2.NBRE_COLIS, " +
                    "BON2.COLISSAGE, " +
                    "BON2.QTE, " +
                    "BON2.QTE_GRAT, " +
                    "BON2.PV_HT, " +
                    "BON2.PA_HT, " +
                    "BON2.TVA, " +
                    "BON2.CODE_DEPOT, " +
                    "BON2.DESTOCK_TYPE, " +
                    "BON2.DESTOCK_CODE_BARRE, " +
                    "BON2.DESTOCK_QTE, " +

                    "PRODUIT.ISNEW, " +
                    "PRODUIT.PV_LIMITE, " +
                    "PRODUIT.STOCK, " +
                    "PRODUIT.PROMO, " +
                    "PRODUIT.QTE_PROMO, " +
                    "PRODUIT.D1, " +
                    "PRODUIT.D2, " +
                    "PRODUIT.PP1_HT " +

                    "FROM BON2 LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE BON2.NUM_BON = '" + bonList.get(i).num_bon + "'";

            List<PostData_Bon2> items = controller.select_bon2_from_database(querry_bon2);

            JSONObject bonObject = new JSONObject();

            // HEADER
            JSONObject header = new JSONObject();
            header.put("NUM_BON", bon.num_bon);
            header.put("CODE_CLIENT", bon.code_client);
            header.put("DATE_BON", bon.date_bon);
            header.put("HEURE", bon.heure);
            header.put("NBR_P", bon.nbr_p);
            header.put("TOT_QTE", bon.tot_qte);
            header.put("MODE_TARIF", bon.mode_tarif);
            header.put("CODE_DEPOT", bon.code_depot);
            header.put("MODE_RG", bon.mode_rg);
            header.put("CODE_VENDEUR", bon.code_vendeur);
            header.put("TOT_HT", bon.tot_ht);
            header.put("TOT_TVA", bon.tot_tva);
            header.put("TIMBRE", bon.timbre);
            header.put("TIMBRE_CHECK", bon.timbre > 0 ? "O" : "F");
            header.put("LATITUDE", bon.latitude);
            header.put("LONGITUDE", bon.longitude);
            header.put("REMISE", bon.remise);
            header.put("MONTANT_ACHAT", bon.montant_achat);
            header.put("ANCIEN_SOLDE", bon.ancien_solde);
            header.put("EXPORTATION", bon.exportation);
            header.put("BLOCAGE", bon.blocage);
            header.put("VERSER", bon.verser);
            header.put("LIVRER", bon.livrer);
            header.put("DATE_LIV", bon.date_liv);
            header.put("IS_IMPORTED", bon.is_imported);
            header.put("IS_EXPORTED", 0);

            bonObject.put("header", header);

            // ITEMS
            JSONArray itemArray = new JSONArray();
            for (PostData_Bon2 it : items) {
                JSONObject item = new JSONObject();
                item.put("CODE_BARRE", it.codebarre);
                item.put("PRODUIT", it.produit);
                item.put("NBRE_COLIS", it.nbr_colis);
                item.put("COLISSAGE", it.colissage);
                item.put("QTE_GRAT", it.gratuit);
                item.put("QTE", it.qte);
                item.put("PV_HT", it.pv_ht);
                item.put("PA_HT", it.pa_ht);
                item.put("DESTOCK_TYPE", it.destock_type);
                item.put("DESTOCK_CODE_BARRE", it.destock_code_barre);
                item.put("DESTOCK_QTE", it.destock_qte);
                item.put("TVA", it.tva);
                item.put("CODE_DEPOT", it.code_depot);
                itemArray.put(item);
            }

            bonObject.put("items", itemArray);
            array.put(bonObject);
        }

        return array;
    }


    public static JSONArray convertBon1_Temp_List(List<PostData_Bon1> bonList, Context context) throws Exception {

        DATABASE controller = new DATABASE(context);

        JSONArray array = new JSONArray();

        for (int i = 0; i < bonList.size(); i++) {
            PostData_Bon1 bon_temp = bonList.get(i);

            String querry_bon2_temp = "SELECT " +
                    "BON2_TEMP.RECORDID, " +
                    "BON2_TEMP.CODE_BARRE, " +
                    "BON2_TEMP.NUM_BON, " +
                    "BON2_TEMP.PRODUIT, " +
                    "BON2_TEMP.NBRE_COLIS, " +
                    "BON2_TEMP.COLISSAGE, " +
                    "BON2_TEMP.QTE, " +
                    "BON2_TEMP.QTE_GRAT, " +
                    "BON2_TEMP.PV_HT, " +
                    "BON2_TEMP.PA_HT, " +
                    "BON2_TEMP.TVA, " +
                    "BON2_TEMP.CODE_DEPOT, " +
                    "BON2_TEMP.DESTOCK_TYPE, " +
                    "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                    "BON2_TEMP.DESTOCK_QTE, " +

                    "PRODUIT.ISNEW, " +
                    "PRODUIT.PV_LIMITE, " +
                    "PRODUIT.STOCK, " +
                    "PRODUIT.PROMO, " +
                    "PRODUIT.QTE_PROMO, " +
                    "PRODUIT.D1, " +
                    "PRODUIT.D2, " +
                    "PRODUIT.PP1_HT " +

                    "FROM BON2_TEMP LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE BON2_TEMP.NUM_BON = '" + bonList.get(i).num_bon + "'";

            List<PostData_Bon2> items = controller.select_bon2_from_database(querry_bon2_temp);

            JSONObject bonObject = new JSONObject();

            // HEADER
            JSONObject header = new JSONObject();
            header.put("NUM_BON", bon_temp.num_bon);
            header.put("CODE_CLIENT", bon_temp.code_client);
            header.put("DATE_BON", bon_temp.date_bon);
            header.put("HEURE", bon_temp.heure);
            header.put("NBR_P", bon_temp.nbr_p);
            header.put("TOT_QTE", bon_temp.tot_qte);
            header.put("MODE_TARIF", bon_temp.mode_tarif);
            header.put("CODE_DEPOT", bon_temp.code_depot);
            header.put("MODE_RG", bon_temp.mode_rg);
            header.put("CODE_VENDEUR", bon_temp.code_vendeur);
            header.put("TOT_HT", bon_temp.tot_ht);
            header.put("TOT_TVA", bon_temp.tot_tva);
            header.put("TIMBRE", bon_temp.timbre);
            header.put("TIMBRE_CHECK", bon_temp.timbre > 0 ? "O" : "F");
            header.put("LATITUDE", bon_temp.latitude);
            header.put("LONGITUDE", bon_temp.longitude);
            header.put("REMISE", bon_temp.remise);
            header.put("MONTANT_ACHAT", bon_temp.montant_achat);
            header.put("ANCIEN_SOLDE", bon_temp.ancien_solde);
            header.put("EXPORTATION", bon_temp.exportation);
            header.put("BLOCAGE", bon_temp.blocage);
            header.put("VERSER", bon_temp.verser);
            header.put("LIVRER", bon_temp.livrer);
            header.put("DATE_LIV", bon_temp.date_liv);
            header.put("IS_IMPORTED", bon_temp.is_imported);
            header.put("IS_EXPORTED", 0);

            bonObject.put("header", header);

            // ITEMS
            JSONArray itemArray = new JSONArray();
            for (PostData_Bon2 it : items) {
                JSONObject item = new JSONObject();
                item.put("CODE_BARRE", it.codebarre);
                item.put("PRODUIT", it.produit);
                item.put("NBRE_COLIS", it.nbr_colis);
                item.put("COLISSAGE", it.colissage);
                item.put("QTE_GRAT", it.gratuit);
                item.put("QTE", it.qte);
                item.put("PV_HT", it.pv_ht);
                item.put("PA_HT", it.pa_ht);
                item.put("DESTOCK_TYPE", it.destock_type);
                item.put("DESTOCK_CODE_BARRE", it.destock_code_barre);
                item.put("DESTOCK_QTE", it.destock_qte);
                item.put("TVA", it.tva);
                item.put("CODE_DEPOT", it.code_depot);
                itemArray.put(item);
            }

            bonObject.put("items", itemArray);
            array.put(bonObject);
        }

        return array;
    }
}
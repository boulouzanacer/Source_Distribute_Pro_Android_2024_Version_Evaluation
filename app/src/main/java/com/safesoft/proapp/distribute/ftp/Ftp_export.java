package com.safesoft.proapp.distribute.ftp;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.FragmentSelectBonTransfer;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Ftp_export {

    private Activity mActivity;
    SharedPreferences prefs;
    private List<PostData_Achat1> achat1s;
    private List<PostData_Bon1> bon1s;
    private List<PostData_Bon1> bon1s_Temp;
    private List<PostData_Inv1> Invs1;
    private List<PostData_Bon2> bon2s;
    private List<PostData_Bon2> bon2s_Temp;
    private List<PostData_Inv2> Inv2s;
    private ArrayList<PostData_Carnet_c> all_versement_client;
    private DATABASE controller;
    private final String PREFS = "ALL_PREFS";
    private String serverIp, serverPort, username, password, ftp_imp, ftp_imp_def, ftp_exp, code_depot, nom_depot, code_vendeur, nom_vendeur;
    private Map<String, String> F_SQL_LIST_ACHAT;
    private Map<String, String> F_SQL_LIST_VENTE;
    private Map<String, String> F_SQL_LIST_COMMAND;
    private Map<String, String> F_SQL_LIST_INVENTAIRE;
    private Map<String, String> F_SQL_LIST_SITUATION;
    private String file_name = null;
    private FTPClient con = null;

    public void start(Activity activity, String SOURCE, String nom_bon) throws ParseException {

        mActivity = activity;

        controller = new DATABASE(mActivity);
        PostData_Params params2 = new PostData_Params();

        try {
            params2 = controller.select_params_from_database("SELECT * FROM PARAMS");
        } catch (Exception ignored) {

        }

        if (params2 != null) {
            serverIp = params2.ftp_server;
            serverPort = params2.ftp_port;
            username = params2.ftp_user;
            password = params2.ftp_pass;
            ftp_exp = params2.ftp_imp;
            ftp_imp_def = params2.ftp_exp;
        }

        //config
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        nom_depot = prefs.getString("NOM_DEPOT", code_depot);
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");
        nom_vendeur = prefs.getString("NOM_VENDEUR", code_vendeur);


        switch (SOURCE) {
            case "ACHAT" -> new LongOperationAchat().execute();
            case "SALE" -> new LongOperationSale().execute();
            case "ORDER" -> new LongOperationOrder().execute();
            case "INVENTAIRE" -> new LongOperationInventaire().execute();
            case "TRANSFERT_LIST" -> new LongOperationGetListBon().execute();
            case "PRODUCT_LIST" -> new LongOperationImportationProduits(nom_bon).execute();
        }

    }


    // create and prepare "sales" local file to export via FTP

    public void prepare_local_file_achat() throws ParseException {

        F_SQL_LIST_ACHAT = new HashMap<>();
        F_SQL_LIST_SITUATION = new HashMap<>();
        String F_SQL = "";

        String querry = "SELECT " +
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

                "BON1.ANCIEN_SOLDE, " +
                "BON1.VERSER, " +
                "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                "BON1.CODE_CLIENT, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.TEL, " +
                "coalesce(CLIENT.RC, '') RC, " +
                "coalesce(CLIENT.IFISCAL, '') IFISCAL, " +
                "coalesce(CLIENT.AI, '') AI, " +
                "coalesce(CLIENT.NIS, '') NIS, " +

                "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                "CLIENT.CREDIT_LIMIT, " +

                "BON1.LATITUDE, " +
                "BON1.LONGITUDE, " +

                "BON1.CODE_DEPOT, " +
                "BON1.CODE_VENDEUR, " +
                "BON1.EXPORTATION, " +
                "BON1.BLOCAGE " +
                "FROM BON1 " +
                "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                "WHERE BLOCAGE = 'F' ORDER BY BON1.NUM_BON";

        achat1s = controller.select_all_achat1_from_database(querry);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");


        for (int i = 0; i < achat1s.size(); i++) {

            Date dt = format.parse(achat1s.get(i).date_bon);
            assert dt != null;

            F_SQL = "ACHAT|" + achat1s.get(i).nbr_p + "|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT, CODE_CLIENT, CLIENT, ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE, CODE_VENDEUR ";

            F_SQL = F_SQL + ")\n VALUES ( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') ," +
                    " '" + bon1s.get(i).code_client.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).client.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).adresse.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).wilaya.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).commune.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).tel.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).rc.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ifiscal.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).nis.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ai.replace("'", "''") + "'," +
                    "  " + bon1s.get(i).latitude_client + ", " + bon1s.get(i).longitude_client;

            F_SQL = F_SQL + ", iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";

            ///////////////////////////////////// BON1 ////////////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO BON1 (RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,CODE_DEPOT, CODE_VENDEUR, " +
                    "CODE_CLIENT,DATE_BON, HEURE,MODE_RG,BLOCAGE,MODE_TARIF," +
                    "VERSER, TIMBRE, REMISE, ANCIEN_SOLDE, LATITUDE, LONGITUDE, UTILISATEUR)\n ";


            F_SQL = F_SQL + " VALUES ( (SELECT GEN_ID(GEN_BON1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:,:CODE_CAISSE:,iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'), ";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','" + bon1s.get(i).mode_rg + "','" + bon1s.get(i).blocage + "','" + bon1s.get(i).mode_tarif + "',";
            F_SQL = F_SQL + bon1s.get(i).verser + "," + bon1s.get(i).timbre + "," + bon1s.get(i).remise + "," + bon1s.get(i).ancien_solde + "," + bon1s.get(i).latitude + "," + bon1s.get(i).longitude + ", 'TERMINAL_MOBILE');\n";


            String querry_select = "SELECT " +
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
                    "WHERE BON2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


            bon2s = controller.select_bon2_from_database(querry_select);

            String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

            for (int j = 0; j < bon2s.size(); j++) {

                ///////////////////////////////////PRODUIT /////////////////////////////////////////
                new PostData_Produit();
                PostData_Produit postData_produit;
                String querry_isnew = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                postData_produit = controller.select_one_produit_from_database(querry_isnew);
                if (postData_produit != null) {
                    if (postData_produit.isNew == 1) {
                        F_SQL = F_SQL + "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PA_HT, PRODUIT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", PV4_HT, PV5_HT, PV6_HT ";
                        }
                        F_SQL = F_SQL + ") VALUES ('" + postData_produit.code_barre + "', '" + postData_produit.ref_produit + "', '" + postData_produit.pa_ht + "', '" + postData_produit.produit + "', '" + postData_produit.tva + "',  '" + postData_produit.stock + "', '" + postData_produit.colissage + "',  '" + postData_produit.pv1_ht + "', '" + postData_produit.pv2_ht + "', '" + postData_produit.pv3_ht + "'";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", '" + postData_produit.pv4_ht + "', '" + postData_produit.pv5_ht + "', '" + postData_produit.pv6_ht + "'";
                        }
                        F_SQL = F_SQL + ") MATCHING (CODE_BARRE);\n";
                    }
                }
                ///////////////////////////////////PRODUIT /////////////////////////////////////////

                ///////////////////////////////////CODE BARRE //////////////////////////////////////
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

                String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN " +
                        "FROM CODEBARRE WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                codebarres = controller.select_all_codebarre_from_database(querry_codebarre);

                for(int k = 0;k<codebarres.size(); k++){
                    //insert into codebarre
                    F_SQL = F_SQL + "UPDATE OR INSERT INTO CODEBARRE (CODE_BARRE, CODE_BARRE_SYN) VALUES (";
                    F_SQL = F_SQL + " '" + codebarres.get(k).code_barre + "' , '" + codebarres.get(k).code_barre_syn + "' ";
                    F_SQL = F_SQL + ") MATCHING (CODE_BARRE);";
                }

                ///////////////////////////////////CODE BARRE //////////////////////////////////////

                ///////////////////////////////////BON 2 ///////////////////////////////////////////
                F_SQL = F_SQL + "INSERT INTO BON2 (CODE_DEPOT,RECORDID,NUM_BON,";
                F_SQL = F_SQL + "CODE_BARRE,PRODUIT,DESTOCK_TYPE,DESTOCK_CODE_BARRE,DESTOCK_QTE,";
                F_SQL = F_SQL + "NBRE_COLIS,COLISSAGE,QTE,QTE_GRAT,";
                F_SQL = F_SQL + "TVA,PV_HT_AR,PV_HT,PA_HT)\n";
                F_SQL = F_SQL + "VALUES\n";
                F_SQL = F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_BON2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL = F_SQL + "'" + bon2s.get(j).codebarre.replace("'", "''") + "','" + bon2s.get(j).produit.replace("'", "''") + "', iif('" + bon2s.get(j).destock_type + "' = 'null', null,'" + bon2s.get(j).destock_type + "') , iif('" + bon2s.get(j).destock_code_barre + "' = 'null', null,'" + bon2s.get(j).destock_code_barre + "') ,'" + bon2s.get(j).destock_qte + "',";
                F_SQL = F_SQL + bon2s.get(j).nbr_colis + "," + bon2s.get(j).colissage + "," + bon2s.get(j).qte + "," + bon2s.get(j).gratuit + ",";
                F_SQL = F_SQL + bon2s.get(j).tva + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pa_ht + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + "ACHATS,VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','BL-VENTE','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "',";
            F_SQL = F_SQL + bon1s.get(i).montant_bon + "," + bon1s.get(i).verser + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (bon1s.get(i).verser != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'BL-VENTE',lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000')," + bon1s.get(i).verser + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "');\n";
            }

            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "VENTE_" + bon1s.get(i).num_bon + "_" + bon1s.get(i).exportation + "_" + bon1s.get(i).date_bon + ".BLV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_VENTE.put(file_name, F_SQL);
        }


        //////////////////////////////////////// SITUATION /////////////////////////////////////////////////
        F_SQL = "";
        all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                "CARNET_C.CODE_CLIENT, " +
                "CARNET_C.DATE_CARNET, " +

                "CLIENT.CLIENT, " +
                "CLIENT.LATITUDE, " +
                "CLIENT.LONGITUDE, " +
                "CLIENT.TEL, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "CLIENT.RC, " +
                "CLIENT.IFISCAL, " +
                "CLIENT.AI, " +
                "CLIENT.NIS, " +
                "CLIENT.MODE_TARIF, " +

                "CARNET_C.HEURE, " +
                "CARNET_C.ACHATS, " +
                "CARNET_C.VERSEMENTS, " +
                "CARNET_C.SOURCE, " +
                "CARNET_C.NUM_BON, " +
                "CARNET_C.MODE_RG, " +
                "CARNET_C.REMARQUES, " +
                "CARNET_C.UTILISATEUR, " +
                "CARNET_C.EXPORTATION, " +
                "CARNET_C.IS_EXPORTED " +


                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON " +
                "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");

        for (int i = 0; i < all_versement_client.size(); i++) {

            Date dt = format.parse(all_versement_client.get(i).carnet_date);
            assert dt != null;

            F_SQL = "SITUATION-CLIENT|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE , CODE_VENDEUR ";

            try {
                F_SQL = F_SQL + ")\n VALUES ( " +
                        " iif('" + code_depot + "' = '000000', null, '" + code_depot + "') , " +
                        " '" + all_versement_client.get(i).code_client + "'," +
                        " iif('" + all_versement_client.get(i).client + "' = null , null, '" + all_versement_client.get(i).client.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).adresse + "' = null , null, '" + all_versement_client.get(i).adresse.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).wilaya + "' = null, null , '" + all_versement_client.get(i).wilaya.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).commune + "' = null, null , '" + all_versement_client.get(i).commune.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).tel + "' = null, null , '" + all_versement_client.get(i).tel.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).rc + "' = 'null', null , '" + all_versement_client.get(i).rc + "') , " +
                        " iif('" + all_versement_client.get(i).ifiscal + "' = null , null, '" + all_versement_client.get(i).ifiscal + "') , " +
                        " iif('" + all_versement_client.get(i).nis + "' = null, null , '" + all_versement_client.get(i).nis + "') , " +
                        " iif('" + all_versement_client.get(i).ai + "' = null, null , '" + all_versement_client.get(i).ai + "') , " +
                        " " + all_versement_client.get(i).latitude + ", " +
                        " " + all_versement_client.get(i).longitude + " ";
            } catch (Exception e) {
                Log.v("ERROR", e.getMessage());
                int x = 0;
            }


            F_SQL = F_SQL + ",iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "') ";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";


            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + " VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + all_versement_client.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + all_versement_client.get(i).carnet_heure + "','SITUATION-CLIENT','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "',";
            F_SQL = F_SQL + " " + all_versement_client.get(i).carnet_versement + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (all_versement_client.get(i).carnet_versement != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'SITUATION-CLIENT',lpad ((SELECT GEN_ID(GEN_CARNET_C_ID,0) FROM RDB$DATABASE) ,6,'000000')," + all_versement_client.get(i).carnet_versement + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "');\n";
            }

            file_name = "SITUATION_VRC" + all_versement_client.get(i).recordid + "_" + all_versement_client.get(i).exportation + "_" + all_versement_client.get(i).carnet_date + ".STC";
            file_name = file_name.replace("/", "_");
            F_SQL_LIST_SITUATION.put(file_name, F_SQL);

        }

    }

    // create and prepare "sales" local file to export via FTP

    public void prepare_local_file_sale() throws ParseException {

        F_SQL_LIST_VENTE = new HashMap<>();
        F_SQL_LIST_SITUATION = new HashMap<>();
        String F_SQL = "";

        String querry = "SELECT " +
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
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "CLIENT.TEL, " +
                "coalesce(CLIENT.RC, '') RC, " +
                "coalesce(CLIENT.IFISCAL, '') IFISCAL, " +
                "coalesce(CLIENT.AI, '') AI, " +
                "coalesce(CLIENT.NIS, '') NIS, " +

                "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                "CLIENT.CREDIT_LIMIT, " +

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
                "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                "WHERE BLOCAGE = 'F' ORDER BY BON1.NUM_BON";

        bon1s = controller.select_all_bon1_from_database(querry);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");


        for (int i = 0; i < bon1s.size(); i++) {

            Date dt = format.parse(bon1s.get(i).date_bon);
            assert dt != null;

            F_SQL = "VENTE|" + bon1s.get(i).nbr_p + "|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE, CODE_VENDEUR ";

            F_SQL = F_SQL + ")\n VALUES ( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') ," +
                    " '" + bon1s.get(i).code_client.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).client.replace("'", "''") + "', " +
                    " '" + bon1s.get(i).adresse.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).wilaya.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).commune.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).tel.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).rc.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ifiscal.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).nis.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ai.replace("'", "''") + "'," +
                    "  " + bon1s.get(i).latitude_client + ", " + bon1s.get(i).longitude_client;

            F_SQL = F_SQL + ", iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";

            ///////////////////////////////////// BON1 ////////////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO BON1 (RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,CODE_DEPOT, CODE_VENDEUR, " +
                    "CODE_CLIENT,DATE_BON, HEURE,MODE_RG,BLOCAGE,MODE_TARIF," +
                    "VERSER, TIMBRE, REMISE, ANCIEN_SOLDE, LATITUDE, LONGITUDE, UTILISATEUR)\n ";


            F_SQL = F_SQL + " VALUES ( (SELECT GEN_ID(GEN_BON1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:,:CODE_CAISSE:,iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'), ";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','" + bon1s.get(i).mode_rg + "','" + bon1s.get(i).blocage + "','" + bon1s.get(i).mode_tarif + "',";
            F_SQL = F_SQL + bon1s.get(i).verser + "," + bon1s.get(i).timbre + "," + bon1s.get(i).remise + "," + bon1s.get(i).ancien_solde + "," + bon1s.get(i).latitude + "," + bon1s.get(i).longitude + ", 'TERMINAL_MOBILE');\n";


            String querry_select = "SELECT " +
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
                    "WHERE BON2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


            bon2s = controller.select_bon2_from_database(querry_select);

            String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

            for (int j = 0; j < bon2s.size(); j++) {

                ///////////////////////////////////PRODUIT ///////////////////////////////////////
                new PostData_Produit();
                PostData_Produit postData_produit;
                String querry_isnew = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                postData_produit = controller.select_one_produit_from_database(querry_isnew);
                if (postData_produit != null) {
                    if (postData_produit.isNew == 1) {
                        F_SQL = F_SQL + "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PA_HT, PRODUIT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", PV4_HT, PV5_HT, PV6_HT ";
                        }
                        F_SQL = F_SQL + ") VALUES ('" + postData_produit.code_barre + "', '" + postData_produit.ref_produit + "', '" + postData_produit.pa_ht + "', '" + postData_produit.produit + "', '" + postData_produit.tva + "',  '" + postData_produit.stock + "', '" + postData_produit.colissage + "',  '" + postData_produit.pv1_ht + "', '" + postData_produit.pv2_ht + "', '" + postData_produit.pv3_ht + "'";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", '" + postData_produit.pv4_ht + "', '" + postData_produit.pv5_ht + "', '" + postData_produit.pv6_ht + "'";
                        }
                        F_SQL = F_SQL + ") MATCHING (CODE_BARRE);\n";
                    }
                }
                ///////////////////////////////////PRODUIT ///////////////////////////////////////


                ///////////////////////////////////CODE BARRE //////////////////////////////////////
                /*ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

                String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN " +
                        "FROM CODEBARRE WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                codebarres = controller.select_all_codebarre_from_database(querry_codebarre);

                for(int k = 0;k<codebarres.size(); k++){
                    //insert into codebarre
                    F_SQL = F_SQL + "UPDATE OR INSERT INTO CODEBARRE (CODE_BARRE, CODE_BARRE_SYN) VALUES (";
                    F_SQL = F_SQL + " '" + codebarres.get(k).code_barre + "' , '" + codebarres.get(k).code_barre_syn + "' ";
                    F_SQL = F_SQL + ") MATCHING (CODE_BARRE);";
                }*/

                ///////////////////////////////////CODE BARRE //////////////////////////////////////

                ///////////////////////////////////BON 2 ///////////////////////////////////////
                F_SQL = F_SQL + "INSERT INTO BON2 (CODE_DEPOT,RECORDID,NUM_BON,";
                F_SQL = F_SQL + "CODE_BARRE,PRODUIT,DESTOCK_TYPE,DESTOCK_CODE_BARRE,DESTOCK_QTE,";
                F_SQL = F_SQL + "NBRE_COLIS,COLISSAGE,QTE,QTE_GRAT,";
                F_SQL = F_SQL + "TVA,PV_HT_AR,PV_HT,PA_HT)\n";
                F_SQL = F_SQL + "VALUES\n";
                F_SQL = F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_BON2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL = F_SQL + "'" + bon2s.get(j).codebarre.replace("'", "''") + "','" + bon2s.get(j).produit.replace("'", "''") + "', iif('" + bon2s.get(j).destock_type + "' = 'null', null,'" + bon2s.get(j).destock_type + "') , iif('" + bon2s.get(j).destock_code_barre + "' = 'null', null,'" + bon2s.get(j).destock_code_barre + "') , iif('" + bon2s.get(j).destock_qte + "' = '0.0', null,'" + bon2s.get(j).destock_qte + "'),";
                F_SQL = F_SQL + bon2s.get(j).nbr_colis + "," + bon2s.get(j).colissage + "," + bon2s.get(j).qte + "," + bon2s.get(j).gratuit + ",";
                F_SQL = F_SQL + bon2s.get(j).tva + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pa_ht + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + "ACHATS,VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','BL-VENTE','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "',";
            F_SQL = F_SQL + bon1s.get(i).montant_bon + "," + bon1s.get(i).verser + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (bon1s.get(i).verser != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'BL-VENTE',lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000')," + bon1s.get(i).verser + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "');\n";
            }

            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "VENTE_" + bon1s.get(i).num_bon + "_" + bon1s.get(i).exportation + "_" + bon1s.get(i).date_bon + ".BLV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_VENTE.put(file_name, F_SQL);
        }


        //////////////////////////////////////// SITUATION /////////////////////////////////////////////////
        F_SQL = "";
        all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                "CARNET_C.CODE_CLIENT, " +
                "CARNET_C.DATE_CARNET, " +

                "CLIENT.CLIENT, " +
                "CLIENT.LATITUDE, " +
                "CLIENT.LONGITUDE, " +
                "CLIENT.TEL, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "CLIENT.RC, " +
                "CLIENT.IFISCAL, " +
                "CLIENT.AI, " +
                "CLIENT.NIS, " +
                "CLIENT.MODE_TARIF, " +

                "CARNET_C.HEURE, " +
                "CARNET_C.ACHATS, " +
                "CARNET_C.VERSEMENTS, " +
                "CARNET_C.SOURCE, " +
                "CARNET_C.NUM_BON, " +
                "CARNET_C.MODE_RG, " +
                "CARNET_C.REMARQUES, " +
                "CARNET_C.UTILISATEUR, " +
                "CARNET_C.EXPORTATION, " +
                "CARNET_C.IS_EXPORTED " +


                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON " +
                "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");

        for (int i = 0; i < all_versement_client.size(); i++) {

            Date dt = format.parse(all_versement_client.get(i).carnet_date);
            assert dt != null;

            F_SQL = "SITUATION-CLIENT|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE , CODE_VENDEUR ";

            try {
                F_SQL = F_SQL + ")\n VALUES ( " +
                        " iif('" + code_depot + "' = '000000', null, '" + code_depot + "') , " +
                        " '" + all_versement_client.get(i).code_client + "'," +
                        " iif('" + all_versement_client.get(i).client + "' = null , null, '" + all_versement_client.get(i).client.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).adresse + "' = null , null, '" + all_versement_client.get(i).adresse.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).wilaya + "' = null , null, '" + all_versement_client.get(i).wilaya.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).commune + "' = null , null, '" + all_versement_client.get(i).commune.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).tel + "' = null, null , '" + all_versement_client.get(i).tel.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).rc + "' = 'null', null , '" + all_versement_client.get(i).rc + "') , " +
                        " iif('" + all_versement_client.get(i).ifiscal + "' = null , null, '" + all_versement_client.get(i).ifiscal + "') , " +
                        " iif('" + all_versement_client.get(i).nis + "' = null, null , '" + all_versement_client.get(i).nis + "') , " +
                        " iif('" + all_versement_client.get(i).ai + "' = null, null , '" + all_versement_client.get(i).ai + "') , " +
                        " " + all_versement_client.get(i).latitude + ", " +
                        " " + all_versement_client.get(i).longitude + " ";
            } catch (Exception e) {
                Log.v("ERROR", e.getMessage());
                int x = 0;
            }


            F_SQL = F_SQL + ",iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "') ";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";


            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + " VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + all_versement_client.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + all_versement_client.get(i).carnet_heure + "','SITUATION-CLIENT','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "',";
            F_SQL = F_SQL + "'" + all_versement_client.get(i).carnet_versement + "');\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////

            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (all_versement_client.get(i).carnet_versement != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'SITUATION-CLIENT',lpad ((SELECT GEN_ID(GEN_CARNET_C_ID,0) FROM RDB$DATABASE) ,6,'000000')," + all_versement_client.get(i).carnet_versement + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "');\n";
            }

            file_name = "SITUATION_VRC" + all_versement_client.get(i).recordid + "_" + all_versement_client.get(i).exportation + "_" + all_versement_client.get(i).carnet_date + ".STC";
            file_name = file_name.replace("/", "_");
            F_SQL_LIST_SITUATION.put(file_name, F_SQL);

        }

    }


    // create and prepare "orders" local file to export via FTP

    public void prepare_local_file_order() throws ParseException {

        F_SQL_LIST_COMMAND = new HashMap<>();
        F_SQL_LIST_SITUATION = new HashMap<>();
        String F_SQL = "";

        String querry = "SELECT " +
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
                "coalesce(CLIENT.RC, '') RC, " +
                "coalesce(CLIENT.IFISCAL, '') IFISCAL, " +
                "coalesce(CLIENT.AI, '') AI, " +
                "coalesce(CLIENT.NIS, '') NIS, " +

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
                "WHERE BLOCAGE = 'F' ORDER BY BON1_TEMP.NUM_BON";

        bon1s_Temp = controller.select_all_bon1_from_database(querry);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd");

        for (int i = 0; i < bon1s_Temp.size(); i++) {

            Date dt = format.parse(bon1s_Temp.get(i).date_bon);
            assert dt != null;

            F_SQL = "COMMANDE|" + bon1s_Temp.get(i).nbr_p + "|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE, CODE_VENDEUR ";

            F_SQL = F_SQL + ")\n VALUES ( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') ," +
                    " '" + bon1s_Temp.get(i).code_client.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).client.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).adresse.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).wilaya.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).commune.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).tel.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).rc.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).ifiscal.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).nis.replace("'", "''") + "'," +
                    " '" + bon1s_Temp.get(i).ai.replace("'", "''") + "', " +
                    " " + bon1s_Temp.get(i).latitude_client + ", " + bon1s_Temp.get(i).longitude_client;

            F_SQL = F_SQL + ", iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";

            ///////////////////////////////////// BON1 ////////////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO BCC1 (RECORDID,NUM_BON,EXPORTATION,CODE_DEPOT, CODE_VENDEUR, " +
                    "CODE_CLIENT,DATE_BON, HEURE,MODE_RG,BLOCAGE,MODE_TARIF," +
                    "VERSER, TIMBRE, REMISE, ANCIEN_SOLDE, LATITUDE, LONGITUDE, UTILISATEUR)\n ";


            F_SQL = F_SQL + " VALUES ( (SELECT GEN_ID(GEN_BCC1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BCC1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:,iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'), ";
            F_SQL = F_SQL + "'" + bon1s_Temp.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s_Temp.get(i).heure + "','" + bon1s_Temp.get(i).mode_rg + "','" + bon1s_Temp.get(i).blocage + "','" + bon1s_Temp.get(i).mode_tarif + "',";
            F_SQL = F_SQL + bon1s_Temp.get(i).verser + "," + bon1s_Temp.get(i).timbre + "," + bon1s_Temp.get(i).remise + "," + bon1s_Temp.get(i).ancien_solde + "," + bon1s_Temp.get(i).latitude + "," + bon1s_Temp.get(i).longitude + ", 'TERMINAL_MOBILE');\n";


            String querry_select = "SELECT " +
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
                    "WHERE BON2_TEMP.NUM_BON = '" + bon1s_Temp.get(i).num_bon + "'";


            bon2s_Temp = controller.select_bon2_from_database(querry_select);

            String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

            for (int j = 0; j < bon2s_Temp.size(); j++) {

                ///////////////////////////////////PRODUIT /////////////////////////////////////////
                new PostData_Produit();
                PostData_Produit postData_produit;
                String querry_isnew = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + bon2s_Temp.get(j).codebarre + "'";

                postData_produit = controller.select_one_produit_from_database(querry_isnew);
                if (postData_produit != null) {
                    if (postData_produit.isNew == 1) {
                        F_SQL = F_SQL + "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PA_HT, PRODUIT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", PV4_HT, PV5_HT, PV6_HT ";
                        }
                        F_SQL = F_SQL + " ) VALUES ('" + postData_produit.code_barre + "', '" + postData_produit.ref_produit + "', '" + postData_produit.pa_ht + "', '" + postData_produit.produit + "', '" + postData_produit.tva + "',  '" + postData_produit.stock + "', '" + postData_produit.colissage + "',  '" + postData_produit.pv1_ht + "', '" + postData_produit.pv2_ht + "', '" + postData_produit.pv3_ht + "'";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", '" + postData_produit.pv4_ht + "', '" + postData_produit.pv5_ht + "', '" + postData_produit.pv6_ht + "'";
                        }
                        F_SQL = F_SQL + ") MATCHING (CODE_BARRE);\n";
                    }
                }
                ///////////////////////////////////PRODUIT /////////////////////////////////////////


                ///////////////////////////////////CODE BARRE //////////////////////////////////////
                /*ArrayList<PostData_Codebarre> codebarres;

                String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN FROM CODEBARRE WHERE CODE_BARRE = '" + bon2s_Temp.get(j).codebarre + "'";

                codebarres = controller.select_all_codebarre_from_database(querry_codebarre);

                for(int k = 0;k<codebarres.size(); k++){
                    //insert into codebarre
                    F_SQL = F_SQL + "UPDATE OR INSERT INTO CODEBARRE (CODE_BARRE, CODE_BARRE_SYN) VALUES (";
                    F_SQL = F_SQL + " '" + codebarres.get(k).code_barre + "' , '" + codebarres.get(k).code_barre_syn + "' ";
                    F_SQL = F_SQL + ") MATCHING (CODE_BARRE);";
                }*/

                ///////////////////////////////////CODE BARRE //////////////////////////////////////

                ///////////////////////////////////BON 2 ///////////////////////////////////////
                F_SQL = F_SQL + "INSERT INTO BCC2 (CODE_DEPOT,RECORDID,NUM_BON,";
                F_SQL = F_SQL + "CODE_BARRE,PRODUIT,DESTOCK_TYPE,DESTOCK_CODE_BARRE,DESTOCK_QTE,";
                F_SQL = F_SQL + "NBRE_COLIS,COLISSAGE,QTE,QTE_GRAT,";
                F_SQL = F_SQL + "TVA,PV_HT_AR,PV_HT,PA_HT)\n";
                F_SQL = F_SQL + "VALUES\n";
                F_SQL = F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_BCC2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BCC1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL = F_SQL + "'" + bon2s_Temp.get(j).codebarre.replace("'", "''") + "','" + bon2s_Temp.get(j).produit.replace("'", "''") + "', iif('" + bon2s_Temp.get(j).destock_type + "' = 'null', null,'" + bon2s_Temp.get(j).destock_type + "') , iif('" + bon2s_Temp.get(j).destock_code_barre + "' = 'null', null,'" + bon2s_Temp.get(j).destock_code_barre + "') ,'" + bon2s_Temp.get(j).destock_qte + "',";
                F_SQL = F_SQL + bon2s_Temp.get(j).nbr_colis + "," + bon2s_Temp.get(j).colissage + "," + bon2s_Temp.get(j).qte + "," + bon2s_Temp.get(j).gratuit + ",";
                F_SQL = F_SQL + bon2s_Temp.get(j).tva + "," + bon2s_Temp.get(j).pv_ht + "," + bon2s_Temp.get(j).pv_ht + "," + bon2s_Temp.get(j).pa_ht + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }


            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "COMMANDE_" + bon1s_Temp.get(i).num_bon + "_" + bon1s_Temp.get(i).exportation + "_" + bon1s_Temp.get(i).date_bon + ".BLV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_COMMAND.put(file_name, F_SQL);
        }


        //////////////////////////////////////// SITUATION /////////////////////////////////////////////////
        F_SQL = "";
        all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                "CARNET_C.CODE_CLIENT, " +
                "CARNET_C.DATE_CARNET, " +

                "CLIENT.CLIENT, " +
                "CLIENT.LATITUDE, " +
                "CLIENT.LONGITUDE, " +
                "CLIENT.TEL, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "CLIENT.RC, " +
                "CLIENT.IFISCAL, " +
                "CLIENT.AI, " +
                "CLIENT.NIS, " +
                "CLIENT.MODE_TARIF, " +

                "CARNET_C.HEURE, " +
                "CARNET_C.ACHATS, " +
                "CARNET_C.VERSEMENTS, " +
                "CARNET_C.SOURCE, " +
                "CARNET_C.NUM_BON, " +
                "CARNET_C.MODE_RG, " +
                "CARNET_C.REMARQUES, " +
                "CARNET_C.UTILISATEUR, " +
                "CARNET_C.EXPORTATION, " +
                "CARNET_C.IS_EXPORTED " +


                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON " +
                "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");

        for (int i = 0; i < all_versement_client.size(); i++) {

            Date dt = format.parse(all_versement_client.get(i).carnet_date);
            assert dt != null;

            F_SQL = "SITUATION-CLIENT|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE , CODE_VENDEUR ";

            F_SQL = F_SQL + ")\n VALUES ( " +
                    " iif('" + code_depot + "' = '000000', null, '" + code_depot + "') , " +
                    " '" + all_versement_client.get(i).code_client + "'," +
                    " iif('" + all_versement_client.get(i).client + "' = null , null, '" + all_versement_client.get(i).client.replace("'", "''") + "') , " +
                    " iif('" + all_versement_client.get(i).adresse + "' = null , null, '" + all_versement_client.get(i).adresse.replace("'", "''") + "') , " +
                    " iif('" + all_versement_client.get(i).wilaya + "' = null , null, '" + all_versement_client.get(i).wilaya.replace("'", "''") + "') , " +
                    " iif('" + all_versement_client.get(i).commune + "' = null , null, '" + all_versement_client.get(i).commune.replace("'", "''") + "') , " +
                    " iif('" + all_versement_client.get(i).tel + "' = null, null , '" + all_versement_client.get(i).tel.replace("'", "''") + "') , " +
                    " iif('" + all_versement_client.get(i).rc + "' = 'null', null , '" + all_versement_client.get(i).rc + "') , " +
                    " iif('" + all_versement_client.get(i).ifiscal + "' = null , null, '" + all_versement_client.get(i).ifiscal + "') , " +
                    " iif('" + all_versement_client.get(i).nis + "' = null, null , '" + all_versement_client.get(i).nis + "') , " +
                    " iif('" + all_versement_client.get(i).ai + "' = null, null , '" + all_versement_client.get(i).ai + "') , " +
                    " " + all_versement_client.get(i).latitude + ", " +
                    " " + all_versement_client.get(i).longitude + " ";

            F_SQL = F_SQL + ",iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";


            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + " VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BCC1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + all_versement_client.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + all_versement_client.get(i).carnet_heure + "','SITUATION-CLIENT','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "',";
            F_SQL = F_SQL + " " + all_versement_client.get(i).carnet_versement + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////

            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (all_versement_client.get(i).carnet_versement != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'SITUATION-CLIENT',lpad ((SELECT GEN_ID(GEN_CARNET_C_ID,0) FROM RDB$DATABASE) ,6,'000000')," + all_versement_client.get(i).carnet_versement + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "');\n";
            }

            file_name = "SITUATION_VRC" + all_versement_client.get(i).recordid + "_" + all_versement_client.get(i).exportation + "_" + all_versement_client.get(i).carnet_date + ".STC";
            file_name = file_name.replace("/", "_");
            F_SQL_LIST_SITUATION.put(file_name, F_SQL);

        }

    }


    public void prepare_local_file_inventaire() throws ParseException {

        F_SQL_LIST_INVENTAIRE = new HashMap<>();
        String F_SQL = "";

        String querry = "SELECT " +
                "INV1.NUM_INV, " +
                "INV1.DATE_INV, " +
                "INV1.HEURE_INV, " +
                "INV1.LIBELLE, " +
                "INV1.NBR_PRODUIT, " +

                "INV1.UTILISATEUR, " +
                "INV1.CODE_DEPOT, " +
                "INV1.EXPORTATION, " +
                "INV1.IS_EXPORTED, " +
                "INV1.DATE_EXPORT_INV, " +
                "INV1.BLOCAGE " +
                "FROM INV1 " +
                "WHERE BLOCAGE = 'F' ORDER BY INV1.NUM_INV";

        try {
            Invs1 = controller.select_list_inventaire_from_database(querry);
        } catch (Exception e) {
            Log.v("zzz", e.getMessage());
        }


        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd");


        for (int i = 0; i < Invs1.size(); i++) {

            Date dt = format.parse(Invs1.get(i).date_inv);
            assert dt != null;

            F_SQL = "INVENTAIRE|" + Invs1.get(i).nbr_produit + "|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";

            ///////////////////////////////////// INV1 ////////////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO INV1 (RECORDID,NUM_INV,EXPORTATION,CODE_DEPOT, " +
                    "DATE_INV, HEURE, LIBELLE, NBR_PRODUIT, BLOCAGE, UTILISATEUR)\n ";

            F_SQL = F_SQL + " VALUES ( (SELECT GEN_ID(GEN_INV1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_INV1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:, iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), ";
            F_SQL = F_SQL + "'" + format2.format(dt) + "','" + Invs1.get(i).heure_inv + "', '" + Invs1.get(i).nom_inv + "', " + Invs1.get(i).nbr_produit + ", 'M',";
            F_SQL = F_SQL + "'TERMINAL_MOBILE');\n";


            String querry_select = "SELECT " +
                    "INV2.RECORDID, " +
                    "INV2.CODE_BARRE, " +
                    "INV2.NUM_INV, " +
                    "INV2.PRODUIT, " +
                    "INV2.NBRE_COLIS, " +
                    "INV2.COLISSAGE, " +
                    "INV2.PA_HT, " +
                    "INV2.QTE, " +
                    "INV2.QTE_TMP, " +
                    "INV2.QTE_NEW, " +
                    "INV2.TVA, " +
                    "INV2.VRAC, " +
                    "INV2.CODE_DEPOT " +
                    "FROM INV2 " +
                    "WHERE INV2.NUM_INV = '" + Invs1.get(i).num_inv + "'";

            Inv2s = controller.select_inventaire2_from_database(querry_select);

            String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

            for (int j = 0; j < Inv2s.size(); j++) {

                ///////////////////////////////////PRODUIT /////////////////////////////////////////
                new PostData_Produit();
                PostData_Produit postData_produit;
                String querry_isnew = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + Inv2s.get(j).codebarre + "'";

                postData_produit = controller.select_one_produit_from_database(querry_isnew);

                if (postData_produit != null) {
                    if (postData_produit.isNew == 1) {
                        F_SQL = F_SQL + "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PA_HT, PRODUIT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", PV4_HT, PV5_HT, PV6_HT ";
                        }
                        F_SQL = F_SQL + ") VALUES ('" + postData_produit.code_barre + "', '" + postData_produit.ref_produit + "', '" + postData_produit.pa_ht + "', '" + postData_produit.produit + "', '" + postData_produit.tva + "',  '" + postData_produit.stock + "', '" + postData_produit.colissage + "',  '" + postData_produit.pv1_ht + "', '" + postData_produit.pv2_ht + "', '" + postData_produit.pv3_ht + "'";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", '" + postData_produit.pv4_ht + "', '" + postData_produit.pv5_ht + "', '" + postData_produit.pv6_ht + "'";
                        }
                        F_SQL = F_SQL + ") MATCHING (CODE_BARRE);\n";
                    }
                }
                /////////////////////////////////// PRODUIT ////////////////////////////////////////

                ///////////////////////////////////CODE BARRE //////////////////////////////////////
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

                String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN " +
                        "FROM CODEBARRE WHERE CODE_BARRE = '" + Inv2s.get(j).codebarre + "'";

                codebarres = controller.select_all_codebarre_from_database(querry_codebarre);

                for(int k = 0;k<codebarres.size(); k++){
                    //insert into codebarre
                    F_SQL = F_SQL + "UPDATE OR INSERT INTO CODEBARRE (CODE_BARRE, CODE_BARRE_SYN) VALUES (";
                    F_SQL = F_SQL + " '" + codebarres.get(k).code_barre + "' , '" + codebarres.get(k).code_barre_syn + "' ";
                    F_SQL = F_SQL + ") MATCHING (CODE_BARRE);";
                }

                ///////////////////////////////////CODE BARRE //////////////////////////////////////

                ///////////////////////////////////INV 2 ///////////////////////////////////////////
                F_SQL = F_SQL + "INSERT INTO INV2 (CODE_DEPOT,RECORDID,NUM_INV,";
                F_SQL = F_SQL + "CODE_BARRE, PA_HT,";
                F_SQL = F_SQL + "QTE, QTE_TMP,";
                F_SQL = F_SQL + "TVA)\n";
                F_SQL = F_SQL + "VALUES\n";
                F_SQL = F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_INV2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_INV1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL = F_SQL + "'" + Inv2s.get(j).codebarre.replace("'", "''") + "',";
                F_SQL = F_SQL + Inv2s.get(j).pa_ht + "," + Inv2s.get(j).qte_theorique + ",";
                F_SQL = F_SQL + Inv2s.get(j).qte_physique + "," + Inv2s.get(j).tva + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }


            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "INVENTAIRE_" + Invs1.get(i).num_inv + "_" + Invs1.get(i).exportation + "_" + Invs1.get(i).date_inv + ".INV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_INVENTAIRE.put(file_name, F_SQL);
        }

    }

    public boolean create_file(String file_name, String F_SQL) {
        try {
            if (file_name != null) {
                FileOutputStream fOut = mActivity.openFileOutput(file_name, MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(F_SQL);
                osw.flush();
                osw.close();
                return true;
            } else {
                return false;
            }

        } catch (Exception ex) {
            return false;
        }
    }


    public boolean connectFtp() {
        try {
            con = new FTPClient();
            con.setDefaultPort(Integer.parseInt(serverPort));
            con.setConnectTimeout(5000);
            con.connect(serverIp);
            con.login(username, password);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public void disconnectFtp() {
        try {
            con.logout();
            con.disconnect();
        } catch (Exception e) {

        }
    }


    // AsynchTAsk Achats

    private final class LongOperationAchat extends AsyncTask<Void, Void, Integer> {

        //ProgressBar mProgressBar;
        ProgressDialog mProgressDialog;

        public LongOperationAchat() {

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;


            try {
                if (connectFtp()) {
                    prepare_local_file_achat();
                    int count_vente = 1;
                    int count_situation = 1;
                    for (Map.Entry map : F_SQL_LIST_VENTE.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_vente = count_vente;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des BA ..." + finalCount_vente + "/" + F_SQL_LIST_VENTE.size());
                                }
                            });

                            s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/ACHAT", mProgressDialog);
                            /*try {
                                if( map.getKey().toString().endsWith(".BLV")){
                                    String num_bon = map.getKey().toString().substring(6, 12);
                                    controller.update_ventes_commandes_as_exported(false, num_bon);
                                }
                            }catch (Exception e){

                            }*/
                        } else {
                            return 1;
                        }
                        count_vente++;
                    }

                    for (Map.Entry map : F_SQL_LIST_SITUATION.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_situation = count_situation;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des STC ..." + finalCount_situation + "/" + F_SQL_LIST_SITUATION.size());
                                }
                            });
                            s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/SITUATION-CLIENT", mProgressDialog);

                        } else {
                            return 1;
                        }
                        count_situation++;
                    }

                } else {
                    return 2;
                }
                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation ftp terminé. \n Total bon Vente : " + F_SQL_LIST_VENTE.size() + "\n Total versements : " + F_SQL_LIST_SITUATION.size())
                        .show();
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            }
        }
    }


    // AsynchTAsk Sales

    private final class LongOperationSale extends AsyncTask<Void, Void, Integer> {

        //ProgressBar mProgressBar;
        ProgressDialog mProgressDialog;

        public LongOperationSale() {

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;


            try {
                if (connectFtp()) {
                    prepare_local_file_sale();
                    int count_vente = 1;
                    int count_situation = 1;
                    for (Map.Entry map : F_SQL_LIST_VENTE.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_vente = count_vente;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des BL ..." + finalCount_vente + "/" + F_SQL_LIST_VENTE.size());
                                }
                            });

                            s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/VENTE", mProgressDialog);
                            try {
                                if (map.getKey().toString().endsWith(".BLV")) {
                                    String num_bon = map.getKey().toString().substring(6, 12);
                                    controller.update_ventes_commandes_as_exported(false, num_bon);
                                }
                            } catch (Exception e) {

                            }
                        } else {
                            return 1;
                        }
                        count_vente++;
                    }

                    for (Map.Entry map : F_SQL_LIST_SITUATION.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_situation = count_situation;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des STC ..." + finalCount_situation + "/" + F_SQL_LIST_SITUATION.size());
                                }
                            });
                            s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/SITUATION-CLIENT", mProgressDialog);

                            try {
                                if (map.getKey().toString().endsWith(".STC")) {
                                    String str1 = map.getKey().toString().substring(10);
                                    int index1_ = str1.indexOf("_");
                                    String str2 = str1.substring(0, index1_);
                                    String recordid = str2.substring(3);
                                    controller.update_versement_exported(recordid);
                                }
                            } catch (Exception e) {
                                Log.v("TAG", e.getMessage());
                            }
                        } else {
                            return 1;
                        }
                        count_situation++;
                    }

                } else {
                    return 2;
                }
                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation ftp terminé. \n Total bon Vente : " + F_SQL_LIST_VENTE.size() + "\n Total versements : " + F_SQL_LIST_SITUATION.size())
                        .show();
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            }
        }
    }


    // AsynchTAsk Orders

    private final class LongOperationOrder extends AsyncTask<Void, Void, Integer> {

        ProgressDialog mProgressDialog;

        public LongOperationOrder() {

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;


            try {
                if (connectFtp()) {
                    prepare_local_file_order();
                    int count_commande = 1;
                    int count_situation = 1;
                    for (Map.Entry map : F_SQL_LIST_COMMAND.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_commande = count_commande;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des commandes ..." + finalCount_commande + "/" + F_SQL_LIST_COMMAND.size());
                                }
                            });
                            if (code_depot.equals("000000") || code_depot.equals("")) {
                                if (code_vendeur.equals("000000") || code_vendeur.equals("")) {
                                    // nothing to do
                                } else {
                                    s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_vendeur, "/COMMANDE", mProgressDialog);
                                    try {
                                        if (map.getKey().toString().endsWith(".BLV")) {
                                            String num_bon = map.getKey().toString().substring(9, 15);
                                            controller.update_ventes_commandes_as_exported(true, num_bon);
                                        }
                                    } catch (Exception e) {

                                    }
                                }
                            } else {
                                s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/COMMANDE", mProgressDialog);
                                try {
                                    if (map.getKey().toString().endsWith(".BLV")) {
                                        String num_bon = map.getKey().toString().substring(9, 15);
                                        controller.update_ventes_commandes_as_exported(true, num_bon);
                                    }
                                } catch (Exception e) {

                                }
                            }


                        } else {
                            return 1;
                        }
                        count_commande++;
                    }

                    for (Map.Entry map : F_SQL_LIST_SITUATION.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_situation = count_situation;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation des STC ..." + finalCount_situation + "/" + F_SQL_LIST_SITUATION.size());
                                }
                            });
                            if (code_depot.equals("000000") || code_depot.equals("")) {
                                if (code_vendeur.equals("000000") || code_vendeur.equals("")) {
                                    // maydir walo
                                } else {
                                    s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_vendeur, "/SITUATION-CLIENT", mProgressDialog);

                                    try {
                                        if (map.getKey().toString().endsWith(".STC")) {
                                            String str1 = map.getKey().toString().substring(10);
                                            int index1_ = str1.indexOf("_");
                                            String str2 = str1.substring(0, index1_);
                                            String recordid = str2.substring(3);
                                            controller.update_versement_exported(recordid);
                                        }
                                    } catch (Exception e) {
                                        Log.v("TAG", e.getMessage());
                                    }
                                }
                            } else {
                                s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/SITUATION-CLIENT", mProgressDialog);
                                try {
                                    if (map.getKey().toString().endsWith(".STC")) {
                                        String str1 = map.getKey().toString().substring(10);
                                        int index1_ = str1.indexOf("_");
                                        String str2 = str1.substring(0, index1_);
                                        String recordid = str2.substring(3);
                                        controller.update_versement_exported(recordid);
                                    }
                                } catch (Exception e) {
                                    Log.v("TAG", e.getMessage());
                                }
                            }


                        } else {
                            return 1;
                        }
                        count_situation++;
                    }

                } else {
                    return 2;
                }

                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation ftp terminé. \n Total bons commandes : " + F_SQL_LIST_COMMAND.size() + "\n Total versements : " + F_SQL_LIST_SITUATION.size())
                        .show();
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            }
        }
    }


    private final class LongOperationInventaire extends AsyncTask<Void, Void, Integer> {

        ProgressDialog mProgressDialog;

        public LongOperationInventaire() {

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;


            try {
                if (connectFtp()) {
                    prepare_local_file_inventaire();
                    int count_inventaire = 1;
                    for (Map.Entry map : F_SQL_LIST_INVENTAIRE.entrySet()) {

                        if (create_file(map.getKey().toString(), map.getValue().toString())) {
                            int finalCount_inventaire = count_inventaire;
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.setMessage("Exportation inventaires ..." + finalCount_inventaire + "/" + F_SQL_LIST_INVENTAIRE.size());
                                }
                            });

                            if (code_depot.equals("000000") || code_depot.equals("")) {
                                s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, "", "/INVENTAIRE", mProgressDialog);
                            } else {
                                s = new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/INVENTAIRE", mProgressDialog);
                            }


                        } else {
                            return 1;
                        }
                        count_inventaire++;
                    }

                } else {
                    return 2;
                }

                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation ftp terminé. \n Total bons inventaires : " + F_SQL_LIST_INVENTAIRE.size())
                        .show();
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            }
        }
    }

    private final class LongOperationGetListBon extends AsyncTask<Void, Void, Integer> {

        ProgressDialog mProgressDialog;
        ArrayList<String> listFile;

        public LongOperationGetListBon() {

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;
            try {
                if (connectFtp()) {
                    listFile = new GetListFromFtp().ftpGestList1(con, nom_depot, mProgressDialog);
                } else {
                    return 2;
                }
                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                FragmentSelectBonTransfer fragmentSelectBon = new FragmentSelectBonTransfer();
                fragmentSelectBon.showDialogbox(mActivity, mActivity.getBaseContext(), listFile);
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            }
        }
    }

    private final class LongOperationImportationProduits extends AsyncTask<Void, Void, Integer> {

        ProgressDialog mProgressDialog;
        String nom_bon;

        public LongOperationImportationProduits(String nom_bon) {

            this.nom_bon = nom_bon;

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage("Connexion serveur FTP ...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int s = 0;


            try {
                if (connectFtp()) {
                    s = new DownloadFileFromFtp().ftpDownload1(mActivity, con, nom_depot, mProgressDialog, nom_bon);
                } else {
                    return 2;
                }
                disconnectFtp();

            } catch (Exception e) {
                e.printStackTrace();
                disconnectFtp();
                return 2;
            }
            return s;
        }


        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if (result == 0) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Le bon de transfert a été bien traité !")
                        .show();
            } else if (result == 1) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Problème au moment d'importation du fichier depuis le serveur FTP")
                        .show();
            } else if (result == 2) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Problem de connexion serveur ftp")
                        .show();
            } else if (result == 3) {
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Importation...")
                        .setContentText("Problem au moment de traitement du fichier !")
                        .show();
            }
        }
    }
}


class UploadToFtp {

    CopyStreamAdapter streamListener;
    ProgressDialog pDialog;
    int status = 0;

    //https://stackoverflow.com/questions/21923833/how-to-show-the-progress-bar-in-ftp-download-in-async-class-in-android

    public int ftpUpload1(Context context, String file_name, String ftp_imp_def, FTPClient mFTPClient, String nom_depot, String vente_situation, final ProgressDialog pDialog) {

        this.pDialog = pDialog;

        try {

            mFTPClient.makeDirectory(ftp_imp_def);
            ftp_imp_def = ftp_imp_def + "/" + nom_depot;
            mFTPClient.makeDirectory(ftp_imp_def);
            ftp_imp_def = ftp_imp_def + vente_situation;
            mFTPClient.makeDirectory(ftp_imp_def);
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream buffIn = null;
            File file = new File(context.getFileStreamPath(file_name) + "");
            buffIn = new BufferedInputStream(new FileInputStream(file), 8192);

            mFTPClient.enterLocalPassiveMode();
            streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                    long percent = bytesTransferred * 100L / file.length();
                    pDialog.setProgress((int) percent);

                    if (totalBytesTransferred == file.length()) {
                        removeCopyStreamListener(streamListener);
                    }
                }

            };

            mFTPClient.setCopyStreamListener(streamListener);
            boolean result = mFTPClient.storeFile("/" + ftp_imp_def + "/" + file_name, buffIn);
            buffIn.close();
            status = 0;

        } catch (Exception e) {
            e.printStackTrace();
            status = 2;
        }

        return status;
    }
}


class GetListFromFtp {

    CopyStreamAdapter streamListener;
    ProgressDialog pDialog;
    ArrayList<String> listFile;

    //https://stackoverflow.com/questions/21923833/how-to-show-the-progress-bar-in-ftp-download-in-async-class-in-android

    public ArrayList<String> ftpGestList1(FTPClient mFTPClient, String nom_depot, final ProgressDialog pDialog) throws IOException {

        this.pDialog = pDialog;

        try {

            boolean changedRemoteDir = mFTPClient.changeWorkingDirectory("/EXP/TRANSFERT/" + nom_depot);
            if (!changedRemoteDir) {
                return null;
            }
            // list all the files which will be downloaded.
            FTPFile[] ftpFiles = mFTPClient.listFiles();
            listFile = new ArrayList<>();
            for (FTPFile file : ftpFiles) {
                if (file.getName().endsWith(".BDR")) {
                    listFile.add(file.getName());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (mFTPClient != null) {
                mFTPClient.logout();
                mFTPClient.disconnect();
            }
        }

        return listFile;
    }
}


class DownloadFileFromFtp {

    ProgressDialog pDialog;

    public int ftpDownload1(Activity activity, FTPClient mFTPClient, String nom_depot, final ProgressDialog pDialog, String nom_bon) throws IOException {

        this.pDialog = pDialog;

        try {

            boolean changedRemoteDir = mFTPClient.changeWorkingDirectory("/EXP/TRANSFERT/" + nom_depot);
            if (!changedRemoteDir) {
                return 1;
            }
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
            mFTPClient.enterLocalPassiveMode();
            boolean success = false;
            try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(Paths.get(activity.getApplicationInfo().dataDir + "/" + nom_bon).toFile().toPath()))) {
                success = mFTPClient.retrieveFile(nom_bon, outputStream);
            }

            //Start reading the file
            FileInputStream is;
            BufferedReader reader;
            final File file = new File(activity.getApplicationInfo().dataDir + "/" + nom_bon);

            if (file.exists()) {
                is = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(is));
                String line = reader.readLine();
                while (line != null) {
                    Log.d("StackOverflow", line);
                    line = reader.readLine();
                }
            } else {
                return 3;
            }

            return 0;

        } catch (Exception e) {
            return 1;
        }
    }
}
package com.safesoft.proapp.distribute.ftp;


import static android.content.Context.MODE_PRIVATE;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class Ftp_export {

    private Activity mActivity;
    SharedPreferences prefs;
    private List<PostData_Bon1> bon1s;
    private List<PostData_Bon2> bon2s;
    private ArrayList<PostData_Carnet_c> all_versement_client;
    private DATABASE controller;
    private final String PREFS = "ALL_PREFS";
    String serverIp,serverPort,username, password, ftp_imp,ftp_imp_def, ftp_exp, code_depot, nom_depot, code_vendeur, nom_vendeur;
    Map<String, String> F_SQL_LIST_VENT;
    Map<String, String> F_SQL_LIST_SITUATION;
    String file_name = null;
    FTPClient con = null;

    public void start(Activity activity) throws ParseException {

        mActivity = activity;

        controller =  new DATABASE(mActivity);
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        //config
        serverIp = Objects.requireNonNull(prefs.getString("SERVEUR_FTP", "")).trim();
        serverPort = prefs.getString("PORT_FTP", "21").trim();

        username = prefs.getString("USER_FTP", "").trim();
        password = prefs.getString("PASSWORD_FTP", "").trim();

        ftp_exp = prefs.getString("IMP_FTP", "EXP").trim();
        ftp_imp_def = prefs.getString("EXP_FTP", "IMP").trim();

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        nom_depot = prefs.getString("NOM_DEPOT", code_depot);
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");
        nom_vendeur = prefs.getString("NOM_VENDEUR", code_vendeur);

        new LongOperation().execute();

    }


    public void prepare_local_file() throws ParseException {

        F_SQL_LIST_VENT =  new HashMap<>();
        F_SQL_LIST_SITUATION =  new HashMap<>();
        String F_SQL = "";

        String querry = "SELECT " +
                "Bon1.RECORDID, " +
                "Bon1.NUM_BON, " +
                "Bon1.DATE_BON, " +
                "Bon1.HEURE, " +
                "Bon1.MODE_RG, " +
                "Bon1.MODE_TARIF, " +

                "Bon1.NBR_P, " +
                "Bon1.TOT_QTE, " +

                "Bon1.TOT_HT, " +
                "Bon1.TOT_TVA, " +
                "Bon1.TIMBRE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE AS TOT_TTC, " +
                "Bon1.REMISE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE AS MONTANT_BON, " +

                "Bon1.ANCIEN_SOLDE, " +
                "Bon1.VERSER, " +
                "Bon1.ANCIEN_SOLDE + (Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE) - Bon1.VERSER AS RESTE, " +

                "Bon1.CODE_CLIENT, " +
                "Client.CLIENT, " +
                "Client.ADRESSE, " +
                "Client.TEL, " +
                "coalesce(Client.RC, '') RC, " +
                "coalesce(Client.IFISCAL, '') IFISCAL, " +
                "coalesce(Client.AI, '') AI, " +
                "coalesce(Client.NIS, '') NIS, " +

                "Client.LATITUDE as LATITUDE_CLIENT, " +
                "Client.LONGITUDE as LONGITUDE_CLIENT, " +

                "Client.SOLDE AS SOLDE_CLIENT, " +
                "Client.CREDIT_LIMIT, " +

                "Bon1.LATITUDE, " +
                "Bon1.LONGITUDE, " +

                "Bon1.CODE_DEPOT, " +
                "Bon1.CODE_VENDEUR, " +
                "Bon1.EXPORTATION, " +
                "Bon1.BLOCAGE " +
                "FROM Bon1 " +
                "LEFT JOIN Client ON Bon1.CODE_CLIENT = Client.CODE_CLIENT " +
                "WHERE BLOCAGE = 'F' ORDER BY Bon1.NUM_BON";

        bon1s = controller.select_vente_from_database(querry);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd");


        for(int i = 0; i< bon1s.size(); i++){

            Date dt = format.parse(bon1s.get(i).date_bon);
            assert dt != null;

            F_SQL = "VENTE|"+bon1s.get(i).nbr_p+"|"+format2.format(dt)+"\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL =  F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('"+ format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL =  F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE,TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE, CODE_VENDEUR ";

            F_SQL =  F_SQL + ")\n VALUES ( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , '"+ bon1s.get(i).code_client.replace("'", "''") + "','"+ bon1s.get(i).client.replace("'", "''") + "','"+ bon1s.get(i).adresse.replace("'", "''") + "','"+ bon1s.get(i).tel.replace("'", "''") + "','" + bon1s.get(i).rc.replace("'", "''") + "','" + bon1s.get(i).ifiscal.replace("'", "''") + "','" +  bon1s.get(i).nis.replace("'", "''") + "','" +  bon1s.get(i).ai.replace("'", "''") + "', "+bon1s.get(i).latitude_client+", "+bon1s.get(i).longitude_client+"";

            F_SQL =  F_SQL + ", iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL =  F_SQL + ") MATCHING (CODE_CLIENT);\n";

            ///////////////////////////////////// BON1 ////////////////////////////////////////////
            F_SQL =  F_SQL + "INSERT INTO BON1 (RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,CODE_DEPOT, CODE_VENDEUR, " +
                    "CODE_CLIENT,DATE_BON, HEURE,MODE_RG,BLOCAGE,MODE_TARIF," +
                    "VERSER, TIMBRE, REMISE, ANCIEN_SOLDE, LATITUDE, LONGITUDE, UTILISATEUR)\n ";




            F_SQL =  F_SQL + " VALUES ( (SELECT GEN_ID(GEN_BON1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:,:CODE_CAISSE:,iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'), ";
            F_SQL =  F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) +"','" + bon1s.get(i).heure +"','" + bon1s.get(i).mode_rg +"','" + bon1s.get(i).blocage + "','" + bon1s.get(i).mode_tarif+ "',";
            F_SQL =  F_SQL + "" + bon1s.get(i).verser + "," + bon1s.get(i).timbre +"," + bon1s.get(i).remise + "," + bon1s.get(i).solde_ancien + "," + bon1s.get(i).latitude + "," + bon1s.get(i).longitude + ", 'TERMINAL_MOBILE');\n" ;


            String querry_select = "" +
                    "SELECT " +
                    "Bon2.RECORDID, " +
                    "Bon2.CODE_BARRE, " +
                    "Bon2.NUM_BON, " +
                    "Bon2.PRODUIT, " +
                    "Bon2.NBRE_COLIS, " +
                    "Bon2.COLISSAGE, " +
                    "Bon2.QTE, " +
                    "Bon2.QTE_GRAT, " +
                    "Bon2.PV_HT, " +
                    "Bon2.TVA, " +
                    "Bon2.CODE_DEPOT, " +
                    "Bon2.PA_HT, " +
                    "Bon2.DESTOCK_TYPE, " +
                    "Bon2.DESTOCK_CODE_BARRE, " +
                    "Bon2.DESTOCK_QTE, " +
                    "Produit.STOCK " +
                    "FROM Bon2 LEFT JOIN Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                    "WHERE Bon2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


            bon2s = controller.select_bon2_from_database(querry_select);

            for(int j = 0; j< bon2s.size(); j++){
                ///////////////////////////////////BON 2 ///////////////////////////////////////
                F_SQL =  F_SQL + "INSERT INTO BON2 (CODE_DEPOT,RECORDID,NUM_BON,";
                F_SQL =  F_SQL + "CODE_BARRE,PRODUIT,DESTOCK_TYPE,DESTOCK_CODE_BARRE,DESTOCK_QTE,";
                F_SQL =  F_SQL + "NBRE_COLIS,COLISSAGE,QTE,QTE_GRAT,";
                F_SQL =  F_SQL + "TVA,PV_HT_AR,PV_HT,PA_HT)\n";
                F_SQL =  F_SQL + "VALUES\n";
                F_SQL =  F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_BON2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL =  F_SQL + "'" + bon2s.get(j).codebarre.replace("'", "''") + "','" + bon2s.get(j).produit.replace("'", "''") + "','" + bon2s.get(j).destock_type + "','" + bon2s.get(j).destock_code_barre + "','" + bon2s.get(j).destock_qte + "',";
                F_SQL =  F_SQL + "" +  bon2s.get(j).nbr_colis + ","   + bon2s.get(j).colissage +","  + bon2s.get(j).qte + "," + bon2s.get(j).gratuit + ",";
                F_SQL =  F_SQL + "" +  bon2s.get(j).tva + ","   + bon2s.get(j).p_u +","  + bon2s.get(j).p_u + "," + bon2s.get(j).pa_ht + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL =  F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL =  F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL =  F_SQL + "ACHATS,VERSEMENTS)\n";
            F_SQL =  F_SQL + "VALUES \n";
            F_SQL =  F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL =  F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','BL-VENTE','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "',";
            F_SQL =  F_SQL + "" +  bon1s.get(i).montant_bon + "," + bon1s.get(i).verser +");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (bon1s.get(i).verser !=0 ) {
                F_SQL =  F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL =  F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL =  F_SQL + "VALUES \n";
                F_SQL =  F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'BL-VENTE',lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000')," + bon1s.get(i).verser + ",";
                F_SQL =  F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "');\n";
            }

            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "VENTE_"+ bon1s.get(i).num_bon+"_"+bon1s.get(i).exportation + "_" + bon1s.get(i).date_bon + ".BLV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_VENT.put(file_name, F_SQL);
        }


        //////////////////////////////////////// SITUATION /////////////////////////////////////////////////
        F_SQL = "";
        all_versement_client = controller.select_carnet_c_from_database("SELECT Carnet_c.RECORDID, " +
                "Carnet_c.CODE_CLIENT, " +
                "Carnet_c.DATE_CARNET, " +
                "Client.CLIENT, " +
                "Client.LATITUDE, " +

                "Client.LONGITUDE, " +
                "Client.TEL, " +
                "Client.CLIENT, " +
                "Client.ADRESSE, " +
                "Client.RC, " +
                "Client.IFISCAL, " +
                "Client.AI, " +
                "Client.NIS, " +
                "Client.MODE_TARIF, " +

                "Carnet_c.HEURE, " +
                "Carnet_c.ACHATS, " +
                "Carnet_c.VERSEMENTS, " +
                "Carnet_c.SOURCE, " +
                "Carnet_c.NUM_BON, " +
                "Carnet_c.MODE_RG, " +
                "Carnet_c.REMARQUES, " +
                "Carnet_c.UTILISATEUR, " +
                "Carnet_c.EXPORTATION " +


                "FROM Carnet_c " +
                "LEFT JOIN Client ON " +
                "Client.CODE_CLIENT = Carnet_c.CODE_CLIENT ");

        for(int i = 0; i< all_versement_client.size(); i++){

            Date dt = format.parse(all_versement_client.get(i).carnet_date);
            assert dt != null;

            F_SQL = "SITUATION-CLIENT|"+format2.format(dt)+"\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL =  F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('"+ format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL =  F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE,TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE , CODE_VENDEUR ";

            F_SQL =  F_SQL + ")\n VALUES ( " +
                    " iif('" + code_depot + "' = '000000', null, '" + code_depot + "') , " +
                    " '"+ all_versement_client.get(i).code_client + "'," +
                    " iif("+ all_versement_client.get(i).client + " = null , null, '"+ all_versement_client.get(i).client.replace("'", "''") + "') , " +
                    " iif("+ all_versement_client.get(i).adresse + " = null , null, '"+ all_versement_client.get(i).adresse.replace("'", "''") + "') , " +
                    " iif("+ all_versement_client.get(i).tel + " = null, null , '"+ all_versement_client.get(i).tel.replace("'", "''") + "') , " +
                    " iif("+ all_versement_client.get(i).rc + " = 'null', null , '"+ all_versement_client.get(i).rc + "') , " +
                    " iif("+ all_versement_client.get(i).ifiscal + " = null , null, '"+ all_versement_client.get(i) + "') , " +
                    " iif("+ all_versement_client.get(i).nis + " = null, null , '"+ all_versement_client.get(i) + "') , " +
                    " iif("+ all_versement_client.get(i).ai + " = null, null , '"+ all_versement_client.get(i) + "') , " +
                    " "+all_versement_client.get(i).latitude+", " +
                    " "+all_versement_client.get(i).longitude+" ";

            F_SQL =  F_SQL + ",iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL =  F_SQL + ") MATCHING (CODE_CLIENT);\n";


            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL =  F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL =  F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL =  F_SQL + "ACHATS,VERSEMENTS)\n";
            F_SQL =  F_SQL + "VALUES \n";
            F_SQL =  F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL =  F_SQL + "'" + all_versement_client.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + all_versement_client.get(i).carnet_heure + "','SITUATION-CLIENT','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "',";
            F_SQL =  F_SQL + " '" + all_versement_client.get(i).carnet_achats + "'," + all_versement_client.get(i).carnet_versement +");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (all_versement_client.get(i).carnet_versement !=0 ) {
                F_SQL =  F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL =  F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL =  F_SQL + "VALUES \n";
                F_SQL =  F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'SITUATION-CLIENT',lpad ((SELECT GEN_ID(GEN_CARNET_C_ID,0) FROM RDB$DATABASE) ,6,'000000')," + all_versement_client.get(i).carnet_versement + ",";
                F_SQL =  F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "');\n";
            }

            file_name = "SITUATION_VRC"+ all_versement_client.get(i).recordid + "_" + all_versement_client.get(i).exportation + "_" + all_versement_client.get(i).carnet_date + ".STC";
            file_name = file_name.replace("/", "_");
            F_SQL_LIST_SITUATION.put(file_name, F_SQL);

        }

    }


    public boolean  create_file(String file_name, String F_SQL){
        try
        {
            if(file_name != null){
                FileOutputStream fOut = mActivity.openFileOutput(file_name, MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(F_SQL);
                osw.flush();
                osw.close();
                return true;
            }else {
                return false;
            }

        }
        catch(Exception ex)
        {
            return false;
        }
    }


    public boolean connectFtp(){
        try
        {
            con = new FTPClient();
            con.connect(serverIp);
            con.setDefaultPort(Integer.parseInt(serverPort));

            if (con.login(username, password)) { return true; }
            else{ return false; }
        }
        catch (Exception e)
        {
            return false;
        }
    }


    public void disconnectFtp(){
        try {
            con.logout();
            con.disconnect();
        }catch (Exception e){

        }
    }


    private final class LongOperation extends AsyncTask<Void, Void, Integer> {

        ProgressDialog mProgressDialog;

        public LongOperation() {

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
                    if(connectFtp()){
                        prepare_local_file();
                        int count_vente = 1;
                        int count_situation = 1;
                        for (Map.Entry map : F_SQL_LIST_VENT.entrySet()) {

                            if(create_file(map.getKey().toString(), map.getValue().toString())){
                                mProgressDialog.setMessage("Exportation des BL ..." + count_vente  + "/" + F_SQL_LIST_VENT.size());
                                s =  new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/VENTE", mProgressDialog);

                            }else {
                                return 1;
                            }
                            count_vente++;
                        }

                        for (Map.Entry map : F_SQL_LIST_SITUATION.entrySet()) {

                            if(create_file(map.getKey().toString(), map.getValue().toString())){
                                mProgressDialog.setMessage("Exportation des STC ..." + count_situation  + "/" + F_SQL_LIST_SITUATION.size());
                                s =  new UploadToFtp().ftpUpload1(mActivity, map.getKey().toString(), ftp_imp_def, con, nom_depot, "/SITUATION-CLIENT", mProgressDialog);

                            }else {
                                return 1;
                            }
                            count_situation++;
                        }

                    }else {
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
            mProgressDialog.hide();
            if(result == 0){
                new SweetAlertDialog(mActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation ftp terminé. \n Total bon Vente : " + F_SQL_LIST_VENT.size() + "\n Total versements : " + F_SQL_LIST_SITUATION.size())
                        .show();
            }else if(result == 1){
                new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problème au niveau de creation du fichier")
                        .show();
            }else if(result == 2){
                new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Problem de connexion serveur ftp")
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

    public int ftpUpload1(Context context, String file_name, String ftp_imp_def, FTPClient mFTPClient, String nom_depot, String vente_situation,  final ProgressDialog pDialog) {

        this.pDialog = pDialog;

        try {

            mFTPClient.makeDirectory(ftp_imp_def);
            ftp_imp_def = ftp_imp_def + "/" + nom_depot;
            mFTPClient.makeDirectory(ftp_imp_def);
            ftp_imp_def = ftp_imp_def + vente_situation;
            mFTPClient.makeDirectory(ftp_imp_def);
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream buffIn = null;
            File file = new File(context.getFileStreamPath(file_name)+ "");
            buffIn = new BufferedInputStream(new FileInputStream(file), 8192);

            mFTPClient.enterLocalPassiveMode();
            streamListener = new CopyStreamAdapter() {

                @Override
                public void bytesTransferred(long totalBytesTransferred, int bytesTransferred, long streamSize) {

                    int percent = (int) (bytesTransferred * 100 / file.length());
                    pDialog.setProgress(percent);

                    if (totalBytesTransferred == file.length()) {
                        removeCopyStreamListener(streamListener);
                    }
                }

            };

            mFTPClient.setCopyStreamListener(streamListener);
            boolean result = mFTPClient.storeFile("/"+ftp_imp_def+"/" + file_name, buffIn);
            buffIn.close();
            status = 0;

        } catch (Exception e) {
            e.printStackTrace();
            status = 2;
        }

        return status;
    }
}
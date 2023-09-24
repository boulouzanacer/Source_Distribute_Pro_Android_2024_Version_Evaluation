package com.safesoft.proapp.distribute.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.safesoft.proapp.distribute.activation.NetClient;
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityEtatC;
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityOrdersClient;
import com.safesoft.proapp.distribute.activities.inventaire.ActivityInventaires;
import com.safesoft.proapp.distribute.activities.vente.ActivityEtatV;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.eventsClasses.SelectedBonTransfertEvent;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.FragmentSelectedBonTransfert;
import com.safesoft.proapp.distribute.ftp.Ftp_export;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Famille;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;
import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.pedant.SweetAlert.SweetAlertDialog;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityImportsExport extends AppCompatActivity {
    private String Server;
    private String Username,Password;
    private final String PREFS = "ALL_PREFS";
    private String Path;
    private DATABASE controller;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog_Free;
    private RelativeLayout Import_bon, Import_client, Import_produit, Export_vente, Export_vente_ftp, Export_commande, Export_commande_ftp, Export_inventaire, EtatV, EtatC, Exported_Vente, Exported_Commande;
    private String code_depot, code_vendeur;
    String currentDateTimeString = null;
    private Context mContext;
    SharedPreferences.Editor editor;

    SharedPreferences prefs;
    private MediaPlayer mp;
    private final EventBus bus = EventBus.getDefault();
    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imports_export);
        controller = new DATABASE(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Import/Export");

        initViews();

        // Register as a subscriber
        bus.register(this);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Server = prefs.getString("ip", "192.168.1.94");
        Path = prefs.getString("path", "C:/PMEPRO1122");
        Username = prefs.getString("username", "SYSDBA");
        Password = prefs.getString("password", "masterkey");

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");

        if(code_depot.equals("000000") || code_depot.equals("")){
            Export_vente.setVisibility(View.GONE);
            Export_vente_ftp.setVisibility(View.GONE);
            Exported_Vente.setVisibility(View.GONE);
            EtatV.setVisibility(View.GONE);
        }


        if((code_depot.equals("000000") || code_depot.equals("")) && (code_vendeur.equals("000000") || code_vendeur.equals(""))){
            Export_vente.setVisibility(View.GONE);
            Export_vente_ftp.setVisibility(View.GONE);
            Exported_Vente.setVisibility(View.GONE);
            EtatV.setVisibility(View.GONE);

            Export_commande.setVisibility(View.GONE);
            Export_commande_ftp.setVisibility(View.GONE);
            Exported_Commande.setVisibility(View.GONE);
            EtatC.setVisibility(View.GONE);
        }


    }

    protected void initViews() {
        //Import_bon = (RelativeLayout) findViewById(R.id.rlt_import_bon);
        //Import_client = (RelativeLayout) findViewById(R.id.rlt_import_client);
        Export_vente = (RelativeLayout) findViewById(R.id.rlt_export_ventes);
        Export_vente_ftp = (RelativeLayout) findViewById(R.id.rlt_export_ventes_ftp);
        Export_commande = (RelativeLayout) findViewById(R.id.rlt_export_commandes);
        Export_commande_ftp = (RelativeLayout) findViewById(R.id.rlt_export_commandes_ftp);
        //Export_inventaire = (RelativeLayout) findViewById(R.id.rlt_export_inventaires);
        Exported_Vente = (RelativeLayout) findViewById(R.id.rlt_exported_ventes);
        Exported_Commande = (RelativeLayout) findViewById(R.id.rlt_exported_commandes);
        EtatV = (RelativeLayout) findViewById(R.id.rlt_etatv);
        EtatC = (RelativeLayout) findViewById(R.id.rlt_etatc);

        mContext = this;
    }

    public void onRelativeClick(View v) throws ParseException {
        if(prefs.getBoolean("USE_SAFE_EVENT", false)){
            GetCurrentIpFromServer get_current_ip_from_server = new GetCurrentIpFromServer(v.getId());
            get_current_ip_from_server.execute();
        }else {
            switshAction(v.getId());
        }

    }


    private void switshAction(int id) throws ParseException {

        switch (id) {
            case R.id.rlt_import_bon -> {

                // 1 check connection
                // 2 get all transfer bon for this deport
                // 3 propose those not exist in local database

                Check_connection_bon_server_task check_connection = new Check_connection_bon_server_task();
                check_connection.execute();
            }
            case R.id.rlt_import_bon_retour -> {
                Check_connection_bon_retour_server_task checkk_connection = new Check_connection_bon_retour_server_task();
                checkk_connection.execute();
            }
            case R.id.rlt_import_fournisseur -> {
                Check_connection_fournisseur_server_for_sychronisation_fournisseur checkk_connection_fournisseur = new Check_connection_fournisseur_server_for_sychronisation_fournisseur();
                checkk_connection_fournisseur.execute();
            }

                /*String querry_is_exp1 = "SELECT RECORDID FROM ACHAT1 WHERE IS_EXPORTED = 0";
                String querry_is_exp2 = "SELECT RECORDID FROM Carnet_c WHERE IS_EXPORTED = 0";
                if(controller.select_count_from_database(querry_is_exp1) == 0){
                    if(controller.select_count_from_database(querry_is_exp2) == 0){
                        Check_connection_client_server_for_sychronisation_client checkkk_connection = new Check_connection_client_server_for_sychronisation_client();
                        checkkk_connection.execute();
                    }else {
                        new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Veuillez exporté les ventes avant la synchronisations des clients" )
                                .show();
                    }
                }else {
                    new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention. !")
                            .setContentText("Veuillez exporté les ventes avant la synchronisations des clients" )
                            .show();
                }*/

            case R.id.rlt_import_client -> {
                String querry1 = "SELECT RECORDID FROM BON1 WHERE IS_EXPORTED = 0";
                String querry2 = "SELECT RECORDID FROM CARNET_C WHERE IS_EXPORTED = 0";
                if (controller.select_count_from_database(querry1) == 0) {
                    if (controller.select_count_from_database(querry2) == 0) {
                        Check_connection_client_server_for_sychronisation_client checkkk_connection = new Check_connection_client_server_for_sychronisation_client();
                        checkkk_connection.execute();
                    } else {
                        new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Veuillez exporté les ventes avant la synchronisations des clients")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention. !")
                            .setContentText("Veuillez exporté les ventes avant la synchronisations des clients")
                            .show();
                }
            }

            case R.id.rlt_import_produit -> {
                String querry3 = "SELECT RECORDID FROM BON1 WHERE IS_EXPORTED = 0";
                String querry4 = "SELECT RECORDID FROM CARNET_C WHERE IS_EXPORTED = 0";
                if (controller.select_count_from_database(querry3) == 0) {
                    if (controller.select_count_from_database(querry4) == 0) {
                        Check_connection_produit_server_for_sychronisation_produit check_connection_produit = new Check_connection_produit_server_for_sychronisation_produit();
                        check_connection_produit.execute();
                    } else {
                        new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Veuillez exporté les ventes avant la synchronisations des produits")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Attention. !")
                            .setContentText("Veuillez exporté les ventes avant la synchronisations des produits")
                            .show();
                }
            }
            case R.id.rlt_import_produit_ftp ->
                    new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Important !")
                            .setContentText(" Cette rubrique est en cours de développement !")
                            .show();

              /*  Activity pactivity;
                pactivity = ActivityImportsExport.this;

                Ftp_export import_produit_ftp = new Ftp_export();
                import_produit_ftp.start(pactivity, "TRANSFERT_LIST", "");*/

            case R.id.rlt_import_parametre -> {
                Check_connection_parametres_server_for_sychronisation_parametres check_connection_parametres = new Check_connection_parametres_server_for_sychronisation_parametres();
                check_connection_parametres.execute();
            }
            case R.id.rlt_export_achats -> {
                Check_connection_export_server check_connection_export_data_vente = new Check_connection_export_server("ACHAT");
                check_connection_export_data_vente.execute();
            }
            case R.id.rlt_export_achats_ftp ->
                    new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Important !")
                            .setContentText(" Cette rubrique est en cours de développement !")
                            .show();

                /*Activity aactivity;
                aactivity = ActivityImportsExport.this;

                Ftp_export export_achat_ftp = new Ftp_export();
                export_achat_ftp.start(aactivity, "ACHAT", "");*/

            case R.id.rlt_export_ventes -> {
                Check_connection_export_server check_connection_export_data_achat = new Check_connection_export_server("VENTE");
                check_connection_export_data_achat.execute();
            }
            case R.id.rlt_export_ventes_ftp -> {
                Activity vactivity;
                vactivity = ActivityImportsExport.this;
                Ftp_export export_vente_ftp = new Ftp_export();
                export_vente_ftp.start(vactivity, "SALE", "");
            }
            case R.id.rlt_export_commandes -> {
                Check_connection_export_server check_connection_export_data_commande = new Check_connection_export_server("COMMANDE");
                check_connection_export_data_commande.execute();
            }
            case R.id.rlt_export_commandes_ftp -> {
                Activity cactivity;
                cactivity = ActivityImportsExport.this;
                Ftp_export export_commande_ftp = new Ftp_export();
                export_commande_ftp.start(cactivity, "ORDER", "");
            }
            case R.id.rlt_export_inventaires -> {
                Check_connection_export_server check_connection_export_data_inventaire = new Check_connection_export_server("INVENTAIRE");
                check_connection_export_data_inventaire.execute();
            }
            case R.id.rlt_export_inventaires_ftp -> {
                Activity iactivity;
                iactivity = ActivityImportsExport.this;
                Ftp_export export_inventaire_ftp = new Ftp_export();
                export_inventaire_ftp.start(iactivity, "INVENTAIRE", "");
            }
            case R.id.rlt_exported_ventes -> {
                Intent exported_ventes_intent = new Intent(ActivityImportsExport.this, ActivitySales.class);
                exported_ventes_intent.putExtra("SOURCE_EXPORT", "EXPORTED");
                startActivity(exported_ventes_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            case R.id.rlt_exported_commandes -> {
                Intent exported_commandes_intent = new Intent(ActivityImportsExport.this, ActivityOrdersClient.class);
                exported_commandes_intent.putExtra("SOURCE_EXPORT", "EXPORTED");
                startActivity(exported_commandes_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            case R.id.rlt_exported_inventaires -> {
                Intent exported_inventaire_intent = new Intent(ActivityImportsExport.this, ActivityInventaires.class);
                exported_inventaire_intent.putExtra("SOURCE_EXPORT", "EXPORTED");
                startActivity(exported_inventaire_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            case R.id.rlt_etatv -> {
                Intent etat_v_intent = new Intent(ActivityImportsExport.this, ActivityEtatV.class);
                startActivity(etat_v_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
            case R.id.rlt_etatc -> {
                Intent etat_c_intent = new Intent(ActivityImportsExport.this, ActivityEtatC.class);
                startActivity(etat_c_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }
    }
    //========================== AsyncTask TO IP from save event ===================================
    public class GetCurrentIpFromServer extends AsyncTask<Void, Integer, Integer> {
        int flag = 0;
        String messageError ="";

        String token = "0.0.0.0";

        int params;

        public GetCurrentIpFromServer(int id){
            params = id;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Liaison avec le serveur ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {

            try {
                NetClient nc = new NetClient("105.96.9.62", 1209); // ip adress and port
                String message_online = prefs.getString("SAFE_EVENT_USER","") + ":" + prefs.getString("SAFE_EVENT_PASS","") + ":"+ "" + ":" + "cccc";
                nc.sendDataWithString(message_online);
                token = nc.receiveDataFromServer();
                if(token.length()<= 15){
                    flag = 1;
                }else {
                    flag = 2;
                    messageError = token;
                }
            } catch (Exception e) {
                flag = 3;
                messageError = e.getMessage();

            }
            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();

            if (integer == 1) {
                // get all transfert bon of this depot
                Server = token;
                // call id button
                try {
                    switshAction(params);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention...")
                        .setContentText("Probleme  : " + messageError)
                        .show();
            }else if (integer == 3) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }


    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_bon_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;

        String messageError ="";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérificaion de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {

            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }

                messageError = e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                // get all transfert bon of this depot
                Import_BonTransfert2_server_task bon_transfert2_task = new Import_BonTransfert2_server_task();
                bon_transfert2_task.execute();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }

    //===========================================================================================
    public class Check_connection_bon_retour_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String messageError = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérificaion de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                // get all transfert bon of this depot
                Import_BonTransfert2_retour_server_task bon_transfert2_task = new Import_BonTransfert2_retour_server_task();
                bon_transfert2_task.execute();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }

    public class Import_BonTransfert2_retour_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        ArrayList<String> transfer1s;
        String messageError = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free.setMessage("Collection des informations ...");

        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                transfer1s = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //============================ GET Trasfer1 ===========================================

                //Date midnightDate = new Date(midnight);
                SharedPreferences pref = getSharedPreferences(PREFS, 0);
                currentDateTimeString = pref.getString("date_time", null);
                String sql1 = "SELECT " +
                            " TRANSFERT1.num_bon" +
                            " FROM TRANSFERT1 " +
                            " WHERE ( CODE_DEPOT_SOURCE = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( CAST( TRANSFERT1.date_bon || ' ' || TRANSFERT1.heure AS timestamp) > '" + currentDateTimeString + "' ) ";


                ResultSet rs1 = stmt.executeQuery(sql1);

                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {

                   String num_bon = rs1.getString("NUM_BON");

                    if (!controller.check_transfer1_if_exist(num_bon))
                        transfer1s.add(num_bon);

                }

                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD" + e.getMessage());
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                if (!((Activity) mContext).isFinishing()) {
                    //propose list
                    showListBons(transfer1s, "Bons de retour");

                }
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_BonTransfert2_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        ArrayList<String> transfer1s;
        String messageError = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free.setMessage("Collection des informations ...");

        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {

                transfer1s = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //============================ GET Trasfer1 ===========================================

                //Date midnightDate = new Date(midnight);
                SharedPreferences pref = getSharedPreferences(PREFS, 0);
                currentDateTimeString = pref.getString("date_time", null);
                String sql1 = "SELECT " +
                            " TRANSFERT1.num_bon " +
                            " FROM TRANSFERT1 " +
                            " WHERE ( CODE_DEPOT_DEST = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( CAST( TRANSFERT1.date_bon || ' ' || TRANSFERT1.heure AS timestamp) > '" + currentDateTimeString + "' )";


                ResultSet rs1 = stmt.executeQuery(sql1);
                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {

                    String num_bon = rs1.getString("NUM_BON");
                    if (!controller.check_transfer1_if_exist(num_bon))
                        transfer1s.add(num_bon);
                }

                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD" + e.getMessage());
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                if (!((Activity) mContext).isFinishing()) {
                    //propose list
                    showListBons(transfer1s, "Bons de transfers");
                }
            }else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }
    //===========================================================================================

    protected void showListBons(ArrayList<String> transfert1s, String title) {
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentSelectedBonTransfert(); // creating new object
        Bundle args = new Bundle();
        args.putStringArrayList("LIST_SELECTED_TRANSFERT_BON", transfert1s);
        args.putString("TITLE", title);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }

    @Subscribe
    public void onBonTransfertSelected(SelectedBonTransfertEvent event) {

        Import_bonTransfer_from_server_task nnn = new Import_bonTransfer_from_server_task(event.getNum_bon());
        nnn.execute();
    }

    public void progressDialogConfig() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation des données...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


    public void progressDialogExportation() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Exportation des données...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


    public void progressDialogConfigFournisseur() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation fournisseurs...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressDialogConfigClient() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation clients...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressDialogConfigProduit() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation produits...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressDialogConfigInventaire() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Exportation inventaires...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_bonTransfer_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        int compt = 0;
        int allrows = 0;
        private final boolean First = true;
        ArrayList<PostData_Transfer2> transfer2s;
        String num_bon;

        public Import_bonTransfer_from_server_task(String _num_bon) {
            num_bon = _num_bon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfig();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Transfer1> transfer1s = new ArrayList<>();
                transfer2s = new ArrayList<>();

                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                String sql11 = "SELECT  TRANSFERT2.NUM_BON FROM TRANSFERT2 WHERE NUM_BON =  '" + num_bon + "'";
                ResultSet rs11 = stmt.executeQuery(sql11);

                while (rs11.next()) {
                    allrows++;
                }

               // First = true;
                publishProgress(1);

                //============================ GET Trasfer1 ===========================================

                String sql1 = "SELECT " +
                        "    TRANSFERT1.num_bon," +
                        "    TRANSFERT1.date_bon," +
                        "    TRANSFERT1.heure," +
                        "    TRANSFERT1.code_depot_source," +
                        "    dep_source.nom_depot as nom_depot_source," +
                        "    TRANSFERT1.code_depot_dest," +
                        "    dep_dest.nom_depot as nom_depot_dest," +
                        "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P " +
                        " FROM TRANSFERT1 " +
                        "left join depot1 as dep_source on ( dep_source.code_depot = TRANSFERT1.code_depot_source) " +
                        "left join depot1 as dep_dest on ( dep_dest.code_depot = TRANSFERT1.code_depot_dest) " +
                        "WHERE NUM_BON = '" + num_bon + "'";

                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Transfer1 transfer1;
                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {
                    transfer1 = new PostData_Transfer1();
                    transfer1.num_bon = rs1.getString("NUM_BON");
                    transfer1.date_bon = rs1.getString("DATE_BON");
                    transfer1.code_depot_s = rs1.getString("CODE_DEPOT_SOURCE");
                    transfer1.nom_depot_s = rs1.getString("NOM_DEPOT_SOURCE");
                    transfer1.code_depot_d = rs1.getString("CODE_DEPOT_DEST");
                    transfer1.nom_depot_d = rs1.getString("NOM_DEPOT_DEST");
                    transfer1.nbr_p = rs1.getString("NBR_P");

                    transfer1s.add(transfer1);

                    compt++;
                    publishProgress(compt);
                }

                //============================ GET Trasfer2 ===========================================
                String sql2 = "SELECT  TRANSFERT2.NUM_BON, TRANSFERT2.CODE_BARRE, PRODUIT.PRODUIT AS PRODUIT, TRANSFERT2.NBRE_COLIS, TRANSFERT2.COLISSAGE, coalesce(TRANSFERT2.QTE,0) AS QTE FROM TRANSFERT2, PRODUIT WHERE (TRANSFERT2.CODE_BARRE = PRODUIT.CODE_BARRE) AND NUM_BON = '" + num_bon + "'";
                ResultSet rs2 = stmt.executeQuery(sql2);

                while (rs2.next()) {

                    PostData_Transfer2 transfer2 = new PostData_Transfer2();

                    transfer2.num_bon = rs2.getString("NUM_BON");
                    transfer2.code_barre = rs2.getString("CODE_BARRE");
                    transfer2.produit = rs2.getString("PRODUIT");
                    transfer2.qte = rs2.getDouble("QTE");
                    transfer2.nbr_colis = rs2.getDouble("NBRE_COLIS");
                    transfer2.colissage = rs2.getDouble("COLISSAGE");

                    transfer2s.add(transfer2);

                    compt++;
                    publishProgress(compt);

                }

                //First = false;
                publishProgress(1);
                compt = 0;

                boolean executed = controller.ExecuteTransactionTrasfer(transfer1s, transfer2s);

               // transfer2s.clear();
               // transfer2s = controller.select_transfer2_from_database("SELECT * FROM TRANSFERT2 WHERE NUM_BON = '" + num_bon + "' GROUP BY CODE_BARRE ");

                PostData_Produit produit;

                for (int i = 0; i < transfer2s.size(); i++) {
                    produit = new PostData_Produit();
                    String querry  = "SELECT STOCK FROM PRODUIT WHERE CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'","''") + "'";
                    produit = controller.check_product_if_exist(querry);

                    PostData_Produit produit_update = null;

                    if (produit.exist) {
                        // UPDATE
                        String sql3 = "SELECT  " +
                                "PRODUIT.PRODUIT , " +
                                "PRODUIT.COLISSAGE, " +
                                "coalesce(PRODUIT.PA_HT,0) AS PA_HT, " +
                                "coalesce(PRODUIT.TVA,0) AS TVA, " +
                                "cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , " +
                                "cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, " +
                                "cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT ";

                        if(TYPE_LOGICIEL.equals("PME PRO")){
                            sql3 = sql3 + " ,PRODUIT.PHOTO  FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "'";
                        }else {
                            sql3 = sql3 + " FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "'";
                        }

                        ResultSet rs3 = stmt.executeQuery(sql3);
                        while (rs3.next()) {

                            produit_update = new PostData_Produit();

                            if (transfer1s.get(0).code_depot_s.equals(code_depot)) {
                                produit_update.stock = produit.stock - transfer2s.get(i).qte;
                            } else {
                                produit_update.stock = produit.stock + transfer2s.get(i).qte ;
                            }

                            produit_update.produit = rs3.getString("PRODUIT");
                            produit_update.pa_ht = rs3.getDouble("PA_HT");
                            produit_update.tva = rs3.getDouble("TVA");
                            produit_update.pv1_ht = rs3.getDouble("PV1_HT");
                            produit_update.pv2_ht = rs3.getDouble("PV2_HT");
                            produit_update.pv3_ht = rs3.getDouble("PV3_HT");
                            produit_update.colissage = rs3.getDouble("COLISSAGE");
                            produit_update.photo = rs3.getBytes("PHOTO");

                        }
                        controller.update_produit(produit_update, transfer2s.get(i).code_barre);


                        //Get all syn codebarre of this product  and  Insert it into codebarre tables
                        String sql4 = "SELECT CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE WHERE CODEBARRE.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "' ";
                        ResultSet rs4 = stmt.executeQuery(sql4);

                        controller.delete_Codebarre(transfer2s.get(i).code_barre);

                        while (rs4.next()) {

                            PostData_Codebarre post_codebarre = new PostData_Codebarre();
                            post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                            post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                            controller.insert_into_codebarre(post_codebarre);
                        }
                    } else {
                        //Get product and  Insert it into produit tables
                        String sql3 = "SELECT  " +
                                "PRODUIT.CODE_BARRE, " +
                                "PRODUIT.REF_PRODUIT,  " +
                                "PRODUIT.COLISSAGE,  " +
                                "PRODUIT.PRODUIT, " +
                                "coalesce(PRODUIT.PA_HT,0) AS PA_HT, " +
                                "coalesce(PRODUIT.TVA,0) AS TVA, " +
                                "cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , " +
                                "cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, " +
                                "cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT ";

                        if(TYPE_LOGICIEL.equals("PME PRO")){
                            sql3 = sql3 + " ,PRODUIT.PHOTO FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "'";
                        }else {
                            sql3 = sql3 + " FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "'";
                        }

                        ResultSet rs3 = stmt.executeQuery(sql3);
                        while (rs3.next()) {

                            produit_update = new PostData_Produit();

                            produit_update.code_barre = rs3.getString("CODE_BARRE");
                            produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                            produit_update.produit = rs3.getString("PRODUIT");
                            produit_update.pa_ht = rs3.getDouble("PA_HT");
                            produit_update.tva = rs3.getDouble("TVA");
                            produit_update.pv1_ht = rs3.getDouble("PV1_HT");
                            produit_update.pv2_ht = rs3.getDouble("PV2_HT");
                            produit_update.pv3_ht = rs3.getDouble("PV3_HT");
                            produit_update.colissage = rs3.getDouble("COLISSAGE");
                            produit_update.stock = transfer2s.get(i).qte;
                            produit_update.photo = rs3.getBytes("PHOTO");

                        }

                        controller.insert_into_produit(produit_update);

                        //Get all syn codebarre of this product  and  Insert it into codebarre tables
                        String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE WHERE CODEBARRE.CODE_BARRE = '" + transfer2s.get(i).code_barre.replace("'", "''") + "' ";
                        ResultSet rs4 = stmt.executeQuery(sql4);

                        while (rs4.next()) {

                            PostData_Codebarre post_codebarre = new PostData_Codebarre();
                            post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                            post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");

                            controller.insert_into_codebarre(post_codebarre);
                        }
                    }

                    compt++;
                    publishProgress(compt);
                }

                stmt.close();



/*
                //============================ GET CODEBARRE =======================================
                String sql4 = "SELECT CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE";
                ResultSet rs4 = stmt.executeQuery(sql4);
                PostData_Codebarre codebarre;
                while (rs4.next()) {
                    codebarre = new PostData_Codebarre();
                    codebarre.code_barre = rs4.getString("CODE_BARRE");
                    codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                    codebarres.add(codebarre);
                }
                */

                if (executed) {
                    flag = 1;
                } else {
                    flag = 3;
                }

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                Log.e("TRACKKK", "ERROR WHEN EXECUTING LOAD PRODUITS, LIST DEPOT2 AND DEPOT1 ASYNCRON TASK " + e.getMessage());
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");
                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
            }

            return flag;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (First)
                mProgressDialog.setMax(allrows);
            else
                mProgressDialog.setMax(transfer2s.size());

            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            super.onPostExecute(integer);
        }
    }
    //===========================================================================================


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound() {
        mp = MediaPlayer.create(this, R.raw.back);
        mp.start();
    }


    // Importation liste client
    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_export_server extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        private String typeBon = "";
        String messageError = "";


        public Check_connection_export_server(String typeBon) {

            this.typeBon = typeBon;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérificaion de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                // export data vente, commande to the server
                switch (typeBon) {
                    case "VENTE" -> {
                        Exporter_ventes_to_server_task export_ventesx_to_server = new Exporter_ventes_to_server_task();
                        export_ventesx_to_server.execute();
                    }
                    case "ACHAT" -> {
                        Exporter_achats_to_server_task export_achats_to_server = new Exporter_achats_to_server_task();
                        export_achats_to_server.execute();
                    }
                    case "COMMANDE" -> {
                        Exporter_commandes_to_server_task export_commandes_to_server = new Exporter_commandes_to_server_task();
                        export_commandes_to_server.execute();
                    }
                    case "INVENTAIRE" -> {
                        Export_inventaire_to_server_task export_inventaire_to_server = new Export_inventaire_to_server_task(true, null);
                        export_inventaire_to_server.execute();
                    }
                }
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }


    //==================== AsyncTask TO export achats to the server
    public class Exporter_achats_to_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String erreurMessage = "";

        int bon_inserted = 0;
        int total_versement = 0;

        List<String> list_num_bon_not_exported;
        List<String> list_recordid_versement_not_exported;

        List<String> list_produit_not_exported;
        String messageError = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nf = NumberFormat.getInstance(Locale.US);
            ((DecimalFormat) nf).applyPattern("###,##0.00");
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                ArrayList<PostData_Achat1> achat1s;
                ArrayList<PostData_Bon2> achat2s;
                PostData_Fournisseur fournisseur;
                list_produit_not_exported = new ArrayList<>();
                list_num_bon_not_exported = new ArrayList<>();
                list_recordid_versement_not_exported = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;

                String querry = "SELECT " +
                        "ACHAT1.RECORDID, " +
                        "ACHAT1.NUM_BON, " +
                        "ACHAT1.CODE_FRS, " +
                        "ACHAT1.DATE_BON, " +
                        "ACHAT1.HEURE, " +
                        "ACHAT1.NBR_P, " +
                        "ACHAT1.TOT_QTE, " +
                        "ACHAT1.EXPORTATION, " +
                        "ACHAT1.BLOCAGE, " +
                        "ACHAT1.CODE_DEPOT, " +

                        "ACHAT1.NBR_P, " +
                        "ACHAT1.TOT_QTE, " +

                        "ACHAT1.TOT_HT, " +
                        "ACHAT1.TOT_TVA, " +
                        "ACHAT1.TIMBRE, " +
                        "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE AS TOT_TTC, " +
                        "ACHAT1.REMISE, " +
                        "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE AS MONTANT_BON, " +

                        "ACHAT1.ANCIEN_SOLDE, " +
                        "ACHAT1.VERSER, " +
                        "ACHAT1.ANCIEN_SOLDE + (ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE) - ACHAT1.VERSER AS RESTE, " +

                        "FOURNIS.FOURNIS, " +
                        "FOURNIS.ADRESSE, " +
                        "FOURNIS.TEL " +
                        "FROM ACHAT1 " +
                        "LEFT JOIN FOURNIS ON (ACHAT1.CODE_FRS = FOURNIS.CODE_FRS) " +
                        "WHERE BLOCAGE = 'F' ORDER BY ACHAT1.NUM_BON";


                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");

                achat1s = controller.select_all_achat1_from_database(querry);

                list_produit_not_exported = update_produit_into_server(con, stmt);

                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

                String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

                // Get CODE_CAISSE

                /*String CODE_CAISSE = "000000";

                String requette_vendeur_caisse = "SELECT coalesce(CODE_VENDEUR, '000000') AS CODE_VENDEUR, coalesce(CODE_CAISSE, '000000') AS CODE_CAISSE FROM DEPOT1 WHERE CODE_DEPOT = '" + code_depot + "' ";
                ResultSet rs111 = stmt.executeQuery(requette_vendeur_caisse);
                while (rs111.next()) {
                    CODE_CAISSE = rs111.getString("CODE_CAISSE");
                }*/

                for (int i = 0; i < achat1s.size(); i++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format.parse(achat1s.get(i).date_bon);
                        assert dt != null;
                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean fournis_exist = false;
                        // check client if exist
                        String fournis_requete = "SELECT CODE_FRS FROM FOURNIS WHERE CODE_FRS = '" + achat1s.get(i).code_frs + "'";
                        ResultSet rs0 = stmt.executeQuery(fournis_requete);
                        while (rs0.next()) {
                            fournis_exist = true;
                        }

                        // insert client if not exist
                        if (!fournis_exist) {
                            fournisseur = new PostData_Fournisseur();

                            String insert_fournisseur;

                            // insert fournisseur
                            insert_fournisseur = "INSERT INTO FOURNIS (CODE_FRS, FOURNIS, ADRESSE, TEL, CODE_DEPOT) VALUES ('"
                                    + achat1s.get(i).code_frs.replace("'", "''") + "' ," +
                                    "'" + achat1s.get(i).fournis.replace("'", "''") + "', " +
                                    "'" + achat1s.get(i).adresse.replace("'", "''") + "', " +
                                    "'" + achat1s.get(i).tel.replace("'", "''") + "', " +
                                    "'" + achat1s.get(i).code_depot.replace("'", "''") + "')";


                            stmt.executeUpdate(insert_fournisseur);
                            con.commit();
                        }

                        String file_name = "";
                        file_name = "ACHAT_"+ achat1s.get(i).num_bon+"_"+achat1s.get(i).exportation + "_" + achat1s.get(i).date_bon + ".BDL";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_achat1_exist = false;
                        String check_achat1_requete = "SELECT NUM_BON FROM BON_A1 WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_achat1_requete);

                        while (rs3.next()) {
                            check_achat1_exist = true;
                        }

                        if (!check_achat1_exist) {
                            String querry_select = "" +
                                    "SELECT " +
                                    "ACHAT2.RECORDID, " +
                                    "ACHAT2.CODE_BARRE, " +
                                    "ACHAT2.NUM_BON, " +
                                    "ACHAT2.PRODUIT, " +
                                    "ACHAT2.NBRE_COLIS, " +
                                    "ACHAT2.COLISSAGE, " +
                                    "ACHAT2.QTE, " +
                                    "ACHAT2.QTE_GRAT, " +
                                    "ACHAT2.PU, " +
                                    "ACHAT2.TVA, " +
                                    "ACHAT2.CODE_DEPOT, " +
                                    "ACHAT2.QTE_GRAT, " +
                                    "PRODUIT.STOCK " +
                                    "FROM ACHAT2 " +
                                    "LEFT JOIN PRODUIT ON (ACHAT2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                    "WHERE ACHAT2.NUM_BON = '" + achat1s.get(i).num_bon + "'";


                            achat2s = controller.select_all_achat2_from_database(querry_select);
                            String[] buffer = new String[achat2s.size() + 1];

                            // Get RECORDID
                            String generator_requet = "SELECT gen_id(gen_bon_a1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            while (rs1.next()) {
                                recordid_numbon = rs1.getInt("RECORDID");
                            }


                            /*String insert_into_journee = "UPDATE OR INSERT INTO JOURNEE  (DATE_JOURNEE) VALUES ( '" + format2.format(dt) + "' ) MATCHING (DATE_JOURNEE) ";
                            stmt.executeUpdate(insert_into_journee);*/

                            String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);


                            String insert_into_bon1 = "INSERT INTO BON_A1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_FRS, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, EXPORTATION, BLOCAGE, CODE_DEPOT) VALUES ('"
                                    + recordid_numbon + "' ," +
                                    " '" + NUM_BON + "' , " +
                                    " '" + format2.format(dt) + "' ," +
                                    " '" + achat1s.get(i).heure + "', "+
                                    " '" + achat1s.get(i).code_frs.replace("'", "''") + "' ," +
                                    // " '" + bon1s.get(i).tot_ht + "' ," +
                                    //  " '" + bon1s.get(i).tot_tva + "' ," +
                                    " iif('" + achat1s.get(i).timbre + "' = 'null',0,'" + achat1s.get(i).timbre + "')," +
                                    " iif('" + achat1s.get(i).remise + "' = 'null',0,'" + achat1s.get(i).remise + "')," +
                                    " '" + achat1s.get(i).verser + "', " +
                                    " '" + achat1s.get(i).solde_ancien + "'," +
                                    " iif('" + achat1s.get(i).mode_rg + "' = 'null',null,'" + achat1s.get(i).mode_rg + "') ," +
                                    " 'TERMINAl_MOBILE'," +
                                    " '" + file_name + "'," +
                                    " 'M' ," +
                                    " iif('" + code_depot + "' = '000000', null,'" + code_depot + "'))";

                            stmt.addBatch(insert_into_bon1);


                            for (int j = 0; j < achat2s.size(); j++) {
                                buffer[j] = "INSERT INTO BON_A2 (NUM_BON, CODE_BARRE, QTE, PA_HT, TVA, NBRE_COLIS, COLISSAGE, CODE_DEPOT ";
                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", QTE_GRAT, PRODUIT ";
                                }
                                buffer[j] = buffer[j]+ ") VALUES ('" + NUM_BON + "' ," +
                                        " '" + achat2s.get(j).codebarre.replace("'", "''") + "' ," +
                                        " '" + achat2s.get(j).qte + "'," +
                                        " '" + achat2s.get(j).p_u + "' ," +
                                        " '" + achat2s.get(j).tva + "' ," +
                                        " '" + achat2s.get(j).nbr_colis + "'," +
                                        " '" + achat2s.get(j).colissage + "'," +
                                        " iif('" + code_depot + "' = '000000', null,'" + code_depot + "') ";

                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", '" + achat2s.get(j).gratuit + "', '" + achat2s.get(j).produit.replace("'", "''") + "' ";
                                }
                                buffer[j] = buffer[j] + ")";

                                stmt.addBatch(buffer[j]);
                            }

                            //   stmt.executeBatch();

                            /*String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                    + achat1s.get(i).code_frs.replace("'", "''") + "', " +
                                    " '" + format2.format(dt) + "', " +
                                    " '" + achat1s.get(i).heure + "', " +
                                    " '" + achat1s.get(i).montant_bon + "' ," +
                                    " '" + achat1s.get(i).verser + "' ," +
                                    " 'BL-VENTE' ," +
                                    " '" + NUM_BON + "'," +
                                    " '" + achat1s.get(i).mode_rg + "'," +
                                    " 'TERMINAL_MOBILE'," +
                                    " ' '," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null ,'" + CODE_CAISSE + "'))";

                            stmt.addBatch(requete_situation);*/


                           /* if (!CODE_CAISSE.equals("000000")) {
                                if (achat1s.get(i).verser != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "' ," +
                                            " '" + CODE_CAISSE + "'," +
                                            " '" + format2.format(dt) + "' ," +
                                            " '" + achat1s.get(i).verser + "', " +
                                            " '0'," +
                                            " 'BL-VENTE' ," +
                                            " '" + NUM_BON + "'," +
                                            " 'ESPECE'," +
                                            " ' '," +
                                            " 'TERMINAL_MOBILE')";
                                    stmt.addBatch(requete_caisse);
                                }
                            }*/

                            stmt.executeBatch();
                            con.commit();
                            // update bon as exported
                            controller.update_achats_commandes_as_exported(false, achat1s.get(i).num_bon);
                            bon_inserted++;
                            stmt.clearBatch();
                            achat2s.clear();
                        }

                    }catch (Exception e){
                        con.rollback();
                        list_num_bon_not_exported.add( e.getMessage());
                        stmt.clearBatch();
                    }

                }

                // Export all versement

                // INSERT ALL VERSEMENTS OF CAURANT CLIENTS
                /*ArrayList<PostData_Carnet_c> all_versement_client;
                ArrayList<PostData_Client> list_client ;

                all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                        "CARNET_C.CODE_CLIENT, " +
                        "CARNET_C.DATE_CARNET, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.LATITUDE, " +

                        "CLIENT.LONGITUDE, " +
                        "CLIENT.TEL, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.ADRESSE, " +
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
                        "CARNET_C.EXPORTATION " +


                        "FROM CARNET_C " +
                        "LEFT JOIN CLIENT ON " +
                        "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");



                for (int g = 0; g < all_versement_client.size(); g++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format.parse(all_versement_client.get(g).carnet_date);
                        assert dt != null;

                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean client_exist = false;
                        // check client if exist
                        String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + all_versement_client.get(g).code_client + "'";
                        ResultSet rs0 = stmt.executeQuery(client_requete);
                        while (rs0.next()) {
                            client_exist = true;
                        }

                        // insert client if not exist
                        if (!client_exist) {
                            fournisseur = new PostData_Fournisseur();

                            // Select client from data base to insert it
                            // client = controller.select_client_from_database(bon1s.get(i).code_client);
                            String insert_client;
                            // insert client
                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, CODE_DEPOT, CODE_VENDEUR, LATITUDE, LONGITUDE) VALUES ('"
                                    + all_versement_client.get(g).code_client.replace("'", "''") + "' ," +
                                    "'" + all_versement_client.get(g).client.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).adresse.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).tel.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).rc.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).ifiscal.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).ai.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).nis.replace("'", "''") + "', " +
                                    "'" + all_versement_client.get(g).mode_tarif + "' , " +
                                    " iif('" + code_depot + "' = '000000', null ,'" + code_depot + "'), " +
                                    " iif('" + code_vendeur + "' = '000000', null ,'" + code_vendeur + "'), " +
                                    " iif('" + all_versement_client.get(g).latitude + "' = 'null', null ,'" + all_versement_client.get(g).latitude + "') , " +
                                    " iif('" + all_versement_client.get(g).longitude + "' = 'null', null ,'" + all_versement_client.get(g).longitude + "'))";


                            stmt.executeUpdate(insert_client);
                            con.commit();
                        }

                        String file_name = "";
                        file_name = "SITUATION_VRC"+ all_versement_client.get(g).recordid + "_" + all_versement_client.get(g).exportation + "_" + all_versement_client.get(g).carnet_date + ".STC";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_carnet_c_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM CARNET_C WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_carnet_c_exist = true;
                        }

                        if(!check_carnet_c_exist){

                            // Get carnet_c recordid
                            String generator_requet = "SELECT gen_id(gen_carnet_c_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            Integer recordid_carnet_c = 0;
                            while (rs1.next()) {
                                recordid_carnet_c = rs1.getInt("RECORDID");
                            }


                            //String RECORDID_CRC = Get_Digits_String(String.valueOf(num_bon_carnet_c), 6);
                            String buffer_versement = "INSERT INTO CARNET_C (RECORDID, CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, EXPORTATION , CODE_VENDEUR, CODE_CAISSE) VALUES (" +
                                    " '" + recordid_carnet_c + "', " +
                                    " '"  + all_versement_client.get(g).code_client.replace("'", "''") + "' ," +
                                    " '" + format2.format(dt) + "'  ," +
                                    " '" + all_versement_client.get(g).carnet_heure + "' ," +
                                    " '" + all_versement_client.get(g).carnet_achats + "' ," +
                                    " '" + all_versement_client.get(g).carnet_versement + "' ," +
                                    " 'SITUATION-CLIENT' ," +
                                    " 'VRC" + recordid_carnet_c + "'," +
                                    " '" + all_versement_client.get(g).carnet_mode_rg + "'," +
                                    " 'TERMINAL_MOBILE'," +
                                    " '" + all_versement_client.get(g).carnet_remarque.replace("'", "''") + "'," +
                                    " '" + file_name + "'," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null,'" + CODE_CAISSE + "'))";
                            stmt.addBatch(buffer_versement);


                            if (!CODE_CAISSE.equals("000000")) {
                                if (all_versement_client.get(g).carnet_versement != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , '" + format2.format(dt) + "' , '" + all_versement_client.get(g).carnet_versement +
                                            "' , '0', 'SITUATION-CLIENT' , 'VRC" + recordid_carnet_c + "', 'ESPECE',  '" + all_versement_client.get(g).carnet_remarque + "', 'TERMINAL_MOBILE')";
                                    stmt.addBatch(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            controller.Update_versement_exported(all_versement_client.get(g).recordid);
                            stmt.clearBatch();
                            total_versement++;

                        }

                    }catch (Exception e){
                        con.rollback();
                        stmt.clearBatch();
                        list_recordid_versement_not_exported.add("VRC"+all_versement_client.get(g).recordid + " - " + all_versement_client.get(g).client + " :  " + nf.format(all_versement_client.get(g).carnet_versement) );
                    }

                }*/

                flag = 1;


            } catch (Exception ex) {
                ex.printStackTrace();
                // con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement

                    flag = 3;
                }

                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                erreurMessage = ex.getMessage();
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            // Problem insert client into database // operation aborded
            if (integer == 1) {
                String nums_bon_error = "";
                String versement_error = "";
                for(int i = 0; i < list_num_bon_not_exported.size(); i++){
                    if(i > 0){
                        nums_bon_error = nums_bon_error + " ,  ";
                    }
                    nums_bon_error = nums_bon_error + list_num_bon_not_exported.get(i);
                }
                if(list_num_bon_not_exported.size() > 0){
                    nums_bon_error = "\nProblem exportation dans les bons : " + nums_bon_error;
                }
                for(int i = 0; i < list_recordid_versement_not_exported.size() ; i++){

                    if(i > 0){
                        versement_error = versement_error + " \n  ";
                    }
                    versement_error = versement_error + list_recordid_versement_not_exported.get(i);

                }
                if(list_recordid_versement_not_exported.size() > 0){
                    versement_error = "\nProblem exportation dans les versements nums : \n" + versement_error;
                }
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé \n Nombre de bons exportés : " + bon_inserted + "\n ==================" + "\n  Nombre de versements exportés : " + total_versement + "\n ==================" + nums_bon_error  + versement_error )
                        .show();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            } else if (integer == 3) {
                //  if(ActivityImportsExport. != null)
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }

    //==================== AsyncTask TO export ventes to the server
    public class Exporter_ventes_to_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String erreurMessage = "";

        int bon_inserted = 0;
        int total_versement = 0;

        List<String> list_produit_not_exported;
        List<String> list_num_bon_not_exported;
        List<String> list_recordid_versement_not_exported;

        String messageError = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nf = NumberFormat.getInstance(Locale.US);
            ((DecimalFormat) nf).applyPattern("###,##0.00");
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                ArrayList<PostData_Bon1> bon1s;
                ArrayList<PostData_Bon2> bon2s;
                PostData_Client client;
                list_produit_not_exported = new ArrayList<>();
                list_num_bon_not_exported = new ArrayList<>();
                list_recordid_versement_not_exported = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;

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
                        "Client.CLIENT, " +
                        "Client.ADRESSE, " +
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

                        "BON1.CODE_DEPOT, " +
                        "BON1.CODE_VENDEUR, " +
                        "BON1.EXPORTATION, " +
                        "BON1.BLOCAGE " +
                        "FROM BON1 " +
                        "LEFT JOIN Client ON BON1.CODE_CLIENT = Client.CODE_CLIENT " +
                        "WHERE BLOCAGE = 'F' ORDER BY BON1.NUM_BON";


                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy");

                bon1s = controller.select_all_bon1_from_database(querry);

                list_produit_not_exported = update_produit_into_server(con, stmt);

                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

                // Get CODE_CAISSE
                String CODE_CAISSE = "000000";
                String requette_vendeur_caisse = "SELECT coalesce(CODE_VENDEUR, '000000') AS CODE_VENDEUR, coalesce(CODE_CAISSE, '000000') AS CODE_CAISSE FROM DEPOT1 WHERE CODE_DEPOT = '" + code_depot + "' ";
                ResultSet rs111 = stmt.executeQuery(requette_vendeur_caisse);
                while (rs111.next()) {
                    CODE_CAISSE = rs111.getString("CODE_CAISSE");
                }

                for (int i = 0; i < bon1s.size(); i++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format.parse(bon1s.get(i).date_bon);
                        assert dt != null;
                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean client_exist = false;
                        // check client if exist
                        String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + bon1s.get(i).code_client + "'";
                        ResultSet rs0 = stmt.executeQuery(client_requete);
                        while (rs0.next()) {
                            client_exist = true;
                        }

                        // insert client if not exist
                        if (!client_exist) {
                            client = new PostData_Client();

                             //Select client from data base to insert it
                             client = controller.select_client_from_database(bon1s.get(i).code_client);

                            String insert_client;

                            // insert client
                            String deport_or_vendeur = "null";
                            if(code_depot.equals("000000")){
                                deport_or_vendeur  = code_vendeur;
                            }else {
                                deport_or_vendeur = code_depot;
                            }

                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, LATITUDE, LONGITUDE, CODE_DEPOT, CODE_VENDEUR) VALUES ('"
                                    + client.code_client + "' , " +
                                    "iif('"+ client.code_client + "' = null,'Client inconnu','" + client.client.replace("'", "''") + " (" + deport_or_vendeur + ")" +"') , " +
                                    "iif('" + client.adresse.replace("'", "''") +"' = null,'Adresse inconnu', " + "'" + client.adresse.replace("'", "''") +"'),  " +
                                    "iif('"+ client.tel +"' = '',0,'"+client.tel +"') , " +
                                    "iif('"+ client.rc +"' = '',0,'"+ client.rc +"') , " +
                                    "iif('"+ client.ifiscal +"' = '',0,'"+ client.ifiscal + "') , " +
                                    "iif('"+ client.ai +"' = '',0,'"+ client.ai + "'),  " +
                                    "iif('" + client.nis +"' = '',0,'" + client.nis +"'), " +
                                    "'" + client.mode_tarif + "' , " +
                                    "'" + client.latitude+"', " +
                                    "'" + client.longitude + "'," +
                                    "iif('" + code_depot +"' = '000000',null,'" + code_depot +"')," +
                                    "iif('" + code_vendeur +"' = '000000',null,'" + code_vendeur +"'))";

                            stmt.executeUpdate(insert_client);
                            con.commit();

                        }

                        String file_name = "";
                        file_name = "VENTE_"+ bon1s.get(i).num_bon+"_"+bon1s.get(i).exportation + "_" + bon1s.get(i).date_bon + ".BLV";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_bon1_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM BON1 WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_bon1_exist = true;
                        }

                        if (!check_bon1_exist) {
                            String querry_select = "" +
                                    "SELECT " +
                                    "BON2.RECORDID, " +
                                    "BON2.CODE_BARRE, " +
                                    "BON2.NUM_BON, " +
                                    "BON2.PRODUIT, " +
                                    "BON2.NBRE_COLIS, " +
                                    "BON2.COLISSAGE, " +
                                    "BON2.QTE, " +
                                    "BON2.QTE_GRAT, " +
                                    "BON2.PU, " +
                                    "BON2.TVA, " +
                                    "BON2.CODE_DEPOT, " +
                                    "BON2.DESTOCK_TYPE, " +
                                    "BON2.DESTOCK_CODE_BARRE, " +
                                    "BON2.DESTOCK_QTE, " +
                                    "PRODUIT.ISNEW, " +
                                    "PRODUIT.STOCK " +
                                    "FROM BON2 LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                    "WHERE BON2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


                            bon2s = controller.select_bon2_from_database(querry_select);
                            String[] buffer = new String[bon2s.size() + 1];

                            // Get RECORDID
                            String generator_requet = "SELECT gen_id(gen_bon1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            while (rs1.next()) {
                                recordid_numbon = rs1.getInt("RECORDID");
                            }


                            String insert_into_journee = "UPDATE OR INSERT INTO JOURNEE  (DATE_JOURNEE) VALUES ( '" + format2.format(dt) + "' ) MATCHING (DATE_JOURNEE) ";
                            stmt.executeUpdate(insert_into_journee);

                            String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);


                            String insert_into_bon1 = "INSERT INTO BON1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, CODE_DEPOT, CODE_VENDEUR, CODE_CAISSE ";
                            if(TYPE_LOGICIEL.equals("PME PRO")){
                                insert_into_bon1 = insert_into_bon1 + ", LATITUDE, LONGITUDE ";
                            }
                            insert_into_bon1 = insert_into_bon1 + ") VALUES ('"
                                    + recordid_numbon + "' ," +
                                    " '" + NUM_BON + "' , '" + format2.format(dt) + "' ," +
                                    " '" + bon1s.get(i).heure + "', "+
                                    " '" + bon1s.get(i).code_client.replace("'", "''") + "' ," +
                                    // " '" + bon1s.get(i).tot_ht + "' ," +
                                    //  " '" + bon1s.get(i).tot_tva + "' ," +
                                    " iif('" + bon1s.get(i).timbre + "' = 'null',0,'" + bon1s.get(i).timbre + "')," +
                                    " iif('" + bon1s.get(i).remise + "' = 'null',0,'" + bon1s.get(i).remise + "')," +
                                    " '" + bon1s.get(i).verser + "', '" + bon1s.get(i).solde_ancien + "'," +
                                    " iif('" + bon1s.get(i).mode_rg + "' = 'null',null,'" + bon1s.get(i).mode_rg + "') ," +
                                    " 'TERMINAl_MOBILE'," +
                                    " iif('" + bon1s.get(i).mode_tarif + "' = 'null',0,'" + bon1s.get(i).mode_tarif + "')," +
                                    " '" + file_name + "'," +
                                    " 'F' ," +
                                    " iif('" + code_depot + "' = '000000', null,'" + code_depot + "')," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null,'" + CODE_CAISSE + "')";

                            if(TYPE_LOGICIEL.equals("PME PRO")){
                                insert_into_bon1 = insert_into_bon1 + "," + bon1s.get(i).latitude + "," + bon1s.get(i).longitude ;
                            }
                            insert_into_bon1 = insert_into_bon1 + ")";

                            stmt.addBatch(insert_into_bon1);

                            for (int j = 0; j < bon2s.size(); j++) {
                                buffer[j] = "INSERT INTO BON2 (NUM_BON, CODE_BARRE, QTE, PV_HT_AR, PV_HT, TVA, NBRE_COLIS, COLISSAGE, DESTOCK_QTE, CODE_DEPOT, DESTOCK_CODE_BARRE, DESTOCK_TYPE ";
                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", QTE_GRAT, PRODUIT ";
                                }
                                buffer[j] = buffer[j] + ") VALUES (" +
                                        " '" + NUM_BON + "' ," +
                                        " '" + bon2s.get(j).codebarre.replace("'", "''") + "' ," +
                                        " '" + bon2s.get(j).qte + "'," +
                                        " '" + bon2s.get(j).p_u + "' ," +
                                        " '" + bon2s.get(j).p_u + "' ," +
                                        " '" + bon2s.get(j).tva + "' ," +
                                        " '" + bon2s.get(j).nbr_colis + "'," +
                                        " '" + bon2s.get(j).colissage + "'," +
                                        " '" + bon2s.get(j).destock_qte + "'," +
                                        " iif('" + code_depot + "' = '000000', null,'" + code_depot + "')," +
                                        " iif('" + bon2s.get(j).destock_code_barre + "' = 'null', null,'" + bon2s.get(j).destock_code_barre + "')," +
                                        " iif('" + bon2s.get(j).destock_type + "' = 'null', null,'" + bon2s.get(j).destock_type + "') ";

                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", '" + bon2s.get(j).gratuit + "', '" + bon2s.get(j).produit.replace("'", "''") + "' ";
                                }
                                buffer[j] = buffer[j] + ")";

                                stmt.addBatch(buffer[j]);

                            }

                         //   stmt.executeBatch();

                            String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                    + bon1s.get(i).code_client.replace("'", "''") + "', " +
                                    " '" + format2.format(dt) + "', " +
                                    " '" + bon1s.get(i).heure + "', " +
                                    " '" + bon1s.get(i).montant_bon + "' ," +
                                    " '" + bon1s.get(i).verser + "' ," +
                                    " 'BL-VENTE' ," +
                                    " '" + NUM_BON + "'," +
                                    " '" + bon1s.get(i).mode_rg + "'," +
                                    " 'TERMINAL_MOBILE'," +
                                    " ' '," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null ,'" + CODE_CAISSE + "'))";

                            stmt.addBatch(requete_situation);


                            if (!CODE_CAISSE.equals("000000")) {
                                if (bon1s.get(i).verser != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "' ," +
                                            " '" + CODE_CAISSE + "'," +
                                            " '" + format2.format(dt) + "' ," +
                                            " '" + bon1s.get(i).verser + "', " +
                                            " '0'," +
                                            " 'BL-VENTE' ," +
                                            " '" + NUM_BON + "'," +
                                            " 'ESPECE'," +
                                            " ' '," +
                                            " 'TERMINAL_MOBILE')";
                                    stmt.addBatch(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            // update bon as exported
                            controller.update_ventes_commandes_as_exported(false, bon1s.get(i).num_bon);
                            bon_inserted++;
                            stmt.clearBatch();
                            bon2s.clear();

                        }
                    }catch (Exception e){
                        con.rollback();
                        list_num_bon_not_exported.add( bon1s.get(i).num_bon);
                        stmt.clearBatch();
                    }

                }

                // Export all versement

                // INSERT ALL VERSEMENTS OF CAURANT CLIENTS
                ArrayList<PostData_Carnet_c> all_versement_client;
                ArrayList<PostData_Client> list_client ;

                all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                        "CARNET_C.CODE_CLIENT, " +
                        "CARNET_C.DATE_CARNET, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.LATITUDE, " +

                        "CLIENT.LONGITUDE, " +
                        "CLIENT.TEL, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.ADRESSE, " +
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
                        "CARNET_C.EXPORTATION " +


                        "FROM CARNET_C " +
                        "LEFT JOIN CLIENT ON " +
                        "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");



                for (int g = 0; g < all_versement_client.size(); g++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format.parse(all_versement_client.get(g).carnet_date);
                        assert dt != null;

                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean client_exist = false;
                        // check client if exist
                        String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + all_versement_client.get(g).code_client + "'";
                        ResultSet rs0 = stmt.executeQuery(client_requete);
                        while (rs0.next()) {
                            client_exist = true;
                        }

                        // insert client if not exist
                        if (!client_exist) {
                            client = new PostData_Client();

                            // Select client from data base to insert it
                             client = controller.select_client_from_database(bon1s.get(g).code_client);
                            String insert_client;
                            // insert client
                            String deport_or_vendeur = "null";
                            if(code_depot.equals("000000")){
                                deport_or_vendeur  = code_vendeur;
                            }else {
                                deport_or_vendeur = code_depot;
                            }

                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, LATITUDE, LONGITUDE, CODE_DEPOT, CODE_VENDEUR) VALUES ('"
                                    + client.code_client + "' , " +
                                    "iif('"+ client.code_client + "' = null,'Client inconnu','" + client.client.replace("'", "''") + " (" + deport_or_vendeur + ")" +"') , " +
                                    "iif('" + client.adresse.replace("'", "''") +"' = null,'Adresse inconnu', " + "'" + client.adresse.replace("'", "''") +"'),  " +
                                    "iif('"+ client.tel +"' = '',0,'"+client.tel +"') , " +
                                    "iif('"+ client.rc +"' = '',0,'"+ client.rc +"') , " +
                                    "iif('"+ client.ifiscal +"' = '',0,'"+ client.ifiscal + "') , " +
                                    "iif('"+ client.ai +"' = '',0,'"+ client.ai + "'),  " +
                                    "iif('" + client.nis +"' = '',0,'" + client.nis +"'), " +
                                    "'" + client.mode_tarif + "' , " +
                                    "'" + client.latitude+"', " +
                                    "'" + client.longitude + "'," +
                                    "iif('" + code_depot +"' = '000000',null,'" + code_depot +"')," +
                                    "iif('" + code_vendeur +"' = '000000',null,'" + code_vendeur +"'))";


                            stmt.executeUpdate(insert_client);
                            con.commit();
                        }

                        String file_name = "";
                        file_name = "SITUATION_VRC"+ all_versement_client.get(g).recordid + "_" + all_versement_client.get(g).exportation + "_" + all_versement_client.get(g).carnet_date + ".STC";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_carnet_c_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM CARNET_C WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_carnet_c_exist = true;
                        }

                        if(!check_carnet_c_exist){

                            // Get carnet_c recordid
                            String generator_requet = "SELECT gen_id(gen_carnet_c_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            int recordid_carnet_c = 0;
                            while (rs1.next()) {
                                recordid_carnet_c = rs1.getInt("RECORDID");
                            }


                            //String RECORDID_CRC = Get_Digits_String(String.valueOf(num_bon_carnet_c), 6);
                            String buffer_versement = "INSERT INTO CARNET_C (RECORDID, CODE_CLIENT, DATE_CARNET, HEURE, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, EXPORTATION , CODE_VENDEUR, CODE_CAISSE) VALUES (" +
                                    " '" + recordid_carnet_c + "', " +
                                    " '"  + all_versement_client.get(g).code_client.replace("'", "''") + "' ," +
                                    " '" + format2.format(dt) + "'  ," +
                                    " '" + all_versement_client.get(g).carnet_heure + "' ," +
                                    " '" + all_versement_client.get(g).carnet_versement + "' ," +
                                    " 'SITUATION-CLIENT' ," +
                                    " 'VRC" + recordid_carnet_c + "'," +
                                    " '" + all_versement_client.get(g).carnet_mode_rg + "'," +
                                    " 'TERMINAL_MOBILE'," +
                                    " '" + all_versement_client.get(g).carnet_remarque.replace("'", "''") + "'," +
                                    " '" + file_name + "'," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null,'" + CODE_CAISSE + "'))";
                            stmt.addBatch(buffer_versement);


                            if (!CODE_CAISSE.equals("000000")) {
                                if (all_versement_client.get(g).carnet_versement != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , '" + format2.format(dt) + "' , '" + all_versement_client.get(g).carnet_versement +
                                            "' , '0', 'SITUATION-CLIENT' , 'VRC" + recordid_carnet_c + "', 'ESPECE',  '" + all_versement_client.get(g).carnet_remarque + "', 'TERMINAL_MOBILE')";
                                    stmt.addBatch(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            controller.update_versement_exported(all_versement_client.get(g).recordid);
                            stmt.clearBatch();
                            total_versement++;

                        }

                    }catch (Exception e){
                        con.rollback();
                        stmt.clearBatch();
                        list_recordid_versement_not_exported.add("VRC"+all_versement_client.get(g).recordid + " - " + all_versement_client.get(g).client + " :  " + nf.format(all_versement_client.get(g).carnet_versement) );
                    }

                }

                flag = 1;


            } catch (Exception ex) {
                ex.printStackTrace();
               // con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    //not executed with problem in the sql statement

                    flag = 3;
                }

                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                erreurMessage = ex.getMessage();
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            // Problem insert client into database // operation aborded
            if (integer == 1) {
                String nums_bon_error = "";
                String versement_error = "";
                for(int i = 0; i < list_num_bon_not_exported.size(); i++){
                    if(i > 0){
                        nums_bon_error = nums_bon_error + " ,  ";
                    }
                    nums_bon_error = nums_bon_error + list_num_bon_not_exported.get(i);
                }
                if(list_num_bon_not_exported.size() > 0){
                    nums_bon_error = "\nProblem exportation dans les bons : " + nums_bon_error;
                }
                for(int i = 0; i < list_recordid_versement_not_exported.size() ; i++){

                    if(i > 0){
                        versement_error = versement_error + " \n  ";
                    }
                    versement_error = versement_error + list_recordid_versement_not_exported.get(i);

                }
                if(list_recordid_versement_not_exported.size() > 0){
                    versement_error = "\nProblem exportation dans les versements nums : \n" + versement_error;
                }
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé \n Nombre de bons exportés : " + bon_inserted + "\n ==================" + "\n  Nombre de versements exportés : " + total_versement + "\n ==================" + nums_bon_error  + versement_error )
                        .show();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            } else if (integer == 3) {
                //  if(ActivityImportsExport. != null)
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }

    ////////////////////////////////EXPORT COMMANDE TO THE SERVER //////////////////////////////////

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Exporter_commandes_to_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String erreurMessage = "";

        int bon_inserted = 0;
        int total_versement = 0;

        List<String> list_produit_not_exported;
        List<String> list_num_bon_not_exported;
        List<String> list_recordid_versement_not_exported;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nf = NumberFormat.getInstance(Locale.US);
            ((DecimalFormat) nf).applyPattern("###,##0.00");
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                
                ArrayList<PostData_Bon1> bon1s_Temp;
                ArrayList<PostData_Bon2> bon2s_Temp;
                PostData_Client client;
                list_produit_not_exported = new ArrayList<>();
                list_num_bon_not_exported = new ArrayList<>();
                list_recordid_versement_not_exported = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;

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

                        "BON1_TEMP.ANCIEN_SOLDE, " +
                        "BON1_TEMP.VERSER, " +
                        "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

                        "BON1_TEMP.CODE_CLIENT, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.ADRESSE, " +
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

                        "BON1_TEMP.CODE_DEPOT, " +
                        "BON1_TEMP.CODE_VENDEUR, " +
                        "BON1_TEMP.EXPORTATION, " +
                        "BON1_TEMP.BLOCAGE " +
                        "FROM BON1_TEMP " +
                        "LEFT JOIN CLIENT ON BON1_TEMP.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                        "WHERE BLOCAGE = 'F' ORDER BY BON1_TEMP.NUM_BON";


                SimpleDateFormat format_local = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat format_distant = new SimpleDateFormat("MM/dd/yyyy");

                bon1s_Temp = controller.select_all_bon1_from_database(querry);

                list_produit_not_exported = update_produit_into_server(con, stmt);

                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");
                // Get CODE_CAISSE

                String CODE_CAISSE = "000000";
                String requette_vendeur_caisse = "SELECT coalesce(CODE_VENDEUR, '000000') AS CODE_VENDEUR, coalesce(CODE_CAISSE, '000000') AS CODE_CAISSE FROM DEPOT1 WHERE CODE_DEPOT = '" + code_depot + "' ";
                ResultSet rs111 = stmt.executeQuery(requette_vendeur_caisse);
                while (rs111.next()) {
                    CODE_CAISSE = rs111.getString("CODE_CAISSE");
                }

                for (int i = 0; i < bon1s_Temp.size(); i++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format_local.parse(bon1s_Temp.get(i).date_bon);
                        assert dt != null;
                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean client_exist = false;
                        // check client if exist
                        String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + bon1s_Temp.get(i).code_client + "'";
                        ResultSet rs0 = stmt.executeQuery(client_requete);
                        while (rs0.next()) {
                            client_exist = true;
                        }

                        // insert client if not exist
                        if (!client_exist) {
                            client = new PostData_Client();

                            // Select client from data base to insert it
                             client = controller.select_client_from_database(bon1s_Temp.get(i).code_client);
                            String insert_client;
                            // insert client
                            String deport_or_vendeur = "null";
                            if(code_depot.equals("000000")){
                                deport_or_vendeur  = code_vendeur;
                            }else {
                                deport_or_vendeur = code_depot;
                            }

                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, LATITUDE, LONGITUDE, CODE_DEPOT, CODE_VENDEUR) VALUES ('"
                                    + client.code_client + "' , " +
                                    "iif('"+ client.code_client + "' = null,'Client inconnu','" + client.client.replace("'", "''") + " (" + deport_or_vendeur + ")" +"') , " +
                                    "iif('" + client.adresse.replace("'", "''") +"' = null,'Adresse inconnu', " + "'" + client.adresse.replace("'", "''") +"'),  " +
                                    "iif('"+ client.tel +"' = '',0,'"+client.tel +"') , " +
                                    "iif('"+ client.rc +"' = '',0,'"+ client.rc +"') , " +
                                    "iif('"+ client.ifiscal +"' = '',0,'"+ client.ifiscal + "') , " +
                                    "iif('"+ client.ai +"' = '',0,'"+ client.ai + "'),  " +
                                    "iif('" + client.nis +"' = '',0,'" + client.nis +"'), " +
                                    "'" + client.mode_tarif + "' , " +
                                    "'" + client.latitude+"', " +
                                    "'" + client.longitude + "'," +
                                    "iif('" + code_depot +"' = '000000',null,'" + code_depot +"')," +
                                    "iif('" + code_vendeur +"' = '000000',null,'" + code_vendeur +"'))";


                            stmt.executeUpdate(insert_client);
                            con.commit();
                        }

                        String file_name = "";
                        file_name = "COMMANDE_"+ bon1s_Temp.get(i).num_bon+"_"+bon1s_Temp.get(i).exportation + "_" + bon1s_Temp.get(i).date_bon + ".BLV";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_bon1_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM BCC1 WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_bon1_exist = true;
                        }

                        if (!check_bon1_exist) {
                            String querry_select = "" +
                                    "SELECT " +
                                    "BON2_TEMP.RECORDID, " +
                                    "BON2_TEMP.CODE_BARRE, " +
                                    "BON2_TEMP.NUM_BON, " +
                                    "BON2_TEMP.PRODUIT, " +
                                    "BON2_TEMP.NBRE_COLIS, " +
                                    "BON2_TEMP.COLISSAGE, " +
                                    "BON2_TEMP.QTE, " +
                                    "BON2_TEMP.QTE_GRAT, " +
                                    "BON2_TEMP.PU, " +
                                    "BON2_TEMP.TVA, " +
                                    "BON2_TEMP.CODE_DEPOT, " +
                                    "BON2_TEMP.DESTOCK_TYPE, " +
                                    "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                                    "BON2_TEMP.DESTOCK_QTE, " +
                                    "PRODUIT.ISNEW, " +
                                    "PRODUIT.STOCK " +
                                    "FROM BON2_TEMP LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                    "WHERE BON2_TEMP.NUM_BON = '" + bon1s_Temp.get(i).num_bon + "'";


                            bon2s_Temp = controller.select_bon2_from_database(querry_select);

                            String[] buffer = new String[bon2s_Temp.size() + 1];

                            // Get RECORDID
                            String generator_requet = "SELECT gen_id(gen_bcc1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            while (rs1.next()) {
                                recordid_numbon = rs1.getInt("RECORDID");
                            }

                            String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);

                            String insert_into_journee = "UPDATE OR INSERT INTO JOURNEE  (DATE_JOURNEE) VALUES ( '" + format_distant.format(dt) + "' ) MATCHING (DATE_JOURNEE) ";
                            stmt.executeUpdate(insert_into_journee);

                            String insert_into_bon1 = "INSERT INTO BCC1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, CODE_DEPOT, CODE_VENDEUR ";
                            if(TYPE_LOGICIEL.equals("PME PRO")){
                                insert_into_bon1 = insert_into_bon1 + ", LATITUDE, LONGITUDE ";
                            }
                            insert_into_bon1 = insert_into_bon1  + ") VALUES ('" + recordid_numbon + "' ," +
                                    " '" + NUM_BON + "' , '" + format_distant.format(dt) + "' ," +
                                    " '" + bon1s_Temp.get(i).heure + "', "+
                                    " '" + bon1s_Temp.get(i).code_client.replace("'", "''") + "' ," +
                                    " iif('" + bon1s_Temp.get(i).timbre + "' = 'null',0,'" + bon1s_Temp.get(i).timbre + "')," +
                                    " iif('" + bon1s_Temp.get(i).remise + "' = 'null',0,'" + bon1s_Temp.get(i).remise + "')," +
                                    " '" + bon1s_Temp.get(i).verser + "', '" + bon1s_Temp.get(i).solde_ancien + "'," +
                                    " iif('" + bon1s_Temp.get(i).mode_rg + "' = 'null',null,'" + bon1s_Temp.get(i).mode_rg + "') ," +
                                    " 'TERMINAl_MOBILE'," +
                                    " iif('" + bon1s_Temp.get(i).mode_tarif + "' = 'null',0,'" + bon1s_Temp.get(i).mode_tarif + "')," +
                                    " '" + file_name + "'," +
                                    " 'F' ," +
                                    " iif('" + code_depot + "' = '000000', null,'" + code_depot + "')," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";


                            if(TYPE_LOGICIEL.equals("PME PRO")){
                                insert_into_bon1 = insert_into_bon1 + "," + bon1s_Temp.get(i).latitude + "," + bon1s_Temp.get(i).longitude ;
                            }
                            insert_into_bon1 = insert_into_bon1 + ")";

                            stmt.addBatch(insert_into_bon1);


                            for (int j = 0; j < bon2s_Temp.size(); j++) {

                                buffer[j] = "INSERT INTO BCC2 (NUM_BON, CODE_BARRE, QTE, PV_HT, TVA, NBRE_COLIS, COLISSAGE, DESTOCK_QTE, CODE_DEPOT, DESTOCK_CODE_BARRE, DESTOCK_TYPE ";
                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", QTE_GRAT, PRODUIT ";
                                }

                                buffer[j] = buffer[j] + ") VALUES ( '" + NUM_BON + "' ," +
                                        " '" + bon2s_Temp.get(j).codebarre.replace("'", "''") + "' ," +
                                        " '" + bon2s_Temp.get(j).qte + "'," +
                                        " '" + bon2s_Temp.get(j).p_u + "' ," +
                                        " '" + bon2s_Temp.get(j).tva + "' ," +
                                        " '" + bon2s_Temp.get(j).nbr_colis + "'," +
                                        " '" + bon2s_Temp.get(j).colissage + "'," +
                                        " '" + bon2s_Temp.get(j).destock_qte + "'," +
                                        " iif('" + code_depot + "' = '000000', null,'" + code_depot + "')," +
                                        " iif('" + bon2s_Temp.get(j).destock_code_barre + "' = 'null', null,'" + bon2s_Temp.get(j).destock_code_barre + "')," +
                                        " iif('" + bon2s_Temp.get(j).destock_type + "' = 'null', null,'" + bon2s_Temp.get(j).destock_type + "')";

                                if(TYPE_LOGICIEL.equals("PME PRO")){
                                    buffer[j] = buffer[j] + ", '" + bon2s_Temp.get(j).gratuit + "', '" + bon2s_Temp.get(j).produit.replace("'", "''") + "' ";
                                }
                                buffer[j] = buffer[j] + ")";

                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            con.commit();
                            // update bon as exported
                            controller.update_ventes_commandes_as_exported(true, bon1s_Temp.get(i).num_bon);
                            bon_inserted++;
                            stmt.clearBatch();
                            bon2s_Temp.clear();

                        }
                    }catch (Exception e){
                        con.rollback();
                        list_num_bon_not_exported.add( bon1s_Temp.get(i).num_bon);
                        stmt.clearBatch();
                    }

                }

                // Export all versement

                // INSERT ALL VERSEMENTS OF CAURANT CLIENTS
                ArrayList<PostData_Carnet_c> all_versement_client;
                ArrayList<PostData_Client> list_client ;

                all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                        "CARNET_C.CODE_CLIENT, " +
                        "CARNET_C.DATE_CARNET, " +

                        "CLIENT.CLIENT, " +
                        "CLIENT.LATITUDE, " +
                        "CLIENT.LONGITUDE, " +
                        "CLIENT.TEL, " +
                        "CLIENT.CLIENT, " +
                        "CLIENT.ADRESSE, " +
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
                        "CARNET_C.EXPORTATION " +


                        "FROM CARNET_C " +
                        "LEFT JOIN CLIENT ON " +
                        "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");



                for (int g = 0; g < all_versement_client.size(); g++) {

                    try {

                        stmt.clearBatch();
                        Date dt = format_local.parse(all_versement_client.get(g).carnet_date);
                        assert dt != null;


                        /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                        boolean client_exist = false;
                        // check client if exist
                        String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + all_versement_client.get(g).code_client + "'";
                        ResultSet rs0 = stmt.executeQuery(client_requete);
                        while (rs0.next()) {
                            client_exist = true;
                        }

                        // insert client if not exist
                        if (!client_exist) {

                            client = new PostData_Client();

                            // Select client from data base to insert it
                            client = controller.select_client_from_database(all_versement_client.get(g).code_client);
                            String insert_client;
                            // insert client
                            String deport_or_vendeur = "null";
                            if(code_depot.equals("000000")){
                                deport_or_vendeur  = code_vendeur;
                            }else {
                                deport_or_vendeur = code_depot;
                            }

                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, LATITUDE, LONGITUDE, CODE_DEPOT, CODE_VENDEUR) VALUES ('"
                                    + client.code_client + "' , " +
                                    "iif('"+ client.code_client + "' = null,'Client inconnu','" + client.client.replace("'", "''") + " (" + deport_or_vendeur + ")" +"') , " +
                                    "iif('" + client.adresse.replace("'", "''") +"' = null,'Adresse inconnu', " + "'" + client.adresse.replace("'", "''") +"'),  " +
                                    "iif('"+ client.tel +"' = '',0,'"+client.tel +"') , " +
                                    "iif('"+ client.rc +"' = '',0,'"+ client.rc +"') , " +
                                    "iif('"+ client.ifiscal +"' = '',0,'"+ client.ifiscal + "') , " +
                                    "iif('"+ client.ai +"' = '',0,'"+ client.ai + "'),  " +
                                    "iif('" + client.nis +"' = '',0,'" + client.nis +"'), " +
                                    "'" + client.mode_tarif + "' , " +
                                    "'" + client.latitude+"', " +
                                    "'" + client.longitude + "'," +
                                    "iif('" + code_depot +"' = '000000',null,'" + code_depot +"')," +
                                    "iif('" + code_vendeur +"' = '000000',null,'" + code_vendeur +"'))";


                            stmt.executeUpdate(insert_client);
                            con.commit();
                        }

                        String file_name = "";
                        file_name = "SITUATION_VRC"+ all_versement_client.get(g).recordid + "_" + all_versement_client.get(g).exportation + "_" + all_versement_client.get(g).carnet_date + ".STC";
                        file_name = file_name.replace("/", "_");

                        // check if bon1 exist
                        boolean check_carnet_c_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM CARNET_C WHERE EXPORTATION = '" + file_name + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_carnet_c_exist = true;
                        }

                        if(!check_carnet_c_exist){

                            // Get carnet_c recordid
                            String generator_requet = "SELECT gen_id(gen_carnet_c_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            int recordid_carnet_c = 0;
                            while (rs1.next()) {
                                recordid_carnet_c = rs1.getInt("RECORDID");
                            }


                            //String RECORDID_CRC = Get_Digits_String(String.valueOf(num_bon_carnet_c), 6);
                            String buffer_versement = "INSERT INTO CARNET_C (RECORDID, CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, EXPORTATION , CODE_VENDEUR, CODE_CAISSE) VALUES (" +
                                    " '" + recordid_carnet_c + "', " +
                                    " '"  + all_versement_client.get(g).code_client.replace("'", "''") + "' ," +
                                    " '" + format_distant.format(dt) + "'  ," +
                                    " '" + all_versement_client.get(g).carnet_heure + "' ," +
                                    " '" + all_versement_client.get(g).carnet_achats + "' ," +
                                    " '" + all_versement_client.get(g).carnet_versement + "' ," +
                                    " 'SITUATION-CLIENT' ," +
                                    " 'VRC" + recordid_carnet_c + "'," +
                                    " '" + all_versement_client.get(g).carnet_mode_rg + "'," +
                                    " 'TERMINAL_MOBILE'," +
                                    " '" + all_versement_client.get(g).carnet_remarque.replace("'", "''") + "'," +
                                    " '" + file_name + "'," +
                                    " iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')," +
                                    " iif('" + CODE_CAISSE + "' = '000000', null,'" + CODE_CAISSE + "'))";
                            stmt.addBatch(buffer_versement);


                            if (!CODE_CAISSE.equals("000000")) {
                                if (all_versement_client.get(g).carnet_versement != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , '" + format_distant.format(dt) + "' , '" + all_versement_client.get(g).carnet_versement +
                                            "' , '0', 'SITUATION-CLIENT' , 'VRC" + recordid_carnet_c + "', 'ESPECE',  '" + all_versement_client.get(g).carnet_remarque + "', 'TERMINAL_MOBILE')";
                                    stmt.addBatch(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            controller.update_versement_exported(all_versement_client.get(g).recordid);
                            stmt.clearBatch();
                            total_versement++;

                        }

                    }catch (Exception e){
                        con.rollback();
                        stmt.clearBatch();
                        list_recordid_versement_not_exported.add("VRC"+all_versement_client.get(g).recordid + " - " + all_versement_client.get(g).client + " :  " + nf.format(all_versement_client.get(g).carnet_versement) );
                    }

                }

                flag = 1;


            } catch (Exception ex) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                    erreurMessage = ex.getMessage();
                } else {
                    //not executed with problem in the sql statement
                    erreurMessage = ex.getMessage();
                    flag = 3;
                }
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            // Problem insert client into database // operation aborded
            if (integer == 1) {
                String nums_bon_error = "";
                String versement_error = "";
                for(int i = 0; i < list_num_bon_not_exported.size(); i++){
                    if(i > 0){
                        nums_bon_error = nums_bon_error + " ,  ";
                    }
                    nums_bon_error = nums_bon_error + list_num_bon_not_exported.get(i);
                }
                if(list_num_bon_not_exported.size() > 0){
                    nums_bon_error = "\nProblem exportation dans les bons de commande : " + nums_bon_error;
                }
                for(int i = 0; i < list_recordid_versement_not_exported.size() ; i++){

                    if(i > 0){
                        versement_error = versement_error + " \n  ";
                    }
                    versement_error = versement_error + list_recordid_versement_not_exported.get(i);

                }
                if(list_recordid_versement_not_exported.size() > 0){
                    versement_error = "\nProblem exportation dans les versements nums : \n" + versement_error;
                }
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé \n Nombre de commandes exportés : " + bon_inserted + "\n ==================" + "\n  Nombre de versements exportés : " + total_versement + "\n ==================" + nums_bon_error  + versement_error )
                        .show();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            } else if (integer == 3) {
                //  if(ActivityImportsExport. != null)
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }

    // Importation liste fournisseur
    //==================== AsyncTask TO Load FOURNISSEUR from server and store them in the local database (sqlite)
    public class Check_connection_fournisseur_server_for_sychronisation_fournisseur extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;

        String messageError = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérificaion de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError= e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                Import_fournisseur_from_server_task fournisseur_task = new Import_fournisseur_from_server_task();
                fournisseur_task.execute();

            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }

    }



    // Importation liste client
    //==================== AsyncTask TO Load CLIENTS from server and store them in the local database (sqlite)
    public class Check_connection_client_server_for_sychronisation_client extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;

        String messageError = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérificaion de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError= e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                Import_client_from_server_task bon_client_task = new Import_client_from_server_task();
                bon_client_task.execute();

            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }

    }


    // Importation liste produit
    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_produit_server_for_sychronisation_produit extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;

        String messageError = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérification de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                Import_produit_from_server_task produit_task = new Import_produit_from_server_task();
                produit_task.execute();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }

    }


    //==================== AsyncTask TO Load parametres from server and store them in the local database (sqlite)
    public class Check_connection_parametres_server_for_sychronisation_parametres extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String messageError = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free = new ProgressDialog(ActivityImportsExport.this);
            mProgressDialog_Free.setMessage("Vérification de la connexion ...");
            mProgressDialog_Free.setCancelable(true);
            mProgressDialog_Free.show();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            try {

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                e.printStackTrace();
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    flag = 3;
                }
                messageError = e.getMessage();
            }

            return flag;
        }


        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 1) {
                // get all transfert bon of this depot
                Import_parametre_from_server_task parametres_task = new Import_parametre_from_server_task();
                parametres_task.execute();
            } else if (integer == 2) {
                mProgressDialog_Free.dismiss();
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                mProgressDialog_Free.dismiss();
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme fatal : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }

    }

    //==================== AsyncTask TO Load clients from server and store them in the local database (sqlite)
    public class Import_parametre_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 0;
        String messageError = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog_Free.setMessage("Synchronisation paramètres...");
        }

        @Override
        protected Integer doInBackground(Void... params) {

            try {
                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);
            }catch (Exception e){
                flag = 2;
                messageError = e.getMessage();
            }

            try {

                    prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                    String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

                    Statement stmt = con.createStatement();
                    //============================ GET Entrprise info ===========================================
                    String sql0 = "SELECT  ENTREPRISE.NOM, ENTREPRISE.ACTIVITE, ENTREPRISE.TEL, ENTREPRISE.ADRESSE, ENTREPRISE.LOGO, ENTREPRISE.NUM_RC, ENTREPRISE.NUM_ART, ENTREPRISE.NUM_NIF, ENTREPRISE.NUM_NIS, ENTREPRISE.COND_VENTES2 FROM ENTREPRISE";
                    ResultSet rs0 = stmt.executeQuery(sql0);
                    while (rs0.next()) {

                        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                        editor.putString("COMPANY_NAME", rs0.getString("NOM"));
                        editor.putString("ACTIVITY_NAME", rs0.getString("ACTIVITE"));
                        editor.putString("COMPANY_TEL", rs0.getString("TEL"));
                        editor.putString("COMPANY_ADRESSE", rs0.getString("ADRESSE"));
                        editor.putString("COMPANY_NUM_RC", rs0.getString("NUM_RC"));
                        editor.putString("COMPANY_NUM_ART", rs0.getString("NUM_ART"));
                        editor.putString("COMPANY_NUM_NIF", rs0.getString("NUM_NIF"));
                        editor.putString("COMPANY_NUM_NIS", rs0.getString("NUM_NIS"));
                        editor.putString("COMPANY_FOOTER", rs0.getString("COND_VENTES2"));

                        byte[] image = rs0.getBytes("LOGO");
                        if(image != null){
                            String img_str = Base64.encodeToString(image, 0);
                            editor.putString("COMPANY_LOGO",img_str);
                        }else {
                            editor.putString("COMPANY_LOGO","");
                        }
                        editor.apply();
                    }

                //============================ GET PARAMS ===========================================
                SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                if(TYPE_LOGICIEL.equals("PME PRO")){
                    String sql1 = "SELECT  PARAMS.QTE_GRAT FROM PARAMS";
                    ResultSet rs1 = stmt.executeQuery(sql1);
                    while (rs1.next()) {
                        editor.putBoolean("VENTE_WITH_QTE_GRAT", rs1.getInt("QTE_GRAT") == 1);
                    }
                }else {
                    editor.putBoolean("VENTE_WITH_QTE_GRAT", false);
                }
                editor.apply();


                if(TYPE_LOGICIEL.equals("PME PRO")){
                    //============================ GET PARAMS2 =========================================
                    String sql2 = "SELECT PARAMS2.PARAMETRES, PARAMS2.VALEURE FROM PARAMS2";
                    ResultSet rs2 = stmt.executeQuery(sql2);

                    PostData_Params params2 = new PostData_Params();
                    while (rs2.next()) {

                        //======================= PRIX ACTIVE ========================
                        if(rs2.getString("PARAMETRES").equals("PRIX_2")){
                            params2.prix_2 = rs2.getInt("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PRIX_3")){
                            params2.prix_3= rs2.getInt("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PRIX_4")){
                            params2.prix_4 = rs2.getInt("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PRIX_5")){
                            params2.prix_5 = rs2.getInt("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PRIX_6")){
                            params2.prix_6 = rs2.getInt("VALEURE");
                        }

                        //======================= PRIX TITRE ==========================
                        if(rs2.getString("PARAMETRES").equals("PV1_TITRE")){
                            params2.pv1_titre = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PV2_TITRE")){
                            params2.pv2_titre= rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PV3_TITRE")){
                            params2.pv3_titre = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PV4_TITRE")){
                            params2.pv4_titre = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PV5_TITRE")){
                            params2.pv5_titre = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("PV6_TITRE")){
                            params2.pv6_titre = rs2.getString("VALEURE");
                        }

                        //====================== FTP =================================
                        if(rs2.getString("PARAMETRES").equals("FTP0")){
                            params2.ftp_server = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP1")){
                            params2.ftp_port = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP2")){
                            params2.ftp_user = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP3")){
                            params2.ftp_pass = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("EXP_FTP")){
                            params2.ftp_imp = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("IMP_FTP")){
                            params2.ftp_exp = rs2.getString("VALEURE");
                        }
                    }

                }else {
                    //============================ GET PARAMS2 =========================================
                    String sql2 = "SELECT PARAMS2.PARAMETRES, PARAMS2.VALEURE FROM PARAMS2";
                    ResultSet rs2 = stmt.executeQuery(sql2);

                    PostData_Params params2 = new PostData_Params();
                    while (rs2.next()) {

                        //====================== FTP =================================
                        if(rs2.getString("PARAMETRES").equals("FTP0")){
                            params2.ftp_server = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP1")){
                            params2.ftp_port = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP2")){
                            params2.ftp_user = rs2.getString("VALEURE");
                        }
                        if(rs2.getString("PARAMETRES").equals("FTP3")){
                            params2.ftp_pass = rs2.getString("VALEURE");
                        }

                        params2.ftp_imp = "EXP";
                        params2.ftp_exp = "IMP";
                    }


                    //============================ GET PARAMS =========================================
                    String sql3 = "SELECT PARAMS.PRIX_2, PARAMS.PRIX_3 FROM PARAMS";
                    ResultSet rs3 = stmt.executeQuery(sql3);
                    while (rs3.next()) {
                        //======================= PRIX ACTIVE ========================
                        params2.prix_2 = rs3.getInt("PRIX_2");
                        params2.prix_3 = rs3.getInt("PRIX_3");
                        params2.prix_4 = 0;
                        params2.prix_5 = 0;
                        params2.prix_6 = 0;

                        params2.pv1_titre = "PRIX 1";
                        params2.pv2_titre = "PRIX 2";
                        params2.pv3_titre = "PRIX 3";
                    }

                    controller.insert_into_params(params2);
                }

                stmt.close();

                flag = 1;

            } catch (Exception e) {
                messageError = e.getMessage();
                con = null;
                flag = 3;
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog_Free.dismiss();
            if (integer == 1) {
                // get all transfert bon of this depot
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information !...")
                        .setContentText("Paramètres synchroniser avec succés")
                        .show();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme de connexion, vérifier les parametres : " + messageError)
                        .show();
            } else {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Probleme recupération des données : " + messageError)
                        .show();
            }
            super.onPostExecute(integer);
        }
    }


    //==================== AsyncTask TO Load fournisseur from server and store them in the local database (sqlite)
    public class Import_fournisseur_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 1;
        int compt = 0;
        int allrows = 0;
        int total_fournisseur = 0;

        String erreurMessage = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfigFournisseur();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                ArrayList<PostData_Fournisseur> fournisseurs = new ArrayList<>();
                //postData_Client client;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //-----------------------------------------

                fournisseurs = controller.select_fournisseurs_from_database("SELECT * FROM FOURNIS");
                total_fournisseur = fournisseurs.size();

                String sql12 = "SELECT  COUNT(*) FROM FOURNIS";
                String sql0 = "SELECT  FOURNIS.CODE_FRS, " +
                        "FOURNIS.FOURNIS, " +
                        "coalesce(FOURNIS.ADRESSE , '/') AS ADRESSE, " +
                        "coalesce(FOURNIS.TEL , '/') AS TEL, " +
                        "coalesce(FOURNIS.NUM_RC , '/') AS NUM_RC, " +
                        "coalesce(FOURNIS.NUM_ART , '/') AS NUM_ART, " +
                        "coalesce(FOURNIS.NUM_IF , '/') AS NUM_IF, " +
                        "coalesce(FOURNIS.NUM_IS , '/') AS NUM_IS, " +
                        "coalesce(FOURNIS.ACHATS , 0) AS ACHATS, " +
                        "coalesce(FOURNIS.VERSER , 0) AS VERSER, " +
                        "coalesce(FOURNIS.SOLDE , 0) AS SOLDE " +
                        " FROM FOURNIS";

                ResultSet rs12 = stmt.executeQuery(sql12);

                while (rs12.next()) {
                    allrows = rs12.getInt("COUNT");
                }
                publishProgress(1);
                //============================ GET Clients ===========================================
                fournisseurs.clear();
                ResultSet rs0 = stmt.executeQuery(sql0);
                PostData_Fournisseur fournisseur;

                while (rs0.next()) {
                    fournisseur = new PostData_Fournisseur();
                    fournisseur.code_frs = rs0.getString("CODE_FRS");
                    fournisseur.fournis = rs0.getString("FOURNIS");
                    fournisseur.tel = rs0.getString("TEL");
                    fournisseur.adresse = rs0.getString("ADRESSE");
                    fournisseur.isNew = 0;

                    fournisseur.rc = rs0.getString("NUM_RC");
                    fournisseur.ai = rs0.getString("NUM_ART");
                    fournisseur.ifiscal = rs0.getString("NUM_IF");
                    fournisseur.nis = rs0.getString("NUM_IS");
                    fournisseur.achat_montant = rs0.getDouble("ACHATS");
                    fournisseur.verser_montant = rs0.getDouble("VERSER");
                    fournisseur.solde_montant = rs0.getDouble("SOLDE");


                    fournisseurs.add(fournisseur);

                    compt++;
                    publishProgress(compt);
                }

                controller.ExecuteTransactionFournisseur(fournisseurs);
                //----------------------------------------------------------
                publishProgress(1);
                stmt.close();
                flag = 1;

            } catch (Exception e) {
                con = null;
                if(e.getMessage().contains("SQL Error")){
                    flag = 3;
                }else if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                }
                erreurMessage = e.getMessage();
            }
            return flag;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressDialog.setMax(allrows);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if(result == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information. !")
                        .setContentText("Importation la liste des fournisseurs bien terminé")
                        .show();
            }else if(result == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(result == 3){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Problèm au niveau de la requette Sql : " + erreurMessage)
                        .show();
            }
            super.onPostExecute(result);
        }
    }



    //==================== AsyncTask TO Load clients from server and store them in the local database (sqlite)
    public class Import_client_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 1;
        int compt = 0;
        int allrows = 0;
        int total_client = 0;

        String erreurMessage = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfigClient();
        }
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                ArrayList<PostData_Client> clients = new ArrayList<>();
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();
                //postData_Client client;

                boolean executed = false;

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                //-----------------------------------------


                con.setAutoCommit(false);
                clients = controller.select_clients_from_database("SELECT * FROM Client");
                total_client = clients.size();
                con.setAutoCommit(false);
                for (int i = 0; i < clients.size(); i++) {

                    //========= CHECK IF CLIENT EXIST IN FIREBIRD PME PRO
                    boolean client_exist = false;
                    String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + clients.get(i).code_client + "' ";
                    ResultSet rs0 = stmt.executeQuery(client_requete);
                    while (rs0.next()) {
                        client_exist = true;
                    }
                    //========= insert client if not exist
                    if(!client_exist){
                        PostData_Client client;
                        // ========Select client from data base to insert it
                        client = controller.select_client_from_database(clients.get(i).code_client);
                        String insert_client;
                        // ====insert client
                        String deport_or_vendeur = "null";
                        if(code_depot.equals("000000")){
                            deport_or_vendeur  = code_vendeur;
                        }else {
                            deport_or_vendeur = code_depot;
                        }

                        insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, NUM_ART, NUM_IS, MODE_TARIF, LATITUDE, LONGITUDE, CODE_DEPOT, CODE_VENDEUR) VALUES ('"
                                + client.code_client + "' , " +
                                "iif('"+ client.code_client + "' = null,'Client inconnu','" + client.client.replace("'", "''") + " (" + deport_or_vendeur + ")" +"') , " +
                                "iif('" + client.adresse.replace("'", "''") +"' = null,'Adresse inconnu', " + "'" + client.adresse.replace("'", "''") +"'),  " +
                                "iif('"+ client.tel +"' = '',0,'"+client.tel +"') , " +
                                "iif('"+ client.rc +"' = '',0,'"+ client.rc +"') , " +
                                "iif('"+ client.ifiscal +"' = '',0,'"+ client.ifiscal + "') , " +
                                "iif('"+ client.ai +"' = '',0,'"+ client.ai + "'),  " +
                                "iif('" + client.nis +"' = '',0,'" + client.nis +"'), " +
                                "'" + client.mode_tarif + "' , " +
                                "'" + client.latitude+"', " +
                                "'" + client.longitude + "'," +
                                "iif('" + code_depot +"' = '000000',null,'" + code_depot +"')," +
                                "iif('" + code_vendeur +"' = '000000',null,'" + code_vendeur +"'))";

                        if(stmt.executeUpdate(insert_client) > 0){
                            //commit insert client
                            con.commit();
                        }
                    }
                }

                con.setAutoCommit(true);


                String sql12 = "SELECT  COUNT(*) FROM CLIENTS WHERE coalesce(CLIENTS.SUP , 0) = 0";
                String sql0 = "SELECT  CLIENTS.CODE_CLIENT, " +
                        "CLIENTS.CLIENT, " +
                        "coalesce(CLIENTS.TEL , '/') AS TEL, " +
                        "coalesce(CLIENTS.ADRESSE , '/') AS ADRESSE, " +
                        "coalesce(CLIENTS.NUM_RC , '/') AS NUM_RC, " +
                        "coalesce(CLIENTS.NUM_IF , '/') AS NUM_IF , " +
                        "coalesce(CLIENTS.NUM_IS , '/') AS NUM_IS , " +
                        "coalesce(CLIENTS.NUM_ART , '/') AS NUM_ART , " +
                        "coalesce(CLIENTS.MODE_TARIF , 0) AS MODE_TARIF, " +
                        "coalesce(CLIENTS.ACHATS,0) AS ACHATS, " +
                        "coalesce(CLIENTS.VERSER,0) AS VERSER, " +
                        "coalesce(CLIENTS.SOLDE,0) AS SOLDE, " +
                        "LATITUDE, " +
                        "LONGITUDE , " +
                        "coalesce(CLIENTS.CREDIT_LIMIT , 0) AS CREDIT_LIMIT " +
                        " FROM CLIENTS WHERE coalesce(CLIENTS.SUP , 0) = 0";

                if(code_depot.equals("000000")){
                    if(code_vendeur.equals("000000")){

                    }else {
                        sql12 = sql12 + " AND CODE_VENDEUR = '" + code_vendeur + "'";
                        sql0 = sql0 + " AND CODE_VENDEUR = '" + code_vendeur + "'";
                    }
                }else {
                    sql12 = sql12 + " AND CODE_DEPOT = '" + code_depot + "'";
                    sql0 = sql0 + " AND CODE_DEPOT = '" + code_depot + "'";
                }

                ResultSet rs12 = stmt.executeQuery(sql12);

                while (rs12.next()) {
                    allrows = rs12.getInt("COUNT");
                }
                publishProgress(1);
                //============================ GET Clients ===========================================
                clients.clear();
                ResultSet rs0 = stmt.executeQuery(sql0);
                PostData_Client client;
                while (rs0.next()) {
                    client = new PostData_Client();
                    client.code_client = rs0.getString("CODE_CLIENT");
                    client.client = rs0.getString("CLIENT");
                    client.tel = rs0.getString("TEL");
                    client.adresse = rs0.getString("ADRESSE");
                    client.rc = rs0.getString("NUM_RC");
                    client.ifiscal = rs0.getString("NUM_IF");
                    client.nis = rs0.getString("NUM_IS");
                    client.ai = rs0.getString("NUM_ART");
                    client.mode_tarif = rs0.getString("MODE_TARIF");
                    client.achat_montant = rs0.getDouble("ACHATS");
                    client.verser_montant = rs0.getDouble("VERSER");
                    client.solde_montant = rs0.getDouble("SOLDE");
                    client.latitude = rs0.getDouble("LATITUDE");
                    client.longitude = rs0.getDouble("LONGITUDE");
                    client.credit_limit = rs0.getDouble("CREDIT_LIMIT");
                    client.isNew = 0;

                    clients.add(client);

                    compt++;
                    publishProgress(compt);
                }

                controller.ExecuteTransactionClient(clients);
                //----------------------------------------------------------
                publishProgress(1);
                stmt.close();
                flag = 1;

            } catch (Exception e) {
                con = null;
                if(e.getMessage().contains("SQL Error")){
                    flag = 3;
                }else if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                }
                erreurMessage = e.getMessage();
            }
            return flag;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressDialog.setMax(allrows);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if(result == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information. !")
                        .setContentText("Importation la liste des clients bien terminé")
                        .show();
            }else if(result == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(result == 3){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Problèm au niveau de la requette Sql : " + erreurMessage)
                        .show();
            }
            super.onPostExecute(result);
        }
    }


    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_produit_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        int flag = 1;
        int compt = 0;
        int allrows = 0;
        ArrayList<PostData_Produit> postData_produits;
        String erreurMessage = "";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfigProduit();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Famille> familles = new ArrayList<>();
                ArrayList<PostData_Produit> produits = new ArrayList<>();
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();


                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //==================================================================================
                //Get familles and  Insert it into FAMILLES tables
                String sql1 = "SELECT RECORDID, FAMILLE FROM FAMILLES";

                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Famille famille;
                while (rs1.next()) {

                    famille = new PostData_Famille();
                    famille.famille = rs1.getString("FAMILLE");

                    familles.add(famille);
                }

                controller.ExecuteTransactionFamille(familles);

                //==================================================================================
                publishProgress(1);


                //Get product and  Insert it into produit tables
               /* String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT ORDER BY PRODUIT";

                postData_produits = controller.select_produits_from_database(querry);*/

                String sql12;
                if (code_depot.equals("000000")) {
                    sql12 = "SELECT  COUNT(*) FROM PRODUIT WHERE Coalesce(PRODUIT.SUP,0) = 0  ";
                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql12 = sql12 + " AND Coalesce(PRODUIT.GER_LOT,0)=0 AND (coalesce(PRODUIT.MAT_PREM,0)=0 OR coalesce(PRODUIT.MAT_PREM,0)=3 )";
                    }
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }

                    //Get product and  Insert it into produit tables
                    String sql3 = "SELECT  PRODUIT.CODE_BARRE, " +
                            "PRODUIT.REF_PRODUIT, " +
                            "PRODUIT.PRODUIT , " +
                            "PRODUIT.DETAILLE ," +
                            "coalesce(PRODUIT.PA_HT,0) AS PA_HT, " +
                            "coalesce(PRODUIT.TVA,0) AS TVA, " +
                            "coalesce(PRODUIT.PAMP_HT,0) AS PAMP, " +
                            "coalesce(PRODUIT.PV1_HT,0) AS PV1_HT, " +
                            "coalesce(PRODUIT.PV2_HT,0) AS PV2_HT, " +
                            "coalesce(PRODUIT.PV3_HT,0) AS PV3_HT, " +
                            "coalesce(PRODUIT.STOCK,0) AS STOCK, " +
                            "coalesce(PRODUIT.FAMILLE,'') AS FAMILLE," +
                            "PRODUIT.COLISSAGE, " +
                            "PRODUIT.DESTOCK_TYPE, " +
                            "PRODUIT.DESTOCK_CODE_BARRE, " +
                            "PRODUIT.DESTOCK_QTE ";

                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql3 = sql3 + ", coalesce(PRODUIT.PV4_HT,0) AS PV4_HT " +
                                      ", coalesce(PRODUIT.PV5_HT,0) AS PV5_HT " +
                                      ", coalesce(PRODUIT.PV6_HT,0) AS PV6_HT" +
                                      ", PRODUIT.PHOTO ";
                    }
                    sql3 = sql3 + " FROM PRODUIT WHERE Coalesce(PRODUIT.SUP,0)=0  ";

                    ResultSet rs3 = stmt.executeQuery(sql3);
                    PostData_Produit produit_update;
                    while (rs3.next()) {

                        produit_update = new PostData_Produit();

                        produit_update.code_barre = rs3.getString("CODE_BARRE");
                        produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                        produit_update.produit = rs3.getString("PRODUIT");
                        produit_update.pa_ht = rs3.getDouble("PA_HT");
                        produit_update.tva = rs3.getDouble("TVA");
                        produit_update.pamp = rs3.getDouble("PAMP");
                        produit_update.pv1_ht = rs3.getDouble("PV1_HT");
                        produit_update.pv2_ht = rs3.getDouble("PV2_HT");
                        produit_update.pv3_ht = rs3.getDouble("PV3_HT");
                        produit_update.stock = rs3.getDouble("STOCK");
                        produit_update.colissage = rs3.getDouble("COLISSAGE");
                        if(TYPE_LOGICIEL.equals("PME PRO")){
                            produit_update.pv4_ht = rs3.getDouble("PV4_HT");
                            produit_update.pv5_ht = rs3.getDouble("PV5_HT");
                            produit_update.pv6_ht = rs3.getDouble("PV6_HT");
                            produit_update.photo = rs3.getBytes("PHOTO");
                        }
                        produit_update.description = rs3.getString("DETAILLE");
                        produit_update.famille = rs3.getString("FAMILLE");
                        produit_update.destock_type = rs3.getString("DESTOCK_TYPE");
                        produit_update.destock_code_barre = rs3.getString("DESTOCK_CODE_BARRE");
                        produit_update.destock_qte = rs3.getDouble("DESTOCK_QTE");
                        produit_update.isNew = 0;

                        produits.add(produit_update);

                        compt++;
                        publishProgress(compt);
                    }

                    //Get all syn codebarre of this product  and  Insert it into codebarre tables
                    String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE LEFT JOIN produit ON ( PRODUIT.code_barre = CODEBARRE.code_barre ) WHERE Coalesce(PRODUIT.SUP,0)=0 ";
                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql4 = sql4 + " AND Coalesce(PRODUIT.GER_LOT,0)=0 AND (coalesce(PRODUIT.MAT_PREM,0)=0 OR coalesce(PRODUIT.MAT_PREM,0)=3 )";
                    }
                    ResultSet rs4 = stmt.executeQuery(sql4);

                    PostData_Codebarre post_codebarre;
                    while (rs4.next()) {

                        post_codebarre = new PostData_Codebarre();
                        post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                        post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                        codebarres.add(post_codebarre);
                    }

                     controller.ExecuteTransactionProduit(produits, codebarres);

                } else {

                    sql12 = "SELECT  COUNT(*) FROM DEPOT2  LEFT JOIN produit ON ( PRODUIT.code_barre = DEPOT2.code_barre )  WHERE DEPOT2.CODE_DEPOT = '" + code_depot + "' AND Coalesce(PRODUIT.SUP,0)=0  ";
                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql12 = sql12 + " AND DEPOT2.CODE_LOT IS NULL AND Coalesce(PRODUIT.GER_LOT,0)=0 AND (coalesce(PRODUIT.MAT_PREM,0)=0 OR coalesce(PRODUIT.MAT_PREM,0)=3 )";
                    }
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }

                    //Get product and  Insert it into produit tables
                    String sql3 = "SELECT DEPOT2.CODE_BARRE, " +
                            " PRODUIT.REF_PRODUIT, " +
                            " PRODUIT.PRODUIT, PRODUIT.DETAILLE, " +
                            " coalesce(PRODUIT.PA_HT, 0) AS PA_HT," +
                            " coalesce(PRODUIT.TVA, 0) AS TVA," +
                            " coalesce(PRODUIT.PAMP_HT, 0) AS PAMP," +
                            " coalesce(PRODUIT.PV1_HT,0) AS PV1_HT, " +
                            " coalesce(PRODUIT.PV2_HT,0) AS PV2_HT, " +
                            " coalesce(PRODUIT.PV3_HT,0) AS PV3_HT, " +
                            " coalesce(DEPOT2.STOCK, 0) AS STOCK," +
                            " coalesce(PRODUIT.FAMILLE,'') AS FAMILLE," +
                            " PRODUIT.COLISSAGE," +
                            " PRODUIT.DESTOCK_TYPE," +
                            " PRODUIT.DESTOCK_CODE_BARRE," +
                            " PRODUIT.DESTOCK_QTE ";

                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql3 = sql3 + ", coalesce(PRODUIT.PV4_HT,0) AS PV4_HT " +
                                      ", coalesce(PRODUIT.PV5_HT,0) AS PV5_HT " +
                                      ", coalesce(PRODUIT.PV6_HT,0) AS PV6_HT" +
                                      ", PRODUIT.PHOTO " +
                                      " FROM DEPOT2 " +
                                      " LEFT JOIN PRODUIT ON ( PRODUIT.code_barre = DEPOT2.code_barre ) " +
                                      " WHERE DEPOT2.code_depot = '" + code_depot + "' AND Coalesce(PRODUIT.SUP,0)=0 AND DEPOT2.CODE_LOT IS NULL AND Coalesce(PRODUIT.GER_LOT,0)=0 AND (coalesce(PRODUIT.MAT_PREM,0)=0 OR coalesce(PRODUIT.MAT_PREM,0)=3 )";
                    }else {
                        sql3 = sql3 + " FROM DEPOT2 " +
                                " LEFT JOIN PRODUIT ON ( PRODUIT.code_barre = DEPOT2.code_barre ) " +
                                " WHERE DEPOT2.code_depot = '" + code_depot + "' AND Coalesce(PRODUIT.SUP,0)=0 ";
                    }

                    ResultSet rs3 = stmt.executeQuery(sql3);
                    PostData_Produit produit_update;
                    while (rs3.next()) {

                        produit_update = new PostData_Produit();

                        produit_update.code_barre = rs3.getString("CODE_BARRE");
                        produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                        produit_update.produit = rs3.getString("PRODUIT");
                        produit_update.pa_ht = rs3.getDouble("PA_HT");
                        produit_update.tva = rs3.getDouble("TVA");
                        produit_update.pamp = rs3.getDouble("PAMP");
                        produit_update.pv1_ht = rs3.getDouble("PV1_HT");
                        produit_update.pv2_ht = rs3.getDouble("PV2_HT");
                        produit_update.pv3_ht = rs3.getDouble("PV3_HT");
                        produit_update.stock = rs3.getDouble("STOCK");
                        produit_update.colissage = rs3.getDouble("COLISSAGE");

                        if(TYPE_LOGICIEL.equals("PME PRO")){
                            produit_update.pv4_ht = rs3.getDouble("PV4_HT");
                            produit_update.pv5_ht = rs3.getDouble("PV5_HT");
                            produit_update.pv6_ht = rs3.getDouble("PV6_HT");
                            produit_update.photo = rs3.getBytes("PHOTO");
                        }

                        produit_update.description = rs3.getString("DETAILLE");
                        produit_update.famille = rs3.getString("FAMILLE");
                        produit_update.destock_type = rs3.getString("DESTOCK_TYPE");
                        produit_update.destock_code_barre = rs3.getString("DESTOCK_CODE_BARRE");
                        produit_update.destock_qte = rs3.getDouble("DESTOCK_QTE");
                        produit_update.isNew = 0;

                        produits.add(produit_update);

                        compt++;
                        publishProgress(compt);
                    }

                    //Get all syn codebarre of this product  and  Insert it into codebarre tables
                    String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE where codebarre.code_barre in ( select DEPOT2.CODE_BARRE FROM DEPOT2 LEFT JOIN produit ON ( PRODUIT.code_barre = DEPOT2.code_barre ) WHERE  DEPOT2.code_depot = '" + code_depot + "' AND Coalesce(PRODUIT.SUP,0)=0 ";
                    if(TYPE_LOGICIEL.equals("PME PRO")){
                        sql4 = sql4 + " AND DEPOT2.CODE_LOT IS NULL AND Coalesce(PRODUIT.GER_LOT,0)=0 AND (coalesce(PRODUIT.MAT_PREM,0)=0 OR coalesce(PRODUIT.MAT_PREM,0)=3 )";
                    }
                    sql4 = sql4 + " )";
                    ResultSet rs4 = stmt.executeQuery(sql4);
                    PostData_Codebarre post_codebarre;
                    while (rs4.next()) {

                        post_codebarre = new PostData_Codebarre();
                        post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                        post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                        codebarres.add(post_codebarre);
                    }

                    controller.ExecuteTransactionProduit(produits, codebarres);
                    stmt.close();
                }

                flag = 1;

            } catch (Exception e) {
                con = null;
                if(e.getMessage().contains("SQL Error")){
                    flag = 3;
                }else if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                }
                erreurMessage = e.getMessage();

            }

            return flag;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            mProgressDialog.setMax(allrows);

            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {
            mProgressDialog.dismiss();
            if(result == 1){
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date currentDateTime = Calendar.getInstance().getTime();
                currentDateTimeString = sdf.format(currentDateTime);
                prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("date_time", currentDateTimeString);
                editor.apply();

                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information. !")
                        .setContentText("Importation la liste des produits bien terminé")
                        .show();
            }else if(result == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(result == 3){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Problèm au niveau de la requette Sql : " + erreurMessage)
                        .show();
            }
            super.onPostExecute(result);

        }
    }


    //class Insert Data into FireBird Database
    //====================================
    public class Export_inventaire_to_server_task extends AsyncTask<Void, Void, Integer> {

        Connection con;
        ArrayList<PostData_Inv1> invs1 = new ArrayList<>();
        ArrayList<PostData_Inv2> invs2 = new ArrayList<>();
        int recordid_inv1;
        //boolean executed = false;
        int flag = 0;
        boolean _all = false;
        String _num_inv = null;
        int total_inventaire = 0;
        int inventaire_inserer = 0;
        int inventaire_exist = 0;

        String erreurMessage = "";

        List<String> list_produit_not_exported;
        List<String> list_num_inv_not_exported;


        public Export_inventaire_to_server_task(boolean all, String num_inv) {
            _all = all;
            _num_inv = num_inv;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfigInventaire();
        }


        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                list_num_inv_not_exported = new ArrayList<>();
                list_produit_not_exported = new ArrayList<>();

                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                con.setAutoCommit(false);

                SimpleDateFormat format_local = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat format_distant = new SimpleDateFormat("MM/dd/yyyy");

                list_produit_not_exported = update_produit_into_server(con, stmt);

                if (_all) {
                    invs1.clear();
                    String querry = "SELECT * FROM INV1 WHERE BLOCAGE = 'F'";
                    if (!code_depot.equals("000000")) {
                        querry =  querry + " AND CODE_DEPOT = '" + code_depot + "'";
                    }else{
                        querry = querry + " AND (IS_EXPORTED <> 1 OR IS_EXPORTED is null)";
                    }

                    invs1 = controller.select_list_inventaire_from_database(querry);
                    total_inventaire = invs1.size();

                    for (int i = 0; i < invs1.size(); i++) {

                        try {

                            String file_name = "";
                            file_name = "INVENTAIRE_"+ invs1.get(i).num_inv+"_"+invs1.get(i).exportation + "_" + invs1.get(i).date_inv + ".INV";
                            file_name = file_name.replace("/", "_");

                            //========= CHECK IF CLIENT EXIST IN FIREBIRD PME PRO
                            boolean inv_exist = false;
                            String inv_requete = "SELECT EXPORTATION FROM INV1 WHERE EXPORTATION = '" + file_name + "' ";
                            ResultSet rs0 = stmt.executeQuery(inv_requete);
                            while (rs0.next()) {
                                inv_exist = true;
                            }
                            if(!inv_exist){

                                String querry_select = "SELECT * FROM INV2 WHERE NUM_INV = '" + invs1.get(i).num_inv + "'";
                                invs2 = controller.select_inventaire2_from_database(querry_select);
                                String[] buffer = new String[invs2.size()];

                                String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                                ResultSet rs1 = stmt.executeQuery(ggg);
                                while (rs1.next()) {
                                    recordid_inv1 = rs1.getInt("RECORDID");
                                }

                                String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);

                                Date dt = format_local.parse(invs1.get(i).date_inv);
                                assert dt != null;

                                String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, NBR_PRODUIT, UTILISATEUR, EXPORTATION, CODE_DEPOT ) VALUES ('"
                                        + recordid_inv1 + "' ," +
                                        "'" + NUM_INV + "' ," +
                                        "'" + format_distant.format(dt) + "'," +
                                        "'" + invs1.get(i).heure_inv + "' ," +
                                        "'" + invs1.get(i).nom_inv + "'," +
                                        "'" + invs2.size() + "'," +
                                        "'TERMINAL_MOBILE'," +
                                        "'" + file_name + "'," +
                                        "iif('" + code_depot +"' = '000000',null,'" + code_depot +"') )";
                                stmt.executeUpdate(vvv);

                                for (int j = 0; j < invs2.size(); j++) {
                                    buffer[j] = "INSERT INTO INV2 (CODE_BARRE, NUM_INV, PA_HT, QTE, TVA, QTE_TMP, CODE_DEPOT" +
                                            ") VALUES ('" + invs2.get(j).codebarre + "'," +
                                            "'" + NUM_INV + "'," +
                                            "'" + invs2.get(j).pa_ht + "'," +
                                            "'" + invs2.get(j).qte_theorique + "'," +
                                            "'" + invs2.get(j).tva + "'," +
                                            "'" + invs2.get(j).qte_physique + "'," +
                                            "iif('" + code_depot +"' = '000000',null,'" + code_depot +"'))";

                                    stmt.addBatch(buffer[j]);
                                }

                                stmt.executeBatch();
                                ///////////////////////////////// MISE A JOUR MONTANT INVENTAIRE //////////////////////
                                String update_inv1_distant = "execute procedure upd_inv1 ('"+ NUM_INV +"')";
                                stmt.executeUpdate(update_inv1_distant);
                                con.commit();
                                stmt.clearBatch();
                                ////////////////////////////////////////////////////////////////////////
                                controller.Update_inventaire1(invs1.get(i).num_inv);
                                invs2.clear();
                                inventaire_inserer++;
                                flag = 1;

                            }

                        }catch (Exception e){
                            con.rollback();
                            list_num_inv_not_exported.add( invs1.get(i).num_inv);
                            stmt.clearBatch();
                        }
                    }
                } else {

                    /// else case export one invontory.
                    invs1.clear();
                    String querry = "SELECT * FROM INV1 WHERE NUM_INV = '" + _num_inv + "'";
                    if (!code_depot.equals("000000")) {
                        querry =  querry + "WHERE CODE_DEPOT = '" + code_depot + "' AND ( IS_EXPORTED <> 1 OR IS_EXPORTED is null)";
                    }else{
                        querry = querry + "WHERE IS_EXPORTED <> 1 OR IS_EXPORTED is null";
                    }

                    invs1 = controller.select_list_inventaire_from_database(querry);
                    total_inventaire = invs1.size();

                    for (int i = 0; i < invs1.size(); i++) {

                        try {

                            String querry_select = "SELECT * FROM INV2 WHERE NUM_INV = '" + invs1.get(i).num_inv + "'";
                            invs2 = controller.select_inventaire2_from_database(querry_select);
                            String[] buffer = new String[invs2.size()];

                            String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(ggg);
                            while (rs1.next()) {
                                recordid_inv1 = rs1.getInt("RECORDID");
                            }

                            String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);

                            Date dt = format_local.parse(invs1.get(i).date_inv);
                            assert dt != null;

                            String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, NBR_PRODUIT, UTILISATEUR, CODE_DEPOT ) VALUES ('"
                                    + recordid_inv1 + "' ," +
                                    "'" + NUM_INV + "' ," +
                                    "'" + format_distant.format(dt) + "'," +
                                    "'" + invs1.get(i).heure_inv + "' ," +
                                    "'" + invs1.get(i).nom_inv + "'," +
                                    "'" + invs2.size() + "'," +
                                    "iif('" + invs1.get(i).utilisateur +"' = 'null' , null,'" + invs1.get(i).utilisateur +"') , " +
                                    "iif('" + code_depot +"' = '000000',null,'" + code_depot +"') )";
                            stmt.executeUpdate(vvv);

                            for (int j = 0; j < invs2.size(); j++) {
                                buffer[j] = "INSERT INTO INV2 (CODE_BARRE, NUM_INV, PA_HT, QTE, TVA, QTE_TMP, NBRE_COLIS, COLISSAGE, CODE_DEPOT" +
                                        ") VALUES ('" + invs2.get(j).codebarre + "'," +
                                        "'" + NUM_INV + "'," +
                                        "'" + invs2.get(j).pa_ht + "'," +
                                        "'" + invs2.get(j).qte_theorique + "'," +
                                        "'" + invs2.get(j).tva + "'," +
                                        "'" + invs2.get(j).qte_physique + "'," +
                                        "iif('" + invs2.get(j).nbr_colis +"' = 0 , null,'" + invs2.get(j).nbr_colis +"') , " +
                                        "iif('" + invs2.get(j).colissage +"' = 0 , null,'" + invs2.get(j).colissage +"') , " +
                                        "iif('" + code_depot +"' = '000000',null,'" + code_depot +"'))";

                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            con.commit();
                            stmt.clearBatch();
                            controller.Update_inventaire1(invs1.get(i).num_inv);
                            invs2.clear();
                            inventaire_inserer++;
                            flag = 1;

                        }catch (Exception e){
                            con.rollback();
                            list_num_inv_not_exported.add( invs1.get(i).num_inv);
                            stmt.clearBatch();
                        }
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                } else {
                    Log.e("tremtek", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");

                    //not executed with problem in the sql statement
                    flag = 3;
                }
                erreurMessage = ex.getMessage();
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            mProgressDialog.dismiss();

            inventaire_exist = total_inventaire - inventaire_inserer;
            if (integer == 1) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé, Total inventaire : " + total_inventaire + "\n bons inserés : " + inventaire_inserer + "\n bons exists : " + inventaire_exist)
                        .show();
            } else if (integer == 2) {
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            } else if (integer == 3) {
                //  if(ActivityImportsExport. != null)
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }

    //===========================================================================================
    ///////////////////////////// GET DIGIT ////////////////////////////////////////////////////
    public String Get_Digits_String(String number, int length) {
        String _number = number;
        while (_number.length() < length) {
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }
   //------------------------------------------------------------------------------------------------

    private List<String>  update_produit_into_server(Connection con, Statement stmt) throws SQLException {
        List<String> list_produit_not_exported = new ArrayList<>();
        ArrayList<PostData_Produit> postData_produits = new ArrayList<>();

        //Get product and  Insert it into produit tables
        String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE ISNEW = 1 ORDER BY PRODUIT";

        postData_produits = controller.select_produits_from_database(querry);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

        for(int i = 0; i< postData_produits.size(); i++){
            //insert produit into server

            try {
                String insert_into_produit = "";
                insert_into_produit =  "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                if(TYPE_LOGICIEL.equals("PME PRO")){
                    insert_into_produit = insert_into_produit +  ", PV4_HT, PV5_HT, PV6_HT ";
                }
                insert_into_produit = insert_into_produit + ") VALUES (" +
                                " '" + postData_produits.get(i).code_barre + "' , " +
                                " '" + postData_produits.get(i).ref_produit + "' ," +
                                " '" + postData_produits.get(i).produit.replace("'", " ") + "', "+
                                " '" + postData_produits.get(i).pa_ht + "' ," +
                                " '" + postData_produits.get(i).tva + "' ," +
                                " '" + postData_produits.get(i).stock + "' ," +
                                " '" + postData_produits.get(i).colissage + "', "+
                                " '" + postData_produits.get(i).pv1_ht + "' ," +
                                " '" + postData_produits.get(i).pv2_ht + "' ," +
                                " '" + postData_produits.get(i).pv3_ht + "' ";

                if(TYPE_LOGICIEL.equals("PME PRO")){
                    insert_into_produit = insert_into_produit +  ", '" + postData_produits.get(i).pv4_ht + "' ," +
                            " '" + postData_produits.get(i).pv5_ht + "' ," +
                            " '" + postData_produits.get(i).pv6_ht + "' ";
                }

                insert_into_produit = insert_into_produit + ") MATCHING (CODE_BARRE)";

                stmt.addBatch(insert_into_produit);

                stmt.executeBatch();
                con.commit();

            }catch (Exception e){
                con.rollback();
                list_produit_not_exported.add(postData_produits.get(i).produit);
            }

            controller.update_produit_after_export(postData_produits.get(i).code_barre);

        }
        return list_produit_not_exported;
    }

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }
}

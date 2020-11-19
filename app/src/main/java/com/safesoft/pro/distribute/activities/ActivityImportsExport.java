package com.safesoft.pro.distribute.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.safesoft.pro.distribute.dropBox.ActivityDropBoxRead_Transfert;
import com.safesoft.pro.distribute.eventsClasses.SelectedBonTransfertEvent;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.dropBox.ActivityDropBoxSend;
import com.safesoft.pro.distribute.dropBox.ActivityDropBoxSend_Commande;
import com.safesoft.pro.distribute.fragments.FragmentSelectedBonTransfert;
import com.safesoft.pro.distribute.postData.PostData_Bon1;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.postData.PostData_Carnet_c;
import com.safesoft.pro.distribute.postData.PostData_Client;
import com.safesoft.pro.distribute.postData.PostData_Codebarre;
import com.safesoft.pro.distribute.postData.PostData_Inv1;
import com.safesoft.pro.distribute.postData.PostData_Inv2;
import com.safesoft.pro.distribute.postData.PostData_Produit;
import com.safesoft.pro.distribute.postData.PostData_Transfer1;
import com.safesoft.pro.distribute.postData.PostData_Transfer2;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.services.ServiceDistribute;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.pedant.SweetAlert.SweetAlertDialog;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;

public class ActivityImportsExport extends AppCompatActivity {

    private ServiceDistribute s;
    private boolean mBound = false;
    private String Server;
    private String Username;
    private String Password;
    private String MY_PREFS_NAME = "ConfigNetwork";
    private String MY_PREF_NAME = "T";

    private String PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
    private String PARAMS_PREFS_IMPORT_EXPORT = "IMPORT_EXPORT";
    private String Path;
    private DATABASE controller;
    private ProgressDialog mProgressDialog;
    private ProgressDialog mProgressDialog_Free;
    private RelativeLayout Import_bon,Import_client, Import_produit, Export_vente, Export_commande, Export_inventaire, EtatV,   BonExported;
    private String code_depot, code_vendeur;
    Date currentTime;
    String currentDateTimeString=null;
    private Context mContext;
    SharedPreferences.Editor editor;

    SharedPreferences p;
    private  MediaPlayer mp;
    private EventBus bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imports_export);
        controller = new     DATABASE(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Import/Export");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        initViews();


        // Register as a subscriber
        bus.register(this);
        p = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
        code_depot = p.getString("CODE_DEPOT", "000000");
        code_vendeur = p.getString("CODE_VENDEUR", "000000");


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent= new Intent(this, ServiceDistribute.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    protected void initViews(){
        Import_bon = (RelativeLayout) findViewById(R.id.rlt_import_bon);
        Import_client = (RelativeLayout) findViewById(R.id.rlt_import_client);
        Export_vente = (RelativeLayout) findViewById(R.id.rlt_export_ventes);
        Export_commande = (RelativeLayout) findViewById(R.id.rlt_export_commandes);
        Export_inventaire = (RelativeLayout) findViewById(R.id.rlt_export_inventaires);
        BonExported = (RelativeLayout) findViewById(R.id.rlt_exported_ventes);
        EtatV = (RelativeLayout) findViewById(R.id.rlt_etatv);
        mContext = this;
    }

    public void onRelativeClick(View v) throws ExecutionException, InterruptedException {
        SharedPreferences prefs3 = getSharedPreferences(PARAMS_PREFS_IMPORT_EXPORT, MODE_PRIVATE);
        switch (v.getId()){
            case R.id.rlt_import_bon:
                // 1 check connection
                // 2 get all transfer bon for this deport
                // 3 propose those not exist in local database


                    Check_connection_bon_server_task check_connection = new Check_connection_bon_server_task();
                    check_connection.execute();

            //    Toast.makeText(getApplicationContext(),"Mode automatic",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rlt_import_bon_retour:
                // 1 check connection
                // 2 get all transfer bon for this deport
                // 3 propose those not exist in local database

               // if(prefs3.getBoolean("IMPORT_ONLINE", false)){
                //    Intent dropbox_intent = new Intent(ActivityImportsExport.this, ActivityDropBoxRead_Transfert.class);
                 //   startActivity(dropbox_intent);
               // }else
               // {
                    Check_connection_bon_retour_server_task checkk_connection = new Check_connection_bon_retour_server_task();
                    checkk_connection.execute();
               // }
                //    Toast.makeText(getApplicationContext(),"Mode automatic",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rlt_import_client:
                // 1 check connection
                // 2 synchronisation la list des client
                // 3 propose those not exist in local database

                Check_connection_client_server_for_sychronisation_client checkkk_connection = new Check_connection_client_server_for_sychronisation_client();
                checkkk_connection.execute();

                break;

            case R.id.rlt_import_produit:
                // 1 check connection
                // 2 synchronisation la list des client
                // 3 propose those not exist in local database

               Check_connection_produit_server_for_sychronisation_produit check_connection_produit = new Check_connection_produit_server_for_sychronisation_produit();
                check_connection_produit.execute();

              //  Toast.makeText(getApplicationContext(),"Veuillez importer les bons de transferts",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rlt_export_ventes:


                if(prefs3.getBoolean("EXPORT_ONLINE", false)){
                    Intent dropbox_intent = new Intent(ActivityImportsExport.this, ActivityDropBoxSend.class);
                    startActivity(dropbox_intent);
                }else {
                    Check_connection_export_server check_connection_export_data = new Check_connection_export_server("VENTE");
                    check_connection_export_data.execute();

                }

                break;
            case R.id.rlt_export_commandes:


                if(prefs3.getBoolean("EXPORT_ONLINE", false)){
                    Intent dropbox_intent = new Intent(ActivityImportsExport.this, ActivityDropBoxSend_Commande.class);
                    startActivity(dropbox_intent);
                }else {
                    Check_connection_export_server check_connection_export_data = new Check_connection_export_server("COMMANDE");
                    check_connection_export_data.execute();
                }

                break;


            case R.id.rlt_export_inventaires:


                if(prefs3.getBoolean("EXPORT_ONLINE", false)){
                   // Intent dropbox_intent = new Intent(ActivityImportsExport.this, ActivityDropBoxSend_Commande.class);
                  //  startActivity(dropbox_intent);
                    Toast.makeText(ActivityImportsExport.this , " Cette option est en cours de developpement, \n vous pouvez desactiver l'option (Exportation en ligne) dans les paramètres,\n  pour pouvoir exporter les inventaires localement !", Toast.LENGTH_LONG).show();
                }else {
                    Check_connection_export_server check_connection_export_data = new Check_connection_export_server("INVENTAIRE");
                    check_connection_export_data.execute();
                }

                break;
            case R.id.rlt_exported_ventes:

                Intent exported_ventes_intent = new Intent(ActivityImportsExport.this, ActivityExportedVentes.class);
                startActivity(exported_ventes_intent);

                break;
            case R.id.rlt_etatv:

                Intent etat_v_intent = new Intent(ActivityImportsExport.this, ActivityEtatV.class);
                startActivity(etat_v_intent);

                break;
        }
    }


    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_bon_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;


        public Check_connection_bon_server_task(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");
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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                con = null;
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
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                // get all transfert bon of this depot
                Import_BonTransfert2_server_task bon_transfert2_task = new Import_BonTransfert2_server_task();
                bon_transfert2_task.execute();
            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }
    }
    //===========================================================================================
    public class Check_connection_bon_retour_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;


        public Check_connection_bon_retour_server_task(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");
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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                con = null;
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
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                // get all transfert bon of this depot
                Import_BonTransfert2_retour_server_task bon_transfert2_task = new Import_BonTransfert2_retour_server_task();
                bon_transfert2_task.execute();
            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }
    }
    public class Import_BonTransfert2_retour_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        ArrayList<String> transfer1s;

        public Import_BonTransfert2_retour_server_task(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

            prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
            code_depot = prefs.getString("CODE_DEPOT", "000000");
        }

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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //============================ GET Trasfer1 ===========================================
                //  String sql1 = "SELECT  TRANSFERT1.NUM_BON, TRANSFERT1.DATE_BON, TRANSFERT1.CODE_DEPOT_SOURCE, DEPOT1.NOM_DEPOT, coalesce(TRANSFERT1.NBR_P,0) AS NBR_P FROM TRANSFERT1 LEFT JOIN DEPOT1 ON (TRANSFERT1.CODE_DEPOT_SOURCE = DEPOT1.NOM_DEPOT_DEST) WHERE CODE_DEPOT_DEST = '" + code_depot + "' UNION ALL SELECT  TRANSFERT1.NUM_BON, TRANSFERT1.DATE_BON, TRANSFERT1.CODE_DEPOT_DEST, DEPOT1.NOM_DEPOT, coalesce(TRANSFERT1.NBR_P,0) AS NBR_P FROM TRANSFERT1 LEFT JOIN DEPOT1 ON (TRANSFERT1.CODE_DEPOT_DEST = DEPOT1.NOM_DEPOT) WHERE CODE_DEPOT_DEST = '" + code_depot + "'";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df_show = new SimpleDateFormat("MM/dd/yyyy");

                c.add(Calendar.DAY_OF_YEAR, 0);
                String week_before = df_show.format(c.getTime());





                //Date midnightDate = new Date(midnight);
                SharedPreferences pref = getSharedPreferences(MY_PREF_NAME, 0);
                String test = pref.getString("time",null);
                String sql1;
                if (test != null) {
                    currentDateTimeString = pref.getString("time", null);
                    sql1  = "select \n" +
                            "    transfert1.num_bon,\n" +
                            "    transfert1.date_bon,\n" +
                            "    transfert1.heure,\n" +
                            "    transfert1.code_depot_source,\n" +
                            "    dep_source.nom_depot as nom_depot_source,\n" +
                            "    transfert1.code_depot_dest,\n" +
                            "    dep_dest.nom_depot as nom_depot_dest,\n" +
                            "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P\n" +
                            "\n" +
                            "from transfert1\n" +
                            "left join depot1 as dep_source on ( dep_source.code_depot = transfert1.code_depot_source)\n" +
                            "left join depot1 as dep_dest on ( dep_dest.code_depot = transfert1.code_depot_dest)\n" +
                            "WHERE ( CODE_DEPOT_SOURCE = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( transfert1.date_bon >= '"+week_before+"' ) AND ( transfert1.heure >= '"+currentDateTimeString+"' ) ";

                }else {
                    sql1 = "select \n" +
                            "    transfert1.num_bon,\n" +
                            "    transfert1.date_bon,\n" +
                            "    transfert1.heure,\n" +
                            "    transfert1.code_depot_source,\n" +
                            "    dep_source.nom_depot as nom_depot_source,\n" +
                            "    transfert1.code_depot_dest,\n" +
                            "    dep_dest.nom_depot as nom_depot_dest,\n" +
                            "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P\n" +
                            "\n" +
                            "from transfert1\n" +
                            "left join depot1 as dep_source on ( dep_source.code_depot = transfert1.code_depot_source)\n" +
                            "left join depot1 as dep_dest on ( dep_dest.code_depot = transfert1.code_depot_dest)\n" +
                            "WHERE ( CODE_DEPOT_SOURCE = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( transfert1.date_bon >= '"+week_before+"' ) ";

                }


                //  "WHERE ( CODE_DEPOT_DEST = '" + code_depot + "' OR CODE_DEPOT_SOURCE = '"+ code_depot + "' ) AND BLOCAGE = 'F' ";

                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Transfer1 transfer1;
                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {

                    transfer1 = new PostData_Transfer1();
                    transfer1.num_bon = rs1.getString("NUM_BON");
                    transfer1.date_bon = rs1.getString("DATE_BON");
                    transfer1.code_depot_s = rs1.getString("CODE_DEPOT_SOURCE");
                    transfer1.nom_depot_s =  rs1.getString("NOM_DEPOT_SOURCE");
                    transfer1.code_depot_d =  rs1.getString("CODE_DEPOT_DEST");
                    transfer1.nom_depot_d =  rs1.getString("NOM_DEPOT_DEST");
                    transfer1.nbr_p = rs1.getString("NBR_P");

                    if(!controller.check_transfer1_if_exist(transfer1.num_bon))
                        transfer1s.add(transfer1.num_bon);

                }

                flag = 1;

            } catch (Exception e) {
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD"  + e.getMessage());
                    flag = 3;
                }
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                if(!((Activity) mContext).isFinishing())
                {
                    //show dialog
                    mProgressDialog_Free.dismiss();
                    //propose list
                    showListBons(transfer1s);

                }


            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }
    }

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_BonTransfert2_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        ArrayList<String> transfer1s;

        public Import_BonTransfert2_server_task(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

            prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
            code_depot = prefs.getString("CODE_DEPOT", "000000");
        }

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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //============================ GET Trasfer1 ===========================================
              //  String sql1 = "SELECT  TRANSFERT1.NUM_BON, TRANSFERT1.DATE_BON, TRANSFERT1.CODE_DEPOT_SOURCE, DEPOT1.NOM_DEPOT, coalesce(TRANSFERT1.NBR_P,0) AS NBR_P FROM TRANSFERT1 LEFT JOIN DEPOT1 ON (TRANSFERT1.CODE_DEPOT_SOURCE = DEPOT1.NOM_DEPOT_DEST) WHERE CODE_DEPOT_DEST = '" + code_depot + "' UNION ALL SELECT  TRANSFERT1.NUM_BON, TRANSFERT1.DATE_BON, TRANSFERT1.CODE_DEPOT_DEST, DEPOT1.NOM_DEPOT, coalesce(TRANSFERT1.NBR_P,0) AS NBR_P FROM TRANSFERT1 LEFT JOIN DEPOT1 ON (TRANSFERT1.CODE_DEPOT_DEST = DEPOT1.NOM_DEPOT) WHERE CODE_DEPOT_DEST = '" + code_depot + "'";
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df_show = new SimpleDateFormat("MM/dd/yyyy");

                c.add(Calendar.DAY_OF_YEAR, 0);
                String week_before = df_show.format(c.getTime());





                //Date midnightDate = new Date(midnight);
                SharedPreferences pref = getSharedPreferences(MY_PREF_NAME, 0);
                String test = pref.getString("time",null);
                String sql1;
                if (test != null) {
                    currentDateTimeString = pref.getString("time", null);
                   sql1  = "select \n" +
                            "    transfert1.num_bon,\n" +
                            "    transfert1.date_bon,\n" +
                            "    transfert1.heure,\n" +
                            "    transfert1.code_depot_source,\n" +
                            "    dep_source.nom_depot as nom_depot_source,\n" +
                            "    transfert1.code_depot_dest,\n" +
                            "    dep_dest.nom_depot as nom_depot_dest,\n" +
                            "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P\n" +
                            "\n" +
                            "from transfert1\n" +
                            "left join depot1 as dep_source on ( dep_source.code_depot = transfert1.code_depot_source)\n" +
                            "left join depot1 as dep_dest on ( dep_dest.code_depot = transfert1.code_depot_dest)\n" +
                            "WHERE ( CODE_DEPOT_DEST = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( transfert1.date_bon >= '"+week_before+"' ) AND ( transfert1.heure >= '"+currentDateTimeString+"' ) ";

                }else {
                     sql1 = "select \n" +
                            "    transfert1.num_bon,\n" +
                            "    transfert1.date_bon,\n" +
                            "    transfert1.heure,\n" +
                            "    transfert1.code_depot_source,\n" +
                            "    dep_source.nom_depot as nom_depot_source,\n" +
                            "    transfert1.code_depot_dest,\n" +
                            "    dep_dest.nom_depot as nom_depot_dest,\n" +
                            "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P\n" +
                            "\n" +
                            "from transfert1\n" +
                            "left join depot1 as dep_source on ( dep_source.code_depot = transfert1.code_depot_source)\n" +
                            "left join depot1 as dep_dest on ( dep_dest.code_depot = transfert1.code_depot_dest)\n" +
                            "WHERE ( CODE_DEPOT_DEST = '" + code_depot + "') AND ( BLOCAGE = 'F' )  AND ( transfert1.date_bon >= '"+week_before+"' ) ";

                }


                //  "WHERE ( CODE_DEPOT_DEST = '" + code_depot + "' OR CODE_DEPOT_SOURCE = '"+ code_depot + "' ) AND BLOCAGE = 'F' ";

                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Transfer1 transfer1;
                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {

                    transfer1 = new PostData_Transfer1();
                    transfer1.num_bon = rs1.getString("NUM_BON");
                    transfer1.date_bon = rs1.getString("DATE_BON");
                    transfer1.code_depot_s = rs1.getString("CODE_DEPOT_SOURCE");
                    transfer1.nom_depot_s =  rs1.getString("NOM_DEPOT_SOURCE");
                    transfer1.code_depot_d =  rs1.getString("CODE_DEPOT_DEST");
                    transfer1.nom_depot_d =  rs1.getString("NOM_DEPOT_DEST");
                    transfer1.nbr_p = rs1.getString("NBR_P");

                    if(!controller.check_transfer1_if_exist(transfer1.num_bon))
                    transfer1s.add(transfer1.num_bon);

                }

                flag = 1;

            } catch (Exception e) {
                con = null;
                if (e.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD");

                } else {
                    //not executed with problem in the sql statement
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD"  + e.getMessage());
                    flag = 3;
                }
            }

            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                if(!((Activity) mContext).isFinishing())
                {
                    //show dialog
                    mProgressDialog_Free.dismiss();
                    //propose list
                    showListBons(transfer1s);

                }


            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }
    }
    //===========================================================================================

    protected void showListBons(ArrayList<String> transfert1s){
        android.app.FragmentManager fm = getFragmentManager();
        DialogFragment dialog = new FragmentSelectedBonTransfert(); // creating new object
        Bundle args = new Bundle();
        args.putStringArrayList("LIST_SELECTED_TRANSFERT_BON", transfert1s);
        dialog.setArguments(args);
        dialog.show(fm, "dialog");

    }

    @Subscribe
    public void onBonTransfertSelected(SelectedBonTransfertEvent event){

        Import_bonTransfer_from_server_task nnn = new Import_bonTransfer_from_server_task(event.getNum_bon());
        nnn.execute();
    }
    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            ServiceDistribute.MyBinder b = (ServiceDistribute.MyBinder) binder;
            s = b.getService();
            mBound = true;
            s.setContext(getBaseContext());
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
            mBound = false;
        }
    };

    public void progressDialogConfig(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation des données...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


    public void progressDialogExportation(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Exportation des données...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }



    public void progressDialogConfigClient(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation clients...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressDialogConfigProduit(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Importation produits...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void progressDialogConfigInventaire(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Exportation inventaires...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_bonTransfer_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        int compt = 0;
        int allrows=0;
        private Boolean First = true;
        ArrayList<PostData_Transfer2> transfer2s;
        String num_bon;

        public Import_bonTransfer_from_server_task(String _num_bon){

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");
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
                ArrayList<PostData_Client> clients = new ArrayList<>();
                transfer2s = new ArrayList<>();
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                //=====================================
                String sql10 = "SELECT  TRANSFERT1.NUM_BON FROM TRANSFERT1";
                ResultSet rs10 = stmt.executeQuery(sql10);
                while (rs10.next()) {
                    allrows ++;
                }

                String sql11 = "SELECT  TRANSFERT2.NUM_BON FROM TRANSFERT2";
                ResultSet rs11 = stmt.executeQuery(sql11);

                while (rs11.next()) {
                    allrows ++;
                }
              /*  String sql12 = "SELECT  CLIENTS.CODE_CLIENT FROM CLIENTS";
                ResultSet rs12 = stmt.executeQuery(sql12);

                while (rs12.next()) {
                    allrows ++;
                }*/
                First = true;
                publishProgress(1);

                //============================ GET Trasfer1 ===========================================

                String sql1 = "select \n" +
                        "    transfert1.num_bon,\n" +
                        "    transfert1.date_bon,\n" +
                        "    transfert1.heure,\n" +
                        "    transfert1.code_depot_source,\n" +
                        "    dep_source.nom_depot as nom_depot_source,\n" +
                        "    transfert1.code_depot_dest,\n" +
                        "    dep_dest.nom_depot as nom_depot_dest,\n" +
                        "   coalesce(TRANSFERT1.NBR_P,0) AS NBR_P\n" +
                        "\n" +
                        "from transfert1\n" +
                        "left join depot1 as dep_source on ( dep_source.code_depot = transfert1.code_depot_source)\n" +
                        "left join depot1 as dep_dest on ( dep_dest.code_depot = transfert1.code_depot_dest)\n" +
                        "WHERE NUM_BON = '"+num_bon+"'";

                ResultSet rs1 = stmt.executeQuery(sql1);
                PostData_Transfer1 transfer1;
                DecimalFormat df = new DecimalFormat("#.##");

                df.setRoundingMode(RoundingMode.HALF_UP);

                while (rs1.next()) {
                    transfer1 = new PostData_Transfer1();
                    transfer1.num_bon = rs1.getString("NUM_BON");
                    transfer1.date_bon = rs1.getString("DATE_BON");
                    transfer1.code_depot_s = rs1.getString("CODE_DEPOT_SOURCE");
                    transfer1.nom_depot_s =  rs1.getString("NOM_DEPOT_SOURCE");
                    transfer1.code_depot_d =  rs1.getString("CODE_DEPOT_DEST");
                    transfer1.nom_depot_d =  rs1.getString("NOM_DEPOT_DEST");
                    transfer1.nbr_p = rs1.getString("NBR_P");
                    transfer1s.add(transfer1);

                    compt++;
                    publishProgress(compt);
                }

                //============================ GET Trasfer2 ===========================================
                String sql2 = "SELECT  TRANSFERT2.NUM_BON, TRANSFERT2.CODE_BARRE, PRODUIT.PRODUIT AS PRODUIT, coalesce(TRANSFERT2.QTE,0) AS QTE FROM TRANSFERT2, PRODUIT WHERE (TRANSFERT2.CODE_BARRE = PRODUIT.CODE_BARRE) AND NUM_BON = '" +num_bon+"'";
                ResultSet rs2 = stmt.executeQuery(sql2);


                while (rs2.next()) {

                    PostData_Transfer2 transfer2 = new PostData_Transfer2();

                    transfer2.num_bon = rs2.getString("NUM_BON");
                    transfer2.code_barre = rs2.getString("CODE_BARRE");
                    transfer2.produit = rs2.getString("PRODUIT");
                    transfer2.qte =  rs2.getString("QTE");
                    transfer2s.add(transfer2);

                    compt++;
                    publishProgress(compt);

                }

                First = false;
                publishProgress(1);
                compt = 0;

                Boolean executed = controller.ExecuteTransactionTrasfer(transfer1s, transfer2s);

                transfer2s.clear();
                transfer2s = controller.select_transfer2_from_database("SELECT * FROM Transfer2 WHERE NUM_BON = '" +num_bon+"' GROUP BY CODE_BARRE ");

                PostData_Produit produit_check_exist;

                for(int i = 0; i< transfer2s.size(); i++){
                    produit_check_exist = new PostData_Produit();
                    produit_check_exist.exist = false;
                    produit_check_exist = controller.check_product_if_exist(transfer2s.get(i).code_barre);

                    PostData_Produit produit_update = null;

                    if(produit_check_exist.exist) {
                        // UPDATE
                    String sql3 = "SELECT  PRODUIT.PRODUIT , coalesce(PRODUIT.PA_HT,0) AS PA_HT, coalesce(PRODUIT.TVA,0) AS TVA, cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT ,PRODUIT.PHOTO FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '"+ transfer2s.get(i).code_barre +"'";
                        ResultSet rs3 = stmt.executeQuery(sql3);

                        while (rs3.next()) {

                            produit_update = new PostData_Produit();

                            if(transfer1s.get(0).code_depot_s.equals(code_depot)){
                                produit_update.stock = String.valueOf(Double.valueOf(produit_check_exist.stock) - Double.valueOf(transfer2s.get(i).qte));
                            }else{ produit_update.stock = String.valueOf(Double.valueOf(transfer2s.get(i).qte) + Double.valueOf(produit_check_exist.stock));
                            }
                            produit_update.produit = rs3.getString("PRODUIT");
                            produit_update.pa_ht = rs3.getString("PA_HT");
                            produit_update.tva = rs3.getString("TVA");
                            produit_update.pv1_ht = rs3.getString("PV1_HT");
                            produit_update.pv2_ht =  rs3.getString("PV2_HT");
                            produit_update.pv3_ht =  rs3.getString("PV3_HT");
                            produit_update.photo =  rs3.getBytes("PHOTO");

                        }
                        controller.Update_produit(produit_update,  transfer2s.get(i).code_barre);


                        //Get all syn codebarre of this product  and  Insert it into codebarre tables
                        String sql4 = "SELECT CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE WHERE CODEBARRE.CODE_BARRE = '"+ transfer2s.get(i).code_barre +"' ";
                        ResultSet rs4 = stmt.executeQuery(sql4);

                        controller.Delete_Codebarre(transfer2s.get(i).code_barre);

                        while (rs4.next()) {

                            PostData_Codebarre post_codebarre = new PostData_Codebarre();

                            post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                            post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");

                            controller.Insert_into_codebarre(post_codebarre);
                        }
                    }else{

                        //Get product and  Insert it into produit tables
                        String sql3 = "SELECT  PRODUIT.CODE_BARRE, PRODUIT.REF_PRODUIT, PRODUIT.PRODUIT , coalesce(PRODUIT.PA_HT,0) AS PA_HT, coalesce(PRODUIT.TVA,0) AS TVA, cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT , PRODUIT.PHOTO FROM PRODUIT WHERE PRODUIT.CODE_BARRE = '"+ transfer2s.get(i).code_barre +"' ";
                        ResultSet rs3 = stmt.executeQuery(sql3);

                        while (rs3.next()) {

                            produit_update = new PostData_Produit();

                            produit_update.code_barre = rs3.getString("CODE_BARRE");
                            produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                            produit_update.produit = rs3.getString("PRODUIT");
                            produit_update.pa_ht = rs3.getString("PA_HT");
                            produit_update.tva = rs3.getString("TVA");
                            produit_update.pv1_ht = rs3.getString("PV1_HT");
                            produit_update.pv2_ht =  rs3.getString("PV2_HT");
                            produit_update.pv3_ht =  rs3.getString("PV3_HT");
                            produit_update.stock =  transfer2s.get(i).qte;
                            //produit_update.photo =  rs3.getBlob("PHOTO");
                            produit_update.photo =  rs3.getBytes("PHOTO");
                        }

                        controller.Insert_into_produit(produit_update);

                        //Get all syn codebarre of this product  and  Insert it into codebarre tables
                        String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE WHERE CODEBARRE.CODE_BARRE = '"+ transfer2s.get(i).code_barre +"' ";
                        ResultSet rs4 = stmt.executeQuery(sql4);

                        while (rs4.next()) {

                            PostData_Codebarre post_codebarre = new PostData_Codebarre();

                            post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                            post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");

                            controller.Insert_into_codebarre(post_codebarre);
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
                }else{
                    flag = 3;
                }

            } catch (Exception e) {
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
            if(First)
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
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound();
        super.onBackPressed();
    }

    public void Sound(){
        mp = MediaPlayer.create(this, R.raw.back);
        mp.start();
    }


    // Importation liste client
    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_export_server extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        private String typeBon = "" ;


        public Check_connection_export_server(String typeBon){

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                con = null;
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
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                // export data vente, commande to the server
                if(typeBon.toString().equals("VENTE")){
                    if((code_depot.toString().equals("000000")) && (code_vendeur.toString().equals("000000"))) {
                        Exporter_ventes_to_server_task export_ventesx_to_server = new Exporter_ventes_to_server_task();
                        export_ventesx_to_server.execute();
                    }else
                    {
                        Exporter_ventesx_to_server_task export_ventes_to_server = new Exporter_ventesx_to_server_task();
                        export_ventes_to_server.execute();

                    }

                }else if(typeBon.toString().equals("COMMANDE")){
                    Exporter_commandes_to_server_task export_commandes_to_server = new Exporter_commandes_to_server_task();
                    export_commandes_to_server.execute();
                }else if(typeBon.toString().equals("INVENTAIRE")){

                    Export_inventaire_to_server_task export_inventaire_to_server = new Export_inventaire_to_server_task(true, null);
                    export_inventaire_to_server.execute();
                }

                mProgressDialog_Free.dismiss();
            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }
    }

    //==================== AsyncTask TO export ventes to the server
    public class Exporter_ventes_to_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        Integer flag = 0;
        String erreurMessage ="";

        int total_bon = 0;
        int bon_inserted = 0;
        int bon_exist = 0;

        int total_client = 0;
        int total_versement = 0;

        public Exporter_ventes_to_server_task() {

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Bon1> bon1s = new ArrayList<>();
                ArrayList<PostData_Bon2> bon2s = new ArrayList<>();
                PostData_Client client;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;

                String querry = "SELECT " +
                        "Bon1.RECORDID, " +
                        "Bon1.NUM_BON, " +
                        "Bon1.CODE_CLIENT, " +
                        "Client.CLIENT, " +
                        "Bon1.LATITUDE, " +
                        "Bon1.LONGITUDE, " +
                        "Bon1.DATE_BON, " +
                        "Bon1.HEURE, " +
                        "Bon1.NBR_P, " +
                        "Bon1.MODE_TARIF, " +
                        "Bon1.CODE_DEPOT, " +
                        "Bon1.MONTANT_BON, " +
                        "Bon1.MODE_RG, " +
                        "Bon1.CODE_VENDEUR, " +
                        "Bon1.EXPORTATION, " +
                        "Bon1.REMISE, " +
                        "Bon1.TIMBRE, " +
                        "Bon1.VERSER, " +
                        "Bon1.RESTE, " +
                        "Bon1.BLOCAGE, " +
                        "Bon1.ANCIEN_SOLDE " +
                        "FROM Bon1 " +
                        "LEFT JOIN Client ON " +
                        "Bon1.CODE_CLIENT = Client.CODE_CLIENT " +
                        "WHERE BLOCAGE = 'F' ORDER BY Bon1.DATE_BON DESC";

                bon1s.clear();
                bon1s = controller.select_vente_from_database(querry);
                total_bon = bon1s.size();

                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
                code_depot = prefs.getString("CODE_DEPOT", "000000");


                // Get CODE_VENDEUR , CODE_CAISSE
                String CODE_VENDEUR = "NOT_EXIST";
                String CODE_CAISSE = "NOT_EXIST";

                String requette_vendeur_caisse = "SELECT coalesce(CODE_VENDEUR, 'NOT_EXIST') AS CODE_VENDEUR, coalesce(CODE_CAISSE, 'NOT_EXIST') AS CODE_CAISSE FROM DEPOT1 WHERE CODE_DEPOT = '"+ code_depot +"' ";
                ResultSet rs111 = stmt.executeQuery(requette_vendeur_caisse);
                while (rs111.next()) {
                    CODE_VENDEUR = rs111.getString("CODE_VENDEUR");
                    CODE_CAISSE = rs111.getString("CODE_CAISSE");
                }

                for (int i = 0; i < bon1s.size(); i++) {

                    /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                    Boolean client_exist = false;
                    // check client if exist
                    String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + bon1s.get(i).code_client + "'";
                    ResultSet rs0 = stmt.executeQuery(client_requete);
                    while (rs0.next()) {
                        client_exist = true;
                    }

                    // insert client if not exist
                    if(!client_exist){
                        client = new PostData_Client();

                        // Select client from data base to insert it
                        client = controller.select_client_from_database(bon1s.get(i).code_client);
                        String insert_client;
                        // insert client
                            insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, RIB, ACHATS, VERSER, LATITUDE, LONGITUDE) VALUES ('"
                                    + bon1s.get(i).code_client + "' , iif('"+ client.code_client +"' = null,'Client inconnu','"+client.client.replace("'", " ")+"') , iif('"+ client.adresse +"' = null,'Adresse inconnu', '"+ client.adresse.replace("'", " ")+"'),  iif('"+ client.tel +"' = '',0,'"+client.tel +"') , iif('"+ client.rc +"' = '',0,'"+client.rc +"') , iif('"+ client.ifiscal +"' = '',0,'"+client.ifiscal +"') , iif('"+ client.rib +"' = '',0,'"+client.rib +"'), '" + client.achat_montant + "', '" + client.verser_montant + "', '"+client.latitude+"', '" + client.longitude + "')";





                        if(stmt.executeUpdate(insert_client) > 0){

                            //commit insert client
                            con.commit();

                            // check if bon1 exist
                            Boolean check_bon1_exist = false;
                            String check_bon1_requete = "SELECT NUM_BON FROM BON1 WHERE EXPORTATION = '" + bon1s.get(i).exportation + "'";
                            ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                            while (rs3.next()) {
                                check_bon1_exist = true;
                            }

                            if(!check_bon1_exist){
                                String querry_select = "" +
                                        "SELECT " +
                                        "Bon2.RECORDID, " +
                                        "Bon2.CODE_BARRE, " +
                                        "Bon2.NUM_BON, " +
                                        "Bon2.PRODUIT, " +
                                        "Bon2.QTE, " +
                                        "Bon2.PV_HT, " +
                                        "Bon2.TVA, " +
                                        "Bon2.CODE_DEPOT, " +
                                        "Bon2.PA_HT, " +
                                        "Produit.STOCK " +
                                        "FROM Bon2 " +
                                        "INNER JOIN " +
                                        "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                                        "WHERE Bon2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


                                bon2s = controller.select_bon2_from_database(querry_select);
                                String[] buffer = new String[bon2s.size()+1];

                                // Get RECORDID
                                String generator_requet = "SELECT gen_id(gen_bon1_id,1) as RECORDID FROM rdb$database";
                                ResultSet rs1 = stmt.executeQuery(generator_requet);
                                while (rs1.next()) {
                                    recordid_numbon = rs1.getInt("RECORDID");
                                }

                                String vvv;

                                String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);
                                     vvv = "INSERT INTO BON1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, HT, TVA, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, LATITUDE, LONGITUDE, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                            + recordid_numbon + "' , '" + NUM_BON + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                            "' , '" + bon1s.get(i).code_client + "' , '" + bon1s.get(i).tot_ht + "' ,'" + bon1s.get(i).tot_tva + "' , iif('"+ bon1s.get(i).timbre+"' = 'null',0,'"+bon1s.get(i).timbre+"'), iif('"+ bon1s.get(i).remise+"' = 'null',0,'"+bon1s.get(i).remise+"')," +
                                            " '" + bon1s.get(i).verser + "', '" + bon1s.get(i).solde_ancien + "', iif('"+ bon1s.get(i).mode_rg+"' = 'null',null,'"+bon1s.get(i).mode_rg+"') , 'TERMINAl-MOBILE', iif('"+ bon1s.get(i).mode_tarif+"' = 'null',0,'"+bon1s.get(i).mode_tarif+"'), '" + bon1s.get(i).exportation + "', 'F' , " + bon1s.get(i).latitude + ", " + bon1s.get(i).longitude + ", iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'))";
                                    stmt.executeUpdate(vvv);


                                    for (int j = 0; j < bon2s.size(); j++) {
                                        buffer[j] = "INSERT INTO BON2 (NUM_BON, CODE_BARRE, PRODUIT, QTE, PV_HT, TVA, PA_HT, PV_HT_AR) VALUES ('" + NUM_BON + "' , '" + bon2s.get(j).codebarre + "' , iif('"+ bon2s.get(j).produit+"' = null, 'Produit inconnu' , '"+bon2s.get(j).produit+"'), '" + bon2s.get(j).qte + "', '" + bon2s.get(j).p_u + "' , '" + bon2s.get(j).tva + "' , '" + bon2s.get(j).pa_ht + "', '" + bon2s.get(j).p_u + "')";
                                        stmt.addBatch(buffer[j]);
                                    }








                                String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                        + bon1s.get(i).code_client + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                        "' , '" + bon1s.get(i).montant_bon + "' , '" + bon1s.get(i).verser + "' ,'BL-VENTE' , '" + NUM_BON + "', '" + bon1s.get(i).mode_rg + "', 'TERMINAL_MOBILE', ' ', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null ,'"+ CODE_CAISSE +"'))";
                                stmt.executeUpdate(requete_situation);


                                stmt.executeBatch();
                                stmt.clearBatch();

                                if(!CODE_CAISSE.equals("NOT_EXIST")){
                                    if(Double.valueOf(bon1s.get(i).verser) != 0){

                                        String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                                + CODE_CAISSE + "' , '" + CODE_CAISSE + "', cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).verser +
                                                "' , '0', 'BL-VENTE' , '" + NUM_BON + "', 'ESPECE', ' ', 'TERMINAL_MOBILE')";
                                        stmt.executeUpdate(requete_caisse);
                                    }
                                }

                                stmt.executeBatch();
                                con.commit();
                                // update bon as exported
                                controller.Update_ventes_commandes_as_exported(false, bon1s.get(i).num_bon);
                                bon2s.clear();
                                stmt.clearBatch();
                                bon_inserted++;


                            }else{
                                bon_exist++;
                            }
                        }else{
                            // Problem insert client into database // operation aborded
                            new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Problème fatal, erreur insertion client ")
                                    .show();
                        }
                    }else{
                        // check if bon1 exist
                        Boolean check_bon1_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM BON1 WHERE EXPORTATION = '" + bon1s.get(i).exportation + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_bon1_exist = true;
                        }

                        if(!check_bon1_exist){
                            String querry_select = "" +
                                    "SELECT " +
                                    "Bon2.RECORDID, " +
                                    "Bon2.CODE_BARRE, " +
                                    "Bon2.NUM_BON, " +
                                    "Bon2.PRODUIT, " +
                                    "Bon2.QTE, " +
                                    "Bon2.PV_HT, " +
                                    "Bon2.TVA, " +
                                    "Bon2.CODE_DEPOT, " +
                                    "Bon2.PA_HT, " +
                                    "Produit.STOCK " +
                                    "FROM Bon2 " +
                                    "INNER JOIN " +
                                    "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                                    "WHERE Bon2.NUM_BON = '" + bon1s.get(i).num_bon + "'";

                            bon2s = controller.select_bon2_from_database(querry_select);
                            String[] buffer = new String[bon2s.size()+1];

                            String generator_requet = "SELECT gen_id(gen_bon1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            while (rs1.next()) {
                                recordid_numbon = rs1.getInt("RECORDID");
                            }

                            String vvv;
                            String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);
                                vvv = "INSERT INTO BON1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, HT, TVA, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, LATITUDE, LONGITUDE, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                        + recordid_numbon + "' , '" + NUM_BON + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                        "' , '" + bon1s.get(i).code_client + "' , '" + bon1s.get(i).tot_ht + "' ,'" + bon1s.get(i).tot_tva + "' , iif('" + bon1s.get(i).timbre + "' = 'null',0,'" + bon1s.get(i).timbre + "'), iif('" + bon1s.get(i).remise + "' = 'null',0,'" + bon1s.get(i).remise + "')," +
                                        " '" + bon1s.get(i).verser + "', '" + bon1s.get(i).solde_ancien + "', iif('" + bon1s.get(i).mode_rg + "' = 'null',null,'" + bon1s.get(i).mode_rg + "') , 'TERMINAl-MOBILE', iif('" + bon1s.get(i).mode_tarif + "' = 'null',0,'" + bon1s.get(i).mode_tarif + "'), '" + bon1s.get(i).exportation + "', 'F' , " + bon1s.get(i).latitude + ", " + bon1s.get(i).longitude + ", iif('" + CODE_VENDEUR + "' = 'NOT_EXIST', null,'" + CODE_VENDEUR + "'), iif('" + CODE_CAISSE + "' = 'NOT_EXIST', null,'" + CODE_CAISSE + "'))";
                                stmt.executeUpdate(vvv);


                                for (int j = 0; j < bon2s.size(); j++) {
                                    buffer[j] = "INSERT INTO BON2 (NUM_BON, CODE_BARRE, PRODUIT, QTE, PV_HT, TVA, PA_HT, PV_HT_AR) VALUES ('" + NUM_BON + "' , '" + bon2s.get(j).codebarre + "' , iif('" + bon2s.get(j).produit + "' = null, 'Produit inconnu' , '" + bon2s.get(j).produit + "'), '" + bon2s.get(j).qte + "', '" + bon2s.get(j).p_u + "' , '" + bon2s.get(j).tva + "' , '" + bon2s.get(j).pa_ht + "', '" + bon2s.get(j).p_u + "')";
                                    stmt.addBatch(buffer[j]);
                                }





                            String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                    + bon1s.get(i).code_client + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                    "' , '" + bon1s.get(i).montant_bon + "' , '" + bon1s.get(i).verser + "' ,'BL-VENTE' , '" + NUM_BON + "', '" + bon1s.get(i).mode_rg + "', 'TERMINAL_MOBILE', ' ', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'))";
                            stmt.executeUpdate(requete_situation);


                            if(!CODE_CAISSE.equals("NOT_EXIST")){
                                if(Double.valueOf(bon1s.get(i).verser) != 0){

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).verser +
                                            "' , '0', 'BL-VENTE' , '" + NUM_BON + "', 'ESPECE', ' ', 'TERMINAL_MOBILE')";
                                    stmt.executeUpdate(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            //update bon as exported
                            controller.Update_ventes_commandes_as_exported(false, bon1s.get(i).num_bon);
                            stmt.clearBatch();
                            bon2s.clear();
                            bon_inserted++;

                        }else{
                            bon_exist++;
                        }
                    }
                }

                // Export all versement

                // INSERT ALL VERSEMENTS OF CAURANT CLIENTS
                ArrayList<PostData_Carnet_c> all_versement_client = new ArrayList<>();
                ArrayList<PostData_Client> list_client = new ArrayList<>();
                stmt.clearBatch();
                list_client = controller.select_clients_from_database("SELECT * FROM Client");
                for(int q = 0; q < list_client.size() ; q ++) {
                    all_versement_client.clear();
                    all_versement_client = controller.select_carnet_c_from_database("SELECT * FROM Carnet_c WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + list_client.get(q).code_client + "'");

                    if(all_versement_client.size() > 0){

                        total_client++;

                        for (int g = 0; g < all_versement_client.size(); g++) {
                            // Get carnet_c recordid
                            String generator_requet = "SELECT gen_id(gen_carnet_c_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            Integer num_bon_carnet_c = 0;
                            while (rs1.next()) {
                                num_bon_carnet_c = rs1.getInt("RECORDID");
                            }

                            //String RECORDID_CRC = Get_Digits_String(String.valueOf(num_bon_carnet_c), 6);
                            String buffer_versement = "INSERT INTO CARNET_C (RECORDID, CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"+ num_bon_carnet_c + "', '"
                                    + all_versement_client.get(g).code_client + "' , cast ('" + all_versement_client.get(g).carnet_date + "' as timestamp) , '" + all_versement_client.get(g).carnet_heure +
                                    "' , '" + all_versement_client.get(g).carnet_achats + "' , '" + all_versement_client.get(g).carnet_versement + "' ,'SITUATION-CLIENT' ,'VRC" + num_bon_carnet_c + "', '" + all_versement_client.get(g).carnet_mode_rg + "', 'TERMINAL_MOBILE', '" + all_versement_client.get(g).carnet_remarque + "', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'))";
                            stmt.executeUpdate(buffer_versement);


                            if (!CODE_CAISSE.equals("NOT_EXIST")) {
                                if (Double.valueOf(all_versement_client.get(g).carnet_versement) != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , cast ('" + all_versement_client.get(g).carnet_date + "' as timestamp) , '" + all_versement_client.get(g).carnet_versement +
                                            "' , '0', 'SITUATION-CLIENT' , 'VRC" + num_bon_carnet_c + "', 'ESPECE',  '" + all_versement_client.get(g).carnet_remarque + "', 'TERMINAL_MOBILE')";
                                    stmt.executeUpdate(requete_caisse);
                                }
                            }

                            total_versement++;
                        }

                        con.commit();
                        // All ok so Make all versements of this client as exported
                        controller.Update_versement_exported(list_client.get(q).code_client);
                    }
                }

                con.setAutoCommit(true);
                bon1s.clear();
                flag = 1;


            } catch (Exception ex) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
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
            if(integer == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé, Total bon Vente : "+ total_bon +"\n Total bon inseré : " + bon_inserted + "\n Total bon exist : " + bon_exist + "\n =================="+"\n Total client versés : "+ total_client + "\n Total versement : " + total_versement )
                        .show();
            }else if(integer == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(integer == 3){
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
        Integer flag = 0;
        String erreurMessage ="";


        int total_bon_commande = 0;
        int bon_inserted_commande = 0;
        int bon_exist_commande = 0;

        public Exporter_commandes_to_server_task() {

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Bon1> bon1sCommande = new ArrayList<>();
                ArrayList<PostData_Bon2> bon2sCommande = new ArrayList<>();
                PostData_Client client;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;
                String code_client;

                //Export Commande
                bon1sCommande.clear();
                String querry_bon1_commande = "SELECT " +
                        "Bon1_temp.RECORDID, " +
                        "Bon1_temp.NUM_BON, " +
                        "Bon1_temp.CODE_CLIENT, " +
                        "Client.CLIENT, " +
                        "Bon1_temp.LATITUDE, " +

                        "Bon1_temp.LONGITUDE, " +
                        "Client.LATITUDE as LATITUDE_CLIENT, " +

                        "Client.LONGITUDE as LONGITUDE_CLIENT, " +
                        "Client.TEL, " +
                        "Client.ADRESSE, " +
                        "Client.RC, " +
                        "Client.IFISCAL, " +
                        "Bon1_temp.DATE_BON, " +
                        "Bon1_temp.HEURE, " +
                        "Bon1_temp.NBR_P, " +
                        "Bon1_temp.MODE_TARIF, " +

                        "Bon1_temp.CODE_DEPOT, " +
                        "Bon1_temp.MONTANT_BON, " +
                        "Bon1_temp.MODE_RG, " +
                        "Bon1_temp.CODE_VENDEUR, " +
                        "Bon1_temp.EXPORTATION, " +
                        "Bon1_temp.REMISE, " +
                        "Bon1_temp.TOT_TTC, " +
                        "Bon1_temp.TOT_TVA, " +
                        "Bon1_temp.TOT_HT, " +




                        "Bon1_temp.TIMBRE, " +
                        "Bon1_temp.VERSER, " +
                        "Bon1_temp.RESTE, " +
                        "Bon1_temp.BLOCAGE, " +
                        "Bon1_temp.ANCIEN_SOLDE " +
                        "FROM Bon1_temp " +
                        "LEFT JOIN Client ON " +
                        "Bon1_temp.CODE_CLIENT = Client.CODE_CLIENT " +
                        "ORDER BY Bon1_temp.DATE_BON DESC";

                bon1sCommande = controller.select_vente_from_database(querry_bon1_commande);
                total_bon_commande = bon1sCommande.size();

                for(int m=0; m < bon1sCommande.size();m++){

                    /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                    Boolean client_exist = false;
                    // check client if exist
                    String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + bon1sCommande.get(m).code_client + "'";
                    ResultSet rs0 = stmt.executeQuery(client_requete);
                    while (rs0.next()) {

                        client_exist = true;
                    }

                    // insert client if not exist
                    if(!client_exist) {
                        client = new PostData_Client();

                        // Select client from data base to insert it
                        client = controller.select_client_from_database(bon1sCommande.get(m).code_client);

                        // insert client
                        String insert_client =   "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, RIB, ACHATS, VERSER, LATITUDE, LONGITUDE, CODE_DEPOT) VALUES ('"
                                + bon1sCommande.get(m).code_client + "' , '" + client.client + "' , '" + client.adresse + "' ,'" + client.tel + "' , '" + client.rc + "' ,'" + client.ifiscal + "' , '" + client.rib
                                + "', '" + client.achat_montant + "', '" + client.verser_montant + "', '" + client.latitude + "', '" + client.longitude + "', '" + bon1sCommande.get(m).code_depot + "')";

                        stmt.executeUpdate(insert_client);
                        //commit insert client
                        con.commit();
                    }

                    // check if bon1 exist
                    Boolean check_bon1_c_exist = false;
                    String check_bon1_c_requete = "SELECT NUM_BON FROM BCC1 WHERE EXPORTATION = '" + bon1sCommande.get(m).exportation + "'";
                    ResultSet rs3 = stmt.executeQuery(check_bon1_c_requete);

                    while (rs3.next()) {
                        check_bon1_c_exist = true;
                    }

                    if(!check_bon1_c_exist){
                        String querry_select = "" +
                                "SELECT " +
                                "Bon2_temp.RECORDID, " +
                                "Bon2_temp.CODE_BARRE, " +
                                "Bon2_temp.NUM_BON, " +
                                "Bon2_temp.PRODUIT, " +
                                "Bon2_temp.QTE, " +
                                "Bon2_temp.PV_HT, " +
                                "Bon2_temp.TVA, " +
                                "Bon2_temp.CODE_DEPOT, " +
                                "Bon2_temp.PA_HT, " +
                                "Produit.STOCK " +
                                "FROM Bon2_temp " +
                                "INNER JOIN " +
                                "Produit ON (Bon2_temp.CODE_BARRE = Produit.CODE_BARRE) " +
                                "WHERE Bon2_temp.NUM_BON = '" + bon1sCommande.get(m).num_bon + "'";

                        bon2sCommande = controller.select_bon2_from_database(querry_select);
                        String[] buffer = new String[bon2sCommande.size()+1];

                        String generator_requet = "SELECT gen_id(gen_bcc1_id,1) as RECORDID FROM rdb$database";
                        ResultSet rs1 = stmt.executeQuery(generator_requet);
                        while (rs1.next()) {
                            recordid_numbon = rs1.getInt("RECORDID");
                        }


                        String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);
                        String vvv = "INSERT INTO BCC1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, HT, TVA, TIMBRE, REMISE, MODE_RG, UTILISATEUR, MODE_TARIF, CODE_DEPOT, EXPORTATION, BLOCAGE, LATITUDE, LONGITUDE) VALUES ('"
                                + recordid_numbon + "' , '" + NUM_BON + "' , cast ('" + bon1sCommande.get(m).date_bon + "' as timestamp) , '" + bon1sCommande.get(m).heure +
                                "' , '" + bon1sCommande.get(m).code_client + "' , '" + bon1sCommande.get(m).tot_ht + "' ,'" + bon1sCommande.get(m).tot_tva + "' , iif('"+ bon1sCommande.get(m).timbre+"' = 'null',0,'"+bon1sCommande.get(m).timbre+"'), iif('"+ bon1sCommande.get(m).remise+"' = 'null',0,'"+bon1sCommande.get(m).remise+"')," +
                                " 'ESPECE', 'TERMINAl-MOBILE', iif('"+ bon1sCommande.get(m).mode_tarif+"' = 'null',0,'"+bon1sCommande.get(m).mode_tarif+"'), '" + bon1sCommande.get(m).code_depot + "' , '" + bon1sCommande.get(m).exportation + "', 'F' , " + bon1sCommande.get(m).latitude + ", " + bon1sCommande.get(m).longitude + " )";

                        stmt.executeUpdate(vvv);


                        for (int j = 0; j < bon2sCommande.size(); j++) {
                            buffer[j] = "INSERT INTO BCC2 (NUM_BON, CODE_BARRE, PRODUIT, QTE, PV_HT, TVA, PA_HT, PV_HT_AR, CODE_DEPOT) VALUES ('" + NUM_BON + "' , '" + bon2sCommande.get(j).codebarre + "' , iif('"+ bon2sCommande.get(j).produit+"' = null,0,'"+bon2sCommande.get(j).produit.replace("'", " ").replace("\"", " ")+"'), '" + bon2sCommande.get(j).qte + "', '" + bon2sCommande.get(j).p_u + "' , '" + bon2sCommande.get(j).tva + "' , '" + bon2sCommande.get(j).pa_ht + "', '" + bon2sCommande.get(j).p_u + "', '" + bon2sCommande.get(j).code_depot + "')";
                            stmt.addBatch(buffer[j]);
                        }

                        stmt.executeBatch();
                        stmt.clearBatch();
                        bon_inserted_commande++;
                        con.commit();
                    }else{
                        bon_exist_commande++;
                    }
                }

                con.commit();
                con.setAutoCommit(true);
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
            if(integer == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé, Total bon commandes : "+ total_bon_commande +"\n Total bon inseré : " + bon_inserted_commande + "\n Total bon exist : " + bon_exist_commande )
                        .show();
            }else if(integer == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(integer == 3){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }


    // Importation liste client
    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_client_server_for_sychronisation_client extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;


        public Check_connection_client_server_for_sychronisation_client(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");
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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                con = null;
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
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                // get all transfert bon of this depot
                Import_client_from_server_task bon_client_task = new Import_client_from_server_task();
                bon_client_task.execute();
                mProgressDialog_Free.dismiss();
            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }

    }

    // Importation liste produit
    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Check_connection_produit_server_for_sychronisation_produit extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;


        public Check_connection_produit_server_for_sychronisation_produit(){
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");
        }

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
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);
                flag = 1;

            } catch (Exception e) {
                con = null;
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
        protected void onPostExecute(Integer integer) {
            if(integer == 1){
                // get all transfert bon of this depot
                Import_produit_from_server_task produit_task = new Import_produit_from_server_task();
                produit_task.execute();
                mProgressDialog_Free.dismiss();
            }else if(integer ==2){
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
            }else {
                mProgressDialog_Free.dismiss();
                Toast.makeText(ActivityImportsExport.this, "probleme fatal", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(integer);
        }

    }

    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_client_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        int compt = 0;
        int allrows=0;

        public Import_client_from_server_task(){

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

            prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
            code_depot = prefs.getString("CODE_DEPOT", "000000");
            code_vendeur = prefs.getString("CODE_VENDEUR", "000000");
        }

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
                boolean executed = false ;

                Intent intent;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();


                if((code_depot.toString().equals("000000")) && (!code_vendeur.toString().equals("000000"))){

                    String sql12 = "SELECT  COUNT(*) FROM CLIENTS WHERE CODE_VENDEUR = '"+ code_vendeur +"'";
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }
                    publishProgress(1);


                    //============================ GET Clients ===========================================
                    String sql0 = "SELECT  CLIENTS.CODE_CLIENT, CLIENTS.CLIENT, CLIENTS.TEL, CLIENTS.ADRESSE, coalesce(CLIENTS.MODE_TARIF , 0) AS MODE_TARIF, coalesce(CLIENTS.ACHATS,0) AS ACHATS, coalesce(CLIENTS.VERSER,0) AS VERSER, coalesce(CLIENTS.SOLDE,0) AS SOLDE, LATITUDE, LONGITUDE , coalesce(CLIENTS.CREDIT_LIMIT , 0) AS CREDIT_LIMIT FROM CLIENTS WHERE CODE_VENDEUR = '"+ code_vendeur +"' ";
                    ResultSet rs0 = stmt.executeQuery(sql0);
                    PostData_Client client;
                    while (rs0.next()) {

                        client = new PostData_Client();
                        client.code_client = rs0.getString("CODE_CLIENT");
                        client.client = rs0.getString("CLIENT");
                        client.tel = rs0.getString("TEL");
                        client.adresse = rs0.getString("ADRESSE");
                        client.mode_tarif =  rs0.getString("MODE_TARIF");
                        client.achat_montant =  rs0.getString("ACHATS");
                        client.verser_montant =  rs0.getString("VERSER");
                        client.solde_montant =  rs0.getString("SOLDE");
                        client.latitude =  rs0.getDouble("LATITUDE");
                        client.longitude =  rs0.getDouble("LONGITUDE");
                        client.credit_limit =  rs0.getDouble("CREDIT_LIMIT");

                        clients.add(client);

                        compt++;
                        publishProgress(compt);
                    }
                    executed = controller.ExecuteTransactionClient(clients);


                }else if((code_depot.toString().equals("000000"))&& (code_vendeur.toString().equals("000000")))
                {

                    String sql12 = "SELECT  COUNT(*) FROM CLIENTS";
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }
                    publishProgress(1);


                    //============================ GET Clients ===========================================
                    String sql0 = "SELECT  CLIENTS.CODE_CLIENT, CLIENTS.CLIENT, CLIENTS.TEL, CLIENTS.ADRESSE, coalesce(CLIENTS.MODE_TARIF , 0) AS MODE_TARIF, coalesce(CLIENTS.ACHATS,0) AS ACHATS, coalesce(CLIENTS.VERSER,0) AS VERSER, coalesce(CLIENTS.SOLDE,0) AS SOLDE, coalesce(CLIENTS.CREDIT_LIMIT , 0) AS CREDIT_LIMIT FROM CLIENTS";
                    ResultSet rs0 = stmt.executeQuery(sql0);
                    PostData_Client client;
                    while (rs0.next()) {

                        client = new PostData_Client();
                        client.code_client = rs0.getString("CODE_CLIENT");
                        client.client = rs0.getString("CLIENT");
                        client.tel = rs0.getString("TEL");
                        client.adresse = rs0.getString("ADRESSE");
                        client.mode_tarif =  rs0.getString("MODE_TARIF");
                        client.achat_montant =  rs0.getString("ACHATS");
                        client.verser_montant =  rs0.getString("VERSER");
                        client.solde_montant =  rs0.getString("SOLDE");

                        client.credit_limit =  rs0.getDouble("CREDIT_LIMIT");

                        clients.add(client);

                        compt++;
                        publishProgress(compt);
                    }
                    executed = controller.ExecuteTransactionClient(clients);
                }else
                    {

                    String sql12 = "SELECT  COUNT(*) FROM CLIENTS WHERE CODE_DEPOT = '"+ code_depot +"'";
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }
                    publishProgress(1);


                    //============================ GET Clients ===========================================
                    String sql0 = "SELECT  CLIENTS.CODE_CLIENT, CLIENTS.CLIENT, CLIENTS.TEL, CLIENTS.ADRESSE, coalesce(CLIENTS.MODE_TARIF , 0) AS MODE_TARIF, coalesce(CLIENTS.ACHATS,0) AS ACHATS, coalesce(CLIENTS.VERSER,0) AS VERSER, coalesce(CLIENTS.SOLDE,0) AS SOLDE, LATITUDE, LONGITUDE , coalesce(CLIENTS.CREDIT_LIMIT , 0) AS CREDIT_LIMIT FROM CLIENTS WHERE CODE_DEPOT = '"+ code_depot +"' ";
                    ResultSet rs0 = stmt.executeQuery(sql0);
                    PostData_Client client;
                    while (rs0.next()) {

                        client = new PostData_Client();
                        client.code_client = rs0.getString("CODE_CLIENT");
                        client.client = rs0.getString("CLIENT");
                        client.tel = rs0.getString("TEL");
                        client.adresse = rs0.getString("ADRESSE");
                        client.mode_tarif =  rs0.getString("MODE_TARIF");
                        client.achat_montant =  rs0.getString("ACHATS");
                        client.verser_montant =  rs0.getString("VERSER");
                        client.solde_montant =  rs0.getString("SOLDE");
                        client.latitude =  rs0.getDouble("LATITUDE");
                        client.longitude =  rs0.getDouble("LONGITUDE");
                        client.credit_limit =  rs0.getDouble("CREDIT_LIMIT");

                        clients.add(client);

                        compt++;
                        publishProgress(compt);
                    }
                    executed = controller.ExecuteTransactionClient(clients);
                }



                publishProgress(1);

                stmt.close();

                if (executed) {
                    flag = 1;
                }else{
                    flag = 3;
                }

            } catch (Exception e) {
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

            mProgressDialog.setMax(allrows);
            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            super.onPostExecute(integer);
        }
    }


    //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
    public class Import_produit_from_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection  con;
        Integer flag = 0;
        int compt = 0;
        int allrows=0;
        ArrayList<PostData_Produit> postData_produits ;


        public Import_produit_from_server_task(){

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

            prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
            code_depot = prefs.getString("CODE_DEPOT", "000000");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogConfigProduit();



        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Produit> produits = new ArrayList<>();
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();
                Boolean executed = false;


                Intent intent;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                publishProgress(1);

                String querry = "SELECT * FROM Produit";

                postData_produits = controller.select_produits_from_database(querry);

                if (code_depot.toString().equals("000000")) {

                    String sql12 = "SELECT  COUNT(*) FROM PRODUIT";
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next()) {
                        allrows = rs12.getInt("COUNT");
                    }

                    //Get product and  Insert it into produit tables
                    String sql3 = "SELECT  PRODUIT.CODE_BARRE, PRODUIT.REF_PRODUIT, PRODUIT.PRODUIT , coalesce(PRODUIT.PA_HT,0) AS PA_HT, coalesce(PRODUIT.TVA,0) AS TVA, cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT , PRODUIT.PHOTO , PRODUIT.STOCK , PRODUIT.DETAILLE FROM PRODUIT";
                    ResultSet rs3 = stmt.executeQuery(sql3);

                    PostData_Produit produit_update;
                    while (rs3.next()) {

                        produit_update = new PostData_Produit();

                        produit_update.code_barre = rs3.getString("CODE_BARRE");
                        produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                        produit_update.produit = rs3.getString("PRODUIT");
                        produit_update.pa_ht = rs3.getString("PA_HT");
                        produit_update.tva = rs3.getString("TVA");
                        produit_update.pv1_ht = rs3.getString("PV1_HT");
                        produit_update.pv2_ht = rs3.getString("PV2_HT");
                        produit_update.pv3_ht = rs3.getString("PV3_HT");
                        produit_update.stock = rs3.getString("STOCK");
                        produit_update.photo = rs3.getBytes("PHOTO");
                        produit_update.DETAILLE = rs3.getString("DETAILLE");

                        produits.add(produit_update);

                        compt++;
                        publishProgress(compt);
                    }


                    //Get all syn codebarre of this product  and  Insert it into codebarre tables
                    String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE ";
                    ResultSet rs4 = stmt.executeQuery(sql4);

                    PostData_Codebarre post_codebarre;
                    while (rs4.next()) {

                        post_codebarre = new PostData_Codebarre();
                        post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                        post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                        codebarres.add(post_codebarre);
                    }
                    executed = controller.ExecuteTransactionProduit(produits, codebarres);
                    if (executed)
                    {
                        flag = 1;
                    } else {
                        flag = 3;
                    }

                } else
                    {
                    if(postData_produits.size() <=0)
                    {

                    String sql12 = "SELECT  COUNT(*) FROM DEPOT2 WHERE CODE_DEPOT = '" + code_depot + "' ";
                    ResultSet rs12 = stmt.executeQuery(sql12);

                    while (rs12.next())
                    {
                        allrows = rs12.getInt("COUNT");
                    }


                    //Get product and  Insert it into produit tables
                    String sql3 = "SELECT DEPOT2.CODE_BARRE, PRODUIT.REF_PRODUIT, coalesce(DEPOT2.stock,0) AS STOCK, PRODUIT.PRODUIT , coalesce(PRODUIT.PA_HT,0) AS PA_HT, coalesce(PRODUIT.TVA,0) AS TVA, cast(coalesce(PRODUIT.PV1_HT,0) as decimal (17,2)) AS PV1_HT , cast (coalesce(PRODUIT.PV2_HT,0) as decimal(17,2))  AS PV2_HT, cast (coalesce(PRODUIT.PV3_HT,0) as decimal(17,2)) AS PV3_HT , PRODUIT.PHOTO, PRODUIT.DETAILLE  " +
                            " FROM DEPOT2 " +
                            " LEFT JOIN produit ON ( PRODUIT.code_barre = DEPOT2.code_barre ) " +
                            " WHERE DEPOT2.code_depot = '" + code_depot + "' ";

                    ResultSet rs3 = stmt.executeQuery(sql3);
                    produits.clear();
                    PostData_Produit produit_update;
                    while (rs3.next()) {

                        produit_update = new PostData_Produit();

                        produit_update.code_barre = rs3.getString("CODE_BARRE");
                        produit_update.ref_produit = rs3.getString("REF_PRODUIT");
                        produit_update.produit = rs3.getString("PRODUIT");
                        produit_update.pa_ht = rs3.getString("PA_HT");
                        produit_update.tva = rs3.getString("TVA");
                        produit_update.pv1_ht = rs3.getString("PV1_HT");
                        produit_update.pv2_ht = rs3.getString("PV2_HT");
                        produit_update.pv3_ht = rs3.getString("PV3_HT");
                        produit_update.stock = rs3.getString("STOCK");
                        produit_update.photo = rs3.getBytes("PHOTO");
                        produit_update.DETAILLE = rs3.getString("DETAILLE");
                        produits.add(produit_update);

                        compt++;
                        publishProgress(compt);
                    }

                    //Get all syn codebarre of this product  and  Insert it into codebarre tables
                    String sql4 = "SELECT  CODEBARRE.CODE_BARRE, CODEBARRE.CODE_BARRE_SYN FROM CODEBARRE where codebarre.code_barre in ( select CODE_BARRE FROM DEPOT2 WHERE DEPOT2.code_depot = '" + code_depot + "')";
                    ResultSet rs4 = stmt.executeQuery(sql4);
                    codebarres.clear();
                    PostData_Codebarre post_codebarre;
                    while (rs4.next()) {

                        post_codebarre = new PostData_Codebarre();
                        post_codebarre.code_barre = rs4.getString("CODE_BARRE");
                        post_codebarre.code_barre_syn = rs4.getString("CODE_BARRE_SYN");
                        codebarres.add(post_codebarre);
                    }

                    executed = controller.ExecuteTransactionProduit(produits, codebarres);

                        stmt.close();

                    if (executed) {

                        flag = 1;
                    } else {
                        flag = 3;
                    }
                }else
                    {
                        runOnUiThread(new Runnable(){
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Liste des produits n'est pas vide",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }



            }catch (Exception e) {
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

            mProgressDialog.setMax(allrows);

            mProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
            SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");

            currentTime = Calendar.getInstance().getTime();
            currentDateTimeString = sdf.format(currentTime);
            SharedPreferences pref = getBaseContext().getSharedPreferences(MY_PREF_NAME, 0);
            SharedPreferences.Editor  editor = pref.edit();
            editor.putString("time", currentDateTimeString);
            editor.apply();
            super.onPostExecute(integer);

        }
    }



    //class Insert Data into FireBird Database
    //====================================
    public class Export_inventaire_to_server_task extends AsyncTask<Void, Void, Integer> {

        Connection  con;
        ArrayList<PostData_Inv1> invs1 = new ArrayList<>();
        ArrayList<PostData_Inv2> invs2 = new ArrayList<>();
        Integer recordid_inv1;
        //Boolean executed = false;
        Integer flag = 0;
        Boolean _all = false;
        String _num_inv = null;
        Integer total_inventaire = 0;
        Integer inventaire_inserer = 0;
        Integer inventaire_exist = 0;

        String erreurMessage ="";

        public Export_inventaire_to_server_task(Boolean all, String num_inv){
            _all = all;
            _num_inv = num_inv;

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

            prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
            code_depot = prefs.getString("CODE_DEPOT", "000000");
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




                System.setProperty("FBAdbLog", "true");
                java.sql.DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();
                con.setAutoCommit(false);



              /*  if(invs1.get(0).is_sent == 1){
                    flag = 4;
                }else{*/
                SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");

                if(_all){

                    String querry = "SELECT * FROM Inventaires1";

                    invs1.clear();
                    invs1 = controller.select_list_inventaire_from_database(querry);
                    if(!code_depot.equals("000000")){

                        total_inventaire = invs1.size();
                        for (int i = 0; i < invs1.size(); i++) {

                            String querry_select = "SELECT * FROM Inventaires2 WHERE NUM_INV = '" + invs1.get(i).num_inv + "'";
                            invs2 = controller.select_inventaire2_from_database(querry_select);
                            String[] buffer = new String[invs2.size()];

                                String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(ggg);
                            while (rs1.next()) {
                                recordid_inv1 = rs1.getInt("RECORDID");
                            }

                            String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);


                            Date myDate = null;
                            try {
                                myDate = df_show.parse(invs1.get(i).date_inv);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String formattedDate_Save = df_save.format(myDate);

                            String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, UTILISATEUR, CODE_DEPOT ) VALUES ('"
                                    + recordid_inv1 + "' , '" + NUM_INV+ "' , '" + formattedDate_Save + "', '" + invs1.get(i).heure_inv + "' , '" + invs1.get(i).nom_inv +
                                    "' , '" + invs1.get(i).utilisateur + "' , '" + invs1.get(i).code_depot + "' )";
                            stmt.executeUpdate(vvv);

                            for (int j = 0; j < invs2.size(); j++) {
                                buffer[j] = "INSERT INTO INV2 (CODE_BARRE, NUM_INV, PA_HT, QTE, TVA,  QTE_TMP, CODE_DEPOT" +
                                        ") VALUES ('" + invs2.get(j).codebarre + "' , '"+ NUM_INV +"' , '" +
                                        invs2.get(j).pa_ht + "' , '" + invs2.get(j).quantity_old + "', '" + invs2.get(j).tva + "' , '" + invs2.get(j).quantity_new + "' , '" + invs1.get(i).code_depot + "')";
                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            stmt.clearBatch();
                            controller.Update_inventaire1(invs1.get(i).num_inv);
                            invs2.clear();
                            inventaire_inserer++;
                        }

                    }else{

                        total_inventaire = invs1.size();
                        for (int i = 0; i < invs1.size(); i++) {

                            String querry_select = "SELECT * FROM Inventaires2 WHERE NUM_INV = '" + invs1.get(i).num_inv + "'";
                            invs2 = controller.select_inventaire2_from_database(querry_select);
                            String[] buffer = new String[invs2.size()];

                            String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(ggg);
                            while (rs1.next()) {
                                recordid_inv1 = rs1.getInt("RECORDID");
                            }
                            String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);

                            Date myDate = null;
                            try {
                                myDate = df_show.parse(invs1.get(i).date_inv);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String formattedDate_Save = df_save.format(myDate);

                            String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, UTILISATEUR ) VALUES ('"
                                    + recordid_inv1 + "' , '" + NUM_INV+ "' , '" + formattedDate_Save + "' , '" + invs1.get(i).heure_inv + "', '" + invs1.get(i).nom_inv +
                                    "' , '" + invs1.get(i).utilisateur + "')";
                            stmt.executeUpdate(vvv);

                            for (int j = 0; j < invs2.size(); j++) {
                                buffer[j] = "INSERT INTO INV2 (CODE_BARRE, NUM_INV, PA_HT, QTE, TVA, QTE_TMP, CODE_DEPOT" +
                                        ") VALUES ('" + invs2.get(j).codebarre + "' , '"+ NUM_INV +"' , '" +
                                        invs2.get(j).pa_ht + "' ,'" + invs2.get(j).quantity_old + "', '" + invs2.get(j).tva + "',  '" + invs2.get(j).quantity_new + "' , '" + invs1.get(i).code_depot + "')";
                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            stmt.clearBatch();
                            controller.Update_inventaire1(invs1.get(i).num_inv);
                            invs2.clear();
                            inventaire_inserer++;
                        }
                    }

                    con.commit();
                    con.setAutoCommit(true);
                    invs1.clear();
                    flag = 1;
                    // }
                }else{ /// else case export once invontory.
                    String querry = "SELECT * FROM Inventaires1 WHERE NUM_INV = '"+ _num_inv +"'";
                    invs1.clear();
                    invs1 = controller.select_list_inventaire_from_database(querry);
                    if(invs1.size() > 0){

                        if(!code_depot.equals("000000")){

                            String querry_select = "SELECT * FROM Inventaires2 WHERE NUM_INV = '" + _num_inv + "'";
                            invs2 = controller.select_inventaire2_from_database(querry_select);
                            String[] buffer = new String[invs2.size()];

                            String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(ggg);
                            while (rs1.next()) {
                                recordid_inv1 = rs1.getInt("RECORDID");
                            }
                            String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);


                            Date myDate = null;
                            try {
                                myDate = df_show.parse(invs1.get(0).date_inv);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String formattedDate_Save = df_save.format(myDate);

                            String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, UTILISATEUR, CODE_DEPOT ) VALUES ('"
                                    + recordid_inv1 + "' , '" + NUM_INV+ "' , '" + formattedDate_Save + "' , '" + invs1.get(0).heure_inv + "', '" + invs1.get(0).nom_inv +
                                    "' , '" + invs1.get(0).utilisateur + "' , '" + invs1.get(0).code_depot + "' )";
                            stmt.executeUpdate(vvv);

                            for (int j = 0; j < invs2.size(); j++) {
                                buffer[j] = "INSERT INTO INV2 (CODE_BARRE, REF_PRODUIT, NUM_INV, PRODUIT, PA_HT, QTE, TVA, QTE_NEW, CODE_DEPOT" +
                                        ") VALUES ('" + invs2.get(j).codebarre + "' , '" +
                                        invs2.get(j).reference + "' , '"+ NUM_INV +"' , '" + invs2.get(j).produit + "', '" +
                                        invs2.get(j).pa_ht + "' , '" + invs2.get(j).quantity_old + "', '" + invs2.get(j).tva + "', '" + invs2.get(j).quantity_new + "' , '" + invs1.get(0).code_depot + "')";
                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            stmt.clearBatch();
                            controller.Update_inventaire1(_num_inv);
                            invs2.clear();

                        }else{

                            String querry_select = "SELECT * FROM Inventaires2 WHERE NUM_INV = '" + _num_inv + "'";
                            invs2 = controller.select_inventaire2_from_database(querry_select);
                            String[] buffer = new String[invs2.size()];

                            String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(ggg);
                            while (rs1.next()) {
                                recordid_inv1 = rs1.getInt("RECORDID");
                            }
                            String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);


                            Date myDate = null;
                            try {
                                myDate = df_show.parse(invs1.get(0).date_inv);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            String formattedDate_Save = df_save.format(myDate);

                            String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, UTILISATEUR ) VALUES ('"
                                    + recordid_inv1 + "' , '" + NUM_INV+ "' , '" + formattedDate_Save + "', '" + invs1.get(0).heure_inv + "' , '" + invs1.get(0).nom_inv +
                                    "' , '" + invs1.get(0).utilisateur + "')";
                            stmt.executeUpdate(vvv);

                            for (int j = 0; j < invs2.size(); j++) {
                                buffer[j] = "INSERT INTO INV2 (CODE_BARRE, REF_PRODUIT, NUM_INV, PRODUIT, PA_HT, QTE, TVA, QTE_TMP, CODE_DEPOT" +
                                        ") VALUES ('" + invs2.get(j).codebarre + "' , '" +
                                        invs2.get(j).reference + "' , '"+ NUM_INV +"' , '" + invs2.get(j).produit + "', '" + invs2.get(j).pa_ht + "' , '" + invs2.get(j).quantity_old + "', '" + invs2.get(j).tva + "', '" +
                                        invs2.get(j).quantity_new + "', '" + invs1.get(0).code_depot + "')";
                                stmt.addBatch(buffer[j]);
                            }

                            stmt.executeBatch();
                            stmt.clearBatch();
                            controller.Update_inventaire1(_num_inv);
                            invs2.clear();
                        }
                    }
                    con.commit();
                    con.setAutoCommit(true);
                    invs1.clear();
                    flag = 1;
                    // }
                }


            } catch (Exception ex) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                    erreurMessage = ex.getMessage();
                } else {
                    Log.e("tremtek", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");

                    //not executed with problem in the sql statement
                    flag = 3;
                    erreurMessage = ex.getMessage();
                }
            }
            return flag;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            mProgressDialog.dismiss();

            inventaire_exist = total_inventaire - inventaire_inserer;
            if(integer == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé, Total inventaire : "+ total_inventaire + "\n bons  inserés : " + inventaire_inserer + "\n bons exists : " + inventaire_exist  )
                        .show();
            }else if(integer == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(integer == 3){
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
    public String Get_Digits_String(String number, Integer length) {
        String _number = number;
        while (_number.length() < length) {
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }
//------------------------------------------------------------------------------------------------

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }
    public class Exporter_ventesx_to_server_task extends AsyncTask<Void, Integer, Integer> {

        Connection con;
        Integer flag = 0;
        String erreurMessage ="";

        int total_bon = 0;
        int bon_inserted = 0;
        int bon_exist = 0;

        int total_client = 0;
        int total_versement = 0;
        SharedPreferences prefs;

        public Exporter_ventesx_to_server_task() {

            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            Server = prefs.getString("ip", "192.168.1.94");
            Path = prefs.getString("path", "C:/PMEPRO1122");
            Username = prefs.getString("username", "SYSDBA");
            Password = prefs.getString("password", "masterkey");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogExportation();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            try {

                ArrayList<PostData_Bon1> bon1s = new ArrayList<>();
                ArrayList<PostData_Bon2> bon2s = new ArrayList<>();
                PostData_Client client;

                System.setProperty("FBAdbLog", "true");
                DriverManager.setLoginTimeout(5);
                Class.forName("org.firebirdsql.jdbc.FBDriver");
                String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                con = DriverManager.getConnection(sCon, Username, Password);

                Statement stmt = con.createStatement();

                con.setAutoCommit(false);
                int recordid_numbon = 0;

                String querry = "SELECT " +
                        "Bon1.RECORDID, " +
                        "Bon1.NUM_BON, " +
                        "Bon1.CODE_CLIENT, " +
                        "Client.CLIENT, " +
                        "Bon1.LATITUDE, " +

                        "Bon1.LONGITUDE, " +
                        "Client.LATITUDE as LATITUDE_CLIENT, " +

                        "Client.LONGITUDE as LONGITUDE_CLIENT, " +
                        "Client.TEL, " +
                        "Client.ADRESSE, " +
                        "Client.RC, " +
                        "Client.IFISCAL, " +
                        "Bon1.DATE_BON, " +
                        "Bon1.HEURE, " +
                        "Bon1.NBR_P, " +
                        "Bon1.MODE_TARIF, " +

                        "Bon1.CODE_DEPOT, " +
                        "Bon1.MONTANT_BON, " +
                        "Bon1.MODE_RG, " +
                        "Bon1.CODE_VENDEUR, " +
                        "Bon1.EXPORTATION, " +
                        "Bon1.REMISE, " +
                        "Bon1.TOT_TTC, " +
                        "Bon1.TOT_TVA, " +
                        "Bon1.TOT_HT, " +




                        "Bon1.TIMBRE, " +
                        "Bon1.VERSER, " +
                        "Bon1.RESTE, " +
                        "Bon1.BLOCAGE, " +
                        "Bon1.ANCIEN_SOLDE " +
                        "FROM Bon1 " +
                        "LEFT JOIN Client ON " +
                        "Bon1.CODE_CLIENT = Client.CODE_CLIENT " +
                        "WHERE BLOCAGE = 'F' ORDER BY Bon1.DATE_BON DESC";

                bon1s.clear();
                bon1s = controller.select_vente_from_database(querry);
                total_bon = bon1s.size();

                 prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

                prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
                code_depot = prefs.getString("CODE_DEPOT", "000000");


                // Get CODE_VENDEUR , CODE_CAISSE
                String CODE_VENDEUR = "NOT_EXIST";
                String CODE_CAISSE = "NOT_EXIST";

                String requette_vendeur_caisse = "SELECT coalesce(CODE_VENDEUR, 'NOT_EXIST') AS CODE_VENDEUR, coalesce(CODE_CAISSE, 'NOT_EXIST') AS CODE_CAISSE FROM DEPOT1 WHERE CODE_DEPOT = '"+ code_depot +"' ";
                ResultSet rs111 = stmt.executeQuery(requette_vendeur_caisse);
                while (rs111.next()) {
                    CODE_VENDEUR = rs111.getString("CODE_VENDEUR");
                    CODE_CAISSE = rs111.getString("CODE_CAISSE");
                }

                for (int i = 0; i < bon1s.size(); i++) {

                    /////////////////////////////   CHECK IF CLIENT EXIST THEN INSERT IT INTO FIREBIRD DATABASES /////////////////////////////////////////
                    Boolean client_exist = false;
                    // check client if exist
                    String client_requete = "SELECT CODE_CLIENT FROM CLIENTS WHERE CODE_CLIENT = '" + bon1s.get(i).code_client + "'";
                    ResultSet rs0 = stmt.executeQuery(client_requete);
                    while (rs0.next()) {
                        client_exist = true;
                    }

                    // insert client if not exist
                    if(!client_exist){
                        client = new PostData_Client();

                        // Select client from data base to insert it
                        client = controller.select_client_from_database(bon1s.get(i).code_client);
                        String insert_client;
                        // insert client
                        insert_client = "INSERT INTO CLIENTS (CODE_CLIENT, CLIENT, ADRESSE, TEL, NUM_RC, NUM_IF, RIB, ACHATS, VERSER, LATITUDE, LONGITUDE, CODE_DEPOT) VALUES ('"
                                + bon1s.get(i).code_client + "' , iif('"+ client.code_client +"' = null,'Client inconnu','"+client.client.replace("'", " ")+"') , iif('"+ client.adresse +"' = null,'Adresse inconnu', '"+ client.adresse.replace("'", " ") +"'),  iif('"+ client.tel +"' = '',0,'"+client.tel +"') , iif('"+ client.rc +"' = '',0,'"+client.rc +"') , iif('"+ client.ifiscal +"' = '',0,'"+client.ifiscal +"') , iif('"+ client.rib +"' = '',0,'"+client.rib +"'), '" + client.achat_montant + "', '" + client.verser_montant + "', '"+client.latitude+"', '" + client.longitude + "','" + bon1s.get(i).code_depot + "')";




                        if(stmt.executeUpdate(insert_client) > 0){

                            //commit insert client
                            con.commit();

                            // check if bon1 exist
                            Boolean check_bon1_exist = false;
                            String check_bon1_requete = "SELECT NUM_BON FROM BON1 WHERE EXPORTATION = '" + bon1s.get(i).exportation + "'";
                            ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                            while (rs3.next()) {
                                check_bon1_exist = true;
                            }

                            if(!check_bon1_exist){
                                String querry_select = "" +
                                        "SELECT " +
                                        "Bon2.RECORDID, " +
                                        "Bon2.CODE_BARRE, " +
                                        "Bon2.NUM_BON, " +
                                        "Bon2.PRODUIT, " +
                                        "Bon2.QTE, " +
                                        "Bon2.PV_HT, " +
                                        "Bon2.TVA, " +
                                        "Bon2.CODE_DEPOT, " +
                                        "Bon2.PA_HT, " +
                                        "Produit.STOCK " +
                                        "FROM Bon2 " +
                                        "INNER JOIN " +
                                        "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                                        "WHERE Bon2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


                                bon2s = controller.select_bon2_from_database(querry_select);
                                String[] buffer = new String[bon2s.size()+1];

                                // Get RECORDID
                                String generator_requet = "SELECT gen_id(gen_bon1_id,1) as RECORDID FROM rdb$database";
                                ResultSet rs1 = stmt.executeQuery(generator_requet);
                                while (rs1.next()) {
                                    recordid_numbon = rs1.getInt("RECORDID");
                                }

                                String vvv;

                                String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);
                                vvv = "INSERT INTO BON1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, HT, TVA, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, LATITUDE, LONGITUDE, CODE_VENDEUR, CODE_CAISSE, CODE_DEPOT) VALUES ('"
                                        + recordid_numbon + "' , '" + NUM_BON + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                        "' , '" + bon1s.get(i).code_client + "' , '" + bon1s.get(i).tot_ht + "' ,'" + bon1s.get(i).tot_tva + "' , iif('"+ bon1s.get(i).timbre+"' = 'null',0,'"+bon1s.get(i).timbre+"'), iif('"+ bon1s.get(i).remise+"' = 'null',0,'"+bon1s.get(i).remise+"')," +
                                        " '" + bon1s.get(i).verser + "', '" + bon1s.get(i).solde_ancien + "', iif('"+ bon1s.get(i).mode_rg+"' = 'null',null,'"+bon1s.get(i).mode_rg+"') , 'TERMINAl-MOBILE', iif('"+ bon1s.get(i).mode_tarif+"' = 'null',0,'"+bon1s.get(i).mode_tarif+"'), '" + bon1s.get(i).exportation + "', 'F' , " + bon1s.get(i).latitude + ", " + bon1s.get(i).longitude + ", iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'),'"+bon1s.get(i).code_depot+"')";
                                stmt.executeUpdate(vvv);


                                for (int j = 0; j < bon2s.size(); j++) {
                                    buffer[j] = "INSERT INTO BON2 (NUM_BON, CODE_BARRE, PRODUIT, QTE, PV_HT, TVA, PA_HT, PV_HT_AR, CODE_DEPOT) VALUES ('" + NUM_BON + "' , '" + bon2s.get(j).codebarre + "' , iif('"+ bon2s.get(j).produit+"' = null, 'Produit inconnu' , '"+bon2s.get(j).produit+"'), '" + bon2s.get(j).qte + "', '" + bon2s.get(j).p_u + "' , '" + bon2s.get(j).tva + "' , '" + bon2s.get(j).pa_ht + "', '" + bon2s.get(j).p_u + "','"+bon2s.get(j).code_depot+"')";
                                    stmt.addBatch(buffer[j]);
                                }








                                String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                        + bon1s.get(i).code_client + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                        "' , '" + bon1s.get(i).montant_bon + "' , '" + bon1s.get(i).verser + "' ,'BL-VENTE' , '" + NUM_BON + "', '" + bon1s.get(i).mode_rg + "', 'TERMINAL_MOBILE', ' ', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null ,'"+ CODE_CAISSE +"'))";
                                stmt.executeUpdate(requete_situation);


                                stmt.executeBatch();
                                stmt.clearBatch();

                                if(!CODE_CAISSE.equals("NOT_EXIST")){
                                    if(Double.valueOf(bon1s.get(i).verser) != 0){

                                        String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                                + CODE_CAISSE + "' , '" + CODE_CAISSE + "', cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).verser +
                                                "' , '0', 'BL-VENTE' , '" + NUM_BON + "', 'ESPECE', ' ', 'TERMINAL_MOBILE')";
                                        stmt.executeUpdate(requete_caisse);
                                    }
                                }

                                stmt.executeBatch();
                                con.commit();
                                // update bon as exported
                                controller.Update_ventes_commandes_as_exported(false, bon1s.get(i).num_bon);
                                bon2s.clear();
                                stmt.clearBatch();
                                bon_inserted++;


                            }else{
                                bon_exist++;
                            }
                        }else{
                            // Problem insert client into database // operation aborded
                            new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Problème fatal, erreur insertion client ")
                                    .show();
                        }
                    }else{
                        // check if bon1 exist
                        Boolean check_bon1_exist = false;
                        String check_bon1_requete = "SELECT NUM_BON FROM BON1 WHERE EXPORTATION = '" + bon1s.get(i).exportation + "'";
                        ResultSet rs3 = stmt.executeQuery(check_bon1_requete);

                        while (rs3.next()) {
                            check_bon1_exist = true;
                        }

                        if(!check_bon1_exist){
                            String querry_select = "" +
                                    "SELECT " +
                                    "Bon2.RECORDID, " +
                                    "Bon2.CODE_BARRE, " +
                                    "Bon2.NUM_BON, " +
                                    "Bon2.PRODUIT, " +
                                    "Bon2.QTE, " +
                                    "Bon2.PV_HT, " +
                                    "Bon2.TVA, " +
                                    "Bon2.CODE_DEPOT, " +
                                    "Bon2.PA_HT, " +
                                    "Produit.STOCK " +
                                    "FROM Bon2 " +
                                    "INNER JOIN " +
                                    "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                                    "WHERE Bon2.NUM_BON = '" + bon1s.get(i).num_bon + "'";

                            bon2s = controller.select_bon2_from_database(querry_select);
                            String[] buffer = new String[bon2s.size()+1];

                            String generator_requet = "SELECT gen_id(gen_bon1_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            while (rs1.next()) {
                                recordid_numbon = rs1.getInt("RECORDID");
                            }

                            String vvv;
                            String NUM_BON = Get_Digits_String(String.valueOf(recordid_numbon), 6);
                            vvv = "INSERT INTO BON1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_CLIENT, HT, TVA, TIMBRE, REMISE, VERSER, ANCIEN_SOLDE, MODE_RG, UTILISATEUR, MODE_TARIF, EXPORTATION, BLOCAGE, LATITUDE, LONGITUDE, CODE_VENDEUR, CODE_CAISSE,CODE_DEPOT) VALUES ('"
                                    + recordid_numbon + "' , '" + NUM_BON + "' , CAST ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                    "' , '" + bon1s.get(i).code_client + "' , '" + bon1s.get(i).tot_ht + "' ,'" + bon1s.get(i).tot_tva + "' , iif('" + bon1s.get(i).timbre + "' = 'null',0,'" + bon1s.get(i).timbre + "'), iif('" + bon1s.get(i).remise + "' = 'null',0,'" + bon1s.get(i).remise + "')," +
                                    " '" + bon1s.get(i).verser + "', '" + bon1s.get(i).solde_ancien + "', iif('" + bon1s.get(i).mode_rg + "' = 'null',null,'" + bon1s.get(i).mode_rg + "') , 'TERMINAl-MOBILE', iif('" + bon1s.get(i).mode_tarif + "' = 'null',0,'" + bon1s.get(i).mode_tarif + "'), '" + bon1s.get(i).exportation + "', 'F' , " + bon1s.get(i).latitude + ", " + bon1s.get(i).longitude + ", iif('" + CODE_VENDEUR + "' = 'NOT_EXIST', null,'" + CODE_VENDEUR + "'), iif('" + CODE_CAISSE + "' = 'NOT_EXIST', null,'" + CODE_CAISSE + "'),'"+bon1s.get(i).code_depot+"')";
                            stmt.executeUpdate(vvv);


                            for (int j = 0; j < bon2s.size(); j++) {
                                buffer[j] = "INSERT INTO BON2 (NUM_BON, CODE_BARRE, PRODUIT, QTE, PV_HT, TVA, PA_HT, PV_HT_AR,CODE_DEPOT) VALUES ('" + NUM_BON + "' , '" + bon2s.get(j).codebarre + "' , iif('" + bon2s.get(j).produit + "' = null, 'Produit inconnu' , '"+bon2s.get(j).produit
                                       +"'), '"+ bon2s.get(j).qte + "', '" + bon2s.get(j).p_u + "' , '" + bon2s.get(j).tva + "' , '" + bon2s.get(j).pa_ht + "', '" + bon2s.get(j).p_u + "','"+bon2s.get(j).code_depot+"')";
                                stmt.addBatch(buffer[j]);
                            }





                            String requete_situation = "INSERT INTO CARNET_C (CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"
                                    + bon1s.get(i).code_client + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).heure +
                                    "' , '" + bon1s.get(i).montant_bon + "' , '" + bon1s.get(i).verser + "' ,'BL-VENTE' , '" + NUM_BON + "', '" + bon1s.get(i).mode_rg + "', 'TERMINAL_MOBILE', ' ', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'))";
                            stmt.executeUpdate(requete_situation);


                            if(!CODE_CAISSE.equals("NOT_EXIST")){
                                if(Double.valueOf(bon1s.get(i).verser) != 0){

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , cast ('" + bon1s.get(i).date_bon + "' as timestamp) , '" + bon1s.get(i).verser +
                                            "' , '0', 'BL-VENTE' , '" + NUM_BON + "', 'ESPECE', ' ', 'TERMINAL_MOBILE')";
                                    stmt.executeUpdate(requete_caisse);
                                }
                            }

                            stmt.executeBatch();
                            con.commit();
                            //update bon as exported
                            controller.Update_ventes_commandes_as_exported(false, bon1s.get(i).num_bon);
                            stmt.clearBatch();
                            bon2s.clear();
                            bon_inserted++;

                        }else
                            {
                            bon_exist++;
                        }
                    }
                }

                // Export all versement

                // INSERT ALL VERSEMENTS OF CAURANT CLIENTS
                ArrayList<PostData_Carnet_c> all_versement_client = new ArrayList<>();
                ArrayList<PostData_Client> list_client = new ArrayList<>();
                stmt.clearBatch();
                list_client = controller.select_clients_from_database("SELECT * FROM Client");
                for(int q = 0; q < list_client.size() ; q ++) {
                    all_versement_client.clear();
                    String q1 = "SELECT * "+

                            "FROM Carnet_c " +

                            "WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + list_client.get(q).code_client + "'";
                    String q2 = "SELECT Carnet_c.RECORDID, " +
                            "Carnet_c.CODE_CLIENT, " +
                            "Carnet_c.DATE_CARNET, " +
                            "Client.CLIENT, " +
                            "Client.LATITUDE, " +

                            "Client.LONGITUDE, " +
                            "Client.TEL, " +
                            "Client.ADRESSE, " +
                            "Client.RC, " +
                            "Client.IFISCAL, " +

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
                            "Client.CODE_CLIENT = Carnet_c.CODE_CLIENT " +
                            "WHERE IS_EXPORTED = 0 AND Carnet_c.CODE_CLIENT = '" + list_client.get(q).code_client + "'";
                    all_versement_client = controller.select_carnet_c_from_database(q2);

                    if(all_versement_client.size() > 0){

                        total_client++;

                        for (int g = 0; g < all_versement_client.size(); g++) {
                            // Get carnet_c recordid
                            String generator_requet = "SELECT gen_id(gen_carnet_c_id,1) as RECORDID FROM rdb$database";
                            ResultSet rs1 = stmt.executeQuery(generator_requet);
                            Integer num_bon_carnet_c = 0;
                            while (rs1.next()) {
                                num_bon_carnet_c = rs1.getInt("RECORDID");
                            }

                            //String RECORDID_CRC = Get_Digits_String(String.valueOf(num_bon_carnet_c), 6);
                            String buffer_versement = "INSERT INTO CARNET_C (RECORDID, CODE_CLIENT, DATE_CARNET, HEURE, ACHATS, VERSEMENTS, SOURCE, NUM_BON, MODE_RG, UTILISATEUR, REMARQUES, CODE_VENDEUR, CODE_CAISSE) VALUES ('"+ num_bon_carnet_c + "', '"
                                    + all_versement_client.get(g).code_client + "' , cast ('" + all_versement_client.get(g).carnet_date + "' as timestamp) , '" + all_versement_client.get(g).carnet_heure +
                                    "' , '" + all_versement_client.get(g).carnet_achats + "' , '" + all_versement_client.get(g).carnet_versement + "' ,'SITUATION-CLIENT' ,'VRC" + num_bon_carnet_c + "', '" + all_versement_client.get(g).carnet_mode_rg + "', 'TERMINAL_MOBILE', '" + all_versement_client.get(g).carnet_remarque + "', iif('"+ CODE_VENDEUR+"' = 'NOT_EXIST', null,'"+ CODE_VENDEUR +"'), iif('"+ CODE_CAISSE+"' = 'NOT_EXIST', null,'"+ CODE_CAISSE +"'))";
                            stmt.executeUpdate(buffer_versement);


                            if (!CODE_CAISSE.equals("NOT_EXIST")) {
                                if (Double.valueOf(all_versement_client.get(g).carnet_versement) != 0) {

                                    String requete_caisse = "INSERT INTO CAISSE2 (CODE_CAISSE, CODE_CAISSE1, DATE_CAISSE, ENTREE , SORTIE, SOURCE , NUM_SOURCE , MODE_RG , REMARQUE , UTILISATEUR) VALUES ('"
                                            + CODE_CAISSE + "', '" + CODE_CAISSE + "' , cast ('" + all_versement_client.get(g).carnet_date + "' as timestamp) , '" + all_versement_client.get(g).carnet_versement +
                                            "' , '0', 'SITUATION-CLIENT' , 'VRC" + num_bon_carnet_c + "', 'ESPECE',  '" + all_versement_client.get(g).carnet_remarque + "', 'TERMINAL_MOBILE')";
                                    stmt.executeUpdate(requete_caisse);
                                }
                            }

                            total_versement++;
                        }

                        con.commit();
                        // All ok so Make all versements of this client as exported
                        controller.Update_versement_exported(list_client.get(q).code_client);
                    }
                }

                con.setAutoCommit(true);
                bon1s.clear();
                flag = 1;


            } catch (Exception ex) {
                con = null;
                Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                if (ex.getMessage().contains("Unable to complete network request to host")) {
                    flag = 2;
                    Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
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
            if(integer == 1){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Exportation...")
                        .setContentText("Exportation terminé, Total bon Vente : "+ total_bon +"\n Total bon inseré : " + bon_inserted + "\n Total bon exist : " + bon_exist + "\n =================="+"\n Total client versés : "+ total_client + "\n Total versement : " + total_versement )
                        .show();
            }else if(integer == 2){
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention. !")
                        .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                        .show();
            }else if(integer == 3){
                //  if(ActivityImportsExport. != null)
                new SweetAlertDialog(ActivityImportsExport.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Erreur...")
                        .setContentText("Erreur SQL : " + erreurMessage)
                        .show();
            }

            super.onPostExecute(integer);
        }
    }
}

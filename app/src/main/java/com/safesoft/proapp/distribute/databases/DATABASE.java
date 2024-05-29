package com.safesoft.proapp.distribute.databases;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Famille;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Position;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Etatv;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;
import com.safesoft.proapp.distribute.postData.PostData_commune;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/08/2016.
 */
public class DATABASE extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4; // Database version
    private static final String DATABASE_NAME = "safe_distribute_pro"; //Database name
    private final Context mContext;

    private final String PREFS = "ALL_PREFS";

    //Constructor DATABASE
    public DATABASE(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub

        mContext = context;
       // String path = mContext.getDatabasePath(DATABASE_NAME).getPath();
       //  File f = mContext.getDatabasePath(DATABASE_NAME);

       //context.deleteDatabase("distribute_pro_data");
       //context.deleteDatabase(DATABASE_NAME);

      // https://www.youtube.com/watch?v=DV24nJCQR10

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.v("TRACKKK","================>  ONCREATE EXECUTED");


        db.execSQL("CREATE TABLE IF NOT EXISTS PRODUIT(PRODUIT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_BARRE VARCHAR, " +
                "REF_PRODUIT VARCHAR, " +
                "PRODUIT VARCHAR, " +
                "PA_HT DOUBLE, " +
                "TVA DOUBLE, " +
                "PAMP DOUBLE, " +
                "PV1_HT DOUBLE, " +
                "PV2_HT DOUBLE, " +
                "PV3_HT DOUBLE , " +
                "PV4_HT DOUBLE, " +
                "PV5_HT DOUBLE, " +
                "PV6_HT DOUBLE, " +
                "STOCK DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "PHOTO BLOB, " +
                "DETAILLE VARCHAR, " +
                "DESTOCK_TYPE VARCHAR, " +
                "DESTOCK_CODE_BARRE VARCHAR, " +
                "DESTOCK_QTE DOUBLE, " +
                "FAMILLE VARCHAR, " +
                "PROMO INTEGER, " +
                "D1 VARCHAR, " +
                "D2 VARCHAR, " +
                "PP1_HT DOUBLE, " +
                "ISNEW INTEGER)");


        db.execSQL("CREATE TABLE IF NOT EXISTS CODEBARRE(CODEBARRE_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE_SYN VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS COMPOSANT(COMPOSANT_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE2 VARCHAR, QTE DOUBLE)");


        db.execSQL("CREATE TABLE IF NOT EXISTS BON1(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_CLIENT VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
                "DATE_F VARCHAR, " +
                "HEURE_F VARCHAR, " +
                "NBR_P INTEGER, " +
                "TOT_QTE DOUBLE, " +
                "MODE_TARIF VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "MODE_RG VARCHAR, " +
                "CODE_VENDEUR VARCHAR, " +
                "TOT_HT DOUBLE, " +
                "TOT_TVA DOUBLE, " +
                "TIMBRE DOUBLE, " +
                "TIMBRE_CHECK VARCHAR, " +
                "LATITUDE REAL DEFAULT 0.00, " +
                "LONGITUDE REAL DEFAULT 0.00, " +
                "REMISE DOUBLE, " +
                "MONTANT_ACHAT DOUBLE, " +
                "ANCIEN_SOLDE DOUBLE, " +
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "VERSER DOUBLE, "+
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS BON2(RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_BARRE VARCHAR, " +
                "NUM_BON VARCHAR , " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE_GRAT DOUBLE , " +
                "QTE DOUBLE , " +
                "PV_HT DOUBLE, " +
                "PA_HT DOUBLE, " +
                "DESTOCK_TYPE VARCHAR, " +
                "DESTOCK_CODE_BARRE VARCHAR, " +
                "DESTOCK_QTE DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR )");


        db.execSQL("CREATE TABLE IF NOT EXISTS TRANSFERT1(NUM_BON VARCHAR PRIMARY KEY, " +
                "DATE_BON VARCHAR, " +
                "CODE_DEPOT_SOURCE VARCHAR, " +
                "NOM_DEPOT_SOURCE VARCHAR, " +
                "CODE_DEPOT_DEST VARCHAR, " +
                "NOM_DEPOT_DEST VARCHAR, " +
                "NBR_P VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS TRANSFERT2(TRANSFER2_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_BON VARCHAR, " +
                "CODE_BARRE VARCHAR ," +
                "PRODUIT VARCHAR," +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE DOUBLE)");


        db.execSQL("CREATE TABLE IF NOT EXISTS CLIENT(" +
                "CLIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_CLIENT VARCHAR, " +
                "CLIENT VARCHAR , " +
                "TEL VARCHAR, " +
                "ADRESSE VARCHAR, " +
                "WILAYA VARCHAR, " +
                "COMMUNE VARCHAR, " +
                "MODE_TARIF VARCHAR, " +
                "LATITUDE REAL DEFAULT 0.0, " +
                "LONGITUDE REAL DEFAULT 0.0, " +
                "ACHATS DOUBLE DEFAULT 0.0, " +
                "VERSER DOUBLE DEFAULT 0.0, " +
                "SOLDE DOUBLE DEFAULT 0.0, " +
                "RC VARCHAR, " +
                "IFISCAL VARCHAR, " +
                "AI VARCHAR,  " +
                "NIS VARCHAR, " +
                "ISNEW INTEGER, " +
                "CREDIT_LIMIT DOUBLE DEFAULT 0.0, " +
                "SOLDE_INI DOUBLE DEFAULT 0.0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS BON1_TEMP(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_CLIENT VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
                "DATE_F VARCHAR, " +
                "HEURE_F VARCHAR, " +
                "NBR_P INTEGER, " +
                "TOT_QTE DOUBLE, " +
                "MODE_TARIF VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "MODE_RG VARCHAR, " +
                "CODE_VENDEUR VARCHAR, " +
                "TOT_HT DOUBLE, " +
                "TOT_TVA DOUBLE, " +
                "TIMBRE DOUBLE, " +
                "TIMBRE_CHECK VARCHAR, " +
                "LATITUDE REAL DEFAULT 0.00, " +
                "LONGITUDE REAL DEFAULT 0.00, " +
                "REMISE DOUBLE, " +
                "MONTANT_ACHAT DOUBLE, " +
                "ANCIEN_SOLDE DOUBLE, " +
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "VERSER DOUBLE, "+
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS BON2_TEMP(RECORDID INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "CODE_BARRE VARCHAR, " +
                "NUM_BON VARCHAR , " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE_GRAT DOUBLE , " +
                "QTE DOUBLE , " +
                "PV_HT DOUBLE, " +
                "PA_HT DOUBLE, " +
                "DESTOCK_TYPE VARCHAR, " +
                "DESTOCK_CODE_BARRE VARCHAR, " +
                "DESTOCK_QTE DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS CARNET_C(RECORDID INTEGER PRIMARY KEY, " +
                "CODE_CLIENT VARCHAR, " +
                "DATE_CARNET VARCHAR , " +
                "HEURE VARCHAR , " +
                "ACHATS DOUBLE , " +
                "VERSEMENTS DOUBLE , " +
                "SOURCE VARCHAR , " +
                "NUM_BON VARCHAR , " +
                "MODE_RG VARCHAR, " +
                "REMARQUES VARCHAR, " +
                "EXPORTATION VARCHAR, " +
                "UTILISATEUR VARCHAR, " +
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS INV1(NUM_INV VARCHAR PRIMARY KEY, DATE_INV VARCHAR, HEURE_INV VARCHAR, LIBELLE VARCHAR, NBR_PRODUIT VARCHAR, UTILISATEUR VARCHAR, CODE_DEPOT VARCHAR, IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)), DATE_EXPORT_INV VARCHAR, BLOCAGE VARCHAR, EXPORTATION VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS INV2(RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR , NUM_INV VARCHAR, PRODUIT VARCHAR, NBRE_COLIS DOUBLE, COLISSAGE DOUBLE, PA_HT DOUBLE, QTE DOUBLE, QTE_TMP DOUBLE, QTE_NEW DOUBLE, TVA DOUBLE, VRAC VARCHAR, CODE_DEPOT VARCHAR )");

       // db.execSQL("CREATE TABLE IF NOT EXISTS Achats1(ACHAT1ID INTEGER, NUM_ACHAT VARCHAR PRIMARY KEY, NOM_ACHAT VARCHAR , DATE_ACHAT VARCHAR, HEURE_ACHAT VARCHAR, UTILISATEUR VARCHAR, CODE_DEPOT VARCHAR, IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)), DATE_EXPORT_ACHAT VARCHAR)");
      //  db.execSQL("CREATE TABLE IF NOT EXISTS Achats2(ACHAT2ID VARCHAR PRIMARY KEY, CODE_BARRE VARCHAR, REF_PRODUIT VARCHAR, NUM_ACHAT VARCHAR, PRODUIT VARCHAR, PA_HT DOUBLE, QTE DOUBLE, TVA DOUBLE, CODE_DEPOT VARCHAR )");
        db.execSQL("CREATE TABLE IF NOT EXISTS Position(POSITION_ID INTEGER PRIMARY KEY AUTOINCREMENT, LAT DOUBLE, LONGI DOUBLE, ADRESS VARCHAR, COLOR boolean CHECK (COLOR IN (0,1)), CLIENT VARCHAR, NUM_BON VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS FAMILLES(FAMILLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, FAMILLE VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS FOURNIS(FOURNIS_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_FRS VARCHAR, FOURNIS VARCHAR, ADRESSE VARCHAR, TEL VARCHAR, ACHATS DOUBLE DEFAULT 0.0, VERSER DOUBLE DEFAULT 0.0, SOLDE DOUBLE DEFAULT 0.0, LATITUDE REAL DEFAULT 0.0, LONGITUDE REAL DEFAULT 0.0, RC VARCHAR, IFISCAL VARCHAR, AI VARCHAR,  NIS VARCHAR, ISNEW INTEGER)");


        db.execSQL("CREATE TABLE IF NOT EXISTS ACHAT1(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_FRS VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
                "NBR_P INTEGER, " +
                "MODE_RG VARCHAR, " +
                "TOT_QTE DOUBLE, " +
                "TOT_HT DOUBLE, " +
                "TOT_TVA DOUBLE, " +
                "TIMBRE DOUBLE, " +
                "TOT_TTC DOUBLE, " +
                "REMISE DOUBLE, " +
                "ANCIEN_SOLDE DOUBLE, " +
                "VERSER DOUBLE, "+
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS ACHAT2(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_BON VARCHAR , " +
                "CODE_BARRE VARCHAR, " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE DOUBLE, " +
                "QTE_GRAT DOUBLE, " +
                "PA_HT DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS ACHAT1_TEMP(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_FRS VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
                "NBR_P INTEGER, " +
                "MODE_RG VARCHAR, " +
                "TOT_QTE DOUBLE, " +
                "TOT_HT DOUBLE, " +
                "TOT_TVA DOUBLE, " +
                "TIMBRE DOUBLE, " +
                "TOT_TTC DOUBLE, " +
                "REMISE DOUBLE, " +
                "ANCIEN_SOLDE DOUBLE, " +
                "VERSER DOUBLE, "+
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS ACHAT2_TEMP(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_BON VARCHAR , " +
                "CODE_BARRE VARCHAR, " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE DOUBLE, " +
                "QTE_GRAT DOUBLE, " +
                "PA_HT DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS ROUTING(RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_CLIENT VARCHAR UNIQUE, CLIENT VARCHAR , TEL VARCHAR, ADRESSE VARCHAR, LATITUDE REAL, LONGITUDE REAL, STATE INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS PARAMS(" +
                "RECORDID INTEGER PRIMARY KEY, " +
                "PV1_TITRE VARCHAR, " +
                "PRIX_2 INTEGER , " +
                "PV2_TITRE VARCHAR, " +
                "PRIX_3 INTEGER , " +
                "PV3_TITRE VARCHAR, " +
                "PRIX_4 INTEGER , " +
                "PV4_TITRE VARCHAR, " +
                "PRIX_5 INTEGER , " +
                "PV5_TITRE VARCHAR, " +
                "PRIX_6 INTEGER , " +
                "PV6_TITRE VARCHAR , " +
                "FTP_SERVER VARCHAR , " +
                "FTP_PORT VARCHAR , " +
                "FTP_USER VARCHAR , " +
                "FTP_PASS VARCHAR , " +
                "FTP_IMP VARCHAR , " +
                "FTP_EXP VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS WILAYAS (ID integer not null primary key, NAME varchar not null, LATITUDE numeric not null, LONGITUDE numeric not null )");

        db.execSQL("CREATE TABLE IF NOT EXISTS COMMUNES  (ID integer not null primary key, NAME varchar not null, POST_CODE varchar not null, WILAYA_ID integer not null, LATITUDE numeric not null, LONGITUDE numeric not null, foreign key(WILAYA_ID) references WILAYAS(ID) on delete cascade)");
        insert_wilaya_commune_into_database(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        Log.v("TRACKKK","================>  ON UPGRADE EXECUTED");

        String[] list_requet = new String[3];

         list_requet[0] = "ALTER TABLE CLIENT ADD COLUMN SOLDE_INI DOUBLE DEFAULT 0";
         list_requet[1] = "ALTER TABLE CLIENT ADD COLUMN WILAYA VARCHAR";
         list_requet[2] = "ALTER TABLE CLIENT ADD COLUMN COMMUNE VARCHAR";


        for (String s : list_requet) {
            try {
                db.execSQL(s);
            } catch (SQLiteException e) {
                Log.v("TRACKKK", e.getMessage());
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////
    //////                             Functions                             //////
    ///////////////////////////////////////////////////////////////////////////////

    //============================== FUNCTION UPDATE table produit =================================
    public boolean ExecuteTransactionTrasfer(ArrayList<PostData_Transfer1> transfer1s, ArrayList<PostData_Transfer2> transfer2s){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                for(int i = 0;i< transfer1s.size(); i++){
                    insert_into_trasfer1(db,transfer1s.get(i));
                }

                for(int j = 0;j< transfer2s.size(); j++){
                    insert_into_trasfer2(db,transfer2s.get(j));
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    //============================== FUNCTION table fournisseur =================================
    public boolean ExecuteTransactionFournisseur(ArrayList<PostData_Fournisseur> fournisseurs){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("FOURNIS", null , null);

                for(int i = 0;i< fournisseurs.size(); i++){
                    insert_into_fournisseur(fournisseurs.get(i));
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public void insert_into_params(PostData_Params params){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            db.delete("PARAMS", null, null);

            try {

                ContentValues values = new ContentValues();
                values.put("PV1_TITRE", params.pv1_titre);
                values.put("PRIX_2", params.prix_2);
                values.put("PV2_TITRE", params.pv2_titre);
                values.put("PRIX_3", params.prix_3);
                values.put("PV3_TITRE", params.pv3_titre);
                values.put("PRIX_4", params.prix_4);
                values.put("PV4_TITRE", params.pv4_titre);
                values.put("PRIX_5", params.prix_5);
                values.put("PV5_TITRE", params.pv5_titre);
                values.put("PRIX_6", params.prix_6);
                values.put("PV6_TITRE", params.pv6_titre);
                values.put("FTP_SERVER", params.ftp_server);
                values.put("FTP_PORT", params.ftp_port);
                values.put("FTP_USER", params.ftp_user);
                values.put("FTP_PASS", params.ftp_pass);
                values.put("FTP_IMP", params.ftp_imp);
                values.put("FTP_EXP", params.ftp_exp);

                db.insert("PARAMS", null, values);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }


    public void update_ftp_params(PostData_Params params){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                ContentValues values = new ContentValues();
                values.put("RECORDID", 1);
                values.put("FTP_SERVER", params.ftp_server);
                values.put("FTP_PORT", params.ftp_port);
                values.put("FTP_USER", params.ftp_user);
                values.put("FTP_PASS", params.ftp_pass);
                values.put("FTP_IMP", params.ftp_imp);
                values.put("FTP_EXP", params.ftp_exp);

                int id = (int) db.insertWithOnConflict("PARAMS", null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    db.update("PARAMS", values,null, null);
                }



                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }


    public boolean insert_into_fournisseur(PostData_Fournisseur fournisseur){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("CODE_FRS", fournisseur.code_frs);
                values.put("FOURNIS", fournisseur.fournis);
                values.put("ADRESSE", fournisseur.adresse);
                values.put("TEL", fournisseur.tel);
                values.put("RC", fournisseur.rc);
                values.put("IFISCAL", fournisseur.ifiscal);
                values.put("AI", fournisseur.ai);
                values.put("NIS", fournisseur.nis);
                values.put("ACHATS", fournisseur.achat_montant);
                values.put("VERSER", fournisseur.verser_montant);
                values.put("SOLDE", fournisseur.solde_montant);

                values.put("ISNEW", fournisseur.isNew);
                db.insert("FOURNIS", null, values);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    //============================== FUNCTION table client =================================
    public boolean ExecuteTransactionClient(ArrayList<PostData_Client> clients){
        boolean executed = false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                db.delete("CLIENT", null , null);

                for(int i = 0;i< clients.size(); i++){

                    insert_into_client(clients.get(i));
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Client TABLE ===============================
    public boolean insert_into_client(PostData_Client client){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("CODE_CLIENT", client.code_client);
                values.put("CLIENT", client.client);
                values.put("WILAYA", client.wilaya);
                values.put("COMMUNE", client.commune);
                values.put("TEL", client.tel);
                values.put("ADRESSE", client.adresse);
                values.put("RC", client.rc);
                values.put("IFISCAL", client.ifiscal);
                values.put("NIS", client.nis);
                values.put("AI", client.ai);
                values.put("MODE_TARIF", client.mode_tarif);
                values.put("ACHATS", client.achat_montant);
                values.put("VERSER", client.verser_montant);
                values.put("SOLDE", client.solde_montant);
                values.put("SOLDE_INI", client.solde_ini);
                values.put("LATITUDE", client.latitude);
                values.put("LONGITUDE", client.longitude);
                values.put("CREDIT_LIMIT", client.credit_limit);
                values.put("ISNEW", client.isNew);

                db.insert("CLIENT", null, values);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public void ExecuteTransactionInsertIntoRouting(ArrayList<PostData_Client> clients){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("ROUTING", null , null);
                for(int i = 0;i < clients.size(); i++){
                    insert_into_routing(clients.get(i));
                }
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }

    public boolean insert_into_routing(PostData_Client client){
        long index = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("CODE_CLIENT", client.code_client);
                values.put("CLIENT", client.client);
                values.put("TEL", client.tel);
                values.put("ADRESSE", client.adresse);
                values.put("LATITUDE", client.latitude);
                values.put("LONGITUDE", client.longitude);
                values.put("STATE", client.state);

                index = db.insert("ROUTING", null, values);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        db.close();
        return index != -1;
    }

    //=============================== FUNCTION TO INSERT INTO Produits TABLE ===============================
    public boolean insert_into_produit(PostData_Produit produit){

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("CODE_BARRE", produit.code_barre);
                values.put("REF_PRODUIT", produit.ref_produit);
                values.put("PRODUIT", produit.produit);
                values.put("PA_HT", produit.pa_ht);
                values.put("TVA", produit.tva);
                values.put("PAMP", produit.pamp);
                values.put("PV1_HT", produit.pv1_ht);
                values.put("PV2_HT", produit.pv2_ht);
                values.put("PV3_HT", produit.pv3_ht);
                values.put("PV4_HT", produit.pv4_ht);
                values.put("PV5_HT", produit.pv5_ht);
                values.put("PV6_HT", produit.pv6_ht);
                values.put("STOCK", produit.stock);
                values.put("COLISSAGE", produit.colissage);
                values.put("PHOTO", produit.photo);
                values.put("DETAILLE", produit.description);
                values.put("FAMILLE", produit.famille);
                values.put("DESTOCK_TYPE", produit.destock_type);
                values.put("DESTOCK_CODE_BARRE", produit.destock_code_barre);
                values.put("DESTOCK_QTE", produit.destock_qte);

                values.put("PROMO", produit.promo);
                values.put("D1", produit.d1);
                values.put("D2", produit.d2);
                values.put("PP1_HT", produit.pp1_ht);

                values.put("ISNEW", produit.isNew);

                db.insert("PRODUIT", null, values);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public boolean update_into_produit(PostData_Produit produit){

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("CODE_BARRE", produit.code_barre);
                values.put("REF_PRODUIT", produit.ref_produit);
                values.put("PRODUIT", produit.produit);
                values.put("PA_HT", produit.pa_ht);
                values.put("TVA", produit.tva);
                values.put("PAMP", produit.pamp);
                values.put("PV1_HT", produit.pv1_ht);
                values.put("PV2_HT", produit.pv2_ht);
                values.put("PV3_HT", produit.pv3_ht);
                values.put("PV4_HT", produit.pv4_ht);
                values.put("PV5_HT", produit.pv5_ht);
                values.put("PV6_HT", produit.pv6_ht);
                values.put("STOCK", produit.stock);
                values.put("COLISSAGE", produit.colissage);
                values.put("PHOTO", produit.photo);
                values.put("DETAILLE", produit.description);
                values.put("FAMILLE", produit.famille);
                values.put("DESTOCK_TYPE", produit.destock_type);
                values.put("DESTOCK_CODE_BARRE", produit.destock_code_barre);
                values.put("DESTOCK_QTE", produit.destock_qte);

                values.put("PROMO", produit.promo);
                values.put("D1", produit.d1);
                values.put("D2", produit.d2);
                values.put("PP1_HT", produit.pp1_ht);

                values.put("ISNEW", produit.isNew);

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {produit.code_barre};
                db.update("PRODUIT",values,  selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }
    //============================== FUNCTION table famille =================================

    public void ExecuteTransactionFamille(ArrayList<PostData_Famille> familles){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete("FAMILLES", null , null);
                for(int i = 0;i< familles.size(); i++){

                    ContentValues values = new ContentValues();
                    values.put("FAMILLE", familles.get(i).famille);

                    db.insert("FAMILLES", null, values);
                }
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    //=============================== table produit ================================================
    public boolean ExecuteTransactionProduit(ArrayList<PostData_Produit> produits, ArrayList<PostData_Codebarre> codebarres){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("PRODUIT", null , null);

                for(int i = 0;i< produits.size(); i++){

                    insert_into_produit(produits.get(i));
                }

                db.delete("CODEBARRE", null , null);

                for(int h=0; h < codebarres.size(); h++){

                    ContentValues values = new ContentValues();
                    values.put("CODE_BARRE", codebarres.get(h).code_barre);
                    values.put("CODE_BARRE_SYN", codebarres.get(h).code_barre_syn);
                    db.insert("CODEBARRE", null, values);

                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //=============================== FUNCTION TO INSERT INTO Transfer1 TABLE ===============================
    public void insert_into_trasfer1(SQLiteDatabase db, PostData_Transfer1 transfer1){
        if(db == null)
            db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NUM_BON", transfer1.num_bon);
        values.put("DATE_BON", transfer1.date_bon);
        values.put("CODE_DEPOT_SOURCE", transfer1.code_depot_s);
        values.put("NOM_DEPOT_SOURCE", transfer1.nom_depot_s);
        values.put("CODE_DEPOT_DEST", transfer1.code_depot_d);
        values.put("NOM_DEPOT_DEST", transfer1.nom_depot_d);
        values.put("NBR_P", transfer1.nbr_p);
        db.insert("TRANSFERT1", null, values);
    }

    //=============================== FUNCTION TO INSERT INTO TRANSFERT2 TABLE ===============================
    public void insert_into_trasfer2(SQLiteDatabase db, PostData_Transfer2 transfer2){
        if(db == null)
            db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NUM_BON", transfer2.num_bon);
        values.put("CODE_BARRE", transfer2.code_barre);
        values.put("PRODUIT", transfer2.produit);
        values.put("QTE", transfer2.qte);
        values.put("NBRE_COLIS", transfer2.nbr_colis);
        values.put("COLISSAGE", transfer2.colissage);
        db.insert("TRANSFERT2", null, values);
    }



    //=============================== FUNCTION TO INSERT INTO Codebarre TABLE ===============================
    public void insert_into_codebarre(PostData_Codebarre codebarre){

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("CODE_BARRE", codebarre.code_barre);
            values.put("CODE_BARRE_SYN", codebarre.code_barre_syn);
            db.insert("CODEBARRE", null, values);
    }


    //=============================== FUNCTION TO INSERT INTO Carnet_c TABLE ===============================
    @SuppressLint("Range")
    public boolean insert_into_carnet_c(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("CODE_CLIENT", carnet_c.code_client);
                values.put("DATE_CARNET", carnet_c.carnet_date);
                values.put("HEURE", carnet_c.carnet_heure);
                values.put("ACHATS", carnet_c.carnet_achats);
                values.put("VERSEMENTS", carnet_c.carnet_versement);
                values.put("SOURCE", carnet_c.carnet_source);
                values.put("NUM_BON", carnet_c.carnet_num_bon);
                values.put("MODE_RG", carnet_c.carnet_mode_rg);
                values.put("REMARQUES", carnet_c.carnet_remarque);
                values.put("UTILISATEUR", carnet_c.carnet_utilisateur);
                values.put("IS_EXPORTED", 0);
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"");


                db.insert("Carnet_c", null, values);


                //update_client

                ContentValues args = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args.put("SOLDE",  String.valueOf(val_nouveau_solde_client));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {carnet_c.code_client};
                db.update("CLIENT", args, selection, selectionArgs);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //============================== FUNCTION SELECT FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public PostData_Produit check_product_if_exist(String querry){
        SQLiteDatabase db = this.getWritableDatabase();

        PostData_Produit produit = new PostData_Produit();
        produit.exist = false;
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                produit.exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produit;
    }


    public boolean check_client_if_exist(String  code_client){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;

        String querry  = "SELECT * FROM CLIENT WHERE CODE_CLIENT = '" + code_client + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exist;
    }


    public boolean check_transfer1_if_exist(String  num_bon){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;

        String querry  = "SELECT * FROM TRANSFERT1 WHERE NUM_BON = '" + num_bon + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return exist;
    }


    //================================== UPDATE TABLE (Inventaires1) =======================================
    public void update_produit(PostData_Produit produit_to_update, String code_barre){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("PRODUIT", produit_to_update.produit);
                args.put("STOCK", produit_to_update.stock);
                args.put("PA_HT", produit_to_update.pa_ht);
                args.put("TVA", produit_to_update.tva);
                args.put("PAMP", produit_to_update.pamp);
                args.put("PV1_HT", produit_to_update.pv1_ht);
                args.put("PV2_HT", produit_to_update.pv2_ht);
                args.put("PV3_HT", produit_to_update.pv3_ht);
                args.put("PV4_HT", produit_to_update.pv4_ht);
                args.put("PV5_HT", produit_to_update.pv5_ht);
                args.put("PV6_HT", produit_to_update.pv6_ht);

                args.put("PHOTO", produit_to_update.photo);

                args.put("PROMO", produit_to_update.promo);
                args.put("D1", produit_to_update.d1);
                args.put("D2", produit_to_update.d2);
                args.put("PP1_HT", produit_to_update.pp1_ht);

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {code_barre};
                db.update("PRODUIT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    public void update_produit_after_export(String code_barre){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                args.put("ISNEW", 0);
                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {code_barre};
                db.update("PRODUIT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    //================================== UPDATE TABLE (Carnet_c) =======================================
    @SuppressLint("Range")
    public boolean update_versement(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("VERSEMENTS",  carnet_c.carnet_versement);
                args.put("REMARQUES", carnet_c.carnet_remarque);
                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_c.recordid};
                db.update("CARNET_C", args, selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE",  String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client};
                db.update("CLIENT", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    ////////////////////////// DELETE Situation ///////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_versement(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_c.recordid};
                db.delete("Carnet_c", selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE",  String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client};
                db.update("CLIENT", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //================================== UPDATE TABLE (Inventaires1) =======================================
    public void update_versement_exported(String recordid){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("IS_EXPORTED",  1);
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid};
                db.update("Carnet_c", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    //================================== UPDATE TABLE (Inventaires1) =======================================
    public void delete_Codebarre(String code_barre){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {code_barre};
                db.delete("CODEBARRE", selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    @SuppressLint("Range")
    public PostData_Params select_params_from_database(String querry){
        PostData_Params params = new PostData_Params();;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                params.pv1_titre = cursor.getString(cursor.getColumnIndex("PV1_TITRE"));
                params.prix_2 = cursor.getInt(cursor.getColumnIndex("PRIX_2"));
                params.pv2_titre = cursor.getString(cursor.getColumnIndex("PV2_TITRE"));
                params.prix_3 = cursor.getInt(cursor.getColumnIndex("PRIX_3"));
                params.pv3_titre = cursor.getString(cursor.getColumnIndex("PV3_TITRE"));
                params.prix_4 = cursor.getInt(cursor.getColumnIndex("PRIX_4"));
                params.pv4_titre = cursor.getString(cursor.getColumnIndex("PV4_TITRE"));
                params.prix_5 = cursor.getInt(cursor.getColumnIndex("PRIX_5"));
                params.pv5_titre = cursor.getString(cursor.getColumnIndex("PV5_TITRE"));
                params.prix_6 = cursor.getInt(cursor.getColumnIndex("PRIX_6"));
                params.pv6_titre = cursor.getString(cursor.getColumnIndex("PV6_TITRE"));
                params.ftp_server = cursor.getString(cursor.getColumnIndex("FTP_SERVER"));
                params.ftp_port = cursor.getString(cursor.getColumnIndex("FTP_PORT"));
                params.ftp_user = cursor.getString(cursor.getColumnIndex("FTP_USER"));
                params.ftp_pass = cursor.getString(cursor.getColumnIndex("FTP_PASS"));
                params.ftp_imp = cursor.getString(cursor.getColumnIndex("FTP_IMP"));
                params.ftp_exp = cursor.getString(cursor.getColumnIndex("FTP_EXP"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return params;
    }


    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Transfer1> select_transfer1_from_database(String querry){
        ArrayList<PostData_Transfer1> transfer1s = new ArrayList<PostData_Transfer1>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Transfer1 transfer1 = new PostData_Transfer1();

                transfer1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                transfer1.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                transfer1.code_depot_s = cursor.getString(cursor.getColumnIndex("CODE_DEPOT_SOURCE"));
                transfer1.nom_depot_s = cursor.getString(cursor.getColumnIndex("NOM_DEPOT_SOURCE"));
                transfer1.code_depot_d = cursor.getString(cursor.getColumnIndex("CODE_DEPOT_DEST"));
                transfer1.nom_depot_d = cursor.getString(cursor.getColumnIndex("NOM_DEPOT_DEST"));
                transfer1.nbr_p = cursor.getString(cursor.getColumnIndex("NBR_P"));

                transfer1s.add(transfer1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transfer1s;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_Position> select_position_from_database(String querry){
        ArrayList<PostData_Position> transfer1s = new ArrayList<PostData_Position>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Position transfer1 = new PostData_Position();

                transfer1.lat = cursor.getDouble(cursor.getColumnIndex("LAT"));
                transfer1.longi = cursor.getDouble(cursor.getColumnIndex("LONGI"));
                transfer1.adresse = cursor.getString(cursor.getColumnIndex("ADRESS"));
                transfer1.color = cursor.getInt(cursor.getColumnIndex("COLOR"));
                transfer1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                transfer1.client = cursor.getString(cursor.getColumnIndex("CLIENT"));

                transfer1s.add(transfer1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transfer1s;
    }


    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Transfer2> select_transfer2_from_database(String querry){
        ArrayList<PostData_Transfer2> transfer2s = new ArrayList<PostData_Transfer2>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Transfer2 transfer2 = new PostData_Transfer2();

                transfer2.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                transfer2.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                transfer2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                transfer2.qte = cursor.getDouble(cursor.getColumnIndex("QTE"));

                transfer2s.add(transfer2);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transfer2s;
    }


    //============================== FUNCTION SELECT Fournisseurs FROM FOURNIS TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Fournisseur> select_fournisseurs_from_database(String querry){
        ArrayList<PostData_Fournisseur> fournisseurs = new ArrayList<PostData_Fournisseur>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Fournisseur fournisseur = new PostData_Fournisseur();

                fournisseur.fournis_id = cursor.getInt(cursor.getColumnIndex("FOURNIS_ID"));
                fournisseur.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                fournisseur.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                fournisseur.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                fournisseur.rc = cursor.getString(cursor.getColumnIndex("RC"));
                fournisseur.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                fournisseur.ai = cursor.getString(cursor.getColumnIndex("AI"));
                fournisseur.nis = cursor.getString(cursor.getColumnIndex("NIS"));

                fournisseur.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                fournisseur.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                fournisseur.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));

                fournisseur.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                fournisseurs.add(fournisseur);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fournisseurs;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Client> select_clients_from_database(String querry){
        ArrayList<PostData_Client> clients = new ArrayList<PostData_Client>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Client client = new PostData_Client();

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                client.wilaya = cursor.getString(cursor.getColumnIndex("WILAYA"));
                client.commune = cursor.getString(cursor.getColumnIndex("COMMUNE"));
                client.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                client.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                client.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                client.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                client.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                client.solde_ini = cursor.getDouble(cursor.getColumnIndex("SOLDE_INI"));
                client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                client.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                client.rc = cursor.getString(cursor.getColumnIndex("RC"));
                client.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                client.ai = cursor.getString(cursor.getColumnIndex("AI"));
                client.nis = cursor.getString(cursor.getColumnIndex("NIS"));

                clients.add(client);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clients;
    }


    @SuppressLint("Range")
    public ArrayList<PostData_wilaya> select_wilayas_from_database(String querry){
        ArrayList<PostData_wilaya> wilayas = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list

        PostData_wilaya wilaya_aucune = new PostData_wilaya();
        wilaya_aucune.id = 0;
        wilaya_aucune.wilaya = "<Aucune>";
        wilaya_aucune.latitude = 0;
        wilaya_aucune.longitude = 0;
        wilayas.add(wilaya_aucune);

        if (cursor.moveToFirst()) {
            do {
                PostData_wilaya wilaya = new PostData_wilaya();

                wilaya.id = cursor.getInt(cursor.getColumnIndex("ID"));
                wilaya.wilaya = cursor.getString(cursor.getColumnIndex("NAME"));
                wilaya.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                wilaya.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));

                wilayas.add(wilaya);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return wilayas;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_commune> select_communes_from_database(String querry){
        ArrayList<PostData_commune> communes = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list

        PostData_commune commune_aucune = new PostData_commune();
        commune_aucune.id = 0;
        commune_aucune.commune = "<Aucune>";
        commune_aucune.post_code = 0;
        commune_aucune.wilaya_id = 0;
        commune_aucune.latitude = 0;
        commune_aucune.longitude = 0;
        communes.add(commune_aucune);

        if (cursor.moveToFirst()) {
            do {
                PostData_commune commune = new PostData_commune();

                commune.id = cursor.getInt(cursor.getColumnIndex("ID"));
                commune.commune = cursor.getString(cursor.getColumnIndex("NAME"));
                commune.post_code = cursor.getInt(cursor.getColumnIndex("POST_CODE"));
                commune.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                commune.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                commune.wilaya_id = cursor.getInt(cursor.getColumnIndex("WILAYA_ID"));

                communes.add(commune);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return communes;
    }
    @SuppressLint("Range")
    public ArrayList<PostData_Client> select_routing_from_database(String querry){
        ArrayList<PostData_Client> clients = new ArrayList<PostData_Client>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Client client = new PostData_Client();

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                client.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                client.state = cursor.getInt(cursor.getColumnIndex("STATE"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));

                clients.add(client);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clients;
    }

    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Carnet_c> select_carnet_c_from_database(String querry){
        ArrayList<PostData_Carnet_c> carnet_cs = new ArrayList<PostData_Carnet_c>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Carnet_c carnet_c = new PostData_Carnet_c();

                carnet_c.recordid = cursor.getString(cursor.getColumnIndex("RECORDID"));
                carnet_c.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                carnet_c.carnet_date = cursor.getString(cursor.getColumnIndex("DATE_CARNET"));
                carnet_c.carnet_heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                carnet_c.carnet_achats = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                carnet_c.carnet_versement = cursor.getDouble(cursor.getColumnIndex("VERSEMENTS"));
                carnet_c.carnet_source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                carnet_c.carnet_num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                carnet_c.carnet_mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                carnet_c.carnet_remarque = cursor.getString(cursor.getColumnIndex("REMARQUES"));
                carnet_c.carnet_utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                carnet_c.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                carnet_c.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));

                carnet_c.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                carnet_c.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                carnet_c.wilaya = cursor.getString(cursor.getColumnIndex("WILAYA"));
                carnet_c.commune = cursor.getString(cursor.getColumnIndex("COMMUNE"));
                carnet_c.tel = cursor.getString(cursor.getColumnIndex("TEL"));

                carnet_c.rc = cursor.getString(cursor.getColumnIndex("RC"));
                carnet_c.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                carnet_c.ai = cursor.getString(cursor.getColumnIndex("AI"));
                carnet_c.nis = cursor.getString(cursor.getColumnIndex("NIS"));
                carnet_c.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));

                carnet_c.latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
                carnet_c.longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));



                carnet_cs.add(carnet_c);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return carnet_cs;
    }

    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Carnet_c select_carnet_c_from_database_single(String querry){
        PostData_Carnet_c carnet_c = new PostData_Carnet_c();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                carnet_c.recordid = cursor.getString(cursor.getColumnIndex("RECORDID"));
                carnet_c.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                carnet_c.carnet_date = cursor.getString(cursor.getColumnIndex("DATE_CARNET"));
                carnet_c.carnet_heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                carnet_c.carnet_achats = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                carnet_c.carnet_versement = cursor.getDouble(cursor.getColumnIndex("VERSEMENTS"));
                carnet_c.carnet_source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                carnet_c.carnet_num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                carnet_c.carnet_mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                carnet_c.carnet_remarque = cursor.getString(cursor.getColumnIndex("REMARQUES"));
                carnet_c.carnet_utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                carnet_c.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));

                //CLIENT
                carnet_c.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                carnet_c.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                carnet_c.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                carnet_c.rc = cursor.getString(cursor.getColumnIndex("RC"));
                carnet_c.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                carnet_c.ai = cursor.getString(cursor.getColumnIndex("AI"));
                carnet_c.nis = cursor.getString(cursor.getColumnIndex("NIS"));
                carnet_c.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                carnet_c.latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
                carnet_c.longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return carnet_c;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Fournisseur select_fournisseur_from_database(String code_frs){

        PostData_Fournisseur fournisseur = new PostData_Fournisseur();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FOURNIS WHERE CODE_FRS = '"+code_frs+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                fournisseur.fournis_id = cursor.getInt(cursor.getColumnIndex("FOURNIS_ID"));
                fournisseur.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                fournisseur.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                fournisseur.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                fournisseur.rc = cursor.getString(cursor.getColumnIndex("RC"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("AI"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("NIS"));
                fournisseur.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                fournisseur.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                fournisseur.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));

                fournisseur.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return fournisseur;
    }


    @SuppressLint("Range")
    public double select_last_price_from_database(String table, String code_client, String codebarre){

        double last_price = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query_bon1 = "SELECT" +
                "    BON1.DATE_BON," +
                "    BON2.RECORDID," +
                "    BON2.NUM_BON," +
                "    ' VENTE' AS OPERATION," +
                "    coalesce(BON2.QTE,0) QTE, " +
                "    coalesce(BON2.QTE_GRAT,0) QTE_GRAT," +
                "    coalesce(BON2.PV_HT,0) PV_HT ," +
                "    coalesce(BON2.TVA,0) TVA ," +
                "    BON2.PV_HT * (1+(coalesce(BON2.TVA,0)/100)) PU_TTC " +
                //"    BON2.pa_ht * (1+(coalesce(BON2.TVA,0)/100)) PA_TTC " +
                "FROM BON2 LEFT JOIN BON1 ON (BON1.NUM_BON = BON2.NUM_BON) " +
                "WHERE BON1.CODE_CLIENT= '" + code_client + "' AND BON2.CODE_BARRE= '" + codebarre + "' order by 1 desc,2 desc,4 desc";


        String query_bon1_temp = "SELECT" +
                "    BON1_TEMP.DATE_BON," +
                "    BON2_TEMP.RECORDID," +
                "    BON2_TEMP.NUM_BON," +
                "    ' VENTE' AS OPERATION," +
                "    coalesce(BON2_TEMP.QTE,0) QTE, " +
                "    coalesce(BON2_TEMP.QTE_GRAT,0) QTE_GRAT," +
                "    coalesce(BON2_TEMP.PV_HT,0) PV_HT ," +
                "    coalesce(BON2_TEMP.TVA,0) TVA ," +
                "    BON2_TEMP.PV_HT * (1+(coalesce(BON2_TEMP.TVA,0)/100)) PU_TTC " +
                //"    BON2.pa_ht * (1+(coalesce(BON2.TVA,0)/100)) PA_TTC " +
                "FROM BON2_TEMP LEFT JOIN BON1_TEMP ON (BON1_TEMP.NUM_BON = BON2_TEMP.NUM_BON) " +
                "WHERE BON1_TEMP.CODE_CLIENT= '" + code_client + "' AND BON2_TEMP.CODE_BARRE= '" + codebarre + "' order by 1 desc,2 desc,4 desc";
        Cursor cursor = null;
        if(table.equals("BON1")){
            cursor = db.rawQuery(query_bon1, null);
        }else if(table.equals("BON1_TEMP")){
            cursor = db.rawQuery(query_bon1_temp, null);
        }
        // looping through all rows and adding to list
        assert cursor != null;
        if (cursor.moveToFirst()) {
            last_price = cursor.getDouble(cursor.getColumnIndex("PU_TTC"));
        }
        cursor.close();
        return last_price;
    }

    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Client select_client_from_database(String code_client){

        PostData_Client client = new PostData_Client();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CLIENT WHERE CODE_CLIENT = '"+code_client+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                client.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                client.wilaya = cursor.getString(cursor.getColumnIndex("WILAYA"));
                client.commune = cursor.getString(cursor.getColumnIndex("COMMUNE"));
                client.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                client.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                client.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                client.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                client.solde_ini = cursor.getDouble(cursor.getColumnIndex("SOLDE_INI"));
                client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                client.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                client.rc = cursor.getString(cursor.getColumnIndex("RC"));
                client.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                client.ai = cursor.getString(cursor.getColumnIndex("AI"));
                client.nis = cursor.getString(cursor.getColumnIndex("NIS"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return client;
    }

    @SuppressLint("Range")
    public PostData_Client select_client_etat_from_database(String clientt){

        PostData_Client client = new PostData_Client();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT CODE_CLIENT FROM CLIENT WHERE CLIENT = '"+clientt+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return client;
    }

    //============================== FUNCTION SELECT Familles FROM FAMILLES TABLE ===================
    @SuppressLint("Range")
    public ArrayList<String> select_familles_from_database(String querry){
        ArrayList<String> familles = new ArrayList<>();
        familles.add("Toutes");
        familles.add("<Aucune>");
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                familles.add(cursor.getString(cursor.getColumnIndex("FAMILLE")));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return familles;
    }

    //============================== FUNCTION SELECT Produits FROM Produit TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Produit> select_produits_from_database(String querry){
        ArrayList<PostData_Produit> produits = new ArrayList<PostData_Produit>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Produit produit = new PostData_Produit();

                produit.produit_id = cursor.getString(cursor.getColumnIndex("PRODUIT_ID"));
                produit.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                produit.ref_produit = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));

                produit.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                produit.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                produit.pamp = cursor.getDouble(cursor.getColumnIndex("PAMP"));
                produit.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                produit.pv1_ht = cursor.getDouble(cursor.getColumnIndex("PV1_HT"));
                produit.pv2_ht = cursor.getDouble(cursor.getColumnIndex("PV2_HT"));
                produit.pv3_ht = cursor.getDouble(cursor.getColumnIndex("PV3_HT"));
                produit.pv4_ht = cursor.getDouble(cursor.getColumnIndex("PV4_HT"));
                produit.pv5_ht = cursor.getDouble(cursor.getColumnIndex("PV5_HT"));
                produit.pv6_ht = cursor.getDouble(cursor.getColumnIndex("PV6_HT"));
                produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                produit.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                produit.stock_colis = cursor.getInt(cursor.getColumnIndex("STOCK_COLIS"));
                produit.stock_vrac = cursor.getInt(cursor.getColumnIndex("STOCK_VRAC"));

                produit.photo = cursor.getBlob(cursor.getColumnIndex("PHOTO"));
                produit.description = cursor.getString(cursor.getColumnIndex("DETAILLE"));
                produit.famille = cursor.getString(cursor.getColumnIndex("FAMILLE"));
                produit.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                produit.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                produit.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                produit.promo = cursor.getInt(cursor.getColumnIndex("PROMO"));
                produit.d1 = cursor.getString(cursor.getColumnIndex("D1"));
                produit.d2 = cursor.getString(cursor.getColumnIndex("D2"));
                produit.pp1_ht = cursor.getDouble(cursor.getColumnIndex("PP1_HT"));

                produit.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                produits.add(produit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return produits;
    }


    @SuppressLint("Range")
    public PostData_Produit select_one_produit_from_database(String querry){
        PostData_Produit produit = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                produit = new PostData_Produit();

                produit.produit_id = cursor.getString(cursor.getColumnIndex("PRODUIT_ID"));
                produit.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                produit.ref_produit = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                produit.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                produit.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                produit.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                produit.pamp = cursor.getDouble(cursor.getColumnIndex("PAMP"));
                produit.pv1_ht = cursor.getDouble(cursor.getColumnIndex("PV1_HT"));
                produit.pv2_ht = cursor.getDouble(cursor.getColumnIndex("PV2_HT"));
                produit.pv3_ht = cursor.getDouble(cursor.getColumnIndex("PV3_HT"));
                produit.pv4_ht = cursor.getDouble(cursor.getColumnIndex("PV4_HT"));
                produit.pv5_ht = cursor.getDouble(cursor.getColumnIndex("PV5_HT"));
                produit.pv6_ht = cursor.getDouble(cursor.getColumnIndex("PV6_HT"));
                produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                produit.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                produit.stock_colis = cursor.getInt(cursor.getColumnIndex("STOCK_COLIS"));
                produit.stock_vrac = cursor.getInt(cursor.getColumnIndex("STOCK_VRAC"));
                produit.photo = cursor.getBlob(cursor.getColumnIndex("PHOTO"));
                produit.description = cursor.getString(cursor.getColumnIndex("DETAILLE"));
                produit.famille = cursor.getString(cursor.getColumnIndex("FAMILLE"));
                produit.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                produit.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                produit.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                produit.promo = cursor.getInt(cursor.getColumnIndex("PROMO"));
                produit.d1 = cursor.getString(cursor.getColumnIndex("D1"));
                produit.d2 = cursor.getString(cursor.getColumnIndex("D2"));
                produit.pp1_ht = cursor.getDouble(cursor.getColumnIndex("PP1_HT"));

                produit.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return produit;
    }
    //========================== FUNCTION SELECT Code_barre FROM DATABASE ===========================
    @SuppressLint("Range")
    public String select_codebarre_from_database(String querry){
        String code_barre = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return code_barre;
    }

    //============================ FUNCTION SELECT commande achat FROM DATABASE=============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat1>  select_all_achat1_from_database(String querry){
        ArrayList<PostData_Achat1> bon1s_a_com = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Achat1 achat1 = new PostData_Achat1();

                achat1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                achat1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                achat1.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                achat1.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                achat1.heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                //////////
                achat1.nbr_p = cursor.getInt(cursor.getColumnIndex("NBR_P"));
                achat1.tot_qte = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                //////////
                achat1.tot_ht = cursor.getDouble(cursor.getColumnIndex("TOT_HT"));
               // achat1.tot_tva = cursor.getDouble(cursor.getColumnIndex("TOT_TVA"));
                achat1.timbre = cursor.getDouble(cursor.getColumnIndex("TIMBRE"));
                achat1.tot_ttc = cursor.getDouble(cursor.getColumnIndex("TOT_TTC"));
                achat1.remise = cursor.getDouble(cursor.getColumnIndex("REMISE"));
                achat1.montant_bon = cursor.getDouble(cursor.getColumnIndex("MONTANT_BON"));
                //////////
                achat1.solde_ancien = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                achat1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                achat1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));

                achat1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                achat1.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));

                //fournisseur
                achat1.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                achat1.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                achat1.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));

                bon1s_a_com.add(achat1);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon1s_a_com;
    }


    //============================ FUNCTION SELECT Ventes FROM DATABASE=============================
    @SuppressLint("Range")
    public ArrayList<PostData_Bon1>  select_all_bon1_from_database(String querry){
        ArrayList<PostData_Bon1> bon1s = new ArrayList<PostData_Bon1>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Bon1 bon1 = new PostData_Bon1();

                //////////////revise par smail le 10.05.2022////////////////////////////////////////
                bon1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                bon1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                bon1.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                bon1.heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                bon1.date_f = cursor.getString(cursor.getColumnIndex("DATE_F"));
                bon1.heure_f = cursor.getString(cursor.getColumnIndex("HEURE_F"));
                bon1.mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                bon1.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                //////////
                bon1.nbr_p = cursor.getInt(cursor.getColumnIndex("NBR_P"));
                bon1.tot_qte = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                //////////
                bon1.tot_ht = cursor.getDouble(cursor.getColumnIndex("TOT_HT"));
                bon1.tot_tva = cursor.getDouble(cursor.getColumnIndex("TOT_TVA"));
                bon1.timbre = cursor.getDouble(cursor.getColumnIndex("TIMBRE"));
                bon1.tot_ttc = cursor.getDouble(cursor.getColumnIndex("TOT_TTC"));
                bon1.remise = cursor.getDouble(cursor.getColumnIndex("REMISE"));
                bon1.montant_bon = cursor.getDouble(cursor.getColumnIndex("MONTANT_BON"));
                bon1.benifice_par_bon = cursor.getDouble(cursor.getColumnIndex("BENIFICE_BON"));
                //////////
                bon1.solde_ancien = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                bon1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                bon1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));
                //////////
                bon1.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                bon1.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                //////////
                bon1.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                bon1.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                bon1.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                bon1.wilaya = cursor.getString(cursor.getColumnIndex("WILAYA"));
                bon1.commune = cursor.getString(cursor.getColumnIndex("COMMUNE"));
                bon1.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                bon1.rc = cursor.getString(cursor.getColumnIndex("RC"));
                bon1.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                bon1.ai = cursor.getString(cursor.getColumnIndex("AI"));
                bon1.nis = cursor.getString(cursor.getColumnIndex("NIS"));

                bon1.client_solde = cursor.getDouble(cursor.getColumnIndex("SOLDE_CLIENT"));
                bon1.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                bon1.latitude_client = cursor.getDouble(cursor.getColumnIndex("LATITUDE_CLIENT"));
                bon1.longitude_client = cursor.getDouble(cursor.getColumnIndex("LONGITUDE_CLIENT"));

                bon1.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                bon1.code_vendeur = cursor.getString(cursor.getColumnIndex("CODE_VENDEUR"));
                bon1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                bon1.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));

                bon1s.add(bon1);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon1s;
    }

    @SuppressLint("Range")
    public int  select_count_from_database(String querry){
        //ArrayList<PostData_Bon1> bon1s = new ArrayList<PostData_Bon1>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }


    //////////////select_bon1_from_database2 revise par smail le 10.05.2022/////////////////////////
    @SuppressLint("Range")
    public PostData_Bon1 select_bon1_from_database2(String querry){
        PostData_Bon1 bon1 = new PostData_Bon1();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //////////////revise par smail le 10.05.2022////////////////////////////////////////
                bon1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                bon1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                bon1.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                bon1.heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                bon1.mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                bon1.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                //////////
                bon1.nbr_p = cursor.getInt(cursor.getColumnIndex("NBR_P"));
                bon1.tot_qte = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                //////////
                bon1.tot_ht = cursor.getDouble(cursor.getColumnIndex("TOT_HT"));
                bon1.tot_tva = cursor.getDouble(cursor.getColumnIndex("TOT_TVA"));
                bon1.timbre = cursor.getDouble(cursor.getColumnIndex("TIMBRE"));
                bon1.tot_ttc = cursor.getDouble(cursor.getColumnIndex("TOT_TTC"));
                bon1.remise = cursor.getDouble(cursor.getColumnIndex("REMISE"));
                bon1.montant_bon = cursor.getDouble(cursor.getColumnIndex("MONTANT_BON"));

                //////////
                bon1.solde_ancien = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                bon1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                bon1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));
                //////////
                bon1.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                bon1.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                //////////
                bon1.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                bon1.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                bon1.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                bon1.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                bon1.rc = cursor.getString(cursor.getColumnIndex("RC"));
                bon1.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                bon1.ai = cursor.getString(cursor.getColumnIndex("AI"));
                bon1.nis = cursor.getString(cursor.getColumnIndex("NIS"));

                bon1.client_solde = cursor.getDouble(cursor.getColumnIndex("SOLDE_CLIENT"));
                bon1.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                bon1.latitude_client = cursor.getDouble(cursor.getColumnIndex("LATITUDE_CLIENT"));
                bon1.longitude_client = cursor.getDouble(cursor.getColumnIndex("LONGITUDE_CLIENT"));

                bon1.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                bon1.code_vendeur = cursor.getString(cursor.getColumnIndex("CODE_VENDEUR"));
                bon1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                bon1.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon1;
    }


    @SuppressLint("Range")
    public PostData_Achat1 select_one_acha1_from_database(String querry){
        PostData_Achat1 achat1 = new PostData_Achat1();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                achat1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                achat1.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                achat1.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                achat1.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                achat1.heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                //////////
                achat1.nbr_p = cursor.getInt(cursor.getColumnIndex("NBR_P"));
                achat1.tot_qte = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                //////////
                achat1.tot_ht = cursor.getDouble(cursor.getColumnIndex("TOT_HT"));
                achat1.tot_tva = cursor.getDouble(cursor.getColumnIndex("TOT_TVA"));
                achat1.timbre = cursor.getDouble(cursor.getColumnIndex("TIMBRE"));
                achat1.tot_ttc = cursor.getDouble(cursor.getColumnIndex("TOT_TTC"));
                achat1.remise = cursor.getDouble(cursor.getColumnIndex("REMISE"));
                achat1.montant_bon = cursor.getDouble(cursor.getColumnIndex("MONTANT_BON"));

                achat1.solde_ancien = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                achat1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                achat1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));

                achat1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                achat1.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));
                achat1.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));

                //fournisseur
                achat1.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                achat1.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                achat1.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));





                //////////


            } while (cursor.moveToNext());
        }
        cursor.close();
        return achat1;
    }

    //================================== GET MAX NUM_BON  =========================================
    @SuppressLint("Range")
    public String select_max_num_bon(String querry){
        String max = "0";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querry, null);
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        cursor.close();

        String num_bon = format_num_bon(String.valueOf(Integer.valueOf(max) + 1), 6);
        return num_bon;
    }


    //=============================== FUNCTION TO INSERT INTO bon1 or bon1_temp TABLE ===============================
    public boolean insert_into_bon1(String _table, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                boolean bon1_exist = false;
                Cursor cursor0 = db.rawQuery("SELECT NUM_BON FROM "+ _table +" WHERE NUM_BON = '"+bon1.num_bon+"' ", null);
                // looping through all rows and adding to list
                if (cursor0.moveToFirst()) {
                    do {
                        bon1_exist = true;
                    } while (cursor0.moveToNext());
                }

                if(bon1_exist){

                    //update_bon1
                    ContentValues args1 = new ContentValues();
                    args1.put("CODE_CLIENT", bon1.code_client);
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {bon1.num_bon};
                    db.update(_table, args1, selection1, selectionArgs1);

                }else {
                    ContentValues values = new ContentValues();
                    values.put("NUM_BON", bon1.num_bon);
                    values.put("CODE_CLIENT", bon1.code_client);
                    values.put("DATE_BON", bon1.date_bon);
                    values.put("HEURE", bon1.heure);
                    values.put("CODE_DEPOT", bon1.code_depot);
                    values.put("MODE_TARIF", bon1.mode_tarif);
                    values.put("VERSER", "0.00");
                    values.put("TIMBRE", "0.00");
                    values.put("REMISE", "0.00");
                    values.put("BLOCAGE", "N");
                    values.put("ANCIEN_SOLDE", bon1.solde_ancien);
                    values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"");
                    db.insert(_table, null, values);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO INSERT INTO bon1 or bon1_temp TABLE ===============================
        public boolean ExCommande_Export_to_ventes(String _table1, String _table2, PostData_Bon1 bon1, ArrayList<PostData_Bon2> bon2s, String new_num_bon){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                    // Insert into bon1
                    ContentValues values = new ContentValues();
                    values.put("NUM_BON", new_num_bon);
                    values.put("CODE_CLIENT", bon1.code_client);
                    values.put("DATE_BON", bon1.date_bon);
                    values.put("HEURE", bon1.heure);
                    values.put("CODE_DEPOT", bon1.code_depot);
                    values.put("VERSER", "0.00");
                    values.put("BLOCAGE", "N");
                    values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"");
                    values.put("NBR_P", bon1.nbr_p);
                    values.put("MODE_TARIF", bon1.mode_tarif);
                    values.put("TOT_HT", bon1.tot_ht);
                    values.put("TOT_TVA", bon1.tot_tva);
                    values.put("TOT_TTC", bon1.tot_ttc);

                    values.put("REMISE", bon1.remise);
                    values.put("TIMBRE", bon1.timbre);
                    values.put("MONTANT_BON", bon1.montant_bon);
                    values.put("CODE_VENDEUR", bon1.code_vendeur);
                    values.put("ANCIEN_SOLDE", bon1.solde_ancien);
                    values.put("RESTE", bon1.reste);
                    values.put("LATITUDE", bon1.latitude);
                    values.put("LONGITUDE", bon1.longitude);
                    db.insert(_table1, null, values);

                // Insert into bon2

                for(int i = 0; i< bon2s.size(); i++){
                    ContentValues values2 = new ContentValues();
                    values2.putNull("RECORDID");
                    values2.put("NUM_BON", new_num_bon);
                    values2.put("CODE_BARRE", bon2s.get(i).codebarre);
                    values2.put("PRODUIT", bon2s.get(i).produit);
                    values2.put("QTE", bon2s.get(i).qte);
                    values2.put("QTE_GRAT", bon2s.get(i).gratuit);
                    values2.put("PV_HT", bon2s.get(i).pv_ht);
                    values2.put("PA_HT", bon2s.get(i).pa_ht);
                    values2.put("TVA", bon2s.get(i).tva);
                    values2.put("CODE_DEPOT", bon1.code_depot);
                    values2.put("PA_HT", bon2s.get(i).pa_ht);

                    db.insert(_table2, null, values2);

                    update_Stock_Produit_vente("BON2_INSERT",bon2s.get(i) , 0.0,0.0);
                }


                // Update Bon1_temp
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "V");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {bon1.num_bon};
                db.update("BON1_TEMP", args1, selection1, selectionArgs1);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //=============================== FUNCTION TO INSERT INTO Bon2 TABLE ===============================
    public boolean insert_into_bon2(String _table, String num_bon, String code_depot, PostData_Bon2 list_bon2){

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                    ContentValues values2 = new ContentValues();
                    values2.putNull("RECORDID");
                    values2.put("NUM_BON", num_bon);
                    values2.put("CODE_BARRE", list_bon2.codebarre);
                    values2.put("PRODUIT", list_bon2.produit);
                    values2.put("NBRE_COLIS", list_bon2.nbr_colis);
                    values2.put("COLISSAGE", list_bon2.colissage);
                    values2.put("QTE", list_bon2.qte);
                    values2.put("QTE_GRAT", list_bon2.gratuit);
                    values2.put("TVA", list_bon2.tva);
                    values2.put("CODE_DEPOT", code_depot);
                    values2.put("PA_HT", list_bon2.pa_ht);
                    values2.put("PV_HT", list_bon2.pv_ht);
                    values2.put("DESTOCK_TYPE", list_bon2.destock_type);
                    values2.put("DESTOCK_CODE_BARRE", list_bon2.destock_code_barre);
                    values2.put("DESTOCK_QTE", list_bon2.destock_qte);

                    db.insert(_table, null, values2);

                    if(_table.equals("BON2")){
                        update_Stock_Produit_vente("BON2_INSERT", list_bon2, 0.0, 0.0);
                    }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE BON2   ===============================
    public boolean update_into_bon2( String _table, String num_bon, PostData_Bon2 list_bon2, Double qte_old, Double gratuit_old){
        boolean executed = false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                ContentValues args1 = new ContentValues();

                args1.put("NBRE_COLIS", list_bon2.nbr_colis);
                args1.put("COLISSAGE", list_bon2.colissage);
                args1.put("QTE", list_bon2.qte);
                args1.put("QTE_GRAT", list_bon2.gratuit);
                args1.put("PV_HT", list_bon2.pv_ht);
                args1.put("TVA", list_bon2.tva);

                String selection1 = "RECORDID=? AND NUM_BON=?";
                String[] selectionArgs1 = {String.valueOf(list_bon2.recordid), num_bon};
                db.update(_table, args1, selection1, selectionArgs1);

                if(_table.equals("BON2")){
                    update_Stock_Produit_vente("BON2_EDIT", list_bon2, qte_old, gratuit_old);
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }

        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //=============================== FUNCTION TO UPDATE ACHAT2   ===============================

    public boolean update_into_achat2( String _table, String num_bon, PostData_Achat2 list_achat2, Double qte_old, Double gratuit_old){
        boolean executed = false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                ContentValues args1 = new ContentValues();
                args1.put("NBRE_COLIS", list_achat2.nbr_colis);
                args1.put("COLISSAGE", list_achat2.colissage);
                args1.put("QTE", list_achat2.qte);
                args1.put("QTE_GRAT", list_achat2.gratuit);
                args1.put("PA_HT", list_achat2.pa_ht);
                args1.put("TVA", list_achat2.tva);

                String selection1 = "RECORDID=? AND NUM_BON=?";
                String[] selectionArgs1 = {String.valueOf(list_achat2.recordid), num_bon};
                db.update(_table, args1, selection1, selectionArgs1);

                if(_table.equals("ACHAT2")){
                    update_Stock_Produit_achat("ACHAT2_EDIT", list_achat2, qte_old, gratuit_old);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    /*
    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    public ArrayList<PostData_Bon2> sshoww(String num_bon){
        ArrayList<PostData_Bon2> bon2s = new ArrayList<PostData_Bon2>();
        SQLiteDatabase db = this.getWritableDatabase();

        String querry= "" +
                "SELECT " +
                "Bon2_temp.CODE_BARRE, " +
                "Bon2_temp.NUM_BON, " +
                "Bon2_temp.PRODUIT, " +
                "Bon2_temp.CODE_DEPOT " +
                "FROM Bon2_temp " +
                "WHERE Bon2_temp.NUM_BON = '" + num_bon + "'";

        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Bon2 bon2 = new PostData_Bon2();

                Log.v("TRACKKK", "CODE_BARRE : "+ cursor.getString(cursor.getColumnIndex("CODE_BARRE")) + " / " +"NUM_BON : "+ cursor.getString(cursor.getColumnIndex("NUM_BON")) + " / " +"PRODUIT : "+ cursor.getString(cursor.getColumnIndex("PRODUIT")) + " / " + "CODE_DEPOT : "+ cursor.getString(cursor.getColumnIndex("CODE_DEPOT")));
                Log.v("TRACKKK", "==================================");

                bon2s.add(bon2);
            } while (cursor.moveToNext());
        }
        return bon2s;
    }
    */
    ////////////////////////////////////// DELETING ////////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_from_bon2(String _table, Integer recordid, PostData_Bon2 bon2){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);

                if(_table.equals("BON2")){
                    update_Stock_Produit_vente("BON2_DELETE", bon2 , 0.0,0.0);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }


    @SuppressLint("Range")
    public void delete_from_achat2(String _table, Integer recordid, PostData_Achat2 achat2){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);

                if(_table.equals("ACHAT2")){
                    update_Stock_Produit_achat("ACHAT2_DELETE", achat2 , 0.0,0.0);
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    @SuppressLint("Range")
    public boolean delete_from_inv2(String _table, Integer recordid){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }


    @SuppressLint("Range")
    public boolean delete_client_from_routing(String CODE_CLIENT){

        int index =0;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {CODE_CLIENT};
                index = db.delete("ROUTING", selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return index == 1;
    }
    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    @SuppressLint("Range")
    public boolean update_bon1_client(String num_bon, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "F");
                args1.put("ANCIEN_SOLDE", bon1.solde_ancien);
                args1.put("VERSER", bon1.verser);
                if(bon1.verser == 0){
                    args1.put("MODE_RG", "A TERME");
                }else{
                    args1.put("MODE_RG", "ESPECE");
                }
                args1.put("RESTE", bon1.reste);
                args1.put("LATITUDE", bon1.latitude);
                args1.put("LONGITUDE", bon1.longitude);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1", args1, selection1, selectionArgs1);

                // get information client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='"+ bon1.code_client+ "'", null);
                // looping through all rows and adding to list
                if (cursor1.moveToFirst()) {
                    do {
                        client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                //update client

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_montant + bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_montant + bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_montant + bon1.montant_bon) - bon1.verser));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {bon1.code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    public boolean update_bon1_temp(String num_bon, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "F");
                args1.put("LATITUDE", bon1.latitude);
                args1.put("LONGITUDE", bon1.longitude);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1_TEMP", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    @SuppressLint("Range")
    public boolean update_bon1_client_edit(String num_bon, String code_client, PostData_Bon1 bon1){

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "N");
                args1.put("ANCIEN_SOLDE", "0.00");
                args1.put("VERSER", "0.00");
                args1.put("RESTE", "0.00");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1", args1, selection1, selectionArgs1);

                //update_client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='"+ code_client+ "'", null);
                // looping through all rows and adding to list
                if (cursor1.moveToFirst()) {
                    do {
                        client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_montant - bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_montant - bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_montant - bon1.montant_bon) +  bon1.verser));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {bon1.code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    public boolean update_bon1_temp_edit(String num_bon, String code_client, PostData_Bon1 bon1){

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "N");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1_TEMP", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;

    }


    public boolean update_achats_commandes_as_exported(boolean isTemp, String num_bon){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                // Update Bon1 or Bon1_temp
                ContentValues args1 = new ContentValues();
                args1.put("IS_EXPORTED", 1);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};

                if(isTemp){
                    db.update("ACHAT1_TEMP", args1, selection1, selectionArgs1);
                }else{
                    db.update("ACHAT1", args1, selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    public boolean update_ventes_commandes_as_exported(boolean isTemp, String num_bon){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                // Update Bon1 or Bon1_temp
                ContentValues args1 = new ContentValues();
                args1.put("IS_EXPORTED", 1);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};

                if(isTemp){
                    db.update("BON1_TEMP", args1, selection1, selectionArgs1);
                }else{
                    db.update("BON1", args1, selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    @SuppressLint("Range")
    public PostData_Bon2 check_if_bon2_exist(String querry){
        //check if bon2 exist
        PostData_Bon2 bon2 = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( querry , null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                bon2 = new PostData_Bon2();
                bon2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                bon2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                bon2.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                bon2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                bon2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                bon2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                bon2.qte = cursor.getDouble(cursor.getColumnIndex("QTE"));
                bon2.gratuit = cursor.getDouble(cursor.getColumnIndex("QTE_GRAT"));
                bon2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                bon2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                bon2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                bon2.pv_ht = cursor.getDouble(cursor.getColumnIndex("PV_HT"));
                bon2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return  bon2;
    }

    @SuppressLint("Range")
    public PostData_Achat2 check_if_achat2_exist(String querry){
        //check if bon2 exist
        PostData_Achat2 achat2 = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( querry , null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                achat2 = new PostData_Achat2();
                achat2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                achat2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                achat2.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                achat2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                achat2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                achat2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                achat2.qte = cursor.getDouble(cursor.getColumnIndex("QTE"));
                achat2.gratuit = cursor.getDouble(cursor.getColumnIndex("QTE_GRAT"));
                achat2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                achat2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                achat2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                achat2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return  achat2;
    }

    public boolean check_if_has_bon(String querry){
        //check if client has bons
        boolean has_bon = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( querry , null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                has_bon = true;
                break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  has_bon;
    }

    @SuppressLint("Range")
    public PostData_Inv2 check_if_inv2_exist(String querry){
        //check if bon2 exist
        PostData_Inv2 inv2 = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                inv2 = new PostData_Inv2();
                inv2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                inv2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                inv2.num_inv = cursor.getString(cursor.getColumnIndex("NUM_INV"));
                inv2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                inv2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                inv2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                inv2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                inv2.qte_theorique = cursor.getDouble(cursor.getColumnIndex("QTE"));
                inv2.qte_physique = cursor.getDouble(cursor.getColumnIndex("QTE_NEW"));
                inv2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                inv2.vrac = cursor.getDouble(cursor.getColumnIndex("VRAC"));
                inv2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return  inv2;
    }
    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Bon2> select_bon2_from_database(String querry){
        ArrayList<PostData_Bon2> bon2s = new ArrayList<PostData_Bon2>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Bon2 bon2 = new PostData_Bon2();

                bon2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                bon2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                bon2.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                bon2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                bon2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                bon2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                bon2.qte = cursor.getDouble(cursor.getColumnIndex("QTE"));
                bon2.gratuit = cursor.getDouble(cursor.getColumnIndex("QTE_GRAT"));
                bon2.pv_ht = cursor.getDouble(cursor.getColumnIndex("PV_HT"));
                bon2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                bon2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                bon2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                bon2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                bon2.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));
                bon2.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                bon2.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                bon2.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                bon2s.add(bon2);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon2s;
    }


    public boolean update_Stock_Produit_vente( String source, PostData_Bon2 panier,Double qte_old,Double gratuit_old ){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                switch (source) {
                    case "BON2_INSERT", "BON2_EDIT" ->
                            args.put("STOCK", panier.stock_produit - panier.qte - panier.gratuit + qte_old + gratuit_old);
                    case "BON2_DELETE" ->
                            args.put("STOCK", panier.stock_produit + panier.qte + panier.gratuit);
                }

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {panier.codebarre};
                db.update("PRODUIT", args, selection, selectionArgs);
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean update_Stock_Produit_achat( String source, PostData_Achat2 panier,Double qte_old,Double gratuit_old ){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                switch (source) {
                    case "ACHAT2_INSERT", "ACHAT2_EDIT" ->
                            args.put("STOCK", panier.stock_produit + panier.qte + panier.gratuit - qte_old - gratuit_old);
                    case "ACHAT2_DELETE" ->
                            args.put("STOCK", panier.stock_produit - panier.qte - panier.gratuit);
                }

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {panier.codebarre};
                db.update("PRODUIT", args, selection, selectionArgs);
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public void  update_pamp_on_insert_update_produit(String codebarre, double stock, double pa_ht_old, double qte, double pa_ht_new, double pamp_old){
        SQLiteDatabase db = this.getWritableDatabase();
        double pamp_new = 0;
        if(stock + qte ==0){
            if(stock <0){
                pamp_new = pa_ht_new;
            }else if(stock >0){
                pamp_new = (pamp_old + pa_ht_new) / 2;
            }
        }else if(stock + qte != 0){
            if(stock <=0){
                pamp_new = pa_ht_new;
            }else if(stock >0){
                pamp_new = ((stock * pa_ht_old) + (qte * pa_ht_new)) / (stock + qte);
            }
        }

        try {
            ContentValues args = new ContentValues();
            args.put("PAMP", pamp_new);
            args.put("PA_HT", pa_ht_new);
            String selection = "CODE_BARRE=?";
            String[] selectionArgs = {codebarre};
            db.update("PRODUIT", args, selection, selectionArgs);
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public void  update_pamp_on_delete_produit(String codebarre, double stock, double pa_ht_old, double qte, double pa_ht_new, double pamp_old){
        SQLiteDatabase db = this.getWritableDatabase();
        double pamp_new = 0;
        if (stock - qte > 0) {
            pamp_new = ((pamp_old * stock) - (pa_ht_new * qte)) / (stock - qte);
        }else{
            pamp_new = pamp_old;
        }

        try {
            ContentValues args = new ContentValues();
            args.put("PAMP", pamp_new);
            args.put("PA_HT", pa_ht_new);
            String selection = "CODE_BARRE=?";
            String[] selectionArgs = {codebarre};
            db.update("PRODUIT", args, selection, selectionArgs);
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }
    //================================== UPDATE TABLE (Inventaires1) =======================================

    public boolean update_fournisseur(PostData_Fournisseur fournisseur){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("FOURNIS", fournisseur.fournis);
                args.put("TEL", fournisseur.tel);
                args.put("ADRESSE", fournisseur.adresse);
                args.put("RC", fournisseur.rc);
                args.put("IFISCAL", fournisseur.ifiscal);
                args.put("AI", fournisseur.ai);
                args.put("NIS", fournisseur.nis);
                //args.put("ISNEW", 1);
                String selection = "CODE_FRS=?";
                String[] selectionArgs = {fournisseur.code_frs};
                db.update("FOURNIS", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean update_position_client(Double latitude , Double longitude, String code_client){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("LATITUDE", latitude);
                args.put("LONGITUDE", longitude);
                args.put("ISNEW", 1);
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean update_client(PostData_Client client){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("CLIENT", client.client);
                args.put("TEL", client.tel);
                args.put("WILAYA", client.wilaya);
                args.put("COMMUNE", client.commune);
                args.put("ADRESSE", client.adresse);
                args.put("MODE_TARIF", client.mode_tarif);
                args.put("ISNEW", client.isNew);
                args.put("RC", client.rc);
                args.put("IFISCAL", client.ifiscal);
                args.put("AI", client.ai);
                args.put("NIS", client.nis);
                //args.put("ISNEW", 1);
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {client.code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();

                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Bon1) ===============================
    public boolean update_bon1(String _table, String num_bon, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("NBR_P", bon1.nbr_p);
                args.put("TOT_QTE", bon1.tot_qte);
                args.put("MODE_TARIF", bon1.mode_tarif);
                args.put("TOT_HT", bon1.tot_ht);
                args.put("TOT_TVA", bon1.tot_tva);
                args.put("MONTANT_ACHAT", bon1.montant_achat);
                args.put("REMISE", bon1.remise);
                args.put("TIMBRE", bon1.timbre);
                args.put("MODE_RG", bon1.mode_rg);
                args.put("CODE_VENDEUR", bon1.code_vendeur);
                args.put("ANCIEN_SOLDE", bon1.solde_ancien);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    //================================== UPDATE TABLE (Bon1_a) ===============================
    public boolean update_achat1(String _table, String num_bon, PostData_Achat1 achat1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("NBR_P", achat1.nbr_p);
                args.put("TOT_QTE", achat1.tot_qte);
                args.put("TOT_HT", achat1.tot_ht);
                args.put("TOT_TVA", achat1.tot_tva);
                args.put("REMISE", achat1.remise);
                args.put("TIMBRE", achat1.timbre);
                args.put("MODE_RG", achat1.mode_rg);
                args.put("ANCIEN_SOLDE", achat1.solde_ancien);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Inventaires1) ===============================
    public boolean update_inv1_nbr_produit(String _table, PostData_Inv1 inv1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("NBR_PRODUIT", inv1.nbr_produit);
               // args.put("DATE_EXPORT_INV", inv1.date_export_inv);

                String selection = "NUM_INV=?";
                String[] selectionArgs = {inv1.num_inv};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    ////////////////////////////////////// DELETING ////////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_bon_vente(boolean isTemp, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Bon2> bon2_delete = new ArrayList<>();
                String selection;

                if(isTemp){
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {bon1.num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                }else{

                    bon2_delete = new ArrayList<>();
                    bon2_delete = select_bon2_from_database("" +
                            "SELECT " +
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
                            "PRODUIT.STOCK " +
                            "FROM Bon2 " +
                            "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2.NUM_BON = '" + bon1.num_bon + "'" );

                    for(int k=0; k< bon2_delete.size();k++) {

                        ContentValues args = new ContentValues();
                        args.put("STOCK", bon2_delete.get(k).stock_produit + bon2_delete.get(k).qte + bon2_delete.get(k).gratuit);

                         selection = "CODE_BARRE=?";
                        String[] selectionArgsss = {bon2_delete.get(k).codebarre};
                        db.update("PRODUIT", args, selection, selectionArgsss);
                    }


                    //update_client
                    PostData_Client client = new PostData_Client();
                    Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='"+ bon1.code_client+ "'", null);
                    // looping through all rows and adding to list
                    if (cursor1.moveToFirst()) {
                        do {
                            client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                            client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                            client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                        } while (cursor1.moveToNext());
                    }

                    ContentValues args = new ContentValues();
                    args.put("ACHATS", String.valueOf(client.achat_montant - bon1.montant_bon));
                    args.put("VERSER", String.valueOf(client.verser_montant - bon1.verser));
                    args.put("SOLDE", String.valueOf((client.solde_montant - bon1.montant_bon) +  bon1.verser));
                     selection = "CODE_CLIENT=?";
                    String[] selectionArgs = {bon1.code_client};
                    db.update("CLIENT", args, selection, selectionArgs);

                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {bon1.num_bon};
                    db.delete("BON1", selection1, selectionArgs1);
                    db.delete("BON2", selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }


    public boolean delete_bon_en_attente(boolean isTemp, String num_bon){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if(isTemp){
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                }else{

                    ArrayList<PostData_Bon2> bon2_delete = new ArrayList<>();
                    bon2_delete = select_bon2_from_database("" +
                            "SELECT " +
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
                            "PRODUIT.STOCK " +
                            "FROM BON2 " +
                            "LEFT JOIN  PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2.NUM_BON = '" + num_bon + "'" );

                    for(int k=0; k< bon2_delete.size();k++) {

                        ContentValues args = new ContentValues();
                        args.put("STOCK", bon2_delete.get(k).stock_produit + bon2_delete.get(k).qte);

                        String selection = "CODE_BARRE=?";
                        String[] selectionArgs = {bon2_delete.get(k).codebarre};
                        db.update("PRODUIT", args, selection, selectionArgs);
                    }

                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon};
                    db.delete("BON1", selection1, selectionArgs1);
                    db.delete("BON2", selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }


    @SuppressLint("Range")
    public void delete_bon_achat(boolean isTemp, PostData_Achat1 achat1){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Achat2> achat2_delete;
                String selection;

                if(isTemp){
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {achat1.num_bon};
                    db.delete("ACHAT1_TEMP", selection, selectionArgs);
                    db.delete("ACHAT2_TEMP", selection, selectionArgs);
                }else{

                    achat2_delete = select_all_achat2_from_database("" +
                            "SELECT " +
                            "ACHAT2.RECORDID, " +
                            "ACHAT2.CODE_BARRE, " +
                            "ACHAT2.NUM_BON, " +
                            "ACHAT2.PRODUIT, " +
                            "ACHAT2.NBRE_COLIS, " +
                            "ACHAT2.COLISSAGE, " +
                            "ACHAT2.QTE, " +
                            "ACHAT2.QTE_GRAT, " +
                            "ACHAT2.PA_HT, " +
                            "ACHAT2.TVA, " +
                            "ACHAT2.CODE_DEPOT, " +
                            "ACHAT2.QTE_GRAT, " +
                            "PRODUIT.PAMP, " +
                            "PRODUIT.STOCK " +
                            "FROM ACHAT2 " +
                            "LEFT JOIN PRODUIT ON (ACHAT2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE ACHAT2.NUM_BON = '" + achat1.num_bon + "'" );

                    for(int k=0; k< achat2_delete.size();k++) {


                        update_Stock_Produit_achat("ACHAT2_DELETE", achat2_delete.get(k), achat2_delete.get(k).qte, achat2_delete.get(k).gratuit);

                        update_pamp_on_delete_produit(
                                achat2_delete.get(k).codebarre,
                                achat2_delete.get(k).stock_produit,
                                achat2_delete.get(k).pa_ht_produit,
                                achat2_delete.get(k).qte,
                                achat2_delete.get(k).pa_ht,
                                achat2_delete.get(k).pa_ht);

                        /*ContentValues args = new ContentValues();
                        args.put("STOCK", bon2_delete.get(k).stock_produit - bon2_delete.get(k).qte - bon2_delete.get(k).gratuit);

                        selection = "CODE_BARRE=?";
                        String[] selectionArgsss = {bon2_delete.get(k).codebarre};
                        db.update("PRODUIT", args, selection, selectionArgsss);*/
                    }


                    //update_fournisseur

                    db.execSQL("UPDATE FOURNIS SET ACHATS = ACHATS - " + achat1.montant_bon + ", VERSER = VERSER - " + achat1.verser + ", SOLDE = SOLDE - " + (achat1.montant_bon - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs+ "'");

                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {achat1.num_bon};

                    db.delete("ACHAT1", selection1, selectionArgs1);
                    db.delete("ACHAT2", selection1, selectionArgs1);

                }

                db.setTransactionSuccessful();

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public boolean delete_bon_after_export(boolean isTemp, String num_bon){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if(isTemp){
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                }else{
                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon};
                    db.delete("BON1", selection1, selectionArgs1);
                    db.delete("BON2", selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }



    public boolean delete_all_bon(boolean isTemp){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if(isTemp){
                    //delete all temp bon

                    String sql_delete_bon2 = "DELETE FROM Bon2_Temp WHERE Bon2_Temp.NUM_BON IN ( SELECT Bon1_Temp.NUM_BON FROM Bon1_Temp WHERE Bon1_Temp.IS_EXPORTED = 1)";
                    String sql_delete_bon1 = "DELETE FROM Bon1_Temp WHERE IS_EXPORTED = 1";
                    db.execSQL(sql_delete_bon2);
                    db.execSQL(sql_delete_bon1);
                    /*
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon.toString()};
                    db.delete("Bon1_temp", selection, selectionArgs);
                    db.delete("Bon2_temp", selection, selectionArgs);
                    */
                }else{
                    // finally delete all bon
                    String sql_delete_bon2 = "DELETE FROM Bon2 WHERE Bon2.NUM_BON IN ( SELECT Bon1.NUM_BON FROM Bon1 WHERE Bon1.IS_EXPORTED = 1)";
                    String sql_delete_bon1 = "DELETE FROM Bon1 WHERE IS_EXPORTED = 1";
                    db.execSQL(sql_delete_bon2);
                    db.execSQL(sql_delete_bon1);
                    /*
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon.toString()};
                    db.delete("Bon1", selection1, selectionArgs1);
                    db.delete("Bon2", selection1, selectionArgs1);
                    */
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }



    @SuppressLint("Range")
    public boolean delete_versement(PostData_Carnet_c carnet_c){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                    //update_client
                    PostData_Client client = new PostData_Client();
                    Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='"+ carnet_c.code_client+ "'", null);
                    // looping through all rows and adding to list
                    if (cursor1.moveToFirst()) {
                        do {
                            client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                            client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                            client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                        } while (cursor1.moveToNext());
                    }

                    ContentValues args1 = new ContentValues();
                    //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                    args1.put("VERSER", client.verser_montant - carnet_c.carnet_versement);
                    args1.put("SOLDE",  client.solde_montant +  carnet_c.carnet_versement);
                    String selection1 = "CODE_CLIENT=?";
                    String[] selectionArgs1 = {carnet_c.code_client};
                    db.update("CLIENT", args1, selection1, selectionArgs1);


                    // Finally delete versement
                    String selection = "RECORDID=?";
                    String[] selectionArgs = {carnet_c.recordid};
                    db.delete("Carnet_c", selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_Etatv> select_etatv_from_database(String wilaya , String commune ,String c_client , String from_d, String to_d, boolean show_benifice){

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<PostData_Etatv>();

        double tot_qte = 0.00;
        double tot_montant_par_qte = 0.00;
        double tot_montant_total = 0.00;
        double total_verser = 0.00;
        double tot_remise = 0.00;
        double tot_credit = 0.00;
        double total_benifice = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "BON2.PRODUIT, " +
                "SUM(BON2.QTE) AS TOT_QTE, " +
                "BON2.PV_HT, " +
                "SUM(BON2.QTE) * (BON2.PV_HT + (BON2.PV_HT * BON2.TVA / 100)) AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN BON2 ON BON2.NUM_BON = BON1.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                "WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') ";
        if(c_client != null){
            querry = querry + " AND BON1.CODE_CLIENT = '"+ c_client +"' " ;
        }
        if(!Objects.equals(wilaya, "<Aucune>")){
            querry = querry + " AND CLIENT.WILAYA = '"+ wilaya +"' " ;
        }
        if(!Objects.equals(commune, "<Aucune>")){
            querry = querry + " AND CLIENT.COMMUNE = '"+ commune +"' " ;
        }
        querry = querry + " AND BON1.BLOCAGE = 'F' ";

        querry = querry + "GROUP BY BON2.PRODUIT, BON2.PV_HT, BON2.QTE_GRAT "  +
                          "UNION ALL "  +
                          "SELECT " +
                          "BON2.PRODUIT, " +
                          "SUM(BON2.QTE_GRAT) AS TOT_QTE, "  +
                          "0.0 AS PV_HT," +
                          "0.0 AS TOTAL_MONTANT_PRODUIT, " +
                          "CLIENT.WILAYA AS WILAYA, " +
                          "CLIENT.COMMUNE AS COMMUNE " +
                          "FROM BON1 " +
                          "LEFT JOIN BON2 ON BON2.NUM_BON = BON1.NUM_BON "  +
                          "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                          "WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') ";

        if(c_client != null){
            querry = querry + " AND BON1.CODE_CLIENT = '"+ c_client +"' " ;
        }
        if(!Objects.equals(wilaya, "<Aucune>")){
            querry = querry + " AND CLIENT.WILAYA = '"+ wilaya +"' " ;
        }
        if(!Objects.equals(commune, "<Aucune>")){
            querry = querry + " AND CLIENT.COMMUNE = '"+ commune +"' " ;
        }
        querry = querry + " AND BON1.BLOCAGE = 'F' AND BON2.QTE_GRAT <> 0 GROUP BY BON2.PRODUIT, BON2.PV_HT, BON2.QTE_GRAT  ORDER BY PRODUIT, TOT_QTE DESC";



        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Etatv etatv = new PostData_Etatv();

                etatv.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                etatv.quantite = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                etatv.montant = cursor.getDouble(cursor.getColumnIndex("TOTAL_MONTANT_PRODUIT"));
                etatv.pv_ht = cursor.getDouble(cursor.getColumnIndex("PV_HT"));
                etatv.code_parent = "1";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));

                tot_qte = tot_qte + etatv.quantite;
                tot_montant_par_qte = tot_montant_par_qte + etatv.montant;

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }


        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);


        String querry1 = "SELECT " +
                "BON1.REMISE, " +
                "BON1.TOT_HT, " +
                "BON1.TOT_TVA, " +
                "BON1.TOT_HT - BON1.REMISE AS MONTANT_BON_HT, " +
                "CARNET_C.VERSEMENTS as VERSEMENTS, " +
                "BON1.VERSER, " +
                "(BON1.TOT_HT - BON1.REMISE) -  BON1.MONTANT_ACHAT AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN CARNET_C ON BON1.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                " WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') ";
        if(c_client != null){
            querry1 = querry1 + " AND BON1.CODE_CLIENT = '"+ c_client +"' " ;
        }
        if(!Objects.equals(wilaya, "<Aucune>")){
            querry1 = querry1 + " AND CLIENT.WILAYA = '"+ wilaya +"' " ;
        }
        if(!Objects.equals(commune, "<Aucune>")){
            querry1 = querry1 + " AND CLIENT.COMMUNE = '"+ commune +"' " ;
        }
        querry1 = querry1 + "  AND BON1.BLOCAGE = 'F'";


        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getDouble(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_HT"));
                etatv2.total_versement = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENTS"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_benifice = total_benifice + etatv2.benifice;

                if(etatv2.vers_client == 0)
                {
                    total_verser = total_verser + etatv2.total_versement;
                }else
                {
                    total_verser = total_verser + (etatv2.total_versement + etatv2.vers_client) ;

                }




                tot_remise = tot_remise + etatv2.total_remise;

            } while (cursor1.moveToNext());
        }


        tot_credit = tot_montant_total - total_verser;
        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL REMISE:";
        etatv.montant = tot_remise;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CHIFFRE D'AFFAIRE :";
        etatv.montant = tot_montant_total;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSER :";
        etatv.montant = total_verser;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOTAL :";
        etatv.montant = tot_credit;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        if(show_benifice){
            etatv = new PostData_Etatv();
            etatv.produit = "BENIFICE TOTAL :";
            etatv.montant = total_benifice;
            etatv.code_parent = "-6";
            all_etatv.add(etatv);
        }


        double objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        objectif = prefs.getLong("OBJECTIF_MONTANT", 0);
        if(tot_montant_total < (Double.valueOf(objectif) / 2) ){
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else if(Double.valueOf(objectif) > tot_montant_total){
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else{
            etatv = new PostData_Etatv();
            etatv.produit = "N3";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }

        return all_etatv;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_Etatv> select_etat_global_from_database(String from_d, String to_d, boolean show_benifice){

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<>();

        double tot_qte = 0.00;
        double tot_montant_par_qte = 0.00;
        double tot_montant_total = 0.00;
        double total_verser = 0.00;
        double tot_remise = 0.00;
        double tot_credit = 0.00;
        double total_verser_c = 0.00;
        double total_benifice = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "BON2.PRODUIT, " +
                "SUM(BON2.QTE) AS TOT_QTE, " +
                "BON2.PV_HT, " +
                "SUM(BON2.QTE) * (BON2.PV_HT + (BON2.PV_HT * BON2.TVA / 100)) AS TOTAL_MONTANT_PRODUIT " +
                "FROM BON2 LEFT JOIN BON1 ON BON2.NUM_BON = BON1.NUM_BON " +
                "WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "')  " +
                "GROUP BY BON2.PRODUIT, BON2.PV_HT, BON2.QTE_GRAT " +
                "UNION ALL " +
                "SELECT " +
                "BON2.PRODUIT, " +
                "SUM(BON2.QTE_GRAT) AS TOT_QTE, " +
                "0.0 AS PV_HT," +
                "0.0 AS TOTAL_MONTANT_PRODUIT " +
                "FROM BON2 LEFT JOIN BON1 ON BON2.NUM_BON = BON1.NUM_BON " +
                "WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') AND BON2.QTE_GRAT <> 0 " +
                "GROUP BY BON2.PRODUIT, BON2.PV_HT, BON2.QTE_GRAT " +
                "ORDER BY PRODUIT, TOT_QTE DESC";

        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Etatv etatv = new PostData_Etatv();

                etatv.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                etatv.quantite = cursor.getDouble(cursor.getColumnIndex("TOT_QTE"));
                etatv.montant = cursor.getDouble(cursor.getColumnIndex("TOTAL_MONTANT_PRODUIT"));
                etatv.pv_ht = cursor.getDouble(cursor.getColumnIndex("PV_HT"));
                etatv.code_parent = "1";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));

                tot_qte = tot_qte + Double.valueOf(etatv.quantite);
                tot_montant_par_qte = tot_montant_par_qte + Double.valueOf(etatv.montant);

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        String querry1 = "SELECT " +
                "bon1.NUM_BON, " +
                "BON1.REMISE, " +
                "BON1.TOT_HT - BON1.REMISE AS MONTANT_BON_HT, " +
                "BON1.VERSER, " +
                "BON1.MONTANT_ACHAT AS TOT_ACHAT, " +
                "(BON1.TOT_HT - BON1.REMISE) -  BON1.MONTANT_ACHAT AS BENIFICE " +
                "FROM BON1 WHERE (BON1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "')";


        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getDouble(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_HT"));
                etatv2.total_versement = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
               // etatv2.vers_client = cursor1.getString(cursor1.getColumnIndex("VERSEMENTS"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_verser = total_verser + etatv2.total_versement ;
                tot_remise = tot_remise + etatv2.total_remise;
                total_benifice = total_benifice + etatv2.benifice;

            } while (cursor1.moveToNext());
        }

        String querry2 = "SELECT CARNET_C.VERSEMENTS FROM CARNET_C WHERE (CARNET_C.DATE_CARNET BETWEEN '"+ from_d +"' AND '" + to_d + "')";

        Cursor cursor2 = db.rawQuery(querry2, null);
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {

                PostData_Carnet_c etatv2 = new PostData_Carnet_c();

                etatv2.carnet_versement = cursor2.getDouble(cursor2.getColumnIndex("VERSEMENTS"));

                total_verser_c = total_verser_c + etatv2.carnet_versement;


            } while (cursor2.moveToNext());
        }
        tot_credit = tot_montant_total - (total_verser + total_verser_c);
        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL REMISE:";
        etatv.montant = tot_remise;

        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CHIFFRE D'AFFAIRE :";
        etatv.montant = tot_montant_total;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSER :";
        etatv.montant = total_verser+total_verser_c;

        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOTAL :";
        etatv.montant = tot_credit;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);


        if(show_benifice){
            etatv = new PostData_Etatv();
            etatv.produit = "BENIFICE TOTAL :";
            etatv.montant = total_benifice;
            etatv.code_parent = "-6";
            all_etatv.add(etatv);
        }


        double objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        objectif = prefs.getLong("OBJECTIF_MONTANT", 0);
        if(tot_montant_total < (objectif / 2) ){
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else if(objectif > tot_montant_total){
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else{
            etatv = new PostData_Etatv();
            etatv.produit = "N3";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }

        return all_etatv;
    }

    public void ResetPda(){
        mContext.deleteDatabase(DATABASE_NAME);
    }

    public File copyAppDbToDownloadFolder(String imei) {
        try {
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "backup_distribute_"+imei); // for example "my_data_backup.db"
            File currentDB = mContext.getDatabasePath(DATABASE_NAME); //databaseName=your current application database name, for example "my_data.db"
            if (currentDB.exists()) {
                FileInputStream fis = new FileInputStream(currentDB);
                FileOutputStream fos = new FileOutputStream(backupDB);
                fos.getChannel().transferFrom(fis.getChannel(), 0, fis.getChannel().size());
                // or fis.getChannel().transferTo(0, fis.getChannel().size(), fos.getChannel());
                fis.close();
                fos.close();
                Log.i("TRACKKK", " copied to download folder");
                return backupDB;
            } else {
                Log.i("TRACKKK", " fail, database not found");
                return null;
            }
        } catch (IOException e) {
            Log.d("TRACKKK", "fail, reason:", e);
            return null;
        }
    }



    /////////////////////////////////////////////// INVENTAIRE ////////////////////////////////////////////////
    //=============================== FUNCTION TO INSERT INTO Inventaires2 TABLE ===============================
    public boolean insert_into_Inv1(String _table, PostData_Inv1 inv1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                boolean inv1_exist = false;
                Cursor cursor0 = db.rawQuery("SELECT NUM_INV FROM "+ _table +" WHERE NUM_INV = '"+inv1.num_inv+"' ", null);
                // looping through all rows and adding to list
                if (cursor0.moveToFirst()) {
                    do {
                        inv1_exist = true;
                    } while (cursor0.moveToNext());
                }

                if(inv1_exist){

                    //update_inv1
                    ContentValues args1 = new ContentValues();
                    args1.put("LIBELLE", inv1.nom_inv);
                    String selection1 = "NUM_INV=?";
                    String[] selectionArgs1 = {inv1.num_inv};
                    db.update(_table, args1, selection1, selectionArgs1);

                }else {
                    ContentValues values = new ContentValues();
                    values.put("LIBELLE", inv1.nom_inv);
                    values.put("NUM_INV", inv1.num_inv);
                    values.put("DATE_INV", inv1.date_inv);
                    values.put("HEURE_INV", inv1.heure_inv);
                    values.put("CODE_DEPOT", inv1.code_depot);
                    values.put("IS_EXPORTED", 0);
                    values.put("BLOCAGE", "N");
                    values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    db.insert(_table, null, values);

                }

                db.setTransactionSuccessful();

                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Inventaires2 TABLE ===============================
    public void insert_into_inventaire2(PostData_Inv2 inv2s){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("CODE_BARRE", inv2s.codebarre);
                values.put("NUM_INV", inv2s.num_inv);
                values.put("PRODUIT", inv2s.produit);
                values.put("NBRE_COLIS", inv2s.nbr_colis);
                values.put("COLISSAGE", inv2s.colissage);
                values.put("PA_HT", inv2s.pa_ht);
                values.put("QTE", inv2s.qte_theorique);
                values.put("QTE_NEW", inv2s.qte_physique + inv2s.vrac);
                values.put("CODE_DEPOT", inv2s.code_depot);
                values.put("VRAC", inv2s.vrac);
                db.insert("INV2", null, values);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }

    }

    public String format_num_bon(String number, Integer length){
        String _number = number;
        while(_number.length() < length){
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }

    //============================== FUNCTION SELECT FROM inventaire1 ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Inv1> select_list_inventaire_from_database(String querry){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PostData_Inv1> inventaires1s = new ArrayList<>();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Inv1 inv = new PostData_Inv1();
                inv.num_inv = cursor.getString(cursor.getColumnIndex("NUM_INV"));
                inv.date_inv = cursor.getString(cursor.getColumnIndex("DATE_INV"));
                inv.heure_inv = cursor.getString(cursor.getColumnIndex("HEURE_INV"));
                inv.nom_inv = cursor.getString(cursor.getColumnIndex("LIBELLE"));
                inv.utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                inv.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                inv.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));
                inv.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                inv.nbr_produit = cursor.getInt(cursor.getColumnIndex("NBR_PRODUIT"));
                inv.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));
                inv.date_export_inv = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_INV"));
                inventaires1s.add(inv);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return inventaires1s;
    }


    @SuppressLint("Range")
    public PostData_Inv1 select_inventaire_from_database(String querry){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Inv1 inventaires1 = new PostData_Inv1();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                inventaires1.num_inv = cursor.getString(cursor.getColumnIndex("NUM_INV"));
                inventaires1.date_inv = cursor.getString(cursor.getColumnIndex("DATE_INV"));
                inventaires1.heure_inv = cursor.getString(cursor.getColumnIndex("HEURE_INV"));
                inventaires1.nom_inv = cursor.getString(cursor.getColumnIndex("LIBELLE"));
                inventaires1.utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                inventaires1.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                inventaires1.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));
                inventaires1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                inventaires1.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));
                inventaires1.date_export_inv = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_INV"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return inventaires1;
    }

    //============================== FUNCTION SELECT FROM Inventaire2 TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Inv2> select_inventaire2_from_database(String querry){
        ArrayList<PostData_Inv2> all_inv2 = new ArrayList<PostData_Inv2>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {

            do {

                PostData_Inv2 inv2 = new PostData_Inv2();
                inv2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                inv2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                inv2.num_inv = cursor.getString(cursor.getColumnIndex("NUM_INV"));
                inv2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                inv2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                inv2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                inv2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                inv2.qte_theorique = cursor.getDouble(cursor.getColumnIndex("QTE"));
                //inv2.q = cursor.getDouble(cursor.getColumnIndex("QTE_TMP"));
                inv2.qte_physique = cursor.getDouble(cursor.getColumnIndex("QTE_NEW"));
                inv2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                inv2.vrac = cursor.getDouble(cursor.getColumnIndex("VRAC"));
                inv2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));

                all_inv2.add(inv2);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return all_inv2;
    }


    //==============================================================================================
    /////////////////////////////////////////////// ACHAT //////////////////////////////////////////
    //=============================== FUNCTION TO INSERT INTO Achats1 TABLE ===============================
    public boolean insert_into_achat1(String _table, PostData_Achat1 achat1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            boolean bon1_exist = false;
            Cursor cursor0 = db.rawQuery("SELECT NUM_BON FROM "+ _table +" WHERE NUM_BON = '"+achat1.num_bon+"' ", null);
            // looping through all rows and adding to list
            if (cursor0.moveToFirst()) {
                do {
                    bon1_exist = true;
                } while (cursor0.moveToNext());
            }

            if(bon1_exist){

                //update_achat1
                ContentValues args1 = new ContentValues();
                args1.put("CODE_FRS", achat1.code_frs);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {achat1.num_bon};
                db.update(_table, args1, selection1, selectionArgs1);

            }else {
                ContentValues values = new ContentValues();
                values.put("NUM_BON", achat1.num_bon);
                values.put("CODE_FRS", achat1.code_frs);
                values.put("DATE_BON", achat1.date_bon);
                values.put("HEURE", achat1.heure);
                values.put("CODE_DEPOT", achat1.code_depot);
                values.put("NBR_P", 0);
                values.put("BLOCAGE", "N");
                values.put("IS_EXPORTED", 0);
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"");
                db.insert(_table, null, values);

            }

            db.setTransactionSuccessful();
            executed =  true;
            db.endTransaction();
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }
    //=============================== FUNCTION TO INSERT INTO Achats2 TABLE ===============================
    public boolean insert_into_achat2(String table, PostData_Achat2 achat2){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("NUM_BON", achat2.num_bon);
                values.put("CODE_BARRE", achat2.codebarre);
                values.put("PRODUIT", achat2.produit);
                values.put("NBRE_COLIS", achat2.nbr_colis);
                values.put("COLISSAGE", achat2.colissage);
                values.put("QTE", achat2.qte);
                values.put("QTE_GRAT", achat2.gratuit);
                values.put("PA_HT", achat2.pa_ht);
                values.put("TVA", achat2.tva);
                values.put("CODE_DEPOT", achat2.code_depot);

                db.insert(table, null, values);

                if(table.equals("ACHAT2")){
                    update_Stock_Produit_achat("ACHAT2_INSERT", achat2, 0.0, 0.0);
                }
                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //============================== FUNCTION SELECT FROM Inventaire2 TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat2> select_all_achat2_from_database(String querry){
        ArrayList<PostData_Achat2> all_achat2 = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        if (cursor.moveToFirst()) {
            do {

                PostData_Achat2 achat2 = new PostData_Achat2();
                achat2.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                achat2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                achat2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                achat2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                achat2.nbr_colis = cursor.getDouble(cursor.getColumnIndex("NBRE_COLIS"));
                achat2.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                achat2.qte = cursor.getDouble(cursor.getColumnIndex("QTE"));
                achat2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                achat2.pamp_produit = cursor.getDouble(cursor.getColumnIndex("PAMP"));
                achat2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                achat2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                achat2.gratuit = cursor.getDouble(cursor.getColumnIndex("QTE_GRAT"));

                all_achat2.add(achat2);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return all_achat2;
    }


    //============================== FUNCTION SELECT FROM code_barre TABLE ===============================
    @SuppressLint("Range")
    public PostData_Codebarre select_produit_codebarre(String scan_result){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Codebarre codebarre = new PostData_Codebarre();
        codebarre.exist = false;
        String querry  = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + scan_result + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                codebarre.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                codebarre.exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return codebarre;
    }

    //================================== UPDATE TABLE (Inventaires2) =======================================
    public boolean Update_inventaire2(PostData_Inv2 _inv2){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args1 = new ContentValues();
                args1.put("NBRE_COLIS", _inv2.nbr_colis);
                args1.put("COLISSAGE", _inv2.colissage);
                args1.put("QTE_NEW", _inv2.qte_physique + _inv2.vrac);
                args1.put("VRAC", _inv2.vrac);

                String selection1 = "RECORDID=? AND NUM_INV=?";
                String[] selectionArgs1 = {String.valueOf(_inv2.recordid), _inv2.num_inv};
                db.update("INV2", args1, selection1, selectionArgs1);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Inventaires1) =======================================
    public boolean Update_inventaire1(String num_inv){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                ContentValues args = new ContentValues();
                args.put("IS_EXPORTED", 1);
                args.put("DATE_EXPORT_INV", date);
                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.update("INV1", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean delete_inventaire_group(String num_inv){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.delete("INV1", selection, selectionArgs);
                db.delete("INV2", selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }

    public boolean delete_transfert(){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("TRANSFERT1", null, null);
                db.delete("TRANSFERT2", null, null);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }

    public boolean delete_fournisseur(String code_frs){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_FRS=?";
                String[] whereArgs = {code_frs};
                db.delete("FOURNIS", whereClause, whereArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }

    public boolean delete_client(String code_client){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_CLIENT=?";
                String[] whereArgs = {code_client};
                db.delete("CLIENT", whereClause, whereArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
        return executed;
    }

    public void delete_produit(String code_barre){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_BARRE=?";
                String[] whereArgs = {code_barre};
                db.delete("PRODUIT", whereClause, whereArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
            executed =  false;
        }
    }

    public boolean validate_bon1_sql(String _table, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                 args.put("ANCIEN_SOLDE", bon1.solde_ancien);
                 args.put("VERSER", bon1.verser);
                 args.put("MODE_RG", bon1.mode_rg);
                 args.put("BLOCAGE", "F");
                 args.put("LATITUDE", bon1.latitude);
                 args.put("LONGITUDE",bon1.longitude);
                 args.put("DATE_F",bon1.date_f);
                 args.put("HEURE_F",bon1.heure_f);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {bon1.num_bon};
                db.update(_table, args, selection, selectionArgs);

                if(_table.equals("BON1")){
                         db.execSQL("UPDATE CLIENT SET ACHATS = ACHATS + " + ( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) + ", VERSER = VERSER + " + bon1.verser + ", SOLDE = SOLDE + " + (( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client+"'");
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean validate_achat1_sql(String _table, PostData_Achat1 achat1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("ANCIEN_SOLDE", achat1.solde_ancien);
                args.put("VERSER", achat1.verser);
                args.put("MODE_RG", achat1.mode_rg);
                args.put("BLOCAGE", "F");
               // args.put("LATITUDE", achat1.latitude);
               // args.put("LONGITUDE",achat1.longitude);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {achat1.num_bon};
                db.update(_table, args, selection, selectionArgs);

                if(_table.equals("ACHAT1")){
                    // you have to ini achat, verser, solde to 0 first time 
                    String sql = "UPDATE FOURNIS SET ACHATS = ACHATS + " + ( achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise ) + ", VERSER = VERSER + " + achat1.verser + ", SOLDE = SOLDE + " + (( achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise ) - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs+"'";
                    db.execSQL(sql);
                }

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean validate_inv1_sql(String _table, String num_inv){

        boolean executed = false;

        try {

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("BLOCAGE", "F");

                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean modifier_achat1_sql(String _table, PostData_Achat1 achat1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("BLOCAGE", "M");

                String selection = "NUM_BON=?";
                String[] selectionArgs = {achat1.num_bon};
                db.update(_table, args, selection, selectionArgs);

                if(_table.equals("ACHAT1")){
                    db.execSQL("UPDATE FOURNIS SET ACHATS = ACHATS - " + ( achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise ) + ", VERSER = VERSER - " + achat1.verser + ", SOLDE = SOLDE - " + (( achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise ) - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs+ "'");
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean modifier_bon1_sql(String _table, String num_bon, PostData_Bon1 bon1){
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("BLOCAGE", "M");

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                if(_table.equals("BON1")){
                    db.execSQL("UPDATE CLIENT SET ACHATS = ACHATS - " + ( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) + ", VERSER = VERSER - " + bon1.verser + ", SOLDE = SOLDE - " + (( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client+"'");
                }

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean modifier_inv1_sql(String _table, String num_inv){

        boolean executed = false;

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("BLOCAGE", "M");

                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed =  true;

            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }

        return executed;
    }

    public boolean backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    private boolean insert_wilaya_commune_into_database( SQLiteDatabase db){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("sql_script/script_communes_wilaya.txt"), StandardCharsets.UTF_8))) {
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                db.execSQL(mLine);
            }
            return true;
        } catch (IOException e) {
            return false;
        }
        //log the exception
    }

}
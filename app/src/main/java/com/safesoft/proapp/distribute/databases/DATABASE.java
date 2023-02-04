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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.safesoft.proapp.distribute.postData.PostData_Position;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
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

import java.util.ArrayList;

/**
 * Created by UK2015 on 21/08/2016.
 */
public class DATABASE extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6; // Database version
    private static final String DATABASE_NAME = "safe_dist_p"; //Database name
    private final Context mContext;

    private final String PREFS_AUTRE = "ConfigAutre";

    //Constructor DATABASE 2
    public DATABASE(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub

        mContext = context;
       // String path = mContext.getDatabasePath(DATABASE_NAME).getPath();
      //  File f = mContext.getDatabasePath(DATABASE_NAME);
      // context.deleteDatabase("safe_distribute");
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        Log.v("TRACKKK","================>  ONCREATE EXECUTED");


        db.execSQL("CREATE TABLE IF NOT EXISTS Produit(PRODUIT_ID INTEGER PRIMARY KEY  AUTOINCREMENT, " +
                "CODE_BARRE VARCHAR , REF_PRODUIT VARCHAR, PRODUIT VARCHAR, PA_HT DOUBLE, TVA DOUBLE,  " +
                "PV1_HT DOUBLE, PV2_HT DOUBLE, PV3_HT DOUBLE , STOCK DOUBLE, COLISSAGE DOUBLE, PHOTO BLOB, DETAILLE VARCHAR, " +
                "DESTOCK_TYPE VARCHAR, DESTOCK_CODE_BARRE VARCHAR, DESTOCK_QTE DOUBLE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Codebarre(CODEBARRE_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE_SYN VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS COMPOSANT(COMPOSANT_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE2 VARCHAR, QTE DOUBLE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Bon1(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_CLIENT VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
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
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Bon2(RECORDID INTEGER PRIMARY KEY, " +
                "CODE_BARRE VARCHAR, " +
                "NUM_BON VARCHAR , " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE_GRAT DOUBLE , " +
                "QTE DOUBLE , " +
                "PV_HT DOUBLE, " +
                "DESTOCK_TYPE VARCHAR, " +
                "DESTOCK_CODE_BARRE VARCHAR, " +
                "DESTOCK_QTE DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR, " +
                "PA_HT DOUBLE )");



        db.execSQL("CREATE TABLE IF NOT EXISTS Transfer1(NUM_BON VARCHAR PRIMARY KEY, " +
                "DATE_BON VARCHAR, " +
                "CODE_DEPOT_SOURCE VARCHAR, " +
                "NOM_DEPOT_SOURCE VARCHAR, " +
                "CODE_DEPOT_DEST VARCHAR, " +
                "NOM_DEPOT_DEST VARCHAR, " +
                "NBR_P VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Transfer2(TRANSFER2_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_BON VARCHAR, " +
                "CODE_BARRE VARCHAR ," +
                "PRODUIT VARCHAR," +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE DOUBLE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Client(CLIENT_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_CLIENT VARCHAR, CLIENT VARCHAR , TEL VARCHAR, ADRESSE VARCHAR, MODE_TARIF VARCHAR, LATITUDE REAL, LONGITUDE REAL, ACHATS DOUBLE, VERSER DOUBLE, SOLDE DOUBLE , RC VARCHAR, IFISCAL VARCHAR, AI VARCHAR,  NIS VARCHAR, CREDIT_LIMIT DOUBLE)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Bon1_temp(" +
                "RECORDID INTEGER, " +
                "NUM_BON VARCHAR PRIMARY KEY, " +
                "CODE_CLIENT VARCHAR, " +
                "DATE_BON VARCHAR, " +
                "HEURE VARCHAR, " +
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
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS Bon2_temp(RECORDID INTEGER PRIMARY KEY, " +
                "CODE_BARRE VARCHAR, " +
                "NUM_BON VARCHAR , " +
                "PRODUIT VARCHAR , " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "QTE_GRAT DOUBLE , " +
                "QTE DOUBLE , " +
                "PV_HT DOUBLE, " +
                "DESTOCK_TYPE VARCHAR, " +
                "DESTOCK_CODE_BARRE VARCHAR, " +
                "DESTOCK_QTE DOUBLE, " +
                "TVA DOUBLE, " +
                "CODE_DEPOT VARCHAR, " +
                "PA_HT DOUBLE )");


        db.execSQL("CREATE TABLE IF NOT EXISTS Carnet_c(RECORDID INTEGER PRIMARY KEY, " +
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
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS Inv1(NUM_INV VARCHAR PRIMARY KEY, DATE_INV VARCHAR, HEURE_INV VARCHAR, LIBELLE VARCHAR, NBR_PRODUIT VARCHAR, UTILISATEUR VARCHAR, CODE_DEPOT VARCHAR, IS_SENT BOOLEAN CHECK (IS_SENT IN (0,1)), DATE_EXPORT_INV VARCHAR, BLOCAGE VARCHAR, EXPORTATION VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Inv2(RECORDID INTEGER PRIMARY KEY, CODE_BARRE VARCHAR , NUM_INV VARCHAR, PRODUIT VARCHAR, NBRE_COLIS DOUBLE, COLISSAGE DOUBLE, PA_HT DOUBLE, QTE DOUBLE, QTE_TMP DOUBLE, QTE_NEW DOUBLE, TVA DOUBLE, VRAC VARCHAR, CODE_DEPOT VARCHAR )");

        db.execSQL("CREATE TABLE IF NOT EXISTS Achats1(ACHAT1ID INTEGER, NUM_ACHAT VARCHAR PRIMARY KEY, NOM_ACHAT VARCHAR , DATE_ACHAT VARCHAR, HEURE_ACHAT VARCHAR, UTILISATEUR VARCHAR, CODE_DEPOT VARCHAR, IS_SENT BOOLEAN CHECK (IS_SENT IN (0,1)), DATE_EXPORT_ACHAT VARCHAR)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Achats2(ACHAT2ID VARCHAR PRIMARY KEY, CODE_BARRE VARCHAR, REF_PRODUIT VARCHAR, NUM_ACHAT VARCHAR, PRODUIT VARCHAR, PA_HT DOUBLE, QTE DOUBLE, TVA DOUBLE, CODE_DEPOT VARCHAR )");
        db.execSQL("CREATE TABLE IF NOT EXISTS Position(POSITION_ID INTEGER PRIMARY KEY AUTOINCREMENT, LAT DOUBLE, LONGI DOUBLE, ADRESS VARCHAR, COLOR BOOLEAN CHECK (COLOR IN (0,1)), CLIENT VARCHAR, NUM_BON VARCHAR)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.v("TRACKKK","================>  ON UPGRADE EXECUTED");


      String[] list_requet = new String[1];

     // list_requet[0] = "ALTER TABLE Bon1 ADD COLUMN IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0";




        for(int i = 0; i< list_requet.length; i++){
            try
            {
                db.execSQL(list_requet[i]);
            }
            catch (SQLiteException e)
            {
                Log.v("TRACKKK", "Failed to create column [{0}]. Most likely it already exists, which is fine.");
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////
    //////                             Functions                             //////
    ///////////////////////////////////////////////////////////////////////////////

    //============================== FUNCTION UPDATE table produit =================================
    public Boolean ExecuteTransactionTrasfer(ArrayList<PostData_Transfer1> transfer1s, ArrayList<PostData_Transfer2> transfer2s){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                for(int i = 0;i< transfer1s.size(); i++){
                    Insert_into_trasfer1(db,transfer1s.get(i));
                }

                for(int j = 0;j< transfer2s.size(); j++){
                    Insert_into_trasfer2(db,transfer2s.get(j));
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

    //============================== FUNCTION table client =================================
    public Boolean ExecuteTransactionClient(ArrayList<PostData_Client> clients){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("Client", null , null);

                for(int i = 0;i< clients.size(); i++){

                        ContentValues values = new ContentValues();
                        values.put("CODE_CLIENT", clients.get(i).code_client);
                        values.put("CLIENT", clients.get(i).client);
                        values.put("TEL", clients.get(i).tel);
                        values.put("ADRESSE", clients.get(i).adresse);
                        values.put("RC", clients.get(i).rc);
                        values.put("IFISCAL", clients.get(i).ifiscal);
                        values.put("NIS", clients.get(i).nis);
                        values.put("AI", clients.get(i).ai);
                        values.put("MODE_TARIF", clients.get(i).mode_tarif);
                        values.put("ACHATS", clients.get(i).achat_montant);
                        values.put("VERSER", clients.get(i).verser_montant);
                        values.put("SOLDE", clients.get(i).solde_montant);
                        values.put("LATITUDE", clients.get(i).latitude);
                        values.put("LONGITUDE", clients.get(i).longitude);
                        values.put("CREDIT_LIMIT", clients.get(i).credit_limit);
                        db.insert("Client", null, values);
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
    public boolean Insert_into_client(PostData_Client client){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("CODE_CLIENT", client.code_client);
                values.put("CLIENT", client.client);
                values.put("TEL", client.tel);
                values.put("ADRESSE", client.adresse);
                values.put("MODE_TARIF", client.mode_tarif);
                values.put("RC", client.rc);
                values.put("IFISCAL", client.ifiscal);
                values.put("AI", client.ai);
                values.put("NIS", client.nis);
                values.put("ACHATS", "0.00");
                values.put("VERSER", "0.00");
                values.put("SOLDE", "0.00");

                db.insert("Client", null, values);

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
    public boolean Insert_into_position(PostData_Position position){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("LAT", position.lat);
                values.put("LONGI", position.longi);
                values.put("ADRESS", position.adresse);
                values.put("COLOR", position.color);
                values.put("NUM_BON", position.num_bon);
                values.put("CLIENT", position.client);

                db.insert("Position", null, values);

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


    public void Get_max_code_client(){

        SQLiteDatabase db = this.getWritableDatabase();

        String querry  = "SELECT MAX(CODE_CLIENT) AS MAX_C FROM Client";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
              //  max = cursor.getString(cursor.getColumnIndex("MAX_C"));
            } while (cursor.moveToNext());
        }

    }

    //=============================== FUNCTION TO INSERT INTO Produits TABLE ===============================
    public void Insert_into_produit(PostData_Produit produit){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("CODE_BARRE", produit.code_barre);
        values.put("REF_PRODUIT", produit.ref_produit);
        values.put("PRODUIT", produit.produit);
        values.put("PA_HT", produit.pa_ht);
        values.put("TVA", produit.tva);
        values.put("PV1_HT", produit.pv1_ht);
        values.put("PV2_HT", produit.pv2_ht);
        values.put("PV3_HT", produit.pv3_ht);
        values.put("STOCK", produit.stock);
        values.put("COLISSAGE", produit.colissage);
        values.put("DESTOCK_TYPE", produit.destock_type);
        values.put("DESTOCK_CODE_BARRE", produit.destock_code_barre);
        values.put("DESTOCK_QTE", produit.destock_qte);
        values.put("PHOTO", produit.photo);
        db.insert("Produit", null, values);
    }


    //============================== FUNCTION table client =================================
    public Boolean ExecuteTransactionProduit(ArrayList<PostData_Produit> produits, ArrayList<PostData_Codebarre> codebarres){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("Produit", null , null);

                for(int i = 0;i< produits.size(); i++){

                    ContentValues values = new ContentValues();
                    values.put("CODE_BARRE", produits.get(i).code_barre);
                    values.put("REF_PRODUIT", produits.get(i).ref_produit);
                    values.put("PRODUIT", produits.get(i).produit);
                    values.put("PA_HT", produits.get(i).pa_ht);
                    values.put("TVA", produits.get(i).tva);
                    values.put("PV1_HT", produits.get(i).pv1_ht);
                    values.put("PV2_HT", produits.get(i).pv2_ht);
                    values.put("PV3_HT", produits.get(i).pv3_ht);
                    values.put("STOCK", produits.get(i).stock);
                    values.put("COLISSAGE", produits.get(i).colissage);
                    values.put("PHOTO", produits.get(i).photo);
                    values.put("DETAILLE", produits.get(i).DETAILLE);
                    values.put("DESTOCK_TYPE", produits.get(i).destock_type);
                    values.put("DESTOCK_CODE_BARRE", produits.get(i).destock_code_barre);
                    values.put("DESTOCK_QTE", produits.get(i).destock_qte);

                    db.insert("Produit", null, values);
                }


                db.delete("Codebarre", null , null);

                for(int h=0; h < codebarres.size(); h++){

                    ContentValues values = new ContentValues();
                    values.put("CODE_BARRE", codebarres.get(h).code_barre);
                    values.put("CODE_BARRE_SYN", codebarres.get(h).code_barre_syn);
                    db.insert("Codebarre", null, values);

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
    public void Insert_into_trasfer1(SQLiteDatabase db, PostData_Transfer1 transfer1){
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
        db.insert("Transfer1", null, values);
    }

    //=============================== FUNCTION TO INSERT INTO Transfer2 TABLE ===============================
    public void Insert_into_trasfer2(SQLiteDatabase db, PostData_Transfer2 transfer2){
        if(db == null)
            db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NUM_BON", transfer2.num_bon);
        values.put("CODE_BARRE", transfer2.code_barre);
        values.put("PRODUIT", transfer2.produit);
        values.put("QTE", transfer2.qte);
        values.put("NBRE_COLIS", transfer2.nbr_colis);
        values.put("COLISSAGE", transfer2.colissage);
        db.insert("Transfer2", null, values);
    }



    //=============================== FUNCTION TO INSERT INTO Codebarre TABLE ===============================
    public void Insert_into_codebarre(PostData_Codebarre codebarre){

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("CODE_BARRE", codebarre.code_barre);
            values.put("CODE_BARRE_SYN", codebarre.code_barre_syn);
            db.insert("Codebarre", null, values);
    }


    //=============================== FUNCTION TO INSERT INTO Carnet_c TABLE ===============================
    @SuppressLint("Range")
    public boolean Insert_into_carnet_c(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        Boolean executed = false;
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
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"");



                db.insert("Carnet_c", null, values);


                //update_client

                ContentValues args = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args.put("SOLDE",  String.valueOf(val_nouveau_solde_client));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {carnet_c.code_client.toString()};
                db.update("Client", args, selection, selectionArgs);


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
    public PostData_Produit check_product_if_exist(String code_barre){
        SQLiteDatabase db = this.getWritableDatabase();

        PostData_Produit produit = new PostData_Produit();
        produit.exist = false;
        String querry  = "SELECT STOCK FROM PRODUIT WHERE CODE_BARRE = '" + code_barre.replace("'","''") + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                produit.exist = true;
            } while (cursor.moveToNext());
        }
        return produit;
    }


    public Boolean check_client_if_exist(String  code_client){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;

        String querry  = "SELECT * FROM Client WHERE CODE_CLIENT = '" + code_client + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        return exist;
    }


    public Boolean check_transfer1_if_exist(String  num_bon){
        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;

        String querry  = "SELECT * FROM Transfer1 WHERE NUM_BON = '" + num_bon + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                exist = true;
            } while (cursor.moveToNext());
        }
        return exist;
    }


    //================================== UPDATE TABLE (Inventaires1) =======================================
    public boolean Update_produit(PostData_Produit produit_to_update, String code_barre){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("PRODUIT", produit_to_update.produit);
                args.put("STOCK", produit_to_update.stock);
                args.put("PA_HT", produit_to_update.pa_ht);
                args.put("TVA", produit_to_update.tva);
                args.put("PV1_HT", produit_to_update.pv1_ht);
                args.put("PV2_HT", produit_to_update.pv2_ht);
                args.put("PV3_HT", produit_to_update.pv3_ht);
                args.put("PHOTO", produit_to_update.photo);
                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {code_barre.toString()};
                db.update("Produit", args, selection, selectionArgs);

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


    //================================== UPDATE TABLE (Carnet_c) =======================================
    @SuppressLint("Range")
    public boolean Update_versement(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("VERSEMENTS",  carnet_c.carnet_versement);
                args.put("REMARQUES", carnet_c.carnet_remarque);
                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_c.recordid};
                db.update("Carnet_c", args, selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE",  String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client.toString()};
                db.update("Client", args1, selection1, selectionArgs1);


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
    public boolean Delete_versement(PostData_Carnet_c carnet_c, double val_nouveau_solde_client , double val_nouveau_montant_versement){
        Boolean executed = false;
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
                String[] selectionArgs1 = {carnet_c.code_client.toString()};
                db.update("Client", args1, selection1, selectionArgs1);


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
    public boolean Update_versement_exported(String recordid){
        Boolean executed = false;
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
    public boolean Delete_Codebarre(String code_barre){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {code_barre.toString()};
                db.delete("Codebarre", selection, selectionArgs);

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
        return transfer2s;
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
                client.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                client.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                client.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                client.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                client.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));

                clients.add(client);
            } while (cursor.moveToNext());
        }
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



                carnet_cs.add(carnet_c);
            } while (cursor.moveToNext());
        }
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


            } while (cursor.moveToNext());
        }
        return carnet_c;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Client select_client_from_database(String code_client){

        PostData_Client client = new PostData_Client();;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Client WHERE CODE_CLIENT = '"+code_client+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                client.mode_tarif = cursor.getString(cursor.getColumnIndex("MODE_TARIF"));
                client.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                client.achat_montant = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                client.verser_montant = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                client.solde_montant = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));

                client.rc = cursor.getString(cursor.getColumnIndex("RC"));
                client.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                client.ai = cursor.getString(cursor.getColumnIndex("AI"));
                client.nis = cursor.getString(cursor.getColumnIndex("NIS"));

            } while (cursor.moveToNext());
        }
        return client;
    }
    @SuppressLint("Range")
    public PostData_Client select_client_etat_from_database(String clientt){

        PostData_Client client = new PostData_Client();;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT CODE_CLIENT FROM Client WHERE Client = '"+clientt+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));


            } while (cursor.moveToNext());
        }
        return client;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public String select_ancien_solde_client_from_database(String code_client){

        String ancien_solde = "0.00";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT SOLDE FROM Client WHERE CODE_CLIENT = '"+code_client+"'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ancien_solde = cursor.getString(cursor.getColumnIndex("SOLDE"));

            } while (cursor.moveToNext());
        }
        return ancien_solde;
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
                produit.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                produit.pv1_ht = cursor.getDouble(cursor.getColumnIndex("PV1_HT"));
                produit.pv2_ht = cursor.getDouble(cursor.getColumnIndex("PV2_HT"));
                produit.pv3_ht = cursor.getDouble(cursor.getColumnIndex("PV3_HT"));
                produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                produit.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                produit.stock_colis = cursor.getInt(cursor.getColumnIndex("STOCK_COLIS"));
                produit.stock_vrac = cursor.getInt(cursor.getColumnIndex("STOCK_VRAC"));
                produit.photo = cursor.getBlob(cursor.getColumnIndex("PHOTO"));
                produit.DETAILLE = cursor.getString(cursor.getColumnIndex("DETAILLE"));
                produit.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                produit.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                produit.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                produits.add(produit);
            } while (cursor.moveToNext());
        }


        return produits;
    }


    //============================== FUNCTION SELECT Produits FROM Produit TABLE ===============================
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


        return code_barre;
    }
    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Bon1>  select_vente_from_database(String querry){
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

                bon1s.add(bon1);
            } while (cursor.moveToNext());
        }
        return bon1s;
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

        return bon1;
    }
    //================== GET MAX NUM_BON INTO TABLE Bon1 ===========================
    @SuppressLint("Range")
    public String Select_max_num_bon_vente(String _table){
        String max = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM " + _table + " WHERE NUM_BON IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        return String.valueOf(Integer.valueOf(max) + 1);
    }

    //================== GET MAX NUM_INV INTO TABLE Inventaires1 ===========================
    @SuppressLint("Range")
    public String Select_max_num_inv_inventaire(String _table){
        String max = "0";
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT MAX(NUM_INV) AS max_id FROM " + _table + " WHERE NUM_INV IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        return String.valueOf(Integer.valueOf(max) + 1);
    }

    //=============================== FUNCTION TO INSERT INTO bon1 or bon1_temp TABLE ===============================
    public Boolean Insert_into_bon1(String _table, PostData_Bon1 bon1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                Boolean bon1_exist = false;
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
        public Boolean ExCommande_Export_to_ventes(String _table1, String _table2, PostData_Bon1 bon1, ArrayList<PostData_Bon2> bon2s, String new_num_bon){
        Boolean executed = false;
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
                    values2.put("PV_HT", bon2s.get(i).p_u);
                    values2.put("TVA", bon2s.get(i).tva);
                    values2.put("CODE_DEPOT", bon1.code_depot);
                    values2.put("PA_HT", bon2s.get(i).pa_ht);

                    db.insert(_table2, null, values2);

                    update_Stock_Produit("BON2_INSERT",bon2s.get(i) , 0.0,0.0);
                }


                // Update Bon1_temp
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "V");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {bon1.num_bon.toString()};
                db.update("Bon1_temp", args1, selection1, selectionArgs1);

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
    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Bon1> select(String querry){
        ArrayList<PostData_Bon1> bon1s = new ArrayList<PostData_Bon1>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Bon1 bon1 = new PostData_Bon1();

                bon1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID")); /// CHANGER PAR SMAIL LE 10.05.2022

                bon1s.add(bon1);
            } while (cursor.moveToNext());
        }
        return bon1s;
    }
    //=============================== FUNCTION TO INSERT INTO Inventaires2 TABLE ===============================
    public Boolean Insert_into_bon2(String _table, String num_bon, String code_depot, PostData_Bon2 list_bon2){

        Boolean executed = false;
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
                    values2.put("PV_HT", list_bon2.p_u);
                    values2.put("TVA", list_bon2.tva);
                    values2.put("CODE_DEPOT", code_depot);
                    values2.put("PA_HT", list_bon2.pa_ht);
                    values2.put("DESTOCK_TYPE", list_bon2.destock_type);
                    values2.put("DESTOCK_CODE_BARRE", list_bon2.destock_code_barre);
                    values2.put("DESTOCK_QTE", list_bon2.destock_qte);

                    db.insert(_table, null, values2);

                    if(_table.equals("Bon2")){
                        update_Stock_Produit("BON2_INSERT", list_bon2, 0.0, 0.0);
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
    public Boolean Update_into_bon2( String _table, String num_bon, PostData_Bon2 list_bon2, Double qte_old, Double gratuit_old){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args1 = new ContentValues();
                args1.put("NBRE_COLIS", list_bon2.nbr_colis);
                args1.put("COLISSAGE", list_bon2.colissage);
                args1.put("QTE", list_bon2.qte);
                args1.put("QTE_GRAT", list_bon2.gratuit);
                args1.put("PV_HT", list_bon2.p_u);
                args1.put("TVA", list_bon2.tva);

                String selection1 = "RECORDID=? AND NUM_BON=?";
                String[] selectionArgs1 = {String.valueOf(list_bon2.recordid), num_bon};
                db.update(_table, args1, selection1, selectionArgs1);

                if(_table.equals("Bon2")){
                    update_Stock_Produit("BON2_EDIT", list_bon2, qte_old, gratuit_old);
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
    public Boolean delete_from_bon2(String _table, Integer recordid, String num_bon, PostData_Bon2 bon2){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);

                if(_table.equals("Bon2")){
                    update_Stock_Produit("BON2_DELETE",bon2 , 0.0,0.0);
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
    public Boolean delete_from_inv2(String _table, Integer recordid){
        Boolean executed = false;
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

    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    @SuppressLint("Range")
    public Boolean update_bon1_client(String num_bon, PostData_Bon1 bon1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "F");
                args1.put("ANCIEN_SOLDE", bon1.solde_ancien);
                args1.put("VERSER", bon1.verser);
                if(Double.valueOf(bon1.verser) == 0){
                    args1.put("MODE_RG", "A TERME");
                }else{
                    args1.put("MODE_RG", "ESPECE");
                }
                args1.put("RESTE", bon1.reste);
                args1.put("LATITUDE", bon1.latitude);
                args1.put("LONGITUDE", bon1.longitude);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon.toString()};
                db.update("Bon1", args1, selection1, selectionArgs1);

                // get information client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM Client WHERE CODE_CLIENT='"+ bon1.code_client+ "'", null);
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
                args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) + Double.valueOf(bon1.montant_bon)));
                args.put("VERSER", String.valueOf(Double.valueOf(client.verser_montant) + Double.valueOf(bon1.verser)));
                args.put("SOLDE", String.valueOf((Double.valueOf(client.solde_montant) + Double.valueOf(bon1.montant_bon)) - Double.valueOf(bon1.verser)));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {bon1.code_client.toString()};
                db.update("Client", args, selection, selectionArgs);

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
    public Boolean update_bon1_temp(String num_bon, PostData_Bon1 bon1){
        Boolean executed = false;
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
                String[] selectionArgs1 = {num_bon.toString()};
                db.update("Bon1_temp", args1, selection1, selectionArgs1);


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
    public Boolean update_bon1_client_edit(String num_bon, String code_client, PostData_Bon1 bon1){

        Boolean executed = false;
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
                String[] selectionArgs1 = {num_bon.toString()};
                db.update("Bon1", args1, selection1, selectionArgs1);

                //update_client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM Client WHERE CODE_CLIENT='"+ code_client+ "'", null);
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
                String[] selectionArgs = {bon1.code_client.toString()};
                db.update("Client", args, selection, selectionArgs);

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
    public Boolean update_bon1_temp_edit(String num_bon, String code_client, PostData_Bon1 bon1){

        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "N");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon.toString()};
                db.update("Bon1_temp", args1, selection1, selectionArgs1);


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


    public Boolean Update_ventes_commandes_as_exported(Boolean isTemp, String num_bon){
        Boolean executed = false;
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
                    db.update("Bon1_temp", args1, selection1, selectionArgs1);
                }else{
                    db.update("Bon1", args1, selection1, selectionArgs1);
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

    public boolean check_if_bon1_valide(String _table, String num_bon){
        //check if bon exist
        Boolean bon1_exist = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor0 = db.rawQuery("SELECT NUM_BON FROM " + _table + " WHERE NUM_BON = '"+num_bon+"' AND BLOCAGE = 'F'", null);
        // looping through all rows and adding to list
        if (cursor0.moveToFirst()) {
            do {
                bon1_exist = true;
            } while (cursor0.moveToNext());
        }

        return  bon1_exist;
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
                bon2.p_u = cursor.getDouble(cursor.getColumnIndex("PV_HT"));
                bon2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));
                bon2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                bon2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                bon2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                bon2.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                bon2.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                bon2.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                bon2s.add(bon2);
            } while (cursor.moveToNext());
        }
        return bon2s;
    }


    public boolean update_Stock_Produit( String source, PostData_Bon2 panier,Double qte_old,Double gratuit_old ){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                if(source.equals("BON2_INSERT")||source.equals("BON2_EDIT")){
                    args.put("STOCK", panier.stock_produit - panier.qte - panier.gratuit + qte_old + gratuit_old);
                }else if(source.equals("BON2_DELETE")){
                    args.put("STOCK", panier.stock_produit + panier.qte + panier.gratuit);
                }
                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {panier.codebarre};
                db.update("Produit", args, selection, selectionArgs);
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



    public boolean update_client(Double latitude , Double longitude, String code_client){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("LATITUDE", latitude);
                args.put("LONGITUDE", longitude);
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {code_client.toString()};
                db.update("Client", args, selection, selectionArgs);

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
        Boolean executed = false;
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
                args.put("REMISE", bon1.remise);
                args.put("TIMBRE", bon1.timbre);
                args.put("MODE_RG", bon1.mode_rg);
                args.put("CODE_VENDEUR", bon1.code_vendeur);
                args.put("ANCIEN_SOLDE", bon1.solde_ancien);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon.toString()};
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
    public boolean update_inv1(String _table, PostData_Inv1 inv1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("NUM_INV", inv1.num_inv);
                args.put("DATE_INV", inv1.date_inv);
                args.put("HEURE_INV", inv1.heure_inv);
                args.put("LIBELLE", inv1.nom_inv);
                args.put("NBR_PRODUIT", inv1.nbr_produit);
                args.put("UTILISATEUR", inv1.utilisateur);
                args.put("CODE_DEPOT", inv1.code_depot);
                args.put("IS_SENT", inv1.is_sent);
                args.put("DATE_EXPORT_INV", inv1.date_export_inv);
                args.put("BLOCAGE", inv1.blocage);
                args.put("EXPORTATION", inv1.exportation);

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
    public Boolean delete_bon_vente(Boolean isTemp, PostData_Bon1 bon1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Bon2> bon2_delete = new ArrayList<>();
                String selection;

                if(isTemp){
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {bon1.num_bon};
                    db.delete("Bon1_temp", selection, selectionArgs);
                    db.delete("Bon2_temp", selection, selectionArgs);
                }else{

                    bon2_delete = new ArrayList<>();
                    bon2_delete = select_bon2_from_database("" +
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
                            "FROM Bon2 " +
                            "LEFT JOIN Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                            "WHERE Bon2.NUM_BON = '" + bon1.num_bon + "'" );

                    for(int k=0; k< bon2_delete.size();k++) {

                        ContentValues args = new ContentValues();
                        args.put("STOCK", bon2_delete.get(k).stock_produit + bon2_delete.get(k).qte + bon2_delete.get(k).gratuit);

                         selection = "CODE_BARRE=?";
                        String[] selectionArgsss = {bon2_delete.get(k).codebarre.toString()};
                        db.update("Produit", args, selection, selectionArgsss);
                    }


                    //update_client
                    PostData_Client client = new PostData_Client();
                    Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM Client WHERE CODE_CLIENT='"+ bon1.code_client+ "'", null);
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
                    String[] selectionArgs = {bon1.code_client.toString()};
                    db.update("Client", args, selection, selectionArgs);

                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {bon1.num_bon.toString()};
                    db.delete("Bon1", selection1, selectionArgs1);
                    db.delete("Bon2", selection1, selectionArgs1);
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


    public Boolean delete_bon_en_attente(Boolean isTemp, String num_bon){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if(isTemp){
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon.toString()};
                    db.delete("Bon1_temp", selection, selectionArgs);
                    db.delete("Bon2_temp", selection, selectionArgs);
                }else{

                    ArrayList<PostData_Bon2> bon2_delete = new ArrayList<>();
                    bon2_delete = select_bon2_from_database("" +
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
                            "FROM Bon2 " +
                            "LEFT JOIN  Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                            "WHERE Bon2.NUM_BON = '" + num_bon + "'" );

                    for(int k=0; k< bon2_delete.size();k++) {

                        ContentValues args = new ContentValues();
                        args.put("STOCK", Double.valueOf(bon2_delete.get(k).stock_produit) + Double.valueOf(bon2_delete.get(k).qte) );

                        String selection = "CODE_BARRE=?";
                        String[] selectionArgs = {bon2_delete.get(k).codebarre.toString()};
                        db.update("Produit", args, selection, selectionArgs);
                    }

                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon.toString()};
                    db.delete("Bon1", selection1, selectionArgs1);
                    db.delete("Bon2", selection1, selectionArgs1);
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


    public Boolean delete_bon_after_export(Boolean isTemp, String num_bon){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if(isTemp){
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon.toString()};
                    db.delete("Bon1_temp", selection, selectionArgs);
                    db.delete("Bon2_temp", selection, selectionArgs);
                }else{
                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon.toString()};
                    db.delete("Bon1", selection1, selectionArgs1);
                    db.delete("Bon2", selection1, selectionArgs1);
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



    public Boolean delete_all_bon(Boolean isTemp){
        Boolean executed = false;
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
    public Boolean delete_versement(PostData_Carnet_c carnet_c){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                    //update_client
                    PostData_Client client = new PostData_Client();
                    Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM Client WHERE CODE_CLIENT='"+ carnet_c.code_client+ "'", null);
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
                    String[] selectionArgs1 = {carnet_c.code_client.toString()};
                    db.update("Client", args1, selection1, selectionArgs1);


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
    public ArrayList<PostData_Etatv> select_etatv_from_database(String c_client , String from_d, String to_d){

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<PostData_Etatv>();

        Double tot_qte = 0.00;
        Double tot_montant_par_qte = 0.00;
        Double tot_montant_total = 0.00;
        Double total_verser = 0.00;

        Double tot_remise = 0.00;
        Double tot_credit = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();
        String querry = "SELECT " +
                "Bon2.PRODUIT, " +
                "SUM(Bon2.QTE) TOT_QTE, " +
                "Bon2.PV_HT, " +
                "(SUM(Bon2.QTE) * Bon2.PV_HT) TOTAL_MONTANT_PRODUIT " +
                "FROM Bon2 LEFT JOIN Bon1 ON " +
                "Bon2.NUM_BON = Bon1.NUM_BON " +
                "WHERE (Bon1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') AND Bon1.CODE_CLIENT = '"+c_client+"' " +
                "GROUP BY Bon2.PRODUIT, Bon2.PV_HT " +
                "ORDER BY TOT_QTE DESC";

        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Etatv etatv = new PostData_Etatv();

                etatv.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                etatv.quantite = cursor.getString(cursor.getColumnIndex("TOT_QTE"));
                etatv.montant = cursor.getString(cursor.getColumnIndex("TOTAL_MONTANT_PRODUIT"));
                etatv.pv_ht = cursor.getString(cursor.getColumnIndex("PV_HT"));
                etatv.code_parent = "1";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));

                tot_qte = tot_qte + Double.valueOf(etatv.quantite);
                tot_montant_par_qte = tot_montant_par_qte + Double.valueOf(etatv.montant);

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        String querry1 = "SELECT " +
                "Bon1.REMISE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE AS MONTANT_BON, " +
                "Carnet_c.VERSEMENTS as VERSEMENTS, " +
                "Bon1.VERSER " +
                "FROM Bon1 LEFT JOIN Carnet_c ON Bon1.CODE_CLIENT = Carnet_c.CODE_CLIENT " +
                "WHERE (Bon1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') AND Bon1.CODE_CLIENT = '"+ c_client +"' ";

        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getString(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon = cursor1.getString(cursor1.getColumnIndex("MONTANT_BON"));
                etatv2.total_versement = cursor1.getString(cursor1.getColumnIndex("VERSER"));
                etatv2.code_parent = "-6";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getString(cursor1.getColumnIndex("VERSEMENTS"));

                tot_montant_total = tot_montant_total + Double.valueOf(etatv2.total_par_bon);
                if(etatv2.vers_client == null)
                {
                    total_verser = total_verser + Double.valueOf(etatv2.total_versement) ;
                }else
                {
                    total_verser = total_verser + (Double.valueOf(etatv2.total_versement) + Double.valueOf(etatv2.vers_client)) ;

                }




                tot_remise = tot_remise + Double.valueOf(etatv2.total_remise);

            } while (cursor1.moveToNext());
        }


        tot_credit = Double.valueOf(tot_montant_total) - Double.valueOf(total_verser);
        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL REMISE:";
        etatv.montant = tot_remise.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CHIFFRE D'AFFAIRE :";
        etatv.montant = tot_montant_total.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSER :";
        etatv.montant = total_verser.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOTAL :";
        etatv.montant = tot_credit.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        String objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_AUTRE, mContext.MODE_PRIVATE);
        objectif = prefs.getString("OBJECTIF_MONTANT", "0.00");
        if(Double.valueOf(tot_montant_total) < (Double.valueOf(objectif) / 2) ){
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else if(Double.valueOf(objectif) > Double.valueOf(tot_montant_total)){
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else{
            etatv = new PostData_Etatv();
            etatv.produit = "N3";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }

        return all_etatv;
    }
    @SuppressLint("Range")
    public ArrayList<PostData_Etatv> select_etatv_global_from_database(String from_d, String to_d){

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<PostData_Etatv>();

        Double tot_qte = 0.00;
        Double tot_montant_par_qte = 0.00;
        Double tot_montant_total = 0.00;
        Double total_verser = 0.00;
        Double tot_remise = 0.00;
        Double tot_credit = 0.00;
        Double total_verser_c = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();
        String querry = "SELECT " +
                "Bon2.PRODUIT, " +
                "SUM(Bon2.QTE) TOT_QTE, " +
                "Bon2.PV_HT, " +
                "(SUM(Bon2.QTE) * Bon2.PV_HT) TOTAL_MONTANT_PRODUIT " +
                "FROM Bon2 LEFT JOIN Bon1 ON " +
                "Bon2.NUM_BON = Bon1.NUM_BON " +
                "WHERE (Bon1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') " +
                "GROUP BY Bon2.PRODUIT, Bon2.PV_HT " +
                "ORDER BY TOT_QTE DESC";

        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Etatv etatv = new PostData_Etatv();

                etatv.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                etatv.quantite = cursor.getString(cursor.getColumnIndex("TOT_QTE"));
                etatv.montant = cursor.getString(cursor.getColumnIndex("TOTAL_MONTANT_PRODUIT"));
                etatv.pv_ht = cursor.getString(cursor.getColumnIndex("PV_HT"));
                etatv.code_parent = "1";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));

                tot_qte = tot_qte + Double.valueOf(etatv.quantite);
                tot_montant_par_qte = tot_montant_par_qte + Double.valueOf(etatv.montant);

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        String querry1 = "SELECT " +
                "Bon1.REMISE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE AS MONTANT_BON, " +

                "Bon1.VERSER " +
                "FROM Bon1 " +
                "WHERE (Bon1.DATE_BON BETWEEN '"+ from_d +"' AND '" + to_d + "') ";

        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getString(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon = cursor1.getString(cursor1.getColumnIndex("MONTANT_BON"));
                etatv2.total_versement = cursor1.getString(cursor1.getColumnIndex("VERSER"));
                etatv2.code_parent = "-6";
                // etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
               // etatv2.vers_client = cursor1.getString(cursor1.getColumnIndex("VERSEMENTS"));

                tot_montant_total = tot_montant_total + Double.valueOf(etatv2.total_par_bon);

                    total_verser = total_verser + (Double.valueOf(etatv2.total_versement)) ;


                tot_remise = tot_remise + Double.valueOf(etatv2.total_remise);

            } while (cursor1.moveToNext());
        }
        String querry2 = "SELECT Carnet_c.VERSEMENTS FROM Carnet_c WHERE (Carnet_c.DATE_CARNET BETWEEN '"+ from_d +"' AND '" + to_d + "')";



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
        etatv.montant = tot_remise.toString();

        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CHIFFRE D'AFFAIRE :";
        etatv.montant = tot_montant_total.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSER :";
        etatv.montant = String.valueOf(total_verser+total_verser_c);

        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOTAL :";
        etatv.montant = tot_credit.toString();
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        String objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_AUTRE, mContext.MODE_PRIVATE);
        objectif = prefs.getString("OBJECTIF_MONTANT", "0.00");
        if(tot_montant_total < (Double.parseDouble(objectif) / 2) ){
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else if(Double.parseDouble(objectif) > tot_montant_total){
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }else{
            etatv = new PostData_Etatv();
            etatv.produit = "N3";
            etatv.quantite = objectif.toString();
            etatv.montant = tot_montant_total.toString();
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }

        return all_etatv;
    }


/*
    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    public ArrayList<PostData_Produits> select_data_produit_from_database(String querry){
        ArrayList<PostData_Produits> all_produits = new ArrayList<PostData_Produits>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Produits produit = new PostData_Produits();

                produit.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                produit.reference = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                produit.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                produit.pa_ht = cursor.getString(cursor.getColumnIndex("PA_HT"));
                produit.tva = cursor.getString(cursor.getColumnIndex("TVA"));
                produit.server_exist = (cursor.getInt(cursor.getColumnIndex("SERVER_EXIST")) == 1)? true : false;

                all_produits.add(produit);
            } while (cursor.moveToNext());
        }
        return all_produits;
    }

    //============================== FUNCTION SELECT FROM Produits TABLE ===============================
    public ArrayList<PostData_Achat2> select_data_achat2_from_database(String querry){
        ArrayList<PostData_Achat2> all_achats = new ArrayList<PostData_Achat2>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Achat2 achat2 = new PostData_Achat2();

                achat2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                achat2.reference = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                achat2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                achat2.pa_ht = cursor.getString(cursor.getColumnIndex("PA_HT"));
                achat2.quantite = cursor.getString(cursor.getColumnIndex("QTE"));
                achat2.tva = cursor.getString(cursor.getColumnIndex("TVA"));
                achat2.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));

                all_achats.add(achat2);
            } while (cursor.moveToNext());
        }
        return all_achats;
    }

    //============================== FUNCTION SELECT FROM Produits TABLE ===============================
    public PostData_Produits select_produit_produits(String scan_result){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Produits produit = new PostData_Produits();
        produit.exist = false;
        String querry  = "SELECT * FROM PRODUIT WHERE CODE_BARRE = '" + scan_result + "' OR REF_PRODUIT = '" + scan_result + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                produit.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                produit.reference = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                produit.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                produit.pa_ht = cursor.getString(cursor.getColumnIndex("PA_HT"));
                produit.tva = cursor.getString(cursor.getColumnIndex("TVA"));
                produit.server_exist = (cursor.getInt(cursor.getColumnIndex("SERVER_EXIST")) == 1)? true : false;

                produit.exist = true;
            } while (cursor.moveToNext());
        }
        return produit;
    }



    //============================== FUNCTION SELECT FROM Produits TABLE ===============================
    public PostData_Codebarre select_produit_codebarre(String scan_result){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Codebarre codebarre = new PostData_Codebarre();
        codebarre.exist = false;
        String querry  = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '" + scan_result + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                codebarre.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                codebarre.exist = true;
            } while (cursor.moveToNext());
        }
        return codebarre;
    }

    //============================== FUNCTION SELECT FROM Depot2 TABLE ===============================

    public PostData_Produits select_produit_depot2(String scan_result,String code_depot){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Produits produit = new PostData_Produits();
        produit.exist = false;
        String querry = "SELECT " +
                "D.CODE_BARRE AS C_B, " +
                "PR.REF_PRODUIT AS R_P , " +
                "PR.PRODUIT as P, " +
                "PR.PA_HT as PA, " +
                "PR.TVA as TVA " +
                "FROM Depot2 AS D, Produits AS PR " +
                "WHERE " +
                "CODE_DEPOT = '" + code_depot + "' " +
                "AND ( C_B = '" + scan_result + "' " +
                "OR R_P = '" + scan_result + "') ";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                produit.codebarre = cursor.getString(cursor.getColumnIndex("C_B"));
                produit.reference = cursor.getString(cursor.getColumnIndex("R_P"));
                produit.produit = cursor.getString(cursor.getColumnIndex("P"));
                produit.pa_ht = cursor.getString(cursor.getColumnIndex("PA"));
                produit.tva = cursor.getString(cursor.getColumnIndex("TVA"));
                produit.exist = true;
            } while (cursor.moveToNext());
        }
        return produit;
    }

    //============================== FUNCTION SELECT FROM Depot TABLE ===============================
    public ArrayList<PostData_ListDepots> select_list_depots_from_database(){
        ArrayList<PostData_ListDepots> list_depots = new ArrayList<PostData_ListDepots>();
        SQLiteDatabase db = this.getWritableDatabase();
        String querry = "SELECT * FROM Depot1";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_ListDepots depot = new PostData_ListDepots();
                depot.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                depot.nom_depot = cursor.getString(cursor.getColumnIndex("NOM_DEPOT"));

                list_depots.add(depot);
            } while (cursor.moveToNext());
        }
        return list_depots;
    }



    //============================== FUNCTION SELECT FROM Depot TABLE ===============================
    public ArrayList<PostData_Fournisseur> select_list_fournisseurs_from_database(){
        ArrayList<PostData_Fournisseur> list_fournisseurs = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String querry = "SELECT * FROM Fournisseurs";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Fournisseur fournisseur = new PostData_Fournisseur();
                fournisseur.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                fournisseur.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));

                list_fournisseurs.add(fournisseur);
            } while (cursor.moveToNext());
        }
        return list_fournisseurs;
    }
    //============================== FUNCTION SELECT FROM Menu TABLE ===============================

    public ArrayList<PostData_Fournisseur> select_list_achat_from_database(String querry){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PostData_Fournisseur> fournisseurs = new ArrayList<>();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Fournisseur fournis = new PostData_Fournisseur();

                fournis.num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                fournis.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                fournis.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                fournis.date_bon = cursor.getString(cursor.getColumnIndex("DATE_BON"));
                fournis.heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                fournis.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                fournis.nom_depot = cursor.getString(cursor.getColumnIndex("NOM_DEPOT"));
                fournis.is_sent = cursor.getInt(cursor.getColumnIndex("IS_SENT"));
                fournis.date_export_bon = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_BON"));
                fournisseurs.add(fournis);
            } while (cursor.moveToNext());
        }
        return fournisseurs;
    }
    //UPDATING ... /////////////////////////////////////////////////////////////////////////////////

    //================================== UPDATE TABLE (Inventaires1) =======================================
    public boolean Update_inventaire1(String num_bon){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                ContentValues args = new ContentValues();
                args.put("IS_SENT", 1);
                args.put("DATE_EXPORT_BON", date);
                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon.toString()};
                db.update("Achat1", args, selection, selectionArgs);

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
    //================================== UPDATE TABLE (Inventaires2) =======================================
    public boolean Update_achat2(PostData_Produits _produit){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                args.put("QTE", _produit.qte);
                String selection = "CODE_BARRE=? OR REF_PRODUIT=?";
                String[] selectionArgs = {_produit.codebarre, _produit.reference};
                db.update("Achat2", args, selection, selectionArgs);

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

    public Boolean delete_achat_group(String num_bon){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon.toString()};
                db.delete("Achat1", selection, selectionArgs);
                db.delete("Achat2", selection, selectionArgs);

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


    public String Get_Digits_String(String number, Integer length){
        String _number = number;
        while(_number.length() < length){
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }

    //================== GET MAX NUM_INV INTO TABLE Inventaires1 ===========================
    public String Select_max_num_inv(SQLiteDatabase db){
        String max = "0";
        String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM Achat1 WHERE NUM_BON IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.v("TRACKKK", "size " + cursor.getCount());
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        return max;
    }

    //============================== FUNCTION SEARCH INTO PRODUITS FROM Produits TABLE ===============================
    public ArrayList<PostData_Produits> search_produit(String value_text){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PostData_Produits> produits = new ArrayList<>();
        String querry  = "SELECT * FROM Produits WHERE CODE_BARRE like '" + value_text + "%' OR REF_PRODUIT like '" + value_text + "%' OR PRODUIT like '"+ value_text +"%' ";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Produits produit = new PostData_Produits();
                produit.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                produit.reference = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                produit.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                produit.pa_ht = cursor.getString(cursor.getColumnIndex("PA_TTC"));
                produit.tva = cursor.getString(cursor.getColumnIndex("TVA"));
                produit.server_exist = (cursor.getInt(cursor.getColumnIndex("SERVER_EXIST")) == 1)? true : false;

                produits.add(produit);

            } while (cursor.moveToNext());
        }
        return produits;
    }
*/

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
    public boolean Insert_into_Inv1(String _table, PostData_Inv1 inv1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                Boolean inv1_exist = false;
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
                    values.put("IS_SENT", 0);
                    values.put("BLOCAGE", "N");
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

    //=============================== FUNCTION TO INSERT INTO Inventaires2 TABLE ===============================
    public Boolean Insert_into_inventaire2(PostData_Inv2 inv2s){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("CODE_BARRE", inv2s.codebarre);
              //  values.put("REF_PRODUIT", inv2s.reference);
                values.put("NUM_INV", inv2s.num_inv);
                values.put("PRODUIT", inv2s.produit);
                values.put("NBRE_COLIS", inv2s.nbr_colis);
                values.put("COLISSAGE", inv2s.colissage);
                values.put("PA_HT", inv2s.pa_ht);
                values.put("QTE", inv2s.qte_theorique);
                values.put("QTE_NEW", inv2s.qte_physique + inv2s.vrac);
               // values.put("TVA", inv2s.tva);
                values.put("CODE_DEPOT", inv2s.code_depot);
                values.put("VRAC", inv2s.vrac);
                db.insert("Inv2", null, values);

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

    public String Get_Digits_String(String number, Integer length){
        String _number = number;
        while(_number.length() < length){
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }

    //================== GET MAX NUM_INV INTO TABLE Inventaires1 ===========================
    @SuppressLint("Range")
    public String Select_max_num_inv(SQLiteDatabase db){
        String max = "0";
        String selectQuery = "SELECT MAX(NUM_INV) AS max_id FROM Inventaires1 WHERE NUM_INV IS NOT NULL";
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.v("TRACKKK", "size " + cursor.getCount());
        if(cursor.getCount() > 0 ) {
            if (cursor.moveToFirst()) {
                if(cursor.getString(0) != null){
                    max   = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        return max;
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
                inv.is_sent = cursor.getInt(cursor.getColumnIndex("IS_SENT"));
                inv.date_export_inv = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_INV"));
                inventaires1s.add(inv);
            } while (cursor.moveToNext());
        }
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
                inventaires1.is_sent = cursor.getInt(cursor.getColumnIndex("IS_SENT"));
                inventaires1.date_export_inv = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_INV"));

            } while (cursor.moveToNext());
        }
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
        return all_inv2;
    }


    //==============================================================================================
    /////////////////////////////////////////////// ACHAT //////////////////////////////////////////
    //=============================== FUNCTION TO INSERT INTO Achats1 TABLE ===============================
    public boolean Insert_into_achat1(String date, String heure, String nom_achat, String code_depot){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                String num_achat = Get_Digits_String(String.valueOf(Integer.parseInt(Select_max_num_inv(db)) + 1),6);
                values.put("NUM_ACHAT", num_achat);
                values.put("DATE_ACHAT", date);
                values.put("HEURE_ACHAT", heure);
                values.put("NOM_ACHAT", nom_achat);

                values.put("UTILISATEUR", "NASSER");
                values.put("CODE_DEPOT", code_depot);
                values.put("IS_SENT", 0);
                db.insert("Achats1", null, values);

                db.setTransactionSuccessful();
                executed =  true;
            } finally {
                db.endTransaction();
            }
        }catch (SQLiteDatabaseLockedException sqlilock){
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        Log.v("TRACKKK", "=== >BEFORE");
        ArrayList<PostData_Achat1> bbbb  = select_list_achat_from_database("SELECT * FROM Achats1");
        Log.v("TRACKKK", "=== >AFTER");

        for(int h = 0; h< bbbb.size(); h++){
            Log.v("TRACKKK", bbbb.get(h).num_achat);
        }
        Log.v("TRACKKK", "==> FINALLY");

        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Achats2 TABLE ===============================
    public Boolean Insert_into_achat2(PostData_Achat2 achat2s){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put("CODE_BARRE", achat2s.codebarre);
                values.put("REF_PRODUIT", achat2s.reference);
                values.put("NUM_ACHAT", achat2s.num_achat);
                values.put("PRODUIT", achat2s.produit);
                values.put("PA_HT", achat2s.pa_ht);
                values.put("QTE", achat2s.quantity);
                values.put("TVA", achat2s.tva);
                db.insert("Achats2", null, values);

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


    //============================== FUNCTION SELECT FROM inventaire1 ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat1> select_list_achat_from_database(String querry){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PostData_Achat1> achat1s = new ArrayList<>();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Achat1 achat1 = new PostData_Achat1();
                achat1.num_achat = cursor.getString(cursor.getColumnIndex("NUM_ACHAT"));
                achat1.date_achat = cursor.getString(cursor.getColumnIndex("DATE_ACHAT"));
                achat1.heure_achat = cursor.getString(cursor.getColumnIndex("HEURE_ACHAT"));
                achat1.nom_achat = cursor.getString(cursor.getColumnIndex("NOM_ACHAT"));
                achat1.utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                achat1.code_depot = cursor.getString(cursor.getColumnIndex("CODE_DEPOT"));
                achat1.is_sent = cursor.getInt(cursor.getColumnIndex("IS_SENT"));
                achat1.date_export_achat = cursor.getString(cursor.getColumnIndex("DATE_EXPORT_ACHAT"));
                achat1s.add(achat1);
            } while (cursor.moveToNext());
        }
        return achat1s;
    }


    //============================== FUNCTION SELECT FROM Inventaire2 TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat2> select_achat2_from_database(String querry){
        ArrayList<PostData_Achat2> all_achat2 = new ArrayList<PostData_Achat2>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Achat2 achat2 = new PostData_Achat2();
                achat2.codebarre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                achat2.reference = cursor.getString(cursor.getColumnIndex("REF_PRODUIT"));
                achat2.produit = cursor.getString(cursor.getColumnIndex("PRODUIT"));
                achat2.pa_ht = cursor.getDouble(cursor.getColumnIndex("PA_HT"));
                achat2.quantity = cursor.getDouble(cursor.getColumnIndex("QTE"));
                achat2.tva = cursor.getDouble(cursor.getColumnIndex("TVA"));

                all_achat2.add(achat2);
            } while (cursor.moveToNext());
        }
        return all_achat2;
    }

    //============================== FUNCTION SELECT FROM code_barre TABLE ===============================
    @SuppressLint("Range")
    public PostData_Codebarre select_produit_codebarre(String scan_result){
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Codebarre codebarre = new PostData_Codebarre();
        codebarre.exist = false;
        String querry  = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '" + scan_result + "'";
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                codebarre.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                codebarre.exist = true;
            } while (cursor.moveToNext());
        }
        return codebarre;
    }

    //================================== UPDATE TABLE (Inventaires2) =======================================
    public boolean Update_inventaire2(PostData_Inv2 _inv2, double qte_old, double vrac_old){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                args.put("QTE_NEW", _inv2.qte_physique);
                String selection = "CODE_BARRE=? OR REF_PRODUIT=?";
                String[] selectionArgs = {_inv2.codebarre, _inv2.reference};
                db.update("Inventaires2", args, selection, selectionArgs);

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
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
                ContentValues args = new ContentValues();
                args.put("IS_SENT", 1);
                args.put("DATE_EXPORT_INV", date);
                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv.toString()};
                db.update("Inv1", args, selection, selectionArgs);

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

    public Boolean delete_inventaire_group(String num_inv){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.delete("Inv1", selection, selectionArgs);
                db.delete("Inv2", selection, selectionArgs);

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
    public Boolean delete_transfert(){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("Transfer1", null, null);
                db.delete("Transfer2", null, null);

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

    public boolean validate_bon1_sql(String _table, String num_bon, PostData_Bon1 bon1){
        Boolean executed = false;
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


                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);


               /* ContentValues args2 = new ContentValues();
                args2.put("ACHATS", bon1.solde_ancien);
                args2.put("VERSER", bon1.verser);
                args2.put("SOLDE", bon1.mode_rg);
                String selection2 = "CODE_CLIENT=?";
                String[] selectionArgs2 = {bon1.code_client};
                db.update("Client", args2, selection2, selectionArgs2);*/

                if(_table.equals("Bon1"))
                db.execSQL("UPDATE Client SET ACHATS = ACHATS + " + ( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) + ", VERSER = VERSER + " + bon1.verser + ", SOLDE = SOLDE + " + (( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client+"'");


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

        Boolean executed = false;

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



    public boolean modifier_bon1_sql(String _table, String num_bon, PostData_Bon1 bon1){
        Boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("BLOCAGE", "M");

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                db.execSQL("UPDATE Client SET ACHATS = ACHATS - " + ( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) + ", VERSER = VERSER - " + bon1.verser + ", SOLDE = SOLDE - " + (( bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise ) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client+"'");

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

        Boolean executed = false;

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

}
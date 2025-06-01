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
import android.widget.Toast;

import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_f;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Etatv;
import com.safesoft.proapp.distribute.postData.PostData_Famille;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.postData.PostData_Tournee1;
import com.safesoft.proapp.distribute.postData.PostData_Tournee2;
import com.safesoft.proapp.distribute.postData.PostData_commune;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by UK2015 on 21/08/2016.
 */
public class DATABASE extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 16; // Database version
    public static final String DATABASE_NAME = "safe_distribute_pro"; //Database name
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
        Log.v("TRACKKK", "================>  ONCREATE EXECUTED");

        db.execSQL("CREATE TABLE IF NOT EXISTS FAMILLES(FAMILLE_ID INTEGER PRIMARY KEY AUTOINCREMENT, FAMILLE VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS PRODUIT(PRODUIT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_BARRE VARCHAR UNIQUE, " +
                "REF_PRODUIT VARCHAR UNIQUE, " +
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
                "PV_LIMITE DOUBLE DEFAULT 0, " +
                "STOCK DOUBLE, " +
                "STOCK_INI DOUBLE DEFAULT 0, " +
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
                "QTE_PROMO DOUBLE, " +
                "ISNEW INTEGER " +
                ")"
        );


        db.execSQL("CREATE TABLE IF NOT EXISTS CODEBARRE(CODEBARRE_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE_SYN VARCHAR)");


        db.execSQL("CREATE TABLE IF NOT EXISTS COMPOSANT(COMPOSANT_ID INTEGER PRIMARY KEY AUTOINCREMENT, CODE_BARRE VARCHAR, CODE_BARRE2 VARCHAR, QTE DOUBLE)");

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
                "VERSER DOUBLE, " +
                "LIVRER INTEGER DEFAULT 0, " +
                "DATE_LIV VARCHAR, " +
                "IS_IMPORTED INTEGER DEFAULT 0, " +
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0 " +
                ")"
        );


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
                "CODE_DEPOT VARCHAR"+
                ")"
        );





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
                "VERSER DOUBLE, " +
                "LIVRER INTEGER DEFAULT 0, " +
                "DATE_LIV VARCHAR, " +
                "IS_IMPORTED INTEGER DEFAULT 0, " +
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0 " +
                ")"
        );


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
                "CODE_DEPOT VARCHAR " +
                ")"
        );


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

        db.execSQL("CREATE TABLE IF NOT EXISTS CARNET_F(RECORDID INTEGER PRIMARY KEY, " +
                "CODE_FRS VARCHAR, " +
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

        db.execSQL("CREATE TABLE IF NOT EXISTS POSITION(POSITION_ID INTEGER PRIMARY KEY AUTOINCREMENT, LAT DOUBLE, LONGI DOUBLE, ADRESS VARCHAR, COLOR boolean CHECK (COLOR IN (0,1)), CLIENT VARCHAR, NUM_BON VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS FOURNIS(FOURNIS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_FRS VARCHAR, " +
                "FOURNIS VARCHAR, " +
                "ADRESSE VARCHAR, " +
                "TEL VARCHAR, " +
                "ACHATS DOUBLE DEFAULT 0.0, " +
                "VERSER DOUBLE DEFAULT 0.0, " +
                "SOLDE DOUBLE DEFAULT 0.0, " +
                "LATITUDE REAL DEFAULT 0.0, " +
                "LONGITUDE REAL DEFAULT 0.0, " +
                "RC VARCHAR, " +
                "IFISCAL VARCHAR, " +
                "AI VARCHAR,  " +
                "NIS VARCHAR, " +
                "ISNEW INTEGER)");


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
                "VERSER DOUBLE, " +
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0 " +
                ")"
        );


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
                "CODE_DEPOT VARCHAR " +
                ")"
        );


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
                "VERSER DOUBLE, " +
                "EXPORTATION VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "IS_EXPORTED BOOLEAN CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0 " +
                ")"
        );


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
                "CODE_DEPOT VARCHAR " +
                ")"

        );


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



        db.execSQL("CREATE TABLE IF NOT EXISTS TOURNEE1(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_TOURNEE VARCHAR , " +
                "DATE_TOURNEE VARCHAR , " +
                "NAME_TOURNEE VARCHAR , " +
                "CODE_VENDEUR VARCHAR , " +
                "CODE_DEPOT VARCHAR , " +
                "EXPORTATION VARCHAR , " +
                "NBR_CLIENT INTEGER, " +
                "IS_EXPORTED INTEGER DEFAULT 0.0)");


        db.execSQL("CREATE TABLE IF NOT EXISTS TOURNEE2(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_TOURNEE INTEGER, " +
                "DATE_PASSAGE VARCHAR , " +
                "HEURE_PASSAGE VARCHAR , " +
                "CODE_CLIENT VARCHAR, " +
                "STATUS VARCHAR , " +
                "LATITUDE REAL DEFAULT 0.0, " +
                "LONGITUDE REAL DEFAULT 0.0, " +
                "OBSERVATION VARCHAR, " +
                "IS_NEW INTEGER " +
                ")"
        );


        db.execSQL("CREATE TABLE IF NOT EXISTS WILAYAS (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR NOT NULL, LATITUDE numeric NOT NULL, LONGITUDE numeric NOT NULL )");

        db.execSQL("CREATE TABLE IF NOT EXISTS COMMUNES (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR NOT NULL, POST_CODE VARCHAR NOT NULL, WILAYA_ID INTEGER NOT NULL, LATITUDE numeric NOT NULL, LONGITUDE numeric NOT NULL, foreign key(WILAYA_ID) references WILAYAS(ID) on delete cascade)");

        insert_wilaya_commune_into_database(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        Log.v("TRACKKK", "================>  ON UPGRADE EXECUTED");

        String[] list_requet = new String[17];

        list_requet[0] = "ALTER TABLE CLIENT ADD COLUMN SOLDE_INI DOUBLE DEFAULT 0";
        list_requet[1] = "ALTER TABLE CLIENT ADD COLUMN WILAYA VARCHAR ";
        list_requet[2] = "ALTER TABLE CLIENT ADD COLUMN COMMUNE VARCHAR ";

        list_requet[3] = "CREATE TABLE IF NOT EXISTS CARNET_F(RECORDID INTEGER PRIMARY KEY, " +
                "CODE_FRS VARCHAR, " +
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
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)";

        list_requet[4] = "ALTER TABLE PRODUIT ADD COLUMN STOCK_INI DOUBLE DEFAULT 0 ";

        list_requet[5] = "ALTER TABLE PRODUIT ADD COLUMN PV_LIMITE DOUBLE DEFAULT 0 ";

        list_requet[6] = "ALTER TABLE PRODUIT ADD COLUMN QTE_PROMO DOUBLE";

        list_requet[7] = "DROP TABLE IF EXISTS TOURNEE1";
        list_requet[8] = "DROP TABLE IF EXISTS TOURNEE2";

        list_requet[9]  = "ALTER TABLE BON1 ADD COLUMN LIVRER INTEGER DEFAULT 0 ";

        list_requet[10]  = "ALTER TABLE BON1 ADD COLUMN DATE_LIV VARCHAR ";

        list_requet[11]  = "ALTER TABLE BON1 ADD COLUMN IS_IMPORTED INTEGER DEFAULT 0";

        list_requet[12] = "ALTER TABLE BON1_TEMP ADD COLUMN LIVRER INTEGER DEFAULT 0 ";

        list_requet[13] = "ALTER TABLE BON1_TEMP ADD COLUMN DATE_LIV VARCHAR ";

        list_requet[14] = "ALTER TABLE BON1_TEMP ADD COLUMN IS_IMPORTED INTEGER DEFAULT 0";

        list_requet[15] = "CREATE TABLE IF NOT EXISTS TOURNEE1(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NUM_TOURNEE VARCHAR ," +
                "DATE_TOURNEE VARCHAR ," +
                "NAME_TOURNEE VARCHAR ," +
                "CODE_VENDEUR VARCHAR ," +
                "CODE_DEPOT VARCHAR ," +
                "EXPORTATION VARCHAR ," +
                "NBR_CLIENT INTEGER," +
                "IS_EXPORTED INTEGER DEFAULT 0.0)";

        list_requet[16] = "CREATE TABLE IF NOT EXISTS TOURNEE2(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NUM_TOURNEE INTEGER,"+
                "DATE_PASSAGE VARCHAR ," +
                "HEURE_PASSAGE VARCHAR ," +
                "CODE_CLIENT VARCHAR," +
                "STATUS VARCHAR ," +
                "LATITUDE REAL DEFAULT 0.0," +
                "LONGITUDE REAL DEFAULT 0.0," +
                "OBSERVATION VARCHAR," +
                "IS_NEW INTEGER," +
                "FOREIGN KEY(CODE_CLIENT) REFERENCES CLIENT (CODE_CLIENT) ON DELETE CASCADE," +
                "FOREIGN KEY(NUM_TOURNEE) REFERENCES TOURNEE1 (NUM_TOURNEE) ON DELETE CASCADE)";


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

    public boolean ResetTableCommandClient() {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("BON2_TEMP", null, null);
                db.delete("BON1_TEMP", null, null);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public boolean update_transfered_commande(String num_bon) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                // Update Bon1_temp or Bon2_temp
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};

                db.delete("BON1_TEMP", selection1, selectionArgs1);
                db.delete("BON2_TEMP", selection1, selectionArgs1);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //============================== FUNCTION UPDATE table produit =================================
    public boolean ExecuteTransactionCommandClient(ArrayList<PostData_Bon1> command1s, ArrayList<PostData_Bon2> command2s) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                for (int i = 0; i < command1s.size(); i++) {
                    insert_into_bon1("BON1_TEMP", command1s.get(i));
                }

                for (int j = 0; j < command2s.size(); j++) {
                    insert_into_bon2("BON2_TEMP", command2s.get(j).num_bon, command2s.get(j).code_depot, command2s.get(j));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public boolean TransactionInsertTourneeClient(ArrayList<PostData_Tournee1> tournee1s, ArrayList<PostData_Tournee2> tournee2s) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                for (int i = 0; i < tournee1s.size(); i++) {
                    insert_into_Tournee1(tournee1s.get(i));
                }

                for (int j = 0; j < tournee2s.size(); j++) {
                    insert_into_tournee2(tournee2s.get(j));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    //============================== FUNCTION table fournisseur =================================
    public boolean ExecuteTransactionFournisseur(ArrayList<PostData_Fournisseur> fournisseurs) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("FOURNIS", null, null);

                for (int i = 0; i < fournisseurs.size(); i++) {
                    insert_into_fournisseur(fournisseurs.get(i));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public void insert_into_params(PostData_Params params) {
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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }


    public void update_ftp_params(PostData_Params params) {
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
                    db.update("PARAMS", values, null, null);
                }


                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }


    public boolean insert_into_fournisseur(PostData_Fournisseur fournisseur) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    //============================== FUNCTION table client =================================
    public boolean ExecuteTransactionClient(ArrayList<PostData_Client> clients) {
        boolean executed = false;
        try {

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                db.delete("CLIENT", null, null);

                for (int i = 0; i < clients.size(); i++) {

                    insert_into_client(clients.get(i));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Client TABLE ===============================
    public boolean insert_into_client(PostData_Client client) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    public void ExecuteTransactionInsertIntoRouting(ArrayList<PostData_Client> clients) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                db.delete("ROUTING", null, null);
                for (int i = 0; i < clients.size(); i++) {
                    insert_into_routing(clients.get(i));
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
    }

    public boolean insert_into_routing(PostData_Client client) {
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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        db.close();
        return index != -1;
    }

    //=============================== FUNCTION TO INSERT INTO Produits TABLE ===============================
    public void insert_into_produit(PostData_Produit produit) {

        SQLiteDatabase db = this.getWritableDatabase();

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
        values.put("PV_LIMITE", produit.pv_limite);
        values.put("STOCK", produit.stock);
        values.put("COLISSAGE", produit.colissage);
        values.put("STOCK_INI", produit.stock_ini);
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
        values.put("QTE_PROMO", produit.qte_promo);

        values.put("ISNEW", produit.isNew);

        db.insert("PRODUIT", null, values);

    }


    //=============================== FUNCTION TO INSERT INTO Produits TABLE ===============================

    public boolean ExecuteTransaction_product_codebarre(PostData_Produit produits, PostData_Codebarre codebarres) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                insert_into_produit(produits);
                insert_into_codebarre(codebarres);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean update_into_produit(PostData_Produit produit) {

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
                values.put("STOCK_INI", produit.stock_ini);
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
                values.put("QTE_PROMO", produit.qte_promo);

                values.put("ISNEW", produit.isNew);

                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {produit.code_barre};
                db.update("PRODUIT", values, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }


    /*public boolean update_into_tournee2(PostData_Tournee2 tournee2) {

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();
                values.put("STATUS", tournee2.status);
                values.put("LATITUDE", tournee2.latitude);
                values.put("LONGITUDE", tournee2.longitude);
                values.put("OBSERVATION", tournee2.observation);
                String selection = "CODE_BARRE=?";
                String[] selectionArgs = {tournee2.recordid.toString()};

                db.update("TOURNEE2", values, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;
    }*/
    //============================== FUNCTION table famille =================================

    public void ExecuteTransactionFamille(ArrayList<PostData_Famille> familles) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete("FAMILLES", null, null);
                for (int i = 0; i < familles.size(); i++) {

                    ContentValues values = new ContentValues();
                    values.put("FAMILLE", familles.get(i).famille);

                    db.insert("FAMILLES", null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    //=============================== table produit ================================================
    public boolean ExecuteTransactionProduit(ArrayList<PostData_Produit> produits, ArrayList<PostData_Codebarre> codebarres) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean executed = false;

        try {

            db.beginTransaction();
            db.delete("PRODUIT", null, null);

            for (int i = 0; i < produits.size(); i++) {

                insert_into_produit(produits.get(i));
            }

            db.delete("CODEBARRE", null, null);

            for (int h = 0; h < codebarres.size(); h++) {

                insert_into_codebarre(codebarres.get(h));

            }

            db.setTransactionSuccessful();
            executed = true;

        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }finally {
            db.endTransaction();
        }
        return executed;
    }
    //=============================== FUNCTION TO INSERT INTO Codebarre TABLE ===============================
    public void insert_into_codebarre(PostData_Codebarre codebarre) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("CODE_BARRE", codebarre.code_barre);
        values.put("CODE_BARRE_SYN", codebarre.code_barre_syn);
        db.insert("CODEBARRE", null, values);
    }


    //=============================== FUNCTION TO INSERT INTO Carnet_c TABLE ===============================
    @SuppressLint("Range")
    public boolean insert_into_carnet_c(PostData_Carnet_c carnet_c, double val_nouveau_solde_client, double val_nouveau_montant_versement) {
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
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");


                db.insert("Carnet_c", null, values);


                //update_client

                ContentValues args = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args.put("SOLDE", String.valueOf(val_nouveau_solde_client));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {carnet_c.code_client};
                db.update("CLIENT", args, selection, selectionArgs);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    @SuppressLint("Range")
    public boolean insert_into_carnet_f(PostData_Carnet_f carnet_f, double val_nouveau_solde_fournisseur, double val_nouveau_montant_versement) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("CODE_FRS", carnet_f.code_frs);
                values.put("DATE_CARNET", carnet_f.carnet_date);
                values.put("HEURE", carnet_f.carnet_heure);
                values.put("ACHATS", carnet_f.carnet_achats);
                values.put("VERSEMENTS", carnet_f.carnet_versement);
                values.put("SOURCE", carnet_f.carnet_source);
                values.put("NUM_BON", carnet_f.carnet_num_bon);
                values.put("MODE_RG", carnet_f.carnet_mode_rg);
                values.put("REMARQUES", carnet_f.carnet_remarque);
                values.put("UTILISATEUR", carnet_f.carnet_utilisateur);
                values.put("IS_EXPORTED", 0);
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");


                db.insert("Carnet_f", null, values);


                //update_client

                ContentValues args = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args.put("SOLDE", String.valueOf(val_nouveau_solde_fournisseur));
                String selection = "CODE_FRS=?";
                String[] selectionArgs = {carnet_f.code_frs};
                db.update("FOURNIS", args, selection, selectionArgs);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //============================== FUNCTION SELECT FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public boolean check_product_if_exist(String querry) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean is_exist = false;
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                is_exist = true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return is_exist;
    }


    public boolean check_client_if_exist(String code_client) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;

        String querry = "SELECT * FROM CLIENT WHERE CODE_CLIENT = '" + code_client + "'";
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


    public void update_produit_after_export(String code_barre) {
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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    public void update_client_after_export(String code_client) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                args.put("ISNEW", 0);
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }



    public void update_fournisseur_after_export(String code_frs) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues args = new ContentValues();
                args.put("ISNEW", 0);
                String selection = "CODE_FRS=?";
                String[] selectionArgs = {code_frs};
                db.update("FOURNIS", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    //================================== UPDATE TABLE (Carnet_c) =======================================
    @SuppressLint("Range")
    public boolean update_versement_client(PostData_Carnet_c carnet_c, double val_nouveau_solde_client, double val_nouveau_montant_versement) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("VERSEMENTS", carnet_c.carnet_versement);
                args.put("REMARQUES", carnet_c.carnet_remarque);
                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_c.recordid};
                db.update("CARNET_C", args, selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE", String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client};
                db.update("CLIENT", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    @SuppressLint("Range")
    public boolean update_versement_fournisseur(PostData_Carnet_f carnet_f, double val_nouveau_solde_fournisseur, double val_nouveau_montant_versement) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("VERSEMENTS", carnet_f.carnet_versement);
                args.put("REMARQUES", carnet_f.carnet_remarque);
                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_f.recordid};
                db.update("CARNET_F", args, selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE", String.valueOf(val_nouveau_solde_fournisseur));
                String selection1 = "CODE_FRS=?";
                String[] selectionArgs1 = {carnet_f.code_frs};
                db.update("FOURNIS", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    ////////////////////////// DELETE Situation ///////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_versement_client(PostData_Carnet_c carnet_c, double val_nouveau_solde_client, double val_nouveau_montant_versement) {
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
                args1.put("SOLDE", String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client};
                db.update("CLIENT", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    @SuppressLint("Range")
    public boolean delete_versement_fournisseur(PostData_Carnet_f carnet_f, double val_nouveau_solde_client, double val_nouveau_montant_versement) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_f.recordid};
                db.delete("CARNET_F", selection, selectionArgs);


                //update_client
                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", String.valueOf(val_nouveau_montant_versement));
                args1.put("SOLDE", String.valueOf(val_nouveau_solde_client));
                String selection1 = "CODE_FRS=?";
                String[] selectionArgs1 = {carnet_f.code_frs};
                db.update("FOURNIS", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Inventaires1) =======================================
    public void update_versement_exported(String recordid) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                args.put("IS_EXPORTED", 1);
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid};
                db.update("Carnet_c", args, selection, selectionArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    //================================== UPDATE TABLE (Inventaires1) =======================================
    public void delete_Codebarre(String code_barre) {
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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }


    @SuppressLint("Range")
    public PostData_Params select_params_from_database(String querry) {
        PostData_Params params = new PostData_Params();
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


    //============================== FUNCTION SELECT Fournisseurs FROM FOURNIS TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Fournisseur> select_fournisseurs_from_database(String querry) {
        ArrayList<PostData_Fournisseur> fournisseurs = new ArrayList<>();
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

                fournisseur.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                fournisseur.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));

                fournisseur.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                fournisseurs.add(fournisseur);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return fournisseurs;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Client> select_clients_from_database(String querry) {
        ArrayList<PostData_Client> clients = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(querry, null);
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
        }finally {
            if (cursor != null) cursor.close();
        }

        return clients;
    }


    @SuppressLint("Range")
    public ArrayList<PostData_wilaya> select_wilayas_from_database(String querry) {
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
    public ArrayList<PostData_commune> select_communes_from_database(String querry) {
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
    public ArrayList<PostData_Client> select_routing_from_database(String querry) {
        ArrayList<PostData_Client> clients = new ArrayList<>();
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


    @SuppressLint("Range")
    public ArrayList<PostData_Client> select_visite_client_from_database(String querry) {
        ArrayList<PostData_Client> clients = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PostData_Client client = new PostData_Client();

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                client.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));
                client.blocage = cursor.getString(cursor.getColumnIndex("BLOCAGE"));

                clients.add(client);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return clients;
    }

    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Carnet_c> select_carnet_c_from_database(String querry) {
        ArrayList<PostData_Carnet_c> carnet_cs = new ArrayList<>();
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


    @SuppressLint("Range")
    public ArrayList<PostData_Carnet_f> select_carnet_f_from_database(String querry) {
        ArrayList<PostData_Carnet_f> carnet_fs = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Carnet_f carnet_f = new PostData_Carnet_f();

                carnet_f.recordid = cursor.getString(cursor.getColumnIndex("RECORDID"));
                carnet_f.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                carnet_f.carnet_date = cursor.getString(cursor.getColumnIndex("DATE_CARNET"));
                carnet_f.carnet_heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                carnet_f.carnet_achats = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                carnet_f.carnet_versement = cursor.getDouble(cursor.getColumnIndex("VERSEMENTS"));
                carnet_f.carnet_source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                carnet_f.carnet_num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                carnet_f.carnet_mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                carnet_f.carnet_remarque = cursor.getString(cursor.getColumnIndex("REMARQUES"));
                carnet_f.carnet_utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                carnet_f.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                carnet_f.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));

                carnet_f.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                carnet_f.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                carnet_f.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                carnet_f.rc = cursor.getString(cursor.getColumnIndex("RC"));
                carnet_f.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                carnet_f.ai = cursor.getString(cursor.getColumnIndex("AI"));
                carnet_f.nis = cursor.getString(cursor.getColumnIndex("NIS"));

                carnet_f.latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
                carnet_f.longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));

                carnet_fs.add(carnet_f);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return carnet_fs;
    }
    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Carnet_c select_carnet_c_from_database_single(String querry) {
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

    @SuppressLint("Range")
    public PostData_Carnet_f select_carnet_f_from_database_single(String querry) {
        PostData_Carnet_f carnet_f = new PostData_Carnet_f();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                carnet_f.recordid = cursor.getString(cursor.getColumnIndex("RECORDID"));
                carnet_f.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                carnet_f.carnet_date = cursor.getString(cursor.getColumnIndex("DATE_CARNET"));
                carnet_f.carnet_heure = cursor.getString(cursor.getColumnIndex("HEURE"));
                carnet_f.carnet_achats = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                carnet_f.carnet_versement = cursor.getDouble(cursor.getColumnIndex("VERSEMENTS"));
                carnet_f.carnet_source = cursor.getString(cursor.getColumnIndex("SOURCE"));
                carnet_f.carnet_num_bon = cursor.getString(cursor.getColumnIndex("NUM_BON"));
                carnet_f.carnet_mode_rg = cursor.getString(cursor.getColumnIndex("MODE_RG"));
                carnet_f.carnet_remarque = cursor.getString(cursor.getColumnIndex("REMARQUES"));
                carnet_f.carnet_utilisateur = cursor.getString(cursor.getColumnIndex("UTILISATEUR"));
                carnet_f.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));

                //CLIENT
                carnet_f.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                carnet_f.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                carnet_f.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                carnet_f.rc = cursor.getString(cursor.getColumnIndex("RC"));
                carnet_f.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                carnet_f.ai = cursor.getString(cursor.getColumnIndex("AI"));
                carnet_f.nis = cursor.getString(cursor.getColumnIndex("NIS"));
                carnet_f.latitude = cursor.getString(cursor.getColumnIndex("LATITUDE"));
                carnet_f.longitude = cursor.getString(cursor.getColumnIndex("LONGITUDE"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return carnet_f;
    }


    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Fournisseur select_fournisseur_from_database(String code_frs) {

        PostData_Fournisseur fournisseur = new PostData_Fournisseur();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FOURNIS WHERE CODE_FRS = '" + code_frs + "'", null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                fournisseur.fournis_id = cursor.getInt(cursor.getColumnIndex("FOURNIS_ID"));
                fournisseur.code_frs = cursor.getString(cursor.getColumnIndex("CODE_FRS"));
                fournisseur.fournis = cursor.getString(cursor.getColumnIndex("FOURNIS"));
                fournisseur.tel = cursor.getString(cursor.getColumnIndex("TEL"));
                fournisseur.adresse = cursor.getString(cursor.getColumnIndex("ADRESSE"));
                fournisseur.rc = cursor.getString(cursor.getColumnIndex("RC"));
                fournisseur.ifiscal = cursor.getString(cursor.getColumnIndex("IFISCAL"));
                fournisseur.ai = cursor.getString(cursor.getColumnIndex("AI"));
                fournisseur.nis = cursor.getString(cursor.getColumnIndex("NIS"));
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
    public double select_last_price_from_database(String table, String code_client, String codebarre) {

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
        if (table.equals("BON1")) {
            cursor = db.rawQuery(query_bon1, null);
        } else if (table.equals("BON1_TEMP")) {
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
    public PostData_Client select_client_from_database(String code_client) {

        PostData_Client client = new PostData_Client();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CLIENT WHERE CODE_CLIENT = '" + code_client + "'", null);
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
    public PostData_Client select_client_etat_from_database(String clientt) {

        PostData_Client client = new PostData_Client();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT CODE_CLIENT FROM CLIENT WHERE CLIENT = '" + clientt + "'", null);
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
    public ArrayList<String> select_familles_from_database(String querry) {
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
    public ArrayList<PostData_Produit> select_produits_from_database(String querry, boolean show_picture_prod) {
        ArrayList<PostData_Produit> produits = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(querry, null)) {
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

                    produit.pv_limite = cursor.getDouble(cursor.getColumnIndex("PV_LIMITE"));

                    produit.stock = cursor.getDouble(cursor.getColumnIndex("STOCK"));
                    produit.colissage = cursor.getDouble(cursor.getColumnIndex("COLISSAGE"));
                    produit.stock_ini = cursor.getDouble(cursor.getColumnIndex("STOCK_INI"));
                    produit.stock_colis = cursor.getInt(cursor.getColumnIndex("STOCK_COLIS"));
                    produit.stock_vrac = cursor.getInt(cursor.getColumnIndex("STOCK_VRAC"));

                    if (show_picture_prod) {
                        produit.photo = cursor.getBlob(cursor.getColumnIndex("PHOTO"));
                    }

                    produit.description = cursor.getString(cursor.getColumnIndex("DETAILLE"));
                    produit.famille = cursor.getString(cursor.getColumnIndex("FAMILLE"));
                    produit.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                    produit.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                    produit.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                    produit.promo = cursor.getInt(cursor.getColumnIndex("PROMO"));
                    produit.d1 = cursor.getString(cursor.getColumnIndex("D1"));
                    produit.d2 = cursor.getString(cursor.getColumnIndex("D2"));
                    produit.pp1_ht = cursor.getDouble(cursor.getColumnIndex("PP1_HT"));
                    produit.qte_promo = cursor.getDouble(cursor.getColumnIndex("QTE_PROMO"));

                    produit.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

                    produits.add(produit);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return produits;
    }


    @SuppressLint("Range")
    public PostData_Produit select_one_produit_from_database(String querry) {
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
                produit.qte_promo = cursor.getDouble(cursor.getColumnIndex("QTE_PROMO"));

                produit.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return produit;
    }

    //========================== FUNCTION SELECT Code_barre FROM DATABASE ===========================
    @SuppressLint("Range")
    public String select_codebarre_from_database(String querry) {
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

    //========================== FUNCTION SELECT All Code_barre FROM DATABASE ===========================
    @SuppressLint("Range")
    public ArrayList<PostData_Codebarre> select_all_codebarre_from_database(String querry) {
        ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        try (Cursor cursor = db.rawQuery(querry, null)) {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    PostData_Codebarre codebarre = new PostData_Codebarre();

                    codebarre.code_barre = cursor.getString(cursor.getColumnIndex("CODE_BARRE"));
                    codebarre.code_barre_syn = cursor.getString(cursor.getColumnIndex("CODE_BARRE_SYN"));

                    codebarres.add(codebarre);

                } while (cursor.moveToNext());
            }
        }
        return codebarres;
    }

    //============================ FUNCTION SELECT commande achat FROM DATABASE=============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat1> select_all_achat1_from_database(String querry) {
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
    public ArrayList<PostData_Bon1> select_all_bon1_from_database(String querry) {
        ArrayList<PostData_Bon1> bon1s = new ArrayList<>();
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
                bon1.montant_achat= cursor.getDouble(cursor.getColumnIndex("MONTANT_ACHAT"));
                bon1.benifice_par_bon = cursor.getDouble(cursor.getColumnIndex("BENIFICE_BON"));
                //////////
                bon1.ancien_solde = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                bon1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                bon1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));
                //////////
                bon1.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                bon1.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));

                bon1.livrer = cursor.getInt(cursor.getColumnIndex("LIVRER"));
                bon1.date_liv = cursor.getString(cursor.getColumnIndex("DATE_LIV"));
                bon1.is_imported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

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
    public int select_count_from_database(String querry) {
        //ArrayList<PostData_Bon1> bon1s = new ArrayList<PostData_Bon1>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }


    //////////////select_bon1_from_database2 revise par smail le 10.05.2022/////////////////////////
    @SuppressLint("Range")
    public PostData_Bon1 select_bon1_from_database2(String querry) {
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
                bon1.montant_achat= cursor.getDouble(cursor.getColumnIndex("MONTANT_ACHAT"));

                //////////
                bon1.ancien_solde = cursor.getDouble(cursor.getColumnIndex("ANCIEN_SOLDE"));
                bon1.verser = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                bon1.reste = cursor.getDouble(cursor.getColumnIndex("RESTE"));

                //////////
                bon1.latitude = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
                bon1.longitude = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));

                //////////
                bon1.livrer = cursor.getInt(cursor.getColumnIndex("LIVRER"));
                bon1.date_liv = cursor.getString(cursor.getColumnIndex("DATE_LIV"));
                bon1.is_imported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

                /////////
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
    public PostData_Achat1 select_one_acha1_from_database(String querry) {
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
    public String select_max_num_bon(String querry) {
        String max = "0";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(querry, null);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                if (cursor.getString(0) != null) {
                    max = cursor.getString(cursor.getColumnIndex("max_id"));
                }
            }
        }
        cursor.close();

        return format_num_bon(String.valueOf(Integer.parseInt(max) + 1), 6);
    }


    //=============================== FUNCTION TO INSERT INTO bon1 or bon1_temp TABLE ===============================
    public boolean insert_into_bon1(String _table, PostData_Bon1 bon1) {
        boolean executed = false;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            // Check if the record exists using parameterized query
            boolean bon1_exist;
            String query = "SELECT 1 FROM " + _table + " WHERE NUM_BON = ?";
            try (Cursor cursor = db.rawQuery(query, new String[]{bon1.num_bon})) {
                bon1_exist = cursor.moveToFirst(); // True if at least one row
            }

            if (bon1_exist) {
                // Update existing record
                ContentValues args1 = new ContentValues();
                args1.put("CODE_CLIENT", bon1.code_client);
                args1.put("MODE_TARIF", bon1.mode_tarif);
                db.update(_table, args1, "NUM_BON = ?", new String[]{bon1.num_bon});
            } else {
                // Insert new record
                ContentValues values = new ContentValues();
                values.put("NUM_BON", bon1.num_bon);
                values.put("CODE_CLIENT", bon1.code_client);
                values.put("DATE_BON", bon1.date_bon);
                values.put("HEURE", bon1.heure);
                values.put("CODE_DEPOT", bon1.code_depot);
                values.put("CODE_VENDEUR", bon1.code_vendeur);
                values.put("MONTANT_ACHAT", bon1.montant_achat);
                values.put("MODE_TARIF", bon1.mode_tarif);
                values.put("NBR_P", bon1.nbr_p);
                values.put("VERSER", bon1.verser);
                values.put("TIMBRE", bon1.timbre);
                values.put("REMISE", bon1.remise);
                values.put("BLOCAGE", bon1.blocage);
                values.put("ANCIEN_SOLDE", bon1.ancien_solde);
                values.put("TOT_QTE", bon1.tot_qte);
                values.put("TOT_HT", bon1.tot_ht);
                values.put("TOT_TVA", bon1.tot_tva);
                values.put("IS_IMPORTED", bon1.is_imported);
                values.put("EXPORTATION", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                db.insert(_table, null, values);
            }

            db.setTransactionSuccessful();
            executed = true;
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }

        return executed;
    }



    //=============================== FUNCTION TO INSERT INTO bon1 or bon1_temp TABLE ===============================
    public boolean Export_commande_to_ventes(PostData_Bon1 bon1, ArrayList<PostData_Bon2> bon2s, String CODE_DEPOT, String new_num_bon) {
        boolean executed = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.beginTransaction();

            // Insert into bon1
            ContentValues values = new ContentValues();
            values.put("NUM_BON", new_num_bon);
            values.put("CODE_CLIENT", bon1.code_client);

            values.put("DATE_BON", bon1.date_bon);
            values.put("HEURE", bon1.heure);

            values.put("NBR_P", bon1.nbr_p);

            values.put("MODE_TARIF", bon1.mode_tarif);
            values.put("CODE_DEPOT", bon1.code_depot);

            values.put("CODE_VENDEUR", bon1.code_vendeur);
            values.put("TOT_HT", bon1.tot_ht);
            values.put("TOT_TVA", bon1.tot_tva);
            values.put("TIMBRE", bon1.timbre);

            values.put("LATITUDE", bon1.latitude);
            values.put("LONGITUDE", bon1.longitude);
            values.put("REMISE", bon1.remise);
            values.put("MONTANT_ACHAT", bon1.montant_achat);
            values.put("ANCIEN_SOLDE", bon1.ancien_solde);
            values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
            values.put("BLOCAGE", "M");
            values.put("VERSER", 0);


            db.insert("BON1", null, values);

            // Insert into bon2

            for (int i = 0; i < bon2s.size(); i++) {
                insert_into_bon2("BON2", new_num_bon, CODE_DEPOT, bon2s.get(i));
            }

            SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar c = Calendar.getInstance();
            String date_livraison = date_format.format(c.getTime());

            // Update Bon1_temp
            ContentValues args1 = new ContentValues();
            args1.put("BLOCAGE", "T");
            args1.put("LIVRER", 1);
            args1.put("DATE_LIV", date_livraison);
            String selection1 = "NUM_BON=?";
            String[] selectionArgs1 = {bon1.num_bon};
            db.update("BON1_TEMP", args1, selection1, selectionArgs1);

            db.setTransactionSuccessful();
            executed = true;

        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        } finally {
            db.endTransaction();
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Bon2 TABLE ===============================
    public boolean insert_into_bon2(String _table, String num_bon, String code_depot, PostData_Bon2 list_bon2) {

        boolean executed = false;
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            db.beginTransaction();
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

            if (_table.equals("BON2")) {
                update_Stock_Produit_vente("BON2_INSERT", list_bon2, 0.0, 0.0);
            }

            db.setTransactionSuccessful();
            executed = true;

        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        } finally {
            db.endTransaction();
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE BON2   ===============================
    public boolean update_into_bon2(String _table, String num_bon, PostData_Bon2 list_bon2, Double qte_old, Double gratuit_old) {
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

                if (_table.equals("BON2")) {
                    update_Stock_Produit_vente("BON2_EDIT", list_bon2, qte_old, gratuit_old);
                }

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }

        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }
    //=============================== FUNCTION TO UPDATE ACHAT2   ===============================

    public boolean update_into_achat2(String _table, String num_bon, PostData_Achat2 list_achat2, Double qte_old, Double gratuit_old) {
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

                if (_table.equals("ACHAT2")) {
                    update_Stock_Produit_achat("ACHAT2_EDIT", list_achat2, qte_old, gratuit_old);
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    ////////////////////////////////////// DELETING ////////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_from_bon2(String _table, Integer recordid, PostData_Bon2 bon2) {
        boolean executed;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);

                if (_table.equals("BON2")) {
                    update_Stock_Produit_vente("BON2_DELETE", bon2, 0.0, 0.0);
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }


    @SuppressLint("Range")
    public void delete_from_achat2(String _table, Integer recordid, PostData_Achat2 achat2) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);

                if (_table.equals("ACHAT2")) {
                    update_Stock_Produit_achat("ACHAT2_DELETE", achat2, 0.0, 0.0);
                }

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    @SuppressLint("Range")
    public boolean delete_from_inv2(String _table, Integer recordid) {
        boolean executed;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                String selection = "RECORDID=?";
                String[] selectionArgs = {recordid.toString()};
                db.delete(_table, selection, selectionArgs);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }


    @SuppressLint("Range")
    public boolean delete_client_from_routing(String CODE_CLIENT) {

        int index = 0;

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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return index == 1;
    }

    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    @SuppressLint("Range")
    public boolean update_bon1_client(String num_bon, PostData_Bon1 bon1) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "F");
                args1.put("ANCIEN_SOLDE", bon1.ancien_solde);
                args1.put("VERSER", bon1.verser);
                if (bon1.verser == 0) {
                    args1.put("MODE_RG", "A TERME");
                } else {
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
                Cursor cursor1 = db.rawQuery("SELECT ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='" + bon1.code_client + "'", null);
                // looping through all rows and adding to list
                if (cursor1.moveToFirst()) {
                    do {
                        client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                //update client

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_montant + bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_montant + bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_montant + bon1.montant_bon) - bon1.verser));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {bon1.code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    public boolean update_bon1_temp(String num_bon, PostData_Bon1 bon1) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }


    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    @SuppressLint("Range")
    public boolean update_bon1_client_edit(String num_bon, String code_client, PostData_Bon1 bon1) {

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "M");
                args1.put("ANCIEN_SOLDE", "0.00");
                args1.put("VERSER", "0.00");
                args1.put("RESTE", "0.00");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1", args1, selection1, selectionArgs1);

                //update_client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='" + code_client + "'", null);
                // looping through all rows and adding to list
                if (cursor1.moveToFirst()) {
                    do {
                        client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_montant - bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_montant - bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_montant - bon1.montant_bon) + bon1.verser));
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {bon1.code_client};
                db.update("CLIENT", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //=============================== FUNCTION TO UPDATE CLIENT ===============================
    public boolean update_bon1_temp_edit(String num_bon, String code_client, PostData_Bon1 bon1) {

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "M");
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {num_bon};
                db.update("BON1_TEMP", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        }
        return executed;

    }


    public boolean update_achats_commandes_as_exported(boolean isTemp, String num_bon) {
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

                if (isTemp) {
                    db.update("ACHAT1_TEMP", args1, selection1, selectionArgs1);
                } else {
                    db.update("ACHAT1", args1, selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    public boolean update_ventes_commandes_as_exported(boolean isTemp, String num_bon) {
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

                if (isTemp) {
                    db.update("BON1_TEMP", args1, selection1, selectionArgs1);
                    db.execSQL("UPDATE BON1_TEMP SET IS_EXPORTED = 1 WHERE IS_IMPORTED = 1");
                } else {
                    db.update("BON1", args1, selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    @SuppressLint("Range")
    public PostData_Bon2 check_if_bon2_exist(String querry) {
        //check if bon2 exist
        PostData_Bon2 bon2 = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
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
                bon2.pv_limite = cursor.getDouble(cursor.getColumnIndex("PV_LIMITE"));
                bon2.stock_produit = cursor.getDouble(cursor.getColumnIndex("STOCK"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon2;
    }

    @SuppressLint("Range")
    public PostData_Achat2 check_if_achat2_exist(String querry) {
        //check if bon2 exist
        PostData_Achat2 achat2 = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
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
        return achat2;
    }

    public boolean check_if_has_bon(String querry) {
        //check if client has bons
        boolean has_bon = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                has_bon = true;
                break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return has_bon;
    }

    @SuppressLint("Range")
    public PostData_Inv2 check_if_inv2_exist(String querry) {
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
        return inv2;
    }

    //============================== FUNCTION SELECT Produits FROM Produits TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Bon2> select_bon2_from_database(String querry) {
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
                bon2.pv_limite = cursor.getInt(cursor.getColumnIndex("PV_LIMITE"));
                bon2.promo = cursor.getInt(cursor.getColumnIndex("PROMO"));
                bon2.d1 = cursor.getString(cursor.getColumnIndex("D1"));
                bon2.d2 = cursor.getString(cursor.getColumnIndex("D2"));
                bon2.pp1_ht = cursor.getDouble(cursor.getColumnIndex("PP1_HT"));
                bon2.qte_promo = cursor.getDouble(cursor.getColumnIndex("QTE_PROMO"));

                bon2.destock_type = cursor.getString(cursor.getColumnIndex("DESTOCK_TYPE"));
                bon2.destock_code_barre = cursor.getString(cursor.getColumnIndex("DESTOCK_CODE_BARRE"));
                bon2.destock_qte = cursor.getDouble(cursor.getColumnIndex("DESTOCK_QTE"));

                bon2s.add(bon2);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon2s;
    }


    public boolean update_Stock_Produit_vente(String source, PostData_Bon2 panier, Double qte_old, Double gratuit_old) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }


    public boolean update_Stock_Produit_achat(String source, PostData_Achat2 panier, Double qte_old, Double gratuit_old) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public void update_pamp_on_insert_update_produit(String codebarre, double stock, double pa_ht_old, double qte, double pa_ht_new, double pamp_old) {
        SQLiteDatabase db = this.getWritableDatabase();
        double pamp_new = 0;
        if (stock + qte == 0) {
            if (stock < 0) {
                pamp_new = pa_ht_new;
            } else if (stock > 0) {
                pamp_new = (pamp_old + pa_ht_new) / 2;
            }
        } else if (stock + qte != 0) {
            if (stock <= 0) {
                pamp_new = pa_ht_new;
            } else if (stock > 0) {
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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public void update_pamp_on_delete_produit(String codebarre, double stock, double pa_ht_old, double qte, double pa_ht_new, double pamp_old) {
        SQLiteDatabase db = this.getWritableDatabase();
        double pamp_new;
        if (stock - qte > 0) {
            pamp_new = ((pamp_old * stock) - (pa_ht_new * qte)) / (stock - qte);
        } else {
            pamp_new = pamp_old;
        }

        try {
            ContentValues args = new ContentValues();
            args.put("PAMP", pamp_new);
            args.put("PA_HT", pa_ht_new);
            String selection = "CODE_BARRE=?";
            String[] selectionArgs = {codebarre};
            db.update("PRODUIT", args, selection, selectionArgs);
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }
    //================================== UPDATE TABLE (Inventaires1) =======================================

    public boolean update_fournisseur(PostData_Fournisseur fournisseur) {
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
                args.put("ISNEW", 1);
                String selection = "CODE_FRS=?";
                String[] selectionArgs = {fournisseur.code_frs};
                db.update("FOURNIS", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean update_position_client(Double latitude, Double longitude, String code_client) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean update_client(PostData_Client client) {
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

                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Bon1) ===============================
    public boolean update_bon1(String _table, String num_bon, PostData_Bon1 bon1) {
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
                args.put("ANCIEN_SOLDE", bon1.ancien_solde);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Bon1_a) ===============================
    public boolean update_achat1(String _table, String num_bon, PostData_Achat1 achat1) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Inventaires1) ===============================
    public boolean update_inv1_nbr_produit(String _table, PostData_Inv1 inv1) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    ////////////////////////////////////// DELETING ////////////////////////////////////////////////
    @SuppressLint("Range")
    public boolean delete_bon_vente(boolean isTemp, PostData_Bon1 bon1) {
        boolean executed;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Bon2> bon2_delete = new ArrayList<>();
                String selection;

                if (isTemp) {
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {bon1.num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                } else {

                    bon2_delete = select_bon2_from_database("SELECT " +
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

                            "FROM BON2 " +
                            "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2.NUM_BON = '" + bon1.num_bon + "'");

                    for (int k = 0; k < bon2_delete.size(); k++) {

                        ContentValues args = new ContentValues();
                        args.put("STOCK", bon2_delete.get(k).stock_produit + bon2_delete.get(k).qte + bon2_delete.get(k).gratuit);

                        selection = "CODE_BARRE=?";
                        String[] selectionArgsss = {bon2_delete.get(k).codebarre};
                        db.update("PRODUIT", args, selection, selectionArgsss);
                    }


                    //update_client
                    PostData_Client client = new PostData_Client();
                    Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='" + bon1.code_client + "'", null);
                    // looping through all rows and adding to list
                    if (cursor1.moveToFirst()) {
                        do {
                            client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                            client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                            client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                        } while (cursor1.moveToNext());
                    }

                    cursor1.close();

                    ContentValues args = new ContentValues();
                    args.put("ACHATS", String.valueOf(client.achat_montant - bon1.montant_bon));
                    args.put("VERSER", String.valueOf(client.verser_montant - bon1.verser));
                    args.put("SOLDE", String.valueOf((client.solde_montant - bon1.montant_bon) + bon1.verser));
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
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public boolean delete_bon_en_attente(boolean isTemp, String num_bon) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if (isTemp) {
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                } else {

                    ArrayList<PostData_Bon2> bon2_delete;
                    bon2_delete = select_bon2_from_database("SELECT " +
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

                            "FROM BON2 " +
                            "LEFT JOIN  PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2.NUM_BON = '" + num_bon + "'");

                    for (int k = 0; k < bon2_delete.size(); k++) {

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
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public void delete_bon_achat(boolean isTemp, PostData_Achat1 achat1) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Achat2> achat2_delete;
                String selection;

                if (isTemp) {
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {achat1.num_bon};
                    db.delete("ACHAT1_TEMP", selection, selectionArgs);
                    db.delete("ACHAT2_TEMP", selection, selectionArgs);
                } else {

                    achat2_delete = select_all_achat2_from_database("SELECT " +
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
                            "WHERE ACHAT2.NUM_BON = '" + achat1.num_bon + "'");

                    for (int k = 0; k < achat2_delete.size(); k++) {


                        update_Stock_Produit_achat("ACHAT2_DELETE", achat2_delete.get(k), achat2_delete.get(k).qte, achat2_delete.get(k).gratuit);

                        update_pamp_on_delete_produit(
                                achat2_delete.get(k).codebarre,
                                achat2_delete.get(k).stock_produit,
                                achat2_delete.get(k).pa_ht_produit,
                                achat2_delete.get(k).qte,
                                achat2_delete.get(k).pa_ht,
                                achat2_delete.get(k).pa_ht);
                    }


                    //update_fournisseur
                    String frs_solde_update = "UPDATE FOURNIS SET ACHATS = ACHATS - " + achat1.montant_bon + ", VERSER = VERSER - " + achat1.verser + ", SOLDE = SOLDE - " + (achat1.montant_bon - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs + "'";
                    db.execSQL(frs_solde_update);

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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public void delete_bon_achat_en_attente(boolean isTemp, PostData_Achat1 achat1) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                ArrayList<PostData_Achat2> achat2_delete;
                String selection;

                if (isTemp) {
                    selection = "NUM_BON=?";
                    String[] selectionArgs = {achat1.num_bon};
                    db.delete("ACHAT1_TEMP", selection, selectionArgs);
                    db.delete("ACHAT2_TEMP", selection, selectionArgs);
                } else {

                    achat2_delete = select_all_achat2_from_database("SELECT " +
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
                            "WHERE ACHAT2.NUM_BON = '" + achat1.num_bon + "'");

                    for (int k = 0; k < achat2_delete.size(); k++) {


                        update_Stock_Produit_achat("ACHAT2_DELETE", achat2_delete.get(k), achat2_delete.get(k).qte, achat2_delete.get(k).gratuit);

                        update_pamp_on_delete_produit(
                                achat2_delete.get(k).codebarre,
                                achat2_delete.get(k).stock_produit,
                                achat2_delete.get(k).pa_ht_produit,
                                achat2_delete.get(k).qte,
                                achat2_delete.get(k).pa_ht,
                                achat2_delete.get(k).pa_ht);

                    }

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
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public boolean delete_bon_after_export(boolean isTemp, String num_bon) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                if (isTemp) {
                    String selection = "NUM_BON=?";
                    String[] selectionArgs = {num_bon};
                    db.delete("BON1_TEMP", selection, selectionArgs);
                    db.delete("BON2_TEMP", selection, selectionArgs);
                } else {
                    // finally delete bon
                    String selection1 = "NUM_BON=?";
                    String[] selectionArgs1 = {num_bon};
                    db.delete("BON1", selection1, selectionArgs1);
                    db.delete("BON2", selection1, selectionArgs1);
                }

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public boolean delete_all_bon(boolean isTemp) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String sql_delete_bon2;
                String sql_delete_bon1;
                if (isTemp) {
                    //delete all temp bon

                    sql_delete_bon2 = "DELETE FROM Bon2_Temp WHERE Bon2_Temp.NUM_BON IN ( SELECT Bon1_Temp.NUM_BON FROM Bon1_Temp WHERE Bon1_Temp.IS_EXPORTED = 1)";
                    sql_delete_bon1 = "DELETE FROM Bon1_Temp WHERE IS_EXPORTED = 1";

                } else {
                    // finally delete all bon
                    sql_delete_bon2 = "DELETE FROM Bon2 WHERE Bon2.NUM_BON IN ( SELECT Bon1.NUM_BON FROM Bon1 WHERE Bon1.IS_EXPORTED = 1)";
                    sql_delete_bon1 = "DELETE FROM Bon1 WHERE IS_EXPORTED = 1";
                }
                db.execSQL(sql_delete_bon2);
                db.execSQL(sql_delete_bon1);

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    @SuppressLint("Range")
    public boolean delete_versement(PostData_Carnet_c carnet_c) {
        boolean executed;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                //update_client
                PostData_Client client = new PostData_Client();
                Cursor cursor1 = db.rawQuery("SELECT  ACHATS, VERSER, SOLDE FROM CLIENT WHERE CODE_CLIENT='" + carnet_c.code_client + "'", null);
                // looping through all rows and adding to list
                if (cursor1.moveToFirst()) {
                    do {
                        client.achat_montant = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_montant = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_montant = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", client.verser_montant - carnet_c.carnet_versement);
                args1.put("SOLDE", client.solde_montant + carnet_c.carnet_versement);
                String selection1 = "CODE_CLIENT=?";
                String[] selectionArgs1 = {carnet_c.code_client};
                db.update("CLIENT", args1, selection1, selectionArgs1);


                // Finally delete versement
                String selection = "RECORDID=?";
                String[] selectionArgs = {carnet_c.recordid};
                db.delete("Carnet_c", selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_Etatv> select_etatv_from_database(String wilaya, String commune, String c_client, String from_d, String to_d) {

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<>();

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
                "WHERE (BON1.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";

        if (c_client != null) {
            querry = querry + " AND BON1.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry = querry + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry = querry + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }
        querry = querry + " AND BON1.BLOCAGE = 'F' ";

        querry = querry + "GROUP BY BON2.PRODUIT, BON2.PV_HT, BON2.QTE_GRAT " +
                "UNION ALL " +
                "SELECT " +
                "BON2.PRODUIT, " +
                "SUM(BON2.QTE_GRAT) AS TOT_QTE, " +
                "0.0 AS PV_HT," +
                "0.0 AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN BON2 ON BON2.NUM_BON = BON1.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                "WHERE (BON1.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";

        if (c_client != null) {
            querry = querry + " AND BON1.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry = querry + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry = querry + " AND CLIENT.COMMUNE = '" + commune + "' ";
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

                tot_qte = tot_qte + etatv.quantite;
                tot_montant_par_qte = tot_montant_par_qte + etatv.montant;

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        cursor.close();


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
                "BON1.TOT_HT + BON1.TOT_TVA AS MONTANT_BON_HT, " +
                "0 AS VERSEMENTS_CLIENT, " +
                "BON1.VERSER AS VERSEMENT_BON, " +
                "(BON1.TOT_HT - BON1.REMISE) -  BON1.MONTANT_ACHAT AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN CARNET_C ON BON1.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                " WHERE (BON1.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";
        if (c_client != null) {
            querry1 = querry1 + " AND BON1.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }
        querry1 = querry1 + "  AND BON1.BLOCAGE = 'F' ";

        querry1 = querry1 + "UNION ALL ";
        querry1 = querry1 + "SELECT 0 AS REMISE, " +
                "0 AS TOT_HT, " +
                "0 AS TOT_TVA, " +
                "0 AS MONTANT_BON_HT, " +
                "CARNET_C.VERSEMENTS AS VERSEMENTS_CLIENT, " +
                "0 AS VERSEMENT_BON, " +
                "0 AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT  " +
                "WHERE (CARNET_C.DATE_CARNET BETWEEN '" + from_d + "' AND '" + to_d + "') ";

        if (c_client != null) {
            querry1 = querry1 + " AND CARNET_C.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }


        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getDouble(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_HT"));
                etatv2.total_versement_bon = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENT_BON"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                //etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENTS_CLIENT"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_benifice = total_benifice + etatv2.benifice;
                total_verser = total_verser + (etatv2.total_versement_bon + etatv2.vers_client);

                tot_remise = tot_remise + etatv2.total_remise;

            } while (cursor1.moveToNext());
        }

        cursor1.close();

        String querry2 = "SELECT SOLDE FROM CLIENT ";
        if (c_client != null) {
            querry2 = querry2 + " WHERE CODE_CLIENT = '" + c_client + "'";
        }
        Cursor cursor2 = db.rawQuery(querry2, null);
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {

                double solde_client = cursor2.getDouble(cursor2.getColumnIndex("SOLDE"));
                tot_credit = tot_credit + solde_client;

            } while (cursor2.moveToNext());
        }

        cursor2.close();

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
        etatv.produit = "BENIFICE TOTAL :";
        etatv.montant = total_benifice;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);


        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOT A CE JOUR:";
        etatv.montant = tot_credit;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);






        double objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        objectif = Double.parseDouble(prefs.getString("OBJECTIF_MONTANT", "0.00"));
        if (tot_montant_total < (Double.valueOf(objectif) / 2)) {
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        } else if (Double.valueOf(objectif) > tot_montant_total) {
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        } else {
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
    public ArrayList<PostData_Etatv> select_etatc_from_database(String wilaya, String commune, String c_client, String from_d, String to_d) {

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<>();

        double tot_qte = 0.00;
        double tot_montant_par_qte = 0.00;
        double tot_montant_total = 0.00;
        double total_verser = 0.00;
        double tot_remise = 0.00;
        double tot_credit = 0.00;
        double total_benifice = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "BON2_TEMP.PRODUIT, " +
                "SUM(BON2_TEMP.QTE) AS TOT_QTE, " +
                "BON2_TEMP.PV_HT, " +
                "SUM(BON2_TEMP.QTE) * (BON2_TEMP.PV_HT + (BON2_TEMP.PV_HT * BON2_TEMP.TVA / 100)) AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN BON2_TEMP ON BON2_TEMP.NUM_BON = BON1_TEMP.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                "WHERE (BON1_TEMP.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";
        if (c_client != null) {
            querry = querry + " AND BON1_TEMP.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry = querry + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry = querry + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }
        querry = querry + " AND BON1_TEMP.BLOCAGE = 'F' ";

        querry = querry + "GROUP BY BON2_TEMP.PRODUIT, BON2_TEMP.PV_HT, BON2_TEMP.QTE_GRAT " +
                "UNION ALL " +
                "SELECT " +
                "BON2_TEMP.PRODUIT, " +
                "SUM(BON2_TEMP.QTE_GRAT) AS TOT_QTE, " +
                "0.0 AS PV_HT," +
                "0.0 AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN BON2_TEMP ON BON2_TEMP.NUM_BON = BON1_TEMP.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                "WHERE (BON1_TEMP.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";

        if (c_client != null) {
            querry = querry + " AND BON1_TEMP.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry = querry + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry = querry + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }
        querry = querry + " AND BON1_TEMP.BLOCAGE = 'F' AND BON2_TEMP.QTE_GRAT <> 0 GROUP BY BON2_TEMP.PRODUIT, BON2_TEMP.PV_HT, BON2_TEMP.QTE_GRAT  ORDER BY PRODUIT, TOT_QTE DESC";


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

                tot_qte = tot_qte + etatv.quantite;
                tot_montant_par_qte = tot_montant_par_qte + etatv.montant;

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        cursor.close();


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
                "BON1_TEMP.REMISE, " +
                "BON1_TEMP.TOT_HT, " +
                "BON1_TEMP.TOT_TVA, " +
                "BON1_TEMP.TOT_HT - BON1_TEMP.REMISE AS MONTANT_BON_HT, " +
                "0 AS VERSEMENTS_CLIENT, " +
                "BON1_TEMP.VERSER AS VERSEMENT_BON, " +
                "(BON1_TEMP.TOT_HT - BON1_TEMP.REMISE) -  BON1_TEMP.MONTANT_ACHAT AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN CARNET_C ON BON1_TEMP.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                " WHERE (BON1_TEMP.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";
        if (c_client != null) {
            querry1 = querry1 + " AND BON1_TEMP.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }
        querry1 = querry1 + "  AND BON1_TEMP.BLOCAGE = 'F' ";

        querry1 = querry1 + "UNION ALL ";
        querry1 = querry1 + "SELECT 0 AS REMISE, " +
                "0 AS TOT_HT, " +
                "0 AS TOT_TVA, " +
                "0 AS MONTANT_BON_HT, " +
                "CARNET_C.VERSEMENTS AS VERSEMENTS_CLIENT, " +
                "0 AS VERSEMENT_BON, " +
                "0 AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT  " +
                "WHERE (CARNET_C.DATE_CARNET BETWEEN '" + from_d + "' AND '" + to_d + "') ";

        if (c_client != null) {
            querry1 = querry1 + " AND CARNET_C.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry1 = querry1 + " AND CLIENT.COMMUNE = '" + commune + "' ";
        }


        Cursor cursor1 = db.rawQuery(querry1, null);
        // looping through all rows and adding to list
        if (cursor1.moveToFirst()) {
            do {

                PostData_Etatv etatv2 = new PostData_Etatv();

                etatv2.total_remise = cursor1.getDouble(cursor1.getColumnIndex("REMISE"));
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_HT"));
                etatv2.total_versement_bon = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENT_BON"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                //etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENTS_CLIENT"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_benifice = total_benifice + etatv2.benifice;
                total_verser = total_verser + (etatv2.total_versement_bon + etatv2.vers_client);

                tot_remise = tot_remise + etatv2.total_remise;

            } while (cursor1.moveToNext());
        }

        cursor1.close();


        String querry2 = "SELECT SOLDE FROM CLIENT ";
        if (c_client != null) {
            querry2 = querry2 + " WHERE CODE_CLIENT = '" + c_client + "'";
        }
        Cursor cursor2 = db.rawQuery(querry2, null);
        // looping through all rows and adding to list
        if (cursor2.moveToFirst()) {
            do {

                double solde_client = cursor2.getDouble(cursor2.getColumnIndex("SOLDE"));
                tot_credit = tot_credit + solde_client;

            } while (cursor2.moveToNext());
        }

        cursor2.close();

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


        etatv = new PostData_Etatv();
        etatv.produit = "BENIFICE TOTAL :";
        etatv.montant = total_benifice;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);



        double objectif;
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        objectif = Double.parseDouble(prefs.getString("OBJECTIF_MONTANT", "0.00"));
        if (tot_montant_total < (objectif / 2)) {
            etatv = new PostData_Etatv();
            etatv.produit = "N1";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        } else if (objectif > tot_montant_total) {
            etatv = new PostData_Etatv();
            etatv.produit = "N2";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        } else {
            etatv = new PostData_Etatv();
            etatv.produit = "N3";
            etatv.quantite = objectif;
            etatv.montant = tot_montant_total;
            etatv.code_parent = "-8";
            all_etatv.add(etatv);
        }

        return all_etatv;
    }

    public void ResetPda() {
        mContext.deleteDatabase(DATABASE_NAME);
        //insertproduits();
    }

    public File copyAppDbToDownloadFolder(String imei) {
        try {
            File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "backup_distribute_" + imei); // for example "my_data_backup.db"
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
    public boolean insert_into_Inv1(String _table, PostData_Inv1 inv1) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                boolean inv1_exist = false;
                Cursor cursor0 = db.rawQuery("SELECT NUM_INV FROM " + _table + " WHERE NUM_INV = '" + inv1.num_inv + "' ", null);
                // looping through all rows and adding to list
                if (cursor0.moveToFirst()) {
                    do {
                        inv1_exist = true;
                    } while (cursor0.moveToNext());
                }

                cursor0.close();

                if (inv1_exist) {

                    //update_inv1
                    ContentValues args1 = new ContentValues();
                    args1.put("LIBELLE", inv1.nom_inv);
                    String selection1 = "NUM_INV=?";
                    String[] selectionArgs1 = {inv1.num_inv};
                    db.update(_table, args1, selection1, selectionArgs1);

                } else {
                    ContentValues values = new ContentValues();
                    values.put("LIBELLE", inv1.nom_inv);
                    values.put("NUM_INV", inv1.num_inv);
                    values.put("DATE_INV", inv1.date_inv);
                    values.put("HEURE_INV", inv1.heure_inv);
                    values.put("CODE_DEPOT", inv1.code_depot);
                    values.put("IS_EXPORTED", 0);
                    values.put("BLOCAGE", "M");
                    values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                    db.insert(_table, null, values);

                }

                db.setTransactionSuccessful();

                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Inventaires2 TABLE ===============================
    public void insert_into_inventaire2(PostData_Inv2 inv2s) {
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
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }

    }

    public String format_num_bon(String number, Integer length) {
        String _number = number;
        while (_number.length() < length) {
            _number = "0" + _number;
        }
        Log.v("TRACKKK", _number);
        return _number;
    }

    //============================== FUNCTION SELECT FROM inventaire1 ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Inv1> select_list_inventaire_from_database(String querry) {
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
    public PostData_Inv1 select_inventaire_from_database(String querry) {
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
    public ArrayList<PostData_Inv2> select_inventaire2_from_database(String querry) {
        ArrayList<PostData_Inv2> all_inv2 = new ArrayList<>();
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

    /////////////////////////////////////////////// TOURNEE ////////////////////////////////////////////////
    //=============================== FUNCTION TO INSERT INTO Tournee1 TABLE ===============================
    public boolean insert_into_Tournee1(PostData_Tournee1 tournee1) {
        boolean executed = false;
        SQLiteDatabase  db = this.getWritableDatabase();

        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("NUM_TOURNEE", tournee1.num_tournee);
            values.put("NAME_TOURNEE", "TOURNEE_" + tournee1.num_tournee);
            values.put("DATE_TOURNEE", tournee1.date_tournee);
            values.put("CODE_VENDEUR", tournee1.code_vendeur);
            values.put("CODE_DEPOT", tournee1.code_depot);
            values.put("NBR_CLIENT", tournee1.nbr_client);
            values.put("IS_EXPORTED", 0);
            values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            values.put("OBSERVATION", tournee1.observation);

            db.insert("TOURNEE1", null, values);

            db.setTransactionSuccessful();

            executed = true;
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }finally {
            db.endTransaction();
        }
        return executed;
    }

    //=============================== FUNCTION TO INSERT INTO Achats2 TABLE ===============================
    public boolean insert_into_tournee2(PostData_Tournee2 tournee2) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues values = new ContentValues();

                values.put("NUM_TOURNEE", tournee2.num_tournee);
                values.put("DATE_PASSAGE", tournee2.date_passage);
                values.put("HEURE_PASSAGE", tournee2.heure_passage);
                values.put("CODE_CLIENT", tournee2.code_client);
                values.put("STATUS", tournee2.status);
                values.put("LATITUDE", tournee2.latitude);
                values.put("LONGITUDE", tournee2.longitude);
                values.put("OBSERVATION", tournee2.observation);
                values.put("IS_NEW", 0);

                db.insert("TOURNEE2", null, values);
                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //============================== FUNCTION SELECT list FROM tournee1 ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Tournee1> select_list_tournee_from_database(String querry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PostData_Tournee1> tournee1s = new ArrayList<>();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                PostData_Tournee1 tournee = new PostData_Tournee1();
                tournee.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                tournee.num_tournee = cursor.getString(cursor.getColumnIndex("NUM_TOURNEE"));
                tournee.date_tournee = cursor.getString(cursor.getColumnIndex("DATE_TOURNEE"));
                tournee.name_tournee = cursor.getString(cursor.getColumnIndex("NAME_TOURNEE"));
                tournee.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                tournee.nbr_client = cursor.getInt(cursor.getColumnIndex("NBR_CLIENT"));
                tournee.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));
                tournee1s.add(tournee);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return tournee1s;
    }

    //============================== FUNCTION SELECT one line FROM tournee1 ===============================
    @SuppressLint("Range")
    public PostData_Tournee1 select_tournee1_from_database(String querry) {
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Tournee1 tournee1 = new PostData_Tournee1();
        Cursor cursor = db.rawQuery(querry, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                tournee1.recordid = cursor.getInt(cursor.getColumnIndex("RECORDID"));
                tournee1.num_tournee = cursor.getString(cursor.getColumnIndex("NUM_TOURNEE"));
                tournee1.date_tournee = cursor.getString(cursor.getColumnIndex("DATE_TOURNEE"));
                tournee1.name_tournee = cursor.getString(cursor.getColumnIndex("NAME_TOURNEE"));
                tournee1.exportation = cursor.getString(cursor.getColumnIndex("EXPORTATION"));
                tournee1.nbr_client = cursor.getInt(cursor.getColumnIndex("NBR_CLIENT"));
                tournee1.is_exported = cursor.getInt(cursor.getColumnIndex("IS_EXPORTED"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return tournee1;
    }

    //==============================================================================================
    /////////////////////////////////////////////// ACHAT //////////////////////////////////////////
    //=============================== FUNCTION TO INSERT INTO Achats1 TABLE ===============================
    public boolean insert_into_achat1(String _table, PostData_Achat1 achat1) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            boolean bon1_exist = false;
            Cursor cursor0 = db.rawQuery("SELECT NUM_BON FROM " + _table + " WHERE NUM_BON = '" + achat1.num_bon + "' ", null);
            // looping through all rows and adding to list
            if (cursor0.moveToFirst()) {
                do {
                    bon1_exist = true;
                } while (cursor0.moveToNext());
            }

            cursor0.close();

            if (bon1_exist) {

                //update_achat1
                ContentValues args1 = new ContentValues();
                args1.put("CODE_FRS", achat1.code_frs);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {achat1.num_bon};
                db.update(_table, args1, selection1, selectionArgs1);

            } else {
                ContentValues values = new ContentValues();
                values.put("NUM_BON", achat1.num_bon);
                values.put("CODE_FRS", achat1.code_frs);
                values.put("DATE_BON", achat1.date_bon);
                values.put("HEURE", achat1.heure);
                values.put("CODE_DEPOT", achat1.code_depot);
                values.put("NBR_P", 0);
                values.put("BLOCAGE", "M");
                values.put("IS_EXPORTED", 0);
                values.put("EXPORTATION", TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
                db.insert(_table, null, values);

            }

            db.setTransactionSuccessful();
            executed = true;
            db.endTransaction();
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }

        return executed;

    }

    //=============================== FUNCTION TO INSERT INTO Achats2 TABLE ===============================
    public boolean insert_into_achat2(String table, PostData_Achat2 achat2) {
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

                if (table.equals("ACHAT2")) {
                    update_Stock_Produit_achat("ACHAT2_INSERT", achat2, 0.0, 0.0);
                }
                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;

    }

    //============================== FUNCTION SELECT FROM Inventaire2 TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Achat2> select_all_achat2_from_database(String querry) {
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
    public PostData_Codebarre select_produit_codebarre(String scan_result) {
        SQLiteDatabase db = this.getWritableDatabase();
        PostData_Codebarre codebarre = new PostData_Codebarre();
        codebarre.exist = false;
        String querry = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + scan_result + "'";
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
    public boolean Update_inventaire2(PostData_Inv2 _inv2) {
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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    //================================== UPDATE TABLE (Inventaires1) =======================================
    public boolean Update_inventaire1(String num_inv) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {

                String date = new SimpleDateFormat("dd/MM/yyyy HH:m m:ss").format(new Date());
                ContentValues args = new ContentValues();
                args.put("IS_EXPORTED", 1);
                args.put("DATE_EXPORT_INV", date);
                String selection = "NUM_INV=?";
                String[] selectionArgs = {num_inv};
                db.update("INV1", args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean delete_inventaire_group(String num_inv) {
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
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public boolean delete_tournee_group(String num_tournee) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {
                String selection = "NUM_TOURNEE=?";
                String[] selectionArgs = {num_tournee};
                db.delete("TOURNEE1", selection, selectionArgs);
                db.delete("TOURNEE2", selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public boolean delete_fournisseur(String code_frs) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_FRS=?";
                String[] whereArgs = {code_frs};
                db.delete("FOURNIS", whereClause, whereArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public boolean delete_client(String code_client) {
        boolean executed;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_CLIENT=?";
                String[] whereArgs = {code_client};
                db.delete("CLIENT", whereClause, whereArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
            executed = false;
        }
        return executed;
    }

    public void delete_produit(String code_barre) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String whereClause = "CODE_BARRE=?";
                String[] whereArgs = {code_barre};
                db.delete("PRODUIT", whereClause, whereArgs);

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
    }

    public boolean validate_bon1_sql(String _table, PostData_Bon1 bon1) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();

                args.put("ANCIEN_SOLDE", bon1.ancien_solde);
                args.put("VERSER", bon1.verser);
                args.put("MODE_RG", bon1.mode_rg);
                args.put("BLOCAGE", "F");
                args.put("LATITUDE", bon1.latitude);
                args.put("LONGITUDE", bon1.longitude);
                args.put("DATE_F", bon1.date_f);
                args.put("HEURE_F", bon1.heure_f);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {bon1.num_bon};
                db.update(_table, args, selection, selectionArgs);

                if (_table.equals("BON1")) {
                    db.execSQL("UPDATE CLIENT SET ACHATS = ACHATS + " + (bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise) + ", VERSER = VERSER + " + bon1.verser + ", SOLDE = SOLDE + " + ((bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client + "'");
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean validate_achat1_sql(String _table, PostData_Achat1 achat1) {
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

                if (_table.equals("ACHAT1")) {
                    // you have to ini achat, verser, solde to 0 first time 
                    String sql = "UPDATE FOURNIS SET ACHATS = ACHATS + " + (achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise) + ", VERSER = VERSER + " + achat1.verser + ", SOLDE = SOLDE + " + ((achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise) - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs + "'";
                    db.execSQL(sql);
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean validate_inv1_sql(String _table, String num_inv) {

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
                executed = true;
            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean modifier_achat1_sql(String _table, PostData_Achat1 achat1) {
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

                if (_table.equals("ACHAT1")) {
                    db.execSQL("UPDATE FOURNIS SET ACHATS = ACHATS - " + (achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise) + ", VERSER = VERSER - " + achat1.verser + ", SOLDE = SOLDE - " + ((achat1.tot_ht + achat1.tot_tva + achat1.timbre - achat1.remise) - achat1.verser) + " WHERE CODE_FRS = '" + achat1.code_frs + "'");
                }

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean modifier_bon1_sql(String _table, String num_bon, PostData_Bon1 bon1) {
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

                if (_table.equals("BON1")) {
                    db.execSQL("UPDATE CLIENT SET ACHATS = ACHATS - " + (bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise) + ", VERSER = VERSER - " + bon1.verser + ", SOLDE = SOLDE - " + ((bon1.tot_ht + bon1.tot_tva + bon1.timbre - bon1.remise) - bon1.verser) + " WHERE CODE_CLIENT = '" + bon1.code_client + "'");
                }

                db.setTransactionSuccessful();
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", sqlilock.getMessage());
        }
        return executed;
    }

    public boolean modifier_inv1_sql(String _table, String num_inv) {

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
                executed = true;

            } finally {
                db.endTransaction();
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
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

    public void backupDatabase() throws IOException {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        if (sd.canWrite()) {
            String currentDBPath = "//data//" + mContext.getPackageName() + "//databases//" + DATABASE_NAME;
            String backupDBPath = "backup_" + DATABASE_NAME;
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = null;
                FileChannel dst = null;
                try {
                    src = new FileInputStream(currentDB).getChannel();
                    dst = new FileOutputStream(backupDB).getChannel();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(mContext, "Backup Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Database does not exist", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Cannot write to external storage", Toast.LENGTH_SHORT).show();
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

    private boolean insert_wilaya_commune_into_database(SQLiteDatabase db) {
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

    public void insertproduits() {

        SQLiteDatabase db = this.getWritableDatabase();

        try {
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1', '9D3F8BF7GJB3A', 'XXRRZ72WTW65U', 'batri hamdi ', '3200.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('2', 'EG60H81BABJ7B', 'R0562X78TR49Z', 'filtr koksi bb', '370.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('4', 'FCCHF7B3K2F2J', '2U09V0ZR2UXTS', 'pinyo labwat koxi b', '2800.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('5', 'GEJAD95E10JB0', 'R0S14T96Y72UW', 'rizavwar znt b', '3500.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('8', '66K235KF9B3HE', '617S69Y21W156', 'sibor motar koxi b', '2650.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('9', '2464797B9G145', 'W-SVTZ0X-UR33', 'mayo roda Pista sam drayvar b', '1850.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('10', 'F581743DK405D', 'U2VT33217ZY2S', 'ciylandri R6 OPrud b', '3200.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('11', '1027', '1027', 'tablo 11 bobin', '1200.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('12', '8KHGCK3F3FKKH', 'YST9V85R7U408', 'Pisto 110 znt', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('13', '6975296970686', '6975296970686', 'diymarar R6 motorcLE', '1500.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('15', '6971383626825', '6971383626825', 'Ligali R6 mot sikl', '220.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('16', '955153530457', '6975416530257', 'sarso lombrayaj 103 ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('18', '4529697821300', '01822298', 'fier koxi', '5000.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('20', '11D8C86CA91K9', '85X2XV886UY10', 'tondar sansla bidal 103', '140.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('21', '0160', '0160', 'PAVITA ARYAR KOXI Khachab ', '1100.0', '0.0', '0.0', '2800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('22', 'diafi 2157', 'diafi2157', 'kaj kliki koxi', '2400.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('23', 'mhmd 141 koxi', 'mhmd 141 koxi', 'bwat kliki koxi ', '2800.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('24', '202104', '202104', 'ligali bostar malozi', '350.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('25', '1004782628008', '1004782628008', 'kafla ta3 daw 103', '100.0', '0.0', '0.0', '150.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('26', '4949902003121', '4949902003121', 'Larach rolma kombli', '3000.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('27', '5HG16F1122DJ6', 'TU439Z8UYSSTS', 'Lamba vayoz zdaj ', '150.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('28', '6975416530998', '6975416530998', 'Pisto kit 2 bwan 46', '6500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('29', '5017974808230', '6941797808230', 'Plak fran koxi aistayt kh', '270.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('30', 'KJ4536E5B1FE8', '11U330V9026U3', 'Pochat Jwane MBK', '150.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('31', '01J7D034KD8G6', '16000STVVU35R', 'kabl fran hmdi ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('32', '43450', '43450', 'kabl sym dor', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('33', '21fhm012-1010-008', '21fm012-1010-008', 'Pisto R6 Ham', '115.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('34', '6941797811711', '6941797819045', 'Lamba daw koxi', '2100.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('35', '6975308491307', '6975308491307', 'nayman F2 TNN', '1300.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('37', '1101485A', '1101485A', 'starter HAm', '530.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('38', '350-ASC-0003', '350-ASC-0003', 'SiPor motar tonik', '2600.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('39', ']C121FHM018-112060', ']C121FHM018-1120-051', 'liblak fran HAm', '450.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('40', '2681459', '2681459', 'karwa HAm 818', '2200.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('42', '6201', '6201', 'Rolma', '120.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('43', '1800433', '1800433', 'kasat HAm F3', '7500.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('44', '14401-A31-0003', '14401-A31-0003', 'chan motar tonik', '1100.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('46', '22102-ARA-0001', '22102-ARA-0001', 'Riyacha Sym F3', '1800.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('47', '43450-ANR-0001', '43450-ANR-0001', 'kabl frane Sym D ', '6500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('48', '5317A-ASC-9000', '512877382131393746608600', 'gabda frane tonik ', '1050.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('49', '28230-H6T-0000', '28230-H6T-0000', 'Pinyo kliki sym F3', '900.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('50', '28253-M7Q-0000', '28253-M7Q-0000', 'hlal tonik ta3 dakhal', '900.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('51', '6975308491307', '6975308491307', 'nayman TNN F2', '1300.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('52', 'CB-8027', 'CB-8027', 'sabo fran koxi', '1800.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('53', '45121-AAA-0000-M3', '45121-AAA-0000-M3', 'Disk fran sym f3 D', '1900.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('54', '31120-F6N-0000', '31120-F6N-0000', 'tablo kora sym D', '3900.0', '0.0', '0.0', '5600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('55', '1865A-XGA-0002', '1865A-XGA-0002', 'Kataliyzar sym D', '1750.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('56', '6976087310100', '6976087310100', 'H de force SR-S150', '3300.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('57', '53200', '53200', 'H de forch St', '3600.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('58', 'movo1036-000', 'movo1036-000', 'karwa 860*19,5*28', '1000.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('59', '2876576D', '2876576D', 'gali lombrayaj HAM 9g', '330.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('60', '17200-ANA-0001', '17200-ANA-0001', 'fitr AIR kombli ORBiT sym', '3500.0', '0.0', '0.0', '4900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('61', '14401-A31-0003', '14401-A31-0003', 'Lâchant motar tonik sym D', '1100.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('63', '2301A-ADB-0005', '2301A-ADB-0005', 'Lombrayaj tonik sym D', '7000.0', '0.0', '0.0', '9500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('64', '45121LM9QyN $', '45121-M9Q-N $', 'disk frane tonik sym D', '2900.0', '0.0', '0.0', '4100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('65', '1803517', '1803517', 'Rigilatar HAM', '5000.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('66', '22fHm004-0620-022', '22fhm004-0620-022', 'kasat R6 HAM', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('67', ']C121FHM018-1120-021', ']C121FHM018-1120-021', 'fibroka HAM', '4500.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('68', 'http://www.xsmt.com', 'http://www.xsmt.com', 'firiydo tonik sym', '9000.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('69', '22fhm004-0620-017', '22fhm004-0620-017', 'Joe diraksyo F3 HAM', '4500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('70', ']C121FHM018-1120-037', ']C121FHM018-1120-037', 'bone kombli R6 HAM', '3600.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('71', '22fHM004-0620-067', '22fHM004-0620-067', 'giyd sobab HAM', '160.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('72', 'QQ7-110', 'QQ-110', 'siylandri koxi so bisto', '2600.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('73', '1859024', '1859024', 'boji HAM', '250.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('74', 'http://www.xsmt.com', 'http://www.xsmt.com', 'kovar château j4', '800.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('75', '0CHEADH8HF58G', '-9U2549TTV0R9', 'vizibl khayt motiko ', '110.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('76', '6976506790643', '106503790643', 'Lamba 2 blon ', '40.0', '0.0', '0.0', '80.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('77', '6975296974707', '6975296974707', 'gbadi frane koxi ', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('78', '6975296979078', '6975296979078', 'onti chok drayvar', '650.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('79', '6941797810844', '6941797810844', 'chambrayar trotinat', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('81', '6976361360234', '6976361360234', 'tablo 8 bobin CG', '1200.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('82', '6011797811711', '6941797811711', 'lamba Gziyno koksi ', '2100.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('83', '6975296972130', '05669721', 'fitr kokxi pista', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('84', '6975308491307', '6975308491307', 'nayman FDL 2', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('85', 'MT215-OT-004', 'MT215-OT-004', 'sobab R6 Autoec', '300.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('86', '096', '096', 'Pisto znati 110 damane', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('87', '6975416530998', '6975416530998', 'Pisto kit 46 Ch', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('88', '6975418130257', '6075413530257', 'sarso lobrayaj PGT', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('90', '931183807974', '931183807974', 'kaja biyl PGT ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('91', '6941797808353', '6941797808353', 'firiydo lobrayaj PGT', '300.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('92', '1907004380001', '1907004380001', 'Bolon vidonj kombli R6', '160.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('93', '202105', '202105', 'Bolon vidonj koxi kombli ', '160.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('94', '085CBKAFHG5HK', 'U4Y9TWW38T8S1', 'tondar biydal PGT', '140.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('95', '6941797808230', '6941797808230', 'Plak frane koxi Okinwa ', '270.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('96', '6975296977449', '6975296977449', 'Pochat jwan MBK ', '150.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('97', '6975416530783', '056016000183', 'Pisto H PGT CH', '550.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('99', '7316577070616', '7316577061645', 'Rolma SKF D ', '950.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('100', '6975296973236', '6975296973236', 'Riyacha koksi ', '500.0', '0.0', '0.0', '850.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('102', '2C14DJFKGA564', '-3RRZWRYV-XTT', 'Rizavwar znt zarga ', '3500.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('103', '6975296973434', '6975296973434', 'Sibor motar kokxi ', '2650.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('104', '6976506790025', '6976506790025', 'Siylandri R6 UPPROUD', '3200.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('105', '711 18 30', '712 18 30', 'karwa Bando CH', '1200.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('106', 'K31967379F702', 'XZ0T5SXX8W3YZ', 'sarklibs sansla Pidal ', '15.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('107', '4F59A97H1J3CD', 'VXS36XTRS85WW', 'siyland Blok motar PGT haba', '10.0', '0.0', '0.0', '30.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('108', '6971383626825', '6971383626825', 'Ligali R6', '220.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('109', 'J1B8HGCJC871H', '334TWS1-X85--', 'kabl fran R6 FDl ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('110', '6976506790377', '6976506790377', 'Laks kliyki znt 110', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('111', '6941797802054', '904507802054', 'flotar R6 kombli ', '550.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('112', '6976506791398', '6976506791398', 'fiycha H4 ', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('113', '6976506790209', '6976506790209', 'gali bandisk znt 110', '150.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('114', '6900001000273', '6900001000273', 'jwan kiylas PGT 47', '20.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('116', '6941797810561', '6941797810561', 'Pnoe trotinat 10', '3200.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('117', '6941797810752', '6041797710752', 'Pnoe trotinat 8', '2800.0', '0.0', '0.0', '3800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('118', '6K17CKCEHD6A5', '423V86X6711S8', 'Laks martawat CYMAM', '850.0', '0.0', '0.0', '150.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('119', '1803022741401', '1803022741401', 'Bomba ziyt koxi ', '450.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('120', '591983626719', '54976719', 'rigilatar 5 fich fomal', '550.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('121', '6900001000471', '6900001000471', 'rigilatar kox mal ', '550.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('122', 'V0625', 'V0625', 'tablo kombli koxi ', '2500.0', '0.0', '0.0', '3600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('123', '6941797410471', '6941797410471', 'Lobrayaj R6 Aryar ', '2300.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('124', 'A-96', 'A-96', 'Lamba Lad Power', '1050.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('125', '3G38J9A8CEA1H', '-59U145R0-YTV', 'Lomrayag AVO TOYU 10''C', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('126', '6975308496173', '6975308496173', 'tondar lâchant koxi ', '300.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('127', '1859', '1859', 'Liblak fran F3 SENSEl', '260.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('128', 'YM056', 'YM056', 'abaray klinito ZNT ', '150.0', '0.0', '0.0', '350.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('129', '6975416530981', '7973419534981', 'Naymane F3 ', '1550.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('130', '9J1KHFBC24G1H', '-8U2355998X2Y', 'kabl fran visba rliyd ', '60.0', '0.0', '0.0', '10.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('131', 'KB6AFK1DH0253', '9-T91V8X84WY1', 'sarklibs sabo fran koxi ', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('132', '0086', '0086', 'Bobiyn kasat PGT khachab', '360.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('133', '1624-1', '1624-1', 'Nayman znt CG Daman', '500.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('134', '6941797411171', '6941797411171', 'Boji visba TNN', '90.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('135', '6976506790827', '6976506790827', 'Bomba Frane ziyt UPRUD', '1100.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('136', '5102KBJ5019HE', '4W171518Z7XX8', 'jwan kiylas PGT H ', '15.0', '0.0', '0.0', '40.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('137', 'C2A0F6GF1814G', '880Y6ZUZS382U', 'Ziyt Pon MAXXIM-OIL', '380.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('138', 'F0KE70HB58GDA', '2US8V37Y8-6T2', 'Ziyt motuL', '1500.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('139', 'AFB8H84B935JD', 'R3UVXW06V6SUW', 'Ziyt AiGZol dozyam', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('140', 'BHK7B825CJAHG', 'W7YY3-V3539X7', 'Rosol KRa3 doblia nwar ', '110.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('141', '6975296972130', '6975296972130', 'Fitre koxi Pista ', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('142', 'P001859', 'P001859', 'Liblak Disk fran F2', '260.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('143', 'HQ1T-215', 'HQ1T-215', 'Fibroka F3 TOYU 10Ć', '4500.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('144', '5TNE1630000', 'E1630000', 'Ligali yamaha koxi ', '540.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('145', 'DiAFi2209-305', 'DiAFi 2209-305', 'Bobiyn kasat ta3 dakhal', '360.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('146', '6941797804546', '6941797804546', 'Laks mortisal PGT CH', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('147', '90F6A2GHG3E64', 'TW85-V894S913', 'Lorma Pisto PGT moto Pyas D', '300.0', '0.0', '0.0', '450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('148', '3536660013977', '3536660013977', 'Karbiratar PGT DG Frons ', '4100.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('149', '1911011280001', '1911011280001', 'Tayo Ziyt fran F3 ', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('150', '6A6FKE50E1442', 'S28RT3V40V-6Z', 'Pnoe Diyro Asli DG ', '5400.0', '0.0', '0.0', '7200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('151', 'YMH- 100', 'YMH 100', 'Fitre koxi mdawar twiL ', '350.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('152', '8684122016617', '8684122016617', 'giydo MVl', '780.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('153', 'JGJ4G7510G11B', '18VS5W1466968', 'kaj forch kohliyn ', '220.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('154', '6975296970327', '6975296970327', 'kliyboji ', '750.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('155', '6975296970327', '6975296970327', 'kliyboji ksiyr VSP', '750.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('156', '8692791104559', '8692791104559', 'Fiyrido FRan RMZ', '630.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('157', '3D5FDHFHHHFHK', '6ZT7WW0-Z4VX5', 'Tayo lisons byad 50m', '1150.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('158', '6972885542774', '6972885542774', 'Sibor motar MVL demi ', '1150.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('159', 'B8H4CF4G5DH3G', '3969WU8R10WUU', 'Bolon Pwat kas MVL ', '80.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('160', 'E1028101', 'E1028101', 'Bolon kartar PGT RMZ ', '185.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('161', 'AB29K0GC57471', 'R791038SVU-S8', 'Riglar rliyd ', '280.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('162', '1610609180001', '1610609180001', 'Riyacha F3 ', '600.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('163', '2945', '2945', 'Lovi fran ZNT 70', '150.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('164', 'JD10F2K633KJG', '903V3T14YT18V', 'karwa PGT liys vantiko D', '295.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('165', '86203,00', '86203,00', 'Pisto PGT barmakit DR', '1150.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('166', '6972885544587', '6972885544587', 'kafla daw MVL ', '240.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('167', 'GlC5012N62', 'GlC5012N62', 'jiklar PGt DR FRANS ', '270.0', '0.0', '0.0', '450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('168', '6975296974035', '6975296974035', 'Kafla ZNt ', '60.0', '0.0', '0.0', '20.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('169', '4AE3979BCFA24', 'Z77RV321YY6SV', 'Siylandri EMO DR', '7100.0', '0.0', '0.0', '9200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('170', 'E1130001', 'E1130001', ' T Forch PGT RMZ kromi ', '21500.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('171', '0969F5A1K7ECH', '29708W6ST1VU9', 'CHatma ninja kromi ', '4200.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('172', 'G4B35GHF416E2', '4TV6000U5TS21', 'chatmo ninja roj ', '4050.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('173', '8E169BK1FJ99F', '6S2RU106313VW', 'Firiydo frane PGT galma ', '790.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('174', 'V0630', '202012', 'kaj OMR koxi VCX', '1150.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('175', 'S8', 'S8', 'kartab VOG', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('176', '8434829002587', '8434829002587', 'siylandri 2P DR RMZ ', '8600.0', '0.0', '0.0', '9800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('178', '8434829002556', '8434829002556', 'Siylandri PGT 2P 47 DR', '8850.0', '0.0', '0.0', '10500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('179', 'K2600C34EC24K', '20T38S054VXY3', 'Rolma Pisto PGT fo RmZ ', '115.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('180', '8684122010431', '8684122010431', 'Chatma PGT koti twiyla kromi ', '1130.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('181', 'K4K5GD1AFBK5J', 'Y6RTZZ20X05R3', 'LIblak Rizavwar PGT blastik ', '580.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('182', 'JJK410G947702', 'X-6VXU79ZWWR2', 'jwan siylandri Polini ', '7.0', '0.0', '0.0', '40.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('183', '065J5107BC33C', '696Z9T53Z7V66', 'Laks mortisar PGT RMZ ', '230.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('184', 'C7543B0H66DCD', '51X2YVZUSUWY1', 'Rolma tabsi moto Pyas DR ', '230.0', '0.0', '0.0', '350.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('185', 'BEDHB0GKB4B93', 'YX9TUZXS33X6W', 'RosouL korsi PGT RMZ ', '380.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('186', 'G1020001', 'G1020001', 'mayo lombrayaj PGT RMZ ', '1750.0', '0.0', '0.0', '2400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('187', '8CGBGJFFC9EB9', '63Z17W2T-5R99', 'bolon chatmo PGT ', '14.0', '0.0', '0.0', '30.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('188', '70gre', '70gre', 'griys koti sriyr ', '650.0', '0.0', '0.0', '120.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('189', 'PM.9025', 'PM.202Y', 'Lombrayaj Polini ', '900.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('190', '06025807', '06025847', 'Pisto PGT 47 DR ', '2150.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('191', '4711420860056', '4711420860056', 'Bobine MOT BKN COIL', '1200.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('192', 'MA-11', 'MA-11', 'klabi BWS 50 HlGHTAC', '480.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('193', '20168501250', '20168501250', 'Laks bidal kromi HMH ', '820.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('194', 'G1134001', 'G1134001', 'Chatma PGT RMZ ', '2400.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('195', '76GJ6DK6J3507', '2775Y22RRU88V', 'Sobab PGT RMZ DR ', '560.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('196', '6976185560018', '6976185560018', 'chan motar PGT 415 KMC', '870.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('197', '01E1G14562176', '5-362TT0UTXY8', 'T forch PGT BMS HMH', '2150.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('198', 'BYL-05 LED', 'BYL-05 LED', 'LED STONDAR ZRG', '850.0', '0.0', '0.0', '1450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('199', '1668', '1668', 'RYONE 17 HMD', '360.0', '0.0', '0.0', '450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('200', 'A57CB9HAK22JA', '-XSR8YR778X29', 'PNE 17 TALYANE ', '2500.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('201', 'F0K26A483FK28', '0267078ZVY88S', 'Batri VSP ta3 ma ', '3600.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('202', '420H', '420H', 'chan motar ZNT HMD GRD', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('203', '428H', '428 H', 'chan motar ZNT 428 GRD', '830.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('204', '12', '12', 'LAKS TRB 12 PGT lys ', '120.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('206', '0389', '0389', 'chan GRD 94 F3', '450.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('207', 'ABJ0E4HFH0E56', 'Z422X-S87939U', 'LAgane rliyda ', '1750.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('208', '206101111', '206101111', 'fibroka ARPRES rgiyg ', '5700.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('209', 'H33671GJ4D61A', 'T1-1X1WTRT99Z', 'mayo lombrayaj PGT CH ', '700.0', '0.0', '0.0', '1050.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('210', '955096971089', '6975296971089', 'joe diraksyo ligali TMP', '750.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('211', 'SX025', 'SX025', 'tabla kiylas R6 NASAKI', '700.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('212', '6975296977357', '6975296977357', 'bandisk Lorma TMMP', '1350.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('213', '6941797802313', '6941797802313', 'kaj siylandri kox ', '800.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('214', '6975296976817', '6975296976817', 'kaj siylandri R6', '1100.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('215', '6975296975025', '955096975025', 'PLAk FRan SAM ', '250.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('216', 'YAM1020-1', 'YAM1020-1', 'Lobrayaj kox aryar DAMAN', '2750.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('217', '6975296979238', '6975296979238', 'gsab lafoch kox ', '4500.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('218', '6975296973366', '6975296973366', 'gardbo kox 1 karbon', '2300.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('219', '6975296973366', '6975296973366', 'gardbo kox 1 nwar ', '1800.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('220', '16', '16', 'fiyroj j4 jijal ', '700.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('221', '6975416532176', '6975416532176', 'karbiratar 12 PGT CMO 2 ZYAM', '1750.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('222', '6975296976282', '6975296976282', 'Rodonti 56 PGT chafraa', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('223', 'BX140742', 'BX1407425', 'bandisk kox GROWSUN', '1200.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('224', '202011044', '202011044', 'Lakloch PGT SHC', '580.0', '0.0', '0.0', '950.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('225', '01', '01', 'Roda kox AV ARYAR ', '6500.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('226', '6975416530837', '6975416530837', 'Bandisk R6 khachab SHC', '1000.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('227', '9136582001496', '9136582001496', 'Sansla PGT 415 TRB', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('228', '6975296971584', '6975296971584', 'TRovizar KOX TMMP', '650.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('229', '5075000071553', '6975296971553', 'TRovizar JET4 TMMP', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('230', '02', '02', 'LaKloch MBK 51 CH', '700.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('231', '1901171802631', '745882287193', 'GARDBO ZNT AV ', '1300.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('232', '005503801376', '6973140467146', 'GARDBO ZNT ARYAR ', '1400.0', '0.0', '0.0', '2300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('233', '6975296977715', '6975296977715', 'LAPARAY kontar kox TMMP', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('234', '206101111', '206101111', 'Rosol motar PGTRES ARP', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('235', '1791', '1791', 'SaBo fran HMD nwar', '1550.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('236', 'QZ', 'QZ', 'kartar ZNT 110', '2500.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('237', 'KJA2D7H5H7117', 'R9SX2WT95VYS0', 'kabtar PGT HMD ta3 khayt', '230.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('238', '1041', '2041', 'jwan spi ZNT 110', '280.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('239', '0398', '0398', 'Chan ZNT 428 GRD NWR', '830.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('240', '0396', '0396', 'Chan ZNT 420 GRD', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('241', '075', '075', 'LaPiP ZNT 110 HMD', '150.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('242', '206101111', '206101111', 'forch AV PGT komPLi ARPRES', '4600.0', '0.0', '0.0', '5800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('243', '06408748470153', '510926484731', 'PNEU 110-70-16 DURO HMD', '5200.0', '0.0', '0.0', '7200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('244', '0234', '0234', 'Bolon motar komPli R6 HMD', '780.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('245', '1414', '1414', 'PLATO SAnsla ZNT 110 HMD', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('246', '1680', '1680', 'Rigilatar HMD 5 fich fomal kbiyra ', '900.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('247', '0202', '0202', 'BOUGiE HMD MATSXAM', '140.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('248', '3J79B407JJFA1', '402V8T-WWT50W', 'ROSOL PIKi dobli BYAd HMD', '70.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('249', '0622', '0622', 'Dimarar F3 HMD', '1800.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('250', 'FA4C49JBCB7GA', 'U8X2V0UWT-5W0', 'jalda laPiP 15', '15.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('251', '0391', '0391', 'CHANE MOTAR F3 94 GIRADO', '450.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('253', '6941797808773', '6941797808773', 'chatmo kox aistayt ', '5800.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('254', '6975996078583', '6975296078573', 'kra3 R6 khachab ', '1400.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('255', '6941797819472', '6941797819472', 'kaje kontar aistayt ', '1200.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('256', '6941797429572', '6941797429572', 'gartchan ZNT TNN ', '1500.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('257', '2302-385', '2302-385', 'fitr Vimax komPli ', '3500.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('258', '6941797818505', '6941797818505', 'chatmo Vimax 29', '9000.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('259', '6975296976732', '6975296976732', 'chatmo DRAYVAR khachab ', '8500.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('260', '6975296977678', '6975296977678', 'T Forch  DRAYVAR 29', '2850.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('261', '6941797818451', '6941727861861', 'T Laforch VIMAKS 29', '4500.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('262', '6941797818345', '6941708718344', 'LAPiP VIMAKS ', '650.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('263', '8942727878475', '002148618475', 'STALASYO VIMAKS 29', '4500.0', '0.0', '0.0', '6700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('264', '6941797818178', '6941797818178', 'GSAB LAFoch STANT ', '5500.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('265', '179573521295', '179533521020', 'chambrayar 3-50-10', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('266', 'TR87', 'TR87', 'chambrayar 120-130/70-12', '470.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('267', '5023584121294', '5023574520205', 'chambrayar 2-50/2-75-16 TR4', '360.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('268', '0884', '0884', 'Gardbo FAR HMD nwar byad', '1400.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('269', '6941797819496', '6941797819496', 'kaj far aistayt avon ', '1850.0', '0.0', '0.0', '3600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('270', 'J0G3CJJE2FCBK', 'S4-YR4T2T8XS7', 'kaj far STANT ', '3000.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('271', 'GH1B6JJ6G51E4', 'SY2-S0Y8R46TR', 'ZDAR STANT BOSTAR ', '3500.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('272', '901117804744', '8942767604744', 'disk Lombrayaj PGT', '100.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('273', '6975296975025', '6975296975025', 'Liblak SAM 1536', '250.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('274', '6975296974981', '6975296974981', 'Lontifit forch F3 drayvar ', '350.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('275', '6975416531933', 'HM-PG-048', 'kadna LOCK', '400.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('276', '1911008500101', 'ASD 2012-131', 'Flotar Rizavwar SAM ', '300.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('277', '6941797802603', 'ASD2308-299', 'NAYMAN KOX TMMP', '1350.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('278', '6975296971089', 'ASO-2203-272', 'jo dirikadyo kbiyra TMMP', '750.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('279', '6941797818185', 'ASD2308-196', 'jo dirikasyo Bostar MBK TMMP', '650.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('280', '6075996976909', 'ASD2308-388', 'PWANi R6 29', '1400.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('281', '1019471128001', '2012-1', 'LAPIP BOSTAR 50', '450.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('282', '6941797804690', '6941797804690', 'khayt BOGI', '90.0', '0.0', '0.0', '15.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('283', '6941797893380', 'ASD 2308-58', 'Bolon kiylas kox TMMP', '20.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('284', '6941797823486', '1001366', 'BRIYD CHATMA 51', '550.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('285', '6975871220571', 'YX-56', 'mayo kasat vititi', '2000.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('286', '6976506791114', 'WJ0201-001', 'Bolon PLATO ZNT 110', '15.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('287', '6941797818437', 'ASD 2308-26 ', 'PWANi VIMAKS TMMP', '1850.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('288', '8C3B9813CEK3A', '2324WS12-T5Y3', 'gsab LAforch Vimax ', '7000.0', '0.0', '0.0', '9500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('289', '2104-21', '2104-21', 'LARACh bandisk R6 29', '500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('290', 'WJ0401-004', 'WJ0401-004', 'LArach  vola R6 zarga ', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('291', 'D30E9DB4KB34G', 'XS360SYS8RSR8', 'LAKS mortisar PGT ', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('292', '58B3GCKH6JE57', 'R80TTR7W35042', 'Bolon chatma PGT maftah ', '14.0', '0.0', '0.0', '30.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('293', '6975296976411', 'ASD2308-263', 'Siylandri Bostar MBK TMMP 47', '2800.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('294', '6975296970686', 'ASD2308-260', 'DImarar R6 29 ALIM', '1450.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('296', '6975296977180', '00812130', 'SOPAP DRYVAR TMMP', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('297', 'ZD0107-025', 'ZD0107-025', 'PLAK FRAN VIMAX', '700.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('298', '8683709013643', '8683709013643', 'LAKlOCH RMZ D ', '1100.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('299', '6941797808032', '6941797808032', 'PLATO KARWA HDIYD TLIFON', '1350.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('300', '6941797818819', '6941797818819', 'PISTO PARMA KiT 29', '750.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('301', '50', '50', 'KLAPI BOStAR +OVITO ', '450.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('302', 'ASD2308-171', '6941797817973', 'KAG FIROG STNT BOSTAR 29', '300.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('303', '340497817942', 'ASD2308-168', 'FIROG STANT kOPLI 29', '1200.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('304', 'E92JG7811K092', 'XVW06-S3W54W3', 'PATRi 4 ORONG ', '2150.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('305', '6975416531230', '6975416531230', 'PISTO CG 150 MG', '850.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('306', '6941797811193', '6941797811193', 'KLIYNITO PGT KhACHAB ', '1000.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('307', '6975416531438', '1415', 'FIRIDO FRAN ZNT ', '300.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('308', '6941797804676', '6941797804676', 'ROLMA PIST PGT ', '100.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('309', '1489', '1489', 'DEMi POCHAT ZNT 110 HMD', '150.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('310', '3J7F5AKH2D842', '68T5-992S5847', 'FIRIDO PGT MVL ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('311', '955096974608', '192996974608', 'FiRIDO FRAN KOX khachab', '450.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('312', '905597819175', '6941797819175', 'KAGE RIYACHA AISTAYT 29', '400.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('313', '6975296976329', '6975296998635', 'KASAT 4 FiCh 29', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('314', '6975296979238', '6975296979238', 'GSAB LAFORCH KOX', '4500.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('315', '6941798703022', '6941798803326', 'STALASYO CG ZNT ', '1500.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('316', '6941797817959', '507197817959', 'FAR STANT 29', '1500.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('317', '6975308497101', '6975308497101', 'ROLMA PON KOX TNN ', '180.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('318', '6941797803839', '6941797803839', 'LAKS KLIKI SPOURESTI ', '2100.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('319', '202010', '202010', 'KARTAR PON KOX ', '1750.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('320', '6941797802863', '6941797802863', 'PINYO LABWAT KOMPLI ALIM ', '2400.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('321', '1906019828008', '1906019828008', 'DEMI POCHAT DRAYVAR ', '150.0', '0.0', '0.0', '450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('322', '6975296979467', '6935299979468', 'KRA3 KOX khachab ', '1400.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('323', '0100198128001', '0100198128001', 'SIYR KAPL KOPRISAR PGT', '14.0', '0.0', '0.0', '30.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('324', '1211-66', '1211-66', 'KARKAZA GRi PGT ', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('325', 'A203', 'A203', 'LiPLAK FRAN BOSTAR TNN ', '250.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('326', '6941797816082', '6941797816082', 'BOMBA FRN R6 ZIYT ', '1150.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('327', '6941797804263', '6941797804263', 'TROVIZAR PGT 103 KROMI 2', '850.0', '0.0', '0.0', '1450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('328', '6941797804362', '6941797804362', 'FIROG SPIX', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('329', '6941797802641', '6941797802641', 'KAG KLIYKI KOX 29', '2200.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('330', '2E36F16D23769', '9Y6U7ZXYU0WTS', 'ROLMA LOMBRAYAG BIKAN 51', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('331', 'CD110', 'CD110', 'SOPAP ZNT 110 DMAN', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('332', 'A-18', 'A-18', 'KARWA 743 BANDO FO', '1350.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('333', '7A0HG9ACFBD72', '654W4-W1UXWTT', 'BOLON LOMBRAYAG 32 PGT ', '70.0', '0.0', '0.0', '100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('334', '6975296976756', '6975296976756', 'RODA AISTAYT 29', '7000.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('335', '6941797811643', '6941797811643', 'LAKS KLIKI 147 TMMP', '460.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('337', '6976314691019', 'SR,JET4C7361', 'TROVIZAR J4 HAM ', '800.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('338', 'XIA22300 F6C-0100', 'XIA22300 F6C-0100', 'Firido Lombrayaj HAM ', '2500.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('339', '6976314690623', 'MIN,TH,3,5mm', 'DISK FRANE ARR J4 F3 Xsmart', '1750.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('340', '6933882533684', '110-70-12 C922 TL', 'PNOU 110/70-12 CST', '6000.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('341', '2,50-17 4PRTTP211BLK', '2,50-27 4PR TTP211BLK', 'PNOU ZNT HAM 2,50-17', '2300.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('342', '6975222075188', '6975222075188', 'PNOU 2,75-17 HAM', '2600.0', '0.0', '0.0', '5300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('343', 'http://www.xsmt.com', '33100-ATA-0200', 'FAR J4 SYM RS', '7800.0', '0.0', '0.0', '9800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('344', 'M0V01036-000', 'M0V01036-000', 'KARWA SR HAM 860*19,5*28', '1000.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('345', 'XIA04039', 'XIA04039', 'karwa HAM 842*20*30', '900.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('346', '6941797801071', 'HA-2308-22', 'LAPIP R6 KMTO ', '400.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('347', '2876521 HAM', '2876521', 'LOPRAYAG AV HAM R6 ORPIT ', '1650.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('348', 'XIA4312A-HIA-0000', 'XIA4312A-HIA-0000', 'FIRIDOU FRANE HAM F3', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('349', '6941797816778', 'HA-2308-7', 'KARBIRATAR R6 KMOTO', '3000.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('350', '4312A-H1A-0001', '4312A-H1A-0001', 'FiRIDOU FRANE SYM F3 D', '1300.0', '0.0', '0.0', '2300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('351', '003', '003', 'KRO LOMBRAYAG RR ', '100.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('352', 'http://www.xsmt.com', '11102-M9Q-3000', 'SIYLAND BLOK SYM F3 ', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('353', '41056905', 'CUXI 110 G7876', 'FIRIYDOU FRANE KOX HAM', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('354', ']C1S9RGD7-GL5-8514', 'S9RGD7-GL5-8514', 'ZIYT PON SYM D', '600.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('355', 'http://www.xsmt.com', '19 10 27 ADB', 'MORTISAR SYM D ARYAR ', '3100.0', '0.0', '0.0', '4700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('356', '21FHM018-1120-005', '21FHM018PPS0-005', 'CHAN 94 HAM ', '500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('357', '6941797800777', 'HA-2308-20', 'LOMBRAYAG AV SYM CH KMOTO', '1900.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('358', '915300404833', 'KOD 4029 HAM', 'TABLO 11 BOBIN HAM ', '1650.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('359', '6976314690777', 'G7746-HAM', 'BOMBA ZIYT MOTAR HAM', '900.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('360', ']C121FHM018-1120-048', 'H201-HAM', 'ROLMA DE PON KOMPLI HAM ', '2050.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('361', '2037 HMD', '2037 HMD', 'FIBROKA R6 HMD', '3600.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('362', '711×18×30 GH', '711×18×30 GH', 'KARWA 711 GH 3ATAf', '680.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('363', '44', '44', 'VIZYAR KOX BAYDA ', '1150.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('364', 'BB48HDF3A63JF', '63UYT-YTX8YZ2', 'KAG KLIYKI R6 TNN 150', '2000.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('365', '6976285390171', '700X40C F/V33 M', 'CHAMBRAYAR 700 HAM ', '340.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('366', '26×1,75', '26×1,75', 'CHAMBRAYAR 26 HAM ', '270.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('367', '12/1,95', '22/195', 'CHAMRAYAR 12 HAM', '230.0', '0.0', '0.0', '350.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('369', 'G7850 HAM ', 'G7850', 'GABDA FRANE ZYT HAM ', '1600.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('370', 'H 153 ', 'H 153', 'GABDA FRANR ZIT HAM', '1600.0', '0.0', '0.0', '2900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('371', '53140-XFA-0000', '53140-XFA-0000', 'PWANi PLASTIK SYM D F3 ', '700.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('372', '693847483203', '2,75-17 DURO ', 'PNOU DURO ZNT HMD ', '2600.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('373', '6975296974608', '2209-140 khachab ', 'firiydo fran kox YAmaha khchab ', '450.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('374', 'G,H', 'G,H', 'FIBROKA  KOX G,H 44', '4250.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('375', 'SR150', 'SR150', 'CHAPO BOGI F3 G,H', '115.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('376', 'BA20D', 'BA20D', 'LAMBA BOSTARE MBK ', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('377', '842 20 30', '842 20 30', 'KARWA GH 842', '600.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('378', '6941797819199', '6941797819199', 'TROVizar MVL TMMP', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('379', 'H 11', 'H 11', 'SIYLANDRI H 11 DRG mot pyas ', '7700.0', '0.0', '0.0', '9500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('380', '1213', '1213', 'motar visba R6 HMD ', '42000.0', '0.0', '0.0', '49000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('381', '100/90-10', '100/90-10', 'chambrayar DURO HMD', '420.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('382', '120/70-12', '120/70-12', 'chambrayar DURO HMD ', '420.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('383', 'GH', 'GH', 'FITRE GH J4 demi ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('384', '1983', '1983', 'PNOE KOX HMD 100/90-10', '3600.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('385', '700×40CCB 608', '700×40CCB 608', 'PNOU 700 TALYANE HMD ', '1150.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('386', '26×1,95 CB 531', '26×1,95 CB 531', 'PNOE 26 TALYANE HMD ', '950.0', '0.0', '0.0', '1450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('387', 'K19JK1KC04CG0', 'YT9TS922Z3557', 'jloda lobrayaj F3 ', '100.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('388', '092HEEG3EJ570', 'UW-RZTX268XXT', 'BATRi 12 ,07 HMD ', '2800.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('389', '533A412E532F9', '221WX92136948', 'GABDA FRANE GOCH SYM DR', '450.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('390', 'http://www.xsmt.com', '61100-AAA-0000-K', 'gardbo j4 SYM DR', '4500.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('391', '6100 XFA', '6100XFA', 'gardbo avon SYM F3 DRG ', '9000.0', '0.0', '0.0', '11500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('392', 'http://www.xsmt.com', '8360A', 'lajnab j4 SYM DRG ', '5000.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('393', '80107-ANA-0003', '80107-ANA-0003', 'gardbo tali j4 SYM DRG', '850.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('394', '53205-X3A-0000-BA', '53205-X3A-0000-BA', 'kaj far j4 SYM DRG ', '3800.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('395', '64310-ATA-0002', '64310-ATA-0002', 'tapi kar3in j4 SYM DRG ', '1700.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('396', '81131-ATA-0003', '81131-ATA-0003', 'zdar ta3 maftah j4 SYM DRG ', '2500.0', '0.0', '0.0', '4700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('397', 'http://www.xsmt.com', '42601-ABA-0003-K', 'RODA AVO F3 SYM DRG ', '7500.0', '0.0', '0.0', '15000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('398', '64301-XFA-0000-BV', '64301-XFA-0000-BV', 'MASK AVO F3 SYM DRG ', '9300.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('399', 'http://www.xsmt.com', 'http://www.xsmt.com', 'kaj far j4 SYM DRG', '9300.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('400', '44600-XFA-9000-S1', '44600-XFA-9000-S1', 'RODA F3 SYM DRG ', '7500.0', '0.0', '0.0', '10500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('401', '42602-ABA', '42602-ABA', 'ROUDA j4 SYM DRG ', '6400.0', '0.0', '0.0', '9200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('402', '44601', '44601', 'ROUDA SYM AVON  F3 200 ', '6900.0', '0.0', '0.0', '10500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('403', '333', '333', 'ROUDA ORBIT SYM DRG nwar ', '6200.0', '0.0', '0.0', '9200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('404', 'P43T', 'P43T', 'LAMBA H4 12V HAM ', '200.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('405', '6976314690647', '6976314690647', 'DiSK FRANE j4 HAM', '1750.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('406', '33400', 'SOC-A90030', 'KLINIYTO AVN SYM DRG ', '2750.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('407', '44830', 'http://www.xsmt.com', 'KABL kontar SYM F3 DRG ', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('408', '31120', '31120-Z7A-0001', 'TABLO KORO SYM F3 DRG', '2900.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('409', '31200', '31200-GY6-0000', 'DIMARAR R6 SYM DRG', '3000.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('410', '43105-ARB', '43105-ARB-000-A-9', 'LIPLAk FRAN ARYAR SYM DRG ', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('411', ']C121FHM018-1120-008', ']C121FHM018-1120-008', 'KLIKi F3 BARANi HAM ', '650.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('412', '6975596974070', '711 18 30', 'karwa BANDO SYM ', '1000.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('413', '35010-F6T-0100', '35010-F6T-0100', 'NAYMAN TONIK SYM DRG', '2600.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('414', '23100', '23100', 'karwa TONIK SYM 23100', '2500.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('415', '31600-N7F-0001', '31600-N7F-0001', 'RIGILATARE F3  SYM DRG', '1500.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('416', 'SAE 85W / 140 GL-5', 'SAE 85/ 140 GL-5', 'ZIYT LABWAT SYM DRG', '600.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('417', ']C122FHM004-1020-100', 'H101-1HALF', 'POCHAT JWAN DEMI HAM j4', '300.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('418', '0303015C', '0303015C', 'SIYLANDRI R6 150 HAM', '3600.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('419', '6015256', '6015256', 'Chatmo far far HAM', '5000.0', '0.0', '0.0', '7200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('420', '7H696306731A1', 'SSY54Z651-03R', 'kovar rizavwar PGT 103', '270.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('421', '55172', '6975308499006', 'LIPLAK KOX TNN ', '180.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('422', '53200', '53200-1LA-PT', 'T LAFORCH F3 ', '2800.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('423', 'XlA2812A-xBA-XB3-0001', 'XlA2812A-BA-XB3-0001', 'BANDISK F3 HAM HMAR ', '4300.0', '0.0', '0.0', '5600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('424', '1711A-ANA-0003', '1711A-ANA-0003', 'LAPIP F3 SYM DRG', '1450.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('425', 'XIA50350-ANA-0001', 'XIA50350-ANA-0001', 'SIPOR motar F3 HAM', '2650.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('426', '19510-XL1-0000', '19510-XL1-0000', 'RIYACHA TONIK SYM DRG', '450.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('427', '12391-GYB2-A000', '6976049432505', 'jwan kilas orbit 2 SYM', '300.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('428', '11345-ABA-0005', '11345-ABA-0005', 'TAYO kaj lobrayaj SYM DRG', '800.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('430', '11100-A2C-0000', '11100-A2C-0000', 'KARTAR F2 SYM DRG', '4500.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('431', '11341-ASC-0200-K1', '11341-ASC-0200-K1', 'kaj kliki TONIK SYM DRG', '5000.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('432', '12310-GYB2-0001', '12310-GYB2-0001', 'KOVAR kilas ORBIT SYM 150', '1500.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('433', '45451-N02-0003', '45451-N02-0003', 'krochi kabl kontar SYM DRG', '120.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('434', '14721-GYB2-A000', '14721-GYB2-A000', 'SOPAP ORBIT SYM DRG', '1400.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('435', '2211A-ABA-0000', '2211A-ABA-0000', 'LOBRAYAG F2 SYM DRG ', '2500.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('436', '14401-M92-0031-M2', '14401-M92-0031-M2', 'LACHAN 94 SYM DRG 94', '1300.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('437', 'XlA2812A-A3F-0006', 'XIA2812A-A3F-0006', 'BANDISK TONIK SYM DRG', '2950.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('438', '35010-X8A-0100', '35340-X8A-4036', 'NAYMAN F3 SYM DRG', '3050.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('439', '23001-ANT-0005', '23001-ANT-0005', 'LOMBRAYAG ARYAR F3 SYM DRiG', '1150.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('440', '12101-GYB2-A000', '12101-GYB2-A000', 'SiYLANDRI ORBIT F2 SYM DRG ', '4500.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('441', '8350A-ATA-0000', '8350A-ATA-0004-KC', 'LAJNAB J4 SYM DRG', '98000.0', '0.0', '0.0', '13000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('442', 'http://www.xsmt.com', '8360A ANA', 'LAJNAB J4 SYM DRG', '98000.0', '0.0', '0.0', '13000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('443', 'YX-95', '951161221455', 'korsi VTT fortune rosol', '800.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('444', 'YX-49', '6975871220281', 'GBADI fran VTT 12', '160.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('445', '1425', '6941797820331', 'LAKS kliki twiyl khachab', '480.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('446', '26', '26', 'FIRIDO KOXi YAMAHA SAFRIN ', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('447', 'S134', '6976886900434', 'LAKS VTT +CHBAK KOMPLi BYAD ', '150.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('448', '06', '06', 'kaj daw koxi 2 VMS HAMRA ', '1500.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('449', '06', '06', 'LAKAlONDR KOXI 2 TA3 gdam karbo', '3500.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('450', '6941797813432', '6941797813432', 'SOPAP KOXI TMMP', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('451', 'ART N0:fIT0888-01', 'ART N0:FIT0888-01', 'LIPLAK TROTINAT FITTOO', '450.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('452', '33-1670', '6975296974165', 'KARBIRATARE VOG CH KHACHAB ', '1550.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('453', '06', '06', 'GARDBO KOXI VMX 2 KARBON ', '3900.0', '0.0', '0.0', '6000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('454', '045', '045', 'ZDAR KOXI VMS TA3 MAFTAH DRG ', '5000.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('455', '0213', '0213', 'PWANI MOB PGT 103 ', '1300.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('456', '57-584 27,5×2,125', '57-584 27,5×2,125', 'PNU 27,5 KPA ', '850.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('457', '26×1,75 47-559', '26×1,75 47 -559', 'PNU 26 ×1,75 LY 161 HDH', '800.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('458', '48', '48', 'SIR KABL RLIYDA maftah 10', '24.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('459', 'S78', 'S78', 'CHONJAR VTT KSIRATAR CH super', '300.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('460', 'S176', 'S176', 'KLINITO VTT FY-202', '500.0', '0.0', '0.0', '750.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('461', '48', '48', 'RODA 12 AVO ARYAR CH ', '1800.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('462', 'VE-31', 'VE-31', 'LAFORCH  20 VTT CH NORMAL ', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('463', 'S49', '6976886900359', 'DIRAYAR VTT PLATO ta3 FOG SKY super', '520.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('464', 'S118', 'S118', 'TIP KORSI DRIGIN AL 6061', '600.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('465', 'S72', '6976886900366', 'SCHONGAR VTT Skype super', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('466', '098-1 SHC', '6975416530233', 'BOBIN TABLO KASAt MBK ', '300.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('467', 'WR180-140-095-6', 'WR180-140-095-6', 'LIGALi BANDO DRG SYM', '160.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('468', 'S0FAMPLAST', 'S0FAMAPLAST', 'TAYO LISONS KHAL 20 MATR ', '650.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('469', 'YX-90', '6975871220410', 'SIPOR TA3LAG VTT ', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('470', 'LH4TH-845', '6973761002764', 'LAKS BLOKAG VTT KHAWI AVO ARYAR ', '220.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('471', 'JOC 075', 'JOK 075', 'MORTISAR AISTAYT RGIYGA VMS DRG', '4000.0', '0.0', '0.0', '6000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('472', '6941797820423', '99-2201', 'LAKS KLIKi F3 CHN KHACHAB ', '850.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('473', '61-62cm', '61-62cm', 'KASK VMS DRG kahaL', '3800.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('474', 'S211', '6976886900670', 'GAS3A VTT SRiYRA TARA LAWAL ', '18.0', '0.0', '0.0', '40.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('475', 'YX-78', '6975871221349', 'RODA 12 RYON VILO AVO ARYAR ', '1800.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('476', '1242', '6941797808018', 'TABSI KARWA MARSODAS ZRAG KhACHAB', '1900.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('477', '6941797802313', '6941797802313', 'KAJ SIYLANDRI KOXi GH 3ATAFI ', '700.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('478', '6975296973236', '6975296973236', 'RIYACHA KOXI VONTLATAR GH ', '300.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('479', 'CUK046GRIS, A', 'CUK046GRS,A', 'LAJNAB KOXI 1 VMS DRG ', '6000.0', '0.0', '0.0', '8400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('480', '06', '06', 'LAJNAB VMS 2 GRI SORI HABA WAHDA ', '400.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('481', '59-60 ', '59-60', 'KASK VMS DRG DOT NWAR tay L', '5100.0', '0.0', '0.0', '6000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('482', '46', '46', 'TABi STANT 46 kolar', '400.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('483', '0158', '6975296970914', 'GARDBO KOXI 2 CH khachab ', '2550.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('484', 'XlA1210A-A61-000', '6976087310131', 'SIYLANDRI F3 HAM ', '4800.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('485', '53200', '53200-ALA-PT', 'T LAFORCH F3 NMAX', '2800.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('486', '038', 'CUK 038', 'T FORCH KOXI CH Dèsignation', '2300.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('487', '2209-85', '6941797608014', 'MORTISAR PGT 103 ROSOL TWAL', '1950.0', '0.0', '0.0', '2800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('488', '001', '001', 'MORTISAR PGT 103 GRI CYMAM STAR', '1300.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('489', 'XIA1310A-A6-0401', 'XIA2310A-A6-0401', 'PISTO F3 HAM ', '1450.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('490', 'XIA1310A-GYB2-000', 'XIA1320A-GYB2-000', 'PISTO F2 HAM 150', '1150.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('491', '13', '13', 'LIZONTIFIT mot pyas DRG', '170.0', '0.0', '0.0', '350.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('492', '1-D24', '1-D24', 'karwa 1-D24 CH', '140.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('493', '29', '29', 'KiVAT MAYO VTT ARYAR HACHYA ', '18.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('494', '9136583001116', '9136583001116', 'pinyo ta3 topsi 11 TRB', '230.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('495', 'MT301', '6972885545508', 'LIBLAK DRYVAR Autotec', '250.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('496', 'HM-PG-040', '6975416532145', 'LOMBRAYAG MBK 51 EURO CYCLE', '4700.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('497', 'jOGVMS', 'jOGVMS', 'LIBLAk fran  JOGVMS', '280.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('498', '747 15,5', '3289157', 'KARWA BOSTAR MBK POWER', '650.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('499', '1630', '6975296974554', 'SIYLANDRI KOXI YAMOYO', '3350.0', '0.0', '0.0', '4800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('500', '29', '29', 'TAPI KOXI KOLAR VMS ', '400.0', '0.0', '0.0', '750.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('501', 'B-16 743', '6971383620533', 'KARWA FO BANDO Motorcycle', '900.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('502', 'B-10 743', '6971383620533', 'KARWA 743 BELT CH', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('503', '9', '9', 'VIZYARA KOXI BAYDA VID ', '1100.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('504', '9', '9', 'ViZYARA KAHLA for VID ', '600.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('505', 'S25', '6976886900311', 'MCHATI S VILO SRAR kolaR ', '200.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('506', 'ASD2308', 'ASD2308', 'KATA FOT gardbo VTT BITWIN ', '50.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('507', '1215', '0200197680008', 'FIRIDO FRANE MOTO BIKAN ', '300.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('508', '1091-92SHC', '1091-92 SHC', 'BANDISK L50 BSTAR ', '550.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('509', '1636', '5975159984578', 'KASAT KOXI YAMOY', '1100.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('510', 'YAM60', 'YAM 60', 'KASAT CG DAMAN ZNT ', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('511', 'MT302 PG001', 'MT302 PG001', 'TAMBOR FRAN PGT MVL ', '960.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('512', 'MT546-OTOO4', '6972885543122', 'TAMBOR FRANE PGT AVON ', '960.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('513', 'ASD2308-506', '3901763804997', 'PWANI PGT 103 darwat klasik ', '630.0', '0.0', '0.0', '1300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('514', 'ASD2308-251', '6975296976916', 'LOVI FRAN KOXI G', '180.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('515', '8A60C235JGHHJ', '62V9Y0U79Y906', 'PLATO S VILO ', '800.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('516', 'YAM 1653', '2107009480001', 'ROLIP TA3 03 YAM ', '600.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('517', 'ASD2308-529', '6941797804379', 'KIYLAS FOX PGT FO RMZ ', '1250.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('518', 'S285', '6976886900403', 'LOVI FRAN TROTINAT ', '1100.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('519', 'S272', '6976886900328', 'KRA3 TROTINAT ', '600.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('520', '1606', '6941797808131', 'BANDISK R6 LORMA YAM ', '1250.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('521', '2310-24', '6941797821376', 'DIMARAR R6 GH', '1450.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('522', 'TR02', '6941797810721', 'PNE TROTINAT 10  khachab ', '2500.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('523', '6976378500258', '6976378500258', 'TAMBOR FAN CHAFRA TDH MARNiYA ', '1100.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('524', 'DYAFI -2061', '1000989628001', 'LIZONTIFIT BOSTAR MBK ', '125.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('525', '20-2204', '6941797821000', 'RIGILATAR 6 FICH khachab ', '750.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('526', '1168-2', '1006271780001', 'KLAPI PGT MALOZI khachab ', '550.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('527', '29', '29', 'JWAN CHATMA TA3 NHAS ZNT ', '17.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('528', '29', '29', 'JWAN CHATMA R6 SRiR ', '25.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('529', 'CUK029', 'CUK029', 'KATALIZAR KOXI VMS DRG ', '1650.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('530', '2021', '6941797420210', 'PINYO+LACHAN ZNT 110 TNN ', '280.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('531', '2310-415', '6975296977098', 'SABO FRAN R6 AV YAM ', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('532', '2-1396', '6975296977098', 'SABO FRAN ARYAR R6 YAM ', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('533', 'MT219-0T015', '6972885545539', 'RIBONI PGT MVL Autotec', '260.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('534', 'A01629013-6', '6941797813531', 'KLIKI TA3 BARA KOXI K moto', '450.0', '0.0', '0.0', '900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('535', '95-2201', '6941797802245', 'KARTAR KOXI TA3 LAJORJ CH', '2850.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('536', '669417,DO', '044031075125', 'LIGALI BOSTAR MBK ', '550.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('537', '99-2201', '8945717820423', 'PINYO KLIKi TA3 DAKhAL DRAYVAR ', '800.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('538', '2203-360', '4092563675753', 'PINYO KLIKI BSTAR OVT ', '300.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('539', '6975296972345', '6975296972345', 'FICHA K7 R6  fi 2', '70.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('540', 'CG125', 'CG125', 'NAYMAN CG ZNT DAMAN ', '30.0', '0.0', '0.0', '70.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('541', '006746', '209830067461', 'ZDAG KONTAR KOXI 2 CH', '650.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('542', '119-2209', '6975296971256', 'DAW ZNT AV Khachab ', '950.0', '0.0', '0.0', '1700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('543', '31×43×10,3', '6975296977838', 'LIZONTIFIt FFORCH DRAYVAR ', '30.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('544', '29', '29', 'PORTAKLi VMS khayt kolar ', '130.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('545', 'SYM-150', '6102320018926', 'SIYLANDRI SYM F3 TOYU ZRAG CH', '3800.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('546', 'A037KY01806MB', 'A037KY01806MB', 'SIYLANDRI E 32 OUMURS', '3900.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('547', 'MA-05', 'MA-05', 'JWAN LMBRAYAG AV BOSTAR MBK ', '14.0', '0.0', '0.0', '30.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('548', 'CHN-024 2310-97', '6941797814194', 'GABDA FAN ZIYT F3 CH DRWT ', '1200.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('549', 'DRIV040', 'DRIV040', 'KAG BATRi DRAYVAR DRG', '500.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('550', '8/X2', '8/X2', 'JONT TROTINAT 8', '3500.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('551', '2310-1821-2210', '6941797808773', 'CHATMO AISTAYT KhaCHAB ', '5200.0', '0.0', '0.0', '7200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('552', '6941797811063', '6941797811063', 'FIROJ TROTINAT ', '50.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('553', '6941797833003', '6941797811001', 'FIROG TROTINAT ', '500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('554', 'BK037012', 'BK037012', 'KOLA BYANSA', '60.0', '0.0', '0.0', '100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('555', '10204', '3961767807905', 'KONTAR KOXI 01 CH ', '4200.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('556', '15100-5wB', '15100-5WB', 'BOMBA MOTAR ZIYT KOXI ', '450.0', '0.0', '0.0', '950.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('557', 'SC02160', 'SC02160', 'SIYLANDRI R6 L 50 FDf ', '2100.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('558', '2209-157', '1000487328003', 'LOMBRAYAG ARYAR SRYRA L 50', '3000.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('559', '2209-158', '1007457228003', 'LOMBRAYAG AV L50 SRIYR ', '1200.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('560', '29', '29', 'Rigilatar Z CG 110 zarga', '300.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('561', 'S88', '6976886900373', 'PLATO VTT ALIMINYOM SRIR ', '1200.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('562', 'TR03', '6941797810738', 'PNOU TROTINAT 8', '2500.0', '0.0', '0.0', '4000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('563', 'CUK007', 'CUK007', 'ANTI CHOK KOXI TA3 PWANI VMS ', '500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('564', 'ASD 2308-65', '6941797816754', 'ONTI CHOk KOXI CH PWANI ', '320.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('565', 'NGK', 'NGK', 'BOUGIE NGK DRG R6 ', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('566', '14401-FY100-HJ', '14401-FY100-HJ', 'LACHAN KOXI MOTAR CH ', '330.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('567', '1108', '6975296972772', 'LIZONTIFIT DRAYVAR MOTAR ', '330.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('568', '45121-AAA-FC', '45121-AAA-FC', 'DISK FRAN AV F3 CH ', '1100.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('569', 'GY6-80STD+', 'GY6-80STD+', 'PISTO L80 ta3 koti ', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('570', '1198', '6941797808087', 'LAKS KLIYKI R6 TWIL KhACHAB ', '500.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('571', '1538', '6975296975049', 'ROSOL FORCH SP 103 TWIL Khachab ', '200.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('572', 'YAM 1211-16', 'YAM 1211-16', 'LAMBA MATRIKIL KOXI CH KOPlI', '200.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('573', '1-1366', '6941892860461', 'ZDAJ KONTAR j4 khachab ', '500.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('574', '6941797822076', '6941797822076', 'KARBIRATAR R6 GH 3TF', '2650.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('575', 'G,H', 'G,H', 'KIYLAS R6 GH 3TF KOMPLI ', '3800.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('576', 'MMG 2310-86', '6941797821833', 'POMPA NAFS GH DEMI 3TF DRG ', '270.0', '0.0', '0.0', '450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('577', 'MMG 2310-68', '6941797817904', 'CHAMBRAYAR 17 GH 3TF ', '350.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('578', '6941797822137', '6941797822137', 'DIRAYAR VTT GH  CHIMANO 3TF', '550.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('579', 'MMG-2310-121', '6941797790122', 'KADNA KBIRA 3TF BuuKer', '650.0', '0.0', '0.0', '950.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('580', '6941797820805', '6941797820805', 'GSAP KOXI AV GH 3TF ', '4100.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('582', 'VMS KOXi', 'VMS kOXi', 'KARINAJ KOXi 2 VMS NWAR ', '5500.0', '0.0', '0.0', '8000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('583', '6958132700173', '6958132700173', 'BOUGIE R6 EFFECTIVE', '230.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('584', '110/70-12 TL', '6976886900168', 'PNOU F3 110/70-12 TL CSCT CH', '4200.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('585', '1-D 24', '1-D 24', 'KARWA ventico 1-D 24 DRG RMZ', '360.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('586', '053089960161', '6975279960161', 'BOUGIE EYCUEM CH RKhIYSA ', '180.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('587', '06020846', '8434829010117', 'PISTO KIT 46 DRG RMZ ', '2150.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('588', 'E1139001', 'E1139001', 'TAMBOR FRAN RMZ AR ', '950.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('589', '31', '31', 'GSAB LAFORCH PGT RMZ ', '1600.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('590', '31', '31', 'LAFORCH AV KROMI PGT RMZ', '4800.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('591', 'E1134001', 'E1134001', 'CHATMA PGT RMZ DRG ', '2380.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('592', '100992', '6941797805116', 'KARBIRATAR PG DAKAR CH 15', '1500.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('593', '6976185560018', '6976185560018', 'SANSLA SAFRA PGT 415 KMC', '880.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('594', 'E1074011', 'E1074011', 'KORSI PGT RMZ HMAR ', '3200.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('595', '13', '13', 'GARTCHAN PGT KAHLIN CH', '450.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('596', '13', '13', 'GARD FORCH PGT 103 KOHLIN CH', '250.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('597', '6976886900014', '6976886900014', 'CHAMBRAYAR TROTINAT 8/×2', '750.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('598', '8683709206311', '8683709206311', 'H  KROMI+GRI PGT RMZ HMH', '2150.0', '0.0', '0.0', '2750.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('599', '31', '31', 'KARWA PGT MDRSA Vantiko DRG ', '375.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('600', '8692791100506', '8692791100506', 'SIYLANDRI PGT RMZ DRG ', '7500.0', '0.0', '0.0', '9500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('601', '034,051', '034.051', 'FIBROKA PGT GIR0DO 3 AKS ORPA ', '6950.0', '0.0', '0.0', '8200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('602', '8683709000209', '12000200', 'KARBIRATAR PGT RMZ 12 DRG', '3000.0', '0.0', '0.0', '3800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('603', '8434823014364', '04020640', 'KIYLAS PGT ARSAL ', '1900.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('604', '1143', '553193979320', 'FITR KOXI AISTAYT PLA Khachab ', '550.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('605', '6976866900836', '004486900236', 'BNOU VOG 2,25-16 TT ', '2000.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('606', '19', '19', 'ROSOL FRAN PGT khal ', '18.0', '0.0', '0.0', '50.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('607', 'G1,E2!', 'G1016001', 'LOMBRAYAG PGT RMZ DRG', '6800.0', '0.0', '0.0', '9000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('608', '2209-86', '2104016728001', 'LAPARAY KLINITO KOXI 2 CH ', '950.0', '0.0', '0.0', '1450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('609', '48', '48', 'BOMBA LISONS R6 3 TAYO ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('610', '26', '26', 'LAjONT 26 KHAWYA CH ', '550.0', '0.0', '0.0', '850.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('611', '28', '28', 'LAJONT VTT 28 kahla  khAWYA ', '850.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('612', '8692791104443', '8692791104443', 'KADLA TA3 DAW PGT RMZ DRG ZARGA ', '220.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('613', '8692791104795', '12001224', 'SAGMO PGT DRG 40 ZARGA ', '295.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('614', '50', '50', 'BWANI KOXI KOMPLI DAMAN ', '1750.0', '0.0', '0.0', '2700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('615', '1022276928001', '1022276928001', 'ZARBOT BOSTAR SRIYR ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('616', 'RMZ ', 'RMZ', 'KLi BOUGI PGT RMZ ', '110.0', '0.0', '0.0', '200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('617', '10500921', '8692791101398', 'HDIYDA DISK LOMBRAYAG PGT RMZ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('618', 'ROlMA ', 'ROLMA ', 'LORMA MAYO LOMBRAYAG PGT ', '180.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('619', '31020940', '490169014657', 'FIBROKA PGT ARSAL ', '4500.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('620', '6134419001588', '6134419001588', 'ZIYT AIGZOL DRG ', '880.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('621', 'RMZ', 'RMZ', 'LAKS 19 RMZ PGT TALI ', '290.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('622', '31', '31', 'ROLIYP PGT TARA TALYA FO RMZ ', '200.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('623', '25020640', '20600', 'MAYO LOMBRAYAG PGT ARSAL ', '1350.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('624', '2905', '2905', 'LIGALi +GLODA LOMBRAYAG KOXI HMD ', '720.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('625', '2310-127', '6941797822205', 'PLATO VTT 26 GH KHL ', '680.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('626', '2748', '6941797822182', 'KORSI VTT GH ROSOL ', '680.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('627', '6941797821284', '6941797821284', 'LOMBRAYAG AV R6 GH ', '1250.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('628', '152FMH23121015', '152FMH23121015', 'MOTAR ZNT 110 GH ', '30000.0', '0.0', '0.0', '38000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('629', '2310-33', '6941797821444', 'NAYMAN KOXI GH ', '1400.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('630', '6975296976756', '3955266986766', 'RODA KOXI GH ', '65000.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('631', '000012345618', '999912345678', 'KARWA A-48 GH ', '150.0', '0.0', '0.0', '350.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('632', '6941797822038', '6941797822038', 'FITR F3 GH ', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('633', '2310-52', '6975296975896', 'FIBROKA PGT GIRAUDO CH ', '3000.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('634', '2310-116', '6941797822090', 'RIGILATAR GH  5 FICH FOMAL ', '550.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('635', '2310-122', '6941797822151', 'MCHATI VILO 12 GH ', '130.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('636', '6975296973717', '6975296973717', 'TABLO KASAT KOMPLI PGT ', '3000.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('637', '6941797821963', '6941797821963', 'LOMBRAYAG AV F3 GH ', '1900.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('638', '6941791881475', '6941797821475', 'LOMBRAYAG KOXI AV GH ', '1450.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('639', '19411945', '2310-123', 'FAKHA FRAN VTT KROMI HDID GH ', '580.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('640', '6941797821277', '6941797821277', 'PISTON R6 150 GH STD ++', '720.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('641', '6011797821710', '6941797821710', 'LOMBRAYAG KOXI AR GH ', '3000.0', '0.0', '0.0', '4600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('642', 'ARRGY5709-19.2X9.0X28X817|1|232832||PTS479364', '5709-19,2×9,0×28×817', 'KARWA MITSUBOSHI ', '1650.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('643', '4044197463381', '4044197463381', 'BOUGIE R6 CHAMPION', '390.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('644', '3700142325104', '3700142325104', 'ZIYT IPONE LABWAT DRG', '100.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('645', 'ARRGY5734-20.0X10.0X30X833 |1|232832||4', '20,0X833', 'KARWA F3 MITSUBOSHI 20,0x833', '1950.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('646', '6976086200419', '6976086200419', 'FITR ZNT SIDI ACHOR ', '700.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('647', '5413048245923', '5413048245923', 'ZIYT CHAMPION 10W40', '1050.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('648', '12V4', '12V4', 'BATRI 12V4 YTX4L-BS', '2200.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('649', 'MVSB9001', 'KOXI ', 'KARWA KOXI MITSUBOSHI', '1600.0', '0.0', '0.0', '2400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('650', 'MVSB9002', 'MVSB9002', 'KARWA 842 MITSUBOSHI', '1400.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('651', '0341', '0341', 'KARBIRARATER KOXI HMD', '4300.0', '0.0', '0.0', '5700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('652', 'SYM 150', 'SYM 150', 'FIBROKA F3 HMD ', '4600.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('653', '1685', '1685', 'kontaktar HMD ', '350.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('654', '0596', '0596', 'SiYLANDRI R6 125 HMD ', '2950.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('655', '0597', '0597', 'SIYLANDRI R6 150 HMD ', '2950.0', '0.0', '0.0', '4500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('656', '16', '16', 'LAGAN ROLO 20 MATR HMD ', '780.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('657', 'SYM 150 F3', 'SYM 150 F3', 'PISTO F3 150 HMD ', '1250.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('658', '16', '16', 'LAKS 12 PGT LIYS TALi ', '140.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('659', '1487', '1487', 'POCHAT jWAN PGT HMD 40', '230.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('660', '1493', '1493', 'DEMI POCHAT R6 150', '125.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('661', '2097', '2097', 'DEMI POCHAT F3 150 HMD ', '250.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('662', '1041', '1041', 'jWAN SPi MOTR R6 HMD ', '280.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('663', '0261', '0261', 'KABL KSIRATAR KOXI HMD ', '280.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('664', '842''20''30', '842''20''30', 'KARWA 842 HMD ', '1350.0', '0.0', '0.0', '1850.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('665', '17/36', '17/36', 'LAGONT 17 PGT 36 HMD ', '880.0', '0.0', '0.0', '1450.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('666', '18', '18', 'LAJONY 18 BKN ', '830.0', '0.0', '0.0', '1500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('667', '23100-A2E-0001', '23100-A2E-0001', 'KARWA F2 SYM DR', '2750.0', '0.0', '0.0', '3700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('668', '23100-ANA-0101', '23100-ANA-0101', 'KARWA SYM F3 DRG', '2700.0', '0.0', '0.0', '3700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('669', 'J10XMT2,50', 'J10XMT2,50', ' LAJONT RODA TONIK DRG ', '900.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '10.0', '10.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('670', '13000-ABA-0005', '13000-ABA-0005', 'FIBROKA F2 SYM DRG ', '8400.0', '0.0', '0.0', '12000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('671', '19', '19', 'FIBROKA KOXI anfrak ', '5000.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('672', '11100-A2C-0000', '11100-A2C-0000', 'KARTAR F2 SYM DRG wastani ', '4350.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('673', '22350-ANL-0000', '22350-ANL-0000', 'FIRIDO LOMBRAYAJ F3 SYM DRG', '5300.0', '0.0', '0.0', '7000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('674', 'XIA2301A-f6C-0100', 'XIA2301A-F6C-0100', 'LOMBRAYAG KOMPLI F3 HAM ', '7100.0', '0.0', '0.0', '9000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('675', '1310A-U3A-0000-A', '1310A-U3A-0000-A', 'PISTO TONIK DRG ', '2750.0', '0.0', '0.0', '3700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('676', 'H069', ']C121FHM018-1120-006', 'Silandr Blok HAM ', '250.0', '0.0', '0.0', '600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('677', '2308-500', '6975296976084', 'FLOTAR karbiratar 15 kombli ', '400.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('678', '8684396460529', '8684396460529', 'CHATMA 51 MBK BIMOZAN ', '3000.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('679', 'DOT4', 'DOT4', 'ZIYT fran ta3 kar3a ', '130.0', '0.0', '0.0', '250.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('680', '21', '21', 'karbiratar 21 DILARTO ', '11000.0', '0.0', '0.0', '13000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('681', '8692791100643', '8692791100643', 'VILA PGT RMZ DRG ', '1900.0', '0.0', '0.0', '2600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('682', '8434829014627', '8434829014627', 'KARTAR PGT ARAPRAS RMZ ', '3050.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('683', 'HMH', 'HMH', 'LAFORCH PG AV KOMPLI KROMI HMH ', '4300.0', '0.0', '0.0', '5200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('684', '12100-A61-0003', '6976087310162', 'SiYLANDRI F3 HAM hmar ', '5000.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('685', '23431-AR1-0001', '23431-AR1-0001', 'LARB F3 200 SYM DRG ', '8900.0', '0.0', '0.0', '14000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('686', '3051A-ARA-0200 ', '3051A-ARA-0200', 'BOBIN KADR 2 FiCH HAM ', '500.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('687', '9120Z-GY6-9031-A', '9120Z-GY6-9031-A', 'JWAN SPI MOTAR SYM ', '1150.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('688', '2812A-XB3-0001', '2812A-XB3-0001', 'BANDISK F3 SYM DRG ', '4500.0', '0.0', '0.0', '6200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('689', '18000-A2F-0000', '18000-A2F-0000', 'CHATMA F2 SYM DRG ', '12000.0', '0.0', '0.0', '15000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('690', '6976314690739', '6976314690739', 'ABARAY KONTAR F3 F2 HAM ', '500.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('691', 'XIA1310A-GYB2-000', '6976087310087', 'PISTON F2 150 HAM ', '1300.0', '0.0', '0.0', '2500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('692', 'H183', '12118670', 'jalda SiPOr motar HAM ', '170.0', '0.0', '0.0', '400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('693', 'H101-1', 'H101-1', 'DEMi PCHAT jWAN HAM ', '320.0', '0.0', '0.0', '650.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('694', '44', '44', 'SOPAP R6 GH ZARGA ', '350.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('695', '26X1/X1', '6975309860331', 'PNOU VTT  26 LABYAD DAKICH HDH', '900.0', '0.0', '0.0', '1600.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('696', 'LYO34', '19753096', 'BNU 700X38C SFAR ', '950.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('697', '1633', '1633', 'MORTISAR KOXI khachab ', '1500.0', '0.0', '0.0', '2800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('698', '2-0163', '6941797811391', 'ZDAG FAR KOXI  1 khachab ', '700.0', '0.0', '0.0', '1400.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('699', '13', '13', 'CHATMA PGT KOTI KAHLA TRB', '730.0', '0.0', '0.0', '1200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('700', '20', '20', 'LAJONT 20 VTT ', '520.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('701', '16', '16', 'LAJONT 16 VTT khawya ', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('702', '1009089000023', '1009089000023', 'LAJONT PGT CHAFRA Khachab kahla ', '11700.0', '0.0', '0.0', '16000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('703', '6194098682157', '6194098682157', 'ZIYT PON LABWAT YAMALUBE', '340.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('704', '16', '16', 'PNOU 16 VTT ', '420.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('705', '20168501900', '20168501900', 'GSAB PGT GRI HMH ', '1500.0', '0.0', '0.0', '2000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('706', '29', '29', 'KRATIYB PGT RAOOM ', '1800.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('707', '29', '29', 'PORT BAGAG ARYAR VMAX ', '1350.0', '0.0', '0.0', '3000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('708', '2308-252', '6975296977074', 'ZDAR MAFTAH DRAYVAR ', '2800.0', '0.0', '0.0', '5500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('709', '22300', '5TN9163000Z6', 'FIRIDO LOMBRAYAJ R6 AFRICA TWIN ', '1350.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('710', '1065', '6941797821048', 'FITR KOXI 1 KOMPLI Khachab ', '1200.0', '0.0', '0.0', '2200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('711', '1564', '6975296971027', 'LAJONT AVO ARYAR AISTAYT KOLAR KhADRA ', '7000.0', '0.0', '0.0', '14000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('712', '1564', '6975296971027', 'LAJONT AVO ARYAR KOXI OKINWA ', '8200.0', '0.0', '0.0', '14000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('713', '101411024', '101411024', 'LAJONT KOXI DRG VMS ZARGA ', '10200.0', '0.0', '0.0', '15000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('714', 'VE-6', 'VE-6', 'STABI CYKLE 16 VTT ', '350.0', '0.0', '0.0', '700.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('715', '29', '29', 'BORT BAGAG ARYAR KOXI 2 ', '1050.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('716', '116111019', '116111019', 'KAG KOTi ARYAR KOXI 1 VMS DRG ', '6000.0', '0.0', '0.0', '8000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('717', '3448523', '3448523', 'KAG KOTI TAPI DRAYVAR VMS DRG ', '3700.0', '0.0', '0.0', '6000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('718', '14721-F8A-000', '14721-F8A-000', 'SOPAP F3 HAM ', '1250.0', '0.0', '0.0', '2300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('719', 'JFG37CFF91KKF', 'XS0-W4T9YVVS4', 'ZDAG DAW KOXI AISTAYT ', '1550.0', '0.0', '0.0', '1900.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('720', '13300', '13300', 'GARDBO AV VOG NWAR ', '2300.0', '0.0', '0.0', '3500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('721', 'VMS', 'VMS', 'GIYDONE KOXI VMS DRG ', '1850.0', '0.0', '0.0', '3200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('722', '56FA2J8KGA079', '81Y8ZRYVTX071', 'FAR KOXI 2 CH ', '4500.0', '0.0', '0.0', '7500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('723', '29', '29', 'KAG BATRI KOXI 2 Khachab ', '430.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('724', '29', '29', 'KAG BATRi KOXI 1', '350.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('725', '6976314692559', '6976314692559', 'FIRIDO FRAN koxi HAM ', '400.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('726', 'G8007', '6976314692122', 'LIPLAK FRN R6 HAM ', '300.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('727', 'XIA45105/45106-ASC-00', '6976087310414', 'LIPLAK FRN AV TONIK HAM ', '500.0', '0.0', '0.0', '1100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('728', '2876222A', '6976309630412', 'BANDISK R6 HAM ', '1900.0', '0.0', '0.0', '2800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('729', '12391-GYB2-A000', '12391-GYB2-A000', 'JWAN KAJ KIYLAS SYM ORBIT ', '350.0', '0.0', '0.0', '800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('730', 'H159', '6976314692054', 'JWAN KAJ KIYLAS F3 HAM ', '250.0', '0.0', '0.0', '500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('731', '6938112695781', '6938112695781', 'PNOU 90/90-10 CHAOYANG tnk', '3400.0', '0.0', '0.0', '5000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('732', 'H101-1-HAM', ']C122FHM004-1020-100', 'DEMI POCHAT F3 HAM ', '300.0', '0.0', '0.0', '6500.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('733', 'XIA101XB15', '6976087310049', 'POCHAT KOMPLI F3 HAM ', '1150.0', '0.0', '0.0', '1800.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('734', '50350-ABA-0000', '50350-ABA-0000', 'SIPOR MOTAR ORBIT SYM DRG', '2800.0', '0.0', '0.0', '4200.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('736', 'H102', '6976314690852', 'Liblak fran F3 HAM ', '440.0', '0.0', '0.0', '1000.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('737', 'KD04072', '6976314692177', 'GIYD SOPAP R6 HAM ', '140.0', '0.0', '0.0', '300.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', NULL, '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('738', '777JDHJAF2D03', '9RV086YRU7196', 'test1', '100.0', '0.0', '0.0', '150.0', '145.0', '140.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('739', '2-1534', '4961261808681', 'KONTAR KOXI 2 khachab ', '5200.0', '0.0', '0.0', '7500.0', '2.0', '2.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('740', '0182', '6941797807868', 'LACHAN MOTAR R6 90 YAMCYCiE', '300.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('741', '1043', '1043', 'LIZONTIFIT R6 HMD ', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('742', '2310-45', '6975296973489', 'ZARBOT KLIKI koxi SRIYR khachab ', '420.0', '0.0', '0.0', '750.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('743', '10W-40', '10W-40', 'ZIYT MOTUL 5000', '1130.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('744', '1225A-GYB2-0000-A', '1225A-GYB2-0000-A', 'POCHAT JWAN KOMPLI SYM F2 ORBIT DRG', '1700.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('745', '11200-A2C-0100', '11200-A2C-0100', 'KARTAR SYM F2 DRG TWIYL ', '9200.0', '0.0', '0.0', '13000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('746', '6F2C572C5KGC2', '14401-M92-0031-M2', 'LACHAN SYM F3 DRG 94 ', '1300.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('747', '19', '19', 'GSAB AV TONIK HAM ', '8800.0', '0.0', '0.0', '12000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('748', '17200-ALA-0001', '17200-ALA-0001', 'FILTRE SYM J4 ORBIT DRG KOMPLI ', '3400.0', '0.0', '0.0', '5000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('749', '17200-XGA-0007', '17200-XGA-0007', 'FILTRE SYM F3 DRG KOMPLI ', '3300.0', '0.0', '0.0', '5000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('750', 'KD040620L', '6976314691828', 'LACHAN MOTAR 44 HAM ', '400.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('751', '45451-N02-0003', '45451-N02-0003', 'KROCHI KABL KONTAR F3 SYM DRG ', '120.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('752', 'XIA12101-GYB2-A000', '6976087310148', 'SIYLANDRI ORBIT HAM ', '400.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('753', '11102-M9Q-3000', '11102-M9Q-3000', 'SIYLANDR BLOK SYM ', '620.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('754', 'G8021', '6976314692306', 'SOPAP J4 SANFONI HAM F6A ', '1500.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('755', '16101-XRA-0000', '16101-XRA-0000', 'STARTER KARBIRATAR SYM DRG J14', '1250.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('757', '084', '084', 'BOMBA AiSONS CG ', '300.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('758', '138303', '6941797823677', 'KAG CHATMA JOKI ', '350.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('759', 'A-78', 'A-78', 'KLIKI VITAS ZNT CG TOYU ', '450.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('760', 'A-77', 'A-77', 'KLIKI VITAS ZNT TOYU CG', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('761', '16', '16', 'BOLON LAPIP R6', '100.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('762', '2×3×44L', '6975296975308', 'LACHAN R6 SRIYRA 44 Tmmp', '300.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('763', '161-2201', '6941797819571', 'SABO FRAN AV KOXI YAMCYCLE khachab ', '1750.0', '0.0', '0.0', '2800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('764', 'ViMX020', 'VIMX020', 'GABDA FRAN VIMAX D VMS ', '450.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('765', '29', '29', 'PATANA SIPON PAR', '50.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('766', '0138', '6975296972086', 'FIBROKA BOSTAR RuimA', '2800.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('767', '17620-CT1C-9000', '17620-CT1C-9000', 'BOCHN RIZAVWAR SYM F3 ST DRG', '300.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('769', '6976063170827', '6976063170827', 'CHAMBRAYAR 14 VILO BARIKA ', '240.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('770', 'A1CK8B643EK88', 'U-X79T86UWS4R', 'SONAT VILO L''ALGÉRIE ', '110.0', '0.0', '0.0', '250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('771', '160-2201', '6975296973328', 'GABDA FRANE KOXI TA3 ZIYT KOMPLI KHACHAB ', '1150.0', '0.0', '0.0', '1900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('772', '6941797410327', '6941797410327', 'DIMARAR R6 TNN ', '1350.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('773', 'V633', '6975308496333', 'FIBROKA KOXI TNN ', '4000.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('774', '6941797815009', '6941797815009', 'KARTAR R6 TWYL 150 BODAN ', '5400.0', '0.0', '0.0', '7200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('775', 'SYM ', 'SYM ', 'KOP KORO SYM F3 TA3 KRA3 ', '1900.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('776', '22300', '5TN9163000Z6', 'FIRIDO LOMBRAYAJ R6 AFRICA TWIN ', '1350.0', '0.0', '0.0', '2100.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('777', '5TN2163ZZ00', '22300', 'FIRIDO LOMBRAYAJ KOXI YAMAHA ', '1500.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('778', '711 18 03', '711 18 30', 'KARWA BANDO KOXI 711 BELT DRG ', '1500.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('779', '747 16,5 30', '747 16,5 30', 'KARWA 747 BOSTAR SPX BANDO ', '1250.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('780', '6941797826456', '6941797826456', 'ALARM SIGARAT Li TCHARJI DAICH ', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('781', '1658', '1001871080001', 'MCHATI PGT KBAR Khachab ', '350.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('782', '12200-ANL-0010', '12200-ANL-0010', 'KIYLAS SYM F3 DRG BLA SOPAP ', '9000.0', '0.0', '0.0', '14000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('784', '6976087310391', '6976087310391', 'T FORCH F3 HAM ', '4600.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('785', '1469', '6975296978576', 'KRA3 TA3 JANB R6 TWIYLA ', '650.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('786', 'V0655', 'V0655', 'KRA3 TA3 JANB KOXI KSIYRA ', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('787', '2303-210', '6941797806243', 'JALDA TA3 MAYO ZNT 110', '170.0', '0.0', '0.0', '450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('788', '53178-FX100-ZC', '53178-FX100-ZC', 'LOVI FRAN G DRAYVAR ', '230.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('789', '2209-95', '1907004228001', 'GID KIYLAS LiBAG R6 BAKIYA ', '130.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('790', '2308-296', '6975296973427', 'POS PYI KOXI 2 ', '1150.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('791', 'S86', '6976886900373', 'DR3 BidAL VTT NWAR khawi ', '220.0', '0.0', '0.0', '450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('792', '2206-2', '6975416530851', 'BOMBA TA3 ZIYT R6 Khachab ', '300.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('793', '951283377153', '951283377153', 'FIBROKA R6 ViPER', '3000.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('794', 'Pg-949', '6975283380269', 'SIPOR TLIFON Moto Cycle', '900.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('795', '5WB-E7451-00', '5TNE16300000', 'BANDISK KOXI YAMAHA ', '1450.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('796', '2209-11', '6975416533838', 'DIMARAR KOXI Khachab ', '1550.0', '0.0', '0.0', '2400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('797', 'Koxi ', 'koxi ', 'DEMI POCHAT KOXI GH ', '160.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('798', '350-10', '350-10', 'PNOU HAM 350-10 aLc RLAF KHAL ', '3400.0', '0.0', '0.0', '4600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('799', 'H191', ']C121FHM018-1120-044', 'LAKS KRA3 F3 HAM ', '400.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('800', '50710-ATA000', '50720-ATA000', 'POS PIYI J4 SYM DRG ', '2700.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('801', 'G8007', '6976314692122', 'LIBLAK FRAN AV R6 HAM ZARGA ', '260.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('802', '16100-ANA-0100', '16100-ANA-0100', 'KARBIRATAR J4 SYM DRG ', '8800.0', '0.0', '0.0', '11500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('803', 'S9RGD7-GL5-8514', ']C1S9RGD7-GL5-8514', 'ZIYT LABWAT SYM DRG ZRAG ', '6200.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('804', 'GH', 'GH', 'LAKLOCH PG 103 GH CH', '600.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('805', '22300-ABA-0000', '22300-ABA-0000', 'FIRIDO LOMBRAYAJ ORBIT 1 SYM DRG ', '3600.0', '0.0', '0.0', '5500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('806', 'KD04053-HAM', '6976314692245', 'LAKS KLIKI R6 HAM ', '750.0', '0.0', '0.0', '12001.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('807', '50350-ABA-0000', '50350-ABA-0000', 'SIPOR MOTAR ORBIT 01 SYM 150 DRG ', '3000.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('808', 'H212', '6976314692214', 'JWAN SPI MOTAR F3 HAM ', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('809', '2303-464', '6941797825329', 'FiTR STONDAR BOSTAR ', '300.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('810', 'H109 HAM ', 'H109 HAM ', 'ROSOL KRA3 F3  HAM SACHI KOMPLI ', '220.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('811', '1544', '925106273342', 'SOFLU LAFOCH KOXI  khachab ', '480.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('812', '2303-646,05', '6941797804218', 'PISTO KIT PGT 103 KWAWSI ', '580.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('813', '1604-1', '6941797821123', 'POS PYI KOXI 2 khachab ', '1150.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('814', '021', '021', 'NAYMAN KOXI VMS DRG ', '1900.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('815', '002', '002', 'NAYMAN DRAYVAR VMS DRG ', '3000.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('816', '50506-ABA-0000', '50506-ABA-0000', 'JLODA TA3 KRA3 ORBIT SYM DRG ', '150.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('817', 'H183 HAM ', 'H183', 'JLODA TA3 SIPOR F3 HAM ', '150.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('818', '1610655200108', 'TIMAX', 'LIPLAK TIMAKX TMMP ', '400.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('819', 'A-07', '6971383621028', 'FITR ZNT 110 ', '300.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('820', '2308-69', '6941797818741', 'PWANI KOXI VODI TRAS TMMP', '1750.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('821', 'KD04071', '6976314691842', 'SOPAP R6 150 HAM ', '600.0', '0.0', '0.0', '1250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('822', '90909-33295', '90909-33295', 'SIYLANDRI BIKAN DRG AV7 RMZ ', '7600.0', '0.0', '0.0', '9500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('823', '4269', '6941797442694', 'SIYLANDRI BIKAN Ch TNN AV 7', '4000.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('824', '1106-107', '526416531049', 'FIBROKA 51 MBK AV10 CH ULTIMO ', '3900.0', '0.0', '0.0', '5200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('825', '15-2201', '6975296971935', 'PWANI DRAYVAR CH Khachab ', '1900.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('826', 'AK9309CJE4228', 'RU0T52TYZ1265', 'SIYLANDRI BIKAN SALMAN DRG LAKAS +kiylas ', '400.0', '0.0', '0.0', '1.0', '8000.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('827', 'FA27GBEC68CG9', 'Z-S-U-T1-TZ7Z', 'TABLO KASAT DRG SALMAN ', '1100.0', '0.0', '0.0', '15000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('828', '4524-108', '4524-108', 'MASK TA3 WAJH SAM ', '780.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('829', '44', '44', 'LOMBRAYAG ARYR KOXI GH ', '3000.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('830', '2310-250', '7040419345678', 'MOTAR KOXI YAM ', '45500.0', '0.0', '0.0', '54000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('831', '2310-246', '6941797802313', 'KAG SIYLANDRI KOXI YAM ', '7500.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('832', '2303-388', '6941797824964', 'GARDBO KOXI KARBON SIKL', '2800.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('833', 'D85-H7', 'D85-H7', 'ZDAR KOXI 1 TRITON SIKL ', '3800.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('834', '163', '163', 'MASK AVO KOXI 2 GRI HIGHTAC', '5200.0', '0.0', '0.0', '8000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('835', '2303-391', '6941797801972', 'MASK AVO KOXI 1 NWAR ', '4800.0', '0.0', '0.0', '7500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('836', '1211-10', '1211-10', 'SABO TA3 TAHT KOXI 1', '3000.0', '0.0', '0.0', '5000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('837', '159', '159', 'SDAR KOXI 2 HIGHTAC', '3800.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('838', '5336-5', '5336-5', 'LAJONT VTT 26 BISKWS', '5500.0', '0.0', '0.0', '9500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('839', '1157', '904107625107', 'FITR KOMPLI FAR FAR TNN ', '1750.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('840', '1174', '6975416531353', 'TABSI KARWA TLIFON Khachab ', '1580.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('841', '43', '43', 'KARINAJ KOXi VMS KARBON ', '37000.0', '0.0', '0.0', '43000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('842', '842 GH', ']C11017252027903', 'KARWA 842 GH ', '680.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('843', 'A6A STD+', '6941797822014', 'PISTO F3 GH 150 ', '900.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('844', '6941797822106', '6941797822106', 'ROLIYP VTT T3 7 GH ', '700.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('845', '6976063138568', '6976063138568', 'PNOU DURO 80/100-14 TL', '4800.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('846', '14 VILO ', '14 VILO ', 'PNOU VILO 14 SAM ', '450.0', '0.0', '0.0', '750.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('847', 'TX35', '6941797821567', 'DIMARAR VTT GH D''ORIGINE ', '700.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('848', '6975296978194', '6975296978194', 'LOMBRAYAG ARYAR R6 GH ', '2600.0', '0.0', '0.0', '3600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('849', '6941797821710', '6941797821710', 'LOMBRAYAG ARYAR KOXI GH ', '3000.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('850', ']C11610552827903', ']C11610552827903', 'BOUGIE R6 GH MASMAR ', '150.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('851', '44', '44', 'PATANA VTT SIPON GH 2 ', '50.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('852', '44', '44', 'PISTO KOXI GH ', '850.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('853', '6001', '6001', 'LORMA MARCAL 6001 GH ', '100.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('854', '6000', '6000', 'LORMA 6000 MARCHAL GH ', '100.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('855', '6201', '6201', 'LORMA MARCHAL 6201 GH ', '120.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('856', '6204', '6204', 'LORMA MARCHAL 6204 GH ', '180.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('857', 'Cache Batterie Driver  ', 'Cache Batterie Driver  ', 'KAJ BATRI DRAYVAR GH', '450.0', '0.0', '0.0', '1100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('858', '6941797822229', '6941797822229', 'PWANI JOKI DEMI GH ', '1350.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('859', '44', '44', 'ROLO TiYO LISONS GH', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('860', '6941797821406', '6941797821406', 'KARBIRATAR R6 GH ', '2650.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('861', 'RMZ ', 'RMZ', 'GARDBO PGT 103 NWAR ', '2400.0', '0.0', '0.0', '3100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('862', '2-25-18 TT', '6976886900229', 'PNOU 2-25-18 CSCT', '1750.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('863', 'CUK027', 'CUK027', 'FIRIDO FRAN KOXI VMS DRG ', '700.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('864', '700', '700', 'RYON VTT 700 28', '850.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('865', '12', '3536660013977', 'KARBIRATAR 12 DRG FRANS ', '4400.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('866', 'TR-31', '6941797829440', 'CHARGAR TROTINAT SIKL ', '2400.0', '0.0', '0.0', '3600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('867', '0346', '6975296978798', 'KASAT F3 KBIYRA Khachab ', '1000.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('868', '110++', '110++', 'PISTO ZNT 110 ++ DAMAN ', '750.0', '0.0', '0.0', '1450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('869', '103', '103', 'TAPI PGT 103 ', '250.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('870', '2303-134', '6941797824278', 'ROLMA FIBROKA FOX HDH', '1000.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('871', '2306-8', '6941797811711', 'LAMBA KOXI Khachab ', '2100.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('872', '2303-533', '6941797825824', 'DIMARAR PLAY 50/ 60 /80', '1650.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('873', 'TONAS ', 'TONAS ', 'FIBROKA 51DRG TONAS ', '4000.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('874', 'RMZ HAND ', 'RMZ HAND ', 'LAKS PIDAL RMZ HAND ', '450.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('876', 'TR-52', '6941797828313', 'KRA3 TROTINAT Khachab ', '900.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('877', 'TR-57', '6941797828368', 'DAW TROTINAT AVO LAD ', '850.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('878', '114-1', '114-1', 'MACHTA VTT S PLASTIK ', '280.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('879', '1669', '6975296974127', 'KOL KARBIRATAR 12 CH SIKL ', '1350.0', '0.0', '0.0', '1900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('880', '2303-101', '2303-101', 'LAKS TAMBOR PGT 103 TWIL ', '75.0', '0.0', '0.0', '150.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('881', '1106-107', '6975416531049', 'FIBROKA 51 ULTIMO khachab CH ', '3900.0', '0.0', '0.0', '5200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('882', '2308-382', '6975296970730', 'LOMBRAYAG ARYAR ARMADA KBIRA ', '4500.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('883', '1397', '1397', 'SABO FRAN AVO YAM Khachab ', '2200.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('884', '36', '36', 'MAYO PGT 36 CH ARYAR SAM ', '2250.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('885', '6202', '1989-77', 'ROLMA 6202 MARCHAL ZARGA ', '130.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('886', '6002', '6002', 'ROLMA 6002 MARCHAL SAM ', '130.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('887', '2303-18', '6941797804935', 'RODONTI 52 56 ', '8500.0', '0.0', '0.0', '1300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('888', '2308-282', '6975296972796', 'KIYLAS DRAYVAR Azadin ', '7800.0', '0.0', '0.0', '10500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('889', '48', '48', 'KAG SIYLANDRI BOSTAR MBK ', '650.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('890', '1047-4', '6975416530271', 'PISTO FOX 40 Khachab ', '750.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('891', '46,04', '6975416530998', 'PISTO KIT 46,04 CH Khachab ', '650.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('892', '2302-81', '9594879959657', 'LAPARAY KONTAR F3', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('893', '2308-413', '6941797811421', 'LAMBA 2 PLON R6 AZADIN ', '450.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('894', '0343', '0343', 'KARBIRATAR 15 HMD DRG ', '2050.0', '0.0', '0.0', '3100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('895', '1237', '6975296973267', 'LIPLAK FRN AVO KOXI Khachab ', '220.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('897', '47272-259', '6931854171452', 'KABL FRAN BOSTAR MBK ', '450.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('898', '01833', '6931854171452', 'KABL KSIRATAR BOSTAR KOMPLI TMMP ', '450.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('899', '1', '1', 'GIYDO L PGT 103', '1350.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('900', '2302-97', '6941797810455', 'BOLON MAYO TA3 TARA TMMP ', '350.0', '0.0', '0.0', '750.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('901', '420', '420', 'SARKLIBS 420 ZNT', '30.0', '0.0', '0.0', '10.0', '100.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('902', 'S-110', 'S-110', 'T FORCH F3 SAM ', '3200.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('903', '103', '103', 'SIYLANDR BLOK SIPOR MOTAR PGT 103', '120.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('904', '146', '146', 'LIGALI KOXI Euro CYCLE', '350.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('905', '1202', '6941797808636', 'SIYLANDR BLOK R6 KOMPLI Khachab ', '380.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('906', '47', '47', 'PISTO 47 POSTR POLINI Khachab ', '850.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('907', 'KROCHi', 'KROCHI ', 'KROCHi KASK VSPA SRIYR ', '120.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('908', '145', '6975416534750', 'LIGALi BOSTAR 4,5 SIKL ', '350.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('909', '2302-280', '3931767809100', 'LAPIP POLINI TA3 15', '350.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('910', '2203-371', '5075006088845', 'ROSOL KRA3 BOSTAR SFAR ', '60.0', '0.0', '0.0', '150.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('911', 'HA-2308-123', '6975296976862', 'SIPOR MOTAR DRAYVAR CH STIF ', '2500.0', '0.0', '0.0', '4200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('912', '1983', '1983', 'PNOU  KOXI HMD 100/90-10 ', '3700.0', '0.0', '0.0', '4800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('913', '125 OKN', '125 OKN ', 'KAPL FRAN R6 F3 HMD KOMPLI ', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('914', '94 HMD ', '94 HMD ', 'LACHAN MOTAR 94 GIRAUDO HMD ', '450.0', '0.0', '0.0', '1100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('915', '1480', '1480', 'POCHAT JWAN F3 HMD KOMPLI ', '450.0', '0.0', '0.0', '1100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('916', '3103', '3103', 'DEMI FITR KOXI 2 HMD ', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('917', '2023', '2023', 'KARWA 711 KOXI HMD ', '1250.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('918', '2023', '2023', 'KARWA 711 KOXI HMD ', '1250.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('919', '1043', '1043', 'JWAN SPI MOTAR HMD 150', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('920', '3720-024', '3720-024', 'LOPTIK PGT HMD ZRAG ', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('921', '0338', '1000422000208', 'DEMI POCHAT FOX +JWAN SPI ', '185.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('922', '17', '1234567890123', 'KARBIRATAR 17 PGT SiFAMA CH ', '1900.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('923', '0204230400203', '0204230400203', 'DEMI FITR R6 RO ', '240.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('924', 'SSSP 2191', '1610548000203', 'SOPAP FORMIUA SPW SAM ', '420.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('925', ' 2107009200201', '2107009200201', 'BANDISK FOX SAM TA3 RAKA ', '3801.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('926', '1007619700204', '1007619700204', 'KLAPI POLINI LASFAR SAM ', '520.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('927', 'm.me/sarlafak12?hash=(null)', '618-2', 'KASK can-am', '3600.0', '0.0', '0.0', '4800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('928', 'CG 150', '1000191900208', 'JWAN SPI CG 150 SAM ', '180.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('929', '20110147', '20110147', 'GIYDO KOXI 2 SAM ', '1650.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('930', '1009145600003', '1009145600003', 'SABO FRAN KOX 2 SFAR SAM ', '1500.0', '0.0', '0.0', '2800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('931', '110', '110', 'KLI BOUGI + TORNIFIS ', '135.0', '0.0', '0.0', '250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('932', '1442', '1442', 'TAMBOR FRAN MOT BIKAN SAM ', '780.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('933', 'SAM ', 'SAM', 'LAGAN RGIYGA SAM ', '46.0', '0.0', '0.0', '70.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('934', '740x460x280', '470x460x280', 'KARINAJ KOMPLI KOXI 2 DOZYAM SAM ', '29800.0', '0.0', '0.0', '25000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('935', '6971383620052', '47272-107', 'MORTISAR ZNT 70 SAM ', '1900.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('936', '19', '19', 'KARINAJ KOXi 2 BYAD DOZYAM SAM ', '3200.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('937', '19', '19', 'MOSTACHA MASK AVO NWAR KARBO BLON SAM ', '1200.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('938', '19', '19', 'GARDBO KOXI NWAR SAM ', '1750.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('939', 'VIN', 'VIN', 'KOVAR NIMIRO KOXI VIN ', '70.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('940', 'SM', 'SM', 'MOSTACHA DAW TALI NWAR SAM ', '1200.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('941', '19', '19', 'KAG BATRi KOXI 2 SAM ', '260.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('942', '19', '19', 'KADNA TA3 KORSI KOXI 2 HDID SAFAr ', '20.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('943', '17272 299', '6971383620090', 'TROVIZAR KOXI 2 SAM BOYDIN ', '780.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('944', '130/70-12 TL', '130/70-12 TL ', 'PNOU 130/70-12 CST ', '5250.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('945', '120/60-13 CST', '120/60-13 CST', 'PNOU 120/60-CST', '5250.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('946', 'SC0 154', 'SC0 154', 'SIYLANDRI R6 150 SAM ', '3200.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('947', '1625', '1625', 'PWANI KOXI KOMPLI SAM ', '1450.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('948', '1627', '6971383620519', 'KASAT KOXI SAM kAHLA ', '1050.0', '0.0', '0.0', '1900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('949', '19', '19', 'PWANI TA3 ZIYT +SABO KOXI KOMPLI ', '2900.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('950', '877915', '05', 'LIPLAK AVO DRG ', '1800.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('951', '03', '03', 'LIPLAK KOHLIN SDL', '6500.0', '0.0', '0.0', '1300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('952', '135', '135', 'liblak R6 SAM ', '200.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('953', '1000151400003', '1448', 'LOPTIK MBK ZARGA ', '250.0', '0.0', '0.0', '1.0', '6500.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('954', 'ST02432', 'ST02432', 'TIP KORSI VTT AMORTISAR BYAD ', '8500.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('955', '02432', '02432', 'TIP KORSI VTT AMORTISSEUR NWAR ', '1150.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('956', '19', '19', 'TABLO KASAT KOMPLI FO RMZ SALAH', '2950.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('957', '18x2 125', '18x2 125', 'PNOU 18x2 125 VTT BYAD ', '480.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('958', '32-630[27×1 1/4]', '32-630[27×1 1/4]', 'PNOU 32-630[27×1 1/4]', '8500.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('959', '01984-2', '01984-2', 'HLAL MDAWAR R6 SAM ', '380.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('960', '6975296978347', '6975296978347', 'FORCHA ZNT 70 KOMPLI SAM ', '6800.0', '0.0', '0.0', '9000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('961', '2243', '2243', 'KLIYKI KOXI TA3 BARA SAM ', '5800.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('962', '1810026700001', '1810026700001', 'FITR KOXI STAYT KOMPLI 0 19', '1250.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('963', 'SS01610', 'SS01610', 'SIPOR MOTAR KOXI KHAWI ', '2600.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('964', '1610', '1610', 'RAKA SIPOR MOTAR KOXI SAM ', '1200.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('965', '19', '19', 'BASIYNA TA3 KOXI CH ', '1150.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('966', '02305', '02305', 'LIDWA F3 SAM ', '680.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('967', '056', '056', 'PAVIYTA KOXI 2 VMS DRG ', '1850.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('968', '014', '014', 'KABL KONTAR KOXI VMS DRG ', '680.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('969', '100', '100', 'ZIYT IPONE ', '1550.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('970', '6941797824995', '6941797824995', 'GARDBO AVO KOXI 1 ZRAG BARAD ', '2500.0', '0.0', '0.0', '4000.0', '1.0', '11.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('971', '055', '055', 'KAJ KARBIRATAR KOXI 2 VMS RARONTI ', '580.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('972', '152', '152', 'PIP KARBIRATAR VIMAKS DRG VMS ', '2000.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('973', '074', '074', 'PIP KARBIRATAR KOXI VMS DRG ', '1050.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('974', '045/B', '045/B', 'KAS TA3 KAHWA DRG VMS ', '400.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('975', '2310-137', '2310-137', 'JLODA TA3 KSIRATAR KOXI ', '500.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('976', '021', '021', 'NAYMAN KOXI 2 DRG VMS 19', '2200.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('977', '066', '066', 'LIPLAK FRAN AVO VMS DRG ', '700.0', '0.0', '0.0', '1300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('978', '060', '060', 'GARDBO ARYAR KOXI VMS DRG ', '1350.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('979', '048', '048', 'KARBIRATAR DRAYVAR DRG VMS ', '8500.0', '0.0', '0.0', '12000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('980', '009', '009', 'PWANI KOXI VMS DRG ', '2900.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('981', '068', '068', 'SIYLANDRI KOXI VMS DRG BLA PISTO ', '3400.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('982', '102', '102', 'SIYLANDRI DRAYVAR VMS PLA PISTO ', '4350.0', '0.0', '0.0', '7200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('983', 'عيشة', 'عيشة', 'باقي 110 مليون', '20.11', '0.0', '0.0', '2024.0', '2024.0', '2024.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('984', 'WJ0401-005', 'WJ0401-005', 'LARACH SIYLANDR R6 BLOK BARIKA', '750.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('985', '86 A', '86 A', 'LAMBA FIROG KOXI VMS DRG 2 HABAT ', '950.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('986', '86', '86', 'LAMBA KLINITO KOXI VMS DRG 2HABQT', '950.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('987', '22110504', '22110504', 'FIROG KOXI 2 VMS DRG ', '220.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('988', '095', '095', 'TONDAR CHAN KOXI VMS DRG ', '680.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('989', '051', '051', 'KONTAR JOKI VMS DRG ', '3000.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('990', '073', '073', 'POMBA TA3 ZIYT KOXI VMS DRG ', '1050.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('991', '029', '029', 'BOMBA LISONS KOXI VMS DRG ', '1680.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('992', '017', '017', 'STALASYO KORO DRAYVAR VMS DRG ', '5100.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('993', '074', '074', 'LAPIP KOXI VMS DRG ', '1050.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('994', '200', '200', 'BOLON VIDONJ DRAYVAR VMS ', '220.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('995', '083A', '083A', 'PINYO KLIKI KOXI SRIYR VMS DRG ', '830.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('996', 'D85-H7', 'D85-H7', 'JWAN SPI KOXI 1', '400.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('997', '086', '086', 'SOPAP KOXI VMS DRG ', '2200.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('998', '002', '002', 'MASK AVO KOXI AISTAYT NWAR ROG VMS DRG ', '5350.0', '0.0', '0.0', '7500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('999', '011', '011', 'KAG FAR MOSTACH KOXI 2 NWAR BLON ', '1200.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1000', '013', '013', 'KAG KONTAR KOXI 2 BLON NWAR ', '550.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1001', '014', '014', 'KAJ KONTAR KOXI FI 2 NWAR BLON 014 ', '1100.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1002', '6975416532022', '6975416532022', 'GAS3A LOMBRAYAG PGT EUROCYC', '370.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1003', '1211-70', '1005347041408', 'FIRIDO LOMBRAYAJ PGT CH Kmoto', '320.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1004', '48', '48', 'RLAF KORSI PGT ', '200.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1005', '2310-72', '7940782819618', 'STALASYO KOXI CH KHACHAB ', '2000.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1006', '1989', '1805026380001', 'LIDWA KOXI KHACHAB ', '450.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1007', '48', '48', 'JWAN CHATMA PGT ', '17.0', '0.0', '0.0', '40.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1008', '1056', '6941797410563', 'FIBROKA R6 TNN ', '2600.0', '0.0', '0.0', '4200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1009', '2303-76', '6941797823929', 'KARBIRATAR 12 CH DIAFI', '1650.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1010', '161031', '6941797823349', 'KARBIRATAR BOSTAR OVITO', '1950.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1011', '14', '14', 'CHATMA KOTI PGT KAHLA KSIYRA ', '730.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1012', '1166', '6975416531421', 'FIRIDO FRAN MVL HAMRIN KHACHAB ', '340.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1013', '2303-147', '6941797824377', 'KIYLAS 51 HDH', '1850.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1014', '2303-148', '6941797824384', 'KIYLAS 51+ZARGA MOTO BIKAN ', '1850.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1015', '02', '02', 'PORT BAGAG KOXI 2 HDIYD RLIYD ', '2750.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1016', 'B-32', 'B-32', 'FiTR PANARIA', '750.0', '0.0', '0.0', '1300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1017', '2091', '2091', 'POCHAT JWAN R6 150 HAM ', '380.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1018', '20X1,95/2,125', '20X1,95/2,125', 'PNOU VTT 20 MAXSTAR', '400.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1019', '6975334761368', '6975334761368', 'CHAMBRAYAR TROTINAT 08 SIMAM', '600.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1020', 'B-33', 'B-33', 'CHAWAYA TONIK KORICHI ', '750.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1021', 'B-32', 'B-32', 'FITRE PANARYA KORICHI', '750.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1022', '6941797410617', '1061', 'BANDISK R6 TNNN ', '1000.0', '0.0', '0.0', '1900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1023', '027-01', '027-01', 'KLINITO PGT 103 4 HABAT ', '1250.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1024', '9536474602415', '9536474602415', 'RODONTI 52 +56 RMZ ', '8800.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1025', '5EGCC2C7B6396', '9536474602415', 'RODONTI 45 CHAFRA RMZ KHAL ', '880.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1026', 'B-19', 'B-19', 'KADNA SISTAM ALARM TA3 VISPA ', '2000.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1027', '9536474602347', '9536474602347', '3AGRAB PGT 103', '650.0', '0.0', '0.0', '150.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1028', '9536474602279', '9536474602279', 'DAR3AT KROMI PGT 103 TRITON ', '470.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1029', '9536474602187', '9536474602187', 'PINYO TABSI PGT 13 FO RMZ ', '260.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1030', 'TINA', 'TINA', 'LIPLAK TONIK TOYU ', '250.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1031', '1803009280001', '1803009280001', 'DEMI POCHAT DRAYVAR KHACHAB ', '120.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1032', '9536474602408', '9536474602408', 'TIB SIPOR PGT 103 FO RMZ ', '150.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1033', '3901767643547', '101456', 'PISTO NGAZ R6 KOMPLI KHACHAB ', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1034', '6941797829785', '1603-9', 'LOMBRAYAG TONIK AVO YAM ', '2750.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1035', '33', '33', 'KASK VMS NWAR MAT ', '4500.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1036', '6941797820218', '2310-157', 'SOPAP KOXI KHACHAB KARTON BAYDA ', '400.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1037', '2393', '2393', 'FIROG FAR +FAR KOMPLI OKINWA', '1850.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1038', '270', '6941797823585', 'LARACH LACHAN R6 KHACHAB ', '800.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1039', '6975416530189', '1035-36', 'FIBROKA FOX KHACHAB ', '4200.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1040', '955096973328', '160-2201', 'GABDA FRANE ZIYT KOXI DAR KHACHAB ', '1250.0', '0.0', '0.0', '1900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1041', '084-02', '6975416534101', 'RIBONI MBK 51', '230.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1042', '6941797410273', '1027', 'DEMI TABLO 11 BOBIN TNNN', '1100.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1043', '6941797807691', '2303-335', 'NAYMAN Z 100 KWAWSI ', '1150.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1044', '6941797820133', '2310-139', 'JWAN LAPIP R6', '350.0', '0.0', '0.0', '100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1045', '6941797823578', '1610817', 'PANDISK BOSTAR KHACHAB ', '1300.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1046', '6941797818833', '0201', 'CHAPO BOGI PG GOMI ROJ ', '110.0', '0.0', '0.0', '250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1047', '6975334766509', '6V2-0008', 'GABDA FRANE KOXI GOCH ', '180.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1048', '2206-2', '6975416530851', 'BOMBA ZIYT MOTAR R6 ', '300.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1049', 'S287', '6976886900649', 'KSIRATAR TROTINAT ', '780.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1050', 'S282', '6976886900526', 'KOVARP PWANI GIRAUDO TROTINAT ', '300.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1051', '22009', '6941797400847', 'TONDAR TA3 TARA CG', '150.0', '0.0', '0.0', '450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1052', '6975296974431', '2310-432', 'DEMI TABLO PGT 103 KHACHAB ', '1650.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1053', '6976063171527', '8,5X2,0', 'PNOU TROTINAT 8,5X2,0 KHACHAB ', '2200.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1054', '2310-238 1061', '2310-238 1061', 'T FORCH KOXI ', '2500.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1055', 'S52', '6976886900359', 'DIMARAR CHIMANO NWAR DAKICH ', '900.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1056', 'OMG-6', 'OMG-6', 'DIMARAR Tourney', '500.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1057', '6941797813203', '4-0163', 'FAR AISTAYT KHACHAB ', '7300.0', '0.0', '0.0', '9500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1058', '6975296976626', '2308-354', 'SIYLANDRI DRAYVAR TMMP ', '4200.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1059', '1110A-ZY100T', '1110A-ZY100T', 'KARTAR KOXI SRIYR YAMAHA ', '3900.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1060', 'V0619', 'V0619', 'DIMARAR KOXI TNNN ', '1620.0', '0.0', '0.0', '2400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1061', '5WB-E7451', '5TNE163000-QGG0-Q', 'DIMARAR KOXI YAMAHA ', '1780.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1062', '14711FY100-FC', '14711FY100-FC', 'SOPAP KOXI HIGHTAC', '500.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1063', 'YAMAHA110-50MM', 'YAMAHA 110-50MM', 'PISTO KOXI YAMAHA 50', '9800.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1064', '90793AT ONCE02C0', '5WB-F6351-00', 'KABL FRAN KOXI YAMAHA ', '350.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1065', 'J005(8,5X15', 'J005(8,5X15', 'LIGALI KOXI YAMAHA ', '480.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1066', '0957000086811', '0957000086811', 'BOUGIE YAMAHA R6', '180.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1067', '17210-5WB-FC-02', '17210-5WB-FC-02', 'LAPIP KOXI HIGTAC', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1068', '13010-A6A-PTSY', '13010-A6A-PTSY', 'SAGMO R6 F3 150 YAMAHA ', '280.0', '0.0', '0.0', '650.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1069', 'MA-18', 'MA-18', 'KAFLA + FICHA FODO FRAZ HAMRAA ', '60.0', '0.0', '0.0', '250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1070', '50300-S5', '50300-S5', 'JOU DIRAKSYO KOXI HIHTAC', '580.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1071', '14550-ZY100-FC', '14550-ZY100-FC', 'TONDAR CHAN KOXI HIGHTAC', '380.0', '0.0', '0.0', '850.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1072', 'MA-11', 'MA-11', 'BOLON VIDONJ KOXI HIGNTAC', '135.0', '0.0', '0.0', '450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1073', 'MA-22', 'MA-22', 'LACHAN MOTAR KOXI YAMAHA 84', '400.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1074', 'YAMAHA110-50MM', 'YAMAHA110-50MM', 'SIYLANDRI KOXI YAMAHA MAJA 110', '3920.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1075', 'SPARE PARTS', 'SPARE PARTS', 'SIYLANDRI R6 150 SPARE KORICHI', '1750.0', '0.0', '0.0', '4200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1076', 'SZ-06,21', 'SZ-06,21', 'KABL KONTAR KOXI SA3ADA TWIYL ', '270.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1077', '6975334763195', '16X13MM 10G ', 'LIGALI FOX 10G', '280.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1078', '6975334761719', '472-0227', 'DISK FRAN OVITO +STANT ', '1250.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1079', '6975334763133', '426-0062 40,05X1,2', 'PISTO BOSTAR 40,05 JIJAL', '650.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1080', '992-0616', '6975334762990', 'VIS KARINAJ WAHDO KHDAR JIYJAL ', '6.0', '0.0', '0.0', '20.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1081', 'BM6A', 'BM6A', 'BOUGIE MOCHAR SPARK JIYJAL', '150.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1082', '6W2-0004', '6W2-0004', 'FIRIDO FRAN KOXI PARTS JIYJAL ', '350.0', '0.0', '0.0', '650.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1083', '472-0221', '472-0221', 'RIGILATAR KOXI 4 FICH PARTAS', '580.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1084', '6975334763331', '995-3466', 'FILTR AISONS STONDAR JIYJAL ', '550.0', '0.0', '0.0', '150.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1085', '6975334760491', '7E2-0006', 'FILTRE AISONS DRAYVAR JIYJAL ', '120.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1086', '6975334765175', '6V2-0015', 'TROVIZAR KOXI 2 NWAR JIYJAL ', '850.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1087', '4 fich', '4 fich', 'FiCHA 4 FICH JIYJAL ', '70.0', '0.0', '0.0', '250.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1088', '252-0070', '252-0070', 'FICHA KLINITO 2+3', '50.0', '0.0', '0.0', '150.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1089', '6975334764987', '6W2-0034', 'KLINITO KOXI 1 TA3 LAMBA JIYJAL ', '950.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1090', '6975334763379', '882-0001', 'DEMI FITR Q BIX', '400.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1091', '6975334763447', '6W2-0048', 'DEMI FITR KOXI SAM JIYJAL ', '450.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1092', '120/70-12', '120/70-12', 'PNOU 120/70-12 HAM ', '4500.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1093', '42601-XGA-0002-KR', '42601-XGA-0002-KR', 'LAJONT SYM F3 150 NWAR ', '8200.0', '0.0', '0.0', '12000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1094', '44600', '44600', 'LAJONT SYM F3 GRI AV ', '6500.0', '0.0', '0.0', '10000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1095', '81200-A2C-0002', '81200-A2C-0002', 'PORTE BAGAGE SYM F3 DRG ', '5000.0', '0.0', '0.0', '7000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1096', 'SYM ', 'SYM ', 'KARWA SYM F3 DRG ', '3500.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1097', '6976087310223', 'XIA2211A-ARA', 'LOMBRAYAG F3 HAM AV ', '4200.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1098', 'XIA17211-XGA', 'XIA17211-XGA', 'DEMI FITR F3 HAM ', '390.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1099', '6976314691033', 'KD04029', 'DEMI TABLO R6  HAM 11 BOBIN ', '1650.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1100', '19510-XJ1', '19510-XJ1', 'RIYACHA SYM TA3 RIYH F3 J4 150', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1101', '31120-ASF-0000', '31120-ASF-0000', 'DEMI TABLO TONIK SYM ', '2500.0', '0.0', '0.0', '4200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1102', '6976049430105', '31120-X1A', 'DEMI TABLO F3 SYM DRG ', '4200.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1103', '6942279217755', 'H4 12V 60/55W', 'LAMBA H4 DAW 1 R6 HAM ', '200.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1104', 'H189', 'H189', 'T LAFORCH ORBIT', '3000.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1105', '6976087310216', 'XIA22000', 'LOMBRAYAG AV TONIK HAM ', '2900.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1106', '81131-XFA-,#.-Af', '81131-XFA-0002-AN', 'KARINAJ TA3 MAFTAH SYM F3 GRI NARDO ', '8950.0', '0.0', '0.0', '12000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1107', '77230-ALA-0001-C', '77230-ALA-0001-C', 'HDIYDA TA3 KORSI SYM F3 ', '500.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1108', '4260A-AES-0000', '4260A-AES-0000', 'MAYO TA3 DISK SYM J4 ', '3900.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1109', 'B-34', 'B-34', 'FIRIDO LOMBRAYAJ TONIK TOYU', '1600.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1110', '35010', '35010-F6T-0100', 'NAYMAN TONIK SYM DRG ', '3350.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1111', '2856138', '6976309630375', 'FIRIDO LOMBRAYAJ R6 HAM ', '1800.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1112', 'JB-05', 'JB-05', 'TONDAR LACHAN KOXI JIYBI ', '350.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1113', 'JB-16', 'JB-16', 'JWAN SPI KOXI JIYBI ', '330.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1114', '44800', '44800', 'APARAY KONTAR SYM F3 DRG ', '1050.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1115', 'XIA45105/45106', 'XIA45105/4506', 'LIPLAK F3 HAM ', '450.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1116', '6976087310346', 'XIA22132', 'JLODA LOMBRAYAG F3 ', '120.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1117', 'JB-16', 'JB-16', 'KIYLAS F3 JYBI ', '12000.0', '0.0', '0.0', '15000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1119', 'JB-03  158MM', 'JB-03  158MM', 'LAKS KLIKI R6 JIYBI ', '480.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1120', 'NO-39', 'NO-39', 'LOMBRAYAG AV KOXI YAMAHA ', '1950.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1121', '5WB-E7451-00', '5TNE163000PJ', 'LOMBRAYAG ARYAR KOXI YAMAHA ', '3950.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1122', '1088-02', '1088-02', 'MORTISAR PGT 103 GRI khachab ', '1580.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1123', '7720A-XFJ-0003-T3-C', '7720A-XFJ-0003-T3-C', 'KORSI F3 SYM DRG MARO', '8300.0', '0.0', '0.0', '11000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1124', 'DAA009', 'DAA009', 'KARTAR TWIYL F3 SYM DRG ', '14200.0', '0.0', '0.0', '17500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1125', '18300-XSA-0000', '18300-XSA-0000', 'CHATMA F3 SYM DRG ', '12000.0', '0.0', '0.0', '16000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1126', '17211-M9Q-0001', '17211-M9Q-0001', 'FILTRE J4 SYM DRG ', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1127', '17211-XGA-0000', '17211-XGA-0000', 'DEMI FILTRE F3 SYM DRG ', '600.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1128', 'XIA04011-1', 'XIA04011-1', 'FIBROKA R6 150 HAM ', '5300.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1129', '23-10', '23-10', 'DISK FRA F3 AV HAM ', '1500.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1130', '5050A-XGA-0003-A', '5050A-XGA-0003-A', 'KRA3 F3 SYM DRG TA3 WAST ', '3000.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1131', '16100-XGA-0101', '16100-XGA-0101', 'KARBIRATAR F3 SYM DRG ', '9200.0', '0.0', '0.0', '12500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1132', '2301A-ANT-0005', '2301A-ANT-0005', 'LOMBRAYAG F3 SYM DRG ', '12500.0', '0.0', '0.0', '16000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1133', 'H134', '6976314691972', 'TROVIZAR F3 HAM ', '1900.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1134', '12310-A61-0004', '12310-A61-0004', 'KOVAR KIYLAS F3 SYM DRG ', '1500.0', '0.0', '0.0', '4200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1135', '81132-XFA-0002', '81132-XFA-0002', 'KROCHI KASK F3 SYM DRG ', '400.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1136', 'H198', ']C121FHM018-1120-047', 'LABRAKAM F3 HAM ', '1600.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1137', 'G8029', '6976314692276', 'LAMBA LAD HAM ', '800.0', '0.0', '0.0', '1300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1138', '13000-XMG-0001', '13000-XMG-0001', 'FIBROKA F3 SYM DRG ', '10000.0', '0.0', '0.0', '16500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1139', '50350-ANA-0001', '50350-ANA-0001', 'SIPOR MOTAR F3 SYM DRG ', '3000.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1140', 'XIA04011-1', 'XIA04011-1', 'FIBROKA R6 HAM ', '5300.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1141', '80153-XFA-0000-AN', '80153-XFA-0000-AN', 'KOVAR BOGI F3 SYM TAHT KORSI GRI NARDO ', '2400.0', '0.0', '0.0', '3600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1142', '80151-XFA-0109-AA', '80151-XFA-0101-AG', 'JALDA TAHT KORSI F3 SYM GRI SORI ', '4550.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1143', '80100XFA', '80100', 'BAVIYTA F3 SYM DRG ', '750.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1144', '53206-XFA-0000-AN', '53206-XFA-0000-AN', 'KOVA FAR F3 SYM DRG GRI NARDO ', '3950.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1145', '81141-XFA-0000-BA', '81141-XFA-0000-BA', 'KAJ KOFR F3 SYM DRG ZRAG Gri', '3950.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1147', '130/60-13', '130/60-13', 'PNOU OMG KORICHI 130/60-13', '3500.0', '0.0', '0.0', '5500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1148', '8350A-XF_R0003-K', '8360A-XFA-000335', 'LAJNAB F3 SYM G/D GRI NARDO ZARGA NWAR MAT ', '13000.0', '0.0', '0.0', '17000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1149', '83620-XFA-0003-KC', '83620-XFA-0003-KC', 'LAJNAB  F3 SYM DRG TA3 TAHT ', '6200.0', '0.0', '0.0', '8500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1150', '53205-XFA-0000-AN', '53205-XFA-0000-AN', 'KAJ FAR F3 SYM DRG GRI NARDO ', '3950.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1151', '3340A-XFA-0100', '3340A-XFA-0100', 'KLINITO AV F3 SYM DRG ', '3850.0', '0.0', '0.0', '5500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1152', '12211-GYB2-A000', '12211-GYB2-A000', 'TABLA KIYLAS SYM F2 ', '1000.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1153', '100/90-10', 'ISO-9001', 'PNOU DURO SAM ', '4800.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1154', '90/90-10', 'F231', 'PNOU KOXI FOTRAPUB 90-10+350-10', '2900.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1155', '110/70-16 TL 6PR', '110/70-16 TL 6PR', 'PNOU 110/70-16 TL 6PR OMG ', '4000.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1156', '120/80-16', 'MT 111', 'PNOU 120/80-16 MT111 FOTRAPUB ', '5100.0', '0.0', '0.0', '7200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1157', '120/70-12', '130/70-12', 'PNOU 120/70-12+130 FOTRAPUB ', '3800.0', '0.0', '0.0', '5500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1158', '28', '28', 'TARA VTT 28', '1500.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1159', '26', '24', 'TARA VTT 26+24', '1250.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1160', '119', '119', 'TIP FORCH KOXI YAMAHA ', '4050.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1161', '3', '3', 'LATONI TA3 BARD ', '3300.0', '0.0', '0.0', '5200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1162', 'VMS 30', 'VMS 30', 'FALIYZA VMS 30 SRIYRA ', '3250.0', '0.0', '0.0', '4600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1163', 'VMS 29', 'VMS 29', 'FALIYZA VMS 29', '4450.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1164', '180', '180', 'VIZYARA F3 NWAR +BLON MAKTOBA SYM ', '1100.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1165', '170', '170', 'VIZYARA KOXI ZARGA+BLON MAKTOBA VMS', '1200.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1166', 'TINA', 'TINA', 'LIPLAK FRA TONIK TOYU ', '230.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1167', 'MA-15', 'MA-15', 'SIYLANDRI BOSTAR 47 POLINI BWS50', '4200.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1168', '5TNE16300000', '5WB-E7451-00', 'KARBIRATAR KOXI YAMAHA ', '4450.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1169', 'MT2,5X16', 'MT2,5X16', 'LAJONT ST MT2,5X16', '7800.0', '0.0', '0.0', '12000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1170', '14711-FY100', '14711-FY100-FC', 'SOPAP KOXI HIGHTAC KHADRA ', '500.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1171', '11385GN', '11385GN', 'PORT BAGAG KOXI 2+1 YAMAHA ', '1350.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1172', '6941797820171', '1245', 'PORT BAGAG KOXI 1 ', '1650.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1173', 'MXTBZ1206', 'OMG', 'MOTAR ZNT 2072 OMG', '26500.0', '0.0', '0.0', '33000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1174', '112', '112', 'TAPI KOXI KOLAR ', '440.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1175', '66', '66', 'MASK AVO F3 NWAR MAT DOZYAM ', '6900.0', '0.0', '0.0', '10000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1176', 'MA-08', 'MA-08', 'KAJ KLIKI KOXI 2 PLASTIK ', '800.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1177', '80W90', '6194098682157', 'ZIYT LABWAT YAMAHA ', '330.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1178', '6971383620120', '024-53', 'ZDAR TA3 MAFTAH F3 DOZYAM NWAR ', '4450.0', '0.0', '0.0', '8000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1179', 'JB-05', 'JB-05', 'LAJONT KOXI ARYAR NWAR ', '3450.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1180', 'COXI ', 'COXI', 'LIPLAK FRN KOXI TOYU', '190.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1181', '4-0185', '6941797820966', 'DISK FRAN F3 KHACHAB ', '1400.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1182', 'T0-11', 'T0-11', 'DEMI FITR AISTAYT ', '550.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1183', 'A-18', 'A-18', 'KARWA 743 BANDO KORICHI ', '1100.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1184', 'A-12', 'A-12', 'DEMI FITR AISTAYT KORICHI ', '380.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1185', 'BOSTAR 50', '1000640200203', 'DEMI TABLO BOSTAR MBK ', '1900.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1186', 'T0-10', 'T0-10', 'ALARM VIBRAR TOYU ', '3200.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1187', 'GY6-150', 'GY6-150', 'LABRAKAM GY6 TOYU ZARGA ', '580.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1189', 'SAM P96', 'SAM P96', 'MP3 BURgar', '1780.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1190', 'SAM 13,38', 'SAM 13,38', 'MP3 SPEAKER SAM ', '2050.0', '0.0', '0.0', '3200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1191', 'JB-05', 'JB-05', 'LABRAKAM F2 ORBIT YIN MAI', '1150.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1192', '5WB-E7451-00', '5JNE163000-J', 'SIYLANDRI KOXI YAMAHA ', '3920.0', '0.0', '0.0', '6200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1193', '1120A-YMH100-', '1120A-YMH', 'KARTAR KOXI TWIL HIGHTAC ', '6580.0', '0.0', '0.0', '8500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1194', '06', '06', 'KONTAR KOXI 2 VMS DRG ', '8960.0', '0.0', '0.0', '12500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1195', '6975296972642', '0231', 'NAYMAN KOXI YAMOYCLE ', '1220.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1196', '6975596974070', '6975596974070', 'NAYMAN DRAYVAR FAKRON ', '1400.0', '0.0', '0.0', '3000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1197', '11341-YAMH100', '11341-YAMH100', 'KAJ KLIKI KOXI 2 HIGHTAC ', '3300.0', '0.0', '0.0', '5200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1198', '6941797890389', '1577-01', 'BOLON VIDONJ R6 KOMPL KHACHAB ', '135.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1199', '6975416531148', '0116', 'FIROG ZNT 70', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1200', '6941797823660', '2204-125', 'STATAR KARBIRATAR KOXI KHACHAB ', '480.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1201', '6975296974660', '1-1155', 'LAKS TARA AV KOXI KHACHAB ', '250.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1202', '6975296978194', '6975296978194', 'LOMBRAYAG R6 ARYAR MADI ', '2280.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1203', 'MA-14', 'MA-14', 'LARACH BANDISK R6 AMAR ', '850.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1204', 'MA-14', 'MA-14', 'LARACH VOLO R6 ', '700.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1205', 'MA-11', 'MA-11', 'KARBIRATAR R6 HIGHTAC ', '3750.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1206', 'JB-05', 'JB-05', 'POCHAT JWAN R6 KOMPLI JB', '300.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1207', 'A-22', 'A-22', 'FIRIDO LOMBRAYAJ R6 F3', '1150.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1208', '1630-9', '6941797829785', 'LOMBRAYAG AV TONIK YAMCYCLE ', '2580.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1209', '6975368610557', '69753686105', 'BANDISK R6 overrunning', '880.0', '0.0', '0.0', '2000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1210', 'MA-11', 'MA-11', 'BANDISK R6 TA3 LORMA HIGHTAC ', '1400.0', '0.0', '0.0', '2700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1211', 'HMD ', 'HMD ', 'KABL FRAN RLIYD HMD ', '700.0', '0.0', '0.0', '110.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1212', 'JOG50', 'JOG50', 'DIMARAR BOSTAR HIGHTAC ', '1450.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1213', '818-19,5-28', '818-19,5-28', 'KARWA 118 MINCJUE SAFRA ', '800.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1214', 'HE511208', 'JB-06', 'KASAT F3 ', '1050.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1215', 'JB-04', 'JB-04', 'DEMI FITR KOXI YAMAHA ', '400.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1216', '53200-S5', '53200-S5', 'T FORCH KOXI HIGHTAC ', '2800.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1217', '4S5-F6311-00', '4S5-F6311-00', 'FIBROKA KOXI YAMAHA ', '4380.0', '0.0', '0.0', '6700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1218', '1146-147', '1146-147', 'SIYLANDRI BOSTAR POLINI 40', '3320.0', '0.0', '0.0', '6000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1219', '53166', '53166', 'GASPA KSIRATAR R6 HIGHTAC ', '900.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1220', '024-1', '6941797819373', 'VOLO R6 VID ', '1250.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1221', '0123', '6941797807950', 'VOLO KOMPLI R6 YAM ', '2280.0', '0.0', '0.0', '3700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1222', '100224500203', '100224500203', 'SAGMO R6 150 SAM ', '280.0', '0.0', '0.0', '650.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1223', '6941797828658', '6941797828658', 'RYON VTT 28', '580.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1224', '6975368610557', '6975368610557', 'LOMBRAYAG AV R6 CHOROK', '1100.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1225', 'JB-16', 'JB-16', 'SOPAP F3 JB 150 ', '760.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1226', 'JB-05', 'JB-05', 'DEMI TABLO F3 JB ', '1480.0', '0.0', '0.0', '2800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1227', '1253', '1253', 'LOMBRAYAG AV F3 DAMAN ', '1700.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1228', '12V35/35W', 'B-35', 'LAMBA BOSTAR AV ', '850.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1229', '6975596974070', '6975596974070', 'LIPLAK R6 YUDA ', '185.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1230', '5WB-E7451-00', '5WB-E7451-00', 'POCHAT JWAN KOXI YAMAHA KOMPLI ', '400.0', '0.0', '0.0', '800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1231', 'CUK058', '058', 'KLINITO KOXI 1 VMS DRG ', '2800.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1232', 'COK057', '057', 'KLINITO AV KOXI 1 VMS DRG ', '2150.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1233', 'V0705', 'V0705', 'KLINITO AV KOXI 1 TNN TA3 LAMBA ', '1380.0', '0.0', '0.0', '2500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1234', '37', '37', 'LAJNAB KOXI 2 NWAR ', '4380.0', '0.0', '0.0', '8000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1235', '13010-A6A', '13010-A6A', 'SAGMO F3 150', '520.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1236', '5WB-F6311-00', '907931T ONCE0230', 'KABL KSIRATAR KOXI YAMAHA ', '340.0', '0.0', '0.0', '700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1237', '6975309860232', 'LY021 26X2,125 HDH', 'PNOU 26 LY021', '950.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1238', '6975416534835', 'HM-PG-112', 'KARTAR BIKAN ZARGA SIKL ', '2700.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1239', '24', '24', 'CHAMBRAYAR VTT 24 ', '340.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1240', '16', '16', 'CHAMBRAYAR VTT 16', '290.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1241', '3901763829891', '1062-3', 'MONBRA KOXI SIKL ', '530.0', '0.0', '0.0', '1100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1242', '6941797802245', '95-2201', 'KARTAR KOXI TA3 LAJORJ KHAC', '3200.0', '0.0', '0.0', '4700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1243', '8683709016019', '19016014', 'MORTISAR RMZ PGT DRG ', '2100.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1244', '415', '6976185560018', 'SANSLA SAFRA 415 KMC', '880.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1245', '6941797807912', '0305', 'PINYO LABWAT KOXI KOMPLI YAM ', '2950.0', '0.0', '0.0', '5000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1246', '3604709000209', '12000200', 'KARBIRATAR PGT RMZ ', '3600.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1247', '12001601', '8692791104559', 'FIRIDO FRAN PGT RYON DRG ', '650.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1248', '8683709505049', '12505040', 'MAYO TARA ARYAR 36 RMZ DRG ', '4400.0', '0.0', '0.0', '5600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1249', 'T56515-01-B2', 'T56515-01-B2', 'PISTO 125 CG STD+ UPPROUD', '850.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1250', '1000989628001', '1000989628001', 'LIZONTIFIT BOSTAR ', '150.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1251', '2303-585', '6941797825497', 'TROVIZAR DRAYVAR SIKL ', '100.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1252', '2017', '4819-2', 'TROVIZAR SYM TNN ', '1200.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1253', '13', '13', 'LAKS AV 13 VTT ', '750.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1254', '6975308496456', 'V0645', 'FLOTAR KOXI RIZAVWAR TNN ', '350.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1255', '6941797823417', '1173', 'TAPSI KARWA TA3 HDID TLIFON ', '1600.0', '0.0', '0.0', '2200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1256', '6975416534873', '116', 'TAPSI KARWA PLASTIK ORO ', '1050.0', '0.0', '0.0', '1450.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1257', 'HMH', 'HMH ', 'KLAPI PGT HAM SIKL ', '300.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1258', '8683709004849', '19004841', 'KARTAR PGT RMZ DRG ', '3900.0', '0.0', '0.0', '5500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1259', '4683598100145', '101000140', 'KIYLAS PGT RMZ DRG ', '2100.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1260', 'YAM01 ', 'YAM01', 'KARBIRATAR R6 KHACHAB DAMAN KHADRA ', '300.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1261', 'E1139001', 'E1139001', 'TAMBOR FRAN RYON RMZ DRG ', '950.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1262', '8434829014658', '31020940', 'FIBROKA ARSAL PGT VIS ', '4500.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1263', '1487', '1487', 'RIZAVWAR ZNT HAMRA ', '300.0', '0.0', '0.0', '4500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1264', '8683709400122', '124009120', 'GARDBO AV PGT RMZ KROMI ', '2100.0', '0.0', '0.0', '2900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1265', '5', '5', 'KRATIB KARI KOHLIYN', '2400.0', '0.0', '0.0', '3600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1266', '16', '16', 'CHAMBRAYAR 16 VTT ', '275.0', '0.0', '0.0', '400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1267', 'F83C8FG8G3B6D', 'SW5T39775Y0X3', 'ROSOL KORSI RMZ ', '400.0', '0.0', '0.0', '650.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1268', 'MBK ', 'MBK ', 'BOLON VOLO BIKAN ', '100.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1269', 'JB-50', 'JB-50', 'LPARAY KONTAR F3 JB', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1270', '6941797808087', '0190', 'LAKS KLIKI R6 TWIL YAM 168', '600.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1271', '031', '031', 'PISTO R6 125 KHACHAB KOTI ', '750.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1272', '6941797804553', '47,01', 'PISTO 47,01 TMMP ', '850.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1273', '2303-546,04', '6941797804218', 'PISTO KIT 46 ', '600.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1274', '8683709501843', '10501841', 'LAFLAKS LOMBRAYAG RMZ DRG ', '650.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1275', '8683709500327', '10500321', 'TAMBOR LOMBRAYAG PGT RMZ DRG ', '650.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1276', '8683709500723', '10500721', 'PORT BIYL PGT RMZ DRG KOMPLI ', '520.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1277', '17', '17', 'PNOU 17 TALYANE ', '2780.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1278', '6975296974882', '2203-373', 'PINYO LABRAKAM KOXI TA3 HDID ', '450.0', '0.0', '0.0', '1100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1279', '6975296970174', 'CX58CX59-24', 'BOUGIE RLIYDA TMM', '150.0', '0.0', '0.0', '300.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1280', '6975416534811', 'HM-PG-110', 'KARTAR MALOZI ', '500.0', '0.0', '0.0', '6700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1281', '1009923128001', '1009923128001', 'KLAPI BOSTAR MBK ', '450.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1282', '120', '120', 'LARACH R6 KROMI ', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1283', '1404-2', '1404-02', 'LATIYJ FRAN ZNT ', '150.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1284', '6441426625776', '1671', 'KARBIRATAR PGT 12 YAM CH ', '1550.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1285', '144DAHA5G223A', 'W969SSZ6Z609U', 'BOLON BANDISK R6 ', '90.0', '0.0', '0.0', '200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1286', '6975296973373', '2303-378', 'PAVIYTA KOXI 1', '100.0', '0.0', '0.0', '1700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1287', 'SZ-03,20,003,JN', 'SZ-03,20', 'KABL KSIRATAR F3 ', '280.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1288', 'PM2D028025', 'PM2D029025', 'LOMBRAYAG POLINI ', '800.0', '0.0', '0.0', '1600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1289', 'G1008001', 'G1008001', 'SOPAP PGT RMZ DRG ', '600.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1290', '12001224', '8692791104795', 'SAGMO 40 PGT ZARGIN ', '280.0', '0.0', '0.0', '500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1291', 'G1045001', 'G1045001', 'BOBIN KASAT RMZ TA3 DAKHAL', '490.0', '0.0', '0.0', '750.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1292', 'RMZ ', 'RMZ ', 'LAKS ARYAR PGT RMZ DRG ', '300.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1293', '8692791101480', '10501340', 'FIRIDO FRAN PGT GALMA KHODRIN', '830.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1294', '034', '034', 'MASK TA3 WAJH ', '780.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1295', 'AV 10', 'AV 10', 'KARTAR MBK 51 KHACHAB ', '2700.0', '0.0', '0.0', '4000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1296', '18', '18', 'CHAMBRAYAR 18 ZARGA SIKL ', '400.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1297', 'GY6200', 'GY6200', 'PISTO E 32 JB-03 MAJA', '1100.0', '0.0', '0.0', '1800.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1298', '110/90-16', '110/90-16', 'CHAMBRAYAR 16 CG ', '500.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1299', '44', '44', 'FIBROKA 51 44', '3600.0', '0.0', '0.0', '5400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1300', '6941797810639', '1206', 'FIBROKA AV 7 ZARGA DAKAR CH', '3800.0', '0.0', '0.0', '5200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1301', '2959', '2959', 'PLATO ZNT KOMPLI 420 ', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1302', '6975296579025', '0166-1', 'KLINITO KOXI 1 ARYAR SIKL ', '1400.0', '0.0', '0.0', '2600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1303', 'JD-24018', 'JD-24018', 'KADNA HDID BICYCLE', '450.0', '0.0', '0.0', '900.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1304', '2302-242', '6941797803839', 'LAKS KLIKI R6 SPORESTI TMMP FI 2', '2100.0', '0.0', '0.0', '3500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1305', '00', '00', 'LARB KOXI MOTSIKL ', '2950.0', '0.0', '0.0', '5000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1306', '1271-2', '1271-1', 'KABL FRAN+KSIRATAR TONIK SIKL ', '400.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1307', '6941797829167', '1630-1', 'SIYLANDRI TONIK SIKL ', '4500.0', '0.0', '0.0', '6500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1308', '6941797829174', '1630-2', 'PISTO TONIK SIKL ', '1150.0', '0.0', '0.0', '2100.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1309', '6941797830712', '0180-5', 'LACHAN MOTAR TONIK SIKL ', '600.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1310', '6941797831047', '4060', 'LABRAKAM TONIK ', '1600.0', '0.0', '0.0', '2700.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1311', '6941797830835', '1629-1', 'JOU DIRAKSYO TONIK SIKL ', '700.0', '0.0', '0.0', '1500.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1312', '020-10', '6941797830712', 'SOPAP TONIK SIKL ', '700.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1313', '6941797830705', '1014-15', 'KOVAR PWANI TONIK ', '500.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1314', '6941797831054', '6015', 'PINYO LABRAKAM TONIK ', '500.0', '0.0', '0.0', '1400.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1315', '43120-H1A', '43120-H1A', 'FIRIDO FRAN F3 SIKL ', '450.0', '0.0', '0.0', '1000.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1316', '6975416531414', '1167', 'FIRIDO FRAN PGT RYON KHACHAB ', '320.0', '0.0', '0.0', '600.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1317', '6941797827422', 'HA-2308-284', 'APARAY KONTAR KOXI KMOTO ', '550.0', '0.0', '0.0', '1200.0', '1.0', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
            db.execSQL("INSERT INTO PRODUIT (PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, STOCK_INI, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, DESTOCK_CODE_BARRE, DESTOCK_QTE, FAMILLE, PROMO, D1, D2, PP1_HT, ISNEW) VALUES ('1318', 'SAFRA ', 'SAFRA', 'SIYR KABL FRAN SAFRA TWIYLA ', '120.0', '0.0', '0.0', '200.0', '1.1', '1.0', '0.0', '0.0', '0.0', '0.0', '0.0', '0.0', NULL, NULL, NULL, NULL, '0.0', '', '0', NULL, NULL, '0.0', '1')");
        }catch (Exception e){
            Log.v("LOG_ADD_PRODUIT", e.getMessage());
        }

        db.close();

    }

    public void hasUniqueConstraint(String tableName) {
        Cursor cursor = null;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Get list of indexes on the table
            cursor = db.rawQuery("PRAGMA index_list(" + tableName + ")", null);
            while (cursor.moveToNext()) {

                @SuppressLint("Range") String indexName = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") int isUnique = cursor.getInt(cursor.getColumnIndex("unique"));
                @SuppressLint("Range") String origin = cursor.getString(cursor.getColumnIndex("origin"));

                if (isUnique == 1) {
                    Log.d("TRACKKK", indexName + " : " + origin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }
}
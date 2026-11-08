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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.safesoft.proapp.distribute.postData.PostData_StockError;
import com.safesoft.proapp.distribute.postData.PostData_Tournee1;
import com.safesoft.proapp.distribute.postData.PostData_Tournee2;
import com.safesoft.proapp.distribute.postData.PostData_commune;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;
import com.safesoft.proapp.distribute.postData.StockData;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by UK2015 on 21/08/2016.
 */
public class DATABASE extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 24; // Database version
    public static final String DATABASE_NAME = "safe_distribute_pro"; //Database name
    private static volatile DATABASE sInstance;
    private static volatile boolean sIndexesApplied = false;
    private final Context mContext;

    private final String PREFS = "ALL_PREFS";

    public static DATABASE getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (DATABASE.class) {
                if (sInstance == null) {
                    sInstance = new DATABASE(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public static void warmUp(@NonNull Context context) {
        new Thread(() -> {
            try {
                getInstance(context).getReadableDatabase();
            } catch (Throwable t) {
                Log.v("TRACKKK", "DATABASE warmUp: " + t.getMessage());
            }
        }, "db-warmup").start();
    }

    //Constructor DATABASE (keep public for backward compatibility, but prefer getInstance)
    public DATABASE(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context.getApplicationContext();
        setWriteAheadLoggingEnabled(true);
    }

    @Override
    public void onOpen(@NonNull SQLiteDatabase db) {
        super.onOpen(db);
        ensureIndexesOnce(db);
    }

    private void ensureIndexesOnce(@NonNull SQLiteDatabase db) {
        if (sIndexesApplied) return;
        synchronized (DATABASE.class) {
            if (sIndexesApplied) return;
            try {
                db.execSQL("PRAGMA synchronous = NORMAL");
                db.execSQL("PRAGMA journal_mode = WAL");
                db.execSQL("PRAGMA temp_store = MEMORY");
                String[] queries = new String[]{
                        "CREATE INDEX IF NOT EXISTS idx_client_code_client ON CLIENT(CODE_CLIENT)",
                        "CREATE INDEX IF NOT EXISTS idx_bon1_code_client ON BON1(CODE_CLIENT)",
                        "CREATE INDEX IF NOT EXISTS idx_bon1_temp_code_client ON BON1_TEMP(CODE_CLIENT)",
                        "CREATE INDEX IF NOT EXISTS idx_bon2_num_bon ON BON2(NUM_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_bon2_temp_num_bon ON BON2_TEMP(NUM_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_bon2_code_barre ON BON2(CODE_BARRE)",
                        "CREATE INDEX IF NOT EXISTS idx_bon2_temp_code_barre ON BON2_TEMP(CODE_BARRE)",
                        "CREATE INDEX IF NOT EXISTS idx_achat2_num_bon ON ACHAT2(NUM_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_achat2_temp_num_bon ON ACHAT2_TEMP(NUM_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_position_num_bon ON POSITION(NUM_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_carnet_c_code_client ON CARNET_C(CODE_CLIENT)",
                        "CREATE INDEX IF NOT EXISTS idx_produit_famille ON PRODUIT(FAMILLE)",
                        "CREATE INDEX IF NOT EXISTS idx_produit_produit ON PRODUIT(PRODUIT)",
                        "CREATE INDEX IF NOT EXISTS idx_client_wilaya ON CLIENT(WILAYA)",
                        "CREATE INDEX IF NOT EXISTS idx_client_commune ON CLIENT(COMMUNE)",
                        "CREATE INDEX IF NOT EXISTS idx_bon1_date_bon ON BON1(DATE_BON)",
                        "CREATE INDEX IF NOT EXISTS idx_achat1_date_achat ON ACHAT1(DATE_ACHAT)",
                        "CREATE INDEX IF NOT EXISTS idx_communes_code_wilaya ON COMMUNES(CODE_WILAYA)"
                };
                for (String q : queries) {
                    try { db.execSQL(q); } catch (Exception ignore) {}
                }
                sIndexesApplied = true;
            } catch (Exception e) {
                Log.v("TRACKKK", "ensureIndexesOnce: " + e.getMessage());
            }
        }
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
                "ISNEW INTEGER, " +
                "IS_IMPORTED INTEGER DEFAULT 0" +
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
                "IS_IMPORTED INTEGER DEFAULT 0, " +
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
                "REMARQUE VARCHAR DEFAULT '', " +
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
                "REMARQUE VARCHAR DEFAULT '', " +
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
                "REMARQUES VARCHAR DEFAULT '', " +
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
                "REMARQUES VARCHAR DEFAULT '', " +
                "EXPORTATION VARCHAR, " +
                "UTILISATEUR VARCHAR, " +
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)) DEFAULT 0)");

        db.execSQL("CREATE TABLE IF NOT EXISTS INV1(" +
                "NUM_INV VARCHAR PRIMARY KEY, " +
                "DATE_INV VARCHAR, " +
                "HEURE_INV VARCHAR, " +
                "LIBELLE VARCHAR, " +
                "NBR_PRODUIT VARCHAR, " +
                "UTILISATEUR VARCHAR, " +
                "CODE_DEPOT VARCHAR, " +
                "IS_EXPORTED boolean CHECK (IS_EXPORTED IN (0,1)), " +
                "DATE_EXPORT_INV VARCHAR, " +
                "BLOCAGE VARCHAR, " +
                "EXPORTATION VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS INV2(" +
                "RECORDID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "CODE_BARRE VARCHAR , " +
                "NUM_INV VARCHAR, " +
                "PRODUIT VARCHAR, " +
                "NBRE_COLIS DOUBLE, " +
                "COLISSAGE DOUBLE, " +
                "PA_HT DOUBLE, " +
                "QTE DOUBLE, " +
                "QTE_TMP DOUBLE, " +
                "QTE_NEW DOUBLE, " +
                "TVA DOUBLE, " +
                "VRAC VARCHAR, " +
                "CODE_DEPOT VARCHAR )");

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
                "IS_IMPORTED INTEGER DEFAULT 0, " +
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


        db.execSQL("CREATE TABLE IF NOT EXISTS WILAYAS (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR NOT NULL, LATITUDE numeric NOT NULL, LONGITUDE numeric NOT NULL, WILAYA_2 VARCHAR )");

        db.execSQL("CREATE TABLE IF NOT EXISTS COMMUNES (RECORDID INTEGER NOT NULL PRIMARY KEY, CODE_COMMUNE VARCHAR NOT NULL, CODE_WILAYA INTEGER NOT NULL, COMMUNE VARCHAR NOT NULL, foreign key(CODE_WILAYA) references WILAYAS(ID) on delete cascade)");

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_inv2_numinv ON INV2(NUM_INV);");

        db.execSQL("CREATE TABLE IF NOT EXISTS NOTES(" +
                "NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOTE_TEXT TEXT NOT NULL, " +
                "CREATED_AT INTEGER NOT NULL" +
                ")");

        insert_wilaya_commune_into_database(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        Log.v("TRACKKK", "================>  ON UPGRADE EXECUTED");

        String[] list_requet = new String[25];

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
                "REMARQUES VARCHAR DEFAULT '', " +
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

        list_requet[17] = "CREATE INDEX IF NOT EXISTS idx_inv2_numinv ON INV2(NUM_INV)";

        list_requet[18] = "CREATE TABLE IF NOT EXISTS NOTES(" +
                "NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NOTE_TEXT TEXT NOT NULL, " +
                "CREATED_AT INTEGER NOT NULL" +
                ")";

        list_requet[19] = "ALTER TABLE BON1 ADD COLUMN REMARQUE VARCHAR DEFAULT '' ";

        list_requet[20] = "ALTER TABLE BON1_TEMP ADD COLUMN REMARQUE VARCHAR DEFAULT '' ";

        list_requet[21]  = "ALTER TABLE PRODUIT ADD COLUMN IS_IMPORTED INTEGER DEFAULT 0";

        list_requet[22]  = "ALTER TABLE CLIENT ADD COLUMN IS_IMPORTED INTEGER DEFAULT 0";
        list_requet[23]  = "ALTER TABLE FOURNIS ADD COLUMN IS_IMPORTED INTEGER DEFAULT 0";

        list_requet[24] = "ALTER TABLE WILAYAS ADD COLUMN WILAYA_2 VARCHAR ";


        for (String s : list_requet) {
            try {
                db.execSQL(s);
            } catch (SQLiteException e) {
                Log.v("TRACKKK", e.getMessage());
            }
        }

        // ============================================================
        // Mise à jour V23 : Remplir WILAYA_2 par matching nom wilaya
        // (valeurs exactes depuis le JSON fourni)
        // ============================================================
        if (oldVersion < 23) {
            try {
                String updateWilayas =
                    "UPDATE WILAYAS SET WILAYA_2 = CASE " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'ADRAR' OR ID=1 THEN '01 - ADRAR' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'CHLEF' OR ID=2 THEN '02 - CHLEF' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'LAGHOUAT' OR ID=3 THEN '03 - LAGHOUAT' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'OUM EL BOUAGHI%' OR ID=4 THEN '04 - OUM EL BOUAGHI' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BATNA' OR ID=5 THEN '05 - BATNA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'BEJAI%' OR ID=6 THEN '06 - BEJAIA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BISKRA' OR ID=7 THEN '07 - BISKRA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BECHAR' OR ID=8 THEN '08 - BECHAR' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BLIDA' OR ID=9 THEN '09 - BLIDA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BOUIRA' OR ID=10 THEN '10 - BOUIRA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TAMANRASSET' OR ID=11 THEN '11 - TAMANRASET' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'TEBESS%' OR ID=12 THEN '12 - TEBESSA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TLEMCEN' OR ID=13 THEN '13 - TLEMCEN' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TIARET' OR ID=14 THEN '14 - TIARET' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'TIZI%OUZOU%' OR ID=15 THEN '15 - TIZI-OUZOU' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'ALGER' OR ID=16 THEN '16 - ALGER' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'DJELFA' OR ID=17 THEN '17 - DJELFA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'JIJEL' OR ID=18 THEN '18 - JIJEL' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'SETIF' OR ID=19 THEN '19 - SETIF' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'SAIDA%' OR UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'SAEFD%' OR ID=20 THEN '20 - SAIDA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'SKIKDA' OR ID=21 THEN '21 - SKIKDA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'SIDI BEL ABB%' OR ID=22 THEN '22 - SIDI BEL ABBES' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'ANNABA' OR ID=23 THEN '23 - ANNABA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'GUELMA' OR ID=24 THEN '24 - GUELMA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'CONSTANTINE' OR ID=25 THEN '25 - CONSTANTINE' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'MEDEA' OR ID=26 THEN '26 - MEDEA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'MOSTAGANEM' OR ID=27 THEN '27 - MOSTAGANEM' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'M%SILA' OR ID=28 THEN '28 - MSILA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'MASCARA' OR ID=29 THEN '29 - MASCARA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'OUARGLA' OR ID=30 THEN '30 - OUARGLA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'ORAN' OR ID=31 THEN '31 - ORAN' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%BAYADH%' OR ID=32 THEN '32 - EL-BAYADH' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'ILLIZI' OR ID=33 THEN '33 - ILLIZI' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'BORDJ%BOUARRIRIDJ%' OR UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'BORDJ%BOU ARRERIDJ%' OR ID=34 THEN '34 - BORDJ BOUARRIRIDJ' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'BOUMERDES' OR ID=35 THEN '35 - BOUMERDES' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%TARF%' OR ID=36 THEN '36 - EL-TARF' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TINDOUF' OR ID=37 THEN '37 - TINDOUF' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TISSEMSILT' OR ID=38 THEN '38 - TISSEMSILT' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%OUED%' OR ID=39 THEN '39 - EL-OUED' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'KHENCHELA' OR ID=40 THEN '40 - KHENCHELA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'SOUK%AHRA%' OR ID=41 THEN '41 - SOUK-AHRAS' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'TIPAZA' OR ID=42 THEN '42 - TIPAZA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'MILA' OR ID=43 THEN '43 - MILA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'AIN%DEFLA%' OR ID=44 THEN '44 - AIN-DEFLA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'NAAMA' OR ID=45 THEN '45 - NAAMA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'AIN%TEMOUCHENT%' OR ID=46 THEN '46 - AIN-TEMOUCHENT' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'GHARDA%' OR ID=47 THEN '47 - GHARDAIA' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) = 'RELIZANE' OR ID=48 THEN '48 - RELIZANE' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'TIMIMOUNE%' OR ID=49 THEN '49 - TIMIMOUNE' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'BORDJ BADJI MOKHTAR%' OR ID=50 THEN '50 - BORDJ BADJI MOKHTAR' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'OULED DJELLAL%' OR ID=51 THEN '51 - OULED DJELLAL' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'BENI ABBES%' OR ID=52 THEN '52 - BENI ABBES' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'IN SALAH%' OR ID=53 THEN '53 - IN SALAH' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'IN GUEZZAM%' OR ID=54 THEN '54 - IN GUEZZAM' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'TOUGGOURT%' OR ID=55 THEN '55 - TOUGGOURT' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'DJANET%' OR ID=56 THEN '56 - DJANET' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%MGHAIR%' OR UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%M%GHAIR%' OR ID=57 THEN '57 - EL M''GHAIR' " +
                    "WHEN UPPER(REPLACE(REPLACE(REPLACE(NAME, '-', ' '), '''', ' '), '  ', ' ')) LIKE 'EL%MENIA%' OR ID=58 THEN '58 - EL MENIA' " +
                    "ELSE WILAYA_2 END";
                db.execSQL(updateWilayas);

                // Wilayas 49-58 manquantes (pas présentes sur ancienne version) → insertion complète
                ContentValues cv;
                int[] ids = {49,50,51,52,53,54,55,56,57,58};
                String[] wnames = {"Timimoun","Bordj Badji Mokhtar","Ouled Djellal","Beni Abbes","In Salah","In Guezzam","Touggourt","Djanet","El M'Ghair","El Menia"};
                String[] wla2 =   {"49 - TIMIMOUNE","50 - BORDJ BADJI MOKHTAR","51 - OULED DJELLAL","52 - BENI ABBES","53 - IN SALAH","54 - IN GUEZZAM","55 - TOUGGOURT","56 - DJANET","57 - EL M'GHAIR","58 - EL MENIA"};
                for (int k=0;k<ids.length;k++) {
                    try {
                        Cursor cCk = db.rawQuery("SELECT ID FROM WILAYAS WHERE ID=?", new String[]{String.valueOf(ids[k])});
                        if (!cCk.moveToFirst()) {
                            cv = new ContentValues();
                            cv.put("ID", ids[k]);
                            cv.put("NAME", wnames[k]);
                            cv.put("LATITUDE", 0.0);
                            cv.put("LONGITUDE", 0.0);
                            cv.put("WILAYA_2", wla2[k]);
                            db.insert("WILAYAS", null, cv);
                        }
                        cCk.close();
                    } catch (Exception ignore) {}
                }
            } catch (Exception e) {
                Log.v("TRACKKK", "Erreur maj WILAYA_2 V23: " + e.getMessage());
            }
        }

        // ============================================================
        // Mise à jour V24 : Réordonner WILAYAS (ID 49-58 → code officiel)
        // et régénération complète table COMMUNES (nouveau schema)
        // avec les données du JSON officiel Algérien.
        // WILAYAS/COMMUNES sont des tables de référence → pas de perte données utilisateur
        // ============================================================
        if (oldVersion < 24) {
            try {
                db.execSQL("DROP TABLE IF EXISTS COMMUNES");
                db.execSQL("DROP TABLE IF EXISTS WILAYAS");
                db.execSQL("CREATE TABLE IF NOT EXISTS WILAYAS (ID INTEGER NOT NULL PRIMARY KEY, NAME VARCHAR NOT NULL, LATITUDE numeric NOT NULL, LONGITUDE numeric NOT NULL, WILAYA_2 VARCHAR )");
                db.execSQL("CREATE TABLE IF NOT EXISTS COMMUNES (RECORDID INTEGER NOT NULL PRIMARY KEY, CODE_COMMUNE VARCHAR NOT NULL, CODE_WILAYA INTEGER NOT NULL, COMMUNE VARCHAR NOT NULL, foreign key(CODE_WILAYA) references WILAYAS(ID) on delete cascade)");
                insert_wilaya_commune_into_database(db);
            } catch (Exception e) {
                Log.v("TRACKKK", "Erreur maj V24 WILAYAS+COMMUNES: " + e.getMessage());
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////
    //////                             Functions                             //////
    ///////////////////////////////////////////////////////////////////////////////

    public static final class NoteRow {
        public final long id;
        @NonNull
        public final String text;
        public final long createdAt;

        public NoteRow(long id, @NonNull String text, long createdAt) {
            this.id = id;
            this.text = text;
            this.createdAt = createdAt;
        }
    }

    public long insertNote(@NonNull String noteText) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("NOTE_TEXT", noteText);
        values.put("CREATED_AT", System.currentTimeMillis());
        return db.insert("NOTES", null, values);
    }

    public int deleteNote(long noteId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("NOTES", "NOTE_ID = ?", new String[]{String.valueOf(noteId)});
    }

    @NonNull
    public ArrayList<NoteRow> selectNotes() {
        ArrayList<NoteRow> notes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT NOTE_ID, NOTE_TEXT, CREATED_AT FROM NOTES ORDER BY NOTE_ID DESC", null);
            int idxId = cursor.getColumnIndex("NOTE_ID");
            int idxText = cursor.getColumnIndex("NOTE_TEXT");
            int idxCreatedAt = cursor.getColumnIndex("CREATED_AT");
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.isNull(idxId) ? 0 : cursor.getLong(idxId);
                    String text = cursor.isNull(idxText) ? "" : cursor.getString(idxText);
                    long createdAt = cursor.isNull(idxCreatedAt) ? 0 : cursor.getLong(idxCreatedAt);
                    notes.add(new NoteRow(id, text, createdAt));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return notes;
    }

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


    public boolean update_transfered_canceled_commande(String num_bon) {
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
            try { db.execSQL("PRAGMA foreign_keys = OFF"); } catch (Throwable ignore) {}
            db.beginTransaction();
            try {

                db.delete("FOURNIS", null, null);

                for (int i = 0; i < fournisseurs.size(); i++) {
                    insert_into_fournisseur(fournisseurs.get(i));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                try { db.endTransaction(); } catch (Throwable ignore) {}
                try { db.execSQL("PRAGMA foreign_keys = ON"); } catch (Throwable ignore) {}
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        } catch (Throwable t) {
            Log.v("TRACKKK", "ExecuteTransactionFournisseur: " + t.getMessage());
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
                values.put("IS_IMPORTED", fournisseur.isImported);

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
            try { db.execSQL("PRAGMA foreign_keys = OFF"); } catch (Throwable ignore) {}
            db.beginTransaction();

            try {

                db.delete("CLIENT", null, null);

                for (int i = 0; i < clients.size(); i++) {

                    insert_into_client(clients.get(i));
                }

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                try { db.endTransaction(); } catch (Throwable ignore) {}
                try { db.execSQL("PRAGMA foreign_keys = ON"); } catch (Throwable ignore) {}
            }
        } catch (SQLiteDatabaseLockedException sqlilock) {
            Log.v("TRACKKK", Objects.requireNonNull(sqlilock.getMessage()));
        } catch (Throwable t) {
            Log.v("TRACKKK", "ExecuteTransactionClient: " + t.getMessage());
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
                values.put("ACHATS", client.achat_client);
                values.put("VERSER", client.verser_client);
                values.put("SOLDE", client.solde_client);
                values.put("SOLDE_INI", client.solde_ini);
                values.put("LATITUDE", client.latitude);
                values.put("LONGITUDE", client.longitude);
                values.put("CREDIT_LIMIT", client.credit_limit);
                values.put("ISNEW", client.isNew);
                values.put("IS_IMPORTED", client.isImported);

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
        values.put("IS_IMPORTED", produit.isImported);

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
            try { db.execSQL("PRAGMA foreign_keys = OFF"); } catch (Throwable ignore) {}
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
        } catch (Throwable t) {
            Log.v("TRACKKK", "ExecuteTransactionProduit: " + t.getMessage());
        } finally {
            try { db.endTransaction(); } catch (Throwable ignore) {}
            try { db.execSQL("PRAGMA foreign_keys = ON"); } catch (Throwable ignore) {}
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


    public boolean checkClientExists(String codeClient) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM CLIENT WHERE CODE_CLIENT = ? LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{codeClient});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
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

                params.pv1_titre = notNull(cursor.getString(cursor.getColumnIndex("PV1_TITRE")), params.pv1_titre);
                params.prix_2 = cursor.getInt(cursor.getColumnIndex("PRIX_2"));
                params.pv2_titre = notNull(cursor.getString(cursor.getColumnIndex("PV2_TITRE")), params.pv2_titre);
                params.prix_3 = cursor.getInt(cursor.getColumnIndex("PRIX_3"));
                params.pv3_titre = notNull(cursor.getString(cursor.getColumnIndex("PV3_TITRE")), params.pv3_titre);
                params.prix_4 = cursor.getInt(cursor.getColumnIndex("PRIX_4"));
                params.pv4_titre = notNull(cursor.getString(cursor.getColumnIndex("PV4_TITRE")), params.pv4_titre);
                params.prix_5 = cursor.getInt(cursor.getColumnIndex("PRIX_5"));
                params.pv5_titre = notNull(cursor.getString(cursor.getColumnIndex("PV5_TITRE")), params.pv5_titre);
                params.prix_6 = cursor.getInt(cursor.getColumnIndex("PRIX_6"));
                params.pv6_titre = notNull(cursor.getString(cursor.getColumnIndex("PV6_TITRE")), params.pv6_titre);
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

    /**
     * SQLite renvoie null pour une colonne NULL. On retombe alors sur la valeur par
     * defaut du modele afin d'eviter un NullPointerException cote UI (ex : construction
     * d'un IconSpinnerItem avec un titre de prix null).
     */
    private static String notNull(String value, String fallback) {
        return value != null ? value : fallback;
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
                fournisseur.isImported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

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
        SQLiteDatabase db = this.getReadableDatabase();
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
                    client.achat_client = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                    client.verser_client = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                    client.solde_client = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                    client.solde_ini = cursor.getDouble(cursor.getColumnIndex("SOLDE_INI"));
                    client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                    client.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));
                    client.isImported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

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
        wilaya_aucune.wilaya_2 = "<Aucune>";
        wilaya_aucune.latitude = 0;
        wilaya_aucune.longitude = 0;
        wilayas.add(wilaya_aucune);

        int idxName = -1, idxWil2 = -1;
        try { idxName = cursor.getColumnIndexOrThrow("NAME"); } catch (Exception ignore) {}
        try { idxWil2 = cursor.getColumnIndexOrThrow("WILAYA_2"); } catch (Exception ignore) {}

        if (cursor.moveToFirst()) {
            do {
                PostData_wilaya wilaya = new PostData_wilaya();

                wilaya.id = cursor.getInt(cursor.getColumnIndex("ID"));

                // Récupérer NAME brut et WILAYA_2
                String nameVal = null;
                if (idxName >= 0 && !cursor.isNull(idxName)) nameVal = cursor.getString(idxName);
                String wil2Val = null;
                if (idxWil2 >= 0 && !cursor.isNull(idxWil2)) wil2Val = cursor.getString(idxWil2);

                wilaya.wilaya_2 = wil2Val;

                // AFFICHAGE : utiliser WILAYA_2 si valide, sinon fallback NAME
                if (wil2Val != null && !wil2Val.trim().isEmpty()) {
                    wilaya.wilaya = wil2Val;
                } else if (nameVal != null && !nameVal.trim().isEmpty()) {
                    wilaya.wilaya = nameVal;
                } else {
                    wilaya.wilaya = "Wilaya " + wilaya.id;
                }

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

        PostData_commune commune_aucune = new PostData_commune();
        commune_aucune.id = 0;
        commune_aucune.code_commune = "";
        commune_aucune.commune = "<Aucune>";
        commune_aucune.post_code = 0;
        commune_aucune.wilaya_id = 0;
        commune_aucune.latitude = 0;
        commune_aucune.longitude = 0;
        communes.add(commune_aucune);

        int idxId = -1, idxCommune = -1, idxCodeCommune = -1, idxCodeWilaya = -1;
        int idxNameOld = -1, idxWilayaIdOld = -1, idxPostOld = -1, idxLatOld = -1, idxLonOld = -1;
        try { idxId = cursor.getColumnIndexOrThrow("RECORDID"); } catch (Exception e) { idxId = -1; }
        try { idxCodeCommune = cursor.getColumnIndexOrThrow("CODE_COMMUNE"); } catch (Exception e) { idxCodeCommune = -1; }
        try { idxCodeWilaya = cursor.getColumnIndexOrThrow("CODE_WILAYA"); } catch (Exception e) { idxCodeWilaya = -1; }
        try { idxCommune = cursor.getColumnIndexOrThrow("COMMUNE"); } catch (Exception e) { idxCommune = -1; }
        try { idxNameOld = cursor.getColumnIndexOrThrow("NAME"); } catch (Exception e) { idxNameOld = -1; }
        try { idxWilayaIdOld = cursor.getColumnIndexOrThrow("WILAYA_ID"); } catch (Exception e) { idxWilayaIdOld = -1; }
        try { idxPostOld = cursor.getColumnIndexOrThrow("POST_CODE"); } catch (Exception e) { idxPostOld = -1; }
        try { idxLatOld = cursor.getColumnIndexOrThrow("LATITUDE"); } catch (Exception e) { idxLatOld = -1; }
        try { idxLonOld = cursor.getColumnIndexOrThrow("LONGITUDE"); } catch (Exception e) { idxLonOld = -1; }
        // fallback ancien ID si RECORDID absent (schéma V<24)
        if (idxId == -1) { try { idxId = cursor.getColumnIndexOrThrow("ID"); } catch (Exception ignore) {} }

        if (cursor.moveToFirst()) {
            do {
                PostData_commune commune = new PostData_commune();

                if (idxId != -1) commune.id = cursor.getInt(idxId);
                if (idxCodeCommune != -1) commune.code_commune = cursor.getString(idxCodeCommune);
                if (idxCodeWilaya != -1) commune.wilaya_id = cursor.getInt(idxCodeWilaya);
                else if (idxWilayaIdOld != -1) commune.wilaya_id = cursor.getInt(idxWilayaIdOld);
                if (idxCommune != -1) commune.commune = cursor.getString(idxCommune);
                else if (idxNameOld != -1) commune.commune = cursor.getString(idxNameOld);
                if (idxPostOld != -1) commune.post_code = cursor.getInt(idxPostOld);
                if (idxLatOld != -1) commune.latitude = cursor.getDouble(idxLatOld);
                if (idxLonOld != -1) commune.longitude = cursor.getDouble(idxLonOld);

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
                fournisseur.isImported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));


            } while (cursor.moveToNext());
        }
        cursor.close();
        return fournisseur;
    }


    @SuppressLint("Range")
    public double select_last_price_from_database(String table, String code_client, String codebarre) {

        double last_price = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String query_bon1 = "SELECT " +
                "BON1.DATE_BON, " +
                "BON2.RECORDID, " +
                "BON2.NUM_BON, " +
                "' VENTE' AS OPERATION, " +
                "coalesce(BON2.QTE,0) QTE, " +
                "coalesce(BON2.QTE_GRAT,0) QTE_GRAT, " +
                "coalesce(BON2.PV_HT,0) PV_HT , " +
                "coalesce(BON2.TVA,0) TVA , " +
                "BON2.PV_HT * (1+(coalesce(BON2.TVA,0)/100)) PU_TTC " +
                //"    BON2.pa_ht * (1+(coalesce(BON2.TVA,0)/100)) PA_TTC " +
                " FROM BON2 LEFT JOIN BON1 ON (BON1.NUM_BON = BON2.NUM_BON) " +
                " WHERE BON1.CODE_CLIENT= ? AND BON2.CODE_BARRE= ? order by 1 desc,2 desc,4 desc";


        String query_bon1_temp = "SELECT " +
                "BON1_TEMP.DATE_BON, " +
                "BON2_TEMP.RECORDID, " +
                "BON2_TEMP.NUM_BON, " +
                "' VENTE' AS OPERATION, " +
                "coalesce(BON2_TEMP.QTE,0) QTE, " +
                "coalesce(BON2_TEMP.QTE_GRAT,0) QTE_GRAT, " +
                "coalesce(BON2_TEMP.PV_HT,0) PV_HT , " +
                "coalesce(BON2_TEMP.TVA,0) TVA , " +
                "BON2_TEMP.PV_HT * (1+(coalesce(BON2_TEMP.TVA,0)/100)) PU_TTC " +
                //"    BON2.pa_ht * (1+(coalesce(BON2.TVA,0)/100)) PA_TTC " +
                " FROM BON2_TEMP LEFT JOIN BON1_TEMP ON (BON1_TEMP.NUM_BON = BON2_TEMP.NUM_BON) " +
                " WHERE BON1_TEMP.CODE_CLIENT= ? AND BON2_TEMP.CODE_BARRE= ? order by 1 desc,2 desc,4 desc";

        Cursor cursor;
        if (table.equals("BON1")) {
            cursor = db.rawQuery(query_bon1, new String[]{code_client, codebarre});
        } else {
            cursor = db.rawQuery(query_bon1_temp, new String[]{code_client, codebarre});
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            int idx = cursor.getColumnIndex("PU_TTC");
            if (idx != -1) {
                last_price = cursor.getDouble(idx);
            }
        }
        cursor.close();

        return last_price;
    }

    //============================== FUNCTION SELECT Clients FROM Client TABLE ===============================
    @SuppressLint("Range")
    public PostData_Client select_client_from_database(String code_client) {

        PostData_Client client = new PostData_Client();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CLIENT WHERE CODE_CLIENT = ?", new String[]{code_client});
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
                client.achat_client = cursor.getDouble(cursor.getColumnIndex("ACHATS"));
                client.verser_client = cursor.getDouble(cursor.getColumnIndex("VERSER"));
                client.solde_client = cursor.getDouble(cursor.getColumnIndex("SOLDE"));
                client.solde_ini = cursor.getDouble(cursor.getColumnIndex("SOLDE_INI"));
                client.credit_limit = cursor.getDouble(cursor.getColumnIndex("CREDIT_LIMIT"));
                client.isNew = cursor.getInt(cursor.getColumnIndex("ISNEW"));
                client.isImported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT CODE_CLIENT FROM CLIENT WHERE CLIENT = ?", new String[]{clientt});
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
        SQLiteDatabase db = this.getReadableDatabase();
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
    public ArrayList<PostData_Produit> select_produits_from_database(String query) {
        ArrayList<PostData_Produit> produits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // ✅ Changement ici

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            // ✅ Préparer les index une seule fois
            int idxId = cursor.getColumnIndex("PRODUIT_ID");
            int idxCodeBarre = cursor.getColumnIndex("CODE_BARRE");
            int idxRefProd = cursor.getColumnIndex("REF_PRODUIT");
            int idxProd = cursor.getColumnIndex("PRODUIT");
            int idxPaHt = cursor.getColumnIndex("PA_HT");
            int idxPamp = cursor.getColumnIndex("PAMP");
            int idxTva = cursor.getColumnIndex("TVA");
            int idxPv1 = cursor.getColumnIndex("PV1_HT");
            int idxPv2 = cursor.getColumnIndex("PV2_HT");
            int idxPv3 = cursor.getColumnIndex("PV3_HT");
            int idxPv4 = cursor.getColumnIndex("PV4_HT");
            int idxPv5 = cursor.getColumnIndex("PV5_HT");
            int idxPv6 = cursor.getColumnIndex("PV6_HT");
            int idxPvLimite = cursor.getColumnIndex("PV_LIMITE");
            int idxStock = cursor.getColumnIndex("STOCK");
            int idxColissage = cursor.getColumnIndex("COLISSAGE");
            int idxStockIni = cursor.getColumnIndex("STOCK_INI");
            int idxStockColis = cursor.getColumnIndex("STOCK_COLIS");
            int idxStockVrac = cursor.getColumnIndex("STOCK_VRAC");
            int idxDescription = cursor.getColumnIndex("DETAILLE");
            int idxFamille = cursor.getColumnIndex("FAMILLE");
            int idxDestockType = cursor.getColumnIndex("DESTOCK_TYPE");
            int idxDestockCodeBarre = cursor.getColumnIndex("DESTOCK_CODE_BARRE");
            int idxDestockQte = cursor.getColumnIndex("DESTOCK_QTE");
            int idxPromo = cursor.getColumnIndex("PROMO");
            int idxD1 = cursor.getColumnIndex("D1");
            int idxD2 = cursor.getColumnIndex("D2");
            int idxPp1 = cursor.getColumnIndex("PP1_HT");
            int idxQtePromo = cursor.getColumnIndex("QTE_PROMO");
            int idxIsNew = cursor.getColumnIndex("ISNEW");
            int idxIsImported = cursor.getColumnIndex("IS_IMPORTED");

            if (cursor.moveToFirst()) {
                do {
                    PostData_Produit produit = new PostData_Produit();

                    // ✅ Assignation directe
                    produit.produit_id = cursor.isNull(idxId) ? "" : cursor.getString(idxId);
                    produit.code_barre = cursor.isNull(idxCodeBarre) ? "" : cursor.getString(idxCodeBarre);
                    produit.ref_produit = cursor.isNull(idxRefProd) ? "" : cursor.getString(idxRefProd);
                    produit.produit = cursor.isNull(idxProd) ? "" : cursor.getString(idxProd);

                    produit.pa_ht = cursor.isNull(idxPaHt) ? 0 : cursor.getDouble(idxPaHt);
                    produit.pamp = cursor.isNull(idxPamp) ? 0 : cursor.getDouble(idxPamp);
                    produit.tva = cursor.isNull(idxTva) ? 0 : cursor.getDouble(idxTva);
                    produit.pv1_ht = cursor.isNull(idxPv1) ? 0 : cursor.getDouble(idxPv1);
                    produit.pv2_ht = cursor.isNull(idxPv2) ? 0 : cursor.getDouble(idxPv2);
                    produit.pv3_ht = cursor.isNull(idxPv3) ? 0 : cursor.getDouble(idxPv3);
                    produit.pv4_ht = cursor.isNull(idxPv4) ? 0 : cursor.getDouble(idxPv4);
                    produit.pv5_ht = cursor.isNull(idxPv5) ? 0 : cursor.getDouble(idxPv5);
                    produit.pv6_ht = cursor.isNull(idxPv6) ? 0 : cursor.getDouble(idxPv6);
                    produit.pv_limite = cursor.isNull(idxPvLimite) ? 0 : cursor.getDouble(idxPvLimite);

                    produit.stock = cursor.isNull(idxStock) ? 0 : cursor.getDouble(idxStock);
                    produit.colissage = cursor.isNull(idxColissage) ? 0 : cursor.getDouble(idxColissage);
                    produit.stock_ini = cursor.isNull(idxStockIni) ? 0 : cursor.getDouble(idxStockIni);
                    produit.stock_colis = cursor.isNull(idxStockColis) ? 0 : cursor.getInt(idxStockColis);
                    produit.stock_vrac = cursor.isNull(idxStockVrac) ? 0 : cursor.getInt(idxStockVrac);

                    produit.description = cursor.isNull(idxDescription) ? "" : cursor.getString(idxDescription);
                    produit.famille = cursor.isNull(idxFamille) ? "" : cursor.getString(idxFamille);
                    produit.destock_type = cursor.isNull(idxDestockType) ? "" : cursor.getString(idxDestockType);
                    produit.destock_code_barre = cursor.isNull(idxDestockCodeBarre) ? "" : cursor.getString(idxDestockCodeBarre);
                    produit.destock_qte = cursor.isNull(idxDestockQte) ? 0 : cursor.getDouble(idxDestockQte);

                    produit.promo = cursor.isNull(idxPromo) ? 0 : cursor.getInt(idxPromo);
                    produit.d1 = cursor.isNull(idxD1) ? "" : cursor.getString(idxD1);
                    produit.d2 = cursor.isNull(idxD2) ? "" : cursor.getString(idxD2);
                    produit.pp1_ht = cursor.isNull(idxPp1) ? 0 : cursor.getDouble(idxPp1);
                    produit.qte_promo = cursor.isNull(idxQtePromo) ? 0 : cursor.getDouble(idxQtePromo);
                    produit.isNew = cursor.isNull(idxIsNew) ? 0 : cursor.getInt(idxIsNew);
                    produit.isImported = cursor.isNull(idxIsNew) ? 0 : cursor.getInt(idxIsImported);

                    produits.add(produit);

                } while (cursor.moveToNext());
            }

        } catch (OutOfMemoryError oom) {
            oom.printStackTrace(); // ✅ Important pour traquer la cause
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            // db.close(); // évite de fermer si ton DBHelper est global
        }

        return produits;
    }



    //============================== FUNCTION SELECT Produits FROM Produit TABLE ===============================
    @SuppressLint("Range")
    public ArrayList<PostData_Produit> select_produits_from_database_for_update(String query) {
        ArrayList<PostData_Produit> produits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); // ✅ Changement ici

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);

            // ✅ Préparer les index une seule fois
            int idxId = cursor.getColumnIndex("PRODUIT_ID");
            int idxCodeBarre = cursor.getColumnIndex("CODE_BARRE");
            int idxRefProd = cursor.getColumnIndex("REF_PRODUIT");
            int idxProd = cursor.getColumnIndex("PRODUIT");
            int idxPaHt = cursor.getColumnIndex("PA_HT");
            int idxPamp = cursor.getColumnIndex("PAMP");
            int idxTva = cursor.getColumnIndex("TVA");
            int idxPv1 = cursor.getColumnIndex("PV1_HT");
            int idxPv2 = cursor.getColumnIndex("PV2_HT");
            int idxPv3 = cursor.getColumnIndex("PV3_HT");
            int idxPv4 = cursor.getColumnIndex("PV4_HT");
            int idxPv5 = cursor.getColumnIndex("PV5_HT");
            int idxPv6 = cursor.getColumnIndex("PV6_HT");
            int idxPvLimite = cursor.getColumnIndex("PV_LIMITE");
            int idxStock = cursor.getColumnIndex("STOCK");
            int idxColissage = cursor.getColumnIndex("COLISSAGE");
            int idxStockIni = cursor.getColumnIndex("STOCK_INI");
            int idxStockColis = cursor.getColumnIndex("STOCK_COLIS");
            int idxStockVrac = cursor.getColumnIndex("STOCK_VRAC");
            int idxPhoto =  cursor.getColumnIndex("PHOTO");
            int idxDescription = cursor.getColumnIndex("DETAILLE");
            int idxFamille = cursor.getColumnIndex("FAMILLE");
            int idxDestockType = cursor.getColumnIndex("DESTOCK_TYPE");
            int idxDestockCodeBarre = cursor.getColumnIndex("DESTOCK_CODE_BARRE");
            int idxDestockQte = cursor.getColumnIndex("DESTOCK_QTE");
            int idxPromo = cursor.getColumnIndex("PROMO");
            int idxD1 = cursor.getColumnIndex("D1");
            int idxD2 = cursor.getColumnIndex("D2");
            int idxPp1 = cursor.getColumnIndex("PP1_HT");
            int idxQtePromo = cursor.getColumnIndex("QTE_PROMO");
            int idxIsNew = cursor.getColumnIndex("ISNEW");
            int idxIsImported = cursor.getColumnIndex("IS_IMPORTED");

            if (cursor.moveToFirst()) {
                do {
                    PostData_Produit produit = new PostData_Produit();

                    // ✅ Assignation directe
                    produit.produit_id = cursor.isNull(idxId) ? "" : cursor.getString(idxId);
                    produit.code_barre = cursor.isNull(idxCodeBarre) ? "" : cursor.getString(idxCodeBarre);
                    produit.ref_produit = cursor.isNull(idxRefProd) ? "" : cursor.getString(idxRefProd);
                    produit.produit = cursor.isNull(idxProd) ? "" : cursor.getString(idxProd);

                    produit.pa_ht = cursor.isNull(idxPaHt) ? 0 : cursor.getDouble(idxPaHt);
                    produit.pamp = cursor.isNull(idxPamp) ? 0 : cursor.getDouble(idxPamp);
                    produit.tva = cursor.isNull(idxTva) ? 0 : cursor.getDouble(idxTva);
                    produit.pv1_ht = cursor.isNull(idxPv1) ? 0 : cursor.getDouble(idxPv1);
                    produit.pv2_ht = cursor.isNull(idxPv2) ? 0 : cursor.getDouble(idxPv2);
                    produit.pv3_ht = cursor.isNull(idxPv3) ? 0 : cursor.getDouble(idxPv3);
                    produit.pv4_ht = cursor.isNull(idxPv4) ? 0 : cursor.getDouble(idxPv4);
                    produit.pv5_ht = cursor.isNull(idxPv5) ? 0 : cursor.getDouble(idxPv5);
                    produit.pv6_ht = cursor.isNull(idxPv6) ? 0 : cursor.getDouble(idxPv6);
                    produit.pv_limite = cursor.isNull(idxPvLimite) ? 0 : cursor.getDouble(idxPvLimite);

                    produit.stock = cursor.isNull(idxStock) ? 0 : cursor.getDouble(idxStock);
                    produit.colissage = cursor.isNull(idxColissage) ? 0 : cursor.getDouble(idxColissage);
                    produit.stock_ini = cursor.isNull(idxStockIni) ? 0 : cursor.getDouble(idxStockIni);
                    produit.stock_colis = cursor.isNull(idxStockColis) ? 0 : cursor.getInt(idxStockColis);
                    produit.stock_vrac = cursor.isNull(idxStockVrac) ? 0 : cursor.getInt(idxStockVrac);

                    if (idxPhoto != -1 && !cursor.isNull(idxPhoto))
                        produit.photo = cursor.getBlob(idxPhoto);

                    produit.description = cursor.isNull(idxDescription) ? "" : cursor.getString(idxDescription);
                    produit.famille = cursor.isNull(idxFamille) ? "" : cursor.getString(idxFamille);
                    produit.destock_type = cursor.isNull(idxDestockType) ? "" : cursor.getString(idxDestockType);
                    produit.destock_code_barre = cursor.isNull(idxDestockCodeBarre) ? "" : cursor.getString(idxDestockCodeBarre);
                    produit.destock_qte = cursor.isNull(idxDestockQte) ? 0 : cursor.getDouble(idxDestockQte);

                    produit.promo = cursor.isNull(idxPromo) ? 0 : cursor.getInt(idxPromo);
                    produit.d1 = cursor.isNull(idxD1) ? "" : cursor.getString(idxD1);
                    produit.d2 = cursor.isNull(idxD2) ? "" : cursor.getString(idxD2);
                    produit.pp1_ht = cursor.isNull(idxPp1) ? 0 : cursor.getDouble(idxPp1);
                    produit.qte_promo = cursor.isNull(idxQtePromo) ? 0 : cursor.getDouble(idxQtePromo);
                    produit.isNew = cursor.isNull(idxIsNew) ? 0 : cursor.getInt(idxIsNew);
                    produit.isImported = cursor.isNull(idxIsNew) ? 0 : cursor.getInt(idxIsImported);

                    produits.add(produit);

                } while (cursor.moveToNext());
            }

        } catch (OutOfMemoryError oom) {
            oom.printStackTrace(); // ✅ Important pour traquer la cause
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            // db.close(); // évite de fermer si ton DBHelper est global
        }

        return produits;
    }


    public Bitmap getProductPhotoBitmap(@NonNull String produitId) {
        SQLiteDatabase db = getReadableDatabase();
        Bitmap bitmap = null;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT PHOTO FROM PRODUIT WHERE PRODUIT_ID = ?", new String[]{produitId});

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                byte[] blob = cursor.getBlob(0);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.RGB_565; // lower memory
                options.inSampleSize = 2; // scale down if needed

                bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length, options);
            }else{

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return bitmap;
    }



    @SuppressLint("Range")
    public PostData_Produit select_one_produit_from_database(String querry) {
        PostData_Produit produit = null;
        SQLiteDatabase db = this.getReadableDatabase();
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
                produit.isImported = cursor.getInt(cursor.getColumnIndex("IS_IMPORTED"));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return produit;
    }

    //========================== FUNCTION SELECT Code_barre FROM DATABASE ===========================
    @SuppressLint("Range")
    public String select_codebarre_from_database(String querry) {
        String code_barre = "";
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
        SQLiteDatabase db = this.getReadableDatabase();
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
                int remarqueIndex = cursor.getColumnIndex("REMARQUE");
                if (remarqueIndex >= 0) {
                    bon1.remarque = cursor.getString(remarqueIndex);
                }

                bon1s.add(bon1);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return bon1s;
    }


    @SuppressLint("Range")
    public int select_count_from_database(String querry) {
        //ArrayList<PostData_Bon1> bon1s = new ArrayList<PostData_Bon1>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(querry, null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }


    //////////////select_bon1_from_database2 revise par smail le 10.05.2022/////////////////////////
    @SuppressLint("Range")
    public PostData_Bon1 select_bon1_from_database2(String querry) {
        PostData_Bon1 bon1 = new PostData_Bon1();
        SQLiteDatabase db = this.getReadableDatabase();
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
                int remarqueIndex = cursor.getColumnIndex("REMARQUE");
                if (remarqueIndex >= 0) {
                    bon1.remarque = cursor.getString(remarqueIndex);
                }


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
                args1.put("REMARQUE", bon1.remarque);
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
                values.put("REMARQUE", bon1.remarque);
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

    public ArrayList<PostData_StockError> check_stock_product(ArrayList<PostData_Bon2> final_panier) {

        ArrayList<PostData_StockError> errors = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Extract all CODE_BARRE
        HashSet<String> codeBarresSet = new HashSet<>();
        for (PostData_Bon2 item : final_panier) {
            if (item.codebarre!= null) {
                codeBarresSet.add(item.codebarre);
            }
        }

        if (codeBarresSet.isEmpty()) return errors;

        // 2. Build IN clause (?, ?, ?...)
        StringBuilder placeholders = new StringBuilder();
        String[] args = new String[codeBarresSet.size()];

        int i = 0;
        for (String code : codeBarresSet) {
            placeholders.append("?");
            if (i < codeBarresSet.size() - 1) placeholders.append(",");
            args[i++] = code;
        }

        // 3. Query once
        Cursor cursor = db.rawQuery(
                "SELECT CODE_BARRE, PRODUIT, IFNULL(STOCK,0) FROM PRODUIT WHERE CODE_BARRE IN (" + placeholders + ")",
                args
        );

        // 4. Store in HashMap
        HashMap<String, StockData> stockMap = new HashMap<>();

        while (cursor.moveToNext()) {
            String code = cursor.getString(0);
            String produit = cursor.getString(1);
            double stock = cursor.getDouble(2);

            stockMap.put(code, new StockData(produit, stock));
        }
        cursor.close();

        // 5. Compare with panier
        for (PostData_Bon2 item : final_panier) {

            String code = item.codebarre;
            double qte = item.qte;

            StockData data = stockMap.get(code);

            if (data == null) {
                // product not found
                errors.add(new PostData_StockError("Produit introuvable (" + code + ")", qte, 0));
                continue;
            }

            if (qte > data.stock) {
                errors.add(new PostData_StockError(data.produit, qte, data.stock));
            }
        }

        return errors;
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
                        client.achat_client = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_client = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_client = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                //update client

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_client + bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_client + bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_client + bon1.montant_bon) - bon1.verser));
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
                        client.achat_client = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_client = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_client = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                ContentValues args = new ContentValues();
                args.put("ACHATS", String.valueOf(client.achat_client - bon1.montant_bon));
                args.put("VERSER", String.valueOf(client.verser_client - bon1.verser));
                args.put("SOLDE", String.valueOf((client.solde_client - bon1.montant_bon) + bon1.verser));
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


    public boolean update_bon1_temp_commande_annuler(PostData_Bon1 bon1_temp) {

        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                String checkQuery = "SELECT BLOCAGE FROM BON1_TEMP WHERE NUM_BON = '" + bon1_temp.num_bon + "'";
                Cursor c = db.rawQuery(checkQuery, null);
                if (c.moveToFirst()) {
                    String currentBlocage = c.getString(0);
                    c.close();
                    if ("A".equals(currentBlocage)) {
                        db.endTransaction();
                        return false;
                    }
                } else {
                    c.close();
                }

                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar c1 = Calendar.getInstance();
                String date_livraison = date_format.format(c1.getTime());

                //update_bon1
                ContentValues args1 = new ContentValues();
                args1.put("BLOCAGE", "A");
                args1.put("DATE_LIV", date_livraison);
                String selection1 = "NUM_BON=?";
                String[] selectionArgs1 = {bon1_temp.num_bon};
                db.update("BON1_TEMP", args1, selection1, selectionArgs1);


                db.setTransactionSuccessful();
                executed = true;
            } finally {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
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

    public boolean update_client(PostData_Client client, String old_code_client) {
        boolean executed = false;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();
            try {

                ContentValues args = new ContentValues();
                //args.put("CODE_CLIENT", client.code_client);
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
                args.put("SOLDE_INI", client.solde_ini);
                args.put("LATITUDE", client.latitude);
                args.put("LONGITUDE", client.longitude);
                String selection = "CODE_CLIENT=?";
                String[] selectionArgs = {old_code_client};
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

                String checkQuery = "SELECT BLOCAGE FROM " + _table + " WHERE NUM_BON = '" + num_bon + "'";
                Cursor c = db.rawQuery(checkQuery, null);
                if (c.moveToFirst()) {
                    String currentBlocage = c.getString(0);
                    c.close();
                    if ("A".equals(currentBlocage)) {
                        db.endTransaction();
                        return false;
                    }
                } else {
                    c.close();
                }

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
                args.put("REMARQUE", bon1.remarque);

                String selection = "NUM_BON=?";
                String[] selectionArgs = {num_bon};
                db.update(_table, args, selection, selectionArgs);

                db.setTransactionSuccessful();
                executed = true;
            } finally {
                if (db.inTransaction()) {
                    db.endTransaction();
                }
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
                            client.achat_client = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                            client.verser_client = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                            client.solde_client = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                        } while (cursor1.moveToNext());
                    }

                    cursor1.close();

                    ContentValues args = new ContentValues();
                    args.put("ACHATS", String.valueOf(client.achat_client - bon1.montant_bon));
                    args.put("VERSER", String.valueOf(client.verser_client - bon1.verser));
                    args.put("SOLDE", String.valueOf((client.solde_client - bon1.montant_bon) + bon1.verser));
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
                        client.achat_client = cursor1.getDouble(cursor1.getColumnIndex("ACHATS"));
                        client.verser_client = cursor1.getDouble(cursor1.getColumnIndex("VERSER"));
                        client.solde_client = cursor1.getDouble(cursor1.getColumnIndex("SOLDE"));

                    } while (cursor1.moveToNext());
                }

                cursor1.close();

                ContentValues args1 = new ContentValues();
                //args.put("ACHATS", String.valueOf(Double.valueOf(client.achat_montant) - Double.valueOf(bon1.montant_bon)));
                args1.put("VERSER", client.verser_client - carnet_c.carnet_versement);
                args1.put("SOLDE", client.solde_client + carnet_c.carnet_versement);
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
        double total_versement_bon = 0.00;
        double total_versement_client = 0.00;
        double total_versement = 0.00;
        double tot_remise = 0.00;
        double tot_credit = 0.00;
        double total_benifice = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "BON2.PRODUIT, " +
                "SUM(BON2.QTE) AS TOT_QTE, " +
                "BON2.PV_HT * (1 + BON2.TVA / 100) AS PV_TTC, " +
                "SUM(BON2.QTE) * (BON2.PV_HT + (BON2.PV_HT * BON2.TVA / 100)) AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN BON2 ON BON2.NUM_BON = BON1.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                "WHERE date(substr(BON1.DATE_BON,7,4)||'-'||substr(BON1.DATE_BON,4,2)||'-'||substr(BON1.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                "0.0 AS PV_TTC," +
                "0.0 AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN BON2 ON BON2.NUM_BON = BON1.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                "WHERE date(substr(BON1.DATE_BON,7,4)||'-'||substr(BON1.DATE_BON,4,2)||'-'||substr(BON1.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                etatv.pv_ttc = cursor.getDouble(cursor.getColumnIndex("PV_TTC"));
                etatv.code_parent = "1";

                tot_qte = tot_qte + etatv.quantite;
                tot_montant_par_qte = tot_montant_par_qte + etatv.montant;

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        cursor.close();


        String querry1 = "SELECT " +
                "BON1.REMISE, " +
                "BON1.TOT_HT, " +
                "BON1.TOT_TVA, " +
                "BON1.TOT_HT + BON1.TOT_TVA AS MONTANT_BON_TTC, " +
                "0 AS VERSEMENTS_CLIENT, " +
                "BON1.VERSER AS VERSEMENT_BON, " +
                "(BON1.TOT_HT - BON1.REMISE) -  BON1.MONTANT_ACHAT AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1 " +
                "LEFT JOIN CARNET_C ON BON1.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1.CODE_CLIENT " +
                //" WHERE (BON1.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";
                " WHERE date(substr(BON1.DATE_BON,7,4)||'-'||substr(BON1.DATE_BON,4,2)||'-'||substr(BON1.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                "0 AS MONTANT_BON_TTC, " +
                "CARNET_C.VERSEMENTS AS VERSEMENTS_CLIENT, " +
                "0 AS VERSEMENT_BON, " +
                "0 AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT  " +
                //"WHERE (CARNET_C.DATE_CARNET BETWEEN '" + from_d + "' AND '" + to_d + "') ";
                " WHERE date(substr(CARNET_C.DATE_CARNET,7,4)||'-'||substr(CARNET_C.DATE_CARNET,4,2)||'-'||substr(CARNET_C.DATE_CARNET,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";


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
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_TTC"));
                etatv2.versement_bon = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENT_BON"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                //etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENTS_CLIENT"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_benifice = total_benifice + etatv2.benifice;
                total_versement_bon = total_versement_bon + etatv2.versement_bon;
                total_versement_client = total_versement_client + etatv2.vers_client;
                total_versement = total_versement + (etatv2.versement_bon + etatv2.vers_client);

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

        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte - tot_remise;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

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
        etatv.produit = "VERSEMENT BONS :";
        etatv.montant = total_versement_bon;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "VERSEMENT CLIENTS :";
        etatv.montant = total_versement_client;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSEMENTS :";
        etatv.montant = total_versement;
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
    public ArrayList<PostData_Client> select_versed_list_client_from_database(String wilaya, String commune, String c_client, String from_d, String to_d) {

        ArrayList<PostData_Client> all_clients = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "CLIENT.CODE_CLIENT, " +
                "CLIENT.CLIENT, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "IFNULL(SUM(CARNET_C.VERSEMENTS), 0) AS TOTAL_VERSEMENTS " +
                "FROM CLIENT " +
                "LEFT JOIN CARNET_C ON CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "AND date(substr(CARNET_C.DATE_CARNET,7,4)||'-'||substr(CARNET_C.DATE_CARNET,4,2)||'-'||substr(CARNET_C.DATE_CARNET,1,2)) " +
                "BETWEEN date('" + from_d + "') AND date('" + to_d + "') " +
                "WHERE 1=1 ";

        if (c_client != null) {
            querry += " AND CLIENT.CODE_CLIENT = '" + c_client + "' ";
        }
        if (!Objects.equals(wilaya, "<Aucune>")) {
            querry += " AND CLIENT.WILAYA = '" + wilaya + "' ";
        }
        if (!Objects.equals(commune, "<Aucune>")) {
            querry += " AND CLIENT.COMMUNE = '" + commune + "' ";
        }

        querry += " GROUP BY CLIENT.CODE_CLIENT, CLIENT.CLIENT, CLIENT.WILAYA, CLIENT.COMMUNE ";
        querry += " HAVING TOTAL_VERSEMENTS > 0";


        Cursor cursor = db.rawQuery(querry, null);
        if (cursor.moveToFirst()) {
            do {

                PostData_Client client = new PostData_Client();

                client.code_client = cursor.getString(cursor.getColumnIndex("CODE_CLIENT"));
                client.client = cursor.getString(cursor.getColumnIndex("CLIENT"));
                client.wilaya = cursor.getString(cursor.getColumnIndex("WILAYA"));
                client.commune = cursor.getString(cursor.getColumnIndex("COMMUNE"));
                client.verser_client = cursor.getDouble(cursor.getColumnIndex("TOTAL_VERSEMENTS"));

                all_clients.add(client);
            } while (cursor.moveToNext());
        }

        cursor.close();


        return all_clients;
    }

    @SuppressLint("Range")
    public ArrayList<PostData_Etatv> select_etatc_from_database(String wilaya, String commune, String c_client, String from_d, String to_d) {

        ArrayList<PostData_Etatv> all_etatv = new ArrayList<>();

        double tot_qte = 0.00;
        double tot_montant_par_qte = 0.00;
        double tot_montant_total = 0.00;
        double total_versement_bon = 0.00;
        double total_versement_client = 0.00;
        double total_versement = 0.00;
        double tot_remise = 0.00;
        double tot_credit = 0.00;
        double total_benifice = 0.00;

        SQLiteDatabase db = this.getWritableDatabase();

        String querry = "SELECT " +
                "BON2_TEMP.PRODUIT, " +
                "SUM(BON2_TEMP.QTE) AS TOT_QTE, " +
                "BON2_TEMP.PV_HT * (1 + BON2_TEMP.TVA / 100) AS PV_TTC, " +
                "SUM(BON2_TEMP.QTE) * (BON2_TEMP.PV_HT + (BON2_TEMP.PV_HT * BON2_TEMP.TVA / 100)) AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN BON2_TEMP ON BON2_TEMP.NUM_BON = BON1_TEMP.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                "WHERE date(substr(BON1_TEMP.DATE_BON,7,4)||'-'||substr(BON1_TEMP.DATE_BON,4,2)||'-'||substr(BON1_TEMP.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";
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
                "0.0 AS PV_TTC," +
                "0.0 AS TOTAL_MONTANT_PRODUIT, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN BON2_TEMP ON BON2_TEMP.NUM_BON = BON1_TEMP.NUM_BON " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                "WHERE date(substr(BON1_TEMP.DATE_BON,7,4)||'-'||substr(BON1_TEMP.DATE_BON,4,2)||'-'||substr(BON1_TEMP.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                etatv.pv_ttc = cursor.getDouble(cursor.getColumnIndex("PV_TTC"));
                etatv.code_parent = "1";

                tot_qte = tot_qte + etatv.quantite;
                tot_montant_par_qte = tot_montant_par_qte + etatv.montant;

                all_etatv.add(etatv);
            } while (cursor.moveToNext());
        }

        cursor.close();


        String querry1 = "SELECT " +
                "BON1_TEMP.REMISE, " +
                "BON1_TEMP.TOT_HT, " +
                "BON1_TEMP.TOT_TVA, " +
                "BON1_TEMP.TOT_HT - BON1_TEMP.REMISE AS MONTANT_BON_TTC, " +
                "0 AS VERSEMENTS_CLIENT, " +
                "BON1_TEMP.VERSER AS VERSEMENT_BON, " +
                "(BON1_TEMP.TOT_HT - BON1_TEMP.REMISE) -  BON1_TEMP.MONTANT_ACHAT AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN CARNET_C ON BON1_TEMP.CODE_CLIENT = CARNET_C.CODE_CLIENT " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = BON1_TEMP.CODE_CLIENT " +
                //" WHERE (BON1_TEMP.DATE_BON BETWEEN '" + from_d + "' AND '" + to_d + "') ";
                "WHERE date(substr(BON1_TEMP.DATE_BON,7,4)||'-'||substr(BON1_TEMP.DATE_BON,4,2)||'-'||substr(BON1_TEMP.DATE_BON,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                "0 AS MONTANT_BON_TTC, " +
                "CARNET_C.VERSEMENTS AS VERSEMENTS_CLIENT, " +
                "0 AS VERSEMENT_BON, " +
                "0 AS BENIFICE, " +
                "CLIENT.WILAYA AS WILAYA, " +
                "CLIENT.COMMUNE AS COMMUNE " +
                "FROM CARNET_C " +
                "LEFT JOIN CLIENT ON CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT  " +
                //"WHERE (CARNET_C.DATE_CARNET BETWEEN '" + from_d + "' AND '" + to_d + "') ";
                " WHERE date(substr(CARNET_C.DATE_CARNET,7,4)||'-'||substr(CARNET_C.DATE_CARNET,4,2)||'-'||substr(CARNET_C.DATE_CARNET,1,2)) BETWEEN date('" + from_d + "') AND date('" + to_d + "') ";

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
                etatv2.total_par_bon_ht = cursor1.getDouble(cursor1.getColumnIndex("MONTANT_BON_TTC"));
                etatv2.versement_bon = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENT_BON"));
                etatv2.benifice = cursor1.getDouble(cursor1.getColumnIndex("BENIFICE"));
                etatv2.code_parent = "-6";
                //etatv.remise = cursor.getString(cursor.getColumnIndex("REMISE"));
                etatv2.vers_client = cursor1.getDouble(cursor1.getColumnIndex("VERSEMENTS_CLIENT"));

                tot_montant_total = tot_montant_total + etatv2.total_par_bon_ht;
                total_benifice = total_benifice + etatv2.benifice;
                total_versement_bon = total_versement_bon + etatv2.versement_bon;
                total_versement_client = total_versement_client + etatv2.vers_client;
                total_versement = total_versement + (etatv2.versement_bon + etatv2.vers_client);

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

        PostData_Etatv etatv = new PostData_Etatv();
        etatv.produit = "TOTAL QTE :";
        etatv.quantite = tot_qte;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL MONTANT :";
        etatv.montant = tot_montant_par_qte - tot_remise;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

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
        etatv.produit = "VERSEMENT BONS :";
        etatv.montant = total_versement_bon;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "VERSEMENT CLIENTS :";
        etatv.montant = total_versement_client;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "TOTAL VERSEMENTS :";
        etatv.montant = total_versement;
        etatv.code_parent = "-6";
        all_etatv.add(etatv);

        etatv = new PostData_Etatv();
        etatv.produit = "CREDIT TOTAL A CE JOUR :";
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
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues(10); // Capacity optimization
            values.put("CODE_BARRE", inv2s.codebarre);
            values.put("NUM_INV", inv2s.num_inv);
            values.put("PRODUIT", inv2s.produit);
            values.put("NBRE_COLIS", inv2s.nbr_colis);
            values.put("COLISSAGE", inv2s.colissage);
            values.put("PA_HT", inv2s.pa_ht);
            values.put("QTE", inv2s.qte_theorique);
            values.put("QTE_NEW", inv2s.qte_physique + inv2s.vrac); // Calcul direct optimisé
            values.put("CODE_DEPOT", inv2s.code_depot);
            values.put("VRAC", inv2s.vrac);

            db.insert("INV2", null, values);
            db.setTransactionSuccessful();

        } catch (SQLiteDatabaseLockedException sqlLockEx) {
            Log.e("DB_LOCK", "Database is locked: " + sqlLockEx.getMessage());
        } catch (Exception ex) {
            Log.e("DB_ERROR", "Error inserting INV2: " + ex.getMessage());
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Exception ignored) {}
            }
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
    public ArrayList<PostData_Inv2> select_inventaire2_from_database(String query) {
        ArrayList<PostData_Inv2> all_inv2 = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {

                // Pré-calcul des index des colonnes
                final int idxRecordId     = cursor.getColumnIndex("RECORDID");
                final int idxCodeBarre    = cursor.getColumnIndex("CODE_BARRE");
                final int idxNumInv       = cursor.getColumnIndex("NUM_INV");
                final int idxProduit      = cursor.getColumnIndex("PRODUIT");
                final int idxNbrColis     = cursor.getColumnIndex("NBRE_COLIS");
                final int idxColissage    = cursor.getColumnIndex("COLISSAGE");
                final int idxPaHt         = cursor.getColumnIndex("PA_HT");
                final int idxQteTheo      = cursor.getColumnIndex("QTE");
                final int idxQtePhys      = cursor.getColumnIndex("QTE_NEW");
                final int idxTva          = cursor.getColumnIndex("TVA");
                final int idxVrac         = cursor.getColumnIndex("VRAC");
                final int idxCodeDepot    = cursor.getColumnIndex("CODE_DEPOT");

                do {
                    PostData_Inv2 inv2 = new PostData_Inv2();
                    inv2.recordid      = cursor.getInt(idxRecordId);
                    inv2.codebarre     = cursor.getString(idxCodeBarre);
                    inv2.num_inv       = cursor.getString(idxNumInv);
                    inv2.produit       = cursor.getString(idxProduit);
                    inv2.nbr_colis     = cursor.getDouble(idxNbrColis);
                    inv2.colissage     = cursor.getDouble(idxColissage);
                    inv2.pa_ht         = cursor.getDouble(idxPaHt);
                    inv2.qte_theorique = cursor.getDouble(idxQteTheo);
                    inv2.qte_physique  = cursor.getDouble(idxQtePhys);
                    inv2.tva           = cursor.getDouble(idxTva);
                    inv2.vrac          = cursor.getDouble(idxVrac);
                    inv2.code_depot    = cursor.getString(idxCodeDepot);

                    all_inv2.add(inv2);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }

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
        SQLiteDatabase db = null;
        boolean executed = false;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            // Capacité définie pour éviter les réallocations internes
            ContentValues values = new ContentValues(4);
            values.put("NBRE_COLIS", _inv2.nbr_colis);
            values.put("COLISSAGE", _inv2.colissage);
            values.put("QTE_NEW", _inv2.qte_physique + _inv2.vrac); // Calcul direct optimisé
            values.put("VRAC", _inv2.vrac);

            // Utilisation d'un WHERE ciblé et efficace
            String where = "RECORDID=? AND NUM_INV=?";
            String[] whereArgs = {
                    String.valueOf(_inv2.recordid),
                    _inv2.num_inv
            };

            // Appel unique pour limiter le coût I/O
            db.update("INV2", values, where, whereArgs);

            db.setTransactionSuccessful();
            executed = true;

        } catch (SQLiteDatabaseLockedException sqlLockEx) {
            Log.e("DB_LOCK", "Database locked during update: " + sqlLockEx.getMessage());
        } catch (Exception ex) {
            Log.e("DB_ERROR", "Error updating INV2: " + ex.getMessage());
        } finally {
            if (db != null) {
                try {
                    db.endTransaction();
                } catch (Exception ignored) {}
            }
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

                String checkQuery = "SELECT BLOCAGE FROM " + _table + " WHERE NUM_BON = '" + num_bon + "'";
                Cursor c = db.rawQuery(checkQuery, null);
                if (c.moveToFirst()) {
                    String currentBlocage = c.getString(0);
                    c.close();
                    if ("A".equals(currentBlocage)) {
                        db.endTransaction();
                        return false;
                    }
                } else {
                    c.close();
                }

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
                if (db.inTransaction()) {
                    db.endTransaction();
                }
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

        // Make sure every committed transaction is written into the main db file
        // before copying it: in WAL mode recent data lives in the -wal file and
        // would otherwise be missing from the backup.
        checkpointWal(mContext);

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
        return restoreDatabaseFile(mContext, new File(inFileName));
    }

    /**
     * Closes the shared SQLiteOpenHelper instance (if any) and resets the
     * "indexes applied" flag so that they are recreated on the next open.
     * Must be called before swapping the database file on disk.
     */
    public static void closeInstance() {
        synchronized (DATABASE.class) {
            if (sInstance != null) {
                try {
                    sInstance.close();
                } catch (Exception ignore) {
                }
                sInstance = null;
            }
            sIndexesApplied = false;
        }
    }

    /**
     * Forces a WAL checkpoint so that all committed data is merged into the main
     * database file. Must be called before copying or uploading the database
     * file, otherwise transactions still living in the -wal file would be
     * missing from the resulting backup.
     */
    public static void checkpointWal(Context context) {
        try {
            SQLiteDatabase db = getInstance(context).getWritableDatabase();
            db.rawQuery("PRAGMA wal_checkpoint(TRUNCATE)", null).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes the current database file together with its WAL/SHM/journal
     * companion files. Leaving the old -wal/-shm files behind is the reason a
     * restored database was ignored: SQLite replayed the stale WAL on top of
     * the newly copied file.
     */
    private static void deleteDatabaseFiles(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        File walFile = new File(dbFile.getPath() + "-wal");
        File shmFile = new File(dbFile.getPath() + "-shm");
        File journalFile = new File(dbFile.getPath() + "-journal");
        if (walFile.exists()) walFile.delete();
        if (shmFile.exists()) shmFile.delete();
        if (journalFile.exists()) journalFile.delete();
        if (dbFile.exists()) dbFile.delete();
    }

    /**
     * Replaces the current database with the given source file and reopens it so
     * that SQLiteOpenHelper runs onCreate/onUpgrade, migrating the old database
     * schema to the current {@link #DATABASE_VERSION}.
     *
     * This fixes the "restauration terminée avec succès mais aucune donnée" bug:
     * the previous implementation overwrote the database file while a connection
     * was still open and left the -wal/-shm files in place, so the app kept
     * serving the old (empty) cached data and the migration never ran.
     */
    public static boolean restoreDatabaseFile(Context context, File sourceFile) {
        if (sourceFile == null || !sourceFile.exists() || sourceFile.length() == 0) {
            Log.v("TRACKKK", "restoreDatabaseFile: source file missing or empty");
            return false;
        }

        File dbFile = context.getDatabasePath(DATABASE_NAME);
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            // 1. Close any open connection, then remove the current database and
            //    its WAL/SHM/journal files.
            closeInstance();
            deleteDatabaseFiles(context);

            // 2. Copy the backup file over the (now deleted) database file.
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(dbFile);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            fos = null;
            fis.close();
            fis = null;

            // 3. Reopen the database so the schema migration (onUpgrade) runs from
            //    the restored (old) version up to DATABASE_VERSION.
            getInstance(context).getWritableDatabase();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (Exception ignore) {
            }
            try {
                if (fis != null) fis.close();
            } catch (Exception ignore) {
            }
        }
    }

    private void insert_wilaya_commune_into_database(SQLiteDatabase db) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("sql_script/script_communes_wilaya.txt"), StandardCharsets.UTF_8))) {
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                db.execSQL(mLine);
            }
        } catch (IOException e) {
        }
        //log the exception
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

    public double getClientTotalVersementsPeriode(String dateDebut, String dateFin) {
        double somme = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT COALESCE(SUM(VERSEMENTS), 0.0) AS TOT FROM CARNET_C " +
                    "WHERE substr(DATE_CARNET, 7, 4) || '-' || substr(DATE_CARNET, 4, 2) || '-' || substr(DATE_CARNET, 1, 2) " +
                    "BETWEEN substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2) " +
                    "AND substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)";
            cursor = db.rawQuery(sql, new String[]{dateDebut, dateDebut, dateDebut, dateFin, dateFin, dateFin});
            if (cursor != null && cursor.moveToFirst()) {
                somme = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return somme;
    }

    public double getClientTotalSoldesPeriode(String dateDebut, String dateFin) {
        double somme = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String sql = "SELECT COALESCE(SUM(MONTANT_ACHAT), 0.0) AS TOT FROM BON1 " +
                    "WHERE substr(DATE_BON, 7, 4) || '-' || substr(DATE_BON, 4, 2) || '-' || substr(DATE_BON, 1, 2) " +
                    "BETWEEN substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2) " +
                    "AND substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)";
            cursor = db.rawQuery(sql, new String[]{dateDebut, dateDebut, dateDebut, dateFin, dateFin, dateFin});
            if (cursor != null && cursor.moveToFirst()) {
                somme = cursor.getDouble(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return somme;
    }

    public static class ClientVersementResult {
        public PostData_Client client;
        public double sommeVersements;
        public int nbOperations;
        public double sommeAchats;
        public ClientVersementResult() {
            client = new PostData_Client();
            sommeVersements = 0.0;
            nbOperations = 0;
            sommeAchats = 0.0;
        }
    }

    public ArrayList<ClientVersementResult> getClientsWithVersementPeriode(String dateDebut, String dateFin) {
        ArrayList<ClientVersementResult> results = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursorCarnets = null;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE);
        java.util.Date dtDebut = null;
        java.util.Date dtFin = null;
        try {
            try { dtDebut = sdf.parse(dateDebut); } catch (Exception ignore) {}
            try {
                dtFin = sdf.parse(dateFin);
                if (dtFin != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance(java.util.Locale.FRANCE);
                    cal.setTime(dtFin);
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    cal.add(java.util.Calendar.MILLISECOND, -1);
                    dtFin = cal.getTime();
                }
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}

        try {
            db = this.getReadableDatabase();

            // ============================================================
            // NIVEAU 1 : CARNET_C avec VERSEMENTS > 0, filtré par période
            // ============================================================
            java.util.HashMap<String, ClientVersementResult> mapPeriode = new java.util.HashMap<>();
            try {
                cursorCarnets = db.rawQuery(
                        "SELECT CODE_CLIENT, DATE_CARNET, VERSEMENTS FROM CARNET_C WHERE COALESCE(VERSEMENTS, 0.0) > 0 AND COALESCE(IS_EXPORTED, 0) = 0",
                        null);
                if (cursorCarnets != null && cursorCarnets.moveToFirst()) {
                    do {
                        String code = null;
                        String dateC = null;
                        double vers = 0.0;
                        try { code = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("CODE_CLIENT")); } catch (Exception ignore) {}
                        try { dateC = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("DATE_CARNET")); } catch (Exception ignore) {}
                        try { vers = cursorCarnets.getDouble(cursorCarnets.getColumnIndexOrThrow("VERSEMENTS")); } catch (Exception ignore) {}
                        if (code == null || vers <= 0.0) continue;

                        boolean inPeriod = false;
                        if (dtDebut == null || dtFin == null) {
                            inPeriod = true;
                        } else {
                            try {
                                java.util.Date d = sdf.parse(dateC);
                                if (d != null && d.compareTo(dtDebut) >= 0 && d.compareTo(dtFin) <= 0) {
                                    inPeriod = true;
                                }
                            } catch (Exception ignore) {}
                        }
                        if (!inPeriod) continue;

                        String key = code.trim();
                        ClientVersementResult r = mapPeriode.get(key);
                        if (r == null) {
                            r = new ClientVersementResult();
                            mapPeriode.put(key, r);
                        }
                        r.sommeVersements += vers;
                        r.nbOperations += 1;
                    } while (cursorCarnets.moveToNext());
                }
            } finally {
                if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
                cursorCarnets = null;
            }

            // Transformer la map en liste + compléter infos client
            if (!mapPeriode.isEmpty()) {
                for (java.util.Map.Entry<String, ClientVersementResult> e : mapPeriode.entrySet()) {
                    String code = e.getKey();
                    ClientVersementResult r = e.getValue();
                    PostData_Client cli = chargerClientParCode(db, code);
                    if (cli == null) continue;
                    if (r.sommeVersements <= 0.0 && cli.verser_client > 0.0) {
                        r.sommeVersements = cli.verser_client;
                        if (r.nbOperations <= 0) r.nbOperations = 1;
                    }
                    r.client = cli;
                    if (r.sommeVersements > 0.0) results.add(r);
                }
            }

            // ============================================================
            // NIVEAU 2 : CARNET_C tout court (sans filtre de période)
            // ============================================================
            if (results.isEmpty()) {
                try {
                    cursorCarnets = db.rawQuery(
                            "SELECT CODE_CLIENT AS code_client, " +
                                    "COALESCE(SUM(VERSEMENTS), 0.0) AS som_vers, " +
                                    "COUNT(*) AS nb_ops " +
                                    "FROM CARNET_C WHERE COALESCE(VERSEMENTS, 0.0) > 0 AND COALESCE(IS_EXPORTED, 0) = 0 " +
                                    "GROUP BY CODE_CLIENT ORDER BY som_vers DESC", null);
                    if (cursorCarnets != null && cursorCarnets.moveToFirst()) {
                        do {
                            String code = null;
                            double sv = 0.0;
                            int nb = 0;
                            try { code = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("code_client")); } catch (Exception ignore) {}
                            try { sv = cursorCarnets.getDouble(cursorCarnets.getColumnIndexOrThrow("som_vers")); } catch (Exception ignore) {}
                            try { nb = cursorCarnets.getInt(cursorCarnets.getColumnIndexOrThrow("nb_ops")); } catch (Exception ignore) {}
                            if (code == null || code.trim().isEmpty() || sv <= 0.0) continue;
                            PostData_Client cli = chargerClientParCode(db, code.trim());
                            if (cli == null) continue;
                            if (sv <= 0.0 && cli.verser_client > 0.0) sv = cli.verser_client;
                            if (nb <= 0 && sv > 0.0) nb = 1;
                            ClientVersementResult r = new ClientVersementResult();
                            r.client = cli;
                            r.sommeVersements = sv;
                            r.nbOperations = nb;
                            if (r.sommeVersements > 0.0) results.add(r);
                        } while (cursorCarnets.moveToNext());
                    }
                } finally {
                    if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
                    cursorCarnets = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
        }

        java.util.Collections.sort(results, new java.util.Comparator<ClientVersementResult>() {
            @Override
            public int compare(ClientVersementResult a, ClientVersementResult b) {
                return Double.compare(b.sommeVersements, a.sommeVersements);
            }
        });
        return results;
    }

    private PostData_Client chargerClientParCode(SQLiteDatabase db, String code) {
        if (db == null || code == null) return null;
        PostData_Client client = null;
        Cursor cC = null;
        try {
            cC = db.rawQuery("SELECT * FROM CLIENT WHERE TRIM(CODE_CLIENT) = ? LIMIT 1", new String[]{code.trim()});
            if (cC != null && cC.moveToFirst()) {
                PostData_Client cli = new PostData_Client();
                try { cli.code_client = cC.getString(cC.getColumnIndex("CODE_CLIENT")); } catch (Exception ignore) {}
                try { cli.client = cC.getString(cC.getColumnIndex("CLIENT")); } catch (Exception ignore) {}
                try { cli.tel = cC.getString(cC.getColumnIndex("TEL")); } catch (Exception ignore) {}
                try { cli.wilaya = cC.getString(cC.getColumnIndex("WILAYA")); } catch (Exception ignore) {}
                try { cli.commune = cC.getString(cC.getColumnIndex("COMMUNE")); } catch (Exception ignore) {}
                try { cli.adresse = cC.getString(cC.getColumnIndex("ADRESSE")); } catch (Exception ignore) {}
                try { cli.mode_tarif = cC.getString(cC.getColumnIndex("MODE_TARIF")); } catch (Exception ignore) {}
                try { cli.latitude = cC.getDouble(cC.getColumnIndex("LATITUDE")); } catch (Exception ignore) {}
                try { cli.longitude = cC.getDouble(cC.getColumnIndex("LONGITUDE")); } catch (Exception ignore) {}
                try { cli.achat_client = cC.getDouble(cC.getColumnIndex("ACHATS")); } catch (Exception ignore) {}
                try { cli.verser_client = cC.getDouble(cC.getColumnIndex("VERSER")); } catch (Exception ignore) {}
                try { cli.solde_client = cC.getDouble(cC.getColumnIndex("SOLDE")); } catch (Exception ignore) {}
                try { cli.solde_ini = cC.getDouble(cC.getColumnIndex("SOLDE_INI")); } catch (Exception ignore) {}
                try { cli.credit_limit = cC.getDouble(cC.getColumnIndex("CREDIT_LIMIT")); } catch (Exception ignore) {}
                try { cli.isNew = cC.getInt(cC.getColumnIndex("ISNEW")); } catch (Exception ignore) {}
                try { cli.isImported = cC.getInt(cC.getColumnIndex("IS_IMPORTED")); } catch (Exception ignore) {}
                try { cli.rc = cC.getString(cC.getColumnIndex("RC")); } catch (Exception ignore) {}
                try { cli.ifiscal = cC.getString(cC.getColumnIndex("IFISCAL")); } catch (Exception ignore) {}
                try { cli.ai = cC.getString(cC.getColumnIndex("AI")); } catch (Exception ignore) {}
                try { cli.nis = cC.getString(cC.getColumnIndex("NIS")); } catch (Exception ignore) {}
                client = cli;
                if (client.client == null || client.client.trim().isEmpty()) {
                    client.client = client.code_client != null ? client.code_client : "Client";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cC != null) try { cC.close(); } catch (Exception ignore) {}
        }
        return client;
    }

    public static class FournisseurVersementResult {
        public PostData_Fournisseur fournisseur;
        public double sommeVersements;
        public int nbOperations;
        public double sommeAchats;
        public FournisseurVersementResult() {
            fournisseur = new PostData_Fournisseur();
            sommeVersements = 0.0;
            nbOperations = 0;
            sommeAchats = 0.0;
        }
    }
    

    public ArrayList<FournisseurVersementResult> getFournisseursWithVersementPeriode(String dateDebut, String dateFin) {
        ArrayList<FournisseurVersementResult> results = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursorCarnets = null;
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRANCE);
        java.util.Date dtDebut = null;
        java.util.Date dtFin = null;
        try {
            try { dtDebut = sdf.parse(dateDebut); } catch (Exception ignore) {}
            try {
                dtFin = sdf.parse(dateFin);
                if (dtFin != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance(java.util.Locale.FRANCE);
                    cal.setTime(dtFin);
                    cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    cal.add(java.util.Calendar.MILLISECOND, -1);
                    dtFin = cal.getTime();
                }
            } catch (Exception ignore) {}
        } catch (Exception ignore) {}

        try {
            db = this.getReadableDatabase();

            // ============================================================
            // NIVEAU 1 : CARNET_F avec VERSEMENTS > 0, filtré par période
            // ============================================================
            java.util.HashMap<String, FournisseurVersementResult> mapPeriode = new java.util.HashMap<>();
            try {
                cursorCarnets = db.rawQuery(
                        "SELECT CODE_FRS, DATE_CARNET, VERSEMENTS FROM CARNET_F WHERE COALESCE(VERSEMENTS, 0.0) > 0 AND COALESCE(IS_EXPORTED, 0) = 0",
                        null);
                if (cursorCarnets != null && cursorCarnets.moveToFirst()) {
                    do {
                        String code = null;
                        String dateC = null;
                        double vers = 0.0;
                        try { code = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("CODE_FRS")); } catch (Exception ignore) {}
                        try { dateC = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("DATE_CARNET")); } catch (Exception ignore) {}
                        try { vers = cursorCarnets.getDouble(cursorCarnets.getColumnIndexOrThrow("VERSEMENTS")); } catch (Exception ignore) {}
                        if (code == null || vers <= 0.0) continue;

                        boolean inPeriod = false;
                        if (dtDebut == null || dtFin == null) {
                            inPeriod = true;
                        } else {
                            try {
                                java.util.Date d = sdf.parse(dateC);
                                if (d != null && d.compareTo(dtDebut) >= 0 && d.compareTo(dtFin) <= 0) {
                                    inPeriod = true;
                                }
                            } catch (Exception ignore) {}
                        }
                        if (!inPeriod) continue;

                        String key = code.trim();
                        FournisseurVersementResult r = mapPeriode.get(key);
                        if (r == null) {
                            r = new FournisseurVersementResult();
                            mapPeriode.put(key, r);
                        }
                        r.sommeVersements += vers;
                        r.nbOperations += 1;
                    } while (cursorCarnets.moveToNext());
                }
            } finally {
                if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
                cursorCarnets = null;
            }

            // Transformer la map en liste + compléter infos fournisseur
            if (!mapPeriode.isEmpty()) {
                for (java.util.Map.Entry<String, FournisseurVersementResult> e : mapPeriode.entrySet()) {
                    String code = e.getKey();
                    FournisseurVersementResult r = e.getValue();
                    PostData_Fournisseur fourn = chargerFournisseurParCode(db, code);
                    if (fourn == null) continue;
                    if (r.sommeVersements <= 0.0 && fourn.verser_montant > 0.0) {
                        r.sommeVersements = fourn.verser_montant;
                        if (r.nbOperations <= 0) r.nbOperations = 1;
                    }
                    r.fournisseur = fourn;
                    if (r.sommeVersements > 0.0) results.add(r);
                }
            }

            // ============================================================
            // NIVEAU 2 : CARNET_F tout court (sans filtre de période)
            // ============================================================
            if (results.isEmpty()) {
                try {
                    cursorCarnets = db.rawQuery(
                            "SELECT CODE_FRS AS code_frs, " +
                                    "COALESCE(SUM(VERSEMENTS), 0.0) AS som_vers, " +
                                    "COUNT(*) AS nb_ops " +
                                    "FROM CARNET_F WHERE COALESCE(VERSEMENTS, 0.0) > 0 AND COALESCE(IS_EXPORTED, 0) = 0 " +
                                    "GROUP BY CODE_FRS ORDER BY som_vers DESC", null);
                    if (cursorCarnets != null && cursorCarnets.moveToFirst()) {
                        do {
                            String code = null;
                            double sv = 0.0;
                            int nb = 0;
                            try { code = cursorCarnets.getString(cursorCarnets.getColumnIndexOrThrow("code_frs")); } catch (Exception ignore) {}
                            try { sv = cursorCarnets.getDouble(cursorCarnets.getColumnIndexOrThrow("som_vers")); } catch (Exception ignore) {}
                            try { nb = cursorCarnets.getInt(cursorCarnets.getColumnIndexOrThrow("nb_ops")); } catch (Exception ignore) {}
                            if (code == null || code.trim().isEmpty() || sv <= 0.0) continue;
                            PostData_Fournisseur fourn = chargerFournisseurParCode(db, code.trim());
                            if (fourn == null) continue;
                            if (sv <= 0.0 && fourn.verser_montant > 0.0) sv = fourn.verser_montant;
                            if (nb <= 0 && sv > 0.0) nb = 1;
                            FournisseurVersementResult r = new FournisseurVersementResult();
                            r.fournisseur = fourn;
                            r.sommeVersements = sv;
                            r.nbOperations = nb;
                            if (r.sommeVersements > 0.0) results.add(r);
                        } while (cursorCarnets.moveToNext());
                    }
                } finally {
                    if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
                    cursorCarnets = null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursorCarnets != null) try { cursorCarnets.close(); } catch (Exception ignore) {}
        }

        java.util.Collections.sort(results, new java.util.Comparator<FournisseurVersementResult>() {
            @Override
            public int compare(FournisseurVersementResult a, FournisseurVersementResult b) {
                return Double.compare(b.sommeVersements, a.sommeVersements);
            }
        });
        return results;
    }

    private PostData_Fournisseur chargerFournisseurParCode(SQLiteDatabase db, String code) {
        if (db == null || code == null) return null;
        PostData_Fournisseur fourn = null;
        Cursor cF = null;
        try {
            cF = db.rawQuery("SELECT * FROM FOURNIS WHERE TRIM(CODE_FRS) = ? LIMIT 1", new String[]{code.trim()});
            if (cF != null && cF.moveToFirst()) {
                PostData_Fournisseur f = new PostData_Fournisseur();
                try { f.fournis_id = cF.getInt(cF.getColumnIndex("FOURNIS_ID")); } catch (Exception ignore) {}
                try { f.code_frs = cF.getString(cF.getColumnIndex("CODE_FRS")); } catch (Exception ignore) {}
                try { f.fournis = cF.getString(cF.getColumnIndex("FOURNIS")); } catch (Exception ignore) {}
                try { f.adresse = cF.getString(cF.getColumnIndex("ADRESSE")); } catch (Exception ignore) {}
                try { f.tel = cF.getString(cF.getColumnIndex("TEL")); } catch (Exception ignore) {}
                try { f.rc = cF.getString(cF.getColumnIndex("RC")); } catch (Exception ignore) {}
                try { f.ifiscal = cF.getString(cF.getColumnIndex("IFISCAL")); } catch (Exception ignore) {}
                try { f.ai = cF.getString(cF.getColumnIndex("AI")); } catch (Exception ignore) {}
                try { f.nis = cF.getString(cF.getColumnIndex("NIS")); } catch (Exception ignore) {}
                try { f.achat_montant = cF.getDouble(cF.getColumnIndex("ACHATS")); } catch (Exception ignore) {}
                try { f.verser_montant = cF.getDouble(cF.getColumnIndex("VERSER")); } catch (Exception ignore) {}
                try { f.solde_montant = cF.getDouble(cF.getColumnIndex("SOLDE")); } catch (Exception ignore) {}
                try { f.latitude = cF.getDouble(cF.getColumnIndex("LATITUDE")); } catch (Exception ignore) {}
                try { f.longitude = cF.getDouble(cF.getColumnIndex("LONGITUDE")); } catch (Exception ignore) {}
                try { f.isNew = cF.getInt(cF.getColumnIndex("ISNEW")); } catch (Exception ignore) {}
                try { f.isImported = cF.getInt(cF.getColumnIndex("IS_IMPORTED")); } catch (Exception ignore) {}
                fourn = f;
                if (fourn.fournis == null || fourn.fournis.trim().isEmpty()) {
                    fourn.fournis = fourn.code_frs != null ? fourn.code_frs : "Fournisseur";
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cF != null) try { cF.close(); } catch (Exception ignore) {}
        }
        return fourn;
    }

    public double getFournisseurTotalVersementsPeriode(String dateDebut, String dateFin) {
        double somme = 0.0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            try {
                String sql = "SELECT COALESCE(SUM(VERSEMENTS), 0.0) AS TOT FROM CARNET_F " +
                        "WHERE COALESCE(VERSEMENTS, 0.0) > 0 " +
                        "AND substr(DATE_CARNET, 7, 4) || '-' || substr(DATE_CARNET, 4, 2) || '-' || substr(DATE_CARNET, 1, 2) " +
                        "BETWEEN substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2) " +
                        "AND substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)";
                cursor = db.rawQuery(sql, new String[]{dateDebut, dateDebut, dateDebut, dateFin, dateFin, dateFin});
                if (cursor != null && cursor.moveToFirst()) {
                    somme = cursor.getDouble(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) { try { cursor.close(); } catch (Exception ignore) {} cursor = null; }
            }
            if (somme <= 0.0) {
                try {
                    cursor = db.rawQuery("SELECT COALESCE(SUM(VERSER), 0.0) AS TOT FROM FOURNIS WHERE COALESCE(VERSER, 0.0) > 0", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        somme = cursor.getDouble(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) try { cursor.close(); } catch (Exception ignore) {}
        }
        return somme;
    }

    public double getFournisseurTotalSoldesPeriode(String dateDebut, String dateFin) {
        double somme = 0.0;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            try {
                String sql = "SELECT COALESCE(SUM(TOT_TTC - coalesce(REMISE,0.0)), 0.0) AS TOT FROM ACHAT1 " +
                        "WHERE COALESCE(TOT_TTC, 0.0) > 0 " +
                        "AND substr(DATE_BON, 7, 4) || '-' || substr(DATE_BON, 4, 2) || '-' || substr(DATE_BON, 1, 2) " +
                        "BETWEEN substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2) " +
                        "AND substr(?, 7, 4) || '-' || substr(?, 4, 2) || '-' || substr(?, 1, 2)";
                cursor = db.rawQuery(sql, new String[]{dateDebut, dateDebut, dateDebut, dateFin, dateFin, dateFin});
                if (cursor != null && cursor.moveToFirst()) {
                    somme = cursor.getDouble(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) { try { cursor.close(); } catch (Exception ignore) {} cursor = null; }
            }
            if (somme <= 0.0) {
                try {
                    cursor = db.rawQuery("SELECT COALESCE(SUM(SOLDE), 0.0) AS TOT FROM FOURNIS", null);
                    if (cursor != null && cursor.moveToFirst()) {
                        somme = cursor.getDouble(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) try { cursor.close(); } catch (Exception ignore) {}
        }
        return somme;
    }

}

package com.safesoft.proapp.distribute.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by UK2015 on 21/08/2016.
 */
public class ServiceDistribute extends Service {

  private final IBinder mBinder = new MyBinder();
  private String Server;
  private String Username;
  private String Password;
  private String MY_PREFS_NAME = "ConfigNetwork";
  private String Path;
  private DATABASE controller;
  private String _params;
  private Connection con = null;
  private Context _context;

  @Override
  public void onCreate() {
    super.onCreate();

    SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
    Server = prefs.getString("ip", "192.168.1.6");
    Path = prefs.getString("path", "D:/P-VENTE/DATA/PME PRO/DISTRIBUTE");
    Username = prefs.getString("username", "SYSDBA");
    Password = prefs.getString("password", "masterkey");
    // Path = "C:/P-VENTE/DATA/RESTO PRO/"+ Database +".FDB";
    controller = new DATABASE(this);

  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    return Service.START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return mBinder;
  }


  public void setContext(Context context) {
    _context = context;
  }



  ///////////////////////// IMPORT PRODUIT /////////////////////////////////////////////////
  public Integer lunch_load_produits(Boolean delete){
    int flag = 0;
    try {
      Import_bonTransfer_from_server_task import_bon_transfer = new Import_bonTransfer_from_server_task();
      flag =  import_bon_transfer.execute().get();
    }catch (Exception e){

    }
    return flag;
  }


  //////////////////////////////////// EXPORT INVENTAIRE /////////////////////////////////////////
  public Integer lunch_export_inventaire(Boolean all, String num_inv){
    int flag = 0;
    try {
      //     Export_inventaire_to_server_task export_inventaire = new Export_inventaire_to_server_task(all,num_inv);
      //   flag =  export_inventaire.execute().get();
      Thread.sleep(3000);
    }catch (Exception e){

    }
    return flag;
  }

  public class MyBinder extends Binder {
    public ServiceDistribute getService() {
      return ServiceDistribute.this;
    }
  }

  //////////////////////////////////// IMPORT PRODUIT ////////////////////////////////////////////
  //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
  public class Import_bonTransfer_from_server_task extends AsyncTask<Void, Void, Integer> {

    Integer flag = 0;
    public Import_bonTransfer_from_server_task(){

    }
    @Override
    protected Integer doInBackground(Void... params) {
      try {

        ArrayList<PostData_Produit> produits = new ArrayList<>();
        ArrayList<PostData_Transfer1> transfer1s = new ArrayList<>();
        ArrayList<PostData_Transfer2> transfer2s = new ArrayList<>();
        ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

        Intent intent;

        System.setProperty("FBAdbLog", "true");
        DriverManager.setLoginTimeout(5);
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
        con = DriverManager.getConnection(sCon, Username, Password);

        Statement stmt = con.createStatement();

        //============================ GET Trasfer1 ===========================================
        String sql1 = "SELECT  TRANSFERT1.NUM_BON, TRANSFERT1.DATE_BON, TRANSFERT1.CODE_DEPOT_SOURCE, TRANSFERT1.NOM_DEPOT_SOURCE, TRANSFERT1.CODE_DEPOT_DEST, TRANSFERT1.NOM_DEPOT_DEST, TRANSFERT1.NBR_P FROM TRANSFERT1";
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
        }

        //============================ GET Trasfer2 ===========================================
        String sql2 = "SELECT  TRANSFERT2.NUM_BON, TRANSFERT2.CODE_BARRE, TRANSFERT1.PRODUIT, TRANSFERT1.QTE FROM TRANSFERT2";
        ResultSet rs2 = stmt.executeQuery(sql2);
        PostData_Transfer2 transfer2;

        while (rs2.next()) {
          transfer2 = new PostData_Transfer2();
          transfer2.num_bon = rs1.getString("NUM_BON");
          transfer2.code_barre = rs1.getString("CODE_BARRE");
          transfer2.produit = rs1.getString("PRODUIT");
          transfer2.qte =  rs1.getDouble("QTE");
          transfer2s.add(transfer2);
        }
        controller.ExecuteTransactionTrasfer(transfer1s, transfer2s);

                /*
                //============================ GET DEPOT1 ===========================================
                String sql2 = "SELECT  DEPOT1.CODE_DEPOT, DEPOT1.NOM_DEPOT FROM DEPOT1";
                ResultSet rs2 = stmt.executeQuery(sql2);
                PostData_Depot1 depot1;
                while (rs2.next()) {
                    depot1 = new PostData_Depot1();
                    depot1.code_depot = rs2.getString("CODE_DEPOT");
                    depot1.nom_depot = rs2.getString("NOM_DEPOT");
                    depot1s.add(depot1);
                }

                //============================ GET DEPOT2 ==========================================
                String sql3 = "SELECT  DEPOT2.CODE_DEPOT, DEPOT2.CODE_BARRE, DEPOT2.STOCK FROM DEPOT2";
                ResultSet rs3 = stmt.executeQuery(sql3);
                PostData_Depot2 depot2;
                while (rs3.next()) {
                    depot2 = new PostData_Depot2();
                    depot2.code_depot = rs3.getString("CODE_DEPOT");
                    depot2.codebarre = rs3.getString("CODE_BARRE");
                    depot2.stock_old = rs3.getString("STOCK");
                    depot2s.add(depot2);
                }

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


                //============================ GET Fournisseurs ===================================
                String sql5 = "SELECT FOURNIS.CODE_FRS, FOURNIS.FOURNIS FROM FOURNIS";
                ResultSet rs5 = stmt.executeQuery(sql5);
                PostData_Fournisseur fournis;
                while (rs5.next()) {
                    fournis = new PostData_Fournisseur();
                    fournis.code_frs = rs5.getString("CODE_FRS");
                    fournis.fournis = rs5.getString("FOURNIS");
                    fournissuer.add(fournis);
                }

                stmt.close();
                Boolean executed = controller.ExecuteTransactionUpdate(produits, depot2s, depot1s, codebarres, fournissuer);
                if (executed) {
                    flag = 1;
                    Log.v("TRACKKK", "==============================================");
                    intent = new Intent("PRODUIT_DEPOT1_DEPOT2_UPDATED");
                    intent.putExtra("param", "LIST PRODUITS, LIST DEPOT2 AND LIST DEPOT1 HAVE UPDATED FROM DATABASE SERVER");
                    sendBroadcast(intent);
                }else{
                    flag = 3;
                }

                */
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
  }
  //===========================================================================================

  /*
      //class Insert Data into FireBird Database
      //====================================
      public class Export_inventaire_to_server_task extends AsyncTask<Void, Void, Integer> {

          ArrayList<PostData_Fournisseur> achats1 = new ArrayList<>();
          ArrayList<PostData_Achat2> achats2 = new ArrayList<>();
          Integer recordid_achat1;
          //Boolean executed = false;
          Integer flag = 0;
          Boolean _all = false;
          String _num_bon = null;
          public Export_inventaire_to_server_task(Boolean all, String num_bon){
              _all = all;
              _num_bon = num_bon;
          }


          @Override
          protected Integer doInBackground(Void... params) {
              // TODO: attempt authentication against a network service.
              try {

                  System.setProperty("FBAdbLog", "true");
                  DriverManager.setLoginTimeout(5);
                  Class.forName("org.firebirdsql.jdbc.FBDriver");
                  String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=ISO8859_1";
                  con = DriverManager.getConnection(sCon, Username, Password);

                  Statement stmt = con.createStatement();
                  con.setAutoCommit(false);
                  SharedPreferences prefs = getSharedPreferences(PARAMS_PREFS_NAME, MODE_PRIVATE);
                  _params = prefs.getString("GESTIO_DEPOT", "0");



                /*  if(invs1.get(0).is_sent == 1){
                      flag = 4;
                  }else{
                  if(_all){
                      String querry = "SELECT * FROM Achat1";
                      achats1.clear();
                      achats1 = controller.select_list_achat_from_database(querry);
                      if(_params.equals("1")){
                          for (int i = 0; i < achats1.size(); i++) {

                              String querry_select = "SELECT * FROM Achat2 WHERE NUM_BON = '" + achats1.get(i).num_bon + "'";
                              achats2 = controller.select_data_achat2_from_database(querry_select);
                              String[] buffer = new String[achats2.size()];

                              String ggg = "SELECT gen_id(GEN_BON_A1_ID,1) as RECORDID FROM rdb$database";
                              ResultSet rs1 = stmt.executeQuery(ggg);
                              while (rs1.next()) {
                                  recordid_achat1 = rs1.getInt("RECORDID");
                              }
                              String NUM_INV = Get_Digits_String(String.valueOf(recordid_achat1), 6);
                              String vvv = "INSERT INTO BON_A1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_FRS, UTILISATEUR, CODE_DEPOT) VALUES ('"
                                      + recordid_achat1 + "' , '" + NUM_INV+ "' , '" + achats1.get(i).date_bon + "' , '" + achats1.get(i).heure + "', '" + achats1.get(i).code_frs +
                                      "' , 'INCONNU' , '" + achats1.get(i).code_depot + "')";
                              stmt.executeUpdate(vvv);

                              for (int j = 0; j < achats2.size(); j++) {
                                  buffer[j] = "INSERT INTO BON_A2 (CODE_BARRE, NUM_BON, PA_HT, QTE, TVA, CODE_DEPOT" +
                                          ") VALUES ('" + achats2.get(j).codebarre + "' , '"+ NUM_INV +"' , '" +
                                          achats2.get(j).pa_ht + "' , '" +
                                          achats2.get(j).quantite + "' , '" + achats2.get(j).tva + "', '" + achats1.get(i).code_depot + "')";
                                  stmt.addBatch(buffer[j]);
                              }

                              stmt.executeBatch();
                              stmt.clearBatch();
                              controller.Update_inventaire1(achats1.get(i).num_bon);
                              achats2.clear();
                          }

                      }else{
                          for (int i = 0; i < achats1.size(); i++) {

                              String querry_select = "SELECT * FROM Achat2 WHERE NUM_BON = '" + achats1.get(i).num_bon + "'";
                              achats2 = controller.select_data_achat2_from_database(querry_select);
                              String[] buffer = new String[achats2.size()];

                              String ggg = "SELECT gen_id(GEN_BON_A1_ID,1) as RECORDID FROM rdb$database";
                              ResultSet rs1 = stmt.executeQuery(ggg);
                              while (rs1.next()) {
                                  recordid_achat1 = rs1.getInt("RECORDID");
                              }
                              String NUM_BON = Get_Digits_String(String.valueOf(recordid_achat1), 6);
                              String vvv = "INSERT INTO BON_A1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_FRS, UTILISATEUR) VALUES ('"
                                      + recordid_achat1 + "' , '" + NUM_BON + "' , '" + achats1.get(i).date_bon + "', '" + achats1.get(i).heure + "' , '" + achats1.get(i).code_frs + "' , 'INCONNU')";
                              stmt.executeUpdate(vvv);

                              for (int j = 0; j < achats2.size(); j++) {
                                  buffer[j] = "INSERT INTO BON_A2 (CODE_BARRE, NUM_BON, PA_HT, TVA, QTE" + ") VALUES " +
                                          "('" + achats2.get(j).codebarre + "' , '"+ NUM_BON  +"' , '" + achats2.get(j).pa_ht + "', '" + achats2.get(j).tva + "' , '" + achats2.get(j).quantite + "')";
                                  stmt.addBatch(buffer[j]);
                              }

                              stmt.executeBatch();
                              stmt.clearBatch();
                              controller.Update_inventaire1(achats1.get(i).num_bon);
                              achats2.clear();
                          }
                      }


                      con.commit();
                      con.setAutoCommit(true);
                      achats1.clear();
                      flag = 1;
                      // }
                  }else{ /// else case export once invontory.
                      String querry = "SELECT * FROM Achat1 WHERE NUM_BON = '"+ _num_bon +"'";
                      achats1.clear();
                      achats1 = controller.select_list_achat_from_database(querry);
                      if(_params.equals("1")){

                              String querry_select = "SELECT * FROM Achat2 WHERE NUM_BON = '" + _num_bon + "'";
                              achats2 = controller.select_data_achat2_from_database(querry_select);
                              String[] buffer = new String[achats2.size()];

                              String ggg = "SELECT gen_id(GEN_BON_A1_ID,1) as RECORDID FROM rdb$database";
                              ResultSet rs1 = stmt.executeQuery(ggg);
                              while (rs1.next()) {
                                  recordid_achat1 = rs1.getInt("RECORDID");
                              }
                              String NUM_BON = Get_Digits_String(String.valueOf(recordid_achat1), 6);
                              String vvv = "INSERT INTO BON_A1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_FRS, UTILISATEUR, CODE_DEPOT) VALUES ('"
                                      + recordid_achat1 + "' , '" + NUM_BON + "' , '" + achats1.get(0).date_bon + "' , '" + achats1.get(0).heure + "', '" + achats1.get(0).code_frs +
                                      "' , 'INCONNU' , '" + achats1.get(0).code_depot + "')";
                              stmt.executeUpdate(vvv);

                              for (int j = 0; j < achats2.size(); j++) {
                                  buffer[j] = "INSERT INTO BON_A2 (CODE_BARRE, NUM_BON, PA_HT, QTE, TVA, CODE_DEPOT" +
                                          ") VALUES ('" + achats2.get(j).codebarre + "' , '"+ NUM_BON +"' , '" +
                                          achats2.get(j).pa_ht + "' , '" +
                                          achats2.get(j).quantite + "', '" + achats2.get(j).tva + "' , '" + achats1.get(0).code_depot + "')";
                                  stmt.addBatch(buffer[j]);
                              }

                              stmt.executeBatch();
                              stmt.clearBatch();
                              controller.Update_inventaire1(_num_bon);
                              achats2.clear();

                      }else{
                              String querry_select = "SELECT * FROM Achat2 WHERE NUM_BON = '" + _num_bon + "'";
                              achats2 = controller.select_data_achat2_from_database(querry_select);
                              String[] buffer = new String[achats2.size()];

                              String ggg = "SELECT gen_id(GEN_BON_A1_ID,1) as RECORDID FROM rdb$database";
                              ResultSet rs1 = stmt.executeQuery(ggg);
                              while (rs1.next()) {
                                  recordid_achat1 = rs1.getInt("RECORDID");
                              }
                              String NUM_BON = Get_Digits_String(String.valueOf(recordid_achat1), 6);
                              String vvv = "INSERT INTO BON_A1 (RECORDID, NUM_BON, DATE_BON, HEURE, CODE_FRS, UTILISATEUR) VALUES ('"
                                      + recordid_achat1 + "' , '" + NUM_BON+ "' , '" + achats1.get(0).date_bon + "' , '" + achats1.get(0).heure + "', '" + achats1.get(0).code_frs +
                                      "' , 'INCONNU')";
                              stmt.executeUpdate(vvv);

                              for (int j = 0; j < achats2.size(); j++) {
                                  buffer[j] = "INSERT INTO BON_A2 (CODE_BARRE, NUM_BON, PA_HT, QTE,TVA" +
                                          ") VALUES ('" + achats2.get(j).codebarre + "', '"+ NUM_BON +"' , '" +
                                          achats2.get(j).pa_ht + "' , '" +
                                          achats2.get(j).quantite + "','" + achats2.get(j).tva + "')";
                              stmt.addBatch(buffer[j]);
                              }

                              stmt.executeBatch();
                              stmt.clearBatch();
                              controller.Update_inventaire1(_num_bon);
                              achats2.clear();
                      }


                      con.commit();
                      con.setAutoCommit(true);
                      achats1.clear();
                      flag = 1;
                      // }
                  }


              } catch (Exception ex) {
                  con = null;
                  Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
                  if (ex.getMessage().contains("Unable to complete network request to host")) {
                      flag = 2;
                      Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
                  } else {
                      //not executed with problem in the sql statement
                      flag = 3;
                  }
              }
              return flag;
          }

      }
      */
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
}

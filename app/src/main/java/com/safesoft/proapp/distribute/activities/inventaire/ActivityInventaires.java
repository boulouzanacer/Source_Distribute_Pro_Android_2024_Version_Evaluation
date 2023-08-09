package com.safesoft.proapp.distribute.activities.inventaire;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterInv1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityInventaires extends AppCompatActivity implements RecyclerAdapterInv1.ItemClick , RecyclerAdapterInv1.ItemLongClick{

  RecyclerView recyclerView;
  RecyclerAdapterInv1 adapter;
  ArrayList<PostData_Inv1> inv1s;
  DATABASE controller;

  private MediaPlayer mp;

  private String Server;
  private String Username,Password;
  private String Path;

  private PostData_Bon1 bon1_print;
  private ArrayList<PostData_Bon2> bon2_print;

  private final String PREFS = "ALL_PREFS";
  private String CODE_DEPOT;
  private String SOURCE_EXPORT = "";

  private NumberFormat nf;
  private String code_depot;

  private ProgressDialog mProgressDialog_Free;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inventaires);

    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Liste inventaires");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    controller = new DATABASE(this);

    if(getIntent() != null){
      SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
    }

    initViews();

    SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
    Server = prefs.getString("ip", "192.168.1.94");
    Path = prefs.getString("path", "C:/PMEPRO1122");
    Username = prefs.getString("username", "SYSDBA");
    Password = prefs.getString("password", "masterkey");

    code_depot = prefs.getString("CODE_DEPOT", "000000");

  }


  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view_inv);

    ///////////////////
    SharedPreferences prefs2 = getSharedPreferences(PREFS, MODE_PRIVATE);
    CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
  }

  @Override
  protected void onStart() {

    setRecycle();

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("##,##0.00");

    super.onStart();
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapterInv1(this, getItems());
    recyclerView.setAdapter(adapter);

  }


  public ArrayList<PostData_Inv1> getItems() {
    inv1s = new ArrayList<>();

    String querry = "";
    if(!SOURCE_EXPORT.equals("EXPORTED")){
      if(CODE_DEPOT.equals("000000")){
        querry = "SELECT * FROM Inv1 WHERE IS_EXPORTED <> 1 OR IS_EXPORTED is null";
      }else{
        querry = "SELECT * FROM Inv1 WHERE CODE_DEPOT = '" + CODE_DEPOT+"' AND ( IS_EXPORTED <> 1 OR IS_EXPORTED is null)";
      }
    }else {
      if(CODE_DEPOT.equals("000000")){
        querry = "SELECT * FROM Inv1 WHERE IS_EXPORTED <> 0";
      }else{
        querry = "SELECT * FROM Inv1 WHERE CODE_DEPOT = '" + CODE_DEPOT+"' AND ( IS_EXPORTED <> 0)";
      }
    }



    // querry = "SELECT * FROM Events";
    inv1s = controller.select_list_inventaire_from_database(querry);

    return inv1s;
  }


  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);
    Intent editIntent = new Intent(ActivityInventaires.this, ActivityInventaire.class);
    editIntent.putExtra("NUM_INV", inv1s.get(position).num_inv);
    editIntent.putExtra("TYPE_ACTIVITY", "EDIT_INV");
    editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
    startActivity(editIntent);
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
  }


  @Override
  public void onLongClick(View v, final int position) {

    final CharSequence[] items = {"Supprimer", "Exporter" };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(R.drawable.blue_circle_24);
    builder.setTitle("Choisissez une action");
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        switch (item){
          case 0:
            new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer l'inventaire " + inv1s.get(position).nom_inv + " ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                      }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {

                        if(!controller.delete_inventaire_group(inv1s.get(position).num_inv)){
                          new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.ERROR_TYPE)
                                  .setTitleText("Erreur...")
                                  .setContentText("Erreur suprssion de l'inventaire ! ")
                                  .show();
                        }

                        setRecycle();

                        sDialog.dismiss();

                      }
                    })
                    .show();

            break;
          case 1:
            if(inv1s.get(position).blocage.equals("F")){
              new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Exportation")
                      .setContentText("Voulez-vous vraiment exporter l'inventaire ( " + inv1s.get(position).nom_inv + " ) ?!")
                      .setCancelText("Anuuler")
                      .setConfirmText("Exporter")
                      .showCancelButton(true)
                      .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                          sDialog.dismiss();
                        }
                      })
                      .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                          Check_connection_export_server check_connection_export_data_inventaire = new Check_connection_export_server("INVENTAIRE", inv1s.get(position).num_inv);
                          check_connection_export_data_inventaire.execute();

                          sDialog.dismiss();

                        }
                      })
                      .show();
            }else {
              new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.WARNING_TYPE)
                      .setTitleText("Attention...")
                      .setContentText("Cet inventaire n'est pas validé, Exportation impossible  ! ")
                      .show();
            }

            //Print_bon(bon1s.get(position).num_bon);
            //Toast.makeText(ActivityInventaires.this, "Cette option est en cours de developpement !", Toast.LENGTH_SHORT).show();

            break;
        }
      }
    });
    builder.show();


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if(!SOURCE_EXPORT.equals("EXPORTED")){
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_inventaire, menu);
    }


    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.new_inventaire){
        Intent editIntent = new Intent(ActivityInventaires.this, ActivityInventaire.class);
        editIntent.putExtra("TYPE_ACTIVITY", "NEW_INV");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    Sound(R.raw.back);
    super.onBackPressed();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }

  public void Sound(int SourceSound){
    mp = MediaPlayer.create(this, SourceSound);
    mp.start();
  }



  public class Check_connection_export_server extends AsyncTask<Void, Integer, Integer> {

    Connection con;
    Integer flag = 0;
    private String typeBon = "";
    private final String num_inv;


    public Check_connection_export_server(String typeBon, String num_inv) {

      this.typeBon = typeBon;
      this.num_inv = num_inv;
    }


    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mProgressDialog_Free = new ProgressDialog(ActivityInventaires.this);
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
      }

      return flag;
    }


    @Override
    protected void onPostExecute(Integer integer) {
      if (integer == 1) {
        // export data vente, commande to the server
        mProgressDialog_Free.dismiss();

       if (typeBon.equals("INVENTAIRE")) {
          Export_inventaire_to_server_task export_inventaire_to_server = new Export_inventaire_to_server_task(num_inv);
          export_inventaire_to_server.execute();
        }

      } else if (integer == 2) {
        mProgressDialog_Free.dismiss();
        Toast.makeText(ActivityInventaires.this, "Problème de connexion, vérifier les parametres", Toast.LENGTH_SHORT).show();
      } else {
        mProgressDialog_Free.dismiss();
        Toast.makeText(ActivityInventaires.this, "Probleme fatal", Toast.LENGTH_SHORT).show();
      }
      super.onPostExecute(integer);
    }
  }


  public class Export_inventaire_to_server_task extends AsyncTask<Void, Void, Integer> {

    Connection con;
    ArrayList<PostData_Inv1> invs1 = new ArrayList<>();
    ArrayList<PostData_Inv2> invs2 = new ArrayList<>();
    Integer recordid_inv1;
    Integer flag = 0;
    String _num_inv = null;

    String erreurMessage = "";

    List<String> list_num_inv_not_exported;

    public Export_inventaire_to_server_task(String num_inv) {

      _num_inv = num_inv;

    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mProgressDialog_Free = new ProgressDialog(ActivityInventaires.this);
      mProgressDialog_Free.setMessage("Exportation inventaires...");
      mProgressDialog_Free.setIndeterminate(false);
      mProgressDialog_Free.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog_Free.setCancelable(true);
      mProgressDialog_Free.show();
    }


    @Override
    protected Integer doInBackground(Void... params) {
      // TODO: attempt authentication against a network service.
      try {

        System.setProperty("FBAdbLog", "true");
        java.sql.DriverManager.setLoginTimeout(5);
        Class.forName("org.firebirdsql.jdbc.FBDriver");
        String sCon = "jdbc:firebirdsql:" + Server + ":" + Path + ".FDB?encoding=WIN1256";
        con = DriverManager.getConnection(sCon, Username, Password);

        Statement stmt = con.createStatement();
        con.setAutoCommit(false);
        list_num_inv_not_exported = new ArrayList<>();

        SimpleDateFormat format_local = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format_distant = new SimpleDateFormat("MM/dd/yyyy");

        /// else case export one invontory.
        invs1.clear();
        String querry = "SELECT * FROM Inv1";
        if (!code_depot.equals("000000")) {
          querry =  querry + " WHERE CODE_DEPOT = '" + code_depot + "' AND NUM_INV = '"+ _num_inv+"'";
        }else{
          querry = querry + " WHERE AND NUM_INV = '"+ _num_inv+"' AND (IS_EXPORTED <> 1 OR IS_EXPORTED is null)";
        }

        invs1 = controller.select_list_inventaire_from_database(querry);

        if(inv1s.size() > 0){
          try {

            String file_name = "";
            file_name = "INVENTAIRE_"+ invs1.get(0).num_inv+"_"+invs1.get(0).exportation + "_" + invs1.get(0).date_inv + ".INV";
            file_name = file_name.replace("/", "_");

            //========= CHECK IF CLIENT EXIST IN FIREBIRD PME PRO
            boolean inv_exist = false;
            String inv_requete = "SELECT EXPORTATION FROM INV1 WHERE EXPORTATION = '" + file_name + "' ";
            ResultSet rs0 = stmt.executeQuery(inv_requete);
            while (rs0.next()) {
              inv_exist = true;
            }
            if(!inv_exist){

              String querry_select = "SELECT * FROM Inv2 WHERE NUM_INV = '" + invs1.get(0).num_inv + "'";
              invs2 = controller.select_inventaire2_from_database(querry_select);
              String[] buffer = new String[invs2.size()];

              String ggg = "SELECT distinct gen_id(gen_inv1_id,1) as RECORDID FROM rdb$database";
              ResultSet rs1 = stmt.executeQuery(ggg);
              while (rs1.next()) {
                recordid_inv1 = rs1.getInt("RECORDID");
              }

              String NUM_INV = Get_Digits_String(String.valueOf(recordid_inv1), 6);

              Date dt = format_local.parse(invs1.get(0).date_inv);
              assert dt != null;

              String vvv = "INSERT INTO INV1 (RECORDID, NUM_INV, DATE_INV, HEURE, LIBELLE, NBR_PRODUIT, UTILISATEUR, EXPORTATION, CODE_DEPOT ) VALUES ('"
                      + recordid_inv1 + "' ," +
                      "'" + NUM_INV + "' ," +
                      "'" + format_distant.format(dt) + "'," +
                      "'" + invs1.get(0).heure_inv + "' ," +
                      "'" + invs1.get(0).nom_inv + "'," +
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
              con.commit();
              stmt.clearBatch();
              ///////////////////////////////// MISE A JOUR MONTANT INVENTAIRE //////////////////////
              String update_inv1_distant = "execute procedure upd_inv1 ('"+ NUM_INV +"')";
              stmt.executeUpdate(update_inv1_distant);
              con.commit();
              ////////////////////////////////////////////////////////////////////////
              controller.Update_inventaire1(invs1.get(0).num_inv);
              invs2.clear();
              flag = 1;

            }


          }catch (Exception e){
            con.rollback();
            list_num_inv_not_exported.add( invs1.get(0).num_inv);
            stmt.clearBatch();
          }
        }


      } catch (Exception ex) {
        ex.printStackTrace();
        con = null;
        Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
        if (Objects.requireNonNull(ex.getMessage()).contains("Unable to complete network request to host")) {
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

      mProgressDialog_Free.dismiss();

      if (integer == 1) {
        new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Exportation...")
                .setContentText("Exportation terminé")
                .show();
      } else if (integer == 2) {
        new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Attention. !")
                .setContentText("Connexion perdu, vérifier la connexion avec le serveur : " + erreurMessage)
                .show();
      } else if (integer == 3) {
        //  if(ActivityImportsExport. != null)
        new SweetAlertDialog(ActivityInventaires.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Erreur...")
                .setContentText("Erreur SQL : " + erreurMessage)
                .show();
      }

      setRecycle();

      super.onPostExecute(integer);
    }
  }

  public String Get_Digits_String(String number, Integer length) {
    String _number = number;
    while (_number.length() < length) {
      _number = "0" + _number;
    }
    Log.v("TRACKKK", _number);
    return _number;
  }
}

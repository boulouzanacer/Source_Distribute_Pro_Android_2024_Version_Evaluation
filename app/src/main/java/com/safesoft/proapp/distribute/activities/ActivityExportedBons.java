package com.safesoft.proapp.distribute.activities;


import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.vente.ActivityBon2;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.PrinterVente;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityExportedBons extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick , RecyclerAdapterBon1.ItemLongClick{


  RecyclerView recyclerView;
  RecyclerAdapterBon1 adapter;
  ArrayList<PostData_Bon1> bon1s;
  DATABASE controller;

  private MediaPlayer mp;


  private PostData_Bon1 bon1_print;
  private ArrayList<PostData_Bon2> bon2_print;


  private String PREFS = "ALL_PREFS";
  Boolean printer_mode_integrate = true;

  private NumberFormat nf;
  private String SOURCE;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ventes);

    if(getIntent() != null){
      SOURCE = getIntent().getStringExtra("SOURCE");
    }else {
      Crouton.makeText(ActivityExportedBons.this, "Erreur choix de source !", Style.ALERT).show();
      return;
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    
    if(SOURCE.equals("SALE")){
      getSupportActionBar().setTitle("Bons de livraison exportés");
    }else if(SOURCE.equals("ORDER")){
      getSupportActionBar().setTitle("Bons de commandes exportés");
    }else if(SOURCE.equals("INVENTAIRE")){
      getSupportActionBar().setTitle("Bons inventaire exportés");
    }

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    controller = new DATABASE(this);

    initViews();


  }

  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view_vente);
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
    if(SOURCE.equals("SALE")){
      adapter = new RecyclerAdapterBon1(this, getItems(), "SALE");
    }else if(SOURCE.equals("ORDER")){
      adapter = new RecyclerAdapterBon1(this, getItems(), "ORDER");
    }

    recyclerView.setAdapter(adapter);

  }


  public ArrayList<PostData_Bon1> getItems() {
    bon1s = new ArrayList<>();
    bon1s.clear();

    String querry;
    
    if(SOURCE.equals("SALE")){
      querry = "SELECT " +
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
              "Client.RC, " +
              "Client.IFISCAL, " +
              "Client.AI, " +
              "Client.NIS, " +

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
              "WHERE IS_EXPORTED = 1 ORDER BY Bon1.NUM_BON ";
    }else{
      querry = "SELECT " +
              "Bon1_Temp.RECORDID, " +
              "Bon1_Temp.NUM_BON, " +
              "Bon1_Temp.DATE_BON, " +
              "Bon1_Temp.HEURE, " +
              "Bon1_Temp.MODE_RG, " +
              "Bon1_Temp.MODE_TARIF, " +

              "Bon1_Temp.NBR_P, " +
              "Bon1_Temp.TOT_QTE, " +

              "Bon1_Temp.TOT_HT, " +
              "Bon1_Temp.TOT_TVA, " +
              "Bon1_Temp.TIMBRE, " +
              "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE AS TOT_TTC, " +
              "Bon1_Temp.REMISE, " +
              "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE AS MONTANT_BON, " +

              "Bon1_Temp.ANCIEN_SOLDE, " +
              "Bon1_Temp.VERSER, " +
              "Bon1_Temp.ANCIEN_SOLDE + (Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE) - Bon1_Temp.VERSER AS RESTE, " +

              "Bon1_Temp.CODE_CLIENT, " +
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

              "Bon1_Temp.LATITUDE, " +
              "Bon1_Temp.LONGITUDE, " +

              "Bon1_Temp.CODE_DEPOT, " +
              "Bon1_Temp.CODE_VENDEUR, " +
              "Bon1_Temp.EXPORTATION, " +
              "Bon1_Temp.BLOCAGE " +
              "FROM Bon1_Temp " +
              "LEFT JOIN Client ON Bon1_Temp.CODE_CLIENT = Client.CODE_CLIENT " +
              "WHERE IS_EXPORTED = 1 ORDER BY Bon1_Temp.NUM_BON ";
    }
    
  


    // querry = "SELECT * FROM Events";
    bon1s = controller.select_vente_from_database(querry);

    return bon1s;
  }


  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);

    Intent intent = new Intent(ActivityExportedBons.this, ActivityBon2.class);
    intent.putExtra("NUM_BON", bon1s.get(position).num_bon);
    startActivity(intent);

  }


  @Override
  public void onLongClick(View v, final int position) {

    if(bon1s.get(position).blocage.equals("F")){
      final CharSequence[] items = { "Supprimer", "Imprimer" };

      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setIcon(R.drawable.blue_circle_24);
      builder.setTitle("Choisissez une action");
      builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
          switch (item){

            case 0:
              new SweetAlertDialog(ActivityExportedBons.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Suppression")
                      .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
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

                          controller.delete_bon_after_export(false, bon1s.get(position).num_bon);
                          setRecycle();

                          sDialog.dismiss();
                        }
                      })
                      .show();

              break;
            case 1:
              try {
                Print_bon(bon1s.get(position).num_bon);
              } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
              }
              break;
          }
        }
      });
      builder.show();
    }else{
      final CharSequence[] items = {"Supprimer", "Imprimer" };

      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setIcon(R.drawable.blue_circle_24);
      builder.setTitle("Choisissez une action");
      builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
          switch (item){
            case 0:
              new SweetAlertDialog(ActivityExportedBons.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Suppression")
                      .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
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

                          controller.delete_bon_en_attente(false, bon1s.get(position).num_bon);
                          setRecycle();

                          sDialog.dismiss();
                        }
                      })
                      .show();

              break;
            case 1:
              try {
                Print_bon(bon1s.get(position).num_bon);
              } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
              }
              break;
          }
        }
      });
      builder.show();
    }


  }

  protected void Print_bon(String num_bon) throws UnsupportedEncodingException {

    
    bon1_print = new PostData_Bon1();
    bon2_print = new ArrayList<>();
  


    if(SOURCE.equals("SALE")){
      bon1_print = controller.select_bon1_from_database2("SELECT " +
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
              "Client.RC, " +
              "Client.IFISCAL, " +
              "Client.AI, " +
              "Client.NIS, " +

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
              "WHERE Bon1.NUM_BON ='"+num_bon+"'");

      bon2_print  = controller.select_bon2_from_database("" +
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
              "WHERE Bon2.NUM_BON = '" + num_bon+ "'");

      Activity bactivity;
      bactivity = ActivityExportedBons.this;

      PrinterVente printer = new PrinterVente();
      printer.start_print_sale_bon(bactivity, bon2_print, bon1_print);
    }else{
      bon1_print = controller.select_bon1_from_database2("SELECT " +
              "Bon1_Temp.RECORDID, " +
              "Bon1_Temp.NUM_BON, " +
              "Bon1_Temp.DATE_BON, " +
              "Bon1_Temp.HEURE, " +
              "Bon1_Temp.MODE_RG, " +
              "Bon1_Temp.MODE_TARIF, " +

              "Bon1_Temp.NBR_P, " +
              "Bon1_Temp.TOT_QTE, " +

              "Bon1_Temp.TOT_HT, " +
              "Bon1_Temp.TOT_TVA, " +
              "Bon1_Temp.TIMBRE, " +
              "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE AS TOT_TTC, " +
              "Bon1_Temp.REMISE, " +
              "Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE AS MONTANT_BON, " +

              "Bon1_Temp.ANCIEN_SOLDE, " +
              "Bon1_Temp.VERSER, " +
              "Bon1_Temp.ANCIEN_SOLDE + (Bon1_Temp.TOT_HT + Bon1_Temp.TOT_TVA + Bon1_Temp.TIMBRE - Bon1_Temp.REMISE) - Bon1_Temp.VERSER AS RESTE, " +

              "Bon1_Temp.CODE_CLIENT, " +
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

              "Bon1_Temp.LATITUDE, " +
              "Bon1_Temp.LONGITUDE, " +

              "Bon1_Temp.CODE_DEPOT, " +
              "Bon1_Temp.CODE_VENDEUR, " +
              "Bon1_Temp.EXPORTATION, " +
              "Bon1_Temp.BLOCAGE " +
              "FROM Bon1_Temp " +
              "LEFT JOIN Client ON Bon1_Temp.CODE_CLIENT = Client.CODE_CLIENT " +
              "WHERE Bon1_Temp.NUM_BON ='"+num_bon+"'");

      bon2_print  = controller.select_bon2_from_database("" +
              "SELECT " +
              "Bon2_Temp.RECORDID, " +
              "Bon2_Temp.CODE_BARRE, " +
              "Bon2_Temp.NUM_BON, " +
              "Bon2_Temp.PRODUIT, " +
              "Bon2_Temp.NBRE_COLIS, " +
              "Bon2_Temp.COLISSAGE, " +
              "Bon2_Temp.QTE, " +
              "Bon2_Temp.QTE_GRAT, " +
              "Bon2_Temp.PV_HT, " +
              "Bon2_Temp.TVA, " +
              "Bon2_Temp.CODE_DEPOT, " +
              "Bon2_Temp.PA_HT, " +
              "Bon2_Temp.DESTOCK_TYPE, " +
              "Bon2_Temp.DESTOCK_CODE_BARRE, " +
              "Bon2_Temp.DESTOCK_QTE, " +
              "Produit.STOCK " +
              "FROM Bon2_Temp " +
              "LEFT JOIN Produit ON (Bon2_Temp.CODE_BARRE = Produit.CODE_BARRE) " +
              "WHERE Bon2_Temp.NUM_BON = '" + num_bon+ "'");

      Activity bactivity;
      bactivity = ActivityExportedBons.this;

      PrinterVente printer = new PrinterVente();
      printer.start_print_order_bon(bactivity, bon2_print, bon1_print);
    }

    
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_ventes_after_export, menu);

    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.delete_all_after_export){
      new SweetAlertDialog(ActivityExportedBons.this, SweetAlertDialog.NORMAL_TYPE)
              .setTitleText("Suppression")
              .setContentText("Voulez-vous vraiment supprimer tout les bons de ventes qui sont déjà exportés au serveur ?!")
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

                  if(!controller.delete_all_bon(false)){
                    new SweetAlertDialog(ActivityExportedBons.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Success!")
                            .setContentText("tous les bons de ventes a été supprimé!")
                            .show();
                  }else{
                    new SweetAlertDialog(ActivityExportedBons.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Ops!")
                            .setContentText("Erreur de suppression de tous les bons de ventes!")
                            .show();
                  }
                  setRecycle();

                  sDialog.dismiss();
                }
              })
              .show();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    Sound(R.raw.back);
    super.onBackPressed();
  }

  public void Sound(int SourceSound){
    mp = MediaPlayer.create(this, SourceSound);
    mp.start();
  }

}

package com.safesoft.pro.distribute.activities.commande;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.safesoft.pro.distribute.activities.vente.ActivityEditSale;
import com.safesoft.pro.distribute.adapters.RecyclerAdapterCommande;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Achat1;
import com.safesoft.pro.distribute.postData.PostData_Achat2;
import com.safesoft.pro.distribute.postData.PostData_Bon1;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.R;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityCommandes extends AppCompatActivity implements RecyclerAdapterCommande.ItemClick , RecyclerAdapterCommande.ItemLongClick{

  ////////////////////////////////////////
  private final int NOPAPER = 3;
  private final int LOWBATTERY = 4;
  private final int PRINTVERSION = 5;
  private final int PRINTCONTENT = 9;
  private final int CANCELPROMPT = 10;
  private final int PRINTERR = 11;
  private final int OVERHEAT = 12;

  RecyclerView recyclerView;
  RecyclerAdapterCommande adapter;
  ArrayList<PostData_Bon1> bon1s;
  DATABASE controller;

  private String Result;
  private Boolean nopaper = false;
  private boolean LowBattery = false;
  private ProgressDialog progressDialog;
  private ProgressDialog progressDialog_wait_connecte;
  private ProgressDialog dialog;
  private MyHandler_Integrate handler_Integrate;
  private MyHandler_Bluetooth handler_Bluetooth;

  private MediaPlayer mp;

  private NumberFormat nf;

  private PostData_Bon1 bon1_print;

  private ArrayList<PostData_Bon2> bon2_print;

  private BluetoothSPP bt,btleft;
  private String PREFS_PRINTER = "ConfigPrinter";
  Boolean printer_mode_integrate = true;
  private String PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
  private String CODE_DEPOT, CODE_VENDEUR;
  private String PREFS_FOOTER = "Footer";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_ventes);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Les commandes");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));

    controller = new DATABASE(this);

    initViews();

    IntentFilter pIntentFilter = new IntentFilter();
    pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
    registerReceiver(printReceive, pIntentFilter);


    bt = new BluetoothSPP(this);


    if(!bt.isBluetoothAvailable()) {
      Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
      finish();
    }

    bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {

      public void onDeviceDisconnected() {
        if (progressDialog_wait_connecte != null && !ActivityCommandes.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityCommandes.this, "Imprimente bluetooth non connecté ", Style.ALERT).show();
      }


      public void onDeviceConnectionFailed() {
        if (progressDialog_wait_connecte != null && !ActivityCommandes.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityCommandes.this, "Imprimente connection erroné ", Style.ALERT).show();
      }

      public void onDeviceConnected(String name, String address) {
        if (progressDialog_wait_connecte != null && !ActivityCommandes.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityCommandes.this, "Imprimente connecté à  "+ name, Style.CONFIRM).show();
        prepareBon_Bluetooth();
      }

    });
  }

  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
  }

  @Override
  protected void onStart() {

    setRecycle();

    SharedPreferences prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
    if(prefs1.getString("PRINTER", "INTEGRATE").toString().equals("INTEGRATE")){
      printer_mode_integrate = true;
    }else {
      printer_mode_integrate = false;
    }

    if(!printer_mode_integrate){
      if (!bt.isBluetoothEnabled()) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
      } else {
        if(!bt.isServiceAvailable()) {
          bt.setupService();
          bt.startService(BluetoothState.DEVICE_ANDROID);
        }
      }
    }

    SharedPreferences prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
    CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");
    CODE_VENDEUR = prefs.getString("CODE_VENDEUR", "000000");

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("##,##0.00");
    super.onStart();
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapterCommande(this, getItems());
    recyclerView.setAdapter(adapter);
  }

  public ArrayList<PostData_Bon1> getItems() {
    bon1s = new ArrayList<>();
    bon1s.clear();

    String querry = "SELECT " +
            "Bon1_Temp.RECORDID, " +
            "Bon1_Temp.NUM_BON, " +
            "Bon1_Temp.CODE_CLIENT, " +
            "Client.CLIENT, " +
            "Bon1_Temp.LATITUDE, " +

            "Bon1_Temp.LONGITUDE, " +
            "Client.LATITUDE as LATITUDE_CLIENT, " +

            "Client.LONGITUDE as LONGITUDE_CLIENT, " +

            "Bon1_Temp.DATE_BON, " +
            "Bon1_Temp.HEURE, " +
            "Bon1_Temp.NBR_P, " +
            "Bon1_Temp.MODE_TARIF, " +

            "Bon1_Temp.CODE_DEPOT, " +
            "Bon1_Temp.MONTANT_BON, " +
            "Bon1_Temp.MODE_RG, " +
            "Bon1_Temp.CODE_VENDEUR, " +
            "Bon1_Temp.EXPORTATION, " +
            "Bon1_Temp.REMISE, " +
            "Bon1_Temp.TOT_TTC, " +
            "Bon1_Temp.TOT_TVA, " +
            "Bon1_Temp.TOT_HT, " +

            "Client.TEL, " +
            "Client.ADRESSE, " +
            "Client.RC, " +
            "Client.IFISCAL, " +


            "Bon1_Temp.TIMBRE, " +
            "Bon1_Temp.VERSER, " +
            "Bon1_Temp.RESTE, " +
            "Bon1_Temp.BLOCAGE, " +
            "Bon1_Temp.ANCIEN_SOLDE " +
            "FROM " +
            "Bon1_temp " +
            "LEFT JOIN Client ON " +
            "Bon1_temp.CODE_CLIENT = Client.CODE_CLIENT " +
            "ORDER BY Bon1_temp.DATE_BON DESC";

    bon1s = controller.select_vente_from_database(querry);

    return bon1s;
  }

  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);
    if(bon1s.get(position).blocage.equals("F")){
      Intent intent = new Intent(ActivityCommandes.this, ActivityCommande2.class);
      intent.putExtra("NUM_BON", bon1s.get(position).num_bon);
      startActivity(intent);
    }else{
      Intent editIntent = new Intent(ActivityCommandes.this, ActivityEditCommande.class);
      editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
      editIntent.putExtra("VALIDATED", "FALSE");
      startActivity(editIntent);
    }

  }


  @Override
  public void onLongClick(View v, final int position) {

    if(bon1s.get(position).blocage.equals("F")){
      final CharSequence[] items = {"Modifier", "Supprimer", "Imprimer", "Exporter vers ventes" };

      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle("Séléctionner action");
      builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
          switch (item){
            case 0:

              new SweetAlertDialog(ActivityCommandes.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Bon de commande")
                      .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                      .setCancelText("Non")
                      .setConfirmText("Modifier")
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

                          Intent editIntent = new Intent(ActivityCommandes.this, ActivityEditCommande.class);
                          editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
                          editIntent.putExtra("VALIDATED", "TRUE");
                          startActivity(editIntent);

                          sDialog.dismiss();
                        }
                      })
                      .show();



              break;
            case 1:
              new SweetAlertDialog(ActivityCommandes.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Supprission")
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

                          controller.delete_bon_en_attente(true, bon1s.get(position).num_bon);
                          setRecycle();

                          sDialog.dismiss();
                        }
                      })
                      .show();

              break;
            case 2:

              Print_bon(bon1s.get(position).num_bon);
              break;
            case 3:
              new SweetAlertDialog(ActivityCommandes.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Exportation")
                      .setContentText("Voulez-vous vraiment exporter ce bon vers les ventes?!")
                      .setCancelText("Non")
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

                          Export_to_sales(bon1s.get(position).num_bon);

                          sDialog.dismiss();
                        }
                      })
                      .show();

              break;
          }
        }
      });
      builder.show();
    }else{
      final CharSequence[] items = {"Supprimer", "Imprimer" };

      AlertDialog.Builder builder = new AlertDialog.Builder(this);

      builder.setTitle("Séléctionner action");
      builder.setItems(items, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int item) {
          switch (item){
            case 0:
              new SweetAlertDialog(ActivityCommandes.this, SweetAlertDialog.NORMAL_TYPE)
                      .setTitleText("Supprission")
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

                          controller.delete_bon_vente(true, bon1s.get(position));
                          setRecycle();

                          sDialog.dismiss();
                        }
                      })
                      .show();

              break;
            case 1:
              // Prepare_Bon();
              //  Print();
              Print_bon(bon1s.get(position).num_bon);
              break;
          }
        }
      });
      builder.show();
    }


  }

  protected void Export_to_sales(String num_bon){

    Prepare_Bon(num_bon);
    String new_num_bon = Get_Digits_String();
    if(controller.ExCommande_Export_to_ventes("Bon1", "Bon2", bon1_print, bon2_print, new_num_bon)){


      Intent editIntent = new Intent(ActivityCommandes.this, ActivityEditSale.class);
      editIntent.putExtra("NUM_BON", bon1_print.num_bon);
      editIntent.putExtra("VALIDATED", "FALSE");
      startActivity(editIntent);


           /* Intent versement_intent = new Intent(ActivityCommandes.this, ActivityVersement.class);
            versement_intent.putExtra("NUM_BON", new_num_bon);
            versement_intent.putExtra("CODE_DEPOT", bon1_print.code_depot);
            versement_intent.putExtra("LATITUDE", bon1_print.latitude);
            versement_intent.putExtra("LONGITUDE", bon1_print.longitude);
            startActivityForResult(versement_intent, 55555);*/
      finish();
    }else{

      new SweetAlertDialog(ActivityCommandes.this, SweetAlertDialog.ERROR_TYPE)
              .setTitleText("Erreur...!")
              .setContentText("Une erreur survenue, Exportation impossible !")
              .show();
    }
  }

  public String Get_Digits_String(){
    String _number = controller.Select_max_num_bon_vente("Bon1");
    while(_number.length() < 6){
      _number = "0" + _number;
    }
    return _number;
  }

  protected void Prepare_Bon(String num_bon){

    bon1_print = new PostData_Bon1();
    bon2_print = new ArrayList<>();
    bon1_print = controller.select_bon1_from_database2("" +
            "SELECT " +
            "Bon1_temp.NUM_BON, " +
            "Bon1_temp.CODE_CLIENT, " +
            "Bon1_temp.DATE_BON, " +
            "Bon1_temp.HEURE, " +
            "Bon1_temp.CODE_DEPOT, " +
            "Bon1_temp.TOT_HT, " +
            "Bon1_temp.TOT_TVA, " +
            "Bon1_temp.TOT_TTC, " +
            "Bon1_temp.TOT_TTC_REMISE, " +
            "Bon1_temp.MONTANT_BON, " +
            "Bon1_temp.REMISE, " +
            "Bon1_temp.REMISE_CHECK, " +
            "Bon1_temp.TIMBRE, " +
            "Bon1_temp.NBR_P, " +
            "Bon1_temp.TIMBRE_CHECK, " +
            "Bon1_temp.RESTE, " +
            "Bon1_temp.VERSER, " +
            "Bon1_temp.LATITUDE, " +
            "Bon1_temp.LONGITUDE, " +
            "Client.CLIENT, " +
            "Client.CREDIT_LIMIT, " +
            "Client.SOLDE AS ANCIEN_SOLDE " +
            "FROM Bon1_temp,Client " +
            "WHERE " +
            "Bon1_temp.CODE_CLIENT = Client.CODE_CLIENT " +
            "AND " +
            "Bon1_temp.NUM_BON ='"+num_bon+"'");

    bon2_print  =controller.select_bon2_from_database("" +
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
            "WHERE Bon2_temp.NUM_BON = '" + num_bon + "'" );


  }


  // Preparation de Bon complet
  protected void Print(){

    handler_Integrate = new MyHandler_Integrate();
    if (LowBattery == true) {
      handler_Integrate.sendMessage(handler_Integrate.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityCommandes.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityCommandes.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }


  protected void Print_bon(String num_bon){

    bon1_print = new PostData_Bon1();
    bon2_print = new ArrayList<>();
    bon1_print = controller.select_bon1_from_database2("" +
            "SELECT " +
            "Bon1_temp.NUM_BON, " +
            "Bon1_temp.CODE_CLIENT, " +
            "Bon1_temp.DATE_BON, " +
            "Bon1_temp.HEURE, " +
            "Bon1_temp.CODE_DEPOT, " +
            "Bon1_temp.TOT_HT, " +
            "Bon1_temp.TOT_TVA, " +
            "Bon1_temp.TOT_TTC, " +
            "Bon1_temp.TOT_TTC_REMISE, " +
            "Bon1_temp.MONTANT_BON, " +
            "Bon1_temp.REMISE, " +
            "Bon1_temp.REMISE_CHECK, " +
            "Bon1_temp.TIMBRE, " +
            "Bon1_temp.NBR_P, " +
            "Bon1_temp.TIMBRE_CHECK, " +
            "Bon1_temp.RESTE, " +
            "Bon1_temp.VERSER, " +
            "Bon1_temp.LATITUDE, " +
            "Bon1_temp.LONGITUDE, " +
            "Client.CLIENT, " +
            "Client.CREDIT_LIMIT, " +
            "Client.SOLDE AS ANCIEN_SOLDE " +
            "FROM Bon1_temp,Client " +
            "WHERE " +
            "Bon1_temp.CODE_CLIENT = Client.CODE_CLIENT " +
            "AND " +
            "Bon1_temp.NUM_BON ='"+num_bon+"'");

    bon2_print  =controller.select_bon2_from_database("" +
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
            "WHERE Bon2_temp.NUM_BON = '" + num_bon + "'" );


    if(printer_mode_integrate){
      prepareBon_IntegratePrinter();
    }else{
      if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
        // print
        Toast.makeText(ActivityCommandes.this, " Imprission ..." , Toast.LENGTH_SHORT).show();
        prepareBon_Bluetooth();

      }else{
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
      }
    }
  }

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if(resultCode == Activity.RESULT_OK){
        bt.connect(data);
        progressDialog_wait_connecte = ProgressDialog.show(ActivityCommandes.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
      }
    } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
      if(resultCode == Activity.RESULT_OK) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_ANDROID);
      } else {
        Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  // Preparation de Bon pour l'imprimente intégrer
  protected void prepareBon_IntegratePrinter(){

    handler_Integrate = new MyHandler_Integrate();
    if (LowBattery == true) {
      handler_Integrate.sendMessage(handler_Integrate.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityCommandes.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityCommandes.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }


  protected void prepareBon_Bluetooth(){

    handler_Bluetooth = new MyHandler_Bluetooth();
    if (LowBattery == true) {
      handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityCommandes.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityCommandes.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_ventes, menu);

    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.new_sale){
      startActivity(new Intent(ActivityCommandes.this, ActivityNewCommande.class));
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

  //Class thread printing
  private class contentPrintThread_Integrate extends Thread {
    @Override
    public void run() {
      super.run();

      try {
        ThermalPrinter.start(ActivityCommandes.this);
        ThermalPrinter.reset();
        ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);

        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);

        SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if(prefs.getBoolean("ENTETE_SHOW", false)){

          ThermalPrinter.addString(prefs.getString("COMPANY_NAME", "") + "\n");
          ThermalPrinter.addString(prefs.getString("ACTIVITY_NAME", "") + "\n");
          ThermalPrinter.addString(prefs.getString("ADRESSE", "") + "\n");
          ThermalPrinter.addString("--------------------------------");
          ThermalPrinter.printString();
        }

        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.enlargeFontSize(2, 2);
        ThermalPrinter.setGray(12);

        ////////////////////////////////
        //  ThermalPrinter.setBold(true);
        ThermalPrinter.addString(" BON DE VENTE\n");
        ThermalPrinter.addString("  "+bon1_print.num_bon+"\n");
        ThermalPrinter.addString("----------------");
        ThermalPrinter.printString();
        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);
        ////////////////////////////////
        ThermalPrinter.addString("CLIENT : " + bon1_print.client);
        ThermalPrinter.printString();
        ThermalPrinter.addString("CODE CLIENT : " + bon1_print.code_client);
        ThermalPrinter.printString();
        ThermalPrinter.addString("          ************");
        ThermalPrinter.printString();
        ThermalPrinter.addString("CODE_DEPOT : " + bon1_print.code_depot);
        ThermalPrinter.printString();
        ThermalPrinter.addString("DATE HEURE : " + bon1_print.date_bon + " " + bon1_print.heure);
        ThermalPrinter.printString();
        ThermalPrinter.addString("--------------------------------");
        ThermalPrinter.printString();
        ///////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(1);
        ThermalPrinter.enlargeFontSize(1, 2);
        ThermalPrinter.setGray(12);
        ///////////////////////////


        for(int b= 0; b<bon2_print.size(); b++){
          if(bon2_print.get(b).produit.length()>48){
            bon2_print.get(b).produit = bon2_print.get(b).produit.substring(0, 47);
          }
          ThermalPrinter.addString(bon2_print.get(b).produit);
          ThermalPrinter.printString();

          String quantite = bon2_print.get(b).qte;
          String prix_u = new DecimalFormat("##,##0.00").format(Double.valueOf(bon2_print.get(b).p_u));
          String total_produit = new DecimalFormat("##,##0.00").format(Double.valueOf(bon2_print.get(b).p_u) * Double.valueOf(bon2_print.get(b).qte));
          String espace_walk0="", espace_walk1="", espace_walk2="", espace_walk3= "", espace_walk4= "";

          //Walk 1
          if(quantite.length() == 1) {
            espace_walk0 = "     ";
          }else  if(quantite.length() == 2){
            espace_walk0 = "    ";
          }else  if(quantite.length() == 3){
            espace_walk0 = "   ";
          }else  if(quantite.length() == 4){
            espace_walk0 = "  ";
          }else  if(quantite.length() == 5){
            espace_walk0 = " ";
          }

          espace_walk1 = "   ";

          //Walk 2
          if(prix_u.length() ==4){
            espace_walk2 = "          ";
          }else if(prix_u.length() ==5){
            espace_walk2 = "         ";
          }else if(prix_u.length() ==6){
            espace_walk2 = "        ";
          }else if(prix_u.length() ==7){
            espace_walk2 = "       ";
          }else if(prix_u.length() ==8){
            espace_walk2 = "      ";
          }else if(prix_u.length() ==9){
            espace_walk2 = "     ";
          }else if(prix_u.length() ==10){
            espace_walk2 = "    ";
          }else if(prix_u.length() ==11){
            espace_walk2 = "   ";
          }else if(prix_u.length() ==12){
            espace_walk2 = "  ";
          }


          //walk3
          espace_walk3 = "   ";
          // walk4
          if(total_produit.length() ==4){
            espace_walk4 = "                ";
          }else if(total_produit.length() ==5){
            espace_walk4 = "               ";
          }else if(total_produit.length() ==6){
            espace_walk4 = "              ";
          }else if(total_produit.length() ==7){
            espace_walk4 = "             ";
          }else if(total_produit.length() ==8){
            espace_walk4 = "            ";
          }else if(total_produit.length() ==9){
            espace_walk4 = "           ";
          }else if(total_produit.length() ==10){
            espace_walk4 = "          ";
          }else if(total_produit.length() ==11){
            espace_walk4 = "         ";
          }else if(total_produit.length() ==12){
            espace_walk4 = "        ";
          }else if(total_produit.length() ==13){
            espace_walk4 = "       ";
          }else if(total_produit.length() ==14){
            espace_walk4 = "      ";
          }else if(total_produit.length() ==15){
            espace_walk4 = "     ";
          }else if(total_produit.length() ==16){
            espace_walk4 = "    ";
          }else if(total_produit.length() ==17){
            espace_walk4 = "   ";
          }else if(total_produit.length() ==18){
            espace_walk4 = "  ";
          }

          ThermalPrinter.addString(espace_walk0 +quantite+ espace_walk1 +"X"+ espace_walk2 + prix_u + espace_walk3 +"="+ espace_walk4 + total_produit);
          ThermalPrinter.printString();
        }

        ThermalPrinter.addString("------------------------------------------------");
        ThermalPrinter.printString();
        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);
        /////////////////////////////////////////////
        String walk ="";
        String total_ht = "TOTAL HT : "  + new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_ht)) + " DA";
        for(int a = 0; a < 32 - total_ht.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + total_ht);
        ThermalPrinter.printString();

        walk ="";
        String tva = "TVA : "  + new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_tva.toString()))+ " DA";
        for(int a = 0; a < 32 - tva.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + tva);
        ThermalPrinter.printString();

        if(bon1_print.timbre_ckecked){
          walk ="";
          String timbre =  "TIMBRE : " +new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.timbre.toString()))+ " DA";
          for(int a = 0; a < 32 - timbre.length(); a++){
            walk = walk + " ";
          }
          ThermalPrinter.addString(walk + timbre);
          ThermalPrinter.printString();
        }

        walk ="";
        String total_ttc = "TOTAL TTC : "  + new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_ttc.toString())) + " DA";
        for(int a = 0; a < 32 - total_ttc.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + total_ttc);
        ThermalPrinter.printString();


        if(bon1_print.remise_ckecked){

          walk = "";
          for(int a = 0; a < 32 - total_ttc.length(); a++){
            walk = walk + " ";
          }

          String line ="";
          for(int a = 0; a <total_ttc.length(); a++){
            line = line + "_";
          }

          ThermalPrinter.addString(walk+ line);
          ThermalPrinter.printString();

          walk ="";
          String remise =  "REMISE : " + new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.remise.toString()))+ " DA";
          for(int a = 0; a < 32 - remise.length(); a++){
            walk = walk + " ";
          }
          ThermalPrinter.addString(walk + remise);
          ThermalPrinter.printString();

          walk ="";
          String total_ttc_apres_remise = "NET A PAYER : " +new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_ttc_remise.toString()))+ " DA";
          for(int a = 0; a < 32 - total_ttc_apres_remise.length(); a++){
            walk = walk + " ";
          }
          ThermalPrinter.addString(walk + total_ttc_apres_remise);
          ThermalPrinter.printString();
        }

        ThermalPrinter.addString("--------------------------------");
        ThermalPrinter.printString();
        walk ="";
        String Anciensolde = "ANCIEN SOLDE : "  +  new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.solde_ancien)) + " DA";
        for(int a = 0; a < 32 - Anciensolde.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + Anciensolde);
        ThermalPrinter.printString();


        if(bon1_print.remise_ckecked){
          walk ="";
          String total_ttc_apres_remise = "NET A PAYER : " +new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_ttc_remise.toString()))+ " DA";
          for(int a = 0; a < 32 - total_ttc_apres_remise.length(); a++){
            walk = walk + " ";
          }
          ThermalPrinter.addString(walk + total_ttc_apres_remise);
          ThermalPrinter.printString();
        }else{
          walk ="";
          String total_ttc_apres_remise = "NET A PAYER : " +new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.tot_ttc.toString()))+ " DA";
          for(int a = 0; a < 32 - total_ttc_apres_remise.length(); a++){
            walk = walk + " ";
          }
          ThermalPrinter.addString(walk + total_ttc_apres_remise);
          ThermalPrinter.printString();
        }


        walk ="";
        String versement = "VERSEMENT : "  + new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.verser))+ " DA";
        for(int a = 0; a < 32 - versement.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + versement);
        ThermalPrinter.printString();

        walk ="";
        String reste = "RESTE : "  +  new DecimalFormat("##,##0.00").format(Double.valueOf(bon1_print.reste)) + " DA";
        for(int a = 0; a < 32 - reste.length(); a++){
          walk = walk + " ";
        }
        ThermalPrinter.addString(walk + reste);
        ThermalPrinter.printString();


        ThermalPrinter.walkPaper(100);

      } catch (TelpoException e) {
        e.printStackTrace();
        Result = e.toString();
        if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
          nopaper = true;
        } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
          handler_Integrate.sendMessage(handler_Integrate.obtainMessage(OVERHEAT, 1, 0, null));
        } else {
          handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTERR, 1, 0, null));
        }
      } finally {
        handler_Integrate.sendMessage(handler_Integrate.obtainMessage(CANCELPROMPT, 1, 0, null));
        if (nopaper){
          handler_Integrate.sendMessage(handler_Integrate.obtainMessage(NOPAPER, 1, 0, null));
          nopaper = false;
          return;
        }
        ThermalPrinter.stop(ActivityCommandes.this);
      }
    }
  }

  private class MyHandler_Integrate extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case NOPAPER:
          noPaperDlg();
          break;
        case LOWBATTERY:
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityCommandes.this);
          alertDialog.setTitle(R.string.operation_result);
          alertDialog.setMessage(getString(R.string.LowBattery));
          alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
          });
          alertDialog.show();
          break;
        case PRINTVERSION:
          dialog.dismiss();
          if (msg.obj.equals("1")) {
            // textPrintVersion.setText(printVersion);
          } else {
            Toast.makeText(ActivityCommandes.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Integrate().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityCommandes.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityCommandes.this);
          overHeatDialog.setTitle(R.string.operation_result);
          overHeatDialog.setMessage(getString(R.string.overTemp));
          overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
          });
          overHeatDialog.show();
          break;
        default:
          Toast.makeText(ActivityCommandes.this, "Print Error!", Toast.LENGTH_LONG).show();
          break;
      }
    }
  }



  private final BroadcastReceiver printReceive = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();
      if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        //TPS390 can not print,while in low battery,whether is charging or not charging
        if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
          if (level * 5 <= scale) {
            LowBattery = true;
          } else {
            LowBattery = false;
          }
        }else {
          if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
            if (level * 5 <= scale) {
              LowBattery = true;
            } else {
              LowBattery = false;
            }
          } else {
            LowBattery = false;
          }
        }
      }
      //Only use for TPS550MTK devices
      else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
        int status = intent.getIntExtra("action", 0);
        int level = intent.getIntExtra("level", 0);
        if(status == 0){
          if(level < 1){
            LowBattery = true;
          }else {
            LowBattery = false;
          }
        }else {
          LowBattery = false;
        }
      }
    }
  };

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      finish();
    }
    return super.onKeyDown(keyCode, event);
  }

  private void noPaperDlg() {
    AlertDialog.Builder dlg = new AlertDialog.Builder(ActivityCommandes.this);
    dlg.setTitle(getString(R.string.noPaper));
    dlg.setMessage(getString(R.string.noPaperNotice));
    dlg.setCancelable(false);
    dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        ThermalPrinter.stop(ActivityCommandes.this);
      }
    });
    dlg.show();
  }

  private class MyHandler_Bluetooth extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case PRINTVERSION:
          dialog.dismiss();
          if (msg.obj.equals("1")) {
            // textPrintVersion.setText(printVersion);
          } else {
            Toast.makeText(ActivityCommandes.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Bluetooth().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityCommandes.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityCommandes.this);
          overHeatDialog.setTitle(R.string.operation_result);
          overHeatDialog.setMessage(getString(R.string.overTemp));
          overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
          });
          overHeatDialog.show();
          break;
        default:
          Toast.makeText(ActivityCommandes.this, "Print Error!", Toast.LENGTH_LONG).show();
          break;
      }
    }
  }


  //Class thread printing
  private class contentPrintThread_Bluetooth extends Thread {
    @Override
    public void run() {
      super.run();

      try {


        byte[] arrayOfByte1 = { 27, 33, 0 };
        byte[] format = { 27, 33, 0 };
        // format[2] = ((byte) (0x20 | arrayOfByte1[2]));

        byte[] format_normal = {27, 15, 0 }; // manipulate your font size in the second parameter
        byte[] format01 = {27, 33, 18 }; // manipulate your font size in the second parameter
        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left =  { 0x1b, 'a', 0x00 }; // left alignment

        byte[] right =  { 0x1b, 'a', 0x02 }; // left alignment

        // Underline
        // format[2] = ((byte)(0x80 | arrayOfByte1[2]));



        SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if(prefs.getBoolean("ENTETE_SHOW", false)){

          format[2] = ((byte) (0x20 | arrayOfByte1[2]));

          bt.send(center, true);
          bt.send(prefs.getString("COMPANY_NAME", ""), true);
          bt.send(prefs.getString("ACTIVITY_NAME", ""), true);
          bt.send(prefs.getString("ADRESSE", ""), true);
          format[2] = ((byte)(0x0 | arrayOfByte1[2]));

          bt.send(format, true);

          bt.send("----------------------------------------------", true);

        }

        ////////////////////////////////
        //bt.send(center, true);
        format[2] = ((byte) (0x20 | arrayOfByte1[2]));

        bt.send(center, true);
        bt.send("BON DE COMMANDE", true);
        bt.send("N "+bon1_print.num_bon, true);
        //bt.send("------------------------------------------------", true);

        SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
        Date myDate = null;
        try {
          myDate = df_save.parse(bon1_print.date_bon);

        } catch (ParseException e) {
          e.printStackTrace();
        }
        String formattedDate_Show = df_show.format(myDate);
        format[2] = ((byte) (0x1 | arrayOfByte1[2]));
        bt.send(left, true);

        ////////////////////////////////
        bt.send("CLIENT : " + bon1_print.client.toUpperCase(), true);
        bt.send("TEL : " + bon1_print.tel, true);

        bt.send("          ************", true);

        // bt.send(format_normal, true);
        bt.send("CODE_DEPOT : " + bon1_print.code_depot, true);
        bt.send("CODE_VENDEUR : " + CODE_VENDEUR , true);
        bt.send("DATE HEURE : " + formattedDate_Show + " " + bon1_print.heure, true);
        bt.send("------------------------------------------------", true);


        ///////////////////////

        ///////////////////////////

        format[2] = ((byte)(0x1 | arrayOfByte1[2]));
        bt.send(format, true);

        for(int b= 0; b< bon2_print.size(); b++){

          if(bon2_print.get(b).produit.length()>48){
            bon2_print.get(b).produit = bon2_print.get(b).produit.substring(0, 47);
          }


          String quantite = bon2_print.get(b).qte;
          String prix_u = nf.format(Double.valueOf(bon2_print.get(b).p_u) + (Double.valueOf(bon2_print.get(b).p_u )*(Double.valueOf(bon2_print.get(b).tva)/Double.valueOf(100))));
          String total_produit = nf.format((Double.valueOf(bon2_print.get(b).p_u) + (Double.valueOf(bon2_print.get(b).p_u )*(Double.valueOf(bon2_print.get(b).tva)/Double.valueOf(100)))) * Double.valueOf(bon2_print.get(b).qte));

          bt.send(bon2_print.get(b).produit +" / "+quantite + " X "+ prix_u +" = "+ total_produit, true);
        }

        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("------------------------------------------------", true);

        /////////////////////////////////////////////


        String walk ="";


        if(bon1_print.timbre_ckecked){
          walk ="";
          String timbre =  "TIMBRE : " + nf.format(Double.valueOf(bon1_print.timbre.toString()))+ " DA";
          for(int a = 0; a < 48 - timbre.length(); a++){
            walk = walk + " ";
          }
          bt.send(walk + timbre, true);
        }

        walk ="";
        String total_ttc = "TOTAL TTC : "  + nf.format(Double.valueOf(bon1_print.tot_ttc.toString())) + " DA";
        for(int a = 0; a < 48 - total_ttc.length(); a++){
          walk = walk + " ";
        }
        bt.send(walk + total_ttc, true);

        if(bon1_print.remise_ckecked){

          walk = "";
          for(int a = 0; a < 48 - total_ttc.length(); a++){
            walk = walk + " ";
          }

          String line ="";
          for(int a = 0; a <total_ttc.length(); a++){
            line = line + "_";
          }

          bt.send(walk + line, true);

          walk ="";
          String remise =  "REMISE : " + nf.format(Double.valueOf(bon1_print.remise.toString()))+ " DA";
          for(int a = 0; a < 48 - remise.length(); a++){
            walk = walk + " ";
          }
          bt.send(walk + remise, true);

          walk ="";
          String total_ttc_apres_remise = "MONTANT BON : " +nf.format(Double.valueOf(bon1_print.tot_ttc_remise.toString()))+ " DA";
          for(int a = 0; a < 48 - total_ttc_apres_remise.length(); a++){
            walk = walk + " ";
          }
          bt.send(walk + total_ttc_apres_remise, true);
        }

        bt.send("------------------------------------------------", true);
        prefs = getSharedPreferences(PREFS_FOOTER, MODE_PRIVATE);
        if(prefs.getBoolean("FOOTER", false)){
          bt.send(right, true);


          bt.send(prefs.getString("MERCI", "").toUpperCase(), true);
          bt.send(left, true);
          format[2] = ((byte)(0x1 | arrayOfByte1[2]));
          bt.send(format, true);
          bt.send(prefs.getString("ENCAS", ""), true);


        }else
        {
          bt.send(right, true);

          format[2] = ((byte)(0x1 | arrayOfByte1[2]));
          bt.send(format, true);
          bt.send("MERCI", true);

        }
        bt.send("\n", true);

                /*
                walk ="";
                String Anciensolde = "ANCIEN SOLDE :
                "  +  nf.format(Double.valueOf(bon1_print.solde_ancien)) + " DA";
                for(int a = 0; a < 48 - Anciensolde.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + Anciensolde, true);


                if(bon1_print.remise_ckecked){
                    walk ="";
                    String total_ttc_apres_remise = "MONTANT BON : " +nf.format(Double.valueOf(bon1_print.tot_ttc_remise.toString()))+ " DA";
                    for(int a = 0; a < 48 - total_ttc_apres_remise.length(); a++){
                        walk = walk + " ";
                    }
                    bt.send(walk + total_ttc_apres_remise, true);

                }else{
                    walk ="";
                    String total_ttc_apres_remise = "MONTANT BON : " +nf.format(Double.valueOf(bon1_print.tot_ttc.toString()))+ " DA";
                    for(int a = 0; a < 48 - total_ttc_apres_remise.length(); a++){
                        walk = walk + " ";
                    }
                    bt.send(walk + total_ttc_apres_remise, true);
                }

                walk ="";
                Double current_solde = (Double.valueOf(bon1_print.solde_ancien) + Double.valueOf(bon1_print.montant_bon));
                String solde_actuel = "SOLDE ACTUEL: "  +  nf.format(current_solde)+ " DA";
                for(int a = 0; a < 48 - solde_actuel.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + solde_actuel, true);


                walk ="";
                String versement = "VERSEMENT : "  + nf.format(Double.valueOf(bon1_print.verser))+ " DA";
                for(int a = 0; a < 48 - versement.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + versement, true);

                walk ="";
                String reste = "NOUVEAU SOLDE : "  +  nf.format(Double.valueOf(bon1_print.reste)) + " DA";
                for(int a = 0; a < 48 - reste.length(); a++){
                    walk = walk + " ";
                }


                bt.send(walk + reste, true);
*/
        bt.send("\n", true);

      } catch (Exception e) {
        e.printStackTrace();
        Result = e.toString();
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTERR, 1, 0, null));

      } finally {
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(CANCELPROMPT, 1, 0, null));
      }
    }
  }



  @Override
  protected void onDestroy() {
    // Unregister
    if (progressDialog != null && !ActivityCommandes.this.isFinishing()) {
      progressDialog.dismiss();
      progressDialog = null;
    }
    unregisterReceiver(printReceive);
    ThermalPrinter.stop();


    if(bt != null)
      bt.stopService();


    super.onDestroy();
  }

}

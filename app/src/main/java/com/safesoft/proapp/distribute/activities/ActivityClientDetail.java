package com.safesoft.proapp.distribute.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.proapp.distribute.adapters.RecyclerAdapter_Situation;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.FragmentVersement;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityClientDetail extends AppCompatActivity implements RecyclerAdapter_Situation.ItemClick {

  ////////////////////////////////////////
  private final int NOPAPER = 3;
  private final int LOWBATTERY = 4;
  private final int PRINTVERSION = 5;
  private final int PRINTCONTENT = 9;
  private final int CANCELPROMPT = 10;
  private final int PRINTERR = 11;
  private final int OVERHEAT = 12;
  private final int PRINTPICTURE = 14;

  private String Result;
  private Boolean nopaper = false;
  private boolean LowBattery = false;
  private ProgressDialog progressDialog;
  private ProgressDialog progressDialog_wait_connecte;
  private final static int MAX_LEFT_DISTANCE = 255;
  private ProgressDialog dialog;
  private MyHandler_Integrate handler_Integrate;
  private MyHandler_Bluetooth handler_Bluetooth;

  private BluetoothSPP bt;
  private String PREFS_PRINTER = "ConfigPrinter";
  private String PREFS_AUTRE = "ConfigAutre";
  Boolean printer_mode_integrate = true;
  private NumberFormat nf;

  private PostData_Client client;
  private TextView TvClient, TvTel, TvLat, TvLog, TvAdresse, TvCodeClient, TvModeT, TvAchat, TvVerser, TvSolde;
  private ImageButton BtnCall, BtnVerser, BtnVente, BtnPosition;

  private  MediaPlayer mp;

  private EventBus bus = EventBus.getDefault();

  private static final int ACCES_FINE_LOCATION = 2;
  private Boolean checkPermission = false;

  private Intent intent_location;

  private Boolean position_yet = false;

  private ProgressDialog progress;

  private DATABASE controller;

  private RecyclerView recyclerView;
  RecyclerAdapter_Situation adapter;
  ArrayList<PostData_Carnet_c> carnet_cs;
  private String CODE_CLIENT;

  private PostData_Client client_print;
  private ArrayList<PostData_Carnet_c> carnet_c_print;




  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_detail);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Détails Client");
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));


    // client.client = getIntent().getStringExtra("CLIENT");
    CODE_CLIENT = getIntent().getStringExtra("CODE_CLIENT");
       /* client.tel = getIntent().getStringExtra("TEL");
        client.adresse = getIntent().getStringExtra("ADRESSE");
        client.latitude = getIntent().getDoubleExtra("LATITUDE", 0.00);
        client.longitude = getIntent().getDoubleExtra("LONGITUDE", 0.00);
        client.mode_tarif = getIntent().getStringExtra("MODE_TARIF");
        client.achat_montant = getIntent().getStringExtra("ACHAT");
        client.verser_montant = getIntent().getStringExtra("VERSER");
        client.solde_montant = getIntent().getStringExtra("SOLDE");
*/
    controller = new DATABASE(this);

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("##,##0.00");

    initViews();

    getClient();

    requestPermission();

    // Register as a subscriber
    bus.register(this);
    intent_location = new Intent(this, ServiceLocation.class);
    if(checkPermission){
      startService(intent_location);
    }

    //////////////////////////////////// PRINTING DECLARATION //////////////////////////////////

    IntentFilter pIntentFilter = new IntentFilter();
    pIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    pIntentFilter.addAction("android.intent.action.BATTERY_CAPACITY_EVENT");
    registerReceiver(printReceive, pIntentFilter);


    bt = new BluetoothSPP(this);

    if(!bt.isBluetoothAvailable()) {
      Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
      //finish();
    }

    bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {

      public void onDeviceDisconnected() {
        if (progressDialog_wait_connecte != null && !ActivityClientDetail.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityClientDetail.this, "Imprimente bluetooth non connecté ", Style.ALERT).show();
      }

      public void onDeviceConnectionFailed() {
        if (progressDialog_wait_connecte != null && !ActivityClientDetail.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityClientDetail.this, "Imprimente connection erroné ", Style.ALERT).show();
      }

      public void onDeviceConnected(String name, String address) {
        if (progressDialog_wait_connecte != null && !ActivityClientDetail.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityClientDetail.this, "Imprimente connecté à  "+ name, Style.CONFIRM).show();
        prepareBon_Bluetooth();
      }

    });

    //////////////////////////////// FIN PRINTING DECLARATION ////////////////////////////////////
  }

  protected void getClient(){
    client = new PostData_Client();
    client = controller.select_client_from_database(CODE_CLIENT);
    iniData(client);
  }

  @Override
  protected void onStart() {

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

    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setRecycle();
  }

  protected void initViews() {

    //TextView
    TvClient = (TextView) findViewById(R.id.client_name);
    TvTel = (TextView) findViewById(R.id.tel);
    TvLat = (TextView) findViewById(R.id.lat);
    TvLog = (TextView) findViewById(R.id.log);
    TvAdresse = (TextView) findViewById(R.id.adresse);
    TvCodeClient = (TextView) findViewById(R.id.code_client);
    TvModeT = (TextView) findViewById(R.id.mode_tarif);
    TvAchat = (TextView) findViewById(R.id.achat);
    TvVerser = (TextView) findViewById(R.id.verser);
    TvSolde = (TextView) findViewById(R.id.solde);

    //Button
    BtnCall = (ImageButton) findViewById(R.id.btnCall);
    BtnVerser = (ImageButton) findViewById(R.id.btnVerser);
    BtnVente = (ImageButton) findViewById(R.id.btnVente);
    BtnPosition = (ImageButton) findViewById(R.id.btnPosition);

    //RecycleViews
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapter_Situation(this, getItems());
    recyclerView.setAdapter(adapter);
  }

  public ArrayList<PostData_Carnet_c> getItems() {

    carnet_cs = new ArrayList<>();
    String querry = "SELECT Carnet_c.RECORDID, " +
            "Carnet_c.CODE_CLIENT, " +
            "Carnet_c.DATE_CARNET, " +
            "Client.CLIENT, " +
            "Client.LATITUDE, " +
            "Client.CODE_CLIENT, " +

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
            "WHERE Client.CODE_CLIENT = '"+ client.code_client +"' ";


    // querry = "SELECT * FROM Events";
    carnet_cs = controller.select_carnet_c_from_database(querry);

    return carnet_cs;
  }

  protected void iniData(PostData_Client client) {

    TvClient.setText(client.client);
    TvTel.setText(client.tel);
    if (client.latitude != null)
      TvLat.setText(client.latitude.toString());

    if (client.longitude != null)
      TvLog.setText(client.longitude.toString());

    if (client.adresse != null)
      TvAdresse.setText(" "+client.adresse);

    TvCodeClient.setText(client.code_client);

    if (client.mode_tarif != null)
      TvModeT.setText(client.mode_tarif);

    SharedPreferences prefs3 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
    if (prefs3.getBoolean("ACHATS_SHOW", false)) {
      TvAchat.setVisibility(View.VISIBLE);
    } else {
      TvAchat.setVisibility(View.GONE);
    }

    if (client.achat_montant != null)
      TvAchat.setText(" "+ new DecimalFormat("##,##0.00").format(Double.valueOf(client.achat_montant.toString())));

    if (client.verser_montant != null)

      TvVerser.setText(" "+   new DecimalFormat("##,##0.00").format(Double.valueOf(client.verser_montant.toString())));

    if (client.solde_montant != null)
      TvSolde.setText(" "+   new DecimalFormat("##,##0.00").format(Double.valueOf(client.solde_montant.toString())));
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnCall:
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + client.tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return;
        }
        startActivity(intent);
        break;
      case R.id.btnVerser:
        showFragmentVersement(false);
        break;
      case R.id.btnVente:

        break;
      case R.id.btnPosition:

        if(client.latitude != null){
          TvLat.setText(client.latitude.toString());
          position_yet = true;
          //start loading
          progress = new ProgressDialog(ActivityClientDetail.this);
          progress.setTitle("Position");
          progress.setMessage("Recherche position...");
          progress.setIndeterminate(true);
          progress.setCancelable(true);
          progress.show();
        }

        if(client.longitude != null)
          TvLog.setText(client.longitude.toString());

        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_details_client, menu);

    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.print_all){
      Print_versement();
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


  public void requestPermission(){
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECIEVE_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
        }
*/
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
      checkPermission = false;
    }else{
      checkPermission = true;
    }
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
        }
        */
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == ACCES_FINE_LOCATION){
      startService(new Intent(this, ServiceLocation.class));
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Subscribe
  public void onEvent(LocationEvent event){
    Log.e("TRACKKK", "Recieved location : " +  event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

    client.latitude = event.getLocationData().getLatitude();
    client.longitude = event.getLocationData().getLongitude();

    if(position_yet){
      if(client.latitude != null)
        TvLat.setText(client.latitude.toString());
      if(client.longitude != null)
        TvLog.setText(client.longitude.toString());

      controller.update_client(client.latitude, client.longitude, client.code_client);
      progress.dismiss();
      position_yet  =false;
    }
  }

  protected void showFragmentVersement(Boolean IfEdit){
    android.app.FragmentManager fm = getFragmentManager();
    DialogFragment dialog = new FragmentVersement(); // creating new object
    Bundle args = new Bundle();
    args.putString("CODE_CLIENT",  client.code_client);
    args.putBoolean("IS_EDIT",  IfEdit);
    dialog.setArguments(args);
    dialog.show(fm, "dialog");
  }


  public void Update_client_details(){
    getClient();
    setRecycle();
  }
  @Override
  public void onClick(View v, int position) {

  }



  /*@Override
  public void onLongClick(View v, final int position) {


    // PostData_Carnet_c ddd = (PostData_Carnet_c) v.get

    recyclerView.getChildItemId(v);

    final CharSequence[] items = {"Modifier", "Supprimer", "Imprimer" };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Séléctionner action");
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        switch (item){
          case 0:

            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Situation")
                    .setContentText("Voulez-vous vraiment modifier cette situation ?!")
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

                        showFragmentVersement(true);
                                       /* Intent editIntent = new Intent(ActivityClientDetail.this, ActivityEditSale.class);
                                        editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
                                        editIntent.putExtra("VALIDATED", "TRUE");
                                        startActivity(editIntent);
*/
                    //    sDialog.dismiss();
                   //   }
                //    })
                //    .show();



         ///   break;
       ///   case 1:
         //   new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.NORMAL_TYPE)
                 //   .setTitleText("Supprission")
                 //   .setContentText("Voulez-vous vraiment supprimer cette situation ?!")
                  //  .setCancelText("Anuuler")
                  //  .setConfirmText("Supprimer")
                  //  .showCancelButton(true)
                  //  .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                   //   @Override
                    //  public void onClick(SweetAlertDialog sDialog) {
                     //   sDialog.dismiss();
                    //  }
                   // })
                //    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                   //   @Override
                    //  public void onClick(SweetAlertDialog sDialog) {

                      //  controller.delete_versement(carnet_cs.get(position));
                      //  setRecycle();
                     //   getClient();

                      //  sDialog.dismiss();
                    //  }
                  //  })
                   // .show();

         //   break;
        //  case 2:
         //   Print_versement();
          //  break;
     //   }
     // }
  //  });
   // builder.show();
 // }*

  protected void Print_versement(){

    client_print = new PostData_Client();
    carnet_c_print = new ArrayList<>();

    client_print = controller.select_client_from_database(CODE_CLIENT);
    carnet_c_print  = controller.select_carnet_c_from_database("SELECT Carnet_c.RECORDID, " +
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
            "WHERE Carnet_c.CODE_CLIENT = '"+ client.code_client +"'");


    if(printer_mode_integrate){
      // prepareBon_IntegratePrinter();
    }else{
      if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
        // print
        Toast.makeText(ActivityClientDetail.this, " ImprEssion ..." , Toast.LENGTH_SHORT).show();
        prepareBon_Bluetooth();

      }else{
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
      }
    }
  }

  protected void prepareBon_Bluetooth(){

    handler_Bluetooth = new MyHandler_Bluetooth();
    if (LowBattery == true) {
      handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityClientDetail.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityClientDetail.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
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
            Toast.makeText(ActivityClientDetail.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Bluetooth().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityClientDetail.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityClientDetail.this);
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
          Toast.makeText(ActivityClientDetail.this, "Print Error!", Toast.LENGTH_LONG).show();
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
        byte[] left =  { 0x1b, 'a', 0x00 }; // center alignment

        // Underline
        // format[2] = ((byte)(0x80 | arrayOfByte1[2]));

        bt.send(format, true);

        SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if(prefs.getBoolean("ENTETE_SHOW", false)){
          bt.send(center, true);

          bt.send(prefs.getString("COMPANY_NAME", ""), true);
          bt.send(prefs.getString("ACTIVITY_NAME", ""), true);
          bt.send(prefs.getString("ADRESSE", ""), true);
          bt.send("-------------------------------------", true);

        }

        ////////////////////////////////
        //bt.send(center, true);
        bt.send(left, true);

        format[2] = ((byte) (0x20 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("    VERSEMENT CLIENT", true);
        //bt.send("------------------------------------------------", true);

        ////////////////////////////////
        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("CLIENT : " + client_print.client, true);
        bt.send("TEL : " + client.tel, true);

        bt.send("CODE CLIENT : " + client_print.code_client, true);
        bt.send("          ************", true);


        SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
        Date myDate = null;
        try {
          myDate = df_save.parse(carnet_c_print.get(0).carnet_date);

        } catch (ParseException e) {
          e.printStackTrace();
        }
        String formattedDate_Show = df_show.format(myDate);


        // bt.send(format_normal, true);
        //bt.send("CODE_DEPOT : " + final_bon1_prepared.code_depot, true);
        bt.send("DATE HEURE : " + formattedDate_Show + " " + carnet_c_print.get(0).carnet_heure, true);
        bt.send("------------------------------------------------", true);


        ///////////////////////

        ///////////////////////////

        format[2] = ((byte)(0x10 | arrayOfByte1[2]));
        bt.send(format, true);

        for(int b= 0; b<carnet_c_print.size(); b++){

          String observation = carnet_c_print.get(b).carnet_remarque;

          bt.send( "Montant versement : "+carnet_c_print.get(b).carnet_versement + " DA" , true);
          bt.send( "Observation :  "+observation , true);
          bt.send( "" , true);
        }

        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        bt.send(format, true);

        bt.send("------------------------------------------------", true);




/*
                String walk ="";
                Double current_solde = (Double.valueOf(final_bon1_prepared.solde_ancien) + Double.valueOf(final_bon1_prepared.montant_bon));
                String solde_actuel = "SOLDE ACTUEL: "  +  nf.format(current_solde)+ " DA";
                for(int a = 0; a < 48 - solde_actuel.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + solde_actuel, true);


                walk ="";
                String versement = "VERSEMENT : "  + nf.format(Double.valueOf(final_bon1_prepared.verser))+ " DA";
                for(int a = 0; a < 48 - versement.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + versement, true);


                walk ="";
                String reste = "NOUVEAU SOLDE : "  +  nf.format(Double.valueOf(final_bon1_prepared.reste)) + " DA";
                for(int a = 0; a < 48 - reste.length(); a++){
                    walk = walk + " ";
                }
                bt.send(walk + reste, true);

                bt.send("\n", true);
                */

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
    bus.unregister(this);
    if (progressDialog != null && !ActivityClientDetail.this.isFinishing()) {
      progressDialog.dismiss();
      progressDialog = null;
    }

    unregisterReceiver(printReceive);

//    try {
//            ThermalPrinter.stop();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    if(bt != null)
      bt.stopService();

    bus.unregister(this);
    stopService(intent_location);

    super.onDestroy();
  }

  ////////////////////////////////////////// START INTEGRETED PRINTER //////////////////////////////////


  private class MyHandler_Integrate extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case NOPAPER:
          noPaperDlg();
          break;
        case LOWBATTERY:
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityClientDetail.this);
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
            Toast.makeText(ActivityClientDetail.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Integrate().start();
          break;
        case PRINTPICTURE:
          //   new printPicture().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityClientDetail.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityClientDetail.this);
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
          Toast.makeText(ActivityClientDetail.this, "Print Error!", Toast.LENGTH_LONG).show();
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
        LowBattery = false;
//        if(SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()){
//          if (level * 5 <= scale) {
//            LowBattery = true;
//          } else {
//            LowBattery = false;
//          }
//        }else {
          if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
            if (level * 5 <= scale) {
              LowBattery = true;
            } else {
              LowBattery = false;
            }
          } else {
            LowBattery = false;
          }
        //}
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
    AlertDialog.Builder dlg = new AlertDialog.Builder(ActivityClientDetail.this);
    dlg.setTitle(getString(R.string.noPaper));
    dlg.setMessage(getString(R.string.noPaperNotice));
    dlg.setCancelable(false);
    dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        ThermalPrinter.stop(ActivityClientDetail.this);
      }
    });
    dlg.show();
  }


  //Class thread printing
  private class contentPrintThread_Integrate extends Thread {
    @Override
    public void run() {
      super.run();

      try {
        ThermalPrinter.start(ActivityClientDetail.this);
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
        ThermalPrinter.addString(" BON DE VERSEMENT\n");
        ThermalPrinter.addString("----------------");
        ThermalPrinter.printString();
        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);
        ////////////////////////////////
        ThermalPrinter.addString("CLIENT : " + client.client);
        ThermalPrinter.printString();
        ThermalPrinter.addString("CODE CLIENT : " + client.code_client);
        ThermalPrinter.printString();
        ThermalPrinter.addString("          ************");
        ThermalPrinter.printString();
        ThermalPrinter.addString("DATE HEURE : " + carnet_cs.get(0).carnet_date + " " + carnet_cs.get(0).carnet_heure);
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


        for(int b= 0; b<carnet_cs.size(); b++){

          ThermalPrinter.addString(carnet_cs.get(b).carnet_versement);
          ThermalPrinter.printString();

          String observation = carnet_cs.get(b).carnet_remarque;

          ThermalPrinter.addString("Montant versement : "+carnet_cs.get(b).carnet_versement + " DA");
          ThermalPrinter.printString();
          ThermalPrinter.addString("Observation : "+carnet_cs.get(b).carnet_remarque);
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




                /*


                walk ="";
                String Actuelsolde = "SOLDE ACTUEL: "  +  nf.format(Double.valueOf(final_bon1_prepared.solde_ancien) + Double.valueOf(final_bon1_prepared.montant_bon)) + " DA";
                for(int a = 0; a < 32 - Actuelsolde.length(); a++){
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + Actuelsolde);
                ThermalPrinter.printString();


                walk ="";
                String versement = "VERSEMENT : "  + nf.format(Double.valueOf(final_bon1_prepared.verser))+ " DA";
                for(int a = 0; a < 32 - versement.length(); a++){
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + versement);
                ThermalPrinter.printString();

                walk ="";
                String reste = "NOUVEAU SOLDE : "  +  nf.format(Double.valueOf(final_bon1_prepared.reste)) + " DA";
                for(int a = 0; a < 32 - reste.length(); a++){
                    walk = walk + " ";
                }
                ThermalPrinter.addString(walk + reste);
                ThermalPrinter.printString();
*/

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
        ThermalPrinter.stop(ActivityClientDetail.this);
      }
    }
  }

  ////////////////////////////////////////// FIN INTEGRETED PRINTER //////////////////////////////////

  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if(resultCode == Activity.RESULT_OK){
        bt.connect(data);
        progressDialog_wait_connecte = ProgressDialog.show(ActivityClientDetail.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
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
}

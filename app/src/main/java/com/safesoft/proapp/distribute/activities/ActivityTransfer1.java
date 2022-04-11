package com.safesoft.proapp.distribute.activities;

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
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.safesoft.proapp.distribute.adapters.RecyclerAdapterTransfert1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityTransfer1 extends AppCompatActivity implements RecyclerAdapterTransfert1.ItemClick ,RecyclerAdapterTransfert1.ItemLongClick{
  private final int NOPAPER = 3;
  private final int LOWBATTERY = 4;
  private final int PRINTVERSION = 5;
  private final int PRINTCONTENT = 9;
  private final int CANCELPROMPT = 10;
  private final int PRINTERR = 11;
  private final int OVERHEAT = 12;
  private final int PRINTPICTURE = 14;
  RecyclerView recyclerView;
  RecyclerAdapterTransfert1 adapter;
  ArrayList<PostData_Transfer1> transfert1s;
  ArrayList<PostData_Transfer1> test;

  ArrayList<PostData_Transfer2> transfers2;
  PostData_Transfer1 postData_transfer1;
  DATABASE controller;
  private String Result;
  private Boolean nopaper = false;
  private boolean LowBattery = false;
  private ProgressDialog progressDialog;
  private ProgressDialog progressDialog_wait_connecte;
  private ProgressDialog dialog;
  private MyHandler_Integrate handler_Integrate;
  private MyHandler_Bluetooth handler_Bluetooth;
  private  MediaPlayer mp;
  private BluetoothSPP bt;
  private String PREFS_PRINTER = "ConfigPrinter";
  Boolean printer_mode_integrate = true;

  private NumberFormat nf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_transfer1);

    controller = new DATABASE(this);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Bons Transferts");
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));
    initViews();

    setRecycle();
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
        if (progressDialog_wait_connecte != null && !ActivityTransfer1.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityTransfer1.this, "Imprimente bluetooth non connecté ", Style.ALERT).show();
      }

      public void onDeviceConnectionFailed() {
        if (progressDialog_wait_connecte != null && !ActivityTransfer1.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityTransfer1.this, "Imprimente connection erroné ", Style.ALERT).show();
      }

      public void onDeviceConnected(String name, String address) {
        if (progressDialog_wait_connecte != null && !ActivityTransfer1.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityTransfer1.this, "Imprimente connecté à  "+ name, Style.CONFIRM).show();
        prepareBon_Bluetooth();
      }

    });
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
          setup();
        }
      }
    }

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("##,##0.00");

    super.onStart();
  }
  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapterTransfert1(this, getItems());
    recyclerView.setAdapter(adapter);
  }

  public ArrayList<PostData_Transfer1> getItems() {
    transfert1s = new ArrayList<>();

    String querry = "SELECT * FROM Transfer1 ORDER BY DATE_BON DESC";
    // querry = "SELECT * FROM Events";
    transfert1s = controller.select_transfer1_from_database(querry);

    return transfert1s;
  }

  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);
    Intent intent = new Intent(ActivityTransfer1.this, ActivityTransfert2Detail.class);
    intent.putExtra("NUM_BON", transfert1s.get(position).num_bon);
    startActivity(intent);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
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

  @Override
  public void onLongClick(View v, final int position)
  {


    final CharSequence[] items = {"Imprimer" };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(R.drawable.selectiondialogs_default_item_icon);
    builder.setTitle("Choisissez une action");
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        switch (item){

          case 0:
            Print_bon(transfert1s.get(position).num_bon);
            break;
        }
      }
    });
    builder.show();



  }
  private class MyHandler_Integrate extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case NOPAPER:
          noPaperDlg();
          break;
        case LOWBATTERY:
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityTransfer1.this);
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
            Toast.makeText(ActivityTransfer1.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Integrate().start();
          break;
        case PRINTPICTURE:
          //   new printPicture().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityTransfer1.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityTransfer1.this);
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
          Toast.makeText(ActivityTransfer1.this, "Print Error!", Toast.LENGTH_LONG).show();
          break;
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
            Toast.makeText(ActivityTransfer1.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new contentPrintThread_Bluetooth().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityTransfer1.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityTransfer1.this);
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
          Toast.makeText(ActivityTransfer1.this, "Print Error!", Toast.LENGTH_LONG).show();
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
    AlertDialog.Builder dlg = new AlertDialog.Builder(ActivityTransfer1.this);
    dlg.setTitle(getString(R.string.noPaper));
    dlg.setMessage(getString(R.string.noPaperNotice));
    dlg.setCancelable(false);
    dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        ThermalPrinter.stop(ActivityTransfer1.this);
      }
    });
    dlg.show();
  }
  protected void prepareBon_Bluetooth(){

    handler_Bluetooth = new MyHandler_Bluetooth();
    if (LowBattery == true) {
      handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityTransfer1.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityTransfer1.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }
  protected void Print_bon(String num_bon){
    transfers2 = new ArrayList<>();
    transfert1s = new ArrayList<PostData_Transfer1>();


    transfert1s  = controller.select_transfer1_from_database("" +
            "SELECT " +
            "Transfer1.NUM_BON, " +
            "Transfer1.DATE_BON, " +

            "Transfer1.NOM_DEPOT_SOURCE, " +
            "Transfer1.NOM_DEPOT_DEST " +


            "FROM Transfer1 " +

            "WHERE Transfer1.NUM_BON = '" + num_bon + "'" );
    transfers2  = controller.select_transfer2_from_database("" +
            "SELECT " +

            "Transfer2.PRODUIT, " +
            "Transfer2.QTE " +

            "FROM Transfer2 " +

            "WHERE Transfer2.NUM_BON = '" + num_bon + "'" );


    if(printer_mode_integrate){
      prepareBon_IntegratePrinter();
    }else{
      if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
        // print
        Toast.makeText(ActivityTransfer1.this, " Impression ..." , Toast.LENGTH_SHORT).show();
        prepareBon_Bluetooth();

      }else{
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
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
        progressDialog = ProgressDialog.show(ActivityTransfer1.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityTransfer1.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }
  private class contentPrintThread_Integrate extends Thread {
    @Override
    public void run() {
      super.run();

      try {
        ThermalPrinter.start(ActivityTransfer1.this);
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
        ThermalPrinter.addString(" BON DE TRANSFERT\n");
        ThermalPrinter.addString("N°"+transfert1s.get(0).num_bon+"\n");
        ThermalPrinter.addString("----------------");
        ThermalPrinter.printString();
        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);
        ////////////////////////////////

        ThermalPrinter.addString("DEPOT SOURCE : " + transfert1s.get(0).nom_depot_s);
        ThermalPrinter.printString();
        ThermalPrinter.addString("          ************");
        ThermalPrinter.printString();
        ThermalPrinter.addString("DEPOT DESTINAION : " + transfert1s.get(0).nom_depot_d);
        ThermalPrinter.printString();
        ThermalPrinter.addString("DATE  : " + transfert1s.get(0).date_bon);
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
        ThermalPrinter.stop(ActivityTransfer1.this);
      }
    }
  }

  @Override
  protected void onDestroy() {
    // Unregister
    if (progressDialog != null && !ActivityTransfer1.this.isFinishing()) {
      progressDialog.dismiss();
      progressDialog = null;
    }
    unregisterReceiver(printReceive);
//    ThermalPrinter.stop();


    if(bt != null)
      bt.stopService();


    super.onDestroy();
  }

  public void setup() {
     /*   Button btnSend = (Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(etMessage.getText().length() != 0) {
                    bt.send(etMessage.getText().toString(), true);
                    etMessage.setText("");
                }
            }
        });
        */
  }
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
        bt.send("BON DE TRANSFERT", true);
        bt.send("N°"+transfert1s.get(0).num_bon, true);
        //bt.send("------------------------------------------------", true);

//                SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
//                SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
//                Date myDate = null;
//                try {
//                    myDate = df_save.parse(transfert1s.get(0).date_bon);
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                String formattedDate_Show = df_show.format(myDate);

        ////////////////////////////////
        format[2] = ((byte) (0x1 | arrayOfByte1[2]));
        bt.send(left, true);
        bt.send("DEPOT SOURCE : " + transfert1s.get(0).nom_depot_s, true);
        bt.send("DEPOT DESTINATION : " + transfert1s.get(0).nom_depot_d, true);
        bt.send("                        ************", true);
        bt.send("PRODUIT"+"                        QUANTITE",true);
        bt.send(transfers2.get(0).produit+"                          "+transfers2.get(0).qte,true);
        // bt.send("QUANTITE : " + transfers2.get(0).qte, true);

        // bt.send(format_normal, true);
//                bt.send("DATE  : " + formattedDate_Show + " " + transfert1s.get(0).date_bon, true);
//                bt.send("------------------------------------------------", true);






        format[2] = ((byte)(0x20 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("                  MERCI", true);

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
  // Preparation de Bon pour l'imprimente intégrer
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if(resultCode == Activity.RESULT_OK){
        bt.connect(data);
        progressDialog_wait_connecte = ProgressDialog.show(ActivityTransfer1.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
      }
    } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
      if(resultCode == Activity.RESULT_OK) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_ANDROID);
        setup();
      } else {
        Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

}

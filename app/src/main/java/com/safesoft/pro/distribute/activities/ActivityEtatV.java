package com.safesoft.pro.distribute.activities;

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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.pro.distribute.adapters.model.MyDataObject;
import com.safesoft.pro.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.pro.distribute.adapters.viewholder.advanced.AdvancedDataViewHolder;
import com.safesoft.pro.distribute.adapters.viewholder.advanced.AdvancedDataViewHolderObjectif;
import com.safesoft.pro.distribute.adapters.viewholder.advanced.HeaderViewHolder;
import com.safesoft.pro.distribute.adapters.viewholder.advanced.HeaderViewHolderTotal;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.eventsClasses.EtatZSelection_Event;
import com.safesoft.pro.distribute.fragments.FragmentSelectUser;
import com.safesoft.pro.distribute.postData.PostData_Client;
import com.safesoft.pro.distribute.postData.PostData_Etatv;
import com.safesoft.pro.distribute.R;
import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.printer.ThermalPrinter;
import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;
import com.victor.loading.book.BookLoading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inloop.simplerecycleradapter.ItemClickListener;
import eu.inloop.simplerecycleradapter.ItemLongClickListener;
import eu.inloop.simplerecycleradapter.SettableViewHolder;
import eu.inloop.simplerecycleradapter.SimpleRecyclerAdapter;

public class ActivityEtatV extends AppCompatActivity implements ItemClickListener<WrappedMyDataObject>, ItemLongClickListener<WrappedMyDataObject> {

  ////////////////////////////////////////
  private final int NOPAPER = 3;
  private final int LOWBATTERY = 4;
  private final int PRINTVERSION = 5;
  private final int PRINTBARCODE = 6;
  private final int PRINTQRCODE = 7;
  private final int PRINTPAPERWALK = 8;
  private final int PRINTCONTENT = 9;
  private final int CANCELPROMPT = 10;
  private final int PRINTERR = 11;
  private final int OVERHEAT = 12;
  private final int MAKER = 13;
  private final int PRINTPICTURE = 14;
  private final int EXECUTECOMMAND = 15;

  private String Result;
  private Boolean nopaper = false;
  private boolean LowBattery = false;
  private ProgressDialog progressDialog;
  private ProgressDialog progressDialog_wait_connecte;
  private final static int MAX_LEFT_DISTANCE = 255;
  private ProgressDialog dialog;
  private MyHandler_Integrate handler_Integrate;
  private MyHandler_Bluetooth handler_Bluetooth;
  private String PREFS_PRINTER = "ConfigPrinter";

  private RecyclerView mRecyclerView;
  private SimpleRecyclerAdapter<WrappedMyDataObject> mRecyclerAdapter;
  private RelativeLayout relative_error;
  private LinearLayout tite_session;
  private ImageView retry;
  private EtatZSelection_Event event_selection;
  private BookLoading bookloading;
  private RelativeLayout empty_data;
  private TextView debut, fin,user;
  private ViewGroup mainView;
  String clientt;
  private BluetoothSPP bt;
  DATABASE controller;
  private MediaPlayer mp;
  Boolean printer_mode_integrate = true;

  private Thread thread;
  private Handler handler;

  private ArrayList<PostData_Etatv> result_etatzg;
  private EventBus bus = EventBus.getDefault();
  private String from_d;
  private String client;
  private String c_client;


  private String to_d;
  private String from_h;
  private String to_h;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_etat_v);

    // Register as a subscriber
    bus.register(this);

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
        if (progressDialog_wait_connecte != null && !ActivityEtatV.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityEtatV.this, "Imprimente bluetooth non connecté ", Style.ALERT).show();
      }

      public void onDeviceConnectionFailed() {
        if (progressDialog_wait_connecte != null && !ActivityEtatV.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityEtatV.this, "Imprimente connection erroné ", Style.ALERT).show();
      }

      public void onDeviceConnected(String name, String address) {
        if (progressDialog_wait_connecte != null && !ActivityEtatV.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityEtatV.this, "Imprimente connecté à  "+ name, Style.CONFIRM).show();
        prepareBon_Bluetooth();
      }

    });
  }

  @Override
  protected void onStart() {

    initViews();

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Statistique des ventes");
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));
    empty_data.setVisibility(View.VISIBLE);

    retry =(ImageView) findViewById(R.id.retry);
    retry.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        get_etatzg();
        start_select_etatz(1);
      }
    });
    controller  = new DATABASE(this);
    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setHasFixedSize(true);

    initAdapter();

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

    super.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    result_etatzg =new ArrayList<>();

    //get_etatzg();
  }


  private void initViews() {

    //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    bookloading = (BookLoading) findViewById(R.id.bookloading);
    relative_error = (RelativeLayout) findViewById(R.id.relative_error);
    mainView = (ViewGroup) findViewById(R.id.lempty_data);
    empty_data = (RelativeLayout) findViewById(R.id.lempty_data);
    tite_session = (LinearLayout) findViewById(R.id.tite_session);

    //TextView
    debut = (TextView) findViewById(R.id.debut);
    fin = (TextView) findViewById(R.id.fin);
    user = (TextView) findViewById(R.id.user);
  }

  public void show_select_etatz(){
    FragmentSelectUser dialogFragment = new FragmentSelectUser();
    dialogFragment.show(getSupportFragmentManager(), "Sample Fragment");
  }


  @Subscribe
  public void getEventSelection(EtatZSelection_Event event){

    event_selection = event;
    debut.setText("De "+ event_selection.getDate_f());
    fin.setText("Vers "+ event_selection.getDate_t());
    if(event.getUser().toString().equals("%")){
      user.setText("Tous");
    }else{
      user.setText(" "+ event_selection.getUser());
    }
    client = event_selection.getUser();
    c_client = event_selection.getCode_user();
    SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");

    SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");

    from_d = event_selection.getDate_f().substring(0, event_selection.getDate_f().indexOf(" "));

    Date myDate = null;
    try {
      myDate = df_save.parse(from_d);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    from_d = df_show.format(myDate);


    to_d = event_selection.getDate_t().substring(0, event_selection.getDate_t().indexOf(" "));;
    Date myDate2 = null;
    try {
      myDate2 = df_save.parse(to_d);

    } catch (ParseException e) {
      e.printStackTrace();
    }
    to_d = df_show.format(myDate2);

    from_h =  event_selection.getDate_f().substring(event_selection.getDate_f().indexOf(" ") + 1, event_selection.getDate_f().length());
    to_h = event_selection.getDate_t().substring(event_selection.getDate_t().indexOf(" ") + 1, event_selection.getDate_t().length());
    get_etatzg();
  }

  public void startProgress(){
    bookloading.start();
  }

  public void stopProgress(){
    bookloading.stop();
  }

  public void stopAndError(){
    bookloading.stop();
    //with erreur image
  }
  @SuppressWarnings("unchecked")
  private void initAdapter() {
    mRecyclerAdapter = new SimpleRecyclerAdapter<>(this, new SimpleRecyclerAdapter.CreateViewHolder<WrappedMyDataObject>() {
      @NonNull
      @Override
      protected SettableViewHolder<WrappedMyDataObject> onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
          case WrappedMyDataObject.ITEM_TYPE_NORMAL:
            return new AdvancedDataViewHolder(ActivityEtatV.this, R.layout.item_mydata, parent);
          case WrappedMyDataObject.ITEM_TYPE_HEADER:
            return new HeaderViewHolder(ActivityEtatV.this, R.layout.item_header, parent);
          case WrappedMyDataObject.ITEM_TYPE_HEADER_TOTAL:
            return new HeaderViewHolderTotal(ActivityEtatV.this, R.layout.item_header_total, parent);
          case WrappedMyDataObject.ITEM_TYPE_OBJECTIF:
            return new AdvancedDataViewHolderObjectif(ActivityEtatV.this, R.layout.item_mydata_objectif, parent);
          default:
            throw new AssertionError("Wrong view type");
        }
      }

      @Override
      protected int getItemViewType(int position) {
        return mRecyclerAdapter.getItem(position).getType();
      }
    });
    mRecyclerAdapter.setLongClickListener(this);
    mRecyclerView.setAdapter(mRecyclerAdapter);
  }

  public void initData(ArrayList<PostData_Etatv> result_etat_z) {

    //here we reset the parents and the children
    mRecyclerAdapter.clear();
    mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItem("Produit", "QTE", "TOT"));

    //  mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItem(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant));

    for (int i = 0; i < result_etat_z.size(); i++) {
      if (result_etat_z.get(i).code_parent.toString().equals("1")) {
        mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItem(new MyDataObject(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant)));
      } else if (result_etat_z.get(i).code_parent.toString().equals("-6")) {
        mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItemTotal("Conclusion Total : "));
        for (int k = i; k < result_etat_z.size() - 1; k++) {
          mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItem(new MyDataObject(result_etat_z.get(k).produit, result_etat_z.get(k).quantite, result_etat_z.get(k).montant)));
          i = k;
        }
      } else if (result_etat_z.get(i).code_parent.toString().equals("-8")) {
        mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItemTotal("Objectif : "));
        mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItemObjectif(new MyDataObject(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant)));
      }
    }
    mRecyclerAdapter.notifyDataSetChanged();
  }


  @Override
  public void onItemClick(@NonNull WrappedMyDataObject item, @NonNull SettableViewHolder<WrappedMyDataObject> viewHolder, @NonNull View view) {
    if (item.getType() == WrappedMyDataObject.ITEM_TYPE_NORMAL) {
      MyDataObject dataObject = item.getDataObject();
      int itemPos = viewHolder.getAdapterPosition();

      switch (view.getId()) {
              /*  case R.id.btn_more:
                 //   setTitle("Action clicked on item: " + dataObject.getTitle());
                    break;
                case R.id.btn_remove:
                    mRecyclerAdapter.removeItem(item, true);
                    break;
                case R.id.btn_move_up:
                    mRecyclerAdapter.swapItem(itemPos, Math.max(0, itemPos - 1), true);
                    break;
                case R.id.btn_move_down:
                    int maxIndex = mRecyclerAdapter.getItemCount() - 1;
                    mRecyclerAdapter.swapItem(itemPos, Math.min(maxIndex, itemPos + 1), true);
                    break;*/
        default:
          //Actual item click
          // setTitle("Last clicked item: " + dataObject.getTitle());
          break;
      }
    }
  }

  @Override
  public boolean onItemLongClick(@NonNull WrappedMyDataObject item, @NonNull SettableViewHolder<WrappedMyDataObject> viewHolder, @NonNull View view) {
    if (item.getType() == WrappedMyDataObject.ITEM_TYPE_NORMAL) {
      MyDataObject dataObject = item.getDataObject();

      if (view.getId() == -1) {
        //setTitle("Action LONG clicked on item: " + dataObject.getTitle());
        return true;
      }

    }
    return false;
  }

  public void start_select_etatz(Integer i){
    empty_data.setVisibility(View.GONE);
    switch (i){
      case 1:
        mRecyclerView.setVisibility(View.GONE);
        relative_error.setVisibility(View.GONE);
        bookloading.setVisibility(View.VISIBLE);
        tite_session.setVisibility(View.GONE);
        startProgress();
        break;
      case 2:
        bookloading.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        tite_session.setVisibility(View.VISIBLE);
        stopProgress();
        break;
      case 3:
        mRecyclerView.setVisibility(View.GONE);
        bookloading.setVisibility(View.GONE);
        relative_error.setVisibility(View.VISIBLE);
        tite_session.setVisibility(View.VISIBLE);
        //visible erreur image
        stopAndError();
        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_etatv, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        break;
      case R.id.synchroniser:

        show_select_etatz();

        break;
      case R.id.print:
        if(result_etatzg.size() > 0){
          if(printer_mode_integrate){
            prepareBon_IntegratePrinter();
          }else{
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
              // print
              Toast.makeText(ActivityEtatV.this, " Impression ..." , Toast.LENGTH_SHORT).show();
              prepareBon_Bluetooth();

            }else{
              bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
              Intent intent = new Intent(getApplicationContext(), DeviceList.class);
              startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
            }
          }
        }

        break;
      default:
        break;
    }

    return super.onOptionsItemSelected(item);
  }


  protected void get_etatzg(){
    //===========
    handler = new Handler() {
      public void handleMessage(Message msg) {
        try {
          //=====================
          switch (msg.what) {
            case 0:
              start_select_etatz(1);
              break;
            case 1:
              start_select_etatz(2);
              //here we reset the parents and the children
              mRecyclerAdapter.clear();
              if(result_etatzg.size() > 7){
                initData(result_etatzg);
              }
              break;
            case 2:
              new SweetAlertDialog(ActivityEtatV.this, SweetAlertDialog.ERROR_TYPE)
                      .setTitleText("Oops...")
                      .setContentText("Vous avez un problem au niveau de la requette SQL! Contanctez le fournisseur")
                      .show();
              start_select_etatz(3);
              break;
          }

        } catch (Exception ex) {

        }
      }
    };

    comunication();
  }

  public void comunication(){

    thread = new Thread(){
      public void run() {
        try {
          handler.sendEmptyMessage(0);
          int flag;

          //success
          if(result_etatzg != null ) {
            result_etatzg.clear();
          }
if(c_client == null)
{
  flag = getEtatGlobal(from_d,  to_d,  from_h,  to_h);


}else
{
  flag = getEtatzgs(c_client,from_d,  to_d,  from_h,  to_h);

}


          if (flag == 0) {
            //failed
            handler.sendEmptyMessage(3);
          } else if (flag == 1) {
            handler.sendEmptyMessage(1);
          } else if (flag == 2) {
            //problem
            handler.sendEmptyMessage(2);
          }

        } catch (Exception e) {
          e.printStackTrace();
          handler.sendEmptyMessage(3);
        }
      };
    };

    thread.start();
  }

  public int getEtatzgs(String c_client,String from_d, String to_d, String from_h, String to_h){
    int flag = 0;
    try {

      result_etatzg =  controller.select_etatv_from_database(c_client,from_d,  to_d,  from_h,  to_h);
      flag = 1;
    }catch (Exception sqle){
      Log.v("TRACKKK", sqle.getMessage());
      flag = 2;
    }
    return flag;
  }
  public int getEtatGlobal(String from_d, String to_d, String from_h, String to_h){
    int flag = 0;
    try {

      result_etatzg =  controller.select_etatv_global_from_database(from_d,  to_d,  from_h,  to_h);
      flag = 1;
    }catch (Exception sqle){
      Log.v("TRACKKK", sqle.getMessage());
      flag = 2;
    }
    return flag;
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
  protected void onDestroy() {
    // Unregister
    if (progressDialog != null && !ActivityEtatV.this.isFinishing()) {
      progressDialog.dismiss();
      progressDialog = null;
    }
    unregisterReceiver(printReceive);
    ThermalPrinter.stop();

    if(bt != null)
      bt.stopService();

    bus.unregister(this);
    super.onDestroy();
  }


  // Preparation de Bon pour l'imprimente intégrer
  protected void prepareBon_IntegratePrinter(){

    handler_Integrate = new ActivityEtatV.MyHandler_Integrate();
    if (LowBattery == true) {
      handler_Integrate.sendMessage(handler_Integrate.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityEtatV.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Integrate.sendMessage(handler_Integrate.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityEtatV.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }


  protected void prepareBon_Bluetooth(){

    handler_Bluetooth = new ActivityEtatV.MyHandler_Bluetooth();
    if (LowBattery == true) {
      handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(LOWBATTERY, 1, 0, null));
    } else {
      if (!nopaper) {
        progressDialog = ProgressDialog.show(ActivityEtatV.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
        handler_Bluetooth.sendMessage(handler_Bluetooth.obtainMessage(PRINTCONTENT, 1, 0, null));
      } else {
        Toast.makeText(ActivityEtatV.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
      }
    }
  }
  //Class thread printing
  private class contentPrintThread_Integrate extends Thread {
    @Override
    public void run() {
      super.run();

      try {
        ThermalPrinter.start(ActivityEtatV.this);
        ThermalPrinter.reset();
        ThermalPrinter.setAlgin(ThermalPrinter.ALGIN_LEFT);

        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);



        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.enlargeFontSize(2, 2);
        ThermalPrinter.setGray(12);

        ////////////////////////////////
        //  ThermalPrinter.setBold(true);
        ThermalPrinter.addString(" Statistique des ventes\n");
        ThermalPrinter.addString("----------------");
        ThermalPrinter.printString();
        /////////////////////////////
        ThermalPrinter.reset();
        ThermalPrinter.setLeftIndent(0);
        ThermalPrinter.setLineSpace(28);
        ThermalPrinter.setFontSize(2);
        ThermalPrinter.setGray(12);
        ////////////////////////////////
        ThermalPrinter.addString("CLIENT : " + event_selection.getUser());
        ThermalPrinter.printString();
        ThermalPrinter.addString("DE : " + event_selection.getDate_f());
        ThermalPrinter.printString();
        ThermalPrinter.addString("VERS : " + event_selection.getDate_t());
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

        for(int b= 0; b<result_etatzg.size(); b++){
          if(result_etatzg.get(b).code_parent.equals("1")){
            if(result_etatzg.get(b).produit.length()>48){
              result_etatzg.get(b).produit = result_etatzg.get(b).produit.substring(0, 47);
            }
            ThermalPrinter.addString(result_etatzg.get(b).produit);
            ThermalPrinter.printString();

            String quantite = result_etatzg.get(b).quantite;
            String prix_u = new DecimalFormat("##,##0.00").format(Double.valueOf(result_etatzg.get(b).pv_ht));
            String total_produit = new DecimalFormat("##,##0.00").format(Double.valueOf(Double.valueOf(result_etatzg.get(b).montant)));
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

        for(int s = 0; s < result_etatzg.size(); s ++){

          if(result_etatzg.get(s).code_parent.equals("-6")) {

            String walk = "";

            if(result_etatzg.get(s).quantite != null){
              String total_ht = "TOTAL QTE : " + new DecimalFormat("##,##0").format(Double.valueOf(result_etatzg.get(s).quantite));
              for (int a = 0; a < 32 - total_ht.length(); a++) {
                walk = walk + " ";
              }
              ThermalPrinter.addString(walk + total_ht);
              ThermalPrinter.printString();
            }

            if(result_etatzg.get(s).montant != null){
              walk = "";
              String tva = "MONTANT TOTAL : " + new DecimalFormat("##,##0.00").format(Double.valueOf(result_etatzg.get(s).montant)) + " DA";
              for (int a = 0; a < 32 - tva.length(); a++) {
                walk = walk + " ";
              }
              ThermalPrinter.addString(walk + tva);
              ThermalPrinter.printString();
            }
          }
        }

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
        ThermalPrinter.stop(ActivityEtatV.this);
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
          AlertDialog.Builder alertDialog = new AlertDialog.Builder(ActivityEtatV.this);
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
            Toast.makeText(ActivityEtatV.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTBARCODE:
          // new barcodePrintThread().start();
          break;
        case PRINTQRCODE:
          //  new qrcodePrintThread().start();
          break;
        case PRINTPAPERWALK:
          //   new paperWalkPrintThread().start();
          break;
        case PRINTCONTENT:
          new contentPrintThread_Integrate().start();
          break;
        case MAKER:
          //   new MakerThread().start();
          break;
        case PRINTPICTURE:
          //   new printPicture().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityEtatV.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case EXECUTECOMMAND:
          //  new executeCommand().start();
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityEtatV.this);
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
          Toast.makeText(ActivityEtatV.this, "Print Error!", Toast.LENGTH_LONG).show();
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
    AlertDialog.Builder dlg = new AlertDialog.Builder(ActivityEtatV.this);
    dlg.setTitle(getString(R.string.noPaper));
    dlg.setMessage(getString(R.string.noPaperNotice));
    dlg.setCancelable(false);
    dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        ThermalPrinter.stop(ActivityEtatV.this);
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
            Toast.makeText(ActivityEtatV.this, "Operation failed", Toast.LENGTH_LONG).show();
          }
          break;
        case PRINTCONTENT:
          new ActivityEtatV.contentPrintThread_Bluetooth().start();
          break;
        case CANCELPROMPT:
          if (progressDialog != null && !ActivityEtatV.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
          }
          break;
        case OVERHEAT:
          AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(ActivityEtatV.this);
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
          Toast.makeText(ActivityEtatV.this, "Print Error!", Toast.LENGTH_LONG).show();
          break;
      }
    }
  }



  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if(resultCode == Activity.RESULT_OK){
        bt.connect(data);
        progressDialog_wait_connecte = ProgressDialog.show(ActivityEtatV.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
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

  //Class thread printing
  private class contentPrintThread_Bluetooth extends Thread {
    @Override
    public void run() {
      super.run();

      try {


        byte[] arrayOfByte1 = { 27, 33, 0 };
        byte[] format = { 27, 33, 0 };

        // byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment

        // Underline
        // format[2] = ((byte)(0x80 | arrayOfByte1[2]));

        bt.send(format, true);

        SharedPreferences prefs = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        if(prefs.getBoolean("ENTETE_SHOW", false)){

          bt.send(prefs.getString("COMPANY_NAME", ""), true);
          bt.send(prefs.getString("ACTIVITY_NAME", ""), true);
          bt.send(prefs.getString("ADRESSE", ""), true);
          bt.send("--------------------------------", true);

        }

        ////////////////////////////////
        //bt.send(center, true);
        format[2] = ((byte) (0x20 | arrayOfByte1[2]));
        bt.send(format, true);

        ////////////////////////////////
        //  ThermalPrinter.setBold(true);
        bt.send(" Statistique des ventes", true);
        bt.send("------------------------", true);


        ////////////////////////////////
        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("CLIENT : " + event_selection.getUser(), true);
        bt.send("DE : " + event_selection.getDate_f(), true);
        bt.send("VERS : " + event_selection.getDate_t(), true);
        bt.send("--------------------------------", true);

        ///////////////////////////

        format[2] = ((byte)(0x1 | arrayOfByte1[2]));
        bt.send(format, true);

        for(int b= 0; b<result_etatzg.size(); b++){
          if(result_etatzg.get(b).code_parent.equals("1")){
            if(result_etatzg.get(b).produit.length()>48){
              result_etatzg.get(b).produit = result_etatzg.get(b).produit.substring(0, 47);
            }


            String quantite = result_etatzg.get(b).quantite;
            String prix_u = new DecimalFormat("##,##0.00").format(Double.valueOf(result_etatzg.get(b).pv_ht));
            String total_produit = new DecimalFormat("##,##0.00").format(Double.valueOf(Double.valueOf(result_etatzg.get(b).montant)));

            bt.send(result_etatzg.get(b).produit + "  / " + quantite +" X "+  prix_u +" = " + total_produit, true);
          }
        }

        format[2] = ((byte)(0x0 | arrayOfByte1[2]));
        bt.send(format, true);
        bt.send("------------------------------------------------", true);

        for(int s = 0; s < result_etatzg.size(); s ++){

          if(result_etatzg.get(s).code_parent.equals("-6")) {

            String walk = "";

            if(result_etatzg.get(s).quantite != null){
              String total_ht = "TOTAL QTE : " + new DecimalFormat("##,##0").format(Double.valueOf(result_etatzg.get(s).quantite));
              for (int a = 0; a < 48 - total_ht.length(); a++) {
                walk = walk + " ";
              }
              bt.send(walk + total_ht, true);
            }

            if(result_etatzg.get(s).montant != null){
              walk = "";
              String tva = "MONTANT TOTAL : " + new DecimalFormat("##,##0.00").format(Double.valueOf(result_etatzg.get(s).montant)) + " DA";
              for (int a = 0; a < 48 - tva.length(); a++) {
                walk = walk + " ";
              }
              bt.send(walk + tva, true);
            }
          }
        }

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
}

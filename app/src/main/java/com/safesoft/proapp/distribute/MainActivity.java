package com.safesoft.proapp.distribute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.Toast;

import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.observer.PrinterObserver;
import com.rt.printerlibrary.observer.PrinterObserverManager;
import com.rt.printerlibrary.printer.RTPrinter;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.fragments.FragmentMain;
import com.safesoft.proapp.distribute.utils.BaseEnum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements PrinterObserver {
  Fragment objFrgment;
  FragmentManager fragmentManager;
  private RTPrinter rtPrinter = null;
  private PrinterFactory printerFactory;
  final String PREFS = "ALL_PREFS";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbardrawer);
    toolbar.setSubtitle("Version : 30.11.23");
    setSupportActionBar(toolbar);

    objFrgment = new FragmentMain();
    fragmentManager = getSupportFragmentManager();
    if(objFrgment != null)
    {
      fragmentManager.beginTransaction().replace(R.id.drawer_layoutt,objFrgment).commit();
    }

    SharedPreferences pref = getSharedPreferences(PREFS, 0);
    if(pref.getString("date_time", null) == null){
      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      Date currentDateTime = Calendar.getInstance().getTime();
      String currentDateTimeString = sdf.format(currentDateTime);
      SharedPreferences.Editor editor = pref.edit();
      editor.putString("date_time", currentDateTimeString);
      editor.apply();
    }

  }


  @Override
  protected void onStart() {
    super.onStart();

    BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_TSC);
    printerFactory = new ThermalPrinterFactory();
    rtPrinter = printerFactory.create();

    //add observer listen for connexion(bluetooth, wifi, usb)
    PrinterObserverManager.getInstance().add(this);

  }


  public void startActivity(Class clss, int request)
  {
    Intent intent = new Intent(this, clss);
    intent.putExtra("SOURCE_EXPORT", "NOTEXPORTED");
    startActivityForResult(intent, request);
  }

  @Override
  public void printerObserverCallback(PrinterInterface printerInterface, int state) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        switch (state) {
          case CommonEnum.CONNECT_STATE_SUCCESS -> {
            Toast.makeText(MainActivity.this, printerInterface.getConfigObject().toString() + " Connecté", Toast.LENGTH_SHORT).show();
            BaseApplication.getInstance().setIsConnected(true);
            rtPrinter.setPrinterInterface(printerInterface);
            BaseApplication.getInstance().setRtPrinter(rtPrinter);
          }
          case CommonEnum.CONNECT_STATE_INTERRUPTED -> {
            Toast.makeText(MainActivity.this, getString(R.string._main_disconnect), Toast.LENGTH_SHORT).show();
            BaseApplication.getInstance().setIsConnected(false);
          }
          //BaseApplication.getInstance().setRtPrinter(null);
          default -> {
          }
        }
      }
    });
  }

  @Override
  public void printerReadMsgCallback(PrinterInterface printerInterface, byte[] bytes) {
    //byte[] bbytes = bytes;
    //PrinterInterface printerInterfacebb = printerInterface;
    //String a = "dddd";
  }
}

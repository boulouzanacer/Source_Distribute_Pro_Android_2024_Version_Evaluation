package com.safesoft.proapp.distribute;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.rt.printerlibrary.connect.PrinterInterface;
import com.rt.printerlibrary.enumerate.CommonEnum;
import com.rt.printerlibrary.factory.printer.PrinterFactory;
import com.rt.printerlibrary.factory.printer.ThermalPrinterFactory;
import com.rt.printerlibrary.observer.PrinterObserver;
import com.rt.printerlibrary.observer.PrinterObserverManager;
import com.rt.printerlibrary.printer.RTPrinter;
import com.safesoft.proapp.distribute.activities.ActivityClients;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.ActivityInventaireAchat;
import com.safesoft.proapp.distribute.activities.ActivityProduits;
import com.safesoft.proapp.distribute.activities.ActivitySetting;
import com.safesoft.proapp.distribute.activities.ActivityTransfer1;
import com.safesoft.proapp.distribute.activities.commande.ActivityOrders;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.utils.BaseEnum;

import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_BON_RECEPTION;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_CLIENTS;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_IMPORT_EXPORT;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_INVENTAIRE_ACHAT;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_ORDER;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_PARAMETRES;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_PRODUITS;
import static com.safesoft.proapp.distribute.mainActivity_Distribute.REQUEST_ACTIVITY_VENTES;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PrinterObserver {
  Fragment objFrgment;
  FragmentManager fragmentManager;
  private  MediaPlayer mp;

  private RTPrinter rtPrinter = null;
  private PrinterFactory printerFactory;
  private DATABASE controller;
  private String MY_PREF_NAME = "T";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbardrawer);
    setSupportActionBar(toolbar);



    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
    objFrgment = new mainActivity_Distribute();
    fragmentManager = getSupportFragmentManager();
    if(objFrgment != null)
    {
      fragmentManager.beginTransaction().replace(R.id.drawer_layoutt,objFrgment).commit();
      drawer.closeDrawer(GravityCompat.START);

    }

    SharedPreferences pref = getSharedPreferences(MY_PREF_NAME, 0);
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

    BaseApplication.instance.setCurrentCmdType(BaseEnum.CMD_ESC);
    printerFactory = new ThermalPrinterFactory();
    rtPrinter = printerFactory.create();

    //add observer listen for connexion(bluetooth, wifi, usb)
    PrinterObserverManager.getInstance().add(this);

  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {

    //((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(800);

    mp = MediaPlayer.create(this, R.raw.pellet);
    mp.start();
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.vente) {
      startActivity(ActivitySales.class, REQUEST_ACTIVITY_VENTES);
    } else if (id == R.id.transferts) {
      startActivity(ActivityTransfer1.class, REQUEST_ACTIVITY_BON_RECEPTION);

    } else if (id == R.id.produits) {
      startActivity(ActivityProduits.class, REQUEST_ACTIVITY_PRODUITS);

    } else if (id == R.id.clients) {
      startActivity(ActivityClients.class, REQUEST_ACTIVITY_CLIENTS);

    } else if (id == R.id.import_export) {
      startActivity(ActivityImportsExport.class, REQUEST_ACTIVITY_IMPORT_EXPORT);

    } else if (id == R.id.commande) {
      startActivity(ActivityOrders.class, REQUEST_ACTIVITY_ORDER);
      //Toast.makeText(this, "Cette rubrique est en cours de dévelopement !", Toast.LENGTH_LONG ).show();

    } else if (id == R.id.inventaire) {
      startActivity(ActivityInventaireAchat.class, REQUEST_ACTIVITY_INVENTAIRE_ACHAT);

    } else if (id == R.id.parametres)
    {
     // startActivity(ActivityLogin.class, REQUEST_ACTIVITY_PARAMETRES);
      startActivity(ActivitySetting.class, REQUEST_ACTIVITY_PARAMETRES);
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void startActivity(Class clss, int request)
  {
    Intent intent = new Intent(this, clss);
    startActivityForResult(intent, request);
  }

  @Override
  public void printerObserverCallback(PrinterInterface printerInterface, int state) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {

        switch (state) {

          case CommonEnum.CONNECT_STATE_SUCCESS:
            Toast.makeText(MainActivity.this, printerInterface.getConfigObject().toString() + " Connecté", Toast.LENGTH_SHORT).show();
            BaseApplication.getInstance().setIsConnected(true);
            rtPrinter.setPrinterInterface(printerInterface);
            BaseApplication.getInstance().setRtPrinter(rtPrinter);
            break;

          case CommonEnum.CONNECT_STATE_INTERRUPTED:

            Toast.makeText(MainActivity.this, getString(R.string._main_disconnect), Toast.LENGTH_SHORT).show();
            BaseApplication.getInstance().setIsConnected(false);
            //BaseApplication.getInstance().setRtPrinter(null);
            break;

          default:
            break;
        }
      }
    });
  }

  @Override
  public void printerReadMsgCallback(PrinterInterface printerInterface, byte[] bytes) {

  }
}

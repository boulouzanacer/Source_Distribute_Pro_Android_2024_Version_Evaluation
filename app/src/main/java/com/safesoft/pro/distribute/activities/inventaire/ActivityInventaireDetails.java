package com.safesoft.pro.distribute.activities.inventaire;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.adapters.ListViewAdapterInventaire2;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.eventsClasses.MessageEvent;
import com.safesoft.pro.distribute.eventsClasses.ScanResultEvent;
import com.safesoft.pro.distribute.fragments.Fragment_All_Product;
import com.safesoft.pro.distribute.fragments.Fragment_Result_Scan_Inventaire;
import com.safesoft.pro.distribute.postData.PostData_Codebarre;
import com.safesoft.pro.distribute.postData.PostData_Inv2;
import com.safesoft.pro.distribute.postData.PostData_Produit;
import com.safesoft.pro.distribute.scanner.FullScannerActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityInventaireDetails extends AppCompatActivity {

  private ArrayList<PostData_Inv2> arrayOfInventaire2;
  private ListViewAdapterInventaire2 Inventaire2Adapter;
  private String NUM_INV;
  private String NOM_INV;
  private DATABASE controller;

  private  MediaPlayer mp;

  private static final int ZBAR_CAMERA_PERMISSION = 1;
  private Button mScan;
  private Class<?> mClss;

  private EventBus bus;
  private BluetoothSPP bt;
  private ProgressDialog progressDialog_wait_connecte;
  private Menu menu;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inventaire_details);

    bus = EventBus.getDefault();
    controller = new DATABASE(this);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    NUM_INV = getIntent().getStringExtra("NUM_INV");
    NOM_INV = getIntent().getStringExtra("NOM_INV");
    getSupportActionBar().setTitle("Inventaire : " + NOM_INV);
    getSupportActionBar().setSubtitle("NUM : " + NUM_INV);

    bt = new BluetoothSPP(this);

    if(!bt.isBluetoothAvailable()) {
      Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_SHORT).show();
      finish();
    }

    bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {

      public void onDeviceDisconnected() {
        if (progressDialog_wait_connecte != null && !ActivityInventaireDetails.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityInventaireDetails.this, "Scanner bluetooth non connecté ", Style.ALERT).show();
      }

      public void onDeviceConnectionFailed() {
        if (progressDialog_wait_connecte != null && !ActivityInventaireDetails.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityInventaireDetails.this, "Scanner connection erroné ", Style.ALERT).show();
      }

      public void onDeviceConnected(String name, String address) {
        if (progressDialog_wait_connecte != null && !ActivityInventaireDetails.this.isFinishing()) {
          progressDialog_wait_connecte.dismiss();
          progressDialog_wait_connecte = null;
        }
        Crouton.makeText(ActivityInventaireDetails.this, "Terminal connecté à  "+ name, Style.CONFIRM).show();
        //prepareBon_Bluetooth();
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
          public void onDataReceived(byte[] data, String message) {
            // Do something when data incoming
            Toast.makeText(ActivityInventaireDetails.this, "Result : " + message, Toast.LENGTH_LONG).show();
            traitement_scan_result(message);
          }
        });
      }

    });


  }

  public void initView(){

    //button
    mScan = (Button) findViewById(R.id.button_scan);
    mScan.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View arg0) {
        // TODO Auto-generated method stub
        launchActivity(FullScannerActivity.class);
      }
    });

    // Create the adapter to convert the array to views
    Inventaire2Adapter = new ListViewAdapterInventaire2(this, R.layout.transfert2_items, getInventaire2());

    // Attach the adapter to a ListView
    ListView listView = (ListView) findViewById(R.id.listview);
    listView.setAdapter(Inventaire2Adapter);

  }

  @Override
  protected void onStart() {
    super.onStart();

    if (!bt.isBluetoothEnabled()) {
      Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
    } else {
      if(!bt.isServiceAvailable()) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_ANDROID);
        //setup();
      }
    }

    // Register as a subscriber
    bus.register( this);
    initView();

  }

  public void launchActivity(Class<?> clss) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      mClss = clss;
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
    } else {
      Intent intent = new Intent(this, clss);
      startActivityForResult(intent, 1005);
    }
  }

  protected ArrayList<PostData_Inv2> getInventaire2(){
    arrayOfInventaire2 = new ArrayList<>();
    arrayOfInventaire2 = controller.select_inventaire2_from_database("SELECT * FROM Inventaires2 WHERE NUM_INV = '" + NUM_INV+ "'" );
    return arrayOfInventaire2;
  }

  @Override
  public boolean onCreateOptionsMenu(final Menu menu) {
    new MenuInflater(this).inflate(R.menu.menu_inventaire_details, menu);
    bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener() {
      @Override
      public void onServiceStateChanged(int state) {
        if(state == BluetoothState.STATE_CONNECTED) {
          // Do something when successfully connected
          menu.getItem(1).setIcon(ContextCompat.getDrawable(ActivityInventaireDetails.this, R.mipmap.bluetooth_connected));
        }else if(state == BluetoothState.STATE_CONNECTING) {
          // Do something while connecting
          menu.getItem(1).setIcon(ContextCompat.getDrawable(ActivityInventaireDetails.this, R.mipmap.bluetooth_connecting));
        }else if(state == BluetoothState.STATE_LISTEN) {
          // Do something when device is waiting for connection
          menu.getItem(1).setIcon(ContextCompat.getDrawable(ActivityInventaireDetails.this, R.mipmap.bluetooth_not_connected));
        }else if(state == BluetoothState.STATE_NONE) {
          // Do something when device don't have any connection
          menu.getItem(1).setIcon(ContextCompat.getDrawable(ActivityInventaireDetails.this, R.mipmap.bluetooth_not_connected));
        }
        invalidateOptionsMenu();
      }
    });
    return (super.onCreateOptionsMenu(menu));
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if (item.getItemId() == R.id.search_produit) {
      //lunche fragment
      ShowFragment_all_product();
    }else {
      if(bt.getServiceState() == BluetoothState.STATE_CONNECTED){
        Toast.makeText(ActivityInventaireDetails.this, " Déjà connecté", Toast.LENGTH_LONG).show();

      }else{
        bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
        Intent intent = new Intent(getApplicationContext(), DeviceList.class);
        startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
      }
    }

    return super.onOptionsItemSelected(item);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // TODO Auto-generated method stub

    if (requestCode == 1005) {
      if (resultCode == Activity.RESULT_OK) {
        String result = data.getStringExtra("result");

        traitement_scan_result(result);
      }
    }else if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
      if(resultCode == Activity.RESULT_OK){
        bt.connect(data);
        progressDialog_wait_connecte = ProgressDialog.show(ActivityInventaireDetails.this, getString(R.string.bl_dy1), getString(R.string.printing_wait1));
      }
    } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
      if(resultCode == Activity.RESULT_OK) {
        bt.setupService();
        bt.startService(BluetoothState.DEVICE_OTHER);
        //setup();
      } else {
        Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.", Toast.LENGTH_SHORT).show();
        finish();
      }
    }

    if (resultCode == Activity.RESULT_CANCELED) {
      //Write your code if there's no result
      // Toast.makeText(this, "Problem to secan the barcode.", Toast.LENGTH_LONG).show();
    }
  }

  protected void traitement_scan_result(String result){

    PostData_Codebarre codebarres = controller.select_produit_codebarre(result);////////

    ArrayList<PostData_Produit> prod = new  ArrayList<PostData_Produit>();
    if(!codebarres.exist){
      String querry = "SELECT * FROM Produit WHERE CODE_BARRE = '"+result+"' OR REF_PRODUIT = '"+result+"'";
      prod = controller.select_produits_from_database(querry);
    }else{
      prod = controller.select_produits_from_database(codebarres.code_barre);
    }

    if(prod.size() > 0){
      Boolean check_before = false;
      String _quantity ="";
      for(int i = 0; i< arrayOfInventaire2.size(); i++){
        if((arrayOfInventaire2.get(i).codebarre.equals(prod.get(0).code_barre) || (arrayOfInventaire2.get(i).reference.equals(prod.get(0).ref_produit)))){
          check_before = true;
          _quantity = arrayOfInventaire2.get(i).quantity_new;
          break;
        }
      }
      ShowFragment_Scan_Result_FragmentDialog(prod.get(0).produit,prod.get(0).ref_produit,prod.get(0).code_barre,prod.get(0).pa_ht, prod.get(0).stock, prod.get(0).tva, NUM_INV, check_before, _quantity);
    }else{
      Crouton.showText(ActivityInventaireDetails.this  , "Produit non exist.", Style.ALERT);
    }
  }

  @Subscribe
  public void onScanResult(ScanResultEvent event){

    PostData_Produit prod = new PostData_Produit();
    prod = event.getProduit();
    Boolean check_before = false;
    String _quantity ="";
    for(int i = 0; i< arrayOfInventaire2.size(); i++){
      if((arrayOfInventaire2.get(i).codebarre.equals(prod.code_barre) || (arrayOfInventaire2.get(i).reference.equals(prod.ref_produit)))){
        check_before = true;
        _quantity = arrayOfInventaire2.get(i).quantity_new;
        break;
      }
    }
    ShowFragment_Scan_Result_FragmentDialog(prod.produit,prod.ref_produit,prod.code_barre,prod.pa_ht, prod.stock, prod.tva,  NUM_INV, check_before, _quantity);
  }

  @Subscribe
  public void onOperationEnded(MessageEvent event){

    // Create the adapter to convert the array to views
    Inventaire2Adapter = new ListViewAdapterInventaire2(this, R.layout.transfert2_items, getInventaire2());

    // Attach the adapter to a ListView
    ListView listView = (ListView) findViewById(R.id.listview);
    listView.setAdapter(Inventaire2Adapter);

  }

  private void ShowFragment_Scan_Result_FragmentDialog(String prod, String reference, String codebarre, String pa_ttc, String quantity_old, String tva, String num_inv, Boolean check_before, String quantity_added) {
    android.app.FragmentManager fm = getFragmentManager();
    DialogFragment dialog = new Fragment_Result_Scan_Inventaire(); // creating new object
    Bundle args = new Bundle();
    args.putString("PRODUIT",prod);
    args.putString("REFERENCE",reference);
    args.putString("CODEBARRE",codebarre);
    args.putString("PA_TTC",pa_ttc);
    args.putString("STOCK_OLD",quantity_old);
    args.putString("TVA",tva);
    args.putString("NUM_INV",num_inv);
    args.putBoolean("CHECK_BEFORE",check_before);
    args.putString("QUANTITY_ADDED",quantity_added);
    dialog.setArguments(args);
    dialog.show(fm, "dialog");
  }

  private void ShowFragment_all_product() {
    android.app.FragmentManager fm = getFragmentManager();
    DialogFragment dialog = new Fragment_All_Product(); // creating new object
    Bundle args = new Bundle();
    dialog.setArguments(args);
    dialog.show(fm, "dialog");
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
  protected void onPause() {
    super.onPause();
    // Unregister
    bus.unregister(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if(bt != null)
      bt.stopService();
  }
}

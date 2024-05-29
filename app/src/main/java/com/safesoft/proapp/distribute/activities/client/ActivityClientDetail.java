package com.safesoft.proapp.distribute.activities.client;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.safesoft.proapp.distribute.activities.product.ActivityProduitDetail;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapter_Situation;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.fragments.FragmentVersementClient;
import com.safesoft.proapp.distribute.gps.ServiceLocation;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.LocationEvent;
import com.safesoft.proapp.distribute.printing.Printing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import mehdi.sakout.fancybuttons.FancyButton;

public class ActivityClientDetail extends AppCompatActivity implements RecyclerAdapter_Situation.ItemClick {


  Boolean printer_mode_integrate = true;
  private NumberFormat nf;

  private PostData_Client client;
  private TextView TvClient, TvTel, TvLat, TvLog, TvAdresse, TvCodeClient, TvModeT, TvAchat, TvVerser, TvSolde;
  private ImageView imgv_client_map;
  private FancyButton Btn_itenerary, Btn_Call, Btn_update_position, BtnVerser, BtnVente;

  private MediaPlayer mp;

  private final EventBus bus = EventBus.getDefault();

  private static final int ACCES_FINE_LOCATION = 2;
  private static final int BLUETOOTH_CONNECT = 3;
  private Boolean checkPermission = false;

  private Intent intent_location;

  private ProgressDialog progress;

  private DATABASE controller;

  private RecyclerView recyclerView;
  RecyclerAdapter_Situation adapter;
  ArrayList<PostData_Carnet_c> carnet_cs;
  private String CODE_CLIENT;
  String url_static_map;
  private PostData_Carnet_c carnet_c_print;

  PostData_Carnet_c selected_versement = null;
  private final String PREFS = "ALL_PREFS";
  SharedPreferences prefs;

  @RequiresApi(api = Build.VERSION_CODES.S)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_client_detail);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Situation Client");
   // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));


    prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

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
    if (checkPermission) {
    //  startService(intent_location);
    }

    setStaticMap(client.latitude, client.longitude);
  }



  private void setStaticMap(double latitude, double longitude){
    int zoom = 15;
    String label = "P";
    url_static_map = "https://maps.googleapis.com/maps/api/staticmap?center="+ latitude + "," + longitude + "&zoom="+ zoom +"&size=1200x300&markers=color:red%7Clabel:" + label + "%7C" + latitude + "," + longitude + "&key=AIzaSyAzMUqTnhsnrXuog5ZjSrnSYMPM-XShfRA";
    if(latitude != 0 && longitude != 0){
      new DownloadImageTask(imgv_client_map).execute();
    }

  }
  protected void getClient() {
    client = new PostData_Client();
    client = controller.select_client_from_database(CODE_CLIENT);
    iniData(client);
  }

  @Override
  protected void onStart() {

    SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
      printer_mode_integrate = prefs1.getString("PRINTER_CONX", "INTEGRATE").equals("INTEGRATE");

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
   // TvLat = (TextView) findViewById(R.id.lat);
   // TvLog = (TextView) findViewById(R.id.log);
    TvAdresse = (TextView) findViewById(R.id.adresse);
    TvCodeClient = (TextView) findViewById(R.id.code_client);
    TvModeT = (TextView) findViewById(R.id.mode_tarif);
    TvAchat = (TextView) findViewById(R.id.achat);
    TvVerser = (TextView) findViewById(R.id.verser);
    TvSolde = (TextView) findViewById(R.id.solde);

    //Button
   // BtnCall = (ImageButton) findViewById(R.id.btnCall);
    BtnVerser = (FancyButton) findViewById(R.id.btnVerser);
    BtnVente = (FancyButton) findViewById(R.id.btnVente);
    Btn_itenerary = (FancyButton) findViewById(R.id.btn_itenerary);
    Btn_update_position = (FancyButton) findViewById(R.id.btn_update_pos);
    Btn_Call = (FancyButton) findViewById(R.id.btnCall);

    Btn_itenerary.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_itenerary_24));
    Btn_update_position.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_update_location_24));
    Btn_Call.setIconResource(AppCompatResources.getDrawable(this, R.drawable.ic_baseline_local_phone_white_24));

    //ImageView
    imgv_client_map = findViewById(R.id.imgv_client_map);
    //RecycleViews
    recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client_detail);
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapter_Situation(this, getItems());
    recyclerView.setAdapter(adapter);
  }

  public ArrayList<PostData_Carnet_c> getItems() {

    carnet_cs = new ArrayList<>();
    String querry = "SELECT CARNET_C.RECORDID, " +
            "CARNET_C.CODE_CLIENT, " +
            "CARNET_C.DATE_CARNET, " +
            "CLIENT.CLIENT, " +
            "CLIENT.LATITUDE, " +
            "CLIENT.CODE_CLIENT, " +

            "CLIENT.LONGITUDE, " +
            "CLIENT.TEL, " +
            "CLIENT.CLIENT, " +
            "CLIENT.ADRESSE, " +
            "CLIENT.WILAYA, " +
            "CLIENT.COMMUNE, " +
            "CLIENT.RC, " +
            "Client.IFISCAL, " +
            "CLIENT.AI, " +
            "CLIENT.NIS, " +
            "CLIENT.MODE_TARIF, " +

            "CARNET_C.HEURE, " +
            "CARNET_C.ACHATS, " +
            "CARNET_C.VERSEMENTS, " +
            "CARNET_C.SOURCE, " +
            "CARNET_C.NUM_BON, " +
            "CARNET_C.MODE_RG, " +
            "CARNET_C.REMARQUES, " +
            "CARNET_C.UTILISATEUR, " +
            "CARNET_C.EXPORTATION, " +
            "CARNET_C.IS_EXPORTED " +

            "FROM CARNET_C " +
            "LEFT JOIN CLIENT ON " +
            "CARNET_C.CODE_CLIENT = CLIENT.CODE_CLIENT " +
            "WHERE CARNET_C.CODE_CLIENT = '"+ client.code_client +"' ORDER BY CARNET_C.HEURE DESC";


    // querry = "SELECT * FROM Events";
    carnet_cs = controller.select_carnet_c_from_database(querry);

    return carnet_cs;
  }

  protected void iniData(PostData_Client client) {

    TvClient.setText(client.client);
    TvTel.setText(client.tel);
    TvAdresse.setText(client.adresse + " / " + client.commune + " - " + client.wilaya);
    TvCodeClient.setText(client.code_client);


    if (client.mode_tarif != null){
      switch (client.mode_tarif) {
        case "0" -> TvModeT.setText("LIBRE");
        case "1" -> TvModeT.setText("1");
        case "2" -> TvModeT.setText("2");
        case "3" -> TvModeT.setText("3");
        case "4" -> TvModeT.setText("4");
        case "5" -> TvModeT.setText("5");
        case "6" -> TvModeT.setText("6");
      }

    }


    if (prefs.getBoolean("SHOW_ACHAT_CLIENT", false)) {
      TvAchat.setVisibility(View.VISIBLE);
    } else {
      TvAchat.setVisibility(View.GONE);
    }

      final BadgeDrawable drawable6 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xff303F9F)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(client.achat_montant)))
                      .text2(" DA ")
                      .build();
      SpannableString spannableString6 = new SpannableString(TextUtils.concat(drawable6.toSpannable()));
      TvAchat.setText(spannableString6);


      final BadgeDrawable drawable7 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xff303F9F)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(client.verser_montant)))
                      .text2(" DA ")
                      .build();
      SpannableString spannableString7 = new SpannableString(TextUtils.concat(drawable7.toSpannable()));
      TvVerser.setText(spannableString7);


    if (prefs.getBoolean("AFFICHAGE_SOLDE_CLIENT", true)) {
      final BadgeDrawable drawable8 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xff303F9F)
                      .text1( new DecimalFormat("##,##0.00").format(Double.valueOf(client.solde_montant)))
                      .text2(" DA " )
                      .build();
      SpannableString spannableString8 = new SpannableString(TextUtils.concat(drawable8.toSpannable()));
      TvSolde.setText(spannableString8);
    } else {
      TvSolde.setText("********");
    }




  }

  private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
      this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(Void... urls) {
      String urldisplay = url_static_map;
      Bitmap mIcon11 = null;
      try {
        InputStream in = new java.net.URL(urldisplay).openStream();
        mIcon11 = BitmapFactory.decodeStream(in);
      } catch (Exception e) {
        Log.e("Error", e.getMessage());
        e.printStackTrace();
        mIcon11 = BitmapFactory.decodeResource(getResources(), R.mipmap.noimg);
      }
      return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
      bmImage.setImageBitmap(result);
    }
  }

  public void onClick(View v) {

    switch (v.getId()) {
      case R.id.btnCall:
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + client.tel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
          return;
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        break;

      case R.id.btnVerser:
        FragmentVersementClient fragmentversementclient= new FragmentVersementClient();
        fragmentversementclient.showDialogbox(ActivityClientDetail.this, client.solde_montant, client.verser_montant, 0, "" , client.code_client, false, "");
        break;

      case R.id.btnVente:
        break;

      case R.id.btn_update_pos:

        startService(intent_location);

         progress = new ProgressDialog(ActivityClientDetail.this);
         progress.setTitle("Position");
         progress.setMessage("Recherche position...");
         progress.setIndeterminate(true);
         progress.setCancelable(true);
         progress.show();

        break;
      case R.id.btn_itenerary:
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ client.latitude + "," + client.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
    }else if(item.getItemId() == R.id.print_versement){
      if(selected_versement != null){
        Print_versement();
      }else {
        Crouton.makeText(ActivityClientDetail.this, "Vous devez séléctionner une versement au-dessous !", Style.ALERT).show();
      }

    }else if(item.getItemId() == R.id.edit_client){
      FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
      fragmentnewclient.showDialogbox(ActivityClientDetail.this, getBaseContext(), "EDIT_CLIENT", client);
    }

    return super.onOptionsItemSelected(item);
  }


  @Override
  public void onBackPressed() {
    Sound();
    super.onBackPressed();
    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
  }


  public void Sound(){
    mp = MediaPlayer.create(this, R.raw.back);
    mp.start();
  }


  @RequiresApi(api = Build.VERSION_CODES.S)
  public void requestPermission(){
/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECIEVE_SMS);
        }
*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT);
          checkPermission = false;
        }else {
          checkPermission = true;
        }

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

    controller.update_position_client(client.latitude, client.longitude, client.code_client);
    if(progress != null){
      progress.dismiss();
    }

    setStaticMap(event.getLocationData().getLatitude(), event.getLocationData().getLongitude());

  }


  public void Update_client_details(){
    getClient();
    setRecycle();
  }


  @Subscribe
  public void onClientSelected(SelectedClientEvent clientEvent){
    getClient();
  }


  @Override
  public void onClick(View v, int position, final  PostData_Carnet_c carnet_c) {

    switch (v.getId()) {
        case R.id.btn_edit_situation -> {

          if (carnet_c.is_exported != 0) {
            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Attention !")
                    .setContentText("Versement déjà exporté, Modification impossible !")
                    .show();
            return;

          }
          if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Situation")
                    .setContentText("Voulez-vous vraiment modifier cette situation?!")
                    .setCancelText("Non")
                    .setConfirmText("Modifier")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                      FragmentVersementClient fragmentversementclient = new FragmentVersementClient();
                      fragmentversementclient.showDialogbox(ActivityClientDetail.this, client.solde_montant, client.verser_montant, carnet_c.carnet_versement, carnet_c.carnet_remarque, client.code_client, true, carnet_c.recordid);

                      sDialog.dismiss();
                    }).show();
          }else {
            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Attention !")
                    .setContentText("Vous n'êtes pas autorisé à modifier cette situation !")
                    .show();
          }

        }
        case R.id.btn_remove_situation -> {

            if (carnet_c.is_exported != 0) {
              new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.WARNING_TYPE)
                      .setTitleText("Attention !")
                      .setContentText("Versement déjà exporté, Suppression impossible !")
                      .show();
              return;

            }

          if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer la situation " + carnet_c.recordid + " ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                      if (controller.delete_versement(carnet_c, client.solde_montant + carnet_c.carnet_versement, client.verser_montant - carnet_c.carnet_versement)) {
                        Crouton.makeText(ActivityClientDetail.this, "Situation supprimé !", Style.INFO).show();
                      } else {
                        Crouton.makeText(ActivityClientDetail.this, "Problème au moment de suppression de la situation !", Style.ALERT).show();
                      }
                      Update_client_details();

                      sDialog.dismiss();

                    }).show();
          }else {
            new SweetAlertDialog(ActivityClientDetail.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Attention !")
                    .setContentText("Vous n'êtes pas autorisé à supprimer cette situation !")
                    .show();
          }

        }

        case R.id.lnr_item_root ->{
        selected_versement = carnet_c;
      }

    }
  }

  protected void Print_versement(){


    carnet_c_print = new PostData_Carnet_c();
    String querry = "SELECT CARNET_C.RECORDID, " +
            "CARNET_C.CODE_CLIENT, " +
            "CARNET_C.DATE_CARNET, " +
            "CLIENT.CLIENT, " +
            "CLIENT.LATITUDE, " +
            "CLIENT.CODE_CLIENT, " +

            "CLIENT.LONGITUDE, " +
            "CLIENT.TEL, " +
            "CLIENT.CLIENT, " +
            "CLIENT.ADRESSE, " +
            "CLIENT.RC, " +
            "Client.IFISCAL, " +
            "CLIENT.AI, " +
            "CLIENT.NIS, " +
            "CLIENT.MODE_TARIF, " +

            "CARNET_C.HEURE, " +
            "CARNET_C.ACHATS, " +
            "CARNET_C.VERSEMENTS, " +
            "CARNET_C.SOURCE, " +
            "CARNET_C.NUM_BON, " +
            "CARNET_C.MODE_RG, " +
            "CARNET_C.REMARQUES, " +
            "CARNET_C.UTILISATEUR, " +
            "CARNET_C.EXPORTATION " +




            "FROM CARNET_C " +
            "LEFT JOIN CLIENT ON " +
            "CARNET_C.CODE_CLIENT = CLIENT.CODE_CLIENT " +
            "WHERE CARNET_C.CODE_CLIENT = '"+ client.code_client +"' ";
   // carnet_c_print  = controller.select_carnet_c_from_database_single(querry);
    carnet_c_print  = selected_versement;


    Activity bactivity;
    bactivity = ActivityClientDetail.this;

    Printing printer = new Printing();
    printer.start_print_versement_client(bactivity, carnet_c_print );

  }


  @Override
  protected void onDestroy() {

    bus.unregister(this);
    stopService(intent_location);

    super.onDestroy();
  }

}

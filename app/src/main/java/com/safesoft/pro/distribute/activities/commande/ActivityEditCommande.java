package com.safesoft.pro.distribute.activities.commande;

import android.Manifest;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.pro.distribute.activities.ActivityNewClient;
import com.safesoft.pro.distribute.adapters.ListViewAdapterPanier;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.fragments.FragmentListClient;
import com.safesoft.pro.distribute.gps.ServiceLocation;
import com.safesoft.pro.distribute.postData.PostData_Achat1;
import com.safesoft.pro.distribute.postData.PostData_Achat2;
import com.safesoft.pro.distribute.postData.PostData_Bon1;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.eventsClasses.LocationEvent;
import com.safesoft.pro.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.pro.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.nekocode.badge.BadgeDrawable;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

import static com.safesoft.pro.distribute.R.id.timbre;

public class ActivityEditCommande extends AppCompatActivity {



  ////////////////////////////////////////
  private static int REQUEST_ACTIVITY_ADDPRODUCT = 5000;
  private static int REQUEST_ACTIVITY_NEW_CLIENT = 5001;

  private static final int ACCES_FINE_LOCATION = 2;
  private Intent intent_location;
  private Boolean checkPermission = false;

  private RelativeLayout rtvl_client;
  private TextView client_name;
  private EditText observation;
  private ListViewAdapterPanier PanierAdapter;
  private Button addProduct;
  private Button addClient, valideFacture;
  private TextView client;
  private DATABASE controller;
  private TextView mode_tarif;
  private  ArrayList<PostData_Bon2> final_panier;
  private TextView total_ht, tva, txv_timbre, total_ttc, total_ttc_remise;
  private CheckBox checkBox_remise, checkBox_timbre;
  private EditText edt_remise;
  private TableRow tr_item_3, tr_item_5, tr_item_4;
  private  Double val_total_ht = 0.00, val_tva = 0.00, val_timbre = 0.00, val_remise = 0.00, val_total_ttc = 0.00, val_total_ttc_remise = 0.00;

  private EventBus bus;
  private String NUM_BON;
  private String IS_VALIDE;
  private String CODE_DEPOT;
  private String formattedDate;
  private String formattedDate_Show;
  private String currentTime;
  private PostData_Client client_selected;
  private PostData_Bon1 bon1;

  private String PREFS_AUTRE = "ConfigAutre";
  private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";

  private  MediaPlayer mp;


  private ExpandableHeightListView expandableListView;

  public static final String BARCODE_KEY = "BARCODE";

  private Barcode barcodeResult;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_sale);


    if(savedInstanceState != null){
      Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
      if(restoredBarcode != null){
        //  result.setText(restoredBarcode.rawValue);
        Toast.makeText(ActivityEditCommande.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
        barcodeResult = restoredBarcode;
      }
    }

    bus = EventBus.getDefault();
    controller = new DATABASE(this);
    bon1 = new PostData_Bon1();

    final_panier = new ArrayList<>();

    //get num bon
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));

    if(getIntent() !=null){
      //
      NUM_BON = getIntent().getStringExtra("NUM_BON");
      IS_VALIDE = getIntent().getStringExtra("VALIDATED");
    }
    if(NUM_BON != null) {
      getSupportActionBar().setTitle(NUM_BON);
    }

    ///////////////////////////////////
    bon1 = controller.select_bon1_from_database2("" +
            "SELECT Bon1_temp.NUM_BON, " +
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
            "Bon1_temp.NUM_BON ='"+ NUM_BON +"'");


    final_panier =  controller.select_bon2_from_database("" +
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
            "WHERE Bon2_temp.NUM_BON = '" + NUM_BON+ "'" );


    // annuler le versement et le montant bon depuis le client
    //////////////////////////////////////////
    if(IS_VALIDE.equals("TRUE")){
      controller.update_bon1_temp_edit(bon1.num_bon, bon1.code_client, bon1);
    }


    formattedDate_Show = bon1.date_bon;
    if(formattedDate_Show != null){
      getSupportActionBar().setSubtitle(formattedDate_Show);
    }

    currentTime = bon1.heure;
    if(currentTime != null){
      getSupportActionBar().setSubtitle(formattedDate_Show + " "  + currentTime);
    }
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    initViews();

    client_selected = controller.select_client_from_database(bon1.code_client);
    onClientSelected(client_selected);

    // Create the adapter to convert the array to views
    PanierAdapter = new ListViewAdapterPanier(this, R.layout.transfert2_items, final_panier);

    expandableListView = (ExpandableHeightListView) findViewById(R.id.expandable_listview);

    expandableListView.setAdapter(PanierAdapter);

    // This actually does the magic
    expandableListView.setExpanded(true);
    registerForContextMenu(expandableListView);

    calcule();


    // Register as a subscriber
    bus.register(this);

    client_name.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(client_name.getText().toString().isEmpty()){
          addProduct.setEnabled(false);
        }else{
          addProduct.setEnabled(true);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    checkBox_remise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          edt_remise.setVisibility(View.VISIBLE);
          tr_item_3.setVisibility(View.VISIBLE);
          tr_item_4.setVisibility(View.VISIBLE);

        }else{
          edt_remise.setVisibility(View.GONE);
          tr_item_3.setVisibility(View.GONE);
          tr_item_4.setVisibility(View.GONE);
        }

        // checkBox_remise.clearFocus();
        edt_remise.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt_remise, InputMethodManager.SHOW_IMPLICIT);
        //edt_remise.getShowSoftInputOnFocus();
      }
    });

    checkBox_timbre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
          tr_item_5.setVisibility(View.VISIBLE);
        }else{
          tr_item_5.setVisibility(View.GONE);
        }
        calcule();
        sauvegarder();
      }
    });

    edt_remise.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if(!s.toString().equals(".")){
          calcule();
          sauvegarder();
        }else{
          // s.replace(0,1,"");
          edt_remise.setText("");
          //edt_remise.setOnEditorActionListener(this);
        }
      }
    });

    edt_remise.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        edt_remise.requestFocus();
      }
    });

    // calcule();

  }


  private void initViews() {

    //  recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    rtvl_client = (RelativeLayout) findViewById(R.id.rtvl_client);
    client = (TextView) findViewById(R.id.client);
    addProduct = (Button) findViewById(R.id.addProduct);
    addClient = (Button) findViewById(R.id.create);
    valideFacture = (Button) findViewById(R.id.valide_facture);

    mode_tarif = (TextView) findViewById(R.id.mode_tarif);
    client_name = (TextView) findViewById(R.id.client);

    //textview
    total_ht = (TextView) findViewById(R.id.total_ht);
    tva = (TextView) findViewById(R.id.tva);
    total_ttc = (TextView) findViewById(R.id.total_ttc);
    txv_timbre = (TextView) findViewById(timbre);
    total_ttc_remise = (TextView) findViewById(R.id.total_ttc_remise);
    observation = (EditText) findViewById(R.id.note);

    // EditText
    edt_remise = (EditText) findViewById(R.id.edt_remise);

    //TablRow
    tr_item_3 = (TableRow) findViewById(R.id.tr_item_3);
    tr_item_4 = (TableRow) findViewById(R.id.tr_item_4);
    tr_item_5 = (TableRow) findViewById(R.id.tr_item_5);

    //checkbox
    checkBox_remise = (CheckBox) findViewById(R.id.checkbox_remise);
    if(bon1.remise_ckecked){
      checkBox_remise.setChecked(true);
      edt_remise.setVisibility(View.VISIBLE);
      edt_remise.setText(bon1.remise);
      tr_item_3.setVisibility(View.VISIBLE);
      tr_item_4.setVisibility(View.VISIBLE);
    }else{
      edt_remise.setVisibility(View.GONE);
      tr_item_3.setVisibility(View.GONE);
      tr_item_4.setVisibility(View.GONE);
    }

    checkBox_timbre = (CheckBox) findViewById(R.id.checkbox_timbre);
    if(bon1.timbre_ckecked){
      checkBox_timbre.setChecked(true);
      tr_item_5.setVisibility(View.VISIBLE);
    }else {
      tr_item_5.setVisibility(View.GONE);
    }

    SharedPreferences prefs = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
    CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");

    intent_location = new Intent(this, ServiceLocation.class);
    SharedPreferences prefs1 = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
    if(prefs1.getBoolean("GPS_LOCALISATION", false)){
      requestPermission();
      if(checkPermission){
        startService(intent_location);
      }
    }else {
      stopService(intent_location);
    }

  }


  public void onClickEvent(View v){
    switch (v.getId()){
      case R.id.rtvl_client:
        showListClient();
        break;
      case R.id.addProduct:
        if(client.getText().length() <1){

          Crouton.makeText(ActivityEditCommande.this, "Vous devez Séléctionner un client tout d'abord", Style.ALERT).show();
          return;
        }
        Intent intentAddProduct = new Intent(ActivityEditCommande.this, ActivityAddProduct_Commande.class);
        intentAddProduct.putExtra("MODE_TARIFF", client_selected.mode_tarif.toString());
        intentAddProduct.putExtra("NUM_BON", bon1.num_bon);
        intentAddProduct.putExtra("CODE_DEPOT", CODE_DEPOT);

        startActivityForResult(intentAddProduct, REQUEST_ACTIVITY_ADDPRODUCT);
        break;
      case R.id.create:
        Intent intentAddClient = new Intent(ActivityEditCommande.this, ActivityNewClient.class);
        startActivityForResult(intentAddClient, REQUEST_ACTIVITY_NEW_CLIENT);
        break;
      case R.id.valide_facture:
        if(client.getText().length() <1){

          Crouton.makeText(ActivityEditCommande.this, "Vous devez Séléctionner un client", Style.ALERT).show();
          return;
        }
        if(final_panier.size() <1){
          Crouton.makeText(ActivityEditCommande.this, "Panier Vide! Séléctionner un produit", Style.INFO).show();
          return;
        }
/*
                Intent versement_intent = new Intent(ActivityEditCommande.this, ActivityVersement.class);
                versement_intent.putExtra("NUM_BON", bon1.num_bon);
                versement_intent.putExtra("CODE_DEPOT", bon1.code_depot);
                startActivityForResult(versement_intent, 55555);
*/

        if(!controller.check_if_bon1_valide("Bon1_temp", bon1.num_bon)){
          new SweetAlertDialog(ActivityEditCommande.this, SweetAlertDialog.NORMAL_TYPE)
                  .setTitleText("Bon de commande")
                  .setContentText("Voulez-vous vraiment valider ce Bon ?!")
                  .setCancelText("Non")
                  .setConfirmText("Oui")
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

                      // Insert bon1 into local database ;
                      // controller.Insert_into_bon1(bon1);

                      // Insert bon1, bon2 into local database
                      if(controller.update_bon1_temp(bon1.num_bon, bon1)){
                        new SweetAlertDialog(ActivityEditCommande.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Réussit!")
                                .setContentText("Bon Validé!")
                                .show();


                      }else{
                        new SweetAlertDialog(ActivityEditCommande.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Bon non validé, erreur fatal!")
                                .show();
                      }
                      sDialog.dismiss();
                    }
                  })
                  .show();
        }else{

          new SweetAlertDialog(ActivityEditCommande.this, SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("Attention !")
                  .setContentText("Ce bon est déjà validé !")
                  .show();
        }
        break;
    }
  }


  protected void showListClient(){
    android.app.FragmentManager fm = getFragmentManager();
    DialogFragment dialog = new FragmentListClient(); // creating new object
    Bundle args = new Bundle();
    dialog.setArguments(args);
    dialog.show(fm, "dialog");

  }


  @Subscribe
  public void onClientSelected(SelectedClientEvent clientEvent){

    onClientSelected(clientEvent.getClient());
  }

  protected void onClientSelected(PostData_Client client_s){

    client_selected = client_s;
    client.setText(client_selected.client);

    bon1.code_client = client_selected.code_client;
    bon1.solde_ancien = client_selected.solde_montant;
    bon1.verser = client_selected.verser_montant;



    if(client_selected.mode_tarif != null){
      if(client_selected.mode_tarif.toString().equals("2")){
        // tariff 2
        final BadgeDrawable drawable4 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                        .badgeColor(0xffCC9999)
                        .text1("TARRIF 2")
                        .build();
        SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
        mode_tarif.setText(spannableString1);

      }else if(client_selected.mode_tarif.toString().equals("3")){
        // tarrif 3
        final BadgeDrawable drawable4 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                        .badgeColor(0xffCC9999)
                        .text1("TARRIF 3")
                        .build();

        SpannableString spannableString2 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
        mode_tarif.setText(spannableString2);
      }
      else{
        // tariff 1
        final BadgeDrawable drawable4 =
                new BadgeDrawable.Builder()
                        .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                        .badgeColor(0xffCC9999)
                        .text1("TARRIF 1")
                        .build();
        SpannableString spannableString3 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
        mode_tarif.setText(spannableString3);
      }
    }else{
      // tariff 1
      final BadgeDrawable drawable4 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT)
                      .badgeColor(0xffCC9999)
                      .text1("TARRIF 1")
                      .build();
      SpannableString spannableString4 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
      mode_tarif.setText(spannableString4);
    }

    if(!controller.Insert_into_bon1("Bon1_temp",bon1)){
      finish();
    }
  }
  protected void initData(){

    final_panier = controller.select_bon2_from_database("" +
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
            "WHERE Bon2_temp.NUM_BON = '" + NUM_BON+ "'" );
    // Create the adapter to convert the array to views
    PanierAdapter = new ListViewAdapterPanier(this, R.layout.transfert2_items, final_panier);

    expandableListView = (ExpandableHeightListView) findViewById(R.id.expandable_listview);

    expandableListView.setAdapter(PanierAdapter);

    // This actually does the magic
    expandableListView.setExpanded(true);

    registerForContextMenu(expandableListView);

    calcule();

    sauvegarder();

  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    if (v.getId()==R.id.expandable_listview) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu_listv, menu);
    }
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    switch(item.getItemId()) {
      case R.id.delete:
        try{
          // remove stuff here
          controller.delete_from_bon2(true, final_panier.get(info.position).recordid, bon1.num_bon ,final_panier.get(info.position));
          final_panier.remove(info.position);
          //PanierAdapter.RefrechPanier(final_panier);
          PanierAdapter = new ListViewAdapterPanier(ActivityEditCommande.this, R.layout.transfert2_items, final_panier);
          expandableListView.setAdapter(PanierAdapter);
          calcule();

        }catch (Exception e){

          new SweetAlertDialog(ActivityEditCommande.this, SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("Attention!")
                  .setContentText("problème lors suppression produits! : " + e.getMessage())
                  .show();
        }

        return true;
      default:
        return super.onContextItemSelected(item);
    }
  }

  public void calcule(){

    val_total_ht = 0.00;
    val_tva = 0.00;
    val_timbre = 0.00;
    val_remise = 0.00;
    val_total_ttc = 0.00;
    val_total_ttc_remise = 0.00;

    for(int k = 0; k< final_panier.size(); k++){

      Double total_montant_produit = (Double.valueOf(final_panier.get(k).p_u) * Double.valueOf(final_panier.get(k).qte));
      Double montant_tva_produit = total_montant_produit * ((Double.valueOf(final_panier.get(k).tva)) / 100);
      val_total_ht = val_total_ht + total_montant_produit;
      val_tva = val_tva + montant_tva_produit;

    }

    if(checkBox_remise.isChecked() && checkBox_timbre.isChecked()){
      val_remise = Double.valueOf(edt_remise.getText().toString());

      val_total_ttc =  (val_total_ht) +  val_tva;
      if((val_total_ttc * 0.01) >= 2500){
        val_timbre = 2500.00;
      }else{
        //float nnn = (val_total_ttc * (1/100));
        val_timbre = (val_total_ttc * 0.01);
      }
      val_total_ttc =  (val_total_ht) + val_tva + val_timbre;

      val_total_ttc_remise = val_total_ttc - val_remise;

      final BadgeDrawable drawable0 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xff303F9F)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_total_ttc_remise.toString())))
                      .text2(" DA")
                      .build();
      SpannableString spannableString0 = new SpannableString(TextUtils.concat(drawable0.toSpannable()));
      total_ttc_remise.setText(spannableString0);

      final BadgeDrawable drawable1 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xffE74C3C)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_timbre.toString())))
                      .text2(" DA")
                      .build();
      SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));
      txv_timbre.setText(spannableString1);


    }else if(checkBox_remise.isChecked()){
      if(edt_remise.getText().toString().length() > 0){
        val_remise = Double.valueOf(edt_remise.getText().toString());
      }else{
        val_remise = 0.00;
      }

      val_total_ttc =  (val_total_ht) + val_tva;
      val_total_ttc_remise = val_total_ttc - val_remise;

      final BadgeDrawable drawable0 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xffE74C3C)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_total_ttc_remise.toString())))
                      .text2(" DA ")
                      .build();
      SpannableString spannableString0 = new SpannableString(TextUtils.concat(drawable0.toSpannable()));
      total_ttc_remise.setText(spannableString0);

    }else if(checkBox_timbre.isChecked()){
      //val_timbre = Double.valueOf(edt_timbre.getText().toString());
      val_total_ttc =  (val_total_ht) + val_tva;
      if((val_total_ttc * 0.01) >= 2500){
        val_timbre = 2500.00;
      }else{
        //float nnn = (val_total_ttc * (1/100));
        val_timbre = (val_total_ttc * 0.01);
      }
      val_total_ttc =  (val_total_ht) +  val_tva + val_timbre;

      final BadgeDrawable drawable1 =
              new BadgeDrawable.Builder()
                      .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
                      .badgeColor(0xffE74C3C)
                      .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_timbre.toString())))
                      .text2(" DA ")
                      .build();
      SpannableString spannableString1 = new SpannableString(TextUtils.concat(drawable1.toSpannable()));
      txv_timbre.setText(spannableString1);
    }else{
      val_total_ttc =  (val_total_ht) + val_tva;
    }

    final BadgeDrawable drawable3 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_total_ht.toString())))
            .text2(" DA ")
            .build();
    SpannableString spannableString3 = new SpannableString(TextUtils.concat(drawable3.toSpannable()));
    total_ht.setText(spannableString3);

    final BadgeDrawable drawable4 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xffE74C3C)
            .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_tva.toString())))
            .text2(" DA ")
            .build();
    SpannableString spannableString4 = new SpannableString(TextUtils.concat(drawable4.toSpannable()));
    tva.setText(spannableString4);

    final BadgeDrawable drawable5 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(new DecimalFormat("##,##0.00").format(Double.valueOf(val_total_ttc.toString())))
            .text2(" DA ")
            .build();
    SpannableString spannableString5 = new SpannableString(TextUtils.concat(drawable5.toSpannable()));
    total_ttc.setText(spannableString5);

  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_bon_vente_commande, menu);
    return true;
  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.menu_scan){

      startScan();
    }
    return super.onOptionsItemSelected(item);
  }


  //Versement
  protected void sauvegarder(){

    bon1.code_client = client_selected.code_client;
    bon1.client = client.getText().toString();
    bon1.tot_ttc =  String.valueOf(total_ttc);
    bon1.tot_ttc_remise =  String.valueOf(total_ttc_remise);
    bon1.code_vendeur = "000000";
    bon1.mode_tarif = client_selected.mode_tarif;
    bon1.solde_ancien = client_selected.solde_montant;
    bon1.nbr_p = String.valueOf(final_panier.size());

    if(checkBox_remise.isChecked()){
      bon1.montant_bon = String.valueOf(val_total_ttc_remise);
      bon1.remise =  String.valueOf(val_remise);
      bon1.remise_ckecked = true;
    }else{
      bon1.montant_bon = String.valueOf(val_total_ttc);
      bon1.remise =  "0.00";
      bon1.remise_ckecked = false;
    }

    if(checkBox_timbre.isChecked()){
      bon1.timbre =  String.valueOf(val_timbre);
      bon1.timbre_ckecked = true;
    }else{
      bon1.timbre =  "0.00";
      bon1.timbre_ckecked = false;
    }

    bon1.tot_ht = String.valueOf(val_total_ht);
    bon1.tot_tva = String.valueOf(val_tva);
    bon1.tot_ttc = String.valueOf(val_total_ttc);
    bon1.tot_ttc_remise = String.valueOf(val_total_ttc_remise);

    //update current bon1
    controller.update_bon1("Bon1_temp",bon1.num_bon, bon1);

  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == 55555){
      finish();
    }else if(requestCode == REQUEST_ACTIVITY_ADDPRODUCT){
      initData();
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  public void requestPermission(){

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
      checkPermission = false;
    }else{
      checkPermission = true;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == ACCES_FINE_LOCATION){
      startService(new Intent(this, ServiceLocation.class));
    }
    if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      return;
    }
    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      startScan();
      return;
    }
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int id) {
        dialog.cancel();
      }
    };
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Error")
            .setMessage(R.string.no_camera_permission)
            .setPositiveButton(android.R.string.ok, listener)
            .show();

  }

  @Override
  public void onBackPressed() {
    Sound( R.raw.back);
    super.onBackPressed();
  }

  public void Sound(int resid){
    mp = MediaPlayer.create(this, resid);
    mp.start();
  }

  @Subscribe
  public void onEvent(LocationEvent event){

    Log.e("TRACKKK", "Recieved location commande : " +  event.getLocationData().getLatitude() + "  //  " + event.getLocationData().getLongitude());

    bon1.latitude = event.getLocationData().getLatitude();
    bon1.longitude = event.getLocationData().getLongitude();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putParcelable(BARCODE_KEY, barcodeResult);
    super.onSaveInstanceState(outState);
  }

  private void startScan() {
    /**
     * Build a new MaterialBarcodeScanner
     */

    final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
            .withActivity(ActivityEditCommande.this)
            .withEnableAutoFocus(true)
            .withBleepEnabled(true)
            .withBackfacingCamera()
            .withCenterTracker()
            .withText("Scanning...")
            .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
              @Override
              public void onResult(Barcode barcode) {
                Sound(R.raw.bleep);
                barcodeResult = barcode;
                // result.setText(barcode.rawValue);
                Toast.makeText(ActivityEditCommande.this, ""+barcode.rawValue, Toast.LENGTH_SHORT).show();
                // Do search after barcode scanned
                //setRecycle(barcode.rawValue, true);

                PostData_Client client = new PostData_Client();
                client = controller.select_client_from_database(barcode.rawValue);
                if(client.client != null){
                  onClientSelected(client);
                }else {
                  Crouton.makeText(ActivityEditCommande.this, "Client non exist sur le terminal", Style.ALERT).show();
                }
              }
            })
            .build();
    materialBarcodeScanner.startScan();
  }

  @Override
  protected void onDestroy() {
    // Unregister
    bus.unregister(this);
    stopService(intent_location);
    super.onDestroy();
  }
}

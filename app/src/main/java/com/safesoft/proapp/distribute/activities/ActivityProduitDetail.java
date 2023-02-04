package com.safesoft.proapp.distribute.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.PrinterVente;

import java.text.DecimalFormat;

public class ActivityProduitDetail extends AppCompatActivity {

  private PostData_Produit produit;
  private ImageView ImgProduit, ImgProduit2;
  private TextView TvCodebarre, TvReference, TvProduit, TvPv1, TvPv2, TvPv3, TvTva, TvAStock , Colissage, DETAILLE;

  private  MediaPlayer mp;

  private String PREFS_AUTRE = "ConfigAutre";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_produit_detail);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    produit = new PostData_Produit();


    produit.code_barre = getIntent().getStringExtra("CODE_BARRE");
    produit.ref_produit = getIntent().getStringExtra("REF_PRODUIT");
    produit.produit = getIntent().getStringExtra("PRODUIT");
    produit.pa_ht = getIntent().getDoubleExtra("PA",0);
    produit.tva = getIntent().getDoubleExtra("TVA",0);
    produit.pv1_ht = getIntent().getDoubleExtra("PV1_HT",0);
    produit.pv2_ht = getIntent().getDoubleExtra("PV2_HT",0);
    produit.pv3_ht = getIntent().getDoubleExtra("PV3_HT",0);
    produit.stock = getIntent().getDoubleExtra("STOCK",0);
    produit.colissage = getIntent().getDoubleExtra("COLISSAGE", 0);
    produit.photo = getIntent().getByteArrayExtra("PHOTO");
    produit.DETAILLE = getIntent().getStringExtra("DETAILLE");

    initViews();

    iniData(produit);
  }

  protected void initViews() {
    //Image
    ImgProduit = (ImageView) findViewById(R.id.imageProduit);
    //ImgProduit2 = (ImageView) findViewById(R.id.imageproduit2);

    //TextView
    TvCodebarre = (TextView) findViewById(R.id.codebarre);
    TvReference = (TextView) findViewById(R.id.reference);
    TvProduit = (TextView) findViewById(R.id.produit);
    TvPv1 = (TextView) findViewById(R.id.pv1);
    TvPv2 = (TextView) findViewById(R.id.pv2);
    TvPv3 = (TextView) findViewById(R.id.pv3);
    TvTva = (TextView) findViewById(R.id.tva);
    TvAStock = (TextView) findViewById(R.id.stock);
    Colissage = (TextView) findViewById(R.id.colissage);
    DETAILLE = (TextView) findViewById(R.id.DETAILLE);
  }

  protected void iniData(PostData_Produit produit) {
    if(produit.photo != null) {
      ImgProduit.setImageBitmap(BitmapFactory.decodeByteArray(produit.photo, 0, produit.photo.length));
      //ImgProduit2.setImageBitmap(BitmapFactory.decodeByteArray(produit.photo, 0, produit.photo.length));
    }
    else{
      ImgProduit.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.noimg));
      //ImgProduit2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.noimg));
    }

    if (produit.code_barre != null)
      TvCodebarre.setText(produit.code_barre.toString());

    if (produit.ref_produit != null)
      TvReference.setText(produit.ref_produit.toString());

    if (produit.produit != null)
      TvProduit.setText(produit.produit.toString());

    SharedPreferences prefs = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
    if(prefs.getString("PV_ID", "PV1").equals("PV1")){
      TvPv1.setVisibility(View.VISIBLE);
      TvPv2.setVisibility(View.GONE);
      TvPv3.setVisibility(View.GONE);
    }else if(prefs.getString("PV_ID", "PV1").equals("PV2")){
      TvPv1.setVisibility(View.GONE);
      TvPv2.setVisibility(View.VISIBLE);
      TvPv3.setVisibility(View.GONE);
    }else{
      TvPv1.setVisibility(View.GONE);
      TvPv2.setVisibility(View.GONE);
      TvPv3.setVisibility(View.VISIBLE);
    }

    //if (produit.pv1_ht != null)
      TvPv1.setText(" "+produit.pv1_ht);

    //if (produit.pv2_ht != null)
      TvPv2.setText(" "+produit.pv2_ht);

    //if (produit.pv3_ht != null)
      TvPv3.setText(" "+produit.pv3_ht);


    //if (produit.tva != null)
      TvTva.setText(" "+ produit.tva);

    //if (produit.stock != null)
      TvAStock.setText(" "+ new DecimalFormat("##,##0.##").format(Double.valueOf(produit.stock)));

    Colissage.setText(" "+ new DecimalFormat("##,##0.##").format(produit.colissage));

    if (produit.DETAILLE != null) {
      DETAILLE.setText(" " + produit.DETAILLE.toString());
    } else {
      Toast.makeText(getApplicationContext(),"non",Toast.LENGTH_LONG).show();
    }
  }

  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btnVerser:

        break;
      case R.id.btnVente:

        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_produit_details, menu);

    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.print){
      Activity bactivity;
      bactivity = ActivityProduitDetail.this;

      PrinterVente printer = new PrinterVente();
      printer.start_print_etiquette(bactivity, produit );

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




}

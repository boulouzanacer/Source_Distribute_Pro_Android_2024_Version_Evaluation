package com.safesoft.proapp.distribute.activities;

import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;

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
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));

    produit = new PostData_Produit();


    produit.code_barre = getIntent().getStringExtra("CODE_BARRE");
    produit.ref_produit = getIntent().getStringExtra("REF_PRODUIT");
    produit.produit = getIntent().getStringExtra("PRODUIT");
    produit.pa_ht = getIntent().getStringExtra("PA");
    produit.tva = getIntent().getStringExtra("TVA");
    produit.pv1_ht = getIntent().getStringExtra("PV1_HT");
    produit.pv2_ht = getIntent().getStringExtra("PV2_HT");
    produit.pv3_ht = getIntent().getStringExtra("PV3_HT");
    produit.stock = getIntent().getStringExtra("STOCK");
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

    if (produit.pv1_ht != null)
      TvPv1.setText(" "+produit.pv1_ht.toString());

    if (produit.pv2_ht != null)
      TvPv2.setText(" "+produit.pv2_ht.toString());

    if (produit.pv3_ht != null)
      TvPv3.setText(" "+produit.pv3_ht.toString());


    if (produit.tva != null)
      TvTva.setText(" "+ produit.tva.toString());

    if (produit.stock != null)
      TvAStock.setText(" "+ new DecimalFormat("##,##0.00").format(Double.valueOf(produit.stock.toString())));

    Colissage.setText(" "+ new DecimalFormat("##,##0.00").format(produit.colissage));

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
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
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

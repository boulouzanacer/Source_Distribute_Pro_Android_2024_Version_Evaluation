package com.safesoft.proapp.distribute.activities.product;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.SpannableString;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import cn.nekocode.badge.BadgeDrawable;

public class ActivityProduitDetail extends AppCompatActivity {

  private PostData_Produit produit;
  private ImageView ImgProduit;
  private TextView TvCodebarre, TvReference, TvProduit, TvPa_ht, TvPamp,
          TvPv1, TvPv2, TvPv3, TvPv4, TvPv5, TvPv6,
          TvPv1_title, TvPv2_title, TvPv3_title, TvPv4_title, TvPv5_title, TvPv6_title,
          TvTva, TvStock , Colissage, Description;

  private LinearLayout Lnr_pv1, Lnr_pv2, Lnr_pv3, Lnr_pv4, Lnr_pv5, Lnr_pv6;
  private  MediaPlayer mp;
  private NumberFormat nf;
  private final String PREFS = "ALL_PREFS";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_produit_detail);

    produit = new PostData_Produit();

    produit.code_barre = getIntent().getStringExtra("CODE_BARRE");
    produit.ref_produit = getIntent().getStringExtra("REF_PRODUIT");
    produit.produit = getIntent().getStringExtra("PRODUIT");
    produit.pa_ht = getIntent().getDoubleExtra("PA_HT",0);
    produit.pamp = getIntent().getDoubleExtra("PAMP",0);
    produit.tva = getIntent().getDoubleExtra("TVA",0);
    produit.pv1_ht = getIntent().getDoubleExtra("PV1_HT",0);
    produit.pv2_ht = getIntent().getDoubleExtra("PV2_HT",0);
    produit.pv3_ht = getIntent().getDoubleExtra("PV3_HT",0);
    produit.pv4_ht = getIntent().getDoubleExtra("PV4_HT",0);
    produit.pv5_ht = getIntent().getDoubleExtra("PV5_HT",0);
    produit.pv6_ht = getIntent().getDoubleExtra("PV6_HT",0);
    produit.stock = getIntent().getDoubleExtra("STOCK",0);
    produit.colissage = getIntent().getDoubleExtra("COLISSAGE", 0);
    produit.photo = getIntent().getByteArrayExtra("PHOTO");
    produit.description = getIntent().getStringExtra("DESCRIPTION");

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Détails produit");

    initViews();

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("####0.##");

    iniData(produit);
  }

  protected void initViews() {
    //Image
    ImgProduit = (ImageView) findViewById(R.id.imageProduit);

    //TextView
    TvCodebarre = (TextView) findViewById(R.id.codebarre);
    TvReference = (TextView) findViewById(R.id.reference);
    TvProduit = (TextView) findViewById(R.id.produit);
    TvPa_ht = (TextView) findViewById(R.id.pa_ht);
    TvPamp = (TextView) findViewById(R.id.pamp);

    Lnr_pv1 = (LinearLayout) findViewById(R.id.lnr_pv1);
    Lnr_pv2 = (LinearLayout) findViewById(R.id.lnr_pv2);
    Lnr_pv3 = (LinearLayout) findViewById(R.id.lnr_pv3);
    Lnr_pv4 = (LinearLayout) findViewById(R.id.lnr_pv4);
    Lnr_pv5 = (LinearLayout) findViewById(R.id.lnr_pv5);
    Lnr_pv6 = (LinearLayout) findViewById(R.id.lnr_pv6);

    TvPv1 = (TextView) findViewById(R.id.pv1);
    TvPv2 = (TextView) findViewById(R.id.pv2);
    TvPv3 = (TextView) findViewById(R.id.pv3);
    TvPv4 = (TextView) findViewById(R.id.pv4);
    TvPv5 = (TextView) findViewById(R.id.pv5);
    TvPv6 = (TextView) findViewById(R.id.pv6);

    TvPv1_title = (TextView) findViewById(R.id.pv1_title);
    TvPv2_title = (TextView) findViewById(R.id.pv2_title);
    TvPv3_title = (TextView) findViewById(R.id.pv3_title);
    TvPv4_title = (TextView) findViewById(R.id.pv4_title);
    TvPv5_title = (TextView) findViewById(R.id.pv5_title);
    TvPv6_title = (TextView) findViewById(R.id.pv6_title);

    TvTva = (TextView) findViewById(R.id.tva);
    TvStock = (TextView) findViewById(R.id.stock);
    Colissage = (TextView) findViewById(R.id.colissage);
    Description = (TextView) findViewById(R.id.Description);
  }

  protected void iniData(PostData_Produit produit) {

    SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

    if(produit.photo != null) {
      ImgProduit.setImageBitmap(BitmapFactory.decodeByteArray(produit.photo, 0, produit.photo.length));
    }
    else{
      ImgProduit.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.noimg));
    }

    if (produit.code_barre != null)
      TvCodebarre.setText(produit.code_barre);

    if (produit.ref_produit != null)
      TvReference.setText(produit.ref_produit);

    if (produit.produit != null)
      TvProduit.setText(produit.produit);

    TvPv1_title.setText(prefs.getString("PV1_TITRE","Prix 1") + " (HT)");
    TvPv2_title.setText(prefs.getString("PV2_TITRE","Prix 2") + " (HT)");
    TvPv3_title.setText(prefs.getString("PV3_TITRE","Prix 3") + " (HT)");
    TvPv4_title.setText(prefs.getString("PV4_TITRE","Prix 4") + " (HT)");
    TvPv5_title.setText(prefs.getString("PV5_TITRE","Prix 5") + " (HT)");
    TvPv6_title.setText(prefs.getString("PV6_TITRE","Prix 6") + " (HT)");

    final BadgeDrawable drawable_pa_ht = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff5CD85A)
            .text1(nf.format(produit.pa_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pa_ht = new SpannableString(TextUtils.concat(drawable_pa_ht.toSpannable()));
    TvPa_ht.setText(spannableString_pa_ht);

    if(prefs.getBoolean("AFFICHAGE_PA_HT", false)){
      TvPa_ht.setVisibility(View.VISIBLE);
    }else {
      TvPa_ht.setVisibility(View.GONE);
    }

    final BadgeDrawable drawable_pamp = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff5CD85A)
            .text1(nf.format(produit.pamp))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pamp = new SpannableString(TextUtils.concat(drawable_pamp.toSpannable()));
    TvPamp.setText(spannableString_pamp);

    final BadgeDrawable drawable_pv1 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv1_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv1 = new SpannableString(TextUtils.concat(drawable_pv1.toSpannable()));
    TvPv1.setText(spannableString_pv1);

    final BadgeDrawable drawable_pv2 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv2_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv2 = new SpannableString(TextUtils.concat(drawable_pv2.toSpannable()));
    TvPv2.setText(spannableString_pv2);

    final BadgeDrawable drawable_pv3 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv3_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv3 = new SpannableString(TextUtils.concat(drawable_pv3.toSpannable()));
    TvPv3.setText(spannableString_pv3);

    final BadgeDrawable drawable_pv4 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv4_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv4 = new SpannableString(TextUtils.concat(drawable_pv4.toSpannable()));
    TvPv4.setText(spannableString_pv4);

    final BadgeDrawable drawable_pv5 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv5_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv5 = new SpannableString(TextUtils.concat(drawable_pv5.toSpannable()));
    TvPv5.setText(spannableString_pv5);

    final BadgeDrawable drawable_pv6 = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xff303F9F)
            .text1(nf.format(produit.pv6_ht))
            .textSize(28f)
            .text2(" DA ")
            .build();
    SpannableString spannableString_pv6 = new SpannableString(TextUtils.concat(drawable_pv6.toSpannable()));
    TvPv6.setText(spannableString_pv6);



    final BadgeDrawable drawable_tva = new BadgeDrawable.Builder()
            .type(BadgeDrawable.TYPE_WITH_TWO_TEXT_COMPLEMENTARY)
            .badgeColor(0xffE74C3C)
            .text1(nf.format(produit.tva))
            .textSize(28f)
            .text2(" % ")
            .build();
    SpannableString spannableString_tva = new SpannableString(TextUtils.concat(drawable_tva.toSpannable()));
    TvTva.setText(spannableString_tva);

    TvStock.setText( new DecimalFormat("##,##0.##").format(Double.valueOf(produit.stock)));
    Colissage.setText("Colissage : " + new DecimalFormat("##,##0.##").format(produit.colissage));

    if (produit.description != null) {
      Description.setText(produit.description);
    }

    if(prefs.getString("PRIX_2","").equals("1")){
      Lnr_pv2.setVisibility(View.VISIBLE);
    }else{
      Lnr_pv2.setVisibility(View.GONE);
    }

    if(prefs.getString("PRIX_3","").equals("1")){
      Lnr_pv3.setVisibility(View.VISIBLE);
    }else{
      Lnr_pv3.setVisibility(View.GONE);
    }

    if(prefs.getString("PRIX_4","").equals("1")){
      Lnr_pv4.setVisibility(View.VISIBLE);
    }else{
      Lnr_pv4.setVisibility(View.GONE);
    }

    if(prefs.getString("PRIX_5","").equals("1")){
      Lnr_pv5.setVisibility(View.VISIBLE);
    }else{
      Lnr_pv5.setVisibility(View.GONE);
    }

    if(prefs.getString("PRIX_6","").equals("1")){
      Lnr_pv6.setVisibility(View.VISIBLE);
    }else{
      Lnr_pv6.setVisibility(View.GONE);
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

      Printing printer = new Printing();
      printer.start_print_etiquette(bactivity, produit );

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




}

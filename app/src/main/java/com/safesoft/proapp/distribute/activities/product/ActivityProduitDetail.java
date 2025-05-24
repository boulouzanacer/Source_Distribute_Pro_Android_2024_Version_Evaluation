package com.safesoft.proapp.distribute.activities.product;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class ActivityProduitDetail extends AppCompatActivity {

    private PostData_Produit produit;
    private ImageView ImgProduit;
    private TextView TvCodebarre, TvReference, TvProduit, TvPa_ht, TvPamp,
            TvPv1, TvPv2, TvPv3, TvPv4, TvPv5, TvPv6,
            TvPv1_title, TvPv2_title, TvPv3_title, TvPv4_title, TvPv5_title, TvPv6_title,
            TvTva, TvStock, Colissage, Description;

    ImagePopup imagePopup;
    private LinearLayout Lnr_pv1, Lnr_pv2, Lnr_pv3, Lnr_pv4, Lnr_pv5, Lnr_pv6;
    private MediaPlayer mp;
    private NumberFormat nf;
    SharedPreferences prefs;
    private DATABASE controller;
    private final String PREFS = "ALL_PREFS";
    private int position_item = 0;

    private boolean is_app_synchronised_mode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_produit_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        controller = new DATABASE(this);
        produit = new PostData_Produit();


        position_item = getIntent().getIntExtra("POSITION_ITEM", 0);
        produit.code_barre = getIntent().getStringExtra("CODE_BARRE");
        produit.ref_produit = getIntent().getStringExtra("REF_PRODUIT");
        produit.produit = getIntent().getStringExtra("PRODUIT");
        produit.pa_ht = getIntent().getDoubleExtra("PA_HT", 0);
        produit.pamp = getIntent().getDoubleExtra("PAMP", 0);
        produit.tva = getIntent().getDoubleExtra("TVA", 0);
        produit.pv1_ht = getIntent().getDoubleExtra("PV1_HT", 0);
        produit.pv2_ht = getIntent().getDoubleExtra("PV2_HT", 0);
        produit.pv3_ht = getIntent().getDoubleExtra("PV3_HT", 0);
        produit.pv4_ht = getIntent().getDoubleExtra("PV4_HT", 0);
        produit.pv5_ht = getIntent().getDoubleExtra("PV5_HT", 0);
        produit.pv6_ht = getIntent().getDoubleExtra("PV6_HT", 0);
        produit.pv1_ttc = produit.pv1_ht * (1 + produit.tva / 100);
        produit.pv2_ttc = produit.pv2_ht * (1 + produit.tva / 100);
        produit.pv3_ttc = produit.pv3_ht * (1 + produit.tva / 100);
        produit.pv4_ttc = produit.pv4_ht * (1 + produit.tva / 100);
        produit.pv5_ttc = produit.pv5_ht * (1 + produit.tva / 100);
        produit.pv6_ttc = produit.pv6_ht * (1 + produit.tva / 100);
        produit.stock = getIntent().getDoubleExtra("STOCK", 0);
        produit.colissage = getIntent().getDoubleExtra("COLISSAGE", 0);
        produit.stock_ini = getIntent().getDoubleExtra("STOCK_INI", 0);

        try{
            produit.photo = ActivityProduits.produits.get(position_item).photo;
        }catch (Exception e){
            produit.photo = null;
        }

        produit.description = getIntent().getStringExtra("DESCRIPTION");
        produit.promo = getIntent().getIntExtra("PROMO", 0);
        produit.d1 = getIntent().getStringExtra("D1");
        produit.d2 = getIntent().getStringExtra("D2");
        produit.pp1_ht = getIntent().getDoubleExtra("PP1_HT", 0);
        produit.qte_promo = getIntent().getDoubleExtra("QTE_PROMO", 0);


        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Détails produit");
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }
        initViews();

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.##");


        iniData(produit);
    }

    protected void initViews() {
        //Image
        ImgProduit = findViewById(R.id.imageProduit);
        imagePopup = new ImagePopup(ActivityProduitDetail.this);

        //TextView
        TvCodebarre = findViewById(R.id.codebarre);
        TvReference = findViewById(R.id.reference);
        TvProduit = findViewById(R.id.produit);
        TvPa_ht = findViewById(R.id.pa_ht);
        TvPamp = findViewById(R.id.pamp);

        Lnr_pv1 = findViewById(R.id.lnr_pv1);
        Lnr_pv2 = findViewById(R.id.lnr_pv2);
        Lnr_pv3 = findViewById(R.id.lnr_pv3);
        Lnr_pv4 = findViewById(R.id.lnr_pv4);
        Lnr_pv5 = findViewById(R.id.lnr_pv5);
        Lnr_pv6 = findViewById(R.id.lnr_pv6);

        TvPv1 = findViewById(R.id.pv1);
        TvPv2 = findViewById(R.id.pv2);
        TvPv3 = findViewById(R.id.pv3);
        TvPv4 = findViewById(R.id.pv4);
        TvPv5 = findViewById(R.id.pv5);
        TvPv6 = findViewById(R.id.pv6);

        TvPv1_title = findViewById(R.id.pv1_title);
        TvPv2_title = findViewById(R.id.pv2_title);
        TvPv3_title = findViewById(R.id.pv3_title);
        TvPv4_title = findViewById(R.id.pv4_title);
        TvPv5_title = findViewById(R.id.pv5_title);
        TvPv6_title = findViewById(R.id.pv6_title);

        TvTva = findViewById(R.id.tva);
        TvStock = findViewById(R.id.stock);
        Colissage = findViewById(R.id.colissage);
        Description = findViewById(R.id.Description);
    }

    protected void iniData(PostData_Produit produit) {

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        is_app_synchronised_mode = prefs.getBoolean("APP_SYNCHRONISED_MODE", false);

        PostData_Params params = new PostData_Params();
        params = controller.select_params_from_database("SELECT * FROM PARAMS");


        if (prefs.getBoolean("SHOW_PROD_PIC", false)) {
            if (produit.photo != null) {
                ImgProduit.setImageBitmap(BitmapFactory.decodeByteArray(produit.photo, 0, produit.photo.length));
            }
        }

        imagePopup.setWindowHeight(800); // Optional
        imagePopup.setWindowWidth(800); // Optional
        imagePopup.setBackgroundColor(Color.BLACK);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional
        imagePopup.initiatePopup(ImgProduit.getDrawable()); // Load Image from Drawable

        if (produit.code_barre != null)
            TvCodebarre.setText(produit.code_barre);

        if (produit.ref_produit != null)
            TvReference.setText(produit.ref_produit);

        if (produit.produit != null)
            TvProduit.setText(produit.produit);


        TvPv1_title.setText(params.pv1_titre + " (TTC)");
        TvPv2_title.setText(params.pv2_titre + " (TTC)");
        TvPv3_title.setText(params.pv3_titre + " (TTC)");
        TvPv4_title.setText(params.pv4_titre + " (TTC)");
        TvPv5_title.setText(params.pv5_titre + " (TTC)");
        TvPv6_title.setText(params.pv6_titre + " (TTC)");


        if (prefs.getBoolean("AFFICHAGE_PA_HT", false)) {
            TvPa_ht.setVisibility(View.VISIBLE);
            TvPamp.setVisibility(View.VISIBLE);
        } else {
            TvPa_ht.setVisibility(View.GONE);
            TvPamp.setVisibility(View.GONE);
        }

        TvPa_ht.setText(nf.format(produit.pa_ht * (1 + produit.tva / 100)) + " DA");
        TvPamp.setText(nf.format(produit.pamp * (1 + produit.tva / 100)) + " DA");
        TvPv1.setText(nf.format(produit.pv1_ttc) + " DA");
        TvPv2.setText(nf.format(produit.pv2_ttc) + " DA");
        TvPv3.setText(nf.format(produit.pv3_ttc) + " DA");
        TvPv4.setText(nf.format(produit.pv4_ttc) + " DA");
        TvPv5.setText(nf.format(produit.pv5_ttc) + " DA");
        TvPv6.setText(nf.format(produit.pv6_ttc) + " DA");
        TvTva.setText(nf.format(produit.tva) + " % ");

        TvStock.setText(new DecimalFormat("##,##0.##").format(Double.valueOf(produit.stock)));
        Colissage.setText("Colissage : " + new DecimalFormat("##,##0.##").format(produit.colissage));

        if (produit.description != null) {
            Description.setText(produit.description);
        }


        String selectedTitle = prefs.getString("PRIX_REVENDEUR", "Libre");

        if (selectedTitle.equals("Libre")) {
            // Show Lnr_pvX if conditions are met
            Lnr_pv1.setVisibility(View.VISIBLE); // Always visible in Libre mode

            if (params.prix_2 == 1 && is_app_synchronised_mode) {
                Lnr_pv2.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv2.setVisibility(View.GONE);
            }

            if (params.prix_3 == 1 && is_app_synchronised_mode) {
                Lnr_pv3.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv3.setVisibility(View.GONE);
            }

            if (params.prix_4 == 1) {
                Lnr_pv4.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv4.setVisibility(View.GONE);
            }

            if (params.prix_5 == 1) {
                Lnr_pv5.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv5.setVisibility(View.GONE);
            }

            if (params.prix_6 == 1) {
                Lnr_pv6.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv6.setVisibility(View.GONE);
            }

        } else {
            // Show only the matched pvX_titre
            Lnr_pv1.setVisibility(selectedTitle.equals(params.pv1_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv2.setVisibility(selectedTitle.equals(params.pv2_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv3.setVisibility(selectedTitle.equals(params.pv3_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv4.setVisibility(selectedTitle.equals(params.pv4_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv5.setVisibility(selectedTitle.equals(params.pv5_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv6.setVisibility(selectedTitle.equals(params.pv6_titre) ? View.VISIBLE : View.GONE);
        }

        ImgProduit.setOnClickListener(v -> imagePopup.viewPopup());
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.print) {
            Activity bactivity;
            bactivity = ActivityProduitDetail.this;
            Printing printer = new Printing();
            prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (prefs.getString("PRINTER_TYPE", "IMP_TICKET").equals("IMP_TICKET")) {
                printer.start_print_etiquette(bactivity, produit);
            } else {
                printer.start_print_etiquette_code_barre(bactivity, produit);
            }


        } else if (item.getItemId() == R.id.pdf) {
            Activity mActivity;
            mActivity = ActivityProduitDetail.this;

            GeneratePDF generate_pdf = new GeneratePDF();
            generate_pdf.startPDFTicket(mActivity, produit.code_barre, produit.produit, produit.pv1_ht, "FROM_TICKET");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound();
        }
        super.onBackPressed();
    }

    public void Sound() {
        mp = MediaPlayer.create(this, R.raw.back);
        mp.start();
    }


}

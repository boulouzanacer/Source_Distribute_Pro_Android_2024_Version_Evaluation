package com.safesoft.proapp.distribute.activities.product;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;

import java.io.File;
import java.io.FileOutputStream;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            //  WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }

        controller = new DATABASE(this);
        produit = new PostData_Produit();


        position_item = getIntent().getIntExtra("POSITION_ITEM", 0);
        produit.produit_id = getIntent().getStringExtra("PRODUCT_ID");
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

        PostData_Params params;
        params = controller.select_params_from_database("SELECT * FROM PARAMS");


        if (prefs.getBoolean("SHOW_PROD_PIC", false)) {
            Bitmap bmp  = controller.getProductPhotoBitmap(produit.produit_id.toString());
            if(bmp != null){
                ImgProduit.setImageBitmap(bmp);
            }else{
                ImgProduit.setImageResource(R.drawable.ic_camera_24);
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


        String prix_revendeur = prefs.getString("PRIX_REVENDEUR", "Libre");

        if (prix_revendeur.equals("Libre")) {
            // Show Lnr_pvX if conditions are met
            Lnr_pv1.setVisibility(View.VISIBLE); // Always visible in Libre mode

            if (params.prix_2 == 1) {
                Lnr_pv2.setVisibility(View.VISIBLE);
            } else {
                Lnr_pv2.setVisibility(View.GONE);
            }

            if (params.prix_3 == 1) {
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
            Lnr_pv1.setVisibility(prix_revendeur.equals(params.pv1_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv2.setVisibility(prix_revendeur.equals(params.pv2_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv3.setVisibility(prix_revendeur.equals(params.pv3_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv4.setVisibility(prix_revendeur.equals(params.pv4_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv5.setVisibility(prix_revendeur.equals(params.pv5_titre) ? View.VISIBLE : View.GONE);
            Lnr_pv6.setVisibility(prix_revendeur.equals(params.pv6_titre) ? View.VISIBLE : View.GONE);
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
            /*Activity bactivity;
            bactivity = ActivityProduitDetail.this;
            Printing printer = new Printing();
            prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (prefs.getString("PRINTER_TYPE", "IMP_TICKET").equals("IMP_TICKET")) {
                printer.start_print_etiquette(bactivity, produit);
            } else {
                printer.start_print_etiquette_code_barre(bactivity, produit);
            }*/

            File pdf = createPDF(produit.produit, produit.code_barre, String.valueOf(produit.pv1_ht + (produit.pv1_ht * produit.tva / 100)));

            sendToBarcode4(pdf);


        } else if (item.getItemId() == R.id.pdf) {
            Activity mActivity;
            mActivity = ActivityProduitDetail.this;

            GeneratePDF generate_pdf = new GeneratePDF();
            generate_pdf.startPDFTicket(mActivity, produit.code_barre, produit.produit, produit.pv1_ht, "FROM_TICKET");
        }
        return super.onOptionsItemSelected(item);
    }

    private File createPDF(
            String productName,
            String barcodeText,
            String price
    )  {

        File pdfFile = new File(getExternalFilesDir(null), "barcode.pdf");

        int width = (int)(40 * 72 / 25.4);   // 113 pt
        int height = (int)(20 * 72 / 25.4);  // 56 pt

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();


        PdfDocument.Page page = document.startPage(pageInfo);


        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        /*
        Nom produit
        */

        paint.setTextSize(8);

        canvas.drawText(
                productName,
                5,
                10,
                paint
        );

        /*
        Génération code barre
        */

        Bitmap barcodeBitmap = generateBarcode(barcodeText);

        Rect src = new Rect(
                0,
                0,
                barcodeBitmap.getWidth(),
                barcodeBitmap.getHeight()
        );


        Rect dst = new Rect(
                5,
                14,
                108,
                35
        );


        canvas.drawBitmap(
                barcodeBitmap,
                src,
                dst,
                paint
        );

        /*
        Texte code barre
    */

        paint.setTextSize(5);

        canvas.drawText(
                barcodeText,
                30,
                40,
                paint
        );



    /*
        Prix
    */

        paint.setTextSize(10);
        paint.setFakeBoldText(true);


        canvas.drawText(
                price + " DA",
                20,
                50,
                paint
        );

        document.finishPage(page);

        try {
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            fos.close();

        } catch(Exception e){
            e.printStackTrace();
        }
        document.close();
        return pdfFile;
    }

    private void sendToBarcode4(File pdfFile){


        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName()+".provider",
                pdfFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // Forcer l'ouverture avec 4Barcode
        intent.setPackage("com.xinye.barcode4");

        try {
            startActivity(intent);
        }
        catch(Exception e){
            Toast.makeText(this, "4Barcode non installé", Toast.LENGTH_LONG).show();
        }

    }


    private Bitmap generateBarcode(String value)
    {

        try {


            BitMatrix matrix =
                    new MultiFormatWriter().encode(
                            value,
                            BarcodeFormat.CODE_128,
                            400,
                            120
                    );


            Bitmap bitmap =
                    Bitmap.createBitmap(
                            400,
                            120,
                            Bitmap.Config.RGB_565
                    );


            for(int x=0;x<400;x++){

                for(int y=0;y<120;y++){

                    bitmap.setPixel(
                            x,
                            y,
                            matrix.get(x,y)
                                    ? Color.BLACK
                                    : Color.WHITE
                    );

                }
            }


            return bitmap;


        }
        catch(Exception e){

            return null;

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}

package com.safesoft.proapp.distribute.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.webkit.WebView;
import android.widget.Button;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.pdf.GeneratePDF;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.eventsClasses.PrintEvent;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.Printing;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityHtmlView extends AppCompatActivity {

    private String TYPE_BON;
    private PostData_Bon1 bon1;
    private PostData_Achat1 achat1;
    private ArrayList<PostData_Bon2> final_panier;
    WebView webview;
    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    private String type_print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //https://stackoverflow.com/questions/26919262/android-4-4-print-to-pdf-without-user-involvement
        WebView.enableSlowWholeDocumentDraw();
        setContentView(R.layout.activity_html_view);

        bon1 = new PostData_Bon1();
        final_panier = new ArrayList<>();
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        webview = findViewById(R.id.webview);


        TYPE_BON = getIntent().getStringExtra("TYPE_BON");
        switch (TYPE_BON) {
            case "ACHAT" -> {
                achat1 = (PostData_Achat1) getIntent().getSerializableExtra("ACHAT1");
                type_print = "ACHAT";
                final_panier = (ArrayList<PostData_Bon2>) getIntent().getSerializableExtra("BON2");
                getSupportActionBar().setTitle("BON ACHAT : " + achat1.num_bon);
                webview.loadDataWithBaseURL("file:///android_asset/html/", prepareAchatHtml(), "text/html", "utf-8", null);
            }
            case "VENTE" -> {
                bon1 = (PostData_Bon1) getIntent().getSerializableExtra("BON1");
                type_print = "VENTE";
                final_panier = (ArrayList<PostData_Bon2>) getIntent().getSerializableExtra("BON2");
                getSupportActionBar().setTitle("BON VENTE : " + bon1.num_bon);
                webview.loadDataWithBaseURL("file:///android_asset/html/", prepareSaleHtml(), "text/html", "utf-8", null);
            }
            case "COMMANDE" -> {
                bon1 = (PostData_Bon1) getIntent().getSerializableExtra("BON1");
                type_print = "COMMANDE";
                final_panier = (ArrayList<PostData_Bon2>) getIntent().getSerializableExtra("BON2");
                getSupportActionBar().setTitle("BON COMMANDE : " + bon1.num_bon);
                webview.loadDataWithBaseURL("file:///android_asset/html/", prepareSaleHtml(), "text/html", "utf-8", null);
            }
        }

    }

    public void takeScreenshot() {

        // do your stuff here
        webview.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        webview.layout(0, 0, webview.getMeasuredWidth(), webview.getMeasuredHeight());
        webview.setDrawingCacheEnabled(true);
        webview.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(webview.getMeasuredWidth(), webview.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        webview.setDrawingCacheEnabled(false);

        Canvas bigcanvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        bigcanvas.drawBitmap(bitmap, 0, iHeight, paint);
        webview.draw(bigcanvas);

        File imageFile = new File(getExternalCacheDir(), "webview_capture1.jpg");

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            bitmap.recycle();
        } catch (IOException e) {
            Log.e("eeeeee", e.getMessage());
        }
    }


    String prepareAchatHtml() {
        StringBuilder data = new StringBuilder();
        data.append("""
                <HTML dir="rtl" lang="ar"><head>
                <link rel="stylesheet" href="mystyle.css">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body>""");

        data.append("<center>");

        String img_str = prefs.getString("COMPANY_LOGO", "");
        if (!img_str.equals("")) {
            data.append("<img src='data:image/bmp;base64,").append(img_str).append("' width='300' height='100'>");
        }


        data.append("<h6>");
        data.append(prefs.getString("COMPANY_NAME", "")); ///////
        data.append("</h6>");

        data.append("<h3>");
        data.append(prefs.getString("ACTIVITY_NAME", "")); //////// Activity name
        data.append("</h3>");

        data.append("<h3>");
        data.append(prefs.getString("COMPANY_ADRESSE", "")); //////// adresse
        data.append("</h3>");

        data.append("<h3>");
        data.append(prefs.getString("COMPANY_TEL", "")); //////// telephone
        data.append("</h3>");

        data.append("</center>");

        data.append(addLine());

        data.append("<h4 style='text-align: left'>");
        data.append("التاريخ : ");
        data.append(achat1.date_bon).append(" ").append(achat1.heure); /////// client
        data.append("</h4>");

        data.append("<h4>");
        data.append("المورد : ");
        data.append(achat1.fournis); /////// client
        data.append("</h4>");

        data.append("<h4>");
        data.append("العنوان : ");
        data.append(achat1.adresse); /////// adresse client
        data.append("</h4>");

        data.append("<h4>");
        data.append("الهاتف : ");
        data.append(achat1.tel); /////// telephone
        data.append("</h4>");

        data.append("<center>");
        data.append("<h4>");
        data.append("وصف شراء رقم : ");
        data.append(achat1.num_bon); /////// num bon
        data.append("</h4>");
        data.append("</center>");

        data.append(addLine());

        data.append("<div class='row'>");
        data.append("<div class='product_title'>المنتوج</div>");
        data.append("<div class='quantity_title'>الكمية</div>");
        data.append("<div class='gratuit_title'>مجاني</div>");
        data.append("<div class='prix_u_title'>السعر</div>");
        data.append("</div>");

        data.append(addLine());

        for (int i = 0; i < final_panier.size(); i++) {
            data.append("<div class='row'>");
            // product value
            data.append("<div class='product_value'>");
            data.append(final_panier.get(i).produit);
            data.append("</div>");

            if (final_panier.get(i).nbr_colis != 0) {
                // nbrcolis value
                data.append("<div class='nbrcolis_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).nbr_colis));
                data.append("</div>");
                // X value
                data.append("<div class='x_value'>");
                data.append("X");
                data.append("</div>");
                // colissage value
                data.append("<div class='colissage_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).colissage));
                data.append("</div>");
                // equal value
                data.append("<div class='equal_value'>");
                data.append("=");
                data.append("</div>");
            } else {
                // nbrcolis value
                data.append("<div class='nbrcolis_value'>");
                data.append("");
                data.append("</div>");
                // X value
                data.append("<div class='x_value'>");
                data.append("");
                data.append("</div>");
                // colissage value
                data.append("<div class='colissage_value'>");
                data.append("");
                data.append("</div>");
                // equal value
                data.append("<div class='equal_value'>");
                data.append("");
                data.append("</div>");
            }

            // quantity value
            data.append("<div class='quantity_value'>");
            data.append(new DecimalFormat("####0.##").format(final_panier.get(i).qte));
            data.append("</div>");

            if (final_panier.get(i).gratuit != 0) {
                // + value
                data.append("<div class='plus_value'>");
                data.append("+");
                data.append("</div>");
                // gratuit value
                data.append("<div class='gratuit_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).gratuit));
                data.append("</div>");
            } else {
                // + value
                data.append("<div class='plus_value'>");
                data.append("");
                data.append("</div>");
                data.append("<div class='gratuit_value'>");
                data.append("");
                data.append("</div>");
            }

            // X value
            data.append("<div class='x2_value'>");
            data.append("X");
            data.append("</div>");
            // price value
            data.append("<div class='prix_u_value'>");
            data.append(new DecimalFormat("####0.00").format(final_panier.get(i).pa_ht));
            data.append("</div>");
            data.append("</div>");


        }
        data.append(addLine());
        data.append("</center>");

        int nbr_produit;
        Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
        String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

        nbr_produit = final_panier.size();
        nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

        total_ht_bon = achat1.tot_ht;
        total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

        tva_bon = achat1.tot_tva;
        tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

        timbre_bon = achat1.timbre;
        timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

        total_bon = achat1.tot_ttc;
        total_bon_str = new DecimalFormat("####0.00").format(total_bon);

        remise_bon = achat1.remise;
        remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

        ancien_solde = achat1.solde_ancien;
        ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

        total_a_payer = achat1.montant_bon;
        total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

        versement = achat1.verser;
        versement_str = new DecimalFormat("####0.00").format(versement);

        nouveau_solde = achat1.reste;
        nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

        data.append("<div style='float:left' dir='ltr' lang='en'>");

        data.append("<table>");

        if (tva_bon != 0 && timbre_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_ht_bon_str);
            data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(tva_bon_str);
            data.append("</td><td class='td_class'>الضريبة</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(timbre_bon_str);
            data.append("</td><td class='td_class'>الطابع ج</td></tr>");
            data.append("</div>");

        } else if (tva_bon != 0) {
            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                data.append("<tr><td class='td_class'>");
                data.append(total_ht_bon_str);
                data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

                data.append("<tr><td class='td_class'>");
                data.append(tva_bon_str);
                data.append("</td><td class='td_class'>الضريبة</td></tr>");
            }
        } else if (timbre_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_ht_bon_str);
            data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(timbre_bon_str);
            data.append("</td><td class='td_class'>الطابع ج</td></tr>");

        }

        if (remise_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_bon_str);
            data.append("</td><td class='td_class'>المبلغ الاجمالي</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(remise_bon_str);
            data.append("</td><td class='td_class'>التخفيض</td></tr>");

        }

        data.append("<tr><td class='td_class'>");
        data.append(total_a_payer_str);
        data.append("</td><td class='td_class'>المبلغ الصافي</td></tr>");

        data.append("</table>");
        data.append("</div>");

        data.append("<table>");
        data.append("<tr style='text-align:center'>");
        data.append("<td style='font-weight: bold'>" + "عدد المنتوجات :").append(nbr_produit_str).append("</td></tr>");
        data.append("</table>");

        data.append(addLine());
        //============================================================
        data.append("<div style='float:right'>");

        data.append("<table>");

        data.append("<tr><td style='text-align: left'>الرصيد القديم : </td>");
        data.append("<td style='text-align: right;font-weight: bold'>");
        data.append(ancien_solde_str);
        data.append("</td></tr>");

        data.append("<tr><td style='text-align: left'>مبلغ الدفع : </td>");
        data.append("<td style='text-align: right;font-weight: bold'>");
        data.append(total_a_payer_str);
        data.append("</td></tr>");

        data.append("<tr><td style='text-align: left'>القسط : </td>");
        data.append("<td style='text-align: right;font-weight: bold'>");
        data.append(versement_str);
        data.append("</td></tr>");

        data.append("<tr><td style='text-align: left'>الرصيد الجديد : </td>");
        data.append("<td style='text-align: right;font-weight: bold'>");
        data.append(nouveau_solde_str);
        data.append("</td></tr>");

        data.append("</table>");
        data.append(addLine());

        data.append("</div>");
        //============================================================

        //data.append("<center>");
        // codebarre
        //data.append("<span class='ean-barcode'>");
        //data.append(bon1.num_bon);
        //data.append("</span>");

        //data.append("</center>");
        data.append("<br>");

        data.append("</body></HTML>");

        return data.toString();
    }

    String prepareSaleHtml() {
        StringBuilder data = new StringBuilder();
        data.append("""
                <HTML dir="rtl" lang="ar"><head>
                <link rel="stylesheet" href="mystyle.css">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body>""");

        data.append("<center>");

        String img_str = prefs.getString("COMPANY_LOGO", "");
        if (!img_str.equals("")) {
            data.append("<img src='data:image/bmp;base64,").append(img_str).append("' width='300' height='100'>");
        }


        data.append("<h6>");
        data.append(prefs.getString("COMPANY_NAME", "")); ///////
        data.append("</h6>");

        data.append("<h3>");
        data.append(prefs.getString("ACTIVITY_NAME", "")); //////// Activity name
        data.append("</h3>");

        data.append("<h3>");
        data.append(prefs.getString("COMPANY_ADRESSE", "")); //////// adresse
        data.append("</h3>");

        data.append("<h3>");
        data.append(prefs.getString("COMPANY_TEL", "")); //////// telephone
        data.append("</h3>");

        data.append("</center>");

        data.append(addLine());

        data.append("<h4 style='text-align: left'>");
        data.append("التاريخ : ");
        data.append(bon1.date_bon).append(" ").append(bon1.heure); /////// client
        data.append("</h4>");

        data.append("<h4>");
        data.append("الزبون : ");
        data.append(bon1.client); /////// client
        data.append("</h4>");

        data.append("<h4>");
        data.append("العنوان : ");
        data.append(bon1.adresse); /////// adresse client
        data.append("</h4>");

        data.append("<h4>");
        data.append("الهاتف : ");
        data.append(bon1.tel); /////// telephone
        data.append("</h4>");

        data.append("<center>");
        data.append("<h4>");
        if (type_print.equals("VENTE")) {
            data.append("وصف بيع رقم : ");
        } else if (type_print.equals("ORDER")) {
            data.append("وصف طلب رقم : ");
        }
        data.append(bon1.num_bon); /////// num bon
        data.append("</h4>");
        data.append("</center>");

        data.append(addLine());

        data.append("<div class='row'>");
        data.append("<div class='product_title'>المنتوج</div>");
        data.append("<div class='quantity_title'>الكمية</div>");
        data.append("<div class='gratuit_title'>مجان</div>");
        data.append("<div class='prix_u_title'>السعر</div>");
        data.append("</div>");

        data.append(addLine());

        for (int i = 0; i < final_panier.size(); i++) {
            data.append("<div class='row'>");
            // product value
            data.append("<div class='product_value'>");
            data.append(final_panier.get(i).produit);
            data.append("</div>");

            if (final_panier.get(i).nbr_colis != 0) {
                // nbrcolis value
                data.append("<div class='nbrcolis_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).nbr_colis));
                data.append("</div>");
                // X value
                data.append("<div class='x_value'>");
                data.append("X");
                data.append("</div>");
                // colissage value
                data.append("<div class='colissage_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).colissage));
                data.append("</div>");
                // equal value
                data.append("<div class='equal_value'>");
                data.append("=");
                data.append("</div>");
            } else {
                // nbrcolis value
                data.append("<div class='nbrcolis_value'>");
                data.append("");
                data.append("</div>");
                // X value
                data.append("<div class='x_value'>");
                data.append("");
                data.append("</div>");
                // colissage value
                data.append("<div class='colissage_value'>");
                data.append("");
                data.append("</div>");
                // equal value
                data.append("<div class='equal_value'>");
                data.append("");
                data.append("</div>");
            }

            // quantity value
            data.append("<div class='quantity_value'>");
            data.append(new DecimalFormat("####0.##").format(final_panier.get(i).qte));
            data.append("</div>");

            if (final_panier.get(i).gratuit != 0) {
                // + value
                data.append("<div class='plus_value'>");
                data.append("+");
                data.append("</div>");
                // gratuit value
                data.append("<div class='gratuit_value'>");
                data.append(new DecimalFormat("####0.##").format(final_panier.get(i).gratuit));
                data.append("</div>");
            } else {
                // + value
                data.append("<div class='plus_value'>");
                data.append("");
                data.append("</div>");
                data.append("<div class='gratuit_value'>");
                data.append("");
                data.append("</div>");
            }

            // X value
            data.append("<div class='x2_value'>");
            data.append("X");
            data.append("</div>");
            // price value
            data.append("<div class='prix_u_value'>");
            data.append(new DecimalFormat("####0.00").format(final_panier.get(i).pv_ht));
            data.append("</div>");
            data.append("</div>");


        }
        data.append(addLine());
        data.append("</center>");

        int nbr_produit;
        Double total_ht_bon, tva_bon, timbre_bon, total_bon, remise_bon, total_a_payer, ancien_solde, versement, nouveau_solde;
        String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;

        nbr_produit = final_panier.size();
        nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(nbr_produit));

        total_ht_bon = bon1.tot_ht;
        total_ht_bon_str = new DecimalFormat("####0.00").format(total_ht_bon);

        tva_bon = bon1.tot_tva;
        tva_bon_str = new DecimalFormat("####0.00").format(tva_bon);

        timbre_bon = bon1.timbre;
        timbre_bon_str = new DecimalFormat("####0.00").format(timbre_bon);

        total_bon = bon1.tot_ttc;
        total_bon_str = new DecimalFormat("####0.00").format(total_bon);

        remise_bon = bon1.remise;
        remise_bon_str = new DecimalFormat("####0.00").format(remise_bon);

        ancien_solde = bon1.solde_ancien;
        ancien_solde_str = new DecimalFormat("####0.00").format(ancien_solde);

        total_a_payer = bon1.montant_bon;
        total_a_payer_str = new DecimalFormat("####0.00").format(total_a_payer);

        versement = bon1.verser;
        versement_str = new DecimalFormat("####0.00").format(versement);

        nouveau_solde = bon1.reste;
        nouveau_solde_str = new DecimalFormat("####0.00").format(nouveau_solde);

        data.append("<div style='float:left' dir='ltr' lang='en'>");

        data.append("<table>");

        if (tva_bon != 0 && timbre_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_ht_bon_str);
            data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(tva_bon_str);
            data.append("</td><td class='td_class'>الضريبة</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(timbre_bon_str);
            data.append("</td><td class='td_class'>الطابع ج</td></tr>");
            data.append("</div>");

        } else if (tva_bon != 0) {

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                data.append("<tr><td class='td_class'>");
                data.append(total_ht_bon_str);
                data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

                data.append("<tr><td class='td_class'>");
                data.append(tva_bon_str);
                data.append("</td><td class='td_class'>الضريبة</td></tr>");
            }


        } else if (timbre_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_ht_bon_str);
            data.append("</td><td class='td_class'>المجموع ب.ض</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(timbre_bon_str);
            data.append("</td><td class='td_class'>الطابع ج</td></tr>");

        }

        if (remise_bon != 0) {

            data.append("<tr><td class='td_class'>");
            data.append(total_bon_str);
            data.append("</td><td class='td_class'>المبلغ الاجمالي</td></tr>");

            data.append("<tr><td class='td_class'>");
            data.append(remise_bon_str);
            data.append("</td><td class='td_class'>التخفيض</td></tr>");

        }

        data.append("<tr><td class='td_class'>");
        data.append(total_a_payer_str);
        data.append("</td><td class='td_class'>المبلغ الصافي</td></tr>");

        data.append("</table>");
        data.append("</div>");

        data.append("<table>");
        data.append("<tr style='text-align:center'>");
        data.append("<td style='font-weight: bold'>" + "عدد المنتوجات :").append(nbr_produit_str).append("</td></tr>");
        data.append("</table>");

        data.append(addLine());
        //============================================================
        data.append("<div style='float:right'>");

        data.append("<table>");

        data.append("<tr><td style='text-align: left'>الرصيد القديم : </td>");
        data.append("<td style='text-align: right;font-weight: bold'>");
        data.append(ancien_solde_str);
        data.append("</td></tr>");

        if (type_print.equals("VENTE")) {
            data.append("<tr><td style='text-align: left'>مبلغ الدفع : </td>");
            data.append("<td style='text-align: right;font-weight: bold'>");
            data.append(total_a_payer_str);
            data.append("</td></tr>");

            data.append("<tr><td style='text-align: left'>القسط : </td>");
            data.append("<td style='text-align: right;font-weight: bold'>");
            data.append(versement_str);
            data.append("</td></tr>");

            data.append("<tr><td style='text-align: left'>الرصيد الجديد : </td>");
            data.append("<td style='text-align: right;font-weight: bold'>");
            data.append(nouveau_solde_str);
            data.append("</td></tr>");
        }

        data.append("</table>");
        data.append(addLine());

        data.append("</div>");
        //============================================================

        String footer = prefs.getString("COMPANY_FOOTER", "");

        if (!footer.equals("")) {
            data.append("<center>");
            data.append(footer);
            data.append("</center>");
        }

        data.append("<br>");

        data.append("</body></HTML>");

        return data.toString();
    }

    String addLine() {
        return "<h4>--------------------------------------------------------</h4>";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_html, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.print) {
            takeScreenshot();
            Activity bactivity;
            bactivity = ActivityHtmlView.this;
            Printing printer = new Printing();
            try {
                printer.start_print_bon_vente(bactivity, "ARABIC", null, null);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
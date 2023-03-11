package com.safesoft.proapp.distribute.pdf;

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.rt.printerlibrary.enumerate.SettingEnum;
import com.safesoft.proapp.distribute.BuildConfig;
import com.safesoft.proapp.distribute.MainActivity;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityPDF;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GeneratePDF {

    Activity mActivity;

    // declaring width and height
    // for our PDF file.
    int pageHeight = 1122;
    int pagewidth = 792;

    int mergeleft = 38;
    int endright = 754;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    SharedPreferences prefs;
    private String PREFS = "ALL_PREFS";

    private final List<String> NO_PERMISSION = new ArrayList<String>();
    private final String[] NEED_PERMISSION = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CAMERA = 0;

    PostData_Bon1 Bon1;
    ArrayList<PostData_Bon2> final_panier;
    String SOURCE;

    public void startPDF(Activity mActivity, PostData_Bon1 Bon1, ArrayList<PostData_Bon2> final_panier, String SOURCE){
        this.mActivity = mActivity;
        this.Bon1 = Bon1;
        this.final_panier = final_panier;
        this.SOURCE = SOURCE;
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);

        // checking our permissions.
       // CheckAllPermission();

        generatePDF();
    }

    private void generatePDF() {
        // creating an object variable
        // for our PDF document.
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();

        String img_str= prefs.getString("COMPANY_LOGO", "");
        if (!img_str.equals("")){
            //decode string to image
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
            canvas.drawBitmap(scaledbmp, 610, 0, paint);
        }

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(17);

        if(!prefs.getString("COMPANY_NAME", "").equals("")){
            canvas.drawText(prefs.getString("COMPANY_NAME", ""), mergeleft, 30, title);
        }
        if(!prefs.getString("ACTIVITY_NAME", "").equals("")){
            canvas.drawText(prefs.getString("ACTIVITY_NAME", ""), mergeleft, 60, title);
        }
        if(!prefs.getString("COMPANY_ADRESSE", "").equals("")){
            canvas.drawText(prefs.getString("COMPANY_ADRESSE", ""), mergeleft, 90, title);
        }
        if(!prefs.getString("COMPANY_TEL", "").equals("")){
            canvas.drawText(prefs.getString("COMPANY_TEL", ""), mergeleft, 120, title);
        }

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        canvas.drawLine(mergeleft, 150,endright, 150, paint);
        canvas.drawLine(mergeleft, 153,endright, 153, paint);

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("BON DE LIVRAISON N°:" + Bon1.num_bon, mergeleft, 180, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);

        //title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Doit : ", 400, 180, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("" + Bon1.code_client, 400, 200, title);
        canvas.drawText("" + Bon1.client, 400, 220, title);
        canvas.drawText("" + Bon1.adresse, 400, 240, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("N.R.C : "+ Bon1.rc, 400, 260, title);
        canvas.drawText("N.I.S : "+ Bon1.nis, 600, 260, title);
        canvas.drawText("N.I.F : "+ Bon1.ifiscal, 400, 280, title);
        canvas.drawText("N.A.I : "+ Bon1.ai, 600, 280, title);

        title.setTextAlign(Paint.Align.RIGHT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Date : " + Bon1.date_bon + " " + Bon1.heure, endright, 180, title);

        title.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(10);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 300, title);
        canvas.drawText(createLine("N°",addCaracter(25, " ") + "DESIGNATION", "NBRE CO", "COLIS", "QTE", "U.G", "PRIX U" + addCaracter(3, " ")), mergeleft , 310 , title);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 320, title);

        double tot_qte = 0.0;
        int facture_y = 330;

        for(int i = 0; i < final_panier.size() ; i++){
            tot_qte = tot_qte + final_panier.get(i).qte;
            facture_y = 330 + (i * 20);

            String nc = "", colissage = "", gratuit = "";
            ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
            if(final_panier.get(i).nbr_colis != 0){
                nc = new DecimalFormat( "####0.##").format(final_panier.get(i).nbr_colis);
                colissage = new DecimalFormat( "####0.##").format(final_panier.get(i).colissage);
             }
            if(final_panier.get(i).gratuit != 0){
                gratuit = new DecimalFormat( "####0.##").format(final_panier.get(i).gratuit);
            }
            canvas.drawText(createLine(
                    String.valueOf(i + 1),
                    final_panier.get(i).produit ,
                    nc,
                    colissage,
                    new DecimalFormat( "####0.##").format(final_panier.get(i).qte), gratuit,
                    new DecimalFormat("####0.00").format(final_panier.get(i).p_u)),
                    mergeleft , facture_y , title);

            canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
        }


        String nbr_produit_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


        nbr_produit_str =  new DecimalFormat( "####0.##").format(Double.valueOf(final_panier.size()));
        total_bon_str   =  new DecimalFormat("####0.00").format(Bon1.tot_ttc);
        remise_bon_str   =  new DecimalFormat("####0.00").format(Bon1.remise);
        ancien_solde_str   =  new DecimalFormat("####0.00").format(Bon1.solde_ancien);
        total_a_payer_str   =  new DecimalFormat("####0.00").format(Bon1.montant_bon);
        versement_str   =  new DecimalFormat("####0.00").format(Bon1.verser);
        nouveau_solde_str   =  new DecimalFormat("####0.00").format(Bon1.reste);

        canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

        ///////////////////////////// ancien solde /////////////////////////////////////////////////
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
        canvas.drawText( "|" +  center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str  ) + "|", mergeleft, facture_y + 30, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);

        ///////////////////////////// montant bon /////////////////////////////////////////////////
        canvas.drawText( "|" +  center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str)  + "|" , mergeleft, facture_y + 50, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

        ///////////////////////////// versement /////////////////////////////////////////////////
        canvas.drawText( "|" +  center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str)  + "|", mergeleft, facture_y + 70, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

        ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

        canvas.drawText( "|" +  center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str)  + "|" , mergeleft, facture_y + 90, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);

        canvas.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

        facture_y = facture_y + 20;

        if(Bon1.remise !=0){

            //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// REMISE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"REMISE") + "|" + right_value(12, remise_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            facture_y = facture_y + 40;
        }

        ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
        canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(Bon1.montant_bon)) + "|" , mergeleft, facture_y + 10, title);
        canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

        pdfDocument.finishPage(myPage);
        File file = null;
        if(SOURCE.equals("FROM_SALE")){
            file = new File(mActivity.getCacheDir(), "BON_VENTE_"+Bon1.num_bon+".pdf");
        }else if(SOURCE.equals("FROM_ORDER")){
           // file = new File(mActivity.getCacheDir(), "safe_pdf_test.pdf");
        }


        try {
            assert file != null;
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

        // open PDF
        openPDF();
    }

    private String createLine(String N, String produit, String nbre_colis, String colissage, String qte, String gratuit, String p_u){
        String result = "|";

        result = result + center_value(5,N) + "|";
        result = result + " "+ produit + addCaracter(61 - produit.length(), " ") + "|";
        result = result + center_value(7,nbre_colis) + "|";
        result = result + center_value(8,colissage) + "|";
        result = result + center_value(11,qte) + "|";
        result = result + center_value(7,gratuit) + "|";
        result = result + right_value(12, p_u) + "|";

        return result;
    }

    private String addCaracter(int space_number, String caracter ){
        String space_result = "";
        while(space_result.length() < space_number){
            space_result = space_result + caracter;
        }
        return space_result;
    }

    private String center_value(int space_number, String value_before){
        String value_after = "";
        //int value_length = value_before.length();

        int value_length = value_before.length();
        int diff = space_number - value_length;
        value_after =   addCaracter((diff / 2), " ") + value_before + addCaracter((diff / 2) + diff%2, " " );


        return  value_after;
    }

    private String right_value(int space_number, String value_before){
        String value_after = "";
        //int value_length = value_before.length();
        if(value_before == ""){
            value_after = addCaracter(space_number, " ");
        }else {
            int value_length = value_before.length();
            int diff = space_number - value_length;
            value_after =   addCaracter(diff, " " ) + value_before;
        }

        return  value_after;
    }

    public void openPDF(){

        try {

            Intent intent = new Intent(mActivity, ActivityPDF.class);
            intent.putExtra("NUM_BON", Bon1.num_bon);
            intent.putExtra("SOURCE", SOURCE);
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CheckAllPermission() {
        NO_PERMISSION.clear();
        for (String s : NEED_PERMISSION) {
            if (mActivity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                NO_PERMISSION.add(s);
            }
        }
        if (NO_PERMISSION.size() == 0) {

        } else {
            mActivity.requestPermissions(NO_PERMISSION.toArray(new String[0]), REQUEST_CAMERA);
        }
    }
}

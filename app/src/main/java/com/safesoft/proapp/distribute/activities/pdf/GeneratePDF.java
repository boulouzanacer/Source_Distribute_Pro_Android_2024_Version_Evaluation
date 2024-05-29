package com.safesoft.proapp.distribute.activities.pdf;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.pdf.PdfDocument;
import android.util.Base64;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GeneratePDF {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
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
    private final String PREFS = "ALL_PREFS";

    private final List<String> NO_PERMISSION = new ArrayList<String>();
    private final String[] NEED_PERMISSION = {
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CAMERA = 0;

    PostData_Bon1 Bon1;
    PostData_Achat1 achat1;
    ArrayList<PostData_Bon2> final_panier_vente;
    ArrayList<PostData_Achat2> final_panier_achat;
    String SOURCE;

    public void startPDFVente(Activity mActivity, PostData_Bon1 bon1, ArrayList<PostData_Bon2> final_panier, String SOURCE){
        this.mActivity = mActivity;
        this.Bon1 = bon1;
        this.final_panier_vente = final_panier;
        this.SOURCE = SOURCE;
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);

        // checking our permissions.
        // CheckAllPermission();

        generatePDFVente();
    }


    public void startPDFAchat(Activity mActivity, PostData_Achat1 achat1, ArrayList<PostData_Achat2> final_panier, String SOURCE){
        this.mActivity = mActivity;
        this.achat1 = achat1;
        this.final_panier_achat = final_panier;
        this.SOURCE = SOURCE;
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);

        // checking our permissions.
        // CheckAllPermission();

        generatePDFAchat();
    }


    public void startPDFTicket(Activity mActivity, String code_bare, String produit, double prix, String SOURCE){
        this.mActivity = mActivity;
        this.SOURCE = SOURCE;

        generatePDFTTicket(code_bare, produit, prix);
    }

    private void generatePDFTTicket(String code_bare, String produit, double prix){

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();


        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(10);
        canvas.drawText(center_value(45, produit) , 10, 20, title);

        title.setTextSize(15);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(center_value(5, new DecimalFormat("####0.00").format(prix) + " DA"), mergeleft + 30, 40, title);

        // barcode image
        Bitmap bitmap = null;

        try {
            bitmap = encodeAsBitmap(code_bare, BarcodeFormat.CODE_128, 600, 200);

        } catch (WriterException e) {
            e.printStackTrace();
        }

        assert bitmap != null;
        scaledbmp = Bitmap.createScaledBitmap(bitmap, 170, 60, false);
        canvas.drawBitmap(scaledbmp, 0, 50, paint);

        pdfDocument.finishPage(myPage);

        File file = null;
        file = new File(mActivity.getCacheDir(), "TICKET_PRODUIT.pdf");

        try {
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

        // open PDF
        openPDF("");
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private void generatePDFAchat() {

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
        if(SOURCE.equals("FROM_ACHAT")){
            canvas.drawText("BON DE RECEPTION N°:" + achat1.num_bon, mergeleft, 180, title);
        }else if(SOURCE.equals("FROM_ACHAT_ORDER")){
            canvas.drawText("BON DE COMMANDE N°:" + achat1.num_bon, mergeleft, 180, title);
        }

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        if(SOURCE.equals("FROM_ACHAT")){
            canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }else if(SOURCE.equals("FROM_ACHAT_ORDER")){
            //canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }

        //title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if(SOURCE.equals("FROM_ACHAT")){
            canvas.drawText("Fournisseur : ", 400, 180, title);
        }else if(SOURCE.equals("FROM_ACHAT_ORDER")){
            canvas.drawText("Fournisseur : ", 400, 180, title);
        }
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("" + achat1.code_frs, 400, 200, title);
        canvas.drawText("" + achat1.fournis, 400, 220, title);
        canvas.drawText("" + achat1.adresse, 400, 240, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
//        canvas.drawText("N.R.C : "+ Bon1.rc, 400, 260, title);
//        canvas.drawText("N.I.S : "+ Bon1.nis, 600, 260, title);
//        canvas.drawText("N.I.F : "+ Bon1.ifiscal, 400, 280, title);
//        canvas.drawText("N.A.I : "+ Bon1.ai, 600, 280, title);

        title.setTextAlign(Paint.Align.RIGHT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Date : " + achat1.date_bon + " " + achat1.heure, endright, 180, title);

        title.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(10);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 300, title);
        canvas.drawText(createLine("N°",addCaracter(25, " ") + "DESIGNATION", "NBRE CO", "COLIS", "QTE", "U.G", "PRIX U" + addCaracter(3, " ")), mergeleft , 310 , title);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 320, title);

        double tot_qte = 0.0;
        int facture_y = 330;

        for(int i = 0; i < final_panier_achat.size() ; i++){
            tot_qte = tot_qte + final_panier_achat.get(i).qte;
            facture_y = 330 + (i * 20);

            String nc = "", colissage = "", gratuit = "";
            ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
            if(final_panier_achat.get(i).nbr_colis != 0){
                nc = new DecimalFormat( "####0.##").format(final_panier_achat.get(i).nbr_colis);
                colissage = new DecimalFormat( "####0.##").format(final_panier_achat.get(i).colissage);
            }
            if(final_panier_achat.get(i).gratuit != 0){
                gratuit = new DecimalFormat( "####0.##").format(final_panier_achat.get(i).gratuit);
            }

            canvas.drawText(createLine(
                            String.valueOf(i + 1),
                            final_panier_achat.get(i).produit ,
                            nc,
                            colissage,
                            new DecimalFormat( "####0.##").format(final_panier_achat.get(i).qte), gratuit,
                            new DecimalFormat("####0.00").format(final_panier_achat.get(i).pa_ht)),
                    mergeleft , facture_y , title);

            canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
        }


        String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


        nbr_produit_str =  new DecimalFormat( "####0.##").format(Double.valueOf(final_panier_achat.size()));
        total_ht_bon_str   =  new DecimalFormat("####0.00").format(achat1.tot_ht);
        tva_bon_str   =  new DecimalFormat("####0.00").format(achat1.tot_tva);
        timbre_bon_str   =  new DecimalFormat("####0.00").format(achat1.timbre);
        total_bon_str   =  new DecimalFormat("####0.00").format(achat1.tot_ttc);
        remise_bon_str   =  new DecimalFormat("####0.00").format(achat1.remise);
        total_a_payer_str   =  new DecimalFormat("####0.00").format(achat1.montant_bon);
        ancien_solde_str   =  new DecimalFormat("####0.00").format(achat1.solde_ancien);
        versement_str   =  new DecimalFormat("####0.00").format(achat1.verser);
        nouveau_solde_str   =  new DecimalFormat("####0.00").format(achat1.reste);

        canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

        ///////////////////////////// ancien solde /////////////////////////////////////////////////
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
        canvas.drawText( "|" +  center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str  ) + "|", mergeleft, facture_y + 30, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


        if(SOURCE.equals("FROM_ACHAT")){
            ///////////////////////////// montant bon /////////////////////////////////////////////////
            canvas.drawText( "|" +  center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str)  + "|" , mergeleft, facture_y + 50, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

            ///////////////////////////// versement /////////////////////////////////////////////////
            canvas.drawText( "|" +  center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str)  + "|", mergeleft, facture_y + 70, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

            ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

            canvas.drawText( "|" +  center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str)  + "|" , mergeleft, facture_y + 90, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
        }


        canvas.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

        facture_y = facture_y + 20;

        if(achat1.tot_tva !=0 && achat1.timbre !=0){
            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TVA /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TVA") + "|" + right_value(12, tva_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|" , mergeleft, facture_y + 50, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

            facture_y = facture_y + 60;

        }else if(achat1.tot_tva != 0){
            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TVA /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TVA") + "|" + right_value(12, tva_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            facture_y = facture_y + 40;

        }else if(achat1.timbre != 0){

            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            facture_y = facture_y + 40;
        }

        if(achat1.remise !=0){

            //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// REMISE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"REMISE") + "|" + right_value(12, remise_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            facture_y = facture_y + 40;
        }

        ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
        canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(achat1.montant_bon)) + "|" , mergeleft, facture_y + 10, title);
        canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

        pdfDocument.finishPage(myPage);
        File file = null;
        if(SOURCE.equals("FROM_ACHAT")){
            file = new File(mActivity.getCacheDir(), "BON_ACHAT_"+ achat1.num_bon+".pdf");
        }else if(SOURCE.equals("FROM_ACHAT_ORDER")){
            file = new File(mActivity.getCacheDir(), "BON_ACHAT_COMMANDE_"+ achat1.num_bon+".pdf");
        }

        try {
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

        // open PDF
        openPDF(achat1.num_bon);
    }



    private void generatePDFVente() {
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
        if(SOURCE.equals("FROM_SALE")){
            canvas.drawText("BON DE LIVRAISON N°:" + Bon1.num_bon, mergeleft, 180, title);
        }else if(SOURCE.equals("FROM_ORDER")){
            canvas.drawText("BON DE COMMANDE N°:" + Bon1.num_bon, mergeleft, 180, title);
        }

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        if(SOURCE.equals("FROM_SALE")){
            canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }else if(SOURCE.equals("FROM_ORDER")){
            //canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }

        //title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if(SOURCE.equals("FROM_SALE")){
            canvas.drawText("Doit : ", 400, 180, title);
        }else if(SOURCE.equals("FROM_ORDER")){
            canvas.drawText("Client : ", 400, 180, title);
        }
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

        for(int i = 0; i < final_panier_vente.size() ; i++){
            tot_qte = tot_qte + final_panier_vente.get(i).qte;
            facture_y = 330 + (i * 20);

            String nc = "", colissage = "", gratuit = "";
            ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
            if(final_panier_vente.get(i).nbr_colis != 0){
                nc = new DecimalFormat( "####0.##").format(final_panier_vente.get(i).nbr_colis);
                colissage = new DecimalFormat( "####0.##").format(final_panier_vente.get(i).colissage);
            }
            if(final_panier_vente.get(i).gratuit != 0){
                gratuit = new DecimalFormat( "####0.##").format(final_panier_vente.get(i).gratuit);
            }

            canvas.drawText(createLine(
                            String.valueOf(i + 1),
                            final_panier_vente.get(i).produit ,
                            nc,
                            colissage,
                            new DecimalFormat( "####0.##").format(final_panier_vente.get(i).qte), gratuit,
                            new DecimalFormat("####0.00").format(final_panier_vente.get(i).pa_ht)),
                    mergeleft , facture_y , title);

            canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
        }


        String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


        nbr_produit_str =  new DecimalFormat( "####0.##").format(Double.valueOf(final_panier_vente.size()));
        total_ht_bon_str   =  new DecimalFormat("####0.00").format(Bon1.tot_ht);
        tva_bon_str   =  new DecimalFormat("####0.00").format(Bon1.tot_tva);
        timbre_bon_str   =  new DecimalFormat("####0.00").format(Bon1.timbre);
        total_bon_str   =  new DecimalFormat("####0.00").format(Bon1.tot_ttc);
        remise_bon_str   =  new DecimalFormat("####0.00").format(Bon1.remise);
        total_a_payer_str   =  new DecimalFormat("####0.00").format(Bon1.montant_bon);

        ancien_solde_str   =  new DecimalFormat("####0.00").format(Bon1.solde_ancien);
        versement_str   =  new DecimalFormat("####0.00").format(Bon1.verser);
        nouveau_solde_str   =  new DecimalFormat("####0.00").format(Bon1.reste);

        canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

        ///////////////////////////// ancien solde /////////////////////////////////////////////////
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
        canvas.drawText( "|" +  center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str  ) + "|", mergeleft, facture_y + 30, title);
        canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


        if(SOURCE.equals("FROM_SALE")){
            ///////////////////////////// montant bon /////////////////////////////////////////////////
            canvas.drawText( "|" +  center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str)  + "|" , mergeleft, facture_y + 50, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

            ///////////////////////////// versement /////////////////////////////////////////////////
            canvas.drawText( "|" +  center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str)  + "|", mergeleft, facture_y + 70, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

            ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

            canvas.drawText( "|" +  center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str)  + "|" , mergeleft, facture_y + 90, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
        }


        canvas.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

        facture_y = facture_y + 20;

        if(Bon1.tot_tva !=0 && Bon1.timbre !=0){
            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TVA /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TVA") + "|" + right_value(12, tva_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|" , mergeleft, facture_y + 50, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

            facture_y = facture_y + 60;

        }else if(Bon1.tot_tva != 0){
            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TVA /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TVA") + "|" + right_value(12, tva_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            facture_y = facture_y + 40;

        }else if(Bon1.timbre != 0){

            //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|" , mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") +"|" + center_value(19,"TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|" , mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

            facture_y = facture_y + 40;
        }

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
            file = new File(mActivity.getCacheDir(), "BON_COMMANDE_"+Bon1.num_bon+".pdf");
        }

        try {
            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();

        // open PDF
        openPDF(Bon1.num_bon);
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

    public void openPDF(String num_bon){

        try {
            Intent intent = new Intent(mActivity, ActivityPDF.class);
            intent.putExtra("NUM_BON", num_bon);
            intent.putExtra("SOURCE", SOURCE);
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
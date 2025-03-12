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
import android.graphics.pdf.PdfDocument;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activation.NetClient;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class GeneratePDF {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    Activity mActivity;

    // declaring width and height
    // for our PDF file.
    //int pageHeight = 1122;
    int pageHeight = 1122;
    int pageWidth = 792;

    int mergeleft = 38;
    int endright = 754;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";
    private DATABASE controller;
    private final List<String> NO_PERMISSION = new ArrayList<>();
    private String code_depot, code_vendeur;
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

    public void startPDFVente(Activity mActivity, PostData_Bon1 bon1, ArrayList<PostData_Bon2> final_panier, String SOURCE) throws ParseException {
        this.mActivity = mActivity;
        this.Bon1 = bon1;
        this.final_panier_vente = final_panier;
        this.SOURCE = SOURCE;
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        controller = new DATABASE(mActivity);

        // checking our permissions.
        // CheckAllPermission();

        generatePDFVente();
    }

    public void startPDFAchat(Activity mActivity, PostData_Achat1 achat1, ArrayList<PostData_Achat2> final_panier, String SOURCE) {
        this.mActivity = mActivity;
        this.achat1 = achat1;
        this.final_panier_achat = final_panier;
        this.SOURCE = SOURCE;
        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        controller = new DATABASE(mActivity);

        // checking our permissions.
        // CheckAllPermission();

        generatePDFAchat();
    }

    public void startPDFTicket(Activity mActivity, String code_bare, String produit, double prix, String SOURCE) {
        this.mActivity = mActivity;
        this.SOURCE = SOURCE;
        controller = new DATABASE(mActivity);

        generatePDFTTicket(code_bare, produit, prix);
    }

    private void generatePDFTTicket(String code_bare, String produit, double prix) {

        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();


        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(10);
        canvas.drawText(center_value(45, produit), 10, 20, title);

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
            hints = new EnumMap<>(EncodeHintType.class);
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

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo1);
        Canvas canvas = page1.getCanvas();

        String img_str = prefs.getString("COMPANY_LOGO", "");
        if (!img_str.isEmpty()) {
            //decode string to image
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
            canvas.drawBitmap(scaledbmp, 610, 0, paint);
        }

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(17);

        if (!prefs.getString("COMPANY_NAME", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_NAME", ""), mergeleft, 30, title);
        }
        if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
            canvas.drawText(prefs.getString("ACTIVITY_NAME", ""), mergeleft, 60, title);
        }
        if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_ADRESSE", ""), mergeleft, 90, title);
        }
        if (!prefs.getString("COMPANY_TEL", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_TEL", ""), mergeleft, 120, title);
        }

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        canvas.drawLine(mergeleft, 150, endright, 150, paint);
        canvas.drawLine(mergeleft, 153, endright, 153, paint);

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if (SOURCE.equals("FROM_ACHAT")) {
            canvas.drawText("BON DE RECEPTION N°:" + achat1.num_bon, mergeleft, 180, title);
        } else if (SOURCE.equals("FROM_ACHAT_ORDER")) {
            canvas.drawText("BON DE COMMANDE N°:" + achat1.num_bon, mergeleft, 180, title);
        }

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        if (SOURCE.equals("FROM_ACHAT")) {
            canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        } else if (SOURCE.equals("FROM_ACHAT_ORDER")) {
            //canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }

        //title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if (SOURCE.equals("FROM_ACHAT")) {
            canvas.drawText("Fournisseur : ", 400, 180, title);
        } else if (SOURCE.equals("FROM_ACHAT_ORDER")) {
            canvas.drawText("Fournisseur : ", 400, 180, title);
        }
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(achat1.code_frs, 400, 200, title);
        canvas.drawText(achat1.fournis, 400, 220, title);
        canvas.drawText(achat1.adresse, 400, 240, title);
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

        ///////////////////////////////////////QR CODE /////////////////////////////////////////////

        try {

            long hash = 0;

            try {
                SimpleDateFormat format_origin = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dt = format_origin.parse(achat1.date_bon + " " + achat1.heure);
                assert dt != null;
                hash = dt.getTime();
            }catch (Exception e){

            }

            // Define the task using Callable
            FutureTask<Integer> futureTask = new FutureTask<>(new sendDataToServerTask(hash, prepare_local_file_achat(achat1, final_panier_achat)));

            // Start the task in a background thread
            new Thread(futureTask).start();

            try {
                // Wait for the task to complete and get the result
                int result = futureTask.get();
                if(result == 1){
                    /////////////// QR CODE //////////////
                    // Step 1: Create a QR Code Bitmap
                    String qrCodeContent = String.valueOf(hash);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

                    try {
                        Bitmap qrCodeBitmap = barcodeEncoder.encodeBitmap(qrCodeContent, BarcodeFormat.QR_CODE, 100, 100);
                        Paint paint_qrcode = new Paint();
                        int qrX = mergeleft - 12;  // Center the QR code horizontally
                        int qrY = 190; // Vertical position for the QR code
                        canvas.drawBitmap(qrCodeBitmap, qrX, qrY, paint_qrcode);

                    }catch (Exception e){
                        Log.e("QR_CODE", e.getMessage());
                    }
                }
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        /////////////////////////////////////QR CODE ////////////////////////////////////////////////

        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 300, title);
        canvas.drawText(createLine("N°", addCaracter(25, " ") + "DESIGNATION", "NBRE CO", "COLIS", "QTE", "U.G", "PRIX U" + addCaracter(3, " ")), mergeleft, 310, title);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 320, title);

        double tot_qte = 0.0;
        int facture_y = 330;

        if(final_panier_achat.size() <= 35){
            for (int i = 0; i < final_panier_achat.size(); i++) {
                tot_qte = tot_qte + final_panier_achat.get(i).qte;
                facture_y = 330 + (i * 20);

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_achat.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_achat.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_achat.get(i).colissage);
                }
                if (final_panier_achat.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_achat.get(i).gratuit);
                }

                canvas.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_achat.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_achat.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_achat.get(i).pa_ht * (1 + (final_panier_achat.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
            }

            String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


            nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(final_panier_achat.size()));
            total_ht_bon_str = new DecimalFormat("####0.00").format(achat1.tot_ht);
            tva_bon_str = new DecimalFormat("####0.00").format(achat1.tot_tva);
            timbre_bon_str = new DecimalFormat("####0.00").format(achat1.timbre);
            total_bon_str = new DecimalFormat("####0.00").format(achat1.tot_ttc);
            remise_bon_str = new DecimalFormat("####0.00").format(achat1.remise);
            total_a_payer_str = new DecimalFormat("####0.00").format(achat1.montant_bon);
            ancien_solde_str = new DecimalFormat("####0.00").format(achat1.solde_ancien);
            versement_str = new DecimalFormat("####0.00").format(achat1.verser);
            nouveau_solde_str = new DecimalFormat("####0.00").format(achat1.reste);

            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            ///////////////////////////// ancien solde /////////////////////////////////////////////////
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
            canvas.drawText("|" + center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str) + "|", mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            if (SOURCE.equals("FROM_ACHAT")) {
                ///////////////////////////// montant bon /////////////////////////////////////////////////
                canvas.drawText("|" + center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str) + "|", mergeleft, facture_y + 50, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                ///////////////////////////// versement /////////////////////////////////////////////////
                canvas.drawText("|" + center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str) + "|", mergeleft, facture_y + 70, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

                ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

                canvas.drawText("|" + center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str) + "|", mergeleft, facture_y + 90, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
            }


            canvas.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

            facture_y = facture_y + 20;

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                if (achat1.tot_tva != 0 && achat1.timbre != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 50, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                    facture_y = facture_y + 60;

                } else if (achat1.tot_tva != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;

                } else if (achat1.timbre != 0) {

                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;
                }
            }


            if (achat1.remise != 0) {

                //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
                canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|", mergeleft, facture_y + 10, title);
                canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                //////////////////////////// REMISE /////////////////////////////////////////////////////
                canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "REMISE") + "|" + right_value(12, remise_bon_str) + "|", mergeleft, facture_y + 30, title);
                canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


                facture_y = facture_y + 40;
            }

            ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(achat1.montant_bon)) + "|", mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            pdfDocument.finishPage(page1);

        }else{

            for (int i = 0; i < 35; i++) {
                tot_qte = tot_qte + final_panier_achat.get(i).qte;
                facture_y = 330 + (i * 20);

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_achat.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_achat.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_achat.get(i).colissage);
                }
                if (final_panier_achat.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_achat.get(i).gratuit);
                }

                canvas.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_achat.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_achat.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_achat.get(i).pa_ht * (1 + (final_panier_achat.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
            }

            pdfDocument.finishPage(page1);

            PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
            PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
            // Step 6: Draw content on the second page
            Canvas canvas2 = page2.getCanvas();

            facture_y = 10;

            canvas2.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);

            for (int i = 35; i < final_panier_achat.size(); i++) {
                tot_qte = tot_qte + final_panier_achat.get(i).qte;
                facture_y = facture_y +  20;

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_achat.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_achat.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_achat.get(i).colissage);
                }
                if (final_panier_achat.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_achat.get(i).gratuit);
                }

                canvas2.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_achat.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_achat.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_achat.get(i).pa_ht * (1 + (final_panier_achat.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas2.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
            }

            String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


            nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(final_panier_achat.size()));
            total_ht_bon_str = new DecimalFormat("####0.00").format(achat1.tot_ht);
            tva_bon_str = new DecimalFormat("####0.00").format(achat1.tot_tva);
            timbre_bon_str = new DecimalFormat("####0.00").format(achat1.timbre);
            total_bon_str = new DecimalFormat("####0.00").format(achat1.tot_ttc);
            remise_bon_str = new DecimalFormat("####0.00").format(achat1.remise);
            total_a_payer_str = new DecimalFormat("####0.00").format(achat1.montant_bon);
            ancien_solde_str = new DecimalFormat("####0.00").format(achat1.solde_ancien);
            versement_str = new DecimalFormat("####0.00").format(achat1.verser);
            nouveau_solde_str = new DecimalFormat("####0.00").format(achat1.reste);

            canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            ///////////////////////////// ancien solde /////////////////////////////////////////////////
            canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
            canvas2.drawText("|" + center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str) + "|", mergeleft, facture_y + 30, title);
            canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            if (SOURCE.equals("FROM_ACHAT")) {
                ///////////////////////////// montant bon /////////////////////////////////////////////////
                canvas2.drawText("|" + center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str) + "|", mergeleft, facture_y + 50, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                ///////////////////////////// versement /////////////////////////////////////////////////
                canvas2.drawText("|" + center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str) + "|", mergeleft, facture_y + 70, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

                ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

                canvas2.drawText("|" + center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str) + "|", mergeleft, facture_y + 90, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
            }


            canvas2.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

            facture_y = facture_y + 20;

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                if (achat1.tot_tva != 0 && achat1.timbre != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 50, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                    facture_y = facture_y + 60;

                } else if (achat1.tot_tva != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;

                } else if (achat1.timbre != 0) {

                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;
                }
            }


            if (achat1.remise != 0) {

                //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
                canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|", mergeleft, facture_y + 10, title);
                canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                //////////////////////////// REMISE /////////////////////////////////////////////////////
                canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "REMISE") + "|" + right_value(12, remise_bon_str) + "|", mergeleft, facture_y + 30, title);
                canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


                facture_y = facture_y + 40;
            }

            ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
            canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(achat1.montant_bon)) + "|", mergeleft, facture_y + 10, title);
            canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            pdfDocument.finishPage(page2);

        }


        File file = null;
        if (SOURCE.equals("FROM_ACHAT")) {
            file = new File(mActivity.getCacheDir(), "BON_ACHAT_" + achat1.num_bon + ".pdf");
        } else if (SOURCE.equals("FROM_ACHAT_ORDER")) {
            file = new File(mActivity.getCacheDir(), "BON_ACHAT_COMMANDE_" + achat1.num_bon + ".pdf");
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
        PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo1);
        Canvas canvas = page1.getCanvas();

        String img_str = prefs.getString("COMPANY_LOGO", "");
        if (!img_str.equals("")) {
            //decode string to image
            byte[] imageAsBytes = Base64.decode(img_str.getBytes(), Base64.DEFAULT);
            bmp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
            canvas.drawBitmap(scaledbmp, 610, 0, paint);
        }

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(17);

        if (!prefs.getString("COMPANY_NAME", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_NAME", ""), mergeleft, 30, title);
        }
        if (!prefs.getString("ACTIVITY_NAME", "").equals("")) {
            canvas.drawText(prefs.getString("ACTIVITY_NAME", ""), mergeleft, 60, title);
        }
        if (!prefs.getString("COMPANY_ADRESSE", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_ADRESSE", ""), mergeleft, 90, title);
        }
        if (!prefs.getString("COMPANY_TEL", "").equals("")) {
            canvas.drawText(prefs.getString("COMPANY_TEL", ""), mergeleft, 120, title);
        }

        title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(15);
        canvas.drawLine(mergeleft, 150, endright, 150, paint);
        canvas.drawLine(mergeleft, 153, endright, 153, paint);

        title.setTextAlign(Paint.Align.LEFT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if (SOURCE.equals("FROM_SALE")) {
            canvas.drawText("BON DE LIVRAISON N°:" + Bon1.num_bon, mergeleft, 180, title);
        } else if (SOURCE.equals("FROM_ORDER")) {
            canvas.drawText("BON DE COMMANDE N°:" + Bon1.num_bon, mergeleft, 180, title);
        }

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        if (SOURCE.equals("FROM_SALE")) {
            canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        } else if (SOURCE.equals("FROM_ORDER")) {
            //canvas.drawText("Mode de paiement : A TERME", mergeleft, 200, title);
        }

        //title.setTextAlign(Paint.Align.CENTER);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if (SOURCE.equals("FROM_SALE")) {
            canvas.drawText("Doit : ", 400, 180, title);
        } else if (SOURCE.equals("FROM_ORDER")) {
            canvas.drawText("Client : ", 400, 180, title);
        }
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText(Bon1.code_client, 400, 200, title);
        canvas.drawText(Bon1.client, 400, 220, title);
        canvas.drawText(Bon1.adresse, 400, 240, title);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("N.R.C : " + Bon1.rc, 400, 260, title);
        canvas.drawText("N.I.S : " + Bon1.nis, 600, 260, title);
        canvas.drawText("N.I.F : " + Bon1.ifiscal, 400, 280, title);
        canvas.drawText("N.A.I : " + Bon1.ai, 600, 280, title);

        title.setTextAlign(Paint.Align.RIGHT);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Date : " + Bon1.date_bon + " " + Bon1.heure, endright, 180, title);

        title.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(10);


        ///////////////////////////////////////QR CODE /////////////////////////////////////////////

        try {

            long hash = 0;

            try {
                SimpleDateFormat format_origin = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date dt = format_origin.parse(Bon1.date_bon + " " + Bon1.heure);
                assert dt != null;
                hash = dt.getTime();
            }catch (Exception e){

            }

            // Define the task using Callable
            FutureTask<Integer> futureTask = new FutureTask<>(new sendDataToServerTask(hash, prepare_local_file_sale(Bon1, final_panier_vente)));

            // Start the task in a background thread
            new Thread(futureTask).start();

            try {
                // Wait for the task to complete and get the result
                int result = futureTask.get();
                if(result == 1){
                    /////////////// QR CODE //////////////
                    // Step 1: Create a QR Code Bitmap
                    String qrCodeContent = String.valueOf(hash);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();

                    try {
                        Bitmap qrCodeBitmap = barcodeEncoder.encodeBitmap(qrCodeContent, BarcodeFormat.QR_CODE, 100, 100);
                        Paint paint_qrcode = new Paint();
                        int qrX = mergeleft - 12;  // Center the QR code horizontally
                        int qrY = 190; // Vertical position for the QR code
                        canvas.drawBitmap(qrCodeBitmap, qrX, qrY, paint_qrcode);

                    }catch (Exception e){
                        Log.e("QR_CODE", e.getMessage());
                    }
                }
                System.out.println(result);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }


        /////////////////////////////////////QR CODE ////////////////////////////////////////////////
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 300, title);
        canvas.drawText(createLine("N°", addCaracter(25, " ") + "DESIGNATION", "NBRE CO", "COLIS", "QTE", "U.G", "PRIX U" + addCaracter(3, " ")), mergeleft, 310, title);
        canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, 320, title);

        double tot_qte = 0.0;
        int facture_y = 330;

        if(final_panier_vente.size() <= 35){

            for (int i = 0; i < final_panier_vente.size(); i++) {
                tot_qte = tot_qte + final_panier_vente.get(i).qte;
                facture_y = 330 + (i * 20);

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_vente.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_vente.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_vente.get(i).colissage);
                }
                if (final_panier_vente.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_vente.get(i).gratuit);
                }

                canvas.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_vente.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_vente.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_vente.get(i).pv_ht * (1 + (final_panier_vente.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
            }

            String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


            nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(final_panier_vente.size()));
            total_ht_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_ht);
            tva_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_tva);
            timbre_bon_str = new DecimalFormat("####0.00").format(Bon1.timbre);
            total_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_ttc);
            remise_bon_str = new DecimalFormat("####0.00").format(Bon1.remise);
            total_a_payer_str = new DecimalFormat("####0.00").format(Bon1.montant_bon);

            ancien_solde_str = new DecimalFormat("####0.00").format(Bon1.ancien_solde);
            versement_str = new DecimalFormat("####0.00").format(Bon1.verser);
            nouveau_solde_str = new DecimalFormat("####0.00").format(Bon1.reste);

            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            ///////////////////////////// ancien solde /////////////////////////////////////////////////
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
            canvas.drawText("|" + center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str) + "|", mergeleft, facture_y + 30, title);
            canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            if (SOURCE.equals("FROM_SALE")) {
                ///////////////////////////// montant bon /////////////////////////////////////////////////
                canvas.drawText("|" + center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str) + "|", mergeleft, facture_y + 50, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                ///////////////////////////// versement /////////////////////////////////////////////////
                canvas.drawText("|" + center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str) + "|", mergeleft, facture_y + 70, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

                ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

                canvas.drawText("|" + center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str) + "|", mergeleft, facture_y + 90, title);
                canvas.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
            }


            canvas.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

            facture_y = facture_y + 20;

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                if (Bon1.tot_tva != 0 && Bon1.timbre != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 50, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                    facture_y = facture_y + 60;

                } else if (Bon1.tot_tva != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;

                } else if (Bon1.timbre != 0) {

                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;
                }
            }


            if (Bon1.remise != 0) {

                //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
                canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|", mergeleft, facture_y + 10, title);
                canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                //////////////////////////// REMISE /////////////////////////////////////////////////////
                canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "REMISE") + "|" + right_value(12, remise_bon_str) + "|", mergeleft, facture_y + 30, title);
                canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


                facture_y = facture_y + 40;
            }

            ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
            canvas.drawText(addCaracter(86, " ") + "|" + center_value(19, "NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(Bon1.montant_bon)) + "|", mergeleft, facture_y + 10, title);
            canvas.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);


            pdfDocument.finishPage(page1);

        }else{

            for (int i = 0; i < 35; i++) {
                tot_qte = tot_qte + final_panier_vente.get(i).qte;
                facture_y = 330 + (i * 20);

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_vente.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_vente.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_vente.get(i).colissage);
                }
                if (final_panier_vente.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_vente.get(i).gratuit);
                }

                canvas.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_vente.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_vente.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_vente.get(i).pv_ht * (1+ (final_panier_vente.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);


            }

            pdfDocument.finishPage(page1);

            PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create();
            PdfDocument.Page page2 = pdfDocument.startPage(pageInfo2);
            // Step 6: Draw content on the second page
            Canvas canvas2 = page2.getCanvas();

            facture_y = 10;

            canvas2.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y +10, title);

            for (int i = 35; i < final_panier_vente.size(); i++) {
                tot_qte = tot_qte + final_panier_vente.get(i).qte;
                facture_y = facture_y +  20;

                String nc = "", colissage = "", gratuit = "";
                ///////////////////////////// nbre colis and colissage /////////////////////////////////////////////
                if (final_panier_vente.get(i).nbr_colis != 0) {
                    nc = new DecimalFormat("####0.##").format(final_panier_vente.get(i).nbr_colis);
                    colissage = new DecimalFormat("####0.##").format(final_panier_vente.get(i).colissage);
                }
                if (final_panier_vente.get(i).gratuit != 0) {
                    gratuit = new DecimalFormat("####0.##").format(final_panier_vente.get(i).gratuit);
                }

                canvas2.drawText(createLine(
                                String.valueOf(i + 1),
                                final_panier_vente.get(i).produit,
                                nc,
                                colissage,
                                new DecimalFormat("####0.##").format(final_panier_vente.get(i).qte), gratuit,
                                new DecimalFormat("####0.00").format(final_panier_vente.get(i).pv_ht * (1 + (final_panier_vente.get(i).tva / 100)))),
                        mergeleft, facture_y, title);

                canvas2.drawText("------------------------------------------------------------------------------------------------------------------------", mergeleft, facture_y + 10, title);
            }

            String nbr_produit_str, total_ht_bon_str, tva_bon_str, timbre_bon_str, total_bon_str, remise_bon_str, total_a_payer_str, ancien_solde_str, versement_str, nouveau_solde_str;


            nbr_produit_str = new DecimalFormat("####0.##").format(Double.valueOf(final_panier_vente.size()));
            total_ht_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_ht);
            tva_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_tva);
            timbre_bon_str = new DecimalFormat("####0.00").format(Bon1.timbre);
            total_bon_str = new DecimalFormat("####0.00").format(Bon1.tot_ttc);
            remise_bon_str = new DecimalFormat("####0.00").format(Bon1.remise);
            total_a_payer_str = new DecimalFormat("####0.00").format(Bon1.montant_bon);

            ancien_solde_str = new DecimalFormat("####0.00").format(Bon1.ancien_solde);
            versement_str = new DecimalFormat("####0.00").format(Bon1.verser);
            nouveau_solde_str = new DecimalFormat("####0.00").format(Bon1.reste);

            canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            ///////////////////////////// ancien solde /////////////////////////////////////////////////
            canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 20, title);
            canvas2.drawText("|" + center_value(19, "ANCIEN SOLDE") + "|" + right_value(12, ancien_solde_str) + "|", mergeleft, facture_y + 30, title);
            canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 40, title);


            if (SOURCE.equals("FROM_SALE")) {
                ///////////////////////////// montant bon /////////////////////////////////////////////////
                canvas2.drawText("|" + center_value(19, "MONTANT BON") + "|" + right_value(12, total_a_payer_str) + "|", mergeleft, facture_y + 50, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                ///////////////////////////// versement /////////////////////////////////////////////////
                canvas2.drawText("|" + center_value(19, "VERSEMENT") + "|" + right_value(12, versement_str) + "|", mergeleft, facture_y + 70, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 80, title);

                ///////////////////////////// Nouveau solde /////////////////////////////////////////////////

                canvas2.drawText("|" + center_value(19, "NOUVEAU SOLDE") + "|" + right_value(12, nouveau_solde_str) + "|", mergeleft, facture_y + 90, title);
                canvas2.drawText(addCaracter(34, "-"), mergeleft, facture_y + 100, title);
            }


            canvas2.drawText("Nombre de produit : " + nbr_produit_str + ", Total quantité : " + new DecimalFormat("####0.##").format(Double.valueOf(tot_qte)), mergeleft, facture_y + 120, title);

            facture_y = facture_y + 20;

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {
                if (Bon1.tot_tva != 0 && Bon1.timbre != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 50, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 60, title);

                    facture_y = facture_y + 60;

                } else if (Bon1.tot_tva != 0) {
                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TVA /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TVA") + "|" + right_value(12, tva_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;

                } else if (Bon1.timbre != 0) {

                    //////////////////////////// TOTAL HT /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL HT") + "|" + right_value(12, total_ht_bon_str) + "|", mergeleft, facture_y + 10, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                    //////////////////////////// TTIMBRE /////////////////////////////////////////////////////
                    canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TIMBRE") + "|" + right_value(12, timbre_bon_str) + "|", mergeleft, facture_y + 30, title);
                    canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);

                    facture_y = facture_y + 40;
                }
            }


            if (Bon1.remise != 0) {

                //////////////////////////// TOTAL TTC /////////////////////////////////////////////////////
                canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "TOTAL TTC") + "|" + right_value(12, total_bon_str) + "|", mergeleft, facture_y + 10, title);
                canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

                //////////////////////////// REMISE /////////////////////////////////////////////////////
                canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "REMISE") + "|" + right_value(12, remise_bon_str) + "|", mergeleft, facture_y + 30, title);
                canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 40, title);


                facture_y = facture_y + 40;
            }

            ///////////////////////////// NET A PAYER /////////////////////////////////////////////////
            canvas2.drawText(addCaracter(86, " ") + "|" + center_value(19, "NET A PAYER") + "|" + right_value(12, new DecimalFormat("####0.00").format(Bon1.montant_bon)) + "|", mergeleft, facture_y + 10, title);
            canvas2.drawText(addCaracter(86, " ") + addCaracter(34, "-"), mergeleft, facture_y + 20, title);

            pdfDocument.finishPage(page2);
        }



        File file = null;
        if (SOURCE.equals("FROM_SALE")) {
            file = new File(mActivity.getCacheDir(), "BON_VENTE_" + Bon1.num_bon + ".pdf");
        } else if (SOURCE.equals("FROM_ORDER")) {
            file = new File(mActivity.getCacheDir(), "BON_COMMANDE_" + Bon1.num_bon + ".pdf");
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



    private String createLine(String N, String produit, String nbre_colis, String colissage, String qte, String gratuit, String p_u) {
        String result = "|";

        result = result + center_value(5, N) + "|";
        result = result + " " + produit + addCaracter(61 - produit.length(), " ") + "|";
        result = result + center_value(7, nbre_colis) + "|";
        result = result + center_value(8, colissage) + "|";
        result = result + center_value(11, qte) + "|";
        result = result + center_value(7, gratuit) + "|";
        result = result + right_value(12, p_u) + "|";

        return result;
    }

    private String addCaracter(int space_number, String caracter) {
        String space_result = "";
        while (space_result.length() < space_number) {
            space_result = space_result + caracter;
        }
        return space_result;
    }

    private String center_value(int space_number, String value_before) {
        String value_after = "";
        //int value_length = value_before.length();

        int value_length = value_before.length();
        int diff = space_number - value_length;
        value_after = addCaracter((diff / 2), " ") + value_before + addCaracter((diff / 2) + diff % 2, " ");


        return value_after;
    }

    private String right_value(int space_number, String value_before) {
        String value_after = "";
        //int value_length = value_before.length();
        if (value_before == "") {
            value_after = addCaracter(space_number, " ");
        } else {
            int value_length = value_before.length();
            int diff = space_number - value_length;
            value_after = addCaracter(diff, " ") + value_before;
        }

        return value_after;
    }

    public void openPDF(String num_bon) {

        try {
            Intent intent = new Intent(mActivity, ActivityPDF.class);
            intent.putExtra("NUM_BON", num_bon);
            intent.putExtra("SOURCE", SOURCE);
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception e) {
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


    static class sendDataToServerTask implements Callable<Integer> {
        int flag = 1;
        String response = "";

        private long hash;
        private String dataContent;

        // Constructor to pass the long data
        public sendDataToServerTask(long data, String dataContent) {
            this.hash = data;
            this.dataContent = dataContent;
        }

        @Override
        public Integer call() {
            // Simulate processing the long data
            try {
                NetClient nc = new NetClient("144.91.122.24", 2324); // ip adress and port
                String message_online = "W" + "@" + hash + "@" + dataContent + "#13#10";
                nc.sendDataWithString(message_online);
                response = nc.receiveDataFromServer();
                if (response.equals("OK")){
                    flag = 1;
                }else{
                    flag = 2;
                }

            } catch (Exception e) {
                flag = 3;
                response = e.getMessage();
            }
            return flag;
        }
    }

/*    public void prepare_local_file_achat() throws ParseException {
        String F_SQL = "";

        String querry = "SELECT " +
                "BON1.RECORDID, " +
                "BON1.NUM_BON, " +
                "BON1.DATE_BON, " +
                "BON1.HEURE, " +
                "BON1.DATE_F, " +
                "BON1.HEURE_F, " +
                "BON1.MODE_RG, " +
                "BON1.MODE_TARIF, " +

                "BON1.NBR_P, " +
                "BON1.TOT_QTE, " +

                "BON1.TOT_HT, " +
                "BON1.TOT_TVA, " +
                "BON1.TIMBRE, " +
                "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE AS TOT_TTC, " +
                "BON1.REMISE, " +
                "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE AS MONTANT_BON, " +

                "BON1.ANCIEN_SOLDE, " +
                "BON1.VERSER, " +
                "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                "BON1.CODE_CLIENT, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.TEL, " +
                "coalesce(CLIENT.RC, '') RC, " +
                "coalesce(CLIENT.IFISCAL, '') IFISCAL, " +
                "coalesce(CLIENT.AI, '') AI, " +
                "coalesce(CLIENT.NIS, '') NIS, " +

                "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                "CLIENT.CREDIT_LIMIT, " +

                "BON1.LATITUDE, " +
                "BON1.LONGITUDE, " +

                "BON1.CODE_DEPOT, " +
                "BON1.CODE_VENDEUR, " +
                "BON1.EXPORTATION, " +
                "BON1.BLOCAGE " +
                "FROM BON1 " +
                "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT " +
                "WHERE BLOCAGE = 'F' ORDER BY BON1.NUM_BON";

        ArrayList<PostData_Achat1> achat1s = controller.select_all_achat1_from_database(querry);

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");


        for (int i = 0; i < achat1s.size(); i++) {

            Date dt = format.parse(achat1s.get(i).date_bon);
            assert dt != null;

            F_SQL = "ACHAT|" + achat1s.get(i).nbr_p + "|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT, CODE_CLIENT, CLIENT, ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE, CODE_VENDEUR ";

            F_SQL = F_SQL + ")\n VALUES ( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') ," +
                    " '" + bon1s.get(i).code_client.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).client.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).adresse.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).wilaya.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).commune.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).tel.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).rc.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ifiscal.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).nis.replace("'", "''") + "'," +
                    " '" + bon1s.get(i).ai.replace("'", "''") + "'," +
                    "  " + bon1s.get(i).latitude_client + ", " + bon1s.get(i).longitude_client;

            F_SQL = F_SQL + ", iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "')";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";

            ///////////////////////////////////// BON1 ////////////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO BON1 (RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,CODE_DEPOT, CODE_VENDEUR, " +
                    "CODE_CLIENT,DATE_BON, HEURE,MODE_RG,BLOCAGE,MODE_TARIF," +
                    "VERSER, TIMBRE, REMISE, ANCIEN_SOLDE, LATITUDE, LONGITUDE, UTILISATEUR)\n ";


            F_SQL = F_SQL + " VALUES ( (SELECT GEN_ID(GEN_BON1_ID,1)    FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'), :EXPORTATION:,:CODE_CAISSE:,iif('" + code_depot + "' = '000000', null,'" + code_depot + "'), iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'), ";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','" + bon1s.get(i).mode_rg + "','" + bon1s.get(i).blocage + "','" + bon1s.get(i).mode_tarif + "',";
            F_SQL = F_SQL + bon1s.get(i).verser + "," + bon1s.get(i).timbre + "," + bon1s.get(i).remise + "," + bon1s.get(i).solde_ancien + "," + bon1s.get(i).latitude + "," + bon1s.get(i).longitude + ", 'TERMINAL_MOBILE');\n";


            String querry_select = "SELECT " +
                    "BON2.RECORDID, " +
                    "BON2.CODE_BARRE, " +
                    "BON2.NUM_BON, " +
                    "BON2.PRODUIT, " +
                    "BON2.NBRE_COLIS, " +
                    "BON2.COLISSAGE, " +
                    "BON2.QTE, " +
                    "BON2.QTE_GRAT, " +
                    "BON2.PV_HT, " +
                    "BON2.PA_HT, " +
                    "BON2.TVA, " +
                    "BON2.CODE_DEPOT, " +
                    "BON2.DESTOCK_TYPE, " +
                    "BON2.DESTOCK_CODE_BARRE, " +
                    "BON2.DESTOCK_QTE, " +
                    "PRODUIT.ISNEW, " +
                    "PRODUIT.STOCK " +
                    "FROM BON2 LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                    "WHERE BON2.NUM_BON = '" + bon1s.get(i).num_bon + "'";


            bon2s = controller.select_bon2_from_database(querry_select);

            String TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");

            for (int j = 0; j < bon2s.size(); j++) {

                ///////////////////////////////////PRODUIT /////////////////////////////////////////
                PostData_Produit postData_produit = new PostData_Produit();
                String querry_isnew = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, FAMILLE, ISNEW, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC , DESTOCK_QTE " +
                        "FROM PRODUIT WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                postData_produit = controller.select_one_produit_from_database(querry_isnew);
                if (postData_produit != null) {
                    if (postData_produit.isNew == 1) {
                        F_SQL = F_SQL + "UPDATE OR INSERT INTO PRODUIT (CODE_BARRE, REF_PRODUIT, PA_HT, PRODUIT, TVA, STOCK, COLISSAGE, PV1_HT, PV2_HT, PV3_HT ";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", PV4_HT, PV5_HT, PV6_HT ";
                        }
                        F_SQL = F_SQL + ") VALUES ('" + postData_produit.code_barre + "', '" + postData_produit.ref_produit + "', '" + postData_produit.pa_ht + "', '" + postData_produit.produit + "', '" + postData_produit.tva + "',  '" + postData_produit.stock + "', '" + postData_produit.colissage + "',  '" + postData_produit.pv1_ht + "', '" + postData_produit.pv2_ht + "', '" + postData_produit.pv3_ht + "'";
                        if (TYPE_LOGICIEL.equals("PME PRO")) {
                            F_SQL = F_SQL + ", '" + postData_produit.pv4_ht + "', '" + postData_produit.pv5_ht + "', '" + postData_produit.pv6_ht + "'";
                        }
                        F_SQL = F_SQL + ") MATCHING (CODE_BARRE);\n";
                    }
                }
                ///////////////////////////////////PRODUIT /////////////////////////////////////////

                ///////////////////////////////////CODE BARRE //////////////////////////////////////
                ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

                String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN " +
                        "FROM CODEBARRE WHERE CODE_BARRE = '" + bon2s.get(j).codebarre + "'";

                codebarres = controller.select_all_codebarre_from_database(querry_codebarre);

                for(int k = 0;k<codebarres.size(); k++){
                    //insert into codebarre
                    F_SQL = F_SQL + "UPDATE OR INSERT INTO CODEBARRE (CODE_BARRE, CODE_BARRE_SYN) VALUES (";
                    F_SQL = F_SQL + " '" + codebarres.get(k).code_barre + "' , '" + codebarres.get(k).code_barre_syn + "' ";
                    F_SQL = F_SQL + ") MATCHING (CODE_BARRE);";
                }

                ///////////////////////////////////CODE BARRE //////////////////////////////////////

                ///////////////////////////////////BON 2 ///////////////////////////////////////////
                F_SQL = F_SQL + "INSERT INTO BON2 (CODE_DEPOT,RECORDID,NUM_BON,";
                F_SQL = F_SQL + "CODE_BARRE,PRODUIT,DESTOCK_TYPE,DESTOCK_CODE_BARRE,DESTOCK_QTE,";
                F_SQL = F_SQL + "NBRE_COLIS,COLISSAGE,QTE,QTE_GRAT,";
                F_SQL = F_SQL + "TVA,PV_HT_AR,PV_HT,PA_HT)\n";
                F_SQL = F_SQL + "VALUES\n";
                F_SQL = F_SQL + "( iif('" + code_depot + "' = '000000', null,'" + code_depot + "') , (SELECT GEN_ID(GEN_BON2_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0)    FROM RDB$DATABASE) ,6,'000000'),";
                F_SQL = F_SQL + "'" + bon2s.get(j).codebarre.replace("'", "''") + "','" + bon2s.get(j).produit.replace("'", "''") + "', iif('" + bon2s.get(j).destock_type + "' = 'null', null,'" + bon2s.get(j).destock_type + "') , iif('" + bon2s.get(j).destock_code_barre + "' = 'null', null,'" + bon2s.get(j).destock_code_barre + "') ,'" + bon2s.get(j).destock_qte + "',";
                F_SQL = F_SQL + bon2s.get(j).nbr_colis + "," + bon2s.get(j).colissage + "," + bon2s.get(j).qte + "," + bon2s.get(j).gratuit + ",";
                F_SQL = F_SQL + bon2s.get(j).tva + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pv_ht + "," + bon2s.get(j).pa_ht + ");\n";
                ///////////////////////////////////BON 2 ///////////////////////////////////////
            }
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + "ACHATS,VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + bon1s.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + bon1s.get(i).heure + "','BL-VENTE','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "',";
            F_SQL = F_SQL + bon1s.get(i).montant_bon + "," + bon1s.get(i).verser + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (bon1s.get(i).verser != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'BL-VENTE',lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000')," + bon1s.get(i).verser + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + bon1s.get(i).mode_rg + "');\n";
            }

            /////////////////////////////////// CAISSE //////////////////////////////////////////////

            file_name = "VENTE_" + bon1s.get(i).num_bon + "_" + bon1s.get(i).exportation + "_" + bon1s.get(i).date_bon + ".BLV";
            file_name = file_name.replace("/", "_");

            F_SQL_LIST_VENTE.put(file_name, F_SQL);
        }


        //////////////////////////////////////// SITUATION /////////////////////////////////////////////////
        F_SQL = "";
        all_versement_client = controller.select_carnet_c_from_database("SELECT CARNET_C.RECORDID, " +
                "CARNET_C.CODE_CLIENT, " +
                "CARNET_C.DATE_CARNET, " +

                "CLIENT.CLIENT, " +
                "CLIENT.LATITUDE, " +
                "CLIENT.LONGITUDE, " +
                "CLIENT.TEL, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
                "CLIENT.RC, " +
                "CLIENT.IFISCAL, " +
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
                "CLIENT.CODE_CLIENT = CARNET_C.CODE_CLIENT ");

        for (int i = 0; i < all_versement_client.size(); i++) {

            Date dt = format.parse(all_versement_client.get(i).carnet_date);
            assert dt != null;

            F_SQL = "SITUATION-CLIENT|" + format2.format(dt) + "\n";
            ///////////////////////////////////// JOURNEE //////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO JOURNEE (DATE_JOURNEE) VALUES ('" + format2.format(dt) + "') MATCHING (DATE_JOURNEE);\n";
            ///////////////////////////////////// CLIENT ///////////////////////////////////////
            F_SQL = F_SQL + "UPDATE OR INSERT INTO CLIENTS (CODE_DEPOT,CODE_CLIENT,CLIENT,ADRESSE, WILAYA, COMMUNE, TEL, NUM_RC, NUM_IF,NUM_IS, NUM_ART, LATITUDE, LONGITUDE , CODE_VENDEUR ";

            try {
                F_SQL = F_SQL + ")\n VALUES ( " +
                        " iif('" + code_depot + "' = '000000', null, '" + code_depot + "') , " +
                        " '" + all_versement_client.get(i).code_client + "'," +
                        " iif('" + all_versement_client.get(i).client + "' = null , null, '" + all_versement_client.get(i).client.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).adresse + "' = null , null, '" + all_versement_client.get(i).adresse.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).wilaya + "' = null, null , '" + all_versement_client.get(i).wilaya.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).commune + "' = null, null , '" + all_versement_client.get(i).commune.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).tel + "' = null, null , '" + all_versement_client.get(i).tel.replace("'", "''") + "') , " +
                        " iif('" + all_versement_client.get(i).rc + "' = 'null', null , '" + all_versement_client.get(i).rc + "') , " +
                        " iif('" + all_versement_client.get(i).ifiscal + "' = null , null, '" + all_versement_client.get(i).ifiscal + "') , " +
                        " iif('" + all_versement_client.get(i).nis + "' = null, null , '" + all_versement_client.get(i).nis + "') , " +
                        " iif('" + all_versement_client.get(i).ai + "' = null, null , '" + all_versement_client.get(i).ai + "') , " +
                        " " + all_versement_client.get(i).latitude + ", " +
                        " " + all_versement_client.get(i).longitude + " ";
            } catch (Exception e) {
                Log.v("ERROR", e.getMessage());
                int x = 0;
            }


            F_SQL = F_SQL + ",iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "') ";

            F_SQL = F_SQL + ") MATCHING (CODE_CLIENT);\n";


            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            F_SQL = F_SQL + "INSERT INTO CARNET_C (CODE_VENDEUR, RECORDID,NUM_BON,EXPORTATION,CODE_CAISSE,";
            F_SQL = F_SQL + "CODE_CLIENT,DATE_CARNET,HEURE,SOURCE,UTILISATEUR,MODE_RG,";
            F_SQL = F_SQL + " VERSEMENTS)\n";
            F_SQL = F_SQL + "VALUES \n";
            F_SQL = F_SQL + "( iif('" + code_vendeur + "' = '000000', null,'" + code_vendeur + "'),  (SELECT GEN_ID(GEN_CARNET_C_ID,1) FROM RDB$DATABASE),lpad ((SELECT GEN_ID(GEN_BON1_ID,0) FROM RDB$DATABASE) ,6,'000000'),:EXPORTATION:,:CODE_CAISSE:,";
            F_SQL = F_SQL + "'" + all_versement_client.get(i).code_client.replace("'", "''") + "','" + format2.format(dt) + "','" + all_versement_client.get(i).carnet_heure + "','SITUATION-CLIENT','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "',";
            F_SQL = F_SQL + " " + all_versement_client.get(i).carnet_versement + ");\n";
            ///////////////////////////////////CARNET CLIENT////////////////////////////////////
            ///////////////////////////////////CAISSE //////////////////////////////////////////
            if (all_versement_client.get(i).carnet_versement != 0) {
                F_SQL = F_SQL + "INSERT INTO CAISSE2 (CODE_CAISSE,CODE_CAISSE1,SOURCE,NUM_SOURCE,ENTREE,";
                F_SQL = F_SQL + "DATE_CAISSE,UTILISATEUR,MODE_RG)\n";
                F_SQL = F_SQL + "VALUES \n";
                F_SQL = F_SQL + "(:CODE_CAISSE:,:CODE_CAISSE:,'SITUATION-CLIENT',lpad ((SELECT GEN_ID(GEN_CARNET_C_ID,0) FROM RDB$DATABASE) ,6,'000000')," + all_versement_client.get(i).carnet_versement + ",";
                F_SQL = F_SQL + "'" + format2.format(dt) + "','TERMINAL_MOBIL','" + all_versement_client.get(i).carnet_mode_rg + "');\n";
            }

            file_name = "SITUATION_VRC" + all_versement_client.get(i).recordid + "_" + all_versement_client.get(i).exportation + "_" + all_versement_client.get(i).carnet_date + ".STC";
            file_name = file_name.replace("/", "_");
            F_SQL_LIST_SITUATION.put(file_name, F_SQL);

        }

    }*/

    // create and prepare "sales" local file to export via FTP

    public String prepare_local_file_sale(PostData_Bon1 bon1, ArrayList<PostData_Bon2> bon2s) {

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");

        // Create the main JSON object
        JSONObject JSON_STRING = new JSONObject();
        JSON_STRING.put("CODE_DEPOT", bon1.code_depot);
        JSON_STRING.put("CODE_VENDEUR", bon1.code_vendeur);
        JSON_STRING.put("DATE_BON", bon1.date_bon);
        JSON_STRING.put("HEURE", bon1.heure);
        JSON_STRING.put("REMISE", bon1.remise);
        JSON_STRING.put("VERSER", bon1.verser);
        JSON_STRING.put("CODE_CLIENT", bon1.code_client);
        JSON_STRING.put("CLIENT", bon1.client);
        JSON_STRING.put("ADRESSE", bon1.adresse);
        JSON_STRING.put("TEL", bon1.tel);
        JSON_STRING.put("SOLDE", bon1.client_solde);

        // Create the items array
        JSONArray itemsArray = new JSONArray();
        for (int j = 0; j < bon2s.size(); j++) {
            JSONObject bon2_item = new JSONObject();
            bon2_item.put("CODE_BARRE", bon2s.get(j).codebarre);
            bon2_item.put("PRODUIT", bon2s.get(j).produit);
            bon2_item.put("NBRE_COLIS", bon2s.get(j).nbr_colis);
            bon2_item.put("COLISSAGE", bon2s.get(j).colissage);
            bon2_item.put("QTE", bon2s.get(j).qte);
            bon2_item.put("QTE_GRAT", bon2s.get(j).gratuit);
            bon2_item.put("PV_HT", bon2s.get(j).pv_ht);
            bon2_item.put("PA_HT", bon2s.get(j).pa_ht);
            bon2_item.put("TVA", bon2s.get(j).tva);
            bon2_item.put("STOCK", bon2s.get(j).stock_produit);
            itemsArray.add(bon2_item);
        }

        JSON_STRING.put("BON1", itemsArray);

        return JSON_STRING.toJSONString();
    }

    public String prepare_local_file_achat(PostData_Achat1 achat1, ArrayList<PostData_Achat2> achat2s) {

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");

        // Create the main JSON object
        JSONObject JSON_STRING = new JSONObject();
        JSON_STRING.put("CODE_DEPOT", achat1.code_depot);
        JSON_STRING.put("DATE_BON", achat1.date_bon);
        JSON_STRING.put("HEURE", achat1.heure);
        JSON_STRING.put("REMISE", achat1.remise);
        JSON_STRING.put("VERSER", achat1.verser);
        JSON_STRING.put("CODE_FRS", achat1.code_frs);
        JSON_STRING.put("FOURNIS", achat1.fournis);
        JSON_STRING.put("ADRESSE", achat1.adresse);
        JSON_STRING.put("TEL", achat1.tel);
        JSON_STRING.put("SOLDE", achat1.solde_ancien);

        // Create the items array
        JSONArray itemsArray = new JSONArray();
        for (int j = 0; j < achat2s.size(); j++) {
            JSONObject bon2_item = new JSONObject();
            bon2_item.put("CODE_BARRE", achat2s.get(j).codebarre);
            bon2_item.put("PRODUIT", achat2s.get(j).produit);
            bon2_item.put("NBRE_COLIS", achat2s.get(j).nbr_colis);
            bon2_item.put("COLISSAGE", achat2s.get(j).colissage);
            bon2_item.put("QTE", achat2s.get(j).qte);
            bon2_item.put("QTE_GRAT", achat2s.get(j).gratuit);
            bon2_item.put("PA_HT", achat2s.get(j).pa_ht);
            bon2_item.put("TVA", achat2s.get(j).tva);
            bon2_item.put("STOCK", achat2s.get(j).stock_produit);
            itemsArray.add(bon2_item);
        }

        JSON_STRING.put("ACHAT1", itemsArray);

        return JSON_STRING.toJSONString();
    }


    // create and prepare "orders" to send to qrcode server
    public String prepare_local_file_order(PostData_Bon1 bon1_temp, ArrayList<PostData_Bon2> bon2s_temp) {

        prefs = mActivity.getSharedPreferences(PREFS, MODE_PRIVATE);
        code_depot = prefs.getString("CODE_DEPOT", "000000");
        code_vendeur = prefs.getString("CODE_VENDEUR", "000000");

        // Create the main JSON object
        JSONObject JSON_STRING = new JSONObject();
        JSON_STRING.put("CODE_DEPOT", bon1_temp.code_depot);
        JSON_STRING.put("CODE_VENDEUR", bon1_temp.code_vendeur);
        JSON_STRING.put("DATE_BON", bon1_temp.date_bon);
        JSON_STRING.put("HEURE", bon1_temp.heure);
        JSON_STRING.put("REMISE", bon1_temp.remise);
        JSON_STRING.put("VERSER", bon1_temp.verser);
        JSON_STRING.put("CODE_CLIENT", bon1_temp.code_client);
        JSON_STRING.put("CLIENT", bon1_temp.client);
        JSON_STRING.put("ADRESSE", bon1_temp.adresse);
        JSON_STRING.put("TEL", bon1_temp.tel);
        JSON_STRING.put("SOLDE", bon1_temp.client_solde);

        // Create the items array
        JSONArray itemsArray = new JSONArray();
        for (int j = 0; j < bon2s_temp.size(); j++) {
            JSONObject bon2_item = new JSONObject();
            bon2_item.put("CODE_BARRE", bon2s_temp.get(j).codebarre);
            bon2_item.put("PRODUIT", bon2s_temp.get(j).produit);
            bon2_item.put("NBRE_COLIS", bon2s_temp.get(j).nbr_colis);
            bon2_item.put("COLISSAGE", bon2s_temp.get(j).colissage);
            bon2_item.put("QTE", bon2s_temp.get(j).qte);
            bon2_item.put("QTE_GRAT", bon2s_temp.get(j).gratuit);
            bon2_item.put("PV_HT", bon2s_temp.get(j).pv_ht);
            bon2_item.put("PA_HT", bon2s_temp.get(j).pa_ht);
            bon2_item.put("TVA", bon2s_temp.get(j).tva);
            bon2_item.put("STOCK", bon2s_temp.get(j).stock_produit);
            itemsArray.add(bon2_item);
        }

        JSON_STRING.put("BON_A2", itemsArray);

        return JSON_STRING.toJSONString();
    }

}
package com.safesoft.proapp.distribute.activities.pdf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.barteksc.pdfviewer.PDFView;
import com.safesoft.proapp.distribute.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class ActivityPDF extends AppCompatActivity {


    PDFView pdfView;
    String SOURCE;
    String NUM_BON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);


        initView();

        if (getIntent() != null) {

            NUM_BON = getIntent().getStringExtra("NUM_BON");
            SOURCE = getIntent().getStringExtra("SOURCE");
            assert SOURCE != null;
            switch (SOURCE) {
                case "FROM_SALE" ->
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Bon de vente N° " + NUM_BON);
                case "FROM_ORDER" ->
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Bon de commande N° " + NUM_BON);
                case "FROM_ACHAT" ->
                        Objects.requireNonNull(getSupportActionBar()).setTitle("Bon d'achat N° " + NUM_BON);
                case "FROM_TICKET" ->
                        Objects.requireNonNull(getSupportActionBar()).setTitle("TICKET PRODUIT ");
            }
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            showPdfFromUri(SOURCE, NUM_BON);
        } else {

        }


    }

    private void initView() {
        pdfView = findViewById(R.id.pdfView);
    }

    private void showPdfFromUri(String SOURCE, String NUM_BON) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = switch (SOURCE) {
                    case "FROM_SALE" -> new File(getCacheDir(), "BON_VENTE_" + NUM_BON + ".pdf");
                    case "FROM_ORDER" ->
                            new File(getCacheDir(), "BON_COMMANDE_" + NUM_BON + ".pdf");
                    case "FROM_ACHAT" -> new File(getCacheDir(), "BON_ACHAT_" + NUM_BON + ".pdf");
                    case "FROM_TICKET" -> new File(getCacheDir(), "TICKET_PRODUIT.pdf");
                    default -> null;
                };
                pdfView.fromFile(file)
                        .defaultPage(0)
                        // .spacing(10)
                        //.enableSwipe(true)
                        .load();
            }
        });

        th.start();
    }

    public void shareFile() {
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();

            File file = switch (SOURCE) {
                case "FROM_SALE" -> new File(getCacheDir(), "BON_VENTE_" + NUM_BON + ".pdf");
                case "FROM_ORDER" -> new File(getCacheDir(), "BON_COMMANDE_" + NUM_BON + ".pdf");
                case "FROM_ACHAT" -> new File(getCacheDir(), "BON_ACHAT_" + NUM_BON + ".pdf");
                case "FROM_TICKET" -> new File(getCacheDir(), "TICKET_PRODUIT.pdf");
                default -> null;
            };

            assert file != null;
            if (file.exists()) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent intent = new Intent(Intent.ACTION_SEND);
                Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(uri, mimeType);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } catch (Exception e) {
            Log.v("sssss", Objects.requireNonNull(e.getMessage()));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.share_pdf) {
            shareFile();
            return true;
        }
        if (id == R.id.print_pdf) {

            //https://stackoverflow.com/questions/33089808/print-existing-pdf-file-in-android
            String pdf_path = null;
            switch (SOURCE) {
                case "FROM_SALE" -> pdf_path = getCacheDir() + "/BON_VENTE_" + NUM_BON + ".pdf";
                case "FROM_ORDER" -> pdf_path = getCacheDir() + "/BON_COMMANDE_" + NUM_BON + ".pdf";
                case "FROM_ACHAT" -> pdf_path = getCacheDir() + "/BON_ACHAT_" + NUM_BON + ".pdf";
                case "FROM_TICKET" -> pdf_path = getCacheDir() + "/TICKET_PRODUIT.pdf";
                default -> {
                }
            }
            printDocument(pdf_path);
        }
        return super.onOptionsItemSelected(item);
    }

    public void printDocument(String path) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printAdapter = new PdfFragmentPrintDocumentAdapter(this, path);
            printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
        } catch (Exception e) {
            // Logger.logError(e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
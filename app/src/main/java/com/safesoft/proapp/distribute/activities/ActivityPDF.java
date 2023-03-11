package com.safesoft.proapp.distribute.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.github.barteksc.pdfviewer.PDFView;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.pdf.GeneratePDF;

import java.io.File;
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

        if(getIntent() != null){

            NUM_BON = getIntent().getStringExtra("NUM_BON");
            SOURCE = getIntent().getStringExtra("SOURCE");
            Objects.requireNonNull(getSupportActionBar()).setTitle("Bon de vente N° " + NUM_BON);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            showPdfFromUri(SOURCE, NUM_BON);
        }else {

        }


    }

    private void initView(){
         pdfView = (PDFView) findViewById(R.id.pdfView);
    }

    private void showPdfFromUri(String SOURCE, String NUM_BON) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                if(SOURCE.equals("FROM_SALE")){
                    file = new File(getCacheDir(), "BON_VENTE_"+NUM_BON+".pdf");
                }else if(SOURCE.equals("FROM_ORDER")){
                    // file = new File(mActivity.getCacheDir(), "safe_pdf_test.pdf");
                }
                pdfView.fromFile(file)
                        .defaultPage(0)
                       // .spacing(10)
                        //.enableSwipe(true)
                        .load();
            }
        });

        th.start();
    }

    public void shareFile(){
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            builder.detectFileUriExposure();

            File file = null;
            if(SOURCE.equals("FROM_SALE")){
                file = new File(getCacheDir(), "BON_VENTE_"+NUM_BON+".pdf");
            }else if(SOURCE.equals("FROM_ORDER")){
                // file = new File(mActivity.getCacheDir(), "safe_pdf_test.pdf");
            }
            assert file != null;
            if (file.exists()) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                Intent intent = new Intent(Intent.ACTION_SEND);
                Uri uri = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION );
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setDataAndType(uri, mimeType);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(intent);
            }
        }catch (Exception e){
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

        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if (id == R.id.share_pdf) {
            shareFile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }
}
package com.safesoft.proapp.distribute.activities.pdf;

import static com.safesoft.proapp.distribute.activities.product.ActivityProduits.produits;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.printing.Printing;

import java.io.File;
import java.util.ArrayList;
import com.github.barteksc.pdfviewer.PDFView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class PdfViewerActivity extends AppCompatActivity {

    private String selected_famile  = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_viewer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Etat de stock");
        }

        PDFView  pdfView = findViewById(R.id.pdfView);
        String path = getIntent().getStringExtra("path");
        selected_famile = getIntent().getStringExtra("selected_famile");
        File file = new File(path);
        Log.d("PDF", "Exists: " + file.exists());
        Log.d("PDF", "Size: " + file.length());
        pdfView.fromFile(file).load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_etat_stock, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else if (item.getItemId() == R.id.print_stock) {
            new SweetAlertDialog(PdfViewerActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Impression")
                    .setContentText("Voulez-vous vraiment imprimer le stock de la séléction ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Imprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        ArrayList<PostData_Produit> produitsWithStock = new ArrayList<>();
                        for (PostData_Produit p : produits) {
                            // stock could be calculated as p.stock, or from (stock_ini, stock_colis, stock_vrac)
                            if (p.stock != 0) {
                                produitsWithStock.add(p);
                            }
                        }
                        Activity bactivity;
                        bactivity = PdfViewerActivity.this;
                        Printing printer = new Printing();
                        printer.start_print_stock(bactivity, "STOCK", produitsWithStock, selected_famile);

                        sDialog.dismiss();

                    }).show();



        }
        return super.onOptionsItemSelected(item);
    }
}
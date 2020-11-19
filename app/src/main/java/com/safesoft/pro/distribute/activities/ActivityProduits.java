package com.safesoft.pro.distribute.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.pro.distribute.activities.vente.ActivityAddProduct;
import com.safesoft.pro.distribute.adapters.RecyclerAdapterProduits;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Produit;
import com.safesoft.pro.distribute.R;

import java.util.ArrayList;

import static com.safesoft.pro.distribute.activities.vente.ActivityAddProduct.BARCODE_KEY;

public class ActivityProduits extends AppCompatActivity implements RecyclerAdapterProduits.ItemClick {

  RecyclerView recyclerView;
  RecyclerAdapterProduits adapter;
  ArrayList<PostData_Produit> produits;
  DATABASE controller;
  private Barcode barcodeResult;

  private  MediaPlayer mp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_produits);
    if(savedInstanceState != null){
      Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
      if(restoredBarcode != null){
        //  result.setText(restoredBarcode.rawValue);
        Toast.makeText(ActivityProduits.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
        barcodeResult = restoredBarcode;
      }
    }
    //toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
    // setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("List Produits");
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));

    controller = new DATABASE(this);

    initViews();

    setRecycle("",false);
  }

  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
  }

  private void setRecycle(String text_search, boolean isscan) {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapterProduits(this, getItems(text_search,isscan));
    recyclerView.setAdapter(adapter);
  }

  public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {
    if(isScan) {
      if (produits.size() > 0) {
        produits = new ArrayList<>();

        String querry = "SELECT * FROM Produit WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "'";
        // querry = "SELECT * FROM Events";
        produits = controller.select_produits_from_database(querry);
      } else {
        produits = new ArrayList<>();

        String querry1 = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '" + querry_search + "'";
        // querry = "SELECT * FROM Events";
        String code_barre = controller.select_codebarre_from_database(querry1);

        String querry2 = "SELECT * FROM Produit WHERE CODE_BARRE = '" + code_barre + "'";
        // querry = "SELECT * FROM Events";
        produits = controller.select_produits_from_database(querry2);


      }
    }else
    {
      if(querry_search.length() >0){
        produits = new ArrayList<>();

        String querry = "SELECT * FROM Produit WHERE PRODUIT LIKE '%"+querry_search+"%' OR CODE_BARRE LIKE '%"+querry_search+"%' OR REF_PRODUIT LIKE '%"+querry_search+"%' ORDER BY PRODUIT";
        // querry = "SELECT * FROM Events";
        produits = controller.select_produits_from_database(querry);
      }else {
        produits = new ArrayList<>();

        String querry = "SELECT * FROM Produit ORDER BY PRODUIT";
        // querry = "SELECT * FROM Events";
        produits = controller.select_produits_from_database(querry);
    }


    }


    return produits;
  }

  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);
    Intent intent = new Intent(ActivityProduits.this, ActivityProduitDetail.class);

    intent.putExtra("CODE_BARRE", produits.get(position).code_barre);
    intent.putExtra("REF_PRODUIT", produits.get(position).ref_produit);
    intent.putExtra("PRODUIT", produits.get(position).produit);
    intent.putExtra("PA", produits.get(position).pa_ht);
    intent.putExtra("TVA", produits.get(position).tva);
    intent.putExtra("PV1_HT", produits.get(position).pv1_ht);
    intent.putExtra("PV2_HT", produits.get(position).pv2_ht);
    intent.putExtra("PV3_HT", produits.get(position).pv3_ht);
    intent.putExtra("STOCK", produits.get(position).stock);
    intent.putExtra("PHOTO", produits.get(position).photo);
    intent.putExtra("DETAILLE", produits.get(position).DETAILLE);

    startActivity(intent);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    MenuInflater inflater = getMenuInflater();
    //   inflater.inflate(R.menu.menu_select_product, menu);
    inflater.inflate(R.menu.menu_bon_vente_commande, menu);
    final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
    searchView.setQueryHint("Rechercher");

//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////

    menu.add(Menu.NONE,Menu.NONE,0,"Rechercher")
            .setIcon(R.mipmap.ic_recherche)
            .setActionView(searchView)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

    // final Context cntx = this;

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextChange(String newText) {
        setRecycle(newText,false);

        return false;
      }

      @Override
      public boolean onQueryTextSubmit(final String query) {

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        Toast.makeText(getBaseContext(), "dummy Search", Toast.LENGTH_SHORT).show();
        setProgressBarIndeterminateVisibility(true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          public void run() {
            //=======

            setProgressBarIndeterminateVisibility(false);

          }
        }, 2000);

        return false;
      }
    });

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.menu_scan){

      startScan();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    Sound(R.raw.back);
    super.onBackPressed();
  }

  public void Sound(int SourceSound){
    mp = MediaPlayer.create(this, SourceSound);
    mp.start();
  }
  private void startScan() {
    /**
     * Build a new MaterialBarcodeScanner
     */

    final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
            .withActivity(ActivityProduits.this)
            .withEnableAutoFocus(true)
            .withBleepEnabled(true)
            .withBackfacingCamera()
            .withCenterTracker()
            .withText("Scanning...")
            .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
              @Override
              public void onResult(Barcode barcode) {
                Sound( R.raw.bleep);
                barcodeResult = barcode;
                // result.setText(barcode.rawValue);
               // Toast.makeText(ActivityProduits.this, ""+barcode.rawValue, Toast.LENGTH_SHORT).show();
                // Do search after barcode scanned
                setRecycle(barcode.rawValue, true);
              }
            })
            .build();
    materialBarcodeScanner.startScan();
  }
}

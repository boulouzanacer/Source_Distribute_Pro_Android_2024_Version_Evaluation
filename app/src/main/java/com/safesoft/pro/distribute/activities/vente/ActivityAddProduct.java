package com.safesoft.pro.distribute.activities.vente;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.pro.distribute.eventsClasses.CheckedPanierEvent;
import com.safesoft.pro.distribute.eventsClasses.CheckedProductEvent;
import com.safesoft.pro.distribute.eventsClasses.DeleteItemEvent;
import com.safesoft.pro.distribute.eventsClasses.MessageEvent;
import com.safesoft.pro.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.postData.PostData_Produit;
import com.safesoft.pro.distribute.R;

import android.support.v7.widget.SearchView;
import android.view.inputmethod.InputMethodManager;
import android.os.Handler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityAddProduct extends AppCompatActivity implements RecyclerAdapterCheckProducts.ItemClick{

    RecyclerView recyclerView;
    RecyclerAdapterCheckProducts adapter;
    ArrayList<PostData_Produit> produits;
    DATABASE controller;
    private EventBus bus ;
    private ArrayList<PostData_Bon2> final_panier;
    private ArrayList<PostData_Produit> temp_produits;
    private String mode_tariff_client;

    private  MediaPlayer mp;

    private String NUM_BON;
    private String CODE_DEPOT;

    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        if(savedInstanceState != null){
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if(restoredBarcode != null){
              //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityAddProduct.this, ""+restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        mode_tariff_client = getIntent().getStringExtra("MODE_TARIFF");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Séléctionner Produits");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        initViews();

        setRecycle("", false);

        // Register as a subscriber
        bus.register(this);
    }

    @Override
    protected void onDestroy() {
        // Unregister
        bus.unregister(this);
        super.onDestroy();
    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        final_panier = new ArrayList<>();
        temp_produits = new ArrayList<>();
        produits = new ArrayList<>();

        if(getIntent() !=null){
            //
            NUM_BON = getIntent().getStringExtra("NUM_BON");
            CODE_DEPOT = getIntent().getStringExtra("CODE_DEPOT");
        }
    }


    private void setRecycle(String text_search, Boolean isScan) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterCheckProducts(this, ActivityAddProduct.this, getItems(text_search, isScan), mode_tariff_client, "1");
        recyclerView.setAdapter(adapter);
        //bus.register(adapter);

    }


    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {

        if(isScan){
            String querry = "SELECT * FROM Produit WHERE CODE_BARRE = '"+querry_search+"' OR REF_PRODUIT = '"+querry_search+"'";
            // querry = "SELECT * FROM Events";
            produits = controller.select_produits_from_database(querry);
            if(produits.size() > 0){
                if(produits.size() > 0 && temp_produits.size() > 0){
                    for(int h = 0; h < produits.size(); h++) {
                        for (int j = 0; j < temp_produits.size(); j++) {
                            if (produits.get(h).code_barre.equals(temp_produits.get(j).code_barre)) {
                                produits.get(h).checked = temp_produits.get(j).checked;
                                produits.get(h).qte_produit = temp_produits.get(j).qte_produit;
                            }
                        }
                    }
                }
            }else{
                String querry1 = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                // querry = "SELECT * FROM Events";
                String code_barre = controller.select_codebarre_from_database(querry1);

                String querry2 = "SELECT * FROM Produit WHERE CODE_BARRE = '"+code_barre+"'";
                // querry = "SELECT * FROM Events";
                produits = controller.select_produits_from_database(querry2);
                    if(produits.size() > 0 && temp_produits.size() > 0){
                        for(int h = 0; h < produits.size(); h++) {
                            for (int j = 0; j < temp_produits.size(); j++) {
                                if (produits.get(h).code_barre.equals(temp_produits.get(j).code_barre)) {
                                    produits.get(h).checked = temp_produits.get(j).checked;
                                    produits.get(h).qte_produit = temp_produits.get(j).qte_produit;
                                }
                            }
                        }
                    }
            }
        }else{
            if(querry_search.length() >0){

                String querry = "SELECT * FROM Produit WHERE ( PRODUIT LIKE '%"+querry_search+"%' OR CODE_BARRE LIKE '%"+querry_search+"%' OR REF_PRODUIT LIKE '%"+querry_search+"%')";
                // querry = "SELECT * FROM Events";
                produits = controller.select_produits_from_database(querry);

                if(produits.size() > 0 && temp_produits.size() > 0){
                    for(int h = 0; h < produits.size(); h++) {
                        for (int j = 0; j < temp_produits.size(); j++) {
                            if (produits.get(h).code_barre.equals(temp_produits.get(j).code_barre)) {
                                produits.get(h).checked = temp_produits.get(j).checked;
                                produits.get(h).qte_produit = temp_produits.get(j).qte_produit;
                            }
                        }
                    }
                }
            }else {

                String querry = "SELECT * FROM Produit";
                // querry = "SELECT * FROM Events";
                produits = controller.select_produits_from_database(querry);

                if(produits.size() > 0 && temp_produits.size() > 0){
                    for(int h = 0; h < produits.size(); h++) {
                        for (int j = 0; j < temp_produits.size(); j++) {
                            if (produits.get(h).code_barre.equals(temp_produits.get(j).code_barre)) {
                                produits.get(h).checked = temp_produits.get(j).checked;
                                produits.get(h).qte_produit = temp_produits.get(j).qte_produit;
                            }

                        }
                    }
                }
            }
        }

        return produits;
    }

    @Override
    public void onClick(View v, int position) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_select_product, menu);
        inflater.inflate(R.menu.menu_bon_vente_commande, menu);

        final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Rechercher");

        //////////////////////////////////////////////////////////////////////
        ///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
        //////////////////////////////////////////////////////////////////////



        menu.add(Menu.NONE,Menu.NONE,0,"Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

       // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText, false);

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

    @Subscribe
    public void onProductCheckedEvent(CheckedProductEvent list_product){

        //temp_produits.clear();
        if(temp_produits.size() > 0){
            for(int m=0; m < temp_produits.size(); m++){
                for(int q=0; q< list_product.getData().size(); q++){
                    if(temp_produits.get(m).code_barre.equals(list_product.getData().get(q).code_barre)){
                        temp_produits.get(m).checked = list_product.getData().get(q).checked ;
                        temp_produits.get(m).qte_produit = list_product.getData().get(q).qte_produit ;
                    }
                }
            }
        }

        temp_produits.addAll(list_product.getData());
    }


    @Subscribe
    public void onEvent(CheckedPanierEvent panier){

      /* if(final_panier != null){
            final_panier.clear();
        }
      */
      boolean item_exist = false;

        if(final_panier.size() > 0) {
            for (int i = 0; i < panier.getData().size(); i++) {
                item_exist = false;
                for (int k = 0; k < final_panier.size(); k++) {
                    if (final_panier.get(k).codebarre.equals(panier.getData().get(i).codebarre)) {
                        item_exist = true;
                        break;
                    }
                }
                if(!item_exist){
                    final_panier.add(panier.getData().get(i));
                }
            }

        } else{
            final_panier.addAll(panier.getData());
        }
    }


    @Subscribe
    public void DeleteItemEvent(DeleteItemEvent item_codebarre){
        for(int s= 0; s< final_panier.size(); s++){
            if(final_panier.get(s).codebarre.equals(item_codebarre.getData())){
                final_panier.remove(s);
            }
        }
    }


    @Subscribe
    public void onMessageRecieve(MessageEvent event){
         final Style ALERTT = new Style.Builder()
                    .setBackgroundColorValue(Color.YELLOW)
                    .build();
        Crouton.makeText(ActivityAddProduct.this, "" + event.getMessage(), ALERTT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }else if(item.getItemId() == R.id.menu_validate){
           // bus.post(new CheckedPanierEvent2(final_panier));
            for(int h = 0; h < final_panier.size(); h++){
                controller.Insert_into_bon2("Bon2", NUM_BON, CODE_DEPOT, final_panier.get(h));
            }

            finish();
        }else if(item.getItemId() == R.id.menu_scan){

            startScan();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Sound( R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int resid){
        mp = MediaPlayer.create(this, resid);
        mp.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan();
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(ActivityAddProduct.this)
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
                      //  Toast.makeText(ActivityAddProduct.this, ""+barcode.rawValue, Toast.LENGTH_SHORT).show();
                        // Do search after barcode scanned
                        setRecycle(barcode.rawValue, true);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }
}

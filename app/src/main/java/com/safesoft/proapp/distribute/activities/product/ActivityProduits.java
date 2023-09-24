package com.safesoft.proapp.distribute.activities.product;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterProduits;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ProductEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.fragments.FragmentNewProduct;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Objects;

public class ActivityProduits extends AppCompatActivity implements RecyclerAdapterProduits.ItemClick {

    RecyclerView recyclerView;
    RecyclerAdapterProduits adapter;
    ArrayList<PostData_Produit> produits;
    DATABASE controller;
    private Barcode barcodeResult;
    private MediaPlayer mp;
    public static final String BARCODE_KEY = "BARCODE";
    private SearchView searchView;
    private TextView nbr_produit;
    private final String PREFS = "ALL_PREFS";
    AutoCompleteTextView famille_dropdown;
    private String selected_famile = "Toutes";
    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produits);

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityProduits.this, "" + restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
                barcodeResult = restoredBarcode;
            }
        }
        //toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        // setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Produits");

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);

        initViews();

        setRecycle("", false);

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_produit);
        nbr_produit = findViewById(R.id.list_produit_nbr_produit);
        famille_dropdown = findViewById(R.id.famille_dropdown);

        ArrayList<String> familles = new ArrayList<>();
        familles = controller.select_familles_from_database("SELECT * FROM FAMILLES");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_famille_item, familles);
        famille_dropdown.setAdapter(adapter);

        //SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        //String TYPE_LOGICIEL = prefs.getString("MODE_FAMILLE", "Tous");
        //famille_dropdown.setSelection(adapter.getPosition(TYPE_LOGICIEL));

        famille_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_famile =  (String) adapterView.getItemAtPosition(i);
                if(selected_famile.equals("<Aucune>")){
                    selected_famile = "";
                }
                setRecycle("", false);
            }
        });
    }

    private void setRecycle(String text_search, boolean isscan) {
        if(isscan){

           // searchView.setIconified(false);
           // searchView.onActionViewExpanded();
            searchView.setQuery(text_search, false);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterProduits(this, getItems(text_search, isscan));
        recyclerView.setAdapter(adapter);
        nbr_produit.setText("Nombre de produit : " + produits.size());
    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {
        String querry = "";
        if(selected_famile.equals("Toutes")){
            if(isScan){
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "'";

                if(produits.size() == 0){
                    String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                }
            }else{
                if(querry_search.length() >0){
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE,DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE PRODUIT LIKE \"%" + querry_search + "%\" OR CODE_BARRE LIKE \"%" + querry_search + "%\" OR REF_PRODUIT LIKE \"%" + querry_search + "%\" ORDER BY PRODUIT";
                }else {
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT ORDER BY PRODUIT";
                }
            }
        }else{

            if(isScan){
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE (CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND FAMILLE = '"+ selected_famile +"'";

                if(produits.size() == 0){
                    String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND FAMILLE = '"+ selected_famile +"'";
                }
            }else{
                if(querry_search.length() >0){
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE (PRODUIT LIKE \"%" + querry_search + "%\" OR CODE_BARRE LIKE \"%" + querry_search + "%\" OR REF_PRODUIT LIKE \"%" + querry_search + "%\") AND FAMILLE = '"+ selected_famile +"' ORDER BY PRODUIT";
                }else {
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE FAMILLE = '"+ selected_famile +"' ORDER BY PRODUIT";

                }
            }
        }

        produits = controller.select_produits_from_database(querry);

        return produits;
    }

    @Subscribe
    public void onProductAdded(ProductEvent productEvent){
        setRecycle("", false);
    }

    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);
        Intent intent = new Intent(ActivityProduits.this, ActivityProduitDetail.class);

        intent.putExtra("CODE_BARRE", produits.get(position).code_barre);
        intent.putExtra("REF_PRODUIT", produits.get(position).ref_produit);
        intent.putExtra("PRODUIT", produits.get(position).produit);
        intent.putExtra("PA_HT", produits.get(position).pa_ht);
        intent.putExtra("TVA", produits.get(position).tva);
        intent.putExtra("PAMP", produits.get(position).pamp);
        intent.putExtra("PV1_HT", produits.get(position).pv1_ht);
        intent.putExtra("PV2_HT", produits.get(position).pv2_ht);
        intent.putExtra("PV3_HT", produits.get(position).pv3_ht);
        intent.putExtra("PV4_HT", produits.get(position).pv4_ht);
        intent.putExtra("PV5_HT", produits.get(position).pv5_ht);
        intent.putExtra("PV6_HT", produits.get(position).pv6_ht);
        intent.putExtra("STOCK", produits.get(position).stock);
        intent.putExtra("COLISSAGE", produits.get(position).colissage);
        intent.putExtra("STOCK_COLIS", produits.get(position).stock_colis);
        intent.putExtra("STOCK_VRAC", produits.get(position).stock_vrac);
        intent.putExtra("PHOTO", produits.get(position).photo);
        intent.putExtra("DESCRIPTION", produits.get(position).description);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        searchView = new SearchView(Objects.requireNonNull(getSupportActionBar()).getThemedContext());

        menu.add(Menu.NONE, Menu.NONE, 0, "Rechercher")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS );

        searchView.setQueryHint("Rechercher");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_products, menu);

        //////////////////////////////////////////////////////////////////////
        ///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
        //////////////////////////////////////////////////////////////////////


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

       // searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.scan) {

            startScan();
        } else if (item.getItemId() == R.id.new_product) {

            FragmentNewProduct fragmentnewproduct = new FragmentNewProduct();
            fragmentnewproduct.showDialogbox(ActivityProduits.this, "NEW_PRODUCT", null);
        }
        return super.onOptionsItemSelected(item);
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
                        Sound(R.raw.bleep);
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

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }


    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }
}

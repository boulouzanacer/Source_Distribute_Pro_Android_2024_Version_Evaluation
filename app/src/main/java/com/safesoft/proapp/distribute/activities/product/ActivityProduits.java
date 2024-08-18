package com.safesoft.proapp.distribute.activities.product;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
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
import com.safesoft.proapp.distribute.fragments.FragmentNewEditProduct;
import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.ImageUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityProduits extends AppCompatActivity implements RecyclerAdapterProduits.ItemClick, RecyclerAdapterProduits.ItemLongClick {

    RecyclerView recyclerView;
    RecyclerAdapterProduits adapter;
    ArrayList<PostData_Produit> produits;
    DATABASE controller;
    private MediaPlayer mp;
    public static final String BARCODE_KEY = "BARCODE";
    private SearchView searchView;
    private TextView nbr_produit, total_prix;
    AutoCompleteTextView famille_dropdown;
    private String selected_famile = "Toutes";
    private EventBus bus;
    FragmentNewEditProduct fragmentnewproduct;
    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produits);

        if (savedInstanceState != null) {
            Barcode restoredBarcode = savedInstanceState.getParcelable(BARCODE_KEY);
            if (restoredBarcode != null) {
                //  result.setText(restoredBarcode.rawValue);
                Toast.makeText(ActivityProduits.this, restoredBarcode.rawValue, Toast.LENGTH_SHORT).show();
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

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        initViews();

        setRecycle("", false);

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_produit);
        nbr_produit = findViewById(R.id.list_produit_nbr_produit);
        total_prix = findViewById(R.id.list_produit_total);
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
                selected_famile = (String) adapterView.getItemAtPosition(i);
                if (selected_famile.equals("<Aucune>")) {
                    selected_famile = "";
                }
                setRecycle("", false);
            }
        });
    }

    private void setRecycle(String text_search, boolean isscan) {
        if (isscan) {

            // searchView.setIconified(false);
            // searchView.onActionViewExpanded();
            searchView.setQuery(text_search, false);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterProduits(this, getItems(text_search, isscan));
        recyclerView.setAdapter(adapter);
        total_prix.setText("Total achats : " + new DecimalFormat("##,##0.00").format(calcule_total()) + " DA");
        nbr_produit.setText("Nombre de produit : " + produits.size());
        if (prefs.getBoolean("AFFICHAGE_PA_HT", false)) {
            total_prix.setVisibility(View.VISIBLE);
        } else {
            total_prix.setVisibility(View.GONE);
        }
    }

    private double calcule_total() {
        double total = 0;
        for (int i = 0; i < produits.size(); i++) {
            if (produits.get(i).stock > 0) {
                total = total + (produits.get(i).stock * produits.get(i).pamp);
            }
        }
        return total;
    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {
        String querry = "";
        if (selected_famile.equals("Toutes")) {
            if (isScan) {
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "'";

                if (produits.isEmpty()) {
                    String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + querry_search + "'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                }
            } else {
                if (!querry_search.isEmpty()) {
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI,PHOTO, DETAILLE, ISNEW, FAMILLE,DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE PRODUIT LIKE \"%" + querry_search + "%\" OR CODE_BARRE LIKE \"%" + querry_search + "%\" OR REF_PRODUIT LIKE \"%" + querry_search + "%\" ORDER BY PRODUIT";
                } else {
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT ORDER BY PRODUIT";
                }
            }
        } else {

            if (isScan) {
                querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE (CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND FAMILLE = '" + selected_famile + "'";

                if (produits.isEmpty()) {
                    String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '" + querry_search + "'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND FAMILLE = '" + selected_famile + "'";
                }
            } else {
                if (!querry_search.isEmpty()) {
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE (PRODUIT LIKE \"%" + querry_search + "%\" OR CODE_BARRE LIKE \"%" + querry_search + "%\" OR REF_PRODUIT LIKE \"%" + querry_search + "%\") AND FAMILLE = '" + selected_famile + "' ORDER BY PRODUIT";
                } else {
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE FAMILLE = '" + selected_famile + "' ORDER BY PRODUIT";

                }
            }
        }

        produits = controller.select_produits_from_database(querry);

        return produits;
    }

    @Subscribe
    public void onProductAdded(ProductEvent productEvent) {
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
        intent.putExtra("STOCK_INI", produits.get(position).stock_ini);
        intent.putExtra("STOCK_COLIS", produits.get(position).stock_colis);
        intent.putExtra("STOCK_VRAC", produits.get(position).stock_vrac);
        intent.putExtra("PHOTO", produits.get(position).photo);
        intent.putExtra("DESCRIPTION", produits.get(position).description);

        intent.putExtra("PROMO", produits.get(position).promo);
        intent.putExtra("D1", produits.get(position).d1);
        intent.putExtra("D2", produits.get(position).d2);
        intent.putExtra("PP1_HT", produits.get(position).pp1_ht);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onLongClick(View v, int position) {
        final CharSequence[] items = {"Modifier", "Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.blue_circle_24);
        builder.setTitle("Choisissez une action");
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
                case 0 -> {

                    new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Modification")
                            .setContentText("Voulez-vous vraiment modifier le produit :  " + produits.get(position).produit + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Modifier")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                FragmentNewEditProduct fragmentnewproduct = new FragmentNewEditProduct();
                                fragmentnewproduct.showDialogbox(ActivityProduits.this, "EDIT_PRODUCT", produits.get(position));
                                sDialog.dismiss();

                            }).show();
                }
                case 1 -> {
                    if (produits.get(position).isNew == 1) {
                        ///// delete product
                        String querry_has_bon2 = "SELECT BON2.CODE_BARRE FROM BON2 LEFT JOIN BON1 ON BON1.NUM_BON == BON2.NUM_BON WHERE BON1.IS_EXPORTED = 0 AND BON2.CODE_BARRE = '" + produits.get(position).code_barre + "'";
                        String querry_has_bon2_temp = "SELECT BON2_TEMP.CODE_BARRE FROM BON2_TEMP LEFT JOIN BON1_TEMP ON BON1_TEMP.NUM_BON == BON2_TEMP.NUM_BON WHERE BON1_TEMP.IS_EXPORTED = 0 AND BON2_TEMP.CODE_BARRE = '" + produits.get(position).code_barre + "'";
                        String querry_has_achat2 = "SELECT ACHAT2.CODE_BARRE FROM ACHAT2 LEFT JOIN ACHAT1 ON ACHAT1.NUM_BON == ACHAT2.NUM_BON WHERE ACHAT1.IS_EXPORTED = 0 AND ACHAT2.CODE_BARRE = '" + produits.get(position).code_barre + "'";

                        if (controller.check_if_has_bon(querry_has_bon2) || controller.check_if_has_bon(querry_has_bon2_temp) || controller.check_if_has_bon(querry_has_achat2)) {
                            // you can't delete this client
                            new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Il exist des bons créer avec ce produit, Suppression impossible")
                                    .show();
                        } else {
                            new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le produit :  " + produits.get(position).produit + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_produit(produits.get(position).code_barre);

                                        setRecycle("", false);
                                        sDialog.dismiss();

                                    }).show();

                        }
                    } else {
                        new SweetAlertDialog(ActivityProduits.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Produit exist sur le serveur, Suppression impossible")
                                .show();
                    }

                }
            }
        });
        builder.show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        searchView = new SearchView(Objects.requireNonNull(getSupportActionBar()).getThemedContext());

        menu.add(Menu.NONE, Menu.NONE, 0, "Rechercher")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

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
            if (fragmentnewproduct == null)
                fragmentnewproduct = new FragmentNewEditProduct();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream blob = new ByteArrayOutputStream();
                    assert imageBitmap != null;
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100 /* Ignored for PNGs */, blob);
                    byte[] inputData = blob.toByteArray();
                    fragmentnewproduct.setImageFromActivity(inputData);
                }
            }
        } else if (requestCode == 4000) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    InputStream iStream;
                    try {
                        iStream = getContentResolver().openInputStream(selectedImage);
                        byte[] inputData = ImageUtils.getBytes(iStream);
                        fragmentnewproduct.setImageFromActivity(inputData);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "An error occured!", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }


}

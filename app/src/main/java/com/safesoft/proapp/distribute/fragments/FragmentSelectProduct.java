package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterCheckProducts;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Codebarre;
import com.safesoft.proapp.distribute.postData.PostData_Produit;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentSelectProduct {

    RecyclerView recyclerView;
    RecyclerAdapterCheckProducts adapter;
    ArrayList<PostData_Produit> produits;
    private static final int CAMERA_PERMISSION = 5;
    DATABASE controller;
    private String mode_tariff_client;
    final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    private boolean hide_stock_moins = true;
    private boolean show_picture_prod = false;
    private EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;
    Context mcontext;
    private EditText editsearch;
    private AppCompatImageButton btn_scan;
    private AppCompatImageButton btn_cancel;
    AutoCompleteTextView famille_dropdown;
    private String selected_famile = "Toutes";
    private String saved_famille = "";
    String SOURCE;
    private Button add_product;

    //PopupWindow display method
    public void showDialogbox(Activity activity, Context context, String mode_tariff_client, String SOURCE) {

        this.activity = activity;
        this.mcontext = context;
        this.SOURCE = SOURCE;

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        controller = new DATABASE(activity);
        bus = EventBus.getDefault();

        this.mode_tariff_client = mode_tariff_client;
        // NUM_BON = num_bon;
        //CODE_DEPOT = code_depot;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.activity_add_product, null);
        dialogBuilder.setView(dialogview);

        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();

        prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        hide_stock_moins = prefs.getBoolean("AFFICHAGE_STOCK_MOINS", false);
        show_picture_prod = prefs.getBoolean("SHOW_PROD_PIC", false);

        initViews(dialogview);


        // Register as a subscriber
        //bus.register(this);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_add_produit);
        editsearch = view.findViewById(R.id.search_field);
        btn_scan = view.findViewById(R.id.scan);
        btn_cancel = view.findViewById(R.id.cancel);
        add_product = view.findViewById(R.id.add_product);
        produits = new ArrayList<>();
        famille_dropdown = view.findViewById(R.id.famille_dropdown);

        ArrayList<String> familles = controller.select_familles_from_database("SELECT * FROM FAMILLES");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mcontext, R.layout.dropdown_famille_item, familles);
        famille_dropdown.setAdapter(adapter);

        prefs = mcontext.getSharedPreferences(PREFS, MODE_PRIVATE);

        // Restore only if FILTRE_SEARCH is true
        if (prefs.getBoolean("FILTRE_SEARCH", false)) {

            saved_famille = prefs.getString("FILTRE_SEARCH_FAMILLE", "");
            editsearch.setText(prefs.getString("FILTRE_SEARCH_VALUE", ""));

            if (!saved_famille.isEmpty() && adapter.getPosition(saved_famille) >= 0) {
                famille_dropdown.setText(saved_famille, false);
                selected_famile = saved_famille.equals("<Aucune>") ? "" : saved_famille;
            }

            setRecycle(editsearch.getText().toString(), false);
        } else {
            editsearch.setText("");
            selected_famile = "Toutes";
            setRecycle("", false);
        }

        famille_dropdown.setOnItemClickListener((adapterView, view1, i, l) -> {
            selected_famile = (String) adapterView.getItemAtPosition(i);
            if (selected_famile.equals("<Aucune>")) {
                selected_famile = "";
            }

            // Save selected family
            prefs.edit().putString("FILTRE_SEARCH_FAMILLE", selected_famile).apply();

            if (prefs.getBoolean("FILTRE_SEARCH", false)) {
                editsearch.setText(prefs.getString("FILTRE_SEARCH_VALUE", ""));
            } else {
                setRecycle(prefs.getString("FILTRE_SEARCH_VALUE", ""), false);
            }
        });

        // Search field listener
        editsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                setRecycle(text, false);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("FILTRE_SEARCH_VALUE", text);
                editor.apply();
            }

            @Override public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });

        btn_scan.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            } else {
                selectProductFromScan();
            }
        });

        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        add_product.setOnClickListener(v -> {
            FragmentNewEditProduct fragmentnewproduct = new FragmentNewEditProduct();
            fragmentnewproduct.showDialogbox(this.activity, "NEW_PRODUCT", null);
            dialog.dismiss();
        });
    }


    private void setRecycle(String text_search, Boolean isScan) {
            if (isScan) {
                editsearch.setText(text_search);
            }

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mcontext);
            recyclerView.setLayoutManager(layoutManager);

            prefs = mcontext.getSharedPreferences(PREFS, MODE_PRIVATE);
            int LAST_CLICKED_POSITION = prefs.getInt("LAST_CLICKED_POSITION", 0);
            recyclerView.scrollToPosition(LAST_CLICKED_POSITION);

            adapter = new RecyclerAdapterCheckProducts(mcontext, activity, getItems(text_search, isScan), mode_tariff_client, dialog, SOURCE);
            recyclerView.setAdapter(adapter);
            //bus.register(adapter);

    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {

        try {

            ///////////////////////////////////CODE BARRE //////////////////////////////////////
            ArrayList<PostData_Codebarre> codebarres = new ArrayList<>();

            String querry_codebarre = "SELECT CODE_BARRE, CODE_BARRE_SYN FROM CODEBARRE WHERE CODE_BARRE != '" + querry_search + "' AND CODE_BARRE_SYN = '" + querry_search + "' ";
            codebarres = controller.select_all_codebarre_from_database(querry_codebarre);
            if(!codebarres.isEmpty()){
                querry_search = codebarres.get(0).code_barre;
            }
            ///////////////////////////////////CODE BARRE //////////////////////////////////////

            String[] words = querry_search.split(" ");
            StringBuilder queryPattern = new StringBuilder();

            for (String word : words) {
                queryPattern.append(word).append("% "); // Add 3-letter prefix with wildcard
            }

            // Trim the trailing space and create the LIKE pattern
            String pattern = queryPattern.toString().trim();

            StringBuilder querry = new StringBuilder();

            if(show_picture_prod){
                // Initialize StringBuilder for dynamic query construction
                querry = new StringBuilder("SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS, DESTOCK_CODE_BARRE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT ");
            }else{
                // Initialize StringBuilder for dynamic query construction
                querry = new StringBuilder("SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, QTE_PROMO, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, PV_LIMITE, STOCK, COLISSAGE, STOCK_INI, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS, DESTOCK_CODE_BARRE, " +
                        "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT ");
            }

            // List to hold query conditions
            List<String> conditions = new ArrayList<>();

            // Add condition for selected family
            if (!selected_famile.equals("Toutes")) {
                conditions.add("FAMILLE = '" + selected_famile + "'");
            }

            // Add conditions based on hide_stock_moins and isScan
            if (hide_stock_moins) {
                if (isScan) {
                    conditions.add("(CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND STOCK > 0");
                } else if (!querry_search.isEmpty()) {
                    conditions.add("(PRODUIT LIKE '%" + pattern + "' OR CODE_BARRE LIKE '%" + pattern + "' OR REF_PRODUIT LIKE '%" + pattern + "') AND STOCK > 0");
                } else {
                    conditions.add("STOCK > 0");
                }
            } else {
                if (isScan) {
                    conditions.add("(CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "')");
                } else if (!querry_search.isEmpty()) {
                    conditions.add("(PRODUIT LIKE '%" + pattern + "' OR CODE_BARRE LIKE '%" + pattern + "' OR REF_PRODUIT LIKE '%" + pattern + "')");
                }
            }

            // Append WHERE clause if there are conditions
            if (!conditions.isEmpty()) {
                querry.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            // Append ORDER BY clause
            querry.append(" ORDER BY PRODUIT");

            // Execute the constructed query
            produits = controller.select_produits_from_database(querry.toString(), show_picture_prod);


        }catch (Exception e){
            new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Erreur. !")
                .setContentText("" + e.getMessage())
                .show();
        }

        return produits;
    }


    private void selectProductFromScan() {

         MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        // Sound( R.raw.bleep);
                        setRecycle(barcode.rawValue, true);
                    }
                }).build();

        materialBarcodeScanner.startScan();
    }

}
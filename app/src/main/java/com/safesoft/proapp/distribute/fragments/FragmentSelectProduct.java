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
import android.widget.AdapterView;
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
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Produit;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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
    String SOURCE;
    private Button add_product;

    //PopupWindow display method
    public void showDialogbox(Activity activity, Context context,  String mode_tariff_client, String SOURCE) {

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


        initViews(dialogview);

        prefs = context.getSharedPreferences(PREFS, MODE_PRIVATE);
        hide_stock_moins = prefs.getBoolean("AFFICHAGE_STOCK_MOINS", false);

        if(prefs.getBoolean("FILTRE_SEARCH", false)){
            editsearch.setText(prefs.getString("FILTRE_SEARCH_VALUE", ""));
        }else {
            editsearch.setText("");
        }

        // Register as a subscriber
        //bus.register(this);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


    }

    private void initViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_add_produit);
        editsearch = (EditText) view.findViewById(R.id.search_field);
        btn_scan = view.findViewById(R.id.scan);
        btn_cancel = view.findViewById(R.id.cancel);

        add_product = (Button) view.findViewById(R.id.add_product);

        produits = new ArrayList<>();

        famille_dropdown = view.findViewById(R.id.famille_dropdown);

        ArrayList<String> familles = new ArrayList<>();
        familles = controller.select_familles_from_database("SELECT * FROM FAMILLES");
        ArrayAdapter adapter = new ArrayAdapter(mcontext, R.layout.dropdown_famille_item, familles);
        famille_dropdown.setAdapter(adapter);
        famille_dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_famile =  (String) adapterView.getItemAtPosition(i);
                if(selected_famile.equals("<Aucune>")){
                    selected_famile = "";
                }
                if(prefs.getBoolean("FILTRE_SEARCH", false)){
                    editsearch.setText(prefs.getString("FILTRE_SEARCH_VALUE", ""));
                }else {
                    setRecycle(prefs.getString("FILTRE_SEARCH_VALUE", ""), false);
                }
            }
        });

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                setRecycle(text, false);
                prefs = mcontext.getSharedPreferences(PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = mcontext.getSharedPreferences(PREFS, MODE_PRIVATE).edit();
                if(prefs.getBoolean("FILTRE_SEARCH", false)){
                    editor.putString("FILTRE_SEARCH_VALUE", text);
                }else{
                    editor.putString("FILTRE_SEARCH_VALUE", text);
                }
                editor.apply();

               /* if(editsearch.isFocused()){

                }*/
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
        });

        btn_scan.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

            }else {
                selectProductFromScan();
            }

        });
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        add_product.setOnClickListener(v -> {
            FragmentNewEditProduct fragmentnewproduct = new FragmentNewEditProduct();
            fragmentnewproduct.showDialogbox(this.activity, "NEW_PRODUCT", null);
            dialog.dismiss();
        });

    }

    private void setRecycle(String text_search, Boolean isScan) {

        if(isScan){
            editsearch.setText(text_search);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mcontext);
        recyclerView.setLayoutManager(layoutManager);

        prefs = mcontext.getSharedPreferences(PREFS, MODE_PRIVATE);
        int LAST_CLICKED_POSITION = prefs.getInt("LAST_CLICKED_POSITION", 0);
        recyclerView.scrollToPosition(LAST_CLICKED_POSITION);

        adapter = new RecyclerAdapterCheckProducts(mcontext , activity, getItems(text_search, isScan), mode_tariff_client, dialog, SOURCE);
        recyclerView.setAdapter(adapter);
        //bus.register(adapter);

    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {
        String querry = "";
        if(selected_famile.equals("Toutes")){

            if(hide_stock_moins){
                if(isScan){
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "' AND STOCK > 0 ";

                    if(produits.isEmpty()){
                        String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                        String code_barre = controller.select_codebarre_from_database(querry1);

                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND STOCK > 0 ";
                    }
                }else{
                    if(!querry_search.isEmpty()){
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE (PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%') AND (STOCK > 0) ORDER BY PRODUIT";
                    }else {
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE STOCK > 0 ORDER BY PRODUIT  ";
                    }
                }
            }else{

                if(isScan){
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "'  ";

                    if(produits.isEmpty()){
                        String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                        String code_barre = controller.select_codebarre_from_database(querry1);

                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                    }
                }else{
                    if(!querry_search.isEmpty()){
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%' ORDER BY PRODUIT ";
                    }else {
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT ORDER BY PRODUIT ";
                    }
                }
            }

        }else {

            if(hide_stock_moins){
                if(isScan){
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT  WHERE (CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND FAMILLE = '"+ selected_famile +"' AND STOCK > 0 ";

                    if(produits.isEmpty()){
                        String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                        String code_barre = controller.select_codebarre_from_database(querry1);

                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND FAMILLE = '"+ selected_famile +"' AND STOCK > 0 ";
                    }
                }else{
                    if(!querry_search.isEmpty()){
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE (PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%') AND FAMILLE = '"+ selected_famile +"' AND (STOCK > 0) ORDER BY PRODUIT";
                    }else {
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE FAMILLE = '"+ selected_famile +"' AND STOCK > 0 ORDER BY PRODUIT  ";
                    }
                }
            }else{

                if(isScan){
                    querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT  WHERE (CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "') AND FAMILLE = '"+ selected_famile +"'  ";

                    if(produits.isEmpty()){
                        String querry1 = "SELECT * FROM CODEBARRE WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                        String code_barre = controller.select_codebarre_from_database(querry1);

                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND FAMILLE = '"+ selected_famile +"'";
                    }
                }else{
                    if(!querry_search.isEmpty()){
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE (PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%') AND FAMILLE = '"+ selected_famile +"' ORDER BY PRODUIT ";
                    }else {
                        querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PAMP, PROMO, D1, D2, PP1_HT, PV1_HT, PV2_HT, PV3_HT, PV4_HT, PV5_HT, PV6_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, ISNEW, FAMILLE, DESTOCK_TYPE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK/PRODUIT.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                                "CASE WHEN PRODUIT.COLISSAGE <> 0 THEN  (PRODUIT.STOCK%PRODUIT.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                                "FROM PRODUIT WHERE FAMILLE = '"+ selected_famile +"' ORDER BY PRODUIT ";
                    }
                }
            }

        }

        produits = controller.select_produits_from_database(querry);

        return produits;
    }


    private void selectProductFromScan() {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
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
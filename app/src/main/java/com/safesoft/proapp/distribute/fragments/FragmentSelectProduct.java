package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
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
    private ArrayList<PostData_Bon2> final_panier;
    private ArrayList<PostData_Produit> temp_produits;
    private String mode_tariff_client;
    final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;

    private MediaPlayer mp;

    // private String NUM_BON;
    //private String CODE_DEPOT;
    private boolean hide_stock_moins = true;

    private EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;
    Context mcontext;

    private EditText editsearch;
    private AppCompatImageButton btn_scan;
    private AppCompatImageButton btn_cancel;
    private String SOURCE_ACTIVITY;

    //PopupWindow display method

    public void showDialogbox(Activity activity, Context context,  String mode_tariff_client, String SOURCE_ACTIVITY) {

        this.activity = activity;
        mcontext = context;
        this.SOURCE_ACTIVITY = SOURCE_ACTIVITY;
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
        if(prefs.getBoolean("AFFICHAGE_STOCK_MOINS", false)){
            hide_stock_moins = true;
        }else{
            hide_stock_moins = false;
        }



        setRecycle("", false);

        // Register as a subscriber
        //bus.register(this);



        //Specify the length and width through constants
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);



    }

    private void initViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_add_produit);
        editsearch = (EditText) view.findViewById(R.id.search_field);
        btn_scan = view.findViewById(R.id.scan);
        btn_cancel = view.findViewById(R.id.cancel);

        final_panier = new ArrayList<>();
        temp_produits = new ArrayList<>();
        produits = new ArrayList<>();

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if(editsearch.isFocused()){
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    setRecycle(text, false);
                }
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

    }

    private void setRecycle(String text_search, Boolean isScan) {

        if(isScan){
            editsearch.setText(text_search);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mcontext);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterCheckProducts(mcontext , activity, getItems(text_search, isScan), mode_tariff_client, dialog, SOURCE_ACTIVITY);
        recyclerView.setAdapter(adapter);
        //bus.register(adapter);

    }

    public ArrayList<PostData_Produit> getItems(String querry_search, Boolean isScan) {

        if(hide_stock_moins){
            if(isScan){
                String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "' AND STOCK > 0 ";
                produits = controller.select_produits_from_database(querry);

                if(produits.size() == 0){
                    String querry1 = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                    String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "' AND STOCK > 0 ";
                    produits = controller.select_produits_from_database(querry2);
                }
            }else{
                if(querry_search.length() >0){
                    String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE,DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE (PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%') AND (STOCK > 0) ORDER BY PRODUIT";
                    produits = controller.select_produits_from_database(querry);
                }else {
                    String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE STOCK > 0 ORDER BY PRODUIT  ";
                    produits = controller.select_produits_from_database(querry);
                }
            }
        }else{

            if(isScan){
                String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                        "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                        "FROM PRODUIT  WHERE CODE_BARRE = '" + querry_search + "' OR REF_PRODUIT = '" + querry_search + "'  ";
                produits = controller.select_produits_from_database(querry);

                if(produits.size() == 0){
                    String querry1 = "SELECT * FROM Codebarre WHERE CODE_BARRE_SYN = '"+querry_search+"'";
                    String code_barre = controller.select_codebarre_from_database(querry1);

                    String querry2 = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE CODE_BARRE = '" + code_barre + "'";
                    produits = controller.select_produits_from_database(querry2);
                }
            }else{
                if(querry_search.length() >0){
                    String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE,DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS , DESTOCK_CODE_BARRE," +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT WHERE PRODUIT LIKE '%" + querry_search + "%' OR CODE_BARRE LIKE '%" + querry_search + "%' OR REF_PRODUIT LIKE '%" + querry_search + "%' ORDER BY PRODUIT ";
                    produits = controller.select_produits_from_database(querry);
                }else {
                    String querry = "SELECT PRODUIT_ID, CODE_BARRE, REF_PRODUIT, PRODUIT, PA_HT, TVA, PV1_HT, PV2_HT, PV3_HT, STOCK, COLISSAGE, PHOTO, DETAILLE, DESTOCK_TYPE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK/Produit.COLISSAGE) ELSE 0 END STOCK_COLIS ,DESTOCK_CODE_BARRE, " +
                            "CASE WHEN Produit.COLISSAGE <> 0 THEN  (Produit.STOCK%Produit.COLISSAGE) ELSE 0 END STOCK_VRAC, DESTOCK_QTE " +
                            "FROM PRODUIT ORDER BY PRODUIT ";
                    produits = controller.select_produits_from_database(querry);
                }
            }
        }


        return produits;
    }


    private void selectProductFromScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */

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
                })
                .build();
        materialBarcodeScanner.startScan();
    }
}
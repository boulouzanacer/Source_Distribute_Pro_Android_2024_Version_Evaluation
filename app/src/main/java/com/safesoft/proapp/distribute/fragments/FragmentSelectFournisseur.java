package com.safesoft.proapp.distribute.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.fournisseur.ActivityFournisseurs;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListClient;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListFournisseur;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentSelectFournisseur {


    private ListView listview;
    private ListViewAdapterListFournisseur adapter;
    private ArrayList<PostData_Fournisseur> listFournisseur;
    private static final int CAMERA_PERMISSION = 5;

    private DATABASE controller;
    private EventBus bus = EventBus.getDefault();
    private Activity activity;
    private AlertDialog dialog;
    private NumberFormat nf;
    private Context mcontext;

    private EditText editsearch;
    private AppCompatImageButton btn_scan;
    private AppCompatImageButton btn_cancel;

    private Button add_fournisseur;
    //PopupWindow display method

    public void showDialogbox(Activity activity, Context context) {

        this.activity = activity;
        mcontext = context;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        controller = new DATABASE(activity);
        bus = EventBus.getDefault();


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_all_fournisseurs, null);
        dialogBuilder.setView(dialogview);


        initViews(dialogview);

        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();

        setListview("", false);

        // Register as a subscriber
        //bus.register(this);






        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);



    }

    private void initViews(View view) {


        editsearch = (EditText) view.findViewById(R.id.search_field);
        add_fournisseur = (Button) view.findViewById(R.id.add_fournisseur);

        btn_scan = view.findViewById(R.id.scan);
        btn_cancel = view.findViewById(R.id.cancel);


        listFournisseur = new ArrayList<>();

        String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS";
        listFournisseur = controller.select_fournisseurs_from_database(querry);

        listview = (ListView) view.findViewById(R.id.list_client);
        editsearch = (EditText) view.findViewById(R.id.search_field);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                if(editsearch.isFocused()){
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    setListview(text, false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        btn_scan.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

            }else{
                startScan();
            }

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        add_fournisseur.setOnClickListener(v -> {
            /*if(bon1.blocage.equals("F")){
                new SweetAlertDialog(ActivityEditSale.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Information!")
                        .setContentText("Ce bon est déja validé")
                        .show();
                return;
            }
            Intent intentAddClient = new Intent(ActivityEditSale.this, ActivityNewClient.class);
            startActivityForResult(intentAddClient, REQUEST_ACTIVITY_NEW_CLIENT);*/

            FragmentNewEditFournisseur fragmentnewfournisseur = new FragmentNewEditFournisseur();
            fragmentnewfournisseur.showDialogbox(this.activity, mcontext, "NEW_FOURNISSEUR", null);
            dialog.dismiss();
        });
    }

    private void setListview(String text_search, Boolean isScan) {
        if(isScan){
            editsearch.setText(text_search);
        }
        adapter = new ListViewAdapterListFournisseur(mcontext, getItems(text_search, isScan), dialog);
        listview.setAdapter(adapter);
        //bus.register(adapter);

    }

    public ArrayList<PostData_Fournisseur> getItems(String querry_search, Boolean isScan) {

        listFournisseur.clear();

        if(isScan){
            String querry = "SELECT * FROM FOURNIS WHERE CODE_FRS = '"+querry_search+"' ORDER BY FOURNIS ";
            // querry = "SELECT * FROM Events";
            listFournisseur = controller.select_fournisseurs_from_database(querry);
        }else{
            if(querry_search.length() >0){

                String querry = "SELECT * FROM FOURNIS WHERE ( FOURNIS LIKE '%"+querry_search+"%' OR CODE_FRS LIKE '%"+querry_search+"%' ) ORDER BY FOURNIS ";
                // querry = "SELECT * FROM Events";
                listFournisseur = controller.select_fournisseurs_from_database(querry);

            }else {

                String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS ";
                // querry = "SELECT * FROM Events";
                listFournisseur = controller.select_fournisseurs_from_database(querry);
            }
        }

        return listFournisseur;
    }


    public void requestPermission(){

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }else{
            startScan();
        }
    }

    private void startScan() {
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
                        setListview(barcode.rawValue, true);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }
}
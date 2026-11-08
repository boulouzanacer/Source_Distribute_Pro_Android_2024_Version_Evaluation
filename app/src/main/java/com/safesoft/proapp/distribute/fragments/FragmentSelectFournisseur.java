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
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListFournisseur;
import com.safesoft.proapp.distribute.databases.DATABASE;
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

    private TextView badgeTotal;
    private TextView badgeDette;
    private TextView badgeCredit;
    private TextView fournisseursCountLabel;

    public void showDialogbox(Activity activity, Context context) {

        this.activity = activity;
        mcontext = context;
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


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Apply immersive mode (hide navigation bar) to the dialog window if enabled.
        com.safesoft.proapp.distribute.app.BaseApplication.applyImmersiveToDialog(dialog.getWindow());


    }

    private void initViews(View view) {


        editsearch = view.findViewById(R.id.search_field);
        add_fournisseur = view.findViewById(R.id.add_fournisseur);

        btn_scan = view.findViewById(R.id.scan);
        btn_cancel = view.findViewById(R.id.cancel);

        badgeTotal = view.findViewById(R.id.badge_total_fournisseurs);
        badgeDette = view.findViewById(R.id.badge_dette_fournisseurs);
        badgeCredit = view.findViewById(R.id.badge_credit_fournisseurs);
        fournisseursCountLabel = view.findViewById(R.id.fournisseurs_count_label);


        listFournisseur = new ArrayList<>();

        String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS";
        listFournisseur = controller.select_fournisseurs_from_database(querry);

        listview = view.findViewById(R.id.list_client);
        editsearch = view.findViewById(R.id.search_field);

        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                if (editsearch.isFocused()) {
                    String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                    setListview(text, false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });

        btn_scan.setOnClickListener(v -> {

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

            } else {
                startScan();
            }

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        add_fournisseur.setOnClickListener(v -> {
            FragmentNewEditFournisseur fragmentnewfournisseur = new FragmentNewEditFournisseur();
            fragmentnewfournisseur.showDialogbox(this.activity, mcontext, "NEW_FOURNISSEUR", null);
            dialog.dismiss();
        });
    }

    private void setListview(String text_search, Boolean isScan) {
        if (isScan) {
            editsearch.setText(text_search);
        }
        ArrayList<PostData_Fournisseur> items = getItems(text_search, isScan);
        updateBadges(items);
        adapter = new ListViewAdapterListFournisseur(mcontext, items, dialog);
        listview.setAdapter(adapter);

    }

    private void updateBadges(ArrayList<PostData_Fournisseur> fournisseurs) {
        int total = fournisseurs.size();
        int dette = 0;
        int credit = 0;
        int zero = 0;
        for (PostData_Fournisseur frs : fournisseurs) {
            if (frs.solde_montant < -0.009) dette++;
            else if (frs.solde_montant > 0.009) credit++;
            else zero++;
        }
        if (badgeTotal != null) badgeTotal.setText(String.valueOf(total));
        if (badgeDette != null) badgeDette.setText(String.valueOf(dette));
        if (badgeCredit != null) badgeCredit.setText(String.valueOf(credit + zero));
        if (fournisseursCountLabel != null) fournisseursCountLabel.setText(String.valueOf(total));
    }

    public ArrayList<PostData_Fournisseur> getItems(String querry_search, Boolean isScan) {

        listFournisseur.clear();

        // 🔥 Escape simple des apostrophes (cause principale de SQLiteException)
        querry_search = (querry_search == null) ? "" : querry_search.replace("'", "''");

        if (isScan) {
            String querry = "SELECT * FROM FOURNIS WHERE CODE_FRS = '" + querry_search + "' ORDER BY FOURNIS ";
            listFournisseur = controller.select_fournisseurs_from_database(querry);
        } else {
            if (querry_search.length() > 0) {

                String querry = "SELECT * FROM FOURNIS WHERE ( FOURNIS LIKE '%" + querry_search + "%' OR CODE_FRS LIKE '%" + querry_search + "%' ) ORDER BY FOURNIS ";
                listFournisseur = controller.select_fournisseurs_from_database(querry);

            } else {

                String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS ";
                listFournisseur = controller.select_fournisseurs_from_database(querry);
            }
        }

        return listFournisseur;
    }


    public void requestPermission() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        } else {
            startScan();
        }
    }

    private void startScan() {

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
                        setListview(barcode.rawValue, true);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }
}

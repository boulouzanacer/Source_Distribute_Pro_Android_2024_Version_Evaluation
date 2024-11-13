package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.ByteDataEvent;
import com.safesoft.proapp.distribute.eventsClasses.ProductEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.postData.PostData_Params;
import com.safesoft.proapp.distribute.postData.PostData_Produit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditProduct {

    final String ALLOWED_CHARACTERS_CODEBARRE = "0123456789ABCDEFGHJK";
    final String ALLOWED_CHARACTERS_REFERENCE = "012345678-9RSTUVWXYZ";

    double val_colissage, val_stock_ini,
            val_prix_achat_ht, val_tva, val_prix_achat_ttc,
            val_prix1_ht, val_prix1_ttc,
            val_prix2_ht, val_prix2_ttc,
            val_prix3_ht, val_prix3_ttc,
            val_prix4_ht, val_prix4_ttc,
            val_prix5_ht, val_prix5_ttc,
            val_prix6_ht, val_prix6_ttc;
    ImageButton generate_codebarre, scan_codebarre;
    ImageButton generate_reference, scan_reference;
    MaterialFancyButton btn_valider, btn_cancel;
    Button btn_from_gallery, btn_from_camera;
    TextInputEditText edt_designation, edt_codebarre,
            edt_reference, edt_colissage, edt_stock_ini,
            edt_prix_achat_ht, edt_tva, edt_prix_achat_ttc,
            edt_prix1_ht, edt_prix1_ttc,
            edt_prix2_ht, edt_prix2_ttc,
            edt_prix3_ht, edt_prix3_ttc,
            edt_prix4_ht, edt_prix4_ttc,
            edt_prix5_ht, edt_prix5_ttc,
            edt_prix6_ht, edt_prix6_ttc;
    LinearLayout lnr_prix1, lnr_prix2, lnr_prix3, lnr_prix4, lnr_prix5, lnr_prix6;
    TextInputLayout txt_input_prix_ht,
            txt_input_tva, txt_input_prix_ttc,
            txt_input_prix1_ht, txt_input_prix1_ttc,
            txt_input_prix2_ht, txt_input_prix2_ttc,
            txt_input_prix3_ht, txt_input_prix3_ttc,
            txt_input_prix4_ht, txt_input_prix4_ttc,
            txt_input_prix5_ht, txt_input_prix5_ttc,
            txt_input_prix6_ht, txt_input_prix6_ttc;
    LinearLayout ly_prix_achat;
    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    ImageView img_product;
    byte[] inputData = null;
    private final String PREFS = "ALL_PREFS";

    PostData_Produit created_produit;
    PostData_Produit old_product;
    private DATABASE controller;
    NumberFormat nf, nq;
    private Barcode barcodeResult;

    private PostData_Params params;
    SharedPreferences prefs;
    //PopupWindow display method

    public void showDialogbox(Activity activity, String SOURCE_ACTIVITY, PostData_Produit old_product) {

        this.activity = activity;
        this.controller = new DATABASE(activity);
        this.old_product = old_product;

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        nq = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nq).applyPattern("####0.##");

        prefs = activity.getSharedPreferences(PREFS, MODE_PRIVATE);
        created_produit = new PostData_Produit();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_product, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


        btn_valider = dialogview.findViewById(R.id.btn_remise);
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_valider.setTextSize(15);
        btn_valider.setIconPosition(POSITION_LEFT);
        btn_valider.setFontIconSize(30);


        btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);

        img_product = dialogview.findViewById(R.id.img_product);

        generate_codebarre = dialogview.findViewById(R.id.generate_codebarre);
        scan_codebarre = dialogview.findViewById(R.id.scan_codebarre);

        generate_reference = dialogview.findViewById(R.id.generate_reference);
        scan_reference = dialogview.findViewById(R.id.scan_reference);

        btn_from_gallery = dialogview.findViewById(R.id.btn_select_from_gallery);
        btn_from_camera = dialogview.findViewById(R.id.btn_select_from_camera);

        ly_prix_achat = dialogview.findViewById(R.id.ly_prix_achat);

        lnr_prix1 = dialogview.findViewById(R.id.lnr_prix1);
        lnr_prix2 = dialogview.findViewById(R.id.lnr_prix2);
        lnr_prix3 = dialogview.findViewById(R.id.lnr_prix3);
        lnr_prix4 = dialogview.findViewById(R.id.lnr_prix4);
        lnr_prix5 = dialogview.findViewById(R.id.lnr_prix5);
        lnr_prix6 = dialogview.findViewById(R.id.lnr_prix6);


        txt_input_prix_ht = dialogview.findViewById(R.id.txt_input_prix_ht);
        txt_input_tva = dialogview.findViewById(R.id.txt_input_tva);
        txt_input_prix_ttc = dialogview.findViewById(R.id.txt_input_prix_ttc);

        txt_input_prix1_ht = dialogview.findViewById(R.id.txt_input_prix1_ht);
        txt_input_prix2_ht = dialogview.findViewById(R.id.txt_input_prix2_ht);
        txt_input_prix3_ht = dialogview.findViewById(R.id.txt_input_prix3_ht);
        txt_input_prix4_ht = dialogview.findViewById(R.id.txt_input_prix4_ht);
        txt_input_prix5_ht = dialogview.findViewById(R.id.txt_input_prix5_ht);
        txt_input_prix6_ht = dialogview.findViewById(R.id.txt_input_prix6_ht);


        txt_input_prix1_ttc = dialogview.findViewById(R.id.txt_input_prix1_ttc);
        txt_input_prix2_ttc = dialogview.findViewById(R.id.txt_input_prix2_ttc);
        txt_input_prix3_ttc = dialogview.findViewById(R.id.txt_input_prix3_ttc);
        txt_input_prix4_ttc = dialogview.findViewById(R.id.txt_input_prix4_ttc);
        txt_input_prix5_ttc = dialogview.findViewById(R.id.txt_input_prix5_ttc);
        txt_input_prix6_ttc = dialogview.findViewById(R.id.txt_input_prix6_ttc);


        edt_designation = dialogview.findViewById(R.id.edt_designation);
        edt_codebarre = dialogview.findViewById(R.id.edt_codebarre);
        edt_reference = dialogview.findViewById(R.id.edt_reference);

        edt_colissage = dialogview.findViewById(R.id.edt_colissage);
        edt_stock_ini = dialogview.findViewById(R.id.edt_stock_ini);

        edt_prix_achat_ht = dialogview.findViewById(R.id.edt_prix_achat_ht);
        edt_tva = dialogview.findViewById(R.id.edt_tva);
        edt_prix_achat_ttc = dialogview.findViewById(R.id.edt_prix_achat_ttc);

        edt_prix1_ht = dialogview.findViewById(R.id.edt_prix1_ht);
        edt_prix2_ht = dialogview.findViewById(R.id.edt_prix2_ht);
        edt_prix3_ht = dialogview.findViewById(R.id.edt_prix3_ht);
        edt_prix4_ht = dialogview.findViewById(R.id.edt_prix4_ht);
        edt_prix5_ht = dialogview.findViewById(R.id.edt_prix5_ht);
        edt_prix6_ht = dialogview.findViewById(R.id.edt_prix6_ht);

        edt_prix1_ttc = dialogview.findViewById(R.id.edt_prix1_ttc);
        edt_prix2_ttc = dialogview.findViewById(R.id.edt_prix2_ttc);
        edt_prix3_ttc = dialogview.findViewById(R.id.edt_prix3_ttc);
        edt_prix4_ttc = dialogview.findViewById(R.id.edt_prix4_ttc);
        edt_prix5_ttc = dialogview.findViewById(R.id.edt_prix5_ttc);
        edt_prix6_ttc = dialogview.findViewById(R.id.edt_prix6_ttc);


        if (SOURCE_ACTIVITY.equals("EDIT_PRODUCT")) {

            edt_codebarre.setText(old_product.code_barre);
            edt_codebarre.setEnabled(false);
            generate_codebarre.setEnabled(false);
            scan_codebarre.setEnabled(false);

            edt_reference.setText(old_product.ref_produit);
            edt_reference.setEnabled(false);
            generate_reference.setEnabled(false);
            scan_reference.setEnabled(false);

            edt_designation.setText(old_product.produit);
            edt_colissage.setText(nq.format(old_product.colissage));
            edt_stock_ini.setText(nq.format(old_product.stock_ini));

            edt_prix_achat_ht.setText(nf.format(old_product.pa_ht));
            edt_tva.setText(nf.format(old_product.tva));

            old_product.pa_ttc = old_product.pa_ht + (old_product.pa_ht * old_product.tva / 100);
            edt_prix_achat_ttc.setText(nf.format(old_product.pa_ttc));


            edt_prix1_ht.setText(nf.format(old_product.pv1_ht));
            edt_prix2_ht.setText(nf.format(old_product.pv2_ht));
            edt_prix3_ht.setText(nf.format(old_product.pv3_ht));
            edt_prix4_ht.setText(nf.format(old_product.pv4_ht));
            edt_prix5_ht.setText(nf.format(old_product.pv5_ht));
            edt_prix6_ht.setText(nf.format(old_product.pv6_ht));


            old_product.pv1_ttc = old_product.pv1_ht + (old_product.pv1_ht * old_product.tva / 100);
            old_product.pv2_ttc = old_product.pv2_ht + (old_product.pv2_ht * old_product.tva / 100);
            old_product.pv3_ttc = old_product.pv3_ht + (old_product.pv3_ht * old_product.tva / 100);
            old_product.pv4_ttc = old_product.pv4_ht + (old_product.pv4_ht * old_product.tva / 100);
            old_product.pv5_ttc = old_product.pv5_ht + (old_product.pv5_ht * old_product.tva / 100);
            old_product.pv6_ttc = old_product.pv6_ht + (old_product.pv6_ht * old_product.tva / 100);

            edt_prix1_ttc.setText(nf.format(old_product.pv1_ttc));
            edt_prix2_ttc.setText(nf.format(old_product.pv2_ttc));
            edt_prix3_ttc.setText(nf.format(old_product.pv3_ttc));
            edt_prix4_ttc.setText(nf.format(old_product.pv4_ttc));
            edt_prix5_ttc.setText(nf.format(old_product.pv5_ttc));
            edt_prix6_ttc.setText(nf.format(old_product.pv6_ttc));

            if (prefs.getBoolean("AFFICHAGE_PA_HT", false)) {
                int black_color = ContextCompat.getColor(activity, R.color.black);
                edt_prix_achat_ttc.setTextColor(black_color);
                edt_prix_achat_ttc.setEnabled(true);
                edt_prix_achat_ht.setTextColor(black_color);
                edt_prix_achat_ht.setEnabled(true);
            }else {
                int white_color = ContextCompat.getColor(activity, R.color.white);
                edt_prix_achat_ttc.setTextColor(white_color);
                edt_prix_achat_ttc.setEnabled(false);
                edt_prix_achat_ht.setTextColor(white_color);
                edt_prix_achat_ht.setEnabled(false);
            }

            if (prefs.getBoolean("EDIT_PRICE", false)) {
                edt_prix1_ttc.setEnabled(false);
                edt_prix2_ttc.setEnabled(false);
                edt_prix3_ttc.setEnabled(false);
                edt_prix4_ttc.setEnabled(false);
                edt_prix5_ttc.setEnabled(false);
                edt_prix6_ttc.setEnabled(false);

                edt_prix1_ht.setEnabled(false);
                edt_prix2_ht.setEnabled(false);
                edt_prix3_ht.setEnabled(false);
                edt_prix4_ht.setEnabled(false);
                edt_prix5_ht.setEnabled(false);
                edt_prix6_ht.setEnabled(false);
            }else{
                edt_prix1_ttc.setEnabled(true);
                edt_prix2_ttc.setEnabled(true);
                edt_prix3_ttc.setEnabled(true);
                edt_prix4_ttc.setEnabled(true);
                edt_prix5_ttc.setEnabled(true);
                edt_prix6_ttc.setEnabled(true);

                edt_prix1_ht.setEnabled(true);
                edt_prix2_ht.setEnabled(true);
                edt_prix3_ht.setEnabled(true);
                edt_prix4_ht.setEnabled(true);
                edt_prix5_ht.setEnabled(true);
                edt_prix6_ht.setEnabled(true);

            }

        }

        ///////////////////
        prefs = activity.getSharedPreferences(PREFS, MODE_PRIVATE);

        params = new PostData_Params();
        params = controller.select_params_from_database("SELECT * FROM PARAMS");

        txt_input_prix1_ht.setHint(params.pv1_titre + " (HT)");
        txt_input_prix2_ht.setHint(params.pv2_titre + " (HT)");
        txt_input_prix3_ht.setHint(params.pv3_titre + " (HT)");
        txt_input_prix4_ht.setHint(params.pv4_titre + " (HT)");
        txt_input_prix5_ht.setHint(params.pv5_titre + " (HT)");
        txt_input_prix6_ht.setHint(params.pv6_titre + " (HT)");

        txt_input_prix1_ttc.setHint(params.pv1_titre + " (TTC)");
        txt_input_prix2_ttc.setHint(params.pv2_titre + " (TTC)");
        txt_input_prix3_ttc.setHint(params.pv3_titre + " (TTC)");
        txt_input_prix4_ttc.setHint(params.pv4_titre + " (TTC)");
        txt_input_prix5_ttc.setHint(params.pv5_titre + " (TTC)");
        txt_input_prix6_ttc.setHint(params.pv6_titre + " (TTC)");


        if (params.prix_2 == 1 || prefs.getBoolean("APP_AUTONOME", false)) {
            lnr_prix2.setVisibility(View.VISIBLE);
        } else {
            lnr_prix2.setVisibility(View.INVISIBLE);
        }

        if (params.prix_3 == 1 || prefs.getBoolean("APP_AUTONOME", false)) {
            lnr_prix3.setVisibility(View.VISIBLE);
        } else {
            lnr_prix3.setVisibility(View.INVISIBLE);
        }

        if (params.prix_4 == 1) {
            lnr_prix4.setVisibility(View.VISIBLE);
        } else {
            lnr_prix4.setVisibility(View.INVISIBLE);
        }

        if (params.prix_5 == 1) {
            lnr_prix5.setVisibility(View.VISIBLE);
        } else {
            lnr_prix5.setVisibility(View.INVISIBLE);
        }

        if (params.prix_6 == 1) {
            lnr_prix6.setVisibility(View.VISIBLE);
        } else {
            lnr_prix6.setVisibility(View.INVISIBLE);
        }


        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
            txt_input_prix_ht.setVisibility(View.VISIBLE);
            txt_input_tva.setVisibility(View.VISIBLE);
            ly_prix_achat.setWeightSum(5);

            txt_input_prix1_ht.setVisibility(View.VISIBLE);
            lnr_prix1.setWeightSum(2);

            txt_input_prix2_ht.setVisibility(View.VISIBLE);
            lnr_prix2.setWeightSum(2);

            txt_input_prix3_ht.setVisibility(View.VISIBLE);
            lnr_prix3.setWeightSum(2);

            txt_input_prix4_ht.setVisibility(View.VISIBLE);
            lnr_prix4.setWeightSum(2);

            txt_input_prix5_ht.setVisibility(View.VISIBLE);
            lnr_prix5.setWeightSum(2);

            txt_input_prix6_ht.setVisibility(View.VISIBLE);
            lnr_prix6.setWeightSum(2);

        } else {

            txt_input_prix_ht.setVisibility(View.GONE);
            txt_input_tva.setVisibility(View.GONE);
            ly_prix_achat.setWeightSum(2);

            txt_input_prix1_ht.setVisibility(View.GONE);
            lnr_prix1.setWeightSum(1);

            txt_input_prix2_ht.setVisibility(View.GONE);
            lnr_prix2.setWeightSum(1);

            txt_input_prix3_ht.setVisibility(View.GONE);
            lnr_prix3.setWeightSum(1);

            txt_input_prix4_ht.setVisibility(View.GONE);
            lnr_prix4.setWeightSum(1);

            txt_input_prix5_ht.setVisibility(View.GONE);
            lnr_prix5.setWeightSum(1);

            txt_input_prix6_ht.setVisibility(View.GONE);
            lnr_prix6.setWeightSum(1);

        }

        generate_codebarre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_codebarre.setText(getRandomString(ALLOWED_CHARACTERS_CODEBARRE));
            }
        });

        scan_codebarre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(view);
            }
        });

        generate_reference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edt_reference.setText(getRandomString(ALLOWED_CHARACTERS_REFERENCE));
            }
        });

        scan_reference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(view);
            }
        });

        btn_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooserGallery();
            }
        });

        btn_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooserCamera();
            }
        });

        btn_valider.setOnClickListener(v -> {
            boolean hasError = false;

            if (edt_codebarre.getText().length() <= 0) {
                edt_codebarre.setError("Code a barre est obligatoire!!");
                hasError = true;
            }

            if (edt_reference.getText().length() <= 0) {
                edt_reference.setError("Reference est obligatoire!!");
                hasError = true;
            }

            if (edt_designation.getText().length() <= 0) {

                edt_designation.setError("Nom de produit est obligatoire!!");
                hasError = true;
            }

            //===================================================================

            if (edt_stock_ini.getText().length() <= 0) {
                edt_stock_ini.setText("0");
                val_stock_ini = 0;
            } else {
                val_stock_ini = Double.parseDouble(edt_stock_ini.getText().toString());
            }

            if (edt_colissage.getText().length() <= 0) {
                edt_colissage.setText("0");
                val_colissage = 0;
            } else {
                val_colissage = Double.parseDouble(edt_colissage.getText().toString());
            }

            //===================================================================

            if (edt_prix_achat_ttc.getText().length() <= 0) {
                edt_prix_achat_ttc.setError("Prix achat (TTC) est obligatoire!!");
                hasError = true;
            }

            if (edt_prix1_ttc.getText().length() <= 0) {
                edt_prix1_ttc.setError(params.pv1_titre + " (TTC) est obligatoire!!");
                hasError = true;
            }

            if (params.prix_2 == 1) {
                if (edt_prix2_ttc.getText().length() <= 0) {
                    edt_prix2_ttc.setError(params.pv2_titre + " (TTC) est obligatoire!!");
                    hasError = true;
                }
            }

            if (params.prix_3 == 1) {
                if (edt_prix3_ttc.getText().length() <= 0) {
                    edt_prix3_ttc.setError(params.pv3_titre + " (TTC) est obligatoire!!");
                    hasError = true;
                }
            }

            if (params.prix_4 == 1) {
                if (edt_prix4_ttc.getText().length() <= 0) {
                    edt_prix4_ttc.setError(params.pv4_titre + " (TTC) est obligatoire!!");
                    hasError = true;
                }
            }

            if (params.prix_5 == 1) {
                if (edt_prix5_ttc.getText().length() <= 0) {
                    edt_prix5_ttc.setError(params.pv5_titre + " (TTC) est obligatoire!!");
                    hasError = true;
                }
            }

            if (params.prix_6 == 1) {
                if (edt_prix6_ttc.getText().length() <= 0) {
                    edt_prix6_ttc.setError(params.pv6_titre + " (TTC) est obligatoire!!");
                    hasError = true;
                }
            }

            if (prefs.getBoolean("AFFICHAGE_HT", false)) {

                if (edt_prix_achat_ht.getText().length() <= 0) {
                    edt_prix_achat_ht.setError("Prix achat HT est obligatoire!!");
                    hasError = true;
                }

                /*if (edt_tva.getText().length() <= 0 ) {
                    edt_tva.setError("TVA est obligatoire!!");
                    hasError = true;
                }*/

                if (edt_prix1_ht.getText().length() <= 0) {
                    edt_prix1_ht.setError("Prix 1 HT est obligatoire!!");
                    hasError = true;
                }

                if (params.prix_2 == 1) {
                    if (edt_prix2_ht.getText().length() <= 0) {
                        edt_prix2_ht.setError("Prix 2 HT est obligatoire!!");
                        hasError = true;
                    }
                }

                if (params.prix_3 == 1) {
                    if (edt_prix3_ht.getText().length() <= 0) {
                        edt_prix3_ht.setError("Prix 3 HT est obligatoire!!");
                        hasError = true;
                    }
                }

                if (params.prix_4 == 1) {
                    if (edt_prix4_ht.getText().length() <= 0) {
                        edt_prix4_ht.setError("Prix 4 HT est obligatoire!!");
                        hasError = true;
                    }
                }

                if (params.prix_5 == 1) {
                    if (edt_prix5_ht.getText().length() <= 0) {
                        edt_prix5_ht.setError("Prix 5 HT est obligatoire!!");
                        hasError = true;
                    }
                }

                if (params.prix_6 == 1) {
                    if (edt_prix6_ht.getText().length() <= 0) {
                        edt_prix6_ht.setError("Prix 6 HT est obligatoire!!");
                        hasError = true;
                    }
                }

            }

            if (!hasError) {

                created_produit.produit = edt_designation.getText().toString();
                created_produit.code_barre = edt_codebarre.getText().toString();
                created_produit.ref_produit = edt_reference.getText().toString();
                created_produit.photo = inputData;
                created_produit.famille = "";

                created_produit.stock_ini = val_stock_ini;
                created_produit.colissage = val_colissage;

                created_produit.pa_ht = Double.parseDouble(edt_prix_achat_ht.getText().toString());
                created_produit.tva = Double.parseDouble(edt_tva.getText().toString());
                created_produit.pa_ttc = Double.parseDouble(edt_tva.getText().toString());

                created_produit.isNew = 1;

                created_produit.pv1_ht = Double.parseDouble(edt_prix1_ht.getText().toString());
                created_produit.pv1_ttc = created_produit.pv1_ht + (created_produit.pv1_ht * created_produit.tva / 100);

                if (params.prix_2 == 1 || prefs.getBoolean("APP_AUTONOME", false)) {
                    created_produit.pv2_ht = Double.parseDouble(edt_prix2_ht.getText().toString());
                    created_produit.pv2_ttc = created_produit.pv2_ht + (created_produit.pv2_ht * created_produit.tva / 100);
                } else {
                    created_produit.pv2_ht = 0.00;
                    created_produit.pv2_ttc = 0.00;
                }

                if (params.prix_3 == 1 || prefs.getBoolean("APP_AUTONOME", false)) {
                    created_produit.pv3_ht = Double.parseDouble(edt_prix3_ht.getText().toString());
                    created_produit.pv3_ttc = created_produit.pv3_ht + (created_produit.pv3_ht * created_produit.tva / 100);
                } else {
                    created_produit.pv3_ht = 0.00;
                    created_produit.pv3_ttc = 0.00;
                }

                if (params.prix_4 == 1) {
                    created_produit.pv4_ht = Double.parseDouble(edt_prix4_ht.getText().toString());
                    created_produit.pv4_ttc = created_produit.pv4_ht + (created_produit.pv4_ht * created_produit.tva / 100);
                } else {
                    created_produit.pv4_ht = 0.00;
                    created_produit.pv4_ttc = 0.00;
                }

                if (params.prix_5 == 1) {
                    created_produit.pv5_ht = Double.parseDouble(edt_prix5_ht.getText().toString());
                    created_produit.pv5_ttc = created_produit.pv5_ht + (created_produit.pv5_ht * created_produit.tva / 100);
                } else {
                    created_produit.pv5_ht = 0.00;
                    created_produit.pv5_ttc = 0.00;
                }

                if (params.prix_6 == 1) {
                    created_produit.pv6_ht = Double.parseDouble(edt_prix6_ht.getText().toString());
                    created_produit.pv6_ttc = created_produit.pv6_ht + (created_produit.pv6_ht * created_produit.tva / 100);
                } else {
                    created_produit.pv6_ht = 0.00;
                    created_produit.pv6_ttc = 0.00;
                }

                if (SOURCE_ACTIVITY.equals("EDIT_PRODUCT")) {

                    created_produit.stock = old_product.stock -old_product.stock_ini + val_stock_ini;

                    try {
                        //update client into database,
                        controller.update_into_produit(created_produit);
                        Crouton.makeText(activity, "Produit bien modifier", Style.INFO).show();
                        ProductEvent added_product_event = new ProductEvent(created_produit);
                        bus.post(added_product_event);

                        dialog.dismiss();
                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème mise à jour produit : " + e.getMessage())
                                .show();
                    }
                } else {
                    created_produit.stock = val_stock_ini;
                    try {
                        //update client into database,
                        controller.insert_into_produit(created_produit);
                        Crouton.makeText(activity, "Produit bien ajouté", Style.INFO).show();
                        ProductEvent added_product_event = new ProductEvent(created_produit);
                        bus.post(added_product_event);

                        dialog.dismiss();
                    }catch (Exception e){
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention. !")
                                .setContentText("Problème insertion : " + e.getMessage())
                                .show();
                    }

                }

                EventBus.getDefault().unregister(this);

            }

        });

        edt_prix_achat_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix_achat_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {

                        onPrixAchatHTChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_tva.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix_achat_ht.isFocused() && !edt_prix_achat_ttc.isFocused()) {

                    try {

                        onTvaChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });

        edt_prix_achat_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix_achat_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrixAchatTTCChange();
                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix1_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix1_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix1TTCChange();
                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix2_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix2_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix2TTCChange();
                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix3_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix3_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix3TTCChange();
                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix4_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix4_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix4TTCChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix5_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix5_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix5TTCChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix6_ttc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix6_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix6TTCChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix1_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix1_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix1HtChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix2_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix2_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix2HtChange();
                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix3_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix3_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix3HtChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix4_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix4_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrix4HtChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix5_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix5_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {

                        onPrix5HtChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        edt_prix6_ht.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!edt_prix6_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {

                        onPrix6HtChange();

                    } catch (Exception ignored) {

                    }
                }

            }
        });


        btn_cancel.setOnClickListener(v -> {
            EventBus.getDefault().unregister(this);
            dialog.dismiss();
        });


        EventBus.getDefault().register(this);
    }

    void onPrixAchatHTChange() {

        if (edt_prix_achat_ht.getText().toString().isEmpty()) {
            val_prix_achat_ht = 0.00;
        } else {
            val_prix_achat_ht = Double.parseDouble(edt_prix_achat_ht.getText().toString());
        }


        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
            edt_tva.setText("0");
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix_achat_ttc = val_prix_achat_ht + (val_prix_achat_ht * val_tva / 100);

        edt_prix_achat_ttc.setText(nq.format(val_prix_achat_ttc));

    }

    void onTvaChange() {

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        if (edt_prix_achat_ht.getText().toString().isEmpty()) {
            val_prix_achat_ht = 0.00;
        } else {
            val_prix_achat_ht = Double.parseDouble(edt_prix_achat_ht.getText().toString());
        }

        if (edt_prix1_ht.getText().toString().isEmpty()) {
            val_prix1_ht = 0.00;
        } else {
            val_prix1_ht = Double.parseDouble(edt_prix1_ht.getText().toString());
        }

        if (edt_prix2_ht.getText().toString().isEmpty()) {
            val_prix2_ht = 0.00;
        } else {
            val_prix2_ht = Double.parseDouble(edt_prix2_ht.getText().toString());
        }

        if (edt_prix3_ht.getText().toString().isEmpty()) {
            val_prix3_ht = 0.00;
        } else {
            val_prix3_ht = Double.parseDouble(edt_prix3_ht.getText().toString());
        }

        if (edt_prix4_ht.getText().toString().isEmpty()) {
            val_prix4_ht = 0.00;
        } else {
            val_prix4_ht = Double.parseDouble(edt_prix4_ht.getText().toString());
        }

        if (edt_prix5_ht.getText().toString().isEmpty()) {
            val_prix5_ht = 0.00;
        } else {
            val_prix5_ht = Double.parseDouble(edt_prix5_ht.getText().toString());
        }

        if (edt_prix6_ht.getText().toString().isEmpty()) {
            val_prix6_ht = 0.00;
        } else {
            val_prix6_ht = Double.parseDouble(edt_prix6_ht.getText().toString());
        }

        val_prix_achat_ttc = val_prix_achat_ht + (val_prix_achat_ht * val_tva / 100);
        val_prix1_ttc = val_prix1_ht + (val_prix1_ht * val_tva / 100);
        val_prix2_ttc = val_prix2_ht + (val_prix2_ht * val_tva / 100);
        val_prix3_ttc = val_prix3_ht + (val_prix3_ht * val_tva / 100);
        val_prix4_ttc = val_prix4_ht + (val_prix4_ht * val_tva / 100);
        val_prix5_ttc = val_prix5_ht + (val_prix5_ht * val_tva / 100);
        val_prix6_ttc = val_prix6_ht + (val_prix6_ht * val_tva / 100);

        edt_prix_achat_ttc.setText(nq.format(val_prix_achat_ttc));
        edt_prix1_ttc.setText(nq.format(val_prix1_ttc));
        edt_prix2_ttc.setText(nq.format(val_prix2_ttc));
        edt_prix3_ttc.setText(nq.format(val_prix3_ttc));
        edt_prix4_ttc.setText(nq.format(val_prix4_ttc));
        edt_prix5_ttc.setText(nq.format(val_prix5_ttc));
        edt_prix6_ttc.setText(nq.format(val_prix6_ttc));

    }

    void onPrixAchatTTCChange() {

        if (edt_prix_achat_ttc.getText().toString().isEmpty()) {
            val_prix_achat_ttc = 0.00;
        } else {
            val_prix_achat_ttc = Double.parseDouble(edt_prix_achat_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
            edt_tva.setText("0");
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix_achat_ht = val_prix_achat_ttc / (1 + val_tva / 100);

        edt_prix_achat_ht.setText(nq.format(val_prix_achat_ht));

    }


    void onPrix1TTCChange() {

        if (edt_prix1_ttc.getText().toString().isEmpty()) {
            val_prix1_ttc = 0.00;
        } else {
            val_prix1_ttc = Double.parseDouble(edt_prix1_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix1_ht = val_prix1_ttc / (1 + val_tva / 100);

        edt_prix1_ht.setText(nq.format(val_prix1_ht));

    }

    void onPrix2TTCChange() {

        if (edt_prix2_ttc.getText().toString().isEmpty()) {
            val_prix2_ttc = 0.00;
        } else {
            val_prix2_ttc = Double.parseDouble(edt_prix2_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix2_ht = val_prix2_ttc / (1 + val_tva / 100);

        edt_prix2_ht.setText(nq.format(val_prix2_ht));

    }

    void onPrix3TTCChange() {

        if (edt_prix3_ttc.getText().toString().isEmpty()) {
            val_prix3_ttc = 0.00;
        } else {
            val_prix3_ttc = Double.parseDouble(edt_prix3_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix3_ht = val_prix3_ttc / (1 + val_tva / 100);

        edt_prix3_ht.setText(nq.format(val_prix3_ht));

    }

    void onPrix4TTCChange() {

        if (edt_prix4_ttc.getText().toString().isEmpty()) {
            val_prix4_ttc = 0.00;
        } else {
            val_prix4_ttc = Double.parseDouble(edt_prix4_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix4_ht = val_prix4_ttc / (1 + val_tva / 100);

        edt_prix4_ht.setText(nq.format(val_prix4_ht));

    }

    void onPrix5TTCChange() {

        if (edt_prix5_ttc.getText().toString().isEmpty()) {
            val_prix5_ttc = 0.00;
        } else {
            val_prix5_ttc = Double.parseDouble(edt_prix5_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix5_ht = val_prix5_ttc / (1 + val_tva / 100);

        edt_prix5_ht.setText(nq.format(val_prix5_ht));

    }

    void onPrix6TTCChange() {

        if (edt_prix6_ttc.getText().toString().isEmpty()) {
            val_prix6_ttc = 0.00;
        } else {
            val_prix6_ttc = Double.parseDouble(edt_prix6_ttc.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix6_ht = val_prix6_ttc / (1 + val_tva / 100);

        edt_prix6_ht.setText(nq.format(val_prix6_ht));

    }

    //===================================
    void onPrix1HtChange() {

        if (edt_prix1_ht.getText().toString().isEmpty()) {
            val_prix1_ht = 0.00;
        } else {
            val_prix1_ht = Double.parseDouble(edt_prix1_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix1_ttc = val_prix1_ht + (val_prix1_ht * val_tva / 100);

        edt_prix1_ttc.setText(nq.format(val_prix1_ttc));

    }

    void onPrix2HtChange() {

        if (edt_prix2_ht.getText().toString().isEmpty()) {
            val_prix2_ht = 0.00;
        } else {
            val_prix2_ht = Double.parseDouble(edt_prix2_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix2_ttc = val_prix2_ht + (val_prix2_ht * val_tva / 100);

        edt_prix2_ttc.setText(nq.format(val_prix2_ttc));

    }

    void onPrix3HtChange() {

        if (edt_prix3_ht.getText().toString().isEmpty()) {
            val_prix3_ht = 0.00;
        } else {
            val_prix3_ht = Double.parseDouble(edt_prix3_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix3_ttc = val_prix3_ht + (val_prix3_ht * val_tva / 100);

        edt_prix3_ttc.setText(nq.format(val_prix3_ttc));

    }

    void onPrix4HtChange() {

        if (edt_prix4_ht.getText().toString().isEmpty()) {
            val_prix4_ht = 0.00;
        } else {
            val_prix4_ht = Double.parseDouble(edt_prix4_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix4_ttc = val_prix4_ht + (val_prix4_ht * val_tva / 100);

        edt_prix4_ttc.setText(nq.format(val_prix4_ttc));

    }

    void onPrix5HtChange() {

        if (edt_prix5_ht.getText().toString().isEmpty()) {
            val_prix5_ht = 0.00;
        } else {
            val_prix5_ht = Double.parseDouble(edt_prix5_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix5_ttc = val_prix5_ht + (val_prix5_ht * val_tva / 100);

        edt_prix5_ttc.setText(nq.format(val_prix5_ttc));

    }

    void onPrix6HtChange() {

        if (edt_prix6_ht.getText().toString().isEmpty()) {
            val_prix6_ht = 0.00;
        } else {
            val_prix6_ht = Double.parseDouble(edt_prix6_ht.getText().toString());
        }

        if (edt_tva.getText().toString().isEmpty()) {
            val_tva = 0.00;
        } else {
            val_tva = Double.parseDouble(edt_tva.getText().toString());
        }

        val_prix6_ttc = val_prix6_ht + (val_prix6_ht * val_tva / 100);

        edt_prix6_ttc.setText(nq.format(val_prix6_ttc));

    }

    private void startScan(View view) {

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(activity)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> {
                    barcodeResult = barcode;

                    if (view.getId() == R.id.scan_codebarre) {
                        // check if barcode is exist in database
                        String querry = "SELECT * FROM PRODUIT WHERE CODE_BARRE = '" + barcodeResult.rawValue + "'";
                        if(!controller.check_product_if_exist(querry)){
                            edt_codebarre.setText(barcodeResult.rawValue);
                        }else {
                            edt_codebarre.setHint("Produit / Codebarre exist ");
                        }
                    } else if (view.getId() == R.id.scan_reference) {
                        edt_reference.setText(barcodeResult.rawValue);
                    }

                })
                .build();
        materialBarcodeScanner.startScan();
    }

    private String getRandomString(String allowed_caracters) {
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(13);
        for (int i = 0; i < 13; ++i)
            sb.append(allowed_caracters.charAt(random.nextInt(allowed_caracters.length())));
        return sb.toString();
    }

    void imageChooserCamera() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(takePicture, 3000);
    }

    void imageChooserGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickPhoto, 4000);
    }

    public void setImageFromActivity(byte[] inputData) {
        this.inputData = inputData;
        Bitmap bitmap = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
        img_product.setImageBitmap(bitmap);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onByteDataRecieved(ByteDataEvent byteDataEvent) {
        this.inputData = byteDataEvent.getByteData();
        Bitmap bitmap = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);
        img_product.setImageBitmap(bitmap);
    }
}
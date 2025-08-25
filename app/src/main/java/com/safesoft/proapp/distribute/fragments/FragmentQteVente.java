package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventBon2;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentQteVente {

    private DATABASE controller;
    MaterialFancyButton btn_valider, btn_cancel;
    TextView txv_produit, txv_message, txv_promotion, txv_last_price;
    TextInputLayout stockavantLayout_colis, stockapresLayout_colis, prixhtLayout, tvaLayout, ugLayout;
    LinearLayout ly_prix_ttc, part_3_qtevente_Layout;
    TextInputEditText edt_colissage, edt_nbr_colis, edt_qte, edt_gratuit, edt_prix_ht, edt_tva, edt_prix_ttc, edt_stock_avant, edt_stock_avant_colis, edt_stock_apres, edt_stock_apres_colis;
    double val_nbr_colis, val_colissage, val_qte, val_gratuit, val_prix_ht, val_tva, val_prix_ttc, val_stock_avant, val_stock_avant_colis, val_stock_apres, val_stock_apres_colis, val_qte_old, val_gratuit_old;
    private Context mContext;
    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    NumberFormat nf, nq;
    PostData_Bon2 arrived_bon2;
    boolean if_bon2_exist = false;
    private final String PREFS = "ALL_PREFS";
    private String SOURCE_LOCAL;
    SharedPreferences prefs;
    String TYPE_LOGICIEL;
    double last_price = 0;

    //PopupWindow display method

    private boolean isUpdating = false;

    public void showDialogbox(String SOURCE, Activity activity, Context context, PostData_Bon2 bon2, double last_price) throws ParseException {

        mContext = context;
        this.activity = activity;
        this.arrived_bon2 = bon2;
        this.SOURCE_LOCAL = SOURCE;
        this.last_price = last_price;
        this.controller = new DATABASE(mContext);

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        nq = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nq).applyPattern("####0.##");


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_qte_vente, null);
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


        btn_valider = dialogview.findViewById(R.id.btn_connect);
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


        stockavantLayout_colis = dialogview.findViewById(R.id.stockavantLayout_colis);
        stockapresLayout_colis = dialogview.findViewById(R.id.stockapresLayout_colis);
        tvaLayout = dialogview.findViewById(R.id.tvaLayout);
        prixhtLayout = dialogview.findViewById(R.id.prixhtLayout);
        ly_prix_ttc = dialogview.findViewById(R.id.ly_prix);
        part_3_qtevente_Layout = dialogview.findViewById(R.id.part_3_qtevente_Layout);
        ugLayout = dialogview.findViewById(R.id.gratuitLayout);


        txv_produit = dialogview.findViewById(R.id.produit_title);
        txv_message = dialogview.findViewById(R.id.message_title);
        txv_promotion = dialogview.findViewById(R.id.promotion_title);
        txv_last_price = dialogview.findViewById(R.id.txv_last_price);

        edt_stock_avant = dialogview.findViewById(R.id.stockavant);
        edt_stock_avant_colis = dialogview.findViewById(R.id.stockavant_colis);
        edt_nbr_colis = dialogview.findViewById(R.id.nbrColis);
        edt_colissage = dialogview.findViewById(R.id.colissage);
        edt_qte = dialogview.findViewById(R.id.qte);
        edt_gratuit = dialogview.findViewById(R.id.gratuit);
        edt_prix_ht = dialogview.findViewById(R.id.prixht);
        edt_tva = dialogview.findViewById(R.id.tva);

        edt_prix_ttc = dialogview.findViewById(R.id.prixttc);
        edt_stock_apres = dialogview.findViewById(R.id.stockapres);
        edt_stock_apres_colis = dialogview.findViewById(R.id.stockapres_colis);

        prefs = mContext.getSharedPreferences(PREFS, MODE_PRIVATE);

        if (prefs.getBoolean("AFFICHAGE_HT", false)) {
            tvaLayout.setVisibility(View.VISIBLE);
            prixhtLayout.setVisibility(View.VISIBLE);
            ly_prix_ttc.setWeightSum(5);

        } else {
            tvaLayout.setVisibility(View.GONE);
            prixhtLayout.setVisibility(View.GONE);
            ly_prix_ttc.setWeightSum(2);
        }

        if (prefs.getBoolean("CAN_EDIT_PRICE", false)) {
            edt_prix_ttc.setEnabled(true);
            edt_prix_ht.setEnabled(true);
            edt_tva.setEnabled(true);
        } else {
            edt_prix_ttc.setEnabled(false);
            edt_prix_ht.setEnabled(false);
            edt_tva.setEnabled(false);
        }

        if (last_price != 0) {
            txv_last_price.setText("Dernier prix vendu pour ce client : " + nf.format(last_price));
        }

        if (prefs.getBoolean("VENTE_WITH_QTE_GRAT", false)) {
            TYPE_LOGICIEL = prefs.getString("TYPE_LOGICIEL", "PME PRO");
            if (TYPE_LOGICIEL.equals("PME PRO")) {
                ugLayout.setVisibility(View.VISIBLE);
                part_3_qtevente_Layout.setWeightSum(4);
            } else {
                ugLayout.setVisibility(View.GONE);
                part_3_qtevente_Layout.setWeightSum(3);
            }
        } else {
            ugLayout.setVisibility(View.GONE);
            part_3_qtevente_Layout.setWeightSum(3);
        }

        //********************************************************************
        if (SOURCE_LOCAL.equals("BON2_INSERT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT")) {
            PostData_Bon2 checked_bon2 = new PostData_Bon2();
            switch (SOURCE_LOCAL) {
                case "BON2_INSERT" -> {
                    String querry = "SELECT " +
                            "BON2.RECORDID, " +
                            "BON2.CODE_BARRE, " +
                            "BON2.NUM_BON, " +
                            "BON2.PRODUIT, " +
                            "BON2.NBRE_COLIS, " +
                            "BON2.COLISSAGE, " +
                            "BON2.QTE, " +
                            "BON2.QTE_GRAT, " +
                            "BON2.PV_HT, " +
                            "BON2.PA_HT, " +
                            "BON2.TVA, " +
                            "BON2.CODE_DEPOT, " +
                            "PRODUIT.PV_LIMITE, " +
                            "PRODUIT.STOCK " +
                            "FROM BON2 " +
                            "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2.NUM_BON = '" + arrived_bon2.num_bon + "' AND BON2.CODE_BARRE = '" + arrived_bon2.codebarre + "'";

                    checked_bon2 = controller.check_if_bon2_exist(querry);
                    SOURCE_LOCAL = "BON2_EDIT";
                }
                case "BON2_TEMP_INSERT" -> {
                    String querry = "SELECT " +
                            "BON2_TEMP.RECORDID, " +
                            "BON2_TEMP.CODE_BARRE, " +
                            "BON2_TEMP.NUM_BON, " +
                            "BON2_TEMP.PRODUIT, " +
                            "BON2_TEMP.NBRE_COLIS, " +
                            "BON2_TEMP.COLISSAGE, " +
                            "BON2_TEMP.QTE, " +
                            "BON2_TEMP.QTE_GRAT, " +
                            "BON2_TEMP.PV_HT, " +
                            "BON2_TEMP.PA_HT, " +
                            "BON2_TEMP.TVA, " +
                            "BON2_TEMP.CODE_DEPOT, " +
                            "PRODUIT.PV_LIMITE, " +
                            "PRODUIT.STOCK " +
                            "FROM BON2_TEMP " +
                            "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2_TEMP.NUM_BON = '" + arrived_bon2.num_bon + "' AND BON2_TEMP.CODE_BARRE = '" + arrived_bon2.codebarre + "'";
                    checked_bon2 = controller.check_if_bon2_exist(querry);
                    SOURCE_LOCAL = "BON2_TEMP_INSERT";
                }
            }

            if (checked_bon2 != null) {
                txv_message.setText("Produit déja inseré avec quantité: " + nq.format(checked_bon2.qte));
                if_bon2_exist = true;
                arrived_bon2 = checked_bon2;
            }

        }

        val_stock_avant = arrived_bon2.stock_produit;
        val_colissage = arrived_bon2.colissage;
        if (val_colissage == 0) {
            val_stock_avant_colis = 0.0;
        } else {
            val_stock_avant_colis = (int) (val_stock_avant / val_colissage);
        }

        val_prix_ht = arrived_bon2.pv_ht;


        val_tva = arrived_bon2.tva;
        val_prix_ttc = val_prix_ht * (1 + (bon2.tva / 100));

        if (val_colissage == 0.0) {
            stockavantLayout_colis.setVisibility(View.GONE);
            stockapresLayout_colis.setVisibility(View.GONE);
        } else {
            stockavantLayout_colis.setVisibility(View.VISIBLE);
            stockapresLayout_colis.setVisibility(View.VISIBLE);
        }
        //******************************************************************

        if (SOURCE_LOCAL.equals("BON2_INSERT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT") || SOURCE_LOCAL.equals("ACHAT2_INSERT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_INSERT")) {

            edt_stock_apres.setText(nq.format(val_stock_avant));
            edt_stock_apres_colis.setText(nq.format(val_stock_avant_colis));
            val_nbr_colis = 0.0;
            val_qte_old = 0.0;
            val_gratuit_old = 0.0;
            val_qte = 0.0;

        } else if (SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_TEMP_EDIT") || SOURCE_LOCAL.equals("ACHAT2_EDIT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_EDIT")) {
            if (SOURCE_LOCAL.equals("BON2_EDIT")) {
                val_stock_avant = arrived_bon2.stock_produit + arrived_bon2.qte + arrived_bon2.gratuit;
            } else if (SOURCE_LOCAL.equals("ACHAT2_EDIT")) {
                val_stock_avant = arrived_bon2.stock_produit - arrived_bon2.qte - arrived_bon2.gratuit;
            }

            if (val_colissage == 0) {
                val_stock_avant_colis = 0.0;
            } else {
                val_stock_avant_colis = (int) (val_stock_avant / val_colissage);
            }

            val_qte = arrived_bon2.qte;
            val_qte_old = arrived_bon2.qte;

            val_nbr_colis = arrived_bon2.nbr_colis;
            val_gratuit = arrived_bon2.gratuit;
            val_gratuit_old = arrived_bon2.gratuit;

            if (val_qte == 0) {
                edt_qte.setText("");
            } else {
                edt_qte.setText(nq.format(val_qte));
            }

            if (val_nbr_colis == 0) {
                edt_nbr_colis.setText("");
            } else {
                edt_nbr_colis.setText(nq.format(val_nbr_colis));
            }

            if (val_gratuit == 0) {
                edt_gratuit.setText("");
            } else {
                edt_gratuit.setText(nq.format(val_gratuit));
            }
            edt_stock_apres.setText(nq.format(arrived_bon2.stock_produit));
            if (val_colissage == 0) {
                val_stock_apres_colis = 0.0;
            } else {
                val_stock_apres_colis = (int) (arrived_bon2.stock_produit / val_colissage);
            }
            edt_stock_apres_colis.setText(nq.format(val_stock_apres_colis));
        }


        if (val_colissage == 0) {
            edt_colissage.setText("");
            edt_qte.requestFocus();
        } else {
            edt_colissage.setText(nq.format(val_colissage));
            edt_nbr_colis.requestFocus();
        }


        txv_produit.setText(arrived_bon2.produit);
        edt_stock_avant.setText(nq.format(val_stock_avant));
        edt_stock_avant_colis.setText(nq.format(val_stock_avant_colis));


        edt_prix_ht.setText(nf.format(val_prix_ht));
        edt_tva.setText(nq.format(val_tva));
        edt_prix_ttc.setText(nf.format(val_prix_ttc));

        // pour barré un text
        //edt_prix_ttc.setPaintFlags(edt_prix_ttc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // onMontantRemiseChange();

        btn_valider.setOnClickListener(v -> {

            if (SOURCE_LOCAL.equals("BON2_INSERT") || SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT") || SOURCE_LOCAL.equals("BON2_TEMP_EDIT")) {
                if (!(prefs.getBoolean("STOCK_MOINS", false))) {
                    if (val_qte > val_stock_avant) {
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Attention!")
                                .setContentText("Stock produit insuffisant, insertion impossible! (Pour la désactiver Allez dans Paramètres -> Paramètres divers -> Cochez (Vente avec stock moins)")
                                .show();

                        return;
                    }
                }
            }

            boolean hasError = false;

            if(val_prix_ttc < arrived_bon2.pv_limite){
                txv_message.setText("Produit avec un prix limit : " + nf.format(arrived_bon2.pv_limite));
                val_prix_ttc = arrived_bon2.pv_limite;
                edt_prix_ttc.setText(nf.format(val_prix_ttc));
                hasError = true;
            }

            if (edt_qte.getText().length() <= 0) {

                edt_qte.setError("Quantité obligatoire!!");
                hasError = true;
            }

            if (edt_prix_ht.getText().length() <= 0 && edt_prix_ht.getVisibility() == View.VISIBLE) {
                edt_prix_ht.setError("Prix HT obligatoire!!");
                hasError = true;
            }

            if (edt_prix_ttc.getText().length() <= 0 && edt_prix_ttc.getVisibility() == View.VISIBLE) {
                edt_prix_ttc.setError("Prix TTC obligatoire!!");
                hasError = true;
            }

            if (!hasError) {

                arrived_bon2.nbr_colis = val_nbr_colis;
                arrived_bon2.colissage = val_colissage;
                arrived_bon2.qte = val_qte;
                arrived_bon2.gratuit = val_gratuit;
                arrived_bon2.pv_ht = val_prix_ht;

                arrived_bon2.tva = val_tva;

                CheckedPanierEventBon2 item_panier = new CheckedPanierEventBon2(arrived_bon2, val_qte_old, val_gratuit_old, if_bon2_exist);
                bus.post(item_panier);

                dialog.dismiss();
            }

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        edt_nbr_colis.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_qte.isFocused()) {
                    try {
                        onNbrColisChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edt_colissage.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_qte.isFocused()) {
                    try {
                        onColissageChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edt_qte.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_nbr_colis.isFocused() && !edt_colissage.isFocused()) {
                    try {
                        onQteChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edt_gratuit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    onGratuitChange();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        edt_prix_ht.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_prix_ttc.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrixHtChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edt_tva.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_prix_ht.isFocused()) {
                    try {
                        onPrixHtChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        edt_prix_ttc.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (!edt_prix_ht.isFocused() && !edt_tva.isFocused()) {
                    try {
                        onPrixTtcChange();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    void onNbrColisChange() {

        if (edt_nbr_colis.getText().toString().isEmpty()) {
            val_nbr_colis = 0.00;
        } else {
            val_nbr_colis = Double.parseDouble(edt_nbr_colis.getText().toString());
        }
        if (edt_gratuit.getText().toString().isEmpty()) {
            val_gratuit = 0.00;
        } else {
            val_gratuit = Double.parseDouble(edt_gratuit.getText().toString());
        }

        if (val_colissage == 0 || val_nbr_colis == 0) {
            val_qte = 0.0;
            edt_qte.setText("");

        } else {
            val_qte = val_nbr_colis * val_colissage;
            edt_qte.setText(nq.format(val_qte));
        }

        check_promo(arrived_bon2);

        if (SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_INSERT") || SOURCE_LOCAL.equals("BON2_TEMP_EDIT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant - val_qte - val_gratuit;
            if (val_colissage == 0) {
                val_stock_apres_colis = 0.0;
            } else {
                val_stock_apres_colis = (int) (val_stock_apres / val_colissage);
            }
        } else if (SOURCE_LOCAL.equals("ACHAT2_EDIT") || SOURCE_LOCAL.equals("ACHAT2_INSERT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_EDIT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant + val_qte + val_gratuit;
            if (val_colissage == 0) {
                val_stock_apres_colis = 0.0;
            } else {
                val_stock_apres_colis = (int) (val_stock_apres / val_colissage);
            }
        }

        edt_stock_apres.setText(nq.format(val_stock_apres));
        edt_stock_apres_colis.setText(nq.format(val_stock_apres_colis));

    }

    void onColissageChange() {
        if (edt_colissage.getText().toString().isEmpty()) {
            val_colissage = 0.00;
        } else {
            val_colissage = Double.parseDouble(edt_colissage.getText().toString());
        }

        if (edt_gratuit.getText().toString().isEmpty()) {
            val_gratuit = 0.00;
        } else {
            val_gratuit = Double.parseDouble(edt_gratuit.getText().toString());
        }

        if (val_colissage == 0 || val_nbr_colis == 0) {
            val_qte = 0.0;
            edt_qte.setText("");

        } else {
            val_qte = val_nbr_colis * val_colissage;
            edt_qte.setText(nq.format(val_qte));
        }

        check_promo(arrived_bon2);

        if (SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_INSERT") || SOURCE_LOCAL.equals("BON2_TEMP_EDIT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant - val_qte - val_gratuit;
            if (val_colissage == 0.0) {
                val_stock_avant_colis = 0.0;
                val_stock_apres_colis = 0.0;
                stockavantLayout_colis.setVisibility(View.GONE);
                stockapresLayout_colis.setVisibility(View.GONE);
            } else {
                val_stock_avant_colis = (int) (val_stock_avant / val_colissage);
                val_stock_apres_colis = (int) (val_stock_apres / val_colissage);
                stockavantLayout_colis.setVisibility(View.VISIBLE);
                stockapresLayout_colis.setVisibility(View.VISIBLE);
            }
        } else if (SOURCE_LOCAL.equals("ACHAT2_EDIT") || SOURCE_LOCAL.equals("ACHAT2_INSERT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_EDIT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant + val_qte + val_gratuit;
            if (val_colissage == 0.0) {
                val_stock_avant_colis = 0.0;
                val_stock_apres_colis = 0.0;
                stockavantLayout_colis.setVisibility(View.GONE);
                stockapresLayout_colis.setVisibility(View.GONE);
            } else {
                val_stock_avant_colis = (int) (val_stock_avant / val_colissage);
                val_stock_apres_colis = (int) (val_stock_apres / val_colissage);
                stockavantLayout_colis.setVisibility(View.VISIBLE);
                stockapresLayout_colis.setVisibility(View.VISIBLE);
            }
        }


        edt_stock_apres.setText(nq.format(val_stock_apres));
        edt_stock_avant_colis.setText(nq.format(val_stock_avant_colis));
        edt_stock_apres_colis.setText(nq.format(val_stock_apres_colis));

    }

    void onQteChange() {

        if (edt_qte.getText().toString().isEmpty()) {
            val_qte = 0.00;
        } else {
            val_qte = Double.parseDouble(edt_qte.getText().toString());
        }


        if (edt_gratuit.getText().toString().isEmpty()) {
            val_gratuit = 0.00;
        } else {
            val_gratuit = Double.parseDouble(edt_gratuit.getText().toString());
        }

        val_nbr_colis = 0.0;
        edt_nbr_colis.setText("");
        val_colissage = 0.0;
        edt_colissage.setText("");
        val_stock_avant_colis = 0.0;
        val_stock_apres_colis = 0.0;
        edt_stock_avant_colis.setText("");
        edt_stock_apres_colis.setText("");

        check_promo(arrived_bon2);

        if (SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_INSERT")|| SOURCE_LOCAL.equals("BON2_TEMP_EDIT") || SOURCE_LOCAL.equals("BON2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant - val_qte - val_gratuit;
        } else if (SOURCE_LOCAL.equals("ACHAT2_EDIT") || SOURCE_LOCAL.equals("ACHAT2_INSERT")|| SOURCE_LOCAL.equals("ACHAT2_TEMP_EDIT") || SOURCE_LOCAL.equals("ACHAT2_TEMP_INSERT")) {
            val_stock_apres = val_stock_avant + val_qte + val_gratuit;
        }

        edt_stock_apres.setText(nq.format(val_stock_apres));

    }

    void onGratuitChange() {

        if (edt_qte.getText().toString().isEmpty()) {
            val_qte = 0.00;
        } else {
            val_qte = Double.parseDouble(edt_qte.getText().toString());
        }

        if (edt_gratuit.getText().toString().isEmpty()) {
            val_gratuit = 0.00;
        } else {
            val_gratuit = Double.parseDouble(edt_gratuit.getText().toString());
        }

        if (SOURCE_LOCAL.equals("BON2_EDIT") || SOURCE_LOCAL.equals("BON2_INSERT")) {
            val_stock_apres = val_stock_avant - val_qte - val_gratuit;
        } else if (SOURCE_LOCAL.equals("ACHAT2_EDIT") || SOURCE_LOCAL.equals("ACHAT2_INSERT")) {
            val_stock_apres = val_stock_avant + val_qte + val_gratuit;
        }

        edt_stock_apres.setText(nq.format(val_stock_apres));
    }

    void onPrixHtChange() {
        if (isUpdating) return;

        isUpdating = true;

        try {

            if (edt_tva.getText().toString().isEmpty()) {
                val_tva = 0.00;
            } else {
                val_tva = Double.parseDouble(edt_tva.getText().toString());
            }

            if (edt_prix_ht.getText().toString().isEmpty()) {
                val_prix_ht = 0.00;
            } else {
                val_prix_ht = Double.parseDouble(edt_prix_ht.getText().toString());
            }
            val_prix_ttc = val_prix_ht * (1 + (val_tva / 100));
            edt_prix_ttc.setText(nf.format(val_prix_ttc));

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            isUpdating = false;
        }

    }


    void onPrixTtcChange() {

        if (isUpdating) return;

        isUpdating = true;

        try {

            if (edt_tva.getText().toString().isEmpty()) {
                val_tva = 0.00;
            } else {
                val_tva = Double.parseDouble(edt_tva.getText().toString());
            }

            if (edt_prix_ttc.getText().toString().isEmpty()) {
                val_prix_ttc = 0.00;
            } else {
                val_prix_ttc = Double.parseDouble(edt_prix_ttc.getText().toString());
            }
            val_prix_ht = val_prix_ttc / (1 + (val_tva / 100));
            edt_prix_ht.setText(nf.format(val_prix_ht));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isUpdating = false;
        }

    }


    private void startBlinkanimation(View view) {
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(100); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        view.startAnimation(anim);
    }


    private void check_promo(PostData_Bon2 bon2){

        try {

            if (bon2.promo == 1 && (val_qte >= bon2.qte_promo)) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date d1 = format.parse(bon2.d1);
                Date d2 = format.parse(bon2.d2);
                Date today = Calendar.getInstance().getTime();
                assert d1 != null;
                assert d2 != null;
                if (d1.getTime() == d2.getTime()) {
                    if (d1.before(today)) {
                        val_prix_ht = arrived_bon2.pp1_ht;
                        txv_promotion.setText("Produit en promotion");
                        startBlinkanimation(txv_promotion);
                    } else {
                        val_prix_ht = arrived_bon2.pv_ht;
                        txv_promotion.setText("");
                    }
                } else if (d1.getTime() <= today.getTime() && today.getTime() <= d2.getTime()) {
                    val_prix_ht = arrived_bon2.pp1_ht;
                    txv_promotion.setText("Produit en promotion");
                    startBlinkanimation(txv_promotion);
                } else {
                    val_prix_ht = arrived_bon2.pv_ht;
                    txv_promotion.setText("");
                }

            } else {
                val_prix_ht = arrived_bon2.pv_ht;
                txv_promotion.setText("");
            }

            edt_prix_ht.setText(nf.format(val_prix_ht));


        }catch (Exception e ){

        }


    }
}

abstract class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }
}
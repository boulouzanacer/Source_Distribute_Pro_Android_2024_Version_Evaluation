package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.CheckedPanierEventInventaire2;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class FragmentQteInventaire {

    private DATABASE controller;
    MaterialFancyButton btn_valider, btn_cancel;
    TextView txv_produit, txv_message;
    LinearLayout part_3_qteinv_Layout;
    TextInputLayout stockavantLayout_colis, ecartLayout_colis, qteLayout, vracLayout;
    TextInputEditText  edt_colissage, edt_nbr_colis, edt_qte_physique, edt_vrac, edt_stock_avant, edt_stock_avant_colis, edt_ecart_qte, edt_ecart_colis;
    Double val_nbr_colis, val_colissage, val_qte_physique, val_vrac,  val_stock_avant, val_stock_avant_colis, val_ecart, val_ecart_colis;

    private Context mContext;

    EventBus bus = EventBus.getDefault();

    Activity activity;
    AlertDialog dialog;
    NumberFormat nf,nq;

    PostData_Inv2 arrived_inv2;

    private final String PREFS = "ALL_PREFS";
    String SOURCE_LOCAL;
    boolean if_inv2_exist = false;

    //PopupWindow display method

    public void showDialogbox(String SOURCE, Activity activity, Context context, PostData_Inv2 inv2) {

        mContext = context;
        this.activity = activity;
        arrived_inv2 = inv2;
        this.SOURCE_LOCAL = SOURCE;
        this.controller = new DATABASE(mContext);

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        nq = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nq).applyPattern("####0.##");


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_qte_inventaire, null);
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


        btn_valider = (MaterialFancyButton) dialogview.findViewById(R.id.btn_remise);
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_valider.setTextSize(15);
        btn_valider.setIconPosition(POSITION_LEFT);
        btn_valider.setFontIconSize(30);

        btn_cancel = (MaterialFancyButton) dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);


        stockavantLayout_colis = dialogview.findViewById(R.id.stockavantLayout_colis);
        ecartLayout_colis = dialogview.findViewById(R.id.ecartLayout_colis);
        vracLayout = dialogview.findViewById(R.id.vracLayout);
        qteLayout = dialogview.findViewById(R.id.qteLayout);

        txv_produit = dialogview.findViewById(R.id.produit_title);
        txv_message = dialogview.findViewById(R.id.message_title);
        edt_stock_avant = dialogview.findViewById(R.id.stockavant);
        edt_stock_avant_colis = dialogview.findViewById(R.id.stockavant_colis);
        edt_nbr_colis = dialogview.findViewById(R.id.nbrColis);
        edt_colissage = dialogview.findViewById(R.id.colissage);
        edt_qte_physique = dialogview.findViewById(R.id.qte_theorique);
        edt_vrac = dialogview.findViewById(R.id.vrac);

        edt_ecart_qte = dialogview.findViewById(R.id.ecart);
        edt_ecart_colis = dialogview.findViewById(R.id.ecart_colis);

        part_3_qteinv_Layout = dialogview.findViewById(R.id.part_3_qteinv_Layout);




        //********************************************************************
        if (SOURCE_LOCAL.equals("INV2_INSERT")){
            PostData_Inv2 checked_inv2 = new PostData_Inv2();
            String querry = "SELECT " +
                    "INV2.RECORDID, " +
                    "INV2.CODE_BARRE, " +
                    "INV2.NUM_INV, " +
                    "INV2.PRODUIT, " +
                    "INV2.NBRE_COLIS, " +
                    "INV2.COLISSAGE, " +
                    "INV2.PA_HT, " +
                    "INV2.QTE, " +
                    "INV2.QTE_TMP, " +
                    "INV2.QTE_NEW, " +
                    "INV2.TVA, " +
                    "INV2.VRAC, " +
                    "INV2.CODE_DEPOT " +
                    "FROM INV2 " +
                    "WHERE INV2.NUM_INV = '" + arrived_inv2.num_inv + "' AND INV2.CODE_BARRE = '" + arrived_inv2.codebarre + "'";
            checked_inv2 = controller.check_if_inv2_exist(querry);
            SOURCE_LOCAL = "BON2_EDIT";

            if(checked_inv2 != null){
                txv_message.setText("Produit déja inseré avec une quantité : " + checked_inv2.qte_physique);
                if_inv2_exist = true;
                arrived_inv2 = checked_inv2;
            }

        }

        val_stock_avant = arrived_inv2.qte_theorique;
        val_colissage = arrived_inv2.colissage;

        if(val_colissage==0){
            val_stock_avant_colis = 0.0;
        }else {
            val_stock_avant_colis = (double) (int) (val_stock_avant / val_colissage);
        }

        if(val_colissage == 0.0){

            edt_colissage.setText("");
            edt_qte_physique.requestFocus();
            stockavantLayout_colis.setVisibility(View.GONE);
            ecartLayout_colis.setVisibility(View.GONE);
            //part_3_qteinv_Layout.setWeightSum(3);
            //vracLayout.setVisibility(View.GONE);

        }else{
            edt_colissage.setText(nq.format(val_colissage));
            edt_nbr_colis.requestFocus();
            stockavantLayout_colis.setVisibility(View.VISIBLE);
            ecartLayout_colis.setVisibility(View.VISIBLE);
            //vracLayout.setVisibility(View.VISIBLE);
            //part_3_qteinv_Layout.setWeightSum(4);
        }

        if (SOURCE.equals("INV2_INSERT")){
            edt_ecart_qte.setText(nq.format(val_stock_avant));
            edt_ecart_colis.setText(nq.format(val_stock_avant_colis));
            val_nbr_colis = 0.0;
            val_qte_physique = 0.0;
            val_vrac = 0.0;

        }else if(SOURCE.equals("INV2_EDIT")){
            val_stock_avant = arrived_inv2.qte_theorique;
            if(val_colissage==0){
                val_stock_avant_colis = 0.0;
            }else {
                val_stock_avant_colis = (double) (int) (val_stock_avant / val_colissage);
            }

            val_vrac = arrived_inv2.vrac;
            val_qte_physique = arrived_inv2.qte_physique - val_vrac;
            val_nbr_colis = arrived_inv2.nbr_colis;


            if (val_qte_physique == 0) {
                edt_qte_physique.setText("");
            } else {
                edt_qte_physique.setText(nq.format(val_qte_physique));
            }

            if (val_nbr_colis == 0) {
                edt_nbr_colis.setText("");
            } else {
                edt_nbr_colis.setText(nq.format(val_nbr_colis));
            }

            if (val_vrac == 0) {
                edt_vrac.setText("");
            } else {
                edt_vrac.setText(nq.format(val_vrac));
            }

            val_ecart = val_stock_avant - val_qte_physique - val_vrac;
            edt_ecart_qte.setText(nq.format(val_ecart));

            if(val_colissage==0){
                val_ecart_colis = 0.0;
            }else {
                val_ecart_colis = (double) (int) (val_ecart / val_colissage);
            }
            edt_ecart_colis.setText(nq.format(val_ecart_colis));

        }


        txv_produit.setText(arrived_inv2.produit);
        edt_stock_avant.setText(nq.format(val_stock_avant));
        edt_stock_avant_colis.setText(nq.format(val_stock_avant_colis));

        // onMontantRemiseChange();

        btn_valider.setOnClickListener(v -> {

            boolean hasError = false;

            if (Objects.requireNonNull(edt_qte_physique.getText()).length() <= 0 ) {

                edt_qte_physique.setError("Quantité obligatoire!!");
                hasError = true;
            }

            if(!hasError){

                arrived_inv2.nbr_colis = val_nbr_colis;
                arrived_inv2.colissage = val_colissage;
                arrived_inv2.qte_physique = val_qte_physique;
                arrived_inv2.vrac = val_vrac;

                CheckedPanierEventInventaire2 item_panier = new CheckedPanierEventInventaire2(arrived_inv2, val_qte_physique, val_vrac, if_inv2_exist);

                bus.post(item_panier);

                dialog.dismiss();
            }

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        edt_nbr_colis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!edt_qte_physique.isFocused()){
                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),
                        onNbrColisChange();

                    }catch (Exception e){
                       // montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                       // taux_remise.getEditText().getText().clear();
                    }
                }

            }
        });

        edt_colissage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!edt_qte_physique.isFocused()){
                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),
                        onColissageChange();


                    }catch (Exception e){
                        // montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                        // taux_remise.getEditText().getText().clear();
                    }
                }

            }
        });

        edt_qte_physique.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!edt_nbr_colis.isFocused() && !edt_colissage.isFocused()){
                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),
                        onQteChange();


                    }catch (Exception e){
                        // montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                        // taux_remise.getEditText().getText().clear();
                    }
                }

            }
        });

        edt_vrac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),
                        onVracChange();


                    }catch (Exception e){
                        // montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                        // taux_remise.getEditText().getText().clear();
                    }


            }
        });

    }

    void onNbrColisChange(){
        if(edt_nbr_colis.getText().toString().isEmpty()){
            val_nbr_colis = 0.00;
        }else {
            val_nbr_colis = Double.parseDouble(edt_nbr_colis.getText().toString());
        }
        if(edt_vrac.getText().toString().isEmpty()){
            val_vrac = 0.00;
        }else {
            val_vrac = Double.parseDouble(edt_vrac.getText().toString());
        }

        if(val_colissage == 0 || val_nbr_colis == 0){
            val_qte_physique = 0.0;
            edt_qte_physique.setText("");

        }else {
            val_qte_physique = val_nbr_colis * val_colissage;
            edt_qte_physique.setText(nq.format(val_qte_physique));
        }
        val_ecart = val_stock_avant - val_qte_physique - val_vrac;
        if(val_colissage ==0){
            val_ecart_colis = 0.0;
        }else {
            val_ecart_colis = (double) (int) (val_ecart / val_colissage);
        }


        edt_ecart_qte.setText(nq.format(val_ecart));
        edt_ecart_colis.setText(nq.format(val_ecart_colis));
    }


    void onColissageChange(){
        if(edt_colissage.getText().toString().isEmpty()){
            val_colissage = 0.00;
        }else {
            val_colissage = Double.parseDouble(edt_colissage.getText().toString());
        }

        if(edt_vrac.getText().toString().isEmpty()){
            val_vrac= 0.00;
        }else {
            val_vrac = Double.parseDouble(edt_vrac.getText().toString());
        }

        if(val_colissage == 0 || val_nbr_colis == 0){
            val_qte_physique = 0.0;
            edt_qte_physique.setText("");

        }else {
            val_qte_physique = val_nbr_colis * val_colissage;
            edt_qte_physique.setText(nq.format(val_qte_physique));
        }

        val_ecart = val_stock_avant - val_qte_physique - val_vrac;
        if(val_colissage == 0.0){
            val_stock_avant_colis = 0.0;
            val_ecart_colis = 0.0;
            stockavantLayout_colis.setVisibility(View.GONE);
            ecartLayout_colis.setVisibility(View.GONE);
        }else{
            val_stock_avant_colis = (double) (int) (val_stock_avant / val_colissage);
            val_ecart_colis = (double) (int)  (val_ecart / val_colissage);
            stockavantLayout_colis.setVisibility(View.VISIBLE);
            ecartLayout_colis.setVisibility(View.VISIBLE);
        }

        edt_ecart_qte.setText(nq.format(val_ecart));
        edt_stock_avant_colis.setText(nq.format(val_stock_avant_colis));
        edt_ecart_colis.setText(nq.format(val_ecart_colis));
    }


    void onQteChange(){
        if(edt_qte_physique.getText().toString().isEmpty()){
            val_qte_physique = 0.00;
        }else {
            val_qte_physique = Double.parseDouble(edt_qte_physique.getText().toString());
        }

        if(edt_vrac.getText().toString().isEmpty()){
            val_vrac = 0.00;
        }else {
            val_vrac = Double.parseDouble(edt_vrac.getText().toString());
        }

        val_nbr_colis = 0.0;
        edt_nbr_colis.setText("");
        val_colissage = 0.0;
        edt_colissage.setText("");
        val_stock_avant_colis = 0.0;
        val_ecart_colis = 0.0;
        edt_stock_avant_colis.setText("");
        edt_ecart_colis.setText("");

        val_ecart = val_stock_avant - val_qte_physique - val_vrac;
        edt_ecart_qte.setText(nq.format(val_ecart));
    }


    void onVracChange(){
        if(edt_qte_physique.getText().toString().isEmpty()){
            val_qte_physique = 0.00;
        }else {
            val_qte_physique = Double.parseDouble(edt_qte_physique.getText().toString());
        }

        if(edt_vrac.getText().toString().isEmpty()){
            val_vrac= 0.00;
        }else {
            val_vrac = Double.parseDouble(edt_vrac.getText().toString());
        }

        val_ecart = val_stock_avant - val_qte_physique - val_vrac;
        if(val_colissage ==0){
            val_ecart_colis = 0.0;
        }else {
            val_ecart_colis = (double) (int) (val_ecart / val_colissage);
        }

        edt_ecart_qte.setText(nq.format(val_ecart));
        edt_ecart_colis.setText(nq.format(val_ecart_colis));
    }
}
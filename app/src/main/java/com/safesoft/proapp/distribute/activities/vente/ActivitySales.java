package com.safesoft.proapp.distribute.activities.vente;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.R;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivitySales extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick, RecyclerAdapterBon1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterBon1 adapter;
    ArrayList<PostData_Bon1> bon1s;
    ArrayList<PostData_Bon2> final_panier;
    DATABASE controller;

    private final String PREFS = "ALL_PREFS";
    Boolean printer_mode_integrate = true;
    private String SOURCE_EXPORT = "";
    SharedPreferences prefs;
    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de livraison");
        getSupportActionBar().setSubtitle("Client");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        controller = new DATABASE(this);

        if(getIntent() != null){
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        }

        initViews();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.remove("FILTRE_SEARCH_VALUE");
        editor.apply();

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_vente);
    }

    @Override
    protected void onStart() {

        setRecycle();

        SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
        printer_mode_integrate = Objects.equals(prefs1.getString("PRINTER", "INTEGRATE"), "INTEGRATE");

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterBon1(this, getItems(), "SALE");
        recyclerView.setAdapter(adapter);
    }


    public ArrayList<PostData_Bon1> getItems() {
        bon1s = new ArrayList<>();

        String querry = "SELECT " +
                "BON1.RECORDID, " +
                "BON1.NUM_BON, " +
                "BON1.DATE_BON, " +
                "BON1.HEURE, " +
                "BON1.DATE_F, " +
                "BON1.HEURE_F, " +
                "BON1.MODE_RG, " +
                "BON1.MODE_TARIF, " +

                "BON1.NBR_P, " +
                "BON1.TOT_QTE, " +

                "BON1.TOT_HT, " +
                "BON1.TOT_TVA, " +
                "BON1.TIMBRE, " +
                "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE AS TOT_TTC, " +
                "BON1.REMISE, " +
                "BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE AS MONTANT_BON, " +

                "BON1.ANCIEN_SOLDE, " +
                "BON1.VERSER, " +
                "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                "BON1.CODE_CLIENT, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.TEL, " +
                "CLIENT.RC, " +
                "CLIENT.IFISCAL, " +
                "CLIENT.AI, " +
                "CLIENT.NIS, " +

                "CLIENT.LATITUDE as LATITUDE_CLIENT, " +
                "CLIENT.LONGITUDE as LONGITUDE_CLIENT, " +

                "CLIENT.SOLDE AS SOLDE_CLIENT, " +
                "CLIENT.CREDIT_LIMIT, " +

                "BON1.LATITUDE, " +
                "BON1.LONGITUDE, " +

                "BON1.CODE_DEPOT, " +
                "BON1.CODE_VENDEUR, " +
                "BON1.EXPORTATION, " +
                "BON1.BLOCAGE " +
                "FROM BON1 " +
                "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT";


        if(!SOURCE_EXPORT.equals("EXPORTED")){
            querry = querry + " WHERE IS_EXPORTED = 0 ORDER BY BON1.NUM_BON ";
        }else {
            querry = querry + " WHERE IS_EXPORTED = 1 ORDER BY BON1.NUM_BON ";
        }

        bon1s = controller.select_all_bon1_from_database(querry);

        return bon1s;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivitySales.this, ActivitySale.class);
        editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_SALE");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onLongClick(View v, final int position) {

        if (bon1s.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Marque retour", "Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0:
                        if(!SOURCE_EXPORT.equals("EXPORTED")){
                            new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Bon de vente")
                                    .setContentText("Voulez-vous vraiment tranferer vers un bon de retour ?!")
                                    .setCancelText("Non")
                                    .setConfirmText("Bon retour")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        Sound(R.raw.beep);

                                        /*Intent editIntent = new Intent(ActivitySales.this, ActivitySale.class);
                                        editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
                                        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_SALE");
                                        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                                        startActivity(editIntent);*/

                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        sDialog.dismiss();
                                    })
                                    .show();
                        }else{
                            new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon est déja exporté")
                                    .show();
                        }

                        break;
                    case 1:
                        /*new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Suppression")
                                .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
                                .setCancelText("Anuuler")
                                .setConfirmText("Supprimer")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    controller.delete_bon_vente(false, bon1s.get(position));
                                    setRecycle();

                                    sDialog.dismiss();

                                })
                                .show();*/

                        break;
                    case 2:

                        if(!bon1s.get(position).blocage.equals("F")){
                            new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }
                        Activity bactivity;
                        bactivity = ActivitySales.this;

                        final_panier =  controller.select_bon2_from_database("" +
                                "SELECT " +
                                "BON2.RECORDID, " +
                                "BON2.CODE_BARRE, " +
                                "BON2.NUM_BON, " +
                                "BON2.PRODUIT, " +
                                "BON2.NBRE_COLIS, " +
                                "BON2.COLISSAGE, " +
                                "BON2.QTE, " +
                                "BON2.QTE_GRAT, " +
                                "BON2.PU, " +
                                "BON2.TVA, " +
                                "BON2.CODE_DEPOT, " +
                                "BON2.DESTOCK_TYPE, " +
                                "BON2.DESTOCK_CODE_BARRE, " +
                                "BON2.DESTOCK_QTE, " +
                                "PRODUIT.ISNEW, " +
                                "PRODUIT.STOCK " +
                                "FROM BON2 " +
                                "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                "WHERE BON2.NUM_BON = '" + bon1s.get(position).num_bon + "'" );
                        Printing printer = new Printing();
                        try {
                            printer.start_print_bon(bactivity, "VENTE", final_panier, bon1s.get(position), null);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            });
            builder.show();
        } else {
            final CharSequence[] items = {"Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                if (item == 0) {
                    new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                controller.delete_bon_en_attente(false, bon1s.get(position).num_bon);
                                setRecycle();

                                sDialog.dismiss();
                            })
                            .show();
                }
            });
            builder.show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!SOURCE_EXPORT.equals("EXPORTED")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_sales_client, menu);
        }
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.new_sale) {
            Intent editIntent = new Intent(ActivitySales.this, ActivitySale.class);
            editIntent.putExtra("TYPE_ACTIVITY", "NEW_SALE");
            editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
            startActivity(editIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

}

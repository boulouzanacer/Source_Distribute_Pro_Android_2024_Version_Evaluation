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
import com.safesoft.proapp.distribute.printing.PrinterVente;
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

    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de livraison");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        controller = new DATABASE(this);

        initViews();


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
                "Bon1.RECORDID, " +
                "Bon1.NUM_BON, " +
                "Bon1.DATE_BON, " +
                "Bon1.HEURE, " +
                "Bon1.MODE_RG, " +
                "Bon1.MODE_TARIF, " +

                "Bon1.NBR_P, " +
                "Bon1.TOT_QTE, " +

                "Bon1.TOT_HT, " +
                "Bon1.TOT_TVA, " +
                "Bon1.TIMBRE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE AS TOT_TTC, " +
                "Bon1.REMISE, " +
                "Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE AS MONTANT_BON, " +

                "Bon1.ANCIEN_SOLDE, " +
                "Bon1.VERSER, " +
                "Bon1.ANCIEN_SOLDE + (Bon1.TOT_HT + Bon1.TOT_TVA + Bon1.TIMBRE - Bon1.REMISE) - Bon1.VERSER AS RESTE, " +

                "Bon1.CODE_CLIENT, " +
                "Client.CLIENT, " +
                "Client.ADRESSE, " +
                "Client.TEL, " +
                "Client.RC, " +
                "Client.IFISCAL, " +
                "Client.AI, " +
                "Client.NIS, " +

                "Client.LATITUDE as LATITUDE_CLIENT, " +
                "Client.LONGITUDE as LONGITUDE_CLIENT, " +

                "Client.SOLDE AS SOLDE_CLIENT, " +
                "Client.CREDIT_LIMIT, " +

                "Bon1.LATITUDE, " +
                "Bon1.LONGITUDE, " +

                "Bon1.CODE_DEPOT, " +
                "Bon1.CODE_VENDEUR, " +
                "Bon1.EXPORTATION, " +
                "Bon1.BLOCAGE " +
                "FROM Bon1 " +
                "LEFT JOIN Client ON Bon1.CODE_CLIENT = Client.CODE_CLIENT " +
                "WHERE IS_EXPORTED = 0 ORDER BY Bon1.NUM_BON";

        // querry = "SELECT * FROM Events";
        bon1s = controller.select_vente_from_database(querry);

        return bon1s;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivitySales.this, ActivitySale.class);
        editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_SALE");
        startActivity(editIntent);

    }


    @Override
    public void onLongClick(View v, final int position) {

        if (bon1s.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Modifier", "Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0:
                        new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Bon de vente")
                                .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                                .setCancelText("Non")
                                .setConfirmText("Modifier")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    Sound(R.raw.beep);

                                    Intent editIntent = new Intent(ActivitySales.this, ActivitySale.class);
                                    editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
                                    editIntent.putExtra("TYPE_ACTIVITY", "EDIT_SALE");
                                    startActivity(editIntent);

                                    sDialog.dismiss();
                                })
                                .show();

                        break;
                    case 1:
                        new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.NORMAL_TYPE)
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
                                .show();

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
                                "Bon2.RECORDID, " +
                                "Bon2.CODE_BARRE, " +
                                "Bon2.NUM_BON, " +
                                "Bon2.PRODUIT, " +
                                "Bon2.NBRE_COLIS, " +
                                "Bon2.COLISSAGE, " +
                                "Bon2.QTE, " +
                                "Bon2.QTE_GRAT, " +
                                "Bon2.PV_HT, " +
                                "Bon2.TVA, " +
                                "Bon2.CODE_DEPOT, " +
                                "Bon2.PA_HT, " +
                                "Bon2.DESTOCK_TYPE, " +
                                "Bon2.DESTOCK_CODE_BARRE, " +
                                "Bon2.DESTOCK_QTE, " +
                                "Produit.STOCK " +
                                "FROM Bon2 " +
                                "LEFT JOIN Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
                                "WHERE Bon2.NUM_BON = '" + bon1s.get(position).num_bon + "'" );
                        PrinterVente printer = new PrinterVente();
                        try {
                            printer.start_print_sale_bon(bactivity, final_panier, bon1s.get(position));
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
                switch (item) {
                    case 0:
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

                        break;
                }
            });
            builder.show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ventes, menu);

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
            startActivity(editIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

}

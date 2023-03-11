package com.safesoft.proapp.distribute.activities.commande;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.PrinterVente;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityOrders extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick, RecyclerAdapterBon1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterBon1 adapter;
    ArrayList<PostData_Bon1> bon1s_temp;
    ArrayList<PostData_Bon2> final_panier;
    DATABASE controller;

    private final String PREFS_PRINTER = "ConfigPrinter";
    Boolean printer_mode_integrate = true;

    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de commandes");
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

        SharedPreferences prefs1 = getSharedPreferences(PREFS_PRINTER, MODE_PRIVATE);
        printer_mode_integrate = Objects.equals(prefs1.getString("PRINTER", "INTEGRATE"), "INTEGRATE");

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterBon1(this, getItems(), "ORDER");
        recyclerView.setAdapter(adapter);
    }


    public ArrayList<PostData_Bon1> getItems() {
        bon1s_temp = new ArrayList<>();

        String querry = "SELECT " +
                "Bon1_temp.RECORDID, " +
                "Bon1_temp.NUM_BON, " +
                "Bon1_temp.DATE_BON, " +
                "Bon1_temp.HEURE, " +
                "Bon1_temp.MODE_RG, " +
                "Bon1_temp.MODE_TARIF, " +

                "Bon1_temp.NBR_P, " +
                "Bon1_temp.TOT_QTE, " +

                "Bon1_temp.TOT_HT, " +
                "Bon1_temp.TOT_TVA, " +
                "Bon1_temp.TIMBRE, " +
                "Bon1_temp.TOT_HT + Bon1_temp.TOT_TVA + Bon1_temp.TIMBRE AS TOT_TTC, " +
                "Bon1_temp.REMISE, " +
                "Bon1_temp.TOT_HT + Bon1_temp.TOT_TVA + Bon1_temp.TIMBRE - Bon1_temp.REMISE AS MONTANT_BON, " +

                "Bon1_temp.ANCIEN_SOLDE, " +
                "Bon1_temp.VERSER, " +
                "Bon1_temp.ANCIEN_SOLDE + (Bon1_temp.TOT_HT + Bon1_temp.TOT_TVA + Bon1_temp.TIMBRE - Bon1_temp.REMISE) - Bon1_temp.VERSER AS RESTE, " +

                "Bon1_temp.CODE_CLIENT, " +
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

                "Bon1_temp.LATITUDE, " +
                "Bon1_temp.LONGITUDE, " +

                "Bon1_temp.CODE_DEPOT, " +
                "Bon1_temp.CODE_VENDEUR, " +
                "Bon1_temp.EXPORTATION, " +
                "Bon1_temp.BLOCAGE " +
                "FROM Bon1_temp " +
                "LEFT JOIN Client ON Bon1_temp.CODE_CLIENT = Client.CODE_CLIENT " +
                "WHERE IS_EXPORTED = 0 ORDER BY Bon1_temp.NUM_BON";

        // querry = "SELECT * FROM Events";
        bon1s_temp = controller.select_vente_from_database(querry);

        return bon1s_temp;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivityOrders.this, ActivityOrder.class);
        editIntent.putExtra("NUM_BON", bon1s_temp.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER");
        startActivity(editIntent);

    }


    @Override
    public void onLongClick(View v, final int position) {

        if (bon1s_temp.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Modifier", "Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0:
                        new SweetAlertDialog(ActivityOrders.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Bon de commande")
                                .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                                .setCancelText("Non")
                                .setConfirmText("Modifier")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    Sound(R.raw.beep);

                                    Intent editIntent = new Intent(ActivityOrders.this, ActivityOrder.class);
                                    editIntent.putExtra("NUM_BON", bon1s_temp.get(position).num_bon);
                                    editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER");
                                    startActivity(editIntent);

                                    sDialog.dismiss();
                                })
                                .show();

                        break;
                    case 1:
                        new SweetAlertDialog(ActivityOrders.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Suppression")
                                .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s_temp.get(position).num_bon + " ?!")
                                .setCancelText("Anuuler")
                                .setConfirmText("Supprimer")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    controller.delete_bon_vente(true, bon1s_temp.get(position));
                                    setRecycle();

                                    sDialog.dismiss();

                                })
                                .show();

                        break;
                    case 2:

                        if(!bon1s_temp.get(position).blocage.equals("F")){
                            new SweetAlertDialog(ActivityOrders.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }
                        Activity bactivity;
                        bactivity = ActivityOrders.this;

                        final_panier =  controller.select_bon2_from_database("" +
                                "SELECT " +
                                "Bon2_temp.RECORDID, " +
                                "Bon2_temp.CODE_BARRE, " +
                                "Bon2_temp.NUM_BON, " +
                                "Bon2_temp.PRODUIT, " +
                                "Bon2_temp.NBRE_COLIS, " +
                                "Bon2_temp.COLISSAGE, " +
                                "Bon2_temp.QTE, " +
                                "Bon2_temp.QTE_GRAT, " +
                                "Bon2_temp.PV_HT, " +
                                "Bon2_temp.TVA, " +
                                "Bon2_temp.CODE_DEPOT, " +
                                "Bon2_temp.PA_HT, " +
                                "Bon2_temp.DESTOCK_TYPE, " +
                                "Bon2_temp.DESTOCK_CODE_BARRE, " +
                                "Bon2_temp.DESTOCK_QTE, " +
                                "Produit.STOCK " +
                                "FROM Bon2_temp " +
                                "LEFT JOIN Produit ON (Bon2_temp.CODE_BARRE = Produit.CODE_BARRE) " +
                                "WHERE Bon2_temp.NUM_BON = '" + bon1s_temp.get(position).num_bon + "'" );
                        PrinterVente printer = new PrinterVente();
                        try {
                            printer.start_print_order_bon(bactivity, final_panier, bon1s_temp.get(position));
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
                        new SweetAlertDialog(ActivityOrders.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Suppression")
                                .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s_temp.get(position).num_bon + " ?!")
                                .setCancelText("Anuuler")
                                .setConfirmText("Supprimer")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    controller.delete_bon_en_attente(true, bon1s_temp.get(position).num_bon);
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
            Intent editIntent = new Intent(ActivityOrders.this, ActivityOrder.class);
            editIntent.putExtra("TYPE_ACTIVITY", "NEW_ORDER");
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

package com.safesoft.proapp.distribute.activities.commande_vente;

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
import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.activities.ActivityRouting;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.Printing;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityOrdersClient extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick, RecyclerAdapterBon1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterBon1 adapter;
    ArrayList<PostData_Bon1> bon1s_temp;
    ArrayList<PostData_Bon2> final_panier;
    DATABASE controller;

    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    Boolean printer_mode_integrate = true;

    private String SOURCE_EXPORT = "";
    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de commandes");
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

        //Reset last item selected in list product
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", 0).apply();
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_vente);

    }

    @Override
    protected void onStart() {

        setRecycle();

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        printer_mode_integrate = Objects.equals(prefs.getString("PRINTER_CONX", "INTEGRATE"), "INTEGRATE");

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
                "BON1_TEMP.RECORDID, " +
                "BON1_TEMP.NUM_BON, " +
                "BON1_TEMP.DATE_BON, " +
                "BON1_TEMP.HEURE, " +
                "BON1_TEMP.DATE_F, " +
                "BON1_TEMP.HEURE_F, " +
                "BON1_TEMP.MODE_RG, " +
                "BON1_TEMP.MODE_TARIF, " +

                "BON1_TEMP.NBR_P, " +
                "BON1_TEMP.TOT_QTE, " +

                "BON1_TEMP.TOT_HT, " +
                "BON1_TEMP.TOT_TVA, " +
                "BON1_TEMP.TIMBRE, " +
                "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE AS TOT_TTC, " +
                "BON1_TEMP.REMISE, " +
                "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE AS MONTANT_BON, " +

                "BON1_TEMP.ANCIEN_SOLDE, " +
                "BON1_TEMP.VERSER, " +
                "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

                "BON1_TEMP.CODE_CLIENT, " +
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

                "BON1_TEMP.LATITUDE, " +
                "BON1_TEMP.LONGITUDE, " +

                "BON1_TEMP.CODE_DEPOT, " +
                "BON1_TEMP.CODE_VENDEUR, " +
                "BON1_TEMP.EXPORTATION, " +
                "BON1_TEMP.BLOCAGE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN CLIENT ON BON1_TEMP.CODE_CLIENT = CLIENT.CODE_CLIENT";


        if(!SOURCE_EXPORT.equals("EXPORTED")){
            querry = querry + " WHERE IS_EXPORTED = 0 ORDER BY BON1_TEMP.NUM_BON ";
        }else {
            querry = querry + " WHERE IS_EXPORTED = 1 ORDER BY BON1_TEMP.NUM_BON ";
        }

        bon1s_temp = controller.select_all_bon1_from_database(querry);

        return bon1s_temp;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivityOrdersClient.this, ActivityOrderClient.class);
        editIntent.putExtra("NUM_BON", bon1s_temp.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER_CLIENT");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    @Override
    public void onLongClick(View v, final int position) {

        if (bon1s_temp.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    /*case 0 -> {
                        if (!SOURCE_EXPORT.equals("EXPORTED")) {
                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Bon de commande")
                                    .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                                    .setCancelText("Non")
                                    .setConfirmText("Modifier")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        Sound(R.raw.beep);

                                        Intent editIntent = new Intent(ActivityOrdersClient.this, ActivityOrderClient.class);
                                        editIntent.putExtra("NUM_BON", bon1s_temp.get(position).num_bon);
                                        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER_CLIENT");
                                        startActivity(editIntent);
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        sDialog.dismiss();
                                    })
                                    .show();
                        } else {

                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon est déja exporté")
                                    .show();
                        }
                    }*/
                    case 0 ->
                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
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
                    case 1 -> {
                        if (!bon1s_temp.get(position).blocage.equals("F")) {
                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }

                        final_panier = controller.select_bon2_from_database("" +
                                "SELECT " +
                                "BON2_TEMP.RECORDID, " +
                                "BON2_TEMP.CODE_BARRE, " +
                                "BON2_TEMP.NUM_BON, " +
                                "BON2_TEMP.PRODUIT, " +
                                "BON2_TEMP.NBRE_COLIS, " +
                                "BON2_TEMP.COLISSAGE, " +
                                "BON2_TEMP.QTE, " +
                                "BON2_TEMP.QTE_GRAT, " +
                                "BON2_TEMP.PU, " +
                                "BON2_TEMP.TVA, " +
                                "BON2_TEMP.CODE_DEPOT, " +
                                "BON2_TEMP.DESTOCK_TYPE, " +
                                "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                                "BON2_TEMP.DESTOCK_QTE, " +
                                "PRODUIT.PA_HT, " +
                                "PRODUIT.PAMP, " +
                                "PRODUIT.ISNEW, " +
                                "PRODUIT.STOCK " +
                                "FROM BON2_TEMP " +
                                "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                "WHERE BON2_TEMP.NUM_BON = '" + bon1s_temp.get(position).num_bon + "'");

                        if (Objects.equals(prefs.getString("MODEL_TICKET", "LATIN"), "LATIN")) {
                            Activity bactivity;
                            bactivity = ActivityOrdersClient.this;

                            Printing printer = new Printing();

                            try {
                                printer.start_print_bon(bactivity, "ORDER", final_panier, bon1s_temp.get(position), null);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Intent html_intent = new Intent(this, ActivityHtmlView.class);
                            html_intent.putExtra("TYPE_BON" , "COMMANDE");
                            html_intent.putExtra("BON1" , bon1s_temp.get(position));
                            html_intent.putExtra("BON2" , final_panier);
                            startActivity(html_intent);
                        }

                    }
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
                    new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
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
                }
            });
            builder.show();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!SOURCE_EXPORT.equals("EXPORTED")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_orders_client, menu);
        }
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.new_sale) {
            Intent editIntent = new Intent(ActivityOrdersClient.this, ActivityOrderClient.class);
            editIntent.putExtra("TYPE_ACTIVITY", "NEW_ORDER_CLIENT");
            editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
            startActivity(editIntent);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (item.getItemId() == R.id.routing) {
            //  https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
            Intent nnn= new Intent(ActivityOrdersClient.this, ActivityRouting.class);
            startActivity(nnn);
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

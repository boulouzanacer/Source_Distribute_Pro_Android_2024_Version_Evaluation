package com.safesoft.proapp.distribute.activities.commande_achat;

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
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityOrderClient;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterAchat1;
import com.safesoft.proapp.distribute.app.BaseApplication;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.utils.Env;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityOrdersFournisseur extends AppCompatActivity implements RecyclerAdapterAchat1.ItemClick, RecyclerAdapterAchat1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterAchat1 adapter;
    ArrayList<PostData_Achat1> achat1s_com;
    ArrayList<PostData_Bon2> final_panier;
    DATABASE controller;
    SharedPreferences prefs;
    private final String PREFS = "ALL_PREFS";

    private String SOURCE_EXPORT = "";
    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de commandes");
        getSupportActionBar().setSubtitle("Fournisseur");
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

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_vente);

    }

    @Override
    protected void onStart() {

        setRecycle();

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterAchat1(this, getItems(), "ACHAT_ORDER");
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<PostData_Achat1> getItems() {
        achat1s_com = new ArrayList<>();

        String querry = "SELECT " +
                "ACHAT1_TEMP.RECORDID, " +
                "ACHAT1_TEMP.NUM_BON, " +
                "ACHAT1_TEMP.DATE_BON, " +
                "ACHAT1_TEMP.HEURE, " +
                "ACHAT1_TEMP.CODE_FRS, " +
                "ACHAT1_TEMP.EXPORTATION, " +
                "ACHAT1_TEMP.BLOCAGE, " +

                "ACHAT1_TEMP.NBR_P, " +
                "ACHAT1_TEMP.TOT_QTE, " +

                "ACHAT1_TEMP.TOT_HT, " +
                "ACHAT1_TEMP.TOT_TVA, " +
                "ACHAT1_TEMP.TIMBRE, " +
                "ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE AS TOT_TTC, " +
                "ACHAT1_TEMP.REMISE, " +
                "ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE - ACHAT1_TEMP.REMISE AS MONTANT_BON, " +

                "ACHAT1_TEMP.ANCIEN_SOLDE, " +
                "ACHAT1_TEMP.VERSER, " +
                "ACHAT1_TEMP.ANCIEN_SOLDE + (ACHAT1_TEMP.TOT_HT + ACHAT1_TEMP.TOT_TVA + ACHAT1_TEMP.TIMBRE - ACHAT1_TEMP.REMISE) - ACHAT1_TEMP.VERSER AS RESTE, " +

                "FOURNIS.FOURNIS, " +
                "FOURNIS.ADRESSE, " +
                "FOURNIS.TEL " +
                "FROM ACHAT1_TEMP " +
                "LEFT JOIN FOURNIS ON (ACHAT1_TEMP.CODE_FRS = FOURNIS.CODE_FRS)";


        if(!SOURCE_EXPORT.equals("EXPORTED")){
            querry = querry + " WHERE IS_EXPORTED = 0 ORDER BY ACHAT1_TEMP.NUM_BON ";
        }else {
            querry = querry + " WHERE IS_EXPORTED = 1 ORDER BY ACHAT1_TEMP.NUM_BON ";
        }

        achat1s_com = controller.select_all_achat1_from_database(querry);

        return achat1s_com;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivityOrdersFournisseur.this, ActivityOrderFournisseur.class);
        editIntent.putExtra("NUM_BON", achat1s_com.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER_ACHAT");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    @Override
    public void onLongClick(View v, final int position) {

        if (achat1s_com.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Modifier", "Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0:
                        if(!SOURCE_EXPORT.equals("EXPORTED")){
                            new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Bon de commande")
                                    .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                                    .setCancelText("Non")
                                    .setConfirmText("Modifier")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        Sound(R.raw.beep);

                                        Intent editIntent = new Intent(ActivityOrdersFournisseur.this, ActivityOrderClient.class);
                                        editIntent.putExtra("NUM_BON", achat1s_com.get(position).num_bon);
                                        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER_ACHAT");
                                        startActivity(editIntent);
                                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                        sDialog.dismiss();
                                    })
                                    .show();
                        }else {

                            new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon est déja exporté")
                                    .show();
                        }


                        break;
                    case 1:
                        new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText("Suppression")
                                .setContentText("Voulez-vous vraiment supprimer le bon " + achat1s_com.get(position).num_bon + " ?!")
                                .setCancelText("Anuuler")
                                .setConfirmText("Supprimer")
                                .showCancelButton(true)
                                .setCancelClickListener(Dialog::dismiss)
                                .setConfirmClickListener(sDialog -> {

                                    controller.delete_bon_achat(true, achat1s_com.get(position));
                                    setRecycle();

                                    sDialog.dismiss();

                                })
                                .show();

                        break;
                    case 2:

                        if(!achat1s_com.get(position).blocage.equals("F")){
                            new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }
                        Activity bactivity;
                        bactivity = ActivityOrdersFournisseur.this;

                        final_panier =  controller.select_bon2_from_database("" +
                                "SELECT " +
                                "BON2_TEMP.RECORDID, " +
                                "BON2_TEMP.CODE_BARRE, " +
                                "BON2_TEMP.NUM_BON, " +
                                "BON2_TEMP.PRODUIT, " +
                                "BON2_TEMP.NBRE_COLIS, " +
                                "BON2_TEMP.COLISSAGE, " +
                                "BON2_TEMP.QTE, " +
                                "BON2_TEMP.QTE_GRAT, " +
                                "BON2_TEMP.PV_HT, " +
                                "BON2_TEMP.TVA, " +
                                "BON2_TEMP.CODE_DEPOT, " +
                                "BON2_TEMP.DESTOCK_TYPE, " +
                                "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                                "BON2_TEMP.DESTOCK_QTE, " +
                                "PRODUIT.ISNEW, " +
                                "PRODUIT.STOCK " +
                                "FROM BON2_TEMP " +
                                "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                "WHERE BON2_TEMP.NUM_BON = '" + achat1s_com.get(position).num_bon + "'" );
                        Printing printer = new Printing();

                       /* try {
                            printer.start_print_order_bon(bactivity, final_panier, bon1s_temp.get(position));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }*/

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
                    new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer le bon " + achat1s_com.get(position).num_bon + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                controller.delete_bon_en_attente(true, achat1s_com.get(position).num_bon);
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
            if(!prefs.getBoolean("APP_ACTIVATED",false) && !achat1s_com.isEmpty()){
                new SweetAlertDialog(ActivityOrdersFournisseur.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Important !")
                        .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                        .show();
            }else {
                Intent editIntent = new Intent(ActivityOrdersFournisseur.this, ActivityOrderFournisseur.class);
                editIntent.putExtra("TYPE_ACTIVITY", "NEW_ORDER_ACHAT");
                editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                startActivity(editIntent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

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

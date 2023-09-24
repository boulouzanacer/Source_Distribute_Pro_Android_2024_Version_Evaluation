package com.safesoft.proapp.distribute.activities.achats;

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
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterAchat1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.Printing;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityAchats extends AppCompatActivity implements RecyclerAdapterAchat1.ItemClick, RecyclerAdapterAchat1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterAchat1 adapter;
    ArrayList<PostData_Achat1> achat1s;
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
        getSupportActionBar().setTitle("Bons d'achats");
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

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_vente);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
    }

    @Override
    protected void onStart() {

        setRecycle();

        SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
        printer_mode_integrate = Objects.equals(prefs1.getString("PRINTER_CONX", "INTEGRATE"), "INTEGRATE");

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterAchat1(this, getItems(), "ACHAT");
        recyclerView.setAdapter(adapter);
    }


    public ArrayList<PostData_Achat1> getItems() {
        achat1s = new ArrayList<>();

        String querry = "SELECT " +
                "ACHAT1.RECORDID, " +
                "ACHAT1.NUM_BON, " +
                "ACHAT1.DATE_BON, " +
                "ACHAT1.HEURE, " +
                "ACHAT1.CODE_FRS, " +
                "ACHAT1.EXPORTATION, " +
                "ACHAT1.BLOCAGE, " +

                "ACHAT1.NBR_P, " +
                "ACHAT1.TOT_QTE, " +

                "ACHAT1.TOT_HT, " +
                "ACHAT1.TOT_TVA, " +
                "ACHAT1.TIMBRE, " +
                "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE AS TOT_TTC, " +
                "ACHAT1.REMISE, " +
                "ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE AS MONTANT_BON, " +

                "ACHAT1.ANCIEN_SOLDE, " +
                "ACHAT1.VERSER, " +
                "ACHAT1.ANCIEN_SOLDE + (ACHAT1.TOT_HT + ACHAT1.TOT_TVA + ACHAT1.TIMBRE - ACHAT1.REMISE) - ACHAT1.VERSER AS RESTE, " +

                "FOURNIS.FOURNIS, " +
                "FOURNIS.ADRESSE, " +
                "FOURNIS.TEL " +
                "FROM ACHAT1 " +
                "LEFT JOIN FOURNIS ON (ACHAT1.CODE_FRS = FOURNIS.CODE_FRS)";


        if(!SOURCE_EXPORT.equals("EXPORTED")){
            querry = querry + " WHERE IS_EXPORTED = 0 ORDER BY ACHAT1.NUM_BON ";
        }else {
            querry = querry + " WHERE IS_EXPORTED = 1 ORDER BY ACHAT1.NUM_BON ";
        }

        achat1s = controller.select_all_achat1_from_database(querry);

        return achat1s;
    }


    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);

        Intent editIntent = new Intent(ActivityAchats.this, ActivityAchat.class);
        editIntent.putExtra("NUM_BON", achat1s.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ACHAT");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onLongClick(View v, final int position) {

        if (achat1s.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                  /*  case 0 -> {
                        if(prefs.getBoolean("AUTORISE_MODIFY_BON", true)){
                            if (!SOURCE_EXPORT.equals("EXPORTED")) {
                                new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.NORMAL_TYPE)
                                        .setTitleText("Bon d'achat")
                                        .setContentText("Voulez-vous vraiment modifier ce bon ?!")
                                        .setCancelText("Non")
                                        .setConfirmText("Modifier")
                                        .showCancelButton(true)
                                        .setCancelClickListener(Dialog::dismiss)
                                        .setConfirmClickListener(sDialog -> {

                                            Sound(R.raw.beep);

                                            Intent editIntent = new Intent(ActivityAchats.this, ActivityAchat.class);
                                            editIntent.putExtra("NUM_BON", achat1s.get(position).num_bon);
                                            editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ACHAT");
                                            editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                                            startActivity(editIntent);
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                            sDialog.dismiss();
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Information!")
                                        .setContentText("Ce bon est déja exporté")
                                        .show();
                            }
                        }else{
                            new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!!")
                                    .setContentText("Vous n'avez pas l'autorisation de modifier, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                                    .show();
                        }

                    }*/
                    case 0 ->{
                        if(prefs.getBoolean("AUTORISE_MODIFY_BON", true)){
                            new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le bon " + achat1s.get(position).num_bon + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_bon_achat(false, achat1s.get(position));
                                        setRecycle();

                                        sDialog.dismiss();

                                    })
                                    .show();
                        }else{
                            new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!!")
                                    .setContentText("Vous n'avez pas l'autorisation de supprimer, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                                    .show();
                        }
                    }

                    case 1 -> {
                        if (!achat1s.get(position).blocage.equals("F")) {
                            new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }
                        Activity bactivity;
                        bactivity = ActivityAchats.this;
                        final_panier = controller.select_all_achat2_from_database("" +
                                "SELECT " +
                                "ACHAT2.RECORDID, " +
                                "ACHAT2.CODE_BARRE, " +
                                "ACHAT2.NUM_BON, " +
                                "ACHAT2.PRODUIT, " +
                                "ACHAT2.NBRE_COLIS, " +
                                "ACHAT2.COLISSAGE, " +
                                "ACHAT2.QTE, " +
                                "ACHAT2.QTE_GRAT, " +
                                "ACHAT2.PU, " +
                                "ACHAT2.TVA, " +
                                "ACHAT2.CODE_DEPOT, " +
                                "ACHAT2.QTE_GRAT, " +
                                "PRODUIT.STOCK " +
                                "FROM ACHAT2 " +
                                "LEFT JOIN PRODUIT ON (ACHAT2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                "WHERE ACHAT2.NUM_BON = '" + achat1s.get(position).num_bon + "'");
                        Printing printer = new Printing();
                        try {
                            printer.start_print_bon(bactivity, "ACHAT", final_panier, null, achat1s.get(position));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
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
                    new SweetAlertDialog(ActivityAchats.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer le bon " + achat1s.get(position).num_bon + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                controller.delete_bon_achat(false, achat1s.get(position));
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
            Intent editIntent = new Intent(ActivityAchats.this, ActivityAchat.class);
            editIntent.putExtra("TYPE_ACTIVITY", "NEW_ACHAT");
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

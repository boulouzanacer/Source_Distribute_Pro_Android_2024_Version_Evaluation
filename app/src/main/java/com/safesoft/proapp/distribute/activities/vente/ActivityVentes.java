package com.safesoft.proapp.distribute.activities.vente;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.utils.Env;


import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityVentes extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick, RecyclerAdapterBon1.ItemLongClick {


    RecyclerView recyclerView;
    RecyclerAdapterBon1 adapter;
    ArrayList<PostData_Bon1> bon1s;
    ArrayList<PostData_Bon2> final_panier;
    DATABASE controller;

    private final String PREFS = "ALL_PREFS";
    private String SOURCE_EXPORT = "";
    private SharedPreferences prefs;
    private NumberFormat nf;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ventes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        initViews();

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bons de livraison");
            getSupportActionBar().setSubtitle("Client");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        controller = new DATABASE(this);

        if (getIntent() != null) {
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        }

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.remove("FILTRE_SEARCH_VALUE");
        editor.remove("FILTRE_SEARCH_FAMILLE");
        editor.apply();

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_vente);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", 0).apply();
    }

    @Override
    protected void onStart() {

        setRecycle("",false);
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle(String text_search, Boolean isSearch) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterBon1(this, getItems(text_search, isSearch), "SALE");

        recyclerView.setAdapter(adapter);
    }


    public ArrayList<PostData_Bon1> getItems(String text_search, Boolean isSearch) {
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
                "BON1.MONTANT_ACHAT, " +
                "BON1.TOT_HT - BON1.REMISE - BON1.MONTANT_ACHAT AS BENIFICE_BON, " +

                "BON1.ANCIEN_SOLDE, " +
                "BON1.VERSER, " +
                "BON1.ANCIEN_SOLDE + (BON1.TOT_HT + BON1.TOT_TVA + BON1.TIMBRE - BON1.REMISE) - BON1.VERSER AS RESTE, " +

                "BON1.CODE_CLIENT, " +
                "CLIENT.CLIENT, " +
                "CLIENT.ADRESSE, " +
                "CLIENT.WILAYA, " +
                "CLIENT.COMMUNE, " +
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

                "BON1.LIVRER, " +
                "BON1.DATE_LIV, " +
                "BON1.IS_IMPORTED, " +

                "BON1.CODE_DEPOT, " +
                "BON1.CODE_VENDEUR, " +
                "BON1.EXPORTATION, " +
                "BON1.BLOCAGE " +
                "FROM BON1 " +
                "LEFT JOIN CLIENT ON BON1.CODE_CLIENT = CLIENT.CODE_CLIENT ";

        /*To order a VARCHAR column that stores dates in the format dd/MM/yyyy in Android (Java) SQLite,
        you will first need to convert the VARCHAR to a date format that SQLite can order correctly.
        SQLite does not natively understand the dd/MM/yyyy format for dates,
        so you'll need to transform it into a format that SQLite can work with (e.g., yyyy-MM-dd or as Julian days).*/

        if (!SOURCE_EXPORT.equals("EXPORTED")) {
            querry = querry + " WHERE IS_EXPORTED = 0 AND (BON1.DATE_BON LIKE '%" + text_search + "%' OR BON1.MODE_RG LIKE '%" + text_search + "%' OR CLIENT.CLIENT LIKE '%" + text_search + "%') ORDER BY strftime('%Y-%m-%d', SUBSTR(BON1.DATE_BON, 7, 4) || '-' || SUBSTR(BON1.DATE_BON, 4, 2) || '-' || SUBSTR(BON1.DATE_BON, 1, 2)) DESC ";
        } else {
            querry = querry + " WHERE IS_EXPORTED = 1 AND (BON1.DATE_BON LIKE '%" + text_search + "%' OR BON1.MODE_RG LIKE '%" + text_search + "%' OR CLIENT.CLIENT LIKE '%" + text_search + "%') ORDER BY strftime('%Y-%m-%d', SUBSTR(BON1.DATE_BON, 7, 4) || '-' || SUBSTR(BON1.DATE_BON, 4, 2) || '-' || SUBSTR(BON1.DATE_BON, 1, 2)) DESC ";
        }

        bon1s = controller.select_all_bon1_from_database(querry);

        return bon1s;
    }


    @Override
    public void onClick(View v, int position) {

        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.beep);
        }
        Intent editIntent = new Intent(ActivityVentes.this, ActivityVente.class);
        editIntent.putExtra("NUM_BON", bon1s.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_SALE");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }


    @Override
    public void onLongClick(View v, final int position) {

        if (bon1s.get(position).blocage.equals("F")) {
            final CharSequence[] items = {"Supprimer", "Imprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    /*case 0 -> {
                        if(prefs.getBoolean("AUTORISE_MODIFY_BON", true)){
                            if (!SOURCE_EXPORT.equals("EXPORTED")) {
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
                                            editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                                            startActivity(editIntent);
                                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                                            sDialog.dismiss();
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Information!")
                                        .setContentText("Ce bon est déja exporté")
                                        .show();
                            }
                        }else{
                            new SweetAlertDialog(ActivitySales.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!!")
                                    .setContentText("Vous n'avez pas l'autorisation de modifier, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                                    .show();
                        }

                    }*/
                    case 0 -> {
                        if (prefs.getBoolean("AUTORISE_MODIFY_BON", true)) {
                            new SweetAlertDialog(ActivityVentes.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_bon_vente(false, bon1s.get(position));
                                        setRecycle("", false);

                                        sDialog.dismiss();

                                    })
                                    .show();
                        } else {
                            new SweetAlertDialog(ActivityVentes.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!!")
                                    .setContentText("Vous n'avez pas l'autorisation de supprimer, Demandez depuis votre superieur ou ( Créer un bon de retour ) ")
                                    .show();
                        }
                    }
                    case 1 -> {
                        if (!bon1s.get(position).blocage.equals("F")) {
                            new SweetAlertDialog(ActivityVentes.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Ce bon n'est pas encore validé")
                                    .show();
                            return;
                        }

                        final_panier = controller.select_bon2_from_database("SELECT " +
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
                                "BON2.DESTOCK_TYPE, " +
                                "BON2.DESTOCK_CODE_BARRE, " +
                                "BON2.DESTOCK_QTE, " +

                                "PRODUIT.ISNEW, " +
                                "PRODUIT.PV_LIMITE, " +
                                "PRODUIT.STOCK, " +
                                "PRODUIT.PROMO, " +
                                "PRODUIT.QTE_PROMO, " +
                                "PRODUIT.D1, " +
                                "PRODUIT.D2, " +
                                "PRODUIT.PP1_HT " +

                                "FROM BON2 " +
                                "LEFT JOIN PRODUIT ON (BON2.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                                "WHERE BON2.NUM_BON = '" + bon1s.get(position).num_bon + "'");

                        if (Objects.equals(prefs.getString("LANGUE_TICKET", "LATIN"), "LATIN")) {
                            Activity bactivity;
                            bactivity = ActivityVentes.this;

                            Printing printer = new Printing();
                            try {
                                printer.start_print_bon_vente(bactivity, "VENTE", final_panier, bon1s.get(position));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {

                            Intent html_intent = new Intent(this, ActivityHtmlView.class);
                            html_intent.putExtra("TYPE_BON", "VENTE");
                            html_intent.putExtra("BON1", bon1s.get(position));
                            html_intent.putExtra("BON2", final_panier);
                            startActivity(html_intent);
                        }

                    }
                }
            });
            builder.show();
        } else {
            final CharSequence[] items = {"Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                if (item == 0) {
                    new SweetAlertDialog(ActivityVentes.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s.get(position).num_bon + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                controller.delete_bon_en_attente(false, bon1s.get(position).num_bon);
                                setRecycle("", false);

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
        MenuInflater inflater = getMenuInflater();
        if (!SOURCE_EXPORT.equals("EXPORTED")) {
            inflater.inflate(R.menu.menu_ventes_not_exported, menu);
        }else{
            inflater.inflate(R.menu.menu_ventes_exported, menu);
        }

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Rechercher");

//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////

        menu.add(Menu.NONE, Menu.NONE, 1, "Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText, true);

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {

                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                Toast.makeText(getBaseContext(), "dummy Search", Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(true);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //=======
                        setProgressBarIndeterminateVisibility(false);

                    }
                }, 2000);

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.new_sale) {
            if (!prefs.getBoolean("APP_ACTIVATED", false) && (!bon1s.isEmpty())) {
                new SweetAlertDialog(ActivityVentes.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Important !")
                        .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                        .show();
            } else {
                Intent editIntent = new Intent(ActivityVentes.this, ActivityVente.class);
                editIntent.putExtra("TYPE_ACTIVITY", "NEW_SALE");
                editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                startActivity(editIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

        }else if(item.getItemId() == R.id.delete_all_bon){
            controller.delete_all_bon(true);
            setRecycle("", false);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.back);
        }
        super.onBackPressed();
    }

    public void Sound(int SourceSound) {
        MediaPlayer mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

}

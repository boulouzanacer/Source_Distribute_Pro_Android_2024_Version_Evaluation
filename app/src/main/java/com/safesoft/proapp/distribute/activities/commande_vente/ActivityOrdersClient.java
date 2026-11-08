package com.safesoft.proapp.distribute.activities.commande_vente;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_StockError;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.utils.Env;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityOrdersClient extends AppCompatActivity implements RecyclerAdapterBon1.ItemClick, RecyclerAdapterBon1.ItemLongClick {

    RecyclerView recyclerView;
    RecyclerAdapterBon1 adapter;
    ArrayList<PostData_Bon1> bon1s_temp;
    ArrayList<PostData_Bon2> final_panier;
    ArrayList<PostData_StockError> error_stock_messages;
    DATABASE controller;
    private TextView list_bon_total;
    private final String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;
    private String SOURCE_EXPORT = "";
    private SearchView searchView;
    private String CODE_DEPOT;
    private TextView btn_pill_all;
    private TextView btn_pill_valid;
    private TextView btn_pill_pending;
    private TextView btn_pill_transferred;
    private TextView btn_pill_cancelled;
    private String currentFilter = "ALL";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date_format;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat heure_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ventes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
                }
                insetsController.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                );
            }
        }

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bons de commandes");
            getSupportActionBar().setSubtitle("Client");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        controller = new DATABASE(this);

        if (getIntent() != null) {
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        }

        initViews();

        date_format = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        heure_format = new SimpleDateFormat("HH:mm:ss", Locale.US);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.remove("FILTRE_SEARCH_VALUE");
        editor.remove("FILTRE_SEARCH_FAMILLE");
        editor.apply();

        //Reset last item selected in list product
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putInt("LAST_CLICKED_POSITION", 0).apply();

        CODE_DEPOT = prefs.getString("CODE_DEPOT", "000000");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_vente);
        list_bon_total = findViewById(R.id.list_bon_total);

        btn_pill_all = findViewById(R.id.btn_pill_all);
        btn_pill_valid = findViewById(R.id.btn_pill_valid);
        btn_pill_pending = findViewById(R.id.btn_pill_pending);
        btn_pill_transferred = findViewById(R.id.btn_pill_transferred);
        btn_pill_cancelled = findViewById(R.id.btn_pill_cancelled);

        View.OnClickListener pillClickListener = v -> {
            int id = v.getId();
            if (id == R.id.btn_pill_all) {
                currentFilter = "ALL";
            } else if (id == R.id.btn_pill_valid) {
                currentFilter = "VALID";
            } else if (id == R.id.btn_pill_pending) {
                currentFilter = "PENDING";
            } else if (id == R.id.btn_pill_transferred) {
                currentFilter = "TRANSFERRED";
            } else if (id == R.id.btn_pill_cancelled) {
                currentFilter = "CANCELLED";
            }
            updateFilterPillsUI();
            String query = (searchView != null && searchView.getQuery() != null) ? searchView.getQuery().toString() : "";
            setRecycle(query);
        };

        btn_pill_all.setOnClickListener(pillClickListener);
        btn_pill_valid.setOnClickListener(pillClickListener);
        btn_pill_pending.setOnClickListener(pillClickListener);
        btn_pill_transferred.setOnClickListener(pillClickListener);
        btn_pill_cancelled.setOnClickListener(pillClickListener);

        if ("EXPORTED".equals(SOURCE_EXPORT)) {
            View filterContainer = findViewById(R.id.filter_bar_container);
            if (filterContainer != null) filterContainer.setVisibility(View.GONE);
        }
    }

    private void updateFilterPillsUI() {
        btn_pill_all.setBackgroundResource(R.drawable.bg_filter_pill_unselected);
        btn_pill_all.setTextColor(android.graphics.Color.parseColor("#475569"));
        btn_pill_all.setTypeface(null, android.graphics.Typeface.NORMAL);

        btn_pill_valid.setBackgroundResource(R.drawable.bg_filter_pill_unselected);
        btn_pill_valid.setTextColor(android.graphics.Color.parseColor("#475569"));
        btn_pill_valid.setTypeface(null, android.graphics.Typeface.NORMAL);

        btn_pill_pending.setBackgroundResource(R.drawable.bg_filter_pill_unselected);
        btn_pill_pending.setTextColor(android.graphics.Color.parseColor("#475569"));
        btn_pill_pending.setTypeface(null, android.graphics.Typeface.NORMAL);

        btn_pill_transferred.setBackgroundResource(R.drawable.bg_filter_pill_unselected);
        btn_pill_transferred.setTextColor(android.graphics.Color.parseColor("#475569"));
        btn_pill_transferred.setTypeface(null, android.graphics.Typeface.NORMAL);

        btn_pill_cancelled.setBackgroundResource(R.drawable.bg_filter_pill_unselected);
        btn_pill_cancelled.setTextColor(android.graphics.Color.parseColor("#475569"));
        btn_pill_cancelled.setTypeface(null, android.graphics.Typeface.NORMAL);

        if ("ALL".equals(currentFilter)) {
            btn_pill_all.setBackgroundResource(R.drawable.bg_filter_pill_selected);
            btn_pill_all.setTextColor(android.graphics.Color.parseColor("#0F172A"));
            btn_pill_all.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("VALID".equals(currentFilter)) {
            btn_pill_valid.setBackgroundResource(R.drawable.bg_filter_pill_selected);
            btn_pill_valid.setTextColor(android.graphics.Color.parseColor("#0F172A"));
            btn_pill_valid.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("PENDING".equals(currentFilter)) {
            btn_pill_pending.setBackgroundResource(R.drawable.bg_filter_pill_selected);
            btn_pill_pending.setTextColor(android.graphics.Color.parseColor("#0F172A"));
            btn_pill_pending.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("TRANSFERRED".equals(currentFilter)) {
            btn_pill_transferred.setBackgroundResource(R.drawable.bg_filter_pill_selected);
            btn_pill_transferred.setTextColor(android.graphics.Color.parseColor("#0F172A"));
            btn_pill_transferred.setTypeface(null, android.graphics.Typeface.BOLD);
        } else if ("CANCELLED".equals(currentFilter)) {
            btn_pill_cancelled.setBackgroundResource(R.drawable.bg_filter_pill_selected);
            btn_pill_cancelled.setTextColor(android.graphics.Color.parseColor("#0F172A"));
            btn_pill_cancelled.setTypeface(null, android.graphics.Typeface.BOLD);
        }
    }

    @Override
    protected void onStart() {
        updateFilterPillsUI();
        setRecycle("");

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    private void setRecycle(String text_search) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterBon1(this, getItems(text_search), "ORDER");
        recyclerView.setAdapter(adapter);

        list_bon_total.setText("Total bon : " + (bon1s_temp != null ? bon1s_temp.size() : 0));
    }

    public ArrayList<PostData_Bon1> getItems(String text_search) {
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
                "BON1_TEMP.REMARQUE, " +

                "BON1_TEMP.NBR_P, " +
                "BON1_TEMP.TOT_QTE, " +

                "BON1_TEMP.TOT_HT, " +
                "BON1_TEMP.TOT_TVA, " +
                "BON1_TEMP.TIMBRE, " +
                "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE AS TOT_TTC, " +
                "BON1_TEMP.REMISE, " +
                "BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE AS MONTANT_BON, " +
                "BON1_TEMP.MONTANT_ACHAT, " +
                "BON1_TEMP.TOT_HT - BON1_TEMP.REMISE - BON1_TEMP.MONTANT_ACHAT AS BENIFICE_BON, " +

                "BON1_TEMP.ANCIEN_SOLDE, " +
                "BON1_TEMP.VERSER, " +
                "BON1_TEMP.ANCIEN_SOLDE + (BON1_TEMP.TOT_HT + BON1_TEMP.TOT_TVA + BON1_TEMP.TIMBRE - BON1_TEMP.REMISE) - BON1_TEMP.VERSER AS RESTE, " +

                "BON1_TEMP.CODE_CLIENT, " +
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

                "BON1_TEMP.LATITUDE, " +
                "BON1_TEMP.LONGITUDE, " +

                "BON1_TEMP.LIVRER, " +
                "BON1_TEMP.DATE_LIV, " +
                "BON1_TEMP.IS_IMPORTED, " +

                "BON1_TEMP.CODE_DEPOT, " +
                "BON1_TEMP.CODE_VENDEUR, " +
                "BON1_TEMP.EXPORTATION, " +
                "BON1_TEMP.BLOCAGE " +
                "FROM BON1_TEMP " +
                "LEFT JOIN CLIENT ON BON1_TEMP.CODE_CLIENT = CLIENT.CODE_CLIENT";

        // Escape simple des apostrophes (cause principale de SQLiteException)
        text_search = (text_search == null) ? "" : text_search.replace("'", "''");

        if (!"EXPORTED".equals(SOURCE_EXPORT)) {
            querry = querry + " WHERE IS_EXPORTED = 0 ";
        } else {
            querry = querry + " WHERE IS_EXPORTED = 1 ";
        }

        if ("VALID".equals(currentFilter)) {
            querry = querry + " AND BON1_TEMP.BLOCAGE = 'F' ";
        } else if ("PENDING".equals(currentFilter)) {
            querry = querry + " AND BON1_TEMP.BLOCAGE = 'M' ";
        } else if ("TRANSFERRED".equals(currentFilter)) {
            querry = querry + " AND BON1_TEMP.BLOCAGE = 'T' ";
        } else if ("CANCELLED".equals(currentFilter)) {
            querry = querry + " AND BON1_TEMP.BLOCAGE = 'A' ";
        }

        querry = querry + " AND (BON1_TEMP.DATE_BON LIKE '%" + text_search + "%' OR BON1_TEMP.MODE_RG LIKE '%" + text_search + "%' OR CLIENT.CLIENT LIKE '%" + text_search + "%') ORDER BY strftime('%Y-%m-%d', substr(DATE_BON, 7, 4) || '-' || substr(DATE_BON, 4, 2) || '-' || substr(DATE_BON, 1, 2)) ASC, strftime('%H:%M:%S', BON1_TEMP.HEURE) ASC ";

        bon1s_temp = controller.select_all_bon1_from_database(querry);

        return bon1s_temp;
    }

    @SuppressWarnings("deprecation")
    private void applyActivityTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, R.anim.push_left_in, R.anim.push_left_out);
        } else {
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
    }

    @Override
    public void onClick(View v, int position) {
        Intent editIntent = new Intent(ActivityOrdersClient.this, ActivityOrderClient.class);
        editIntent.putExtra("NUM_BON", bon1s_temp.get(position).num_bon);
        editIntent.putExtra("TYPE_ACTIVITY", "EDIT_ORDER_CLIENT");
        editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
        startActivity(editIntent);
        applyActivityTransition();
    }

    @Override
    public void onLongClick(View v, final int position) {
        if ("F".equals(bon1s_temp.get(position).blocage)) {
            showValidatedOrderDialog(v, position);
        } else {
            showPendingOrderDialog(v, position);
        }
    }

    private void showValidatedOrderDialog(View v, final int position) {
        final CharSequence[] items = {"Supprimer", "Imprimer", "Transferer vers bon vente", "Itineraire client", "Commande annuler"};

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setIcon(R.drawable.blue_circle_24);
        builder.setTitle("Choisissez une action");
        builder.setItems(items, (dialog, item) -> {
            switch (item) {
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
                                    setRecycle("");
                                    sDialog.dismiss();
                                })
                                .show();
                case 1 -> {
                    if (!"F".equals(bon1s_temp.get(position).blocage)) {
                        new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Ce bon n'est pas encore validé")
                                .show();
                        return;
                    }

                    final_panier = controller.select_bon2_from_database("SELECT " +
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
                            "BON2_TEMP.DESTOCK_TYPE, " +
                            "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                            "BON2_TEMP.DESTOCK_QTE, " +

                            "PRODUIT.ISNEW, " +
                            "PRODUIT.PV_LIMITE, " +
                            "PRODUIT.STOCK, " +
                            "PRODUIT.PROMO, " +
                            "PRODUIT.QTE_PROMO, " +
                            "PRODUIT.D1, " +
                            "PRODUIT.D2, " +
                            "PRODUIT.PP1_HT " +

                            "FROM BON2_TEMP " +
                            "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2_TEMP.NUM_BON = '" + bon1s_temp.get(position).num_bon + "'");

                    if (Objects.equals(prefs.getString("LANGUE_TICKET", "LATIN"), "LATIN")) {
                        Activity bactivity = ActivityOrdersClient.this;
                        Printing printer = new Printing();

                        try {
                            printer.start_print_bon_vente(bactivity, "ORDER", final_panier, bon1s_temp.get(position));
                        } catch (Exception e) {
                            Log.e("ActivityOrdersClient", "Error printing bon de commande", e);

                            if (e.getMessage() != null && e.getMessage().contains("android.bluetooth.BluetoothAdapter.getBondedDevices()")) {
                                new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Erreur")
                                        .setContentText("Le bluetooth n'est pas disponible.")
                                        .show();
                            }
                        }
                    } else {
                        Intent html_intent = new Intent(this, ActivityHtmlView.class);
                        html_intent.putExtra("TYPE_BON", "COMMANDE");
                        html_intent.putExtra("BON1", bon1s_temp.get(position));
                        html_intent.putExtra("BON2", final_panier);
                        startActivity(html_intent);
                    }
                }
                case 2 -> {
                    String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM BON1 WHERE NUM_BON IS NOT NULL";
                    String NUM_BON_BON1 = controller.select_max_num_bon(selectQuery);

                    final_panier = controller.select_bon2_from_database("SELECT " +
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
                            "BON2_TEMP.DESTOCK_TYPE, " +
                            "BON2_TEMP.DESTOCK_CODE_BARRE, " +
                            "BON2_TEMP.DESTOCK_QTE, " +

                            "PRODUIT.ISNEW, " +
                            "PRODUIT.PV_LIMITE, " +
                            "PRODUIT.STOCK, " +
                            "PRODUIT.PROMO, " +
                            "PRODUIT.QTE_PROMO, " +
                            "PRODUIT.D1, " +
                            "PRODUIT.D2, " +
                            "PRODUIT.PP1_HT " +

                            "FROM BON2_TEMP " +
                            "LEFT JOIN PRODUIT ON (BON2_TEMP.CODE_BARRE = PRODUIT.CODE_BARRE) " +
                            "WHERE BON2_TEMP.NUM_BON = '" + bon1s_temp.get(position).num_bon + "'");

                    if (!(prefs.getBoolean("STOCK_MOINS", false))) {
                        // function to check availability of stock
                        error_stock_messages = controller.check_stock_product(final_panier);
                        if (!error_stock_messages.isEmpty()) {
                            //prepare message
                            StringBuilder message = new StringBuilder("Stock insuffisant pour les produits suivants : \n");
                            for (PostData_StockError err : error_stock_messages) {
                                message.append(err.produit).append(" / QTE: ").append(err.qte).append(" / STOCK: ").append(err.stock).append("\n");
                            }

                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!")
                                    .setContentText(message.toString())
                                    .show();
                            return;
                        }
                    }

                    // get date and time
                    Calendar c = Calendar.getInstance();
                    String formattedDate_Show = date_format.format(c.getTime());
                    String currentTime = heure_format.format(c.getTime());

                    bon1s_temp.get(position).date_bon = formattedDate_Show;
                    bon1s_temp.get(position).heure = currentTime;

                    if (controller.Export_commande_to_ventes(bon1s_temp.get(position), final_panier, CODE_DEPOT, NUM_BON_BON1)) {
                        setRecycle("");
                        new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Bon de commande transféré avec succès")
                                .show();
                    } else {
                        new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Erreur!")
                                .setContentText("Probleme de transfère de bon de commande, Transfère annulé !")
                                .show();
                    }
                }
                case 3 -> {
                    try {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + bon1s_temp.get(position).latitude_client + "," + bon1s_temp.get(position).longitude_client);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    } catch (Exception e) {
                        Crouton.makeText(ActivityOrdersClient.this, "Erreur de navigation : Position client non disponible", Style.ALERT).show();
                    }
                }
                case 4 -> {
                    if ("A".equals(bon1s_temp.get(position).blocage)) {
                        new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Information!")
                                .setContentText("Cette commande est déjà annulée.")
                                .show();
                        return;
                    }
                    new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Annulation!")
                            .setContentText("Voulez-vous vraiment annuler cette commande " + bon1s_temp.get(position).num_bon + " ?, Opération non reversible!!!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Confirmer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {
                                controller.update_bon1_temp_commande_annuler(bon1s_temp.get(position));
                                setRecycle("");
                                sDialog.dismiss();
                            })
                            .show();
                }
            }
        });
        builder.show();
    }

    private void showPendingOrderDialog(View v, final int position) {
        final CharSequence[] items = {"Supprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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
                            setRecycle("");
                            sDialog.dismiss();
                        })
                        .show();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (!"EXPORTED".equals(SOURCE_EXPORT)) {
            inflater.inflate(R.menu.menu_ventes_not_exported, menu);
        } else {
            inflater.inflate(R.menu.menu_ventes_exported, menu);
        }
        Context themedContext = getSupportActionBar() != null ? getSupportActionBar().getThemedContext() : this;
        searchView = new SearchView(themedContext);
        searchView.setQueryHint("Rechercher");

        menu.add(Menu.NONE, Menu.NONE, 1, "Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText);
                return false;
            }

            @Override
            @SuppressWarnings("deprecation")
            public boolean onQueryTextSubmit(final String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null && searchView != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }

                Toast.makeText(getBaseContext(), "dummy Search", Toast.LENGTH_SHORT).show();
                setProgressBarIndeterminateVisibility(true);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> setProgressBarIndeterminateVisibility(false), 2000);

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.new_sale) {
            if (!prefs.getBoolean("APP_ACTIVATED", false) && !bon1s_temp.isEmpty()) {
                new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Important !")
                        .setContentText(Env.MESSAGE_DEMANDE_ACTIVITATION)
                        .show();
            } else {
                Intent editIntent = new Intent(ActivityOrdersClient.this, ActivityOrderClient.class);
                editIntent.putExtra("TYPE_ACTIVITY", "NEW_ORDER_CLIENT");
                editIntent.putExtra("SOURCE_EXPORT", SOURCE_EXPORT);
                startActivity(editIntent);
                applyActivityTransition();
            }
            return true;
        } else if (item.getItemId() == R.id.delete_all_bon) {
            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer tous les bon exportés ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {
                        controller.delete_all_bon(true);
                        setRecycle("");
                        sDialog.dismiss();
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

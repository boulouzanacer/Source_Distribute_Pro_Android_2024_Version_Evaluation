package com.safesoft.proapp.distribute.activities.commande_vente;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityHtmlView;
import com.safesoft.proapp.distribute.activities.client.TSPActivityMaps;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterBon1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.printing.Printing;
import com.safesoft.proapp.distribute.utils.Env;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    DATABASE controller;
    private TextView list_bon_total;
    private final String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;
    private String SOURCE_EXPORT = "";
    private NumberFormat nf;
    private SearchView searchView;
    private String CODE_DEPOT;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat date_format;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat heure_format;



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

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bons de commandes");
            getSupportActionBar().setSubtitle("Client");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        controller = new DATABASE(this);

        if (getIntent() != null) {
            SOURCE_EXPORT = getIntent().getStringExtra("SOURCE_EXPORT");
        }

        initViews();

        date_format = new SimpleDateFormat("dd/MM/yyyy");
        heure_format = new SimpleDateFormat("HH:mm:ss");

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

    }

    @Override
    protected void onStart() {

        setRecycle("", false);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void setRecycle(String text_search, Boolean isSearch) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterBon1(this, getItems(text_search, isSearch), "ORDER");
        recyclerView.setAdapter(adapter);

        list_bon_total.setText("Total bon : " + bon1s_temp.size());
    }


    public ArrayList<PostData_Bon1> getItems(String text_search, Boolean isSearch) {
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


        if (!SOURCE_EXPORT.equals("EXPORTED")) {
            querry = querry + " WHERE IS_EXPORTED = 0 AND (BON1_TEMP.DATE_BON LIKE '%" + text_search + "%' OR BON1_TEMP.MODE_RG LIKE '%" + text_search + "%' OR CLIENT.CLIENT LIKE '%" + text_search + "%') ORDER BY strftime('%Y-%m-%d', substr(DATE_BON, 7, 4) || '-' || substr(DATE_BON, 4, 2) || '-' || substr(DATE_BON, 1, 2)) ASC, strftime('%H:%M:%S', BON1_TEMP.HEURE) ASC ";
        } else {
            querry = querry + " WHERE IS_EXPORTED = 1 AND (BON1_TEMP.DATE_BON LIKE '%" + text_search + "%' OR BON1_TEMP.MODE_RG LIKE '%" + text_search + "%' OR CLIENT.CLIENT LIKE '%" + text_search + "%') ORDER BY strftime('%Y-%m-%d', substr(DATE_BON, 7, 4) || '-' || substr(DATE_BON, 4, 2) || '-' || substr(DATE_BON, 1, 2)) ASC, strftime('%H:%M:%S', BON1_TEMP.HEURE) ASC ";
        }

        bon1s_temp = controller.select_all_bon1_from_database(querry);

        return bon1s_temp;
    }


    @Override
    public void onClick(View v, int position) {
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
            final CharSequence[] items = {"Supprimer", "Imprimer", "Transferer vers bon vente", "Itineraire client"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
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
                                        setRecycle("", false);

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
                            Activity bactivity;
                            bactivity = ActivityOrdersClient.this;

                            Printing printer = new Printing();

                            try {
                                printer.start_print_bon_vente(bactivity, "ORDER", final_panier, bon1s_temp.get(position));
                            } catch (Exception e) {

                                e.printStackTrace();

                                if(e.getMessage().contains("android.bluetooth.BluetoothAdapter.getBondedDevices()")){
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

                        // get date and time
                        Calendar c = Calendar.getInstance();
                        String formattedDate_Show = date_format.format(c.getTime());
                        String currentTime = heure_format.format(c.getTime());

                        bon1s_temp.get(position).date_bon = formattedDate_Show;
                        bon1s_temp.get(position).heure = currentTime;

                        if (controller.Export_commande_to_ventes(bon1s_temp.get(position), final_panier, CODE_DEPOT, NUM_BON_BON1 )) {
                            setRecycle("", false);
                            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Information!")
                                    .setContentText("Bon de commande transféré avec succès")
                                    .show();
                        }else{
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

                        }catch (Exception e){
                            Crouton.makeText(ActivityOrdersClient.this, "Erreur de navigation : Position client non disponible", Style.ALERT).show();
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
                    new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
                            .setTitleText("Suppression")
                            .setContentText("Voulez-vous vraiment supprimer le bon " + bon1s_temp.get(position).num_bon + " ?!")
                            .setCancelText("Anuuler")
                            .setConfirmText("Supprimer")
                            .showCancelButton(true)
                            .setCancelClickListener(Dialog::dismiss)
                            .setConfirmClickListener(sDialog -> {

                                controller.delete_bon_en_attente(true, bon1s_temp.get(position).num_bon);
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
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

        } else if (item.getItemId() == R.id.routing) {
            /*//  https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
            Intent nnn = new Intent(ActivityOrdersClient.this, ActivityMaps.class);
            startActivity(nnn);*/

            if ((ContextCompat.checkSelfPermission(ActivityOrdersClient.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(ActivityOrdersClient.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityOrdersClient.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3232);
                ActivityCompat.requestPermissions(ActivityOrdersClient.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3232);
            } else {
                //https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
                Intent tsp_intent = new Intent(ActivityOrdersClient.this, TSPActivityMaps.class);
                startActivity(tsp_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }else if(item.getItemId() == R.id.delete_all_bon){
            new SweetAlertDialog(ActivityOrdersClient.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer tous les bon exportés ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {

                        controller.delete_all_bon(true);
                        setRecycle("", false);

                        sDialog.dismiss();
                    })
                    .show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

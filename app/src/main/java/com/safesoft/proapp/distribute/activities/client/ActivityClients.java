package com.safesoft.proapp.distribute.activities.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClients;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.fragments.FragmentVersementClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityClients extends AppCompatActivity implements RecyclerAdapterClients.ItemClick, RecyclerAdapterClients.ItemLongClick, RecyclerAdapterClients.OnEncaisserListener {

    private static final int CAMERA_PERMISSION = 5;
    RecyclerView recyclerView;
    RecyclerAdapterClients adapter;
    ArrayList<PostData_Client> clients;
    DATABASE controller;
    private EventBus bus;
    private SearchView searchView;
    private TextView kpi_total_versement;
    private TextView kpi_total_solde;
    private final String[] NEED_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final List<String> NO_PERMISSION = new ArrayList<>();

    private void CheckAllPermission() {
        NO_PERMISSION.clear();
        for (String s : NEED_PERMISSION) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                NO_PERMISSION.add(s);
            }
        }
        if (!NO_PERMISSION.isEmpty()) {
            requestPermissions(NO_PERMISSION.toArray(new String[0]), 3232);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_clients);

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
            getSupportActionBar().setTitle("Liste Clients");
            getSupportActionBar().setSubtitle("0 client");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        CheckAllPermission();

        controller = new DATABASE(getApplicationContext());
        clients = new ArrayList<>();

        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);

        initViews();
    }

    @Override
    protected void onResume() {
        setRecycle("", false);
        super.onResume();
    }

    public void refreshAfterVersement() {
        setRecycle("", false);
    }

    private TextView chip_jour;
    private TextView chip_semaine;
    private TextView chip_mois;
    private int selectedPeriode = 0; // 0=Jour, 1=Semaine, 2=Mois

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view_client);
        kpi_total_versement = findViewById(R.id.kpi_total_versement);
        kpi_total_solde = findViewById(R.id.kpi_total_solde);

        chip_jour = findViewById(R.id.chip_jour);
        chip_semaine = findViewById(R.id.chip_semaine);
        chip_mois = findViewById(R.id.chip_mois);

        if (chip_jour != null) {
            chip_jour.setOnClickListener(v -> selectPeriode(0));
        }
        if (chip_semaine != null) {
            chip_semaine.setOnClickListener(v -> selectPeriode(1));
        }
        if (chip_mois != null) {
            chip_mois.setOnClickListener(v -> selectPeriode(2));
        }
        applyChipVisuals(selectedPeriode);

        if (kpi_total_versement != null) {
            View innerLayout = (View) kpi_total_versement.getParent();
            View cardView = innerLayout != null ? (View) innerLayout.getParent() : null;
            View target = cardView != null ? cardView : (innerLayout != null ? innerLayout : kpi_total_versement);
            target.setClickable(true);
            target.setFocusable(true);
            try (android.content.res.TypedArray ta = obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground})) {
                android.graphics.drawable.Drawable d = ta.getDrawable(0);
                if (d != null) {
                    target.setForeground(d);
                }
            }
            target.setOnClickListener(v -> {
                Intent intent = new Intent(ActivityClients.this, ActivityVersedClient.class);
                intent.putExtra(ActivityVersedClient.EXTRA_PERIODE, selectedPeriode);
                startActivity(intent);
            });
            kpi_total_versement.setOnClickListener(null);
        }
        if (kpi_total_solde != null) {
            kpi_total_solde.setOnClickListener(null);
        }
    }

    private void selectPeriode(int periode) {
        selectedPeriode = periode;
        applyChipVisuals(periode);
        refreshKpisPeriode();
    }

    private void applyChipVisuals(int periode) {
        if (chip_jour != null) {
            chip_jour.setBackgroundResource(periode == 0 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_jour.setTextColor(periode == 0 ? 0xFF0F172A : 0xFF64748B);
            chip_jour.setTypeface(null, periode == 0 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        if (chip_semaine != null) {
            chip_semaine.setBackgroundResource(periode == 1 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_semaine.setTextColor(periode == 1 ? 0xFF0F172A : 0xFF64748B);
            chip_semaine.setTypeface(null, periode == 1 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
        if (chip_mois != null) {
            chip_mois.setBackgroundResource(periode == 2 ? R.drawable.bg_chip_segmented_selected : R.drawable.bg_chip_segmented_unselected);
            chip_mois.setTextColor(periode == 2 ? 0xFF0F172A : 0xFF64748B);
            chip_mois.setTypeface(null, periode == 2 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        }
    }

    private String[] computeDateBounds(int periode) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        Calendar today = Calendar.getInstance(Locale.FRANCE);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar start = Calendar.getInstance(Locale.FRANCE);
        start.setTime(today.getTime());
        if (periode == 1) {
            start.add(Calendar.DAY_OF_YEAR, -6);
        } else if (periode == 2) {
            start.add(Calendar.DAY_OF_YEAR, -29);
        }
        return new String[]{sdf.format(start.getTime()), sdf.format(today.getTime())};
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setRecycle(String text_search, Boolean isScan) {
        if (isScan && searchView != null) {
            searchView.setQuery(text_search, false);
        }

        getItems(text_search);

        if (adapter == null) {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerAdapterClients(this, clients, this, this, this);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        int count = clients.size();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(String.format(Locale.FRANCE, "%,d client%s", count, (count > 1 ? "s" : "")));
        }

        refreshKpisPeriode();
    }

    private void refreshKpisPeriode() {
        if (controller == null || kpi_total_versement == null || kpi_total_solde == null) return;
        String[] bounds = computeDateBounds(selectedPeriode);
        double totVersement = controller.getClientTotalVersementsPeriode(bounds[0], bounds[1]);
        double totSolde = controller.getClientTotalSoldesPeriode(bounds[0], bounds[1]);
        kpi_total_versement.setText(String.format(Locale.FRANCE, "%,.2f DA", totVersement));
        kpi_total_solde.setText(String.format(Locale.FRANCE, "%,.2f DA", totSolde));
    }

    public ArrayList<PostData_Client> getItems(String qqry) {
        clients.clear();
        String querry = getString(qqry);

        try {
            clients.addAll(controller.select_clients_from_database(querry));
            return clients;
        } catch (Exception e) {
            Log.e("CLIENTS_QUERY_ERROR", querry, e);
            // fallback safe query
            clients.addAll(controller.select_clients_from_database("SELECT * FROM CLIENT ORDER BY CLIENT"));
            return clients;
        }
    }

    @NonNull
    private static String getString(String qqry) {
        String search = (qqry == null) ? "" : qqry.trim();

        // Escape simple des apostrophes (cause principale de SQLiteException)
        search = search.replace("'", "''");

        String querry;
        if (!search.isEmpty()) {
            querry = "SELECT * FROM CLIENT WHERE CODE_CLIENT LIKE '%" + search + "%' OR CLIENT LIKE '%" + search + "%' " + "OR TEL LIKE '%" + search + "%' " + "ORDER BY CLIENT";
        } else {
            querry = "SELECT * FROM CLIENT ORDER BY CLIENT";
        }
        return querry;
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onClientSelected(SelectedClientEvent clientEvent) {
        setRecycle("", false);
    }

    @SuppressWarnings("deprecation")
    private void applySlideTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(Activity.OVERRIDE_TRANSITION_OPEN, R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @Override
    public void onClick(View v, int position) {
        if (v.getId() == R.id.item_root) {
            if (position < 0 || position >= clients.size()) return;

            Intent intent = new Intent(ActivityClients.this, ActivityClientDetail.class);
            intent.putExtra("CODE_CLIENT", clients.get(position).code_client);
            startActivity(intent);
            applySlideTransition();
        }
    }

    @Override
    public void onLongClick(View v, int position) {
        if (v.getId() == R.id.item_root) {
            if (position < 0 || position >= clients.size()) return;

            final CharSequence[] items = {"Modifier", "Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0 -> {
                        if (clients.get(position).isImported == 1) {
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Client importé depuis le serveur, Vous n'avez pas le droit de le supprimer !")
                                    .show();
                            return;
                        }

                        FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
                        fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext(), "EDIT_CLIENT", clients.get(position));
                    }
                    case 1 -> {
                        if (clients.get(position).isImported == 1) {
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Client importé depuis le serveur, Vous n'avez pas le droit de le supprimer !")
                                    .show();
                            return;
                        }

                        String querry_has_bon1 = "SELECT CODE_CLIENT FROM BON1 WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + clients.get(position).code_client + "'";
                        String querry_has_bon1_temp = "SELECT CODE_CLIENT FROM BON1_TEMP WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + clients.get(position).code_client + "'";
                        if (controller.check_if_has_bon(querry_has_bon1) || controller.check_if_has_bon(querry_has_bon1_temp)) {
                            // you can't delete this client
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Il exist des bons créer avec ce client")
                                    .show();
                        } else {
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le client " + clients.get(position).client + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {
                                        controller.delete_client(clients.get(position).code_client);
                                        setRecycle("", false);
                                        sDialog.dismiss();
                                    }).show();
                        }
                    }
                }
            });
            builder.show();
        }
    }

    @Override
    public void onEncaisser(View v, int position) {
        if (position < 0 || position >= clients.size()) return;
        PostData_Client client = clients.get(position);
        FragmentVersementClient fragmentVersementClient = new FragmentVersementClient();
        fragmentVersementClient.showDialogbox(
                ActivityClients.this,
                client.solde_client,
                client.verser_client,
                0,
                "",
                client.code_client,
                false,
                ""
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_clients, menu);

        searchView = new SearchView(getSupportActionBar() != null ? getSupportActionBar().getThemedContext() : this);
        searchView.setQueryHint("Rechercher");

        menu.add(Menu.NONE, Menu.NONE, 0, "Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText, false);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null && searchView != null) {
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                }
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
        } else if (item.getItemId() == R.id.map) {
            if ((ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3232);
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3232);
            } else {
                startActivity(new Intent(ActivityClients.this, ActivityMaps.class));
                applySlideTransition();
            }
            return true;
        } else if (item.getItemId() == R.id.new_client) {
            FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
            fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext(), "NEW_CLIENT", null);
            return true;
        } else if (item.getItemId() == R.id.scan_client) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            } else {
                startScan();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(barcode -> setRecycle(barcode.rawValue, true))
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        Log.d("LeakCheck", "ActivityClients is being destroyed");
    }
}

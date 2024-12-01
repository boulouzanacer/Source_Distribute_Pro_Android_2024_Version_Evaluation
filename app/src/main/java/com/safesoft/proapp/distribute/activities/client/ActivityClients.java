package com.safesoft.proapp.distribute.activities.client;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScanner;
import com.edwardvanraak.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClients;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityClients extends AppCompatActivity implements RecyclerAdapterClients.ItemClick, RecyclerAdapterClients.ItemLongClick {

    private static final int CAMERA_PERMISSION = 5;
    RecyclerView recyclerView;
    RecyclerAdapterClients adapter;
    ArrayList<PostData_Client> clients;
    DATABASE controller;
    private MediaPlayer mp;
    private EventBus bus;
    private TextView nbr_client;
    private SearchView searchView;
    private final String PREFS = "ALL_PREFS";
    SharedPreferences prefs;
    private final String[] NEED_PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private final List<String> NO_PERMISSION = new ArrayList<String>();

    private void CheckAllPermission() {
        NO_PERMISSION.clear();
        for (String s : NEED_PERMISSION) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                NO_PERMISSION.add(s);
            }
        }
        if (NO_PERMISSION.isEmpty()) {

        } else {
            requestPermissions(NO_PERMISSION.toArray(new String[0]), 3232);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        CheckAllPermission();

        controller = new DATABASE(this);
        clients = new ArrayList<>();

        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Clients");

        initViews();

        setRecycle("", false);

    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_client);
        nbr_client = findViewById(R.id.list_client_nbr_client);
    }


    private void setRecycle(String text_search, Boolean isScan) {
        if(isScan){
            searchView.setQuery(text_search, false);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterClients(this, getItems(text_search));
        recyclerView.setAdapter(adapter);
        nbr_client.setText("Nombre de client : " + clients.size());

    }

    public ArrayList<PostData_Client> getItems(String qqry) {

        clients.clear();

        if (!qqry.isEmpty()) {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT WHERE CODE_CLIENT LIKE '%" + qqry + "%' OR CLIENT LIKE '%" + qqry + "%' OR TEL LIKE '%" + qqry + "%' ORDER BY CLIENT";
            clients = controller.select_clients_from_database(querry);
        } else {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT ORDER BY CLIENT";
            clients = controller.select_clients_from_database(querry);
        }

        return clients;
    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent) {
        setRecycle("", false);
    }

    @Override
    public void onClick(View v, int position) {

        if (v.getId() == R.id.item_root) {

            if (prefs.getBoolean("ENABLE_SOUND", false)) {
                Sound(R.raw.beep);
            }
            Intent intent = new Intent(ActivityClients.this, ActivityClientDetail.class);

            intent.putExtra("CODE_CLIENT", clients.get(position).code_client);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void onLongClick(View v, int position) {
        if (v.getId() == R.id.item_root) {
            final CharSequence[] items = {"Modifier", "Supprimer"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.blue_circle_24);
            builder.setTitle("Choisissez une action");
            builder.setItems(items, (dialog, item) -> {
                switch (item) {
                    case 0 -> {
                        FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
                        fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext(), "EDIT_CLIENT", clients.get(position));
                    }
                    case 1 -> {
                        if (clients.get(position).isNew == 0) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_clients, menu);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Rechercher");

//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////

        menu.add(Menu.NONE, Menu.NONE, 0, "Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText, false);

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
        }
        if (item.getItemId() == R.id.map) {
            if ((ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3232);
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3232);
            } else {
                startActivity(new Intent(ActivityClients.this, ActivityMaps.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        } else if (item.getItemId() == R.id.new_client) {
            FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
            fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext(), "NEW_CLIENT", null);
        }else if(item.getItemId() == R.id.scan_client){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

            } else {
                startScan();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void startScan() {
        /**
         * Build a new MaterialBarcodeScanner
         */

        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Scanning...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        // Sound( R.raw.bleep);
                        setRecycle(barcode.rawValue, true);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    public void onBackPressed() {

        if (prefs.getBoolean("ENABLE_SOUND", false)) {
            Sound(R.raw.back);
        }
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }


}

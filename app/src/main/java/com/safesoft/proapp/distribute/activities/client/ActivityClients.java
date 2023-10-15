package com.safesoft.proapp.distribute.activities.client;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
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

import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.activities.ActivityRouting;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.activities.vente.ActivitySales;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClients;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityClients extends AppCompatActivity implements RecyclerAdapterClients.ItemClick, RecyclerAdapterClients.ItemLongClick{

    private static final int REQUEST_ACTIVITY_NEW_CLIENT = 4000;
    RecyclerView recyclerView;
    RecyclerAdapterClients adapter;
    ArrayList<PostData_Client> clients;
    DATABASE controller;
    private  MediaPlayer mp;
    private EventBus bus;
    private TextView nbr_client;

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
        if (NO_PERMISSION.size() == 0) {

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
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Clients");

    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client);
        nbr_client = (TextView) findViewById(R.id.list_client_nbr_client);
    }

    @Override
    protected void onStart() {

        initViews();

        setRecycle("");

        super.onStart();
    }

    private void setRecycle(String text_search) {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterClients(this, getItems(text_search));
        recyclerView.setAdapter(adapter);
        nbr_client.setText("Nombre de client : " + clients.size());

    }

    public ArrayList<PostData_Client> getItems(String qqry) {
        if(qqry.length() > 0){
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT WHERE CODE_CLIENT LIKE '%"+qqry+"%' OR CLIENT LIKE '%"+qqry+"%' OR TEL LIKE '%"+qqry+"%' ORDER BY CLIENT";
            // querry = "SELECT * FROM Events";
            clients = controller.select_clients_from_database(querry);
        }else {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT ORDER BY CLIENT";
            // querry = "SELECT * FROM Events";
            clients = controller.select_clients_from_database(querry);
        }

        return clients;
    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent){
        setRecycle("");
    }

    @Override
    public void onClick(View v, int position) {

        if(v.getId() == R.id.item_root){
            Sound(R.raw.beep);
            Intent intent = new Intent(ActivityClients.this, ActivityClientDetail.class);

            // intent.putExtra("CLIENT", clients.get(position).client);
            intent.putExtra("CODE_CLIENT", clients.get(position).code_client);
            // intent.putExtra("TEL", clients.get(position).tel);
            // intent.putExtra("LATITUDE", clients.get(position).latitude);
            // intent.putExtra("LONGITUDE", clients.get(position).longitude);
            // intent.putExtra("ADRESSE", clients.get(position).adresse);
            //  intent.putExtra("MODE_TARIF", clients.get(position).mode_tarif);
            //  intent.putExtra("ACHAT", clients.get(position).achat_montant);
            //  intent.putExtra("VERSER", clients.get(position).verser_montant);
            //  intent.putExtra("SOLDE", clients.get(position).solde_montant);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

    }

    @Override
    public void onLongClick(View v, int position) {
        if(v.getId() == R.id.item_root){
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
                    case 1 ->{
                        if(clients.get(position).isNew == 0){
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Client importé depuis le serveur, Vous n'avez pas le droit de le supprimer !")
                                    .show();
                            return;
                        }

                        String querry_has_bon1 = "SELECT CODE_CLIENT FROM BON1 WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + clients.get(position).code_client + "'";
                        String querry_has_bon1_temp = "SELECT CODE_CLIENT FROM BON1_TEMP WHERE IS_EXPORTED = 0 AND CODE_CLIENT = '" + clients.get(position).code_client + "'";
                        if(controller.check_if_has_bon(querry_has_bon1) || controller.check_if_has_bon(querry_has_bon1_temp)){
                            // you can't delete this client
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention. !")
                                    .setContentText("Il exist des bons créer avec ce client")
                                    .show();
                        }else {
                            new SweetAlertDialog(ActivityClients.this, SweetAlertDialog.NORMAL_TYPE)
                                    .setTitleText("Suppression")
                                    .setContentText("Voulez-vous vraiment supprimer le client " + clients.get(position).client + " ?!")
                                    .setCancelText("Anuuler")
                                    .setConfirmText("Supprimer")
                                    .showCancelButton(true)
                                    .setCancelClickListener(Dialog::dismiss)
                                    .setConfirmClickListener(sDialog -> {

                                        controller.delete_client(clients.get(position).code_client);

                                        setRecycle("");
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

        final SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("Rechercher");

//////////////////////////////////////////////////////////////////////
///    ENLEVER LES COMENTAIRES POUR ACTIVER L'OPTION DE RECHERCHE   ///
//////////////////////////////////////////////////////////////////////

        menu.add(Menu.NONE,Menu.NONE,0,"Search")
                .setIcon(R.mipmap.ic_recherche)
                .setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // final Context cntx = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                setRecycle(newText);

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
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        if(item.getItemId() == R.id.map){
            if ((ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(ActivityClients.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3232);
                ActivityCompat.requestPermissions(ActivityClients.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3232);
            }else{
                startActivity(new Intent(ActivityClients.this, ActivityMaps.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }else if(item.getItemId() == R.id.new_client){
            FragmentNewEditClient fragmentnewclient = new FragmentNewEditClient();
            fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext(), "NEW_CLIENT", null);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound){
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }


}

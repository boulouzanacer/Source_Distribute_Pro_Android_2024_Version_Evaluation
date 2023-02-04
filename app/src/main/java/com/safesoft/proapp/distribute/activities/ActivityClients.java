package com.safesoft.proapp.distribute.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.safesoft.proapp.distribute.adapters.RecyclerAdapter;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class ActivityClients extends AppCompatActivity implements RecyclerAdapter.ItemClick{

    private static int REQUEST_ACTIVITY_NEW_CLIENT = 4000;
    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    ArrayList<PostData_Client> clients;
    DATABASE controller;
    private  MediaPlayer mp;
    private EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients);

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List Clients");

    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client);
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
        adapter = new RecyclerAdapter(this, getItems(text_search));
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<PostData_Client> getItems(String qqry) {
        if(qqry.length() > 0){
            clients = new ArrayList<>();
            String querry = "SELECT * FROM Client WHERE CODE_CLIENT LIKE '%"+qqry+"%' OR CLIENT LIKE '%"+qqry+"%' OR TEL LIKE '%"+qqry+"%' ORDER BY CLIENT";
            // querry = "SELECT * FROM Events";
            clients = controller.select_clients_from_database(querry);
        }else {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM Client ORDER BY CLIENT";
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
            startActivity(new Intent(ActivityClients.this, ActivityMaps.class));
        }
        if(item.getItemId() == R.id.new_client){
            FragmentNewClient fragmentnewclient = new FragmentNewClient();
            fragmentnewclient.showDialogbox(ActivityClients.this, getBaseContext());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
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

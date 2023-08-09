package com.safesoft.proapp.distribute.activities.fournisseur;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.client.ActivityClientDetail;
import com.safesoft.proapp.distribute.activities.client.TSPActivityMaps;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClients;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterFournisseurs;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ActivityFournisseurs extends AppCompatActivity implements RecyclerAdapterFournisseurs.ItemClick{

    private static final int REQUEST_ACTIVITY_NEW_CLIENT = 4000;
    RecyclerView recyclerView;
    RecyclerAdapterFournisseurs adapter;
    ArrayList<PostData_Fournisseur> fournisseurs;
    DATABASE controller;
    private  MediaPlayer mp;
    private EventBus bus;
    private TextView nbr_fournisseur;

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
        getSupportActionBar().setTitle("List Fournisseurs");

    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client);
        nbr_fournisseur = (TextView) findViewById(R.id.list_client_nbr_client);
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
        adapter = new RecyclerAdapterFournisseurs(this, getItems(text_search));
        recyclerView.setAdapter(adapter);
        nbr_fournisseur.setText("Nombre de fournisseur : " + fournisseurs.size());

    }

    public ArrayList<PostData_Fournisseur> getItems(String qqry) {
        if(qqry.length() > 0){
            fournisseurs = new ArrayList<>();
            String querry = "SELECT * FROM FOURNIS WHERE CODE_FRS LIKE '%"+qqry+"%' OR FOURNIS LIKE '%"+qqry+"%' OR TEL LIKE '%"+qqry+"%' ORDER BY FOURNIS";
            // querry = "SELECT * FROM Events";
            fournisseurs = controller.select_fournisseurs_from_database(querry);
        }else {
            fournisseurs = new ArrayList<>();
            String querry = "SELECT * FROM FOURNIS ORDER BY FOURNIS";
            // querry = "SELECT * FROM Events";
            fournisseurs = controller.select_fournisseurs_from_database(querry);
        }

        return fournisseurs;
    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent){
        setRecycle("");
    }

    @Override
    public void onClick(View v, int position) {

        if(v.getId() == R.id.item_root){
            Sound(R.raw.beep);
            /*Intent intent = new Intent(ActivityFournisseurs.this, ActivityClientDetail.class);

            // intent.putExtra("CLIENT", clients.get(position).client);
            intent.putExtra("CODE_FRS", fournisseurs.get(position).code_frs);
            // intent.putExtra("TEL", clients.get(position).tel);
            // intent.putExtra("LATITUDE", clients.get(position).latitude);
            // intent.putExtra("LONGITUDE", clients.get(position).longitude);
            // intent.putExtra("ADRESSE", clients.get(position).adresse);
            //  intent.putExtra("MODE_TARIF", clients.get(position).mode_tarif);
            //  intent.putExtra("ACHAT", clients.get(position).achat_montant);
            //  intent.putExtra("VERSER", clients.get(position).verser_montant);
            //  intent.putExtra("SOLDE", clients.get(position).solde_montant);

            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_fournisseur, menu);

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

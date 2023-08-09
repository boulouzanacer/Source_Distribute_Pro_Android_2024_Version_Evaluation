package com.safesoft.proapp.distribute.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.client.ActivityClientDetail;
import com.safesoft.proapp.distribute.activities.client.ActivityClients;
import com.safesoft.proapp.distribute.activities.client.TSPActivityMaps;
import com.safesoft.proapp.distribute.activities.commande_vente.ActivityOrderClient;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.activities.vente.ActivitySale;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClients;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterRouting;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;
import com.safesoft.proapp.distribute.fragments.FragmentSelectClient;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityRouting extends AppCompatActivity implements RecyclerAdapterRouting.ItemClick, RecyclerAdapterRouting.ItemLongClick{
    RecyclerView recyclerView;
    RecyclerAdapterRouting adapter;
    ArrayList<PostData_Client> clients;
    DATABASE controller;
    private EventBus bus;
    private final String PREFS = "ALL_PREFS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);

        controller = new DATABASE(this);
        bus = EventBus.getDefault();
        // Register as a subscriber
        bus.register(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Clients à visité");


    }

    private void initViews() {

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_client);

    }

    @Override
    protected void onStart() {

        initViews();

        setRecycle();

        super.onStart();
    }



    private void setRecycle() {

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterRouting(this, getItems());
        recyclerView.setAdapter(adapter);

    }

    public ArrayList<PostData_Client> getItems() {

        clients = new ArrayList<>();String querry = "SELECT * FROM ROUTING ORDER BY RECORDID";
        clients = controller.select_routing_from_database(querry);

        return clients;
    }


    @Subscribe
    public void onClientSelected(SelectedClientEvent clientEvent){
        if(controller.insert_into_routing(clientEvent.getClient())){
            setPathValue(false);
        }
        setRecycle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_routing, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        if(item.getItemId() == R.id.map){
            if ((ContextCompat.checkSelfPermission(ActivityRouting.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(ActivityRouting.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityRouting.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3232);
                ActivityCompat.requestPermissions(ActivityRouting.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3232);
            }else{
                //  https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
                Intent tsp_intent = new Intent(ActivityRouting.this, TSPActivityMaps.class);
                startActivity(tsp_intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        }

        if(item.getItemId() == R.id.add_client){
            showListClient();
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showListClient()
    {
        // Initialize activity
        Activity activity;
        // define activity of this class//
        activity = ActivityRouting.this;
        FragmentSelectClient fragmentSelectClient = new FragmentSelectClient();
        fragmentSelectClient.showDialogbox(activity, ActivityRouting.this, "FROM_ROUTING");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        bus.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v, int position) {

    }

    @Override
    public void onLongClick(View v, int position) {
        if(v.getId() == R.id.item_root){

            new SweetAlertDialog(ActivityRouting.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Suppression")
                    .setContentText("Voulez-vous vraiment supprimer le client ( " + clients.get(position).client + " ) ?")
                    .setCancelText("Non")
                    .setConfirmText("Oui")
                    .showCancelButton(true)
                    .setCancelClickListener(Dialog::dismiss)
                    .setConfirmClickListener(sDialog -> {
                        try{

                            if(controller.delete_client_from_routing(clients.get(position).code_client)){
                                setPathValue(false);
                            }
                            setRecycle();

                        }catch (Exception e){
                            new SweetAlertDialog(ActivityRouting.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Attention!")
                                    .setContentText("problème lors de suppression du client : " + e.getMessage())
                                    .show();
                        }
                        sDialog.dismiss();
                    }).show();
        }
    }

    private void setPathValue(boolean value){
        SharedPreferences.Editor editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();
        editor.putBoolean("HAS_PATH",  value);
        editor.apply();
    }


}
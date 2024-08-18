package com.safesoft.proapp.distribute.activities.transfert;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.MenuItem;
import android.view.View;

import com.safesoft.proapp.distribute.adapters.RecyclerAdapterTransfert1;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


public class ActivityTransferts extends AppCompatActivity implements RecyclerAdapterTransfert1.ItemClick, RecyclerAdapterTransfert1.ItemLongClick {

    RecyclerView recyclerView;
    RecyclerAdapterTransfert1 adapter;
    ArrayList<PostData_Transfer1> transfert1s;
    ArrayList<PostData_Transfer1> test;

    ArrayList<PostData_Transfer2> transfers2;
    PostData_Transfer1 postData_transfer1;
    DATABASE controller;

    private MediaPlayer mp;
    private final String PREFS = "ALL_PREFS";
    Boolean printer_mode_integrate = true;

    private NumberFormat nf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer1);

        controller = new DATABASE(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bons de Transferts");

        initViews();

        setRecycle();


    }

    @Override
    protected void onStart() {

        setRecycle();

        SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
        printer_mode_integrate = prefs1.getString("PRINTER_CONX", "INTEGRATE").equals("INTEGRATE");

        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("##,##0.00");

        super.onStart();
    }

    private void initViews() {

        recyclerView = findViewById(R.id.recycler_view_transfert1);
    }

    private void setRecycle() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapterTransfert1(this, getItems());
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<PostData_Transfer1> getItems() {
        transfert1s = new ArrayList<>();

        String querry = "SELECT * FROM TRANSFERT1 ORDER BY DATE_BON DESC";
        // querry = "SELECT * FROM Events";
        transfert1s = controller.select_transfer1_from_database(querry);

        return transfert1s;
    }

    @Override
    public void onClick(View v, int position) {

        Sound(R.raw.beep);
        Intent intent = new Intent(ActivityTransferts.this, ActivityTransfert.class);
        intent.putExtra("NUM_BON", transfert1s.get(position).num_bon);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    @Override
    public void onLongClick(View v, final int position) {


        final CharSequence[] items = {"Imprimer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.blue_circle_24);
        builder.setTitle("Choisissez une action");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Print_bon(transfert1s.get(position).num_bon);
                }
            }
        });
        builder.show();

    }

    protected void Print_bon(String num_bon) {
        transfers2 = new ArrayList<>();
        transfert1s = new ArrayList<PostData_Transfer1>();


        transfert1s = controller.select_transfer1_from_database("SELECT " +
                "TRANSFERT1.NUM_BON, " +
                "TRANSFERT1.DATE_BON, " +

                "TRANSFERT1.NOM_DEPOT_SOURCE, " +
                "TRANSFERT1.NOM_DEPOT_DEST " +


                "FROM TRANSFERT1 " +

                "WHERE TRANSFERT1.NUM_BON = '" + num_bon + "'");
        transfers2 = controller.select_transfer2_from_database("SELECT " +

                "TRANSFERT2.PRODUIT, " +
                "TRANSFERT2.QTE " +

                "FROM TRANSFERT2 " +

                "WHERE TRANSFERT2.NUM_BON = '" + num_bon + "'");


        if (printer_mode_integrate) {

        }
    }


}

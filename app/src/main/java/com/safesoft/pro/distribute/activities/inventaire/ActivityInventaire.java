package com.safesoft.pro.distribute.activities.inventaire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.adapters.RecyclerAdapterInv1;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Bon1;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.postData.PostData_Inv1;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ActivityInventaire extends AppCompatActivity implements RecyclerAdapterInv1.ItemClick , RecyclerAdapterInv1.ItemLongClick{

  RecyclerView recyclerView;
  RecyclerAdapterInv1 adapter;
  ArrayList<PostData_Inv1> inv1s;
  DATABASE controller;

  private MediaPlayer mp;


  private PostData_Bon1 bon1_print;
  private ArrayList<PostData_Bon2> bon2_print;

  private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
  private String CODE_DEPOT, CODE_VENDEUR;

  private NumberFormat nf;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_inventaire);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Les inventaires");
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    controller = new DATABASE(this);

    initViews();

  }

  private void initViews() {

    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

    ///////////////////
    SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
    CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
    CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");
  }

  @Override
  protected void onStart() {

    setRecycle();

    // Declare US print format
    nf = NumberFormat.getInstance(Locale.US);
    ((DecimalFormat) nf).applyPattern("##,##0.00");

    super.onStart();
  }

  private void setRecycle() {
    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new RecyclerAdapterInv1(this, getItems());
    recyclerView.setAdapter(adapter);
  }


  public ArrayList<PostData_Inv1> getItems() {
    inv1s = new ArrayList<>();
    inv1s.clear();

    String querry = "";
    if(CODE_DEPOT.equals("000000")){
      querry = "SELECT * FROM Inventaires1";
    }else{
      querry = "SELECT * FROM Inventaires1 WHERE CODE_DEPOT = '" + CODE_DEPOT+"'";
    }

    // querry = "SELECT * FROM Events";
    inv1s = controller.select_list_inventaire_from_database(querry);

    return inv1s;
  }


  @Override
  public void onClick(View v, int position) {

    Sound(R.raw.beep);
    Intent editIntent = new Intent(ActivityInventaire.this, ActivityInventaireDetails.class);
    editIntent.putExtra("NUM_INV", inv1s.get(position).num_inv);
    editIntent.putExtra("NOM_INV", inv1s.get(position).nom_inv);
    startActivity(editIntent);
  }


  @Override
  public void onLongClick(View v, final int position) {

    final CharSequence[] items = {"Supprimer", "Exporter" };

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setIcon(R.drawable.selectiondialogs_default_item_icon);
    builder.setTitle("Choisissez une action");
    builder.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int item) {
        switch (item){
          case 0:
            new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("Supprission")
                    .setContentText("Voulez-vous vraiment supprimer l'inventaire " + inv1s.get(position).nom_inv + " ?!")
                    .setCancelText("Anuuler")
                    .setConfirmText("Supprimer")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                      }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                      @Override
                      public void onClick(SweetAlertDialog sDialog) {

                        if(!controller.delete_inventaire_group(inv1s.get(position).num_inv)){
                          new SweetAlertDialog(ActivityInventaire.this, SweetAlertDialog.ERROR_TYPE)
                                  .setTitleText("Erreur...")
                                  .setContentText("Erreur suprssion de l'inventaire ! ")
                                  .show();
                        }
                        setRecycle();

                        sDialog.dismiss();
                      }
                    })
                    .show();

            break;
          case 1:
            //Print_bon(bon1s.get(position).num_bon);
            Toast.makeText(ActivityInventaire.this, "Cette option est en cours de developpement !", Toast.LENGTH_SHORT).show();
            break;
        }
      }
    });
    builder.show();


  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_inventaire, menu);

    // return true so that the menu pop up is opened
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();
    }else if(item.getItemId() == R.id.new_inventaire){
      startActivityForResult(new Intent(ActivityInventaire.this, ActivityNewInventory.class), 5000);
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

    super.onDestroy();
  }

}

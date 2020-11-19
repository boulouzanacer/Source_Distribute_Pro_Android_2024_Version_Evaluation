package com.safesoft.pro.distribute.activities.vente;

import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.safesoft.pro.distribute.adapters.ListViewAdapterBon2;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Achat2;
import com.safesoft.pro.distribute.postData.PostData_Bon2;
import com.safesoft.pro.distribute.R;

import java.util.ArrayList;

public class ActivityBon2 extends AppCompatActivity {

  private ArrayList<PostData_Bon2> arrayOfBon2;
  private ListViewAdapterBon2 Bon2Adapter;
  private String NUM_BON;
  private DATABASE controller;

  private MediaPlayer mp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bon2);

    controller = new DATABASE(this);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    NUM_BON = getIntent().getStringExtra("NUM_BON");
    getSupportActionBar().setTitle("Bon de vente : " + NUM_BON);
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));
  }

  @Override
  protected void onStart() {
    initView();

    super.onStart();
  }

  public void initView(){

    // Create the adapter to convert the array to views
    Bon2Adapter = new ListViewAdapterBon2(this, R.layout.transfert2_items, getBon2());

    // Attach the adapter to a ListView
    ListView listView = (ListView) findViewById(R.id.listview);
    listView.setAdapter(Bon2Adapter);
  }

  protected ArrayList<PostData_Bon2> getBon2(){
    arrayOfBon2 = new ArrayList<>();
    arrayOfBon2 = controller.select_bon2_from_database("" +
            "SELECT " +
            "Bon2.RECORDID, " +
            "Bon2.CODE_BARRE, " +
            "Bon2.NUM_BON, " +
            "Bon2.PRODUIT, " +
            "Bon2.QTE, " +
            "Bon2.QTE, " +
            "Bon2.PV_HT, " +


            "Bon2.TVA, " +
            "Bon2.CODE_DEPOT, " +
            "Bon2.PA_HT, " +
            "Produit.STOCK " +
            "FROM Bon2 " +
            "INNER JOIN " +
            "Produit ON (Bon2.CODE_BARRE = Produit.CODE_BARRE) " +
            "WHERE Bon2.NUM_BON = '" + NUM_BON+ "'" );
    return arrayOfBon2;
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
  }

  public void Sound(int SourceSound){
    mp = MediaPlayer.create(this, SourceSound);
    mp.start();
  }
}

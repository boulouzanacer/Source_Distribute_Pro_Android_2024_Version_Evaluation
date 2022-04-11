package com.safesoft.proapp.distribute.activities.inventaire;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Inv1;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityNewInventory extends AppCompatActivity {

  private com.emmasuzuki.easyform.EasyTextInputLayout EdNInventaire;
  private SingleDateAndTimePicker date_picker;
  private Button Ajouter;
  private DATABASE controller;

  private MediaPlayer mp;

  private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";

  private String CODE_DEPOT, CODE_VENDEUR;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_inventory);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Nouveau inventaire");

    controller = new DATABASE(this);
    initViews();
  }
  private void initViews() {

    //EditText
    EdNInventaire = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.nom_inventaire_edittext);

    date_picker = (SingleDateAndTimePicker) findViewById(R.id.date_picker);

    //Button
    Ajouter = (Button) findViewById(R.id.sender);

    ///////////////////
    SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
    CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
    CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");

  }

  public void onClickButton(View v){
    if(EdNInventaire.getEditText().getText().length() <1){
      EdNInventaire.setError("Nom inventaire est obligatoire");
      return;
    }

    PostData_Inv1 inv1 = new PostData_Inv1();

    inv1.nom_inv = EdNInventaire.getEditText().getText().toString();
    SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    inv1.date_inv = df_show.format(date_picker.getDate());

    Calendar c = Calendar.getInstance();
    inv1.heure_inv = sdf.format(c.getTime());

    Boolean state_insert_inventaire = false;
    if(!CODE_DEPOT.equals("000000")){
      state_insert_inventaire = controller.Insert_into_inventaire1(inv1.date_inv, inv1.heure_inv, inv1.nom_inv, CODE_DEPOT);
    }else{
      state_insert_inventaire = controller.Insert_into_inventaire1(inv1.date_inv, inv1.heure_inv, inv1.nom_inv, null);
    }

    if(state_insert_inventaire){
      EdNInventaire.getEditText().setText("");
      Crouton.makeText(ActivityNewInventory.this, "Inventaire bien ajouté", Style.INFO).show();

    }else{
      Crouton.makeText(ActivityNewInventory.this, "Problème insertion", Style.ALERT).show();
    }


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
    Sound();
    super.onBackPressed();
  }

  public void Sound(){
    mp = MediaPlayer.create(this, R.raw.back);
    mp.start();
  }
}

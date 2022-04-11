package com.safesoft.proapp.distribute.activities.achats;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Achat1;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityNewAchat extends AppCompatActivity {

  private com.emmasuzuki.easyform.EasyTextInputLayout EdNAchat;
  private SingleDateAndTimePicker date_picker;
  private Button Ajouter;
  private DATABASE controller;

  private MediaPlayer mp;

  private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";

  private String CODE_DEPOT, CODE_VENDEUR;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_new_achat);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("Nouvelle Achat");
    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
            .getColor(R.color.black)));

    controller = new DATABASE(this);
    initViews();
  }
  private void initViews() {

    //EditText
    EdNAchat = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.nom_inventaire_edittext);

    date_picker = (SingleDateAndTimePicker) findViewById(R.id.date_picker);

    //Button
    Ajouter = (Button) findViewById(R.id.sender);

    ///////////////////
    SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
    CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
    CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");

  }

  public void onClickButton(View v){
    if(EdNAchat.getEditText().getText().length() <1){
      EdNAchat.setError("Nom d'achat est obligatoire");
      return;
    }

    PostData_Achat1 achat1 = new PostData_Achat1();

    achat1.nom_achat = EdNAchat.getEditText().getText().toString();
    SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    achat1.date_achat = df_show.format(date_picker.getDate());

    Calendar c = Calendar.getInstance();
    achat1.heure_achat = sdf.format(c.getTime());

    Boolean state_insert_achat = false;
    if(!CODE_DEPOT.equals("000000")){
      state_insert_achat = controller.Insert_into_achat1(achat1.date_achat, achat1.heure_achat, achat1.nom_achat, CODE_DEPOT);
    }else{
      state_insert_achat = controller.Insert_into_achat1(achat1.date_achat, achat1.heure_achat, achat1.nom_achat, null);
    }

    if(state_insert_achat){
      EdNAchat.getEditText().setText("");
      Crouton.makeText(ActivityNewAchat.this, "Achat bien ajouté", Style.INFO).show();
      //onBackPressed();
    }else{
      Crouton.makeText(ActivityNewAchat.this, "Problème insertion", Style.ALERT).show();
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

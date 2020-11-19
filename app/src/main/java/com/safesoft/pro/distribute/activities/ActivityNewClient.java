package com.safesoft.pro.distribute.activities;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.safesoft.pro.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.postData.PostData_Client;
import com.safesoft.pro.distribute.R;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

public class ActivityNewClient extends AppCompatActivity {

    private com.emmasuzuki.easyform.EasyTextInputLayout EdClient, EdAdresse, EdPhone, EdRc, EdIf, EdRib;
    private Button Ajouter;
    private DATABASE controller;

    private MediaPlayer mp;

    private EventBus bus;
    private SelectedClientEvent eventAddClient = null;

    private String PARAMS_PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
    private String PREFS_AUTRE = "ConfigAutre";

    private String CODE_DEPOT, CODE_VENDEUR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_client);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nouveau client");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        controller = new DATABASE(this);
        initViews();

        bus = EventBus.getDefault();
    }
    private void initViews() {

        //EditText
        EdClient = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.nom_client_check_edittext);
        EdAdresse = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.adresse_client_check_edittext);
        EdPhone = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.phone_client_check_edittext);
        EdRc = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.rc_edittext);
        EdIf = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.if_edittext);
        EdRib = (com.emmasuzuki.easyform.EasyTextInputLayout) findViewById(R.id.rib_edittext);



        //Button
        Ajouter = (Button) findViewById(R.id.sender);

        ///////////////////
        SharedPreferences prefs2 = getSharedPreferences(PARAMS_PREFS_CODE_DEPOT, MODE_PRIVATE);
        CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
        CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");

    }

    public void onClickButton(View v){
        if(EdClient.getEditText().getText().length() <1){
            EdClient.setError("Nom client est obligatoire");
            return;
        }

        if(EdAdresse.getEditText().getText().length() <1){
            EdAdresse.setError("Adresse client est obligatoire");
            return;
        }

        if(EdPhone.getEditText().getText().length() <1){
            EdPhone.setError("Numéro de tele est obligatoire");
            return;
        }
/*
        if(EdRc.getEditText().getText().length() <1){
            EdRc.setError("Numéro registre commerce est obligatoire");
            return;
        }

        if(EdIf.getEditText().getText().length() <1){
            EdIf.setError("Numéro d'identification fiscal est obligatoire");
            return;
        }

        if(EdRib.getEditText().getText().length() <1){
            EdRib.setError("Numéro de RIB est obligatoire");
            return;
        }
*/

        PostData_Client new_client = new PostData_Client();

        if(CODE_DEPOT.equals("000000")){
            new_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + CODE_VENDEUR+"";

        }else{
            new_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + CODE_DEPOT+"";

        }
        new_client.client = EdClient.getEditText().getText().toString();
        new_client.adresse = EdAdresse.getEditText().getText().toString();
        SharedPreferences prefs = getSharedPreferences(PREFS_AUTRE, MODE_PRIVATE);
        if(prefs.getString("PV_ID", "PV1").equals("PV1")){
            new_client.mode_tarif = "1";
        }else if(prefs.getString("PV_ID", "PV1").equals("PV2")){
            new_client.mode_tarif = "2";
        }else{
            new_client.mode_tarif = "3";
        }
        new_client.tel = EdPhone.getEditText().getText().toString();
        new_client.rc = EdRc.getEditText().getText().toString();
        new_client.ifiscal = EdIf.getEditText().getText().toString();
        new_client.rib = EdRib.getEditText().getText().toString();


        //Insert client into database,
        Boolean state_insert_client = controller.Insert_into_client(new_client);
        if(state_insert_client){
            EdClient.getEditText().setText("");
            EdAdresse.getEditText().setText("");
            EdPhone.getEditText().setText("");

            EdRc.getEditText().setText("");
            EdIf.getEditText().setText("");
            EdRib.getEditText().setText("");

            Crouton.makeText(ActivityNewClient.this, "Client bien ajouté", Style.INFO).show();

            eventAddClient = new SelectedClientEvent(new_client);

            bus.post(eventAddClient);

            finish();

        }else{
            Crouton.makeText(ActivityNewClient.this, "Problème insertion", Style.ALERT).show();
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

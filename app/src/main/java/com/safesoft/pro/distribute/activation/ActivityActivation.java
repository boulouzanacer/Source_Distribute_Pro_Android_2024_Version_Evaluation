package com.safesoft.pro.distribute.activation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.splashscreen.splashScreen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityActivation extends AppCompatActivity {

    private static final int READ_PHONE_STATE = 5;
    private MediaPlayer mp;
    private String PREFS_ACTIVATION = "PREFES_ACTIVATION";

    private ImageButton btnActiver;
    private ProgressDialog progressDialog;
    private boolean state_a = false;
    private boolean phone_read_state = false;

    private Animation animation;
    private Animation animation1;

    TelephonyManager telephonyManager;

    private EasyTextInputLayout _code_client, _nom_client, _numero_tele, _revendeur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activation);


        requestPermission();

        telephonyManager = (TelephonyManager) getSystemService(ActivityActivation.TELEPHONY_SERVICE);
        if (phone_read_state) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                getSupportActionBar().setSubtitle("Activation");
                return;
            }
            getSupportActionBar().setSubtitle("Activation ( MEI : " + telephonyManager.getDeviceId() + " )");
        } else {
            getSupportActionBar().setSubtitle("Activation");
        }

        getSupportActionBar().setTitle("Distribute (Version non Enregistrée)");
        btnActiver = (ImageButton) findViewById(R.id.activer);
        // notice = (TextView) findViewById(R.id.notice);


        _code_client = (EasyTextInputLayout) findViewById(R.id.code_client_check_edittext);
        _code_client.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("0")) {
                    if (s.length() < 6) {
                        _code_client.setError("Enter un valid code client");
                    }
                } else {
                    _code_client.setError(null);
                }
            }
        });

        _nom_client = (EasyTextInputLayout) findViewById(R.id.nom_client_check_edittext);
        _nom_client.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().contains(".")) {
                    _nom_client.getEditText().setText(s.toString().replace(".", ""));
                }
            }
        });

        _numero_tele = (EasyTextInputLayout) findViewById(R.id.phone_client_check_edittext);
        _revendeur = (EasyTextInputLayout) findViewById(R.id.revendeur_check_edittext);

    }


    @Override
    protected void onStart() {
        animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_button_activation);
        animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_right_button_activation);

        startAnimation();

        if(phone_read_state){
            sendRequest("gg");
        }


        super.onStart();
    }

    protected void startAnimation() {
        btnActiver.startAnimation(animation);
//        notice.startAnimation(animation1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_PHONE_STATE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            phone_read_state = true;
            getSupportActionBar().setSubtitle("Activation ( MEI : " + telephonyManager.getDeviceId() + " )");
            sendRequest("gg");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onClickButton(View v) {
        switch (v.getId()) {
            case R.id.activer:
                if (!state_a) {
                    if (check_fields()) {
                        sendRequest("gg");
                    }
                } else {
                    startActivity(new Intent(getApplicationContext(),splashScreen.class));


                }

                break;
        }
    }

    protected boolean check_fields() {
        boolean check = true;

        if (_code_client.getEditText().getText().length() <= 0) {
            check = false;
            _code_client.getEditText().setError("Code client est obligatoire");
        } else if (_code_client.getEditText().getText().length() != 6) {
            if (_code_client.getEditText().getText().length() == 1) {
                if (!_code_client.getEditText().getText().toString().equals("0")) {
                    check = false;
                    _code_client.getEditText().setError("Le code client doit contient que 6 digits ou la valuer 0");
                }
            } else {
                check = false;
                _code_client.getEditText().setError("Le code client doit contient que 6 digits ou la valuer 0");
            }

        }

        if (_nom_client.getEditText().getText().length() <= 0) {
            check = false;
            _nom_client.getEditText().setError("Nom client est obligatoire");
        }

        if (_numero_tele.getEditText().getText().length() <= 0) {
            check = false;
            _numero_tele.getEditText().setError("Numéro du téléphone est obligatoire");
        }

        /*
        if(_revendeur.getEditText().getText().length() <= 0){
            check = false;
            _revendeur.getEditText().setError("Revendeur est obligatoire");
        }
        */

        return check;
    }

    public void sendRequest(String request) {
        final String _msg = request;
        try {
            if (_msg.length() > 0) {

                progressDialog = ProgressDialog.show(ActivityActivation.this, getString(R.string.leading_activation), getString(R.string.activation_wait));

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (hasActiveInternetConnection()) {
                            try {
                                NetClient nc = new NetClient("105.96.9.62", 2578); // ip adress and port

                                if (ActivityCompat.checkSelfPermission(ActivityActivation.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
/*
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
                                                progressDialog.dismiss();
                                                progressDialog = null;
                                            }
                                            Sound(R.raw.back);
                                            Crouton.makeText(ActivityActivation.this, "Permission de lire IMEI de téléphone n'est encore garanti",Style.ALERT).show();
                                        }
                                    });
                                    */
                                    return;
                                }
                                String message_online = "DISTRIBUTION." + telephonyManager.getDeviceId() + "." + _code_client.getEditText().getText() + "." + _nom_client.getEditText().getText() + "." + _numero_tele.getEditText().getText() + ".SAFESOFT";
                                nc.sendDataWithString(message_online);
                                final String r = nc.receiveDataFromServer();
                                if(r.equals("NON")){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
                                                progressDialog.dismiss();
                                                progressDialog = null;
                                            }
                                            Sound(R.raw.back);
                                            Crouton.makeText(ActivityActivation.this, "Demande envoyée",Style.INFO).show();
                                        }
                                    });
                                }else{
                                    Sound(R.raw.beep);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String token = "";
                                            String nom_client = "";
                                            String telephone = "";
                                            token = r.substring(0,r.indexOf(";"));
                                            if(token.equals("OK")){
                                                nom_client = r.substring(r.indexOf(";")+1, r.lastIndexOf(";"));
                                                telephone = r.substring( r.lastIndexOf(";") + 1, r.length());
                                                Crouton.makeText(ActivityActivation.this, " Opération success \n pour le client  : "+ nom_client + " / " + telephone ,Style.CONFIRM).show();
                                                getSupportActionBar().setTitle("Distribute ( Version enregistrée )");

                                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_ACTIVATION, MODE_PRIVATE).edit();
                                                editor.putString("NOM_CLIENT", nom_client.toString());
                                                editor.putString("TELEPHONE", telephone.toString());
                                                editor.putBoolean("IS_ACTIVATED", true);
                                                // editor.putString("MEI", activity.getText().toString());
                                                editor.commit();
                                                state_a = true;

                                                btnActiver.setImageResource(R.mipmap.commencer);


                                                _code_client.getEditText().setEnabled(false);
                                                _nom_client.getEditText().setEnabled(false);
                                                _numero_tele.getEditText().setEnabled(false);
                                                _revendeur.getEditText().setEnabled(false);

                                                startAnimation();
                                            }else {
                                                Crouton.makeText(ActivityActivation.this, "Erreur dans la réponse !",Style.CONFIRM).show();
                                            }

                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
                                                progressDialog.dismiss();
                                                progressDialog = null;
                                            }
                                        }
                                    });
                                }

                                Log.e("TRACKKK", "==========> " + r);

                            }catch (Exception e){
                                Log.e("TRACKKK", "Server is down ");
                                Log.e("TRACKKK", e.getMessage());

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
                                            progressDialog.dismiss();
                                            progressDialog = null;
                                        }
                                        Sound(R.raw.back);
                                        Crouton.makeText(ActivityActivation.this, "Problème de Connexion Internt ou Réseau mobile",Style.ALERT).show();
                                    }
                                });
                            }
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
                                        progressDialog.dismiss();
                                        progressDialog = null;
                                    }
                                    Sound(R.raw.back);
                                    Crouton.makeText(ActivityActivation.this, "Problème de Connexion Internt ou Réseau mobile",Style.ALERT).show();
                                }
                            });
                        }
                    }
                }).start();

            }else
            {
                //On affiche un message d'erreur dans un Toast
                Crouton.makeText(ActivityActivation.this, "Demande non envoyé, requete vide !",Style.ALERT).show();
            }
        }catch (Exception e){
            Log.e("TRACKKK", "Error checking internet connection", e);
            Toast.makeText(this, "Problème dans l'envoie de la demande!", Toast.LENGTH_SHORT).show();
        }finally {

        }
    }

    public boolean hasActiveInternetConnection() {
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
            return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
        } catch (IOException e) {
            Log.e("TRACKKK", "Error checking internet connection"+ e.getMessage());
            return false;
        }
    }

    public void Sound(int SourceSound){
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }

    public void requestPermission(){

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECIEVE_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
            checkPermission = false;
        }else{
            checkPermission = true;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }
*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
            phone_read_state = false;
        }else{
            phone_read_state = true ;
        }
    }
}

package com.safesoft.proapp.distribute.activation;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.safesoft.proapp.distribute.MainActivity;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.splashscreen.splashScreen;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityActivation extends AppCompatActivity {

    private String deviceId2 = "";
    private String revendeur;
    TextView inforevenedeur1,inforevenedeur2,inforevenedeur3,inforevenedeur4;

    ImageView qrImage;
    private int code_activation;
    EditText code_activation_input;
    public static final String PREFS = "ALL_PREFS";

    //////////////ancien declaration ////////////
    private static String TAG = ActivityActivation.class.getSimpleName();

    private static final int READ_PHONE_STATE = 5;
    private MediaPlayer mp;
    //private String PREFS = "ALL_PREFS";

    private ImageButton btnActiver;
    private ProgressDialog progressDialog;
    private boolean state_a = false;
    private boolean phone_read_state = false;

    private Animation animation;
    private Animation animation1;

    TelephonyManager telephonyManager;

    private EasyTextInputLayout _code_client, _nom_client, _numero_tele, _revendeur;

    String deviceID = "";
    //////////////ancien declaration ////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_activation);

        //telephonyManager = (TelephonyManager) getSystemService(ActivityActivation.TELEPHONY_SERVICE);

        inforevenedeur1 = findViewById(R.id.InfoRevendeur1);
        inforevenedeur2 = findViewById(R.id.InfoRevendeur2);
        inforevenedeur3 = findViewById(R.id.InfoRevendeur3);
        inforevenedeur4 = findViewById(R.id.InfoRevendeur4);

        Intent intent = getIntent();
        deviceId2 = intent.getStringExtra(splashScreen.NUM_SERIE);
        revendeur = intent.getStringExtra(splashScreen.revendeur);
        info_revendeur2();

        TextView tv = findViewById(R.id.NUM_SERIAL);
        tv.setText(deviceId2);

        qrImage = findViewById(R.id.QR_CODE_IM);
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix matrix = writer.encode(deviceId2, BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(matrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
///////////////////////////////////  old activation//////////////
//        try {
//            deviceID = telephonyManager.getDeviceId();
//        } catch (Exception e) {
//            Log.d(TAG, "onCreate: " + e.getMessage());
//            deviceID = UniqueDeviceID.getUniqueID().substring(0, 15);
//        }
//        Log.d("device id", "onCreate: device ID is " + deviceID);
//
//        requestPermission();
//
//        if (phone_read_state) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                getSupportActionBar().setSubtitle("Activation");
//                return;
//            }
//            getSupportActionBar().setSubtitle("Activation ( MEI : " + deviceID + " )");
//        } else {
//            getSupportActionBar().setSubtitle("Activation");
//        }
//
//        getSupportActionBar().setTitle("Distribute (Version non Enregistrée)");
//        btnActiver = (ImageButton) findViewById(R.id.activer);
//        // notice = (TextView) findViewById(R.id.notice);
//
//        _code_client = (EasyTextInputLayout) findViewById(R.id.code_client_check_edittext);
//        _code_client.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!s.toString().equals("0")) {
//                    if (s.length() < 6) {
//                        _code_client.setError("Enter un valid code client");
//                    }
//                } else {
//                    _code_client.setError(null);
//                }
//            }
//        });
//
//        _nom_client = (EasyTextInputLayout) findViewById(R.id.nom_client_check_edittext);
//        _nom_client.getEditText().addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.toString().contains(".")) {
//                    _nom_client.getEditText().setText(s.toString().replace(".", ""));
//                }
//            }
//        });
//
//        _numero_tele = (EasyTextInputLayout) findViewById(R.id.phone_client_check_edittext);
//        _revendeur = (EasyTextInputLayout) findViewById(R.id.revendeur_check_edittext);
///////////////////////////////////  old activation//////////////

    }
    public void onClick_Btn_Activation(View view) {
        Button btn_start = findViewById(R.id.BTN_START);
        code_activation_input = findViewById(R.id.CODE_ACTIVATION);

        try {
            code_activation = Integer.parseInt(code_activation_input.getText().toString());
        }
        catch (Exception e)
        {
            code_activation = 0;
        }

        int i = 0;
        int o = deviceId2.length()+1;
        for(int t=0;t<= deviceId2.length()-1 ;t++){
            i = i + (int)deviceId2.charAt(t) * 47293 * o;
            o = o - 1;
        }
        
        if (code_activation == i) {
           // Toast.makeText(ActivityActivation.this, "Code d'activation correct",Toast.LENGTH_SHORT).show();
            Crouton.makeText(ActivityActivation.this, "Code d'activation correct", Style.INFO).show();
            btn_start.setVisibility(View.VISIBLE);  ///// 357272286 ///// 350889357
            saveData();
        } else {
            //Toast.makeText(ActivityActivation.this, "Code d'activation incorrect",Toast.LENGTH_SHORT).show();
            Crouton.makeText(ActivityActivation.this, "Code d'activation incorrect", Style.ALERT).show();
            ((EditText)findViewById(R.id.CODE_ACTIVATION)).setText("");
            btn_start.setVisibility(View.INVISIBLE);


        }
    }

    public void onClick_Btn_Start(View view) {
        startActivity(new Intent(ActivityActivation.this, MainActivity.class));
    }


//    @Override
//    protected void onStart() {
//        animation = AnimationUtils.loadAnimation(this, R.anim.slide_left_button_activation);
//        animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_right_button_activation);
//
//        startAnimation();
//
//        super.onStart();
//    }

//    protected void startAnimation() {
//        btnActiver.startAnimation(animation);
////        notice.startAnimation(animation1);
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == READ_PHONE_STATE) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            phone_read_state = true;
//            getSupportActionBar().setSubtitle("Activation ( MEI : " + deviceID + " )");
//            sendRequest("gg");
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

//    public void onClickButton(View v) {
//        switch (v.getId()) {
//            case R.id.activer:
//                if (!state_a) {
//                    if (check_fields()) {
//                        sendRequest("gg");
//                    }
//                } else {
//                    startActivity(new Intent(getApplicationContext(), splashScreen.class));
//
//
//                }
//
//                break;
//        }
//    }

//    protected boolean check_fields() {
//        boolean check = true;
//
//        if (_code_client.getEditText().getText().length() <= 0) {
//            check = false;
//            _code_client.getEditText().setError("Code client est obligatoire");
//        } else if (_code_client.getEditText().getText().length() != 6) {
//            if (_code_client.getEditText().getText().length() == 1) {
//                if (!_code_client.getEditText().getText().toString().equals("0")) {
//                    check = false;
//                    _code_client.getEditText().setError("Le code client doit contient que 6 digits ou la valuer 0");
//                }
//            } else {
//                check = false;
//                _code_client.getEditText().setError("Le code client doit contient que 6 digits ou la valuer 0");
//            }
//
//        }
//
//        if (_nom_client.getEditText().getText().length() <= 0) {
//            check = false;
//            _nom_client.getEditText().setError("Nom client est obligatoire");
//        }
//
//        if (_numero_tele.getEditText().getText().length() <= 0) {
//            check = false;
//            _numero_tele.getEditText().setError("Numéro du téléphone est obligatoire");
//        }
//
//        /*
//        if(_revendeur.getEditText().getText().length() <= 0){
//            check = false;
//            _revendeur.getEditText().setError("Revendeur est obligatoire");
//        }
//        */
//
//        return check;
//    }

//    public void sendRequest(String request) {
//        try {
//            if (request.length() > 0) {
//
//                progressDialog = ProgressDialog.show(ActivityActivation.this, getString(R.string.leading_activation), getString(R.string.activation_wait));
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (hasActiveInternetConnection()) {
//                            try {
//                                NetClient nc = new NetClient("105.96.9.62", 2578); // ip adress and port
//
//                                if (ActivityCompat.checkSelfPermission(ActivityActivation.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                                    /*runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
//                                                progressDialog.dismiss();
//                                                progressDialog = null;
//                                            }
//                                            Sound(R.raw.back);
//                                            Crouton.makeText(ActivityActivation.this, "Permission de lire IMEI de téléphone n'est encore garanti",Style.ALERT).show();
//                                        }
//                                    });*/
//                                    return;
//                                }
//                                String message_online = "DISTRIBUTION." + deviceID + "." + _code_client.getEditText().getText() + "." + _nom_client.getEditText().getText() + "." + _numero_tele.getEditText().getText() + ".SAFESOFT";
//                                nc.sendDataWithString(message_online);
//                                Log.d("TAG", "run: " + message_online);
//                                final String r = nc.receiveDataFromServer();
//                                Log.d("TAG", "run: " + r);
//                                if (r.equals("NON")) {
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
//                                                progressDialog.dismiss();
//                                                progressDialog = null;
//                                            }
//                                            Sound(R.raw.back);
//                                            Crouton.makeText(ActivityActivation.this, "Demande envoyée", Style.INFO).show();
//                                        }
//                                    });
//                                } else {
//                                    Sound(R.raw.beep);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            String token = "";
//                                            String nom_client = "";
//                                            String telephone = "";
//                                            token = r.substring(0, r.indexOf(";"));
//                                            if (token.equals("OK")) {
//                                                nom_client = r.substring(r.indexOf(";") + 1, r.lastIndexOf(";"));
//                                                telephone = r.substring(r.lastIndexOf(";") + 1, r.length());
//                                                Crouton.makeText(ActivityActivation.this, " Opération success \n pour le client  : " + nom_client + " / " + telephone, Style.CONFIRM).show();
//                                                getSupportActionBar().setTitle("Distribute ( Version enregistrée )");
//
//                                                SharedPreferences.Editor editor = getSharedPreferences(PREFS_ACTIVATION, MODE_PRIVATE).edit();
//                                                editor.putString("NOM_CLIENT", nom_client.toString());
//                                                editor.putString("TELEPHONE", telephone.toString());
//                                                editor.putBoolean("IS_ACTIVATED", true);
//                                                // editor.putString("MEI", activity.getText().toString());
//                                                editor.commit();
//                                                state_a = true;
//
//                                                btnActiver.setImageResource(R.mipmap.commencer);
//
//
//                                                _code_client.getEditText().setEnabled(false);
//                                                _nom_client.getEditText().setEnabled(false);
//                                                _numero_tele.getEditText().setEnabled(false);
//                                                _revendeur.getEditText().setEnabled(false);
//
//                                                startAnimation();
//                                            } else {
//                                                Crouton.makeText(ActivityActivation.this, "Erreur dans la réponse !", Style.CONFIRM).show();
//                                            }
//
//                                            if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
//                                                progressDialog.dismiss();
//                                                progressDialog = null;
//                                            }
//                                        }
//                                    });
//                                }
//
//                                Log.e("TRACKKK", "==========> " + r);
//
//                            } catch (Exception e) {
//                                Log.e("TRACKKK", "Server is down ");
//                                Log.e("TRACKKK", e.getMessage());
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
//                                            progressDialog.dismiss();
//                                            progressDialog = null;
//                                        }
//                                        Sound(R.raw.back);
//                                        Crouton.makeText(ActivityActivation.this, "Problème de Connexion Internt ou Réseau mobile", Style.ALERT).show();
//                                    }
//                                });
//                            }
//                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    if (progressDialog != null && !ActivityActivation.this.isFinishing()) {
//                                        progressDialog.dismiss();
//                                        progressDialog = null;
//                                    }
//                                    Sound(R.raw.back);
//                                    Crouton.makeText(ActivityActivation.this, "Problème de Connexion Internt ou Réseau mobile", Style.ALERT).show();
//                                }
//                            });
//                        }
//                    }
//                }).start();
//
//            } else {
//                //On affiche un message d'erreur dans un Toast
//                Crouton.makeText(ActivityActivation.this, "Demande non envoyé, requete vide !", Style.ALERT).show();
//            }
//        } catch (Exception e) {
//            Log.e("TRACKKK", "Error checking internet connection", e);
//            Toast.makeText(this, "Problème dans l'envoie de la demande!", Toast.LENGTH_SHORT).show();
//        } finally {
//
//        }
//    }

//    public boolean hasActiveInternetConnection() {
//        return true;
//    }
//
//    public void Sound(int SourceSound) {
//        mp = MediaPlayer.create(this, SourceSound);
//        mp.start();
//    }
//
//    public void requestPermission() {
//
//        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, RECIEVE_SMS);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCES_FINE_LOCATION);
//            checkPermission = false;
//        }else{
//            checkPermission = true;
//        }
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
//        }
//*/
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE);
//            phone_read_state = false;
//        } else {
//            phone_read_state = true;
//        }
//    }
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NUM_SERIE",deviceId2);
        editor.putInt("CODE_ACTIVATION",code_activation);
        editor.apply();
    }

    public void info_revendeur2() {
        inforevenedeur1 = findViewById(R.id.InfoRevendeur1);
        inforevenedeur2 = findViewById(R.id.InfoRevendeur2);
        inforevenedeur3 = findViewById(R.id.InfoRevendeur3);
        inforevenedeur4 = findViewById(R.id.InfoRevendeur4);

        if (revendeur.equals("SAFE SOFT"))  {
            inforevenedeur1.setText(R.string.INFO_SAFE_SOFT1);
            inforevenedeur2.setText(R.string.INFO_SAFE_SOFT2);
            inforevenedeur3.setText(R.string.INFO_SAFE_SOFT3);
            inforevenedeur4.setText(R.string.INFO_SAFE_SOFT4);
        }
        if (revendeur.equals("TIEMPO SOFT")) {
            inforevenedeur1.setText(R.string.INFO_TIEMPO_SOFT1);
            inforevenedeur2.setText(R.string.INFO_TIEMPO_SOFT2);
            inforevenedeur3.setText(R.string.INFO_TIEMPO_SOFT3);
            inforevenedeur4.setText(R.string.INFO_TIEMPO_SOFT4);
        }
        if (revendeur.equals("CHERRATA SOFT")) {
            inforevenedeur1.setText(R.string.INFO_CHERRATA_SOFT1);
            inforevenedeur2.setText(R.string.INFO_CHERRATA_SOFT2);
            inforevenedeur3.setText(R.string.INFO_CHERRATA_SOFT3);
            inforevenedeur4.setText(R.string.INFO_CHERRATA_SOFT4);
        }
        if (revendeur.equals("TECH POS")) {
            inforevenedeur1.setText(R.string.INFO_TECH_POS1);
            inforevenedeur2.setText(R.string.INFO_TECH_POS2);
            inforevenedeur3.setText(R.string.INFO_TECH_POS3);
            inforevenedeur4.setText(R.string.INFO_TECH_POS4);
        }
        if (revendeur.equals("GLOBAL TECH")) {
            inforevenedeur1.setText(R.string.INFO_GLOBAL_TECH1);
            inforevenedeur2.setText(R.string.INFO_GLOBAL_TECH2);
            inforevenedeur3.setText(R.string.INFO_GLOBAL_TECH3);
            inforevenedeur4.setText(R.string.INFO_GLOBAL_TECH4);
        }
        if (revendeur.equals("CAM PLUS")) {
            inforevenedeur1.setText(R.string.INFO_CAM_PLUS1);
            inforevenedeur2.setText(R.string.INFO_CAM_PLUS2);
            inforevenedeur3.setText(R.string.INFO_CAM_PLUS3);
            inforevenedeur4.setText(R.string.INFO_CAM_PLUS4);
        }

        if (revendeur.equals("TIFAWT TECHNOLOGIE")) {
            inforevenedeur1.setText(R.string.INFO_TIFAWT_TECHNOLOGIE1);
            inforevenedeur2.setText(R.string.INFO_TIFAWT_TECHNOLOGIE2);
            inforevenedeur3.setText(R.string.INFO_TIFAWT_TECHNOLOGIE3);
            inforevenedeur4.setText(R.string.INFO_TIFAWT_TECHNOLOGIE4);
        }
    }

}

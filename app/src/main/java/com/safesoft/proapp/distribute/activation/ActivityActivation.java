package com.safesoft.proapp.distribute.activation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.safesoft.proapp.distribute.MainActivity;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.splashscreen.splashScreen;
import com.safesoft.proapp.distribute.app.BaseApplication;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ActivityActivation extends AppCompatActivity {

    private String deviceId2 = "";
    private String revendeur;
    TextView inforevenedeur1, inforevenedeur2, inforevenedeur3, inforevenedeur4;

    ImageView qrImage;
    private int code_activation;
    EditText code_activation_input;
    public static final String PREFS = "ALL_PREFS";

    public ActivityActivation() {
    }
    //////////////ancien declaration ////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_activation);

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
            BitMatrix matrix = writer.encode(deviceId2, BarcodeFormat.QR_CODE, 350, 350);
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

    public void saveActivationData(boolean is_activated) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("APP_ACTIVATED", is_activated);
        editor.apply();
    }

    public void onClick_Btn_Activation(View view) {
        Button btn_start = findViewById(R.id.BTN_START);
        code_activation_input = findViewById(R.id.CODE_ACTIVATION);

        try {
            code_activation = Integer.parseInt(code_activation_input.getText().toString());
        } catch (Exception e) {
            code_activation = 0;
        }

        int i = 0;
        int o = deviceId2.length() + 1;
        for (int t = 0; t <= deviceId2.length() - 1; t++) {
            i = i + (int) deviceId2.charAt(t) * 47293 * o;
            o = o - 1;
        }

        if (code_activation == i) {
            // Toast.makeText(ActivityActivation.this, "Code d'activation correct",Toast.LENGTH_SHORT).show();
            Crouton.makeText(ActivityActivation.this, "Code d'activation correct", Style.INFO).show();
            btn_start.setVisibility(View.VISIBLE);  ///// 357272286 ///// 350889357
            saveData();
            saveActivationData(true);

        } else {
            //Toast.makeText(ActivityActivation.this, "Code d'activation incorrect",Toast.LENGTH_SHORT).show();
            Crouton.makeText(ActivityActivation.this, "Code d'activation incorrect", Style.ALERT).show();
            ((EditText) findViewById(R.id.CODE_ACTIVATION)).setText("");
            btn_start.setVisibility(View.INVISIBLE);
            saveActivationData(false);
        }
    }

    public void onClick_Btn_Start(View view) {
        startActivity(new Intent(ActivityActivation.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NUM_SERIE", deviceId2);
        editor.putInt("CODE_ACTIVATION", code_activation);
        editor.apply();
    }

    public void info_revendeur2() {
        inforevenedeur1 = findViewById(R.id.InfoRevendeur1);
        inforevenedeur2 = findViewById(R.id.InfoRevendeur2);
        inforevenedeur3 = findViewById(R.id.InfoRevendeur3);
        inforevenedeur4 = findViewById(R.id.InfoRevendeur4);

        //00
        if (revendeur.equals("SAFE SOFT")) {
            inforevenedeur1.setText(R.string.INFO_SAFE_SOFT1);
            inforevenedeur2.setText(R.string.INFO_SAFE_SOFT2);
            inforevenedeur3.setText(R.string.INFO_SAFE_SOFT3);
            inforevenedeur4.setText(R.string.INFO_SAFE_SOFT4);
        }
        //01
        if (revendeur.equals("COMPOS SOFT")) {
            inforevenedeur1.setText(R.string.INFO_COMPOS1);
            inforevenedeur2.setText(R.string.INFO_COMPOS2);
            inforevenedeur3.setText(R.string.INFO_COMPOS3);
            inforevenedeur4.setText(R.string.INFO_COMPOS4);
        }
        //02
        if (revendeur.equals("TIEMPO SOFT")) {
            inforevenedeur1.setText(R.string.INFO_TIEMPO_SOFT1);
            inforevenedeur2.setText(R.string.INFO_TIEMPO_SOFT2);
            inforevenedeur3.setText(R.string.INFO_TIEMPO_SOFT3);
            inforevenedeur4.setText(R.string.INFO_TIEMPO_SOFT4);
        }
        //03
        if (revendeur.equals("CHERRATA SOFT")) {
            inforevenedeur1.setText(R.string.INFO_CHERRATA_SOFT1);
            inforevenedeur2.setText(R.string.INFO_CHERRATA_SOFT2);
            inforevenedeur3.setText(R.string.INFO_CHERRATA_SOFT3);
            inforevenedeur4.setText(R.string.INFO_CHERRATA_SOFT4);
        }
        //04
        if (revendeur.equals("EASY SOFT")) {
            inforevenedeur1.setText(R.string.INFO_EASY_SOFT1);
            inforevenedeur2.setText(R.string.INFO_EASY_SOFT1);
            inforevenedeur3.setText(R.string.INFO_EASY_SOFT3);
            inforevenedeur4.setText(R.string.INFO_EASY_SOFT4);
        }
        //05
        if (revendeur.equals("MOON SOFT")) {
            inforevenedeur1.setText(R.string.INFO_MOON_SOFT1);
            inforevenedeur2.setText(R.string.INFO_MOON_SOFT2);
            inforevenedeur3.setText(R.string.INFO_MOON_SOFT3);
            inforevenedeur4.setText(R.string.INFO_MOON_SOFT4);
        }
        //06
        if (revendeur.equals("GLOBAL TECH")) {
            inforevenedeur1.setText(R.string.INFO_GLOBAL_TECH1);
            inforevenedeur2.setText(R.string.INFO_GLOBAL_TECH2);
            inforevenedeur3.setText(R.string.INFO_GLOBAL_TECH3);
            inforevenedeur4.setText(R.string.INFO_GLOBAL_TECH4);
        }

        //07
        if (revendeur.equals("DSX SYSTEME")) {
            inforevenedeur1.setText(R.string.INFO_POLYRAW1);
            inforevenedeur2.setText(R.string.INFO_POLYRAW2);
            inforevenedeur3.setText(R.string.INFO_POLYRAW3);
            inforevenedeur4.setText(R.string.INFO_POLYRAW4);
        }

        //08
        if (revendeur.equals("PRO SOLUTION")) {
            inforevenedeur1.setText(R.string.INFO_PROSOLUTION1);
            inforevenedeur2.setText(R.string.INFO_PROSOLUTION2);
            inforevenedeur3.setText(R.string.INFO_PROSOLUTION3);
            inforevenedeur4.setText(R.string.INFO_PROSOLUTION4);
        }

        //09
        if (revendeur.equals("SOFT SPACE")) {
            inforevenedeur1.setText(R.string.INFO_SOFTSPACE1);
            inforevenedeur2.setText(R.string.INFO_SOFTSPACE2);
            inforevenedeur3.setText(R.string.INFO_SOFTSPACE3);
            inforevenedeur4.setText(R.string.INFO_SOFTSPACE4);
        }

        //10
        if (revendeur.equals("TECH POS")) {
            inforevenedeur1.setText(R.string.INFO_TECH_POS1);
            inforevenedeur2.setText(R.string.INFO_TECH_POS2);
            inforevenedeur3.setText(R.string.INFO_TECH_POS3);
            inforevenedeur4.setText(R.string.INFO_TECH_POS4);
        }

        //11
        if (revendeur.equals("NDHL")) {
            inforevenedeur1.setText(R.string.INFO_NDHL1);
            inforevenedeur2.setText(R.string.INFO_NDHL2);
            inforevenedeur3.setText(R.string.INFO_NDHL3);
            inforevenedeur4.setText(R.string.INFO_NDHL4);
        }

        //12
        if (revendeur.equals("IBSMAX")) {
            inforevenedeur1.setText(R.string.INFO_IBSMAX1);
            inforevenedeur2.setText(R.string.INFO_IBSMAX2);
            inforevenedeur3.setText(R.string.INFO_IBSMAX3);
            inforevenedeur4.setText(R.string.INFO_IBSMAX4);
        }

        //13
        if (revendeur.equals("MMBOX")) {
            inforevenedeur1.setText(R.string.INFO_MMBOX1);
            inforevenedeur2.setText(R.string.INFO_MMBOX2);
            inforevenedeur3.setText(R.string.INFO_MMBOX3);
            inforevenedeur4.setText(R.string.INFO_MMBOX4);
        }

        //14
        if (revendeur.equals("LAGA")) {
            inforevenedeur1.setText(R.string.INFO_LAGA1);
            inforevenedeur2.setText(R.string.INFO_LAGA2);
            inforevenedeur3.setText(R.string.INFO_LAGA3);
            inforevenedeur4.setText(R.string.INFO_LAGA4);
        }

        //15
        if (revendeur.equals("DELPHI SOFT")) {
            inforevenedeur1.setText(R.string.INFO_DELPHI1);
            inforevenedeur2.setText(R.string.INFO_DELPHI2);
            inforevenedeur3.setText(R.string.INFO_DELPHI3);
            inforevenedeur4.setText(R.string.INFO_DELPHI4);
        }

        //16
        if (revendeur.equals("ACIDOMTECH")) {
            inforevenedeur1.setText(R.string.INFO_ACIDOMTECH1);
            inforevenedeur2.setText(R.string.INFO_ACIDOMTECH2);
            inforevenedeur3.setText(R.string.INFO_ACIDOMTECH3);
            inforevenedeur4.setText(R.string.INFO_ACIDOMTECH4);
        }

        //17
        if (revendeur.equals("EL KHALIL SOFT")) {
            inforevenedeur1.setText(R.string.INFO_UNIVERSOFT1);
            inforevenedeur2.setText(R.string.INFO_UNIVERSOFT2);
            inforevenedeur3.setText(R.string.INFO_UNIVERSOFT3);
            inforevenedeur4.setText(R.string.INFO_UNIVERSOFT4);
        }

        //18
        if (revendeur.equals("BBS")) {
            inforevenedeur1.setText(R.string.INFO_BBS1);
            inforevenedeur2.setText(R.string.INFO_BBS2);
            inforevenedeur3.setText(R.string.INFO_BBS3);
            inforevenedeur4.setText(R.string.INFO_BBS4);
        }

        //20
        if (revendeur.equals("GENERAL IT")) {
            inforevenedeur1.setText(R.string.INFO_GENERALIT1);
            inforevenedeur2.setText(R.string.INFO_GENERALIT2);
            inforevenedeur3.setText(R.string.INFO_GENERALIT3);
            inforevenedeur4.setText(R.string.INFO_GENERALIT4);
        }

        //21
        if (revendeur.equals("TIFAWT TECHNOLOGIE")) {
            inforevenedeur1.setText(R.string.INFO_TIFAWT_TECHNOLOGIE1);
            inforevenedeur2.setText(R.string.INFO_TIFAWT_TECHNOLOGIE2);
            inforevenedeur3.setText(R.string.INFO_TIFAWT_TECHNOLOGIE3);
            inforevenedeur4.setText(R.string.INFO_TIFAWT_TECHNOLOGIE4);
        }

        //26
        if (revendeur.equals("DARIA COMPUTER")) {
            inforevenedeur1.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur2.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur3.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur4.setText(R.string.INFO_DARIASOFT1);
        }

        //30
        if (revendeur.equals("DATA PRO")) {
            inforevenedeur1.setText(R.string.INFO_DATAPRO1);
            inforevenedeur2.setText(R.string.INFO_DATAPRO2);
            inforevenedeur3.setText(R.string.INFO_DATAPRO3);
            inforevenedeur4.setText(R.string.INFO_DATAPRO4);
        }

        //31
        if (revendeur.equals("AFKAR SOFT")) {
            inforevenedeur1.setText(R.string.INFO_AFKARSOFT1);
            inforevenedeur2.setText(R.string.INFO_AFKARSOFT2);
            inforevenedeur3.setText(R.string.INFO_AFKARSOFT3);
            inforevenedeur4.setText(R.string.INFO_AFKARSOFT4);
        }

        //32
        if (revendeur.equals("AZAD ADRAR")) {
            inforevenedeur1.setText(R.string.INFO_AZAD1);
            inforevenedeur2.setText(R.string.INFO_AZAD1);
            inforevenedeur3.setText(R.string.INFO_AZAD1);
            inforevenedeur4.setText(R.string.INFO_AZAD1);
        }

        //33
        if (revendeur.equals("VAST SOFT")) {
            inforevenedeur1.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur2.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur3.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur4.setText(R.string.INFO_VASTSOFT1);
        }

        //34
        if (revendeur.equals("EASY SOLUTION")) {
            inforevenedeur1.setText(R.string.INFO_EASYSOLUTION1);
            inforevenedeur2.setText(R.string.INFO_EASYSOLUTION2);
            inforevenedeur3.setText(R.string.INFO_EASYSOLUTION3);
            inforevenedeur4.setText(R.string.INFO_EASYSOLUTION4);
        }


        //35
        if (revendeur.equals("CAM PLUS")) {
            inforevenedeur1.setText(R.string.INFO_CAM_PLUS1);
            inforevenedeur2.setText(R.string.INFO_CAM_PLUS2);
            inforevenedeur3.setText(R.string.INFO_CAM_PLUS3);
            inforevenedeur4.setText(R.string.INFO_CAM_PLUS4);
        }

        //37
        if (revendeur.equals("EXPERT INFO")) {
            inforevenedeur1.setText(R.string.INFO_EXPERTINFO1);
            inforevenedeur2.setText(R.string.INFO_EXPERTINFO2);
            inforevenedeur3.setText(R.string.INFO_EXPERTINFO3);
            inforevenedeur4.setText(R.string.INFO_EXPERTINFO4);
        }

        //40 ATLANTIC SOLUTION


        //42
        if (revendeur.equals("KATIA QUEEN SOFT")) {
            inforevenedeur1.setText(R.string.INFO_QUEENSOFT1);
            inforevenedeur2.setText(R.string.INFO_QUEENSOFT2);
            inforevenedeur3.setText(R.string.INFO_QUEENSOFT3);
            inforevenedeur4.setText(R.string.INFO_QUEENSOFT4);
        }

        //43
        if (revendeur.equals("NOUH BARIKA")) {
            inforevenedeur1.setText(R.string.INFO_NOUHBARIKA1);
            inforevenedeur2.setText(R.string.INFO_NOUHBARIKA2);
            inforevenedeur3.setText(R.string.INFO_NOUHBARIKA3);
            inforevenedeur4.setText(R.string.INFO_NOUHBARIKA4);
        }

        //44
        if (revendeur.equals("OA TECH")) {
            inforevenedeur1.setText(R.string.INFO_OATECH1);
            inforevenedeur2.setText(R.string.INFO_OATECH2);
            inforevenedeur3.setText(R.string.INFO_OATECH3);
            inforevenedeur4.setText(R.string.INFO_OATECH4);
        }


        //45
        if (revendeur.equals("MATEN AFKAR")) {
            inforevenedeur1.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur2.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur3.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur4.setText(R.string.INFO_MATENAFKAR1);
        }

        //46 APEX

        //47 INFO_MICRO_DRIEF
        if (revendeur.equals("INFO MICRO DRIEF")) {
            inforevenedeur1.setText(R.string.INFO_INFO_MICRO1);
            inforevenedeur2.setText(R.string.INFO_INFO_MICRO2);
            inforevenedeur3.setText(R.string.INFO_INFO_MICRO3);
            inforevenedeur4.setText(R.string.INFO_INFO_MICRO4);
        }

        //48
        if (revendeur.equals("BR SOFT")) {
            inforevenedeur1.setText(R.string.INFO_BRSOFT1);
            inforevenedeur2.setText(R.string.INFO_BRSOFT2);
            inforevenedeur3.setText(R.string.INFO_BRSOFT3);
            inforevenedeur4.setText(R.string.INFO_BRSOFT4);
        }

        //49 TECHNO_INFODZ
        if (revendeur.equals("TECHNO_INFODZ")) {
            inforevenedeur1.setText(R.string.INFO_TECHNO_INFODZ1);
            inforevenedeur2.setText(R.string.INFO_TECHNO_INFODZ2);
            inforevenedeur3.setText(R.string.INFO_TECHNO_INFODZ3);
            inforevenedeur4.setText(R.string.INFO_TECHNO_INFODZ4);
        }

        //50 SSG_SERVICES

    }

}

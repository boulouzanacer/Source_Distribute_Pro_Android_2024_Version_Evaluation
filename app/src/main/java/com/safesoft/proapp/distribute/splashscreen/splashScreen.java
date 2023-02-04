package com.safesoft.proapp.distribute.splashscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.provider.Settings.Secure;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.safesoft.proapp.distribute.MainActivity;
import com.safesoft.proapp.distribute.activation.ActivityActivation;
import com.safesoft.proapp.distribute.R;

import java.util.Timer;
import java.util.TimerTask;

public class splashScreen extends AppCompatActivity {

    private String PREFS = "ALL_PREFS";
    public static final String NUM_SERIE = "INFO_ACTIVATION";

    public static final String revendeur = "SAFE SOFT";
    //public static final String revendeur = "TIEMPO SOFT";
    //public static final String revendeur = "CHERRATA SOFT";
    //public static final String revendeur = "TECH POS";
    //public static final String revendeur = "CAM PLUS";
    //public static final String revendeur = "GLOBAL TECH";
    //public static final String revendeur = "VAST SOFT";
    //public static final String revendeur = "TIFAWT TECHNOLOGIE";
    //public static final String revendeur = "EASY SOFT";

    ImageView imagesplash,logo_revendeur;
    LinearLayout linear_layout_revendeur;
    TextView inforevenedeur1,inforevenedeur2,inforevenedeur3,inforevenedeur4;
    Animation slide_in_from_left, slide_in_from_right;

    private int code_activation;
    private String deviceId2 = "";
    String deviceId;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        imagesplash = findViewById(R.id.ImageSplash);
        logo_revendeur = findViewById(R.id.logo_revendeur);
        linear_layout_revendeur = findViewById(R.id.linear_layout_revendeur);

        slide_in_from_left = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
        slide_in_from_right = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_right);

        info_revendeur1();
        imagesplash.setAnimation(slide_in_from_left);
        logo_revendeur.setAnimation(slide_in_from_right);
        linear_layout_revendeur.setAnimation(slide_in_from_left);

        deviceId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        for(int i=0;i<= deviceId.length()-1 ;i++){
            deviceId2 = deviceId2 + deviceId.charAt(i);
            if ((i + 1) % 4 == 0 && i != 15) {
                deviceId2 = deviceId2 + "-";
            }
        }
        deviceId2 = "8T"+ deviceId2.substring(2);
        deviceId2= deviceId2.toUpperCase();
        int i = 0;
        int o = deviceId2.length()+1;
        for(int t=0;t<= deviceId2.length()-1 ;t++){
            i = i + (int)deviceId2.charAt(t) * 47293 * o;
            o = o - 1;
        }
        final int codeactivation = i;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                code_activation = prefs.getInt("CODE_ACTIVATION",0);
                if (codeactivation == code_activation ) {
                    startActivity(new Intent(splashScreen.this, MainActivity.class));
                    ////// START LOGIN FORM ////////////////
                } else {
                    Intent intent = new Intent(splashScreen.this,ActivityActivation.class);
                    intent.putExtra(NUM_SERIE,deviceId2);
                    intent.putExtra(revendeur,revendeur);
                    startActivity(intent);
                }

                //finish();
            }
        },2000);


//        TextView test_spash = (TextView) findViewById(R.id.textsplash);
//        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/fff_tusj.ttf");
//        test_spash.setTypeface(face);
//        //thread for splash screen running
//        Thread logoTimer = new Thread() {
//            public void run() {
//                try {
//                    sleep(2000);
//                } catch (InterruptedException e) {
//                    Log.d("Exception", "Exception" + e);
//                } finally {
//                    SharedPreferences prefs = getSharedPreferences(PREFS_ACTIVATION, MODE_PRIVATE);
//                    if (prefs.getBoolean("IS_ACTIVATED", false)) {
//                        startActivity(new Intent(splashScreen.this, MainActivity.class));
//                    } else {
//                        startActivity(new Intent(splashScreen.this, ActivityActivation.class));
//                    }
//                }
//                finish();
//            }
//        };
//        logoTimer.start();
    }
    public void info_revendeur1() {
        inforevenedeur1 = findViewById(R.id.InfoRevendeur1);
        inforevenedeur2 = findViewById(R.id.InfoRevendeur2);
        inforevenedeur3 = findViewById(R.id.InfoRevendeur3);
        inforevenedeur4 = findViewById(R.id.InfoRevendeur4);

        if (revendeur.equals("SAFE SOFT"))  {
            logo_revendeur.setImageResource(R.drawable.logo_safe_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_SAFE_SOFT1);
            inforevenedeur2.setText(R.string.INFO_SAFE_SOFT2);
            inforevenedeur3.setText(R.string.INFO_SAFE_SOFT3);
            inforevenedeur4.setText(R.string.INFO_SAFE_SOFT4);
        }
        if (revendeur.equals("TIEMPO SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_tiempo_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_TIEMPO_SOFT1);
            inforevenedeur2.setText(R.string.INFO_TIEMPO_SOFT2);
            inforevenedeur3.setText(R.string.INFO_TIEMPO_SOFT3);
            inforevenedeur4.setText(R.string.INFO_TIEMPO_SOFT4);
        }
        if (revendeur.equals("CHERRATA SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_cherrata_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_CHERRATA_SOFT1);
            inforevenedeur2.setText(R.string.INFO_CHERRATA_SOFT2);
            inforevenedeur3.setText(R.string.INFO_CHERRATA_SOFT3);
            inforevenedeur4.setText(R.string.INFO_CHERRATA_SOFT4);
        }
        if (revendeur.equals("TECH POS")) {
            logo_revendeur.setImageResource(R.drawable.logo_techpos_500_100);
            inforevenedeur1.setText(R.string.INFO_TECH_POS1);
            inforevenedeur2.setText(R.string.INFO_TECH_POS2);
            inforevenedeur3.setText(R.string.INFO_TECH_POS3);
            inforevenedeur4.setText(R.string.INFO_TECH_POS4);
        }
        if (revendeur.equals("GLOBAL TECH")) {
            logo_revendeur.setImageResource(R.drawable.logo_global_500_100);
            inforevenedeur1.setText(R.string.INFO_GLOBAL_TECH1);
            inforevenedeur2.setText(R.string.INFO_GLOBAL_TECH2);
            inforevenedeur3.setText(R.string.INFO_GLOBAL_TECH3);
            inforevenedeur4.setText(R.string.INFO_GLOBAL_TECH4);
        }
        if (revendeur.equals("CAM PLUS")) {
            logo_revendeur.setImageResource(R.drawable.logo_cam_plus_500_100);
            inforevenedeur1.setText(R.string.INFO_CAM_PLUS1);
            inforevenedeur2.setText(R.string.INFO_CAM_PLUS2);
            inforevenedeur3.setText(R.string.INFO_CAM_PLUS3);
            inforevenedeur4.setText(R.string.INFO_CAM_PLUS4);
        }
        if (revendeur.equals("TIFAWT TECHNOLOGIE")) {
            logo_revendeur.setImageResource(R.drawable.logo_tifawt_500_100);
            inforevenedeur1.setText(R.string.INFO_TIFAWT_TECHNOLOGIE1);
            inforevenedeur2.setText(R.string.INFO_TIFAWT_TECHNOLOGIE2);
            inforevenedeur3.setText(R.string.INFO_TIFAWT_TECHNOLOGIE3);
            inforevenedeur4.setText(R.string.INFO_TIFAWT_TECHNOLOGIE4);
        }
    }
}

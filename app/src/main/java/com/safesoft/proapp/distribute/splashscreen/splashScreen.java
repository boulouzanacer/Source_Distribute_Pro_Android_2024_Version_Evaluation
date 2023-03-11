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
import com.safesoft.proapp.distribute.activation.ActivityCodeRevendeur;

import java.util.Timer;
import java.util.TimerTask;

public class splashScreen extends AppCompatActivity {

    private String PREFS = "ALL_PREFS";
    public static final String NUM_SERIE = "INFO_ACTIVATION";

    public static String revendeur = "SAFE SOFT";
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

        if(getIntent() != null){
            revendeur = getIntent().getStringExtra("REVENDEUR");
        }
        revendeur = getIntent().getStringExtra("REVENDEUR");

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

                } else {
                    ////// GO TO REVENDEUR SCREEN ////////////////
                    Intent intent = new Intent(splashScreen.this, ActivityActivation.class);
                    intent.putExtra(NUM_SERIE,deviceId2);
                    intent.putExtra(revendeur,revendeur);
                    startActivity(intent);
                }
                finish();

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

        //00
        if (revendeur.equals("SAFE SOFT"))  {
            logo_revendeur.setImageResource(R.drawable.logo_safe_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_SAFE_SOFT1);
            inforevenedeur2.setText(R.string.INFO_SAFE_SOFT2);
            inforevenedeur3.setText(R.string.INFO_SAFE_SOFT3);
            inforevenedeur4.setText(R.string.INFO_SAFE_SOFT4);
        }
        //01
        if (revendeur.equals("COMPOS SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_compos_500_100);
            inforevenedeur1.setText(R.string.INFO_COMPOS1);
            inforevenedeur2.setText(R.string.INFO_COMPOS2);
            inforevenedeur3.setText(R.string.INFO_COMPOS3);
            inforevenedeur4.setText(R.string.INFO_COMPOS4);
        }
        //02
        if (revendeur.equals("TIEMPO SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_tiempo_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_TIEMPO_SOFT1);
            inforevenedeur2.setText(R.string.INFO_TIEMPO_SOFT2);
            inforevenedeur3.setText(R.string.INFO_TIEMPO_SOFT3);
            inforevenedeur4.setText(R.string.INFO_TIEMPO_SOFT4);
        }
        //03
        if (revendeur.equals("CHERRATA SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_cherrata_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_CHERRATA_SOFT1);
            inforevenedeur2.setText(R.string.INFO_CHERRATA_SOFT2);
            inforevenedeur3.setText(R.string.INFO_CHERRATA_SOFT3);
            inforevenedeur4.setText(R.string.INFO_CHERRATA_SOFT4);
        }
        //04
        if (revendeur.equals("EASY SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_easy_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_EASY_SOFT1);
            inforevenedeur2.setText(R.string.INFO_EASY_SOFT1);
            inforevenedeur3.setText(R.string.INFO_EASY_SOFT3);
            inforevenedeur4.setText(R.string.INFO_EASY_SOFT4);
        }
        //05
        if (revendeur.equals("MOON SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_moon_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_MOON_SOFT1);
            inforevenedeur2.setText(R.string.INFO_MOON_SOFT2);
            inforevenedeur3.setText(R.string.INFO_MOON_SOFT3);
            inforevenedeur4.setText(R.string.INFO_MOON_SOFT4);
        }
        //06
        if (revendeur.equals("GLOBAL TECH")) {
            logo_revendeur.setImageResource(R.drawable.logo_global_500_100);
            inforevenedeur1.setText(R.string.INFO_GLOBAL_TECH1);
            inforevenedeur2.setText(R.string.INFO_GLOBAL_TECH2);
            inforevenedeur3.setText(R.string.INFO_GLOBAL_TECH3);
            inforevenedeur4.setText(R.string.INFO_GLOBAL_TECH4);
        }
        //07
        if (revendeur.equals("POLYRAW")) {
            logo_revendeur.setImageResource(R.drawable.logo_poliraw_500_100);
            inforevenedeur1.setText(R.string.INFO_GLOBAL_TECH1);
            inforevenedeur2.setText(R.string.INFO_GLOBAL_TECH2);
            inforevenedeur3.setText(R.string.INFO_GLOBAL_TECH3);
            inforevenedeur4.setText(R.string.INFO_GLOBAL_TECH4);
        }
        //08
        if (revendeur.equals("PRO SOLUTION")) {
            logo_revendeur.setImageResource(R.drawable.logo_pro_solution_500_100);
            inforevenedeur1.setText(R.string.INFO_PROSOLUTION1);
            inforevenedeur2.setText(R.string.INFO_PROSOLUTION2);
            inforevenedeur3.setText(R.string.INFO_PROSOLUTION3);
            inforevenedeur4.setText(R.string.INFO_PROSOLUTION4);
        }
        //09
        if (revendeur.equals("ACIDOM TECH")) {
            logo_revendeur.setImageResource(R.drawable.logo_acidomtech_500_100);
            inforevenedeur1.setText(R.string.INFO_ACIDOMTECH1);
            inforevenedeur2.setText(R.string.INFO_ACIDOMTECH2);
            inforevenedeur3.setText(R.string.INFO_ACIDOMTECH3);
            inforevenedeur4.setText(R.string.INFO_ACIDOMTECH4);
        }
        //10
        if (revendeur.equals("NDHL")) {
            logo_revendeur.setImageResource(R.drawable.logo_ndhl_500_100);
            inforevenedeur1.setText(R.string.INFO_NDHL1);
            inforevenedeur2.setText(R.string.INFO_NDHL2);
            inforevenedeur3.setText(R.string.INFO_NDHL3);
            inforevenedeur4.setText(R.string.INFO_NDHL4);
        }
        //11
        if (revendeur.equals("TECH POS")) {
            logo_revendeur.setImageResource(R.drawable.logo_techpos_500_100);
            inforevenedeur1.setText(R.string.INFO_TECH_POS1);
            inforevenedeur2.setText(R.string.INFO_TECH_POS2);
            inforevenedeur3.setText(R.string.INFO_TECH_POS3);
            inforevenedeur4.setText(R.string.INFO_TECH_POS4);
        }
        //12
        if (revendeur.equals("UNIVER SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_universoft_500_100);
            inforevenedeur1.setText(R.string.INFO_UNIVERSOFT1);
            inforevenedeur2.setText(R.string.INFO_UNIVERSOFT2);
            inforevenedeur3.setText(R.string.INFO_UNIVERSOFT3);
            inforevenedeur4.setText(R.string.INFO_UNIVERSOFT4);
        }
        //13
        if (revendeur.equals("IBSMAX")) {
            logo_revendeur.setImageResource(R.drawable.logo_ibsmax_500_100);
            inforevenedeur1.setText(R.string.INFO_IBSMAX1);
            inforevenedeur2.setText(R.string.INFO_IBSMAX2);
            inforevenedeur3.setText(R.string.INFO_IBSMAX3);
            inforevenedeur4.setText(R.string.INFO_IBSMAX4);
        }
        //14
        if (revendeur.equals("MMBOX")) {
            logo_revendeur.setImageResource(R.drawable.logo_mmbox_500_100);
            inforevenedeur1.setText(R.string.INFO_MMBOX1);
            inforevenedeur2.setText(R.string.INFO_MMBOX2);
            inforevenedeur3.setText(R.string.INFO_MMBOX3);
            inforevenedeur4.setText(R.string.INFO_MMBOX4);
        }
        //15
        if (revendeur.equals("DELPHI SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_delphi_soft_500_100);
            inforevenedeur1.setText(R.string.INFO_DELPHI1);
            inforevenedeur2.setText(R.string.INFO_DELPHI2);
            inforevenedeur3.setText(R.string.INFO_DELPHI3);
            inforevenedeur4.setText(R.string.INFO_DELPHI4);
        }
        //16
        if (revendeur.equals("BBS")) {
            logo_revendeur.setImageResource(R.drawable.logo_bbs_500_100);
            inforevenedeur1.setText(R.string.INFO_BBS1);
            inforevenedeur2.setText(R.string.INFO_BBS2);
            inforevenedeur3.setText(R.string.INFO_BBS3);
            inforevenedeur4.setText(R.string.INFO_BBS4);
        }
        //17
        if (revendeur.equals("EXPERT INFO")) {
             logo_revendeur.setImageResource(R.drawable.logo_expert_info_500_100);
            inforevenedeur1.setText(R.string.INFO_EXPERTINFO1);
            inforevenedeur2.setText(R.string.INFO_EXPERTINFO2);
            inforevenedeur3.setText(R.string.INFO_EXPERTINFO3);
            inforevenedeur4.setText(R.string.INFO_EXPERTINFO4);
        }
        //18
        if (revendeur.equals("ARC TECH")) {
            logo_revendeur.setImageResource(R.drawable.logo_expert_info_500_100);
            inforevenedeur1.setText(R.string.INFO_ARCTECH1);
            inforevenedeur2.setText(R.string.INFO_ARCTECH2);
            inforevenedeur3.setText(R.string.INFO_ARCTECH3);
            inforevenedeur4.setText(R.string.INFO_ARCTECH4);
        }
        //19
        if (revendeur.equals("DATA PRO")) {
            logo_revendeur.setImageResource(R.drawable.logo_datapro_500_100);
            inforevenedeur1.setText(R.string.INFO_DATAPRO1);
            inforevenedeur2.setText(R.string.INFO_DATAPRO1);
            inforevenedeur3.setText(R.string.INFO_DATAPRO1);
            inforevenedeur4.setText(R.string.INFO_DATAPRO1);
        }
        //20
        if (revendeur.equals("LAGA")) {
            logo_revendeur.setImageResource(R.drawable.logo_laga_500_100);
            inforevenedeur1.setText(R.string.INFO_LAGA1);
            inforevenedeur2.setText(R.string.INFO_LAGA2);
            inforevenedeur3.setText(R.string.INFO_LAGA3);
            inforevenedeur4.setText(R.string.INFO_LAGA4);
        }
        //21
        if (revendeur.equals("SOFT SPACE")) {
            logo_revendeur.setImageResource(R.drawable.logo_softspace_500_100);
            inforevenedeur1.setText(R.string.INFO_SOFTSPACE1);
            inforevenedeur2.setText(R.string.INFO_SOFTSPACE2);
            inforevenedeur3.setText(R.string.INFO_SOFTSPACE3);
            inforevenedeur4.setText(R.string.INFO_SOFTSPACE4);
        }
        //22
        if (revendeur.equals("AFKAR SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_afkarsoft_500_100);
            inforevenedeur1.setText(R.string.INFO_AFKARSOFT1);
            inforevenedeur2.setText(R.string.INFO_AFKARSOFT2);
            inforevenedeur3.setText(R.string.INFO_AFKARSOFT3);
            inforevenedeur4.setText(R.string.INFO_AFKARSOFT4);
        }
        //23
        if (revendeur.equals("AZAD ADRAR")) {
            logo_revendeur.setImageResource(R.drawable.logo_azad_500_100);
            inforevenedeur1.setText(R.string.INFO_AZAD1);
            inforevenedeur2.setText(R.string.INFO_AZAD1);
            inforevenedeur3.setText(R.string.INFO_AZAD1);
            inforevenedeur4.setText(R.string.INFO_AZAD1);
        }
        //24
        if (revendeur.equals("GENERAL IT")) {
            logo_revendeur.setImageResource(R.drawable.logo_azad_500_100);
            inforevenedeur1.setText(R.string.INFO_GENERALIT1);
            inforevenedeur2.setText(R.string.INFO_GENERALIT2);
            inforevenedeur3.setText(R.string.INFO_GENERALIT3);
            inforevenedeur4.setText(R.string.INFO_GENERALIT4);
        }
        //25
        if (revendeur.equals("VAST SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_vastsoft_500_100);
            inforevenedeur1.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur2.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur3.setText(R.string.INFO_VASTSOFT1);
            inforevenedeur4.setText(R.string.INFO_VASTSOFT1);
        }
        //26
        if (revendeur.equals("CAM PLUS")) {
            logo_revendeur.setImageResource(R.drawable.logo_cam_plus_500_100);
            inforevenedeur1.setText(R.string.INFO_CAM_PLUS1);
            inforevenedeur2.setText(R.string.INFO_CAM_PLUS2);
            inforevenedeur3.setText(R.string.INFO_CAM_PLUS3);
            inforevenedeur4.setText(R.string.INFO_CAM_PLUS4);
        }
        //27
        if (revendeur.equals("EASY SOLUTION")) {
            logo_revendeur.setImageResource(R.drawable.logo_easysolution_500_100);
            inforevenedeur1.setText(R.string.INFO_EASYSOLUTION1);
            inforevenedeur2.setText(R.string.INFO_EASYSOLUTION2);
            inforevenedeur3.setText(R.string.INFO_EASYSOLUTION3);
            inforevenedeur4.setText(R.string.INFO_EASYSOLUTION4);
        }
        //28
        if (revendeur.equals("TIFAWT TECHNOLOGIE")) {
            logo_revendeur.setImageResource(R.drawable.logo_tifawt_500_100);
            inforevenedeur1.setText(R.string.INFO_TIFAWT_TECHNOLOGIE1);
            inforevenedeur2.setText(R.string.INFO_TIFAWT_TECHNOLOGIE2);
            inforevenedeur3.setText(R.string.INFO_TIFAWT_TECHNOLOGIE3);
            inforevenedeur4.setText(R.string.INFO_TIFAWT_TECHNOLOGIE4);
        }
        //31
        if (revendeur.equals("DARIA COMPUTER")) {
            logo_revendeur.setImageResource(R.drawable.logo_daria_500_100);
            inforevenedeur1.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur2.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur3.setText(R.string.INFO_DARIASOFT1);
            inforevenedeur4.setText(R.string.INFO_DARIASOFT1);
        }
        //32
        if (revendeur.equals("MATEN AFKAR")) {
            logo_revendeur.setImageResource(R.drawable.logo_maten_500_100);
            inforevenedeur1.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur2.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur3.setText(R.string.INFO_MATENAFKAR1);
            inforevenedeur4.setText(R.string.INFO_MATENAFKAR1);
        }
        //33
        if (revendeur.equals("NOUH BARIKA")) {
            logo_revendeur.setImageResource(R.drawable.logo_nouhbarika_500_100);
            inforevenedeur1.setText(R.string.INFO_NOUHBARIKA1);
            inforevenedeur2.setText(R.string.INFO_NOUHBARIKA2);
            inforevenedeur3.setText(R.string.INFO_NOUHBARIKA3);
            inforevenedeur4.setText(R.string.INFO_NOUHBARIKA4);
        }
        //34
        if (revendeur.equals("KATIA QUEEN SOFT")) {
            logo_revendeur.setImageResource(R.drawable.logo_queensoft_500_100);
            inforevenedeur1.setText(R.string.INFO_QUEENSOFT1);
            inforevenedeur2.setText(R.string.INFO_QUEENSOFT2);
            inforevenedeur3.setText(R.string.INFO_QUEENSOFT3);
            inforevenedeur4.setText(R.string.INFO_QUEENSOFT4);
        }
        //35
        if (revendeur.equals("OA TECH")) {
            logo_revendeur.setImageResource(R.drawable.logo_oatech_500_100);
            inforevenedeur1.setText(R.string.INFO_OATECH1);
            inforevenedeur2.setText(R.string.INFO_OATECH2);
            inforevenedeur3.setText(R.string.INFO_OATECH3);
            inforevenedeur4.setText(R.string.INFO_OATECH4);
        }



    }
}

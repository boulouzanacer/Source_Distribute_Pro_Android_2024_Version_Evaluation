package com.safesoft.proapp.distribute.activation;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.splashscreen.splashScreen;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class ActivityCodeRevendeur extends AppCompatActivity {

    private final String PREFS = "ALL_PREFS";

    private boolean isVendorSaved = false;

    private String REVENDEUR = "SAFE SOFT";

    private OtpTextView otpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code_revendeur);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        isVendorSaved = prefs.getBoolean("SAVED_REVENDEUR", false);
        REVENDEUR = prefs.getString("REVENDEUR", "SAFE SOFT");

        if (isVendorSaved) {
            Intent intent = new Intent(ActivityCodeRevendeur.this, splashScreen.class);
            intent.putExtra("REVENDEUR", REVENDEUR);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }

        otpTextView = findViewById(R.id.otp_view);
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
                // Toast.makeText(ActivityCodeRevendeur.this, "bbbbb",  Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.
                //Toast.makeText(ActivityCodeRevendeur.this, "The OTP is " + otp,  Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ActivityCodeRevendeur.this, splashScreen.class);

                //Vérification du revendeur
                switch (otp) {
                    case "0000" -> { // SAFESOFT
                        intent.putExtra("REVENDEUR", "SAFE SOFT");
                        saveData("SAFE SOFT");
                        startActivity(intent);
                        finish();
                    }
                    case "0101" -> { // COMPOS SOFT
                        intent.putExtra("REVENDEUR", "COMPOS SOFT");
                        saveData("COMPOS SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "0202" -> { // TIEMPO SOFT
                        intent.putExtra("REVENDEUR", "TIEMPO SOFT");
                        saveData("TIEMPO SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "0303" -> { // CHERRATA SOFT
                        intent.putExtra("REVENDEUR", "CHERRATA SOFT");
                        saveData("CHERRATA SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "0404" -> { // EASY SOFT
                        intent.putExtra("REVENDEUR", "EASY SOFT");
                        saveData("EASY SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "0505" -> { // MOON SOFT
                        intent.putExtra("REVENDEUR", "MOON SOFT");
                        saveData("MOON SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "0606" -> { // GLOBAL TECH
                        intent.putExtra("REVENDEUR", "GLOBAL TECH");
                        saveData("GLOBAL TECH");
                        startActivity(intent);
                        finish();
                    }

                    case "0707" -> { // POLYRAW
                        intent.putExtra("REVENDEUR", "DSX SYSTEME");
                        saveData("DSX SYSTEME");
                        startActivity(intent);
                        finish();
                    }

                    case "0808" -> { // PROSOLUTION
                        intent.putExtra("REVENDEUR", "PRO SOLUTION");
                        saveData("PRO SOLUTION");
                        startActivity(intent);
                        finish();
                    }

                    case "0909" -> { // ACIDOMTECH
                        intent.putExtra("REVENDEUR", "SOFT SPACE");
                        saveData("SOFT SPACE");
                        startActivity(intent);
                        finish();
                    }

                    case "1010" -> { // TECH POS
                        intent.putExtra("REVENDEUR", "TECH POS");
                        startActivity(intent);
                        saveData("TECH POS");
                        finish();
                    }

                    case "1111" -> { // NDHL
                        intent.putExtra("REVENDEUR", "NDHL");
                        saveData("NDHL");
                        startActivity(intent);
                        finish();
                    }

                    case "1212" -> { // IBSMAX
                        intent.putExtra("REVENDEUR", "IBSMAX");
                        startActivity(intent);
                        saveData("IBSMAX");
                        finish();
                    }

                    case "1313" -> { // MMBOX
                        intent.putExtra("REVENDEUR", "MMBOX");
                        startActivity(intent);
                        saveData("MMBOX");
                        finish();
                    }

                    case "1414" -> { //LAGA
                        intent.putExtra("REVENDEUR", "LAGA");
                        startActivity(intent);
                        saveData("LAGA");
                        finish();
                    }

                    case "1515" -> { //DELPHI
                        intent.putExtra("REVENDEUR", "DELPHI SOFT");
                        startActivity(intent);
                        saveData("DELPHI SOFT");
                        finish();
                    }

                    case "1616" -> { // UNIVERSOFT
                        intent.putExtra("REVENDEUR", "ACIDOMTECH");
                        startActivity(intent);
                        saveData("ACIDOMTECH");
                        finish();
                    }

                    case "1717" -> { // UNIVERSOFT
                        intent.putExtra("REVENDEUR", "EL KHALIL SOFT");
                        startActivity(intent);
                        saveData("EL KHALIL SOFT");
                        finish();
                    }

                    case "1818" -> { //BBS
                        intent.putExtra("REVENDEUR", "BBS");
                        startActivity(intent);
                        saveData("BBS");
                        finish();
                    }

                    case "2020" -> { //EXPERT INFO
                        intent.putExtra("REVENDEUR", "GENERAL IT");
                        startActivity(intent);
                        saveData("GENERAL IT");
                        finish();
                    }

                    case "2121" -> { //ARC TECH
                        intent.putExtra("REVENDEUR", "TIFAWT TECHNOLOGIE");
                        startActivity(intent);
                        saveData("TIFAWT TECHNOLOGIE");
                        finish();
                    }

                    case "2626" -> { //DATA PRO
                        intent.putExtra("REVENDEUR", "DARIA COMPUTER");
                        startActivity(intent);
                        saveData("DARIA COMPUTER");
                        finish();
                    }

                    case "3030" -> { //LAGA
                        intent.putExtra("REVENDEUR", "DATA PRO");
                        startActivity(intent);
                        saveData("DATA PRO");
                        finish();
                    }

                    case "3131" -> { //SOFT SPACE
                        intent.putExtra("REVENDEUR", "AFKAR SOFT");
                        startActivity(intent);
                        saveData("AFKAR SOFT");
                        finish();
                    }

                    case "3232" -> { //AFKAR SOFT
                        intent.putExtra("REVENDEUR", "AZAD ADRAR");
                        startActivity(intent);
                        saveData("AZAD ADRAR");
                        finish();
                    }

                    case "3333" -> { //AZAD ADRAR
                        intent.putExtra("REVENDEUR", "VAST SOFT");
                        startActivity(intent);
                        saveData("VAST SOFT");
                        finish();
                    }

                    case "3434" -> { //GENERAL IT
                        intent.putExtra("REVENDEUR", "EASY SOLUTION");
                        startActivity(intent);
                        saveData("EASY SOLUTION");
                        finish();
                    }

                    case "3535" -> { // VAST SOFT
                        intent.putExtra("REVENDEUR", "CAM PLUS");
                        startActivity(intent);
                        saveData("CAM PLUS");
                        finish();
                    }

                    case "3737" -> { // CAM PLUS
                        intent.putExtra("REVENDEUR", "EXPERT INFO");
                        saveData("EXPERT INFO");
                        startActivity(intent);
                        finish();
                    }

                    case "4040" -> { // ATLANTIC SOLUTION
                        intent.putExtra("REVENDEUR", "ATLANTIC SOLUTION");
                        saveData("ATLANTIC SOLUTION");
                        startActivity(intent);
                        finish();
                    }

                    case "4242" -> { // TIFAWT TECHNOLOGIE
                        intent.putExtra("REVENDEUR", "KATIA QUEEN SOFT");
                        saveData("KATIA QUEEN SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "4343" -> { // DARIA COMPUTER
                        intent.putExtra("REVENDEUR", "NOUH BARIKA");
                        saveData("NOUH BARIKA");
                        startActivity(intent);
                        finish();
                    }

                    case "4444" -> { // MATEN AFKAR
                        intent.putExtra("REVENDEUR", "OA TECH");
                        saveData("OA TECH");
                        startActivity(intent);
                        finish();
                    }

                    case "4545" -> { //NOUH BARIKA
                        intent.putExtra("REVENDEUR", "MATEN AFKAR");
                        saveData("MATEN AFKAR");
                        startActivity(intent);
                        finish();
                    }

                    case "4646" -> { // KATIA QUEEN SOFT
                        intent.putExtra("REVENDEUR", "APEX");
                        saveData("APEX");
                        startActivity(intent);
                        finish();
                    }

                    case "4747" -> { // OA TECH
                        intent.putExtra("REVENDEUR", "INFO MICRO DRIEF");
                        saveData("INFO MICRO DRIEF");
                        startActivity(intent);
                        finish();
                    }

                    case "4848" -> { // BR SOFT
                        intent.putExtra("REVENDEUR", "BR SOFT");
                        saveData("BR SOFT");
                        startActivity(intent);
                        finish();
                    }

                    case "4949" -> { // BR SOFT
                        intent.putExtra("REVENDEUR", "TECHNO_INFODZ");
                        saveData("TECHNO_INFODZ");
                        startActivity(intent);
                        finish();
                    }

                    case "5050" -> { // BR SOFT
                        intent.putExtra("REVENDEUR", "SSG SERVICES");
                        saveData("SSG SERVICES");
                        startActivity(intent);
                        finish();
                    }

                    default -> otpTextView.showError();
                }
            }

        });

        // otpTextView.showSuccess();
    }

    public void saveData(String revendeur) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("SAVED_REVENDEUR", true);
        editor.putString("REVENDEUR", revendeur);
        editor.apply();
    }
}
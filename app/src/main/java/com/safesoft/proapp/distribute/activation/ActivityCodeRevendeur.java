package com.safesoft.proapp.distribute.activation;

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
                        intent.putExtra("REVENDEUR", "POLYRAW");
                        saveData("POLYRAW");
                        startActivity(intent);
                        finish();
                    }
                    case "0808" -> { // PROSOLUTION
                        intent.putExtra("REVENDEUR", "PRO SOLUTION");
                        saveData("PROSOLUTION");
                        startActivity(intent);
                        finish();
                    }
                    case "0909" -> { // ACIDOMTECH
                        intent.putExtra("REVENDEUR", "ACIDOM TECH");
                        saveData("ACIDOMTECH");
                        startActivity(intent);
                        finish();
                    }
                    case "1010" -> { // NDHL
                        intent.putExtra("REVENDEUR", "NDHL");
                        saveData("NDHL");
                        startActivity(intent);
                        finish();
                    }
                    case "1111" -> { // TECH POS
                        intent.putExtra("REVENDEUR", "TECH POS");
                        startActivity(intent);
                        saveData("TECH POS");
                        finish();
                    }
                    case "1212" -> { // UNIVERSOFT
                        intent.putExtra("REVENDEUR", "UNIVER SOFT");
                        startActivity(intent);
                        saveData("UNIVERSOFT");
                        finish();
                    }
                    case "1313" -> { // IBSMAX
                        intent.putExtra("REVENDEUR", "IBSMAX");
                        startActivity(intent);
                        saveData("IBSMAX");
                        finish();
                    }
                    case "1414" -> { // MMBOX
                        intent.putExtra("REVENDEUR", "MMBOX");
                        startActivity(intent);
                        saveData("MMBOX");
                        finish();
                    }
                    case "1515" -> { //DELPHI
                        intent.putExtra("REVENDEUR", "DELPHI SOFT");
                        startActivity(intent);
                        saveData("DELPHI SOFT");
                        finish();
                    }
                    case "1616" -> { //BBS
                        intent.putExtra("REVENDEUR", "BBS");
                        startActivity(intent);
                        saveData("BBS");
                        finish();
                    }
                    case "1717" -> { //EXPERT INFO
                        intent.putExtra("REVENDEUR", "EXPERT INFO");
                        startActivity(intent);
                        saveData("EXPERT INFO");
                        finish();
                    }
                    case "1818" -> { //ARC TECH
                        intent.putExtra("REVENDEUR", "ARC TECH");
                        startActivity(intent);
                        saveData("ARC TECH");
                        finish();
                    }
                    case "1919" -> { //DATA PRO
                        intent.putExtra("REVENDEUR", "DATA PRO");
                        startActivity(intent);
                        saveData("DATA PRO");
                        finish();
                    }
                    case "2020" -> { //LAGA
                        intent.putExtra("REVENDEUR", "LAGA");
                        startActivity(intent);
                        saveData("LAGA");
                        finish();
                    }
                    case "2121" -> { //SOFT SPACE
                        intent.putExtra("REVENDEUR", "SOFT SPACE");
                        startActivity(intent);
                        saveData("SOFT SPACE");
                        finish();
                    }
                    case "2222" -> { //AFKAR SOFT
                        intent.putExtra("REVENDEUR", "AFKAR SOFT");
                        startActivity(intent);
                        saveData("AFKAR SOFT");
                        finish();
                    }
                    case "2323" -> { //AZAD ADRAR
                        intent.putExtra("REVENDEUR", "AZAD ADRAR");
                        startActivity(intent);
                        saveData("AZAD ADRAR");
                        finish();
                    }
                    case "2424" -> { //GENERAL IT
                        intent.putExtra("REVENDEUR", "GENERAL IT");
                        startActivity(intent);
                        saveData("GENERAL IT");
                        finish();
                    }
                    case "2525" -> { // VAST SOFT
                        intent.putExtra("REVENDEUR", "VAST SOFT");
                        startActivity(intent);
                        saveData("VAST SOFT");
                        finish();
                    }
                    case "2626" -> { // CAM PLUS
                        intent.putExtra("REVENDEUR", "CAM PLUS");
                        saveData("CAM PLUS");
                        startActivity(intent);
                        finish();
                    }
                    case "2727" -> { // EASY SOLUTION
                        intent.putExtra("REVENDEUR", "EASY SOLUTION");
                        saveData("EASY SOLUTION");
                        startActivity(intent);
                        finish();
                    }
                    case "2828" -> { // TIFAWT TECHNOLOGIE
                        intent.putExtra("REVENDEUR", "TIFAWT TECHNOLOGIE");
                        saveData("TIFAWT TECHNOLOGIE");
                        startActivity(intent);
                        finish();
                    }
                    case "3131" -> { // DARIA COMPUTER
                        intent.putExtra("REVENDEUR", "DARIA COMPUTER");
                        saveData("DARIA COMPUTER");
                        startActivity(intent);
                        finish();
                    }
                    case "3232" -> { // MATEN AFKAR
                        intent.putExtra("REVENDEUR", "MATEN AFKAR");
                        saveData("MATEN AFKAR");
                        startActivity(intent);
                        finish();
                    }
                    case "3333" -> { //NOUH BARIKA
                        intent.putExtra("REVENDEUR", "NOUH BARIKA");
                        saveData("NOUH BARIKA");
                        startActivity(intent);
                        finish();
                    }
                    case "3434" -> { // KATIA QUEEN SOFT
                        intent.putExtra("REVENDEUR", "KATIA QUEEN SOFT");
                        saveData("KATIA QUEEN SOFT");
                        startActivity(intent);
                        finish();
                    }
                    case "3535" -> { // OA TECH
                        intent.putExtra("REVENDEUR", "OA TECH");
                        saveData("OA TECH");
                        startActivity(intent);
                        finish();
                    }
                    case "3636" -> { // BR SOFT
                        intent.putExtra("REVENDEUR", "BR SOFT");
                        saveData("BR SOFT");
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
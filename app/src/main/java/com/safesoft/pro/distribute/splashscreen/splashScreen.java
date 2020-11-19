package com.safesoft.pro.distribute.splashscreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.safesoft.pro.distribute.MainActivity;
import com.safesoft.pro.distribute.activation.ActivityActivation;
import com.safesoft.pro.distribute.R;

public class splashScreen extends AppCompatActivity {

  private String PREFS_ACTIVATION = "PREFES_ACTIVATION";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_screen);

    TextView test_spash = (TextView) findViewById(R.id.textsplash);
    Typeface face = Typeface.createFromAsset(getAssets(), "fonts/fff_tusj.ttf");
    test_spash.setTypeface(face);
    //thread for splash screen running
    Thread logoTimer = new Thread() {
      public void run() {
        try {
          sleep(2000);
        } catch (InterruptedException e) {
          Log.d("Exception", "Exception" + e);
        } finally {
          SharedPreferences prefs = getSharedPreferences(PREFS_ACTIVATION, MODE_PRIVATE);
          if(prefs.getBoolean("IS_ACTIVATED", false)){
            startActivity(new Intent(splashScreen.this, MainActivity.class));
          }else{
            startActivity(new Intent(splashScreen.this, ActivityActivation.class));
          }
        }
        finish();
      }
    };
    logoTimer.start();
  }
}

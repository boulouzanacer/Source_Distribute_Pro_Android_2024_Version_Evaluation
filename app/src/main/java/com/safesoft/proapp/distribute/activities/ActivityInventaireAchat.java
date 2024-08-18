package com.safesoft.proapp.distribute.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.achats.ActivityAchats;
import com.safesoft.proapp.distribute.activities.inventaire.ActivityInventaires;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

public class ActivityInventaireAchat extends AppCompatActivity {

    private RelativeLayout Inventaire, Achat;

    private Context mContext;


    private MediaPlayer mp;
    private final EventBus bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventaire_achat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inventaires/Achats");
        initViews();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initViews() {
        //  Inventaire = (RelativeLayout) findViewById(R.id.rlt_import_bon);
        Achat = findViewById(R.id.rlt_import_client);
        mContext = this;
    }

    public void onRelativeClick(View v) throws ExecutionException, InterruptedException {
        switch (v.getId()) {
            case R.id.rlt_inventaire:
                lunchActivity(ActivityInventaires.class);
                break;
            case R.id.rlt_achat:
                lunchActivity(ActivityAchats.class);
                break;
        }
    }

    protected void lunchActivity(Class cls) {

        Intent exported_inventaire_intent = new Intent(ActivityInventaireAchat.this, cls);
        exported_inventaire_intent.putExtra("SOURCE_EXPORT", "NOTEXPORTED");
        startActivity(exported_inventaire_intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Sound(R.raw.back);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void Sound(int SourceSound) {
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }
}

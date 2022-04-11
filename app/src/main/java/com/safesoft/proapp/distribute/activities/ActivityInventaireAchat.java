package com.safesoft.proapp.distribute.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.achats.ActivityAchat;
import com.safesoft.proapp.distribute.activities.inventaire.ActivityInventaire;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

public class ActivityInventaireAchat extends AppCompatActivity {

    private RelativeLayout Inventaire,Achat;

    private Context mContext;


    private  MediaPlayer mp;
    private EventBus bus = EventBus.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventaire_achat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Inventaires/Achats");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.black)));
        initViews();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void initViews(){
        //  Inventaire = (RelativeLayout) findViewById(R.id.rlt_import_bon);
        Achat = (RelativeLayout) findViewById(R.id.rlt_import_client);
        mContext = this;
    }

    public void onRelativeClick(View v) throws ExecutionException, InterruptedException {
        switch (v.getId()){
            case R.id.rlt_inventaire:
                lunchActivity(ActivityInventaire.class);
                break;
            case R.id.rlt_achat:
                lunchActivity(ActivityAchat.class);
                break;
        }
    }

    protected void lunchActivity(Class cls){
        startActivity(new Intent(ActivityInventaireAchat.this, cls));
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
        Sound(R.raw.back);
        super.onBackPressed();
    }

    public void Sound(int SourceSound){
        mp = MediaPlayer.create(this, SourceSound);
        mp.start();
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}

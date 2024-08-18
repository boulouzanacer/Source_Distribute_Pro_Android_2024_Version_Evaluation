package com.safesoft.proapp.distribute.activities.transfert;

import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.safesoft.proapp.distribute.adapters.ListViewAdapterTransfert2;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Transfer2;
import com.safesoft.proapp.distribute.R;

import java.util.ArrayList;

public class ActivityTransfert extends AppCompatActivity {

    private ArrayList<PostData_Transfer2> arrayOfTransfert2;
    private ListViewAdapterTransfert2 Transfert2Adapter;
    private String NUM_BON;
    private DATABASE controller;

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert2_detail);

        controller = new DATABASE(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NUM_BON = getIntent().getStringExtra("NUM_BON");
        getSupportActionBar().setTitle("Bon Transfert : " + NUM_BON);
        initView();

    }

    public void initView() {

        // Create the adapter to convert the array to views
        Transfert2Adapter = new ListViewAdapterTransfert2(this, R.layout.transfert2_items, getTransfert2());

        // Attach the adapter to a ListView
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(Transfert2Adapter);
    }

    protected ArrayList<PostData_Transfer2> getTransfert2() {
        arrayOfTransfert2 = new ArrayList<>();
        arrayOfTransfert2 = controller.select_transfer2_from_database("SELECT * FROM TRANSFERT2 WHERE NUM_BON = '" + NUM_BON + "'");
        return arrayOfTransfert2;
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

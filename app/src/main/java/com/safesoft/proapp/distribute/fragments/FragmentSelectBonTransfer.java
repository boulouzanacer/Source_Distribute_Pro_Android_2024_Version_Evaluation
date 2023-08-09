package com.safesoft.proapp.distribute.fragments;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityImportsExport;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListClient;
import com.safesoft.proapp.distribute.adapters.viewholder.ListViewAdapterListBon;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedBonEvent;
import com.safesoft.proapp.distribute.ftp.Ftp_export;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.apache.commons.net.ftp.FTPFile;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class FragmentSelectBonTransfer {


    private ListView listview;
    private ListViewAdapterListBon adapter;
    private ArrayList<String> listBon;

    private Activity activity;
    private AlertDialog dialog;
    private Context mcontext;

    private AppCompatImageButton btn_cancel;

    private  ArrayList<String> listFile;

    //PopupWindow display method
    public void showDialogbox(Activity activity, Context context,  ArrayList<String> listFile) {

        this.activity = activity;
        mcontext = context;
        this.listFile = listFile;
        EventBus.getDefault().register(this);


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_all_bons, null);
        dialogBuilder.setView(dialogview);


        initViews(dialogview);

        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();

        setListview();

        // Register as a subscriber
        //bus.register(this);

        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void initViews(View view) {

        btn_cancel = view.findViewById(R.id.cancel);
        listBon = new ArrayList<>();
        listBon = listFile;
        listview = (ListView) view.findViewById(R.id.list_bon);
        btn_cancel.setOnClickListener(v -> {
            EventBus.getDefault().unregister(this);
            dialog.dismiss();
        });

    }

    private void setListview() {

        adapter = new ListViewAdapterListBon(mcontext, listBon, dialog);
        listview.setAdapter(adapter);
        //bus.register(adapter);

    }


    @Subscribe()
    public void onBonTransfertSelectedEvent(SelectedBonEvent event) throws ParseException {
        // Do something
        String nom_bon = event.getBon();
        Ftp_export export_vente_ftp = new Ftp_export();
        export_vente_ftp.start(activity, "PRODUCT_LIST", nom_bon);
    }
}
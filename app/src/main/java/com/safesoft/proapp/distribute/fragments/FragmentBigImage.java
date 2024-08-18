package com.safesoft.proapp.distribute.fragments;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.rilixtech.materialfancybutton.MaterialFancyButton;

public class FragmentBigImage {

    MaterialFancyButton btn_cancel;
    Activity activity;
    AlertDialog dialog;

    public void showDialogbox(Activity activity) {

        this.activity = activity;


        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }

}
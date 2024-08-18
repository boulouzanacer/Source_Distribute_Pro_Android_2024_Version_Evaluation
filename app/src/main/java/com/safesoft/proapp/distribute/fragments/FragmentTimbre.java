package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.TimbreEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FragmentTimbre {

    MaterialFancyButton btn_timbre, btn_cancel;
    private Context mContext;
    private final double t = 0.00;
    private double val_timbre = 0.00;
    private final EventBus bus = EventBus.getDefault();
    Context mcontext;
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;

    //PopupWindow display method

    public void showDialogbox(Activity activity, double sent_montant_timbre) {

        this.activity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_timbre, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);


        btn_timbre = dialogview.findViewById(R.id.btn_timbre);
        btn_timbre.setBackgroundColor(Color.parseColor("#3498db"));
        btn_timbre.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_timbre.setTextSize(15);
        btn_timbre.setIconPosition(POSITION_LEFT);
        btn_timbre.setFontIconSize(30);

        btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);

        TextInputEditText montant_timbre = dialogview.findViewById(R.id.montant_timbre);

        val_timbre = sent_montant_timbre;

        montant_timbre.setText(String.valueOf(val_timbre));

        btn_timbre.setOnClickListener(v -> {

            val_timbre = Double.parseDouble(montant_timbre.getText().toString());
            TimbreEvent timbre_data = new TimbreEvent(val_timbre);
            // Post the event
            bus.post(timbre_data);

            dialog.dismiss();

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }


}
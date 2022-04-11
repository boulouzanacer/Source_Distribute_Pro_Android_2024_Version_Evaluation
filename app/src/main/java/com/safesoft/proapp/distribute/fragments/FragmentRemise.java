package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FragmentRemise {

    MaterialFancyButton btn_remise, btn_cancel;
    private EasyTextInputLayout montant_avant_remise;
    private EasyTextInputLayout montant_remise;
    private EasyTextInputLayout taux_remise;
    private EasyTextInputLayout montant_apres_remise;
    private Context mContext;
    private double val_avant_remise = 0.00, t = 0.00, val_taux = 0.00, val_apres_remise = 0.00, val_remise = 0.00;
    String str;

    private EventBus bus = EventBus.getDefault();
    Context mcontext;
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;

    //PopupWindow display method

    public void showDialogbox(Activity activity, double sent_montant_av_remise, double sent_montant_remise) {

        this.activity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater =activity.getLayoutInflater();
        View dialogview= inflater.inflate(R.layout.fragment_remise, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        btn_remise = (MaterialFancyButton) dialogview.findViewById(R.id.btn_remise);
        btn_remise.setBackgroundColor(Color.parseColor("#3498db"));
        btn_remise.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_remise.setTextSize(15);
        btn_remise.setIconPosition(POSITION_LEFT);
        btn_remise.setFontIconSize(30);

        btn_cancel = (MaterialFancyButton) dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);

        montant_avant_remise =  dialogview.findViewById(R.id.montant_avant_remise);
        montant_remise =  dialogview.findViewById(R.id.montant_remise);
        taux_remise =  dialogview.findViewById(R.id.taux_remise);
        montant_apres_remise =  dialogview.findViewById(R.id.montant_apres_remise);

        val_avant_remise = sent_montant_av_remise;
        val_remise = sent_montant_remise;

        montant_avant_remise.getEditText().setText(nf.format(val_avant_remise));
        montant_remise.getEditText().setText(String.valueOf(val_remise));

        onMontantRemiseChange();

        btn_remise.setOnClickListener(v -> {

            if(montant_avant_remise.getEditText().getText().length() > 0){

                RemiseEvent remise_data = new RemiseEvent(val_remise, val_taux, val_apres_remise);
                // Post the event
                bus.post(remise_data);

                dialog.dismiss();

            }else {
                montant_avant_remise.getEditText().setError("Montant obligatoire!!");
            }
        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        montant_remise.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!taux_remise.getEditText().isFocused()){
                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),
                        onMontantRemiseChange();

                    }catch (Exception e){
                        montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                        taux_remise.getEditText().getText().clear();
                    }
                }

            }
        });



        taux_remise.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!montant_remise.getEditText().isFocused()){
                    try{
                        //Double.valueOf(montant_avant_remise.getEditText().getText())
                        // montant_remise.getEditText().getText(
                        //  100-((M-R)/M*100),

                        onTauxChanged();


                    }catch (Exception e){
                        montant_apres_remise.getEditText().setText(nf.format(montant_avant_remise.getEditText().getText().toString()));
                        montant_remise.getEditText().getText().clear();
                    }
                }

            }
        });

    }


    void onMontantRemiseChange(){
        if(montant_remise.getEditText().getText().toString().isEmpty()){
            val_remise = 0.00;
        }else {
            val_remise = Double.parseDouble(montant_remise.getEditText().getText().toString());
        }
        val_apres_remise  = val_avant_remise - val_remise;
        montant_apres_remise.getEditText().setText(nf.format(val_apres_remise));
        val_taux = 100 - ((val_avant_remise - val_remise) / val_avant_remise * 100);
        taux_remise.getEditText().setText(nf.format(val_taux));
    }

    void onTauxChanged(){
        if(taux_remise.getEditText().getText().toString().isEmpty()){
            val_taux = 0.00;
        }else {
            val_taux = Double.parseDouble(taux_remise.getEditText().getText().toString());
        }
        val_remise = val_taux * val_avant_remise / 100;
        montant_remise.getEditText().setText(nf.format(val_remise));
        val_apres_remise = val_avant_remise - (val_taux * val_avant_remise / 100);
        montant_apres_remise.getEditText().setText(nf.format(val_apres_remise));
    }

}
package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.eventsClasses.RemiseEvent;
import com.safesoft.proapp.distribute.eventsClasses.ValidateFactureEvent;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FragmentValider {

    MaterialFancyButton btn_valider, btn_cancel;
    private TextInputEditText edt_ancien_sold, edt_montant_bon, edt_solde_actuel, edt_versement, edt_nouveau_solde;
    private double val_ancien_sold = 0.0, val_montant_bon = 0.0, val_solde_actuel = 0.0, val_versement = 0.0, val_nouveau_solde= 0.0;

    private EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;

    //PopupWindow display method
    public void showDialogbox(Activity activity, double recieve_ancien_solde, double recieve_montant_bon, double recieve_versement) {

        this.activity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater =activity.getLayoutInflater();
        View dialogview= inflater.inflate(R.layout.fragment_valider, null);
        dialogBuilder.setView(dialogview);
        dialogBuilder.setCancelable(false);
        dialogBuilder.create();
        dialog = dialogBuilder.show();


        //Specify the length and width through constants
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);


        btn_valider = (MaterialFancyButton) dialogview.findViewById(R.id.btn_remise);
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_valider.setTextSize(15);
        btn_valider.setIconPosition(POSITION_LEFT);
        btn_valider.setFontIconSize(30);

        btn_cancel = (MaterialFancyButton) dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);


        edt_ancien_sold =  dialogview.findViewById(R.id.ancien_sold);
        edt_montant_bon =  dialogview.findViewById(R.id.montant_bon_actuel);
        edt_solde_actuel =  dialogview.findViewById(R.id.solde_actuel);
        edt_versement =  dialogview.findViewById(R.id.versement);
        edt_nouveau_solde =  dialogview.findViewById(R.id.nouveau_solde);

        val_ancien_sold = recieve_ancien_solde;
        val_montant_bon = recieve_montant_bon;
        val_solde_actuel = recieve_ancien_solde + recieve_montant_bon;
        val_versement = recieve_versement;
        val_nouveau_solde = val_solde_actuel - val_versement;

        edt_ancien_sold.setText(nf.format(val_ancien_sold));
        edt_montant_bon.setText(nf.format(val_montant_bon));
        edt_solde_actuel.setText(nf.format(val_solde_actuel));
        edt_versement.setText(nf.format(val_versement));
        edt_nouveau_solde.setText(nf.format(val_nouveau_solde));

        //onVersementChange();

        btn_valider.setOnClickListener(v -> {

            if(edt_versement.getText().length() > 0){

               // RemiseEventRemiseEvent remise_data = new RemiseEvent(val_remise, val_taux, val_apres_remise);
                ValidateFactureEvent Valider_bon_versement = new ValidateFactureEvent( val_versement);
                // Post the event
                bus.post(Valider_bon_versement);

                dialog.dismiss();

            }else {
                edt_versement.setError("Montant versement obligatoire!!");
            }
        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });


        edt_versement.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                    try{
                        onVersementChange();

                    }catch (Exception e){

                    }

            }
        });

    }


    void onVersementChange(){
        if(edt_versement.getText().toString().isEmpty()){
            val_versement = 0.00;
        }else {
            val_versement = Double.parseDouble(edt_versement.getText().toString());
        }
        val_nouveau_solde  = val_solde_actuel - val_versement;
        edt_nouveau_solde.setText(nf.format(val_nouveau_solde));
    }

}
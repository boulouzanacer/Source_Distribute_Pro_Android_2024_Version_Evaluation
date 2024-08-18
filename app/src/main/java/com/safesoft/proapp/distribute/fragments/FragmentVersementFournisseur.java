package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
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
import com.safesoft.proapp.distribute.activities.client.ActivityClientDetail;
import com.safesoft.proapp.distribute.activities.fournisseur.ActivityFournisseurDetail;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_f;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class FragmentVersementFournisseur {

    MaterialFancyButton btn_valider, btn_cancel;
    private TextInputEditText edt_observation, edt_solde_actuel, edt_versement, edt_nouveau_solde;
    private double val_solde_actuel = 0.0, val_versement_actuel = 0.0, val_versement = 0.0, val_nouveau_solde = 0.0, val_nouveau_versement = 0.0;
    private final String observation = "";
    private String CODE_FRS;
    private Boolean IS_EDIT;
    private String recordid;

    private final EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;
    private DATABASE controller;
    private String NUM_BON_CARNET_F;

    //PopupWindow display method
    public void showDialogbox(Activity activity, double recieve_solde_fournisseur, double recieve_versement_fournisseur, double montant_versement, String recieve_observation, String recieve_code_frs, boolean ifIsEdit, String recordid) {

        this.activity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");
        controller = new DATABASE(activity);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_versement_fournisseur, null);
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


        btn_valider = dialogview.findViewById(R.id.btn_remise);
        btn_valider.setBackgroundColor(Color.parseColor("#3498db"));
        btn_valider.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_valider.setTextSize(15);
        btn_valider.setIconPosition(POSITION_LEFT);
        btn_valider.setFontIconSize(30);

        btn_cancel = dialogview.findViewById(R.id.btn_cancel);
        btn_cancel.setBackgroundColor(Color.parseColor("#3498db"));
        btn_cancel.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        btn_cancel.setTextSize(15);
        btn_cancel.setIconPosition(POSITION_LEFT);
        btn_cancel.setFontIconSize(30);


        //get num bon
        String selectQuery = "SELECT MAX(NUM_BON) AS max_id FROM CARNET_F WHERE NUM_BON IS NOT NULL";
        NUM_BON_CARNET_F = controller.select_max_num_bon(selectQuery);

        edt_solde_actuel = dialogview.findViewById(R.id.solde_actuel);
        edt_versement = dialogview.findViewById(R.id.versement);
        edt_observation = dialogview.findViewById(R.id.observation);
        edt_nouveau_solde = dialogview.findViewById(R.id.nouveau_solde);


        val_versement = montant_versement;
        val_solde_actuel = recieve_solde_fournisseur + val_versement;
        val_nouveau_solde = val_solde_actuel - val_versement;
        val_versement_actuel = recieve_versement_fournisseur - val_versement;
        val_nouveau_versement = val_versement_actuel + val_versement;
        this.recordid = recordid;


        edt_solde_actuel.setText(nf.format(val_solde_actuel));
        edt_versement.setText(nf.format(val_versement));
        edt_observation.setText(recieve_observation);
        edt_nouveau_solde.setText(nf.format(val_nouveau_solde));
        CODE_FRS = recieve_code_frs;
        IS_EDIT = ifIsEdit;

        btn_valider.setOnClickListener(v -> {

            if (Objects.requireNonNull(edt_versement.getText()).length() > 0) {
                PostData_Carnet_f carnet_f = new PostData_Carnet_f();


                // get date and time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
                // SimpleDateFormat format2 = new SimpleDateFormat("YYYY-MM-dd");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String formattedDate = df_show.format(c.getTime());
                String currentTime = sdf.format(c.getTime());

                carnet_f.carnet_date = formattedDate;
                carnet_f.carnet_heure = currentTime;
                carnet_f.recordid = recordid;
                carnet_f.carnet_num_bon = NUM_BON_CARNET_F;
                carnet_f.code_frs = CODE_FRS;
                carnet_f.carnet_achats = val_solde_actuel;
                carnet_f.carnet_versement = val_versement;
                carnet_f.carnet_remarque = edt_observation.getText().toString();

                if (!IS_EDIT) {
                    if (controller.insert_into_carnet_f(carnet_f, val_nouveau_solde, val_nouveau_versement)) {

                        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success!")
                                .setContentText(" Versement Ajouté! ")
                                .show();

                        ((ActivityFournisseurDetail) activity).Update_fournisseur_details();

                    } else {
                        // message erreur
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Opss!")
                                .setContentText(" Erreur d'insertion versement! ")
                                .show();
                    }
                } else {

                    if (controller.update_versement_fournisseur(carnet_f, val_nouveau_solde, val_nouveau_versement)) {

                        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success!")
                                .setContentText(" Versement Modifié! ")
                                .show();

                        ((ActivityClientDetail) activity).Update_client_details();

                    } else {
                        // message erreur
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Opss!")
                                .setContentText(" Erreur modification versement! ")
                                .show();
                    }
                }

                dialog.dismiss();
            } else {
                edt_versement.setError("Montant obligatoire!!");
            }
            /*if(edt_versement.getText().length() > 0){

               // RemiseEventRemiseEvent remise_data = new RemiseEvent(val_remise, val_taux, val_apres_remise);
                ValidateFactureEvent Valider_bon_versement = new ValidateFactureEvent( val_versement);
                // Post the event
                bus.post(Valider_bon_versement);

                dialog.dismiss();

            }else {
                edt_versement.setError("Montant versement obligatoire!!");
            }*/
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
                try {
                    onVersementChange();

                } catch (Exception e) {

                }

            }
        });

    }


    void onVersementChange() {
        if (edt_versement.getText().toString().isEmpty()) {
            val_versement = 0.00;
        } else {
            val_versement = Double.parseDouble(edt_versement.getText().toString());
        }
        val_nouveau_solde = val_solde_actuel - val_versement;
        val_nouveau_versement = val_versement_actuel + val_versement;
        edt_nouveau_solde.setText(nf.format(val_nouveau_solde));
    }

}
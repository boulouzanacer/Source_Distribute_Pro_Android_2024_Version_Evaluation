package com.safesoft.proapp.distribute.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.eventsClasses.SelectedFournisseurEvent;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;
import com.safesoft.proapp.distribute.utils.ToggleButtonGroupTableLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditFournisseur {

    MaterialFancyButton btn_valider, btn_cancel;
    TextInputEditText  edt_fournisseur_name, edt_fournisseur_adress, edt_fournisseur_telephone, edt_fournisseur_registre, edt_fournisseur_nif, edt_fournisseur_nis, edt_fournisseur_ai;
    private Context mContext;

    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;

    private final String PREFS = "ALL_PREFS";

    private String CODE_DEPOT, CODE_VENDEUR;

    PostData_Fournisseur created_fournisseur;
    private DATABASE controller;
    private PostData_Fournisseur old_fournisseur;

    //PopupWindow display method

    public void showDialogbox(Activity activity, Context context, String SOURCE_ACTIVITY, PostData_Fournisseur old_fournisseur) {

        this.mContext = context;
        this.activity = activity;
        this.controller = new DATABASE(activity);
        this.old_fournisseur = old_fournisseur;

        created_fournisseur = new PostData_Fournisseur();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_fourniseur, null);
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


        edt_fournisseur_name = dialogview.findViewById(R.id.edt_fournisseur_name);
        edt_fournisseur_adress = dialogview.findViewById(R.id.edt_fournisseur_adress);
        edt_fournisseur_telephone = dialogview.findViewById(R.id.edt_fournisseur_telephone);
        edt_fournisseur_registre = dialogview.findViewById(R.id.edt_fournisseur_registre);
        edt_fournisseur_nif = dialogview.findViewById(R.id.edt_fournisseur_nif);
        edt_fournisseur_nis = dialogview.findViewById(R.id.edt_fournisseur_nis);
        edt_fournisseur_ai = dialogview.findViewById(R.id.edt_fournisseur_ai);

        if(SOURCE_ACTIVITY.equals("EDIT_FOURNISSEUR")){

            edt_fournisseur_name.setText(old_fournisseur.fournis);
            edt_fournisseur_adress.setText(old_fournisseur.adresse);
            edt_fournisseur_telephone.setText(old_fournisseur.tel);
            edt_fournisseur_registre.setText(old_fournisseur.rc);
            edt_fournisseur_nif.setText(old_fournisseur.ifiscal);
            edt_fournisseur_nis.setText(old_fournisseur.nis);
            edt_fournisseur_ai.setText(old_fournisseur.ai);
        }

        btn_valider.setOnClickListener(v -> {
            boolean hasError = false;

            if (edt_fournisseur_name.getText().length() <= 0) {

                edt_fournisseur_name.setError("Nom obligatoire!!");
                hasError = true;
            }
            if (edt_fournisseur_adress.getText().length() <= 0 ) {
                edt_fournisseur_adress.setError("Adresse obligatoire!!");
                hasError = true;
            }

            if (edt_fournisseur_telephone.getText().length() <= 0 ) {
                edt_fournisseur_telephone.setError("Telephone obligatoire!!");
                hasError = true;
            }

            if (!hasError) {

                created_fournisseur.fournis = edt_fournisseur_name.getText().toString();
                created_fournisseur.adresse =  edt_fournisseur_adress.getText().toString();
                created_fournisseur.tel = edt_fournisseur_telephone.getText().toString();
                created_fournisseur.rc = edt_fournisseur_registre.getText().toString();
                created_fournisseur.ifiscal = edt_fournisseur_nif.getText().toString();
                created_fournisseur.nis = edt_fournisseur_nis.getText().toString();
                created_fournisseur.ai = edt_fournisseur_ai.getText().toString();

                created_fournisseur.isNew = 1;

                SharedPreferences prefs2 = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
                CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");


                if(SOURCE_ACTIVITY.equals("EDIT_FOURNISSEUR")){

                    created_fournisseur.code_frs = old_fournisseur.code_frs;

                    //Update client into database,
                    boolean state_insert_fournisseur = controller.update_fournisseur(created_fournisseur);
                    if(state_insert_fournisseur){

                        Crouton.makeText(activity, "Fournisseur bien modifier", Style.INFO).show();

                        SelectedFournisseurEvent added_fournisseur = new SelectedFournisseurEvent(created_fournisseur);
                        bus.post(added_fournisseur);

                        dialog.dismiss();

                    }else{
                        Crouton.makeText(activity, "Problème de mise à jour client", Style.ALERT).show();
                    }
                }else {

                    if(CODE_DEPOT.equals("000000")){
                        created_fournisseur.code_frs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"_"+ CODE_VENDEUR+"";

                    }else{
                        created_fournisseur.code_frs = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"_"+ CODE_DEPOT+"";
                    }

                    //insert client into database,
                    boolean state_update_client = controller.insert_into_fournisseur(created_fournisseur);
                    if(state_update_client){

                        Crouton.makeText(activity, "Fournisseur bien ajouté", Style.INFO).show();

                        SelectedFournisseurEvent added_fournisseur = new SelectedFournisseurEvent(created_fournisseur);
                        bus.post(added_fournisseur);

                        dialog.dismiss();

                    }else{
                        Crouton.makeText(activity, "Problème insertion", Style.ALERT).show();
                    }
                }

            }

        });

        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }
}
package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.rt.printerlibrary.driver.usb.rw.Pos;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.SelectedClientEvent;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.TimeUnit;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class FragmentNewEditClient {

    MaterialFancyButton btn_valider, btn_cancel;
    TextInputEditText  edt_client_name, edt_client_adress, edt_client_telephone, edt_client_registre, edt_client_nif, edt_client_nis, edt_client_ai;
    RadioGroup radioGroup_mode_tarif;
    RadioButton selectedRadioButton;
    private Context mContext;

    EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;

    private final String PREFS = "ALL_PREFS";

    private String CODE_DEPOT, CODE_VENDEUR;

    PostData_Client created_client;
    private DATABASE controller;
    private PostData_Client old_client;

    //PopupWindow display method

    public void showDialogbox(Activity activity, Context context, String SOURCE_ACTIVITY, PostData_Client old_client) {

        this.mContext = context;
        this.activity = activity;
        this.controller = new DATABASE(activity);
        this.old_client = old_client;

        created_client = new PostData_Client();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_add_client, null);
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


        edt_client_name = dialogview.findViewById(R.id.edt_client_name);
        edt_client_adress = dialogview.findViewById(R.id.edt_client_adress);
        edt_client_telephone = dialogview.findViewById(R.id.edt_client_telephone);
        edt_client_registre = dialogview.findViewById(R.id.edt_client_registre);
        edt_client_nif = dialogview.findViewById(R.id.edt_client_nif);
        edt_client_nis = dialogview.findViewById(R.id.edt_client_nis);
        edt_client_ai = dialogview.findViewById(R.id.edt_client_ai);

        radioGroup_mode_tarif = (RadioGroup) dialogview.findViewById(R.id.rd_mode_tarif);
        // onMontantRemiseChange();

        if(SOURCE_ACTIVITY.equals("EDIT_CLIENT")){

            edt_client_name.setText(old_client.client);
            edt_client_adress.setText(old_client.adresse);
            edt_client_telephone.setText(old_client.tel);
            edt_client_registre.setText(old_client.rc);
            edt_client_nif.setText(old_client.ifiscal);
            edt_client_nis.setText(old_client.nis);
            edt_client_ai.setText(old_client.ai);

            if(old_client.mode_tarif.equals("0")){
                radioGroup_mode_tarif.check(R.id.rb_1);
            }
            if(old_client.mode_tarif.equals("1")){
                radioGroup_mode_tarif.check(R.id.rb_2);
            }
            if(old_client.mode_tarif.equals("2")){
                radioGroup_mode_tarif.check(R.id.rb_3);
            }
            if(old_client.mode_tarif.equals("3")){
                radioGroup_mode_tarif.check(R.id.rb_4);
            }
        }

        btn_valider.setOnClickListener(v -> {
            boolean hasError = false;

            if (edt_client_name.getText().length() <= 0) {

                edt_client_name.setError("Nom obligatoire!!");
                hasError = true;
            }
            if (edt_client_adress.getText().length() <= 0 ) {
                edt_client_adress.setError("Adresse obligatoire!!");
                hasError = true;
            }

            if (edt_client_telephone.getText().length() <= 0 ) {
                edt_client_telephone.setError("Telephone obligatoire!!");
                hasError = true;
            }

            if (!hasError) {


                SharedPreferences prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                if(prefs.getString("PV_ID", "PV1").equals("PV1")){
                    created_client.mode_tarif = "1";
                }else if(prefs.getString("PV_ID", "PV1").equals("PV2")){
                    created_client.mode_tarif = "2";
                }else{
                    created_client.mode_tarif = "3";
                }

                created_client.client = edt_client_name.getText().toString();
                created_client.adresse =  edt_client_adress.getText().toString();
                created_client.tel = edt_client_telephone.getText().toString();
                created_client.rc = edt_client_registre.getText().toString();
                created_client.ifiscal = edt_client_nif.getText().toString();
                created_client.nis = edt_client_nis.getText().toString();
                created_client.ai = edt_client_ai.getText().toString();

                created_client.mode_tarif ="0";

                int selectedRadioButtonId = radioGroup_mode_tarif.getCheckedRadioButtonId();
                if (selectedRadioButtonId != -1) {
                    selectedRadioButton = dialogview.findViewById(selectedRadioButtonId);
                    String selectedRbText = selectedRadioButton.getText().toString();
                    if(selectedRbText.equals("Libre")){
                        created_client.mode_tarif ="0";
                    }
                    if(selectedRbText.equals("Tarif 1")){
                        created_client.mode_tarif ="1";
                    }

                    if(selectedRbText.equals("Tarif 2")){
                        created_client.mode_tarif ="2";
                    }
                    if(selectedRbText.equals("Tarif 3")){
                        created_client.mode_tarif ="3";
                    }
                }

                SharedPreferences prefs2 = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                CODE_DEPOT = prefs2.getString("CODE_DEPOT", "000000");
                CODE_VENDEUR = prefs2.getString("CODE_VENDEUR", "000000");



                if(SOURCE_ACTIVITY.equals("EDIT_CLIENT")){

                    created_client.code_client = old_client.code_client;

                    //Insert client into database,
                    boolean state_insert_client = controller.update_client(created_client);
                    if(state_insert_client){

                        Crouton.makeText(activity, "Client bien modifier", Style.INFO).show();

                        SelectedClientEvent added_client = new SelectedClientEvent(created_client);
                        bus.post(added_client);

                        dialog.dismiss();

                    }else{
                        Crouton.makeText(activity, "Problème de mise à jour client", Style.ALERT).show();
                    }
                }else {

                    if(CODE_DEPOT.equals("000000")){
                        created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"_"+ CODE_VENDEUR+"";

                    }else{
                        created_client.code_client = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) +"_"+ CODE_DEPOT+"";
                    }

                    //update client into database,
                    boolean state_update_client = controller.insert_into_client(created_client);
                    if(state_update_client){

                        Crouton.makeText(activity, "Client bien ajouté", Style.INFO).show();

                        SelectedClientEvent added_client = new SelectedClientEvent(created_client);
                        bus.post(added_client);

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
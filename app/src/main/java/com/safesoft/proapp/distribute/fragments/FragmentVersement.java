package com.safesoft.proapp.distribute.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emmasuzuki.easyform.EasyTextInputLayout;
import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.ActivityClientDetail;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Carnet_c;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentVersement extends SwipeAwayDialogFragment {

    private ArrayList<PostData_Client> clients;
    private DATABASE controller;
    private MaterialFancyButton facebookLoginBtn;
    private EasyTextInputLayout montant, observation;
    private String CODE_CLIENT;
    private Boolean IS_EDIT;

    private String formattedDate;
    private String formattedDate_Show;
    private String currentTime;

    private Context mContext;
    private String OLD_VERSEMENT;

    public FragmentVersement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controller = new DATABASE(getActivity());
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_versement, container, false);

        clients = new ArrayList<>();

        facebookLoginBtn =  (MaterialFancyButton) rootView.findViewById(R.id.btn_spotify);
        facebookLoginBtn.setBackgroundColor(Color.parseColor("#3498db"));
        facebookLoginBtn.setFocusBackgroundColor(Color.parseColor("#5474b8"));
        facebookLoginBtn.setTextSize(15);
        facebookLoginBtn.setIconFont("fontawesome.ttf");
        facebookLoginBtn.setIconPosition(POSITION_LEFT);
        facebookLoginBtn.setFontIconSize(30);

        montant = (EasyTextInputLayout) rootView.findViewById(R.id.montant_versement_edittext);
        observation = (EasyTextInputLayout) rootView.findViewById(R.id.observation_edittext);

        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(montant.getEditText().getText().length() > 0){
                    PostData_Carnet_c carnet_c = new PostData_Carnet_c();
                    carnet_c.code_client = CODE_CLIENT ;

                    // get date and time
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    formattedDate_Show = df_show.format(c.getTime());
                    formattedDate = df_save.format(c.getTime());
                    currentTime = sdf.format(c.getTime());

                    carnet_c.carnet_date = formattedDate;
                    carnet_c.carnet_heure = currentTime;
                    carnet_c.carnet_versement = montant.getEditText().getText().toString();
                    carnet_c.carnet_remarque = observation.getEditText().getText().toString();

                    if(!IS_EDIT){
                        if(controller.Insert_into_carnet_c(carnet_c)){
                            // message success
                            // update adapter

                            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText(" Versement Ajouté! ")
                                    .show();

                            ((ActivityClientDetail) mContext).Update_client_details();

                            getDialog().dismiss();
                        }else{
                            // message erreur
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Opss!")
                                    .setContentText(" Erreur d'insertion versement! ")
                                    .show();
                        }
                    }else{

                        if(controller.Update_versement(carnet_c, OLD_VERSEMENT )){
                            // message success
                            // update adapter

                            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText(" Versement Modifié! ")
                                    .show();

                            ((ActivityClientDetail) mContext).Update_client_details();

                            getDialog().dismiss();
                        }else{
                            // message erreur
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("Opss!")
                                    .setContentText(" Erreur modification versement! ")
                                    .show();
                        }
                    }

                }else {
                    montant.getEditText().setError("Montant obligatoire!!");
                }
            }
        });


        CODE_CLIENT = getArguments().getString("CODE_CLIENT");
        IS_EDIT = getArguments().getBoolean("IS_EDIT");
        if(IS_EDIT){
            PostData_Carnet_c carnet_c = new PostData_Carnet_c();
            carnet_c = controller.select_carnet_c_from_database_single("SELECT * FROM Carnet_c WHERE CODE_CLIENT = '"+ CODE_CLIENT +"' ");
            montant.getEditText().setText(carnet_c.carnet_versement);
            observation.getEditText().setText(carnet_c.carnet_remarque);
            OLD_VERSEMENT = carnet_c.carnet_versement;
        }

        return rootView;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void finich_fragment(){
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

}

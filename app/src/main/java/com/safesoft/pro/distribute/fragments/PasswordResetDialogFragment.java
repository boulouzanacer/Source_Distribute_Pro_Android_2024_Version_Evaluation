package com.safesoft.pro.distribute.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by UK2016 on 12/06/2017.
 */

public class PasswordResetDialogFragment extends DialogFragment {

  private EditText mPassword;
  private Button reset;
  private DATABASE controller;

  public PasswordResetDialogFragment() {
    // Empty constructor is required for DialogFragment
    // Make sure not to add arguments to the constructor
    // Use `newInstance` instead as shown below
  }

  public static PasswordResetDialogFragment newInstance(String title) {
    PasswordResetDialogFragment frag = new PasswordResetDialogFragment();
    Bundle args = new Bundle();
    args.putString("title", title);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.dialogfragment_password_reset, container);
  }

  @Override
  public void onStart() {
    controller = new DATABASE(getActivity());
    super.onStart();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // Get field from view
    mPassword = (EditText) view.findViewById(R.id.txt_pasword);
    reset = (Button) view.findViewById(R.id.reset);
    reset.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(mPassword.getText().toString().equals("safesoft") || mPassword.getText().toString().equals("SAFESOFT")){
          new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                  .setTitleText("Réinitialisation")
                  .setContentText("Voulez-vous vraiment réinitialiser cette appareil, attention cette opération supprimera tous vos données?!")
                  .setCancelText("Annuler")
                  .setConfirmText("Continuer")
                  .showCancelButton(true)
                  .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                      sDialog.dismiss();
                      getDialog().dismiss();
                    }
                  })
                  .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                      controller.ResetPda();
                      sDialog.dismiss();
                      getDialog().dismiss();
                    }
                  })
                  .show();
        }else{
          Animation shake = AnimationUtils.loadAnimation(getActivity(),  R.anim.shakanimation);
          reset.startAnimation(shake);
          //getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        }
      }
    });
    // Fetch arguments from bundle and set title
    String title = getArguments().getString("title", "Password ? ");
    getDialog().setTitle(title);
    // Show soft keyboard automatically and request focus to field
    mPassword.requestFocus();
    getDialog().getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
  }
}

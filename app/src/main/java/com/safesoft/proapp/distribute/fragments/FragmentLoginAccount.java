package com.safesoft.proapp.distribute.fragments;

import static com.rilixtech.materialfancybutton.MaterialFancyButton.POSITION_LEFT;
import static com.safesoft.proapp.distribute.MainActivity.getAndroidID;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.cloud.ConnectCloud;
import com.safesoft.proapp.distribute.eventsClasses.ConnectedCloudEvent;
import com.safesoft.proapp.distribute.eventsClasses.onConnectCloudEvent;
import com.safesoft.proapp.distribute.utils.Env;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class FragmentLoginAccount {

    MaterialFancyButton btn_valider, btn_cancel;
    private TextInputEditText edt_email, edt_password;
    private TextView txtv_info_compte_cloud;

    private final EventBus bus = EventBus.getDefault();
    Activity activity;
    AlertDialog dialog;
    private NumberFormat nf;
    private final String PREFS = "ALL_PREFS";
    private SharedPreferences pref;

    //PopupWindow display method
    public void showDialogbox(Activity activity, String old_email, String old_password) {

        this.activity = activity;
        // Declare US print format
        nf = NumberFormat.getInstance(Locale.US);
        ((DecimalFormat) nf).applyPattern("####0.00");

        EventBus.getDefault().register(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogview = inflater.inflate(R.layout.fragment_login_account, null);
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

        pref = activity.getSharedPreferences(PREFS, 0);

        btn_valider = dialogview.findViewById(R.id.btn_connect);
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


        edt_email = dialogview.findViewById(R.id.edt_email);
        edt_password = dialogview.findViewById(R.id.edt_password);
        txtv_info_compte_cloud = dialogview.findViewById(R.id.txtv_info_compte_cloud);
        txtv_info_compte_cloud.setText("");

        edt_email.setText(old_email);
        edt_password.setText(old_password);


        btn_valider.setOnClickListener(v -> {

            String current_version = "0";
            String versionCode = "0";
            String android_unique_id = "0";
            String seriel_number = "0";
            int activation_code = 0;
            String revendeur = "";


            try {
                PackageManager pm = activity.getPackageManager();
                String packageName = activity.getPackageName();
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);

                current_version = String.valueOf(packageInfo.versionName);
                versionCode = String.valueOf(packageInfo.versionCode);
                android_unique_id = getAndroidID(activity);
                seriel_number = pref.getString("NUM_SERIE", "0");
                revendeur = pref.getString("REVENDEUR", "0");
                activation_code = pref.getInt("CODE_ACTIVATION", 0);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }


            String email = "";
            String password = "";

            if (Objects.requireNonNull(edt_email.getText()).length() > 0) {
                email = Objects.requireNonNull(edt_email.getText()).toString();
            } else {
                edt_email.setError("Email est obligatoire!!");
            }

            if (Objects.requireNonNull(edt_password.getText()).length() > 0) {
                password = Objects.requireNonNull(edt_password.getText()).toString();
            } else {
                edt_password.setError("Mot de passe est obligatoire!!");
            }

            // call and check validate account.
            new ConnectCloud().execute(Env.URL_CONNECT_EMAIL, email, password);


        });

        btn_cancel.setOnClickListener(v -> {
            EventBus.getDefault().unregister(this);
            dialog.dismiss();
        });
    }

    @Subscribe
    public void onConnectCloudEvent(onConnectCloudEvent event) {
         onConnectCloudEvent recieved_connect_cloud_event = event;
        if(recieved_connect_cloud_event.getCode() == 200){
            if(recieved_connect_cloud_event.getIsConnect()){

                txtv_info_compte_cloud.setTextColor(activity.getResources().getColor(R.color.tag_green_bg));
                txtv_info_compte_cloud.setText(recieved_connect_cloud_event.getMessage());
                EventBus.getDefault().post(new ConnectedCloudEvent(edt_email.getText().toString(), edt_password.getText().toString()));
               // dialog.dismiss();

            }else{
                    Log.v("TAG", "Email not exist");
                    txtv_info_compte_cloud.setTextColor(activity.getResources().getColor(R.color.orange));
                    txtv_info_compte_cloud.setText(recieved_connect_cloud_event.getMessage());
            }

        }else if(recieved_connect_cloud_event.getCode() == 205) {
            txtv_info_compte_cloud.setTextColor(activity.getResources().getColor(R.color.orange));
            txtv_info_compte_cloud.setText(event.getMessage());
        }
    }

}
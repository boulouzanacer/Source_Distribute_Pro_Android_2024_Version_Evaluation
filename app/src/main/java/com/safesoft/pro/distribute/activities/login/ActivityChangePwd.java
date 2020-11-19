package com.safesoft.pro.distribute.activities.login;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.safesoft.pro.distribute.R;


import butterknife.ButterKnife;

public class ActivityChangePwd extends AppCompatActivity {

    private static final String TAG = "ActivityChangePwd";
    private String PREFS_LOGIN = "ConfigPassword";

    private EditText _oldpasswordText;
    private EditText _passwordText;
    private EditText _reEnterPasswordText;
    private Button _signupButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _oldpasswordText = (EditText) findViewById(R.id.input_password_old);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");


        final ProgressDialog progressDialog = new ProgressDialog(ActivityChangePwd.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Changement de mot de passe ...");
        progressDialog.show();


        if (!validate()) {
            onSignupFailed();
            progressDialog.dismiss();
            return;
        }

        _signupButton.setEnabled(false);

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE).edit();
        editor.putString("PASSWORD", _passwordText.getText().toString());
        editor.commit();



        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Erreur changement mot de passe !", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public boolean validate() {
        boolean valid = true;

        String oldpassword = _oldpasswordText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        SharedPreferences prefs_login = getSharedPreferences(PREFS_LOGIN, MODE_PRIVATE);

        if(!(prefs_login.getString("PASSWORD", "0000").toString().equals(oldpassword)) && !(prefs_login.getString("PASSWORD", "0000").toString().equals("0000"))){
            valid = false;
            _oldpasswordText.setError("Remettre l'ancien mot de passe");
        }else{
            _oldpasswordText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Entre 4 et 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10) {
            _reEnterPasswordText.setError("Entre 4 et 10 alphanumeric characters");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        if (!(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Les deux mots de passe sont pas identiques");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
    }
}
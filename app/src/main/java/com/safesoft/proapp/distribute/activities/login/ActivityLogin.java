package com.safesoft.proapp.distribute.activities.login;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.safesoft.proapp.distribute.activities.ActivitySetting;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.activities.client.ActivityClients;
import com.safesoft.proapp.distribute.activities.map.ActivityMaps;
import com.safesoft.proapp.distribute.fragments.FragmentNewEditClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ActivityLogin extends AppCompatActivity {
    private static final int REQUEST_SIGNUP = 0;

    private EditText _passwordText;
    private Button _loginButton;
    private final String PREFS = "ALL_PREFS";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        Toolbar toolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Connexion paramètres");
        }

        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(ActivityLogin.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Connexion...");
        progressDialog.show();

        if (!validate()) {
            onLoginFailed();
            progressDialog.dismiss();
            return;
        }

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }


    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        startActivity(new Intent(ActivityLogin.this, ActivitySetting.class));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }


    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Erreur accès au paramètres !!", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }


    public boolean validate() {

        boolean valid = true;
        SharedPreferences prefs_login = getSharedPreferences(PREFS, MODE_PRIVATE);
        String password = _passwordText.getText().toString();

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("Entre 4 et 10 caractère");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        String password_s = prefs_login.getString("PASSWORD", "0000");

        if (password.compareTo(password_s) == 0 || checkPassword(password)) {
            _passwordText.setError(null);
        } else {
            valid = false;
            _passwordText.setError("Mot de passe incorrect");
        }

        return valid;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkPassword(String input) {
        String storedHash = "182f212011b0857064ff3d25f62304c792628816f46aca950d0d81f99884cc21";

        String inputHash = sha256(input);
        return inputHash.equals(storedHash);
    }

    private String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

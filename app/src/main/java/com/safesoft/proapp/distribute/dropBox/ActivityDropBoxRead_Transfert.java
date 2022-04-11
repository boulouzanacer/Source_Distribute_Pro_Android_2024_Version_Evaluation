

package com.safesoft.proapp.distribute.dropBox;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Transfer1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ActivityDropBoxRead_Transfert extends Activity {
  private static final String TAG = "DBRoulette";
  private static String ACCESS_TOKEN = "";

  private static final int ACCES_READ_WRITE_EXTERNEL_STORAGE = 1;
  private Boolean checkPermission = false;
  DbxClientV2 client = null;

  ///////////////////////////////////////////////////////////////////////////
  //                      Your app-specific settings.                      //
  ///////////////////////////////////////////////////////////////////////////

  // Replace this with your app key and secret assigned by Dropbox.
  // Note that this is a really insecure way to do this, and you shouldn't
  // ship code which contains your key & secret in such an obvious way.
  // Obfuscation is good.
  final static private String APP_KEY = "oqzeht4nuhmlmap";
  final static private String APP_SECRET = "cm8shfl7yzelaku";

  // If you'd like to change the access type to the full Dropbox instead of
  // an app folder, change this value.

  ///////////////////////////////////////////////////////////////////////////
  //                      End app-specific settings.                       //
  ///////////////////////////////////////////////////////////////////////////

  // You don't need to change these, leave them alone.
  final static private String ACCOUNT_PREFS_NAME = "prefs";
  final static private String ACCESS_TOKEN_PREFERS = "ACCESS_TOKEN";
  private DbxRequestConfig config = null;

  private String PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
  private String PREFS_CONNEXION = "ConfigNetwork";

  // Android widgets
  private Button mExportCSV, ClearDrbx;

  private String DEPOT_DIR ,CODE_VENDEUR_DIR, DATABASE_NAME;


  private String mCameraFileName;

  private DATABASE controller;

  // In the class declaration section:
  private DropboxAPI<AndroidAuthSession> mDBApi;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null)
    {
      mCameraFileName = savedInstanceState.getString("mCameraFileName");
    }


    // Basic Android widgets
    setContentView(R.layout.activity_dropbox_send);

    controller = new DATABASE(this);

    // This is the button to take a photo
    mExportCSV = (Button)findViewById(R.id.export_button);
    ClearDrbx = (Button)findViewById(R.id.clear_button);

    mExportCSV.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if(ISassociateCompte()){
          requestPermission();
          if(checkPermission){
            Exporter_data_to_dropbox dropbox = new Exporter_data_to_dropbox();
            dropbox.execute();

          }
        }

      }
    });

    ClearDrbx.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        clearKeys();
        new SweetAlertDialog(ActivityDropBoxRead_Transfert.this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Réussi!")
                .setContentText("DropBox est bien diassocié! ")
                .show();
      }
    });

    SharedPreferences prefs = getSharedPreferences(PREFS_CODE_DEPOT, MODE_PRIVATE);
    DEPOT_DIR = prefs.getString("CODE_DEPOT", "000000");
    CODE_VENDEUR_DIR = prefs.getString("CODE_VENDEUR", "000000");

    prefs = getSharedPreferences(PREFS_CONNEXION, MODE_PRIVATE);
    String path_ddb = prefs.getString("path", "000000");

    File file = new File(path_ddb);
    DATABASE_NAME = file.getName();



    ISassociateCompte();


  }

  protected boolean ISassociateCompte(){
    boolean check = false;
    ACCESS_TOKEN = getKeys();
    if(ACCESS_TOKEN == null){
      // And later in some initialization function:
      AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
      AndroidAuthSession session = new AndroidAuthSession(appKeys);
      mDBApi = new DropboxAPI<AndroidAuthSession>(session);


      // MyActivity below should be your activity class name
      mDBApi.getSession().startOAuth2Authentication(ActivityDropBoxRead_Transfert.this);
    }else{
      check = true;
    }
    return check;
  }




  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putString("mCameraFileName", mCameraFileName);
    super.onSaveInstanceState(outState);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(ACCESS_TOKEN == null){
      if (mDBApi.getSession().authenticationSuccessful()) {
        try {
          // Required to complete auth, sets the access token on the session
          mDBApi.getSession().finishAuthentication();

          ACCESS_TOKEN = mDBApi.getSession().getOAuth2AccessToken();
          storeKeys(ACCESS_TOKEN);

        } catch (IllegalStateException e) {
          Log.i("DbAuthLog", "Error authenticating", e);
        }
      }
    }

  }




  private void showToast(String msg) {
    Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
    error.show();
  }

  /**
   * Shows keeping the access keys returned from Trusted Authenticator in a local
   * store, rather than storing user name & password, and re-authenticating each
   * time (which is not to be done, ever).
   *
   * @return Array of [access_key, access_secret], or null if none stored
   */


  private String getKeys() {
    SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
    String ACCESS_TOKEN = prefs.getString(ACCESS_TOKEN_PREFERS, null);
    return ACCESS_TOKEN;
  }


  /**
   * Shows keeping the access keys returned from Trusted Authenticator in a local
   * store, rather than storing user name & password, and re-authenticating each
   * time (which is not to be done, ever).
   */

  private void storeKeys(String token) {
    // Save the access key for later
    SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
    Editor edit = prefs.edit();
    edit.putString(ACCESS_TOKEN_PREFERS, token);
    edit.commit();
  }


  private void clearKeys() {
    SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
    Editor edit = prefs.edit();
    edit.clear();
    edit.commit();
  }




  //==================== AsyncTask TO Load produits from server and store them in the local database (sqlite)
  public class Exporter_data_to_dropbox extends AsyncTask<Void, Integer, Integer> {

    Integer flag = 0;
    int compt = 0;
    int allrows = 0;
    private Boolean First = true;

    private ArrayList<PostData_Transfer1> bon1s;
    final String FILES = "/PMEPRO_FOLDER";
    //String path= Environment.getExternalStorageDirectory().getPath()+FILES; // Folder path
    String path = "https://www.dropbox.com/s/8s3b8umnmxdkr8b/TRANSFERT-FOURGON-000076.BA?dl=0";
    // File myFile;
    File[] files;
    private OutputStreamWriter myOutWriter;
    private FileOutputStream fOut;

    private ArrayList<String> listElementItem;

    public Exporter_data_to_dropbox() {

    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      //  progressDialogConfig();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

      return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {

      try {
        listElementItem = new ArrayList<>();
       // Toast.makeText(getApplicationContext(),""+Environment.getExternalStorageDirectory().getPath()+FILES,Toast.LENGTH_LONG).show();

        URLConnection conn = new URL(path).openConnection();
        conn.connect();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                is, "UTF-8"), 8);
        String line = null;
        while ((line = reader.readLine()) != null) {
          listElementItem.add(line);
          System.out.println("psst"+line);
          Toast.makeText (getApplicationContext(),"psst"+line,Toast.LENGTH_LONG).show();
        }
        is.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
     // Toast.makeText(getApplicationContext(),""+Environment.getExternalStorageDirectory().getPath()+FILES,Toast.LENGTH_LONG).show();

      super.onPostExecute(integer);
    }
  }

  public void requestPermission(){

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ACCES_READ_WRITE_EXTERNEL_STORAGE);
      checkPermission = false;
    }else{
      checkPermission = true;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode == ACCES_READ_WRITE_EXTERNEL_STORAGE){
      Exporter_data_to_dropbox dropbox = new Exporter_data_to_dropbox();
      dropbox.execute();

    }
  }


}

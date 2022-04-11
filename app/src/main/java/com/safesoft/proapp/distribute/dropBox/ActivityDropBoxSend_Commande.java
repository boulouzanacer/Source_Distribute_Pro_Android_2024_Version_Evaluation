/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.safesoft.proapp.distribute.dropBox;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ActivityDropBoxSend_Commande extends Activity {
  private static final String TAG = "DBRoulette";
  private static String ACCESS_TOKEN = "";

  private static final int ACCES_READ_WRITE_EXTERNEL_STORAGE = 1;
  private Boolean checkPermission = false;

  ///////////////////////////////////////////////////////////////////////////
  //                      Your app-specific settings.                      //
  ///////////////////////////////////////////////////////////////////////////

  // Replace this with your app key and secret assigned by Dropbox.
  // Note that this is a really insecure way to do this, and you shouldn't
  // ship code which contains your key & secret in such an obvious way.
  // Obfuscation is good.
  final static private String APP_KEY = "n5ejiviq9apdu9x";
  final static private String APP_SECRET = "p73q58wv0cz0gzo";

  // If you'd like to change the access type to the full Dropbox instead of
  // an app folder, change this value.

  ///////////////////////////////////////////////////////////////////////////
  //                      End app-specific settings.                       //
  ///////////////////////////////////////////////////////////////////////////

  // You don't need to change these, leave them alone.
  final static private String ACCOUNT_PREFS_NAME = "prefs";
  final static private String ACCESS_TOKEN_PREFERS = "ACCESS_TOKEN";

  private String PREFS_CODE_DEPOT = "CODE_DEPOT_PREFS";
  private String PREFS_CONNEXION = "ConfigNetwork";

  // Android widgets
  private Button mExportCSV, ClearDrbx;

  private String DEPOT_DIR, CODE_VENDEUR_DIR ;
  private String DATABASE_NAME ;


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
        ClearDrbx.setText("Assosier dropBox");
        new SweetAlertDialog(ActivityDropBoxSend_Commande.this, SweetAlertDialog.SUCCESS_TYPE)
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
      mDBApi.getSession().startOAuth2Authentication(ActivityDropBoxSend_Commande.this);
    }else{
      check = true;
    }
    return check;
  }

  protected void loadListFile(){
    String path = Environment.getExternalStorageDirectory().toString()+"/PMEPRO_FOLDER";
    ListView listview = (ListView) findViewById(R.id.list_file_dropbox);
    Log.d("Files", "Path: " + path);
    File directory = new File(path);
    if (directory.exists()) {
      File[] files = directory.listFiles();
      Log.d("Files", "Size: "+ files.length);
      String[] mApps = new String[files.length];
      for (int i = 0; i < files.length; i++)
      {
        Log.d("Files", "FileName:" + files[i].getName());
        mApps[i] = files[i].getName();
      }
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mApps);
      listview.setAdapter(adapter);
    }else{
      Toast.makeText(ActivityDropBoxSend_Commande.this, "Vérifier la permission ( WRITE_EXTERNEL_STORAGE ) , sinon la création du chemin", Toast.LENGTH_LONG).show();
    }
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

    private ArrayList<PostData_Bon1> bon1s;

    final String FILES = "/PMEPRO_FOLDER";
    String path= Environment.getExternalStorageDirectory().getPath()+FILES; // Folder path
    // File myFile;
    File[] files;
    private OutputStreamWriter myOutWriter;
    private FileOutputStream fOut;


    public Exporter_data_to_dropbox() {

    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      //  progressDialogConfig();
    }

    @Override
    protected Integer doInBackground(Void... params) {
      try {

        bon1s = new ArrayList<>();
        ArrayList<PostData_Bon2> bon2s = new ArrayList<>();
        PostData_Client client;


        File folderFile = new File(path);
        if (!folderFile.exists()) {
          folderFile.mkdirs();
        }

          String querry = "SELECT " +
                  "Bon1_Temp.RECORDID, " +
                  "Bon1_Temp.NUM_BON, " +
                  "Bon1_Temp.CODE_CLIENT, " +
                  "Client.CLIENT, " +
                  "Bon1_Temp.LATITUDE, " +

                  "Bon1_Temp.LONGITUDE, " +
                  "Client.LATITUDE as LATITUDE_CLIENT, " +

                  "Client.LONGITUDE as LONGITUDE_CLIENT, " +

                  "Bon1_Temp.DATE_BON, " +
                  "Bon1_Temp.HEURE, " +
                  "Bon1_Temp.NBR_P, " +
                  "Bon1_Temp.MODE_TARIF, " +

                  "Bon1_Temp.CODE_DEPOT, " +
                  "Bon1_Temp.MONTANT_BON, " +
                  "Bon1_Temp.MODE_RG, " +
                  "Bon1_Temp.CODE_VENDEUR, " +
                  "Bon1_Temp.EXPORTATION, " +
                  "Bon1_Temp.REMISE, " +
                  "Bon1_Temp.TOT_TTC, " +
                  "Bon1_Temp.TOT_TVA, " +
                  "Bon1_Temp.TOT_HT, " +

                  "Client.TEL, " +
                  "Client.ADRESSE, " +
                  "Client.RC, " +
                  "Client.IFISCAL, " +


                  "Bon1_Temp.TIMBRE, " +
                  "Bon1_Temp.VERSER, " +
                  "Bon1_Temp.RESTE, " +
                  "Bon1_Temp.BLOCAGE, " +
                  "Bon1_Temp.ANCIEN_SOLDE " +
                  "FROM Bon1_temp " +
                  "LEFT JOIN Client ON " +
                  "Bon1_Temp.CODE_CLIENT = Client.CODE_CLIENT WHERE Bon1_Temp.BLOCAGE = 'F'" +
                  "ORDER BY Bon1_Temp.DATE_BON DESC";
        bon1s.clear();
        bon1s = controller.select_vente_from_database(querry);

        OutputStreamWriter myOutWriter = null;
        FileOutputStream fOut= null;
        files = new  File[bon1s.size()];

        for (int i = 0; i < bon1s.size(); i++) {

          client = new PostData_Client();
          client = controller.select_client_from_database(bon1s.get(i).code_client);

          files[i] = new File(folderFile, "COMMANDE_"+bon1s.get(i).num_bon+"_"+bon1s.get(i).exportation+".BL");
          files[i].createNewFile();

          fOut = new FileOutputStream(files[i]);
          myOutWriter = new OutputStreamWriter(fOut);

          String querry_select = "SELECT Bon2_Temp.RECORDID, " +
                  "Bon2_Temp.CODE_BARRE, " +
                  "Bon2_Temp.NUM_BON, " +
                  "Bon2_Temp.PRODUIT, " +
                  "Bon2_Temp.QTE, " +
                  "Bon2_Temp.PV_HT, " +
                  "Bon2_Temp.TVA, " +
                  "Bon2_Temp.CODE_DEPOT, " +
                  "Bon2_Temp.PA_HT, " +
                  "Produit.STOCK " +
                  "FROM Bon2_Temp " +
                  "INNER JOIN " +
                  "Produit ON (Bon2_Temp.CODE_BARRE = Produit.CODE_BARRE) " +
                  "WHERE Bon2_Temp.NUM_BON = '" + bon1s.get(i).num_bon
                  + "' ";

          bon2s = controller.select_bon2_from_database(querry_select);

          myOutWriter.append("[BCC1]");
          myOutWriter.append("\n");

          SimpleDateFormat df_show = new SimpleDateFormat("dd/MM/yyyy");
          SimpleDateFormat df_save = new SimpleDateFormat("MM/dd/yyyy");
          Date myDate = null;
          try {
            myDate = df_save.parse(bon1s.get(i).date_bon);

          } catch (ParseException e) {
            e.printStackTrace();
          }
            String formattedDate_Show = df_show.format(myDate);

            myOutWriter.append("DATE_BON="+ formattedDate_Show );
            myOutWriter.append("\n");
            myOutWriter.append("HEURE="+bon1s.get(i).heure);
            myOutWriter.append("\n");
            myOutWriter.append("CODE_CLIENT="+bon1s.get(i).code_client );
            myOutWriter.append("\n");
            myOutWriter.append("HT="+bon1s.get(i).tot_ht);
            myOutWriter.append("\n");
            myOutWriter.append("TVA="+bon1s.get(i).tot_tva);
            myOutWriter.append("\n");
            myOutWriter.append("ANCIEN_SOLDE="+bon1s.get(i).solde_ancien);
            myOutWriter.append("\n");
            myOutWriter.append("LATITUDE="+bon1s.get(i).latitude );
            myOutWriter.append("\n");
            myOutWriter.append("LONGITUDE="+bon1s.get(i).longitude );
            myOutWriter.append("\n\r");
            myOutWriter.append("REMISE="+bon1s.get(i).remise);
            myOutWriter.append("\n");
            myOutWriter.append("VERSER="+bon1s.get(i).verser);

            myOutWriter.append("\n");
            myOutWriter.append("NBR_P="+bon1s.get(i).nbr_p);
            myOutWriter.append("\n");
            myOutWriter.append("TTC="+bon1s.get(i).tot_ttc);
            myOutWriter.append("\n");

            myOutWriter.append("MODE_RG="+"ESPECE");
            myOutWriter.append("\n");
            myOutWriter.append("UTILISATEUR=USER");
            myOutWriter.append("\n");
            myOutWriter.append("MODE_TARIF="+bon1s.get(i).mode_tarif );

            myOutWriter.append("\n");
            myOutWriter.append("CLIENT="+bon1s.get(i).client );
            myOutWriter.append("\n");
            myOutWriter.append("ADRESSE="+bon1s.get(i).adresse );
            myOutWriter.append("\n");
            myOutWriter.append("TEL="+bon1s.get(i).tel );
            myOutWriter.append("\n");
            myOutWriter.append("LATITUDE_CLIENT="+bon1s.get(i).latitude_client );
            myOutWriter.append("\n");
            myOutWriter.append("LONGITUDE_CLIENT="+bon1s.get(i).longitude_client);
            myOutWriter.append("\n");

            myOutWriter.append("NUM_RC="+bon1s.get(i).rc );
            myOutWriter.append("\n");
            myOutWriter.append("NUM_IF="+bon1s.get(i).ifiscal);
            myOutWriter.append("\n\r");


          for (int j = 0; j < bon2s.size(); j++) {

            myOutWriter.append("[BCC2"+j+"]");
            myOutWriter.append("\n");

            myOutWriter.append("CODEBARRE="+bon2s.get(j).codebarre);
            myOutWriter.append("\n");
            myOutWriter.append("PRODUIT="+bon2s.get(j).produit);
            myOutWriter.append("\n");
            myOutWriter.append("QTE="+bon2s.get(j).qte);
            myOutWriter.append("\n");
            myOutWriter.append("PV_HT="+bon2s.get(j).p_u);
            myOutWriter.append("\n");
            myOutWriter.append("TVA="+bon2s.get(j).tva);
            myOutWriter.append("\n");
            myOutWriter.append("PA_HT="+bon2s.get(j).pa_ht);
            myOutWriter.append("\n");
            myOutWriter.append("PV_HT_AR="+bon2s.get(j).p_u);
            myOutWriter.append("\n");
            myOutWriter.append("CODE_DEPOT="+bon2s.get(j).code_depot);
            myOutWriter.append("\n\r");

          }

          bon2s.clear();
          myOutWriter.close();
          fOut.close();
        }

        bon1s.clear();
        flag = 1;


      } catch (Exception ex) {

        Log.e("TRACKKK", "YOU HAVE AN SQL ERROR IN YOUR REQUEST  " + ex.getMessage());
        if (ex.getMessage().contains("Unable to complete network request to host")) {
          flag = 2;
          Log.e("TRACKKK", "ENABLE TO CONNECT TO SERVER FIREBIRD DATA STORED IN THE LOCAL DATABASE ");
        } else {
          //not executed with problem in the sql statement
          flag = 3;
        }
      }
      return flag;
    }

    @Override
    protected void onPostExecute(Integer integer) {

      String dir = "/";
        dir ="/"+ DATABASE_NAME+"/COMMANDES/CD_"+DEPOT_DIR+"/";


      for (int i = 0; i < files.length; i++) {
        UploadFile upload = new UploadFile(ActivityDropBoxSend_Commande.this, ACCESS_TOKEN, dir , files[i]);
        upload.execute();
      }

      loadListFile();

      super.onPostExecute(integer);
    }
  }

  public void requestPermission(){

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACCES_READ_WRITE_EXTERNEL_STORAGE);
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

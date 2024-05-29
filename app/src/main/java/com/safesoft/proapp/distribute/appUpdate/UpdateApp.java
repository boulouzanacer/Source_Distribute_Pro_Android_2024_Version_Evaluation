package com.safesoft.proapp.distribute.appUpdate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UpdateApp extends AsyncTask<String,Integer, Integer> {
  private Context context;
  private ProgressDialog mProgressDialog;
  private Activity mActivity;
  private String version = "0";
  int flag = 0;
  public void setContext(Context contextf){
    context = contextf;
  }

  public UpdateApp(Activity activity,  String version) {
    this.mActivity = activity;
    this.version = version;
  }

  @Override
  protected void onPreExecute() {
    mProgressDialog = new ProgressDialog(mActivity);
    mProgressDialog.setTitle("Téléchargement de mise à jour en cours....");
    mProgressDialog.setCanceledOnTouchOutside(false);
    mProgressDialog.setCancelable(false);
    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mProgressDialog.setMax(100);
    mProgressDialog.show();

    mProgressDialog.setProgress(0);

    super.onPreExecute();
  }


  @Override
  protected Integer doInBackground(String... arg0) {
    try {

      URL url = new URL(arg0[0]);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setDoOutput(true);
      connection.connect();

      // Get file length
      long fileLength = connection.getContentLength();

      File outputFile = new File(context.getExternalCacheDir(), "update"+ version + ".apk");
      FileOutputStream fos = new FileOutputStream(outputFile);

      InputStream is = connection.getInputStream();

      byte[] buffer = new byte[4096];
      int count = 0;
      long total = 0;

      while ((count = is.read(buffer)) != -1) {
        total += count;

        if (fileLength > 0) { // Only if total length is known
          long ffff = Math.abs((total * 100) / fileLength);
          if(ffff == 31){
            Log.e("count", (String.valueOf(ffff)));
          }
          publishProgress((int)(total * 100 / fileLength));
        }

        fos.write(buffer, 0, count);
      }

      fos.close();
      is.close();
      flag = 1;

    } catch (Exception e) {
      Log.e("Update error! " , e.getMessage());
      flag = 2;
    }
    return flag;
  }

  @Override
  protected void onProgressUpdate(Integer... progress) {
    super.onProgressUpdate(progress);
    mProgressDialog.setProgress(progress[0]);
//    progressText.setText("Download progress: " + progress[0] + "%");
  }

  @Override
  protected void onPostExecute(Integer result) {
    super.onPostExecute(result);
    if (mProgressDialog != null && mProgressDialog.isShowing())
      mProgressDialog.dismiss();

    if(result == 1){
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", new File(context.getExternalCacheDir() +"/update"+version+".apk")), "application/vnd.android.package-archive");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      context.startActivity(intent);
      //mActivity.finish();
    }else {
      new SweetAlertDialog(mActivity, SweetAlertDialog.ERROR_TYPE).setTitleText("Update failed!").show();
    }
  }
}

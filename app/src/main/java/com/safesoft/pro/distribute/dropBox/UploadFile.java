/*
 * Copyright (c) 2011 Dropbox, Inc.
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


package com.safesoft.pro.distribute.dropBox;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.users.FullAccount;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Here we show uploading a file in a background thread, trying to show
 * typical exception handling and flow of control for an app that uploads a
 * file from Dropbox.
 */
public class UploadFile extends AsyncTask<Void, Long, Boolean> {


  private String mPath;
  private File mFile;

  private long mFileLen;
  private String ACCESS_TOKEN;
  private Context mContext;
  private final ProgressDialog mDialog;

  private String ErrMsg;


  public UploadFile(Context context, String ACCESS_TOKEN, String dropboxPath, File file) {
    // We set the context this way so we don't accidentally leak activities
    mContext = context.getApplicationContext();

    mFileLen = file.length();
    mPath = dropboxPath;
    mFile = file;
    mFile.setWritable(true);
    mFile.setReadable(true);
    mFile.setExecutable(true);
    this.ACCESS_TOKEN = ACCESS_TOKEN;

    mDialog = new ProgressDialog(context);
    mDialog.setMessage("Chargement " + file.getName());
    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    mDialog.setIndeterminate(true);
     /*   mDialog.setButton("Annuler", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // This will cancel the putFile operation
                mRequest.abort();
            }
        });*/
    mDialog.show();
  }

  @Override
  protected Boolean doInBackground(Void... params) {
    // By creating a request, we get a handle to the putFile operation,
    // so we can cancel it later if we want to

    Boolean status = false;
    DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder("examples-v2-demo")
            .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
            .build();

    DbxClientV2 client = new DbxClientV2(requestConfig, ACCESS_TOKEN);

    // Get current acrcount info
    FullAccount account = null;

    try {
      account = client.users().getCurrentAccount();
      System.out.println(account.getName().getDisplayName());

      // Get files and folder metadata from Dropbox root directory
      ListFolderResult result = client.files().listFolder("");

      while (true) {
        for (Metadata metadata : result.getEntries()) {
          System.out.println(metadata.getPathLower());
        }

        if (!result.getHasMore()) {
          break;
        }


        result = client.files().listFolderContinue(result.getCursor());
      }

      // Upload to Dropbox
      try (FileInputStream fis = new FileInputStream(mFile)) {
        String path = mPath + mFile.getName();

        if(mFile.exists()) {
          try {
            Metadata metadata = client.files().delete(path);

          } catch (DbxException dbxe) {
            dbxe.printStackTrace();
          }


        }


          FileMetadata Fmetadata = client.files().uploadBuilder(path).uploadAndFinish(fis);
          status = true;



      } catch (FileNotFoundException e) {
        ErrMsg = ""+ e.getMessage();
        status = false;
      } catch (IOException e) {
        ErrMsg = ""+ e.getMessage();
        status = false;
      }
    } catch (DbxException e) {
      ErrMsg = ""+ e.getMessage();
    }
    return status;
  }

  @Override
  protected void onProgressUpdate(Long... progress) {
    int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
    mDialog.setProgress(percent);
  }

  @Override
  protected void onPostExecute(Boolean result) {
    mDialog.dismiss();
    if (result) {
      showToast(mFile.getName() + " est bien chargé sur DropBox");
    } else {
      showToast("Erreur chargement du fichier "+ mFile.getName() + " / " + ErrMsg);
    }
  }

  private void showToast(String msg) {
    Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
    error.show();
  }
}

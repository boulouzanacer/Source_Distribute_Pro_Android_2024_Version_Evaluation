/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.safesoft.proapp.distribute.databases.backup;


import com.safesoft.proapp.distribute.app.BaseApplication;
import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.utils.Permissions;

import java.io.File;
import java.io.OutputStream;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LocalBackup {

    private final Activity activity;

    public LocalBackup(Activity activity) {
        this.activity = activity;
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void performBackup(final DATABASE db) {

        Permissions.verifyStoragePermissions(activity);

        String outFileName = Environment.getExternalStorageDirectory() + File.separator+"backup_distribute_pro_data.db";
        File folder = new File(Environment.getExternalStorageDirectory().getPath());

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            if (db.backup(outFileName)) {
                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(BaseApplication.instance.getString(R.string.l3_information_2))
                        .setContentText(BaseApplication.instance.getString(R.string.l3_sauvegarde_de_base_de_donnees_terminee_a))
                        .show();
            } else {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(BaseApplication.instance.getString(R.string.l3_attention))
                        .setContentText(BaseApplication.instance.getString(R.string.l3_prbleme_dimportation_de_la_base_de_donne))
                        .show();
            }
        } else
            Crouton.makeText(activity, BaseApplication.instance.getString(R.string.l4_probleme_creation_dossier), Style.ALERT).show();
    }

    //ask to the user what backup to restore
    public void performRestore(final DATABASE db) {

        Permissions.verifyStoragePermissions(activity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getResources().getString(R.string.app_name));
        if (folder.exists()) {

            final File[] files = folder.listFiles();

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.select_dialog_item);
            for (File file : files)
                arrayAdapter.add(file.getName());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
            builderSingle.setTitle(BaseApplication.instance.getString(R.string.l3_restorer));
            builderSingle.setNegativeButton(BaseApplication.instance.getString(R.string.l3_annuler), (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                try {
                    if (db.importDB(files[which].getPath())) {
                        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(BaseApplication.instance.getString(R.string.l3_information_2))
                                .setContentText(BaseApplication.instance.getString(R.string.l3_restauration_terminee_avec_succes))
                                .show();
                    } else {
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(BaseApplication.instance.getString(R.string.l3_attention))
                                .setContentText(BaseApplication.instance.getString(R.string.l3_prbleme_dimportation_de_la_base_de_donne))
                                .show();
                    }
                } catch (Exception e) {
                    Crouton.makeText(activity, BaseApplication.instance.getString(R.string.l4_probleme_restauration), Style.ALERT).show();
                }
            });
            builderSingle.show();
        } else
            Crouton.makeText(activity, BaseApplication.instance.getString(R.string.l4_dossier_sauvegarde_absent), Style.ALERT).show();
    }


    public void saveFileToExternalStorage(String fileName, byte[] fileContent) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri externalUri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            externalUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
        }
        Uri fileUri = activity.getContentResolver().insert(externalUri, values);

        try (OutputStream outputStream = activity.getContentResolver().openOutputStream(fileUri)) {
            if (outputStream != null) {
                outputStream.write(fileContent);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

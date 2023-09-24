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

import android.app.Activity;
import android.os.Environment;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;

import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.utils.Permissions;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LocalBackup {

    private final Activity activity;

    public LocalBackup(Activity activity) {
        this.activity = activity;
    }

    //ask to the user a name for the backup and perform it. The backup will be saved to a custom folder.
    public void performBackup(final DATABASE db, final String outFileName) {

        Permissions.verifyStoragePermissions(activity);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + activity.getResources().getString(R.string.app_name));

        boolean success = true;
        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {

            String out = outFileName +"backup_distribute_pro_data.db";

            if(db.backup(out)){
                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Information !")
                        .setContentText("Sauvegarde de base de données terminée avec succès !")
                        .show();
            }else {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Attention !")
                        .setContentText("Prblème d'importation de la base de données, Réessayer !")
                        .show();
            }
        } else
            Crouton.makeText(activity, "Problème de création du dossier. Réessayer", Style.ALERT).show();
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
            builderSingle.setTitle("Restorer:");
            builderSingle.setNegativeButton("annuler", (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                        try {
                            if(db.importDB(files[which].getPath())){
                                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Information !")
                                        .setContentText("Restauration terminée avec succès !")
                                        .show();
                            }else {
                                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("Attention !")
                                        .setContentText("Prblème d'importation de la base de données, Réessayer !")
                                        .show();
                            }
                        } catch (Exception e) {
                            Crouton.makeText(activity, "Problème de restauration. Réessayer", Style.ALERT).show();
                        }
                    });
            builderSingle.show();
        } else
            Crouton.makeText(activity, "Dossier de sauvegarde absent..\nFaites une sauvegarde avant une restauration !", Style.ALERT).show();
    }

}

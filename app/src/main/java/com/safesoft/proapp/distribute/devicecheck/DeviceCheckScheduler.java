package com.safesoft.proapp.distribute.devicecheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Planification de la verification silencieuse du device aupres du Device Checker.
 *
 * Strategie :
 *  - Aucune verification periodique. Le check est declenche uniquement lorsque
 *    l'application est lancee ou revient au premier plan (voir BaseApplication).
 *  - Le travail s'execute uniquement lorsque le reseau est disponible
 *    (NetworkType.CONNECTED) : hors ligne, il reste en file d'attente et
 *    s'execute des que la connexion revient.
 *  - En cas d'echec, reprise avec backoff exponentiel.
 */
public class DeviceCheckScheduler {

    private static final String TAG = "DeviceCheckScheduler";

    private static final String PREFS = "ALL_PREFS";

    private static final String PERIODIC_WORK_NAME = "device_check_periodic";
    private static final String IMMEDIATE_WORK_NAME = "device_check_immediate";

    private DeviceCheckScheduler() {
    }

    public static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /**
     * Annule l'ancien check periodique (toutes les 6 h) encore enregistre par une
     * version precedente de l'application. Le check n'est plus periodique.
     */
    public static void cancelPeriodic(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME);
        Log.i(TAG, "Ancien check periodique annule (check au lancement / 1er plan uniquement).");
    }

    /** Declenche un check unique (des que le reseau est disponible). */
    public static void scheduleImmediate(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DeviceCheckWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                IMMEDIATE_WORK_NAME, ExistingWorkPolicy.REPLACE, request);

        Log.i(TAG, "Check device declenche (lancement / retour au premier plan).");
    }
}

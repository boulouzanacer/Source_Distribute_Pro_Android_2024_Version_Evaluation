package com.safesoft.proapp.distribute.devicecheck;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Planification de la presence du device aupres du Device Checker.
 *
 * Strategie (option hybride) :
 *  - A l'entree au premier plan : enregistrement / heartbeat immediat, puis
 *    repetition d'un heartbeat tant que l'application reste visible.
 *  - A la sortie du premier plan : signal de deconnexion immediat
 *    (/api/devices/disconnect) et arret du heartbeat.
 *  - Le heartbeat sert de filet de securite : si l'application est tuee sans
 *    avoir pu signaler sa sortie, le serveur bascule le device hors ligne des
 *    que le dernier heartbeat depasse sa fenetre "en ligne" (1 min).
 *
 * Le heartbeat s'appuie sur un {@link Handler} (et non un PeriodicWorkRequest,
 * dont l'intervalle minimal est de 15 min) : il ne tourne que tant que
 * l'application est au premier plan, ce qui borne le nombre d'appels API.
 */
public class DeviceCheckScheduler {

    private static final String TAG = "DeviceCheckScheduler";

    private static final String PERIODIC_WORK_NAME = "device_check_periodic";
    private static final String IMMEDIATE_WORK_NAME = "device_check_immediate";
    private static final String DISCONNECT_WORK_NAME = "device_check_disconnect";

    /** Cle d'entree du worker : action a effectuer ("check" ou "disconnect"). */
    public static final String KEY_ACTION = "action";
    public static final String ACTION_CHECK = "check";
    public static final String ACTION_DISCONNECT = "disconnect";

    /**
     * Intervalle du heartbeat tant que l'application est au premier plan.
     * Aligne sur la fenetre "en ligne" du serveur (1 min) : un heartbeat toutes
     * les 45 s garde un device au premier plan vu "en ligne" sans clignotement,
     * tout en permettant une bascule hors ligne rapide (<= 1 min) lorsque
     * l'application est quittee ou tuee.
     */
    private static final long HEARTBEAT_INTERVAL_MS = 45_000L;

    /** Handler + tache du heartbeat (unique, recree a chaque entree au premier plan). */
    private static Handler heartbeatHandler;
    private static Runnable heartbeatTask;

    private DeviceCheckScheduler() {
    }

    /**
     * Annule l'ancien check periodique (toutes les 6 h) encore enregistre par une
     * version precedente de l'application. Le check n'est plus periodique.
     */
    public static void cancelPeriodic(Context context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_WORK_NAME);
        Log.i(TAG, "Ancien check periodique annule (check au lancement / 1er plan uniquement).");
    }

    /**
     * Appele lorsque l'application passe (ou repasse) au premier plan : enregistre
     * le device immediatement puis maintient un heartbeat tant qu'elle est visible.
     */
    public static void onForeground(Context context) {
        scheduleImmediate(context);
        startHeartbeat(context);
    }

    /**
     * Appele lorsque l'application n'est plus visible : signale immediatement la
     * sortie et arrete le heartbeat. Si l'application est tuee sans ce signal, le
     * serveur bascule le device hors ligne des que le dernier heartbeat depasse la
     * fenetre "en ligne" (1 min).
     */
    public static void onBackground(Context context) {
        stopHeartbeat();
        scheduleDisconnect(context);
    }

    /**
     * Declenche un check unique (enregistrement / heartbeat), des que le reseau
     * est disponible. Le travail precedent non execute est remplace afin d'eviter
     * toute accumulation de requetes.
     */
    public static void scheduleImmediate(Context context) {
        Data input = new Data.Builder()
                .putString(KEY_ACTION, ACTION_CHECK)
                .build();
        enqueue(context, IMMEDIATE_WORK_NAME, input);
        Log.i(TAG, "Check device planifie (enregistrement / heartbeat).");
    }

    /** Demarre le heartbeat periodique tant que l'application est visible. */
    private static void startHeartbeat(Context context) {
        final Context appContext = context.getApplicationContext();
        stopHeartbeat();

        heartbeatHandler = new Handler(Looper.getMainLooper());
        heartbeatTask = new Runnable() {
            @Override
            public void run() {
                scheduleImmediate(appContext);
                if (heartbeatHandler != null && heartbeatTask != null) {
                    heartbeatHandler.postDelayed(this, HEARTBEAT_INTERVAL_MS);
                }
            }
        };
        heartbeatHandler.postDelayed(heartbeatTask, HEARTBEAT_INTERVAL_MS);
        Log.i(TAG, "Heartbeat demarre (intervalle " + (HEARTBEAT_INTERVAL_MS / 1000) + " s).");
    }

    /** Arrete le heartbeat (sortie du premier plan ou nouvelle entree). */
    private static void stopHeartbeat() {
        if (heartbeatHandler != null && heartbeatTask != null) {
            heartbeatHandler.removeCallbacks(heartbeatTask);
        }
        heartbeatHandler = null;
        heartbeatTask = null;
    }

    /** Planifie le signal de sortie (passage en arriere-plan / fermeture). */
    private static void scheduleDisconnect(Context context) {
        Data input = new Data.Builder()
                .putString(KEY_ACTION, ACTION_DISCONNECT)
                .build();
        enqueue(context, DISCONNECT_WORK_NAME, input);
        Log.i(TAG, "Signal de deconnexion planifie (arriere-plan).");
    }

    /** Enfile un travail unique soumis a la disponibilite du reseau. */
    private static void enqueue(Context context, String uniqueName, Data input) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DeviceCheckWorker.class)
                .setConstraints(constraints)
                .setInputData(input)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                uniqueName, ExistingWorkPolicy.REPLACE, request);
    }
}

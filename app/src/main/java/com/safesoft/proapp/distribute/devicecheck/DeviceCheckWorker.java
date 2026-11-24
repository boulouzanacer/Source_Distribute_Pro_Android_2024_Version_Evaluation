package com.safesoft.proapp.distribute.devicecheck;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import retrofit2.Response;

/**
 * Worker qui effectue la verification silencieuse du device.
 *
 * Fonctionnement :
 *  - Recupere le NUM_SERIE (device ID) depuis les SharedPreferences.
 *  - Si aucun device ID n'est present (app non demarree), rien a faire.
 *  - Appelle l'API /api/devices/check avec le device ID, le revendeur,
 *    la version de l'app et le package.
 *  - Si la reponse vaut "blocked" -> applique la bascule en mode evaluation
 *    (effacement de l'activation + retour splash).
 *  - Si la reponse vaut "active" ou "registered" -> rien a faire (l'app
 *    continue de fonctionner normalement).
 *  - En cas d'erreur reseau / serveur -> retry (WorkManager reprendra
 *    automatiquement des que le reseau revient).
 */
public class DeviceCheckWorker extends Worker {

    private static final String TAG = "DeviceCheckWorker";
    private static final String PREFS = "ALL_PREFS";

    // Cles des SharedPreferences.
    private static final String KEY_NUM_SERIE = "NUM_SERIE";
    private static final String KEY_REVENDEUR = "REVENDEUR";
    private static final String KEY_APP_ACTIVATED = "APP_ACTIVATED";
    private static final String KEY_CODE_ACTIVATION = "CODE_ACTIVATION";

    public DeviceCheckWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        String deviceId = prefs.getString(KEY_NUM_SERIE, "");
        if (deviceId == null || deviceId.trim().isEmpty()) {
            // Pas encore de device ID (splashScreen n'a pas encore tourne).
            Log.i(TAG, "Device check ignore : aucun NUM_SERIE en SharedPreferences.");
            return Result.success();
        }

        String apiUrl = DeviceCheckClient.getApiUrl(context);

        try {
            String revendeur = prefs.getString(KEY_REVENDEUR, "SAFE SOFT");
            String appVersion = getVersionName(context);
            String packageName = context.getPackageName();
            String appName = getAppName(context);

            // Code d'activation : 0 lorsque l'application n'est pas activee.
            int activationCode = prefs.getBoolean(KEY_APP_ACTIVATED, false)
                    ? prefs.getInt(KEY_CODE_ACTIVATION, 0)
                    : 0;

            DeviceCheckClient.DeviceCheckRequest request =
                    new DeviceCheckClient.DeviceCheckRequest(
                            deviceId, revendeur, appVersion, packageName, appName,
                            activationCode);

            String token = DeviceCheckClient.getApiToken(context);

            // Trace de la requete sortante.
            Log.i(TAG, "==> Device check : POST " + apiUrl + "devices/check");
            Log.i(TAG, "    device=" + deviceId + ", revendeur=" + revendeur
                    + ", app=" + appName + " v" + appVersion + " (" + packageName + ")");
            Log.i(TAG, "    activation_code=" + activationCode
                    + (activationCode == 0 ? " (desactive)" : ""));

            long startTime = System.currentTimeMillis();

            Response<DeviceCheckClient.DeviceCheckResponse> response =
                    DeviceCheckClient.getApi(context)
                            .checkDevice("Bearer " + token, request)
                            .execute();

            long duration = System.currentTimeMillis() - startTime;

            if (!response.isSuccessful() || response.body() == null) {
                Log.w(TAG, "<== Echec du device check : HTTP " + response.code()
                        + " en " + duration + " ms" + readErrorBody(response));
                return Result.retry();
            }

            DeviceCheckClient.DeviceCheckResponse body = response.body();
            Log.i(TAG, "<== Reponse HTTP " + response.code() + " en " + duration + " ms");
            Log.i(TAG, "    status=" + body.status + ", action=" + body.action
                    + ", message=" + body.message);

            if (body.isBlocked()) {
                // Le device a ete bloque depuis l'interface web.
                Log.w(TAG, "Device bloque : passage en mode evaluation.");
                DeviceCheckActions.applyBlocked(context);
                return Result.success();
            }

            // Device actif (ou nouvellement enregistre) : rien a faire.
            Log.i(TAG, "Device actif : aucune action requise.");
            return Result.success();

        } catch (Exception e) {
            Log.e(TAG, "Erreur reseau lors du device check (POST " + apiUrl + "devices/check) : "
                    + e.getClass().getSimpleName() + " - " + e.getMessage(), e);
            // Reseau indisponible ou serveur injoignable : on retente plus tard.
            return Result.retry();
        }
    }

    /** Lit le corps d'erreur de la reponse (tronque) pour faciliter le diagnostic. */
    private String readErrorBody(Response<?> response) {
        try {
            if (response.errorBody() == null) {
                return "";
            }
            String body = response.errorBody().string();
            if (body.length() > 300) {
                body = body.substring(0, 300) + "...";
            }
            return " - " + body;
        } catch (Exception e) {
            return "";
        }
    }

    private String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            return "0";
        }
    }

    /** Nom de l'application tel qu'affiche a l'utilisateur (ex: "Distribute pro"). */
    private String getAppName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            return String.valueOf(pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(), 0)));
        } catch (PackageManager.NameNotFoundException e) {
            return context.getPackageName();
        }
    }
}

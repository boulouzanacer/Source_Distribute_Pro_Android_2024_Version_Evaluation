package com.safesoft.proapp.distribute.devicecheck;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.safesoft.proapp.distribute.activities.splashscreen.splashScreen;

/**
 * Actions appliquees lorsque le Device Checker renvoie un statut "blocked".
 *
 * Lorsqu'un device est bloque depuis l'interface web, l'application mobile doit :
 *  1. Supprimer les donnees d'activation (retour en mode evaluation).
 *  2. Rediriger vers l'ecran de splash qui affichera la boite de dialogue
 *     d'activation / demo.
 */
public class DeviceCheckActions {

    private static final String TAG = "DeviceCheckActions";
    private static final String PREFS = "ALL_PREFS";

    // Cles utilisees par le systeme d'activation existant (splashScreen).
    private static final String KEY_NUM_SERIE = "NUM_SERIE";
    private static final String KEY_CODE_ACTIVATION = "CODE_ACTIVATION";
    private static final String KEY_APP_ACTIVATED = "APP_ACTIVATED";

    // Marqueur persistant : l'application est en etat "bloque". Il evite de
    // rejouer la bascule a chaque heartbeat et suspend la verification tant que
    // le device reste en liste noire.
    private static final String KEY_BLOCKED = "APP_BLOCKED";

    private DeviceCheckActions() {
    }

    /** Indique si l'application est actuellement en etat "bloque". */
    public static boolean isBlocked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_BLOCKED, false);
    }

    /** Leve le marqueur "bloque" (appele apres une activation reussie). */
    public static void clearBlocked(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_BLOCKED, false).apply();
    }

    /**
     * Applique la bascule en mode evaluation :
     *  - efface APP_ACTIVATED, CODE_ACTIVATION et NUM_SERIE des SharedPreferences ;
     *  - arrete le heartbeat et les verifications en attente ;
     *  - redirige vers l'activite splashScreen (qui re-evaluera l'activation).
     *
     * Idempotent : si la bascule a deja ete appliquee, ne fait rien. Sans cela,
     * chaque heartbeat (45 s) relancait l'ecran Demo/Activer en boucle.
     */
    public static void applyBlocked(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

            if (prefs.getBoolean(KEY_BLOCKED, false)) {
                Log.i(TAG, "Device deja bloque : aucune nouvelle bascule.");
                return;
            }

            prefs.edit()
                    .putBoolean(KEY_BLOCKED, true)
                    .remove(KEY_APP_ACTIVATED)
                    .remove(KEY_CODE_ACTIVATION)
                    .remove(KEY_NUM_SERIE)
                    .apply();

            Log.w(TAG, "Device bloque par Device Checker. Donnees d'activation effacees.");

            // Stoppe le heartbeat et les verifications en attente : plus aucun
            // appel API tant que le device reste bloque.
            DeviceCheckScheduler.onBlocked(context);

            Intent intent = new Intent(context, splashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Throwable t) {
            Log.e(TAG, "Echec de l'application de l'action bloque", t);
        }
    }
}

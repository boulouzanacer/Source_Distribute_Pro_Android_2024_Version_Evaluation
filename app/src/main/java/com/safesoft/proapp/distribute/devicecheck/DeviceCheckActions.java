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

    private DeviceCheckActions() {
    }

    /**
     * Applique la bascule en mode evaluation :
     *  - efface APP_ACTIVATED, CODE_ACTIVATION et NUM_SERIE des SharedPreferences ;
     *  - redirige vers l'activite splashScreen (qui re-evaluera l'activation).
     */
    public static void applyBlocked(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                    .remove(KEY_APP_ACTIVATED)
                    .remove(KEY_CODE_ACTIVATION)
                    .remove(KEY_NUM_SERIE)
                    .apply();

            Log.w(TAG, "Device bloque par Device Checker. Donnees d'activation effacees.");

            Intent intent = new Intent(context, splashScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);

        } catch (Throwable t) {
            Log.e(TAG, "Echec de l'application de l'action bloque", t);
        }
    }
}

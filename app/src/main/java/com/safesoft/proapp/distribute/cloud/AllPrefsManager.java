package com.safesoft.proapp.distribute.cloud;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class AllPrefsManager {

    private static final String PREF_NAME = "ALL_PREFS";

    private final SharedPreferences prefs;

    public AllPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Return all preferences as Map<String, String>
     * (boolean/int converted to string for API POST)
     */
    public Map<String, String> getAllForApi() {
        Map<String, ?> all = prefs.getAll();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, ?> entry : all.entrySet()) {
            Object value = entry.getValue();

            if (value != null) {
                result.put(entry.getKey(), String.valueOf(value));
            }
        }
        return result;
    }

    /* Optional typed getters if you need them elsewhere */

    public String getString(String key) {
        return prefs.getString(key, "");
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    public int getInt(String key) {
        return prefs.getInt(key, 0);
    }
}

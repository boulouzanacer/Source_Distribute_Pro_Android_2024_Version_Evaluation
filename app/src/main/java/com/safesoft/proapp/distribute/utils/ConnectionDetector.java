package com.safesoft.proapp.distribute.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {

    private final Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isInternetAvailble() {
        return isConnectingToInternet() || isConnectingToWifi();
    }

    private boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info)
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }

        }
        return false;
    }

    private boolean isConnectingToWifi() {
        ConnectivityManager connManager = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null) {
            return mWifi.getState() == NetworkInfo.State.CONNECTED;
        }
        return false;
    } }

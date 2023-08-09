package com.shahroz.svlibrary.utils;

import android.content.Context;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

/**
 * Created by shahroz on 1/8/2016.
 */
public class Util {
    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static void showSnackBarMessage(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}

package com.safesoft.proapp.distribute.utils;

public class StandardFunctions {

    public static double getTimbreValue(double val_total_ttc){
        double val_timbre = 0;
        if (val_total_ttc  <= 300) {
            val_timbre = 0.00;
        } else if(val_total_ttc > 300 && val_total_ttc <= 30000) {
            val_timbre = (val_total_ttc * 0.01);
        }else if(val_total_ttc > 30000 && val_total_ttc <= 100000){
            val_timbre = (val_total_ttc * 0.015);
        }else if(val_total_ttc > 100000){
            val_timbre = (val_total_ttc * 0.02);
        }
        if(val_timbre <= 5){
            val_timbre = 5;
        }

        val_timbre = Math.round(val_timbre);

        return val_timbre;
    }
}

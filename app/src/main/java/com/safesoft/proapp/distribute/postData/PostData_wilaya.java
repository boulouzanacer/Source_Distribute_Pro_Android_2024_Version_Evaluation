package com.safesoft.proapp.distribute.postData;

/**
 * Created by UK2015 on 24/08/2016.
 */
public class PostData_wilaya {

    public int id;
    public String wilaya;      // Nom affiché : soit WILAYA_2 (ex: "16 - ALGER") soit fallback NAME
    public String wilaya_2;    // Valeur brute colonne WILAYA_2 (peut être null si ancienne DB)
    public double latitude;
    public double longitude;
}

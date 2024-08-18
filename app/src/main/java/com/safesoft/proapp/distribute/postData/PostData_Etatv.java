package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 06/06/2016.
 */
public class PostData_Etatv implements Serializable {
    public String parent;
    public String code_parent;
    public String produit;
    public double quantite;
    public double montant;

    public double total_remise;
    public double total_par_bon_ht;
    public double total_versement_bon;
    public double total_versement_client;
    public double benifice;
    public double vers_client;

    public double pv_ht;
}
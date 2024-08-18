package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Achat2 implements Serializable {
    public Integer recordid;
    public String num_bon;
    public String codebarre;
    public String produit;
    public double nbr_colis;
    public double colissage;
    public double qte;
    public double gratuit;
    public double pa_ht;
    public double tva;
    public String code_depot;
    public double stock_produit;
    public double pa_ht_produit;
    public double pamp_produit;
    public int isNew = 0;

}

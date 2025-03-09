package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon2 implements Serializable {
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
    public double pv_ht;
    public double pv_limite;
    public String code_depot;
    public double stock_produit;
    public int isNew = 0;
    public String destock_type;
    public String destock_code_barre;
    public double destock_qte;

    //////// PROMO ////////////////
    public int promo = 0;
    public String d1;
    public String d2;
    public double pp1_ht;

}

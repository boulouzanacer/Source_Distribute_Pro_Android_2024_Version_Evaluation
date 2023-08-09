package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Produit implements Serializable {
    public String produit_id;
    public String code_barre;
    public String ref_produit;
    public String produit;
    public double pa_ht;
    public double pa_ttc;
    public double pamp;
    public double tva;

    public double pv1_ht;
    public double pv2_ht;
    public double pv3_ht;
    public double pv4_ht;
    public double pv5_ht;
    public double pv6_ht;

    public double pv1_ttc;
    public double pv2_ttc;
    public double pv3_ttc;
    public double pv4_ttc;
    public double pv5_ttc;
    public double pv6_ttc;

    public double colissage;
    public double stock_colis;
    public int stock_vrac;

    public double stock;
    public byte[]  photo;
    public Boolean exist;
    public String description;
    public String famille;
    public String destock_type;
    public String destock_code_barre;
    public double destock_qte;
    public int isNew = 0;
}

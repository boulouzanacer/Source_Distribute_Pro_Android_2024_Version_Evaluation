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
    public double tva;
    public double pv1_ht;
    public double pv2_ht;
    public double pv3_ht;
    public double colissage;
    public int stock_colis;
    public int stock_vrac;
    public double stock;
    public String qte_produit;
    public byte[]  photo;
    public Boolean exist;
    public String DETAILLE;
    public String destock_type;
    public String destock_code_barre;
    public double destock_qte;
}

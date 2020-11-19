package com.safesoft.pro.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Produit implements Serializable {
    public String produit_id;
    public String code_barre;
    public String ref_produit;
    public String produit;
    public String pa_ht;
    public String tva;
    public String pv1_ht;
    public String pv2_ht;
    public String pv3_ht;
    public String stock;
    public String qte_produit;
    public byte[]  photo;
    public Boolean exist;
    public int nbr_cb;
    public Boolean checked = false;
    public String DETAILLE;
}

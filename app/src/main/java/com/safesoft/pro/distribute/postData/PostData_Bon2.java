package com.safesoft.pro.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon2 implements Serializable{
  public Integer recordid;
  public String num_bon;
  public String codebarre;
  public String produit;
  public String qte;
  public String tva;
  public String p_u;
  public String code_depot;
  public String pa_ht;
  public String montant_produit;
  public String stock_produit;
  public Boolean checked = false;
}

package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon2 implements Serializable{
  public Integer recordid;
  public String num_bon;
  public String codebarre;
  public String produit;
  public double nbr_colis;
  public double colissage;
  public double qte;
  public double gratuit;
  public double tva;
  public double p_u;
  public String code_depot;
  public double stock_produit;
  public String destock_type;
  public String destock_code_barre;
  public double destock_qte;
}

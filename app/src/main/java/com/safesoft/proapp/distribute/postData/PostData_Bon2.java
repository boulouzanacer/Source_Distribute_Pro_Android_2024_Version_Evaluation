package com.safesoft.proapp.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 31/08/2016.
 */
public class PostData_Bon2 implements Serializable{
  public Integer recordid;
  public String num_bon;
  public String codebarre;
  public String produit = " ";
  public Double nbr_colis;
  public Double colissage;
  public Double qte;
  public Double gratuit;
  public Double tva;
  public Double p_u;
  public String code_depot;
  public Double pa_ht;
  public Double stock_produit;
  public String destock_type;
  public String destock_code_barre;
  public Double destock_qte;

  public Boolean checked = false;
}

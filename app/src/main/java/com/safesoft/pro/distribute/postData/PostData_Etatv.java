package com.safesoft.pro.distribute.postData;

import java.io.Serializable;

/**
 * Created by UK2015 on 06/06/2016.
 */
public class PostData_Etatv implements Serializable {
  public String parent;
  public String code_parent;
  public String produit;
  public String quantite;
  public String montant;

  public String total_remise;
  public String total_par_bon;
  public String total_versement;
  public String vers_client;

  public String pv_ht;
}
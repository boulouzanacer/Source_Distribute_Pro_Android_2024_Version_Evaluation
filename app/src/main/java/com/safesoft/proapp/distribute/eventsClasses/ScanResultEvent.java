package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Produit;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ScanResultEvent {
  private PostData_Produit produit;

  public ScanResultEvent(PostData_Produit produit){
    this.produit = produit;
  }

  public PostData_Produit getProduit(){
    return produit;
  }
}

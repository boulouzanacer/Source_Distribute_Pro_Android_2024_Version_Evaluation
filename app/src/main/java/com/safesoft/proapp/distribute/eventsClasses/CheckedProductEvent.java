package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Produit;

import java.util.List;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckedProductEvent {
  private List<PostData_Produit> panier;

  public CheckedProductEvent(List<PostData_Produit> panier){
    this.panier = panier;
  }

  public List<PostData_Produit> getData(){
    return panier;
  }
}

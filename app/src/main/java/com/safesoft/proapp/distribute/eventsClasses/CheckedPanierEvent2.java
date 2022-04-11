package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.util.List;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckedPanierEvent2 {
  private List<PostData_Bon2> panier;

  public CheckedPanierEvent2(List<PostData_Bon2> panier){
    this.panier = panier;
  }

  public List<PostData_Bon2> getData(){
    return panier;
  }
}

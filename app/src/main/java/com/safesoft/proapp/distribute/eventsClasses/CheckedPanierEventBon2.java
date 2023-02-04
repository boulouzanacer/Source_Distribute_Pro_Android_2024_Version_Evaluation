package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.util.List;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckedPanierEventBon2 {
  private PostData_Bon2 panier;
  private Double qte_old;
  private Double gratuit_old;

  public CheckedPanierEventBon2(PostData_Bon2 panier, Double qte_old, Double gratuit_old){
    this.panier = panier;
    this.qte_old = qte_old;
    this.gratuit_old = gratuit_old;
  }

  public PostData_Bon2 getData(){
    return panier;
  }
  public Double getQteOld(){
    return qte_old;
  }
  public Double getGratuitOld(){
    return gratuit_old;
  }
}

package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Bon2;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckedPanierEventInventaire2 {
  private PostData_Inv2 panier;
  private Double qte_physique_old;
  private Double vrac_old;

  public CheckedPanierEventInventaire2(PostData_Inv2 panier, Double qte_physique_old, Double vrac_old){
    this.panier = panier;
    this.qte_physique_old = qte_physique_old;
    this.vrac_old = vrac_old;
  }

  public PostData_Inv2 getData(){
    return panier;
  }

  public Double getQtePhysiqueOld(){
    return qte_physique_old;
  }

  public Double getVracOld(){
    return vrac_old;
  }
}

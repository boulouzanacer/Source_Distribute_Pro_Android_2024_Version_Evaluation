package com.safesoft.proapp.distribute.eventsClasses;


import com.safesoft.proapp.distribute.postData.PostData_Bon1;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

import java.util.List;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class PreparedBonEvent {
  private PostData_Bon1 bon1;
  private List<PostData_Bon2> panier;

  public PreparedBonEvent(PostData_Bon1 bon1, List<PostData_Bon2> panier){
    this.bon1 = bon1;
    this.panier = panier;
  }

  public PostData_Bon1 getBon1Data(){
    return bon1;
  }
  public List<PostData_Bon2> getPanierData(){
    return panier;
  }
}

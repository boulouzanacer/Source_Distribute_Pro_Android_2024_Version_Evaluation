package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Client;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedBonEvent {

  private final String nom_bon;

  public SelectedBonEvent(String nom_bon){
    this.nom_bon = nom_bon;
  }

  public String getBon(){
    return nom_bon;
  }
}

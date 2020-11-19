package com.safesoft.pro.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ValidateFactureEvent {
  private String ancien;
  private String versement;
  private String reste;

  public ValidateFactureEvent(String ancien, String versement, String reste){
    this.ancien = ancien;
    this.versement = versement;
    this.reste = reste;
  }

  public String getAncien(){
    return ancien;
  }

  public String getVersement(){
    return versement;
  }

  public String getReste(){
    return reste;
  }
}

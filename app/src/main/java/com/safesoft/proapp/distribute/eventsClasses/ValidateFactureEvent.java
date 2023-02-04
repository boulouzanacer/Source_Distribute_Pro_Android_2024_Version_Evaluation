package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ValidateFactureEvent {
  private Double versement;

  public ValidateFactureEvent(Double versement){
    this.versement = versement;
  }

  public Double getVersement(){
    return versement;
  }

}

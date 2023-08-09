package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ValidateFactureEvent {
  private final double versement;

  public ValidateFactureEvent(double versement){
    this.versement = versement;
  }

  public double getVersement(){
    return versement;
  }

}

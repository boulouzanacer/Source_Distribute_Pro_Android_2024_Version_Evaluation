package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class RemiseEvent {
  private final Double remise;
  private final Double taux;
  private final Double apres_remise;

  public RemiseEvent(Double remise, Double taux, Double apres_remise){
    this.remise = remise;
    this.taux = taux;
    this.apres_remise = apres_remise;
  }

  public Double getRemise(){
    return remise;
  }

  public Double getTaux(){
    return taux;
  }

  public Double getApresRemise(){
    return apres_remise;
  }
}

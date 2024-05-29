package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class GetServerHashEvent {
  private String hash;
  public GetServerHashEvent(String hash){
    this.hash = hash;
  }


  public String getHash(){
    return hash;
  }

}

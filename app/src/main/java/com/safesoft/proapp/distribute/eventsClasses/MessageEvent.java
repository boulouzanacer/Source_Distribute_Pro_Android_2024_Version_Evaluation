package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class MessageEvent {
  private String message;

  public MessageEvent(String message){
    this.message = message;
  }

  public String getMessage(){
    return message;
  }
}

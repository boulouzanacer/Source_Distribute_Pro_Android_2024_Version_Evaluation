package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckVersionEvent {
  private int code;
  private String message;
  private String version;

  public CheckVersionEvent(int code, String message, String version){
    this.code = code;
    this.message = message;
    this.version = version;
  }

  public int getCode(){
    return code;
  }

  public String getMessage(){
    return message;
  }

  public String getVersion() {
    return version;
  }
}

package com.safesoft.pro.distribute.eventsClasses;

/**
 * Created by UK2016 on 16/01/2017.
 */

public class EtatZSelection_Event {
  public String user;
  public String code_user;



  public String date_f;
  public String date_t;

  public EtatZSelection_Event(String user, String date_f, String date_t,String code_user){
    this.user = user;
    this.date_f = date_f;
    this.date_t = date_t;
    this.code_user=code_user;
  }

  public String getUser(){
    return user;
  }
  public String getDate_f(){
    return date_f;
  }
  public String getDate_t(){
    return date_t;
  }
  public String getCode_user() {
    return code_user;
  }


}

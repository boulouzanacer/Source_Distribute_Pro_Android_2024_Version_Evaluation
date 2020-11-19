package com.safesoft.pro.distribute.eventsClasses;

import com.safesoft.pro.distribute.postData.PostData_Client;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedClientEvent {

  private PostData_Client client;

  public SelectedClientEvent(PostData_Client client){
    this.client = client;
  }

  public PostData_Client getClient(){
    return client;
  }
}

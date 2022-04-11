package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class DeleteItemEvent {

  private String item_codebarre;

  public DeleteItemEvent(String item_codebarre){
    this.item_codebarre = item_codebarre;
  }

  public String getData(){
    return item_codebarre;
  }

}

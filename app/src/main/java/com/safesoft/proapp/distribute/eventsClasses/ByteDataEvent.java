package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class ByteDataEvent {
  private final byte[] inputData;

  public ByteDataEvent(byte[] inputData){
    this.inputData = inputData;
  }

  public byte[] getByteData(){
    return inputData;
  }

}

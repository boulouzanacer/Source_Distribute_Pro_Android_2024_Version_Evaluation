package com.safesoft.proapp.distribute.eventsClasses;

import android.location.Location;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class LocationEvent {
  private Location mLocation;

  public LocationEvent(Location location){
    this.mLocation = location;
  }

  public Location getLocationData(){
    return mLocation;
  }
}

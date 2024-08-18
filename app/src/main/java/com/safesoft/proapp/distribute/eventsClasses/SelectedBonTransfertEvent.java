package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedBonTransfertEvent {

    private final String num_bon;

    public SelectedBonTransfertEvent(String num_bon) {
        this.num_bon = num_bon;
    }

    public String getNum_bon() {
        return num_bon;
    }
}

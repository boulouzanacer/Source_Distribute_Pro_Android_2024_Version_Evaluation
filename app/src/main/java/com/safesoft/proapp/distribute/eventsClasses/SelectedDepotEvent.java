package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedDepotEvent {

    private final String code_depot;
    private final String nom_depot;

    public SelectedDepotEvent(String code_depot, String nomDepot) {
        this.code_depot = code_depot;
        this.nom_depot = nomDepot;
    }

    public String getCode_depot() {
        return code_depot;
    }

    public String getNom_depot() {
        return nom_depot;
    }
}

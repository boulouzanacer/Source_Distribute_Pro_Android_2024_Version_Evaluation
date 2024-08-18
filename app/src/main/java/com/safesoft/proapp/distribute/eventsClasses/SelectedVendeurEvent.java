package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedVendeurEvent {

    private final String code_vendeur;
    private final String nom_vendeur;

    public SelectedVendeurEvent(String code_depot, String nomDepot) {
        this.code_vendeur = code_depot;
        this.nom_vendeur = nomDepot;
    }

    public String getCode_vendeur() {
        return code_vendeur;
    }

    public String getNom_vendeur() {
        return nom_vendeur;
    }
}

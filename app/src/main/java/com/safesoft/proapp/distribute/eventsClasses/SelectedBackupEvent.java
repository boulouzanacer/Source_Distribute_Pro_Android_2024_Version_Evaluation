package com.safesoft.proapp.distribute.eventsClasses;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedBackupEvent {

    private final String nom_database;

    public SelectedBackupEvent(String nom_database) {

        this.nom_database = nom_database;
    }

    public String getNom_database() {
        return nom_database;
    }
}

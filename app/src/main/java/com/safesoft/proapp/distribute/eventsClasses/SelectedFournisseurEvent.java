package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Fournisseur;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class SelectedFournisseurEvent {

    private final PostData_Fournisseur fournisseur;

    public SelectedFournisseurEvent(PostData_Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public PostData_Fournisseur getFournisseur() {
        return fournisseur;
    }
}

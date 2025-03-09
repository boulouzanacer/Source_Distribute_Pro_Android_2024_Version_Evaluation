package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Produit;
import com.safesoft.proapp.distribute.postData.PostData_Tournee2;

/**
 * Created by UK2016 on 16/07/2023.
 */

public class PassageEvent {

    private final PostData_Tournee2 tournee2;

    public PassageEvent(PostData_Tournee2 tournee2) {
        this.tournee2 = tournee2;
    }

    public PostData_Tournee2 getProduct() {
        return tournee2;
    }
}

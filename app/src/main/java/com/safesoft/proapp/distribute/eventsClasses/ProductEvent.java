package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Produit;

/**
 * Created by UK2016 on 16/07/2023.
 */

public class ProductEvent {

    private final PostData_Produit produit;

    public ProductEvent(PostData_Produit produit) {
        this.produit = produit;
    }

    public PostData_Produit getProduct() {
        return produit;
    }
}

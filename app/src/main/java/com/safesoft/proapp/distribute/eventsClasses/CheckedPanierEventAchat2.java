package com.safesoft.proapp.distribute.eventsClasses;

import com.safesoft.proapp.distribute.postData.PostData_Achat2;
import com.safesoft.proapp.distribute.postData.PostData_Bon2;

/**
 * Created by UK2016 on 28/03/2017.
 */

public class CheckedPanierEventAchat2 {
    private final PostData_Achat2 panier;
    private final double qte_old;
    private final double gratuit_old;
    private boolean if_exist = false;

    public CheckedPanierEventAchat2(PostData_Achat2 panier, double qte_old, double gratuit_old, boolean if_exist) {
        this.panier = panier;
        this.qte_old = qte_old;
        this.gratuit_old = gratuit_old;
        this.if_exist = if_exist;
    }

    public PostData_Achat2 getData() {
        return panier;
    }

    public double getQteOld() {
        return qte_old;
    }

    public double getGratuitOld() {
        return gratuit_old;
    }

    public boolean getIfExist() {
        return if_exist;
    }

}

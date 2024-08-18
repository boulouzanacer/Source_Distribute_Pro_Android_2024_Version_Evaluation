package com.safesoft.proapp.distribute.adapters.model;

import java.util.concurrent.atomic.AtomicInteger;

public class MyDataObject {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger();

    private final String mTitle;
    private final double mQuantite;
    private final double mMontant;
    private final String mParent;

    public MyDataObject(String title, double quantite, double montant, String mParent) {
        this.mTitle = title;
        this.mQuantite = quantite;
        this.mMontant = montant;
        this.mParent = mParent;
    }

    public String getParent() {
        return mParent;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getQuantite() {
        return mQuantite;
    }

    public double getMontant() {
        return mMontant;
    }
}
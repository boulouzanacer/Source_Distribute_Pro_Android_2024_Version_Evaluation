package com.safesoft.pro.distribute.adapters.model;

import java.util.concurrent.atomic.AtomicInteger;

public class MyDataObject {

  private static final AtomicInteger sNextGeneratedId = new AtomicInteger();

  private final String mTitle;
  private final String mQuantite;
  private final String mMontant;
  private final int mId;

  public MyDataObject(String title, String quantite,String montant) {
    this.mTitle = title;
    this.mQuantite = quantite;
    this.mMontant = montant;
    this.mId = sNextGeneratedId.getAndIncrement();
  }

  public int getId() {
    return mId;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getQuantite() {
    return mQuantite;
  }

  public String getMontant() {
    return mMontant;
  }
}
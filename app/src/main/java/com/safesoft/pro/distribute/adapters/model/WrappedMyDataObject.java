package com.safesoft.pro.distribute.adapters.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class WrappedMyDataObject {

  public static final int ITEM_TYPE_NORMAL = 1;
  public static final int ITEM_TYPE_HEADER = 2;
  public static final int ITEM_TYPE_HEADER_TOTAL = 3;
  public static final int ITEM_TYPE_OBJECTIF = 4;

  private final MyDataObject mDataObject;
  private final String mHeaderTitle;
  private final String mHeaderQuantite;
  private final String mHeaderMontant;
  private final int mType;

  private WrappedMyDataObject(int type, @Nullable MyDataObject dataObject, @Nullable String headerTitle, @Nullable String headerQuantite, @Nullable String headerMontant) {
    mDataObject = dataObject;
    mHeaderTitle = headerTitle;
    mHeaderQuantite = headerQuantite;
    mHeaderMontant = headerMontant;
    mType = type;
  }

  public static WrappedMyDataObject initDataItem(@NonNull MyDataObject dataObject) {
    return new WrappedMyDataObject(ITEM_TYPE_NORMAL, dataObject,null,null,null);
  }

  public static WrappedMyDataObject initHeaderItem(@NonNull String headerTitle, @NonNull String headerQuantite, @NonNull String headerMontant) {
    return new WrappedMyDataObject(ITEM_TYPE_HEADER, null, headerTitle, headerQuantite, headerMontant);
  }

  public static WrappedMyDataObject initHeaderItemTotal(String title) {
    return new WrappedMyDataObject(ITEM_TYPE_HEADER_TOTAL, null, title, null, null);
  }

  public static WrappedMyDataObject initDataItemObjectif(@NonNull MyDataObject dataObject) {
    return new WrappedMyDataObject(ITEM_TYPE_OBJECTIF, dataObject,null,null,null);
  }
  public int getType() {
    return mType;
  }

  public MyDataObject getDataObject() {
    return mDataObject;
  }

  public String getHeaderTitle() {
    return mHeaderTitle;
  }

  public String getmHeaderQuantite() {
    return mHeaderQuantite;
  }

  public String getmHeaderMontant() {
    return mHeaderMontant;
  }
}

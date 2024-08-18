package com.safesoft.proapp.distribute.postData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by UK2015 on 24/08/2016.
 */
public class PostData_Vendeur implements Parcelable {

    public int RECORDID;
    public String nom_vendeur;
    public String code_vendeur;

    public PostData_Vendeur() {

    }

    public static final Creator<PostData_Vendeur> CREATOR = new Creator<>() {
        @Override
        public PostData_Vendeur createFromParcel(Parcel in) {
            return new PostData_Vendeur();
        }

        @Override
        public PostData_Vendeur[] newArray(int size) {
            return new PostData_Vendeur[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(RECORDID);
        dest.writeString(nom_vendeur);
        dest.writeString(code_vendeur);
    }
}

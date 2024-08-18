package com.safesoft.proapp.distribute.postData;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * Created by UK2015 on 24/08/2016.
 */
public class PostData_Depot implements Parcelable {

    public int RECORDID;
    public String nom_depot;
    public String code_depot;

    public PostData_Depot() {

    }

    public static final Creator<PostData_Depot> CREATOR = new Creator<>() {
        @Override
        public PostData_Depot createFromParcel(Parcel in) {
            return new PostData_Depot();
        }

        @Override
        public PostData_Depot[] newArray(int size) {
            return new PostData_Depot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(RECORDID);
        dest.writeString(nom_depot);
        dest.writeString(code_depot);
    }
}

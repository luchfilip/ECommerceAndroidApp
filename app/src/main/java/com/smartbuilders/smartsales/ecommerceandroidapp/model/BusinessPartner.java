package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 30/5/2016.
 */
public class BusinessPartner extends Model implements Parcelable {

    private String name;
    private String taxId;
    private String address;


    protected BusinessPartner(Parcel in) {
        super(in);
        name = in.readString();
        taxId = in.readString();
        address = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(taxId);
        dest.writeString(address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessPartner> CREATOR = new Creator<BusinessPartner>() {
        @Override
        public BusinessPartner createFromParcel(Parcel in) {
            return new BusinessPartner(in);
        }

        @Override
        public BusinessPartner[] newArray(int size) {
            return new BusinessPartner[size];
        }
    };
}

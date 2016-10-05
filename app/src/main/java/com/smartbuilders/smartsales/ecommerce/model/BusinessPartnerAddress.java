package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 5/10/2016.
 */
public class BusinessPartnerAddress extends Model implements Parcelable {

    public static final int TYPE_DELIVERY_ADDRESS = 1;

    private String address;
    private int addressType;

    public BusinessPartnerAddress() {

    }

    protected BusinessPartnerAddress(Parcel in) {
        super(in);
        address = in.readString();
        addressType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(address);
        dest.writeInt(addressType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessPartnerAddress> CREATOR = new Creator<BusinessPartnerAddress>() {
        @Override
        public BusinessPartnerAddress createFromParcel(Parcel in) {
            return new BusinessPartnerAddress(in);
        }

        @Override
        public BusinessPartnerAddress[] newArray(int size) {
            return new BusinessPartnerAddress[size];
        }
    };

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }
}

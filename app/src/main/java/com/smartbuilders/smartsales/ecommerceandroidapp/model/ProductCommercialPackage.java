package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 4/23/2016.
 */
public class ProductCommercialPackage implements Parcelable {

    private int units;
    private String unitDescription;

    public ProductCommercialPackage(){

    }

    public ProductCommercialPackage(int units, String unitDescription){
        setUnits(units);
        setUnitDescription(unitDescription);
    }

    protected ProductCommercialPackage(Parcel in) {
        units = in.readInt();
        unitDescription = in.readString();
    }

    public static final Creator<ProductCommercialPackage> CREATOR = new Creator<ProductCommercialPackage>() {
        @Override
        public ProductCommercialPackage createFromParcel(Parcel in) {
            return new ProductCommercialPackage(in);
        }

        @Override
        public ProductCommercialPackage[] newArray(int size) {
            return new ProductCommercialPackage[size];
        }
    };

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    public void setUnitDescription(String unitDescription) {
        this.unitDescription = unitDescription;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(units);
        dest.writeString(unitDescription);
    }
}

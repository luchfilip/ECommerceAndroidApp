package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by stein on 16/7/2016.
 */
public class ProductTax extends Model implements Parcelable {

    private float percentage;
    private String name;

    public ProductTax() {
        super();
    }

    protected ProductTax(Parcel in) {
        super(in);
        percentage = in.readFloat();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(percentage);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductTax> CREATOR = new Creator<ProductTax>() {
        @Override
        public ProductTax createFromParcel(Parcel in) {
            return new ProductTax(in);
        }

        @Override
        public ProductTax[] newArray(int size) {
            return new ProductTax[size];
        }
    };

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public String getPercentageStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getPercentage());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

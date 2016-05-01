package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 7/4/2016.
 */
public class WishListLine extends Model implements Parcelable {

    private Product product;

    public WishListLine() {

    }

    protected WishListLine(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WishListLine> CREATOR = new Creator<WishListLine>() {
        @Override
        public WishListLine createFromParcel(Parcel in) {
            return new WishListLine(in);
        }

        @Override
        public WishListLine[] newArray(int size) {
            return new WishListLine[size];
        }
    };

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}

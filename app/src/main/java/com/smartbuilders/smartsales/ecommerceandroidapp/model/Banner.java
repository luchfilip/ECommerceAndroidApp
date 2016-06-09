package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 1/6/2016.
 */
public class Banner extends Model implements Parcelable {

    private String imageFileName;
    private int productId;
    private int productBrandId;
    private int productSubCategoryId;
    private int productCategoryId;

    public Banner(){

    }

    protected Banner(Parcel in) {
        super(in);
        imageFileName = in.readString();
        productId = in.readInt();
        productBrandId = in.readInt();
        productSubCategoryId = in.readInt();
        productCategoryId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(imageFileName);
        dest.writeInt(productId);
        dest.writeInt(productBrandId);
        dest.writeInt(productSubCategoryId);
        dest.writeInt(productCategoryId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        @Override
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        @Override
        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductBrandId() {
        return productBrandId;
    }

    public void setProductBrandId(int productBrandId) {
        this.productBrandId = productBrandId;
    }

    public int getProductSubCategoryId() {
        return productSubCategoryId;
    }

    public void setProductSubCategoryId(int productSubCategoryId) {
        this.productSubCategoryId = productSubCategoryId;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}

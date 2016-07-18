package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 25/3/2016.
 */
public class ProductCategory extends Model implements Parcelable {

    public static final Parcelable.Creator<ProductCategory> CREATOR = new Parcelable.Creator<ProductCategory>() {
        @Override
        public ProductCategory createFromParcel(Parcel in) {
            return new ProductCategory(in);
        }

        @Override
        public ProductCategory[] newArray(int size) {
            return new ProductCategory[size];
        }
    };

    private int imageId;
    private String name;
    private String description;
    private int productsActiveQty;

    public ProductCategory(){
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getProductsActiveQty() {
        return productsActiveQty;
    }

    public void setProductsActiveQty(int productsActiveQty) {
        this.productsActiveQty = productsActiveQty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ProductCategory(Parcel in) {
        super(in);
        imageId = in.readInt();
        name = in.readString();
        description = in.readString();
        productsActiveQty = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(imageId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(productsActiveQty);
    }

}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 23/3/2016.
 */
public class ProductBrand extends Model implements Parcelable {

    private String name;
    private String description;
    private int imageId;
    private int productsActiveQty;

    public ProductBrand(){

    }

    public ProductBrand(int id, String name, String description){
        setId(id);
        setName(name);
        setDescription(description);
    }

    protected ProductBrand(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
        imageId = in.readInt();
        productsActiveQty = in.readInt();
    }

    public static final Creator<ProductBrand> CREATOR = new Creator<ProductBrand>() {
        @Override
        public ProductBrand createFromParcel(Parcel in) {
            return new ProductBrand(in);
        }

        @Override
        public ProductBrand[] newArray(int size) {
            return new ProductBrand[size];
        }
    };

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(imageId);
        dest.writeInt(productsActiveQty);
    }
}

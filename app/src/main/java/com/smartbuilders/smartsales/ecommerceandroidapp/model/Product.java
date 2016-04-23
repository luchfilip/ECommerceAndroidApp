package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;

import java.util.ArrayList;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Product extends Model implements Parcelable {

    private String internalCode;
    private ProductBrand brand;
    private int imageId = R.mipmap.ic_launcher;
    private ArrayList<Integer> imagesIds;
    private String name;
    private String description;

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public ProductSubCategory getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(ProductSubCategory productSubCategory) {
        this.productSubCategory = productSubCategory;
    }

    private ProductCategory productCategory;
    private ProductSubCategory productSubCategory;

    public Product(){

    }

    protected Product(Parcel in) {
        internalCode = in.readString();
        imageId = in.readInt();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
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

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public ProductBrand getBrand() {
        return brand;
    }

    public void setBrand(ProductBrand brand) {
        this.brand = brand;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ArrayList<Integer> getImagesIds() {
        return imagesIds;
    }

    public void setImagesIds(ArrayList<Integer> imagesIds) {
        this.imagesIds = imagesIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(internalCode);
        dest.writeInt(imageId);
        dest.writeString(name);
        dest.writeString(description);
    }
}

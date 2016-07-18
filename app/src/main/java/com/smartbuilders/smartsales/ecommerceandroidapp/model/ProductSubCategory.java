package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 27/3/2016.
 */
public class ProductSubCategory extends ProductCategory implements Parcelable {

    public static final Parcelable.Creator<ProductSubCategory> CREATOR = new Parcelable.Creator<ProductSubCategory>() {
        @Override
        public ProductSubCategory createFromParcel(Parcel in) {
            return new ProductSubCategory(in);
        }

        @Override
        public ProductSubCategory[] newArray(int size) {
            return new ProductSubCategory[size];
        }
    };

    private int productCategoryId;
    private int productsActiveQty;

    public ProductSubCategory() {
        super();
    }

    public long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    @Override
    public int getProductsActiveQty() {
        return productsActiveQty;
    }

    @Override
    public void setProductsActiveQty(int productsActiveQty) {
        this.productsActiveQty = productsActiveQty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ProductSubCategory(Parcel in) {
        super(in);
        productCategoryId = in.readInt();
        productsActiveQty = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productCategoryId);
        dest.writeInt(productsActiveQty);
    }

    @Override
    public boolean equals(Object o) {
        try {
            return ((ProductSubCategory) o).getId() == getId();
        } catch (Exception e) { }
        return super.equals(o);
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 27/3/2016.
 */
public class ProductSubCategory extends ProductCategory {

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

    public ProductSubCategory() {
    }

    public ProductSubCategory(int productCategoryId, int id, String name, String description) {
        setProductCategoryId(productCategoryId);
        setId(id);
        setName(name);
        setDescription(description);
    }

    public long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ProductSubCategory(Parcel in) {
        super(in);
        productCategoryId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productCategoryId);
    }
}

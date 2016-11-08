package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlbertoSarco on 8/11/2016.
 */
public class ProductSearchResult implements Parcelable {

    private int productId;
    private String productName;
    private String productInternalCode;
    private String productReference;
    private String productPurpose;
    private int productSubCategoryId;
    private String productSubCategoryName;

    public ProductSearchResult() {

    }

    protected ProductSearchResult(Parcel in) {
        productId = in.readInt();
        productName = in.readString();
        productInternalCode = in.readString();
        productReference = in.readString();
        productPurpose = in.readString();
        productSubCategoryId = in.readInt();
        productSubCategoryName = in.readString();
    }

    public static final Creator<ProductSearchResult> CREATOR = new Creator<ProductSearchResult>() {
        @Override
        public ProductSearchResult createFromParcel(Parcel in) {
            return new ProductSearchResult(in);
        }

        @Override
        public ProductSearchResult[] newArray(int size) {
            return new ProductSearchResult[size];
        }
    };

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductInternalCode() {
        return productInternalCode;
    }

    public void setProductInternalCode(String productInternalCode) {
        this.productInternalCode = productInternalCode;
    }

    public String getProductInternalCodeMayoreoFormat() {
        try {
            return productInternalCode.charAt(0)+"-"+productInternalCode.substring(1,4)+"-"+productInternalCode.substring(4);
        } catch (Exception e) {
            return productInternalCode;
        }
    }

    public String getProductReference() {
        return productReference;
    }

    public void setProductReference(String productReference) {
        this.productReference = productReference;
    }

    public String getProductPurpose() {
        return productPurpose;
    }

    public void setProductPurpose(String productPurpose) {
        this.productPurpose = productPurpose;
    }

    public int getProductSubCategoryId() {
        return productSubCategoryId;
    }

    public void setProductSubCategoryId(int productSubCategoryId) {
        this.productSubCategoryId = productSubCategoryId;
    }

    public String getProductSubCategoryName() {
        return productSubCategoryName;
    }

    public void setProductSubCategoryName(String productSubCategoryName) {
        this.productSubCategoryName = productSubCategoryName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(productName);
        dest.writeString(productInternalCode);
        dest.writeString(productReference);
        dest.writeString(productPurpose);
        dest.writeInt(productSubCategoryId);
        dest.writeString(productSubCategoryName);
    }
}

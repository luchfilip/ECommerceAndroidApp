package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearch extends Model implements Parcelable {

    private String textToSearch;
    private String productName;
    private String productReference;
    private String productPurpose;
    private int productId;
    private int subcategoryId;
    private ProductSubCategory productSubCategory;

    public RecentSearch(){
        super();
    }

    protected RecentSearch(Parcel in) {
        super(in);
        textToSearch = in.readString();
        productName = in.readString();
        productReference = in.readString();
        productPurpose = in.readString();
        productId = in.readInt();
        subcategoryId = in.readInt();
        productSubCategory = in.readParcelable(ProductSubCategory.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(textToSearch);
        dest.writeString(productName);
        dest.writeString(productReference);
        dest.writeString(productPurpose);
        dest.writeInt(productId);
        dest.writeInt(subcategoryId);
        dest.writeParcelable(productSubCategory, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecentSearch> CREATOR = new Creator<RecentSearch>() {
        @Override
        public RecentSearch createFromParcel(Parcel in) {
            return new RecentSearch(in);
        }

        @Override
        public RecentSearch[] newArray(int size) {
            return new RecentSearch[size];
        }
    };

    public String getTextToSearch() {
        return textToSearch;
    }

    public void setTextToSearch(String textToSearch) {
        this.textToSearch = textToSearch;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(int subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public ProductSubCategory getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(ProductSubCategory productSubCategory) {
        this.productSubCategory = productSubCategory;
    }
}

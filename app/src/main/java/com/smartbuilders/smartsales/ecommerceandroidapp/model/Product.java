package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Product extends Model implements Parcelable {

    private int productBrandId;
    private ProductBrand productBrand;
    private String name;
    private String internalCode;
    private String description;
    private String imageFileName;
    private int productCategoryId;
    private ProductCategory productCategory;
    private int productSubCategoryId;
    private ProductSubCategory productSubCategory;
    private int productCommercialPackageId;
    private ProductCommercialPackage productCommercialPackage;
    private int availability;
    private boolean isFavorite;
    private float rating = -1;

    public Product(){
        super();
    }

    protected Product(Parcel in) {
        super(in);
        name = in.readString();
        internalCode = in.readString();
        description = in.readString();
        imageFileName = in.readString();
        availability = in.readInt();
        productBrandId = in.readInt();
        productBrand = in.readParcelable(ProductBrand.class.getClassLoader());
        productCategoryId = in.readInt();
        productCategory = in.readParcelable(ProductCategory.class.getClassLoader());
        productSubCategoryId = in.readInt();
        productSubCategory = in.readParcelable(ProductSubCategory.class.getClassLoader());
        productCommercialPackageId = in.readInt();
        productCommercialPackage = in.readParcelable(ProductCommercialPackage.class.getClassLoader());
        isFavorite = in.readByte() == 1;
        rating = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(internalCode);
        dest.writeString(description);
        dest.writeString(imageFileName);
        dest.writeInt(availability);
        dest.writeInt(productBrandId);
        dest.writeParcelable(productBrand, flags);
        dest.writeInt(productCategoryId);
        dest.writeParcelable(productCategory, flags);
        dest.writeInt(productSubCategoryId);
        dest.writeParcelable(productSubCategory, flags);
        dest.writeInt(productCommercialPackageId);
        dest.writeParcelable(productCommercialPackage, flags);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeFloat(rating);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public ProductBrand getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(ProductBrand productBrand) {
        this.productBrand = productBrand;
    }

    public ProductCommercialPackage getProductCommercialPackage() {
        return productCommercialPackage;
    }

    public void setProductCommercialPackage(ProductCommercialPackage productCommercialPackage) {
        this.productCommercialPackage = productCommercialPackage;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getProductBrandId() {
        return productBrandId;
    }

    public void setProductBrandId(int productBrandId) {
        this.productBrandId = productBrandId;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public int getProductSubCategoryId() {
        return productSubCategoryId;
    }

    public void setProductSubCategoryId(int productSubCategoryId) {
        this.productSubCategoryId = productSubCategoryId;
    }

    public int getProductCommercialPackageId() {
        return productCommercialPackageId;
    }

    public void setProductCommercialPackageId(int productCommercialPackageId) {
        this.productCommercialPackageId = productCommercialPackageId;
    }

    @Override
    public boolean equals(Object o) {
        try {
            return ((Product) o).getName().equals(getName())
                    && ((Product) o).getProductSubCategory().equals(getProductSubCategory());
        } catch (Exception e) { }
        return super.equals(o);
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Product extends Model implements Parcelable {

    private ProductBrand productBrand;
    private int imageId = R.drawable.no_image_available;
    private String name;
    private String description;
    private String imageFileName;
    private ProductCategory productCategory;
    private ProductSubCategory productSubCategory;
    private ProductCommercialPackage productCommercialPackage;
    private int availability;
    private boolean isFavorite;

    public Product(){
        super();
    }

    protected Product(Parcel in) {
        super(in);
        imageId = in.readInt();
        name = in.readString();
        description = in.readString();
        imageFileName = in.readString();
        availability = in.readInt();
        productBrand = in.readParcelable(ProductBrand.class.getClassLoader());
        productCategory = in.readParcelable(ProductCategory.class.getClassLoader());
        productSubCategory = in.readParcelable(ProductSubCategory.class.getClassLoader());
        productCommercialPackage = in.readParcelable(ProductCommercialPackage.class.getClassLoader());
        isFavorite = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(imageId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageFileName);
        dest.writeInt(availability);
        dest.writeParcelable(productBrand, flags);
        dest.writeParcelable(productCategory, flags);
        dest.writeParcelable(productSubCategory, flags);
        dest.writeParcelable(productCommercialPackage, flags);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
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

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
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

    @Override
    public boolean equals(Object o) {
        try {
            return ((Product) o).getName().equals(getName())
                    && ((Product) o).getProductSubCategory().equals(getProductSubCategory());
        } catch (Exception e) { }
        return super.equals(o);
    }
}

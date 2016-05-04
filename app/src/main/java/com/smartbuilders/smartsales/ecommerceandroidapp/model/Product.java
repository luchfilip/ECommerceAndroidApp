package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.smartbuilders.smartsales.ecommerceandroidapp.R;

import java.util.ArrayList;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Product extends Model implements Parcelable {

    private ProductBrand productBrand;
    private int imageId = R.drawable.no_image_available;
    private ArrayList<Integer> imagesIds;
    private String name;
    private String description;
    private String imageFileName;
    private ProductCategory productCategory;
    private ProductSubCategory productSubCategory;
    private ProductCommercialPackage productCommercialPackage;
    private int availability;

    public Product(){
        super();
    }

    protected Product(Parcel in) {
        super(in);
        productBrand = in.readParcelable(ProductBrand.class.getClassLoader());
        imageId = in.readInt();
        name = in.readString();
        description = in.readString();
        imageFileName = in.readString();
        productCategory = in.readParcelable(ProductCategory.class.getClassLoader());
        productSubCategory = in.readParcelable(ProductSubCategory.class.getClassLoader());
        productCommercialPackage = in.readParcelable(ProductCommercialPackage.class.getClassLoader());
        availability = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(productBrand, flags);
        dest.writeInt(imageId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageFileName);
        dest.writeParcelable(productCategory, flags);
        dest.writeParcelable(productSubCategory, flags);
        dest.writeParcelable(productCommercialPackage, flags);
        dest.writeInt(availability);
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

    public ArrayList<Integer> getImagesIds() {
        return imagesIds;
    }

    public void setImagesIds(ArrayList<Integer> imagesIds) {
        this.imagesIds = imagesIds;
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

    @Override
    public boolean equals(Object o) {
        try {
            return ((Product) o).getName().equals(getName())
                    && ((Product) o).getProductSubCategory().equals(getProductSubCategory());
        } catch (Exception e) { }
        return super.equals(o);
    }
}

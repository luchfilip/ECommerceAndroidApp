package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Product extends Model implements Parcelable {

    private int productBrandId;
    private ProductBrand productBrand;
    private String name;
    private String internalCode;
    private String description;
    private String purpose;
    private String imageFileName;
    private int productCategoryId;
    private ProductCategory productCategory;
    private int productSubCategoryId;
    private ProductSubCategory productSubCategory;
    private int productCommercialPackageId;
    private ProductCommercialPackage productCommercialPackage;
    private int productTaxId;
    private ProductTax productTax;
    private ProductPriceAvailability defaultProductPriceAvailability;
    private List<ProductPriceAvailability> productPriceAvailabilities;
    private boolean isFavorite;
    private float rating = -1;

    public Product(){
        super();
        //Se inicializan estos objetos para evitar NullPointerException
        productPriceAvailabilities = new ArrayList<>();
        productBrand = new ProductBrand();
        defaultProductPriceAvailability = new ProductPriceAvailability();
        productTax = new ProductTax();
        productCategory = new ProductCategory();
        productSubCategory = new ProductSubCategory();
        productCommercialPackage = new ProductCommercialPackage();
    }

    protected Product(Parcel in) {
        super(in);
        productBrandId = in.readInt();
        productBrand = in.readParcelable(ProductBrand.class.getClassLoader());
        name = in.readString();
        internalCode = in.readString();
        description = in.readString();
        purpose = in.readString();
        imageFileName = in.readString();
        productCategoryId = in.readInt();
        productCategory = in.readParcelable(ProductCategory.class.getClassLoader());
        productSubCategoryId = in.readInt();
        productSubCategory = in.readParcelable(ProductSubCategory.class.getClassLoader());
        productCommercialPackageId = in.readInt();
        productCommercialPackage = in.readParcelable(ProductCommercialPackage.class.getClassLoader());
        productTaxId = in.readInt();
        productTax = in.readParcelable(ProductTax.class.getClassLoader());
        isFavorite = in.readByte() != 0;
        rating = in.readFloat();
        defaultProductPriceAvailability = in.readParcelable(ProductPriceAvailability.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productBrandId);
        dest.writeParcelable(productBrand, flags);
        dest.writeString(name);
        dest.writeString(internalCode);
        dest.writeString(description);
        dest.writeString(purpose);
        dest.writeString(imageFileName);
        dest.writeInt(productCategoryId);
        dest.writeParcelable(productCategory, flags);
        dest.writeInt(productSubCategoryId);
        dest.writeParcelable(productSubCategory, flags);
        dest.writeInt(productCommercialPackageId);
        dest.writeParcelable(productCommercialPackage, flags);
        dest.writeInt(productTaxId);
        dest.writeParcelable(productTax, flags);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeFloat(rating);
        dest.writeParcelable(defaultProductPriceAvailability, flags);
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

    public ProductSubCategory getProductSubCategory() {
        return productSubCategory;
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

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public ProductCommercialPackage getProductCommercialPackage() {
        return productCommercialPackage;
    }

    public int getProductTaxId() {
        return productTaxId;
    }

    public void setProductTaxId(int productTaxId) {
        this.productTaxId = productTaxId;
    }

    public ProductTax getProductTax() {
        return productTax;
    }

    public ProductPriceAvailability getDefaultProductPriceAvailability(){
        return defaultProductPriceAvailability;
    }

    public void setProductPriceAvailabilities(List<ProductPriceAvailability> productPriceAvailabilities) {
        this.productPriceAvailabilities = productPriceAvailabilities;
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

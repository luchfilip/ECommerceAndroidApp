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
    private String purpose;
    private String imageFileName;
    private int productCategoryId;
    private ProductCategory productCategory;
    private int productSubCategoryId;
    private ProductSubCategory productSubCategory;
    private int productCommercialPackageId;
    private ProductCommercialPackage productCommercialPackage;
    private int taxId;
    private Tax tax;
    private float price;
    private int currencyId;
    private Currency currency;
    private int availability;
    private boolean isFavorite;
    private float rating = -1;

    public Product(){
        super();
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
        taxId = in.readInt();
        tax = in.readParcelable(Tax.class.getClassLoader());
        price = in.readFloat();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        availability = in.readInt();
        isFavorite = in.readByte() != 0;
        rating = in.readFloat();
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
        dest.writeInt(taxId);
        dest.writeParcelable(tax, flags);
        dest.writeFloat(price);
        dest.writeInt(currencyId);
        dest.writeParcelable(currency, flags);
        dest.writeInt(availability);
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

    public void setProductBrand(ProductBrand productBrand) {
        this.productBrand = productBrand;
    }

    public ProductCommercialPackage getProductCommercialPackage() {
        return productCommercialPackage;
    }

    public void setProductCommercialPackage(ProductCommercialPackage productCommercialPackage) {
        this.productCommercialPackage = productCommercialPackage;
    }

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
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

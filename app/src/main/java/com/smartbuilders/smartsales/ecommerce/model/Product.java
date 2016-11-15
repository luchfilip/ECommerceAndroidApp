package com.smartbuilders.smartsales.ecommerce.model;

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
    private String reference;
    private int productCategoryId;
    private int productSubCategoryId;
    private ProductCommercialPackage productCommercialPackage;
    private int productTaxId;
    private ProductTax productTax;
    private ProductPriceAvailability productPriceAvailability;
    private boolean isFavorite;
    private float rating = -1;
    private boolean requireFullFill;

    public Product(){
        super();
        //Se inicializan estos objetos para evitar NullPointerException
        productBrand = new ProductBrand();
        productPriceAvailability = new ProductPriceAvailability();
        productTax = new ProductTax();
        productCommercialPackage = new ProductCommercialPackage();
        requireFullFill = true;
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
        reference = in.readString();
        productCategoryId = in.readInt();
        productSubCategoryId = in.readInt();
        productCommercialPackage = in.readParcelable(ProductCommercialPackage.class.getClassLoader());
        productTaxId = in.readInt();
        productTax = in.readParcelable(ProductTax.class.getClassLoader());
        isFavorite = in.readByte() != 0;
        rating = in.readFloat();
        productPriceAvailability = in.readParcelable(ProductPriceAvailability.class.getClassLoader());
        requireFullFill = in.readByte() != 0;
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
        dest.writeString(reference);
        dest.writeInt(productCategoryId);
        dest.writeInt(productSubCategoryId);
        dest.writeParcelable(productCommercialPackage, flags);
        dest.writeInt(productTaxId);
        dest.writeParcelable(productTax, flags);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeFloat(rating);
        dest.writeParcelable(productPriceAvailability, flags);
        dest.writeByte((byte) (requireFullFill ? 1 : 0));
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
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

    public ProductPriceAvailability getProductPriceAvailability(){
        return productPriceAvailability;
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

    public String getInternalCodeMayoreoFormat() {
        try {
            return (new StringBuilder()).append(internalCode.charAt(0)).append("-")
                    .append(internalCode.substring(1,4)).append("-")
                    .append(internalCode.substring(4)).toString();
        } catch (Exception e) {
            return internalCode;
        }
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

    public boolean isRequireFullFill() {
        return requireFullFill;
    }

    public void setRequireFullFill(boolean requireFullFill) {
        this.requireFullFill = requireFullFill;
    }

    @Override
    public boolean equals(Object o) {
        try {
            return ((Product) o).getName().equals(name)
                    && ((Product) o).getProductSubCategoryId() == productSubCategoryId;
        } catch (Exception e) { }
        return super.equals(o);
    }
}

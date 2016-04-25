package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProduct extends Model implements Parcelable {

    private int productId;
    private int mainPageSectionId;
    private Product product;
    private int priority;

    public MainPageProduct() {

    }

    protected MainPageProduct(Parcel in) {
        super(in);
        productId = in.readInt();
        mainPageSectionId = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        priority = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productId);
        dest.writeInt(mainPageSectionId);
        dest.writeParcelable(product, flags);
        dest.writeInt(priority);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainPageProduct> CREATOR = new Creator<MainPageProduct>() {
        @Override
        public MainPageProduct createFromParcel(Parcel in) {
            return new MainPageProduct(in);
        }

        @Override
        public MainPageProduct[] newArray(int size) {
            return new MainPageProduct[size];
        }
    };

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getMainPageSectionId() {
        return mainPageSectionId;
    }

    public void setMainPageSectionId(int mainPageSectionId) {
        this.mainPageSectionId = mainPageSectionId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

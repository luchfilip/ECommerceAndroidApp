package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 5/4/2016.
 */
public class OrderLine extends Model implements Parcelable {

    private Product product;
    private int quantityOrdered;
    private double price;
    private Currency currency;

    public OrderLine(){

    }

    protected OrderLine(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        price = in.readDouble();
        currency = in.readParcelable(Currency.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityOrdered);
        dest.writeDouble(price);
        dest.writeParcelable(currency, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderLine> CREATOR = new Creator<OrderLine>() {
        @Override
        public OrderLine createFromParcel(Parcel in) {
            return new OrderLine(in);
        }

        @Override
        public OrderLine[] newArray(int size) {
            return new OrderLine[size];
        }
    };

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PublicKey;

/**
 * Created by Alberto on 7/4/2016.
 */
public class InvoiceLine extends Model implements Parcelable {

    private Product product;
    private int quantityInvoiced;
    private double price;
    private Currency currency;

    public InvoiceLine() {

    }

    protected InvoiceLine(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        quantityInvoiced = in.readInt();
        price = in.readDouble();
        currency = in.readParcelable(Currency.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityInvoiced);
        dest.writeDouble(price);
        dest.writeParcelable(currency, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InvoiceLine> CREATOR = new Creator<InvoiceLine>() {
        @Override
        public InvoiceLine createFromParcel(Parcel in) {
            return new InvoiceLine(in);
        }

        @Override
        public InvoiceLine[] newArray(int size) {
            return new InvoiceLine[size];
        }
    };

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantityInvoiced() {
        return quantityInvoiced;
    }

    public void setQuantityInvoiced(int quantityInvoiced) {
        this.quantityInvoiced = quantityInvoiced;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

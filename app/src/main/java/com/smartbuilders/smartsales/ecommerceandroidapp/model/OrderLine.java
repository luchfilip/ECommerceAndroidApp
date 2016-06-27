package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 5/4/2016.
 */
public class OrderLine extends Model implements Parcelable {

    private int productId;
    private Product product;
    private int quantityOrdered;
    private double price;
    private int currencyId;
    private Currency currency;
    private double taxPercentage;
    private double totalLineAmount;

    public OrderLine(){

    }

    protected OrderLine(Parcel in) {
        super(in);
        productId = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        price = in.readDouble();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        taxPercentage = in.readDouble();
        totalLineAmount = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productId);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityOrdered);
        dest.writeDouble(price);
        dest.writeInt(currencyId);
        dest.writeParcelable(currency, flags);
        dest.writeDouble(taxPercentage);
        dest.writeDouble(totalLineAmount);
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

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getTotalLineAmount() {
        return totalLineAmount;
    }

    public void setTotalLineAmount(double totalLineAmount) {
        this.totalLineAmount = totalLineAmount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public boolean equals(Object o) {
        try{
            return ((OrderLine) o).getId() == getId();
        }catch(Exception e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderLine extends Model implements Parcelable {

    private Product product;
    private int quantityOrdered;
    private double price;
    private Currency currency;
    private double taxPercentage;
    private double totalLineAmount;
    private int businessPartnerId;

    public SalesOrderLine(){

    }

    protected SalesOrderLine(Parcel in) {
        super(in);
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        price = in.readDouble();
        currency = in.readParcelable(Currency.class.getClassLoader());
        taxPercentage = in.readDouble();
        totalLineAmount = in.readDouble();
        businessPartnerId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityOrdered);
        dest.writeDouble(price);
        dest.writeParcelable(currency, flags);
        dest.writeDouble(taxPercentage);
        dest.writeDouble(totalLineAmount);
        dest.writeInt(businessPartnerId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SalesOrderLine> CREATOR = new Parcelable.Creator<SalesOrderLine>() {
        @Override
        public SalesOrderLine createFromParcel(Parcel in) {
            return new SalesOrderLine(in);
        }

        @Override
        public SalesOrderLine[] newArray(int size) {
            return new SalesOrderLine[size];
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

    public int getBusinessPartnerId() {
        return businessPartnerId;
    }

    public void setBusinessPartnerId(int businessPartnerId) {
        this.businessPartnerId = businessPartnerId;
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

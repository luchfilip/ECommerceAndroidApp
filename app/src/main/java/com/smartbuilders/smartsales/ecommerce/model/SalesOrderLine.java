package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderLine extends Model implements Parcelable {

    private int productId;
    private Product product;
    private int quantityOrdered;
    private double price;
    private int currencyId;
    private Currency currency;
    private double taxPercentage;
    private double totalLineAmount;
    private int businessPartnerId;

    public SalesOrderLine(){

    }

    protected SalesOrderLine(Parcel in) {
        super(in);
        productId = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        price = in.readDouble();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        taxPercentage = in.readDouble();
        totalLineAmount = in.readDouble();
        businessPartnerId = in.readInt();
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

    public String getPriceStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getPrice());
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

    public String getTaxPercentageStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxPercentage());
    }

    public double getTotalLineAmount() {
        return totalLineAmount;
    }

    public void setTotalLineAmount(double totalLineAmount) {
        this.totalLineAmount = totalLineAmount;
    }

    public String getTotalLineAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalLineAmount());
    }

    public int getBusinessPartnerId() {
        return businessPartnerId;
    }

    public void setBusinessPartnerId(int businessPartnerId) {
        this.businessPartnerId = businessPartnerId;
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

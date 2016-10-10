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
    private int currencyId;
    private Currency currency;
    private double productPrice;
    private double productTaxPercentage;
    private double lineTaxAmount;
    private double subTotalLineAmount;
    private double totalLineAmount;
    private int businessPartnerId;
    private boolean isQuantityOrderedInvalid;

    public SalesOrderLine(){

    }

    protected SalesOrderLine(Parcel in) {
        super(in);
        productId = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        productPrice = in.readDouble();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        productTaxPercentage = in.readDouble();
        lineTaxAmount = in.readDouble();
        subTotalLineAmount = in.readDouble();
        totalLineAmount = in.readDouble();
        businessPartnerId = in.readInt();
        isQuantityOrderedInvalid = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productId);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityOrdered);
        dest.writeDouble(productPrice);
        dest.writeInt(currencyId);
        dest.writeParcelable(currency, flags);
        dest.writeDouble(productTaxPercentage);
        dest.writeDouble(lineTaxAmount);
        dest.writeDouble(subTotalLineAmount);
        dest.writeDouble(totalLineAmount);
        dest.writeInt(businessPartnerId);
        dest.writeByte((byte) (isQuantityOrderedInvalid ? 1 : 0));
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

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getPriceStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getProductPrice());
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

    public double getProductTaxPercentage() {
        return productTaxPercentage;
    }

    public void setProductTaxPercentage(double productTaxPercentage) {
        this.productTaxPercentage = productTaxPercentage;
    }

    public String getProductTaxPercentageStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getProductTaxPercentage());
    }

    public double getLineTaxAmount() {
        return lineTaxAmount;
    }

    public void setLineTaxAmount(double lineTaxAmount) {
        this.lineTaxAmount = lineTaxAmount;
    }

    public String getLineTaxAmountStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getLineTaxAmount());
    }

    public double getSubTotalLineAmount() {
        return subTotalLineAmount;
    }

    public void setSubTotalLineAmount(double subTotalLineAmount) {
        this.subTotalLineAmount = subTotalLineAmount;
    }

    public String getSubTotalLineAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalLineAmount());
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

    public boolean isQuantityOrderedInvalid() {
        return isQuantityOrderedInvalid;
    }

    public void setQuantityOrderedInvalid(boolean quantityOrderedInvalid) {
        isQuantityOrderedInvalid = quantityOrderedInvalid;
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

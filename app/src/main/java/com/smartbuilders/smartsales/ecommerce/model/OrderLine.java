package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by Alberto on 5/4/2016.
 */
public class OrderLine extends Model implements Parcelable {

    private int productId;
    private Product product;
    private int quantityOrdered;
    private boolean isQuantityOrderedInvalid;
    private int currencyId;
    private Currency currency;
    private double productPrice;
    private double productTaxPercentage;
    private double lineTaxAmount;
    private double subTotalLineAmount;
    private double totalLineAmount;
    private int businessPartnerId;

    public OrderLine(){

    }

    protected OrderLine(Parcel in) {
        super(in);
        productId = in.readInt();
        product = in.readParcelable(Product.class.getClassLoader());
        quantityOrdered = in.readInt();
        isQuantityOrderedInvalid = in.readByte() != 0;
        productPrice = in.readDouble();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        productTaxPercentage = in.readDouble();
        lineTaxAmount = in.readDouble();
        subTotalLineAmount = in.readDouble();
        totalLineAmount = in.readDouble();
        businessPartnerId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(productId);
        dest.writeParcelable(product, flags);
        dest.writeInt(quantityOrdered);
        dest.writeByte((byte) (isQuantityOrderedInvalid ? 1 : 0));
        dest.writeDouble(productPrice);
        dest.writeInt(currencyId);
        dest.writeParcelable(currency, flags);
        dest.writeDouble(productTaxPercentage);
        dest.writeDouble(lineTaxAmount);
        dest.writeDouble(subTotalLineAmount);
        dest.writeDouble(totalLineAmount);
        dest.writeInt(businessPartnerId);
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

    public boolean isQuantityOrderedInvalid() {
        return isQuantityOrderedInvalid;
    }

    public void setQuantityOrderedInvalid(boolean quantityOrderedInvalid) {
        isQuantityOrderedInvalid = quantityOrderedInvalid;
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

    public double getTotalLineAmount() {
        return totalLineAmount;
    }

    public void setTotalLineAmount(double totalLineAmount) {
        this.totalLineAmount = totalLineAmount;
    }

    public String getTotalLineAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalLineAmount());
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

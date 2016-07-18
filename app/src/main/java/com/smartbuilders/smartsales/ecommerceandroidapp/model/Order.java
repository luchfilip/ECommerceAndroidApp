package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by Alberto on 5/4/2016.
 */
public class Order extends Model implements Parcelable {

    private int linesNumber;
    private double subTotalAmount;
    private double taxAmount;
    private double totalAmount;
    private int salesOrderId;
    private BusinessPartner businessPartner;
    private int businessPartnerId;

    public Order() {

    }

    public String getOrderNumber() {
        return String.format(new Locale("es","VE"), "%06d", getId());
    }

    public int getLinesNumber() {
        return linesNumber;
    }

    public void setLinesNumber(int linesNumber) {
        this.linesNumber = linesNumber;
    }

    public double getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(double subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public String getSubTotalAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount());
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTruncatedTaxAmount(){
        return new BigDecimal(taxAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public String getTaxAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount());
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTruncatedTotalAmount() {
        return new BigDecimal(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public String getTotalAmountStringFormat(){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalAmount());
    }

    public int getSalesOrderId() {
        return salesOrderId;
    }

    public String getSalesOrderNumber() {
        return String.format("%06d", getSalesOrderId());
    }

    public void setSalesOrderId(int salesOrderId) {
        this.salesOrderId = salesOrderId;
    }

    public BusinessPartner getBusinessPartner() {
        return businessPartner;
    }

    public void setBusinessPartner(BusinessPartner businessPartner) {
        this.businessPartner = businessPartner;
    }

    public int getBusinessPartnerId() {
        return businessPartnerId;
    }

    public void setBusinessPartnerId(int businessPartnerId) {
        this.businessPartnerId = businessPartnerId;
    }

    protected Order(Parcel in) {
        super(in);
        linesNumber = in.readInt();
        subTotalAmount = in.readDouble();
        taxAmount = in.readDouble();
        totalAmount = in.readDouble();
        salesOrderId = in.readInt();
        businessPartner = in.readParcelable(BusinessPartner.class.getClassLoader());
        businessPartnerId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(linesNumber);
        dest.writeDouble(subTotalAmount);
        dest.writeDouble(taxAmount);
        dest.writeDouble(totalAmount);
        dest.writeInt(salesOrderId);
        dest.writeParcelable(businessPartner, flags);
        dest.writeInt(businessPartnerId);
    }

    @Override
    public boolean equals(Object o) {
        try {
            if(o instanceof Order){
                return getId() == ((Order) o).getId();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public String getCreatedStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
                    new Locale("es","VE")).format(getCreated());
        } catch (Exception e) { }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}

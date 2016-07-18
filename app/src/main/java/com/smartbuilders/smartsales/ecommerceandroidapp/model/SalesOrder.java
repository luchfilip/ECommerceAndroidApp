package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrder extends Model implements Parcelable {

    private int linesNumber;
    private double subTotalAmount;
    private double taxAmount;
    private double totalAmount;
    private BusinessPartner businessPartner;
    private int businessPartnerId;
    private Date validTo;

    public SalesOrder() {

    }

    public String getSalesOrderNumber() {
        return String.format("%06d", getId());
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

    public String getCreatedStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
                    new Locale("es","VE")).format(getCreated());
        } catch (Exception e) { }
        return null;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getValidToStringFormat() {
        try {
            return DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("es","VE")).format(getValidTo());
        } catch (Exception e) { }
        return null;
    }

    protected SalesOrder(Parcel in) {
        super(in);
        linesNumber = in.readInt();
        subTotalAmount = in.readDouble();
        taxAmount = in.readDouble();
        totalAmount = in.readDouble();
        businessPartner = in.readParcelable(BusinessPartner.class.getClassLoader());
        businessPartnerId = in.readInt();
        try{
            long date = in.readLong();
            if(date>0){
                validTo = new Date(date);
            }
        }catch(Exception e){}
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(linesNumber);
        dest.writeDouble(subTotalAmount);
        dest.writeDouble(taxAmount);
        dest.writeDouble(totalAmount);
        dest.writeParcelable(businessPartner, flags);
        dest.writeInt(businessPartnerId);
        dest.writeLong(validTo!=null ? validTo.getTime() : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<SalesOrder> CREATOR = new Parcelable.Creator<SalesOrder>() {
        @Override
        public SalesOrder createFromParcel(Parcel in) {
            return new SalesOrder(in);
        }

        @Override
        public SalesOrder[] newArray(int size) {
            return new SalesOrder[size];
        }
    };

}

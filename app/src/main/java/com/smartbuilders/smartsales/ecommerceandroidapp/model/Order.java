package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;

/**
 * Created by Alberto on 5/4/2016.
 */
public class Order extends Model implements Parcelable {

    private int orderLinesNumber;
    private double subTotalAmount;
    private double taxAmount;
    private double totalAmount;

    public Order() {

    }

    public String getOrderNumber() {
        return String.valueOf(getId());
    }

    public int getOrderLinesNumber() {
        return orderLinesNumber;
    }

    public void setOrderLinesNumber(int orderLinesNumber) {
        this.orderLinesNumber = orderLinesNumber;
    }

    public double getSubTotalAmount() {
        return subTotalAmount;
    }

    public void setSubTotalAmount(double subTotalAmount) {
        this.subTotalAmount = subTotalAmount;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    protected Order(Parcel in) {
        super(in);
        orderLinesNumber = in.readInt();
        subTotalAmount = in.readDouble();
        taxAmount = in.readDouble();
        totalAmount = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(orderLinesNumber);
        dest.writeDouble(subTotalAmount);
        dest.writeDouble(taxAmount);
        dest.writeDouble(totalAmount);
    }

    public String getCreatedStringFormat(){
        try {
            return DateFormat.getDateTimeInstance().format(getCreated());
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

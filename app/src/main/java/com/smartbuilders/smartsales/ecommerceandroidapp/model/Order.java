package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 5/4/2016.
 */
public class Order extends Model implements Parcelable {

    private String orderNumber;
    private int orderLineNumbers;


    public Order() {

    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderLineNumbers() {
        return orderLineNumbers;
    }

    public void setOrderLineNumbers(int orderLineNumbers) {
        this.orderLineNumbers = orderLineNumbers;
    }

    protected Order(Parcel in) {
        super(in);
        orderNumber = in.readString();
        orderLineNumbers = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(orderNumber);
        dest.writeInt(orderLineNumbers);
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

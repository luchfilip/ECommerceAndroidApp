package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 11/11/2016.
 */

public class Notification extends Model implements Parcelable {

    private int productId;
    private int orderTrackingId;
    private String title;
    private String message;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getOrderTrackingId() {
        return orderTrackingId;
    }

    public void setOrderTrackingId(int orderTrackingId) {
        this.orderTrackingId = orderTrackingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedTimeStringFormat(){
        try {
            return  (new SimpleDateFormat("hh:mm a", new Locale("es","VE"))).format(getCreated());
        } catch (Exception e) {
            return null;
        }
    }

    public String getCreatedDateStringFormat(){
        try {
            return DateFormat.getDateInstance(DateFormat.LONG, new Locale("es","VE")).format(getCreated());
        } catch (Exception e) {
            return null;
        }
    }
}

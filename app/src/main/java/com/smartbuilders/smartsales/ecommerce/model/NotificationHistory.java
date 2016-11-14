package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 11/11/2016.
 */

public class NotificationHistory extends Model implements Parcelable {

    public static final int TYPE_WISH_LIST_PRODUCT_AVAILABILITY_VARIATION = 1;
    public static final int TYPE_NEW_ORDER_TRACKING = 2;
    public static final int STATUS_NOT_SEEN = 1;
    public static final int STATUS_SEEN = 2;

    private int relatedId;
    private String title;
    private String message;
    private int type;
    private int status;

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

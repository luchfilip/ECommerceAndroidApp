package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
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

    public NotificationHistory() {

    }

    protected NotificationHistory(Parcel in) {
        relatedId = in.readInt();
        title = in.readString();
        message = in.readString();
        type = in.readInt();
        status = in.readInt();
    }

    public static final Creator<NotificationHistory> CREATOR = new Creator<NotificationHistory>() {
        @Override
        public NotificationHistory createFromParcel(Parcel in) {
            return new NotificationHistory(in);
        }

        @Override
        public NotificationHistory[] newArray(int size) {
            return new NotificationHistory[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(relatedId);
        dest.writeString(title);
        dest.writeString(message);
        dest.writeInt(type);
        dest.writeInt(status);
    }
}

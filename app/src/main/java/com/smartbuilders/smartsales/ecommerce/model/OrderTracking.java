package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 18/10/2016.
 */

public class OrderTracking extends Model implements Parcelable {

    private String title;
    private String subTitle;
    private Date date;
    private int imageResId;
    private boolean isLastState;

    public OrderTracking() {

    }

    public static final Creator<OrderTracking> CREATOR = new Creator<OrderTracking>() {
        @Override
        public OrderTracking createFromParcel(Parcel in) {
            return new OrderTracking(in);
        }

        @Override
        public OrderTracking[] newArray(int size) {
            return new OrderTracking[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    private OrderTracking(Parcel in) {
        title = in.readString();
        subTitle = in.readString();
        imageResId = in.readInt();
        isLastState = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeInt(imageResId);
        dest.writeByte((byte) (isLastState ? 1 : 0));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getDateStringFormat (){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(getDate());
        } catch (Exception e) { }
        return null;
    }

    public boolean isLastState() {
        return isLastState;
    }

    public void setLastState(boolean lastState) {
        isLastState = lastState;
    }
}

package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ChatContact extends BusinessPartner implements Parcelable {

    private Date maxChatMessageCreateTime;

    public ChatContact() {

    }

    public Date getMaxChatMessageCreateTime() {
        return maxChatMessageCreateTime;
    }

    public void setMaxChatMessageCreateTime(Date maxChatMessageCreateTime) {
        this.maxChatMessageCreateTime = maxChatMessageCreateTime;
    }

    public String getMaxChatMessageCreateTimeStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(maxChatMessageCreateTime);
        } catch (Exception e) { }
        return null;
    }

    private ChatContact(Parcel in) {
        super(in);
        try{
            Long date = in.readLong();
            setMaxChatMessageCreateTime(date > 0 ? new Date(date) : null);
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(maxChatMessageCreateTime != null ? maxChatMessageCreateTime.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ChatContact> CREATOR = new Creator<ChatContact>() {
        @Override
        public ChatContact createFromParcel(Parcel in) {
            return new ChatContact(in);
        }

        @Override
        public ChatContact[] newArray(int size) {
            return new ChatContact[size];
        }
    };
}

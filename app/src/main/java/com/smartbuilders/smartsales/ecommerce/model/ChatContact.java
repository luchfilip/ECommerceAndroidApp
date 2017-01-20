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

    private Date lastMessageCreateTimeInConversation;
    private String lastMessageInConversation;

    public ChatContact() {

    }

    public Date getLastMessageCreateTimeInConversation() {
        return lastMessageCreateTimeInConversation;
    }

    public void setLastMessageCreateTimeInConversation(Date lastMessageCreateTimeInConversation) {
        this.lastMessageCreateTimeInConversation = lastMessageCreateTimeInConversation;
    }

    public String getMaxChatMessageCreateTimeStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(lastMessageCreateTimeInConversation);
        } catch (Exception e) { }
        return null;
    }

    public String getLastMessageInConversation() {
        return lastMessageInConversation;
    }

    public void setLastMessageInConversation(String lastMessageInConversation) {
        this.lastMessageInConversation = lastMessageInConversation;
    }

    private ChatContact(Parcel in) {
        super(in);
        try{
            Long date = in.readLong();
            this.lastMessageCreateTimeInConversation = date > 0 ? new Date(date) : null;
        }catch(Exception ex){ ex.printStackTrace(); }
        this.lastMessageInConversation = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.lastMessageCreateTimeInConversation != null ? this.lastMessageCreateTimeInConversation.getTime() : -1);
        dest.writeString(this.lastMessageInConversation);
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

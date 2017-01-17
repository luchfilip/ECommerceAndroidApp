package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ChatMessage extends Model implements Parcelable{

    private String message;

    public ChatMessage() {

    }

    private ChatMessage(Parcel in) {
        message = in.readString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(getCreated());
        } catch (Exception e) { }
        return null;
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }
}

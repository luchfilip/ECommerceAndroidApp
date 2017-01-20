package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Locale;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ChatMessage extends Model implements Parcelable{

    public static final int STATUS_UNREADED = 0;
    public static final int STATUS_READED = 1;
    public static final int TYPE_REQUEST_PRODUCT_PRICE = 2;
    public static final int TYPE_SEND_PRODUCT_PRICE = 3;

    private String message;
    private int receiverChatContactId;
    private int senderChatContactId;
    private int productId;
    private int chatMessageType;
    private String imageFileName;
    private int status;

    public ChatMessage() {

    }

    private ChatMessage(Parcel in) {
        message = in.readString();
        receiverChatContactId = in.readInt();
        senderChatContactId = in.readInt();
        productId = in.readInt();
        chatMessageType = in.readInt();
        imageFileName = in.readString();
        status = in.readInt();
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getReceiverChatContactId() {
        return receiverChatContactId;
    }

    public void setReceiverChatContactId(int receiverChatContactId) {
        this.receiverChatContactId = receiverChatContactId;
    }

    public int getSenderChatContactId() {
        return senderChatContactId;
    }

    public void setSenderChatContactId(int senderChatContactId) {
        this.senderChatContactId = senderChatContactId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getChatMessageType() {
        return chatMessageType;
    }

    public void setChatMessageType(int chatMessageType) {
        this.chatMessageType = chatMessageType;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreatedStringFormat(){
        try {
            return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT,
                    new Locale("es","VE")).format(getCreated());
        } catch (Exception e) { }
        return null;
    }

    public String getCreatedTimeStringFormat(){
        try {
            return DateFormat.getTimeInstance(DateFormat.SHORT, new Locale("es","VE")).format(getCreated());
        } catch (Exception e) { }
        return null;
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
        dest.writeString(message);
        dest.writeInt(receiverChatContactId);
        dest.writeInt(senderChatContactId);
        dest.writeInt(productId);
        dest.writeInt(chatMessageType);
        dest.writeString(imageFileName);
        dest.writeInt(status);
    }
}

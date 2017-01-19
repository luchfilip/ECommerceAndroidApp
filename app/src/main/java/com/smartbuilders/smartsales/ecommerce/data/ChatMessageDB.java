package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ChatMessageDB {

    final private Context mContext;
    final private User mUser;

    public ChatMessageDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<ChatMessage> getMessagesFromContact(int chatContactId) {
        ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "SELECT CHAT_MESSAGE_ID, CREATE_TIME, MESSAGE, SENDER_USER_ID, RECEIVER_USER_ID, PRODUCT_ID, STATUS " +
                    " FROM CHAT_MESSAGE " +
                    " WHERE SENDER_USER_ID = ? AND SENDER_IS_ACTIVE = 'Y' " +
                    " UNION " +
                    " SELECT CHAT_MESSAGE_ID, CREATE_TIME, MESSAGE, SENDER_USER_ID, RECEIVER_USER_ID, PRODUCT_ID, STATUS " +
                    " FROM CHAT_MESSAGE " +
                    " WHERE RECEIVER_USER_ID = ? AND RECEIVER_IS_ACTIVE = 'Y' " +
                    " ORDER BY CHAT_MESSAGE_ID ASC",
                    new String[]{String.valueOf(chatContactId), String.valueOf(chatContactId)}, null);
            if(c!=null){
                while (c.moveToNext()) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(c.getInt(0));
                    try {
                        chatMessage.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(1)).getTime()));
                    } catch (ParseException ex) {
                        try {
                            chatMessage.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
                        } catch (ParseException e) {
                            //empty
                        }
                    } catch (Exception ex) {
                        //empty
                    }
                    chatMessage.setMessage(c.getString(2));
                    chatMessage.setSenderChatContactId(c.getInt(3));
                    chatMessage.setReceiverChatContactId(c.getInt(4));
                    chatMessage.setProductId(c.getInt(5));
                    chatMessage.setStatus(c.getInt(6));
                    chatMessages.add(chatMessage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return chatMessages;
    }

    public int getUnreadMessagesCount() {
        return 0;
    }

    public String addChatMessage(ChatMessage chatMessage) {
        try {
            chatMessage.setId(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "CHAT_MESSAGE"));
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "INSERT INTO CHAT_MESSAGE (CHAT_MESSAGE_ID, SENDER_USER_ID, RECEIVER_USER_ID, " +
                                    " MESSAGE, MESSAGE_TYPE, PRODUCT_ID, IMAGE_FILE_NAME, CREATE_TIME, STATUS, " +
                                    " APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            new String[]{String.valueOf(chatMessage.getId()),
                            String.valueOf(chatMessage.getSenderChatContactId()),
                            String.valueOf(chatMessage.getReceiverChatContactId()),
                            chatMessage.getMessage(), String.valueOf(chatMessage.getChatMessageType()),
                            String.valueOf(chatMessage.getProductId()), chatMessage.getImageFileName(),
                            DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(ChatMessage.STATUS_READED),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
            if(rowsAffected <= 0){
                return "Error 001 - No se insertó el mensaje en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deactiveConversationByContactId(int chatContactId) {
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, "UPDATE CHAT_MESSAGE SET RECEIVER_IS_ACTIVE=?, UPDATE_TIME=? WHERE RECEIVER_USER_ID=?",
                    new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(chatContactId)});
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, "UPDATE CHAT_MESSAGE SET SENDER_IS_ACTIVE=?, UPDATE_TIME=? WHERE SENDER_USER_ID=?",
                    new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(chatContactId)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deactiveMessage(int chatContactId, int chatMessageId) {
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, "UPDATE CHAT_MESSAGE SET RECEIVER_IS_ACTIVE=?, UPDATE_TIME=? WHERE CHAT_MESSAGE_ID=? AND RECEIVER_USER_ID=?",
                    new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(chatMessageId), String.valueOf(chatContactId)});

            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, "UPDATE CHAT_MESSAGE SET SENDER_IS_ACTIVE=?, UPDATE_TIME=? WHERE CHAT_MESSAGE_ID=? AND SENDER_USER_ID=?",
                    new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(chatMessageId), String.valueOf(chatContactId)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
}

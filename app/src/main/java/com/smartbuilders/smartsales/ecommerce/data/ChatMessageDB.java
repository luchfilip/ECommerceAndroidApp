package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;
import java.util.Date;

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

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage("Hola");
        chatMessage.setCreated(new Date());

        chatMessages.add(chatMessage);

        chatMessage = new ChatMessage();
        chatMessage.setMessage("Esto es una prueba!!");
        chatMessage.setCreated(new Date());

        chatMessages.add(chatMessage);

        return chatMessages;
    }
}

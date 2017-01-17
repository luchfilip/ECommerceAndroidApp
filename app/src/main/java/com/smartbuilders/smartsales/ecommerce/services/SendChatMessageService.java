package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;

import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;

import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;

/**
 * Created by AlbertoSarco on 17/1/2017.
 */
public class SendChatMessageService extends IntentService {

    public static final String KEY_CHAT_MESSAGE = "KEY_CHAT_MESSAGE";
    public static final String KEY_USER_ID = "KEY_USER_ID";
    public static final String ACTION_RESP = "ACTION_RESP";
    public static final String MESSAGE = "MESSAGE";

    public SendChatMessageService() {
        super(SendChatMessageService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SendChatMessageService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        ChatMessage chatMessage = workIntent.getParcelableExtra(KEY_CHAT_MESSAGE);

        User user = ApplicationUtilities.getUserByIdFromAccountManager(getApplicationContext(),
                workIntent.getStringExtra(KEY_USER_ID));

        String resultMsg;
        try {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("authToken", user.getAuthToken());
            parameters.put("userId", user.getServerUserId());
            parameters.put("senderChatContactId", chatMessage.getSenderChatContactId());
            parameters.put("receiverChatContactId", chatMessage.getReceiverChatContactId());
            parameters.put("message", chatMessage.getMessage());
            parameters.put("chatMessageType", chatMessage.getChatMessageType());
            parameters.put("productId", chatMessage.getProductId());
            ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                    user.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageCommunication?wsdl",
                    "sendChatMessage",
                    "urn:sendChatMessage",
                    parameters);
            Object response =  a.getWSResponse();
            if(response instanceof SoapPrimitive){
                resultMsg = response.toString();
            }else if (response != null){
                throw new ClassCastException("response classCastException.");
            }else{
                throw new NullPointerException("response is null.");
            }
        } catch (Exception e) {
            resultMsg = e.getMessage();
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(MESSAGE, resultMsg);
        sendBroadcast(broadcastIntent);
    }
}

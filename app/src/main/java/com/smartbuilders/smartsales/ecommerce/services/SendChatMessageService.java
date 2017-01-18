package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;

import com.smartbuilders.smartsales.ecommerce.data.ChatMessageDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatMessage;
import com.smartbuilders.synchronizer.ids.datamanager.TablesDataSendToAndReceiveFromServer;
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
        User user = ApplicationUtilities.getUserByIdFromAccountManager(getApplicationContext(),
                workIntent.getStringExtra(KEY_USER_ID));

        if (workIntent.getParcelableExtra(KEY_CHAT_MESSAGE)!=null) {
            //guardar el nuevo mensaje en base de dato
            (new ChatMessageDB(getApplicationContext(), user))
                    .addChatMessage((ChatMessage) workIntent.getParcelableExtra(KEY_CHAT_MESSAGE));
        }

        try {
            //enviar todos los mensajes en cola y recibir los que aun no se hayan recibido
            (new TablesDataSendToAndReceiveFromServer(user, getApplicationContext(), "{\"1\":\"CHAT_MESSAGE\"}",
                    TablesDataSendToAndReceiveFromServer.TRANSMISSION_CLIENT_TO_SERVER_AND_SERVER_TO_CLIENT)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (workIntent.getParcelableExtra(KEY_CHAT_MESSAGE)!=null) {
                //notificar el nuevo mensaje al usuario que recibe
                LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("authToken", user.getAuthToken());
                parameters.put("userId", user.getServerUserId());
                parameters.put("senderUserId", ((ChatMessage) workIntent.getParcelableExtra(KEY_CHAT_MESSAGE)).getSenderChatContactId());
                parameters.put("receiverUserId", ((ChatMessage) workIntent.getParcelableExtra(KEY_CHAT_MESSAGE)).getReceiverChatContactId());

                ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                        user.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageCommunication?wsdl",
                        "sendMessage",
                        "urn:sendMessage",
                        parameters);
                Object response = a.getWSResponse();
                if (response instanceof SoapPrimitive) {
                    //resultMsg = response.toString();
                } else if (response != null) {
                    throw new ClassCastException("response classCastException.");
                } else {
                    throw new NullPointerException("response is null.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

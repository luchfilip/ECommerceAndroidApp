package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.RequestUserPasswordFragment;

import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;

/**
 * Created by stein on 7/6/2016.
 */
public class RequestUserPasswordService extends IntentService {

    private static final String TAG = RequestUserPasswordService.class.getSimpleName();

    public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";


    public RequestUserPasswordService() {
        super(RequestUserPasswordService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RequestUserPasswordService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String serverAddress = workIntent.getStringExtra(SERVER_ADDRESS);
        String userName = workIntent.getStringExtra(USER_NAME);
        String userEmail = workIntent.getStringExtra(USER_EMAIL);
        String resultMsg;
        try {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("userGroup", getString(R.string.ids_user_group_name));
            parameters.put("userName", userName);
            parameters.put("userEmail", userEmail);
            ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                                                        serverAddress,
                                                        "/IntelligentDataSynchronizer/services/ManageUser?wsdl",
                                                        "requestUserPassword",
                                                        "urn:requestUserPassword",
                                                        parameters);
            Object response =  a.getWSResponse();
            if(response instanceof SoapPrimitive){
                Log.d(TAG, "response: "+response.toString());
                //TODO: enviar broadcast a la aplicacion
                resultMsg = response.toString();
            }else if (response != null){
                throw new ClassCastException("response classCastException.");
            }else {
                throw new NullPointerException("response is null.");
            }
        } catch (Exception e) {
            resultMsg = e.getMessage();
            e.printStackTrace();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RequestUserPasswordFragment.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RequestUserPasswordFragment.MESSAGE, resultMsg);
        sendBroadcast(broadcastIntent);
    }
}
package com.smartbuilders.smartsales.ecommerceandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;

/**
 * Created by stein on 22/7/2016.
 */
public class SyncDataWithServer extends IntentService {

    private static final String TAG = SyncDataWithServer.class.getSimpleName();

    public static final String KEY_USER_ID = "SyncDataWithServer.KEY_USER_ID";
    public static final String KEY_SQL_SELECTION = "SyncDataWithServer.KEY_SQL_SELECTION";
    public static final String KEY_SQL_SELECTION_ARGS = "SyncDataWithServer.KEY_SQL_SELECTION_ARGS";

    public SyncDataWithServer() {
        super(SyncDataWithServer.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncDataWithServer(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {

        try {
            User user = ApplicationUtilities
                .getUserByIdFromAccountManager(getApplicationContext(), workIntent.getStringExtra(KEY_USER_ID));
            if (user!=null) {
                JSONObject jsonObject = new JSONObject();
                String[] selectionArgs = workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS);
                for (int i=0; i<selectionArgs.length; i++) {
                    if (selectionArgs[i]!=null){
                        jsonObject.put(String.valueOf(i), selectionArgs[i]);
                    }
                }

                LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("authToken", user.getAuthToken());
                parameters.put("userGroupName", user.getUserGroup());
                parameters.put("userId", user.getServerUserId());
                parameters.put("selection", workIntent.getStringExtra(KEY_SQL_SELECTION));
                parameters.put("selectionArgs",  jsonObject.toString());
                parameters.put("columnCount",  selectionArgs.length);
                ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                        user.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                        "syncDataFromClient",
                        "urn:syncDataFromClient",
                        parameters,
                        2000);
                Object response =  a.getWSResponse();
                if(response instanceof SoapPrimitive){
                    Log.w(TAG, "response: " + response.toString());
                }else if (response != null){
                    throw new ClassCastException("response classCastException.");
                }else{
                    throw new NullPointerException("response is null.");
                }
            } else {
                throw new Exception("user is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

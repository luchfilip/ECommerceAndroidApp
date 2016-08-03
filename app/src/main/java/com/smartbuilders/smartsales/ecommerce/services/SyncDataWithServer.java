package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.utils.ApplicationUtilities;
import com.smartbuilders.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerce.data.FailedSyncDataWithServerDB;
import com.smartbuilders.smartsales.ecommerce.model.FailedSyncDataWithServer;

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
    public static final String KEY_RETRY_FAILED_SYNC_DATA_WITH_SERVER = "SyncDataWithServer.KEY_RETRY_FAILED_SYNC_DATA_WITH_SERVER";


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
            if (user != null) {
                if(workIntent.getBooleanExtra(KEY_RETRY_FAILED_SYNC_DATA_WITH_SERVER, false)){
                    FailedSyncDataWithServerDB failedSyncDataWithServerDB = new FailedSyncDataWithServerDB(getApplicationContext(), user);
                    for(FailedSyncDataWithServer failedSyncDataWithServer : failedSyncDataWithServerDB.getAllFailedSyncDataWithServer()){
                        sendDataToServer(user, failedSyncDataWithServer.getSelection(),
                                failedSyncDataWithServer.getSelectionArgs(), failedSyncDataWithServer.getColumnCount());
                        failedSyncDataWithServerDB.deleteFailedSyncDataWithServerById(failedSyncDataWithServer.getId());
                    }
                }else {
                    JSONObject jsonObject = new JSONObject();
                    String[] selectionArgs = workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS);
                    for (int i = 0; i < selectionArgs.length; i++) {
                        if (selectionArgs[i] != null) {
                            jsonObject.put(String.valueOf(i), selectionArgs[i]);
                        }
                    }
                    try {
                        sendDataToServer(user, workIntent.getStringExtra(KEY_SQL_SELECTION), jsonObject.toString(), selectionArgs.length);
                    } catch (Exception e) {
                        Log.e(TAG, String.valueOf(e.getClass().getName()) + ": " + String.valueOf(e.getMessage()));
                        (new FailedSyncDataWithServerDB(getApplicationContext(), user))
                                .insertFailedSyncDataWithServerRow(workIntent.getStringExtra(KEY_SQL_SELECTION),
                                        jsonObject.toString(), selectionArgs.length);
                    }
                }
            } else {
                throw new Exception("user is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendDataToServer(User user, String selection, String selectionArgs, int columnCount) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", user.getAuthToken());
        parameters.put("userGroupName", user.getUserGroup());
        parameters.put("userId", user.getServerUserId());
        parameters.put("selection", selection);
        parameters.put("selectionArgs", selectionArgs);
        parameters.put("columnCount", columnCount);
        ConsumeWebService a = new ConsumeWebService(getApplicationContext(),
                user.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                "syncDataFromClient",
                "urn:syncDataFromClient",
                parameters,
                2000);
        Object response = a.getWSResponse();
        if (response instanceof SoapPrimitive) {
            Log.w(TAG, "response: " + response.toString());
        } else if (response != null) {
            throw new ClassCastException("response classCastException.");
        } else {
            throw new NullPointerException("response is null.");
        }
    }
}

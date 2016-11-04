package com.smartbuilders.smartsales.ecommerce.services;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.smartbuilders.smartsales.ecommerce.data.SyncDataRealTimeWithServerDB;
import com.smartbuilders.smartsales.ecommerce.model.SyncDataRealTimeWithServer;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;

/**
 * Created by stein on 22/7/2016.
 */
public class SyncDataRealTimeWithServerService extends IntentService {

    private static final String TAG = SyncDataRealTimeWithServerService.class.getSimpleName();

    public static final String KEY_USER_ID = "SyncDataRealTimeWithServerService.KEY_USER_ID";
    public static final String KEY_SQL_SELECTION = "SyncDataRealTimeWithServerService.KEY_SQL_SELECTION";
    public static final String KEY_SQL_SELECTION_ARGS = "SyncDataRealTimeWithServerService.KEY_SQL_SELECTION_ARGS";


    public SyncDataRealTimeWithServerService() {
        super(SyncDataRealTimeWithServerService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncDataRealTimeWithServerService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            User user = ApplicationUtilities
                    .getUserByIdFromAccountManager(getApplicationContext(), workIntent.getStringExtra(KEY_USER_ID));
            if (user != null) {
                SyncDataRealTimeWithServerDB syncDataRealTimeWithServerDB = new SyncDataRealTimeWithServerDB(getApplicationContext(), user);

                /******************************************************************************/
                //si hay un nuevo registro por sincronizar entonces se agrega a la cola de registros por sincronizar
                if (workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS)!=null
                        && !TextUtils.isEmpty(workIntent.getStringExtra(KEY_SQL_SELECTION))) {
                    //se agrega el nuevo registro
                    JSONObject jsonObject = new JSONObject();
                    String[] selectionArgs = workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS);
                    for (int i = 0; i < selectionArgs.length; i++) {
                        if (selectionArgs[i] != null) {
                            jsonObject.put(String.valueOf(i), selectionArgs[i]);
                        }
                    }

                    syncDataRealTimeWithServerDB.insertDataToSyncWithServer(workIntent.getStringExtra(KEY_SQL_SELECTION),
                                    jsonObject.toString(), selectionArgs.length);
                }
                /*****************************************************************************/

                //se sincronizan los registros encolados para sincronizar en tiempo real
                JSONArray totalData = new JSONArray();
                JSONObject data = new JSONObject();
                for(SyncDataRealTimeWithServer syncDataRealTimeWithServer : syncDataRealTimeWithServerDB.getAllDataToSyncWithServer()){
                    if (!TextUtils.isEmpty(syncDataRealTimeWithServer.getSelection())
                            && !TextUtils.isEmpty(syncDataRealTimeWithServer.getSelectionArgs())
                            && syncDataRealTimeWithServer.getColumnCount()>0) {
                        data.put("0", syncDataRealTimeWithServer.getSelection());
                        data.put("1", syncDataRealTimeWithServer.getSelectionArgs());
                        data.put("2", syncDataRealTimeWithServer.getColumnCount());
                        totalData.put(data);
                    }
                }
                syncDataRealTimeWithServerDB.deleteDataToSyncWithServer(sendDataToServer(user, totalData));
            } else {
                throw new Exception("user is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendDataToServer(User user, JSONArray data) throws Exception {
        Log.w(TAG, "sendDataToServer(User user, "+data.toString()+")");
        Log.w(TAG, "data.length(): "+data.length());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", user.getAuthToken());
        parameters.put("userGroupName", user.getUserGroup());
        parameters.put("userId", user.getServerUserId());
        parameters.put("data", data.toString());
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
            return response.toString();
        } else if (response != null) {
            throw new ClassCastException("response classCastException.");
        } else {
            throw new NullPointerException("response is null.");
        }
    }
}

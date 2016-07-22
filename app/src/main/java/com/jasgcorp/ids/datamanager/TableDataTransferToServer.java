package com.jasgcorp.ids.datamanager;

import android.content.Context;
import android.util.Log;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.jasgcorp.ids.utils.DataBaseRemoteManagement;
import com.smartbuilders.smartsales.ecommerceandroidapp.session.Parameter;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 */
public class TableDataTransferToServer extends Thread {

	private static final String TAG = TableDataTransferToServer.class.getSimpleName();

	private Context context;
	private User mUser;

	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private int mConnectionTimeOut;
	
	public TableDataTransferToServer(User user, Context context) throws Exception{
		this.context = context;
		this.mUser = user;
        this.mConnectionTimeOut = Parameter.getConnectionTimeOutValue(context, mUser);
	}

	/**
	 * detiene el hilo de sincronizacion
	 */
	public void stopSynchronization(){
		Log.d(TAG, "stopSynchronization()");
		sync = false;
	}
	
	public String getExceptionMessage(){
		return exceptionMessage;
	}
	
	public String getExceptionClass(){
		return exceptionClass;
	}
	
	@Override
	public void run() {
		Log.d(TAG, "run()");
		try {
            syncPercentage = 0;
			if (sync) {
                sendUserDataToServer(getUserTablesToSync());
			}
            syncPercentage = 100;
		} catch (Exception e) {
			e.printStackTrace();
            reportSyncError(e.getMessage(), e.getClass().getName());
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
	}

    private JSONObject getUserTablesToSync() throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        ConsumeWebService a = new ConsumeWebService(context,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                "getTablesAndSQLToReceiveFromClient",
                "urn:getTablesAndSQLToReceiveFromClient",
                parameters,
                mConnectionTimeOut);
        return new JSONObject(a.getWSResponse().toString());
    }

    private void sendUserDataToServer(JSONObject userTablesToSync) throws Exception {
        Iterator<?> keysTemp = userTablesToSync.keys();
        while(keysTemp.hasNext()){
            try {
                String key = (String) keysTemp.next();
                Log.d(TAG, "table: "+key+", sql: "+String.valueOf(userTablesToSync.get(key)));
                Object result = DataBaseRemoteManagement
                        .getJsonBase64CompressedQueryResult(context, mUser, 9000, ((String) userTablesToSync.get(key)));
                if (result instanceof String) {
                    sendDataToServer((String) result, null);
                } else if (result instanceof Exception) {
                    sendDataToServer(null, String.valueOf(((Exception) result).getMessage()));
                    throw (Exception) result;
                } else {
                    sendDataToServer(null, "result is null for sql: "+String.valueOf(userTablesToSync.get(key)));
                    throw new Exception("result is null for sql: "+String.valueOf(userTablesToSync.get(key)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendDataToServer (String data, String errorMessage) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("data", data);
        parameters.put("errorMessage", errorMessage);
        ConsumeWebService a = new ConsumeWebService(context,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                "receiveDataFromClient",
                "urn:receiveDataFromClient",
                parameters,
                mConnectionTimeOut);
        a.getWSResponse();
    }

	public float getSyncPercentage() {
		return syncPercentage;
	}

    private void reportSyncError(String errorMessage, String exceptionClass) {
        try{
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("authToken", mUser.getAuthToken());
            parameters.put("userGroupName", mUser.getUserGroup());
            parameters.put("userId", mUser.getServerUserId());
            parameters.put("errorMessage", errorMessage);
            parameters.put("exceptionClass", exceptionClass);
            ConsumeWebService a = new ConsumeWebService(context,
                    mUser.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                    "reportSyncError",
                    "urn:reportSyncError",
                    parameters,
                    mConnectionTimeOut);
            a.getWSResponse();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

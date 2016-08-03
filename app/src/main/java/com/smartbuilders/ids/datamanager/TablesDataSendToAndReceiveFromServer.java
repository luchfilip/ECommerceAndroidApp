package com.smartbuilders.ids.datamanager;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.providers.DataBaseContentProvider;
import com.smartbuilders.ids.utils.ConsumeWebService;
import com.smartbuilders.ids.utils.DataBaseUtilities;
import com.smartbuilders.smartsales.ecommerce.data.FailedSyncDataWithServerDB;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;

/**
 *
 */
public class TablesDataSendToAndReceiveFromServer extends Thread {
	
	private static final String TAG = TablesDataSendToAndReceiveFromServer.class.getSimpleName();

	private Context context;
	private boolean sync;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private User mUser;
    private int mConnectionTimeOut;
    private String mTablesToSyncJSONObject;

    public TablesDataSendToAndReceiveFromServer(User user, Context context, String tablesToSyncJSONObject) throws Exception{
        this(user, context);
        this.mTablesToSyncJSONObject = tablesToSyncJSONObject;
    }

	public TablesDataSendToAndReceiveFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.mUser = user;
        this.mConnectionTimeOut = Parameter.getConnectionTimeOutValue(context, mUser);
        this.sync = true;
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
            long initTime = System.currentTimeMillis();
            if(mTablesToSyncJSONObject!=null) {
                if(sync){
                    getGlobalDataFromWS(context, mUser, new JSONObject(mTablesToSyncJSONObject));
                }
            }else{
                if (sync) {
                    sendUserDataToServer(getUserTablesAndSQLToSync());
                    (new FailedSyncDataWithServerDB(context, mUser)).cleanFailedSyncDataWithServer();
                }
                if(sync){
                    getUserDataFromWS(context, mUser, getUserTablesToSync());
                }
                if(sync){
                    getGlobalDataFromWS(context, mUser, getGlobalTablesToSync());
                }
            }
            syncPercentage = 100;
            Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
            reportSyncError(String.valueOf(e.getMessage()), String.valueOf(e.getClass().getName()));
			exceptionMessage = String.valueOf(e.getMessage());
			exceptionClass = String.valueOf(e.getClass().getName());
		}
		sync = false;
	}

    private JSONObject getUserTablesAndSQLToSync() throws Exception {
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
            //try {
                if(sync){
                    String key = (String) keysTemp.next();
                    Object result = DataBaseUtilities
                            .getJsonBase64CompressedQueryResult(context, mUser, ((String) userTablesToSync.get(key)));
                    if(sync){
                        if (result instanceof String) {
                            sendDataToServer(key, (String) result, null);
                        } else if (result instanceof Exception) {
                            sendDataToServer(key, null, String.valueOf(((Exception) result).getMessage()));
                            throw (Exception) result;
                        } else {
                            sendDataToServer(key, null, "result is null for sql: "+String.valueOf(userTablesToSync.get(key)));
                            throw new Exception("result is null for sql: "+String.valueOf(userTablesToSync.get(key)));
                        }
                    }else{
                        sendDataToServer(key, null, "Synchronization was stopped by user.");
                    }
                }else{
                    //se detiene el bucle
                    break;
                }
            //} catch (JSONException e) {
            //    e.printStackTrace();
            //}
        }
    }

    private void sendDataToServer (String tableName, String data, String errorMessage) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("tableName", tableName);
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

    private List<SoapPrimitive> getGlobalTablesToSync() throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        ConsumeWebService a = new ConsumeWebService(context,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                "getGlobalTablesToSync",
                "urn:getGlobalTablesToSync",
                parameters,
                mConnectionTimeOut);
        return (List<SoapPrimitive>) a.getWSResponse();
    }

    public void getGlobalDataFromWS(Context context, User user, JSONObject tablesToSync) throws Exception {
        if (tablesToSync!=null && tablesToSync.keys()!=null) {
            Iterator keys = tablesToSync.keys();
            while(keys.hasNext()){
                if (sync) {
                    execRemoteQueryAndInsert(context, user, (String) tablesToSync.get(keys.next().toString()));
                    syncPercentage++;
                } else {
                    break;
                }
            }
        }
    }

	public void getGlobalDataFromWS(Context context, User user, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if (sync) {
                    execRemoteQueryAndInsert(context, user, tableToSync.toString());
                    syncPercentage++;
                } else {
                    break;
                }
            }
        }
	}

    private List<SoapPrimitive> getUserTablesToSync() throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        ConsumeWebService a = new ConsumeWebService(context,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                "getUserTablesToSync",
                "urn:getUserTablesToSync",
                parameters,
                mConnectionTimeOut);
        return (List<SoapPrimitive>) a.getWSResponse();
    }

	public void getUserDataFromWS(Context context, User user, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if(sync){
                    execRemoteQueryAndInsert(context, user, tableToSync.toString());
                    syncPercentage++;
                }
            }
        }
	}

	/**
	 *
	 * @param context
	 * @param user
	 * @param tableName
	 */
	private void execRemoteQueryAndInsert(Context context, User user, String tableName) throws Exception {
        Cursor c = null;
        LinkedHashMap<String, Object> parameters = null;
        try{
            if (user!=null) {
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null, "select COUNT(SEQUENCE_ID), MAX(SEQUENCE_ID) from " + tableName, null, null);
            } else {
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "select COUNT(SEQUENCE_ID), MAX(SEQUENCE_ID) from " + tableName, null, null);
            }
            //Se verifica la cantidad de resgistros de la tabla para luego usarlo como dato del
            //lado del servidor
            if(c!=null && c.moveToNext()) {
                parameters = new LinkedHashMap<>();
                parameters.put("authToken", mUser.getAuthToken());
                parameters.put("userGroupName", mUser.getUserGroup());
                parameters.put("userId", mUser.getServerUserId());
                parameters.put("tableName", tableName);
                parameters.put("tableCount", c.getInt(0));
                parameters.put("maxSeqId", c.getString(1)==null ? -1 : c.getInt(1));
                Log.d(TAG, "tableName: "+tableName+", tableCount: "+c.getInt(0)+", maxSeqId: "+String.valueOf(c.getString(1)==null ? -1 : c.getInt(1)));
            }
        } finally {
            if (c!=null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(parameters!=null && parameters.size()==6) {
            ConsumeWebService a = new ConsumeWebService(context,
                    mUser.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                    "sendDataToClient",
                    "urn:sendDataToClient",
                    parameters,
                    mConnectionTimeOut);
            Object result = a.getWSResponse();
            if (result instanceof List<?>) {
                try {
                    try {
                        DataBaseUtilities.insertDataFromWSResultData(((List<SoapPrimitive>) result).get(0) != null ? ((List<SoapPrimitive>) result).get(0).toString() : null,
                                ((List<SoapPrimitive>) result).get(1) != null ? ((List<SoapPrimitive>) result).get(1).toString() : null,
                                tableName, context, user);
                    } catch (IOException e) {
                        if (((List<SoapPrimitive>) result).get(0) != null
                                && ((List<SoapPrimitive>) result).get(0).toString().equals("NOTHING_TO_SYNC")) {
                            Log.d(TAG, "table: " + tableName + ", nothing to sync.");
                        } else {
                            throw e;
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                        reportSyncError(String.valueOf(e.getMessage()), e.getClass().getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (result instanceof Exception) {
                throw (Exception) result;
            } else if (result != null) {
                throw new Exception("Error while executing execRemoteQueryAndInsert(" + mUser.getServerAddress() + ", " + tableName + "), ClassCastException.");
            } else {
                throw new Exception("Error while executing execRemoteQueryAndInsert(" + mUser.getServerAddress() + ", " + tableName + "), result is null.");
            }
        }
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

	public float getSyncPercentage() {
		return syncPercentage;
	}

}

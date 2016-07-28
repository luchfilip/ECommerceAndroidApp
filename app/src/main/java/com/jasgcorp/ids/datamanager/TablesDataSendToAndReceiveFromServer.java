package com.jasgcorp.ids.datamanager;

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

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.jasgcorp.ids.utils.DataBaseUtilities;
import com.smartbuilders.smartsales.ecommerce.data.FailedSyncDataWithServerDB;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 *
 */
public class TablesDataSendToAndReceiveFromServer extends Thread {
	
	private static final String TAG = TablesDataSendToAndReceiveFromServer.class.getSimpleName();

    public static final String SYNC_SESSION_ID_SHARED_PREFS_KEY = "SYNC_SESSION_ID_SHARED_PREFS_KEY";

	private Context context;
	private boolean sync;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private User mUser;
    private int mConnectionTimeOut;
	
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
            Utils.incrementSyncSessionId(context);

            long initTime = System.currentTimeMillis();
            if (sync) {
                sendUserDataToServer(getUserTablesAndSQLToSync());
                (new FailedSyncDataWithServerDB(context, mUser)).cleanFailedSyncDataWithServer();
            }
			if(sync){
				getGlobalDataFromWS(context, Utils.getSyncSessionId(context), getGlobalTablesToSync());
			}
			if(sync){
				getUserDataFromWS(context, Utils.getSyncSessionId(context), mUser, getUserTablesToSync());
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

	public void getGlobalDataFromWS(Context context, int currentSyncSessionID, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if(sync) {
                    execRemoteQueryAndInsert(context, null, currentSyncSessionID, tableToSync.toString());
                    syncPercentage++;
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

	public void getUserDataFromWS(Context context, int currentSyncSessionID, User user, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if(sync){
                    execRemoteQueryAndInsert(context, user, currentSyncSessionID, tableToSync.toString());
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
	private void execRemoteQueryAndInsert(Context context, User user, int currentSyncSessionID, String tableName) throws Exception {
        Cursor c = null;
        try{
            if (user!=null) {
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null, "select count(*) from " + tableName, null, null);
            } else {
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "select count(*) from " + tableName, null, null);
            }
            //Se verifica la cantidad de resgistros de la tabla para luego usarlo como dato del
            //lado del servidor
            if(c!=null && c.moveToNext()) {
                LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("authToken", mUser.getAuthToken());
                parameters.put("userGroupName", mUser.getUserGroup());
                parameters.put("userId", mUser.getServerUserId());
                parameters.put("userBusinessPartnerId", mUser.getBusinessPartnerId());
                parameters.put("tableName", tableName);
                parameters.put("tableCount", c.getInt(0));
                parameters.put("maxSeqId", 0);
                ConsumeWebService a = new ConsumeWebService(context,
                        mUser.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                        "sendDataToClient",
                        "urn:sendDataToClient",
                        parameters,
                        mConnectionTimeOut);
                Object result = a.getWSResponse();
                if (result instanceof SoapPrimitive) {
                    try {
                        DataBaseUtilities.insertDataFromWSResultData(result.toString(), tableName, currentSyncSessionID, context, user);
                    } catch (IOException e) {
                        if (!result.toString().equals("NOTHING_TO_SYNC")){
                            throw e;
                        }
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                        reportSyncError(String.valueOf(e.getMessage()), e.getClass().getName());
                    }
                } else if(result instanceof Exception) {
                    throw (Exception) result;
                } else if (result!=null) {
                    throw new Exception("Error while executing execRemoteQueryAndInsert("+mUser.getServerAddress()+", "+tableName+"), ClassCastException.");
                } else {
                    throw new Exception("Error while executing execRemoteQueryAndInsert("+mUser.getServerAddress()+", "+tableName+"), result is null.");
                }
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

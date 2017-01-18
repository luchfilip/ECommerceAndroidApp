package com.smartbuilders.synchronizer.ids.datamanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.smartbuilders.smartsales.ecommerce.data.SyncDataRealTimeWithServerDB;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.database.DatabaseHelper;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;
import com.smartbuilders.synchronizer.ids.utils.DataBaseUtilities;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;

import net.iharder.Base64;

/**
 *
 */
public class TablesDataSendToAndReceiveFromServer extends Thread {

    public static final int TRANSMISSION_SERVER_TO_CLIENT = 1;
    public static final int TRANSMISSION_CLIENT_TO_SERVER = 2;
    public static final int TRANSMISSION_SERVER_TO_CLIENT_AND_CLIENT_TO_SERVER = 3;
    public static final int TRANSMISSION_CLIENT_TO_SERVER_AND_SERVER_TO_CLIENT = 4;

	private static final String TAG = TablesDataSendToAndReceiveFromServer.class.getSimpleName();

	private Context mContext;
	private boolean sync;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
    private float numberOfTablesToSync;
    private float numberOfTableSynced;
	private User mUser;
    private int mConnectionTimeOut;
    private String mTablesToSyncJSONObject;
    private boolean mIsInitialLoad;
    private int mTransmissionWay;
    private int mCurrentAppVersionCode;

    public TablesDataSendToAndReceiveFromServer(User user, Context context, String tablesToSyncJSONObject,
                                                int transmissionWay) throws Exception{
        this(user, context, false);
        this.mTablesToSyncJSONObject = tablesToSyncJSONObject;
        this.mTransmissionWay = transmissionWay;
    }

	public TablesDataSendToAndReceiveFromServer(User user, Context context, boolean isInitialLoad) throws Exception{
		this.mContext = context;
		this.mUser = user;
        this.mConnectionTimeOut = Parameter.getConnectionTimeOutValue(context, mUser);
        this.sync = true;
        this.mIsInitialLoad = isInitialLoad;
	}
	/**
	 * detiene el hilo de sincronizacion
	 */
	public void stopSynchronization(){
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
            syncPercentage = 0f;
            mCurrentAppVersionCode = Utils.getAppVersionCode(mContext);
            long initTime = System.currentTimeMillis();
            List<SoapPrimitive> tablesToSync = new ArrayList<>();
            if (mTablesToSyncJSONObject!=null) {
                if (sync) {
                    Iterator keys;
                    switch (mTransmissionWay) {
                        case TRANSMISSION_SERVER_TO_CLIENT:
                            keys = (new JSONObject(mTablesToSyncJSONObject)).keys();
                            while(keys.hasNext()){
                                tablesToSync.add(new SoapPrimitive(null, null,
                                        (String) new JSONObject(mTablesToSyncJSONObject).get(keys.next().toString())));
                            }
                            numberOfTablesToSync = tablesToSync.size();
                            getDataFromWS(mContext, mUser, tablesToSync);
                            break;
                        case TRANSMISSION_CLIENT_TO_SERVER:
                            numberOfTablesToSync = (new JSONObject(mTablesToSyncJSONObject)).length();
                            sendUserDataToServer(getSQLToSync(mTablesToSyncJSONObject));
                            break;
                        case TRANSMISSION_CLIENT_TO_SERVER_AND_SERVER_TO_CLIENT:
                            numberOfTablesToSync = (new JSONObject(mTablesToSyncJSONObject)).length();

                            /*******************/
                            sendUserDataToServer(getSQLToSync(mTablesToSyncJSONObject));
                            /*******************/

                            /*******************/
                            keys = (new JSONObject(mTablesToSyncJSONObject)).keys();
                            while(keys.hasNext()){
                                tablesToSync.add(new SoapPrimitive(null, null,
                                        (String) new JSONObject(mTablesToSyncJSONObject).get(keys.next().toString())));
                            }
                            getDataFromWS(mContext, mUser, tablesToSync);
                            /*******************/
                            break;
                        case TRANSMISSION_SERVER_TO_CLIENT_AND_CLIENT_TO_SERVER:
                            numberOfTablesToSync = (new JSONObject(mTablesToSyncJSONObject)).length();

                            /*******************/
                            keys = (new JSONObject(mTablesToSyncJSONObject)).keys();
                            while(keys.hasNext()){
                                tablesToSync.add(new SoapPrimitive(null, null,
                                        (String) new JSONObject(mTablesToSyncJSONObject).get(keys.next().toString())));
                            }
                            getDataFromWS(mContext, mUser, tablesToSync);
                            /*******************/

                            /*******************/
                            sendUserDataToServer(getSQLToSync(mTablesToSyncJSONObject));
                            /*******************/
                            break;
                    }
                }

            } else {
                JSONObject userTablesAndSqlToSync = null;
                if (sync) {
                    userTablesAndSqlToSync = getUserTablesAndSQLToSync();
                }
                if (sync) {
                    tablesToSync = new ArrayList<>();
                    if (mIsInitialLoad) {
                        tablesToSync.addAll(getTablesToSyncInitialLoad());
                    } else {
                        tablesToSync.addAll(getTablesToSync());
                    }
                }
                if (sync) {
                    numberOfTablesToSync = (userTablesAndSqlToSync!=null ? userTablesAndSqlToSync.length() : 0) + tablesToSync.size();
                    sendUserDataToServer(userTablesAndSqlToSync);
                }
                if(sync){
                    getDataFromWS(mContext, mUser, tablesToSync);
                }
            }
            syncPercentage = 100f;
            Log.d(TAG, "Total Synchronization Time: "+(System.currentTimeMillis() - initTime)+"ms");
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
        parameters.put("syncSessionId", mUser.getServerSyncSessionId());
        parameters.put("appVersionCode", mCurrentAppVersionCode);
        ConsumeWebService a = new ConsumeWebService(mContext,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "getTablesAndSQLToReceiveFromClient",
                "urn:getTablesAndSQLToReceiveFromClient",
                parameters,
                mConnectionTimeOut);
        return new JSONObject(a.getWSResponse().toString());
    }

    private JSONObject getSQLToSync(String tablesNamesJSONObject) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("syncSessionId", mUser.getServerSyncSessionId());
        parameters.put("appVersionCode", mCurrentAppVersionCode);
        parameters.put("tablesNamesJSONObject", tablesNamesJSONObject);
        ConsumeWebService a = new ConsumeWebService(mContext,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "getSQLToReceiveFromClient",
                "urn:getSQLToReceiveFromClient",
                parameters,
                mConnectionTimeOut);
        return new JSONObject(a.getWSResponse().toString());
    }

    private void sendUserDataToServer(JSONObject userTablesToSync) throws Exception {
        Iterator<?> keysTemp = userTablesToSync.keys();
        while(keysTemp.hasNext()){
            if(sync){
                String key = (String) keysTemp.next();
                if (userTablesToSync.get(key)!=null) {
                    Object result = DataBaseUtilities
                            .getJsonBase64CompressedQueryResult(mContext, mUser, (String) userTablesToSync.get(key));
                    if(sync){
                        numberOfTableSynced++;
                        syncPercentage = ((numberOfTableSynced * 100) / numberOfTablesToSync);
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
                }
            }else{
                //se detiene el bucle
                break;
            }
        }
        //si se termino de sincronizar todos los datos que van al server entonces se limpia la cola
        //de los datos que estaban pendientes por sincronizar en tiempo real
        (new SyncDataRealTimeWithServerDB(mContext, mUser)).deleteDataToSyncWithServer();
    }

    private void sendDataToServer (String tableName, String data, String errorMessage) throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("syncSessionId", mUser.getServerSyncSessionId());
        parameters.put("appVersionCode", mCurrentAppVersionCode);
        parameters.put("tableName", tableName);
        parameters.put("data", data);
        parameters.put("errorMessage", errorMessage);
        ConsumeWebService a = new ConsumeWebService(mContext,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "receiveDataFromClient",
                "urn:receiveDataFromClient",
                parameters,
                mConnectionTimeOut);
        a.getWSResponse();
    }

    private List<SoapPrimitive> getTablesToSync() throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("syncSessionId", mUser.getServerSyncSessionId());
        parameters.put("appVersionCode", mCurrentAppVersionCode);
        ConsumeWebService a = new ConsumeWebService(mContext,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "getTablesToSync",
                "urn:getTablesToSync",
                parameters,
                mConnectionTimeOut);
        return (List<SoapPrimitive>) a.getWSResponse();
    }

    private List<SoapPrimitive> getTablesToSyncInitialLoad() throws Exception {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("authToken", mUser.getAuthToken());
        parameters.put("userGroupName", mUser.getUserGroup());
        parameters.put("userId", mUser.getServerUserId());
        parameters.put("syncSessionId", mUser.getServerSyncSessionId());
        parameters.put("appVersionCode", mCurrentAppVersionCode);
        ConsumeWebService a = new ConsumeWebService(mContext,
                mUser.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                "getTablesToSyncInitialLoad",
                "urn:getTablesToSyncInitialLoad",
                parameters,
                mConnectionTimeOut);
        return (List<SoapPrimitive>) a.getWSResponse();
    }

    private void getDataFromWS(Context context, User user, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if (sync) {
                    execRemoteQueryAndInsert(context, user, tableToSync.toString());
                    numberOfTableSynced++;
                    syncPercentage = ((numberOfTableSynced * 100) / numberOfTablesToSync);
                } else {
                    break;
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
                parameters.put("syncSessionId", mUser.getServerSyncSessionId());
                parameters.put("appVersionCode", mCurrentAppVersionCode);
                parameters.put("tableName", tableName);
                parameters.put("tableCount", c.getInt(0));
                parameters.put("maxSeqId", c.getString(1)==null ? -1 : c.getInt(1));
                //Log.d(TAG, "tableName: "+tableName+", tableCount: "+c.getInt(0)+", maxSeqId: "+String.valueOf(c.getString(1)==null ? -1 : c.getInt(1)));
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

        if(parameters!=null && parameters.size()==8) {
            ConsumeWebService a = new ConsumeWebService(context,
                    mUser.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
                    "sendDataToClient",
                    "urn:sendDataToClient",
                    parameters,
                    mConnectionTimeOut);
            Object result = a.getWSResponse();
            if (result instanceof List<?>) {
                SQLiteDatabase sqLiteDatabase = user==null
                        ? new DatabaseHelper(context).getWritableDatabase()
                        : new DatabaseHelper(context, user).getWritableDatabase();
                try {
                    try {
                        /**********************************************************************************************/
                        int counterEntireCompressedData = 0;
                        int counter;
                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray(DataBaseUtilities.unGzip(
                                    Base64.decode((((List<SoapPrimitive>) result).get(0) != null
                                            ? ((List<SoapPrimitive>) result).get(0).toString() : null), Base64.GZIP)));
                        } catch (IOException e) {
                            //Seguramente entre aqui cuando no hay nada que sincronizar
                            throw new IOException("IOException, tableName: "+tableName+", data: " + String.valueOf(((List<SoapPrimitive>) result).get(0) != null
                                    ? ((List<SoapPrimitive>) result).get(0).toString() : null));
                        }
                        Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
                        if(keys.hasNext()){
                            int columnCount = 0;
                            JSONArray jsonArray2 = new JSONArray(DataBaseUtilities.unGzip(Base64.decode(jsonArray
                                    .getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
                            StringBuilder insertSentence = new StringBuilder("INSERT OR REPLACE INTO ").append(tableName).append(" (");
                            try{
                                counter = 0;
                                Iterator<?> keysTemp = jsonArray2.getJSONObject(counter).keys();
                                while(keysTemp.hasNext()){
                                    if(columnCount==0){
                                        insertSentence.append(jsonArray2.getJSONObject(counter).getString((String) keysTemp.next()));
                                    } else {
                                        insertSentence.append(", ").append(jsonArray2.getJSONObject(counter).getString((String) keysTemp.next()));
                                    }
                                    columnCount++;
                                }
                                insertSentence.append(") VALUES (");
                                for (int i = 0; i<columnCount; i++) {
                                    insertSentence.append((i==0) ? "?" : ", ?");
                                }
                                insertSentence.append(")");
                            } catch (Exception e){
                                e.printStackTrace();
                            }

                            SQLiteStatement statement = null;
                            try {
                                statement = sqLiteDatabase.compileStatement(insertSentence.toString());
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
                                    sqLiteDatabase.beginTransactionNonExclusive();
                                } else {
                                    sqLiteDatabase.beginTransaction();
                                }
                                counter = 1;
                                Iterator<?> keysTemp;
                                String key;
                                //Se itera a traves de la data
                                while (counter <= jsonArray2.length()) {
                                    if (++counter >= jsonArray2.length()) {
                                        if (keys.hasNext()) {
                                            counter = 0;
                                            jsonArray2 = new JSONArray(DataBaseUtilities.unGzip(Base64.decode(jsonArray
                                                    .getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
                                            if (jsonArray2.length() < 1) {
                                                break;
                                            }
                                        } else {
                                            if (++counterEntireCompressedData >= jsonArray.length()) {
                                                break;
                                            } else {
                                                counter = 0;
                                                keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
                                                jsonArray2 = new JSONArray(DataBaseUtilities.unGzip(Base64.decode(jsonArray
                                                        .getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
                                                if (jsonArray2.length() < 1) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    //Se prepara la data que se insertara
                                    statement.clearBindings();
                                    keysTemp = jsonArray2.getJSONObject(counter).keys();
                                    while(keysTemp.hasNext()){
                                        key = (String) keysTemp.next();
                                        statement.bindString(Integer.valueOf(key), jsonArray2.getJSONObject(counter).getString(key));
                                    }
                                    statement.executeInsert();
                                    //Fin de preparacion de la data que se insertara
                                }
                                //la condicion de SEQUENCE_ID <> 0 es para que no elimine los registro que no se han sincronizado
                                sqLiteDatabase.delete(tableName, TextUtils.isEmpty(((List<SoapPrimitive>) result).get(1) != null ? ((List<SoapPrimitive>) result).get(1).toString() : null) ? null
                                                : ("SEQUENCE_ID <> 0 AND SEQUENCE_ID NOT IN (" +
                                                DataBaseUtilities.unGzip(Base64.decode(((List<SoapPrimitive>) result).get(1) != null ? ((List<SoapPrimitive>) result).get(1).toString() : null, Base64.GZIP)) + ")"),
                                        null);
                                sqLiteDatabase.setTransactionSuccessful();
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw e;
                            } finally {
                                if(statement!=null) {
                                    try {
                                        statement.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if(sqLiteDatabase!=null) {
                                    try {
                                        sqLiteDatabase.endTransaction();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        /**********************************************************************************************/
                    } catch (SQLiteException e) {
                        e.printStackTrace();
                        reportSyncError(String.valueOf(e.getMessage()), e.getClass().getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (sqLiteDatabase!=null) {
                        try {
                            sqLiteDatabase.close();
                        } catch (Exception e) {
                            //do nothing
                        }
                    }
                }
            } else if (result instanceof Exception) {
                throw (Exception) result;
            } else if (result != null) {
                throw new Exception("Error while executing execRemoteQueryAndInsert(...), result is not null, ClassCastException.");
            } else {
                throw new Exception("Error while executing execRemoteQueryAndInsert(...), result is null.");
            }
        }
	}

    private void reportSyncError(String errorMessage, String exceptionClass) {
        try{
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("authToken", mUser.getAuthToken());
            parameters.put("userGroupName", mUser.getUserGroup());
            parameters.put("userId", mUser.getServerUserId());
            parameters.put("syncSessionId", mUser.getServerSyncSessionId());
            parameters.put("appVersionCode", mCurrentAppVersionCode);
            parameters.put("errorMessage", errorMessage);
            parameters.put("exceptionClass", exceptionClass);
            ConsumeWebService a = new ConsumeWebService(mContext,
                    mUser.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageDBDataTransfer?wsdl",
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

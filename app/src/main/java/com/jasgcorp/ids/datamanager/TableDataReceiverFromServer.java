package com.jasgcorp.ids.datamanager;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;

import net.iharder.Base64;

public class TableDataReceiverFromServer extends Thread {
	
	private static final String TAG = TableDataReceiverFromServer.class.getSimpleName();

	private Context context;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private User mUser;
	
	public TableDataReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.mUser = user;
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
			if(sync){
				getGlobalDataFromWS(context, getGlobalTablesToSync());
			}
			if(sync){
				getUserDataFromWS(context, mUser, getUserTablesToSync());
			}
            syncPercentage = 100;
            Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
            reportSyncError(e.getMessage(), e.getClass().getName());
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
		sync = false;
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
                parameters);
        return (List<SoapPrimitive>) a.getWSResponse();
    }

	public void getGlobalDataFromWS(Context context, List<SoapPrimitive> tablesToSync) throws Exception {
        if (tablesToSync!=null && !tablesToSync.isEmpty()) {
            for (SoapPrimitive tableToSync : tablesToSync) {
                if(sync) {
                    execRemoteQueryAndInsert(context, null, tableToSync.toString());
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
                parameters);
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
                ConsumeWebService a = new ConsumeWebService(context,
                        mUser.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
                        "getDataForTable",
                        "urn:getDataForTable",
                        parameters);
                Object result = a.getWSResponse();
                if (result instanceof SoapPrimitive) {
                    try {
                        insertDataFromWSResultData(result.toString(), tableName, context, user);
                    } catch (IOException e) {
                        if (!result.toString().equals("NOTHING_TO_SYNC")){
                            throw e;
                        }
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

	/**
	 *
	 * @param data
	 * @param tableName
	 * @param context
	 * @throws Exception
	 */
	public void insertDataFromWSResultData(String data, String tableName, Context context, User user) throws Exception {
		int counterEntireCompressedData = 0;
		int counter;
		JSONArray jsonArray = new JSONArray(ApplicationUtilities.unGzip(Base64.decode(data, Base64.GZIP)));
		Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
		if(keys.hasNext()){
			int columnCount = 0;
			JSONArray jsonArray2 = new JSONArray(ApplicationUtilities.unGzip(Base64.decode(jsonArray
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

			int columnIndex;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			try {
                if(user == null){
                    db = (new DatabaseHelper(context)).getWritableDatabase();
                }else{
				    db = (new DatabaseHelper(context, user)).getWritableDatabase();
                }

				statement = db.compileStatement(insertSentence.toString());
				db.beginTransaction();
                counter = 1;
				//Se itera a traves de la data
				while (counter <= jsonArray2.length()) {
					if (++counter >= jsonArray2.length()) {
						if (keys.hasNext()) {
							counter = 0;
							jsonArray2 = new JSONArray(ApplicationUtilities.unGzip(Base64
                                    .decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
							if (jsonArray2.length() < 1) {
								break;
							}
						} else {
							if (++counterEntireCompressedData >= jsonArray.length()) {
								break;
							} else {
								counter = 0;
								keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
								jsonArray2 = new JSONArray(ApplicationUtilities.unGzip(Base64
                                        .decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
								if (jsonArray2.length() < 1) {
									break;
								}
							}
						}
					}
					//Se prepara la data que se insertara
					statement.clearBindings();
					for (columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
						try {
							//data que se insertara
							statement.bindString(columnIndex, jsonArray2.getJSONObject(counter).getString(String.valueOf(columnIndex)));
						} catch (JSONException e) {
                            //Log.w(TAG, e.getMessage()!=null ? e.getMessage() : "insertDataFromWSResultData - JSONException");
                        } catch (Exception e) {
							e.printStackTrace();
						}
					}
					statement.execute();
					//Fin de preparacion de la data que se insertara
				}
				db.setTransactionSuccessful();
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
				if (db!=null) {
					try {
						db.endTransaction();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
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
                    parameters);
            a.getWSResponse();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

	public float getSyncPercentage() {
		return syncPercentage;
	}

}

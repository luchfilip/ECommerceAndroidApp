package com.jasgcorp.ids.datamanager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.codehaus.jettison.json.JSONArray;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import net.iharder.Base64;

public class TableDataReceiverFromServer extends Thread {
	
	private static final String TAG = TableDataReceiverFromServer.class.getSimpleName();

	private Context context;
	private String serverAddress;
	private String authToken;
	private Long serverUserId;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private DatabaseHelper dbHelper;
	private float syncPercentage;
	private User mUser;
	
	public TableDataReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.serverAddress = user.getServerAddress();
		this.authToken = user.getAuthToken();
		this.serverUserId = user.getServerUserId();
		this.mUser = user;
		dbHelper = new DatabaseHelper(context, user);
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
			//if(sync){
			//	pullIDSTableDataFromServer();
			//	sync = false;
			//}
			//while (sync) {
			//	pullTableDataFromServer(getRowEventIdsInClient());
			//}
			sync = Utils.appRequireInitialLoad(context, mUser);
			if(sync){
				loadInitialDataFromWS(context, mUser);
			}
		/*} catch (ConnectException e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		} catch (SocketException e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		} catch (IOException e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();*/
		} catch (Exception e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
		sync = false;
	}

	public void loadInitialDataFromWS(Context context, User user) throws Exception {
		long initTime = System.currentTimeMillis();
        syncPercentage = 0;
		if(sync){
			execRemoteQueryAndInsert(context, user,
					"select IDARTICULO, IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, " +
							" USO, OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, " +
							" UNIDADVENTA_COMERCIAL, EMPAQUE_COMERCIAL, LAST_RECEIVED_DATE " +
							" from ARTICULOS where ACTIVO = 'V'",
					"INSERT OR REPLACE INTO ARTICULOS (IDARTICULO, IDPARTIDA, " +
							" IDMARCA, NOMBRE, DESCRIPCION, USO, OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, " +
							" CODVIEJO, UNIDADVENTA_COMERCIAL, EMPAQUE_COMERCIAL, LAST_RECEIVED_DATE) " +
							" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            syncPercentage = 10;
		}
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select BRAND_ID, NAME, DESCRIPTION from BRAND where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BRAND (BRAND_ID, NAME, DESCRIPTION) VALUES (?, ?, ?)");
            syncPercentage = 20;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select CATEGORY_ID, NAME, DESCRIPTION from Category where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO CATEGORY (CATEGORY_ID, NAME, DESCRIPTION) VALUES (?, ?, ?)");
            syncPercentage = 30;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select MAINPAGE_PRODUCT_ID, MAINPAGE_PRODUCT_SECTION_ID, PRODUCT_ID, PRIORITY from MAINPAGE_PRODUCT where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO MAINPAGE_PRODUCT (MAINPAGE_PRODUCT_ID, MAINPAGE_PRODUCT_SECTION_ID, PRODUCT_ID, PRIORITY) VALUES (?, ?, ?, ?)");
            syncPercentage = 40;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select MAINPAGE_PRODUCT_SECTION_ID, NAME, DESCRIPTION, PRIORITY from MAINPAGE_PRODUCT_SECTION where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO MAINPAGE_PRODUCT_SECTION (MAINPAGE_PRODUCT_SECTION_ID, NAME, DESCRIPTION, PRIORITY) VALUES (?, ?, ?, ?)");
            syncPercentage = 50;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"SELECT PRODUCT_ID, AVAILABILITY FROM PRODUCT_AVAILABILITY WHERE ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_AVAILABILITY (PRODUCT_ID, AVAILABILITY) VALUES (?, ?)");
            syncPercentage = 60;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select PRODUCT_IMAGE_ID, PRODUCT_ID, FILE_NAME, PRIORITY from PRODUCT_IMAGE where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_IMAGE (PRODUCT_IMAGE_ID, PRODUCT_ID, FILE_NAME, PRIORITY) VALUES (?, ?, ?, ?)");
            syncPercentage = 70;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION from SUBCATEGORY where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO SUBCATEGORY (SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION) VALUES (?, ?, ?, ?)");
            syncPercentage = 80;
        }
        if(sync){
			execRemoteQueryAndInsert(context, user,
					"select PRODUCT_ID, PRODUCT_RELATED_ID, TIMES from PRODUCT_SHOPPING_RELATED where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_SHOPPING_RELATED (PRODUCT_ID, PRODUCT_RELATED_ID, TIMES) VALUES (?, ?, ?)");
            syncPercentage = 90;
        }
		if(sync){
			execRemoteQueryAndInsert(context, user,
					"select BANNER_ID, PRODUCT_ID, BRAND_ID, SUBCATEGORY_ID, CATEGORY_ID, IMAGE_FILE_NAME from BANNER where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BANNER (BANNER_ID, PRODUCT_ID, BRAND_ID, SUBCATEGORY_ID, CATEGORY_ID, IMAGE_FILE_NAME) VALUES (?, ?, ?, ?, ?, ?)");
			syncPercentage = 95;
		}
		if(sync){
			execRemoteQueryAndInsert(context, user,
					"select BRAND_PROMOTIONAL_CARD_ID, BRAND_ID, IMAGE_FILE_NAME, PROMOTIONAL_TEXT, " +
							" BACKGROUND_R_COLOR, BACKGROUND_G_COLOR, BACKGROUND_B_COLOR, PROMOTIONAL_TEXT_R_COLOR, " +
							" PROMOTIONAL_TEXT_G_COLOR, PROMOTIONAL_TEXT_B_COLOR " +
						" from BRAND_PROMOTIONAL_CARD where ISACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BRAND_PROMOTIONAL_CARD (BRAND_PROMOTIONAL_CARD_ID, BRAND_ID, " +
							" IMAGE_FILE_NAME, PROMOTIONAL_TEXT, BACKGROUND_R_COLOR, BACKGROUND_G_COLOR, " +
							" BACKGROUND_B_COLOR, PROMOTIONAL_TEXT_R_COLOR, PROMOTIONAL_TEXT_G_COLOR, " +
							" PROMOTIONAL_TEXT_B_COLOR) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			syncPercentage = 100;
		}
		Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
	}

	/**
	 *
	 * @param context
	 * @param user
	 * @param sql
	 * @param insertSentence
	 */
	private void execRemoteQueryAndInsert(Context context, User user, String sql,
                                          String insertSentence) throws Exception {
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("authToken", user.getAuthToken());
		parameters.put("userId", user.getServerUserId());
		parameters.put("sql", sql);
		ConsumeWebService a = new ConsumeWebService(context,
				user.getServerAddress(),
				"/IntelligentDataSynchronizer/services/ManageRemoteDBAccess?wsdl",
				"executeQuery",
				"urn:executeQuery",
				parameters);
        Object result = a.getWSResponse();
        if(result instanceof SoapPrimitive){
            insertDataFromWSResultData(result.toString(), insertSentence, context, user);
        }else if (result !=null){
            throw new Exception("Error while executing execQueryRemoteDB("+user.getServerAddress()+", "+sql+"), ClassCastException.");
        }else{
            throw new Exception("Error while executing execQueryRemoteDB("+user.getServerAddress()+", "+sql+"), result is null.");
        }
	}

	/**
	 *
	 * @param data
	 * @param insertSentence
	 * @param context
	 * @param user
	 * @throws Exception
	 */
	public void insertDataFromWSResultData(String data, String insertSentence, Context context, User user) throws Exception {
		int counterEntireCompressedData = 0;
		int counter = 0;
		JSONArray jsonArray = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(data, Base64.GZIP)));
		Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
		if(keys.hasNext()){
			int columnCount = 0;
			JSONArray jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
			try{
				counter = 1;
				Iterator<?> keysTemp = jsonArray2.getJSONObject(counter).keys();
				while(keysTemp.hasNext()){
					keysTemp.next();
					columnCount++;
				}
			} catch (Exception e){
				e.printStackTrace();
			}

			int columnIndex;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			try {
				db = (new DatabaseHelper(context, user)).getWritableDatabase();

				statement = db.compileStatement(insertSentence);
				db.beginTransaction();
				//Se itera a traves de la data
				while (counter <= jsonArray2.length()) {
					if (++counter >= jsonArray2.length()) {
						if (keys.hasNext()) {
							counter = 0;
							jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
							if (jsonArray2.length() < 1) {
								break;
							}
						} else {
							if (++counterEntireCompressedData >= jsonArray.length()) {
								break;
							} else {
								counter = 0;
								keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
								jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
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

//	/**
//	 * Devuelve los
//	 * @return
//	 */
//	private String getRowEventIdsInClient() {
//		return null;
//	}
//
//	/**
//	 *
//	 * @throws Exception
//	 */
//	private void pullIDSTableDataFromServer() throws Exception{
//		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
//		parameters.put("authToken", authToken);
//		parameters.put("userId", serverUserId);
//		ConsumeWebService a = new ConsumeWebService(context,
//													serverAddress,
//									    			"/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
//									    			"sendIDSDataToClient",
//									    			"urn:sendIDSDataToClient",
//									    			parameters);
//		try{
//			Object response = a.getWSResponse();
//			//se valida si el usuario no ha detenido la sincronizacion
//			if(sync){
//				if(response instanceof SoapPrimitive){
//					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
//						Log.d(TAG, "Nothing to sync");
//						stopSynchronization();
//					}else{
//						processIDSTableData(response);
//					}
//				}else if (response!=null){
//					throw new Exception("response classCastException, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
//				}else {
//					throw new Exception("response is null, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
//				}
//			}else{
//				Log.w(TAG, "La sincronizacion se detuvo.");
//			}
//		} catch (ConnectException e){
//			Log.e(TAG, "ConnectException");
//			e.printStackTrace();
//			throw e;
//		} catch(SocketTimeoutException e){
//			Log.e(TAG, "SocketTimeoutException");
//			e.printStackTrace();
//			throw e;
//		} catch (SocketException e){
//			Log.e(TAG, "SocketException");
//			e.printStackTrace();
//			throw e;
//		} catch (IOException e){
//			Log.e(TAG, "IOException");
//			e.printStackTrace();
//			throw e;
//		} catch (Exception e){
//			Log.e(TAG, "Exception");
//			e.printStackTrace();
//			throw new Exception("Exception while execute pullIDSTableDataFromServer(), Exception Message: "+e.getMessage());
//		}
//	}
	
//	/**
//	 *
//	 * @param data
//	 * @throws IOException
//	 * @throws Exception
//	 */
//	private void processIDSTableData(Object data) throws IOException, Exception {
//		//TODO insert in table
//		Cursor c = ApplicationUtilities.parseJsonCursorToCursor(((SoapPrimitive) data).toString());
//		StringBuffer sb = new StringBuffer("> ");
//		Log.d(TAG, sb.toString());
//		while(c.moveToNext()){
//			Log.d(TAG, "processIDSTableData: " + c.getString(0));
//		}
//	}
	
//	/**
//	 *
//	 * @param rowEventIdsInClient
//	 * @throws Exception
//	 */
//	private void pullTableDataFromServer(String rowEventIdsInClient) throws Exception{
//		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
//		parameters.put("authToken", authToken);
//		parameters.put("userId", serverUserId);
//		parameters.put("rowEventIdsInClient", rowEventIdsInClient);
//		ConsumeWebService a = new ConsumeWebService(context,
//													serverAddress,
//									    			"/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl",
//									    			"sendDataToClient",
//									    			"urn:sendDataToClient",
//									    			parameters);
//		try{
//			Object response = a.getWSResponse();
//			//se valida si el usuario no ha detenido la sincronizacion
//			if(sync){
//				if(response instanceof SoapPrimitive){
//					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
//						sync = false;
//					}else{
//						processData(response);
//					}
//				}else if (response!=null){
//					throw new Exception("response classCastException, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
//				}else {
//					throw new Exception("response is null, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
//				}
//			}else{
//				Log.w(TAG, "La sincronizacion se detuvo.");
//			}
//		} catch (ConnectException e){
//			Log.e(TAG, "ConnectException");
//			e.printStackTrace();
//			throw e;
//		} catch(SocketTimeoutException e){
//			Log.e(TAG, "SocketTimeoutException");
//			e.printStackTrace();
//			throw e;
//		} catch (SocketException e){
//			Log.e(TAG, "SocketException");
//			e.printStackTrace();
//			throw e;
//		} catch (IOException e){
//			Log.e(TAG, "IOException");
//			e.printStackTrace();
//			throw e;
//		} catch (Exception e){
//			Log.e(TAG, "Exception");
//			e.printStackTrace();
//			throw new Exception("Exception while execute pullTableDataFromServer("+rowEventIdsInClient+"), Exception Message: "+e.getMessage());
//		}
//	}
	
//	/**
//	 *
//	 * @param data
//	 * @throws IOException
//	 * @throws Exception
//	 */
//	private void processData(Object data) throws IOException, Exception {
//		//TODO insert in table
//		//0)ROW_EVENT_ID,
//		//1)EVENT_TYPE,
//		//2)TABLE_NAME,
//		//3)TABLE_VERSION,
//		//4)ROW_PK,
//		//5)NEW_VALUE
//		Cursor c = ApplicationUtilities.parseJsonCursorToCursor(((SoapPrimitive) data).toString());
//		StringBuffer sb = new StringBuffer("> ");
//		//Log.d(TAG, sb.toString());
//		while(c.moveToNext()){
//			if(c.getString(1).equals("I") || c.getString(1).equals("U")){
//				//Log.d(TAG, "INSERT INTO "+c.getString(2)+" () VALUES "+c.getString(5));
//			}else if(c.getString(1).equals("D")){
//				//Log.d(TAG, "DELETE FROM "+c.getString(2)+" WHERE "+c.getString(4));
//			}
//		}
//
//	}

	public float getSyncPercentage() {
		return syncPercentage;
	}

}

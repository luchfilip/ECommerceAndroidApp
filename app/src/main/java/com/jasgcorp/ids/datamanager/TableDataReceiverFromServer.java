package com.jasgcorp.ids.datamanager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;

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
//			if(sync){
//				pullIDSTableDataFromServer();
//				sync = false;
//			}
//			while (sync) {
//				pullTableDataFromServer(getRowEventIdsInClient());
//			}

			long initTime = System.currentTimeMillis();
			Cursor c = null;
			DatabaseHelper dbh;
			SQLiteDatabase db = null;
			ContentValues cv = new ContentValues();;
			try{
				dbh = new DatabaseHelper(context, mUser);
				db = dbh.getWritableDatabase();
				c = getDataFromWS(context, "select IDARTICULO, IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, " +
						" USO, OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, " +
						" UNIDADVENTA_COMERCIAL, EMPAQUE_COMERCIAL, LAST_RECEIVED_DATE " +
						" from ARTICULOS where ACTIVO = 'V'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("IDARTICULO", c.getInt(0));
						cv.put("IDPARTIDA", c.getInt(1));
						cv.put("IDMARCA", c.getInt(2));
						cv.put("NOMBRE", c.getString(3));
						cv.put("DESCRIPCION", c.getString(4));
						cv.put("USO", c.getString(5));
						cv.put("OBSERVACIONES", c.getString(6));
						cv.put("IDREFERENCIA", c.getString(7));
						cv.put("NACIONALIDAD", c.getString(8));
						cv.put("CODVIEJO", c.getString(9));
						cv.put("UNIDADVENTA_COMERCIAL", c.getInt(10));
						cv.put("EMPAQUE_COMERCIAL", c.getString(11));
						cv.put("LAST_RECEIVED_DATE", c.getString(12));
						db.insertWithOnConflict("ARTICULOS", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					} catch(Exception e) {
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select BRAND_ID, NAME, DESCRIPTION from BRAND where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("BRAND_ID", c.getInt(0));
						cv.put("NAME", c.getString(1));
						cv.put("DESCRIPTION", c.getString(2));
						db.insertWithOnConflict("BRAND", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select CATEGORY_ID, NAME, DESCRIPTION from Category where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("CATEGORY_ID", c.getInt(0));
						cv.put("NAME", c.getString(1));
						cv.put("DESCRIPTION", c.getString(2));
						db.insertWithOnConflict("CATEGORY", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select MAINPAGE_PRODUCT_ID, MAINPAGE_SECTION_ID, PRODUCT_ID, PRIORITY " +
						" from MAINPAGE_PRODUCT where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("MAINPAGE_PRODUCT_ID", c.getInt(0));
						cv.put("MAINPAGE_SECTION_ID", c.getInt(1));
						cv.put("PRODUCT_ID", c.getInt(2));
						cv.put("PRIORITY", c.getInt(3));
						db.insertWithOnConflict("MAINPAGE_PRODUCT", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY " +
						" from MAINPAGE_SECTION where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("MAINPAGE_SECTION_ID", c.getInt(0));
						cv.put("NAME", c.getString(1));
						cv.put("DESCRIPTION", c.getString(2));
						cv.put("PRIORITY", c.getInt(3));
						db.insertWithOnConflict("MAINPAGE_SECTION", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select PRODUCT_ID, AVAILABILITY, CREATE_TIME, UPDATE_TIME " +
						" from PRODUCT_AVAILABILITY where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("PRODUCT_ID", c.getInt(0));
						cv.put("AVAILABILITY", c.getInt(1));
						cv.put("CREATE_TIME", c.getString(2));
						cv.put("UPDATE_TIME", c.getString(3));
						db.insertWithOnConflict("PRODUCT_AVAILABILITY", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select PRODUCT_IMAGE_ID, PRODUCT_ID, FILE_NAME, PRIORITY " +
						" from PRODUCT_IMAGE where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("PRODUCT_IMAGE_ID", c.getInt(0));
						cv.put("PRODUCT_ID", c.getInt(1));
						cv.put("FILE_NAME", c.getString(2));
						cv.put("PRIORITY", c.getInt(3));
						db.insertWithOnConflict("PRODUCT_IMAGE", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION " +
						" from SUBCATEGORY where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("SUBCATEGORY_ID", c.getInt(0));
						cv.put("CATEGORY_ID", c.getInt(1));
						cv.put("NAME", c.getString(2));
						cv.put("DESCRIPTION", c.getString(3));
						db.insertWithOnConflict("SUBCATEGORY", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					}catch(Exception e){
						e.getMessage();
					}
				}
				c.close();
				c=null;

				c = getDataFromWS(context, "select PRODUCT_ID, PRODUCT_RELATED_ID, TIMES " +
						" from PRODUCT_SHOPPING_RELATED where ISACTIVE = 'Y'", mUser);
				while (c.moveToNext()) {
					try {
						cv.clear();
						cv.put("PRODUCT_ID", c.getInt(0));
						cv.put("PRODUCT_RELATED_ID", c.getInt(1));
						cv.put("TIMES", c.getInt(2));
						db.insertWithOnConflict("PRODUCT_SHOPPING_RELATED", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
					} catch(Exception e) {
						e.getMessage();
					}
				}

				//c = getDataFromWS(context, "select * from ECommerce_Order", user);
				//while (c.moveToNext()) {
				//    try {
				//        cv.clear();
				//        cv.put("", "");
				//        db.insertWithOnConflict("ECOMMERCE_ORDER", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
				//    }catch(Exception e){
				//        e.getMessage();
				//    }
				//}
				//c.close();
				//c=null;

				//c = getDataFromWS(context, "select * from ECommerce_OrderLine", user);
				//while (c.moveToNext()) {
				//    try {
				//        cv.clear();
				//        cv.put("", "");
				//        db.insertWithOnConflict("ECOMMERCE_ORDERLINE", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
				//    }catch(Exception e){
				//        e.getMessage();
				//    }
				//}
				//c.close();
				//c=null;

				//c = getDataFromWS(context, "select * from Recent_Search", user);
				//while (c.moveToNext()) {
				//    try {
				//        cv.clear();
				//        cv.put("", "");
				//        db.insertWithOnConflict("RECENT_SEARCH", null, cv, SQLiteDatabase.CONFLICT_REPLACE);
				//    }catch(Exception e){
				//        e.getMessage();
				//    }
				//}
				//c.close();
				//c=null;
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
				if (c!=null) {
					try {
						c.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (db!=null) {
					try {
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
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

	public static Cursor getDataFromWS(final Context context, final String sql, final User user){
		try {
			return new AsyncTask<Void, Void, Cursor>() {
				@Override
				protected Cursor doInBackground(Void... voids) {
					try {
						return context.getContentResolver().query(DataBaseContentProvider
										.REMOTE_DB_URI.buildUpon()
										.appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
										.build(),
								null,
								sql,
								null,
								null);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute().get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Devuelve los 
	 * @return
	 */
	private String getRowEventIdsInClient() {
		return null;
	}
	
	/**
	 * 
	 * @throws Exception 
	 */
	private void pullIDSTableDataFromServer() throws Exception{
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("authToken", authToken);
		parameters.put("userId", serverUserId);
		ConsumeWebService a = new ConsumeWebService(context, 
													serverAddress, 
									    			"/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl", 
									    			"sendIDSDataToClient", 
									    			"urn:sendIDSDataToClient", 
									    			parameters);
		try{
			Object response = a.getWSResponse();
			//se valida si el usuario no ha detenido la sincronizacion
			if(sync){
				if(response instanceof SoapPrimitive){
					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
						Log.d(TAG, "Nothing to sync");
						stopSynchronization();
					}else{
						processIDSTableData(response);
					}
				}else if (response!=null){
					throw new Exception("response classCastException, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
				}else {
					throw new Exception("response is null, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
				}
			}else{
				Log.w(TAG, "La sincronizacion se detuvo.");
			}
		} catch (ConnectException e){
			Log.e(TAG, "ConnectException");
			e.printStackTrace();
			throw e;
		} catch(SocketTimeoutException e){
			Log.e(TAG, "SocketTimeoutException");
			e.printStackTrace();
			throw e;
		} catch (SocketException e){
			Log.e(TAG, "SocketException");
			e.printStackTrace();
			throw e;
		} catch (IOException e){
			Log.e(TAG, "IOException");
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			Log.e(TAG, "Exception");
			e.printStackTrace();
			throw new Exception("Exception while execute pullIDSTableDataFromServer(), Exception Message: "+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param data
	 * @throws IOException
	 * @throws Exception
	 */
	private void processIDSTableData(Object data) throws IOException, Exception {
		//TODO insert in table
		Cursor c = ApplicationUtilities.parseJsonCursorToCursor(((SoapPrimitive) data).toString());
		StringBuffer sb = new StringBuffer("> ");
		Log.d(TAG, sb.toString());
		while(c.moveToNext()){
			Log.d(TAG, "processIDSTableData: " + c.getString(0));
		}
		
	}
	
	/**
	 * 
	 * @param rowEventIdsInClient
	 * @throws Exception 
	 */
	private void pullTableDataFromServer(String rowEventIdsInClient) throws Exception{
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("authToken", authToken);
		parameters.put("userId", serverUserId);
		parameters.put("rowEventIdsInClient", rowEventIdsInClient);
		ConsumeWebService a = new ConsumeWebService(context, 
													serverAddress, 
									    			"/IntelligentDataSynchronizer/services/ManageTableDataTransfer?wsdl", 
									    			"sendDataToClient", 
									    			"urn:sendDataToClient", 
									    			parameters);
		try{
			Object response = a.getWSResponse();
			//se valida si el usuario no ha detenido la sincronizacion
			if(sync){
				if(response instanceof SoapPrimitive){
					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
						sync = false;
					}else{
						processData(response);
					}
				}else if (response!=null){
					throw new Exception("response classCastException, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
				}else {
					throw new Exception("response is null, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+"]");
				}
			}else{
				Log.w(TAG, "La sincronizacion se detuvo.");
			}
		} catch (ConnectException e){
			Log.e(TAG, "ConnectException");
			e.printStackTrace();
			throw e;
		} catch(SocketTimeoutException e){
			Log.e(TAG, "SocketTimeoutException");
			e.printStackTrace();
			throw e;
		} catch (SocketException e){
			Log.e(TAG, "SocketException");
			e.printStackTrace();
			throw e;
		} catch (IOException e){
			Log.e(TAG, "IOException");
			e.printStackTrace();
			throw e;
		} catch (Exception e){
			Log.e(TAG, "Exception");
			e.printStackTrace();
			throw new Exception("Exception while execute pullTableDataFromServer("+rowEventIdsInClient+"), Exception Message: "+e.getMessage());
		}
	}
	
	/**
	 * 
	 * @param data
	 * @throws IOException
	 * @throws Exception
	 */
	private void processData(Object data) throws IOException, Exception {
		//TODO insert in table
		//0)ROW_EVENT_ID,
		//1)EVENT_TYPE,
		//2)TABLE_NAME,
		//3)TABLE_VERSION,
		//4)ROW_PK,
		//5)NEW_VALUE
		Cursor c = ApplicationUtilities.parseJsonCursorToCursor(((SoapPrimitive) data).toString());
		StringBuffer sb = new StringBuffer("> ");
		//Log.d(TAG, sb.toString());
		while(c.moveToNext()){
			if(c.getString(1).equals("I") || c.getString(1).equals("U")){
				//Log.d(TAG, "INSERT INTO "+c.getString(2)+" () VALUES "+c.getString(5));
			}else if(c.getString(1).equals("D")){
				//Log.d(TAG, "DELETE FROM "+c.getString(2)+" WHERE "+c.getString(4));
			}
		}
		
	}
	public float getSyncPercentage() {
		return syncPercentage;
	}
}

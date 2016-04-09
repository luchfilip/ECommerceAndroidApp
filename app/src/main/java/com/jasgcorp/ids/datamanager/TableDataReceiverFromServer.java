package com.jasgcorp.ids.datamanager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
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
	
	public TableDataReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.serverAddress = user.getServerAddress();
		this.authToken = user.getAuthToken();
		this.serverUserId = user.getServerUserId();
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
			if(sync){
				pullIDSTableDataFromServer();
				sync = false;
			}
			while (sync) {
				pullTableDataFromServer(getRowEventIdsInClient());
			} 
		} catch (ConnectException e) {
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
			exceptionClass = e.getClass().getName();
		} catch (Exception e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
		sync = false;
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

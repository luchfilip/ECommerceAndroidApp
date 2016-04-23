package com.jasgcorp.ids.datamanager;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.iharder.Base64;

import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;

/**
 * 
 * @author jsarco
 *
 */
public class FolderDataReceiverFromServer extends Thread {
	
	private static final String TAG = FolderDataReceiverFromServer.class.getSimpleName();
	
	private Context context;
	private String serverAddress;
	private String authToken;
	private Long serverUserId;
	private File folder;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private DatabaseHelper dbHelper;
	private float syncPercentage;
	
	public FolderDataReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.serverAddress = user.getServerAddress();
		this.authToken = user.getAuthToken();
		this.serverUserId = user.getServerUserId();
		this.folder = new File(context.getExternalFilesDir(null)+"/"+user.getUserGroup()+"/"+user.getUserName()+"/Data_In/");//-->Android/data/package.name/files/...
		// if the directory does not exist, create it
		if (!folder.exists()) {
		    try{
		        if(!folder.mkdirs()){
		        	Log.w(TAG, "Failed to create folder: "+folder.getPath()+".");
		        }
		    } catch(SecurityException se){
		    	se.printStackTrace();
		    }        
		}
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
				initSync();
				while (sync) {
					pullFileFromServer(getActiveFileSyncIdsInDb());
				}
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
	}
	
	/**
	 * consulta del servidor los archivos que estan activos para sincronizar
	 * @throws Exception
	 */
	private void initSync() throws Exception{
		Log.d(TAG, "initSync() - begin");
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("authToken", authToken);
		parameters.put("userId", serverUserId);
		ConsumeWebService a = new ConsumeWebService(context, 
													serverAddress, 
									    			"/IntelligentDataSynchronizer/services/ManageFolderDataTransfer?wsdl", 
									    			"initSync", 
									    			"urn:initSync", 
									    			parameters);
		try{
			Object response = a.getWSResponse();
			//se valida si el usuario no ha detenido la sincronizacion
			if(sync){
				if(response instanceof SoapPrimitive){
					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
						Log.d(TAG, "Nothing to sync.");
						stopSynchronization();
					}else{
						processActiveFileSyncIds(response);
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
			throw new Exception("Exception while execute initSync(), Exception Message: "+e.getMessage());
		}
	}
	
	/**
	 * elimina del cliente los archivos que no estan activos asi como de la
	 * tabla IDS_INCOMING_FILE_SYNC
	 * @param data
	 */
	private void processActiveFileSyncIds(Object data) {
		Log.d(TAG, "processActiveFileSyncIds("+((SoapPrimitive) data).toString()+")");
		if(data instanceof SoapPrimitive && ((SoapPrimitive) data).toString()!=null){
			SQLiteDatabase database = null;
			Log.d(TAG, "serverUserId: "+serverUserId);
			try {
				database = dbHelper.getReadableDatabase();
				ContentValues cv = new ContentValues();
				cv.put("ISACTIVE","N");
				int rowsAffected = database.update("IDS_INCOMING_FILE_SYNC", 
													cv, 
													"FILE_SYNC_ID NOT IN ("+((SoapPrimitive) data).toString()+")", 
													null);
				Log.d(TAG, "rowsAffected: "+rowsAffected);
				if(rowsAffected>0){
					ArrayList<String> filesToRemove = new ArrayList<String>();
					Cursor c = null;
					try {
						c = database.query("IDS_INCOMING_FILE_SYNC", /*table*/
													new String[]{"FOLDER_CLIENT_NAME", "FILE_NAME"}, /*columns*/
													"ISACTIVE=? AND ERROR_MESSAGE is null", /*selection*/
													new String[] {"N"}, /*selectionArgs*/
													null, /*groupBy*/
													null, /*having*/
													null /*orderBy*/); 
						if(c!=null){
							while(c.moveToNext()){
								try{
									filesToRemove.add(folder.getPath().toString()+"/"+c.getString(1));
								}catch(Exception e1){
									e1.printStackTrace();
								}
							}
						}
					} catch (Exception e){
						e.printStackTrace();
					} finally {
						if(c!=null){
							c.close();
						}
					}
					File file = null;
					for(String fileToRemove : filesToRemove){
						try{
							Log.d(TAG, "fileToRemove: "+fileToRemove);
							file = new File(fileToRemove);
							if(file!=null && file.exists()){
								file.delete();
							}
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							file = null;
						}
					}
					database.delete("IDS_INCOMING_FILE_SYNC", "ISACTIVE=?", new String[] {"N"});
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(database!=null){
					database.close();
				}
			}
		}
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	private String getActiveFileSyncIdsInDb() throws Exception{
		StringBuffer filesReceived = new StringBuffer();
		SQLiteDatabase database = null; 
		Cursor c = null;
		try {
			database = dbHelper.getReadableDatabase(); 
			c = database.query("IDS_INCOMING_FILE_SYNC", /*table*/
										new String[]{"FILE_SYNC_ID"}, /*columns*/
										"ISACTIVE=?", /*where*/
										new String[] {"Y"}, /*selectionArgs*/
										null, /*groupBy*/
										null, /*having*/
										null /*orderBy*/); 
			if(c!=null && c.moveToNext()){
				filesReceived.append(c.getString(0));
				while(c.moveToNext()){
					filesReceived.append(",").append(c.getString(0));
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(c!=null){
				c.close();
			}
			if(database!=null){
				database.close();
			}
		}
		return filesReceived.toString();
	}
	
	/**
	 * 
	 * @param fileIdsInClient
	 * @throws Exception 
	 */
	private void pullFileFromServer(String fileIdsInClient) throws Exception{
		Log.d(TAG, "pullFileFromServer("+fileIdsInClient+")");
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
		parameters.put("authToken", authToken);
		parameters.put("userId", serverUserId);
		parameters.put("fileIdsInClient", fileIdsInClient);
		ConsumeWebService a = new ConsumeWebService(context, 
													serverAddress, 
									    			"/IntelligentDataSynchronizer/services/ManageFolderDataTransfer?wsdl", 
									    			"sendDataToClient", 
									    			"urn:sendDataToClient", 
									    			parameters);
		try{
			Object response = a.getWSResponse();
			//se valida si el usuario no ha detenido la sincronizacion
			if(sync){
				if(response instanceof SoapPrimitive){
					if(((SoapPrimitive) response).toString().equals("NOTHING_TO_SYNC")){
						Log.d(TAG, "Nothing to sync.");
						stopSynchronization();
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
			throw new Exception("Exception while execute pullFileFromServer("+fileIdsInClient+"), Exception Message: "+e.getMessage());
		}
	}
	
	private void processData(Object data) {
		String errorMessage = null;
		String fileSyncId = null;
		String folderClientName = null;
		String fileName = null;
		Integer fileSize = null;
		try {
			JSONObject dataReceived = new JSONObject(((SoapPrimitive) data).toString());
			//TODO insert in table
			fileSyncId =  dataReceived.getString("1");
			fileName = dataReceived.getString("2");
			fileSize = Integer.valueOf(dataReceived.getString("3"));
			folderClientName = dataReceived.getString("5");
			try{
				if(dataReceived.has("syncPercentage")){
					syncPercentage = Float.valueOf(dataReceived.getString("syncPercentage"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			if(dataReceived.has("4")){
				ApplicationUtilities.saveToFile(Base64.decode(ApplicationUtilities.ungzip(Base64.decode(dataReceived.getString("4"), Base64.GZIP)), Base64.GZIP), 
												folder.getPath().toString()+"/"+dataReceived.getString("2"));
				
				//TODO: aqui se debe ir procesando el archivo de datos recibidos. Si hay algun problema al procesarlo se envia un 
				//mensaje de error al servidor y se guarda en el log de errores.
				Log.d(TAG, "File \""+dataReceived.getString("2")+"\" was received successfully from server.");
			}else{
				throw new Exception("Data is null.");
			}
		} catch(Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage()!=null ? e.getMessage() : "Exception with message null while receive file.";
		}
		insertIncomingFile(fileSyncId, folderClientName, fileName, fileSize, errorMessage);
	}
	
	/**
	 *
	 * @param fileSyncId
	 * @param folderClientName
	 * @param fileName
	 * @param fileSize
	 * @param errorMessage
	 */
	private void insertIncomingFile(String fileSyncId, String folderClientName, 
									String fileName, Integer fileSize, String errorMessage){
		SQLiteDatabase database = dbHelper.getWritableDatabase(); 
		ContentValues values = new ContentValues(); 
		values.put("FILE_SYNC_ID", fileSyncId); 
		values.put("FOLDER_CLIENT_NAME", folderClientName); 
		values.put("FILE_NAME", fileName); 
		values.put("FILE_SIZE", fileSize); 
		values.put("ERROR_MESSAGE", errorMessage); 
		database.insert("IDS_INCOMING_FILE_SYNC", null, values); 
		database.close();
	}
	
	public float getSyncPercentage(){
		return syncPercentage;
	}
}

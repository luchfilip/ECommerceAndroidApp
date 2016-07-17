package com.jasgcorp.ids.datamanager;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;

/**
 * before 28.02.2016
 * @author jsarco
 *
 */
public class FolderDataTransferToServer extends Thread {
	private static final String TAG = FolderDataTransferToServer.class.getSimpleName();

	private Context context;
	private String serverAddress;
	private String authToken;
	private Long serverUserId;
	private String userGroup;
	private String userName;
	private File folder;
	
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private DatabaseHelper dbHelper;
	private float syncPercentage;
	
	/**
	 * 
	 * @param user
	 * @param context
	 * @throws Exception 
	 */
	public FolderDataTransferToServer(User user, Context context) throws Exception{
		//this.context = context;
		//this.serverAddress = user.getServerAddress();
		//this.authToken = user.getAuthToken();
		//this.serverUserId = user.getServerUserId();
		//this.userGroup = user.getUserGroup();
		//this.userName = user.getUserName();
		//this.folder = new File(context.getExternalFilesDir(null)+"/"+userGroup+"/"+userName+"/Data_Out/");//-->Android/data/package.name/files/...
		//// if the directory does not exist, create it
		//if (!folder.exists()) {
		//	try{
		//		if(!folder.mkdirs()){
		//			Log.w(TAG, "Failed to create folder: "+folder.getPath()+".");
		//		}
		//	} catch(SecurityException se){
		//		se.printStackTrace();
		//	}
		//}
		//dbHelper = new DatabaseHelper(context, user);
	}
	
	/**
	 * 
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
	
	/**
	 * 
	 */
	public void run() {
		Log.d(TAG, "run()");
		sync = false;
		//try {
		//	if(sync){
		//		initSync();
		//		while (sync) {
		//			sendFileToServer();
		//		}
		//	}
		//} catch (ConnectException e) {
		//	e.printStackTrace();
		//	exceptionMessage = e.getMessage();
		//	exceptionClass = e.getClass().getName();
		//} catch (SocketTimeoutException e) {
		//	e.printStackTrace();
		//	exceptionMessage = e.getMessage();
		//	exceptionClass = e.getClass().getName();
		//} catch (SocketException e) {
		//	e.printStackTrace();
		//	exceptionMessage = e.getMessage();
		//	exceptionClass = e.getClass().getName();
		//} catch (IOException e) {
		//	e.printStackTrace();
		//	exceptionMessage = e.getMessage();
		//	exceptionClass = e.getClass().getName();
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	exceptionMessage = e.getMessage();
		//	exceptionClass = e.getClass().getName();
		//}
	}
	
	///**
	// *
	// */
	//private void initSync(){
	//	ArrayList<String> foldersToTransfer = getFoldersToTransfer();
	//	if(foldersToTransfer==null || foldersToTransfer.size()<=0){
	//		stopSynchronization();
	//	}else{
	//		for(String folderClientName : foldersToTransfer){
	//			ArrayList<String> filesToTransfer = ApplicationUtilities
	//					.getFilesInFolder(context.getExternalFilesDir(null)+"/"+userGroup+"/"+userName+"/Data_Out/"+folderClientName+"/");
	//			if(filesToTransfer==null || filesToTransfer.size()<=0){
	//				stopSynchronization();
	//			}else{
	//				SQLiteDatabase database = null;
	//				ContentValues values = null;
	//				try{
	//					File file = null;
	//					database = dbHelper.getWritableDatabase();
	//					for(String fileToTransfer : filesToTransfer){
	//						file = new File(fileToTransfer);
	//						values = new ContentValues();
	//						values.put("FOLDER_CLIENT_NAME", folderClientName);
	//						values.put("FILE_PATH", file.getAbsolutePath());
	//						values.put("FILE_SIZE", Long.valueOf(file.getTotalSpace()).intValue());
	//						database.insert("OUTGOING_FILE_SYNC_ID", null, values);
	//						file = null;
	//					}
	//				}catch(Exception e){
	//					e.printStackTrace();
	//				}finally{
	//					if(database!=null){
	//						database.close();
	//					}
	//				}
	//
	//			}
	//		}
	//	}
	//}
	
	//private ArrayList<String> getFoldersToTransfer() {
	//	// TODO Auto-generated method stub
	//	return null;
	//}

	///**
	// *
	// * @return
	// * @throws Exception
	// */
	//private void sendFileToServer() throws Exception{
	//	Log.d(TAG, "sendFileToServer() - begin");
	//	String fileName = getNextFileToTransfer();
	//	if(fileName==null || fileName.isEmpty()){
	//		stopSynchronization();
	//	}else{
	//		String dataFile = ApplicationUtilities.encodeFileToBase64Binary(new File(folder.getPath().toString()+"/"+fileName));
	//
	//		LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
	//		parameters.put("authToken", authToken);
	//		parameters.put("userId", serverUserId);
	//		parameters.put("fileName", fileName);
	//		parameters.put("dataFile", dataFile);
	//		ConsumeWebService a = new ConsumeWebService(context,
	//													serverAddress,
	//													"/IntelligentDataSynchronizer/services/ManageFolderDataTransfer?wsdl",
	//													"receiveDataFromClient",
	//													"urn:receiveDataFromClient",
	//													parameters);
	//		boolean result = false;
	//		try{
	//			Object response = a.getWSResponse();
	//			//No se valida si el usuario ha detenido la sincronizacion ya que no se puede detener el envio
	//			//del archivo ni tampoco se puede hacer que el servidor no procese el archivo,
	//			//asi que si se envio el archivo y el servidor lo proceso es bueno saber si lo hizo correctamente o no.
	//			if(response instanceof SoapPrimitive){
	//				result = ((SoapPrimitive) response).toString().equals("true");
	//			}else if (response==null){
	//				throw new Exception("response is null, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+", dataFile: "+dataFile+"]");
	//			}else{
	//				throw new Exception("response classCastException, [serverAddress: "+serverAddress+", serverUserId: "+serverUserId+", dataFile: "+dataFile+"]");
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
	//			throw new Exception("Exception while execute sendFileToServer(), Exception Message: "+e.getMessage());
	//		}
	//		Log.d(TAG, "result: "+result);
	//	}
	//	Log.d(TAG, "sendFileToServer() - ends");
	//}
	
	///**
	// *
	// * @return
	// */
	//private String getNextFileToTransfer() {
	//	return null;
	//}

	public float getSyncPercentage() {
		return syncPercentage;
	}
}
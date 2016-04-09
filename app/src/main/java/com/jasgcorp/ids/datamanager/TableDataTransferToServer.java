package com.jasgcorp.ids.datamanager;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.content.Context;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;

public class TableDataTransferToServer extends Thread{

	private static final String TAG = TableDataTransferToServer.class.getSimpleName();

	private Context context;
	private String serverAddress;
	private String authToken;
	private String userName;
	private String userGroup;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private DatabaseHelper dbHelper;
	private float syncPercentage;
	
	public TableDataTransferToServer(User user, Context context) throws Exception{
		this.context = context;
		this.serverAddress = user.getServerAddress();
		this.authToken = user.getAuthToken();
		this.userName = user.getUserName();
		this.userGroup = user.getUserGroup();
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
//		try {
			while (sync) {
				sync = false;
			} 
//		} catch (ConnectException e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			exceptionClass = e.getClass().getName();
//		} catch (SocketTimeoutException e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			exceptionClass = e.getClass().getName();
//		} catch (SocketException e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			exceptionClass = e.getClass().getName();
//		} catch (IOException e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			exceptionClass = e.getClass().getName();
//		} catch (Exception e) {
//			e.printStackTrace();
//			exceptionMessage = e.getMessage();
//			exceptionClass = e.getClass().getName();
//		}
	}

	public float getSyncPercentage() {
		return syncPercentage;
	}
}

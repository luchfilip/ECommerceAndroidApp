package com.jasgcorp.ids.datamanager;

import android.content.Context;
import android.util.Log;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ConsumeWebService;

import org.ksoap2.serialization.SoapPrimitive;

import java.util.LinkedHashMap;
import java.util.List;

public class TableDataTransferToServer extends Thread{

	private static final String TAG = TableDataTransferToServer.class.getSimpleName();

	private Context context;
	private User mUser;

	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	
	public TableDataTransferToServer(User user, Context context) throws Exception{
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

	private List<SoapPrimitive> getUserTablesToSync() throws Exception {
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

	public float getSyncPercentage() {
		return syncPercentage;
	}
}

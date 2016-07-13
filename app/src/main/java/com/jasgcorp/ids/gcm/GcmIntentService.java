package com.jasgcorp.ids.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;

/**
 * http://developer.android.com/training/sync-adapters/running-sync-adapter.html
 * @author jsarco
 *
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    /**
     * solicita al usuario que sincronice, segun el SERVER_USER_ID que se reciba en la solicitud
     */
    public static final String KEY_SYNC_REQUEST = "com.jasgcorp.ids.gcm.KEY_SYNC_REQUEST";

    /**
     * solicita a la aplicacion que remueva el auth_token del usuario, segun el SERVER_USER_ID que
     * se reciba en la solicitud
     */
    public static final String KEY_INVALIDATE_USER_AUTH_TOKEN_REQUEST =
            "com.jasgcorp.ids.gcm.KEY_INVALIDATE_USER_AUTH_TOKEN_REQUEST";

    /**
     * SERVER_USER_ID unico de cada usuario
     */
    public static final String KEY_SERVER_USER_ID = "com.jasgcorp.ids.gcm.KEY_SERVER_USER_ID";

    /**
     * solicita a la aplicacion que realice una consulta a la base de datos, segun el SERVER_USER_ID
     * que se reciba en la solicitud
     */
	public static final String KEY_QUERY_REQUEST = "com.jasgcorp.ids.gcm.KEY_QUERY_REQUEST";

    /**
     * consulta sql que se realizara en la base de datos
     */
	public static final String KEY_QUERY_SQL = "com.jasgcorp.ids.gcm.KEY_QUERY_SQL";

    /**
     * solicita a la aplicacion la version de la aplicacion que se encuentra instalada
     */
    public static final String KEY_INSTALLED_APP_VERSION_REQUEST =
            "com.jasgcorp.ids.gcm.KEY_INSTALLED_APP_VERSION_REQUEST";

    /**
     * solicita a la aplicacion la version de Android que se encuentra instalada en el dispositivo
     */
    public static final String KEY_ANDROID_API_VERSION_REQUEST =
            "com.jasgcorp.ids.gcm.KEY_ANDROID_API_VERSION_REQUEST";

    /**
     * solicita a la aplicacion la informacion del archivo, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_FILE_INFO_REQUEST = "com.jasgcorp.ids.gcm.KEY_FILE_EXISTS_REQUEST";

    /**
     * solicita a la aplicacion que elimine un archivo, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_DELETE_FILE_REQUEST = "com.jasgcorp.ids.gcm.KEY_DELETE_FILE_REQUEST";

    /**
     * path de un archivo en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no
     */
    public static final String KEY_FILE_PATH = "com.jasgcorp.ids.gcm.KEY_FILE_PATH";

    /**
     * solicita a la aplicacion los nombres de los archivo que se encuentra en una carpeta,
     * segun el SERVER_USER_ID que se reciba en la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_FILES_NAME_BY_FOLDER_REQUEST =
            "com.jasgcorp.ids.gcm.KEY_FILES_NAME_BY_FOLDER_REQUEST";

    /**
     * solicita a la aplicacion la informacion de la carpeta, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_FOLDER_INFO_REQUEST = "com.jasgcorp.ids.gcm.KEY_FOLDER_INFO_REQUEST";

    /**
     * solicita a la aplicacion que elimine una carpeta, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_DELETE_FOLDER_REQUEST = "com.jasgcorp.ids.gcm.KEY_DELETE_FOLDER_REQUEST";

    /**
     * path de una carpeta en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no.
     */
    public static final String KEY_FOLDER_PATH = "com.jasgcorp.ids.gcm.KEY_FILE_PATH";


	@Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    }
	
    public GcmIntentService() {
        super(GcmIntentService.class.getSimpleName());
    }

	@Override
	protected void onHandleIntent(Intent intent) {
        // Get a GCM object instance
        GoogleCloudMessaging gcm =
                GoogleCloudMessaging.getInstance(this);
        // Get the type of GCM message
        String messageType = gcm.getMessageType(intent);
        Bundle extras = intent.getExtras();
        
        if(extras!=null && !extras.isEmpty()){
	        /*
	         * Test the message type and examine the message contents.
	         * Since GCM is a general-purpose messaging system, you
	         * may receive normal messages that don't require a sync
	         * adapter run.
	         * The following code tests for a a boolean flag indicating
	         * that the message is requesting a transfer from the device.
	         */
	        if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)){
	        	//Run the Sync Adapter When Server Data Changes
	        	if(extras.containsKey("action")){
                    String action = extras.getString("action");
                    if(action!=null) {
                        if(action.equals(KEY_SYNC_REQUEST) && extras.containsKey(KEY_SERVER_USER_ID)){
                            try {
                                String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                if (serverUserId != null) {
                                    AccountManager accountManager = AccountManager.get(this);
                                    Account[] accounts = accountManager.getAccountsByType(getString(R.string.authenticator_acount_type));
                                    Account userAccount = null;
                                    for (Account account : accounts) {
                                        if (accountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID).equals(serverUserId)) {
                                            userAccount = account;
                                            break;
                                        }
                                    }
                                    if(userAccount!=null){
                                        if (!ApplicationUtilities.isSyncActive(userAccount, DataBaseContentProvider.AUTHORITY)) {
                                            Bundle settingsBundle = new Bundle();
                                            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                                            settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                                            ContentResolver.requestSync(userAccount, DataBaseContentProvider.AUTHORITY, settingsBundle);
                                            //TODO: enviar respuesta al servidor
                                        }else{
                                            //TODO: enviar respuesta al servidor
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_INVALIDATE_USER_AUTH_TOKEN_REQUEST)
                                && extras.containsKey(KEY_SERVER_USER_ID)){
                            try {
                                String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                if(serverUserId!=null){
                                    AccountManager accountManager = AccountManager.get(this);
                                    Account[] accounts = accountManager.getAccountsByType(getString(R.string.authenticator_acount_type));
                                    Account userAccount = null;
                                    for(Account account : accounts){
                                        if(accountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID).equals(serverUserId)){
                                            userAccount = account;
                                            break;
                                        }
                                    }
                                    if(userAccount!=null){
                                        accountManager.invalidateAuthToken(getString(R.string.authenticator_acount_type),
                                                accountManager.peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
                                        //TODO: enviar respuesta al servidor
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_QUERY_REQUEST)
                                && extras.containsKey(KEY_QUERY_SQL)){
                            try {
                                String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                if (serverUserId != null) {
                                    User user = ApplicationUtilities
                                            .getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                    if (user != null) {
                                        //TODO: enviar respuesta al servidor
                                    } else {
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_INSTALLED_APP_VERSION_REQUEST)){
                            try {
                                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                if(pInfo!=null){
                                    String version = pInfo.versionName;
                                    int verCode = pInfo.versionCode;
                                    //TODO: enviar respuesta al servidor
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_ANDROID_API_VERSION_REQUEST)){
                            try {
                                int androidApiVersion = android.os.Build.VERSION.SDK_INT;
                                //TODO: enviar respuesta al servidor
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_FILE_INFO_REQUEST)
                                && extras.containsKey(KEY_FILE_PATH)){
                            try {
                                String filePath = extras.getString(KEY_FILE_PATH);
                                if(filePath!=null){
                                    String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                    if(serverUserId!=null){
                                        User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user!=null) {
                                            File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                    + File.separator + user.getUserName(), filePath);
                                            if(file.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        } else {
                                            File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                            if(file.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_DELETE_FILE_REQUEST)
                            && extras.containsKey(KEY_FILE_PATH)){
                            try {
                                String filePath = extras.getString(KEY_FILE_PATH);
                                if(filePath!=null){
                                    String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                    if(serverUserId!=null){
                                        User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user!=null) {
                                            File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                    + File.separator + user.getUserName(), filePath);
                                            if(file.exists()){
                                                if(file.delete()){
                                                    //TODO: enviar respuesta al servidor
                                                }else{
                                                    //TODO: enviar respuesta al servidor
                                                }
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        } else {
                                            File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                            if(file.exists()){
                                                if(file.delete()){
                                                    //TODO: enviar respuesta al servidor
                                                }else{
                                                    //TODO: enviar respuesta al servidor
                                                }
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_FILES_NAME_BY_FOLDER_REQUEST)
                                && extras.containsKey(KEY_FOLDER_PATH)){
                            try {
                                String folderPath = extras.getString(KEY_FOLDER_PATH);
                                if(folderPath!=null){
                                    String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                    if(serverUserId!=null){
                                        User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user!=null) {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                    + File.separator + user.getUserName(), folderPath);
                                            if(folder.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        } else {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_FOLDER_INFO_REQUEST)
                                && extras.containsKey(KEY_FOLDER_PATH)){
                            try {
                                String folderPath = extras.getString(KEY_FOLDER_PATH);
                                if(folderPath!=null){
                                    String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                    if(serverUserId!=null){
                                        User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user!=null) {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                    + File.separator + user.getUserName(), folderPath);
                                            if(folder.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        } else {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                //TODO: enviar respuesta al servidor
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else if(action.equals(KEY_DELETE_FOLDER_REQUEST)
                                && extras.containsKey(KEY_FOLDER_PATH)){
                            try {
                                String folderPath = extras.getString(KEY_FOLDER_PATH);
                                if(folderPath!=null){
                                    String serverUserId = extras.getString(KEY_SERVER_USER_ID);
                                    if(serverUserId!=null){
                                        User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user!=null) {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                    + File.separator + user.getUserName(), folderPath);
                                            if(folder.exists()){
                                                if(folder.delete()){
                                                    //TODO: enviar respuesta al servidor
                                                }else{
                                                    //TODO: enviar respuesta al servidor
                                                }
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        } else {
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                if(folder.delete()){
                                                    //TODO: enviar respuesta al servidor
                                                }else{
                                                    //TODO: enviar respuesta al servidor
                                                }
                                            }else{
                                                //TODO: enviar respuesta al servidor
                                            }
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                }else{
                                    //TODO: enviar respuesta al servidor
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                //TODO: enviar respuesta al servidor
                            }
                        }else{
                            //TODO: enviar respuesta al servidor
                        }
                    }else{
                        //TODO: enviar respuesta al servidor
                    }
	        	}else{
                    //TODO: enviar respuesta al servidor
                }
	        }else{
	        	try {
            		Log.d(TAG, "messageType: "+messageType);
            	} catch(NullPointerException e) {
                    //do nothing
                }
	        }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private String listFilesByFolder(final File folder) {
        StringBuilder filesName = new StringBuilder();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesByFolder(fileEntry);
            } else {
                filesName.append(fileEntry.getName()).append(", ");
            }
        }
        return filesName.toString();
    }

    /**
     *
     * @param userGroup
     * @param userName
     * @param messageCode
     * @param createTime
     * @param resultSet
     * @param externalServerAddress
     * @return
     */
    private String sendToServer(String userGroup, String userName, String messageCode,
                                String createTime, String resultSet, String externalServerAddress){
        try{
            String namespace = "http://client.ws.afv.febeca.com";
            String url="http://" +externalServerAddress + "/AfvDroid-WS/services/Sincronizador?wsdl";
            String soapAction = null;
            SoapObject request = null;

            if(userName!=null && messageCode!=null && createTime!=null){
                String methodName = "InsertACKGoogleCloudMessaging";
                soapAction = namespace + methodName;
                request = new SoapObject(namespace, methodName);
                request.addProperty("userGroup", userGroup);
                request.addProperty("userName", userName);
                request.addProperty("messageId", messageCode);
                request.addProperty("createTime", createTime);
            }else if(userName!=null && messageCode!=null && resultSet!=null){
                String methodName = "resultsetGoogleCloudMessaging";
                soapAction = namespace + methodName;
                request = new SoapObject(namespace, methodName);
                request.addProperty("userGroup", userGroup);
                request.addProperty("userName", userName);
                request.addProperty("messageId", messageCode);
                request.addProperty("resultset", resultSet);
            }

            if(request!=null){
                SoapSerializationEnvelope envelope =
                        new SoapSerializationEnvelope(SoapEnvelope.VER11);

                envelope.dotNet = true;

                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransportSE = new HttpTransportSE(url);

                httpTransportSE.call(soapAction, envelope);
                return envelope.getResponse().toString().equals("1") ? "Envio exitoso." : "Envio fallido.";
            }else{
                return "request is null";
            }
        } catch(Exception e){
            return e.getMessage();
        }
    }
}

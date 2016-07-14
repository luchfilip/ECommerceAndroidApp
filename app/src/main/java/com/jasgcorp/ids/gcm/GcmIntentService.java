package com.jasgcorp.ids.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * http://developer.android.com/training/sync-adapters/running-sync-adapter.html
 * @author jsarco
 *
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    /**
     * representa el ID de la solicitud realizada por el servidor
     */
    public static final String KEY_REQUEST_ID = "com.jasgcorp.ids.gcm.KEY_REQUEST_ID";

    /**
     * solicita al usuario que sincronice, segun el SERVER_USER_ID que se reciba en la solicitud
     */
    public static final String KEY_REQUEST_SYNC = "com.jasgcorp.ids.gcm.KEY_REQUEST_SYNC";

    /**
     * solicita a la aplicacion que remueva el auth_token del usuario, segun el SERVER_USER_ID que
     * se reciba en la solicitud
     */
    public static final String KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN =
            "com.jasgcorp.ids.gcm.KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN";

    /**
     * SERVER_USER_ID unico de cada usuario
     */
    public static final String KEY_PARAM_SERVER_USER_ID = "com.jasgcorp.ids.gcm.KEY_PARAM_SERVER_USER_ID";

    /**
     * solicita a la aplicacion que realice una consulta a la base de datos, segun el SERVER_USER_ID
     * que se reciba en la solicitud
     */
	public static final String KEY_REQUEST_SQL_QUERY = "com.jasgcorp.ids.gcm.KEY_REQUEST_SQL_QUERY";

    /**
     * consulta sql que se realizara en la base de datos
     */
	public static final String KEY_PARAM_SQL_QUERY = "com.jasgcorp.ids.gcm.KEY_PARAM_SQL_QUERY";

    /**
     * solicita a la aplicacion la version de la aplicacion que se encuentra instalada
     */
    public static final String KEY_REQUEST_INSTALLED_APP_VERSION =
            "com.jasgcorp.ids.gcm.KEY_REQUEST_INSTALLED_APP_VERSION";

    /**
     * solicita a la aplicacion la version de Android que se encuentra instalada en el dispositivo
     */
    public static final String KEY_REQUEST_ANDROID_API_VERSION =
            "com.jasgcorp.ids.gcm.KEY_REQUEST_ANDROID_API_VERSION";

    /**
     * solicita a la aplicacion la informacion del archivo, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_REQUEST_FILE_INFO = "com.jasgcorp.ids.gcm.KEY_REQUEST_FILE_INFO";

    /**
     * solicita a la aplicacion que elimine un archivo, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_REQUEST_DELETE_FILE = "com.jasgcorp.ids.gcm.KEY_REQUEST_DELETE_FILE";

    /**
     * path de un archivo en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no
     */
    public static final String KEY_PARAM_FILE_PATH = "com.jasgcorp.ids.gcm.KEY_PARAM_FILE_PATH";

    /**
     * solicita a la aplicacion los nombres de los archivo que se encuentra en una carpeta,
     * segun el SERVER_USER_ID que se reciba en la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_FILES_NAME_BY_FOLDER =
            "com.jasgcorp.ids.gcm.KEY_REQUEST_FILES_NAME_BY_FOLDER";

    /**
     * solicita a la aplicacion la informacion de la carpeta, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_FOLDER_INFO = "com.jasgcorp.ids.gcm.KEY_REQUEST_FOLDER_INFO";

    /**
     * solicita a la aplicacion que elimine una carpeta, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_DELETE_FOLDER = "com.jasgcorp.ids.gcm.KEY_REQUEST_DELETE_FOLDER";

    /**
     * path de una carpeta en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no.
     */
    public static final String KEY_PARAM_FOLDER_PATH = "com.jasgcorp.ids.gcm.KEY_PARAM_FOLDER_PATH";


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
                    if(action!=null && extras.containsKey(KEY_REQUEST_ID)) {
                        if(extras.getInt(KEY_REQUEST_ID)>0){
                            int requestId = extras.getInt(KEY_REQUEST_ID);
                            if(action.equals(KEY_REQUEST_SYNC)
                                    && extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                try {
                                    String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
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
                                                sendResponseToServer(getApplicationContext(), requestId,
                                                        "user: "+userAccount.name+", synchronization initialized.", null);
                                            }else{
                                                throw new Exception("the synchronization is active.");
                                            }
                                        }else{
                                            throw new Exception("userAccount is null.");
                                        }
                                    }else{
                                        throw new Exception("serverUserId is null.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_SYNC) " +
                                                    "&& extras.containsKey(KEY_PARAM_SERVER_USER_ID)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN)
                                    && extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                try {
                                    String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
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
                                            sendResponseToServer(getApplicationContext(), requestId,
                                                    "user: "+userAccount.name+", authentication token invalidated.", null);
                                        }else{
                                            throw new Exception("userAccount is null.");
                                        }
                                    }else{
                                        throw new Exception("serverUserId is null.");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN) " +
                                                    "&& extras.containsKey(KEY_PARAM_SERVER_USER_ID)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_SQL_QUERY)
                                    && extras.containsKey(KEY_PARAM_SQL_QUERY)){
                                try {
                                    if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                        if (serverUserId != null) {
                                            User user = ApplicationUtilities
                                                    .getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                            if (user != null) {

                                                //TODO: enviar respuesta al servidor
                                            } else {
                                                throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        //TODO: enviar respuesta al servidor
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_SQL_QUERY) " +
                                                    "&& extras.containsKey(KEY_PARAM_SQL_QUERY)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)){
                                try {
                                    PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                    if(packageInfo!=null){
                                        sendResponseToServer(getApplicationContext(),
                                                requestId, "versionName: " + packageInfo.versionName +
                                                        ", versionCode: "+packageInfo.versionCode+".", null);
                                    }else{
                                        throw new Exception("packageInfo is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_ANDROID_API_VERSION)){
                                try {
                                    sendResponseToServer(getApplicationContext(),
                                            requestId, "androidApiVersion: " + android.os.Build.VERSION.SDK_INT + ".", null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_ANDROID_API_VERSION)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_FILE_INFO)
                                    && extras.containsKey(KEY_PARAM_FILE_PATH)){
                                try {
                                    String filePath = extras.getString(KEY_PARAM_FILE_PATH);
                                    if(filePath!=null){
                                        if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                            if(serverUserId!=null){
                                                User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                if (user!=null) {
                                                    File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                            + File.separator + user.getUserName(), filePath);
                                                    if(file.exists()){
                                                        sendResponseToServer(getApplicationContext(),
                                                                requestId, "file: \"" + file.getAbsolutePath() + "\", "+getFileInfo(file), null);
                                                    }else{
                                                        throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                                    }
                                                } else {
                                                    throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                }
                                            }else{
                                                throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                            }
                                        }else{
                                            File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                            if(file.exists()){
                                                sendResponseToServer(getApplicationContext(),
                                                        requestId, "file: \"" + file.getAbsolutePath() + "\", "+getFileInfo(file), null);
                                            }else{
                                                throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                            }
                                        }
                                    }else{
                                        throw new Exception("extras.getString(KEY_PARAM_FILE_PATH) is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_FILE_INFO) " +
                                                    "&& extras.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_DELETE_FILE)
                                && extras.containsKey(KEY_PARAM_FILE_PATH)){
                                try {
                                    String filePath = extras.getString(KEY_PARAM_FILE_PATH);
                                    if(filePath!=null){
                                        if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                            if(serverUserId!=null){
                                                User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                if (user!=null) {
                                                    File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                            + File.separator + user.getUserName(), filePath);
                                                    if(file.exists()){
                                                        if(file.delete()){
                                                            sendResponseToServer(getApplicationContext(),
                                                                    requestId, "file: \"" + file.getAbsolutePath() + "\" deleted successfully.", null);
                                                        }else{
                                                            throw new Exception("file: \"" + file.getAbsolutePath() + "\" was not deleted.");
                                                        }
                                                    }else{
                                                        throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                                    }
                                                } else {
                                                    throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                }
                                            }else{
                                                throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                            }
                                        }else{
                                            File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                            if(file.exists()){
                                                if(file.delete()){
                                                    sendResponseToServer(getApplicationContext(),
                                                            requestId, "file: \"" + file.getAbsolutePath() + "\" deleted successfully.", null);
                                                }else{
                                                    throw new Exception("file: \"" + file.getAbsolutePath() + "\" was not deleted.");
                                                }
                                            }else{
                                                throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                            }
                                        }
                                    }else{
                                        throw new Exception("extras.getString(KEY_PARAM_FILE_PATH) is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_DELETE_FILE) " +
                                                    "&& extras.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER)
                                    && extras.containsKey(KEY_PARAM_FOLDER_PATH)){
                                try {
                                    String folderPath = extras.getString(KEY_PARAM_FOLDER_PATH);
                                    if(folderPath!=null){
                                        if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                                String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                                if(serverUserId!=null){
                                                    User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                    if (user!=null) {
                                                        File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                                + File.separator + user.getUserName(), folderPath);
                                                        if(folder.exists()){
                                                            sendResponseToServer(getApplicationContext(),
                                                                    requestId, "folder: \"" + folder.getAbsolutePath() +
                                                                            "\", files: "+listFilesByFolder(folder), null);
                                                        }else{
                                                            throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                        }
                                                    } else {
                                                        throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                    }
                                                }else{
                                                    throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                                }
                                            }else{
                                                throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                            }
                                        }else{
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                sendResponseToServer(getApplicationContext(),
                                                        requestId, "folder: \"" + folder.getAbsolutePath() +
                                                                "\", files: "+listFilesByFolder(folder), null);
                                            }else{
                                                throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                            }
                                        }
                                    }else{
                                        throw new Exception("extras.getString(KEY_PARAM_FOLDER_PATH) is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER) " +
                                                    "&& extras.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_FOLDER_INFO)
                                    && extras.containsKey(KEY_PARAM_FOLDER_PATH)){
                                try {
                                    String folderPath = extras.getString(KEY_PARAM_FOLDER_PATH);
                                    if(folderPath!=null){
                                        if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                            if(serverUserId!=null){
                                                User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                if (user!=null) {
                                                    File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                            + File.separator + user.getUserName(), folderPath);
                                                    if(folder.exists()){
                                                        sendResponseToServer(getApplicationContext(),
                                                                requestId, "folder: \"" + folder.getAbsolutePath() + "\", "+getFolderInfo(folder), null);
                                                    }else{
                                                        throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                    }
                                                } else {
                                                    throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                }
                                            }else{
                                                throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                            }
                                        }else{
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                sendResponseToServer(getApplicationContext(),
                                                        requestId, "folder: \"" + folder.getAbsolutePath() + "\", "+getFolderInfo(folder), null);
                                            }else{
                                                throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                            }
                                        }
                                    }else{
                                        throw new Exception("extras.getString(KEY_PARAM_FOLDER_PATH) is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_FOLDER_INFO) " +
                                                    "&& extras.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                                }
                            }else if(action.equals(KEY_REQUEST_DELETE_FOLDER)
                                    && extras.containsKey(KEY_PARAM_FOLDER_PATH)){
                                try {
                                    String folderPath = extras.getString(KEY_PARAM_FOLDER_PATH);
                                    if(folderPath!=null){
                                        if(extras.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            String serverUserId = extras.getString(KEY_PARAM_SERVER_USER_ID);
                                            if(serverUserId!=null){
                                                User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                if (user!=null) {
                                                    File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                            + File.separator + user.getUserName(), folderPath);
                                                    if(folder.exists()){
                                                        if(folder.delete()){
                                                            sendResponseToServer(getApplicationContext(),
                                                                    requestId, "folder: \"" + folder.getAbsolutePath() + "\" deleted successfully.", null);
                                                        }else{
                                                            throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" was not deleted.");
                                                        }
                                                    }else{
                                                        throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                    }
                                                } else {
                                                    throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                }
                                            }else{
                                                throw new Exception("extras.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is not null but user is null.");
                                            }
                                        }else{
                                            File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                            if(folder.exists()){
                                                if(folder.delete()){
                                                    sendResponseToServer(getApplicationContext(),
                                                            requestId, "folder: \"" + folder.getAbsolutePath() + "\" deleted successfully.", null);
                                                }else{
                                                    throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" was not deleted.");
                                                }
                                            }else{
                                                throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                            }
                                        }
                                    }else{
                                        throw new Exception("extras.getString(KEY_PARAM_FOLDER_PATH) is null");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    sendResponseToServer(getApplicationContext(), requestId,
                                            "action.equals(KEY_REQUEST_DELETE_FOLDER) " +
                                                    "&& extras.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                                }
                            }else{
                                sendResponseToServer(getApplicationContext(), requestId, null, "action no es compatible con ninguna de las opciones");
                            }
                        }else{
                            sendResponseToServer(getApplicationContext(), -1, null, "KEY_REQUEST_ID<=0");
                        }
                    }else{
                        if(action==null && !extras.containsKey(KEY_REQUEST_ID)){
                            sendResponseToServer(getApplicationContext(), -1, null, "action==null && !extras.containsKey(KEY_REQUEST_ID)");
                        }else if (action==null){
                            sendResponseToServer(getApplicationContext(), -1, null, "action==null");
                        }else{
                            sendResponseToServer(getApplicationContext(), -1, null, "!extras.containsKey(KEY_REQUEST_ID)");
                        }
                    }
	        	}else{
                    sendResponseToServer(getApplicationContext(), -1, null, "!extras.containsKey(\"action\")");
                }
	        }else{
                sendResponseToServer(getApplicationContext(), -1, "messageType: "+String.valueOf(messageType),
                        "!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)");
	        }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private String getFileInfo(File file){
        return "fileSize: "+getReadableSize(getFolderSize(file));
    }

    private String getFolderInfo(File folder){
        return "numberOfFiles: "+folder.list().length+", folderSize: "+getReadableSize(getFolderSize(folder));
    }

    public static long getFolderSize(File file) {
        long size;
        if (file.isDirectory()) {
            size = 0;
            for (File child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }

    public static String getReadableSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
     *
     * @param context
     * @param requestId
     * @param response
     * @param errorMessage
     */
    private void sendResponseToServer(Context context, int requestId, String response, String errorMessage){
        User user = Utils.getCurrentUser(getApplicationContext());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        parameters.put("serverUserId", user.getServerUserId());
        parameters.put("userGroup", user.getUserGroup());
        parameters.put("userName", user.getUserName());
        parameters.put("authToken", user.getAuthToken());
        parameters.put("appVersion", Utils.getAppVersionName(context));
        parameters.put("requestId", requestId);
        parameters.put("response", response);
        parameters.put("errorMessage", errorMessage);
        ConsumeWebService a = new ConsumeWebService(context,
                user.getServerAddress(),
                "/IntelligentDataSynchronizer/services/ManageUserResponse?wsdl",
                "insertMessageResponse",
                "urn:insertMessageResponse",
                parameters);
        try{
            a.getWSResponse();
        } catch (ConnectException e){
            Log.e(TAG, "ConnectException");
            e.printStackTrace();
        } catch(SocketTimeoutException e){
            Log.e(TAG, "SocketTimeoutException");
            e.printStackTrace();
        } catch (SocketException e){
            Log.e(TAG, "SocketException");
            e.printStackTrace();
        } catch (IOException e){
            Log.e(TAG, "IOException");
            e.printStackTrace();
        } catch (Exception e){
            Log.e(TAG, "Exception");
            e.printStackTrace();
        }
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
}

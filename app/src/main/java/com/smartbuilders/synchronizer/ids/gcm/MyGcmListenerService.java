/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smartbuilders.synchronizer.ids.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.SynchronizerContentProvider;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;
import com.smartbuilders.synchronizer.ids.utils.DataBaseUtilities;
import com.smartbuilders.synchronizer.ids.utils.ServerUtilities;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.synchronizer.ids.data.ServerAddressBackupDB;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Recibe los mensajes push con solicitudes enviadas desde el servidor
 */
public class MyGcmListenerService extends GcmListenerService {

    /**
     * representa el ID de la solicitud realizada por el servidor
     */
    public static final String KEY_REQUEST_ID = "com.smartbuilders.ids.gcm.KEY_REQUEST_ID";

    /**
     * solicita al usuario que sincronice, segun el SERVER_USER_ID que se reciba en la solicitud
     */
    public static final String KEY_REQUEST_SYNC = "com.smartbuilders.ids.gcm.KEY_REQUEST_SYNC";

    /**
     * indica las tablas que se sincronizaran
     */
    public static final String KEY_PARAM_TABLES_TO_SYNC = "com.smartbuilders.ids.gcm.KEY_PARAM_TABLES_TO_SYNC";

    /**
     * solicita a la aplicacion que remueva el auth_token del usuario, segun el SERVER_USER_ID que
     * se reciba en la solicitud
     */
    public static final String KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN =
            "com.smartbuilders.ids.gcm.KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN";

    /**
     * SERVER_USER_ID unico de cada usuario
     */
    public static final String KEY_PARAM_SERVER_USER_ID = "com.smartbuilders.ids.gcm.KEY_PARAM_SERVER_USER_ID";

    /**
     * solicita a la aplicacion que realice una consulta a la base de datos, segun el SERVER_USER_ID
     * que se reciba en la solicitud
     */
    public static final String KEY_REQUEST_QUERY_DATA_BASE = "com.smartbuilders.ids.gcm.KEY_REQUEST_QUERY_DATA_BASE";

    /**
     * consulta sql que se realizara en la base de datos
     */
    public static final String KEY_PARAM_SQL_QUERY = "com.smartbuilders.ids.gcm.KEY_PARAM_SQL_QUERY";

    /**
     * solicita a la aplicacion la version de la aplicacion que se encuentra instalada
     */
    public static final String KEY_REQUEST_INSTALLED_APP_VERSION =
            "com.smartbuilders.ids.gcm.KEY_REQUEST_INSTALLED_APP_VERSION";

    /**
     * solicita a la aplicacion la version de Android que se encuentra instalada en el dispositivo
     */
    public static final String KEY_REQUEST_ANDROID_API_VERSION =
            "com.smartbuilders.ids.gcm.KEY_REQUEST_ANDROID_API_VERSION";

    /**
     * solicita a la aplicacion la informacion del archivo, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_REQUEST_FILE_INFO = "com.smartbuilders.ids.gcm.KEY_REQUEST_FILE_INFO";

    /**
     * solicita a la aplicacion que elimine un archivo, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FILE_PATH del archivo
     */
    public static final String KEY_REQUEST_DELETE_FILE = "com.smartbuilders.ids.gcm.KEY_REQUEST_DELETE_FILE";

    /**
     * path de un archivo en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no
     */
    public static final String KEY_PARAM_FILE_PATH = "com.smartbuilders.ids.gcm.KEY_PARAM_FILE_PATH";

    /**
     * solicita a la aplicacion los nombres de los archivo que se encuentra en una carpeta,
     * segun el SERVER_USER_ID que se reciba en la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_FILES_NAME_BY_FOLDER =
            "com.smartbuilders.ids.gcm.KEY_REQUEST_FILES_NAME_BY_FOLDER";

    /**
     * solicita a la aplicacion la informacion de la carpeta, segun el SERVER_USER_ID que se reciba en
     * la solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_FOLDER_INFO = "com.smartbuilders.ids.gcm.KEY_REQUEST_FOLDER_INFO";

    /**
     * solicita a la aplicacion que elimine una carpeta, segun el SERVER_USER_ID que se reciba en la
     * solicitud y el FOLDER_PATH de la carpeta.
     */
    public static final String KEY_REQUEST_DELETE_FOLDER = "com.smartbuilders.ids.gcm.KEY_REQUEST_DELETE_FOLDER";

    /**
     * path de una carpeta en especifico, este variara su ruta inicial segun se use el SERVER_USER_ID
     * en la consulta, o no.
     */
    public static final String KEY_PARAM_FOLDER_PATH = "com.smartbuilders.ids.gcm.KEY_PARAM_FOLDER_PATH";

    /**
     * solicita a la aplicacion que modifique la direccion IP del serrvidor segun el valor de
     * KEY_PARAM_SERVER_ADDRESS
     */
    public static final String KEY_REQUEST_CHANGE_SERVER_ADDRESS = "com.smartbuilders.ids.gcm.KEY_REQUEST_CHANGE_SERVER_ADDRESS";

    /**
     * parametro que representa la direccion IP del servidor
     */
    public static final String KEY_PARAM_SERVER_ADDRESS = "com.smartbuilders.ids.gcm.KEY_PARAM_SERVER_ADDRESS";

    /**
     * le notifica a la aplicacion que el servidor esta disponible para sincronizar
     */
    public static final String KEY_NOTIFY_SERVER_IS_ALIVE = "com.smartbuilders.ids.gcm.KEY_NOTIFY_SERVER_IS_ALIVE";

    private static final String TAG = MyGcmListenerService.class.getSimpleName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            Log.d(TAG, "onMessageReceived("+from+", Bundle data)");
            // normal downstream message.
            //Run the Sync Adapter When Server Data Changes
            if(data.containsKey("action") && data.containsKey("requestMethodName")){
                String action = data.getString("action");
                String requestMethodName = data.getString("requestMethodName");
                if(action!=null && data.containsKey(KEY_REQUEST_ID)) {
                    int requestId = 0;
                    try {
                        requestId = Integer.valueOf(data.getString(KEY_REQUEST_ID));
                    } catch (NumberFormatException e) {
                        sendResponseToServer(this, requestMethodName, requestId, null, e.getMessage());
                    }
                    if(requestId>0){
                        if(action.equals(KEY_REQUEST_SYNC)){
                            try {
                                AccountManager mAccountManager = AccountManager.get(this);
                                for(Account account : mAccountManager.getAccountsByType(getString(R.string.authenticator_account_type))){
                                    if (!ApplicationUtilities.isSyncActive(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)) {
                                        if(ApplicationUtilities.appRequireFullSync(this, account)){
                                            ApplicationUtilities.initSyncByAccount(this, account);
                                            sendResponseToServer(this, requestMethodName, requestId,
                                                    "user: "+account.name+", App require full sync, Full Synchronization initialized.", null);
                                        }else if(!ApplicationUtilities.appRequireInitialLoad(this, account)){
                                            //se manda a correr la sincronizacion de manera inmediata
                                            if (data.containsKey(KEY_PARAM_TABLES_TO_SYNC) && data.getString(KEY_PARAM_TABLES_TO_SYNC)!=null) {
                                                getContentResolver()
                                                        .query(SynchronizerContentProvider.SYNC_DATA_FROM_SERVER_URI.buildUpon()
                                                                .appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID,
                                                                        AccountManager.get(this).getUserData(account, AccountGeneral.USERDATA_USER_ID))
                                                                .appendQueryParameter(SynchronizerContentProvider.KEY_TABLES_TO_SYNC,
                                                                        data.getString(KEY_PARAM_TABLES_TO_SYNC)).build(),
                                                                null, null, null, null);
                                                sendResponseToServer(this, requestMethodName, requestId,
                                                        "user: "+account.name+", tablesToSync: "+data.getString(KEY_PARAM_TABLES_TO_SYNC)+
                                                                ", synchronization initialized.", null);
                                            } else {
                                                ApplicationUtilities.initSyncByAccount(this, account);
                                                sendResponseToServer(this, requestMethodName, requestId,
                                                        "user: "+account.name+", Full Synchronization initialized.", null);
                                            }
                                        }else{
                                            throw new Exception("App require initial load.");
                                        }
                                    }else{
                                        throw new Exception("the synchronization is active.");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_SYNC)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN)
                                && data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                            try {
                                if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                    AccountManager accountManager = AccountManager.get(this);
                                    Account userAccount = ApplicationUtilities
                                            .getAccountByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                    if(userAccount!=null){
                                        accountManager.invalidateAuthToken(getString(R.string.authenticator_account_type),
                                                accountManager.peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
                                        accountManager.invalidateAuthToken(getString(R.string.authenticator_account_type),
                                                accountManager.peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
                                        sendResponseToServer(this, requestMethodName, requestId,
                                                "user: "+userAccount.name+", authentication token invalidated.", null);
                                    }else{
                                        throw new Exception("userAccount is null.");
                                    }
                                }else{
                                    throw new Exception("serverUserId is null.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN) " +
                                                "&& data.containsKey(KEY_PARAM_SERVER_USER_ID)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_QUERY_DATA_BASE)
                                && data.containsKey(KEY_PARAM_SQL_QUERY)){
                            try {
                                User user = null;
                                if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                    if (data.getString(KEY_PARAM_SERVER_USER_ID) != null) {
                                        user = ApplicationUtilities
                                                .getUserByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                        if (user == null) {
                                            throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                        }
                                    }else{
                                        throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                    }
                                }
                                Object result = DataBaseUtilities
                                        .getJsonBase64CompressedQueryResult(this, user, data.getString(KEY_PARAM_SQL_QUERY));
                                if (result instanceof String) {
                                    sendResponseToServer(this, requestMethodName, requestId, (String) result, null);
                                } else if (result instanceof Exception) {
                                    throw (Exception) result;
                                } else {
                                    throw new Exception("result is null for sql: "+String.valueOf(data.getString(KEY_PARAM_SQL_QUERY)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_QUERY_DATA_BASE) " +
                                                "&& data.containsKey(KEY_PARAM_SQL_QUERY)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)){
                            try {
                                PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                                if(packageInfo!=null){
                                    sendResponseToServer(this, requestMethodName,
                                            requestId, "versionName: " + packageInfo.versionName +
                                                    ", versionCode: "+packageInfo.versionCode+".", null);
                                }else{
                                    throw new Exception("packageInfo is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_ANDROID_API_VERSION)){
                            try {
                                sendResponseToServer(this, requestMethodName,
                                        requestId, "androidApiVersion: " + android.os.Build.VERSION.SDK_INT + ".", null);
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_ANDROID_API_VERSION)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FILE_INFO)
                                && data.containsKey(KEY_PARAM_FILE_PATH)){
                            try {
                                if(data.getString(KEY_PARAM_FILE_PATH)!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                            if (user!=null) {
                                                File file = new File(this.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), data.getString(KEY_PARAM_FILE_PATH));
                                                if(file.exists()){
                                                    sendResponseToServer(this, requestMethodName,
                                                            requestId, "file: \"" + file.getAbsolutePath() + "\", "+getFileInfo(file), null);
                                                }else{
                                                    throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File file = new File(this.getExternalFilesDir(null), data.getString(KEY_PARAM_FILE_PATH));
                                        if(file.exists()){
                                            sendResponseToServer(this, requestMethodName,
                                                    requestId, "file: \"" + file.getAbsolutePath() + "\", "+getFileInfo(file), null);
                                        }else{
                                            throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_FILE_PATH) is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FILE_INFO) " +
                                                "&& data.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_DELETE_FILE)
                                && data.containsKey(KEY_PARAM_FILE_PATH)){
                            try {
                                if(data.getString(KEY_PARAM_FILE_PATH)!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                            if (user!=null) {
                                                File file = new File(this.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), data.getString(KEY_PARAM_FILE_PATH));
                                                if(file.exists()){
                                                    if(file.delete()){
                                                        sendResponseToServer(this, requestMethodName,
                                                                requestId, "file: \"" + file.getAbsolutePath() + "\" deleted successfully.", null);
                                                    }else{
                                                        throw new Exception("file: \"" + file.getAbsolutePath() + "\" was not deleted.");
                                                    }
                                                }else{
                                                    throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File file = new File(this.getExternalFilesDir(null), data.getString(KEY_PARAM_FILE_PATH));
                                        if(file.exists()){
                                            if(file.delete()){
                                                sendResponseToServer(this, requestMethodName,
                                                        requestId, "file: \"" + file.getAbsolutePath() + "\" deleted successfully.", null);
                                            }else{
                                                throw new Exception("file: \"" + file.getAbsolutePath() + "\" was not deleted.");
                                            }
                                        }else{
                                            throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_FILE_PATH) is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_DELETE_FILE) " +
                                                "&& data.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                if(data.getString(KEY_PARAM_FOLDER_PATH)!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(this,
                                                    data.getString(KEY_PARAM_SERVER_USER_ID));
                                            if (user!=null) {
                                                File folder = new File(this.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), data.getString(KEY_PARAM_FOLDER_PATH));
                                                if(folder.exists()){
                                                    sendResponseToServer(this, requestMethodName,
                                                            requestId, "folder: \"" + folder.getAbsolutePath() +
                                                                    "\", files: "+String.valueOf(listFilesByFolder(folder)), null);
                                                }else{
                                                    throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File folder = new File(this.getExternalFilesDir(null), data.getString(KEY_PARAM_FOLDER_PATH));
                                        if(folder.exists()){
                                            sendResponseToServer(this, requestMethodName,
                                                    requestId, "folder: \"" + folder.getAbsolutePath() +
                                                            "\", files: "+String.valueOf(listFilesByFolder(folder)), null);
                                        }else{
                                            throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_FOLDER_PATH) is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FOLDER_INFO)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                if(data.getString(KEY_PARAM_FOLDER_PATH)!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                            if (user!=null) {
                                                File folder = new File(this.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), data.getString(KEY_PARAM_FOLDER_PATH));
                                                if(folder.exists()){
                                                    sendResponseToServer(this, requestMethodName,
                                                            requestId, "folder: \"" + folder.getAbsolutePath() + "\", "+getFolderInfo(folder), null);
                                                }else{
                                                    throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File folder = new File(this.getExternalFilesDir(null), data.getString(KEY_PARAM_FOLDER_PATH));
                                        if(folder.exists()){
                                            sendResponseToServer(this, requestMethodName,
                                                    requestId, "folder: \"" + folder.getAbsolutePath() + "\", "+getFolderInfo(folder), null);
                                        }else{
                                            throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_FOLDER_PATH) is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FOLDER_INFO) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_DELETE_FOLDER)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                String folderPath = data.getString(KEY_PARAM_FOLDER_PATH);
                                if(folderPath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.getString(KEY_PARAM_SERVER_USER_ID)!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(this, data.getString(KEY_PARAM_SERVER_USER_ID));
                                            if (user!=null) {
                                                File folder = new File(this.getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), folderPath);
                                                if(folder.exists()){
                                                    if(folder.delete()){
                                                        sendResponseToServer(this, requestMethodName,
                                                                requestId, "folder: \"" + folder.getAbsolutePath() + "\" deleted successfully.", null);
                                                    }else{
                                                        throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" was not deleted.");
                                                    }
                                                }else{
                                                    throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+data.getString(KEY_PARAM_SERVER_USER_ID)+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is not null but user is null.");
                                        }
                                    }else{
                                        File folder = new File(this.getExternalFilesDir(null), folderPath);
                                        if(folder.exists()){
                                            if(folder.delete()){
                                                sendResponseToServer(this, requestMethodName,
                                                        requestId, "folder: \"" + folder.getAbsolutePath() + "\" deleted successfully.", null);
                                            }else{
                                                throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" was not deleted.");
                                            }
                                        }else{
                                            throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_FOLDER_PATH) is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_DELETE_FOLDER) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_CHANGE_SERVER_ADDRESS)
                                && data.containsKey(KEY_PARAM_SERVER_ADDRESS)) {
                            try {
                                if(data.get(KEY_PARAM_SERVER_ADDRESS)!=null){
                                    //Se almacena la direccion como una nueva direccion de backup
                                    (new ServerAddressBackupDB(this))
                                            .addServerAddressBackup(data.get(KEY_PARAM_SERVER_ADDRESS).toString());

                                    AccountManager accountManager = AccountManager.get(this);

                                    for(Account account : accountManager.getAccountsByType(getString(R.string.authenticator_account_type))){
                                        if(ServerUtilities.isServerAvailableByAddress(data.get(KEY_PARAM_SERVER_ADDRESS).toString())){
                                            accountManager.setUserData(account,
                                                    AccountGeneral.USERDATA_SERVER_ADDRESS,
                                                    data.get(KEY_PARAM_SERVER_ADDRESS).toString());
                                            sendResponseToServer(this, requestMethodName, requestId,
                                                    "Server address changed, new address: "+data.get(KEY_PARAM_SERVER_ADDRESS).toString(), null);
                                        }else{
                                            throw new Exception("Server is not available with address: "+data.get(KEY_PARAM_SERVER_ADDRESS).toString());
                                        }
                                    }
                                }else{
                                    throw new Exception("data.getString(KEY_PARAM_SERVER_ADDRESS) is null or data.getString(KEY_PARAM_SERVER_USER_ID) is null");
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_CHANGE_SERVER_ADDRESS)" +
                                                " && data.containsKey(KEY_PARAM_SERVER_ADDRESS)", e.getMessage());
                            }
                        }else if(action.equals(KEY_NOTIFY_SERVER_IS_ALIVE)){
                            try {
                                AccountManager accountManager = AccountManager.get(this);
                                for(Account account : accountManager.getAccountsByType(getString(R.string.authenticator_account_type))){
                                    if (!ApplicationUtilities.isSyncActive(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)) {
                                        Date lastSuccessFullySyncTime = ApplicationUtilities.getLastFullSyncTime(this, account);
                                        if(lastSuccessFullySyncTime!=null){
                                            if(((System.currentTimeMillis() - lastSuccessFullySyncTime.getTime())/1000) >= Utils.getSyncPeriodicityFromPreferences(this)) {
                                                ApplicationUtilities.initSyncByAccount(this, account);
                                            }else{
                                                ApplicationUtilities.initSyncDataWithServerService(this,
                                                        accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                                                throw new Exception("El tiempo de la ultima sincronizacion es menor al periodo de sincronizacion.");
                                            }
                                        }else{
                                            ApplicationUtilities.initSyncByAccount(this, account);
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                                sendResponseToServer(this, requestMethodName, requestId,
                                        "action.equals(KEY_NOTIFY_SERVER_IS_ALIVE)", e.getMessage());
                            }
                        }else{
                            sendResponseToServer(this, requestMethodName, requestId, null, "action no es compatible con ninguna de las opciones");
                        }
                    }else{
                        sendResponseToServer(this, requestMethodName, -1, null, "KEY_REQUEST_ID<=0");
                    }
                }else{
                    if(action==null && !data.containsKey(KEY_REQUEST_ID)){
                        sendResponseToServer(this, requestMethodName, -1, null, "action==null && !data.containsKey(KEY_REQUEST_ID)");
                    }else if (action==null){
                        sendResponseToServer(this, requestMethodName, -1, null, "action==null");
                    }else{
                        sendResponseToServer(this, requestMethodName, -1, null, "!data.containsKey(KEY_REQUEST_ID)");
                    }
                }
            }else{
                sendResponseToServer(this, null, -1, null, "!data.containsKey(\"action\") || !data.containsKey(\"requestMethodName\")");
            }
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        //sendNotification(data.getString("message"));
        // [END_EXCLUDE]
    }
    // [END receive_message]

    private String getFileInfo(File file){
        return "fileSize: "+getReadableSize(getFolderSize(file));
    }

    private String getFolderInfo(File folder){
        return "numberOfFiles: "+folder.list().length+", folderSize: "+getReadableSize(getFolderSize(folder));
    }

    private static long getFolderSize(File file) {
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

    private static String getReadableSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
     *
     * @param context
     * @param requestMethodName
     * @param requestId
     * @param response
     * @param errorMessage
     */
    private void sendResponseToServer(Context context, String requestMethodName, int requestId,
                                      String response, String errorMessage){
        User user = Utils.getCurrentUser(context);
        if (user!=null) {
            LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
            parameters.put("serverUserId", user.getServerUserId());
            parameters.put("userGroup", user.getUserGroup());
            parameters.put("userName", user.getUserName());
            parameters.put("authToken", user.getAuthToken());
            parameters.put("appVersion", Utils.getAppVersionName(context));
            parameters.put("requestMethodName", requestMethodName);
            parameters.put("requestId", requestId);
            parameters.put("response", response);
            parameters.put("errorMessage", errorMessage);
            ConsumeWebService a = new ConsumeWebService(context,
                    user.getServerAddress(),
                    "/IntelligentDataSynchronizer/services/ManageUserResponse?wsdl",
                    "insertMessageResponse",
                    "urn:insertMessageResponse",
                    parameters,
                    Parameter.getConnectionTimeOutValue(context, user));
            try{
                a.getWSResponse();
            } catch (Exception e){
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "sendResponseToServer(...) - user is null");
        }
    }

    private String listFilesByFolder(final File folder) {
        if (folder!=null) {
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
        return null;
    }
}
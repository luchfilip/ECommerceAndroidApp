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
package com.jasgcorp.ids.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.jasgcorp.ids.utils.DataBaseRemoteManagement;
import com.smartbuilders.smartsales.ecommerceandroidapp.MainActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.session.Parameter;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * Recibe los mensajes push con solicitudes enviadas desde el servidor
 */
public class MyGcmListenerService extends GcmListenerService {

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
    public static final String KEY_REQUEST_QUERY_DATA_BASE = "com.jasgcorp.ids.gcm.KEY_REQUEST_QUERY_DATA_BASE";

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
                        sendResponseToServer(getApplicationContext(), requestMethodName, requestId, null, e.getMessage());
                    }
                    if(requestId>0){
                        if(action.equals(KEY_REQUEST_SYNC)
                                && data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                            try {
                                String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                if (serverUserId != null) {
                                    Account userAccount = ApplicationUtilities
                                            .getAccountByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                    if(userAccount!=null){
                                        if (!ApplicationUtilities.isSyncActive(this, userAccount)) {
                                            ApplicationUtilities.initSyncByAccount(this, userAccount);
                                            sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_SYNC) " +
                                                "&& data.containsKey(KEY_PARAM_SERVER_USER_ID)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN)
                                && data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                            try {
                                String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                if(serverUserId!=null){
                                    AccountManager accountManager = AccountManager.get(this);
                                    Account userAccount = ApplicationUtilities
                                            .getAccountByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                    if(userAccount!=null){
                                        accountManager.invalidateAuthToken(getString(R.string.authenticator_account_type),
                                                accountManager.peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
                                        accountManager.invalidateAuthToken(getString(R.string.authenticator_account_type),
                                                accountManager.peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
                                        sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                                "user: "+userAccount.name+", authentication token invalidated.", null);
                                    }else{
                                        throw new Exception("userAccount is null.");
                                    }
                                }else{
                                    throw new Exception("serverUserId is null.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_INVALIDATE_USER_AUTH_TOKEN) " +
                                                "&& data.containsKey(KEY_PARAM_SERVER_USER_ID)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_QUERY_DATA_BASE)
                                && data.containsKey(KEY_PARAM_SQL_QUERY)){
                            try {
                                User user = null;
                                if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                    String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                    if (serverUserId != null) {
                                        user = ApplicationUtilities
                                                .getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                        if (user == null) {
                                            throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                        }
                                    }else{
                                        throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                    }
                                }
                                Object result = DataBaseRemoteManagement
                                        .getJsonBase64CompressedQueryResult(getApplicationContext(), user, 1000, data.getString(KEY_PARAM_SQL_QUERY));
                                if (result instanceof String) {
                                    sendResponseToServer(getApplicationContext(), requestMethodName, requestId, (String) result, null);
                                } else if (result instanceof Exception) {
                                    throw (Exception) result;
                                } else {
                                    throw new Exception("result is null for sql: "+String.valueOf(data.getString(KEY_PARAM_SQL_QUERY)));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_QUERY_DATA_BASE) " +
                                                "&& data.containsKey(KEY_PARAM_SQL_QUERY)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)){
                            try {
                                PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                if(packageInfo!=null){
                                    sendResponseToServer(getApplicationContext(), requestMethodName,
                                            requestId, "versionName: " + packageInfo.versionName +
                                                    ", versionCode: "+packageInfo.versionCode+".", null);
                                }else{
                                    throw new Exception("packageInfo is null");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_INSTALLED_APP_VERSION)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_ANDROID_API_VERSION)){
                            try {
                                sendResponseToServer(getApplicationContext(), requestMethodName,
                                        requestId, "androidApiVersion: " + android.os.Build.VERSION.SDK_INT + ".", null);
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_ANDROID_API_VERSION)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FILE_INFO)
                                && data.containsKey(KEY_PARAM_FILE_PATH)){
                            try {
                                String filePath = data.getString(KEY_PARAM_FILE_PATH);
                                if(filePath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                        if(serverUserId!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                            if (user!=null) {
                                                File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), filePath);
                                                if(file.exists()){
                                                    sendResponseToServer(getApplicationContext(), requestMethodName,
                                                            requestId, "file: \"" + file.getAbsolutePath() + "\", "+getFileInfo(file), null);
                                                }else{
                                                    throw new Exception("file: \"" + file.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                        if(file.exists()){
                                            sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FILE_INFO) " +
                                                "&& data.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_DELETE_FILE)
                                && data.containsKey(KEY_PARAM_FILE_PATH)){
                            try {
                                String filePath = data.getString(KEY_PARAM_FILE_PATH);
                                if(filePath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                        if(serverUserId!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                            if (user!=null) {
                                                File file = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), filePath);
                                                if(file.exists()){
                                                    if(file.delete()){
                                                        sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File file = new File(getApplicationContext().getExternalFilesDir(null), filePath);
                                        if(file.exists()){
                                            if(file.delete()){
                                                sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_DELETE_FILE) " +
                                                "&& data.containsKey(KEY_PARAM_FILE_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                String folderPath = data.getString(KEY_PARAM_FOLDER_PATH);
                                if(folderPath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                            String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                            if(serverUserId!=null){
                                                User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                                if (user!=null) {
                                                    File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                            + File.separator + user.getUserName(), folderPath);
                                                    if(folder.exists()){
                                                        sendResponseToServer(getApplicationContext(), requestMethodName,
                                                                requestId, "folder: \"" + folder.getAbsolutePath() +
                                                                        "\", files: "+String.valueOf(listFilesByFolder(folder)), null);
                                                    }else{
                                                        throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                    }
                                                } else {
                                                    throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                                }
                                            }else{
                                                throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                        if(folder.exists()){
                                            sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FILES_NAME_BY_FOLDER) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_FOLDER_INFO)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                String folderPath = data.getString(KEY_PARAM_FOLDER_PATH);
                                if(folderPath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                        if(serverUserId!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                            if (user!=null) {
                                                File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), folderPath);
                                                if(folder.exists()){
                                                    sendResponseToServer(getApplicationContext(), requestMethodName,
                                                            requestId, "folder: \"" + folder.getAbsolutePath() + "\", "+getFolderInfo(folder), null);
                                                }else{
                                                    throw new Exception("folder: \"" + folder.getAbsolutePath() + "\" does not exists.");
                                                }
                                            } else {
                                                throw new Exception("serverUserId is "+serverUserId+" but user is null.");
                                            }
                                        }else{
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is null.");
                                        }
                                    }else{
                                        File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                        if(folder.exists()){
                                            sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_FOLDER_INFO) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else if(action.equals(KEY_REQUEST_DELETE_FOLDER)
                                && data.containsKey(KEY_PARAM_FOLDER_PATH)){
                            try {
                                String folderPath = data.getString(KEY_PARAM_FOLDER_PATH);
                                if(folderPath!=null){
                                    if(data.containsKey(KEY_PARAM_SERVER_USER_ID)){
                                        String serverUserId = data.getString(KEY_PARAM_SERVER_USER_ID);
                                        if(serverUserId!=null){
                                            User user = ApplicationUtilities.getUserByServerUserIdFromAccountManager(getApplicationContext(), serverUserId);
                                            if (user!=null) {
                                                File folder = new File(getApplicationContext().getExternalFilesDir(null) + File.separator + user.getUserGroup()
                                                        + File.separator + user.getUserName(), folderPath);
                                                if(folder.exists()){
                                                    if(folder.delete()){
                                                        sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                            throw new Exception("data.containsKey(KEY_PARAM_SERVER_USER_ID) but serverUserId is not null but user is null.");
                                        }
                                    }else{
                                        File folder = new File(getApplicationContext().getExternalFilesDir(null), folderPath);
                                        if(folder.exists()){
                                            if(folder.delete()){
                                                sendResponseToServer(getApplicationContext(), requestMethodName,
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
                                sendResponseToServer(getApplicationContext(), requestMethodName, requestId,
                                        "action.equals(KEY_REQUEST_DELETE_FOLDER) " +
                                                "&& data.containsKey(KEY_PARAM_FOLDER_PATH)", e.getMessage());
                            }
                        }else{
                            sendResponseToServer(getApplicationContext(), requestMethodName, requestId, null, "action no es compatible con ninguna de las opciones");
                        }
                    }else{
                        sendResponseToServer(getApplicationContext(), requestMethodName, -1, null, "KEY_REQUEST_ID<=0");
                    }
                }else{
                    if(action==null && !data.containsKey(KEY_REQUEST_ID)){
                        sendResponseToServer(getApplicationContext(), requestMethodName, -1, null, "action==null && !data.containsKey(KEY_REQUEST_ID)");
                    }else if (action==null){
                        sendResponseToServer(getApplicationContext(), requestMethodName, -1, null, "action==null");
                    }else{
                        sendResponseToServer(getApplicationContext(), requestMethodName, -1, null, "!data.containsKey(KEY_REQUEST_ID)");
                    }
                }
            }else{
                sendResponseToServer(getApplicationContext(), null, -1, null, "!data.containsKey(\"action\") || !data.containsKey(\"requestMethodName\")");
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

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
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
     * @param requestMethodName
     * @param requestId
     * @param response
     * @param errorMessage
     */
    private void sendResponseToServer(Context context, String requestMethodName, int requestId, String response, String errorMessage){
        User user = Utils.getCurrentUser(getApplicationContext());
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
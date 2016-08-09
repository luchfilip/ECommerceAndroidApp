package com.smartbuilders.ids.providers;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;

import org.codehaus.jettison.json.JSONObject;
import org.ksoap2.serialization.SoapPrimitive;

import com.smartbuilders.ids.datamanager.TablesDataSendToAndReceiveFromServer;
import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.utils.ApplicationUtilities;
import com.smartbuilders.ids.utils.ConsumeWebService;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * 
 * @author Jesus Sarco
 *
 */
public class SynchronizerContentProvider extends ContentProvider{
	
	private static final String AUTHORITY =
            "com.smartbuilders.smartsales.providers.SynchronizerContentProvider";
	
	private static final Uri CONTENT_URI 					= Uri.parse("content://"+AUTHORITY);
	
	public static final Uri START_SYNC_URI					= Uri.withAppendedPath(CONTENT_URI, "startsync");
	public static final Uri STOP_SYNC_URI 					= Uri.withAppendedPath(CONTENT_URI, "stopsync");
	public static final Uri SYNC_PROGRESS_PERCENTAGE_URI 	= Uri.withAppendedPath(CONTENT_URI, "syncprogress");
	public static final Uri USER_SIGN_UP_URI 				= Uri.withAppendedPath(CONTENT_URI, "signUp");
	public static final Uri USER_SIGN_IN_URI 				= Uri.withAppendedPath(CONTENT_URI, "signIn");
	public static final Uri USER_GET_AUTH_TOKEN_URI 		= Uri.withAppendedPath(CONTENT_URI, "getAuthToken");
	public static final Uri USER_SIGN_OUT_URI 				= Uri.withAppendedPath(CONTENT_URI, "signOut");
	public static final Uri REGISTER_GCM_ID_IN_SERVER_URI	= Uri.withAppendedPath(CONTENT_URI, "registerGcmIdInServer");
	
	public static final String KEY_USER_ID 						= "USER_ID";
	public static final String KEY_USER_NAME 					= "USER_NAME";
	public static final String KEY_USER_PASS 					= "USER_PASS";
	public static final String KEY_USER_SERVER_ADDRESS 			= "USER_SERVER_ADDRESS";
	public static final String KEY_USER_GROUP 					= "USER_GROUP";
	public static final String KEY_USER_AUTH_TOKEN 				= "USER_AUTH_TOKEN";
	public static final String KEY_USER_SAVE_DB_EXTERNAL_CARD 	= "USER_SAVE_DB_EXTERNAL_CARD";
	public static final String KEY_USER_GCM_ID 					= "USER_GCM_ID";
	public static final String KEY_USER_SYNC_STATE 				= "USER_SYNC_STATE";
	
	private static final int START 						= 1;
	private static final int STOP 						= 2;
	private static final int SYNC_PROGRESS_PERCENTAGE 	= 3;
	private static final int SIGN_UP 					= 4;
	private static final int SIGN_IN 					= 5;
    private static final int GET_AUTH_TOKEN				= 6;
	private static final int SIGN_OUT 					= 7;
	private static final int REGISTER_GCM_ID_IN_SERVER	= 8;
	
	private static final UriMatcher uriMatcher;

	private TablesDataSendToAndReceiveFromServer tableDataReceiveFromServerThread;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "startsync", START);
		uriMatcher.addURI(AUTHORITY, "stopsync", STOP);
		uriMatcher.addURI(AUTHORITY, "syncprogress", SYNC_PROGRESS_PERCENTAGE);
		uriMatcher.addURI(AUTHORITY, "signUp", SIGN_UP);
		uriMatcher.addURI(AUTHORITY, "signIn", SIGN_IN);
        uriMatcher.addURI(AUTHORITY, "getAuthToken", GET_AUTH_TOKEN);
		uriMatcher.addURI(AUTHORITY, "signOut", SIGN_OUT);
		uriMatcher.addURI(AUTHORITY, "registerGcmIdInServer", REGISTER_GCM_ID_IN_SERVER);
	}
	
	@Override
	public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(@NonNull Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(@NonNull Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Cursor query(@NonNull Uri uri, String[] projection, String selection,
						String[] selectionArgs, String sortOrder) {
		Cursor response = null;
		switch (uriMatcher.match(uri)) {
			case START:
	    		response = startSynchronization(uri);
	        break;
	        case STOP:
	        	response = stopSynchronization(uri);
	        break;
	        case SYNC_PROGRESS_PERCENTAGE:
	        	response = getSyncProgressPercentage(uri);
	        break;
	        case SIGN_UP:
	        	response = signUp(uri);
	        break;
	        case SIGN_IN:
	        	response = signIn(uri);
	        break;
            case GET_AUTH_TOKEN:
                response = getAuthToken(uri);
                break;
	        case SIGN_OUT:
	        	response = signOut(uri);
	        break;
	        case REGISTER_GCM_ID_IN_SERVER:
	        	response = registerGcmIdInServer(uri);
	        break;
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return response;
	}
	
	@Override
	public int update(@NonNull Uri uri, ContentValues values, String selection,
					  String[] selectionArgs) {
		return -1;
	}
	
	/**
	 * Inicia los hilos de envio y recepcion de datos
	 * @param uri
	 * @return
	 */
	private Cursor startSynchronization(Uri uri){
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		String errorMessage = null;
		String exceptionClass = null;
		Boolean result = false;
		if(uri.getQueryParameter(KEY_USER_ID)==null){
			throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			try {
				if (uri.getQueryParameter("tables_to_sync")!=null) {
					tableDataReceiveFromServerThread = new TablesDataSendToAndReceiveFromServer(user, getContext(), uri.getQueryParameter("tables_to_sync"));
				} else {
					tableDataReceiveFromServerThread = new TablesDataSendToAndReceiveFromServer(user, getContext());
				}
				tableDataReceiveFromServerThread.start();
				result = true;
			} catch(IllegalThreadStateException e) {
				exceptionClass = e.getClass().getName();
				e.printStackTrace();
			} catch (Exception e) {
				exceptionClass = e.getClass().getName();
				e.printStackTrace();
				if(tableDataReceiveFromServerThread!=null && tableDataReceiveFromServerThread.isAlive()){
					tableDataReceiveFromServerThread.stopSynchronization();
				}
				errorMessage = e.getMessage();
			}
		}
		cursor.addRow(new Object[]{result, errorMessage, exceptionClass});
		return cursor;
	}
	
	/**
	 * Detiene los hilos de envio y recepcion de datos
	 * @param uri
	 * @return
	 */
	private Cursor stopSynchronization(Uri uri) {
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		Boolean result = false;
		String errorMessage = null;
		String exceptionClass = null;
		if(uri.getQueryParameter(KEY_USER_ID)==null){
			throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
		}else{
			if(tableDataReceiveFromServerThread!=null && tableDataReceiveFromServerThread.isAlive()){
				tableDataReceiveFromServerThread.stopSynchronization();
			}
			
			//Esperar a que los hilos de sincronizacion se detengan
            if(tableDataReceiveFromServerThread!=null){
				while(tableDataReceiveFromServerThread.isAlive()){ }
			}
			result = true;
		}
		cursor.addRow(new Object[]{result, errorMessage, exceptionClass});
		return cursor;
	}
	
	/**
	 * Devuelve un cursor con 2 valores, 
	 * -state: porcentaje en float de la sincronizacion.
	 * -error_message: "mensaje de error, si es que hubo, sino devuelve null".
	 * @param uri
	 * @return
	 */
	private Cursor getSyncProgressPercentage(Uri uri){
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		String errorMessage = null;
		String exceptionClass = null;
		Float syncPercentage = 0F;
		if(uri.getQueryParameter(KEY_USER_ID)==null){
			throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
		}else{
			if(!tableDataReceiveFromServerThread.isAlive()){
				if(tableDataReceiveFromServerThread.getExceptionMessage()==null){
					syncPercentage = 100F;
				}else{
					errorMessage = tableDataReceiveFromServerThread.getExceptionMessage();
					exceptionClass = tableDataReceiveFromServerThread.getExceptionClass();
				}
			}else{
				if(tableDataReceiveFromServerThread.getSyncPercentage()>syncPercentage){
					syncPercentage = tableDataReceiveFromServerThread.getSyncPercentage();
				}
			}

            if(syncPercentage>=100){
                if(tableDataReceiveFromServerThread.getExceptionMessage()!=null){
                    errorMessage = tableDataReceiveFromServerThread.getExceptionMessage();
                    exceptionClass = tableDataReceiveFromServerThread.getExceptionClass();
                }
            }
		}
    	cursor.addRow(new Object[]{syncPercentage, errorMessage, exceptionClass});
		return cursor;
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	private Cursor signUp(Uri uri){
		MatrixCursor cursor = new MatrixCursor(new String[]{"userProfileId",
                "serverUserId", "authToken", "error_message", "exception_class"});
        String userProfileId = "";
		String serverUserId = "";
		String authToken = "";
		String errorMessage = null;
		String exceptionClass = null;
		if(uri.getQueryParameter(KEY_USER_ID)==null 
			|| uri.getQueryParameter(KEY_USER_NAME)==null
			|| uri.getQueryParameter(KEY_USER_PASS)==null
			|| uri.getQueryParameter(KEY_USER_SERVER_ADDRESS)==null
			|| uri.getQueryParameter(KEY_USER_GROUP)==null
			|| uri.getQueryParameter(KEY_USER_SAVE_DB_EXTERNAL_CARD)==null){
			throw new IllegalArgumentException("No userId, userName, userPass, userServerAddress, userGroup, userSaveDBExternalCard, parameters found in the Uri passed.");
		}else{
			User user = new User(uri.getQueryParameter(KEY_USER_ID));
			user.setUserName(uri.getQueryParameter(KEY_USER_NAME));
			user.setUserPass(uri.getQueryParameter(KEY_USER_PASS));
			user.setServerAddress(uri.getQueryParameter(KEY_USER_SERVER_ADDRESS));
			user.setUserGroup(uri.getQueryParameter(KEY_USER_GROUP));
			user.setGcmRegistrationId(uri.getQueryParameter(KEY_USER_GCM_ID));
			user.setSaveDBInExternalCard(uri.getQueryParameter(KEY_USER_SAVE_DB_EXTERNAL_CARD).equals("true"));
			//TODO: validar el authenticationToken
			
			try {
				LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
				parameters.put("userName", user.getUserName());
				parameters.put("userGroup", user.getUserGroup());
				parameters.put("userPass", user.getUserPass());
				parameters.put("gcmId", user.getGcmRegistrationId());
				
				ConsumeWebService a = new ConsumeWebService(getContext(),
															user.getServerAddress(), 
															"/IntelligentDataSynchronizer/services/ManageUser?wsdl", 
											    			"signUp", 
											    			"urn:signUp", 
											    			parameters);
				Object response =  a.getWSResponse();
				if(response instanceof SoapPrimitive){
					JSONObject json = new JSONObject(response.toString());
                    userProfileId = json.getString("USER_PROFILE_ID");
					serverUserId = json.getString("SERVER_USER_ID");
					authToken = json.getString("AUTH_TOKEN");
				}else if (response != null){
					throw new ClassCastException("response classCastException, "+user+".");
				}else {
					throw new NullPointerException("response is null, "+user+".");
				}
			} catch(ConnectException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "ConnectException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketTimeoutException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketTimeoutException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(IOException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "IOException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "Exception, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			}
		}
		cursor.addRow(new Object[]{userProfileId, serverUserId, authToken, errorMessage, exceptionClass});
		return cursor;
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	private Cursor signIn(Uri uri){
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		Integer codeResult = ApplicationUtilities.ERROR;
		String errorMessage = null;
		String exceptionClass = null;
		if(uri.getQueryParameter(KEY_USER_ID)==null 
			|| uri.getQueryParameter(KEY_USER_AUTH_TOKEN)==null){
			throw new IllegalArgumentException("No userId or authToken, parameters found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			user.setAuthToken(uri.getQueryParameter(KEY_USER_AUTH_TOKEN));
			//TODO: validar el authenticationToken
			
			try {
				LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
				parameters.put("authToken", user.getAuthToken());
				parameters.put("userId", user.getServerUserId());
				parameters.put("userPass", user.getUserPass());
				
				ConsumeWebService a = new ConsumeWebService(getContext(),
															user.getServerAddress(), 
															"/IntelligentDataSynchronizer/services/ManageUser?wsdl", 
											    			"signIn", 
											    			"urn:signIn", 
											    			parameters);
				Object response =  a.getWSResponse();
				if(response instanceof SoapPrimitive){
					codeResult = Integer.valueOf(response.toString());
				}else if (response != null){
					throw new ClassCastException("response classCastException, "+user+".");
				}else {
					throw new NullPointerException("response is null, "+user+".");
				}
			} catch(ConnectException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "ConnectException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketTimeoutException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketTimeoutException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(IOException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "IOException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "Exception, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			}
		}
		cursor.addRow(new Object[]{codeResult, errorMessage, exceptionClass});
		return cursor;
	}

    /**
     *
     * @param uri
     * @return
     */
    private Cursor getAuthToken(Uri uri){
        MatrixCursor cursor = new MatrixCursor(new String[]{"authToken", "error_message", "exception_class"});
        String authToken = null;
        String errorMessage = null;
        String exceptionClass = null;
        if(uri.getQueryParameter(KEY_USER_ID)==null){
            throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
        }else{
            User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
            try {
                LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
                parameters.put("userId", user.getServerUserId());
                parameters.put("userPass", user.getUserPass());

                ConsumeWebService a = new ConsumeWebService(getContext(),
                        user.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageUser?wsdl",
                        "getAuthToken",
                        "urn:getAuthToken",
                        parameters);
                Object response =  a.getWSResponse();
                if(response instanceof SoapPrimitive){
                    authToken = response.toString();
                }else if (response != null){
                    throw new ClassCastException("response classCastException, "+user+".");
                }else {
                    throw new NullPointerException("response is null, "+user+".");
                }
            } catch(ConnectException e){
                e.printStackTrace();
                errorMessage = e.getMessage()==null ? "ConnectException, "+user+"." : e.getMessage();
                exceptionClass = e.getClass().getName();
            } catch(SocketTimeoutException e){
                e.printStackTrace();
                errorMessage = e.getMessage()==null ? "SocketTimeoutException, "+user+"." : e.getMessage();
                exceptionClass = e.getClass().getName();
            } catch(SocketException e){
                e.printStackTrace();
                errorMessage = e.getMessage()==null ? "SocketException, "+user+"." : e.getMessage();
                exceptionClass = e.getClass().getName();
            } catch(IOException e){
                e.printStackTrace();
                errorMessage = e.getMessage()==null ? "IOException, "+user+"." : e.getMessage();
                exceptionClass = e.getClass().getName();
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage()==null ? "Exception, "+user+"." : e.getMessage();
                exceptionClass = e.getClass().getName();
            }
        }
        cursor.addRow(new Object[]{authToken, errorMessage, exceptionClass});
        return cursor;
    }
	
	/**
	 *
	 * @param uri
	 * @return
	 */
	private Cursor signOut(Uri uri){
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		String result = null;
		String errorMessage = null;
		String exceptionClass = null;
		if(uri.getQueryParameter(KEY_USER_ID)==null 
				|| uri.getQueryParameter(KEY_USER_SYNC_STATE)==null){
			throw new IllegalArgumentException("No userId or syncState paremeter found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			String syncState = uri.getQueryParameter(KEY_USER_SYNC_STATE);
			//TODO: validar el authenticationToken
			
			try {
				LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
				parameters.put("authToken", user.getAuthToken());
				parameters.put("userId", user.getServerUserId());
				parameters.put("syncState", syncState);
				
				ConsumeWebService a = new ConsumeWebService(getContext(),
															user.getServerAddress(), 
															"/IntelligentDataSynchronizer/services/ManageUser?wsdl", 
											    			"signOut", 
											    			"urn:signOut", 
											    			parameters);
				Object response =  a.getWSResponse();
				if(response instanceof SoapPrimitive){
					result = response.toString();
				}else if (response != null){
					throw new ClassCastException("response classCastException, "+user+".");
				}else {
					throw new NullPointerException("response is null, "+user+".");
				}
			} catch(ConnectException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "ConnectException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketTimeoutException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketTimeoutException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(SocketException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "SocketException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch(IOException e){
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "IOException, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			} catch (Exception e) {
				e.printStackTrace();
				errorMessage = e.getMessage()==null ? "Exception, "+user+"." : e.getMessage();
				exceptionClass = e.getClass().getName();
			}
		}
		cursor.addRow(new Object[]{result, errorMessage, exceptionClass});
		return cursor;
	}
	
	/**
	 * 
	 * @param uri
	 * @throws SQLException
	 */
	private Cursor registerGcmIdInServer(Uri uri) throws SQLException {
		MatrixCursor cursor = new MatrixCursor(new String[]{"state", "error_message", "exception_class"});
		String errorMessage = null;
		String exceptionClass = null;
		Boolean result = false;
		if(uri.getQueryParameter(KEY_USER_ID)==null || uri.getQueryParameter(KEY_USER_GCM_ID)==null){
			throw new IllegalArgumentException("No userId or gcmId parameter found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			
			LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("authToken", user.getAuthToken());
			parameters.put("userId", user.getServerUserId());
			parameters.put("gcmId", uri.getQueryParameter(KEY_USER_GCM_ID));
	    	ConsumeWebService a = new ConsumeWebService(getContext(),
														user.getServerAddress(), 
										    			"/IntelligentDataSynchronizer/services/ManageUser?wsdl", 
										    			"registerUserGcmId", 
										    			"urn:registerUserGcmId", 
										    			parameters);
	    	try{
				Object response =  a.getWSResponse();
				if(response instanceof SoapPrimitive){
					result = response.toString().equals("true");
				}else if (response instanceof Exception){
					throw (Exception) response;
				}else if (response == null){
					throw new NullPointerException("response is null, "+user+".");
				}else {
					throw new ClassCastException("response classCastException, "+user+".");
				}
			}catch (Exception e){
				e.printStackTrace();
				errorMessage = e.getMessage();
				exceptionClass = e.getClass().getName();
			}
		}
		cursor.addRow(new Object[]{result, errorMessage, exceptionClass});
		return cursor;
	}

}
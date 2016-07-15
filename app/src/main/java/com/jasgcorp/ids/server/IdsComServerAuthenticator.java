package com.jasgcorp.ids.server;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.jasgcorp.ids.gcm.RegistrationIntentService;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.SynchronizerContentProvider;
import com.jasgcorp.ids.syncadapter.model.ServerAuthenticate;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

/**
 * Handles the communication with IDS server
 * @author jsarco
 *
 */
public class IdsComServerAuthenticator implements ServerAuthenticate {

	private static final String TAG = IdsComServerAuthenticator.class.getSimpleName();

    @Override
    public void userSignUp(User user, String authType, Context ctx) throws Exception {
    	Log.d(TAG, "userSignUp("+user+", "+authType+", Context ctx)");

		try {
            //Nos registramos en los servidores de GCM
            user.setGcmRegistrationId(InstanceID.getInstance(ctx).getToken(ctx.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
        Cursor response = null;
        try{
        	response = ctx.getContentResolver()
							.query(SynchronizerContentProvider.USER_SIGN_UP_URI.buildUpon()
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_NAME, user.getUserName())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_PASS, user.getUserPass())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_SERVER_ADDRESS, user.getServerAddress())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_GROUP, user.getUserGroup())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_GCM_ID, user.getGcmRegistrationId())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_SAVE_DB_EXTERNAL_CARD, String.valueOf(user.isSaveDBInExternalCard())).build(), 
					        		null, null, null, null);
			if(response == null){
				user.setSessionToken(ctx.getString(R.string.error_validating_user_return_null));
			}else{
				if(response.moveToNext()){
					//Se revisa si hubo alguna excepcion al ejecutar la consulta
					if(response.getString(response.getColumnIndex("error_message"))!=null){
						String exceptionClass = response.getColumnIndex("exception_class")<0 ? null : response.getString(response.getColumnIndex("exception_class"));
        				if(exceptionClass!=null && 
    						(exceptionClass.equals(ConnectException.class.getName())
								|| exceptionClass.equals(SocketTimeoutException.class.getName())
								|| exceptionClass.equals(SocketException.class.getName())
								|| exceptionClass.equals(IOException.class.getName()))){
        					throw new IOException(response.getString(response.getColumnIndex("error_message")));
        				}
        				throw new Exception(response.getString(response.getColumnIndex("error_message")));
					}
					user.setBusinessPartnerId(Integer.valueOf(response.getString(response.getColumnIndex("businessPartnerId"))));
					user.setUserProfileId(Integer.valueOf(response.getString(response.getColumnIndex("userProfileId"))));
					user.setServerUserId(Long.valueOf(response.getString(response.getColumnIndex("serverUserId"))));
					user.setAuthToken(response.getString(response.getColumnIndex("authToken")));
					user.setSessionToken(ApplicationUtilities.ST_NEW_USER_AUTHORIZED);
//					switch (response.getInt(response.getColumnIndex("state"))) {
//						case ApplicationUtilities.NEW_USER_AUTHORIZED:
//							getGcmRegId = true;
//							//TODO: colocar aqui el AuthToken unico del usuario
//							user.setAuthToken(null);
//							user.setSessionToken(ApplicationUtilities.ST_NEW_USER_AUTHORIZED);
//						break;
//						case ApplicationUtilities.USER_AUTHORIZED:
//							getGcmRegId = true;
//							user.setSessionToken(ApplicationUtilities.ST_USER_AUTHORIZED);
//						break;
//						case ApplicationUtilities.USER_NOT_AUTHORIZED:
//							user.setSessionToken(ApplicationUtilities.ST_USER_NOT_AUTHORIZED);
//						break;
//						case ApplicationUtilities.USER_WRONG_PASSWORD:
//							user.setSessionToken(ApplicationUtilities.ST_USER_WRONG_PASSWORD);
//						break;
//						case ApplicationUtilities.USER_NOT_EXIST_IN_SERVER:
//							user.setSessionToken(ApplicationUtilities.ST_USER_NOT_EXIST_IN_SERVER);
//						break;
//					}
				}else{
					user.setSessionToken(ctx.getString(R.string.user_response_move_to_next_false));
				}
			}
			Log.d(TAG, "userSignUp - user: "+user);
        }catch(Exception e){
        	e.printStackTrace();
        	throw e;
        }finally{
        	if(response!=null){
        		response.close();
        	}
        }
    }
    
    @Override
    public void userSignIn(User user, String authType, Context ctx) throws Exception {
        Log.d(TAG, "userSignIn");
        boolean getGcmRegId = false;
        Cursor response = null;
        try{
        	response = ctx.getContentResolver()
							.query(SynchronizerContentProvider.USER_SIGN_IN_URI.buildUpon()
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_AUTH_TOKEN, user.getAuthToken()).build(), 
					        		null, null, null, null);
			if(response == null){
				user.setSessionToken(ctx.getString(R.string.error_validating_user_return_null));
			}else{
				if(response.moveToNext()){
					//Se revisa si hubo alguna excepcion al ejecutar la consulta
					if(response.getString(response.getColumnIndex("error_message"))!=null){
						String exceptionClass = response.getColumnIndex("exception_class")<0 ? null : response.getString(response.getColumnIndex("exception_class"));
        				if(exceptionClass!=null && 
    						(exceptionClass.equals(ConnectException.class.getName())
								|| exceptionClass.equals(SocketTimeoutException.class.getName())
								|| exceptionClass.equals(SocketException.class.getName())
								|| exceptionClass.equals(IOException.class.getName()))){
        					throw new IOException(response.getString(response.getColumnIndex("error_message")));
        				}
        				throw new Exception(response.getString(response.getColumnIndex("error_message")));
					}
					
					switch (response.getInt(response.getColumnIndex("state"))) {
						case ApplicationUtilities.NEW_USER_AUTHORIZED:
							getGcmRegId = true;
							user.setSessionToken(ApplicationUtilities.ST_NEW_USER_AUTHORIZED);
						break;
						case ApplicationUtilities.USER_AUTHORIZED:
							getGcmRegId = true;
							user.setSessionToken(ApplicationUtilities.ST_USER_AUTHORIZED);
						break;
						case ApplicationUtilities.USER_NOT_AUTHORIZED:
							user.setSessionToken(ApplicationUtilities.ST_USER_NOT_AUTHORIZED);
						break;
						case ApplicationUtilities.USER_WRONG_PASSWORD:
							user.setSessionToken(ApplicationUtilities.ST_USER_WRONG_PASSWORD);
						break;
						case ApplicationUtilities.USER_NOT_EXIST_IN_SERVER:
							user.setSessionToken(ApplicationUtilities.ST_USER_NOT_EXIST_IN_SERVER);
						break;
					}
				}else{
					user.setSessionToken(ctx.getString(R.string.user_response_move_to_next_false));
				}
			}
        }catch(Exception e){
        	e.printStackTrace();
        	throw e;
        }finally{
        	if(response!=null){
        		response.close();
        	}
        }
		if(getGcmRegId){
            //Nos registramos en los servidores de GCM
            ctx.startService(new Intent(ctx, RegistrationIntentService.class)
                    .putExtra(RegistrationIntentService.KEY_USER_ID, user.getUserId()));
		}
		if (TextUtils.isEmpty(user.getSessionToken())) {
			user.setSessionToken(ctx.getString(R.string.user_session_token_null));
		}
    }

	@Override
	public void userSignOut(User user, String syncState, Context ctx)
			throws Exception {
    	Log.d(TAG, "userSignOut("+user+", "+syncState+", Context ctx)");
        Cursor response = null;
        try{
        	response = ctx.getContentResolver()
							.query(SynchronizerContentProvider.USER_SIGN_OUT_URI.buildUpon()
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId())
									.appendQueryParameter(SynchronizerContentProvider.KEY_USER_SYNC_STATE, syncState).build(), 
					        		null, null, null, null);
			if(response == null){
				user.setSessionToken(ctx.getString(R.string.error_validating_user_return_null));
			}else{
				if(response.moveToNext()){
					//Se revisa si hubo alguna excepcion al ejecutar la consulta
					if(response.getString(response.getColumnIndex("error_message"))!=null){
						String exceptionClass = response.getColumnIndex("exception_class")<0 ? null : response.getString(response.getColumnIndex("exception_class"));
        				if(exceptionClass!=null && 
    						(exceptionClass.equals(ConnectException.class.getName())
								|| exceptionClass.equals(SocketTimeoutException.class.getName())
								|| exceptionClass.equals(SocketException.class.getName())
								|| exceptionClass.equals(IOException.class.getName()))){
        					throw new IOException(response.getString(response.getColumnIndex("error_message")));
        				}
        				throw new Exception(response.getString(response.getColumnIndex("error_message")));
					}
					Log.d(TAG, "response.getString(response.getColumnIndex(\"state\")): "+response.getString(response.getColumnIndex("state")));
				}else{
					throw new Exception("userSignOut("+user+", "+syncState+", Context ctx) - response.moveToNext() is false.");
				}
			}
			Log.d(TAG, "userSignOut - end");
        }catch(Exception e){
        	e.printStackTrace();
        	throw e;
        }finally{
        	if(response!=null){
        		response.close();
        	}
        }
	}
}

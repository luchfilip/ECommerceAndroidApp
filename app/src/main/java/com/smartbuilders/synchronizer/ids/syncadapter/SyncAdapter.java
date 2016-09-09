package com.smartbuilders.synchronizer.ids.syncadapter;

import static com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral.sServerAuthenticate;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.data.SyncLogDB;
import com.smartbuilders.synchronizer.ids.model.SyncLog;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.SynchronizerContentProvider;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

/**
 * http://developer.android.com/training/sync-adapters/creating-sync-adapter.html
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String TAG 						= SyncAdapter.class.getSimpleName();
	public static final String PERIODIC_SYNCHRONIZATION_STARTED 	= "SyncAdapter.PERIODIC_SYNCHRONIZATION_STARTED";
	public static final String PERIODIC_SYNCHRONIZATION_FINISHED 	= "SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED";
	public static final String PERIODIC_SYNCHRONIZATION_CANCELED 	= "SyncAdapter.PERIODIC_SYNCHRONIZATION_CANCELED";
	public static final String SYNCHRONIZATION_STARTED 		        = "SyncAdapter.SYNCHRONIZATION_STARTED";
	public static final String SYNCHRONIZATION_PROGRESS 	        = "SyncAdapter.SYNCHRONIZATION_PROGRESS";
	public static final String SYNCHRONIZATION_FINISHED 	        = "SyncAdapter.SYNCHRONIZATION_FINISHED";
    public static final String FULL_SYNCHRONIZATION_FINISHED 	    = "SyncAdapter.FULL_SYNCHRONIZATION_FINISHED";
	public static final String SYNCHRONIZATION_CANCELED 	        = "SyncAdapter.SYNCHRONIZATION_CANCELED";
	public static final String IO_EXCEPTION 				        = "SyncAdapter.SYNCHRONIZATION_IO_EXCEPTION";
	public static final String GENERAL_EXCEPTION 			= "SyncAdapter.GENERAL_EXCEPTION";
	public static final String CONNECT_EXCEPTION 			= "SyncAdapter.CONNECT_EXCEPTION";
	public static final String SOCKET_EXCEPTION 			= "SyncAdapter.SOCKET_EXCEPTION";
	public static final String OPERATION_CANCELED_EXCEPTION = "SyncAdapter.OPERATION_CANCELED_EXCEPTION";
	public static final String XML_PULL_PARSE_EXCEPTION 	= "SyncAdapter.XML_PULL_PARSE_EXCEPTION";
	public static final String AUTHENTICATOR_EXCEPTION 		= "SyncAdapter.AUTHENTICATOR_EXCEPTION";
	public static final String USER_ID 						= "SyncAdapter.USER_ID";
	public static final String LOG_MESSAGE 					= "SyncAdapter.LOG_MESSAGE";
	public static final String LOG_MESSAGE_DETAIL			= "SyncAdapter.LOG_MESSAGE_DETAIL";
	public static final String SYNC_INIT_TIME				= "SyncAdapter.SYNC_INIT_TIME";
	private static final long MAX_RETRY_NUMBER 				= 0;
	private static final long DELAY_TIME_TO_RETRY_SYNC 		= 5; //time in seconds
	private static final int SYNCHRONIZATION_CANCELLED 		= 2;
	private static final int SYNCHRONIZATION_RUNNING 		= 1;

	public static final String KEY_CURRENT_USER = "com.smartbuilders.ids.SettingsActivity.KEY_CURRENT_USER";
	public static final String KEY_PERIODIC_SYNC_ACTIVE = "com.smartbuilders.ids.SettingsActivity.KEY_PERIODIC_SYNC_ACTIVE";
	
	/**
	 * El usuario inicio sesion de sincronizacion
	 */
	public final static String STATE_NEW_SESSION = "NS";
	/**
	 * El usuario sincronizo completamente
	 */
	public final static String STATE_SESSION_SUCCESS = "SS";
    /**
     * El usuario realizó una sincronizacion periodica
     */
    public final static String STATE_PERIODIC_SESSION_SUCCESS = "PS";
    /**
     * El usuario realizó una sincronizacion completa
     */
    public final static String STATE_FULL_SESSION_SUCCESS = "FS";
	/**
	 * Hay algun registro con error
	 */
	public final static String STATE_RECORDS_ERROR = "RE";
	/**
	 * El usuario cancelo la sincronizacion
	 */
	public final static String STATE_SESSION_CANCELLED = "SC";
	/**
	 * Ocurrio un error de comunicacion
	 */
	public final static String STATE_COMMUNICATION_ERROR = "CE";
	
	// Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    
    private static int syncStatus;
    
    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
        * If your app uses a content resolver, get an instance of it
        * from the incoming Context
        */
        mContentResolver = context.getContentResolver();
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(Account account,
					            Bundle extras,
					            String authority,
					            ContentProviderClient provider,
					            SyncResult syncResult) {
    	long syncInitTime = System.currentTimeMillis();
    	AccountManager accountManager = AccountManager.get(getContext());
        User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(),
				accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
        
        boolean isAPeriodicSync = false;
    	if(extras!=null && extras.containsKey(KEY_PERIODIC_SYNC_ACTIVE)
                && extras.getBoolean(KEY_PERIODIC_SYNC_ACTIVE)){
    		isAPeriodicSync = true;
            try {
                if(((System.currentTimeMillis() - ApplicationUtilities.getLastFullSyncTime(getContext(), user.getUserId()).getTime())/1000)
						< Utils.getSyncPeriodicityFromPreferences(getContext())) {
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    	}

    	try{
        	syncStatus = SYNCHRONIZATION_CANCELLED;
        	
    		// Get the session token for the current account
    		sServerAuthenticate.userSignIn(user, account.type, getContext());

			switch (user.getSessionToken()) {
				case ApplicationUtilities.ST_NEW_USER_AUTHORIZED:
				case ApplicationUtilities.ST_USER_AUTHORIZED:
				break;
				case ApplicationUtilities.ST_USER_NOT_AUTHORIZED:
					//invalidate authentication token
					accountManager.invalidateAuthToken(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE,
							accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));

					//notify authenticator error
		        	syncStatus = SYNCHRONIZATION_CANCELLED;
		        	throw new AuthenticatorException(getContext().getString(R.string.user_not_authorized));
				case ApplicationUtilities.ST_USER_NOT_EXIST_IN_SERVER:
					//invalidate authentication token
					accountManager.invalidateAuthToken(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE,
							accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));

					//notify authenticator error
		        	syncStatus = SYNCHRONIZATION_CANCELLED;
		        	throw new AuthenticatorException(getContext().getString(R.string.user_not_exist_in_server));
				case ApplicationUtilities.ST_USER_WRONG_PASSWORD:
					//invalidate authentication token
	        		accountManager.invalidateAuthToken(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE,
	        											accountManager.peekAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
					//notify authenticator error
		        	syncStatus = SYNCHRONIZATION_CANCELLED;
	        		throw new AuthenticatorException(getContext().getString(R.string.user_wrong_password));
				
				default:
		        	syncStatus = SYNCHRONIZATION_CANCELLED;
		        	throw new AuthenticatorException(user.getSessionToken());
			}
			    		
    		Cursor result = null;
			try{
                //start synchronization
				result = getContext().getContentResolver()
						.query(SynchronizerContentProvider.START_SYNC_URI.buildUpon()
								.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId())
								.appendQueryParameter(SynchronizerContentProvider.KEY_IS_INITIAL_LOAD,
										String.valueOf(ApplicationUtilities.appRequireInitialLoad(getContext(), account))).build(),
								null, null, null, null);

				if(result.moveToNext()){
					if(Boolean.valueOf(result.getString(result.getColumnIndex("state")))){
						syncStatus = SYNCHRONIZATION_RUNNING;
						//notify synchronization start
						recordLogAndSendBroadcast(user.getUserId(), isAPeriodicSync ? PERIODIC_SYNCHRONIZATION_STARTED : SYNCHRONIZATION_STARTED,
													R.string.sync_started, R.string.sync_started, syncInitTime, SyncLog.VISIBLE, getContext());
						
					}else{
						throw new OperationCanceledException(result.getString(result.getColumnIndex("error_message")));
					}
				}
        	}catch (OperationCanceledException e) {
        		e.printStackTrace();
        		syncStatus = SYNCHRONIZATION_CANCELLED;
        		//notify synchronization canceled
        		recordLogAndSendBroadcast(user.getUserId(), SYNCHRONIZATION_CANCELED,
						getContext().getString(R.string.sync_cancelled), e.getMessage(), syncInitTime, SyncLog.VISIBLE, getContext());
        		throw new OperationCanceledException(e.getMessage());
            }catch(Exception e){
            	e.printStackTrace();
				(new SyncLogDB(getContext())).registerLogInDataBase(user.getUserId(), GENERAL_EXCEPTION,
						getContext().getString(R.string.general_exception), e.getMessage(), SyncLog.INVISIBLE);
				throw e;
			}finally{
				if(result!=null){
					result.close();
				}
			}
			Float syncProgressPercentage = 0f;

			//notify synchronization progress percentage
			recordLogAndSendBroadcast(user.getUserId(), SYNCHRONIZATION_PROGRESS, ApplicationUtilities.formatFloatTwoDecimals(syncProgressPercentage), syncInitTime, SyncLog.INVISIBLE, getContext());
        	while(syncProgressPercentage<100){
        		if(syncStatus==SYNCHRONIZATION_CANCELLED){
        			try{
        				result = getContext().getContentResolver()
    							        	.query(SynchronizerContentProvider.STOP_SYNC_URI.buildUpon()
    							        			.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId()).build(), 
    							        			null, null, null, null);
        				if(result.moveToNext()){
        					if(Boolean.valueOf(result.getString(result.getColumnIndex("state")))){
        						throw new OperationCanceledException(getContext().getString(R.string.sync_cancelled_by_user));
        					}else{
        						syncStatus = SYNCHRONIZATION_RUNNING;
        					}
        				}
    	        	}catch(OperationCanceledException e){
    	        		e.printStackTrace();
    	        		syncProgressPercentage = 1000F;
    	        		syncStatus = SYNCHRONIZATION_CANCELLED;
    	        		//notify synchronization canceled
    	        		recordLogAndSendBroadcast(user.getUserId(), SYNCHRONIZATION_CANCELED,
								getContext().getString(R.string.sync_cancelled), e.getMessage(), syncInitTime, SyncLog.VISIBLE, getContext());
        				throw new OperationCanceledException(e.getMessage());
    	        	}finally{
        				if(result!=null){
        					result.close();
        				}
        			}
        		}else{
        			try{
        				//Se consulta el porcentaje de progreso de la sincronizacion.
	        			result = getContext().getContentResolver()
		        							.query(SynchronizerContentProvider.SYNC_PROGRESS_PERCENTAGE_URI.buildUpon()
    							        			.appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId()).build(), 
		        									null, null, null, null);
	        			if(syncStatus!=SYNCHRONIZATION_CANCELLED && result!=null && result.moveToNext()){
	        				//Si hay un mensaje de error al consultar el progreso de la sincronizacion
	        				//entonces se detiene la sincronizacion y se muestra el mensaje de error
		        			if(result.getString(result.getColumnIndex("error_message"))!=null){
		        				
		        				if(result.getString(result.getColumnIndex("exception_class"))!=null 
	        						&& (result.getString(result.getColumnIndex("exception_class")).equals(ConnectException.class.getName())
        								|| result.getString(result.getColumnIndex("exception_class")).equals(SocketTimeoutException.class.getName())
        								|| result.getString(result.getColumnIndex("exception_class")).equals(SocketException.class.getName())
        								|| result.getString(result.getColumnIndex("exception_class")).equals(IOException.class.getName())
                                        || result.getString(result.getColumnIndex("exception_class")).equals(org.ksoap2.SoapFault.class.getName()))){
		        					throw new IOException(result.getString(result.getColumnIndex("error_message")));
		        				} 
								throw new Exception(result.getString(result.getColumnIndex("error_message")));
		        			}else{
		        				syncProgressPercentage = Float.valueOf(result.getString(result.getColumnIndex("state")));
		        			}
		        			//notify synchronization progress percentage
		        			recordLogAndSendBroadcast(user.getUserId(), SYNCHRONIZATION_PROGRESS,
									ApplicationUtilities.formatFloatTwoDecimals(syncProgressPercentage), syncInitTime, SyncLog.INVISIBLE, getContext());
		        			//se espera por unos 2000 milisegundos antes de volver a preguntar
		            		try {
		            		    Thread.sleep(2000);
		            		} catch(InterruptedException ex) {
		            		    Thread.currentThread().interrupt();
		            		}
	        			}
        			} finally{
        				if(result!=null){
        					result.close();
        				}
        			}
        		}
        	}
        	if(syncProgressPercentage>=100){
	        	//notify synchronization finished successful
		        recordLogAndSendBroadcast(user.getUserId(),
											isAPeriodicSync ? PERIODIC_SYNCHRONIZATION_FINISHED : FULL_SYNCHRONIZATION_FINISHED,
											R.string.sync_finished, 
											getContext().getString(R.string.sync_finished_duration,
													ApplicationUtilities.parseMillisecondsToHMS(System.currentTimeMillis() - syncInitTime, ApplicationUtilities.TIME_FORMAT_2)),
											syncInitTime,
											SyncLog.VISIBLE,
											getContext());
		        try {
					sServerAuthenticate.userSignOut(user, isAPeriodicSync ? STATE_PERIODIC_SESSION_SUCCESS : STATE_FULL_SESSION_SUCCESS, getContext());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        }
	    } catch (OperationCanceledException e) {
	    	syncStatus = SYNCHRONIZATION_CANCELLED;
	    	try {
				sServerAuthenticate.userSignOut(user, STATE_SESSION_CANCELLED, getContext());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
	    	e.printStackTrace();
        } catch (IOException e) {
        	syncStatus = SYNCHRONIZATION_CANCELLED;
        	syncResult.delayUntil = DELAY_TIME_TO_RETRY_SYNC;
        	syncResult.stats.numIoExceptions++;
        	if(syncResult.stats.numIoExceptions > MAX_RETRY_NUMBER){
        		syncResult.tooManyRetries = true;
        	}
            (new SyncLogDB(getContext())).registerLogInDataBase(user.getUserId(), IO_EXCEPTION, getContext().getString(R.string.io_exception), e.getMessage(), SyncLog.INVISIBLE);
        	recordLogAndSendBroadcast(user.getUserId(), IO_EXCEPTION, R.string.io_exception, e.getMessage(), syncInitTime, SyncLog.VISIBLE, getContext());
            e.printStackTrace();
        } catch (AuthenticatorException e) {
        	syncStatus = SYNCHRONIZATION_CANCELLED;
        	syncResult.delayUntil = DELAY_TIME_TO_RETRY_SYNC;
        	syncResult.stats.numAuthExceptions++;
        	if(syncResult.stats.numAuthExceptions > MAX_RETRY_NUMBER){
        		syncResult.tooManyRetries = true;
        	}
            (new SyncLogDB(getContext())).registerLogInDataBase(user.getUserId(), AUTHENTICATOR_EXCEPTION, getContext().getString(R.string.authenticator_exception), e.getMessage(), SyncLog.INVISIBLE);
        	recordLogAndSendBroadcast(user.getUserId(), AUTHENTICATOR_EXCEPTION, R.string.authenticator_exception, e.getMessage(), syncInitTime, SyncLog.VISIBLE, getContext());
            e.printStackTrace();
        } catch(Exception e){
        	syncStatus = SYNCHRONIZATION_CANCELLED;
			recordLogAndSendBroadcast(user.getUserId(), GENERAL_EXCEPTION, getContext().getString(R.string.general_exception), e.getMessage(), syncInitTime, SyncLog.VISIBLE, getContext());
			e.printStackTrace();
    	}
    	Log.d(TAG, "debug> SYNCHRONIZATION FINISHED");
    }

    @Override
    public void onSyncCanceled() {
    	syncStatus = SYNCHRONIZATION_CANCELLED;
    	super.onSyncCanceled();
    }
    
    /**
     * 
     * @param userId
     * @param logType
     * @param logMessage
     * @param syncInitTime
     * @param visibility
     * @param ctx
     */
    private static void recordLogAndSendBroadcast(String userId, String logType, String logMessage, long syncInitTime, int visibility, Context ctx){
    	recordLogAndSendBroadcast(userId, logType, logMessage, logMessage, syncInitTime, visibility, ctx);
    }
    
    /**
     * 
     * @param userId
     * @param logType
     * @param logMessage
     * @param logMessageDetail
     * @param syncInitTime
     * @param visibility
     * @param ctx
     */
    private static void recordLogAndSendBroadcast(String userId, String logType, String logMessage, String logMessageDetail, long syncInitTime, int visibility, Context ctx){
    	if(userId!=null){
    		if(logMessage==null){
	    		logMessage = "No log message registered.";
	    	}
            (new SyncLogDB(ctx)).registerLogInDataBase(userId, logType, logMessage, logMessageDetail, visibility);
	    	ctx.sendBroadcast((new Intent(logType))
	    			.putExtra(USER_ID, userId)
	    			.putExtra(LOG_MESSAGE, logMessage)
	    			.putExtra(LOG_MESSAGE_DETAIL, logMessageDetail)
	    			.putExtra(SYNC_INIT_TIME, syncInitTime));
		}
    }
    
    /**
     * 
     * @param userId
     * @param logType
     * @param logMessageResId
     * @param logMessageDetailResId
     * @param syncInitTime
     * @param visibility
     * @param ctx
     */
    private static void recordLogAndSendBroadcast(String userId, String logType, int logMessageResId, int logMessageDetailResId, long syncInitTime, int visibility, Context ctx){
    	recordLogAndSendBroadcast(userId, logType, ctx.getString(logMessageResId), ctx.getString(logMessageDetailResId), syncInitTime, visibility, ctx);
    }
    
    /**
     * 
     * @param userId
     * @param logType
     * @param logMessageResId
     * @param logMessageDetail
     * @param syncInitTime
     * @param visibility
     * @param ctx
     */
    private static void recordLogAndSendBroadcast(String userId, String logType, int logMessageResId, String logMessageDetail, long syncInitTime, int visibility, Context ctx){
    	recordLogAndSendBroadcast(userId, logType, ctx.getString(logMessageResId), logMessageDetail, syncInitTime, visibility, ctx);
    }
}

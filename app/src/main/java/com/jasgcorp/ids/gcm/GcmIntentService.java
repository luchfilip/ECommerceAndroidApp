package com.jasgcorp.ids.gcm;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * http://developer.android.com/training/sync-adapters/running-sync-adapter.html
 * @author jsarco
 *
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();
    // Incoming Intent key for extended data
    public static final String KEY_SYNC_REQUEST = "com.jasgcorp.ids.gcm.KEY_SYNC_REQUEST";
    public static final String KEY_SERVER_USER_ID = "com.jasgcorp.ids.gcm.KEY_SERVER_USER_ID";

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
	        	for(String key : extras.keySet()){
	        		Log.d(TAG, "key: "+key);
	        	}
	        	//Run the Sync Adapter When Server Data Changes
	        	if(extras.containsKey("action") && extras.getString("action")!=null){
		        	if(extras.getString("action").equals(KEY_SYNC_REQUEST) && extras.containsKey(KEY_SERVER_USER_ID)){
		        		String serverUserId = extras.getString(KEY_SERVER_USER_ID);
		    	        Bundle settingsBundle = new Bundle();
		    	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		    	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		        		AccountManager mAccountmanager = AccountManager.get(this);
		        		
		        		Account[] accounts = mAccountmanager.getAccountsByType(getString(R.string.authenticator_acount_type));
		        		for(Account account : accounts){
		        			if(mAccountmanager.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID).equals(serverUserId)){
		        				if(!ApplicationUtilities.isSyncActive(account, DataBaseContentProvider.AUTHORITY)){
		        					ContentResolver.requestSync(account, DataBaseContentProvider.AUTHORITY, settingsBundle);
					            }
		        				break;
	        				}
		        		}
		        	}
	        	}
	        }else{
	        	try{
            		Log.d(TAG, "messageType: "+messageType);
            	}catch(NullPointerException e){ }
	        }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}

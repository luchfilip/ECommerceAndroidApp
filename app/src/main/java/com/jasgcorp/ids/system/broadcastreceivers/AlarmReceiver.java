package com.jasgcorp.ids.system.broadcastreceivers;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.R;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.scheduler.SchedulerSyncData;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver{

	public static final String KEY_CURRENT_USER_ID 	= "com.jasgcorp.ids.scheduler.AlarmReceiver.KEY_CURRENT_USER_ID";
	public static final String KEY_CURRENT_ALARM_ID = "com.jasgcorp.ids.scheduler.AlarmReceiver.KEY_CURRENT_ALARM_ID";
	public static final String ACTION 				= "com.jasgcorp.ids.scheduler.AlarmReceiver.Action";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			Bundle extras = intent.getExtras();
			if(extras!=null 
					&& extras.containsKey(KEY_CURRENT_USER_ID) 
					&& extras.containsKey(KEY_CURRENT_ALARM_ID)){
				AccountManager mAccountManager = AccountManager.get(context);
				final Account availableAccounts[] = mAccountManager.getAccountsByType(context.getString(R.string.authenticator_acount_type));
				if(availableAccounts!=null && availableAccounts.length>0){
					for(Account account : availableAccounts){
						if(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID).equals(extras.getString(KEY_CURRENT_USER_ID))){
							try {
								User user = new User(extras.getString(KEY_CURRENT_USER_ID));
								SchedulerSyncData data = ApplicationUtilities.getSchedulerSyncDataByID(extras.getInt(KEY_CURRENT_ALARM_ID), context);
								if(data!=null && !data.isMonday() && !data.isTuesday() && !data.isWednesday() 
										&& !data.isThursday() && !data.isFriday() && !data.isSaturday() && !data.isSunday()){
									data.setActive(false);
									// si la alarma no se repite ningun dia entonces se desactiva
									ApplicationUtilities.setActiveSyncSchedulerData(user, data, context);
								}
						        Bundle settingsBundle = new Bundle();
						        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
						        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

						        if(!ApplicationUtilities.isSyncActive(account, DataBaseContentProvider.AUTHORITY)){
						        	ContentResolver.requestSync(account, DataBaseContentProvider.AUTHORITY, settingsBundle);
						        	Toast.makeText(context, "Automatic synchronization of Intelligent Data Synchronizer for user "+account.name+" has begun!!!",
									        Toast.LENGTH_LONG).show();
								    // Vibrate the mobile phone
								    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
								    vibrator.vibrate(2000);
					            }
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						}
					}
			    }
		    }
	    }
	}

}

package com.smartbuilders.synchronizer.ids.receivers;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver{

	public static final String KEY_CURRENT_USER_ID 	= BuildConfig.APPLICATION_ID + "." + AlarmReceiver.class.getSimpleName() + ".KEY_CURRENT_USER_ID";
	public static final String KEY_CURRENT_ALARM_ID = BuildConfig.APPLICATION_ID + "." + AlarmReceiver.class.getSimpleName() + ".KEY_CURRENT_ALARM_ID";
	public static final String ACTION 				= BuildConfig.APPLICATION_ID + "." + AlarmReceiver.class.getSimpleName() + ".ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			Bundle extras = intent.getExtras();
			if(extras!=null 
					&& extras.containsKey(KEY_CURRENT_USER_ID) 
					&& extras.containsKey(KEY_CURRENT_ALARM_ID)){
				AccountManager mAccountManager = AccountManager.get(context);
				final Account availableAccounts[] = mAccountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);
				if(availableAccounts.length>0){
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

						        if(!ApplicationUtilities.isSyncActive(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)){
									//TODO: revisar si la ultima sincronizacion se realizo en un tiempo mayor al
									//definido por el periodo de sincronizacion
						        	ApplicationUtilities.initSyncByAccount(context, account);
						        	//Toast.makeText(context, "Automatic synchronization of Intelligent Data Synchronizer for user "+account.name+" has begun!!!",
									//        Toast.LENGTH_SHORT).show();
								    // Vibrate the mobile phone
								    //Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
								    //vibrator.vibrate(2000);
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

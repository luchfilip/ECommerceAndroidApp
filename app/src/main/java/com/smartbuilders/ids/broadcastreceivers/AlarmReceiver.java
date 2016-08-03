package com.smartbuilders.ids.broadcastreceivers;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver{

	public static final String KEY_CURRENT_USER_ID 	= "com.smartbuilders.ids.scheduler.AlarmReceiver.KEY_CURRENT_USER_ID";
	public static final String KEY_CURRENT_ALARM_ID = "com.smartbuilders.ids.scheduler.AlarmReceiver.KEY_CURRENT_ALARM_ID";
	public static final String ACTION 				= "com.smartbuilders.ids.scheduler.AlarmReceiver.Action";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			Bundle extras = intent.getExtras();
			if(extras!=null 
					&& extras.containsKey(KEY_CURRENT_USER_ID) 
					&& extras.containsKey(KEY_CURRENT_ALARM_ID)){
				AccountManager mAccountManager = AccountManager.get(context);
				final Account availableAccounts[] = mAccountManager.getAccountsByType(context.getString(R.string.authenticator_account_type));
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

						        if(!ApplicationUtilities.isSyncActive(context, account)){
									//TODO: revisar si la ultima sincronizacion se realizo en un tiempo mayor al
									//definido por el periodo de sincronizacion
						        	ApplicationUtilities.initSyncByAccount(context, account);
						        	//Toast.makeText(context, "Automatic synchronization of Intelligent Data Synchronizer for user "+account.name+" has begun!!!",
									//        Toast.LENGTH_LONG).show();
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

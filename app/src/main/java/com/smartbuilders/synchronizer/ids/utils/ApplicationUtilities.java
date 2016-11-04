package com.smartbuilders.synchronizer.ids.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.iharder.Base64;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.services.SyncDataRealTimeWithServerService;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.data.SyncLogDB;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.synchronizer.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.broadcastreceivers.AlarmReceiver;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SyncInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;

/**
 * 
 * @author jsarco
 *
 */
public class ApplicationUtilities {
	
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	protected static final String TAG = ApplicationUtilities.class.getSimpleName();
	public static final int URL_VALIDATOR_OK 				= 1;
	public static final int URL_VALIDATOR_STRING_NULL 		= 2;
	public static final int URL_VALIDATOR_IO_EXCEPTION 		= 3;
	public static final int URL_VALIDATOR_MALFORMED 		= 4;
	public static final int ERROR							= -11;
	public static final int PREFERENCES_CONFIGURATION_ERROR	= 11;
	public static final int PREFERENCES_INVALID_USER 		= 12;
	public static final int PREFERENCES_INVALID_GROUP 		= 13;
	public static final int PREFERENCES_OK 					= 14;
	public static final int LOCAL_CONNECTIVITY_OK 			= 15;
	public static final int NO_LOCAL_CONNECTIVITY 			= 16;
	public static final int NO_INTERNET_ACCESS 				= 17;
	public static final int NO_DATA_TO_SYNC					= 18;
	public static final int START_SYNCHRONIZATION			= 19;
	public static final int START_FIRST_SYNCHRONIZATION		= 20;
	public static final int USER_NOT_EXIST_IN_SERVER		= 21;
	public static final int USER_NOT_AUTHORIZED				= 22;
	public static final int USER_AUTHORIZED					= 23;
	public static final int ERROR_SOCKECT_TIMEOUT_EXCEPTION	= 24;
	public static final int ERROR_CONNECT_EXCEPTION			= 25;
	public static final int SYNCHRONIZATION_COMPLETE		= 26;
	public static final int NEW_USER_AUTHORIZED				= 27;
	public static final int SYNCHRONIZATION_TIMEOUT_ERROR	= 28;
	public static final int ERROR_UNKNOWN_SERVER			= 29;
	public static final int PRYMARY_SERVER_ADDRESS_MODIFIED	= 30;
	public static final int PRYMARY_SERVER_ADDRESS_REPAIRED	= 31;
	public static final int USER_WRONG_PASSWORD				= 32;
	public static final int USER_RETURN_NULL				= 27;
	public static final int USER_RESPONSE_MOVE_TO_NEXT_FALSE = 28;
	
	//Constantes para Session Token
	public static final String ST_NEW_USER_AUTHORIZED = "NEW_USER_AUTHORIZED";
	public static final String ST_USER_NOT_AUTHORIZED = "USER_NOT_AUTHORIZED";
	public static final String ST_USER_AUTHORIZED = "USER_AUTHORIZED";
	public static final String ST_USER_WRONG_PASSWORD = "USER_WRONG_PASSWORD";
	public static final String ST_USER_NOT_EXIST_IN_SERVER = "USER_NOT_EXIST_IN_SERVER";

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss",Locale.getDefault());
    
    public static final String TIME_FORMAT_1 = "%02d:%02d:%02d";
    public static final String TIME_FORMAT_2 = "%02d H, %02d m, %02d s";
	
	/**
	 * Devuelve el numero de version del codigo de la aplicacion.
	 * Lo que se encuentra en el manifest como android:versionCode
	 * @param context
	 * @return
	 */
	public static int getAppVersion(Context context){
	    try{
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	 
	        return packageInfo.versionCode;
	    }catch (NameNotFoundException e){
	        throw new RuntimeException("Error al obtener version: " + e);
	    }
	}

    /**
     *
     * @param account
     * @param authority
     * @return
     */
	public static boolean isSyncActive(Account account, String authority) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return isSyncActiveHoneycomb(account, authority);
        } else {
            @SuppressWarnings("deprecation")
			SyncInfo currentSync = ContentResolver.getCurrentSync();
            return currentSync != null && currentSync.account.equals(account) 
                    && currentSync.authority.equals(authority);
        }
    }

	/**
	 *
	 * @param context
	 * @param account
     */
	public static void initSyncByAccount(Context context, Account account) {
		Bundle settingsBundle = new Bundle();
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
		ContentResolver.requestSync(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, settingsBundle);
	}

	/**
	 * 
	 * @param account
	 * @param authority
	 * @return
	 */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static boolean isSyncActiveHoneycomb(Account account, String authority) {
        for(SyncInfo syncInfo : ContentResolver.getCurrentSyncs()) {
            if(syncInfo.account.equals(account) && syncInfo.authority.equals(authority)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	public static boolean checkPlayServices(Activity activity) {
		GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
		int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
						.show();
			} else {
				Log.i(TAG, "This device is not supported.");
				activity.finish();
			}
			return false;
		}
		return true;
	}

    /**
     * Revisa si hay una nueva version de la aplicacion disponible en el market. tambien maneja si
     * la actualizacion es obligatoria u obligatoria
     */
    public static void checkAppVersion(final Activity activity, final User user) {
        DialogInterface.OnClickListener goToMarket = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+activity.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    intent.setPackage("com.android.vending");
                    dialog.dismiss();
                    activity.startActivity(intent);
                    activity.finish();
                }catch (ActivityNotFoundException e){
                    // Ok that didn't work, try the market method.
                    try{
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+activity.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        intent.setPackage("com.android.vending");
                        dialog.dismiss();
                        activity.startActivity(intent);
                        activity.finish();
                    }catch (ActivityNotFoundException f){
                        // Ok, weird. Maybe they don't have any market app. Just show the website.
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+activity.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        dialog.dismiss();
                        activity.startActivity(intent);
                        activity.finish();
                    }
                }
            }
        };
        try {
            if(Parameter.getLastMandatoryAppVersion(activity, user) > getAppVersion(activity)){
                //actualizacion obligatoria
                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle(R.string.new_version_available_title)
                        .setMessage(R.string.new_mandatory_version_available_message)
                        .setPositiveButton(R.string.update, goToMarket)
                        .show();
            }else if (Parameter.getMarketAppVersion(activity, user) > getAppVersion(activity)){
                //actualizacion opcional
                new AlertDialog.Builder(activity)
                        .setCancelable(true)
                        .setTitle(R.string.new_version_available_title)
                        .setMessage(R.string.new_version_available_message)
                        .setPositiveButton(R.string.update, goToMarket)
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static Date getLastFullSyncTime(Context ctx, Account account) {
		return getLastFullSyncTime(ctx, AccountManager.get(ctx).getUserData(account,
                AccountGeneral.USERDATA_USER_ID));
	}

	public static Date getLastFullSyncTime(Context ctx, String userId) {
		Cursor c = null;
		try{
            c = ctx.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT MAX(CREATE_TIME) FROM IDS_SYNC_LOG WHERE USER_ID=? AND (LOG_TYPE LIKE ? OR LOG_TYPE LIKE ?)",
                    new String[]{userId, "%.FULL_SYNCHRONIZATION_FINISHED",
                            "%.PERIODIC_SYNCHRONIZATION_FINISHED"}, null);
            if(c!=null && c.moveToNext() && c.getString(0)!=null){
                return sdf.parse(c.getString(0));
            }
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
		return null;
	}

    /**
     * Se verifica que la ultima sincronizacion se haya realizado en un tiempo
     * mayor o igual al periodo de sincronizacion definido.
     * @param ctx
     * @param account
     * @return
     */
    public static boolean appRequireFullSync(Context ctx, Account account) {
        return appRequireFullSync(ctx, AccountManager.get(ctx).getUserData(account,
                AccountGeneral.USERDATA_USER_ID));
    }

	/**
	 * Se verifica que la ultima sincronizacion se haya realizado en un tiempo
	 * mayor o igual al periodo de sincronizacion definido.
	 * @param ctx
	 * @param userId
     * @return
     */
	public static boolean appRequireFullSync(Context ctx, String userId) {
        Date lastSuccessFullySyncTime = ApplicationUtilities.getLastFullSyncTime(ctx, userId);
        return lastSuccessFullySyncTime == null
                || (((System.currentTimeMillis() - lastSuccessFullySyncTime.getTime()) / 1000) >= Utils.getSyncPeriodicityFromPreferences(ctx));
    }

    /**
     * Verifica si el usuario aun no tiene una sincronizacion completa realizada
     * @param context
     * @param account
     * @return
     */
    public static boolean appRequireInitialLoad(Context context, Account account) {
        return getLastFullSyncTime(context, account)==null;
    }

    /**
     * Verifica si el usuario aun no tiene una sincronizacion completa realizada
     * @param context
     * @param userId
     * @return
     */
    public static boolean appRequireInitialLoad(Context context, String userId) {
        return getLastFullSyncTime(context, userId)==null;
    }

    /**
     * Devuelve la ultima fecha de sincronizacion de un Account en un Sync Adapter
     * @param mAccount
     * @param contentAuthority
     * @return
     */
    public static String getLastSyncTime(Account mAccount, String contentAuthority) {
        long result = 0;
        try {
            Method getSyncStatus = ContentResolver.class.getMethod(
                    "getSyncStatus", Account.class, String.class);
            if (mAccount != null) {
                Object status = getSyncStatus.invoke(null, mAccount, contentAuthority);
                Class<?> statusClass = Class
                        .forName("android.content.SyncStatusInfo");
                boolean isStatusObject = statusClass.isInstance(status);
                if (isStatusObject) {
                    Field successTime = statusClass.getField("lastSuccessTime");
                    result = successTime.getLong(status);
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException |
                ClassNotFoundException | NoSuchFieldException | NullPointerException | InvocationTargetException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
        return format.format(new Date(result));
    }
    
    /**
     * Register the user in the local data base
     * @param userId
     * @param serverAddress
     * @param userGroup
     * @param userName
     * @param ctx
     */
    public static void registerUserInDataBase(String userId, int userProfileId, Long serverUserId,
                                              String serverAddress, String userGroup, String userName, String authToken, Context ctx){
		try{
	    	ctx.getContentResolver()
	    		.update(DataBaseContentProvider.INTERNAL_DB_URI, new ContentValues(),
						"INSERT INTO IDS_USER (USER_ID, USER_PROFILE_ID, SERVER_USER_ID, " +
                                " USER_NAME, SERVER_ADDRESS, USER_GROUP, AUTH_TOKEN) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
						new String[]{userId, String.valueOf(userProfileId), serverUserId.toString(),
								userName, serverAddress, userGroup, authToken});
	    }catch(Exception e){
			e.printStackTrace();
		}
    }

    /**
     * Devuelve el Id del usuario si existe AccountManager, sino devuelve null
     * @param serverAddress
     * @param userName
     * @param userGroup
     * @param ctx
     * @return
     */
	public static String getUserId(String serverAddress, String userName, String userGroup, Context ctx){
		AccountManager mAccountManager = AccountManager.get(ctx);
		for(Account account : mAccountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE)){
			if((serverAddress!=null 
					&& mAccountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_ADDRESS)!=null
					&& serverAddress.equals(mAccountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_ADDRESS)))
				&& (userName!=null 
					&& account.name!=null 
					&& userName.equals(account.name))
				&& (userGroup!=null 
					&& mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_GROUP)!=null
					&& userGroup.equals(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_GROUP)))){
				return mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param ctx
	 * @return
	 * @throws Exception 
	 */
	public static String getNewUserId(Context ctx) throws Exception{
		Cursor cursor = null;
		try{
			cursor = ctx.getContentResolver()
							.query(DataBaseContentProvider.INTERNAL_DB_URI, null,
									"SELECT MAX(USER_ID)+1 FROM IDS_USER",
									null, null);
			if(cursor!=null && cursor.moveToNext()){
				return cursor.getString(0)!=null ? cursor.getString(0) : "1";
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new Exception("Error getting a new UserId. "+e.getMessage());
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return "1";
	}
    
	/**
	 * 
	 * @return
	 */
	public static boolean isExternalStorageReadOnly() {
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
	}  
 
	/**
	 * 
	 * @return
	 */
	public static boolean isExternalStorageAvailable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}  
	
	/**
	 * 
	 * @param _files
	 * @param zipFileName
	 */
    public static void zip(String[] _files, String zipFileName) {
		try {
			int BUFFER = 20000;
			BufferedInputStream origin;
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFileName)));
			byte data[] = new byte[BUFFER];

			for (int i = 0; i < _files.length; i++) {
				FileInputStream fi = new FileInputStream(_files[i]);
				origin = new BufferedInputStream(fi, BUFFER);

				ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
				out.putNextEntry(entry);
				int count;

				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}

			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Inserta el registro en la base de datos y devuelve el ID creado
     * @param user
     * @param data
     * @param ctx
     */
    public static void registerSyncSchedulerData(User user, SchedulerSyncData data, Context ctx) throws Exception {
    	Cursor c = null;
    	try{
    		if(user!=null){
    			c = ctx.getContentResolver()
    					.query(DataBaseContentProvider.INTERNAL_DB_URI, null,
								"SELECT COUNT(*) " +
                                " FROM IDS_SCHEDULER_SYNC " +
                                " WHERE USER_ID=? AND HOUR=? AND MINUTE=? AND MONDAY=? AND TUESDAY=? " +
                                    " AND WEDNESDAY=? AND THURSDAY=? AND FRIDAY=? AND SATURDAY=? AND SUNDAY=?",
								new String[] {user.getUserId(),
                                        String.valueOf(data.getHour()),
                                        String.valueOf(data.getMinute()),
                                        data.isMonday()?"Y":"N",
                                        data.isTuesday()?"Y":"N",
                                        data.isWednesday()?"Y":"N",
                                        data.isThursday()?"Y":"N",
                                        data.isFriday()?"Y":"N",
                                        data.isSaturday()?"Y":"N",
                                        data.isSunday()?"Y":"N"}, null);
				if(c!=null && c.moveToNext() && c.getInt(0)>0){
					throw new Exception(ctx.getString(R.string.alarm_already_set));
				}
                if(c!=null){
    			    c.close();
                }
    			
    			ctx.getContentResolver()
    				.update(DataBaseContentProvider.INTERNAL_DB_URI, null,
							"INSERT INTO IDS_SCHEDULER_SYNC (USER_ID, HOUR, MINUTE, MONDAY, TUESDAY, " +
                                    " WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
							new String[]{user.getUserId(), 
    									Integer.valueOf(data.getHour()).toString(), 
    									Integer.valueOf(data.getMinute()).toString(), 
    									data.isMonday()?"Y":"N", 
										data.isTuesday()?"Y":"N", 
										data.isWednesday()?"Y":"N", 
										data.isThursday()?"Y":"N",
										data.isFriday()?"Y":"N", 
										data.isSaturday()?"Y":"N", 
										data.isSunday()?"Y":"N"});
    			
				c = ctx.getContentResolver()
						.query(DataBaseContentProvider.INTERNAL_DB_URI, null,
								"SELECT MAX(SCHEDULER_SYNC_ID) FROM IDS_SCHEDULER_SYNC",
								null, null);
				if(c!=null && c.moveToNext()){
					data.setSchedulerSyncDataId(c.getInt(0)==0?1:c.getInt(0));
					data.setActive(true);
					registerSyncSchedulerDataInAlarmManager(user, data, ctx);
				}
			}
	    }catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
    }
    
    /**
     * 
     * @param user
     * @param data
     * @param ctx
     */
    private static void registerSyncSchedulerDataInAlarmManager(User user, SchedulerSyncData data, Context ctx){
		Intent intent = new Intent(AlarmReceiver.ACTION)
							.putExtra(AlarmReceiver.KEY_CURRENT_USER_ID, user.getUserId())
							.putExtra(AlarmReceiver.KEY_CURRENT_ALARM_ID, data.getSchedulerSyncDataId());
	
		PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, data.getSchedulerSyncDataId(), intent, 0);
		
		AlarmManager alarmManager = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, data.getHour());
		calendar.set(Calendar.MINUTE, data.getMinute());
		
		if(!data.isMonday() && !data.isTuesday() && !data.isWednesday() 
				&& !data.isThursday() && !data.isFriday() && !data.isSaturday() && !data.isSunday()){
			//si aun no se ha cumplido la hora de la alarma
			if((new Date()).before(new Date(calendar.getTimeInMillis()))){
				//se agenda la alarma para el mismo dia
				alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
			}else{
				//se agenda la alarma para el dia siguiente
				alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY , alarmIntent);
			}

		}else {
			if(data.isMonday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				}
				// With setInexactRepeating(), you have to use one of the AlarmManager interval
				// constants--in this case, AlarmManager.INTERVAL_DAY.
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isTuesday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.TUESDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isWednesday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.WEDNESDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isThursday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.THURSDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isFriday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.FRIDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isSaturday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SATURDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
			if(data.isSunday()){
				if(calendar.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
					calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				}
				alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				        							AlarmManager.INTERVAL_DAY * 7, alarmIntent);
			}
		}
    }
    
    /**
     * 
     * @param data
     * @param ctx
     * @return
     * @throws Exception 
     */
    public static boolean removeSyncSchedulerData(User user, SchedulerSyncData data, Context ctx) throws Exception{
    	try{
    		if(user!=null && data!=null){
    			int rowsAffected = ctx.getContentResolver()
			    						.update(DataBaseContentProvider.INTERNAL_DB_URI,
												new ContentValues(),
			    								"DELETE FROM IDS_SCHEDULER_SYNC WHERE SCHEDULER_SYNC_ID=?",
		    									new String[]{Integer.valueOf(data.getSchedulerSyncDataId()).toString()});
    			if(rowsAffected>0){
    				removeSyncSchedulerDataInAlarmManager(user, data, ctx);
    				return true;
    			}
			}
	    }catch(Exception e){
			throw e;
		}
    	return false;
    }
    
    /**
     * 
     * @param user
     * @param data
     * @param ctx
     * @throws Exception
     */
    private static void removeSyncSchedulerDataInAlarmManager(User user, SchedulerSyncData data, Context ctx) 
    		throws Exception{
    	try{
    		if(user!=null && data!=null){
    			AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
    			Intent intent = new Intent(AlarmReceiver.ACTION)
									.putExtra(AlarmReceiver.KEY_CURRENT_USER_ID, user.getUserId())
									.putExtra(AlarmReceiver.KEY_CURRENT_ALARM_ID, data.getSchedulerSyncDataId());
    			PendingIntent p = PendingIntent.getBroadcast(ctx, data.getSchedulerSyncDataId(), intent, 0);
    			am.cancel(p);
    			p.cancel();
			}
	    }catch(Exception e){
			throw e;
		}
    }
    
    /**
     * 
     * @param data
     * @param ctx
     * @return
     * @throws Exception
     */
    public static boolean setActiveSyncSchedulerData(User user, SchedulerSyncData data, Context ctx) throws Exception{
    	try{
    		if(user!=null && data!=null){
    			int rowsAffected = ctx.getContentResolver()
    									.update(DataBaseContentProvider.INTERNAL_DB_URI,
												new ContentValues(),
    											"UPDATE IDS_SCHEDULER_SYNC SET IS_ACTIVE=? WHERE SCHEDULER_SYNC_ID=?",
    											new String[]{data.isActive()?"Y":"N", Integer.valueOf(data.getSchedulerSyncDataId()).toString()});
    			if(rowsAffected>0){
    				if(data.isActive()){
    					registerSyncSchedulerDataInAlarmManager(user, data, ctx);
    				}else{
    					removeSyncSchedulerDataInAlarmManager(user, data, ctx);
    				}
    				return true;
    			}
			}
	    }catch(Exception e){
			throw e;
		}
    	return false;
    }
    
    /**
     * 
     * @param user
     * @param ctx
     * @return
     */
    public static ArrayList<SchedulerSyncData> getSchedulerSyncDataByUser(User user, Context ctx){
    	ArrayList<SchedulerSyncData> data = new ArrayList<SchedulerSyncData>();
    	Cursor c = null;
    	try{
    		if(user!=null){
				c = ctx.getContentResolver()
						.query(DataBaseContentProvider.INTERNAL_DB_URI, null,
								new StringBuilder("SELECT SCHEDULER_SYNC_ID, HOUR, MINUTE, MONDAY, TUESDAY, ")
										.append(" WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, IS_ACTIVE ")
										.append(" FROM IDS_SCHEDULER_SYNC WHERE USER_ID=").append(user.getUserId()).toString(),
										null, null);
                if(c!=null){
                    while(c.moveToNext()){
                        data.add(new SchedulerSyncData(c.getInt(0),
                                                        c.getInt(1),
                                                        c.getInt(2),
                                                        c.getString(3).equals("Y"),
                                                        c.getString(4).equals("Y"),
                                                        c.getString(5).equals("Y"),
                                                        c.getString(6).equals("Y"),
                                                        c.getString(7).equals("Y"),
                                                        c.getString(8).equals("Y"),
                                                        c.getString(9).equals("Y"),
                                                        c.getString(10).equals("Y")));
                    }
                }
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
		return data;
    }
    
    /**
     * 
     * @param schedulerSyncDataId
     * @param ctx
     * @return
     */
    public static SchedulerSyncData getSchedulerSyncDataByID(int schedulerSyncDataId, Context ctx){
    	Cursor c = null;
    	try{
			c = ctx.getContentResolver()
					.query(DataBaseContentProvider.INTERNAL_DB_URI, null,
							new StringBuilder("SELECT HOUR, MINUTE, MONDAY, TUESDAY, ")
									.append("WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, IS_ACTIVE ")
									.append("FROM IDS_SCHEDULER_SYNC WHERE SCHEDULER_SYNC_ID=").append(schedulerSyncDataId).toString(),
									null, null);
			if(c!=null && c.moveToNext()){
				return new SchedulerSyncData(schedulerSyncDataId,
												c.getInt(0), 
												c.getInt(1), 
												c.getString(2).equals("Y"), 
												c.getString(3).equals("Y"), 
												c.getString(4).equals("Y"), 
												c.getString(5).equals("Y"), 
												c.getString(6).equals("Y"), 
												c.getString(7).equals("Y"), 
												c.getString(8).equals("Y"),
												c.getString(9).equals("Y"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
		return null;
    }
    
    /**
     * 
     * @param myByteArray
     * @param filePathName
     * @throws IOException
     */
    public static void saveToFile(byte[] myByteArray, String filePathName) throws IOException{
    	FileOutputStream fos = new FileOutputStream(filePathName);
    	fos.write(myByteArray);
    	fos.close();
    }
    
	/**
	 * Method used for encode the file to base64 binary format
	 * @param file
	 * @return encoded file format
	 */
	public static String encodeFileToBase64Binary(File file){
	    String encodedfile = null;
	    try {
	        FileInputStream fileInputStreamReader = new FileInputStream(file);
	        byte[] bytes = new byte[(int)file.length()];
	        fileInputStreamReader.read(bytes);
	        encodedfile = Base64.encodeBytes(bytes).toString();
	        fileInputStreamReader.close();
	    } catch(FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return encodedfile;
	}
    
    /**
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFromFile(File file) throws IOException{
    	ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ( (read = ios.read(buffer)) != -1 ) {
                ous.write(buffer, 0, read);
            }
        } finally { 
            try {
                 if ( ous != null ) 
                     ous.close();
            } catch ( IOException e) {
            }

            try {
                 if ( ios != null ) 
                      ios.close();
            } catch ( IOException e) {
            }
        }
        return ous.toByteArray();
    }
    
    /**
     * 
     * @param s
     * @return
     * @throws Exception
     */
    public static byte[] gzip(String s) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        OutputStreamWriter osw = new OutputStreamWriter(gzip, "UTF-8");
        osw.write(s);
        osw.close();
        return bos.toByteArray();
    }
    

    
	/**
	 * Importante! el nombre de la tabla es sencible a mayusculas o minusculas
	 * @param tableName
	 * @return
	 */
	public boolean tableExist(String tableName){
//		String query = "SELECT name FROM sqlite_master " +
//					"WHERE type='table' AND name='"+tableName+"' COLLATE NOCASE";
//		Cursor c=null;
//		try {
//			c = rawQuery(query, null);
//			return c.moveToFirst();
//		} catch (SQLiteException e) {
//			e.printStackTrace();
//			return false;
//		} finally {
//			if(c!=null){
//				c.close();
//			}
//		}
		return false;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<String> getFilesInFolder(String path) {
		File folder = new File(path);
//		Log.d(TAG, "getFilesInFolder("+path+")");
		ArrayList<String> filesName = new ArrayList<String>();
		if(folder.listFiles()!=null){
			for (final File fileEntry : folder.listFiles()) {
//				if (fileEntry.isDirectory()) {
//					//filesPathName.addAll(listFilesInFolder(fileEntry));
//				} else {
					if (fileEntry.isFile()) {
						filesName.add(fileEntry.getName());
//						Log.v(TAG, "File= " + folder.getAbsolutePath()+ "/" + fileEntry.getName());
					}
//				}
		    }
		}else{
			Log.w(TAG, "No existen archivos en el directorio: \""+folder.getPath()+"\".");
		}
		return filesName;
	}
	
//	private static ArrayList<String> listFilesForFolder(File folder) {
//		ArrayList<String> filesPathName = new ArrayList<String>();
//		for (final File fileEntry : folder.listFiles()) {
//			if (fileEntry.isDirectory()) {
//				filesPathName.addAll(listFilesForFolder(fileEntry));
//			} else {
//				if (fileEntry.isFile()) {
//					filesPathName.add(fileEntry.getName());
//					System.out.println("File= " + folder.getAbsolutePath()+ "\\" + fileEntry.getName());
//				}
//			}
//	    }
//		return filesPathName;
//	}
	
	public static String getDataDir(Context context) throws Exception {
	    return context.getPackageManager()
	            .getPackageInfo(context.getPackageName(), 0)
	            .applicationInfo.dataDir;
	}
	
	/**
	 * 
	 * @param context
	 */
	public static void vibrate(Context context){
		try {
		 	//Set the pattern for vibration   
		    long pattern[]={100,400,200,400,200,1000};
			 
	        //Start the vibration
		    Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
			//start vibration with repeated count, use -1 if you don't want to repeat the vibration
			vibrator.vibrate(pattern, -1);     
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param context
	 * @param soundType
	 */
	public static void playSound(Context context, int soundType)  {
		try {
//			Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//			MediaPlayer mMediaPlayer = new MediaPlayer();
//			mMediaPlayer.setDataSource(context, soundUri);
//			final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//			
//			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
//				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//				mMediaPlayer.setLooping(true);
//				mMediaPlayer.prepare();
//				mMediaPlayer.start();
//			}
			final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
		    //tg.startTone(ToneGenerator.TONE_PROP_BEEP);
		    //tg.startTone(ToneGenerator.TONE_PROP_ACK); //Double beeps
		    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);//Double beeps
		    //tg.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE);//Sounds all wrong
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param millis
	 * @return
	 */
	public static String parseMillisecondsToHMS(long millis, String format){
		return String.format(format, 
							TimeUnit.MILLISECONDS.toHours(millis),
							TimeUnit.MILLISECONDS.toMinutes(millis) -  
							TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), // The change is in this line
							TimeUnit.MILLISECONDS.toSeconds(millis) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));   
	}

	/**
	 * 
	 * @param floatValue
	 * @return
	 */
	public static String formatFloatTwoDecimals(float floatValue){
		return String.format("%.2f", floatValue);
	}

	/**
	 *
	 * @param ctx
	 * @param account
	 * @return
	 */
	public static User getUserByAccount(Context ctx, Account account) {
		return getUserFromAccountManager(ctx, AccountManager.get(ctx).getUserData(account,
                AccountGeneral.USERDATA_USER_ID), null);
	}

	/**
	 *
	 * @param ctx
	 * @param userId
	 * @return
	 */
	public static User getUserByIdFromAccountManager(Context ctx, String userId) {
		return getUserFromAccountManager(ctx, userId, null);
	}

	/**
	 *
	 * @param ctx
	 * @param serverUserId
     * @return
     */
	public static User getUserByServerUserIdFromAccountManager(Context ctx, String serverUserId) {
		return getUserFromAccountManager(ctx, null, serverUserId);
	}

	/**
	 *
	 * @param ctx
	 * @param userId
	 * @param serverUserId
     * @return
     */
	private static User getUserFromAccountManager(Context ctx, String userId, String serverUserId) {
		try{
			AccountManager mAccountManager = AccountManager.get(ctx);
			Account userAccount = getAccountFromAccountManager(ctx, userId, serverUserId);
			if (userAccount!=null) {
				User user = new User(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_USER_ID));
				user.setUserProfileId(Integer.valueOf(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_USER_PROFILE_ID)));
				user.setServerUserId(Integer.valueOf(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_SERVER_USER_ID)));
				user.setUserName(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_USER_NAME));
				user.setUserPass(mAccountManager.getPassword(userAccount));
				user.setServerAddress(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_SERVER_ADDRESS));
				user.setUserGroup(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_USER_GROUP));
				user.setGcmRegistrationId(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_GCM_REGISTRATION_ID));
				user.setSaveDBInExternalCard(mAccountManager.getUserData(userAccount, AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD).equals("true"));
				user.setAuthToken(mAccountManager. peekAuthToken(userAccount, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS));
				return user;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *
	 * @param ctx
	 * @param userId
	 * @return
	 */
	public static Account getAccountByIdFromAccountManager(Context ctx, String userId) {
		return getAccountFromAccountManager(ctx, userId, null);
	}

	/**
	 *
	 * @param ctx
	 * @param serverUserId
	 * @return
	 */
	public static Account getAccountByServerUserIdFromAccountManager(Context ctx, String serverUserId) {
		return getAccountFromAccountManager(ctx, null, serverUserId);
	}

	/**
	 *
	 * @param ctx
	 * @param userId
	 * @param serverUserId
	 * @return
	 */
	private static Account getAccountFromAccountManager(Context ctx, String userId, String serverUserId) {
		try{
			AccountManager mAccountManager = AccountManager.get(ctx);
			if(userId!=null) {
				for(Account account : mAccountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE)){
					if(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID).equals(userId)){
						return account;
					}
				}
			} else if (serverUserId!=null) {
				for(Account account : mAccountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE)){
					if(mAccountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID).equals(serverUserId)){
						return account;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param context
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	public static boolean moveDB(Context context, User user) 
			throws Exception{
//		boolean mExternalStorageAvailable = false;
//		boolean mExternalStorageWriteable = false;
//		String state = Environment.getExternalStorageState();
//		Log.d(TAG, "state: "+state);
//		if (Environment.MEDIA_MOUNTED.equals(state)) {
//		    // We can read and write the media
//		    mExternalStorageAvailable = mExternalStorageWriteable = true;
//		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//		    // We can only read the media
//		    mExternalStorageAvailable = true;
//		    mExternalStorageWriteable = false;
//		} else {
//		    // Something else is wrong. It may be one of many other states, but all we need
//		    //  to know is we can neither read nor write
//		    mExternalStorageAvailable = mExternalStorageWriteable = false;
//		}
//		//http://developer.android.com/reference/android/os/Environment.html#isExternalStorageRemovable()
//		if(!mExternalStorageAvailable){
//			throw new Exception("External storage is unavalible.");
//		}else if(!mExternalStorageWriteable){
//			throw new Exception("Imposible to write in external storage.");
//		}else if(!Environment.isExternalStorageRemovable()){
//			throw new Exception("The sd card is unavailable.");
//		}else{
//			//Si existe una memoria externa pero no es removible
//		}
//		return true;
//		throw new Exception("se cago!");
//		String sdpath,sd1path,usbdiskpath,sd0path; 
//		if(new File("/storage/extSdCard/").exists()) { 
//		  sdpath="/storage/extSdCard/";
//		  Log.i("Sd Cardext Path",sdpath); 
//		} 
//		if(new File("/storage/sdcard1/").exists()) { 
//		  sd1path="/storage/sdcard1/";
//		  Log.i("Sd Card1 Path",sd1path); 
//		} 
//		if(new File("/storage/usbcard1/").exists()) { 
//		  usbdiskpath="/storage/usbcard1/";
//		  Log.i("USB Path",usbdiskpath); 
//		} 
//		if(new File("/storage/sdcard0/").exists()) { 
//		  sd0path="/storage/sdcard0/";
//		  Log.i("Sd Card0 Path",sd0path); 
//		}
//		if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) { 
//		  Log.i("Environment.getExternalStorageDirectory().getAbsolutePath()",Environment.getExternalStorageDirectory().getAbsolutePath()); 
//		}
//		if(new File(Environment.getExternalStorageDirectory().getAbsolutePath()).exists()) { 
//			Log.i("Environment.getExternalStorageDirectory().getAbsolutePath()",Environment.getExternalStorageDirectory().getAbsolutePath()); 
//		}
//		if(new File(Environment.getExternalStorageDirectory().getParent()).exists()) { 
//			Log.i("new File(Environment.getExternalStorageDirectory().getParent())",new File(Environment.getExternalStorageDirectory().getParent()).getAbsolutePath()); 
//		}
//		Map<String, File> externalLocations = ExternalStorage.getAllStorageLocations();
//		File sdCard = externalLocations.get(ExternalStorage.SD_CARD);
//		if(sdCard!=null && sdCard.exists()) { 
//			Log.i("sdCard",sdCard.getAbsolutePath()); 
//		}
//		File externalSdCard = externalLocations.get(ExternalStorage.EXTERNAL_SD_CARD);
//		if(externalSdCard!=null && externalSdCard.exists()) { 
//			Log.i("externalSdCard",externalSdCard.getAbsolutePath()); 
//		}
		
		File internalFilesDir = null;
		File externalFilesDir = null;
		boolean isInInternalDir = false;
		boolean isInExternalDir = false;
		boolean useExternalFilesDir = user.isSaveDBInExternalCard();
		String dataBaseName = ApplicationUtilities.getDatabaseNameByUser(user);
    	try{
    		Log.d(TAG, "externalDir: "+context.getDatabasePath(context.getExternalFilesDir(null)+"/"+user.getUserGroup()+"/"+user.getUserName()+"/"+dataBaseName));
    		externalFilesDir = new File(context.getDatabasePath(context.getExternalFilesDir(null)+"/"+user.getUserGroup()+"/"+user.getUserName()+"/"+dataBaseName).getAbsolutePath());
    		if(externalFilesDir.exists()){
    			isInExternalDir = true;
    		}
    	}catch(Exception e){ 
    		e.printStackTrace();
    	}
    	try{
			Log.d(TAG, "internalDir: "+context.getDatabasePath(dataBaseName).getAbsolutePath());
			internalFilesDir = new File(context.getDatabasePath(dataBaseName).getAbsolutePath());
			if(internalFilesDir.exists()){
				isInInternalDir = true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    	if(isInInternalDir && useExternalFilesDir){
    		//move from internal to external files dir
    		Log.i(TAG, "Move from internal to external files dir");
    		copyAndDeleteSource(internalFilesDir, externalFilesDir);
    		return true;
    	}else if (isInExternalDir && !useExternalFilesDir){
    		//move from external to internal files dir
    		Log.i(TAG, "Move from external to internal files dir.");
    		copyAndDeleteSource(externalFilesDir, internalFilesDir);
    		return true;
    	}else if (!isInInternalDir && !isInExternalDir){
    		//data base not found
    		throw new Exception("Data base not found in external or internal directories.");
    	}else {
    		//do not to do nothing.
    		Log.i(TAG, "Do nothing.");
    		return true;
    	}
	}
	
	/**
	 * 
	 * @param source
	 * @param destination
	 * @throws IOException
	 */
	private static void copyAndDeleteSource(File source, File destination) 
			throws IOException{
		InputStream inStream = null;
		OutputStream outStream = null;
		try{
    	    inStream = new FileInputStream(source);
    	    outStream = new FileOutputStream(destination);

    	    byte[] buffer = new byte[1024];

    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
    	    	outStream.write(buffer, 0, length);
    	    }

    	    inStream.close();
    	    outStream.close();

    	    //delete the original file
    	    Log.d(TAG, source.getAbsolutePath()+".delete(): "+source.delete());

    	    Log.d(TAG, "File is copied successful!");
    	}catch(IOException e){
    	    throw e;
    	}
	}

	/**
	 * Clean users data in device and data base
	 * @param ctx
	 */
	public static void cleanUsersData(Context ctx, Account[] accounts){
    	Cursor c = null;
    	try{
    		//currents users
    		ArrayList<User> currentsUsers = new ArrayList<User>();
    		AccountManager mAccountManager = AccountManager.get(ctx);
			for(Account account : accounts){
                if (account.type.equals(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE)) {
                    currentsUsers.add(getUserByIdFromAccountManager(ctx, mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID)));
                    //Log.v(TAG+" - Current Users", getUserByIdFromAccountManager(ctx, mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID)).toString());
                }
			}
    		//registered users
    		ArrayList<User> registeredUsers = new ArrayList<User>();
			c = ctx.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT USER_ID, USER_NAME, SERVER_ADDRESS, USER_GROUP FROM IDS_USER",
                    null, null);
			if(c!=null){
				while(c.moveToNext()){
					User user = new User(c.getString(0));
					user.setUserName(c.getString(1));
					user.setServerAddress(c.getString(2));
					user.setUserGroup(c.getString(3));
					registeredUsers.add(user);
				}
				c.close();
				c=null;
			}
			registeredUsers.removeAll(currentsUsers);
			for(User userRemoved : registeredUsers){
				Log.w(TAG+" - Removed Users", userRemoved.toString());
		    	try{
		    		//delete files in device
					delete(new File(ctx.getExternalFilesDir(null)+"/"+userRemoved.getUserGroup()+"/"+userRemoved.getUserName()+"/"));
					
					//delete schedulers in database
					try{
						c = ctx.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI,
                                null, "SELECT SCHEDULER_SYNC_ID FROM IDS_SCHEDULER_SYNC WHERE USER_ID = ?",
                                new String[]{userRemoved.getUserId()}, null);
                        if(c!=null){
                            while(c.moveToNext()){
                                removeSyncSchedulerData(userRemoved, new SchedulerSyncData(c.getInt(0)), ctx);
                            }
                        }
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						if(c!=null){
							c.close();
						}
					}

					//delete logs in database
					(new SyncLogDB(ctx)).cleanLog(userRemoved.getUserId());
			    	
			    	//delete user in database
			    	ctx.getContentResolver()
						.update(DataBaseContentProvider.INTERNAL_DB_URI, 
								null,
								"DELETE FROM IDS_USER WHERE USER_ID = ?",
                                new String[]{userRemoved.getUserId()});
			    	
			    	//delete specific database for user
			    	ctx.getContentResolver()
						.update(DataBaseContentProvider.DROP_USER_DB_URI.buildUpon()
								.appendQueryParameter(DataBaseContentProvider.KEY_USER_DB_NAME, ApplicationUtilities.getDatabaseNameByUser(userRemoved))
								.appendQueryParameter(DataBaseContentProvider.KEY_USER_SAVE_DB_EXTERNAL_CARD, Boolean.valueOf(userRemoved.isSaveDBInExternalCard()).toString()).build(), 
								null,
								null, 
								null);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(c!=null){
				c.close();
			}
		}
	}
	
	/**
	 * Delete files and directories
	 * @param file
	 * @throws IOException
	 */
    private static void delete(File file) throws IOException{
    	if(file.isDirectory()){
    		//directory is empty, then delete it
    		if(file.list().length==0){
    		   file.delete();
    		   Log.d(TAG, "Directory is deleted : " + file.getAbsolutePath());
    		}else{
    		   //list all the directory contents
        	   String files[] = file.list();
        	   for (String temp : files) {
        	      //construct the file structure
        	      File fileDelete = new File(file, temp);
        	      //recursive delete
        	      delete(fileDelete);
        	   }
        	   //check the directory again, if empty then delete it
        	   if(file.list().length==0){
        		   file.delete();
        		   Log.d(TAG, "Directory is deleted : " + file.getAbsolutePath());
        	   }
    		}
    	}else{
    		//if file, then delete it
    		file.delete();
    		Log.d(TAG, "File is deleted : " + file.getAbsolutePath());
    	}
    }
    
    public static String getDatabaseNameByUser(User user){
    	try{
    		return user.getUserId()+"_"+user.getUserGroup()+"_"+user.getUserName();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return null;
    }


	/**
	 * solicitud de sincronizacion de los datos que se sincronizan en tiempo real
	 * @param context
	 * @param userId
     */
	public static void initSyncDataWithServerService(Context context, String userId){
		Intent syncDataIntent = new Intent(context, SyncDataRealTimeWithServerService.class);
		syncDataIntent.putExtra(SyncDataRealTimeWithServerService.KEY_USER_ID, userId);
		context.startService(syncDataIntent);
	}
}

package com.smartbuilders.synchronizer.ids;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.jasgcorp.ids.R;
import com.jasgcorp.ids.logsync.LogSyncData;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.CachedFileProvider;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity 
							implements OnSharedPreferenceChangeListener {
	
	private static final String TAG = SettingsActivity.class.getSimpleName();
	public static final String STATE_CURRENT_USER = "state_current_user";
	
	private AccountManager mAccountManager;
	private Account mAccount;
	private User mCurrentUser;
	private Bundle periodicSyncBundleParam;
	private ProgressDialog waitPlease;
	
	@SuppressWarnings({ "deprecation" })
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		// Set all default values once for this application
		// This must be done in the 'Main' first activity
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null && extras.containsKey(ApplicationUtilities.KEY_CURRENT_USER)){
			mCurrentUser = extras.getParcelable(ApplicationUtilities.KEY_CURRENT_USER);
		}else if(savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_USER)){
			mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
		}
		
		if(mCurrentUser==null){
			//TODO: mostrar mensaje de error al usuario antes de cerrar la ventana.
			finish();
		}else{
			// show the current value in the settings screen
			for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
				initSummary(getPreferenceScreen().getPreference(i));
			}
			
			mAccountManager = AccountManager.get(this);
			final Account availableAccounts[] = mAccountManager.getAccountsByType(getString(R.string.authenticator_acount_type));
			for(Account account : availableAccounts){
				if(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID).equals(mCurrentUser.getUserId())){
					mAccount = account;
					getPreferenceScreen().findPreference("server_address").setSummary(mCurrentUser.getServerAddress());
					((EditTextPreference)getPreferenceScreen().findPreference("server_address")).setText(mCurrentUser.getServerAddress());
					
					getPreferenceScreen().findPreference("user_name").setTitle(getString(R.string.user_name)+": "+mCurrentUser.getUserName()+".");
					getPreferenceScreen().findPreference("user_name").setSummary(getString(R.string.sync_user_group)+": "+mCurrentUser.getUserGroup()+".");

					if(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE)!=null){
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule"))
							.setEnabled(!mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE).equals("auto_sync_disabled"));

						((ListPreference) getPreferenceScreen().findPreference("auto_sync_network_mode"))
							.setValue(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE));
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_network_mode"))
							.setSummary(((ListPreference) getPreferenceScreen().findPreference("auto_sync_network_mode")).getEntry());
					}else{
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_network_mode")).setValue(null);
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).setEnabled(false);
					}

					if(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY)!=null 
							&& !mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY).equals("0")){
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule"))
							.setValue(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY));
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule"))
							.setSummary(((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).getEntry());
					}else{
						((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).setValue(null);
					}
					
					((CheckBoxPreference) getPreferenceScreen().findPreference("use_external_storage"))
						.setChecked(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD)!=null
								&& Boolean.valueOf(mAccountManager.getUserData(mAccount, AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD)));
					break;
				}
			}
			
			(getPreferenceScreen().findPreference("send_bug_report"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											sendEmailBugReport(mCurrentUser);
											return true;
										}
									});
			
			(getPreferenceScreen().findPreference("clean_log"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											cleanLog();
											return true;
										}
									});
			
			(getPreferenceScreen().findPreference("about"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											showAboutInformation();
											return true;
										}
									});
			
			(getPreferenceScreen().findPreference("sql_console_for_user"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											showSQLConsoleForUser();
											return true;
										}
									});
			
			(getPreferenceScreen().findPreference("sql_console_for_ids"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											showSQLConsoleForIDS();
											return true;
										}
									});
			
			(getPreferenceScreen().findPreference("notifications"))
									.setOnPreferenceClickListener(new OnPreferenceClickListener() {
										@Override
										public boolean onPreferenceClick(Preference preference) {
											showNotificationManager();
											return true;
										}
									});

			((CheckBoxPreference) getPreferenceScreen().findPreference("use_external_storage"))
									.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {            
								        public boolean onPreferenceChange(Preference preference, Object newValue) {
								            return moveDataBase(Boolean.valueOf(newValue.toString()));
								        }
								    }); 
			
			periodicSyncBundleParam = new Bundle();
			periodicSyncBundleParam.putBoolean(KEY_PERIODIC_SYNC_ACTIVE, true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
							.registerOnSharedPreferenceChangeListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	        				.unregisterOnSharedPreferenceChangeListener(this);
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
   		outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
    	super.onSaveInstanceState(outState);
    }
	
	@SuppressWarnings("deprecation")
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updatePreferences(getPreferenceScreen().findPreference(key));
		if(key.equals("server_address")){
			mAccountManager.setUserData(mAccount, 
					AccountGeneral.USERDATA_SERVER_ADDRESS, 
					getPreferenceScreen().findPreference(key).getSummary().toString());
			//TODO: renombrar el serverAddress para este usuario en la tabla de log de sincronizacion
		}else if(key.equals("auto_sync_schedule")){
			ListPreference listPreference = (ListPreference) findPreference(key);
			if(listPreference.getValue().equals("0")){
				ContentResolver.setSyncAutomatically(mAccount, getString(R.string.sync_adapter_content_authority), false);
            }else{
            	/*
	        	 * http://developer.android.com/training/sync-adapters/running-sync-adapter.html
	        	 * Run the Sync Adapter Periodically
	             * Turn on periodic syncing
	             */
	            ContentResolver.addPeriodicSync(mAccount,
	            								getString(R.string.sync_adapter_content_authority),
	            								periodicSyncBundleParam,
							                    Long.valueOf(listPreference.getValue().toString()));
	            /*
		         * http://developer.android.com/training/sync-adapters/running-sync-adapter.html
		         * Run the Sync Adapter After a Network Message
		         * Turn on automatic syncing for the default account and authority
		         */
	        	ContentResolver.setSyncAutomatically(mAccount, getString(R.string.sync_adapter_content_authority), true);
            }
			mAccountManager.setUserData(mAccount, 
										AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY, 
										listPreference.getValue().toString());
            listPreference.setSummary(listPreference.getEntry());
		}else if(key.equals("auto_sync_network_mode")){
			ListPreference listPreference = (ListPreference) findPreference(key);
			if(listPreference.getValue().equals("auto_sync_disabled")){
				((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).setValue("0");
				((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).setEnabled(false);
            }else{
            	((ListPreference) getPreferenceScreen().findPreference("auto_sync_schedule")).setEnabled(true);
            }
			mAccountManager.setUserData(mAccount, 
										AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE, 
										listPreference.getValue().toString());
            listPreference.setSummary(listPreference.getEntry());
		}
	}

	private void initSummary(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory cat = (PreferenceCategory) p;
			for (int i = 0; i < cat.getPreferenceCount(); i++) {
				initSummary(cat.getPreference(i));
			}
    	} else {
    		updatePreferences(p);
	    }
	}

	private void updatePreferences(Preference p) {
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary(editTextPref.getText());
	    }
	}
	
	/**
	 * 
	 * @param currentUser
	 */
	private void sendEmailBugReport(User currentUser){
		waitPlease = ProgressDialog.show(this, getString(R.string.loading_log_file), getString(R.string.wait_please), true, false);
        new AsyncTask<Object, Void, Intent>() {
        	
        	private Context ctx;
        	private final String KEY_ERROR_MESSAGE = "ERR_MSG";
        	
            @Override
            protected Intent doInBackground(Object... params) {
            	ctx = (Context) params[0];
            	Intent emailIntent = new Intent(Intent.ACTION_SEND);
        	    try {
        	    	emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});
                	emailIntent.putExtra(Intent.EXTRA_CC, new String[]{""});
        			emailIntent.putExtra(Intent.EXTRA_BCC, new String[]{""});
        			emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + getString(R.string.bug_report));
        			emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.bug_report_email_message));
        			
       				generateBugReportFile(ApplicationUtilities.getSyncLogByUser((User) params[1], ctx),
				       						getCacheDir() + File.separator + getString(R.string.bug_report_file_name),
				       						ctx); 

       				// need this to prompts email client only
        	        emailIntent.setType("message/rfc822");
        	
        			//Add the attachment by specifying a reference to our custom ContentProvider 
        		    //and the specific file of interest 
        		    emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://" + CachedFileProvider.AUTHORITY + "/" + getString(R.string.bug_report_file_name) + ".txt")); 
        		    return emailIntent;
        	    } catch(android.content.ActivityNotFoundException ex){
        	    	ex.printStackTrace();
        	    	emailIntent.putExtra(KEY_ERROR_MESSAGE, getString(R.string.no_email_client_installed_error));
        	    } catch (Throwable t) {
        	    	t.printStackTrace();
        	    	emailIntent.putExtra(KEY_ERROR_MESSAGE, t.getMessage());
        		}
                return emailIntent;
            }

            @Override
            protected void onPostExecute(Intent emailIntent) {
            	try{
	            	if(emailIntent.getExtras().containsKey(KEY_ERROR_MESSAGE)){
	            		Toast.makeText(ctx, emailIntent.getExtras().getString(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
	            	}else{
	            		startActivity(Intent.createChooser(emailIntent, getString(R.string.email_client_label)));
	            	}
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            	waitPlease.dismiss();
            }
            
        	/**
        	 * 
        	 * @param arrayList
        	 * @param fileName
        	 * @param ctx
        	 * @throws Throwable 
        	 */
        	private void generateBugReportFile(ArrayList<LogSyncData> arrayList, String fileName, Context ctx) throws Throwable{
        		//check if external storage is available
        		if (!ApplicationUtilities.isExternalStorageAvailable() 
        				|| ApplicationUtilities.isExternalStorageReadOnly()) {  
        			throw new Throwable(ctx.getString(R.string.external_storage_unavailable));
        		} else {
        		    File file = new File(fileName+".txt"); 
        		    BufferedWriter bufferedWriter = null;
        		    try {
        		    	if(!file.createNewFile()){
        		    		file.delete();
        		    		file.createNewFile();
        		    	}
        				bufferedWriter = new BufferedWriter(new FileWriter(file));
        		    	for(LogSyncData line : arrayList){
        		    		bufferedWriter.append(line.getLogDateStringFormat()).append(": ").append(line.getLogMessage()).append("\n");
        				}
        		    	//ApplicationUtilities.zip(new String[]{(fileName+".txt")}, fileName+".rar");
        			} catch (IOException e1) {
        				e1.printStackTrace();
        			} finally {
        				if(bufferedWriter!=null){
        					try {
        						bufferedWriter.close();
        					} catch (IOException e) {
        						e.printStackTrace();
        					}
        				}
        			}
        		}
        	}
            
        }.execute(this, currentUser);
    }
	
	private void cleanLog() {
		final Context context = this;
		new AlertDialog.Builder(context)
						.setMessage(R.string.clean_log_question)
						.setPositiveButton(R.string.accept, new AlertDialog.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									ApplicationUtilities.cleanLog(context, mCurrentUser.getUserId(), 0);
									Toast.makeText(context, context.getString(R.string.log_clean_succesfully), Toast.LENGTH_LONG).show();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						})
						.setNegativeButton(R.string.cancel, null)
						.show();
	}
	
	private void showAboutInformation(){
		startActivity(new Intent(this, AboutActivity.class));
	}
	
	private void showSQLConsoleForUser(){
		startActivity((new Intent(this, SQLConsoleActivity.class)).putExtra(SQLConsoleActivity.KEY_CURRENT_USER, (Parcelable) mCurrentUser));
	}
	
	private void showSQLConsoleForIDS(){
		startActivity((new Intent(this, SQLConsoleActivity.class)).putExtra(SQLConsoleActivity.KEY_CURRENT_USER, (Parcelable) null));
	}
	
	private void showNotificationManager(){
		startActivity((new Intent(this, NotificationsManagerActivity.class)).putExtra(NotificationsManagerActivity.KEY_CURRENT_USER, (Parcelable) mCurrentUser));
	}
	
	/**
	 * 
	 * @return
	 */
	private boolean moveDataBase(boolean useExternalStorage){
		mCurrentUser.setSaveDBInExternalCard(useExternalStorage);
		waitPlease = ProgressDialog.show(this, getString(R.string.moving_database), getString(R.string.wait_please), true, false);
        try {
        	final String KEY_ERROR_MESSAGE = "ERR_MSG";
			Intent responseIntent = new AsyncTask<Object, Void, Intent>() {
									    @Override
									    protected Intent doInBackground(Object... params) {
									    	Intent responseIntent = new Intent((Context) params[0], SettingsActivity.class);
										    try {
												ApplicationUtilities.moveDB((Context) params[0], (User) params[1]);
										    } catch (Exception e) {
										    	e.printStackTrace();
										    	responseIntent.putExtra(KEY_ERROR_MESSAGE, e.getMessage());
											}
									        return responseIntent;
									    }
						
									    @Override
									    protected void onPostExecute(Intent responseIntent) {
									    	waitPlease.dismiss();
									    }
									}.execute(this, mCurrentUser).get();
			
        	if(responseIntent!=null 
    			&& responseIntent.getExtras()!=null 
    			&& responseIntent.getExtras().containsKey(KEY_ERROR_MESSAGE)){
        		//Si hay un mensaje de error
        		new AlertDialog.Builder(this)
				        	    .setMessage(responseIntent.getExtras().getString(KEY_ERROR_MESSAGE))
				        	    .setNeutralButton(R.string.accept, null)
				        	    .setIcon(android.R.drawable.ic_dialog_alert)
				        	    .show();
        		mCurrentUser.setSaveDBInExternalCard(!mCurrentUser.isSaveDBInExternalCard());
        	}else{
        		//Si todo sale bien.
	            mAccountManager.setUserData(mAccount, 
						AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD, 
						Boolean.valueOf(mCurrentUser.isSaveDBInExternalCard()).toString());
        		Toast.makeText(this, getString(R.string.database_moved_successfully), Toast.LENGTH_LONG).show();
        		return true;
        	}
		} catch (InterruptedException e) {
			e.printStackTrace();
			mCurrentUser.setSaveDBInExternalCard(!mCurrentUser.isSaveDBInExternalCard());
		} catch (ExecutionException e) {
			e.printStackTrace();
			mCurrentUser.setSaveDBInExternalCard(!mCurrentUser.isSaveDBInExternalCard());
		}
        return false;
	}
	
	@Override
	protected void onStop() {
		if(waitPlease!=null && waitPlease.isShowing()){
			waitPlease.cancel();
		}
		super.onStop();
	}
}

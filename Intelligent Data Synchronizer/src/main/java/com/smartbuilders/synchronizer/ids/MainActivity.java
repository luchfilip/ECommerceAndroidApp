package com.smartbuilders.synchronizer.ids;

import java.util.ArrayList;

import com.jasgcorp.ids.R;
import com.jasgcorp.ids.logsync.LogSyncAdapter;
import com.jasgcorp.ids.logsync.LogSyncData;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.AccountUtilities;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.jasgcorp.ids.utils.anim.FadeInFadeOutTextView;

import android.os.Bundle;
import android.os.Parcelable;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ActionBar;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author jsarco before 08.03.2016
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	
	private String TAG = this.getClass().getSimpleName();
	public static final String STATE_CURRENT_USER = "state_current_user";
	public static final String STATE_SYNC_PROGRESS = "state_sync_progress";
	public static final String STATE_SYNC_INIT_TIME = "state_sync_init_time";
	public static final String STATE_SHOW_WAIT_PLEASE_SYNC_STARTING = "state_show_wait_please_sync_starting";
	public static final String STATE_SHOW_WAIT_PLEASE_SYNC_STOPING = "state_show_wait_please_sync_stoping";

    private AccountManager mAccountManager;
    private Button startStopButton;
	private boolean isSyncRunning;
	private User mCurrentUser;
	private ListView mSyncLogsList;
	private TextView lastSuccessfulSyncLabel;
	private TextView lastSuccessfulSync;
	private TextView syncProgress;
	private LogSyncAdapter logSyncAdapter;
	private boolean finishActivityOnResultOperationCanceledException;
	private FadeInFadeOutTextView syncProgressAnimation;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
	// Instance fields
	private Account mAccount;
	private ProgressDialog syncStartingWaitPlease;
	private ProgressDialog syncStopingWaitPlease;
	private boolean showWaitPleaseSyncStarting;
	private boolean showWaitPleaseSyncStoping;
	private long syncInitTime;
	
	private BroadcastReceiver syncAdapterReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	if(intent!=null && intent.getAction()!=null){
        		Bundle extras = intent.getExtras();
        		if(extras!=null){
        			if(extras.containsKey(SyncAdapter.USER_ID) 
        					&& extras.getString(SyncAdapter.USER_ID).equals(mCurrentUser.getUserId())){
        			
	        			if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_PROGRESS) 
		        				&& extras.containsKey(SyncAdapter.LOG_MESSAGE)){
		        			syncProgress.setText(extras.getString(SyncAdapter.LOG_MESSAGE)+"%");
		        			lastSuccessfulSync.setText(ApplicationUtilities.parseMillisecondsToHMS(System.currentTimeMillis() - syncInitTime, ApplicationUtilities.TIME_FORMAT_1));
		        		}else if(extras.containsKey(SyncAdapter.LOG_MESSAGE)){
	        				logSyncAdapter.addItem(new LogSyncData(intent.getAction(), extras.getString(SyncAdapter.LOG_MESSAGE), extras.getString(SyncAdapter.LOG_MESSAGE_DETAIL)));
		        		}
	        			if(extras.containsKey(SyncAdapter.SYNC_INIT_TIME)){
	        				syncInitTime = extras.getLong(SyncAdapter.SYNC_INIT_TIME);
	        			}
	        			
		        		if(intent.getAction().equals(SyncAdapter.AUTHENTICATOR_EXCEPTION) 
		        				|| intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
		        				|| intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)
		        				|| intent.getAction().equals(SyncAdapter.IO_EXCEPTION)
		        				|| intent.getAction().equals(SyncAdapter.GENERAL_EXCEPTION)){
		        			//Vibrar
		        			if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
		            				|| intent.getAction().equals(SyncAdapter.IO_EXCEPTION)
		            				|| intent.getAction().equals(SyncAdapter.GENERAL_EXCEPTION)){
		        				ApplicationUtilities.vibrate(context);
		        				ApplicationUtilities.playSound(context, 0);
		        			}
		        			closeSyncStartingWaitPleaseDialog();
		        			closeSyncStopingWaitPleaseDialog();
		        			updateUserInterface(false);
		        		}else if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_STARTED)){
		        			closeSyncStartingWaitPleaseDialog();
		        			closeSyncStopingWaitPleaseDialog();
		        			updateUserInterface(true);
		        		}
	        		}else if(intent.getAction().equals(ConsumeWebService.SHOW_TOAST_MESSAGE) 
	        				&& extras.containsKey(ConsumeWebService.MESSAGE)){
        				Toast.makeText(context, extras.getString(ConsumeWebService.MESSAGE), Toast.LENGTH_LONG).show();
	        		}else{
	        			Toast.makeText(context, "Ha llegado un mensaje para otro usuario.", Toast.LENGTH_SHORT).show();
	        		}
        		}
        		//TODO: manejar los broadcast de la sincronizacion periodica.
        	}
        }
    };
    
    @Override
    protected void onStart() {
    	super.onStart();
        try{
	        IntentFilter intentFilter = new IntentFilter(SyncAdapter.SYNCHRONIZATION_STARTED);
	        intentFilter.addAction(SyncAdapter.PERIODIC_SYNCHRONIZATION_STARTED);
			intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_CANCELED);
			intentFilter.addAction(SyncAdapter.PERIODIC_SYNCHRONIZATION_CANCELED);
	        intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_PROGRESS);
			intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_FINISHED);
			intentFilter.addAction(SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED);
			intentFilter.addAction(SyncAdapter.AUTHENTICATOR_EXCEPTION);
			intentFilter.addAction(SyncAdapter.GENERAL_EXCEPTION);
			intentFilter.addAction(SyncAdapter.IO_EXCEPTION);
			intentFilter.addAction(SyncAdapter.OPERATION_CANCELED_EXCEPTION);
			intentFilter.addAction(SyncAdapter.XML_PULL_PARSE_EXCEPTION);
			intentFilter.addAction(ConsumeWebService.SHOW_TOAST_MESSAGE);
			getApplicationContext().registerReceiver(syncAdapterReceiver, intentFilter);
		}catch(Exception e){
			e.printStackTrace();
		}
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Typeface italicDigital = Typeface.createFromAsset(getAssets(), "fonts/ds_digii.ttf");
        Typeface impactLabelReversed = Typeface.createFromAsset(getAssets(), "fonts/impact_label_reversed.ttf");

        mAccountManager = AccountManager.get(this);
        //mAccountManager = (AccountManager) getSystemService(Context.ACCOUNT_SERVICE);

        startStopButton = (Button) findViewById(R.id.start_stop_sync);
        
        lastSuccessfulSyncLabel = (TextView)findViewById(R.id.last_successful_sync_label);
        lastSuccessfulSyncLabel.setTypeface(impactLabelReversed);
        
        lastSuccessfulSync = (TextView) findViewById(R.id.last_successful_sync);
        lastSuccessfulSync.setTypeface(italicDigital);

        syncProgress = (TextView) findViewById(R.id.sync_progress);
	    syncProgress.setTypeface(italicDigital);

        ((TextView)findViewById(R.id.sync_progress_label)).setTypeface(impactLabelReversed);
        ((TextView)findViewById(R.id.log_sync_label)).setTypeface(impactLabelReversed);
        
        //initialization of onClickListener to the buttons
		findViewById(R.id.start_stop_sync).setOnClickListener(this);
        
		Bundle extras = this.getIntent().getExtras();
		if(extras!=null){
			if(mCurrentUser==null && 
				extras.containsKey(STATE_CURRENT_USER)){
				mCurrentUser = extras.getParcelable(STATE_CURRENT_USER);
	        }
		}
		
        ArrayList<LogSyncData> synchronizationLog = new ArrayList<LogSyncData>();
        mSyncLogsList = (ListView) findViewById(R.id.sync_logs);
        logSyncAdapter = new LogSyncAdapter(MainActivity.this, synchronizationLog);
        
        mSyncLogsList.setAdapter(logSyncAdapter);
        //code to add header and footer to listview
        LayoutInflater inflater = getLayoutInflater();

        /********************************************************************/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.parent_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //code to add header and footer to listview
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_header, mDrawerList,
                										false);
        ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.drawer_footer, mDrawerList,
                										false);
        footer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawer(mDrawerList);
				addNewAccount(getString(R.string.authenticator_acount_type), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
			}
		});
        mDrawerList.addHeaderView(header, null, false);
        mDrawerList.addFooterView(footer, null, false);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, new String[0]));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        // enable ActionBar app icon to behave as action to toggle nav drawer
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // If your minSdkVersion is 11 or higher, instead use:
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                //R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        /********************************************************************/
        
        if (savedInstanceState != null) {
        	mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
        	syncProgress.setText(savedInstanceState.getString(STATE_SYNC_PROGRESS));
        	syncInitTime = savedInstanceState.getLong(STATE_SYNC_INIT_TIME);
        	showWaitPleaseSyncStarting 	= savedInstanceState.getBoolean(STATE_SHOW_WAIT_PLEASE_SYNC_STARTING);
        	showWaitPleaseSyncStoping 	= savedInstanceState.getBoolean(STATE_SHOW_WAIT_PLEASE_SYNC_STOPING);
        	invalidateOptionsMenu();
        }
        
        syncProgressAnimation = new FadeInFadeOutTextView(this, syncProgress);
    }
    
    /********************************************************************/
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        selectItem(position, mAccountManager.getAccountsByType(getString(R.string.authenticator_acount_type))[position-1]);
    }

    private void selectItem(int position, Account account) {
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        loadUserData(account);
    }
    
    @Override
    public void setTitle(CharSequence title) {
        getActionBar().setTitle(getString(R.string.app_name_abrev)+": "+title);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i(TAG, "onPostCreate");
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    /********************************************************************/
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	Log.i(TAG, "onSaveInstanceState");
   		outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
    	outState.putString(STATE_SYNC_PROGRESS, syncProgress.getText().toString());
    	outState.putLong(STATE_SYNC_INIT_TIME, syncInitTime);
    	outState.putBoolean(STATE_SHOW_WAIT_PLEASE_SYNC_STARTING, showWaitPleaseSyncStarting);
    	outState.putBoolean(STATE_SHOW_WAIT_PLEASE_SYNC_STOPING, showWaitPleaseSyncStoping);
    	super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");
    }
    
    /*
     * Inicia o detiene la sincronizacion. 
     */
    private void startStopSync(){
    	if(isSyncRunning){
    		stopSync();
    	}else{
    		if(NetworkConnectionUtilities.isOnline(this) 
    				&& (NetworkConnectionUtilities.isWifiConnected(this))||NetworkConnectionUtilities.isMobileConnected(this)) {
    			try {
					startSync();
				} catch (Exception e) {
					e.printStackTrace();
		            new AlertDialog.Builder(this)
						.setMessage(e.getMessage())
						.setPositiveButton(R.string.accept, null)
						.show();
				}
	        } else {
        		//show network connection unavailable error.
        		Toast.makeText(this, R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
	        }
    	}
    }
    
    private void startSync() throws Exception{
    	if(!isSyncRunning){
			for(Account account : mAccountManager.getAccountsByType(getString(R.string.authenticator_acount_type))){
				if(ApplicationUtilities.isSyncActive(account, getString(R.string.sync_adapter_content_authority))){
					throw new Exception(getString(R.string.sync_already_running_for_user, account.name));
				}
			}
    		syncInitTime = System.currentTimeMillis();
    				
    		showWaitPleaseSyncStarting = true;
    		showSyncStartingWaitPleaseDialog();
	    	// Pass the settings flags by inserting them in a bundle
	        Bundle settingsBundle = new Bundle();
	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
	        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
	        //ContentResolver.setIsSyncable(mAccount, getString(R.string.sync_adapter_content_authority), 1);
	        /*
	         * Request the sync for the default account, authority, and
	         * manual sync settings
	         */
	        ContentResolver.requestSync(mAccount, getString(R.string.sync_adapter_content_authority), settingsBundle);
	        isSyncRunning = true;
        }
    }
    
    /**
     * 
     */
    private void stopSync(){
    	if(isSyncRunning || ApplicationUtilities.isSyncActive(mAccount, getString(R.string.sync_adapter_content_authority))){
    		showWaitPleaseSyncStoping = true;
    		showSyncStopingWaitPleaseDialog();
	    	//ContentResolver.setIsSyncable(mAccount, getString(R.string.sync_adapter_content_authority), 0);
	    	ContentResolver.cancelSync(mAccount, getString(R.string.sync_adapter_content_authority));
	    	isSyncRunning = false;
    	}
    }
    
    /**
     * 
     */
    private void closeSyncStartingWaitPleaseDialog(){
    	try{
    		showWaitPleaseSyncStarting 	= false;
    		if(syncStartingWaitPlease!=null && syncStartingWaitPlease.isShowing()){
    			syncStartingWaitPlease.dismiss();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @param message
     */
    private void showSyncStartingWaitPleaseDialog(){
    	Log.d(TAG, "showSyncStartingWaitPleaseDialog("+getString(R.string.starting_sync)+")");
    	syncStartingWaitPlease = ProgressDialog.show(this, getString(R.string.starting_sync), getString(R.string.wait_please), true, false);
    	syncStartingWaitPlease.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopSync();
			}
		});
    }
    
    /**
     * 
     */
    private void closeSyncStopingWaitPleaseDialog(){
    	try{
    		showWaitPleaseSyncStoping 	= false;
    		if(syncStopingWaitPlease!=null && syncStopingWaitPlease.isShowing()){
    			syncStopingWaitPlease.dismiss();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
    
    /**
     * 
     * @param message
     */
    private void showSyncStopingWaitPleaseDialog(){
    	Log.d(TAG, "showSyncStopingWaitPleaseDialog("+getString(R.string.starting_sync)+")");
    	syncStopingWaitPlease = ProgressDialog.show(this, getString(R.string.starting_sync), getString(R.string.wait_please), true, false);
    }
    
    private void updateUserInterface(boolean isSyncRunning){
    	if(showWaitPleaseSyncStarting){
    		showSyncStartingWaitPleaseDialog();
    	}else if(showWaitPleaseSyncStoping){
    		showSyncStopingWaitPleaseDialog();
    	}
    	
    	this.isSyncRunning = isSyncRunning;
    	
    	if(isSyncRunning){
    		lastSuccessfulSyncLabel.setText(getString(R.string.sync_duration));
    		lastSuccessfulSync.setText(ApplicationUtilities.parseMillisecondsToHMS(0, ApplicationUtilities.TIME_FORMAT_1));
//    		startRepeatingTask();
    		
    		syncProgressAnimation.startAnimations();
    		try{
    			mSyncLogsList.invalidateViews();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
            startStopButton.setText(R.string.stop_sync);
    	}else{
    		lastSuccessfulSyncLabel.setText(getString(R.string.last_succesful_sync_label));
    		lastSuccessfulSync.setText(AccountUtilities.getLastSyncTime(mAccount, getString(R.string.sync_adapter_content_authority)));
//    		stopRepeatingTask();
    		syncProgress.setText(R.string.dash_line);
    		syncProgressAnimation.stopAnimations();
        	try{
        		//TODO: revisar porque falla ClassCastException en equipos menores a KIT-KAT
        		mSyncLogsList.invalidateViews();
        	}catch(Exception e){
    			e.printStackTrace();
    		}
    		startStopButton.setText(R.string.start_sync);
    	}
    }
    
    private void loadUserData(Account account){
    	mCurrentUser = ApplicationUtilities.getUserByIdFromAccountManager(getApplicationContext(), mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
    	
    	mAccount = account;
    	ContentResolver.setIsSyncable(mAccount, getString(R.string.sync_adapter_content_authority), 1);
    	setTitle(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_NAME));
        lastSuccessfulSync.setText(AccountUtilities.getLastSyncTime(account, getString(R.string.sync_adapter_content_authority)));
		//TODO: colocar un aviso de Wait Please
		logSyncAdapter.refreshResultList(ApplicationUtilities.getSyncLogByUser(mCurrentUser, LogSyncData.VISIBLE, this));

		updateUserInterface(ApplicationUtilities.isSyncActive(mAccount, getString(R.string.sync_adapter_content_authority)));
    }
    
    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
            		Bundle bnd = future.getResult();
                    if(bnd!=null && bnd.containsKey(AccountManager.KEY_ACCOUNT_NAME)){
                    	String userId = bnd.getBundle(AccountManager.KEY_USERDATA)
                    						.getString(AccountGeneral.USERDATA_USER_ID);
                    	final Account availableAccounts[] = mAccountManager.getAccountsByType(getString(R.string.authenticator_acount_type));
                		if (availableAccounts!=null && availableAccounts.length>0) {
            				for(Account account : availableAccounts){
            					if(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID).equals(userId)){
            						mCurrentUser = ApplicationUtilities.getUserByIdFromAccountManager(getApplicationContext(), userId);
                        			//TODO: decidir si se quedara este mensaje al crear el usuario
                        			showMessage("Account was created. "+mCurrentUser);
            						break;
            					}
            				}
                		}
                    }
                } catch(OperationCanceledException e){
                	if(finishActivityOnResultOperationCanceledException){
                		finish();
                	}
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }, null);
    }

    private void showMessage(final String msg) {
    	if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.start_stop_sync:
				startStopSync();
				break;
	        default:
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		final Account availableAccounts[] = mAccountManager.getAccountsByType(getString(R.string.authenticator_acount_type));
		if(availableAccounts!=null && availableAccounts.length>0){
			//Se carga la lista de los usuarios del panel izquierdo
			String[] accountsNames = new String[availableAccounts.length];
			for(int i = 0; i<availableAccounts.length ; i++){
				accountsNames[i] = mAccountManager.getUserData(availableAccounts[i], AccountGeneral.USERDATA_USER_GROUP) + 
									": " + mAccountManager.getUserData(availableAccounts[i], AccountGeneral.USERDATA_USER_NAME);
			}
			// set up the drawer's list view with items and click listener
	        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, accountsNames));

			if(mCurrentUser!=null){
				for(int i = 0; i<availableAccounts.length ; i++){
					if(mAccountManager.getUserData(availableAccounts[i], AccountGeneral.USERDATA_USER_ID).equals(mCurrentUser.getUserId())){
						selectItem(i+1, availableAccounts[i]);
						break;
					}
				}
			}else{
				selectItem(1, availableAccounts[0]);
			}
	    }

		if(mAccount==null){
			addNewAccount(getString(R.string.authenticator_acount_type), AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
			finishActivityOnResultOperationCanceledException = true;
		}else{
			finishActivityOnResultOperationCanceledException = false;

        	updateUserInterface(ApplicationUtilities.isSyncActive(mAccount, getString(R.string.sync_adapter_content_authority)));

			if(!ApplicationUtilities.checkPlayServices(this)){
				((DrawerLayout)findViewById(R.id.parent_layout)).setVisibility(View.GONE);
				ActionBar actionBar = getActionBar();
			    actionBar.hide();
			}else if(((DrawerLayout)findViewById(R.id.parent_layout)).getVisibility()==View.GONE){
				((DrawerLayout)findViewById(R.id.parent_layout)).setVisibility(View.VISIBLE);
				ActionBar actionBar = getActionBar();
			    actionBar.show();
			}
			
			//new AsyncTask<User, Void, Object>() {
			//    @Override
			//    protected Object doInBackground(User... user) {
			//    	try{
			//			Cursor c = getContentResolver().query(DataBaseContentProvider
			//														.REMOTE_DB_URI.buildUpon()
			//														.appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user[0].getUserId())
			//														.build(), 
			//													null, 
			//													"select * from ids_user", 
			//													null, 
			//													null);
			//			StringBuffer sb = new StringBuffer("> ");
			//			for(int i =0 ; i<c.getColumnCount(); i++){
			//				sb.append(c.getColumnName(i)).append(", ");
			//			}
			//			Log.d(TAG, sb.toString());
			//			while(c.moveToNext()){
			//				sb = new StringBuffer("> ");
			//				for(int i =0 ; i<c.getColumnCount(); i++){
			//					sb.append(c.getString(i)).append(", ");
			//				}
			//				Log.d(TAG, sb.toString());
			//			}
			//		}catch(Exception e){
			//			e.printStackTrace();
			//		}
			//        return null;
			//    }
			//}.execute(mCurrentUser);
		}
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        menu.findItem(R.id.action_sync_scheduler).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    
	// This method is called once the menu is selected
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        Intent i;
        switch (item.getItemId()) {
        case R.id.action_settings:
        	// Launch settings activity
        	i = new Intent(this, SettingsActivity.class);
        	i.putExtra(ApplicationUtilities.KEY_CURRENT_USER, (Parcelable) mCurrentUser);
        	startActivity(i);
        	break;
        case R.id.action_sync_scheduler:
        	// Launch scheduler manager activity
        	i = new Intent(this, SchedulerManagerActivity.class);
        	i.putExtra(SchedulerManagerActivity.KEY_CURRENT_USER, (Parcelable) mCurrentUser);
        	startActivity(i);
        	break;
        case R.id.action_log_settings:
        	break;
        }
        return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		try{
    		unregisterReceiver(syncAdapterReceiver);
    	}catch(Exception e){ }
	}
	
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	closeSyncStopingWaitPleaseDialog();
    	closeSyncStartingWaitPleaseDialog();
    }
}


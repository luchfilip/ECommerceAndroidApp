package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 2/6/2016.
 */
public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    private static final String STATE_SYNCHRONIZATION_STATE = "STATE_SYNCHRONIZATION_STATE";

    private static final int SYNC_RUNNING = 1;
    private static final int SYNC_ERROR = 2;
    private static final int SYNC_STOPED = 3;
    private static final int SYNC_FINISHED = 4;

    private AccountManager mAccountManager;
    private boolean finishActivityOnResultOperationCanceledException;
    private User mCurrentUser;
    private int mSynchronizationState;

    private BroadcastReceiver syncAdapterReceiver =  new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null && intent.getAction()!=null){
                Bundle extras = intent.getExtras();
                if(extras!=null){
                    if(extras.containsKey(SyncAdapter.USER_ID) && mCurrentUser!=null
                            && extras.getString(SyncAdapter.USER_ID).equals(mCurrentUser.getUserId())){
                        if (intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_STARTED)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_PROGRESS)) {
                            findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
                            findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
                            mSynchronizationState = SYNC_RUNNING;
                        } else if (intent.getAction().equals(SyncAdapter.AUTHENTICATOR_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)
                                || intent.getAction().equals(SyncAdapter.IO_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.GENERAL_EXCEPTION)){
                            if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)){
                                initApp();
                                mSynchronizationState = SYNC_FINISHED;
                            }else{
                                mSynchronizationState = SYNC_ERROR;
                                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                                findViewById(R.id.progressContainer).setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        try {
            IntentFilter intentFilter = new IntentFilter(SyncAdapter.SYNCHRONIZATION_STARTED);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_CANCELED);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_PROGRESS);
            intentFilter.addAction(SyncAdapter.SYNCHRONIZATION_FINISHED);
            intentFilter.addAction(SyncAdapter.AUTHENTICATOR_EXCEPTION);
            intentFilter.addAction(SyncAdapter.GENERAL_EXCEPTION);
            intentFilter.addAction(SyncAdapter.IO_EXCEPTION);
            intentFilter.addAction(SyncAdapter.OPERATION_CANCELED_EXCEPTION);
            intentFilter.addAction(SyncAdapter.XML_PULL_PARSE_EXCEPTION);
            getApplicationContext().registerReceiver(syncAdapterReceiver, intentFilter);
        } catch(Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey(STATE_SYNCHRONIZATION_STATE)){
                mSynchronizationState = savedInstanceState.getInt(STATE_SYNCHRONIZATION_STATE);
            }
        }

        mAccountManager = AccountManager.get(this);
        mCurrentUser = Utils.getCurrentUser(this);

        final Account availableAccounts[] = mAccountManager
                .getAccountsByType(getString(R.string.authenticator_acount_type));

        findViewById(R.id.exit_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.reTry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
                if (availableAccounts != null && availableAccounts.length > 0) {
                    checkInitialLoad(mAccountManager, availableAccounts[0]);
                }
            }
        });


        if (availableAccounts != null && availableAccounts.length > 0) {
            if(mSynchronizationState==SYNC_ERROR){
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
            }else{
                checkInitialLoad(mAccountManager, availableAccounts[0]);
            }
        } else {
            addNewAccount(getString(R.string.authenticator_acount_type),
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            finishActivityOnResultOperationCanceledException = true;
        }
    }

    /**
     * Add new account to the account manager
     * @param accountType
     * @param authTokenType
     */
    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType,
                authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();
                            if(bnd!=null && bnd.containsKey(AccountManager.KEY_ACCOUNT_NAME)){
                                String userId = bnd.getBundle(AccountManager.KEY_USERDATA)
                                        .getString(AccountGeneral.USERDATA_USER_ID);
                                final Account availableAccounts[] = mAccountManager
                                        .getAccountsByType(getString(R.string.authenticator_acount_type));
                                if (availableAccounts.length>0) {
                                    for(Account account : availableAccounts){
                                        if(mAccountManager.getUserData(account,
                                                AccountGeneral.USERDATA_USER_ID).equals(userId)){
                                            mCurrentUser = ApplicationUtilities.getUserByIdFromAccountManager(SplashScreen.this,
                                                    mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                                            checkInitialLoad(mAccountManager, account);
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
                        }
                    }
                }, null);
    }

    private void initApp(){
        findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
        findViewById(R.id.progressContainer).setVisibility(View.GONE);
        Utils.createImageFiles(this, mCurrentUser);
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Verifica si tiene que hacerse la carga inicial
     */
    private void checkInitialLoad(AccountManager accountManager, Account account) {
        findViewById(R.id.progressContainer).setVisibility(View.GONE);

        ContentResolver.setIsSyncable(account, getString(R.string.sync_adapter_content_authority), 1);

        mCurrentUser = ApplicationUtilities.getUserByIdFromAccountManager(this,
                accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));

        if (mCurrentUser!=null && (Utils.appRequireInitialLoadOfGlobalData(this)
                || Utils.appRequireInitialLoadOfUserData(this, mCurrentUser))) {
                if(NetworkConnectionUtilities.isOnline(this)
                        && (NetworkConnectionUtilities.isWifiConnected(this))||NetworkConnectionUtilities.isMobileConnected(this)) {
                    findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
                    if(!ApplicationUtilities.isSyncActive(account, getString(R.string.sync_adapter_content_authority))){
                        Bundle settingsBundle = new Bundle();
                        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                        ContentResolver.requestSync(account, getString(R.string.sync_adapter_content_authority), settingsBundle);
                        mSynchronizationState = SYNC_RUNNING;
                    }
                } else {
                    //show network connection unavailable error.
                    Toast.makeText(this, R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
                    //TODO: mostrar en pantalla error de conexion
                    findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                    findViewById(R.id.progressContainer).setVisibility(View.GONE);
                    mSynchronizationState = SYNC_ERROR;
                }
        } else if (mCurrentUser!=null) {
            mSynchronizationState = SYNC_FINISHED;
            initApp();
        } else {
            //TODO: mostrar error en pantalla
            //startActivity(new Intent(this, SplashScreen.class));
            //finish();
            Log.d(TAG, "mCurrentUser is null");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            unregisterReceiver(syncAdapterReceiver);
        }catch(Exception e){ }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SYNCHRONIZATION_STATE, mSynchronizationState);
        super.onSaveInstanceState(outState);
    }
}

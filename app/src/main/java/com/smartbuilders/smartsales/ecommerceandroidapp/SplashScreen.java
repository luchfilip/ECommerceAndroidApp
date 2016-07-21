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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.AuthenticatorActivity;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.services.LoadProductsThumbImage;
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
                                //se manda a descargar todas las imagenes thumbs de los productos aqui
                                //porque en la sincronizacion inicial no se descargan ya que aun la
                                //productImage esta vacia
                                startService(new Intent(SplashScreen.this, LoadProductsThumbImage.class));
                                mSynchronizationState = SYNC_FINISHED;
                            }else{
                                mSynchronizationState = SYNC_ERROR;
                                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                                if(extras.containsKey(SyncAdapter.LOG_MESSAGE_DETAIL)) {
                                    ((TextView) findViewById(R.id.error_message)).setText(String.valueOf(extras.getString(SyncAdapter.LOG_MESSAGE_DETAIL)));
                                }
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
            registerReceiver(syncAdapterReceiver, intentFilter);
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

        final Account availableAccounts[] = mAccountManager
                .getAccountsByType(getString(R.string.authenticator_account_type));

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

        if (availableAccounts.length > 0) {
            if(mSynchronizationState==SYNC_ERROR){
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
            }else{
                new Thread() {
                    @Override
                    public void run() {
                        final StringBuilder authToken = new StringBuilder();
                        final StringBuilder exceptionMessage = new StringBuilder();
                        try {
                            String aux = mAccountManager.blockingGetAuthToken(availableAccounts[0],
                                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
                            if(aux!=null){
                                authToken.append(aux);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            exceptionMessage.append(e.getMessage());
                        } finally {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(TextUtils.isEmpty(exceptionMessage)){
                                        if(TextUtils.isEmpty(authToken)){
                                            final Intent intent = new Intent(SplashScreen.this, AuthenticatorActivity.class);
                                            intent.putExtra(AuthenticatorActivity.ARG_USER_ID, mAccountManager.getUserData(availableAccounts[0], AccountGeneral.USERDATA_USER_ID));
                                            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                                            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
                                            startActivityForResult(intent, 100);
                                        }else{
                                            checkInitialLoad(mAccountManager, availableAccounts[0]);
                                        }
                                    }else{
                                        findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                        new AlertDialog.Builder(SplashScreen.this)
                                                .setTitle("Error iniciando la aplicación")
                                                .setMessage(exceptionMessage)
                                                .setPositiveButton(R.string.accept, null)
                                                .setCancelable(false)
                                                .show();
                                    }
                                }
                            });
                        }
                    }
                }.start();
            }
        } else {
            addNewAccount(getString(R.string.authenticator_account_type),
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            finishActivityOnResultOperationCanceledException = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            if(resultCode==RESULT_OK){
                initApp();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!ApplicationUtilities.checkPlayServices(this)){
            findViewById(R.id.parent_layout).setVisibility(View.GONE);
        }else if(findViewById(R.id.parent_layout).getVisibility()==View.GONE){
            findViewById(R.id.parent_layout).setVisibility(View.VISIBLE);
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
                                        .getAccountsByType(getString(R.string.authenticator_account_type));
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
        //Utils.createImageFiles(this, mCurrentUser);
        //se manda a descargar todas las imagenes thumbs de los productos
        //startService(new Intent(this, LoadProductsThumbImage.class));
        startActivity(new Intent(SplashScreen.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
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

        if(mCurrentUser!=null){
            if (Utils.appRequireInitialLoad(this)) {
                    if(NetworkConnectionUtilities.isOnline(this)
                            /*&& (NetworkConnectionUtilities.isWifiConnected(this)||NetworkConnectionUtilities.isMobileConnected(this))*/) {
                        findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
                        if(account!=null && !ApplicationUtilities.isSyncActive(this, account)){
                            ApplicationUtilities.initSyncByAccount(this, account);
                            mSynchronizationState = SYNC_RUNNING;
                        }
                    } else {
                        //show network connection unavailable error.
                        Toast.makeText(this, R.string.network_connection_unavailable, Toast.LENGTH_SHORT).show();
                        //TODO: mostrar en pantalla error de conexion
                        findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.error_message)).setText(R.string.network_connection_unavailable);
                        findViewById(R.id.progressContainer).setVisibility(View.GONE);
                        mSynchronizationState = SYNC_ERROR;
                    }
            } else {
                mSynchronizationState = SYNC_FINISHED;
                if(account!=null && !ApplicationUtilities.isSyncActive(this, account)){
                    ApplicationUtilities.initSyncByAccount(this, account);
                }
                initApp();
            }
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
        }catch(Exception e){
            //do nothing
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SYNCHRONIZATION_STATE, mSynchronizationState);
        super.onSaveInstanceState(outState);
    }
}

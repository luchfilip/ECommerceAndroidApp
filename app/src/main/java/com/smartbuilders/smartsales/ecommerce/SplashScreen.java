package com.smartbuilders.smartsales.ecommerce;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.PeriodicSync;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.salesforcesystem.SalesForceSystemMainActivity;
import com.smartbuilders.synchronizer.ids.AuthenticatorActivity;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.syncadapter.SyncAdapter;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.NetworkConnectionUtilities;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.List;

/**
 * Jesus Sarco, 2/6/2016.
 */
public class SplashScreen extends AppCompatActivity {

    private static final String STATE_SYNCHRONIZATION_STATE = "STATE_SYNCHRONIZATION_STATE";
    private static final String STATE_SYNC_PROGRESS_PERCENTAGE = "STATE_SYNC_PROGRESS_PERCENTAGE";

    private static final int SYNC_RUNNING = 1;
    private static final int SYNC_ERROR = 2;
    private static final int SYNC_STOPED = 3;
    private static final int SYNC_FINISHED = 4;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1234;

    private AccountManager mAccountManager;
    private boolean finishActivityOnResultOperationCanceledException;
    private User mUser;
    private int mSynchronizationState;
    private TextView mProgressPercentageTextView;
    private String mProgressPercentage = ApplicationUtilities.formatFloatTwoDecimals(0);
    private TextView mSyncDurationTextView;
    private Account availableAccounts[];

    private BroadcastReceiver syncAdapterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    if (extras.containsKey(SyncAdapter.USER_ID) && mUser != null
                            && extras.getString(SyncAdapter.USER_ID) != null
                            && extras.getString(SyncAdapter.USER_ID).equals(mUser.getUserId())) {
                        if (intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_STARTED)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_PROGRESS)) {
                            findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
                            findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
                            if (extras.containsKey(SyncAdapter.SYNC_INIT_TIME)) {
                                mSyncDurationTextView.setText(ApplicationUtilities.parseMillisecondsToHMS((System.currentTimeMillis() - extras.getLong(SyncAdapter.SYNC_INIT_TIME)),
                                        ApplicationUtilities.TIME_FORMAT_1));
                            }
                            if (intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_STARTED)) {
                                mProgressPercentage = ApplicationUtilities.formatFloatTwoDecimals(0);
                                mProgressPercentageTextView.setText(getString(R.string.progress_percentage, mProgressPercentage));
                            } else if (intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_PROGRESS)) {
                                if (extras.containsKey(SyncAdapter.LOG_MESSAGE)) {
                                    mProgressPercentage = extras.getString(SyncAdapter.LOG_MESSAGE);
                                    mProgressPercentageTextView.setText(getString(R.string.progress_percentage, mProgressPercentage));
                                }
                            }
                            mSynchronizationState = SYNC_RUNNING;
                        } else if (intent.getAction().equals(SyncAdapter.AUTHENTICATOR_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED)
                                || intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)
                                || intent.getAction().equals(SyncAdapter.IO_EXCEPTION)
                                || intent.getAction().equals(SyncAdapter.GENERAL_EXCEPTION)) {
                            if (intent.getAction().equals(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED)) {
                                mProgressPercentage = ApplicationUtilities.formatFloatTwoDecimals(100);
                                mProgressPercentageTextView.setText(getString(R.string.progress_percentage, mProgressPercentage));
                                initApp();
                                mSynchronizationState = SYNC_FINISHED;
                            } else {
                                mSynchronizationState = SYNC_ERROR;
                                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                                //if (extras.containsKey(SyncAdapter.LOG_MESSAGE_DETAIL)) {
                                //    ((TextView) findViewById(R.id.error_message)).setText(String.valueOf(extras.getString(SyncAdapter.LOG_MESSAGE_DETAIL)));
                                //}
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
            intentFilter.addAction(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED);
            intentFilter.addAction(SyncAdapter.AUTHENTICATOR_EXCEPTION);
            intentFilter.addAction(SyncAdapter.GENERAL_EXCEPTION);
            intentFilter.addAction(SyncAdapter.IO_EXCEPTION);
            intentFilter.addAction(SyncAdapter.OPERATION_CANCELED_EXCEPTION);
            intentFilter.addAction(SyncAdapter.XML_PULL_PARSE_EXCEPTION);
            registerReceiver(syncAdapterReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_SYNCHRONIZATION_STATE)) {
                mSynchronizationState = savedInstanceState.getInt(STATE_SYNCHRONIZATION_STATE);
            }
            if (savedInstanceState.containsKey(STATE_SYNC_PROGRESS_PERCENTAGE)) {
                mProgressPercentage = savedInstanceState.getString(STATE_SYNC_PROGRESS_PERCENTAGE);
            }
        }

        mProgressPercentageTextView = (TextView) findViewById(R.id.progress_percentage);
        mProgressPercentageTextView.setText(getString(R.string.progress_percentage, mProgressPercentage));

        mSyncDurationTextView = (TextView) findViewById(R.id.sync_duration);

        mAccountManager = AccountManager.get(this);

        findViewById(R.id.exit_app_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkPermission();
    }

    private void checkPermission() {
        availableAccounts = mAccountManager.getAccountsByType(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.GET_ACCOUNTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(SplashScreen.this)
                        .setMessage("El permiso solicitado es necesario para poder sincronizar los datos.")
                        .setPositiveButton(R.string.re_try, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(SplashScreen.this,
                                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                                        MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);
                            }
                        })
                        .setCancelable(false)
                        .show();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                        MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return;
        }

        activateInterface();
    }

    private void activateInterface() {
        findViewById(R.id.reTry_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.GONE);
                if (availableAccounts.length > 0) {
                    checkInitialLoad(mAccountManager, availableAccounts[0]);
                }
            }
        });

        if (availableAccounts.length > 0) {
            if (mSynchronizationState == SYNC_ERROR) {
                findViewById(R.id.error_loading_data_linearLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        final StringBuilder authToken = new StringBuilder();
                        final StringBuilder exceptionMessage = new StringBuilder();
                        try {
                            String aux = mAccountManager.blockingGetAuthToken(availableAccounts[0],
                                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, true);
                            if (aux != null) {
                                authToken.append(aux);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            exceptionMessage.append(e.getMessage());
                        } finally {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (TextUtils.isEmpty(exceptionMessage)) {
                                        if (TextUtils.isEmpty(authToken)) {
                                            final Intent intent = new Intent(SplashScreen.this, AuthenticatorActivity.class);
                                            intent.putExtra(AuthenticatorActivity.ARG_USER_ID, mAccountManager.getUserData(availableAccounts[0], AccountGeneral.USERDATA_USER_ID));
                                            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                                            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
                                            startActivityForResult(intent, 100);
                                        } else {
                                            checkInitialLoad(mAccountManager, availableAccounts[0]);
                                        }
                                    } else {
                                        findViewById(R.id.progressContainer).setVisibility(View.GONE);
                                        new AlertDialog.Builder(SplashScreen.this)
                                                .setTitle(R.string.error_initializing_app)
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
            addNewAccount(BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            finishActivityOnResultOperationCanceledException = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_GET_ACCOUNTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    activateInterface();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    new AlertDialog.Builder(SplashScreen.this)
                            .setMessage("Imposible continuar con la aplicación, se necesita otorgar " +
                                    "permisos de lectura de contactos para para poder acceder a la aplicación.")
                            .setPositiveButton(R.string.close_app, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                initApp();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!ApplicationUtilities.checkPlayServices(this)) {
            findViewById(R.id.parent_layout).setVisibility(View.GONE);
        } else if (findViewById(R.id.parent_layout).getVisibility() == View.GONE) {
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
                            if (bnd != null && bnd.getBundle(AccountManager.KEY_USERDATA)!=null) {
                                String userId = bnd.getBundle(AccountManager.KEY_USERDATA)
                                        .getString(AccountGeneral.USERDATA_USER_ID);
                                mUser = ApplicationUtilities.getUserByIdFromAccountManager(SplashScreen.this, userId);

                                //mostrar pantalla de bienvenida de la aplicacion
                                startActivity(new Intent(SplashScreen.this, WelcomeScreenSlideActivity.class));
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                                checkInitialLoad(mAccountManager, ApplicationUtilities.getAccountByIdFromAccountManager(SplashScreen.this, userId));
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
        if (BuildConfig.IS_SALES_FORCE_SYSTEM) {
            startActivity(new Intent(this, SalesForceSystemMainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
        } else {
            startActivity(new Intent(this, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP));
        }
        finish();
    }

    /**
     * Verifica si tiene que hacerse la carga inicial
     */
    private void checkInitialLoad(AccountManager accountManager, Account account) {
        findViewById(R.id.progressContainer).setVisibility(View.GONE);

        mUser = ApplicationUtilities.getUserByIdFromAccountManager(this,
                accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));

        if(mUser !=null){
            /***************************************************************************************/
            boolean setPeriodicSync = false;
            ContentResolver.setMasterSyncAutomatically(true);
            //se define la sincronizacion automatica solo si no se ha definido antes
            List<PeriodicSync> periodicSyncList = ContentResolver.getPeriodicSyncs(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY);
            if(periodicSyncList==null || periodicSyncList.isEmpty()){
                setPeriodicSync = true;
            } else {
                for (PeriodicSync periodicSync : periodicSyncList){
                    if (periodicSync.account.name.equals(account.name)
                            && periodicSync.period!=Utils.getSyncPeriodicityFromPreferences(this)){
                        setPeriodicSync = true;
                        break;
                    }
                }
            }

            if (setPeriodicSync) {
                ContentResolver.removePeriodicSync(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, new Bundle());

                //Turn on periodic syncing
                ContentResolver.setIsSyncable(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, true);

                ContentResolver.addPeriodicSync(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY,
                        Bundle.EMPTY, Utils.getSyncPeriodicityFromPreferences(this));
            } else if (!ContentResolver.getSyncAutomatically(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)) {
                //Turn on periodic syncing
                ContentResolver.setIsSyncable(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY, true);
            }
            /***************************************************************************************/

            if (ApplicationUtilities.appRequireInitialLoad(this, mUser.getUserId())) {
                if(NetworkConnectionUtilities.isOnline(this)) {
                    findViewById(R.id.progressContainer).setVisibility(View.VISIBLE);
                    if(account!=null && !ApplicationUtilities.isSyncActive(account, BuildConfig.SYNC_ADAPTER_CONTENT_AUTHORITY)){
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
                initApp();
            }
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
        outState.putString(STATE_SYNC_PROGRESS_PERCENTAGE, mProgressPercentage);
        super.onSaveInstanceState(outState);
    }
}

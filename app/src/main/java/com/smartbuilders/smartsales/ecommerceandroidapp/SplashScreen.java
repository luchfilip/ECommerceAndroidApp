package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 2/6/2016.
 */
public class SplashScreen extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    private AccountManager mAccountManager;
    private boolean finishActivityOnResultOperationCanceledException;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAccountManager = AccountManager.get(this);

        final Account availableAccounts[] = mAccountManager
                .getAccountsByType(getString(R.string.authenticator_acount_type));
        if (availableAccounts != null && availableAccounts.length > 0) {
            loadUserData(availableAccounts[0], mAccountManager);
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
                                if (availableAccounts!=null && availableAccounts.length>0) {
                                    for(Account account : availableAccounts){
                                        if(mAccountManager.getUserData(account,
                                                AccountGeneral.USERDATA_USER_ID).equals(userId)){
                                            loadUserData(account, mAccountManager);
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

    private void loadUserData(Account account, AccountManager accountManager){
        ContentResolver.setIsSyncable(account, getString(R.string.sync_adapter_content_authority), 1);
        User user = ApplicationUtilities.getUserByIdFromAccountManager(this,
                accountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
        Intent i = new Intent(SplashScreen.this, MainActivity.class);
        i.putExtra(MainActivity.KEY_CURRENT_USER, user);
        startActivity(i);

        // close this activity
        finish();
    }
}

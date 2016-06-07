package com.jasgcorp.ids;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.smartbuilders.smartsales.ecommerceandroidapp.ResetPasswordActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.SignUpActivity;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.model.AccountGeneral;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import static com.jasgcorp.ids.syncadapter.model.AccountGeneral.sServerAuthenticate;

/**
 * The Authenticator activity.
 * <p/>
 * Called by the Authenticator and in charge of identifing the user.
 * <p/>
 * It sends back to the Authenticator the result.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE 			= "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE 				= "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME 			= "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT 	= "IS_ADDING_ACCOUNT";
    public final static String STATE_USERNAME 		= "com.jasgcorp.ids.AuthenticatorActivity.state_username";
    public final static String STATE_USERPASS 		= "com.jasgcorp.ids.AuthenticatorActivity.state_userpass";
    public final static String STATE_SERVERADDRESS 	= "com.jasgcorp.ids.AuthenticatorActivity.state_serveraddress";
    public final static String STATE_USERGROUP 	= "com.jasgcorp.ids.AuthenticatorActivity.state_usergroup";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private Account mAccount;
    private String mAuthTokenType;
    private ProgressDialog waitPlease;
    private User user;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Utils.getCurrentUser(getApplicationContext())!=null) {
            finish();
        } else {
            //setTypeFaceToViews();
            mAccountManager = AccountManager.get(getBaseContext());

            String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
            String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
            mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
            if (mAuthTokenType == null)
                mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

            if(accountType!=null && accountName!=null){
                for(Account account : mAccountManager.getAccountsByType(accountType)){
                    if(account.name.equals(accountName)){
                        mAccount = account;
                        user = new User(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_ID));
                        user.setServerUserId(Long.valueOf(mAccountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID)));
                        user.setUserName(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_NAME));
                        user.setUserPass(mAccountManager.getPassword(account));
                        user.setServerAddress(mAccountManager.getUserData(account, AccountGeneral.USERDATA_SERVER_ADDRESS));
                        user.setUserGroup(mAccountManager.getUserData(account, AccountGeneral.USERDATA_USER_GROUP));
                        user.setSaveDBInExternalCard(mAccountManager.getUserData(account, AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD).equals("true"));
                        break;
                    }
                }
                if(user!=null){
                    findViewById(R.id.accountName).setEnabled(false);
                    findViewById(R.id.server_address).setEnabled(false);
                    findViewById(R.id.user_group).setEnabled(false);

                    ((EditText) findViewById(R.id.accountName)).setText(accountName);
                    ((EditText) findViewById(R.id.accountPassword)).setText(user.getUserPass());
                    ((EditText) findViewById(R.id.server_address)).setText(user.getServerAddress());
                    ((EditText) findViewById(R.id.user_group)).setText(user.getUserGroup());
                }
            } else if (savedInstanceState != null) {

                ((EditText) findViewById(R.id.accountName)).setText(savedInstanceState.getString(STATE_USERNAME));
                ((EditText) findViewById(R.id.accountPassword)).setText(savedInstanceState.getString(STATE_USERPASS));
                ((EditText) findViewById(R.id.server_address)).setText(savedInstanceState.getString(STATE_SERVERADDRESS));
                ((EditText) findViewById(R.id.user_group)).setText(savedInstanceState.getString(STATE_USERGROUP));
            }

            findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });

            if (findViewById(R.id.reset_password_textView)!=null){
                findViewById(R.id.reset_password_textView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AuthenticatorActivity.this, ResetPasswordActivity.class));
                    }
                });

            }
            if (findViewById(R.id.sign_up_textView)!=null){
                findViewById(R.id.sign_up_textView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AuthenticatorActivity.this, SignUpActivity.class));
                    }
                });
            }
        }
    }

    //private void setTypeFaceToViews(){
    //    ((TextView) findViewById(R.id.company_phrase)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((EditText) findViewById(R.id.accountName)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((EditText) findViewById(R.id.accountPassword)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((EditText) findViewById(R.id.server_address)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((EditText) findViewById(R.id.user_group)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((Button) findViewById(R.id.submit)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((TextView) findViewById(R.id.reset_password_textView)).setTypeface(Utils.getGlobalTypeFace(this));
    //    ((TextView) findViewById(R.id.sign_up_textView)).setTypeface(Utils.getGlobalTypeFace(this));
    //}

    @Override
    protected void onResume() {
        super.onResume();
        if(!ApplicationUtilities.checkPlayServices(this)){
            findViewById(R.id.parent_layout).setVisibility(View.GONE);
        }else if(findViewById(R.id.parent_layout).getVisibility()==View.GONE){
            findViewById(R.id.parent_layout).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_USERNAME, ((EditText) findViewById(R.id.accountName)).getText().toString());
        outState.putString(STATE_USERPASS, ((EditText) findViewById(R.id.accountPassword)).getText().toString());
        outState.putString(STATE_SERVERADDRESS, ((EditText) findViewById(R.id.server_address)).getText().toString());
        outState.putString(STATE_USERGROUP, ((EditText) findViewById(R.id.user_group)).getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void submit() {
        try {
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //final String userGroup 		= ((TextView) findViewById(R.id.user_group)).getText().toString();
        final String userGroup 		= "catalogo-febeca";
        final String userName 		= ((EditText) findViewById(R.id.accountName)).getText().toString();
        final String userPass 		= ((EditText) findViewById(R.id.accountPassword)).getText().toString();
        final String serverAddress 	= ((EditText) findViewById(R.id.server_address)).getText().toString();
        final boolean saveDBInExternalCard = false;

        waitPlease = ProgressDialog.show(this, getString(R.string.authenticating_user), getString(R.string.wait_please), true, false);

        new AsyncTask<Context, Void, Intent>() {

            @Override
            protected Intent doInBackground(Context... context) {

                Bundle data = new Bundle();
                if(user!=null){//entra aqui cuando hay problemas con la clave del usuario
                    try {
                        user.setUserPass(userPass);
                        sServerAuthenticate.userSignIn(user, getString(R.string.authenticator_acount_type), getBaseContext());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(user.getSessionToken().equals(ApplicationUtilities.ST_NEW_USER_AUTHORIZED)
                            || user.getSessionToken().equals(ApplicationUtilities.ST_USER_AUTHORIZED)){
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userGroup+"-"+userName);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authenticator_acount_type));
                        data.putString(AccountManager.KEY_AUTHTOKEN, user.getSessionToken());
                        data.putString(PARAM_USER_PASS, userPass);
                    }else{
                        data.putString(KEY_ERROR_MESSAGE, user.getSessionToken());
                    }
                }else{
                    try {
                        if(userName!=null && !userName.isEmpty()
                                && userPass!=null && !userPass.isEmpty()
                                && serverAddress!=null && !serverAddress.isEmpty()){
                            URL url = new URL(serverAddress);
                            URLConnection conn = url.openConnection();
                            conn.setConnectTimeout(1000*5);//5 seconds
                            conn.connect();
                            String userId = ApplicationUtilities.getUserId(serverAddress, userName, userGroup, context[0]);
                            if(userId==null){
                                userId = ApplicationUtilities.getNewUserId(context[0]);
                                User user = new User(userId);
                                user.setUserGroup(userGroup);
                                user.setUserName(userName);
                                user.setUserPass(userPass);
                                user.setServerAddress(serverAddress);
                                user.setSaveDBInExternalCard(saveDBInExternalCard);

                                sServerAuthenticate.userSignUp(user, mAuthTokenType, context[0]);
                                if(user.getSessionToken().equals(ApplicationUtilities.ST_NEW_USER_AUTHORIZED)
                                        || user.getSessionToken().equals(ApplicationUtilities.ST_USER_AUTHORIZED)){
                                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userGroup+"-"+userName);
                                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, getString(R.string.authenticator_acount_type));
                                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getAuthToken());

                                    // We keep the server address and GCM_REG_ID as an extra data on the account.
                                    Bundle userData = new Bundle();
                                    userData.putString(AccountGeneral.USERDATA_SERVER_ADDRESS, user.getServerAddress());
                                    userData.putString(AccountGeneral.USERDATA_GCM_REGISTRATION_ID, user.getGcmRegistrationId());
                                    userData.putString(AccountGeneral.USERDATA_USER_GROUP, user.getUserGroup());
                                    userData.putString(AccountGeneral.USERDATA_USER_ID, user.getUserId());
                                    userData.putString(AccountGeneral.USERDATA_SERVER_USER_ID, user.getServerUserId()!=null ? user.getServerUserId().toString() : null);
                                    userData.putString(AccountGeneral.USERDATA_USER_NAME, user.getUserName());
                                    userData.putString(AccountGeneral.USERDATA_USER_SESSION_TOKEN, user.getSessionToken());
                                    userData.putString(AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD, String.valueOf(user.isSaveDBInExternalCard()));
                                    data.putBundle(AccountManager.KEY_USERDATA, userData);

                                    data.putString(PARAM_USER_PASS, userPass);
                                }else{
                                    data.putString(KEY_ERROR_MESSAGE, user.getSessionToken());
                                }
                            }else{
                                data.putString(KEY_ERROR_MESSAGE, getString(R.string.user_already_registered));
                            }
                        }else{
                            data.putString(KEY_ERROR_MESSAGE, getString(R.string.mandatory_fields_empty));
                        }
                    } catch (MalformedURLException e) {
                        // the URL is not in a valid form
                        e.printStackTrace();
                        data.putString(KEY_ERROR_MESSAGE, getString(R.string.error_server_address_malformedurlexception));
                    } catch (IOException e) {
                        // the connection couldn't be established
                        e.printStackTrace();
                        data.putString(KEY_ERROR_MESSAGE, getString(R.string.error_server_address_ioexception));
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                }
                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                waitPlease.dismiss();
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute(this);
    }

    private void finishLogin(Intent intent) {
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            mAccount = new Account(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), getString(R.string.authenticator_acount_type));

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            boolean saved = mAccountManager.addAccountExplicitly(mAccount, accountPassword, intent.getBundleExtra(AccountManager.KEY_USERDATA));
            if(saved){
                mAccountManager.setAuthToken(mAccount, mAuthTokenType, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
                //Register the user in the local data base.
                ApplicationUtilities.registerUserInDataBase(intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_ID),
                        Long.valueOf(intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_SERVER_USER_ID)),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_SERVER_ADDRESS),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_GROUP),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_NAME),
                        intent.getStringExtra(AccountManager.KEY_AUTHTOKEN),
                        this);
            }else if(user!=null){
                Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            mAccountManager.setPassword(mAccount, accountPassword);
            mAccountManager.setAuthToken(mAccount, mAuthTokenType, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

}
package com.smartbuilders.synchronizer.ids;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.RequestResetUserPasswordActivity;
import com.smartbuilders.smartsales.ecommerce.RequestUserPasswordActivity;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import static com.smartbuilders.synchronizer.ids.syncadapter.model.AccountGeneral.sServerAuthenticate;

/**
 * The Authenticator activity.
 * <p/>
 * Called by the Authenticator and in charge of identifing the mUser.
 * <p/>
 * It sends back to the Authenticator the result.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE 			= "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE 				= "AUTH_TYPE";
    public final static String ARG_USER_ID 			        = "USER_ID";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT 	= "IS_ADDING_ACCOUNT";
    public final static String STATE_USERNAME 		        = "AuthenticatorActivity.state_username";
    public final static String STATE_USER_PASS              = "AuthenticatorActivity.state_userpass";
    public final static String STATE_SERVER_ADDRESS         = "AuthenticatorActivity.state_serveraddress";
    public final static String STATE_USER_GROUP             = "AuthenticatorActivity.state_usergroup";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";

    private AccountManager mAccountManager;
    private Account mAccount;
    private String mAuthTokenType;
    private ProgressDialog waitPlease;
    private User mUser;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (getIntent()!=null && getIntent().getBooleanExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true)
                && Utils.getCurrentUser(getApplicationContext())!=null) {
            finish();
        } else {
            mAccountManager = AccountManager.get(getBaseContext());

            mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
            if (mAuthTokenType == null) {
                mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
            }
            if(getIntent().getStringExtra(ARG_USER_ID)!=null){
                mUser = ApplicationUtilities.getUserByIdFromAccountManager(this, getIntent().getStringExtra(ARG_USER_ID));
                mAccount = ApplicationUtilities.getAccountByIdFromAccountManager(this, getIntent().getStringExtra(ARG_USER_ID));
                if(mUser !=null && mAccount!=null){
                    findViewById(R.id.accountName).setEnabled(false);
                    findViewById(R.id.user_group).setEnabled(false);

                    ((EditText) findViewById(R.id.accountName)).setText(mUser.getUserName());
                    ((EditText) findViewById(R.id.accountPassword)).setText(mUser.getUserPass());
                    ((EditText) findViewById(R.id.server_address)).setText(mUser.getServerAddress());
                    ((EditText) findViewById(R.id.user_group)).setText(mUser.getUserGroup());
                }
            } else if (savedInstanceState != null) {

                ((EditText) findViewById(R.id.accountName)).setText(savedInstanceState.getString(STATE_USERNAME));
                ((EditText) findViewById(R.id.accountPassword)).setText(savedInstanceState.getString(STATE_USER_PASS));
                ((EditText) findViewById(R.id.server_address)).setText(savedInstanceState.getString(STATE_SERVER_ADDRESS));
                ((EditText) findViewById(R.id.user_group)).setText(savedInstanceState.getString(STATE_USER_GROUP));
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
                        startActivity(new Intent(AuthenticatorActivity.this, RequestResetUserPasswordActivity.class));
                    }
                });

            }
            if (findViewById(R.id.sign_up_textView)!=null){
                findViewById(R.id.sign_up_textView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(AuthenticatorActivity.this, RequestUserPasswordActivity.class));
                    }
                });
            }
        }

        final View activityRootView = findViewById(R.id.parent_layout);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if ((activityRootView.getRootView().getHeight() - activityRootView.getHeight()) > 100) {
                    //se esta mostrando el teclado
                    if(findViewById(R.id.fields_scrollView)!=null){
                        View lastChild = ((ScrollView) findViewById(R.id.fields_scrollView))
                                .getChildAt(((ScrollView) findViewById(R.id.fields_scrollView)).getChildCount() - 1);
                        int delta = lastChild.getBottom() + findViewById(R.id.fields_scrollView).getPaddingBottom()
                                - (findViewById(R.id.fields_scrollView).getScrollY() + findViewById(R.id.fields_scrollView).getHeight());
                        ((ScrollView) findViewById(R.id.fields_scrollView)).smoothScrollBy(0, delta);
                    }
                }
            }
        });
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_USERNAME, ((EditText) findViewById(R.id.accountName)).getText().toString());
        outState.putString(STATE_USER_PASS, ((EditText) findViewById(R.id.accountPassword)).getText().toString());
        outState.putString(STATE_SERVER_ADDRESS, ((EditText) findViewById(R.id.server_address)).getText().toString());
        outState.putString(STATE_USER_GROUP, ((EditText) findViewById(R.id.user_group)).getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void submit() {
        final String userGroup 		= (findViewById(R.id.user_group)!=null && findViewById(R.id.user_group).getVisibility()!=View.VISIBLE)
                ? getString(R.string.ids_user_group_name) : ((TextView) findViewById(R.id.user_group)).getText().toString();
        final String userName 		= ((EditText) findViewById(R.id.accountName)).getText().toString();
        final String userPass 		= ((EditText) findViewById(R.id.accountPassword)).getText().toString();
        final String serverAddress 	= ((EditText) findViewById(R.id.server_address)).getText().toString();
        final boolean saveDBInExternalCard = false;

        Utils.lockScreenOrientation(this);
        waitPlease = ProgressDialog.show(this, getString(R.string.authenticating_user),
                getString(R.string.wait_please), true, false);

        new AsyncTask<Context, Void, Intent>() {

            @Override
            protected Intent doInBackground(Context... context) {

                Bundle data = new Bundle();
                if(mUser !=null){//entra aqui cuando hay problemas con la clave del usuario
                    try {
                        mUser.setUserPass(userPass);
                        sServerAuthenticate.userGetAuthToken(mUser, mAuthTokenType, getBaseContext());

                        if(mUser.getAuthToken()!=null){
                            mAccountManager.setAuthToken(mAccount, mAuthTokenType, mUser.getAuthToken());

                            // Get the session token for the current account
                            sServerAuthenticate.userSignIn(mUser, mAuthTokenType, getBaseContext());

                            switch (mUser.getSessionToken()) {
                                case ApplicationUtilities.ST_NEW_USER_AUTHORIZED:
                                case ApplicationUtilities.ST_USER_AUTHORIZED:
                                    data.putString(AccountManager.KEY_ACCOUNT_NAME, userGroup+"-"+userName);
                                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);
                                    data.putString(AccountManager.KEY_AUTHTOKEN, mUser.getAuthToken());
                                    data.putString(PARAM_USER_PASS, userPass);
                                    break;
                                case ApplicationUtilities.ST_USER_NOT_AUTHORIZED:
                                    throw new AuthenticatorException(getString(R.string.user_not_authorized));
                                case ApplicationUtilities.ST_USER_NOT_EXIST_IN_SERVER:
                                    throw new AuthenticatorException(getString(R.string.user_not_exist_in_server));
                                case ApplicationUtilities.ST_USER_WRONG_PASSWORD:
                                    throw new AuthenticatorException(getString(R.string.user_wrong_password));
                                default:
                                    throw new AuthenticatorException(mUser.getSessionToken());
                            }
                        }else{
                            throw new AuthenticatorException(getString(R.string.user_session_token_null));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                }else{
                    try {
                        if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPass)
                                && !TextUtils.isEmpty(serverAddress)){
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
                                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);
                                    data.putString(AccountManager.KEY_AUTHTOKEN, user.getAuthToken());

                                    // We keep the server address and GCM_REG_ID as an extra data on the account.
                                    Bundle userData = new Bundle();
                                    userData.putString(AccountGeneral.USERDATA_SERVER_ADDRESS, user.getServerAddress());
                                    userData.putString(AccountGeneral.USERDATA_GCM_REGISTRATION_ID, user.getGcmRegistrationId());
                                    userData.putString(AccountGeneral.USERDATA_USER_GROUP, user.getUserGroup());
                                    userData.putString(AccountGeneral.USERDATA_USER_ID, user.getUserId());
                                    userData.putString(AccountGeneral.USERDATA_USER_PROFILE_ID, String.valueOf(user.getUserProfileId()));
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
                Utils.unlockScreenOrientation(AuthenticatorActivity.this);
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
            mAccount = new Account(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME), BuildConfig.AUTHENTICATOR_ACCOUNT_TYPE);

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the mUser)
            boolean saved = mAccountManager.addAccountExplicitly(mAccount, accountPassword, intent.getBundleExtra(AccountManager.KEY_USERDATA));
            if(saved){
                mAccountManager.setAuthToken(mAccount, mAuthTokenType, intent.getStringExtra(AccountManager.KEY_AUTHTOKEN));
                //Register the mUser in the local data base.
                ApplicationUtilities.registerUserInDataBase(intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_ID),
                        Integer.valueOf(intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_PROFILE_ID)),
                        Long.valueOf(intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_SERVER_USER_ID)),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_SERVER_ADDRESS),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_GROUP),
                        intent.getBundleExtra(AccountManager.KEY_USERDATA).getString(AccountGeneral.USERDATA_USER_NAME),
                        intent.getStringExtra(AccountManager.KEY_AUTHTOKEN),
                        this);
            }else if(mUser !=null){
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
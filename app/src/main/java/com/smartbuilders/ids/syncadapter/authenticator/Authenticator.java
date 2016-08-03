package com.smartbuilders.ids.syncadapter.authenticator;

import java.io.IOException;
import java.net.SocketException;

import com.smartbuilders.ids.AuthenticatorActivity;
import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.syncadapter.model.AccountGeneral;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static com.smartbuilders.ids.syncadapter.model.AccountGeneral.sServerAuthenticate;

/**
 * Created by Alberto on 23/3/2016.
 */
public class Authenticator extends AbstractAccountAuthenticator {

    private static final String TAG = Authenticator.class.getSimpleName();

    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType, String authTokenType,
                             String[] requiredFeatures, Bundle options)
            throws NetworkErrorException {
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account, Bundle options) throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response,
                                 String accountType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY)
                && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String serverAddress 		= am.getUserData(account, AccountGeneral.USERDATA_SERVER_ADDRESS);
        String gcmRegistrationId 	= am.getUserData(account, AccountGeneral.USERDATA_GCM_REGISTRATION_ID);
        String userGroup 			= am.getUserData(account, AccountGeneral.USERDATA_USER_GROUP);
        String syncPeriodicity 		= am.getUserData(account, AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY);
        String syncNetworkMode 		= am.getUserData(account, AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE);
        String userId 				= am.getUserData(account, AccountGeneral.USERDATA_USER_ID);
        int businessPartnerId	    = Integer.valueOf(am.getUserData(account, AccountGeneral.USERDATA_BUSINESS_PARTNER_ID));
        int userProfileId   	    = Integer.valueOf(am.getUserData(account, AccountGeneral.USERDATA_USER_PROFILE_ID));
        Long serverUserId 			= Long.valueOf(am.getUserData(account, AccountGeneral.USERDATA_SERVER_USER_ID));
        String userName 			= am.getUserData(account, AccountGeneral.USERDATA_USER_NAME);
        boolean saveDBInExternalCard= am.getUserData(account, AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD).equals("true");

        String authToken = am.peekAuthToken(account, authTokenType);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String userPass = am.getPassword(account);
            if (userPass != null) {
                try {
                    //Log.d(TAG, "> re-authenticating with the existing password");
                    User user = new User(userId);
                    user.setBusinessPartnerId(businessPartnerId);
                    user.setUserProfileId(userProfileId);
                    user.setServerUserId(serverUserId);
                    user.setUserGroup(userGroup);
                    user.setUserName(userName);
                    user.setUserPass(userPass);
                    user.setServerAddress(serverAddress);
                    user.setSaveDBInExternalCard(saveDBInExternalCard);

                    sServerAuthenticate.userSignIn(user, authTokenType, mContext);
                    if (user.getAuthToken()!=null) {
                        authToken = user.getAuthToken();
                        if(user.getGcmRegistrationId()!=null){
                            gcmRegistrationId = user.getGcmRegistrationId();
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new NetworkErrorException(e.getMessage(), e.getCause());
                } catch (Exception e) {
                    e.printStackTrace();
                    final Bundle result = new Bundle();
                    result.putString(AccountManager.KEY_ERROR_MESSAGE, e.getMessage());
                    return result;
                }
            }
        }

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            result.putString(AccountGeneral.USERDATA_SERVER_ADDRESS, serverAddress);
            result.putString(AccountGeneral.USERDATA_GCM_REGISTRATION_ID, gcmRegistrationId);
            result.putString(AccountGeneral.USERDATA_USER_GROUP, userGroup);
            result.putString(AccountGeneral.USERDATA_AUTO_SYNC_PERIODICITY, syncPeriodicity);
            result.putString(AccountGeneral.USERDATA_AUTO_SYNC_NETWORK_MODE, syncNetworkMode);
            result.putString(AccountGeneral.USERDATA_USER_ID, userId);
            result.putInt(AccountGeneral.USERDATA_BUSINESS_PARTNER_ID, businessPartnerId);
            result.putInt(AccountGeneral.USERDATA_USER_PROFILE_ID, userProfileId);
            result.putLong(AccountGeneral.USERDATA_SERVER_USER_ID, serverUserId);
            result.putString(AccountGeneral.USERDATA_USER_NAME, userName);
            result.putString(AccountGeneral.USERDATA_SAVE_DB_IN_EXTERNAL_CARD, String.valueOf(saveDBInExternalCard));
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_USER_ID, userId);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, false);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account, String authTokenType, Bundle options)
            throws NetworkErrorException {
        // TODO Auto-generated method stub
        return null;
    }

}
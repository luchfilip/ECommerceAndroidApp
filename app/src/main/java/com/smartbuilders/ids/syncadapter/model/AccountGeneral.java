package com.smartbuilders.ids.syncadapter.model;

import com.smartbuilders.ids.server.IdsComServerAuthenticator;

public class AccountGeneral {
    /**
     * User data fields
     */
    public static final String USERDATA_SERVER_ADDRESS = "userServerAddress";
    public static final String USERDATA_GCM_REGISTRATION_ID = "userGcmRegistrationId";
    public static final String USERDATA_USER_GROUP = "userGroup";
    public static final String USERDATA_AUTO_SYNC_PERIODICITY = "userAutoSyncPeriodicity";
    public static final String USERDATA_AUTO_SYNC_NETWORK_MODE = "userAutoSyncNetworkMode";
    public static final String USERDATA_USER_ID = "userId";
    public static final String USERDATA_BUSINESS_PARTNER_ID = "businessPartnerId";
    public static final String USERDATA_USER_PROFILE_ID = "userProfileId";
    public static final String USERDATA_SERVER_USER_ID = "serverUserId";
    public static final String USERDATA_USER_NAME = "userName";
    public static final String USERDATA_USER_SESSION_TOKEN = "sessionToken";
    public static final String USERDATA_SAVE_DB_IN_EXTERNAL_CARD = "saveDBInExternalCard";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an IDS account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an IDS account";

    public static final ServerAuthenticate sServerAuthenticate = new IdsComServerAuthenticator();

}

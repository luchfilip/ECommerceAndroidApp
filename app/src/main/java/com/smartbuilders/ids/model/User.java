package com.smartbuilders.ids.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

	private String userName;
    private String userPass;
    private String sessionToken;
    private String authToken;
    private String serverAddress;
    private String gcmRegistrationId;
    private String userGroup;
    private Long serverUserId;
    private String userId;
    private boolean saveDBInExternalCard;
    private int businessPartnerId;
    private int userProfileId;

    public User(){

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userPass);
        dest.writeString(sessionToken);
        dest.writeString(authToken);
        dest.writeString(serverAddress);
        dest.writeString(gcmRegistrationId);
        dest.writeString(userGroup);
        dest.writeLong(serverUserId);
        dest.writeString(userId);
        dest.writeInt(businessPartnerId);
        dest.writeInt(userProfileId);
        //dest.writeByte((byte) (saveDBInExternalCard ? 1 : 0));
    }

    public User(Parcel in) {
        userName = in.readString();
        userPass = in.readString();
        sessionToken = in.readString();
        authToken = in.readString();
        serverAddress = in.readString();
        gcmRegistrationId = in.readString();
        userGroup = in.readString();
        serverUserId = in.readLong();
        userId = in.readString();
        businessPartnerId = in.readInt();
        userProfileId = in.readInt();
        //saveDBInExternalCard = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * 
     * @return
     */
    public String getGcmRegistrationId() {
		return gcmRegistrationId;
	}

    /**
     * 
     * @param gcmRegistrationId
     */
	public void setGcmRegistrationId(String gcmRegistrationId) {
		this.gcmRegistrationId = gcmRegistrationId;
	}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    
    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
    
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

	/**
	 * @return the serverAddress
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * @param serverAddress the serverAddress to set
	 */
	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}
    
    /**
     * 
     * @param userId
     */
    public User(String userId){
    	this.userId = userId;
    }
    
    @Override
    public String toString() {
    	return "[userId: "+userId+", authToken: \""+authToken+"\", userName: \""+userName+
                "\", userPass:\""+userPass+"\", serverAddress: \""+serverAddress+"\", " +
    			"userGroup: \""+userGroup+"\", saveDBInExternalCard: \""+saveDBInExternalCard+"\"]";
    }

    @Override
    public boolean equals(Object o) {
    	if(o instanceof User){
    		if(this.userId!=null){
    			return this.userId.equals(((User)o).getUserId());
    		}
    	}
    	return false;
    }
    
	/**
	 * @return the userGroup
	 */
	public String getUserGroup() {
		return userGroup;
	}

	/**
	 * @param userGroup the userGroup to set
	 */
	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public Long getServerUserId() {
		return serverUserId;
	}

	public void setServerUserId(Long serverUserId) {
		this.serverUserId = serverUserId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the saveDBInExternalCard
	 */
	public boolean isSaveDBInExternalCard() {
		return saveDBInExternalCard;
	}

	/**
	 * @param saveDBInExternalCard the saveDBInExternalCard to set
	 */
	public void setSaveDBInExternalCard(boolean saveDBInExternalCard) {
		this.saveDBInExternalCard = saveDBInExternalCard;
	}

    public int getBusinessPartnerId() {
        return businessPartnerId;
    }

    public void setBusinessPartnerId(int businessPartnerId) {
        this.businessPartnerId = businessPartnerId;
    }

    public int getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(int userProfileId) {
        this.userProfileId = userProfileId;
    }
}
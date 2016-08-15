package com.smartbuilders.synchronizer.ids;

import com.jasgcorp.ids.model.User;

import android.app.Activity;
import android.os.Bundle;

public class NotificationsManagerActivity extends Activity {

	public static final String KEY_CURRENT_USER = "com.jasgcorp.ids.NotificationsManagerActivity.KEY_CURRENT_USER";
	public static final String STATE_CURRENT_USER = "state_current_user";
	
	private User mCurrentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications_manager);
		
		Bundle extras = getIntent().getExtras();
		if(extras!=null && extras.containsKey(KEY_CURRENT_USER)){
			mCurrentUser = extras.getParcelable(KEY_CURRENT_USER);
		}else if(savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_USER)){
			mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
		}
		
	}
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
   		outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
    	super.onSaveInstanceState(outState);
    }
}

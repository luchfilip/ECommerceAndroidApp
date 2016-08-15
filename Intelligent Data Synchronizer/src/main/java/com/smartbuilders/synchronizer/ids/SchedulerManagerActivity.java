package com.smartbuilders.synchronizer.ids;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.adapters.SchedulerManagerAdapter;

import java.util.ArrayList;

public class SchedulerManagerActivity extends Activity implements OnClickListener {
	public static final String STATE_CURRENT_USER = "state_current_user";
	public static final String KEY_CURRENT_USER = "com.jasgcorp.ids.SchedulerManagerActivity.KEY_CURRENT_USER";
	private static final String TAG = SchedulerManagerActivity.class.getSimpleName();
	private ListView mSchedulerList;
	private SchedulerManagerAdapter schedulerManagerAdapter;
	private User mCurrentUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheduler_manager);
		
		Typeface impactLabelReversed = Typeface.createFromAsset(getAssets(), "fonts/impact_label_reversed.ttf");
		((TextView)findViewById(R.id.sync_scheduler_list)).setTypeface(impactLabelReversed);
		
        mSchedulerList = (ListView) findViewById(R.id.scheduler_listView);
        
        schedulerManagerAdapter = new SchedulerManagerAdapter(this, new ArrayList<SchedulerSyncData>());
        schedulerManagerAdapter.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(final View v) {
	            new AlertDialog.Builder(v.getContext())
					.setMessage(R.string.delete_schedule)
					.setPositiveButton(R.string.accept, new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							removeSyncSchedulerItem(mCurrentUser, (SchedulerSyncData) schedulerManagerAdapter.getItem(mSchedulerList.getPositionForView(v)));
						}
					})
					.setNegativeButton(R.string.cancel, null)
					.show();
				return true;
			}
		});
        
        schedulerManagerAdapter.setOnClockImageClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SchedulerSyncData item = (SchedulerSyncData) schedulerManagerAdapter.getItem(mSchedulerList.getPositionForView(v));
				item.setActive(!item.isActive());
				setActiveSyncSchedulerItem(mCurrentUser, item);
			}
		});
        
        mSchedulerList.setAdapter(schedulerManagerAdapter);
        
        findViewById(R.id.add_sync_schedule).setOnClickListener(this);
	        
		Bundle extras = getIntent().getExtras();
		if(extras!=null && extras.containsKey(KEY_CURRENT_USER)){
			mCurrentUser = extras.getParcelable(KEY_CURRENT_USER);
			schedulerManagerAdapter.refreshDataList(ApplicationUtilities.getSchedulerSyncDataByUser(mCurrentUser, this));
		}else if (savedInstanceState != null && savedInstanceState.containsKey(STATE_CURRENT_USER)) {
			mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
			schedulerManagerAdapter.refreshDataList(ApplicationUtilities.getSchedulerSyncDataByUser(mCurrentUser, this));
		}else{
			//TODO: mostrar mensaje de error por no estar cargado el mCurrentUser
		}
	}

	private void showTimePickerDialog(View v) {
	    DialogFragment newFragment = TimeDaysOfWeekPicker.newInstance(this.getApplicationContext(), mCurrentUser);
	    newFragment.show(getFragmentManager(), TimeDaysOfWeekPicker.TAG);
	}
	
	public void addSyncSchedulerItem(SchedulerSyncData item){
		schedulerManagerAdapter.addItem(item);
	}
	
	public void removeSyncSchedulerItem(User user, SchedulerSyncData item){
		try{
			ApplicationUtilities.removeSyncSchedulerData(user, item, getApplicationContext());
			schedulerManagerAdapter.removeItem(item);
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void setActiveSyncSchedulerItem(User user, SchedulerSyncData item){
		try{
			ApplicationUtilities.setActiveSyncSchedulerData(user, item, this);
			schedulerManagerAdapter.notifyDataSetChanged();
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.schedule_manager, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.add_sync_schedule:
				showTimePickerDialog(v);
				break;
	        default:
		}
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
   		outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
    	super.onSaveInstanceState(outState);
    }
}

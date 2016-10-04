package com.smartbuilders.synchronizer.ids;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TimePicker;
import android.widget.Toast;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;

public class TimeDaysOfWeekPicker extends DialogFragment implements OnClickListener {

	public static final String TAG = TimeDaysOfWeekPicker.class.getSimpleName();
	private View v;
	private User mCurrentUser;
	private TimePicker timePicker;
	private Context mContext;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.dialog_time_daysofweek_picker, container);
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		v.findViewById(R.id.cancel).setOnClickListener(this);
		v.findViewById(R.id.accept).setOnClickListener(this);
		v.findViewById(R.id.monday).setOnClickListener(this);
		v.findViewById(R.id.tuesday).setOnClickListener(this);
		v.findViewById(R.id.wednesday).setOnClickListener(this);
		v.findViewById(R.id.thursday).setOnClickListener(this);
		v.findViewById(R.id.friday).setOnClickListener(this);
		v.findViewById(R.id.saturday).setOnClickListener(this);
		v.findViewById(R.id.sunday).setOnClickListener(this);
		timePicker = (TimePicker) v.findViewById(R.id.timePicker); 
		return v;
	}
	
	
	public static TimeDaysOfWeekPicker newInstance(Context context, User currentUser)  {
		TimeDaysOfWeekPicker c = new TimeDaysOfWeekPicker();
		c.mContext = context;
		c.mCurrentUser = currentUser;
        return c;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.cancel:
				dismiss();
				break;
			case R.id.accept:
				addAlarmEvent();
				break;
			case R.id.monday:
				monday = !monday;
				changeDrawable(v, monday);
				break;
			case R.id.tuesday:
				tuesday = !tuesday;
				changeDrawable(v, tuesday);
				break;
			case R.id.wednesday:
				wednesday = !wednesday;
				changeDrawable(v, wednesday);
				break;
			case R.id.thursday:
				thursday = !thursday;
				changeDrawable(v, thursday);
				break;
			case R.id.friday:
				friday = !friday;
				changeDrawable(v, friday);
				break;
			case R.id.saturday:
				saturday = !saturday;
				changeDrawable(v, saturday);
				break;
			case R.id.sunday:
				sunday = !sunday;
				changeDrawable(v, sunday);
				break;
	        default:
		}
	}
	
	/**
	 * 
	 */
	private void addAlarmEvent(){
		SchedulerSyncData data = new SchedulerSyncData(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
		data.setMonday(monday);
		data.setTuesday(tuesday);
		data.setWednesday(wednesday);
		data.setThursday(thursday);
		data.setFriday(friday);
		data.setSaturday(saturday);
		data.setSunday(sunday);
		
		try{
			ApplicationUtilities.registerSyncSchedulerData(mCurrentUser, data, mContext);
			
			//update the listview in the parent
			SchedulerManagerActivity prev = ((SchedulerManagerActivity)getActivity());
	        prev.addSyncSchedulerItem(data);
	        
			dismiss();
		}catch(Exception e){
			e.printStackTrace();
			Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void changeDrawable(View v, boolean isActive){
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			if(isActive){
				v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_ui_rounded_orange));
		    }else{
		    	v.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_ui_rounded));
		    }
		} else {
			if(isActive){
				v.setBackground(getResources().getDrawable(R.drawable.border_ui_rounded_orange));
		    }else{
		    	v.setBackground(getResources().getDrawable(R.drawable.border_ui_rounded));
		    }
		}
	}

}

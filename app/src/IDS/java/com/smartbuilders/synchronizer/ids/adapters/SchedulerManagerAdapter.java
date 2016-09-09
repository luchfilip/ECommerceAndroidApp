package com.smartbuilders.synchronizer.ids.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbuilders.synchronizer.ids.scheduler.SchedulerSyncData;
import com.smartbuilders.synchronizer.ids.R;

public class SchedulerManagerAdapter extends BaseAdapter {

	private ArrayList<SchedulerSyncData> data;
  	private LayoutInflater inflater;
  	//private Typeface italicBoldDigital;
  	private int activeDayColor;
  	private OnLongClickListener onLongClickListener;
  	private OnClickListener onClockImageClickListener;
  	
	static class ViewHolder {
		TextView time;
		TextView monday;
		TextView tuesday;
		TextView wednesday;
		TextView thursday;
		TextView friday;
		TextView saturday;
		TextView sunday;
		ImageView clockImage;
    }
	
	public SchedulerManagerAdapter(Context c, ArrayList<SchedulerSyncData> d){
		inflater = LayoutInflater.from(c);
  		this.data = d;
  		//italicBoldDigital = Typeface.createFromAsset(c.getAssets(), "fonts/ds_digit.ttf");
  		activeDayColor = c.getResources().getColor(R.color.Orange);
  	}
  	
	@Override
  	public int getCount(){
        return data.size();
  	}

  	@Override
  	public Object getItem(int position) {
        return data.get(position);
  	}

  	@Override
  	public long getItemId(int position) {
  		return position;
    }

  	@Override
  	public int getViewTypeCount() {
        return 1;
  	}

  	@Override
  	public int getItemViewType(int position) {
        return 0;
  	}

  	@Override
  	public void notifyDataSetChanged(){
  		super.notifyDataSetChanged();
  	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
   		ViewHolder holder;
   		if (convertView == null){
   			convertView = inflater.inflate(R.layout.scheduler_list_item, null);
	        holder = new ViewHolder();
	        holder.time = (TextView) convertView.findViewById(R.id.time);
	        //holder.time.setTypeface(italicBoldDigital);
	        holder.monday = (TextView) convertView.findViewById(R.id.monday);
			holder.tuesday = (TextView) convertView.findViewById(R.id.tuesday);
			holder.wednesday = (TextView) convertView.findViewById(R.id.wednesday);
			holder.thursday = (TextView) convertView.findViewById(R.id.thursday);
			holder.friday = (TextView) convertView.findViewById(R.id.friday);
			holder.saturday = (TextView) convertView.findViewById(R.id.saturday);
			holder.sunday = (TextView) convertView.findViewById(R.id.sunday);
			holder.clockImage = (ImageView) convertView.findViewById(R.id.clock_image);
			holder.clockImage.setOnClickListener(onClockImageClickListener);
	        convertView.setTag(holder);
	        convertView.setOnLongClickListener(onLongClickListener);
   		} else{
   			holder = (ViewHolder) convertView.getTag();
   		}

   		if(data.get(position).isMonday()){
   			holder.monday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isTuesday()){
   			holder.tuesday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isWednesday()){
   			holder.wednesday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isThursday()){
   			holder.thursday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isFriday()){
   			holder.friday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isSaturday()){
   			holder.saturday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isSunday()){
   			holder.sunday.setTextColor(activeDayColor);
   		}
   		if(data.get(position).isActive()){
   			holder.clockImage.setImageResource(R.drawable.ic_alarm_on_black_48dp);
   		}else{
   			holder.clockImage.setImageResource(R.drawable.ic_alarm_off_black_48dp);
   		}
   		holder.time.setText(data.get(position).getTimeStringFormat());
   		
   		return convertView;
	}
	
	public void removeItem(SchedulerSyncData item){
		this.data.remove(item);
   		notifyDataSetChanged();
	}
	
   	public void addItem (SchedulerSyncData item){
   		this.data.add(item);
   		notifyDataSetChanged();
   	}
   	
    public void refreshDataList(ArrayList<SchedulerSyncData> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener){
    	this.onLongClickListener = onLongClickListener;
    }
    
    public void setOnClockImageClickListener(OnClickListener onClickListener){
    	this.onClockImageClickListener = onClickListener;
    }
    
}

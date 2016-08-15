package com.smartbuilders.synchronizer.ids.adapters;

import java.util.ArrayList;

import com.smartbuilders.synchronizer.ids.model.SyncLog;
import com.smartbuilders.synchronizer.ids.syncadapter.SyncAdapter;
import com.smartbuilders.synchronizer.ids.R;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LogSyncAdapter extends BaseAdapter{
	
	private static class ViewHolder {
		TextView logDate;
		TextView logTime;
		TextView logMessage;
		ImageView logImage;
		View convertView;
    }

  	private ArrayList<SyncLog> data;
  	private LayoutInflater inflater;
  	private Typeface consoleFont;
  	
  	public LogSyncAdapter(Context c, ArrayList<SyncLog> d) {
  		this.data = d;
  		inflater = LayoutInflater.from(c);
  		consoleFont = Typeface.createFromAsset(c.getAssets(), "fonts/console.ttf");
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
   	public View getView(final int position, View convertView, ViewGroup parent) {
   		ViewHolder holder;
   		if (convertView == null){
   			convertView = inflater.inflate(R.layout.logs_list_item, (ViewGroup) null);
	        holder = new ViewHolder();
	        holder.logImage = (ImageView) convertView.findViewById(R.id.log_image);
	        holder.logDate = (TextView) convertView.findViewById(R.id.log_date);
	        holder.logDate.setTypeface(consoleFont);
	        holder.logTime = (TextView) convertView.findViewById(R.id.log_time);
	        holder.logTime.setTypeface(consoleFont);
	        holder.logMessage = (TextView) convertView.findViewById(R.id.log_message);
	        holder.logMessage.setTypeface(consoleFont);
	        holder.convertView = convertView;

	        convertView.setTag(holder);
   		} else{
   			holder = (ViewHolder) convertView.getTag();
   		}
   		if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_STARTED)){
   			holder.logImage.setImageResource(R.drawable.sync_started);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_STARTED)){
   			holder.logImage.setImageResource(R.drawable.ic_alarm_on_black_18dp);
   		}else if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)){
   			holder.logImage.setImageResource(R.drawable.green_checkmark);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED)){
   			holder.logImage.setImageResource(R.drawable.green_checkmark);
   		}else if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)){
   			holder.logImage.setImageResource(R.drawable.cancel);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_CANCELED)){
   			holder.logImage.setImageResource(R.drawable.cancel);
   		}else{
   			holder.logImage.setImageResource(R.drawable.error);
   		}
   		// Setting all values in listview
   		holder.logDate.setText(data.get(position).getLogDateStringFormat());
   		holder.logTime.setText(data.get(position).getLogTimeStringFormat());
   		holder.logMessage.setText(data.get(position).getLogMessage());
   		
   		holder.convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
	            new AlertDialog.Builder(v.getContext())
					.setMessage(data.get(position).getLogMessageDetail())
					.setNeutralButton(R.string.accept, null)
					.show();
				return true;
			}
		});
   		return convertView;
   	}
   	
   	public void addItem (SyncLog item){
   		this.data.add(item);
   		notifyDataSetChanged();
   	}
   	
    public void refreshResultList(ArrayList<SyncLog> data){
        this.data = data;
        notifyDataSetChanged();
    }

}
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

  	private ArrayList<SyncLog> data;
  	private static Typeface mConsoleFont;
    private Context mContext;
  	
  	public LogSyncAdapter(Context c, ArrayList<SyncLog> d) {
        this.mContext = c;
  		this.data = d;
  		mConsoleFont = Typeface.createFromAsset(c.getAssets(), "fonts/console.ttf");
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
        View view = convertView;
   		ViewHolder viewHolder;
   		if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.logs_list_item, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
   		} else{
            viewHolder = (ViewHolder) view.getTag();
   		}

        // Setting all values in listview
   		if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_STARTED)){
            viewHolder.logImage.setImageResource(R.drawable.sync_started);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_STARTED)){
            viewHolder.logImage.setImageResource(R.drawable.ic_alarm_on_black_18dp);
   		}else if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_FINISHED)
				|| data.get(position).getLogType().equals(SyncAdapter.FULL_SYNCHRONIZATION_FINISHED)){
            viewHolder.logImage.setImageResource(R.drawable.green_checkmark);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED)){
            viewHolder.logImage.setImageResource(R.drawable.green_checkmark);
   		}else if(data.get(position).getLogType().equals(SyncAdapter.SYNCHRONIZATION_CANCELED)){
            viewHolder.logImage.setImageResource(R.drawable.cancel);
   		}else if (data.get(position).getLogType().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_CANCELED)){
            viewHolder.logImage.setImageResource(R.drawable.cancel);
   		}else{
            viewHolder.logImage.setImageResource(R.drawable.error);
   		}

        viewHolder.logDate.setText(data.get(position).getLogDateStringFormat());
        viewHolder.logTime.setText(data.get(position).getLogTimeStringFormat());
        viewHolder.logMessage.setText(data.get(position).getLogMessage());

        view.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
	            new AlertDialog.Builder(v.getContext())
					.setMessage(data.get(position).getLogMessageDetail())
					.setNeutralButton(R.string.accept, null)
					.show();
				return true;
			}
		});
   		return view;
   	}

    public static class ViewHolder {
        TextView logDate;
        TextView logTime;
        TextView logMessage;
        ImageView logImage;

        public ViewHolder(View v) {
            logImage = (ImageView) v.findViewById(R.id.log_image);
            logDate = (TextView) v.findViewById(R.id.log_date);
            logDate.setTypeface(mConsoleFont);
            logTime = (TextView) v.findViewById(R.id.log_time);
            logTime.setTypeface(mConsoleFont);
            logMessage = (TextView) v.findViewById(R.id.log_message);
            logMessage.setTypeface(mConsoleFont);
        }
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
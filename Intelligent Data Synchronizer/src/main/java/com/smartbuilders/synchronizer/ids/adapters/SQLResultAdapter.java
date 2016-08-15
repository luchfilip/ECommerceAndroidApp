package com.smartbuilders.synchronizer.ids.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartbuilders.synchronizer.ids.R;
import com.smartbuilders.synchronizer.ids.sqlresult.SQLResultDataRow;

public class SQLResultAdapter extends BaseAdapter{
	static class ViewHolder {
       ArrayList<TextView> dataRowColumns;
       TextView rowNumber;
       int position;
    }

  	private ArrayList<SQLResultDataRow> data;
  	private ArrayList<Integer> columnsWidths;
  	private LayoutInflater inflater;
  	private Typeface consoleFont;
  	public static final int TEXT_SIZE_IN_SP 	= 24;
  	public static final int TV_MARGIN_LEFT 		= 8;
  	public static final int TV_MARGIN_TOP 		= 2;
  	public static final int TV_MARGIN_RIGHT 	= 8;
  	public static final int TV_MARGIN_BOTTOM 	= 8;

  	public SQLResultAdapter(Context c) {
  		this.data = new ArrayList<SQLResultDataRow>();
  		this.columnsWidths = new ArrayList<Integer>();
  		inflater = LayoutInflater.from(c);
  		consoleFont = Typeface.createFromAsset(c.getAssets(), "fonts/console.ttf");
  	}

  	public SQLResultAdapter(Context c, ArrayList<SQLResultDataRow> d, ArrayList<Integer> columnsWidths) {
  		this.data = d;
  		this.columnsWidths = columnsWidths;
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
   	public View getView(int position, View convertView, ViewGroup parent) {
   		ViewHolder holder;
   		if (convertView == null){
   			convertView = inflater.inflate(R.layout.sqlresult_list_item, (ViewGroup) null);
	        holder = new ViewHolder();
	        int n=data.size(), cifras=0;
            while(n!=0){ n = n/10; cifras++; }

	        holder.rowNumber = (TextView) convertView.findViewById(R.id.row_number);
	        LayoutParams layoutParams = new LayoutParams((int)(cifras * TEXT_SIZE_IN_SP * 1.5), LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(TV_MARGIN_LEFT, TV_MARGIN_TOP, TV_MARGIN_RIGHT, TV_MARGIN_BOTTOM);
			holder.rowNumber.setLayoutParams(layoutParams);
	        holder.rowNumber.setTypeface(consoleFont);
	        holder.rowNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_IN_SP);
	        
	        holder.dataRowColumns = new ArrayList<TextView>();
	        
	        TextView column;
	        for(int i=0; i<data.get(0).getColumnsQty(); i++){
	        	column = new TextView(parent.getContext());
	        	layoutParams = new LayoutParams((columnsWidths!=null && columnsWidths.size()>i) 
													? (int) (columnsWidths.get(i) * TEXT_SIZE_IN_SP * 1.39) 
													: LayoutParams.WRAP_CONTENT, 
												LayoutParams.WRAP_CONTENT);
	        	layoutParams.setMargins(TV_MARGIN_LEFT, TV_MARGIN_TOP, TV_MARGIN_RIGHT, TV_MARGIN_BOTTOM);
	        	column.setLayoutParams(layoutParams);
	        	column.setTypeface(consoleFont);
	        	column.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_IN_SP);
	        	((LinearLayout) convertView).addView(column);
	        	holder.dataRowColumns.add(column);
	        }
	        convertView.setTag(holder);
	        holder.position = position;
   		} else{
   			holder = (ViewHolder) convertView.getTag();
   		}
		holder.rowNumber.setText(String.valueOf(position+1));
   		for(int i=0; i<data.get(0).getColumnsQty(); i++){
   			if(data.get(position).getColumn(i)==null){
   				holder.dataRowColumns.get(i).setTextColor(Color.parseColor("#D3D3D3"));
   				holder.dataRowColumns.get(i).setText("(null)");
   			}else{
   				holder.dataRowColumns.get(i).setText(data.get(position).getColumn(i));
   			}
   		}
   		return convertView;
   	}
}

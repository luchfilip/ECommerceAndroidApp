package com.smartbuilders.synchronizer.ids.sqlresult;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class SQLResultDataRow implements Parcelable{

	private ArrayList<String> columns;

	/**
	 * 
	 */
	public SQLResultDataRow(){
		this.columns = new ArrayList<String>();
	}
	
	/**
	 * 
	 * @param index
	 * @return
	 */
	public String getColumn(int index) {
		return columns.get(index);
	}

	/**
	 * 
	 * @param text
	 */
	public void addColumn(String text) {
		this.columns.add(text);
	}

	/**
	 * 
	 * @return
	 */
	public int getColumnsQty(){
		return this.columns.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getColumns(){
		return columns;
	}
	
	/**
	 * 
	 * @param columns
	 */
	private void setColumns(ArrayList<String> columns){
		this.columns = columns;
	}
	
    @Override
    public int describeContents() {
    	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeStringList(getColumns());
    }

    public SQLResultDataRow(Parcel in){
    	this.setColumns(in.createStringArrayList());
    }
    
    public static final Creator<SQLResultDataRow> CREATOR = new Creator<SQLResultDataRow>() {
    	public SQLResultDataRow createFromParcel(Parcel in) {
    		return new SQLResultDataRow(in);
    	}

    	public SQLResultDataRow[] newArray(int size) {
    		return new SQLResultDataRow[size];
    	}
    };
}

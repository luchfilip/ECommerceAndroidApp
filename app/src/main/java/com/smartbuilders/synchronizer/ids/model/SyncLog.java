package com.smartbuilders.synchronizer.ids.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class SyncLog implements Parcelable {

	private Date logDate;
    private String logType;
    private String logMessage;
    private String logMessageDetail;
    public static final int VISIBLE 	= 1;
    public static final int INVISIBLE 	= 0;

    public SyncLog(Date logDate, String logType, String logMessage, String logMessageDetail) {
    	this.logDate = logDate;
    	this.logType = logType;
    	this.logMessage = logMessage;
    	this.logMessageDetail = logMessageDetail;
    }
   
    public SyncLog(String logType, String logMessage, String logMessageDetail) {
    	this.logDate = new Date();
    	this.logType = logType;
    	this.logMessage = logMessage;
    	this.logMessageDetail = logMessageDetail;
    }
    
    /**
     * 
     * @param logDate
     */
    public void setLogDate(Date logDate) {
    	this.logDate = logDate;
    }

    /**
     * 
     * @return
     */
    public Date getLogDate() {
    	return logDate;
    }
    
    /**
     * 
     * @return
     */
    public String getLogDateStringFormat() {
    	SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    	return format.format(logDate);
    }
    
    /**
     * 
     * @return
     */
    public String getLogTimeStringFormat() {
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss aa", Locale.getDefault());
    	return format.format(logDate);
    }

    /**
     * 
     * @return
     */
    public String getLogType() {
        return logType;
    }

    /**
     * 
     * @param logType
     */
    public void setLogType(String logType) {
        this.logType = logType;
    }
    
    /**
     * 
     * @return
     */
    public String getLogMessage() {
        return logMessage;
    }

    /**
     * 
     * @param logMessage
     */
    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    /**
     * 
     * @return
     */
    public String getLogMessageDetail() {
		return logMessageDetail;
	}

    /**
     * 
     * @param logMessageDetail
     */
	public void setLogMessageDetail(String logMessageDetail) {
		this.logMessageDetail = logMessageDetail;
	}

    @Override
    public int describeContents() {
    	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeString(getLogType());
    	dest.writeString(getLogMessage());
    	dest.writeString(getLogMessageDetail());
    	dest.writeLong(getLogDate().getTime());
    }

    public SyncLog(Parcel in){
    	this.logType = in.readString();
    	this.logMessage = in.readString();
    	this.logMessageDetail = in.readString();
    	this.logDate = new Date(in.readLong());
    }
    
    public static final Creator<SyncLog> CREATOR = new Creator<SyncLog>() {
    	public SyncLog createFromParcel(Parcel in) {
    		return new SyncLog(in);
    	}

    	public SyncLog[] newArray(int size) {
    		return new SyncLog[size];
    	}
    };
}
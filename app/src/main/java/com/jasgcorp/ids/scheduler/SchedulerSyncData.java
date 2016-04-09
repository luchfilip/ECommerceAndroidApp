package com.jasgcorp.ids.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Parcel;
import android.os.Parcelable;

import com.jasgcorp.ids.logsync.LogSyncData;

public class SchedulerSyncData implements Parcelable {
    
	private int schedulerSyncDataId;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean sunday;
	private int hourOfDay;
	private int minute;
	private boolean isActive;
	
    /**
	 * @return the monday
	 */
	public boolean isMonday() {
		return monday;
	}

	/**
	 * @param monday the monday to set
	 */
	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	/**
	 * @return the tuesday
	 */
	public boolean isTuesday() {
		return tuesday;
	}

	/**
	 * @param tuesday the tuesday to set
	 */
	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	/**
	 * @return the wednesday
	 */
	public boolean isWednesday() {
		return wednesday;
	}

	/**
	 * @param wednesday the wednesday to set
	 */
	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

	/**
	 * @return the thursday
	 */
	public boolean isThursday() {
		return thursday;
	}

	/**
	 * @param thursday the thursday to set
	 */
	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	/**
	 * @return the friday
	 */
	public boolean isFriday() {
		return friday;
	}

	/**
	 * @param friday the friday to set
	 */
	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	/**
	 * @return the saturday
	 */
	public boolean isSaturday() {
		return saturday;
	}

	/**
	 * @param saturday the saturday to set
	 */
	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	/**
	 * @return the sunday
	 */
	public boolean isSunday() {
		return sunday;
	}

	/**
	 * @param sunday the sunday to set
	 */
	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	/**
	 * @return the hour
	 */
	public int getHour() {
		return hourOfDay;
	}

	/**
	 * @param hour the hour to set
	 */
	public void setHour(int hour) {
		this.hourOfDay = hour;
	}

	/**
	 * @return the minute
	 */
	public int getMinute() {
		return minute;
	}

	/**
	 * @param minute the minute to set
	 */
	public void setMinute(int minute) {
		this.minute = minute;
	}

	/**
	 * Devuelve la hora a la cual esta programada la alarma
	 * @return
	 */
	public String getTimeStringFormat(){
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", java.util.Locale.getDefault());
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		return sdf.format(calendar.getTime());
	}
	
	/**
	 * @return the creator
	 */
	public static Creator<LogSyncData> getCreator() {
		return CREATOR;
	}

	/**
	 * 
	 * @param hourOfDay
	 * @param minute
	 */
	public SchedulerSyncData(int hourOfDay, int minute) {
    	this.hourOfDay = hourOfDay;
    	this.minute = minute;
    }
	
	/**
	 * 
	 * @param id
	 * @param hourOfDay
	 * @param minute
	 * @param monday
	 * @param tuesday
	 * @param wednesday
	 * @param thursday
	 * @param friday
	 * @param saturday
	 * @param sunday
	 * @param isActive
	 */
	public SchedulerSyncData(int id, int hourOfDay, int minute, boolean monday, boolean tuesday, 
			boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean isActive) {
		this.schedulerSyncDataId = id;
    	this.hourOfDay 	= hourOfDay;
    	this.minute 	= minute;
    	this.monday 	= monday;
    	this.tuesday 	= tuesday;
    	this.wednesday 	= wednesday;
    	this.thursday 	= thursday;
    	this.friday 	= friday;
    	this.saturday 	= saturday;
    	this.sunday 	= sunday;
    	this.isActive 	= isActive;
    }
    
	/**
	 * 
	 * @param id
	 */
	public SchedulerSyncData(int id) {
		this.schedulerSyncDataId = id;
    }
	
    public SchedulerSyncData(Parcel in){
    	this.schedulerSyncDataId = in.readInt();
    	this.monday = in.readString().equals(Boolean.TRUE.toString());
    	this.tuesday = in.readString().equals(Boolean.TRUE.toString());
    	this.wednesday = in.readString().equals(Boolean.TRUE.toString());
    	this.thursday = in.readString().equals(Boolean.TRUE.toString());
    	this.friday = in.readString().equals(Boolean.TRUE.toString());
    	this.saturday = in.readString().equals(Boolean.TRUE.toString());
    	this.sunday = in.readString().equals(Boolean.TRUE.toString());
    	this.hourOfDay = in.readInt();
    	this.minute = in.readInt();
    	this.isActive = in.readString().equals(Boolean.TRUE.toString());;
    }

    @Override
    public int describeContents() {
    	return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	dest.writeInt(getSchedulerSyncDataId());
    	dest.writeString(isMonday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isTuesday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isWednesday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isThursday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isFriday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isSaturday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeString(isSunday()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    	dest.writeInt(getHour());
    	dest.writeInt(getMinute());
    	dest.writeString(isActive()?Boolean.TRUE.toString():Boolean.FALSE.toString());
    }

    @Override
    public String toString() {
    	return "[id: "+schedulerSyncDataId+", hour: "+getHour()+", minute: "+getMinute()+"]";
    }
    
    /**
	 * @return the schedulerSyncDataId
	 */
	public int getSchedulerSyncDataId() {
		return schedulerSyncDataId;
	}

	/**
	 * @param schedulerSyncDataId the schedulerSyncDataId to set
	 */
	public void setSchedulerSyncDataId(int schedulerSyncDataId) {
		this.schedulerSyncDataId = schedulerSyncDataId;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public static final Creator<LogSyncData> CREATOR = new Creator<LogSyncData>() {
    	public LogSyncData createFromParcel(Parcel in) {
    		return new LogSyncData(in);
    	}

    	public LogSyncData[] newArray(int size) {
    		return new LogSyncData[size];
    	}
    };
}

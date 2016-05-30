package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Alberto on 7/4/2016.
 */
public class Model implements Parcelable {

    private int id;
    private Date created;
    private String createdBy;
    private Date updated;

    public Model(){

    }

    protected Model(Parcel in) {
        id = in.readInt();
        createdBy = in.readString();
        try{
            Long date = in.readLong();
            setCreated(date > 0 ? new Date(date) : null);
        }catch(Exception ex){ ex.printStackTrace(); }
        try{
            Long date = in.readLong();
            setUpdated(date > 0 ? new Date(date) : null);
        }catch(Exception ex){ ex.printStackTrace(); }
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return String.format("%06d", getId());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createdBy);
        dest.writeLong(created != null ? created.getTime() : -1);
        dest.writeLong(updated != null ? updated.getTime() : -1);
    }
}

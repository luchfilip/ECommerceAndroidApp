package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageSection extends Model implements Parcelable {

    private String name;
    private String description;
    private int priority;

    public MainPageSection() {

    }

    protected MainPageSection(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
        priority = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(priority);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainPageSection> CREATOR = new Creator<MainPageSection>() {
        @Override
        public MainPageSection createFromParcel(Parcel in) {
            return new MainPageSection(in);
        }

        @Override
        public MainPageSection[] newArray(int size) {
            return new MainPageSection[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

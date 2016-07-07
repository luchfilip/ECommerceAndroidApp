package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 7/7/2016.
 */
public class MainPageTitleSection implements Parcelable {

    private String title;

    public MainPageTitleSection (String title){
        setTitle(title);
    }

    protected MainPageTitleSection(Parcel in) {
        title = in.readString();
    }

    public static final Creator<MainPageTitleSection> CREATOR = new Creator<MainPageTitleSection>() {
        @Override
        public MainPageTitleSection createFromParcel(Parcel in) {
            return new MainPageTitleSection(in);
        }

        @Override
        public MainPageTitleSection[] newArray(int size) {
            return new MainPageTitleSection[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
    }
}

package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 30/5/2016.
 */
public class Company extends BusinessPartner implements Parcelable {

    private String contactCenterPhoneNumber;
    private String faxNumber;
    private String webPage;

    public Company() {

    }

    protected Company(Parcel in) {
        super(in);
        contactCenterPhoneNumber = in.readString();
        faxNumber = in.readString();
        webPage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(contactCenterPhoneNumber);
        dest.writeString(faxNumber);
        dest.writeString(webPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Company> CREATOR = new Creator<Company>() {
        @Override
        public Company createFromParcel(Parcel in) {
            return new Company(in);
        }

        @Override
        public Company[] newArray(int size) {
            return new Company[size];
        }
    };

    public String getContactCenterPhoneNumber() {
        return contactCenterPhoneNumber;
    }

    public void setContactCenterPhoneNumber(String contactCenterPhoneNumber) {
        this.contactCenterPhoneNumber = contactCenterPhoneNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }
}

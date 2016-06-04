package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 30/5/2016.
 */
public class BusinessPartner extends Model implements Parcelable {

    private String name;
    private String commercialName;
    private String taxId;
    private String address;
    private String contactPerson;
    private String emailAddress;
    private String phoneNumber;

    public BusinessPartner () {

    }

    protected BusinessPartner(Parcel in) {
        super(in);
        name = in.readString();
        commercialName = in.readString();
        taxId = in.readString();
        address = in.readString();
        contactPerson = in.readString();
        emailAddress = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(commercialName);
        dest.writeString(taxId);
        dest.writeString(address);
        dest.writeString(contactPerson);
        dest.writeString(emailAddress);
        dest.writeString(phoneNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BusinessPartner> CREATOR = new Creator<BusinessPartner>() {
        @Override
        public BusinessPartner createFromParcel(Parcel in) {
            return new BusinessPartner(in);
        }

        @Override
        public BusinessPartner[] newArray(int size) {
            return new BusinessPartner[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

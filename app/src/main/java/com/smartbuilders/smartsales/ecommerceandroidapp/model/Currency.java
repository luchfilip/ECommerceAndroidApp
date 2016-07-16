package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 5/4/2016.
 */
public class Currency extends Model implements Parcelable {

    private String symbol;
    private String countryName;
    private String internationalCode;
    private String name;
    private int unicodeDecimal;
    private String unicodeHex;

    public Currency() {
        super();
    }

    protected Currency(Parcel in) {
        super(in);
        symbol = in.readString();
        countryName = in.readString();
        internationalCode = in.readString();
        name = in.readString();
        unicodeDecimal = in.readInt();
        unicodeHex = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(symbol);
        dest.writeString(countryName);
        dest.writeString(internationalCode);
        dest.writeString(name);
        dest.writeInt(unicodeDecimal);
        dest.writeString(unicodeHex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getInternationalCode() {
        return internationalCode;
    }

    public void setInternationalCode(String internationalCode) {
        this.internationalCode = internationalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUnicodeDecimal() {
        return unicodeDecimal;
    }

    public void setUnicodeDecimal(int unicodeDecimal) {
        this.unicodeDecimal = unicodeDecimal;
    }

    public String getUnicodeHex() {
        return unicodeHex;
    }

    public void setUnicodeHex(String unicodeHex) {
        this.unicodeHex = unicodeHex;
    }
}

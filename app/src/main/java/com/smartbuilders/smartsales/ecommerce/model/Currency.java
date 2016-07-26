package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Alberto on 5/4/2016.
 */
public class Currency extends Model implements Parcelable {

    private String symbol;
    private String countryName;
    private String currencyName;
    private String internationalCode;
    private String unicodeDecimal;
    private String unicodeHex;

    public Currency() {
        super();
    }

    protected Currency(Parcel in) {
        super(in);
        symbol = in.readString();
        countryName = in.readString();
        currencyName = in.readString();
        internationalCode = in.readString();
        unicodeDecimal = in.readString();
        unicodeHex = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(symbol);
        dest.writeString(countryName);
        dest.writeString(currencyName);
        dest.writeString(internationalCode);
        dest.writeString(unicodeDecimal);
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

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getInternationalCode() {
        return internationalCode;
    }

    public void setInternationalCode(String internationalCode) {
        this.internationalCode = internationalCode;
    }

    public String getName() {
        if(unicodeDecimal!=null){
            String[] unicodeDecimalValues = unicodeDecimal.replaceAll("\\s+", "").trim().split(",");
            StringBuilder name = new StringBuilder();
            try {
                for (String unicodeDecimalValue : unicodeDecimalValues) {
                    name.append(Character.toChars(Integer.valueOf(unicodeDecimalValue)));
                }
                return name.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "N/A";
    }

    public String getUnicodeDecimal() {
        return unicodeDecimal;
    }

    public void setUnicodeDecimal(String unicodeDecimal) {
        this.unicodeDecimal = unicodeDecimal;
    }

    public String getUnicodeHex() {
        return unicodeHex;
    }

    public void setUnicodeHex(String unicodeHex) {
        this.unicodeHex = unicodeHex;
    }
}

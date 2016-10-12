package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by stein on 17/7/2016.
 */
public class ProductPriceAvailability extends Model implements Parcelable {

    private float price;
    private int availability;
    private int currencyId;
    private Currency currency;
    private float tax;
    private float totalPrice;

    public ProductPriceAvailability() {
        super();
        currency = new Currency();
    }

    protected ProductPriceAvailability(Parcel in) {
        super(in);
        price = in.readFloat();
        availability = in.readInt();
        currencyId = in.readInt();
        currency = in.readParcelable(Currency.class.getClassLoader());
        tax = in.readFloat();
        totalPrice = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(price);
        dest.writeInt(availability);
        dest.writeInt(currencyId);
        dest.writeParcelable(currency, flags);
        dest.writeFloat(tax);
        dest.writeFloat(totalPrice);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductPriceAvailability> CREATOR = new Creator<ProductPriceAvailability>() {
        @Override
        public ProductPriceAvailability createFromParcel(Parcel in) {
            return new ProductPriceAvailability(in);
        }

        @Override
        public ProductPriceAvailability[] newArray(int size) {
            return new ProductPriceAvailability[size];
        }
    };

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPriceStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getPrice());
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public int getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(int currencyId) {
        this.currencyId = currencyId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public String getTaxStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getTax());
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPriceStringFormat() {
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalPrice());
    }
}

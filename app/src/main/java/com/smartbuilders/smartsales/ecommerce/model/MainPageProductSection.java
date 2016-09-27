package com.smartbuilders.smartsales.ecommerce.model;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductSection extends Model implements Parcelable {

    private String name;
    private String description;
    private int priority;
    private ArrayList<Product> products;
    private Intent seeAllIntent;

    public MainPageProductSection() {

    }

    protected MainPageProductSection(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
        priority = in.readInt();
        products = in.createTypedArrayList(Product.CREATOR);
        seeAllIntent = in.readParcelable(Intent.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(priority);
        dest.writeTypedList(products);
        dest.writeParcelable(seeAllIntent, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MainPageProductSection> CREATOR = new Creator<MainPageProductSection>() {
        @Override
        public MainPageProductSection createFromParcel(Parcel in) {
            return new MainPageProductSection(in);
        }

        @Override
        public MainPageProductSection[] newArray(int size) {
            return new MainPageProductSection[size];
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

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public Intent getSeeAllIntent() {
        return seeAllIntent;
    }

    public void setSeeAllIntent(Intent seeAllIntent) {
        this.seeAllIntent = seeAllIntent;
    }
}

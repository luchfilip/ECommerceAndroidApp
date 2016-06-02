package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageSection extends Model implements Parcelable {

    private String name;
    private String description;
    private int priority;
    private ArrayList<Product> products;

    public MainPageSection() {

    }

    protected MainPageSection(Parcel in) {
        super(in);
        name = in.readString();
        description = in.readString();
        priority = in.readInt();
        products = in.createTypedArrayList(Product.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeInt(priority);
        dest.writeTypedList(products);
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

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}

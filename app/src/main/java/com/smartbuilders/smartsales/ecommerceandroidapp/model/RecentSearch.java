package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearch extends Model implements Parcelable {

    private String textToSearch;
    private int productId;
    private int subcategoryId;

    public RecentSearch(){
        super();
    }

    protected RecentSearch(Parcel in) {
        super(in);
        textToSearch = in.readString();
        productId = in.readInt();
        subcategoryId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(textToSearch);
        dest.writeInt(productId);
        dest.writeInt(subcategoryId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecentSearch> CREATOR = new Creator<RecentSearch>() {
        @Override
        public RecentSearch createFromParcel(Parcel in) {
            return new RecentSearch(in);
        }

        @Override
        public RecentSearch[] newArray(int size) {
            return new RecentSearch[size];
        }
    };

    public String getTextToSearch() {
        return textToSearch;
    }

    public void setTextToSearch(String textToSearch) {
        this.textToSearch = textToSearch;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(int subcategoryId) {
        this.subcategoryId = subcategoryId;
    }
}

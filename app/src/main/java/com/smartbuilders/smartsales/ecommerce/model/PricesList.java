package com.smartbuilders.smartsales.ecommerce.model;

import android.os.Parcelable;

/**
 * Created by AlbertoSarco on 14/10/2016.
 */

public class PricesList extends Model implements Parcelable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

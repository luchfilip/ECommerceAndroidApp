package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderLineDB {

    private Context context;
    private User user;

    public SalesOrderLineDB(Context context, User user){
        this.context = context;
        this.user = user;
    }
}

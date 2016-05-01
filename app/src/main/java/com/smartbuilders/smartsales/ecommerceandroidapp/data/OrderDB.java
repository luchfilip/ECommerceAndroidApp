package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public OrderDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

}

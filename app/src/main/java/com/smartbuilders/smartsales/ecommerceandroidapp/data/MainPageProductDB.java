package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public MainPageProductDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }
}

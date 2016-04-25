package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageSectionDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public MainPageSectionDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<>
}

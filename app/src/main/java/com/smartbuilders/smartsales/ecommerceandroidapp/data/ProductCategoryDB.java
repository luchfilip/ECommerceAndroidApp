package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductCategoryDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;


    public ProductCategoryDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<ProductCategory> getActiveProductCategories(){
        ArrayList<ProductCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT CATEGORY_ID, NAME, DESCRIPTION FROM CATEGORY WHERE ISACTIVE = 'Y'", null);
        while(c.moveToNext()){
            ProductCategory productCategory = new ProductCategory();
            productCategory.setId(c.getInt(0));
            productCategory.setName(c.getString(1));
            productCategory.setDescription(c.getString(2));
            categories.add(productCategory);
        }
        return categories;
    }

}

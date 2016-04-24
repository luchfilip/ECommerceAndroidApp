package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

import java.util.ArrayList;

/**
 * Created by stein on 4/23/2016.
 */
public class ProductBrandDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public ProductBrandDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<ProductBrand> getActiveProductBrands(){
        ArrayList<ProductBrand> productBrands = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT BRAND_ID, NAME, DESCRIPTION FROM BRAND " +
                " WHERE ISACTIVE = 'Y' ORDER BY NAME ASC", null);
        while(c.moveToNext()){
            ProductBrand productBrand = new ProductBrand();
            productBrand.setId(c.getInt(0));
            productBrand.setName(c.getString(1).toUpperCase());
            productBrand.setDescription(c.getString(2).toUpperCase());
            productBrands.add(productBrand);
        }
        return productBrands;
    }
}

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
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT BRAND_ID, NAME, DESCRIPTION FROM BRAND " +
                            " WHERE ISACTIVE = 'Y' ORDER BY NAME ASC", null);
            while(c.moveToNext()){
                ProductBrand productBrand = new ProductBrand();
                productBrand.setId(c.getInt(0));
                productBrand.setName(c.getString(1).toUpperCase());
                productBrand.setDescription(c.getString(2).toUpperCase());
                productBrands.add(productBrand);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return productBrands;
    }
}

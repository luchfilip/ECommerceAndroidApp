package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductSubCategoryDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public ProductSubCategoryDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategories(){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION " +
                " FROM SUBCATEGORY WHERE ISACTIVE = 'Y'", null);
        while(c.moveToNext()){
            ProductSubCategory productSubCategory = new ProductSubCategory();
            productSubCategory.setId(c.getInt(1));
            productSubCategory.setProductCategoryId(c.getInt(0));
            productSubCategory.setName(c.getString(2));
            productSubCategory.setDescription(c.getString(3));
            categories.add(productSubCategory);
        }
        return categories;
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategoriesByCategoryId(int categoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUBCATEGORY_ID, NAME, DESCRIPTION " +
                " FROM SUBCATEGORY WHERE ISACTIVE = 'Y' AND CATEGORY_ID ="+categoryId, null);
        while(c.moveToNext()){
            ProductSubCategory productSubCategory = new ProductSubCategory();
            productSubCategory.setId(c.getInt(0));
            productSubCategory.setProductCategoryId(categoryId);
            productSubCategory.setName(c.getString(1));
            productSubCategory.setDescription(c.getString(2));
            categories.add(productSubCategory);
        }
        return categories;
    }
}

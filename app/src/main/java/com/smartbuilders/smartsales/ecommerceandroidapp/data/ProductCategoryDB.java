package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductCategoryDB {

    private Context context;

    public ProductCategoryDB(Context context){
        this.context = context;
    }

    public ArrayList<ProductCategory> getActiveProductCategories(){
        ArrayList<ProductCategory> categories = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT C.CATEGORY_ID, C.NAME, C.DESCRIPTION, COUNT(C.CATEGORY_ID) " +
                    " FROM CATEGORY C " +
                        " INNER JOIN SUBCATEGORY S ON S.CATEGORY_ID = C.CATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT P ON P.SUBCATEGORY_ID = S.SUBCATEGORY_ID AND P.IS_ACTIVE = ? " +
                    " WHERE C.IS_ACTIVE = ? " +
                    " GROUP BY C.CATEGORY_ID, C.NAME, C.DESCRIPTION ",
                    new String[]{"Y", "Y", "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    ProductCategory productCategory = new ProductCategory();
                    productCategory.setId(c.getInt(0));
                    productCategory.setName(c.getString(1));
                    productCategory.setDescription(c.getString(2));
                    productCategory.setProductsActiveQty(c.getInt(3));
                    categories.add(productCategory);
                }
            }
            Collections.sort(categories, new Comparator<ProductCategory>() {
                @Override
                public int compare(ProductCategory lhs, ProductCategory rhs) {
                    try{
                        return Integer.valueOf(lhs.getName()).compareTo(Integer.valueOf(rhs.getName()));
                    }catch(Exception e){}
                    try{
                        return lhs.getName().compareTo(rhs.getName());
                    }catch(Exception e){}
                    return 0;
                }
            });
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
        }
        return categories;
    }

    public ProductCategory getActiveProductCategoryById(int productCategoryId){
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT CATEGORY_ID, NAME, DESCRIPTION FROM CATEGORY WHERE CATEGORY_ID=? AND IS_ACTIVE=?",
                    new String[]{String.valueOf(productCategoryId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                ProductCategory productCategory = new ProductCategory();
                productCategory.setId(c.getInt(0));
                productCategory.setName(c.getString(1));
                productCategory.setDescription(c.getString(2));
                return productCategory;
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
        }
        return null;
    }
}

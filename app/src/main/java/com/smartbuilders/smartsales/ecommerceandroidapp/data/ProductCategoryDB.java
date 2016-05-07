package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT C.CATEGORY_ID, C.NAME, C.DESCRIPTION, COUNT(C.CATEGORY_ID) " +
                    " FROM CATEGORY C " +
                        " INNER JOIN SUBCATEGORY S ON S.CATEGORY_ID = C.CATEGORY_ID AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN ARTICULOS A ON A.IDPARTIDA = S.SUBCATEGORY_ID AND A.ACTIVO = 'V' " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' AND PA.AVAILABILITY>0 " +
                    " WHERE C.ISACTIVE = 'Y' " +
                    " GROUP BY C.CATEGORY_ID, C.NAME, C.DESCRIPTION ", null);
            while(c.moveToNext()){
                ProductCategory productCategory = new ProductCategory();
                productCategory.setId(c.getInt(0));
                productCategory.setName(c.getString(1));
                productCategory.setDescription(c.getString(2));
                productCategory.setProductsActiveQty(c.getInt(3));
                categories.add(productCategory);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return categories;
    }
}

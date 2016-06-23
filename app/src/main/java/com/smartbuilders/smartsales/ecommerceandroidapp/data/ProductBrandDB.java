package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

import java.util.ArrayList;

/**
 * Created by stein on 4/23/2016.
 */
public class ProductBrandDB {

    private Context context;
    private User user;

    public ProductBrandDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public ArrayList<ProductBrand> getActiveProductBrands(){
        ArrayList<ProductBrand> productBrands = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT B.BRAND_ID, B.NAME, B.DESCRIPTION, COUNT(B.BRAND_ID) " +
                    " FROM BRAND B " +
                        " INNER JOIN PRODUCT P ON P.BRAND_ID = B.BRAND_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                    " WHERE B.IS_ACTIVE = ? " +
                    " GROUP BY B.BRAND_ID, B.NAME, B.DESCRIPTION " +
                    " ORDER BY B.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "Y", "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    ProductBrand productBrand = new ProductBrand();
                    productBrand.setId(c.getInt(0));
                    productBrand.setName(c.getString(1).toUpperCase());
                    productBrand.setDescription(c.getString(2).toUpperCase());
                    productBrand.setProductsActiveQty(c.getInt(3));
                    productBrands.add(productBrand);
                }
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
        return productBrands;
    }
}

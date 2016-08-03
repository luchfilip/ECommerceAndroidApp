package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.util.ArrayList;

/**
 * Created by stein on 29/6/2016.
 */
public class RecommendedProductDB {

    private Context mContext;
    private User mUser;

    public RecommendedProductDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Product> getRecommendedProductsByBusinessPartnerId(int businessPartnerId){
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Integer> productsIds = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT RP.PRODUCT_ID " +
                    " FROM  RECOMMENDED_PRODUCT RP " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = RP.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                    " WHERE RP.BUSINESS_PARTNER_ID = ? AND RP.IS_ACTIVE = ? " +
                    " ORDER BY RP.PRIORITY desc",
                    new String[]{"Y", String.valueOf(businessPartnerId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    productsIds.add(c.getInt(0));
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
        ProductDB productDB = new ProductDB(mContext, mUser);
        for (Integer productId : productsIds) {
            Product product = productDB.getProductById(productId);
            if(product!=null){
                products.add(product);
            }
        }
        return products;
    }

    public int getRecommendedProductsCountByBusinessPartnerId(int businessPartnerId){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(PRODUCT_ID) FROM RECOMMENDED_PRODUCT " +
                            " WHERE BUSINESS_PARTNER_ID = ? AND IS_ACTIVE = ? ",
                    new String[]{String.valueOf(businessPartnerId), "Y"}, null);
            if (c!=null && c.moveToNext()) {
                return c.getInt(0);
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
        return 0;
    }

}

package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 29/6/2016.
 */
public class ProductRecentlySeenDB {

    private Context mContext;
    private User mUser;

    public ProductRecentlySeenDB(Context context, User user) {
        this.mContext = context;
        this.mUser = user;
    }

    public void addProduct(int productId){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), new ContentValues(),
                    "INSERT OR REPLACE INTO PRODUCT_RECENTLY_SEEN (PRODUCT_RECENTLY_SEEN_ID, USER_ID, PRODUCT_ID, " +
                            " APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(getMaxProductRecentlySeenId() + 1), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(productId), Utils.getAppVersionName(mContext),
                            mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getProductsRecentlySeenList(){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT PRODUCT_ID " +
                    " FROM PRODUCT_RECENTLY_SEEN " +
                    " WHERE USER_ID = ? " +
                    " ORDER BY PRODUCT_RECENTLY_SEEN_ID desc " +
                    " LIMIT 30",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                ProductDB productDB = new ProductDB(mContext, mUser);
                while(c.moveToNext()){
                    products.add(productDB.getProductById(c.getInt(0)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    private int getMaxProductRecentlySeenId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(PRODUCT_RECENTLY_SEEN_ID) FROM PRODUCT_RECENTLY_SEEN WHERE USER_ID = ?",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
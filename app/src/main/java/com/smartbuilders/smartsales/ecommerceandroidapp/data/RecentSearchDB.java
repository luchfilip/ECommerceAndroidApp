package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.RecentSearch;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearchDB {

    private Context mContext;
    private User mCurrentUser;
    private ProductSubCategoryDB mProductSubCategoryDB;

    public RecentSearchDB(Context context, User user){
        this.mContext = context;
        this.mCurrentUser = user;
        this.mProductSubCategoryDB = new ProductSubCategoryDB(context);
    }

    /**
     *
     * @param text
     * @param productId
     * @param subCategoryId
     */
    public void insertRecentSearch(String text, int productId, int subCategoryId){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), new ContentValues(),
                    "INSERT INTO RECENT_SEARCH (PRODUCT_ID, SUBCATEGORY_ID, TEXT_TO_SEARCH, " +
                            " APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(productId), String.valueOf(subCategoryId), text,
                            Utils.getAppVersionName(mContext), mCurrentUser.getUserName(),
                            Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param recentSearchId
     */
    public void deleteRecentSearchById(int recentSearchId){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), new ContentValues(), "DELETE FROM RECENT_SEARCH WHERE RECENT_SEARCH_ID = ?",
                    new String[]{String.valueOf(recentSearchId)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<RecentSearch> getRecentSearches(){
        ArrayList<RecentSearch> recentSearches = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "SELECT RECENT_SEARCH_ID, TEXT_TO_SEARCH, PRODUCT_ID, SUBCATEGORY_ID " +
                    " FROM RECENT_SEARCH " +
                    " ORDER BY CREATE_TIME desc",
                    null, null);
            if(c!=null){
                while(c.moveToNext()){
                    RecentSearch recentSearch = new RecentSearch();
                    recentSearch.setId(c.getInt(0));
                    recentSearch.setTextToSearch(c.getString(1));
                    recentSearch.setProductId(c.getInt(2));
                    recentSearch.setSubcategoryId(c.getInt(3));
                    recentSearch.setProductSubCategory(mProductSubCategoryDB.getActiveProductSubCategoryById(recentSearch.getSubcategoryId()));
                    recentSearches.add(recentSearch);
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
        return recentSearches;
    }

    public void deleteAllRecentSearches(){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), new ContentValues(), "DELETE FROM RECENT_SEARCH", new String[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

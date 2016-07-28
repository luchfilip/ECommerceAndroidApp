package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;

import com.smartbuilders.smartsales.ecommerce.model.RecentSearch;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearchDB {

    private Context mContext;
    private User mUser;
    private ProductSubCategoryDB mProductSubCategoryDB;

    public RecentSearchDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
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
            if(!TextUtils.isEmpty(text)){
                mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                        .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE))
                        .build(), null,
                        "INSERT INTO RECENT_SEARCH (RECENT_SEARCH_ID, USER_ID, PRODUCT_ID, SUBCATEGORY_ID, TEXT_TO_SEARCH, " +
                                " CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new String[]{String.valueOf(getMaxRecentSearchId() + 1), String.valueOf(mUser.getServerUserId()),
                                String.valueOf(productId), String.valueOf(subCategoryId), text,
                                Utils.getAppVersionName(mContext), mUser.getUserName(),
                                Utils.getMacAddress(mContext)});
            }
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE))
                    .build(), null, "UPDATE RECENT_SEARCH SET IS_ACTIVE = ? WHERE RECENT_SEARCH_ID = ? AND USER_ID = ?",
                    new String[]{"N", String.valueOf(recentSearchId), String.valueOf(mUser.getServerUserId())});
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT RECENT_SEARCH_ID, TEXT_TO_SEARCH, PRODUCT_ID, SUBCATEGORY_ID " +
                    " FROM RECENT_SEARCH " +
                    " WHERE USER_ID = ? " +
                    " ORDER BY CREATE_TIME desc",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE))
                    .build(), null, "UPDATE RECENT_SEARCH SET IS_ACTIVE = ? WHERE USER_ID = ?",
                    new String[]{"N", String.valueOf(mUser.getServerUserId())});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private int getMaxRecentSearchId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(RECENT_SEARCH_ID) FROM RECENT_SEARCH WHERE USER_ID = ?",
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

package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.RecentSearch;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearchDB {

    private Context mContext;
    private User mCurrentUser;

    public RecentSearchDB(Context context, User user){
        this.mContext = context;
        this.mCurrentUser = user;
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
                    .build(), null,
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
                    .build(), null, "DELETE FROM RECENT_SEARCH WHERE RECENT_SEARCH_ID = ?",
                    new String[]{String.valueOf(recentSearchId)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param limit
     * @return
     */
    public ArrayList<RecentSearch> getRecentSearches(int limit){
        ArrayList<RecentSearch> recentSearches = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "SELECT RS.TEXT_TO_SEARCH, RS.PRODUCT_ID, RS.SUBCATEGORY_ID, RS.RECENT_SEARCH_ID, S.NAME, S.DESCRIPTION " +
                    " FROM RECENT_SEARCH RS " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = RS.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                    " ORDER BY RS.CREATE_TIME desc",
                    new String[]{"Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    RecentSearch recentSearch = new RecentSearch();
                    recentSearch.setId(c.getInt(3));
                    recentSearch.setTextToSearch(c.getString(0));
                    recentSearch.setProductId(c.getInt(1));
                    recentSearch.setSubcategoryId(c.getInt(2));
                    recentSearch.setProductSubCategory(new ProductSubCategory(0, c.getInt(2), c.getString(4), c.getString(5)));
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
                    .build(), null, "DELETE FROM RECENT_SEARCH", new String[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

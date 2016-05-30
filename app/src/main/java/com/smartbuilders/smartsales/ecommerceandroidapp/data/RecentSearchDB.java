package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.RecentSearch;

import java.util.ArrayList;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearchDB {

    private Context context;
    private User user;

    public RecentSearchDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    /**
     *
     * @param text
     * @param productId
     * @param subCategoryId
     */
    public void insertRecentSearch(String text, int productId, int subCategoryId){
        try {
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, "INSERT INTO RECENT_SEARCH (PRODUCT_ID, SUBCATEGORY_ID, TEXT_TO_SEARCH) VALUES (?, ?, ?)",
                    new String[]{String.valueOf(productId), String.valueOf(subCategoryId), text});
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
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, "DELETE FROM RECENT_SEARCH WHERE RECENT_SEARCH_ID = ?", new String[]{String.valueOf(recentSearchId)});
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
            String sql = "SELECT TEXT_TO_SEARCH, PRODUCT_ID, SUBCATEGORY_ID, RECENT_SEARCH_ID FROM RECENT_SEARCH ORDER BY CREATE_TIME desc" ;
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, null, null);
            while(c.moveToNext()){
                RecentSearch recentSearch = new RecentSearch();
                recentSearch.setId(c.getInt(3));
                recentSearch.setTextToSearch(c.getString(0));
                recentSearch.setProductId(c.getInt(1));
                recentSearch.setSubcategoryId(c.getInt(2));
                recentSearches.add(recentSearch);
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
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, "DELETE FROM RECENT_SEARCH", new String[0]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

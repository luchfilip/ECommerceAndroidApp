package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.RecentSearch;

import java.util.ArrayList;

/**
 * Created by stein on 12/5/2016.
 */
public class RecentSearchDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public RecentSearchDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    /**
     *
     * @param text
     * @param productId
     * @param subCategoryId
     */
    public void insertRecentSearch(String text, int productId, int subCategoryId){
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("PRODUCT_ID", productId);
            cv.put("SUBCATEGORY_ID", subCategoryId);
            cv.put("TEXT_TO_SEARCH", text);
            db.insert("RECENT_SEARCH", null, cv);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param recentSearchId
     */
    public void deleteRecentSearchById(int recentSearchId){
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            db.delete("RECENT_SEARCH", "RECENT_SEARCH_ID = ?", new String[]{String.valueOf(recentSearchId)});
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @param limit
     * @return
     */
    public ArrayList<RecentSearch> getRecentSearches(int limit){
        ArrayList<RecentSearch> recentSearches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.query("RECENT_SEARCH", new String[]{"TEXT_TO_SEARCH", "PRODUCT_ID",
                    "SUBCATEGORY_ID", "RECENT_SEARCH_ID"}, null, null, null, null, "CREATE_TIME desc");
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
            if (db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return recentSearches;
    }

    public void deleteAllRecentSearches(){
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            db.delete("RECENT_SEARCH", null, null);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

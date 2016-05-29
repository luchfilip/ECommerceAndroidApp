package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageSection;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageSectionDB {

    private Context context;
    private User user;
    //private DatabaseHelper dbh;

    public MainPageSectionDB(Context context, User user){
        this.context = context;
        this.user = user;
        //this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<MainPageSection> getActiveMainPageSections(){
        ArrayList<MainPageSection> mainPageSections = new ArrayList<>();
        //SQLiteDatabase db = null;
        Cursor c = null;
        try {
            //db = dbh.getReadableDatabase();
            String sql = "SELECT MAINPAGE_SECTION_ID, NAME, DESCRIPTION " +
                    " FROM MAINPAGE_SECTION " +
                    " WHERE ISACTIVE = 'Y' " +
                    " ORDER BY PRIORITY ASC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, null, null);
            //c = db.rawQuery("SELECT MAINPAGE_SECTION_ID, NAME, DESCRIPTION " +
            //        " FROM MAINPAGE_SECTION " +
            //        " WHERE ISACTIVE = 'Y' " +
            //        " ORDER BY PRIORITY ASC", null);
            while(c.moveToNext()){
                MainPageSection mainPageSection = new MainPageSection();
                mainPageSection.setId(c.getInt(0));
                mainPageSection.setName(c.getString(1));
                mainPageSection.setDescription(c.getString(2));
                mainPageSections.add(mainPageSection);
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
            //if(db!=null){
            //    try {
            //        db.close();
            //    } catch (Exception e){
            //        e.printStackTrace();
            //    }
            //}
        }
        return mainPageSections;
    }
}

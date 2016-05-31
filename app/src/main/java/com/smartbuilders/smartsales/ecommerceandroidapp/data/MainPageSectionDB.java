package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.itextpdf.text.log.SysoCounter;
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

    public MainPageSectionDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public ArrayList<MainPageSection> getActiveMainPageSections(){
        ArrayList<MainPageSection> mainPageSections = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT DISTINCT MS.MAINPAGE_SECTION_ID, MS.NAME, MS.DESCRIPTION " +
                    " FROM MAINPAGE_SECTION MS " +
                        " INNER JOIN MAINPAGE_PRODUCT MP ON MP.MAINPAGE_SECTION_ID = MS.MAINPAGE_SECTION_ID AND MP.ISACTIVE = 'Y' " +
                    " WHERE MS.ISACTIVE = 'Y' " +
                    " ORDER BY MS.PRIORITY ASC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, null, null);
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
        }
        return mainPageSections;
    }
}

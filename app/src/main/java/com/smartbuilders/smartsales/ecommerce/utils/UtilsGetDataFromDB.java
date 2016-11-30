package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

/**
 * Created by AlbertoSarco on 30/11/2016.
 */
public class UtilsGetDataFromDB {

    public static int getCountFromTableName(Context context, User user, String tableName) {
        Cursor c = null;
        try {
            c = context.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                            .build(), null,
                            "SELECT count(*) FROM " + tableName, null, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
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
        return -1;
    }

}

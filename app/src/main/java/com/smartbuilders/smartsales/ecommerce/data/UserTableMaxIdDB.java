package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by stein on 3/8/2016.
 */
public class UserTableMaxIdDB {

    public static int getNewIdForTable(Context context, User user, String tableName) {
        int newId = 0;
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, "select MAX(ID) from USER_TABLE_MAX_ID where USER_ID = ? AND TABLE_NAME = ?",
                    new String[]{String.valueOf(user.getServerUserId()), tableName}, null);
            if(c!=null && c.moveToNext()){
                newId = c.getInt(0);
            }
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        newId++;
        int rowsAffected = context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                null,
                "insert into USER_TABLE_MAX_ID (USER_ID, TABLE_NAME, ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?) ",
                new String[]{String.valueOf(user.getServerUserId()), tableName, String.valueOf(newId),
                        DateFormat.getCurrentDateTimeSQLFormat(), Utils.getAppVersionName(context),
                        user.getUserName(), Utils.getMacAddress(context)});
        if (rowsAffected <= 0){
            throw new SQLException("Error creando nuevo ID, no se insertó el registro en la base de datos.");
        }
        return newId;
    }
}

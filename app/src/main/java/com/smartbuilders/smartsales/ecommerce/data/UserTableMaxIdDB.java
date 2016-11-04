package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Created by stein on 3/8/2016.
 */
public class UserTableMaxIdDB {

    public static int getNewIdForTable(Context context, User user, final String tableName) {
        int newId = 0;
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, "select MAX(ID) from USER_TABLE_MAX_ID where USER_ID = ? AND TABLE_NAME = ?",
                    new String[]{String.valueOf(user.getServerUserId()), tableName}, null);
            if(c!=null){
                if(c.moveToNext()){
                    newId = c.getInt(0);
                }else{
                    context.getContentResolver()
                            .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                                    null,
                                    "insert into USER_TABLE_MAX_ID (USER_ID, TABLE_NAME, ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                                    " VALUES (?, ?, ?, ?, ?, ?, ?) ",
                                    new String[]{String.valueOf(user.getServerUserId()), tableName, String.valueOf(newId),
                                            DateFormat.getCurrentDateTimeSQLFormat(), Utils.getAppVersionName(context),
                                            user.getUserName(), Utils.getMacAddress(context)});
                }
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

        try {
            String sqlValidator = null;
            String[] selectionArgs = null;
            switch (tableName) {
                case "ECOMMERCE_ORDER":
                    sqlValidator = "SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "ECOMMERCE_ORDER_LINE":
                    sqlValidator = "SELECT MAX(ECOMMERCE_ORDER_LINE_ID) FROM ECOMMERCE_ORDER_LINE WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "ECOMMERCE_SALES_ORDER":
                    sqlValidator = "SELECT MAX(ECOMMERCE_SALES_ORDER_ID) FROM ECOMMERCE_SALES_ORDER WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "ECOMMERCE_SALES_ORDER_LINE":
                    sqlValidator = "SELECT MAX(ECOMMERCE_SALES_ORDER_LINE_ID) FROM ECOMMERCE_SALES_ORDER_LINE WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "USER_BUSINESS_PARTNER":
                    sqlValidator = "SELECT MAX(USER_BUSINESS_PARTNER_ID) FROM USER_BUSINESS_PARTNER WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "RECENT_SEARCH":
                    sqlValidator = "SELECT MAX(RECENT_SEARCH_ID) FROM RECENT_SEARCH WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;
                case "PRODUCT_RECENTLY_SEEN":
                    sqlValidator = "SELECT MAX(PRODUCT_RECENTLY_SEEN_ID) FROM PRODUCT_RECENTLY_SEEN WHERE USER_ID = ?";
                    selectionArgs = new String[]{String.valueOf(user.getServerUserId())};
                    break;

            }
            if (sqlValidator!=null){
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null, sqlValidator, selectionArgs, null);
                if(c!=null && c.moveToNext() && c.getInt(0)>newId){
                    newId = c.getInt(0);
                }
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

        //se incrementa el id en uno
        newId++;
        int rowsAffected = context.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                        null,
                        "UPDATE USER_TABLE_MAX_ID " +
                                " SET ID = ?, CREATE_TIME = ?, APP_VERSION = ?, APP_USER_NAME = ?, DEVICE_MAC_ADDRESS = ?, SEQUENCE_ID = 0 " +
                        " WHERE USER_ID = ? AND TABLE_NAME = ?",
                        new String[]{String.valueOf(newId), DateFormat.getCurrentDateTimeSQLFormat(),
                                Utils.getAppVersionName(context), user.getUserName(), Utils.getMacAddress(context),
                                String.valueOf(user.getServerUserId()), tableName});
        if (rowsAffected <= 0){
            throw new SQLException("Error creando nuevo ID, no se insertó el registro en la base de datos.");
        }
        return newId;
    }
}

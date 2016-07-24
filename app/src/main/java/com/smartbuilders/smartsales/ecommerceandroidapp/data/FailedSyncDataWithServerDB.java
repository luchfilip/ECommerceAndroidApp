package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.FailedSyncDataWithServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 23/7/2016.
 */
public class FailedSyncDataWithServerDB {

    private Context mContext;
    private User mUser;

    public FailedSyncDataWithServerDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public void insertFailedSyncDataWithServerRow(String selection, String selectionArgs, int columnCount) {
        mContext.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                        null,
                        "INSERT INTO FAILED_SYNC_DATA_WITH_SERVER (selection, selectionArgs, columnCount) VALUES (?, ?, ?) ",
                        new String[]{selection, selectionArgs, String.valueOf(columnCount)});
    }

    public void deleteFailedSyncDataWithServerById(int id){
        mContext.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                        null, "DELETE FROM FAILED_SYNC_DATA_WITH_SERVER WHERE row_id = ?",
                        new String[]{String.valueOf(id)});
    }

    public void cleanFailedSyncDataWithServer(){
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            new ContentValues(), "DELETE FROM FAILED_SYNC_DATA_WITH_SERVER", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<FailedSyncDataWithServer> getAllFailedSyncDataWithServer(){
        List<FailedSyncDataWithServer> failedSyncDataWithServerList = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT row_id, selection, selectionArgs, columnCount " +
                            " FROM FAILED_SYNC_DATA_WITH_SERVER " +
                            " ORDER BY row_id ASC",
                    null, null);
            if(c!=null){
                while(c.moveToNext()){
                    failedSyncDataWithServerList.add(new FailedSyncDataWithServer(c.getInt(0),
                            c.getString(1), c.getString(2), c.getInt(3)));
                }
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
        return failedSyncDataWithServerList;
    }
}

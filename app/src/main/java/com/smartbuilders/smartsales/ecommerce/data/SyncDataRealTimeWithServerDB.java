package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.SyncDataRealTimeWithServer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 23/7/2016.
 */
public class SyncDataRealTimeWithServerDB {

    private static final String TAG = SyncDataRealTimeWithServerDB.class.getSimpleName();
    private Context mContext;
    private User mUser;

    public SyncDataRealTimeWithServerDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public void insertDataToSyncWithServer(String selection, String selectionArgs, int columnCount) {
        mContext.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                        null, "INSERT INTO FAILED_SYNC_DATA_WITH_SERVER (selection, selectionArgs, columnCount) VALUES (?, ?, ?) ",
                        new String[]{selection, selectionArgs, String.valueOf(columnCount)});
    }

    public void deleteDataToSyncWithServer(String idsToDelete){
        Log.w(TAG, "deleteDataToSyncWithServer("+idsToDelete+")");
        mContext.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                        null, "DELETE FROM FAILED_SYNC_DATA_WITH_SERVER WHERE ROW_ID IN ("+idsToDelete+")", null);
    }

    public void deleteDataToSyncWithServer(){
        mContext.getContentResolver()
                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                        null, "DELETE FROM FAILED_SYNC_DATA_WITH_SERVER", null);
    }

    public List<SyncDataRealTimeWithServer> getAllDataToSyncWithServer(){
        List<SyncDataRealTimeWithServer> syncDataRealTimeWithServerList = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, "SELECT row_id, selection, selectionArgs, columnCount FROM FAILED_SYNC_DATA_WITH_SERVER ORDER BY row_id ASC",
                            null, null);
            if(c!=null){
                while(c.moveToNext()){
                    syncDataRealTimeWithServerList.add(new SyncDataRealTimeWithServer(c.getInt(0),
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
        return syncDataRealTimeWithServerList;
    }
}

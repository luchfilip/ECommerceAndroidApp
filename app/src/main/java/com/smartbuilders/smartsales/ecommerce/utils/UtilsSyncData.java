package com.smartbuilders.smartsales.ecommerce.utils;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.SynchronizerContentProvider;

/**
 * Created by AlbertoSarco on 30/11/2016.
 */
public class UtilsSyncData {

    public static void requestSyncByTableName(Context context, User user, String tableName) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver()
                        .query(SynchronizerContentProvider.SYNC_DATA_FROM_SERVER_URI.buildUpon()
                                .appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, user.getUserId())
                                .appendQueryParameter(SynchronizerContentProvider.KEY_TABLES_TO_SYNC, tableName).build(),
                                null, null, null, null);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}

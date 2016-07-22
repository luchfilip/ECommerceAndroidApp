package com.smartbuilders.smartsales.ecommerceandroidapp.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by stein on 22/7/2016.
 */
public class SyncDataWithServer extends IntentService {

    private static final String TAG = SyncDataWithServer.class.getSimpleName();

    public static final String KEY_SQL_SELECTION = "SyncDataWithServer.KEY_SQL_SELECTION";
    public static final String KEY_SQL_SELECTION_ARGS = "SyncDataWithServer.KEY_SQL_SELECTION_ARGS";

    public SyncDataWithServer() {
        super(SyncDataWithServer.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SyncDataWithServer(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        String selection = workIntent.getStringExtra(KEY_SQL_SELECTION);
        String[] selectionArgs = workIntent.getStringArrayExtra(KEY_SQL_SELECTION_ARGS);
        Log.d(TAG, "selection: "+selection);
        Log.d(TAG, "selectionArgs: "+selectionArgs);
    }
}

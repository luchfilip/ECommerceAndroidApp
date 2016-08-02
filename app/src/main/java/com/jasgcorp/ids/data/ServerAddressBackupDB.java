package com.jasgcorp.ids.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;

import java.util.ArrayList;

/**
 * Created by stein on 24/7/2016.
 */
public class ServerAddressBackupDB {

    private Context mContext;

    public ServerAddressBackupDB(Context context){
        this.mContext = context;
    }

    public void addServerAddressBackup(String serverAddress){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "INSERT OR REPLACE INTO IDS_SERVER_ADDRESS_BACKUP (SERVER_ADDRESS, IS_ACTIVE) VALUES (?, ?)",
                    new String[]{serverAddress, "Y"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getActiveServerAddressBackupList(){
        ArrayList<String> serverAddressBackupList = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "select SERVER_ADDRESS from IDS_SERVER_ADDRESS_BACKUP where IS_ACTIVE=?",
                    new String[]{"Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    serverAddressBackupList.add(c.getString(0));
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
        return serverAddressBackupList;
    }
}

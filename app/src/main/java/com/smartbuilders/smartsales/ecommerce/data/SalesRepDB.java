package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

/**
 * Created by AlbertoSarco on 18/1/2017.
 */

public class SalesRepDB {

    private Context mContext;
    private User mUser;

    public SalesRepDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public int getSalesRepId() {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "SELECT SALES_REP_ID FROM SALES_REP WHERE USER_ID=? AND IS_ACTIVE=?",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y"}, null);
            if(c!=null && c.moveToNext()) {
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
        return 0;
    }
}

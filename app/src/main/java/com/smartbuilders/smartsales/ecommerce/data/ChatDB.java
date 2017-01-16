package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.smartbuilders.synchronizer.ids.model.User;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */

public class ChatDB {

    final private Context mContext;
    final private User mUser;

    public ChatDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public int getUnreadMessagesCount() {
        return 0;
    }
}

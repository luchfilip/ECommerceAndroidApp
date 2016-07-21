package com.jasgcorp.ids.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.accounts.Account;
import android.content.ContentResolver;

public class AccountUtilities {
	
    /**
     * Devuelve la ultima fecha de sincronizacion de un Account en un Sync Adapter
     * @param mAccount
     * @param contentAuthority
     * @return
     */
    public static String getLastSyncTime(Account mAccount, String contentAuthority) {
        try {
            Method getSyncStatus = ContentResolver.class.getMethod(
                    "getSyncStatus", Account.class, String.class);
            if (mAccount != null) {
                Object status = getSyncStatus.invoke(null, mAccount, contentAuthority);
                Class<?> statusClass = Class
                        .forName("android.content.SyncStatusInfo");
                boolean isStatusObject = statusClass.isInstance(status);
                if (isStatusObject) {
                    return new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
                            .format(new Date((statusClass.getField("lastSuccessTime")).getLong(status)));
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | IllegalArgumentException | ClassNotFoundException | NoSuchFieldException
                | NullPointerException e) {
        	e.printStackTrace();
        }
        return null;
    }
	
}

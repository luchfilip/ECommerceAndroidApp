package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by AlbertoSarco on 11/11/2016.
 */
public class NotificationHistoryDB {

    private Context mContext;
    private User mUser;

    public NotificationHistoryDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public void insertNotificationHistory(String title, String message, int type, int relatedId) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "INSERT INTO NOTIFICATION_HISTORY (TITLE, MESSAGE, TYPE, RELATED_ID, STATUS, CREATE_TIME) " +
                                    " VALUES (?, ?, ?, ?, ?, ?) ",
                            new String[]{title, message, String.valueOf(type), String.valueOf(relatedId),
                                    String.valueOf(NotificationHistory.STATUS_NOT_SEEN), DateFormat.getCurrentDateTimeSQLFormat()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NotificationHistory> getNotifications() {
        ArrayList<NotificationHistory> notificationHistories = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT NOTIFICATION_HISTORY_ID, TITLE, MESSAGE, RELATED_ID, TYPE, STATUS, CREATE_TIME " +
                    " FROM NOTIFICATION_HISTORY WHERE IS_ACTIVE = ? " +
                    " ORDER BY NOTIFICATION_HISTORY_ID DESC", new String[]{"Y"}, null);
            if(c!=null){
                while (c.moveToNext()) {
                    NotificationHistory notificationHistory = new NotificationHistory();
                    notificationHistory.setId(c.getInt(0));
                    notificationHistory.setTitle(c.getString(1));
                    notificationHistory.setMessage(c.getString(2));
                    notificationHistory.setRelatedId(c.getInt(3));
                    notificationHistory.setType(c.getInt(4));
                    notificationHistory.setStatus(c.getInt(5));
                    try{
                        notificationHistory.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(6)).getTime()));
                    }catch(ParseException ex){
                        try {
                            notificationHistory.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(c.getString(6)).getTime()));
                        } catch (ParseException e) {
                            //empty
                        }
                    }catch(Exception ex){
                        //empty
                    }
                    notificationHistories.add(notificationHistory);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return notificationHistories;
    }

    public void updateNotificationsStatus(int status) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, "UPDATE NOTIFICATION_HISTORY SET STATUS = ?",
                            new String[]{String.valueOf(status)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getCountByStatus(int status) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                            "SELECT count(NOTIFICATION_HISTORY_ID) FROM NOTIFICATION_HISTORY WHERE STATUS = ? AND IS_ACTIVE = ?",
                            new String[]{String.valueOf(status), "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public String deleteNotification(int id){
        try {
            //Solo se permite eliminar la linea si no esta asociada a un pedido
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE NOTIFICATION_HISTORY SET IS_ACTIVE = ? WHERE NOTIFICATION_HISTORY_ID = ?",
                    new String[]{"N", String.valueOf(id)});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String restoreNotification(int id){
        try {
            //Solo se permite eliminar la linea si no esta asociada a un pedido
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE NOTIFICATION_HISTORY SET IS_ACTIVE = ? WHERE NOTIFICATION_HISTORY_ID = ?",
                    new String[]{"Y", String.valueOf(id)});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deactivateAllNotifications() {
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE NOTIFICATION_HISTORY SET IS_ACTIVE = ?",
                    new String[]{"N"});
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
}

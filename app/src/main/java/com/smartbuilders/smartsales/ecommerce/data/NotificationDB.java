package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.Notification;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AlbertoSarco on 11/11/2016.
 */

public class NotificationDB {

    private Context mContext;
    private User mUser;

    public NotificationDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Notification> getNotifications() {
        ArrayList<Notification> notifications = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "",
                    new String[]{}, null);
            if(c!=null && c.moveToNext()){
                while (c.moveToNext()) {
                    Notification notification = new Notification();

                    notifications.add(notification);
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
        Notification notification = new Notification();
        notification.setTitle("Nueva notificación 1.");
        notification.setMessage("Esta es una nueva notificación generada para el usuario.");
        notification.setCreated(new Date());
        notifications.add(notification);

        notification = new Notification();
        notification.setTitle("Nueva notificación 2.");
        notification.setMessage("Esta es una nueva notificación generada para el usuario.");
        notification.setCreated(new Date());
        notifications.add(notification);

        notification = new Notification();
        notification.setTitle("Nueva notificación 3.");
        notification.setMessage("Esta es una nueva notificación generada para el usuario.");
        notification.setCreated(new Date());
        notifications.add(notification);

        notification = new Notification();
        notification.setTitle("Nueva notificación 4.");
        notification.setMessage("Esta es una nueva notificación generada para el usuario.");
        notification.setCreated(new Date());
        notifications.add(notification);

        notification = new Notification();
        notification.setTitle("Nueva notificación 5.");
        notification.setMessage("Esta es una nueva notificación generada para el usuario.");
        notification.setCreated(new Date());
        notifications.add(notification);
        return notifications;
    }

    public void updateNotificationsStatus() {

    }
}

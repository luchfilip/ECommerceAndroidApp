package com.smartbuilders.smartsales.ecommerce.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.smartbuilders.smartsales.ecommerce.R;

/**
 * Created by stein on 31/7/2016.
 */
public class NotificationUtils {

    private static final int NOTIFICATION_ID = 111222;

    public static void createNotification(Context context, String contentTitle, String contentText,
                                          Intent resultIntent) {
        NotificationCompat.Builder mBuilder;

        //if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(contentTitle)
                    .setColor(context.getResources().getColor(R.color.colorPrimary))
                    .setContentText(contentText);
        //} else {
        //    // Lollipop specific setColor method goes here.
        //    mBuilder = new NotificationCompat.Builder(context)
        //            .setSmallIcon(R.drawable.ic_launcher_transparent)
        //            .setContentTitle(contentTitle)
        //            .setColor(context.getResources().getColor(R.color.colorPrimary))
        //            .setContentText(contentText);
        //}

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        //stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);

        /*******************************************************************************************/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String ringtone = sharedPreferences.getString("notifications_new_availabilities_wish_list_ringtone",
                context.getString(R.string.pref_ringtone_silent));
        boolean useSound = !TextUtils.isEmpty(ringtone) && !ringtone.equals(context.getString(R.string.pref_ringtone_silent));
        boolean vibrate = sharedPreferences.getBoolean("notifications_new_availabilities_wish_list_vibrate", true);
        if(useSound && vibrate) {
            mBuilder.setSound(Uri.parse(ringtone));
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE
                    | Notification.DEFAULT_LIGHTS);
        } else if (useSound) {
            mBuilder.setSound(Uri.parse(ringtone));
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        } else if (vibrate) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE
                    | Notification.DEFAULT_LIGHTS);
        }
        /*******************************************************************************************/

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static void cancelNotification(Context context){
        try {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.smartbuilders.smartsales.ecommerce.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.WishListActivity;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.BadgeUtils;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Jesus Sarco, 31.07.2016
 */
public class SynchronizationBroadcastReceiver extends BroadcastReceiver {
    public SynchronizationBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null && intent.getAction()!=null){
            if(intent.getAction().equals(SyncAdapter.SYNCHRONIZATION_FINISHED) ||
                    intent.getAction().equals(SyncAdapter.PERIODIC_SYNCHRONIZATION_FINISHED)){
                checkNewAvailabilitiesInWishList(context);
            }
        }
    }

    private void checkNewAvailabilitiesInWishList(Context context){
        User user = Utils.getCurrentUser(context);
        if (user!=null) {
            OrderLineDB orderLineDB = new OrderLineDB(context, user);
            ArrayList<OrderLine> orderLines = orderLineDB.getWishList();
            boolean showNotification = false;
            for (OrderLine orderLine : orderLines) {
                if (orderLine.getProduct().getDefaultProductPriceAvailability().getAvailability()>orderLine.getQuantityOrdered()) {
                    showNotification = true;
                }

                if(orderLine.getQuantityOrdered() != orderLine.getProduct().getDefaultProductPriceAvailability().getAvailability()) {
                    orderLine.setQuantityOrdered(orderLine.getProduct().getDefaultProductPriceAvailability().getAvailability());
                    orderLineDB.updateOrderLine(orderLine);
                }
            }

            BadgeUtils.setBadge(context, 12);

            if(showNotification){
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(context.getString(R.string.new_availabilities))
                                .setContentText(context.getString(R.string.new_availabilities_in_wishList));
                // Creates an explicit intent for an Activity in your app
                final Intent resultIntent = new Intent(context, WishListActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                // Adds the back stack for the Intent (but not the Intent itself)
                //stackBuilder.addParentStack(ResultActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT );
                mBuilder.setContentIntent(resultPendingIntent);
                mBuilder.setAutoCancel(true);
                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(1640, mBuilder.build());
            }
        }
    }
}

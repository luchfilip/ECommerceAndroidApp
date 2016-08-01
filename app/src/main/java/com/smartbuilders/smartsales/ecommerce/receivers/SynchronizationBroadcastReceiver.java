package com.smartbuilders.smartsales.ecommerce.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.syncadapter.SyncAdapter;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.WishListActivity;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.BadgeUtils;
import com.smartbuilders.smartsales.ecommerce.utils.NotificationUtils;
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
            }

            if(showNotification){
                BadgeUtils.setBadge(context, 1);
                // Creates an explicit intent for an Activity in your app
                final Intent resultIntent = new Intent(context, WishListActivity.class);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NotificationUtils.createNotification(context, context.getString(R.string.new_availabilities),
                        context.getString(R.string.new_availabilities_in_wishList), resultIntent);
            }
        }
    }
}

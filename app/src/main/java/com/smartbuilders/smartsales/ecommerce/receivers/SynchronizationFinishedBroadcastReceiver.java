package com.smartbuilders.smartsales.ecommerce.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.smartbuilders.smartsales.ecommerce.data.NotificationHistoryDB;
import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.synchronizer.ids.model.User;
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
public class SynchronizationFinishedBroadcastReceiver extends BroadcastReceiver {
    public SynchronizationFinishedBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkNewAvailabilitiesInWishList(context);
    }

    private void checkNewAvailabilitiesInWishList(Context context){
        if (!NotificationUtils.isNotificationShown(context) && PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean("notifications_new_availabilities_wish_list", true)) {
            User user = Utils.getCurrentUser(context);
            if (user != null) {
                NotificationHistoryDB notificationHistoryDB = new NotificationHistoryDB(context, user);
                OrderLineDB orderLineDB = new OrderLineDB(context, user);
                ArrayList<OrderLine> orderLines = orderLineDB.getWishList();
                boolean showNotification = false;
                for (OrderLine orderLine : orderLines) {
                    int productAvailabilityVariation = orderLine.getProduct().getProductPriceAvailability().getAvailability()
                            - orderLine.getQuantityOrdered();
                    if(productAvailabilityVariation!=0) {
                        if(productAvailabilityVariation > 0){
                            notificationHistoryDB.insertNotificationHistory("Variación de disponibilidad, artículo en Favoritos",
                                    orderLine.getProduct().getName() +
                                    "<br/><font color=#159204>" + context.getString(R.string.availability_positive_variation, String.valueOf(productAvailabilityVariation))+"</font>",
                                    NotificationHistory.TYPE_WISH_LIST_PRODUCT_AVAILABILITY_VARIATION,
                                    orderLine.getProductId());
                        } else {
                            notificationHistoryDB.insertNotificationHistory("Variación de disponibilidad, artículo en Favoritos",
                                    orderLine.getProduct().getName() +
                                    "<br/><font color=#c82c14>" + context.getString(R.string.availability_variation, String.valueOf(productAvailabilityVariation))+"</font>",
                                    NotificationHistory.TYPE_WISH_LIST_PRODUCT_AVAILABILITY_VARIATION,
                                    orderLine.getProductId());
                        }
                        showNotification = true;
                    }
                }

                if (showNotification) {
                    orderLineDB.updateProductAvailabilitiesInWishList();
                    BadgeUtils.setBadge(context, notificationHistoryDB.getCountByStatus(NotificationHistory.STATUS_NOT_SEEN));
//                    // Creates an explicit intent for an Activity in your app
//                    final Intent resultIntent = new Intent(context, WishListActivity.class);
//                    resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    NotificationUtils.createNotification(context, context.getString(R.string.app_name),
//                            context.getString(R.string.new_availabilities_in_wishList), resultIntent);
                }
            }
        }
    }
}

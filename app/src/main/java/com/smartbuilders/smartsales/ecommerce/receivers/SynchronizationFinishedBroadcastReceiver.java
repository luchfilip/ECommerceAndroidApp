package com.smartbuilders.smartsales.ecommerce.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.smartbuilders.smartsales.ecommerce.data.NotificationHistoryDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderTrackingDB;
import com.smartbuilders.smartsales.ecommerce.model.NotificationHistory;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsOriginalImage;
import com.smartbuilders.smartsales.ecommerce.services.LoadProductsThumbImage;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.OrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.BadgeUtils;
import com.smartbuilders.smartsales.ecommerce.utils.NotificationNewNotifications;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, 31.07.2016
 */
public class SynchronizationFinishedBroadcastReceiver extends BroadcastReceiver {
    public SynchronizationFinishedBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        checkNewNotifications(context);
    }

    private void checkNewNotifications(Context context){
        if (context!=null) {
            User user = Utils.getCurrentUser(context);
            if (user!=null) {
                final NotificationHistoryDB notificationHistoryDB = new NotificationHistoryDB(context, user);
                final OrderLineDB orderLineDB = new OrderLineDB(context, user);
                final boolean notifyIncrementAvailabilityInWishList = PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean("notifications_increment_availabilities_wish_list", true);
                final boolean notifyDecrementAvailabilityInWishList = PreferenceManager.getDefaultSharedPreferences(context)
                        .getBoolean("notifications_decrement_availabilities_wish_list", true);
                boolean showNotification = false;
                if (notifyIncrementAvailabilityInWishList || notifyDecrementAvailabilityInWishList) {
                    for (OrderLine orderLine : orderLineDB.getWishList()) {
                        int productAvailabilityVariation = orderLine.getProduct().getProductPriceAvailability().getAvailability()
                                - orderLine.getQuantityOrdered();
                        if (productAvailabilityVariation != 0) {
                            if (productAvailabilityVariation > 0) {
                                if (notifyIncrementAvailabilityInWishList) {
                                    notificationHistoryDB.insertNotificationHistory("Aumento de disponibilidad, artículo en Favoritos",
                                            "(<b>" + orderLine.getProduct().getInternalCodeMayoreoFormat() + "</b>) " + orderLine.getProduct().getName() +
                                                    "<br/><font color=#159204>" + context.getString(R.string.availability_positive_variation, String.valueOf(productAvailabilityVariation)) + "</font>",
                                            NotificationHistory.TYPE_WISH_LIST_PRODUCT_AVAILABILITY_VARIATION,
                                            orderLine.getProductId());
                                    showNotification = true;
                                }
                            } else {
                                if (notifyDecrementAvailabilityInWishList) {
                                    notificationHistoryDB.insertNotificationHistory("Disminución de disponibilidad, artículo en Favoritos",
                                            "(<b>" + orderLine.getProduct().getInternalCodeMayoreoFormat() + "</b>) " + orderLine.getProduct().getName() +
                                                    "<br/><font color=#c82c14>" + context.getString(R.string.availability_variation, String.valueOf(productAvailabilityVariation)) + "</font>",
                                            NotificationHistory.TYPE_WISH_LIST_PRODUCT_AVAILABILITY_VARIATION,
                                            orderLine.getProductId());
                                    showNotification = true;
                                }
                            }
                        }
                    }
                }
                orderLineDB.updateProductAvailabilitiesInWishList();

                final boolean notifyNewOrderTracking = Parameter.isActiveOrderTracking(context, user)
                        && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications_new_order_tracking", true);
                //boolean showNotificationNewOrderTracking = false;
                if (notifyNewOrderTracking) {
                    for (OrderTracking orderTracking : (new OrderTrackingDB(context, user)).getOrderTrackingWithoutNotification()) {
                        notificationHistoryDB.insertNotificationHistory("Nuevo estatus en rastreo de pedidos",
                                "<b>Pedido No.: "+orderTracking.getOrderNumber()+"</b>" +
                                "</br>Estatus: "+orderTracking.getOrderTrackingState().getTitle(),
                                NotificationHistory.TYPE_NEW_ORDER_TRACKING,
                                orderTracking.getId());
                        showNotification = true;
                    }
                }

                if (showNotification
                        && !NotificationNewNotifications.isNotificationShown(context)) {
                    NotificationNewNotifications.createNotification(context);
                }

                BadgeUtils.setBadge(context, notificationHistoryDB.getCountByStatus(NotificationHistory.STATUS_NOT_SEEN));

                //Se limpia de la carpeta de imagenes miniatura las imagenes que no corresponden
                LoadProductsThumbImage.cleanFolder(context, user);
                //Se limpia de la carpeta de imagenes de alta resolución las imagenes que no corresponden
                LoadProductsOriginalImage.cleanFolder(context, user);
            }
        }
    }
}

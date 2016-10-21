package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.OrderTracking;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AlbertoSarco on 18/10/2016.
 */
public class OrderTrackingDB {

    private Context mContext;
    private User mUser;

    public OrderTrackingDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<OrderTracking> getOrderTracking (int businessPartnerId, int orderId) {
        ArrayList<OrderTracking> orderTrackings = new ArrayList<>();

        OrderTracking orderTracking = new OrderTracking();
        orderTracking.setId(1);
        orderTracking.setTitle("Recibido por Vendedor");
        orderTracking.setSubTitle("El pedido fue recibido en el sistema de ventas del vendedor");
        orderTracking.setDate(new Date());
        orderTracking.setImageResId(R.drawable.ic_phone_android_black_48dp);
        orderTrackings.add(orderTracking);

        orderTracking = new OrderTracking();
        orderTracking.setId(2);
        orderTracking.setTitle("Pedido Generado");
        orderTracking.setSubTitle("No. Pedido: , Monto: ");
        orderTracking.setDate(new Date());
        orderTracking.setImageResId(R.drawable.ic_receipt_black_48dp);
        orderTrackings.add(orderTracking);

        orderTracking = new OrderTracking();
        orderTracking.setId(3);
        orderTracking.setTitle("Factura Generada");
        orderTracking.setSubTitle("No. Factura: , Monto: ");
        orderTracking.setDate(new Date());
        orderTracking.setImageResId(R.drawable.ic_description_black_48dp);
        orderTrackings.add(orderTracking);

        orderTracking = new OrderTracking();
        orderTracking.setId(4);
        orderTracking.setTitle("Mercancia Despachada");
        orderTracking.setSubTitle("Fecha de entrega estimada: ");
        orderTracking.setDate(new Date());
        orderTracking.setImageResId(R.drawable.ic_local_shipping_black_48dp);
        orderTrackings.add(orderTracking);

        orderTracking = new OrderTracking();
        orderTracking.setId(5);
        orderTracking.setLastState(true);
        orderTracking.setTitle("Mercancia Entregada");
        orderTracking.setSubTitle("Recibida por: ");
        orderTracking.setDate(new Date());
        orderTracking.setImageResId(R.drawable.ic_store_black_48dp);
        orderTrackings.add(orderTracking);

        return orderTrackings;
    }

    public OrderTracking getMaxOrderTracking(int businessPartnerId, int orderId) {
        OrderTracking orderTracking = new OrderTracking();
        switch (orderId) {
            case  1:
                orderTracking.setId(5);
                orderTracking.setLastState(true);
                orderTracking.setTitle("Mercancia Entregada");
                orderTracking.setSubTitle("Recibida por: ");
                orderTracking.setDate(new Date());
                orderTracking.setImageResId(R.drawable.ic_store_black_48dp);
                break;
            case  2:
                orderTracking.setId(4);
                orderTracking.setLastState(false);
                orderTracking.setTitle("Mercancia Despachada");
                orderTracking.setSubTitle("Fecha de entrega estimada: ");
                orderTracking.setDate(new Date());
                orderTracking.setImageResId(R.drawable.ic_local_shipping_black_48dp);
                break;
            case  3:
                orderTracking.setId(3);
                orderTracking.setLastState(false);
                orderTracking.setTitle("Factura Generada");
                orderTracking.setSubTitle("No. Factura: , Monto: ");
                orderTracking.setDate(new Date());
                orderTracking.setImageResId(R.drawable.ic_description_black_48dp);
                break;
            default:
                orderTracking.setId(1);
                orderTracking.setLastState(false);
                orderTracking.setTitle("Recibido por Vendedor");
                orderTracking.setSubTitle("El pedido fue recibido en el sistema de ventas del vendedor");
                orderTracking.setDate(new Date());
                orderTracking.setImageResId(R.drawable.ic_phone_android_black_48dp);
                break;
        }
        return orderTracking;
    }

    public float getProgressPercentage(int businessPartnerId, int orderId) {
        return 0;
    }

    public String getProgressText(int businessPartnerId, int orderId) {
        return "Completado!";
    }
}

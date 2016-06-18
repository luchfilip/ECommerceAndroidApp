package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderDB {

    private Context context;
    private User user;

    public OrderDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public String createOrderFromOrderLines(Integer salesOrderId, Integer businessPartnerId,
                                            ArrayList<OrderLine> orderLines){
        return createOrder(salesOrderId, businessPartnerId, orderLines, true);
    }

    public String createOrderFromShoppingCart(){
        return createOrder(null, null, (new OrderLineDB(context, user)).getShoppingCart(), false);
    }

    public int getLastFinalizedOrderId(){
        Cursor c = null;
        try {
            String sql = "SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", OrderLineDB.FINALIZED_ORDER_DOCTYPE}, null);
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
        return 0;
    }

    public Order getActiveOrderById(int orderId){
        Cursor c = null;
        Order order = null;
        try {
            String sql = "SELECT ECOMMERCE_ORDER_ID, CREATE_TIME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL "+
                    " FROM ECOMMERCE_ORDER " +
                    " WHERE ECOMMERCE_ORDER_ID = ? AND ISACTIVE = ?";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{String.valueOf(orderId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                order = new Order();
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(1)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
                order.setLinesNumber(c.getInt(2));
                order.setSubTotalAmount(c.getDouble(3));
                order.setTaxAmount(c.getDouble(4));
                order.setTotalAmount(c.getDouble(5));
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
        return order;
    }

    public ArrayList<Order> getActiveOrders(){
        return getActiveOrders(false);
    }

    public ArrayList<Order> getActiveOrdersFromSalesOrders(){
        return getActiveOrders(true);
    }

    private String createOrder(Integer salesOrderId, Integer businessPartnerId,
                               ArrayList<OrderLine> orderLines, boolean insertOrderLinesInDB){
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        if((orderLines!=null && insertOrderLinesInDB) || orderLineDB.getActiveShoppingCartLinesNumber()>0){
            Cursor c = null;
            int orderId = 0;
            try {
                double subTotal=0, tax=0, total=0;

                int rowsAffected = context.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                null,
                                "INSERT INTO ECOMMERCE_ORDER (ECOMMERCE_SALES_ORDER_ID, BUSINESS_PARTNER_ID, " +
                                        " DOC_STATUS, DOC_TYPE, APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, ISACTIVE) " +
                                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(salesOrderId), String.valueOf(businessPartnerId), "CO", OrderLineDB.FINALIZED_ORDER_DOCTYPE,
                                        Utils.getAppVersionName(context), user.getUserName(), String.valueOf(orderLines.size()),
                                        String.valueOf(subTotal), String.valueOf(tax), String.valueOf(total), "Y"});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }

                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                        null,
                        "SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?",
                        new String[]{"Y", OrderLineDB.FINALIZED_ORDER_DOCTYPE}, null);
                if(c!=null && c.moveToNext()){
                    orderId = c.getInt(0);
                }
            } catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            } finally {
                if(c != null) {
                    try {
                        c.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (insertOrderLinesInDB) {
                for (OrderLine orderLine : orderLines) {
                    orderLineDB.addOrderLineToFinalizedOrder(orderLine, orderId);
                }
            } else {
                if(orderLineDB.moveShoppingCartToFinalizedOrderByOrderId(orderId)<=0){
                    try {
                        int rowsAffected = context.getContentResolver()
                                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                        null,
                                        "DELETE FROM ECOMMERCE_ORDER WHERE ECOMMERCE_ORDER_ID = ?",
                                        new String[]{String.valueOf(orderId)});
                        if(rowsAffected <= 0){
                            return "Error 003 - No se insertó el pedido en la base de datos ni se eliminó la cabecera.";
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    return "Error 002 - No se insertó el pedido en la base de datos.";
                }
            }
        }else{
            return "No existen productos en el Carrito de compras.";
        }
        return null;
    }

    /**
     *
     * @return
     */
    private ArrayList<Order> getActiveOrders(boolean fromSalesOrder){
        ArrayList<Order> activeOrders = new ArrayList<>();
        Cursor c = null;
        try {
            if (fromSalesOrder) {
                String sql = "SELECT O.ECOMMERCE_ORDER_ID, O.DOC_STATUS, O.CREATE_TIME, O.UPDATE_TIME, " +
                            " O.APP_VERSION, O.APP_USER_NAME, O.LINES_NUMBER, O.SUB_TOTAL, O.TAX, O.TOTAL, " +
                            " O.ECOMMERCE_SALES_ORDER_ID, O.BUSINESS_PARTNER_ID " +
                        " FROM ECOMMERCE_ORDER O " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = O.BUSINESS_PARTNER_ID AND BP.ISACTIVE = ? " +
                        " WHERE ISACTIVE = ? AND DOC_TYPE = ? " +
                            " AND ECOMMERCE_SALES_ORDER_ID IS NOT NULL AND BUSINESS_PARTNER_ID IS NOT NULL " +
                        " ORDER BY ECOMMERCE_ORDER_ID desc";
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null, sql, new String[]{"Y", "Y", OrderLineDB.FINALIZED_ORDER_DOCTYPE}, null);
            } else {
                String sql = "SELECT ECOMMERCE_ORDER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, " +
                            " APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, " +
                            " ECOMMERCE_SALES_ORDER_ID, BUSINESS_PARTNER_ID " +
                        " FROM ECOMMERCE_ORDER " +
                        " WHERE ISACTIVE = ? AND DOC_TYPE = ? " +
                            //" AND (ECOMMERCE_SALES_ORDER_ID IS NULL OR ECOMMERCE_SALES_ORDER_ID=0)" +
                            //" AND (BUSINESS_PARTNER_ID IS NULL OR BUSINESS_PARTNER_ID=0) " +
                        " ORDER BY ECOMMERCE_ORDER_ID desc";
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null, sql, new String[]{"Y", OrderLineDB.FINALIZED_ORDER_DOCTYPE}, null);

            }
            while(c.moveToNext()){
                Order order = new Order();
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
                order.setLinesNumber(c.getInt(6));
                order.setSubTotalAmount(c.getDouble(7));
                order.setTaxAmount(c.getDouble(8));
                order.setTotalAmount(c.getDouble(9));
                order.setSalesOrderId(c.getInt(10));
                order.setBusinessPartnerId(c.getInt(11));
                activeOrders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        if(fromSalesOrder) {
            BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(context, user);
            for(Order order : activeOrders){
                order.setBusinessPartner(businessPartnerDB.getActiveBusinessPartnerById(order.getBusinessPartnerId()));
            }
        }
        return activeOrders;
    }
}

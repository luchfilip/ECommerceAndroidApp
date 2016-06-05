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

    public String createOrderFromOrderLines(ArrayList<OrderLine> orderLines){
        return createOrder(OrderLineDB.FINALIZED_ORDER_DOCTYPE, orderLines, true);
    }

    public String createOrderFromShoppingCart(){
        return createOrder(OrderLineDB.FINALIZED_ORDER_DOCTYPE,
                (new OrderLineDB(context, user)).getShoppingCart(), false);
    }

    public String createOrderFromShoppingSale(){
        return createOrder(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                (new OrderLineDB(context, user)).getShoppingSale(), false);
    }

    public String createOrder(String docType, ArrayList<OrderLine> orderLines, boolean insertOrderLinesInDB){
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        if(orderLines != null
                && (docType.equals(OrderLineDB.FINALIZED_ORDER_DOCTYPE) && orderLineDB.getActiveShoppingCartLinesNumber()>0)
                || (docType.equals(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE) && orderLineDB.getActiveShoppingSalesLinesNumber()>0)
                || insertOrderLinesInDB){
            Cursor c = null;
            int orderId = 0;
            try {
                double subTotal=0, tax=0, total=0;
                for(OrderLine orderLine : orderLines){
                    subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
                    tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
                    total += subTotal + tax;
                }

                int rowsAffected = context.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                null,
                                "INSERT INTO ECOMMERCE_ORDER (DOC_STATUS, DOC_TYPE, APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, ISACTIVE) " +
                                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{"CO", docType, Utils.getAppVersionName(context), user.getUserName(),
                                        String.valueOf(orderLines.size()), String.valueOf(subTotal), String.valueOf(tax),
                                        String.valueOf(total), "Y"});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }

                String sql = "SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?";
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null, sql, new String[]{"Y", docType}, null);
                if(c.moveToNext()){
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
            if(docType.equals(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE)){
                if (insertOrderLinesInDB) {
                    for (OrderLine orderLine : orderLines) {
                        orderLineDB.addOrderLineToFinalizedOrder(orderLine, orderId);
                    }
                } else {
                    if(orderLineDB.moveShoppingSaleToFinalizedSaleOrderByOrderId(orderId)<=0){
                        try {
                            int rowsAffected = context.getContentResolver()
                                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                            null,
                                            "DELETE FROM ECOMMERCE_ORDER WHERE ECOMMERCE_ORDER_ID = ?",
                                            new String[]{String.valueOf(orderId)});
                            if(rowsAffected <= 0){
                                return "Error 003 - No se insertó la cotización en la base de datos ni se eliminó la cabecera.";
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            return e.getMessage();
                        }
                        return "Error 002 - No se insertó la cotización en la base de datos.";
                    }
                }
            } else if (docType.equals(OrderLineDB.FINALIZED_ORDER_DOCTYPE)) {
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
            }
        }else{
            return "No existen productos en el Carrito de compras.";
        }
        return null;
    }

    public Order getLastFinalizedOrder(){
        return getLastOrderByDocType(OrderLineDB.FINALIZED_ORDER_DOCTYPE);
    }

    public Order getLastFinalizedSalesOrder() {
        return getLastOrderByDocType(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE);
    }

    private Order getLastOrderByDocType(String docType){
        Cursor c = null;
        Order order = null;
        try {
            String sql = "SELECT ECOMMERCE_ORDER_ID, CREATE_TIME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL"+
                    " FROM ECOMMERCE_ORDER " +
                    " WHERE ECOMMERCE_ORDER_ID = (SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?)";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
            if(c.moveToNext()){
                order = new Order();
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(1)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
                order.setOrderLinesNumber(c.getInt(2));
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

    public ArrayList<Order> getActiveSalesOrders(){
        return getActiveOrders(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE);
    }

    public ArrayList<Order> getActiveOrders(){
        return getActiveOrders(OrderLineDB.FINALIZED_ORDER_DOCTYPE);
    }

    /**
     *
     * @param docType
     * @return
     */
    private ArrayList<Order> getActiveOrders(String docType){
        ArrayList<Order> activeOrders = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT ECOMMERCE_ORDER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, " +
                    " APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL " +
                    " FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ? order by ECOMMERCE_ORDER_ID desc";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
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
                order.setOrderLinesNumber(c.getInt(6));
                order.setSubTotalAmount(c.getDouble(7));
                order.setTaxAmount(c.getDouble(8));
                order.setTotalAmount(c.getDouble(9));
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
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        for(Order order : activeOrders){
            order.setOrderLinesNumber(orderLineDB.getOrderLineNumbersByOrderId(order.getId()));
        }
        return activeOrders;
    }

}

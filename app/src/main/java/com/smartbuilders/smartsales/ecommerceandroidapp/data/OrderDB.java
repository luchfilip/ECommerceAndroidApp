package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
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
    private DatabaseHelper dbh;

    public OrderDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public String createOrderFromShoppingCart(){
        return createOrder(OrderLineDB.FINALIZED_ORDER_DOCTYPE);
    }

    public String createOrderFromShoppingSale(){
        return createOrder(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE);
    }

    public String createOrder(String docType){
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        if(orderLineDB.getActiveShoppingCartLinesNumber()>0){
            SQLiteDatabase db = null;
            Cursor c = null;
            int orderId = 0;
            try {
                db = dbh.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put("DOC_STATUS", "CO");
                cv.put("DOC_TYPE", docType);
                cv.put("APP_VERSION", Utils.getAppVersionName(context));
                cv.put("APP_USER_NAME", user.getUserName());
                cv.put("ISACTIVE", "Y");
                if(db.insert("ECOMMERCE_ORDER", null, cv) <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }
                c = db.rawQuery("SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?",
                        new String[]{"Y", docType});
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
                if(db != null) {
                    try {
                        db.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if(docType.equals(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE)){
                if(orderLineDB.moveShoppingSaleToFinalizedSaleOrderByOrderId(orderId)<=0){
                    try {
                        db = dbh.getWritableDatabase();
                        if(db.delete("ECOMMERCE_ORDER", "ECOMMERCE_ORDER_ID = ?", new String[]{String.valueOf(orderId)}) <= 0){
                            return "Error 003 - No se insertó la cotizacion en la base de datos.";
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return e.getMessage();
                    } finally {
                        if(db != null) {
                            try {
                                db.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return "Error 002 - No se insertó la cotizacion en la base de datos.";
                }
            } else if (docType.equals(OrderLineDB.FINALIZED_ORDER_DOCTYPE)) {
                if(orderLineDB.moveShoppingCartToFinalizedOrderByOrderId(orderId)<=0){
                    try {
                        db = dbh.getWritableDatabase();
                        if(db.delete("ECOMMERCE_ORDER", "ECOMMERCE_ORDER_ID = ?", new String[]{String.valueOf(orderId)}) <= 0){
                            return "Error 003 - No se insertó el pedido en la base de datos.";
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return e.getMessage();
                    } finally {
                        if(db != null) {
                            try {
                                db.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return "Error 002 - No se insertó el pedido en la base de datos.";
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
        SQLiteDatabase db = null;
        Cursor c = null;
        Order order = new Order();
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT ECOMMERCE_ORDER_ID, CB_PARTNER_ID, DOC_STATUS, DOC_TYPE, " +
                        " ISACTIVE, CREATE_TIME, UPDATE_TIME, APP_VERSION, APP_USER_NAME " +
                    " FROM ECOMMERCE_ORDER " +
                    " WHERE ECOMMERCE_ORDER_ID = (SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?)",
                    new String[]{"Y", docType});
            if(c.moveToNext()){
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(5)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(5)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
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
            if(db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return order;
    }

    /**
     *
     * @return
     */
    public ArrayList<Order> getActiveOrders(){
        ArrayList<Order> activeOrders = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.query("ECOMMERCE_ORDER", new String[]{"ECOMMERCE_ORDER_ID", "CB_PARTNER_ID",
                    "DOC_STATUS", "DOC_TYPE", "CREATE_TIME", "UPDATE_TIME", "APP_VERSION",
                    "APP_USER_NAME"}, "ISACTIVE = ?", new String[]{"Y"}, null, null, "CREATE_TIME DESC");
            while(c.moveToNext()){
                Order order = new Order();
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(4)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(4)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        for(Order order : activeOrders){
            order.setOrderLineNumbers(orderLineDB.getOrderLineNumbersByOrderId(order.getId()));
        }
        return activeOrders;
    }

}

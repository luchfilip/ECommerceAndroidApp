package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

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

    public String createOrderFromShoppingcart(){
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        if(orderLineDB.getActiveShoppingCartLinesNumber()>0){
            SQLiteDatabase db = null;
            Cursor c = null;
            int orderId = 0;
            try {
                db = dbh.getWritableDatabase();
                //new StringBuffer("CREATE TABLE IF NOT EXISTS ECOMMERCE_ORDER ")
                //        .append("(ECOMMERCE_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                //        .append("CB_PARTNER_ID INTEGER DEFAULT NULL, ")
                //        .append("DOC_STATUS CHAR(2) DEFAULT NULL, ")
                //        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
                //        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
                //        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
                //        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
                //        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
                //        .append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();
                ContentValues cv = new ContentValues();
                cv.put("DOC_STATUS", "CO");
                cv.put("DOC_TYPE", OrderLineDB.FINALIZED_ORDER_DOCTYPE);
                cv.put("APP_VERSION", Utils.getAppVersionName(context));
                cv.put("APP_USER_NAME", user.getUserName());
                cv.put("ISACTIVE", "Y");
                if(db.insert("ECOMMERCE_ORDER", null, cv) <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }
                c = db.rawQuery("SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ?",
                        new String[]{"Y"});
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
        }else{
            return "No existen productos en el Carrito de compras.";
        }
        return null;
    }

    public int getLastFinalizedOrderId(){
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ?",
                    new String[]{"Y"});
            if(c.moveToNext()){
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
            if(db != null) {
                try {
                    db.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
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
                order.setCreated(new Date(c.getLong(5)));
                order.setUpdated(new Date(c.getLong(6)));
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

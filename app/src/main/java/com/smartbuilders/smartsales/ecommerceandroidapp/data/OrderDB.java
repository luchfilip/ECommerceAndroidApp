package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
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
    private DatabaseHelper dbh;

    public OrderDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public String createOrderFromShoppingCart(){
        return createOrder(OrderLineDB.FINALIZED_ORDER_DOCTYPE,
                (new OrderLineDB(context, user)).getShoppingCart());
    }

    public String createOrderFromShoppingSale(){
        return createOrder(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                (new OrderLineDB(context, user)).getShoppingSale());
    }

    public String createOrder(String docType, ArrayList<OrderLine> orderLines){
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        if(orderLines != null
                && (docType.equals(OrderLineDB.FINALIZED_ORDER_DOCTYPE) && orderLineDB.getActiveShoppingCartLinesNumber()>0)
                || (docType.equals(OrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE) && orderLineDB.getActiveShoppingSalesLinesNumber()>0)){
            SQLiteDatabase db = null;
            Cursor c = null;
            int orderId = 0;
            try {
                db = dbh.getWritableDatabase();
                double subTotal=0, tax=0, total=0;
                for(OrderLine orderLine : orderLines){
                    subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
                    tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
                    total += subTotal + tax;
                }
                ContentValues cv = new ContentValues();
                cv.put("DOC_STATUS", "CO");
                cv.put("DOC_TYPE", docType);
                cv.put("APP_VERSION", Utils.getAppVersionName(context));
                cv.put("APP_USER_NAME", user.getUserName());
                cv.put("ORDERLINES_NUMBER", orderLines.size());
                cv.put("SUB_TOTAL", subTotal);
                cv.put("TAX", tax);
                cv.put("TOTAL", total);
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
        //SQLiteDatabase db = null;
        Cursor c = null;
        Order order = null;
        try {
            //db = dbh.getReadableDatabase();
            //c = db.rawQuery("SELECT ECOMMERCE_ORDER_ID, CB_PARTNER_ID, DOC_STATUS, DOC_TYPE, " +
            //            " ISACTIVE, CREATE_TIME, UPDATE_TIME, APP_VERSION, APP_USER_NAME, " +
            //            " ORDERLINES_NUMBER, SUB_TOTAL, TAX, TOTAL"+
            //        " FROM ECOMMERCE_ORDER " +
            //        " WHERE ECOMMERCE_ORDER_ID = (SELECT MAX(ECOMMERCE_ORDER_ID) FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?)",
            //        new String[]{"Y", docType});
            String sql = "SELECT ECOMMERCE_ORDER_ID, CREATE_TIME, ORDERLINES_NUMBER, SUB_TOTAL, TAX, TOTAL"+
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
            //if(db != null) {
            //    try {
            //        db.close();
            //    } catch (Exception e) {
            //        e.printStackTrace();
            //    }
            //}
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
        //SQLiteDatabase db = null;
        Cursor c = null;
        try {
            //db = dbh.getReadableDatabase();
            //c = db.query("ECOMMERCE_ORDER", new String[]{"ECOMMERCE_ORDER_ID", "CB_PARTNER_ID",
            //        "DOC_STATUS", "CREATE_TIME", "UPDATE_TIME", "APP_VERSION", "APP_USER_NAME",
            //        "ORDERLINES_NUMBER", "SUB_TOTAL", "TAX", "TOTAL"},
            //        "ISACTIVE = ? AND DOC_TYPE = ?", new String[]{"Y", docType}, null, null, "CREATE_TIME DESC");
            String sql = "SELECT ECOMMERCE_ORDER_ID, CB_PARTNER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, APP_VERSION, APP_USER_NAME, ORDERLINES_NUMBER, SUB_TOTAL, TAX, TOTAL " +
                    " FROM ECOMMERCE_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
            while(c.moveToNext()){
                Order order = new Order();
                order.setId(c.getInt(0));
                try{
                    order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(3)).getTime()));
                }catch(ParseException ex){
                    try {
                        order.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(3)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
                order.setOrderLinesNumber(c.getInt(7));
                order.setSubTotalAmount(c.getDouble(8));
                order.setTaxAmount(c.getDouble(9));
                order.setTotalAmount(c.getDouble(10));
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
            //if(db!=null){
            //    try {
            //        db.close();
            //    } catch (Exception e){
            //        e.printStackTrace();
            //    }
            //}
        }
        OrderLineDB orderLineDB = new OrderLineDB(context, user);
        for(Order order : activeOrders){
            order.setOrderLinesNumber(orderLineDB.getOrderLineNumbersByOrderId(order.getId()));
        }
        return activeOrders;
    }

    //public String updateOrder(Order order){
    //    //SQLiteDatabase db = null;
    //    try {
    //        //db = dbh.getWritableDatabase();
    //        //ContentValues cv = new ContentValues();
    //        //cv.put("ORDERLINES_NUMBER", order.getOrderLinesNumber());
    //        //cv.put("SUB_TOTAL", order.getSubTotalAmount());
    //        //cv.put("TAX", order.getTaxAmount());
    //        //cv.put("TOTAL", order.getTotalAmount());
    //        //cv.put("UPDATE_TIME", "datetime('now')");
    //        //if(db.update ("ECOMMERCE_ORDER", cv, "ECOMMERCE_ORDER_ID=?",
    //        //        new String[]{ Integer.valueOf(order.getId()).toString()})<1) {
    //        //    return "No se actualizó el registro en la base de datos.";
    //        //}
    //        if(context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
    //                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
    //                .build(), null,
    //                "UPDATE ECOMMERCE_ORDER SET ORDERLINES_NUMBER = ?, SUB_TOTAL = ?, TAX = ?, TOTAL = ?, UPDATE_TIME = ? WHERE ECOMMERCE_ORDER_ID = ?",
    //                new String[]{String.valueOf(order.getOrderLinesNumber()), String.valueOf(order.getSubTotalAmount()),
    //                        String.valueOf(order.getTaxAmount()), String.valueOf(order.getTotalAmount()), "datetime('now')" ,
    //                        String.valueOf(order.getId())}) < 1){
    //            return "No se actualizó el registro en la base de datos.";
    //        }
    //    } catch (Exception e){
    //        e.printStackTrace();
    //        return e.getMessage();
    //    } //finally {
    //    //    if(db != null) {
    //    //        try {
    //    //            db.close();
    //    //        } catch (Exception e) {
    //    //            e.printStackTrace();
    //    //        }
    //    //    }
    //    //}
    //    return null;
    //}

}

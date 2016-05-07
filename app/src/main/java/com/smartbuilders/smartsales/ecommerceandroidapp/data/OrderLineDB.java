package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderLineDB {

    private static final String TAG = OrderLineDB.class.getSimpleName();

    public static final String WISHLIST_DOCTYPE = "WL";
    public static final String SHOPPING_CART_DOCTYPE = "SC";
    public static final String FINALIZED_ORDER_DOCTYPE = "FO";

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public OrderLineDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public String addProductToShoppingCart(Product product, int qtyRequested){
        return addOrderLine(product, qtyRequested, SHOPPING_CART_DOCTYPE);
    }

    public String addProductToWhisList(Product product){
        return addOrderLine(product, 0, WISHLIST_DOCTYPE);
    }

    public String moveOrderLineToShoppingCart(OrderLine orderLine){
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("DOC_TYPE", SHOPPING_CART_DOCTYPE);
            if(db.update ("ECOMMERCE_ORDERLINE", cv, "ECOMMERCE_ORDERLINE_ID=? AND DOC_TYPE=?",
                    new String[]{ Integer.valueOf(orderLine.getId()).toString(), WISHLIST_DOCTYPE})<1) {
                return "No se actualizó el registro en la base de datos.";
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
        return null;
    }

    public String deleteOrderLine(OrderLine orderLine){
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ISACTIVE", "N");
            if(db.update ("ECOMMERCE_ORDERLINE", cv, "ECOMMERCE_ORDERLINE_ID=?",
                    new String[]{ Integer.valueOf(orderLine.getId()).toString()})<1) {
                return "No se actualizó el registro en la base de datos.";
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
        return null;
    }

    public ArrayList<OrderLine> getShoppingCart(){
        return getOrderLines(SHOPPING_CART_DOCTYPE, null);
    }

    public ArrayList<OrderLine> getWishList(){
        return getOrderLines(WISHLIST_DOCTYPE, null);
    }

    public ArrayList<OrderLine> getActiveFinalizedOrderLinesByOrderId(int orderId){
        return getActiveOrderLinesByOrderId(FINALIZED_ORDER_DOCTYPE, orderId);
    }

    private ArrayList<OrderLine> getActiveOrderLinesByOrderId (String docType, int orderId) {
        return getOrderLines(docType, orderId);
    }

    private String addOrderLine(Product product, int qtyRequested, String docType) {
        SQLiteDatabase db = null;
        try {
            db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("PRODUCT_ID", product.getId());
            cv.put("QTY_REQUESTED", qtyRequested);
            cv.put("SALES_PRICE", "");
            cv.put("DOC_TYPE", docType);
            cv.put("ISACTIVE", "Y");
            cv.put("APP_VERSION", Utils.getAppVersionName(context));
            cv.put("APP_USER_NAME", user.getUserName());
            db.insert("ECOMMERCE_ORDERLINE", null, cv);
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
        return null;
    }

    private ArrayList<OrderLine> getOrderLines(String docType, Integer orderId) {
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();

            c = db.rawQuery("SELECT ECOMMERCE_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED " +
                    " FROM ECOMMERCE_ORDERLINE " +
                    " WHERE ISACTIVE = ? AND DOC_TYPE = ? " +
                        (orderId!=null ? " AND ECOMMERCE_ORDER_ID = "+orderId : "") +
                    " ORDER BY CREATE_TIME DESC", new String[]{"Y", docType});
            while(c.moveToNext()){
                OrderLine orderLine = new OrderLine();
                orderLine.setId(c.getInt(0));
                Product product = new Product();
                product.setId(c.getInt(1));
                orderLine.setQuantityOrdered(c.getInt(2));
                orderLine.setProduct(product);
                orderLines.add(orderLine);
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
        ProductDB productDB = new ProductDB(context, user);
        ArrayList<OrderLine> orderLinesToDelete = new ArrayList<>();
        for(OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProduct().getId()));
            if(orderLine.getProduct()==null){
                orderLinesToDelete.add(orderLine);
            }
        }
        if(!orderLinesToDelete.isEmpty()){
            orderLines.removeAll(orderLinesToDelete);
        }

        return orderLines;
    }

    public int getOrderLineNumbersByOrderId(int orderId){
        Log.d(TAG, "getOrderLineNumbersByOrderId("+orderId+")");
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT COUNT(ECOMMERCE_ORDERLINE_ID) FROM ECOMMERCE_ORDERLINE WHERE ECOMMERCE_ORDER_ID="+orderId+" AND ISACTIVE = ?",
                    new String[]{"Y"});
            while(c.moveToNext()){
                return c.getInt(0);
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
        return 0;
    }

    public int getActiveShoppingCartLinesNumber(){
        return getActiveOrderLinesNumber(SHOPPING_CART_DOCTYPE);
    }

    public int getActiveWishListLinesNumber(){
        return getActiveOrderLinesNumber(WISHLIST_DOCTYPE);
    }

    private int getActiveOrderLinesNumber(String docType) {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT COUNT(*) FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE=? AND ISACTIVE = ?",
                    new String[]{docType, "Y"});
            while(c.moveToNext()){
                return c.getInt(0);
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
        return 0;
    }

    public int moveShoppingCartToFinalizedOrderByOrderId(int orderId) {
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("ECOMMERCE_ORDER_ID", orderId);
            cv.put("UPDATE_TIME", "datetime('now')");
            cv.put("DOC_TYPE", FINALIZED_ORDER_DOCTYPE);
            return db.update("ECOMMERCE_ORDERLINE", cv, "ISACTIVE = ? AND DOC_TYPE = ?",
                    new String[]{"Y", SHOPPING_CART_DOCTYPE});
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
        return 0;
    }
}

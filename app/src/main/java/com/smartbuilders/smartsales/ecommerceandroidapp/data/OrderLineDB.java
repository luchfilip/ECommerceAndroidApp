package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderLineDB {

    public static final String WISHLIST_DOCTYPE = "WL";
    public static final String SHOPPING_CART_DOCTYPE = "SC";

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

    public ArrayList<OrderLine> getShoppingCart(){
        return getOrderLines(SHOPPING_CART_DOCTYPE);
    }

    public ArrayList<OrderLine> getWishList(){
        return getOrderLines(WISHLIST_DOCTYPE);
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
            cv.put("APP_VERSION", "");
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

    private ArrayList<OrderLine> getOrderLines(String docType) {
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            //.append("(ECOMMERCE_ORDERLINE_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
            //        .append("ECOMMERCE_ORDER_ID DEFAULT NULL, ")
            //        .append("PRODUCT_ID INTEGER NOT NULL, ")
            //        .append("QTY_REQUESTED INTEGER NOT NULL, ")
            //        .append("SALES_PRICE DOUBLE DEFAULT NULL, ")
            //        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
            //        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
            //        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
            //        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
            //        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
            c = db.rawQuery("SELECT ECOMMERCE_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED " +
                    " FROM ECOMMERCE_ORDERLINE " +
                    " WHERE ISACTIVE = 'Y' " +
                        " AND DOC_TYPE = '" + docType + "' " +
                    " ORDER BY CREATE_TIME DESC", null);
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
        for(OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProduct().getId()));
        }
        return orderLines;
    }
}

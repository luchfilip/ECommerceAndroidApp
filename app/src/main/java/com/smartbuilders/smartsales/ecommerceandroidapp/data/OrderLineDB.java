package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderLineDB {

    public static final String WISHLIST_DOCTYPE = "WL";
    public static final String SHOPPING_CART_DOCTYPE = "SC";
    public static final String FINALIZED_ORDER_DOCTYPE = "FO";

    private Context mContext;
    private User user;

    public OrderLineDB(Context context, User user){
        this.mContext = context;
        this.user = user;
    }

    public String addOrderLineToFinalizedOrder(OrderLine orderLine, int orderId){
        return addOrderLine(orderLine.getProductId(), orderLine.getQuantityOrdered(), 0, 0, FINALIZED_ORDER_DOCTYPE, orderId);
    }

    public String addProductToShoppingCart(int productId, int qtyRequested){
        return addOrderLine(productId, qtyRequested, 0, 0, SHOPPING_CART_DOCTYPE, null);
    }

    public String addProductToWishList(int productId){
        return addOrderLine(productId, 0, 0, 0, WISHLIST_DOCTYPE, null);
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

    public int moveShoppingCartToFinalizedOrderByOrderId(int orderId) {
        return moveOrderLinesToOrderByOrderId(orderId, FINALIZED_ORDER_DOCTYPE, SHOPPING_CART_DOCTYPE);
    }

    public int getActiveShoppingCartLinesNumber(){
        return getActiveOrderLinesNumber(SHOPPING_CART_DOCTYPE);
    }

    public String removeProductFromWishList(int productId){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                            null,
                            "DELETE FROM ECOMMERCE_ORDERLINE WHERE PRODUCT_ID = ? AND DOC_TYPE = ?",
                            new String[]{String.valueOf(productId), WISHLIST_DOCTYPE});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String moveOrderLineToShoppingCart(OrderLine orderLine, int qtyRequested){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_ORDERLINE SET DOC_TYPE = ?, QTY_REQUESTED = ?, " +
                                " UPDATE_TIME = ? WHERE ECOMMERCE_ORDERLINE_ID = ? AND DOC_TYPE=?",
                            new String[]{SHOPPING_CART_DOCTYPE, String.valueOf(qtyRequested), "datetime('now')",
                                        String.valueOf(orderLine.getId()), WISHLIST_DOCTYPE});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateOrderLine(OrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDERLINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                        " TAX_PERCENTAGE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                    " WHERE ECOMMERCE_ORDERLINE_ID = ?",
                    new String[]{String.valueOf(orderLine.getQuantityOrdered()),
                    String.valueOf(orderLine.getPrice()), String.valueOf(orderLine.getTaxPercentage()),
                    String.valueOf(orderLine.getTotalLineAmount()), "datetime('now')",
                    String.valueOf(orderLine.getId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deleteOrderLine(OrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "DELETE FROM ECOMMERCE_ORDERLINE WHERE ECOMMERCE_ORDERLINE_ID = ?",
                    new String[]{String.valueOf(orderLine.getId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String clearWishList(){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, "DELETE FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE = ?",
                    new String[]{WISHLIST_DOCTYPE});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public ArrayList<OrderLine> getOrderLinesBySalesOrderId(int salesOrderId){
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_SALES_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED, " +
                        " SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDERLINE " +
                    " WHERE DOC_TYPE = ? AND ECOMMERCE_SALES_ORDER_ID = ? AND IS_ACTIVE = ? " +
                    " ORDER BY ECOMMERCE_SALES_ORDERLINE_ID DESC",
                    new String[]{SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                            String.valueOf(salesOrderId), "Y"}, null);
            if(c!=null){
                ProductDB productDB = new ProductDB(mContext, user);
                while(c.moveToNext()){
                    OrderLine orderLine = new OrderLine();
                    orderLine.setId(c.getInt(0));
                    orderLine.setProductId(c.getInt(1));
                    orderLine.setQuantityOrdered(c.getInt(2));
                    orderLine.setPrice(c.getDouble(3));
                    orderLine.setTaxPercentage(c.getDouble(4));
                    orderLine.setTotalLineAmount(c.getDouble(5));
                    orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
                    if(orderLine.getProduct()!=null){
                        orderLines.add(orderLine);
                    }
                }
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
        return orderLines;
    }

    private String addOrderLine(int productId, int qtyRequested, double productPrice,
                                double productTaxPercentage, String docType, Integer orderId) {
        try {
            OrderLine ol = new OrderLine();
            ol.setProductId(productId);
            ol.setPrice(productPrice);
            ol.setQuantityOrdered(qtyRequested);
            ol.setTaxPercentage(productTaxPercentage);
            ol.setTotalLineAmount(OrderLineBR.getTotalLine(ol));
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "INSERT INTO ECOMMERCE_ORDERLINE (PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, " +
                        " TAX_PERCENTAGE, TOTAL_LINE, DOC_TYPE, ECOMMERCE_ORDER_ID, IS_ACTIVE, " +
                        " APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(ol.getProductId()),
                                String.valueOf(ol.getQuantityOrdered()), String.valueOf(ol.getPrice()),
                                String.valueOf(ol.getTaxPercentage()), String.valueOf(ol.getTotalLineAmount()),
                                docType, (orderId==null ? null : String.valueOf(orderId)), "Y",
                                Utils.getAppVersionName(mContext), user.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    private ArrayList<OrderLine> getOrderLines(String docType, Integer orderId) {
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDERLINE " +
                    " WHERE " + (orderId!=null ? " ECOMMERCE_ORDER_ID = "+orderId+ " AND " : "") +
                        " DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{docType, "Y"}, null);
            if(c!=null){
                ProductDB productDB = new ProductDB(mContext, user);
                while(c.moveToNext()){
                    OrderLine orderLine = new OrderLine();
                    orderLine.setId(c.getInt(0));
                    orderLine.setProductId(c.getInt(1));
                    orderLine.setQuantityOrdered(c.getInt(2));
                    orderLine.setPrice(c.getDouble(3));
                    orderLine.setTaxPercentage(c.getDouble(4));
                    orderLine.setTotalLineAmount(c.getDouble(5));
                    orderLine.setProduct(productDB.getProductById(orderLine.getProductId()));
                    if(orderLine.getProduct()!=null){
                        orderLines.add(orderLine);
                    }
                }
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
        return orderLines;
    }

    public int getOrderLineNumbersByOrderId(int orderId){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT COUNT(ECOMMERCE_ORDERLINE_ID) FROM ECOMMERCE_ORDERLINE " +
                    " WHERE ECOMMERCE_ORDER_ID=? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(orderId), "Y"}, null);
            if(c!=null && c.moveToNext()){
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
        }
        return 0;
    }

    private int getActiveOrderLinesNumber(String docType) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE=? AND IS_ACTIVE = ?",
                    new String[]{docType, "Y"}, null);
            if(c!=null && c.moveToNext()){
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
        }
        return 0;
    }

    private int moveOrderLinesToOrderByOrderId(int orderId, String newDocType, String currentDocType) {
        try {
            return mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDERLINE SET ECOMMERCE_ORDER_ID = ?, UPDATE_TIME = ?, " +
                    " DOC_TYPE = ? WHERE IS_ACTIVE = ? AND DOC_TYPE = ?",
                    new String[]{String.valueOf(orderId), "datetime('now')", newDocType, "Y", currentDocType});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isProductInWishList(int productId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                            .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_ORDERLINE WHERE PRODUCT_ID=? AND DOC_TYPE=? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(productId), WISHLIST_DOCTYPE, "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0)>0;
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
        return false;
    }
}

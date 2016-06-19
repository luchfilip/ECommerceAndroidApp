package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.OrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 4/30/2016.
 */
public class OrderLineDB {

    public static final String WISHLIST_DOCTYPE = "WL";
    public static final String SHOPPING_CART_DOCTYPE = "SC";
    public static final String FINALIZED_ORDER_DOCTYPE = "FO";

    private Context context;
    private User user;

    public OrderLineDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public String addOrderLineToFinalizedOrder(OrderLine orderLine, int orderId){
        return addOrderLine(orderLine.getProduct(), orderLine.getQuantityOrdered(), 0, 0, FINALIZED_ORDER_DOCTYPE, orderId);
    }

    public String addProductToShoppingCart(Product product, int qtyRequested){
        return addOrderLine(product, qtyRequested, 0, 0, SHOPPING_CART_DOCTYPE, null);
    }

    public String addProductToWishList(Product product){
        return addOrderLine(product, 0, 0, 0, WISHLIST_DOCTYPE, null);
    }

    public String removeProductFromWishList(Product product){
        try {
            String sql = "DELETE FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE = ? AND PRODUCT_ID = ?";
            int rowsAffected = context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                            null, sql,
                            new String[]{WISHLIST_DOCTYPE, String.valueOf(product.getId())});
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
            String sql = "UPDATE ECOMMERCE_ORDERLINE SET DOC_TYPE = ?, QTY_REQUESTED = ?, " +
                    " UPDATE_TIME = ? WHERE ECOMMERCE_ORDERLINE_ID = ? AND DOC_TYPE=?";
            int rowsAffected = context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                            null, sql,
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
            String sql = "UPDATE ECOMMERCE_ORDERLINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                    " TAX_PERCENTAGE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                    " WHERE ECOMMERCE_ORDERLINE_ID = ?";
            int rowsAffected = context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                    null, sql,
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
            String sql = "DELETE FROM ECOMMERCE_ORDERLINE WHERE ECOMMERCE_ORDERLINE_ID = ?";
            int rowsAffected = context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                null, sql, new String[]{String.valueOf(orderLine.getId())});
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
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, "DELETE FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE = ?",
                    new String[]{WISHLIST_DOCTYPE});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public ArrayList<OrderLine> getShoppingCart(){
        return getOrderLines(SHOPPING_CART_DOCTYPE, null);
    }

    public ArrayList<OrderLine> getOrderLinesBySalesOrderId(int salesOrderId){
        ArrayList<OrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT SOL.ECOMMERCE_SALES_ORDERLINE_ID, SOL.PRODUCT_ID, SOL.QTY_REQUESTED, " +
                        " SOL.SALES_PRICE, SOL.TAX_PERCENTAGE, SOL.TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDERLINE SOL " +
                        " INNER JOIN ARTICULOS A ON A.IDARTICULO = SOL.PRODUCT_ID AND A.ACTIVO = ?  " +
                    " WHERE SOL.ISACTIVE = ? AND SOL.DOC_TYPE = ? " +
                        " AND SOL.ECOMMERCE_SALES_ORDER_ID = ? " +
                    " ORDER BY SOL.CREATE_TIME DESC",
                    new String[]{"V", "Y", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE, String.valueOf(salesOrderId)}, null);
            while(c.moveToNext()){
                OrderLine orderLine = new OrderLine();
                orderLine.setId(c.getInt(0));
                Product product = new Product();
                product.setId(c.getInt(1));
                orderLine.setQuantityOrdered(c.getInt(2));
                orderLine.setPrice(c.getDouble(3));
                orderLine.setTaxPercentage(c.getDouble(4));
                orderLine.setTotalLineAmount(c.getDouble(5));
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
        }
        ProductDB productDB = new ProductDB(context, user);
        for(OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProduct().getId(), false));
        }
        return orderLines;
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

    private String addOrderLine(Product product, int qtyRequested, double productPrice,
                                double productTaxPercentage, String docType, Integer orderId) {
        try {
            OrderLine ol = new OrderLine();
            ol.setProduct(product);
            ol.setPrice(productPrice);
            ol.setQuantityOrdered(qtyRequested);
            ol.setTaxPercentage(productTaxPercentage);
            ol.setTotalLineAmount(OrderLineBR.getTotalLine(ol));
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "INSERT INTO ECOMMERCE_ORDERLINE (PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, " +
                        " TAX_PERCENTAGE, TOTAL_LINE, DOC_TYPE, ECOMMERCE_ORDER_ID, ISACTIVE, APP_VERSION, APP_USER_NAME) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(ol.getProduct().getId()),
                                String.valueOf(ol.getQuantityOrdered()), String.valueOf(ol.getPrice()),
                                String.valueOf(ol.getTaxPercentage()), String.valueOf(ol.getTotalLineAmount()),
                                docType, (orderId==null ? null : String.valueOf(orderId)), "Y",
                                Utils.getAppVersionName(context), user.getUserName()});
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
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDERLINE WHERE ISACTIVE = ? AND DOC_TYPE = ? " +
                        (orderId!=null ? " AND ECOMMERCE_ORDER_ID = "+orderId : "") +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{"Y", docType}, null);
            while(c.moveToNext()){
                OrderLine orderLine = new OrderLine();
                orderLine.setId(c.getInt(0));
                Product product = new Product();
                product.setId(c.getInt(1));
                orderLine.setQuantityOrdered(c.getInt(2));
                orderLine.setPrice(c.getDouble(3));
                orderLine.setTaxPercentage(c.getDouble(4));
                orderLine.setTotalLineAmount(c.getDouble(5));
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
        }
        ProductDB productDB = new ProductDB(context, user);
        ArrayList<OrderLine> orderLinesToDelete = new ArrayList<>();
        for(OrderLine orderLine : orderLines) {
            orderLine.setProduct(productDB.getProductById(orderLine.getProduct().getId(), false));
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
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT COUNT(ECOMMERCE_ORDERLINE_ID) FROM ECOMMERCE_ORDERLINE " +
                    " WHERE ECOMMERCE_ORDER_ID=? AND ISACTIVE = ?",
                    new String[]{String.valueOf(orderId), "Y"}, null);
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
        }
        return 0;
    }

    public int getActiveShoppingCartLinesNumber(){
        return getActiveOrderLinesNumber(SHOPPING_CART_DOCTYPE);
    }

    private int getActiveOrderLinesNumber(String docType) {
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE=? AND ISACTIVE = ?",
                    new String[]{docType, "Y"}, null);
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
        }
        return 0;
    }

    public int moveShoppingCartToFinalizedOrderByOrderId(int orderId) {
        return moveOrderLinesToOrderByOrderId(orderId, FINALIZED_ORDER_DOCTYPE, SHOPPING_CART_DOCTYPE);
    }

    private int moveOrderLinesToOrderByOrderId(int orderId, String newDocType, String currentDocType) {
        try {
            return context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null,
                    "UPDATE ECOMMERCE_ORDERLINE SET ECOMMERCE_ORDER_ID = ?, UPDATE_TIME = ?, " +
                    " DOC_TYPE = ? WHERE ISACTIVE = ? AND DOC_TYPE = ?",
                    new String[]{String.valueOf(orderId), "datetime('now')", newDocType, "Y", currentDocType});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}

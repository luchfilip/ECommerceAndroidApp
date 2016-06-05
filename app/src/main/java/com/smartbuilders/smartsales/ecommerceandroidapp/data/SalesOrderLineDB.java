package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderLineDB {

    public static final String SHOPPING_SALE_DOCTYPE = "SS";
    public static final String FINALIZED_SALES_ORDER_DOCTYPE = "FSO";

    private Context context;
    private User user;

    public SalesOrderLineDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public String addProductToShoppingSale(Product product, int qtyRequested, double productPrice, double productTaxPercentage, int businessPartnerId){
        return addSalesOrderLine(product, qtyRequested, productPrice, productTaxPercentage, SHOPPING_SALE_DOCTYPE, null, businessPartnerId);
    }

    public String addSalesOrderLineToFinalizedSalesOrder(SalesOrderLine orderLine, int orderId, int businessPartnerId){
        return addSalesOrderLine(orderLine.getProduct(), orderLine.getQuantityOrdered(), 0, 0, FINALIZED_SALES_ORDER_DOCTYPE, orderId, businessPartnerId);
    }

    public ArrayList<SalesOrderLine> getShoppingSale(int businessPartnerId){
        return getOrderLines(SHOPPING_SALE_DOCTYPE, null);
    }

    public ArrayList<SalesOrderLine> getSalesOrderLines(Integer salesOrderId){
        return getOrderLines(FINALIZED_SALES_ORDER_DOCTYPE, salesOrderId);
    }

    public ArrayList<SalesOrderLine> getActiveFinalizedSalesOrderLinesByOrderId(int orderId){
        return getActiveOrderLinesByOrderId(FINALIZED_SALES_ORDER_DOCTYPE, orderId);
    }

    public int getActiveShoppingSalesLinesNumber(int businessPartnerId){
        return getActiveOrderLinesNumber(SHOPPING_SALE_DOCTYPE);
    }

    public int moveShoppingSaleToFinalizedSaleOrderByOrderId(int businessPartnerId, int orderId) {
        return moveOrderLinesToOrderByOrderId(businessPartnerId, orderId, FINALIZED_SALES_ORDER_DOCTYPE, SHOPPING_SALE_DOCTYPE);
    }

    private ArrayList<SalesOrderLine> getActiveOrderLinesByOrderId (String docType, int orderId) {
        return getOrderLines(docType, orderId);
    }

    public String updateSalesOrderLine(SalesOrderLine orderLine){
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

    private int getActiveOrderLinesNumber(String docType) {
        Cursor c = null;
        try {
            String sql = "SELECT COUNT(*) FROM ECOMMERCE_ORDERLINE WHERE DOC_TYPE=? AND ISACTIVE = ?";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{docType, "Y"}, null);
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

    private String addSalesOrderLine(Product product, int qtyRequested, double productPrice,
                                double productTaxPercentage, String docType, Integer orderId, int businessPartnerId) {
        try {
            SalesOrderLine ol = new SalesOrderLine();
            ol.setProduct(product);
            ol.setPrice(productPrice);
            ol.setQuantityOrdered(qtyRequested);
            ol.setTaxPercentage(productTaxPercentage);
            ol.setTotalLineAmount(SalesOrderLineBR.getTotalLine(ol));

            String sql = "INSERT INTO ECOMMERCE_SORDERLINE (PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, " +
                    " TAX_PERCENTAGE, TOTAL_LINE, DOC_TYPE, ECOMMERCE_ORDER_ID, ISACTIVE, APP_VERSION, APP_USER_NAME) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, sql,
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

    public String deleteSalesOrderLine(SalesOrderLine orderLine){
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

    private ArrayList<SalesOrderLine> getOrderLines(String docType, Integer orderId) {
        ArrayList<SalesOrderLine> orderLines = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT ECOMMERCE_ORDERLINE_ID, PRODUCT_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_ORDERLINE WHERE ISACTIVE = ? AND DOC_TYPE = ? " +
                    (orderId!=null ? " AND ECOMMERCE_ORDER_ID = "+orderId : "") +
                    " ORDER BY CREATE_TIME DESC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
            while(c.moveToNext()){
                SalesOrderLine orderLine = new SalesOrderLine();
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
        ArrayList<SalesOrderLine> orderLinesToDelete = new ArrayList<>();
        for(SalesOrderLine orderLine : orderLines) {
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
            String sql = "SELECT COUNT(ECOMMERCE_ORDERLINE_ID) FROM ECOMMERCE_ORDERLINE " +
                    " WHERE ECOMMERCE_ORDER_ID=? AND ISACTIVE = ?";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{String.valueOf(orderId), "Y"}, null);
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

    private int moveOrderLinesToOrderByOrderId(int businessPartnerId, int orderId, String newDocType, String currentDocType) {
        try {
            String sql = "UPDATE ECOMMERCE_ORDERLINE SET ECOMMERCE_ORDER_ID = ?, UPDATE_TIME = ?, " +
                    " DOC_TYPE = ? WHERE ISACTIVE = ? AND DOC_TYPE = ?";
            return context.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                    null, sql,
                    new String[]{String.valueOf(orderId), "datetime('now')", newDocType, "Y", currentDocType});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}

package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderLineBR;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderLineDB {

    public static final String SHOPPING_SALE_DOC_TYPE = "SS";
    public static final String FINALIZED_SALES_ORDER_DOC_TYPE = "FSO";

    private Context mContext;
    private User mUser;

    public SalesOrderLineDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public String addProductToShoppingSale(int productId, int qtyRequested, double productPrice, double productTaxPercentage, int businessPartnerId){
        return addSalesOrderLine(productId, qtyRequested, productPrice, productTaxPercentage, SHOPPING_SALE_DOC_TYPE, null, businessPartnerId);
    }

    public String addSalesOrderLineToFinalizedSalesOrder(SalesOrderLine orderLine, int orderId, int businessPartnerId){
        return addSalesOrderLine(orderLine.getProductId(), orderLine.getQuantityOrdered(), 0, 0, FINALIZED_SALES_ORDER_DOC_TYPE, orderId, businessPartnerId);
    }

    public ArrayList<SalesOrderLine> getShoppingSale(int businessPartnerId){
        return getActiveSalesOrderLinesByBusinessPartnerId(SHOPPING_SALE_DOC_TYPE, businessPartnerId);
    }

    public ArrayList<SalesOrderLine> getActiveFinalizedSalesOrderLinesByOrderId(int orderId){
        return getActiveOrderLinesByOrderId(FINALIZED_SALES_ORDER_DOC_TYPE, orderId);
    }

    public int moveShoppingSaleToFinalizedSaleOrderByOrderId(int businessPartnerId, int orderId) {
        return moveOrderLinesToOrderByOrderId(businessPartnerId, orderId, FINALIZED_SALES_ORDER_DOC_TYPE, SHOPPING_SALE_DOC_TYPE);
    }

    /**
     *
     * @param docType
     * @param orderId
     * @return
     */
    private ArrayList<SalesOrderLine> getActiveOrderLinesByOrderId (String docType, int orderId) {
        return getSalesOrderLinesByOrderId(docType, orderId);
    }

    /**
     *
     * @param orderLine
     * @return
     */
    public String updateSalesOrderLine(SalesOrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                        " TAX_PERCENTAGE = ?, TOTAL_LINE = ?, UPDATE_TIME = ? " +
                    " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{String.valueOf(orderLine.getQuantityOrdered()),
                            String.valueOf(orderLine.getPrice()), String.valueOf(orderLine.getTaxPercentage()),
                            String.valueOf(orderLine.getTotalLineAmount()), DateFormat.getCurrentDateTimeSQLFormat(),
                            String.valueOf(orderLine.getId()), String.valueOf(mUser.getServerUserId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public int getActiveShoppingSaleLinesNumberByBusinessPartnerId(int businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(*) FROM ECOMMERCE_SALES_ORDER_LINE " +
                        " WHERE BUSINESS_PARTNER_ID=? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()),
                            SHOPPING_SALE_DOC_TYPE, "Y"}, null);
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
        return -1;
    }

    /**
     *
     * @param productId
     * @param qtyRequested
     * @param productPrice
     * @param productTaxPercentage
     * @param docType
     * @param orderId
     * @param businessPartnerId
     * @return
     */
    private String addSalesOrderLine(int productId, int qtyRequested, double productPrice,
                                double productTaxPercentage, String docType, Integer orderId, int businessPartnerId) {
        try {
            SalesOrderLine salesOrderLine = new SalesOrderLine();
            salesOrderLine.setId(getMaxSalesOrderLineId() + 1);
            salesOrderLine.setProductId(productId);
            salesOrderLine.setPrice(productPrice);
            salesOrderLine.setQuantityOrdered(qtyRequested);
            salesOrderLine.setTaxPercentage(productTaxPercentage);
            salesOrderLine.setTotalLineAmount(SalesOrderLineBR.getTotalLine(salesOrderLine));

            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "INSERT INTO ECOMMERCE_SALES_ORDER_LINE (ECOMMERCE_SALES_ORDER_LINE_ID, USER_ID, " +
                        " PRODUCT_ID, BUSINESS_PARTNER_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, " +
                        " TOTAL_LINE, DOC_TYPE, ECOMMERCE_SALES_ORDER_ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(salesOrderLine.getId()), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(salesOrderLine.getProductId()), String.valueOf(businessPartnerId),
                            String.valueOf(salesOrderLine.getQuantityOrdered()), String.valueOf(salesOrderLine.getPrice()),
                            String.valueOf(salesOrderLine.getTaxPercentage()), String.valueOf(salesOrderLine.getTotalLineAmount()),
                            docType, (orderId==null ? null : String.valueOf(orderId)), DateFormat.getCurrentDateTimeSQLFormat(),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     *
     * @param orderLine
     * @return
     */
    public String deactiveSalesOrderLine(SalesOrderLine orderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                        " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{"N", "datetime('now','localtime')", String.valueOf(orderLine.getId()),
                            String.valueOf(mUser.getServerUserId())});
            if (rowsAffected < 1) {
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     *
     * @param docType
     * @param businessPartnerId
     * @return
     */
    private ArrayList<SalesOrderLine> getActiveSalesOrderLinesByBusinessPartnerId(String docType, int businessPartnerId) {
        ArrayList<SalesOrderLine> salesOrderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_SALES_ORDER_LINE_ID, PRODUCT_ID, BUSINESS_PARTNER_ID, QTY_REQUESTED, " +
                        " SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?" +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()),
                            docType, "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    SalesOrderLine salesOrderLine = new SalesOrderLine();
                    salesOrderLine.setId(c.getInt(0));
                    salesOrderLine.setProductId(c.getInt(1));
                    salesOrderLine.setBusinessPartnerId(c.getInt(2));
                    salesOrderLine.setQuantityOrdered(c.getInt(3));
                    salesOrderLine.setPrice(c.getDouble(4));
                    salesOrderLine.setTaxPercentage(c.getDouble(5));
                    salesOrderLine.setTotalLineAmount(c.getDouble(6));
                    salesOrderLines.add(salesOrderLine);
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
        ArrayList<SalesOrderLine> salesOrderLinesToRemove = new ArrayList<>();
        ProductDB productDB = new ProductDB(mContext, mUser);
        for (SalesOrderLine salesOrderLine : salesOrderLines) {
            salesOrderLine.setProduct(productDB.getProductById(salesOrderLine.getProductId()));
            if(salesOrderLine.getProduct()==null){
                salesOrderLinesToRemove.add(salesOrderLine);
            }
        }
        if (!salesOrderLinesToRemove.isEmpty()) {
            salesOrderLines.removeAll(salesOrderLinesToRemove);
        }
        return salesOrderLines;
    }

    /**
     *
     * @param docType
     * @param salesOrderId
     * @return
     */
    private ArrayList<SalesOrderLine> getSalesOrderLinesByOrderId(String docType, int salesOrderId) {
        ArrayList<SalesOrderLine> salesOrderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_SALES_ORDER_LINE_ID, PRODUCT_ID, BUSINESS_PARTNER_ID, " +
                        " QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?" +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            docType, "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    SalesOrderLine salesOrderLine = new SalesOrderLine();
                    salesOrderLine.setId(c.getInt(0));
                    salesOrderLine.setProductId(c.getInt(1));
                    salesOrderLine.setBusinessPartnerId(c.getInt(2));
                    salesOrderLine.setQuantityOrdered(c.getInt(3));
                    salesOrderLine.setPrice(c.getDouble(4));
                    salesOrderLine.setTaxPercentage(c.getDouble(5));
                    salesOrderLine.setTotalLineAmount(c.getDouble(6));
                    salesOrderLines.add(salesOrderLine);
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
        ArrayList<SalesOrderLine> salesOrderLinesToRemove = new ArrayList<>();
        ProductDB productDB = new ProductDB(mContext, mUser);
        for (SalesOrderLine salesOrderLine : salesOrderLines) {
            salesOrderLine.setProduct(productDB.getProductById(salesOrderLine.getProductId()));
            if(salesOrderLine.getProduct()==null){
                salesOrderLinesToRemove.add(salesOrderLine);
            }
        }
        if (!salesOrderLinesToRemove.isEmpty()) {
            salesOrderLines.removeAll(salesOrderLinesToRemove);
        }
        return salesOrderLines;
    }

    /**
     *
     * @param salesOrderId
     * @return
     */
    public int getOrderLineNumbersBySalesOrderId(int salesOrderId){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(ECOMMERCE_SALES_ORDER_LINE_ID) " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            "Y"}, null);
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

    /**
     *
     * @param userBusinessPartnerId
     * @param salesOrderId
     * @param newDocType
     * @param currentDocType
     * @return
     */
    private int moveOrderLinesToOrderByOrderId(int userBusinessPartnerId, int salesOrderId, String newDocType,
                                               String currentDocType) {
        try {
            return mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE " +
                    " SET ECOMMERCE_SALES_ORDER_ID = ?, UPDATE_TIME = ?, DOC_TYPE = ? " +
                    " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(salesOrderId), DateFormat.getCurrentDateTimeSQLFormat(), newDocType,
                            String.valueOf(userBusinessPartnerId), String.valueOf(mUser.getServerUserId()),
                            currentDocType, "Y"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param businessPartnerId
     * @return
     */
    public String deactiveLinesFromShoppingSaleByBusinessPartnerId(int businessPartnerId) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                                    " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ?",
                            new String[]{"N", "datetime('now','localtime')",
                                    String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()), SHOPPING_SALE_DOC_TYPE});
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    private int getMaxSalesOrderLineId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(ECOMMERCE_SALES_ORDER_LINE_ID) FROM ECOMMERCE_SALES_ORDER_LINE WHERE USER_ID = ?",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
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

}

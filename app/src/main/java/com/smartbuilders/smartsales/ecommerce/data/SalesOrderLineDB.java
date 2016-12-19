package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
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

    /**
     *
     * @param salesOrderLine
     * @return
     */
    public String addSalesOrderLinesToShoppingSale(SalesOrderLine salesOrderLine) {
        return addSalesOrderLine(salesOrderLine, SHOPPING_SALE_DOC_TYPE, null);
    }

    /**
     *
     * @param productId
     * @param businessPartnerId
     * @return
     */
    public SalesOrderLine getSalesOrderLineFromShoppingSales(int productId, int businessPartnerId){
        return getSalesOrderLine(productId, businessPartnerId, SHOPPING_SALE_DOC_TYPE);
    }

    /**
     *
     * @return
     */
    public ArrayList<SalesOrderLine> getShoppingSale(){
        return getSalesOrderLinesByDocType(SHOPPING_SALE_DOC_TYPE);
    }

    /**
     *
     * @return
     */
    public ArrayList<SalesOrderLine> getShoppingSaleByBusinessPartnerId(int businessPartnersId){
        return getSalesOrderLinesByDocTypeAndBusinessPartnerId(SHOPPING_SALE_DOC_TYPE, businessPartnersId);
    }

    /**
     *
     * @param salesOrderId
     * @param businessPartnerId
     * @return
     */
    public ArrayList<SalesOrderLine> getSalesOrderLinesList(int salesOrderId, int businessPartnerId){
        ArrayList<SalesOrderLine> salesOrderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "SELECT ECOMMERCE_SALES_ORDER_LINE_ID, PRODUCT_ID, BUSINESS_PARTNER_ID, " +
                            " QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE " +
                            " FROM ECOMMERCE_SALES_ORDER_LINE " +
                            " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND BUSINESS_PARTNER_ID = ? " +
                            " AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                            " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(businessPartnerId), FINALIZED_SALES_ORDER_DOC_TYPE, "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    SalesOrderLine salesOrderLine = new SalesOrderLine();
                    salesOrderLine.setId(c.getInt(0));
                    salesOrderLine.setProductId(c.getInt(1));
                    salesOrderLine.setBusinessPartnerId(c.getInt(2));
                    salesOrderLine.setQuantityOrdered(c.getInt(3));
                    salesOrderLine.setProductPrice(c.getDouble(4));
                    salesOrderLine.setProductTaxPercentage(c.getDouble(5));
                    salesOrderLine.setLineTaxAmount(c.getDouble(6));
                    salesOrderLine.setSubTotalLineAmount(c.getDouble(7));
                    salesOrderLine.setTotalLineAmount(c.getDouble(8));
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
        ProductDB productDB = new ProductDB(mContext, mUser);
        for (SalesOrderLine salesOrderLine : salesOrderLines) {
            salesOrderLine.setProduct(productDB.getProductById(salesOrderLine.getProductId()));
            if(salesOrderLine.getProduct()==null){
                Product product = new Product();
                product.setId(salesOrderLine.getProductId());
                product.setName("No hay información disponible");
                salesOrderLine.setProduct(product);
            }
        }
        return salesOrderLines;
    }

    ///**
    // *
    // * @param businessPartnerId
    // * @param salesOrderId
    // * @return
    // */
    //public int moveShoppingSaleToSalesOrder(int businessPartnerId, int salesOrderId) {
    //    try {
    //        return mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
    //                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
    //                        .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
    //                null,
    //                "UPDATE ECOMMERCE_SALES_ORDER_LINE " +
    //                        " SET ECOMMERCE_SALES_ORDER_ID = ?, UPDATE_TIME = ?, DOC_TYPE = ?, SEQUENCE_ID = 0 " +
    //                        " WHERE ECOMMERCE_SALES_ORDER_LINE_ID IN ("+getSalesOrderLinesIds(businessPartnerId, SHOPPING_SALE_DOC_TYPE)+") " +
    //                            " AND BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?",
    //                new String[]{String.valueOf(salesOrderId), DateFormat.getCurrentDateTimeSQLFormat(), FINALIZED_SALES_ORDER_DOC_TYPE,
    //                        String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()),
    //                        SHOPPING_SALE_DOC_TYPE, "Y"});
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    }
    //    return 0;
    //}

    /**
     *
     * @param salesOrderLine
     * @param orderId
     * @return
     */
    public String moveSalesOrderLineToFinalizedSalesOrder(SalesOrderLine salesOrderLine, int orderId){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET ECOMMERCE_SALES_ORDER_ID = ?, QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                            " TAX_PERCENTAGE = ?, TAX_AMOUNT = ?, SUB_TOTAL_LINE = ?, TOTAL_LINE = ?, " +
                            " UPDATE_TIME = ?, DOC_TYPE = ?, SEQUENCE_ID = 0 " +
                            " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ? " +
                            " AND BUSINESS_PARTNER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                            " AND (ECOMMERCE_SALES_ORDER_ID IS NULL OR ECOMMERCE_SALES_ORDER_ID = 0)",
                    new String[]{String.valueOf(orderId), String.valueOf(salesOrderLine.getQuantityOrdered()),
                            String.valueOf(salesOrderLine.getProductPrice()), String.valueOf(salesOrderLine.getProductTaxPercentage()),
                            String.valueOf(salesOrderLine.getLineTaxAmount()), String.valueOf(salesOrderLine.getSubTotalLineAmount()),
                            String.valueOf(salesOrderLine.getTotalLineAmount()), DateFormat.getCurrentDateTimeSQLFormat(), FINALIZED_SALES_ORDER_DOC_TYPE,
                            String.valueOf(salesOrderLine.getId()), String.valueOf(mUser.getServerUserId()), String.valueOf(salesOrderLine.getBusinessPartnerId()),
                            SHOPPING_SALE_DOC_TYPE, "Y"});
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
     * @param businessPartnerId
     * @param docType
     * @return
     */
    private String getSalesOrderLinesIds(int businessPartnerId, String docType) {
        StringBuilder salesOrderLinesIds = new StringBuilder();
        Cursor c = null;
        try {
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                            "SELECT ECOMMERCE_SALES_ORDER_LINE_ID FROM ECOMMERCE_SALES_ORDER_LINE " +
                            " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?",
                            new String[]{String.valueOf(businessPartnerId),
                                    String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    salesOrderLinesIds.append(salesOrderLinesIds.length()>0 ? "," : "").append(c.getInt(0));
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
        return salesOrderLinesIds.toString();
    }

    /**
     *
     * @param salesOrderLine
     * @return
     */
    public String updateSalesOrderLine(SalesOrderLine salesOrderLine){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET QTY_REQUESTED = ?, SALES_PRICE = ?, " +
                        " TAX_PERCENTAGE = ?, TAX_AMOUNT = ?, SUB_TOTAL_LINE = ?, TOTAL_LINE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                    " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ?",
                    new String[]{String.valueOf(salesOrderLine.getQuantityOrdered()),
                            String.valueOf(salesOrderLine.getProductPrice()), String.valueOf(salesOrderLine.getProductTaxPercentage()),
                            String.valueOf(salesOrderLine.getLineTaxAmount()), String.valueOf(salesOrderLine.getSubTotalLineAmount()),
                            String.valueOf(salesOrderLine.getTotalLineAmount()), DateFormat.getCurrentDateTimeSQLFormat(),
                            String.valueOf(salesOrderLine.getId()), String.valueOf(mUser.getServerUserId())});
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
     * @param salesOrderLine
     * @param docType
     * @param orderId
     * @return
     */
    private String addSalesOrderLine(SalesOrderLine salesOrderLine, String docType, Integer orderId) {
        try {
            salesOrderLine.setId(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "ECOMMERCE_SALES_ORDER_LINE"));
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "INSERT INTO ECOMMERCE_SALES_ORDER_LINE (ECOMMERCE_SALES_ORDER_LINE_ID, USER_ID, " +
                            " PRODUCT_ID, BUSINESS_PARTNER_ID, QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE," +
                            " TOTAL_LINE, DOC_TYPE, ECOMMERCE_SALES_ORDER_ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(salesOrderLine.getId()), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(salesOrderLine.getProductId()), String.valueOf(salesOrderLine.getBusinessPartnerId()),
                            String.valueOf(salesOrderLine.getQuantityOrdered()), String.valueOf(salesOrderLine.getProductPrice()),
                            String.valueOf(salesOrderLine.getProductTaxPercentage()), String.valueOf(salesOrderLine.getLineTaxAmount()),
                            String.valueOf(salesOrderLine.getSubTotalLineAmount()), String.valueOf(salesOrderLine.getTotalLineAmount()),
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
     * @param id
     * @return
     */
    public String deactivateSalesOrderLine(int id){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                        " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ? " +
                            " AND (ECOMMERCE_SALES_ORDER_ID IS NULL OR ECOMMERCE_SALES_ORDER_ID = 0)",
                    new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(id),
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
     * @param id
     * @return
     */
    public String restoreSalesOrderLine(int id){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                            " WHERE ECOMMERCE_SALES_ORDER_LINE_ID = ? AND USER_ID = ? " +
                            " AND (ECOMMERCE_SALES_ORDER_ID IS NULL OR ECOMMERCE_SALES_ORDER_ID = 0)",
                    new String[]{"Y", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(id),
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

    private ArrayList<SalesOrderLine> getSalesOrderLinesByDocType(String docType) {
        try {
            return getSalesOrderLinesByDocTypeAndBusinessPartnerId(docType, Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<SalesOrderLine>();
        }
    }

    /**
     *
     * @param docType
     * @param businessPartnerId
     * @return
     */
    private ArrayList<SalesOrderLine> getSalesOrderLinesByDocTypeAndBusinessPartnerId(String docType, int businessPartnerId) {
        ArrayList<SalesOrderLine> salesOrderLines = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT ECOMMERCE_SALES_ORDER_LINE_ID, PRODUCT_ID, BUSINESS_PARTNER_ID, " +
                        " QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? " +
                    " ORDER BY CREATE_TIME DESC",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    SalesOrderLine salesOrderLine = new SalesOrderLine();
                    salesOrderLine.setId(c.getInt(0));
                    salesOrderLine.setProductId(c.getInt(1));
                    salesOrderLine.setBusinessPartnerId(c.getInt(2));
                    salesOrderLine.setQuantityOrdered(c.getInt(3));
                    salesOrderLine.setProductPrice(c.getDouble(4));
                    salesOrderLine.setProductTaxPercentage(c.getDouble(5));
                    salesOrderLine.setLineTaxAmount(c.getDouble(6));
                    salesOrderLine.setSubTotalLineAmount(c.getDouble(7));
                    salesOrderLine.setTotalLineAmount(c.getDouble(8));
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
     * @param businessPartnerId
     * @return
     */
    public int getOrderLineQtyBySalesOrderId(int salesOrderId, int businessPartnerId){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(ECOMMERCE_SALES_ORDER_LINE_ID) " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ? AND BUSINESS_PARTNER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(businessPartnerId), "Y"}, null);
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
     * @param businessPartnerId
     * @return
     */
    public String deactivateLinesFromShoppingSaleByBusinessPartnerId(int businessPartnerId) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                                    " WHERE ECOMMERCE_SALES_ORDER_LINE_ID IN ("+getSalesOrderLinesIds(businessPartnerId, SHOPPING_SALE_DOC_TYPE)+") " +
                                    " AND BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ?",
                            new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(),
                                    String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()), SHOPPING_SALE_DOC_TYPE});
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    /**
     *
     * @param productId
     * @param businessPartnerId
     * @param docType
     * @return
     */
    private SalesOrderLine getSalesOrderLine(int productId, int businessPartnerId, String docType){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT ECOMMERCE_SALES_ORDER_LINE_ID, PRODUCT_ID, BUSINESS_PARTNER_ID, " +
                        " QTY_REQUESTED, SALES_PRICE, TAX_PERCENTAGE, TAX_AMOUNT, SUB_TOTAL_LINE, TOTAL_LINE " +
                    " FROM ECOMMERCE_SALES_ORDER_LINE " +
                    " WHERE PRODUCT_ID = ? AND BUSINESS_PARTNER_ID = ? " +
                        " AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(productId), String.valueOf(businessPartnerId),
                            String.valueOf(mUser.getServerUserId()), docType, "Y"}, null);
            if(c!=null && c.moveToNext()){
                SalesOrderLine salesOrderLine = new SalesOrderLine();
                salesOrderLine.setId(c.getInt(0));
                salesOrderLine.setProductId(c.getInt(1));
                salesOrderLine.setBusinessPartnerId(c.getInt(2));
                salesOrderLine.setQuantityOrdered(c.getInt(3));
                salesOrderLine.setProductPrice(c.getDouble(4));
                salesOrderLine.setProductTaxPercentage(c.getDouble(5));
                salesOrderLine.setLineTaxAmount(c.getDouble(6));
                salesOrderLine.setSubTotalLineAmount(c.getDouble(7));
                salesOrderLine.setTotalLineAmount(c.getDouble(8));
                c.close();
                salesOrderLine.setProduct((new ProductDB(mContext, mUser)).getProductById(salesOrderLine.getProductId()));
                if(salesOrderLine.getProduct()!=null){
                    return salesOrderLine;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(c != null && !c.isClosed()) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

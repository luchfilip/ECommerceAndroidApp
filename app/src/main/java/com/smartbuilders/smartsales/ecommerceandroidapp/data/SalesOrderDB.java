package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.businessRules.SalesOrderBR;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderDB {

    private Context mContext;
    private User mCurrentUser;

    public SalesOrderDB(Context context, User user){
        this.mContext = context;
        this.mCurrentUser = user;
    }

    public String createSalesOrderFromShoppingSale(int businessPartnerId, Date validTo){
        return createOrder(businessPartnerId, (new SalesOrderLineDB(mContext, mCurrentUser)).getShoppingSale(businessPartnerId), validTo, false);
    }

    //public int getLastFinalizedSalesOrderId() {
    //    Cursor c = null;
    //    try {
    //        c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
    //                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
    //                .build(), null,
    //                "SELECT MAX(ECOMMERCE_SALES_ORDER_ID) " +
    //                " FROM ECOMMERCE_SALES_ORDER SO " +
    //                    " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.ISACTIVE = ? " +
    //                " WHERE SO.ISACTIVE = ? AND SO.DOC_TYPE = ?",
    //                new String[]{"Y", "Y", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE}, null);
    //        if(c!=null && c.moveToNext()){
    //            return c.getInt(0);
    //        }
    //    } catch (Exception e){
    //        e.printStackTrace();
    //    } finally {
    //        if(c != null) {
    //            try {
    //                c.close();
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //    return -1;
    //}

    public SalesOrder getActiveSalesOrderById(int salesOrderId) {
        Cursor c = null;
        SalesOrder salesOrder = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.CREATE_TIME, SO.LINES_NUMBER, " +
                        " SO.SUB_TOTAL, SO.TAX, SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO "+
                    " FROM ECOMMERCE_SALES_ORDER SO " +
                        " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                    " WHERE SO.ECOMMERCE_SALES_ORDER_ID = ? AND SO.IS_ACTIVE = ?",
                    new String[]{"Y", String.valueOf(salesOrderId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                salesOrder = new SalesOrder();
                salesOrder.setId(c.getInt(0));
                try{
                    salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(1)).getTime()));
                }catch(ParseException ex){
                    try {
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
                    } catch (ParseException e) { }
                }catch(Exception ex){ }
                salesOrder.setLinesNumber(c.getInt(2));
                salesOrder.setSubTotalAmount(c.getDouble(3));
                salesOrder.setTaxAmount(c.getDouble(4));
                salesOrder.setTotalAmount(c.getDouble(5));
                salesOrder.setBusinessPartnerId(c.getInt(6));
                try {
                    if(c.getString(7)!=null){
                        salesOrder.setValidTo((new SimpleDateFormat("yyyy-MM-dd")).parse(c.getString(7)));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(salesOrder!=null){
                salesOrder.setBusinessPartner((new UserBusinessPartnerDB(mContext, mCurrentUser))
                        .getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
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
        return salesOrder;
    }

    public ArrayList<SalesOrder> getActiveShoppingSalesOrders(){
        ArrayList<SalesOrder> activeOrders = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(OL.BUSINESS_PARTNER_ID), OL.BUSINESS_PARTNER_ID " +
                    " FROM ECOMMERCE_SALES_ORDERLINE OL " +
                        " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_BUSINESS_PARTNER_ID = OL.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                    " WHERE OL.IS_ACTIVE = ? AND OL.DOC_TYPE = ? " +
                    " GROUP BY OL.BUSINESS_PARTNER_ID",
                    new String[]{"Y", "Y", SalesOrderLineDB.SHOPPING_SALE_DOCTYPE}, null);
            if(c!=null){
                while(c.moveToNext()){
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setLinesNumber(c.getInt(0));
                    salesOrder.setBusinessPartnerId(c.getInt(1));
                    activeOrders.add(salesOrder);
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
        UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mCurrentUser);
        for(SalesOrder salesOrder : activeOrders){
            salesOrder.setBusinessPartner(userBusinessPartnerDB.getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
        }
        return activeOrders;
    }

    public ArrayList<SalesOrder> getActiveSalesOrders(){
        ArrayList<SalesOrder> activeSalesOrders = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.DOC_STATUS, SO.CREATE_TIME, SO.UPDATE_TIME, " +
                        " SO.APP_VERSION, SO.APP_USER_NAME, SO.LINES_NUMBER, SO.SUB_TOTAL, SO.TAX, " +
                        " SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO " +
                    " FROM ECOMMERCE_SALES_ORDER SO " +
                        " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                    " WHERE SO.IS_ACTIVE = ? AND SO.DOC_TYPE = ? " +
                    " order by SO.ECOMMERCE_SALES_ORDER_ID desc",
                    new String[]{"Y", "Y", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE}, null);
            if(c!=null){
                while(c.moveToNext()){
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setId(c.getInt(0));
                    try{
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                    }catch(ParseException ex){
                        try {
                            salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                        } catch (ParseException e) { }
                    }catch(Exception ex){ }
                    salesOrder.setLinesNumber(c.getInt(6));
                    salesOrder.setSubTotalAmount(c.getDouble(7));
                    salesOrder.setTaxAmount(c.getDouble(8));
                    salesOrder.setTotalAmount(c.getDouble(9));
                    salesOrder.setBusinessPartnerId(c.getInt(10));
                    try {
                        if(c.getString(11)!=null){
                            salesOrder.setValidTo((new SimpleDateFormat("yyyy-MM-dd")).parse(c.getString(11)));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activeSalesOrders.add(salesOrder);
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
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(mContext, mCurrentUser);
        UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mCurrentUser);
        for(SalesOrder salesOrder : activeSalesOrders){
            salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineNumbersBySalesOrderId(salesOrder.getId()));
            salesOrder.setBusinessPartner(userBusinessPartnerDB.getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
        }
        return activeSalesOrders;
    }

    public String createOrder(int businessPartnerId, ArrayList<SalesOrderLine> orderLines, Date validTo, boolean insertOrderLinesInDB){
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(mContext, mCurrentUser);
        if((orderLines != null && insertOrderLinesInDB)
                || salesOrderLineDB.getActiveShoppingSaleLinesNumberByBusinessPartnerId(businessPartnerId)>0){
            Cursor c = null;
            int salesOrderId = 0;
            try {
                double subTotal = SalesOrderBR.getSubTotalAmount(orderLines),
                        tax = SalesOrderBR.getTaxAmount(orderLines),
                        total = SalesOrderBR.getTotalAmount(orderLines);

                int rowsAffected = mContext.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                                null,
                                "INSERT INTO ECOMMERCE_SALES_ORDER (BUSINESS_PARTNER_ID, DOC_STATUS, DOC_TYPE, APP_VERSION, " +
                                        " APP_USER_NAME, DEVICE_MAC_ADDRESS, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, VALID_TO, IS_ACTIVE) " +
                                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(businessPartnerId), "CO", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                                        Utils.getAppVersionName(mContext), mCurrentUser.getUserName(),
                                        Utils.getMacAddress(mContext), String.valueOf(orderLines.size()),
                                        String.valueOf(subTotal), String.valueOf(tax), String.valueOf(total),
                                        validTo!=null?(new SimpleDateFormat("yyyy-MM-dd")).format(validTo):null, "Y"});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }

                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                        null,
                        "SELECT MAX(ECOMMERCE_SALES_ORDER_ID) FROM ECOMMERCE_SALES_ORDER WHERE IS_ACTIVE = ? AND DOC_TYPE = ?",
                        new String[]{"Y", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE}, null);
                if(c!=null && c.moveToNext()){
                    salesOrderId = c.getInt(0);
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
            }
            if (insertOrderLinesInDB) {
                for (SalesOrderLine orderLine : orderLines) {
                    salesOrderLineDB.addSalesOrderLineToFinalizedSalesOrder(orderLine, salesOrderId, businessPartnerId);
                }
            } else {
                if(salesOrderLineDB.moveShoppingSaleToFinalizedSaleOrderByOrderId(businessPartnerId, salesOrderId)<=0){
                    try {
                        int rowsAffected = mContext.getContentResolver()
                                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                                        null,
                                        "DELETE FROM ECOMMERCE_SALES_ORDER WHERE ECOMMERCE_SALES_ORDER_ID = ?",
                                        new String[]{String.valueOf(salesOrderId)});
                        if(rowsAffected <= 0){
                            return "Error 003 - No se insertó la cotización en la base de datos ni se eliminó la cabecera.";
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    return "Error 002 - No se insertó la cotización en la base de datos.";
                }
            }
        }else{
            return "No existen productos en el Carrito de compras.";
        }
        return null;
    }

    public String deactiveSalesOrderById(int salesOrderId) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDERLINE SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? ",
                            new String[]{"N", "datetime('now','localtime')", String.valueOf(salesOrderId)});
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? ",
                            new String[]{"N", "datetime('now','localtime')", String.valueOf(salesOrderId)});
            if(rowsAffected <= 0){
                return "No se actualizó ningún registro en la base de datos";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }
}

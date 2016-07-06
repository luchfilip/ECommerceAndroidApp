package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
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
    private User mUser;

    public SalesOrderDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public String createSalesOrderFromShoppingSale(int businessPartnerId, Date validTo){
        return createSalesOrder(businessPartnerId, (new SalesOrderLineDB(mContext, mUser))
                .getShoppingSale(businessPartnerId), validTo, false);
    }

    public SalesOrder getActiveSalesOrderById(int salesOrderId) {
        Cursor c = null;
        SalesOrder salesOrder = null;
        try {
            if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.CREATE_TIME, SO.LINES_NUMBER, " +
                                " SO.SUB_TOTAL, SO.TAX, SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO "+
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.ECOMMERCE_SALES_ORDER_ID = ? AND SO.USER_ID = ? AND SO.IS_ACTIVE = ?",
                        new String[]{"Y", String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.CREATE_TIME, SO.LINES_NUMBER, " +
                                " SO.SUB_TOTAL, SO.TAX, SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO "+
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.ECOMMERCE_SALES_ORDER_ID = ? AND SO.USER_ID = ? AND SO.IS_ACTIVE = ?",
                        new String[]{"Y", String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            }

            if(c!=null && c.moveToNext()){
                salesOrder = new SalesOrder();
                salesOrder.setId(c.getInt(0));
                try{
                    salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(1)).getTime()));
                }catch(ParseException ex){
                    try {
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
                    } catch (ParseException e) {
                        //empty
                    }
                }catch(Exception ex){
                    //empty
                }
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
                if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                    salesOrder.setBusinessPartner((new UserBusinessPartnerDB(mContext, mUser))
                            .getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
                }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                    salesOrder.setBusinessPartner((new BusinessPartnerDB(mContext, mUser))
                            .getActiveBusinessPartnerById(salesOrder.getBusinessPartnerId()));
                }

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
            if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT COUNT(SOL.BUSINESS_PARTNER_ID), SOL.BUSINESS_PARTNER_ID " +
                                " FROM ECOMMERCE_SALES_ORDERLINE SOL " +
                                " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_ID = SOL.USER_ID " +
                                " AND BP.USER_BUSINESS_PARTNER_ID = SOL.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                                " WHERE SOL.USER_ID = ? AND SOL.DOC_TYPE = ? AND SOL.IS_ACTIVE = ? " +
                                " GROUP BY SOL.BUSINESS_PARTNER_ID",
                        new String[]{"Y", String.valueOf(mUser.getServerUserId()),
                                SalesOrderLineDB.SHOPPING_SALE_DOCTYPE, "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT COUNT(SOL.BUSINESS_PARTNER_ID), SOL.BUSINESS_PARTNER_ID " +
                        " FROM ECOMMERCE_SALES_ORDERLINE SOL " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.USER_ID = SOL.USER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SOL.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SOL.BUSINESS_PARTNER_ID = ? AND SOL.USER_ID = ? AND SOL.DOC_TYPE = ? AND SOL.IS_ACTIVE = ? " +
                        " GROUP BY SOL.BUSINESS_PARTNER_ID",
                        new String[]{"Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                String.valueOf(mUser.getServerUserId()), SalesOrderLineDB.SHOPPING_SALE_DOCTYPE, "Y"}, null);
            }

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
        if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : activeOrders){
                salesOrder.setBusinessPartner(userBusinessPartnerDB.getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : activeOrders){
                salesOrder.setBusinessPartner(businessPartnerDB.getActiveBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }
        return activeOrders;
    }

    public ArrayList<SalesOrder> getActiveSalesOrders(){
        ArrayList<SalesOrder> activeSalesOrders = new ArrayList<>();
        Cursor c = null;
        try {
            if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.DOC_STATUS, SO.CREATE_TIME, SO.UPDATE_TIME, " +
                            " SO.APP_VERSION, SO.APP_USER_NAME, SO.LINES_NUMBER, SO.SUB_TOTAL, SO.TAX, " +
                            " SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO " +
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.USER_ID = ? AND SO.DOC_TYPE = ? AND SO.IS_ACTIVE = ?  " +
                        " order by SO.ECOMMERCE_SALES_ORDER_ID desc",
                        new String[]{"Y", String.valueOf(mUser.getServerUserId()),
                                SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE, "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.DOC_STATUS, SO.CREATE_TIME, SO.UPDATE_TIME, " +
                            " SO.APP_VERSION, SO.APP_USER_NAME, SO.LINES_NUMBER, SO.SUB_TOTAL, SO.TAX, " +
                            " SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO " +
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.BUSINESS_PARTNER_ID = ? SO.USER_ID = ? AND SO.DOC_TYPE = ? AND SO.IS_ACTIVE = ?  " +
                        " order by SO.ECOMMERCE_SALES_ORDER_ID desc",
                        new String[]{"Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                String.valueOf(mUser.getServerUserId()), SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE, "Y"}, null);
            }

            if(c!=null){
                while(c.moveToNext()){
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setId(c.getInt(0));
                    try{
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(c.getString(2)).getTime()));
                    }catch(ParseException ex){
                        try {
                            salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-hh.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
                        } catch (ParseException e) {
                            //empty
                        }
                    }catch(Exception ex){
                        //empty
                    }
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
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(mContext, mUser);
        if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : activeSalesOrders){
                salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineNumbersBySalesOrderId(salesOrder.getId()));
                salesOrder.setBusinessPartner(userBusinessPartnerDB.getActiveUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }else if(mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            BusinessPartnerDB BusinessPartnerDB = new BusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : activeSalesOrders){
                salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineNumbersBySalesOrderId(salesOrder.getId()));
                salesOrder.setBusinessPartner(BusinessPartnerDB.getActiveBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }
        return activeSalesOrders;
    }

    public String createSalesOrder(int businessPartnerId, ArrayList<SalesOrderLine> orderLines, Date validTo, boolean insertOrderLinesInDB){
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(mContext, mUser);
        int activeShoppingSalesLineNumber = salesOrderLineDB.getActiveShoppingSaleLinesNumberByBusinessPartnerId(businessPartnerId);
        if((orderLines != null && insertOrderLinesInDB) || activeShoppingSalesLineNumber>0){
            int salesOrderId;
            try {
                salesOrderId = getMaxSalesOrderId() + 1;

                double subTotal = SalesOrderBR.getSubTotalAmount(orderLines),
                        tax = SalesOrderBR.getTaxAmount(orderLines),
                        total = SalesOrderBR.getTotalAmount(orderLines);

                int rowsAffected = mContext.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                                null,
                                "INSERT INTO ECOMMERCE_SALES_ORDER (ECOMMERCE_SALES_ORDER_ID, USER_ID, BUSINESS_PARTNER_ID, DOC_STATUS, DOC_TYPE, " +
                                        " APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, VALID_TO) " +
                                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                                        String.valueOf(businessPartnerId), "CO", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                                        Utils.getAppVersionName(mContext), mUser.getUserName(),
                                        Utils.getMacAddress(mContext), String.valueOf(orderLines!=null ? orderLines.size() : activeShoppingSalesLineNumber),
                                        String.valueOf(subTotal), String.valueOf(tax), String.valueOf(total),
                                        validTo!=null?(new SimpleDateFormat("yyyy-MM-dd")).format(validTo):null});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }
            } catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
            if (orderLines!=null && insertOrderLinesInDB) {
                for (SalesOrderLine orderLine : orderLines) {
                    salesOrderLineDB.addSalesOrderLineToFinalizedSalesOrder(orderLine, salesOrderId, businessPartnerId);
                }
            } else {
                if(salesOrderLineDB.moveShoppingSaleToFinalizedSaleOrderByOrderId(businessPartnerId, salesOrderId)<=0){
                    try {
                        int rowsAffected = mContext.getContentResolver()
                                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                                        null,
                                        "DELETE FROM ECOMMERCE_SALES_ORDER WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ?",
                                        new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId())});
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
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDERLINE SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ?",
                            new String[]{"N", "datetime('now','localtime')", String.valueOf(salesOrderId),
                                    String.valueOf(mUser.getServerUserId())});
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER SET IS_ACTIVE = ?, UPDATE_TIME = ? " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ?",
                            new String[]{"N", "datetime('now','localtime')", String.valueOf(salesOrderId),
                                    String.valueOf(mUser.getServerUserId())});
            if(rowsAffected <= 0){
                return "No se actualizó ningún registro en la base de datos";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    private int getMaxSalesOrderId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "SELECT MAX(ECOMMERCE_SALES_ORDER_ID) FROM ECOMMERCE_SALES_ORDER WHERE USER_ID = ?",
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

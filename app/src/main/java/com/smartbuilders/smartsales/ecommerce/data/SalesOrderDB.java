package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderBR;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.UtilsGetDataFromDB;
import com.smartbuilders.smartsales.ecommerce.utils.UtilsSyncData;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import org.codehaus.jettison.json.JSONObject;

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
        //TODO: eliminar en el release siguiente a la version 30
        if (mUser!=null && UtilsGetDataFromDB.getCountFromTableName(context, mUser, "SALES_REP")<=0) {
            try {
                JSONObject tablesToSync = new JSONObject();
                tablesToSync.put("1", "SALES_REP");
                UtilsSyncData.requestSyncByTableName(context, mUser, tablesToSync.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param businessPartnerId
     * @param validTo
     * @param businessPartnerAddressId
     * @return
     */
    public String createSalesOrderFromShoppingSale(int businessPartnerId, Date validTo,
                                                   int businessPartnerAddressId, ArrayList<SalesOrderLine> salesOrderLines) {
        if(salesOrderLines!=null && salesOrderLines.size()>0){
            try {
                int salesOrderId = UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "ECOMMERCE_SALES_ORDER");
                double subTotal = SalesOrderBR.getSubTotalAmount(salesOrderLines),
                        tax = SalesOrderBR.getTaxAmount(salesOrderLines),
                        total = SalesOrderBR.getTotalAmount(salesOrderLines);

                int rowsAffected = mContext.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                                null,
                                "INSERT INTO ECOMMERCE_SALES_ORDER (ECOMMERCE_SALES_ORDER_ID, USER_ID, BUSINESS_PARTNER_ID, BUSINESS_PARTNER_ADDRESS_ID, DOC_STATUS, DOC_TYPE, " +
                                        " CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, VALID_TO) " +
                                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()),
                                        String.valueOf(businessPartnerId),
                                        String.valueOf(businessPartnerAddressId),
                                        "CO", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOC_TYPE,
                                        DateFormat.getCurrentDateTimeSQLFormat(),
                                        Utils.getAppVersionName(mContext), mUser.getUserName(),
                                        Utils.getMacAddress(mContext),
                                        String.valueOf(salesOrderLines.size()),
                                        String.valueOf(subTotal),
                                        String.valueOf(tax),
                                        String.valueOf(total),
                                        validTo!=null?(new SimpleDateFormat("yyyy-MM-dd")).format(validTo):null});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }
                SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(mContext, mUser);
                for (SalesOrderLine salesOrderLine : salesOrderLines) {
                    salesOrderLine.setBusinessPartnerId(businessPartnerId);
                    salesOrderLineDB.moveSalesOrderLineToFinalizedSalesOrder(salesOrderLine, salesOrderId);
                }
            } catch (Exception e){
                e.printStackTrace();
                return e.getMessage();
            }
        }else{
            return "No existen productos en la cotización.";
        }
        return null;
    }

    ///**
    // *
    // * @param businessPartnerId
    // * @return
    // */
    //private SalesOrder getSalesOrderFromShoppingSale(int businessPartnerId) {
    //    Cursor c = null;
    //    try {
    //        c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
    //                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
    //                "SELECT SUB_TOTAL_LINE, TAX_AMOUNT, TOTAL_LINE " +
    //                " FROM ECOMMERCE_SALES_ORDER_LINE " +
    //                " WHERE BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND DOC_TYPE = ? AND IS_ACTIVE = ? ",
    //                new String[]{String.valueOf(businessPartnerId),
    //                        String.valueOf(mUser.getServerUserId()),
    //                        SalesOrderLineDB.SHOPPING_SALE_DOC_TYPE, "Y"}, null);
    //        if(c!=null){
    //            SalesOrder salesOrder = new SalesOrder();
    //            salesOrder.setBusinessPartnerId(businessPartnerId);
    //            while(c.moveToNext()){
    //                salesOrder.setLinesNumber(salesOrder.getLinesNumber() + 1);
    //                salesOrder.setSubTotalAmount(salesOrder.getSubTotalAmount() + c.getDouble(0));
    //                salesOrder.setTaxAmount(salesOrder.getTaxAmount() + c.getDouble(1));
    //                salesOrder.setTotalAmount(salesOrder.getTotalAmount() + c.getDouble(2));
    //            }
    //            return salesOrder;
    //        }
    //    } catch (Exception e) {
    //        e.printStackTrace();
    //    } finally {
    //        if(c!=null){
    //            try {
    //                c.close();
    //            } catch (Exception e){
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    //    return null;
    //}

    public SalesOrder getSalesOrderById(int salesOrderId) {
        Cursor c = null;
        SalesOrder salesOrder = null;
        try {
            if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.CREATE_TIME, SO.LINES_NUMBER, " +
                                " SO.SUB_TOTAL, SO.TAX, SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO, SO.BUSINESS_PARTNER_ADDRESS_ID "+
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN SALES_REP SR ON SR.USER_ID = SO.USER_ID AND SR.IS_ACTIVE = ? " +
                            " INNER JOIN USER_BUSINESS_PARTNERS UBP ON UBP.USER_ID = SR.SALES_REP_ID AND UBP.IS_ACTIVE = ? " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = UBP.BUSINESS_PARTNER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.ECOMMERCE_SALES_ORDER_ID = ? AND SO.USER_ID = ? AND SO.IS_ACTIVE = ?",
                        new String[]{"Y", "Y", "Y", String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.CREATE_TIME, SO.LINES_NUMBER, " +
                                " SO.SUB_TOTAL, SO.TAX, SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO, SO.BUSINESS_PARTNER_ADDRESS_ID "+
                                " FROM ECOMMERCE_SALES_ORDER SO " +
                                " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                                " WHERE SO.ECOMMERCE_SALES_ORDER_ID = ? AND SO.USER_ID = ? AND SO.IS_ACTIVE = ?",
                        new String[]{"Y", String.valueOf(salesOrderId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            }

            if(c!=null && c.moveToNext()){
                salesOrder = new SalesOrder();
                salesOrder.setId(c.getInt(0));
                try{
                    salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(1)).getTime()));
                }catch(ParseException ex){
                    try {
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(c.getString(1)).getTime()));
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
                salesOrder.setBusinessPartnerAddressId(c.getInt(8));
            }
            if(salesOrder!=null){
                if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                    salesOrder.setBusinessPartner((new BusinessPartnerDB(mContext, mUser))
                            .getBusinessPartnerById(salesOrder.getBusinessPartnerId()));
                }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                    salesOrder.setBusinessPartner((new UserBusinessPartnerDB(mContext, mUser))
                            .getUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
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

    public ArrayList<SalesOrder> getShoppingSalesList(){
        ArrayList<SalesOrder> salesOrders = new ArrayList<>();
        Cursor c = null;
        try {
            if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT COUNT(SOL.BUSINESS_PARTNER_ID), SOL.BUSINESS_PARTNER_ID " +
                        " FROM ECOMMERCE_SALES_ORDER_LINE SOL " +
                            " INNER JOIN SALES_REP SR ON SR.USER_ID = SOL.USER_ID AND SR.IS_ACTIVE = ? " +
                            " INNER JOIN USER_BUSINESS_PARTNERS UBP ON UBP.USER_ID = SR.SALES_REP_ID AND UBP.IS_ACTIVE = ? " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = UBP.BUSINESS_PARTNER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SOL.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SOL.BUSINESS_PARTNER_ID = ? AND SOL.USER_ID = ? AND SOL.DOC_TYPE = ? AND SOL.IS_ACTIVE = ? " +
                        " GROUP BY SOL.BUSINESS_PARTNER_ID",
                        new String[]{"Y", "Y", "Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                String.valueOf(mUser.getServerUserId()), SalesOrderLineDB.SHOPPING_SALE_DOC_TYPE, "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT COUNT(SOL.BUSINESS_PARTNER_ID), SOL.BUSINESS_PARTNER_ID " +
                        " FROM ECOMMERCE_SALES_ORDER_LINE SOL " +
                            " INNER JOIN USER_BUSINESS_PARTNER UBP ON UBP.USER_ID = SOL.USER_ID " +
                                " AND UBP.USER_BUSINESS_PARTNER_ID = SOL.BUSINESS_PARTNER_ID AND UBP.IS_ACTIVE = ? " +
                        " WHERE SOL.USER_ID = ? AND SOL.DOC_TYPE = ? AND SOL.IS_ACTIVE = ? " +
                        " GROUP BY SOL.BUSINESS_PARTNER_ID",
                        new String[]{"Y", String.valueOf(mUser.getServerUserId()),
                                SalesOrderLineDB.SHOPPING_SALE_DOC_TYPE, "Y"}, null);
            }

            if(c!=null){
                while(c.moveToNext()){
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setLinesNumber(c.getInt(0));
                    salesOrder.setBusinessPartnerId(c.getInt(1));
                    salesOrders.add(salesOrder);
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
        if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : salesOrders){
                salesOrder.setBusinessPartner(businessPartnerDB.getBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : salesOrders){
                salesOrder.setBusinessPartner(userBusinessPartnerDB.getUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }
        return salesOrders;
    }

    public ArrayList<SalesOrder> getSalesOrderList(){
        ArrayList<SalesOrder> salesOrders = new ArrayList<>();
        Cursor c = null;
        try {
            if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.DOC_STATUS, SO.CREATE_TIME, SO.UPDATE_TIME, " +
                            " SO.APP_VERSION, SO.APP_USER_NAME, SO.LINES_NUMBER, SO.SUB_TOTAL, SO.TAX, " +
                            " SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO, SO.BUSINESS_PARTNER_ADDRESS_ID " +
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN SALES_REP SR ON SR.USER_ID = SO.USER_ID AND SR.IS_ACTIVE = ? " +
                            " INNER JOIN USER_BUSINESS_PARTNERS UBP ON UBP.USER_ID = SR.SALES_REP_ID AND UBP.IS_ACTIVE = ? " +
                            " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = UBP.BUSINESS_PARTNER_ID " +
                                " AND BP.BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.BUSINESS_PARTNER_ID = ? AND SO.USER_ID = ? AND SO.DOC_TYPE = ? AND SO.IS_ACTIVE = ?  " +
                        " order by SO.ECOMMERCE_SALES_ORDER_ID desc",
                        new String[]{"Y", "Y", "Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                                String.valueOf(mUser.getServerUserId()), SalesOrderLineDB.FINALIZED_SALES_ORDER_DOC_TYPE, "Y"}, null);
            }else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                                .build(), null,
                        "SELECT SO.ECOMMERCE_SALES_ORDER_ID, SO.DOC_STATUS, SO.CREATE_TIME, SO.UPDATE_TIME, " +
                            " SO.APP_VERSION, SO.APP_USER_NAME, SO.LINES_NUMBER, SO.SUB_TOTAL, SO.TAX, " +
                            " SO.TOTAL, SO.BUSINESS_PARTNER_ID, SO.VALID_TO, SO.BUSINESS_PARTNER_ADDRESS_ID " +
                        " FROM ECOMMERCE_SALES_ORDER SO " +
                            " INNER JOIN USER_BUSINESS_PARTNER BP ON BP.USER_ID = SO.USER_ID " +
                                " AND BP.USER_BUSINESS_PARTNER_ID = SO.BUSINESS_PARTNER_ID AND BP.IS_ACTIVE = ? " +
                        " WHERE SO.USER_ID = ? AND SO.DOC_TYPE = ? AND SO.IS_ACTIVE = ?  " +
                        " order by SO.ECOMMERCE_SALES_ORDER_ID desc",
                        new String[]{"Y", String.valueOf(mUser.getServerUserId()),
                                SalesOrderLineDB.FINALIZED_SALES_ORDER_DOC_TYPE, "Y"}, null);
            }

            if(c!=null){
                while(c.moveToNext()){
                    SalesOrder salesOrder = new SalesOrder();
                    salesOrder.setId(c.getInt(0));
                    try{
                        salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(2)).getTime()));
                    }catch(ParseException ex){
                        try {
                            salesOrder.setCreated(new Timestamp(new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(c.getString(2)).getTime()));
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
                    salesOrders.add(salesOrder);
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
        if(BuildConfig.IS_SALES_FORCE_SYSTEM || mUser.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
            BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : salesOrders){
                salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineQtyBySalesOrderId(salesOrder.getId(), salesOrder.getBusinessPartnerId()));
                salesOrder.setBusinessPartner(businessPartnerDB.getBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        } else if(mUser.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
            UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(mContext, mUser);
            for(SalesOrder salesOrder : salesOrders){
                salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineQtyBySalesOrderId(salesOrder.getId(), salesOrder.getBusinessPartnerId()));
                salesOrder.setBusinessPartner(userBusinessPartnerDB.getUserBusinessPartnerById(salesOrder.getBusinessPartnerId()));
            }
        }
        return salesOrders;
    }

    public String deactivateSalesOrderById(int salesOrderId) {
        try {
            mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER_LINE SET IS_ACTIVE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ?",
                            new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(salesOrderId),
                                    String.valueOf(mUser.getServerUserId())});
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE ECOMMERCE_SALES_ORDER SET IS_ACTIVE = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                                " WHERE ECOMMERCE_SALES_ORDER_ID = ? AND USER_ID = ?",
                            new String[]{"N", DateFormat.getCurrentDateTimeSQLFormat(), String.valueOf(salesOrderId),
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
}

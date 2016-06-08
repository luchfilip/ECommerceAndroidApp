package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by stein on 5/6/2016.
 */
public class SalesOrderDB {

    public static final String TAG = SalesOrderDB.class.getSimpleName();

    private Context context;
    private User user;

    public SalesOrderDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public String createSalesOrderFromShoppingSale(int businessPartnerId){
        return createOrder(businessPartnerId, (new SalesOrderLineDB(context, user)).getShoppingSale(businessPartnerId), false);
    }

    public SalesOrder getLastFinalizedSalesOrder() {
        return getLastSalesOrderByDocType(SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE);
    }

    public ArrayList<SalesOrder> getActiveShoppingSalesOrders(){
        ArrayList<SalesOrder> activeOrders = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT COUNT(OL.BUSINESS_PARTNER_ID), OL.BUSINESS_PARTNER_ID " +
                    " FROM ECOMMERCE_SALES_ORDERLINE OL " +
                        " INNER JOIN BUSINESS_PARTNER BP ON BP.BUSINESS_PARTNER_ID = OL.BUSINESS_PARTNER_ID AND BP.ISACTIVE = ? " +
                    " WHERE OL.ISACTIVE = ? AND OL.DOC_TYPE = ? " +
                    " GROUP BY OL.BUSINESS_PARTNER_ID";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", SalesOrderLineDB.SHOPPING_SALE_DOCTYPE}, null);
            while(c.moveToNext()){
                SalesOrder salesOrder = new SalesOrder();
                salesOrder.setLinesNumber(c.getInt(0));
                BusinessPartner businessPartner = new BusinessPartner();
                businessPartner.setId(c.getInt(1));
                salesOrder.setBusinessPartner(businessPartner);
                activeOrders.add(salesOrder);
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
        BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(context, user);
        for(SalesOrder salesOrder : activeOrders){
            salesOrder.setBusinessPartner(businessPartnerDB.getBusinessPartnerById(salesOrder.getBusinessPartner().getId()));
        }
        return activeOrders;
    }

    public ArrayList<SalesOrder> getActiveSalesOrders(){
        return getActiveSalesOrders(SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE);
    }

    public String createOrder(int businessPartnerId, ArrayList<SalesOrderLine> orderLines, boolean insertOrderLinesInDB){
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(context, user);
        if(orderLines != null
                && ((salesOrderLineDB.getActiveShoppingSalesLinesNumber(businessPartnerId)>0)
                || insertOrderLinesInDB)){
            Cursor c = null;
            int salesOrderId = 0;
            try {
                double subTotal=0, tax=0, total=0;
                for(SalesOrderLine orderLine : orderLines){
                    subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
                    tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
                    total += subTotal + tax;
                }

                int rowsAffected = context.getContentResolver()
                        .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
                                null,
                                "INSERT INTO ECOMMERCE_SALES_ORDER (BUSINESS_PARTNER_ID, DOC_STATUS, DOC_TYPE, APP_VERSION, " +
                                        " APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL, ISACTIVE) " +
                                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                                new String[]{String.valueOf(businessPartnerId), "CO", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE,
                                        Utils.getAppVersionName(context), user.getUserName(), String.valueOf(orderLines.size()),
                                        String.valueOf(subTotal), String.valueOf(tax), String.valueOf(total), "Y"});
                if(rowsAffected <= 0){
                    return "Error 001 - No se insertó el pedido en la base de datos.";
                }

                String sql = "SELECT MAX(ECOMMERCE_SALES_ORDER_ID) FROM ECOMMERCE_SALES_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?";
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null, sql, new String[]{"Y", SalesOrderLineDB.FINALIZED_SALES_ORDER_DOCTYPE}, null);
                if(c.moveToNext()){
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
                        int rowsAffected = context.getContentResolver()
                                .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId()).build(),
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

    private SalesOrder getLastSalesOrderByDocType(String docType){
        Cursor c = null;
        SalesOrder salesOrder = null;
        try {
            String sql = "SELECT ECOMMERCE_SALES_ORDER_ID, CREATE_TIME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL"+
                    " FROM ECOMMERCE_SALES_ORDER " +
                    " WHERE ECOMMERCE_SALES_ORDER_ID = (SELECT MAX(ECOMMERCE_SALES_ORDER_ID) FROM ECOMMERCE_SALES_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ?)";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
            if(c.moveToNext()){
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

    /**
     *
     * @param docType
     * @return
     */
    private ArrayList<SalesOrder> getActiveSalesOrders(String docType){
        ArrayList<SalesOrder> activeSalesOrders = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT ECOMMERCE_SALES_ORDER_ID, DOC_STATUS, CREATE_TIME, UPDATE_TIME, " +
                    " APP_VERSION, APP_USER_NAME, LINES_NUMBER, SUB_TOTAL, TAX, TOTAL " +
                    " FROM ECOMMERCE_SALES_ORDER WHERE ISACTIVE = ? AND DOC_TYPE = ? order by ECOMMERCE_SALES_ORDER_ID desc";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", docType}, null);
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
                activeSalesOrders.add(salesOrder);
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
        SalesOrderLineDB salesOrderLineDB = new SalesOrderLineDB(context, user);
        for(SalesOrder salesOrder : activeSalesOrders){
            salesOrder.setLinesNumber(salesOrderLineDB.getOrderLineNumbersBySalesOrderId(salesOrder.getId()));
        }
        return activeSalesOrders;
    }
}
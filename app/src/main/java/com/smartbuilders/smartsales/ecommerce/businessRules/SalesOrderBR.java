package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.SynchronizerContentProvider;

import org.codehaus.jettison.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stein on 13/6/2016.
 */
public class SalesOrderBR {

    public static double getSubTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double subTotal=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            subTotal += salesOrderLine.getQuantityOrdered() * salesOrderLine.getPrice();
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(salesOrderLines));
    }

    public static double getTaxAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double tax=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            tax += salesOrderLine.getQuantityOrdered() * salesOrderLine.getPrice() *
                    (salesOrderLine.getTaxPercentage()/100);
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(salesOrderLines));
    }

    public static double getTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double total = getSubTotalAmount(salesOrderLines) + getTaxAmount(salesOrderLines);
        return new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalAmount(salesOrderLines));
    }

    public static String createSalesOrderFromShoppingSale(Context context, User user, int businessPartnerId, Date validTo) {
        String result;
        try {
            result = new SalesOrderDB(context, user)
                    .createSalesOrderFromShoppingSale(businessPartnerId, validTo);
            syncDataWithServer(context, user.getUserId());
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    public static String deactiveSalesOrderById(Context context, User user, int salesOrderId) {
        String result;
        try {
            result = (new SalesOrderDB(context, user)).deactiveSalesOrderById(salesOrderId);
            syncDataWithServer(context, user.getUserId());
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    private static void syncDataWithServer(Context context, String userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("1", "ECOMMERCE_SALES_ORDER_LINE");
            jsonObject.put("2", "ECOMMERCE_SALES_ORDER");
            context.getContentResolver().query(SynchronizerContentProvider.SYNC_DATA_TO_SERVER_URI.buildUpon()
                            .appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, userId)
                            .appendQueryParameter(SynchronizerContentProvider.KEY_TABLES_TO_SYNC, jsonObject.toString()).build(),
                    null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

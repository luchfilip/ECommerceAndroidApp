package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.synchronizer.ids.providers.SynchronizerContentProvider;

import org.codehaus.jettison.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by stein on 18/7/2016.
 */
public class OrderBR {

    public static double getSubTotalAmount(ArrayList<OrderLine> orderLines){
        double subTotal=0;
        for(OrderLine orderLine : orderLines){
            subTotal += orderLine.getSubTotalLineAmount();
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(orderLines));
    }

    public static double getTaxAmount(ArrayList<OrderLine> orderLines){
        double tax=0;
        for(OrderLine orderLine : orderLines){
            tax += orderLine.getLineTaxAmount();
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(orderLines));
    }

    public static double getTotalAmount(ArrayList<OrderLine> orderLines){
        double total=0;
        for(OrderLine orderLine : orderLines){
            total += orderLine.getTotalLineAmount();
        }
        return new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTotalAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalAmount(orderLines));
    }

    public static void validateQuantityOrderedInOrderLines(Context context, User user, ArrayList<OrderLine> orderLines) {
        if (orderLines!=null && !orderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (OrderLine orderLine : orderLines) {
                Product product = productDB.getProductById(orderLine.getProductId());
                //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                //pedida no es multiplo del empaque comercial del producto entonces se marca que
                //esa linea del pedido tiene error en la cantidad pedida.
                orderLine.setQuantityOrderedInvalid((orderLine.getQuantityOrdered() > product.getDefaultProductPriceAvailability().getAvailability())
                        || (orderLine.getQuantityOrdered()%product.getProductCommercialPackage().getUnits()!=0));
            }
        }
    }

    public static String isValidQuantityOrderedInOrderLines(Context context, User user, ArrayList<OrderLine> orderLines) {
        if (orderLines!=null && !orderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (OrderLine orderLine : orderLines) {
                Product product = productDB.getProductById(orderLine.getProductId());
                //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                //pedida no es multiplo del empaque comercial del producto entonces se marca que
                //esa linea del pedido tiene error en la cantidad pedida.
                if ((orderLine.getQuantityOrdered() > product.getDefaultProductPriceAvailability().getAvailability())
                        || (orderLine.getQuantityOrdered()%product.getProductCommercialPackage().getUnits()!=0)) {
                    return "Error en cantidad pedida del articulo "+product.getName();
                }
            }
        }
        return null;
    }

    public static String createOrderFromShoppingCart(Context context, User user, int businessPartnerAddressId) {
        String result;
        try {
            result = (new OrderDB(context, user)).createOrderFromShoppingCart(businessPartnerAddressId);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        if (result==null) {
            syncDataWithServer(context, user.getUserId());
        }
        return result;
    }

    public static String createOrderFromOrderLines(Context context, User user, int salesOrderId,
                                                   int businessPartnerAddressId, ArrayList<OrderLine> orderLines){
        String result;
        try {
            result = (new OrderDB(context, user)).createOrderFromOrderLines(salesOrderId, businessPartnerAddressId, orderLines);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        if (result==null) {
            syncDataWithServer(context, user.getUserId());
        }
        return result;
    }

    private static void syncDataWithServer(Context context, String userId) {
        //TODO: MANJEAR QUE PASA SI NO SE REALIZA LA CONEXION EN EL MOMENTO
        Cursor cursor = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("1", "ECOMMERCE_ORDER_LINE");
            jsonObject.put("2", "ECOMMERCE_ORDER");
            cursor = context.getContentResolver().query(SynchronizerContentProvider.SYNC_DATA_TO_SERVER_URI.buildUpon()
                            .appendQueryParameter(SynchronizerContentProvider.KEY_USER_ID, userId)
                            .appendQueryParameter(SynchronizerContentProvider.KEY_TABLES_TO_SYNC, jsonObject.toString()).build(),
                    null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor!=null) {
                cursor.close();
            }
        }
    }
}

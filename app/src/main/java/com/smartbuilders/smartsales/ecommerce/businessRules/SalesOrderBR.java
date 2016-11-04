package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

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
            subTotal += salesOrderLine.getSubTotalLineAmount();
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(salesOrderLines));
    }

    public static double getTaxAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double tax=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            tax += salesOrderLine.getLineTaxAmount();
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(salesOrderLines));
    }

    public static double getTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double total=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            total += salesOrderLine.getTotalLineAmount();
        }
        return new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalAmount(salesOrderLines));
    }

    public static String createSalesOrderFromShoppingSale(Context context, User user,
                                                          Date validTo, int businessPartnerAddressId){
        try {
            return createSalesOrderFromShoppingSale(context, user,
                    Utils.getAppCurrentBusinessPartnerId(context, user), validTo, businessPartnerAddressId);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String createSalesOrderFromShoppingSale(Context context, User user, Integer businessPartnerId,
                                                          Date validTo, int businessPartnerAddressId){
        String result;
        try {
            result = new SalesOrderDB(context, user)
                    .createSalesOrderFromShoppingSale(businessPartnerId, validTo, businessPartnerAddressId);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    public static String deactiveSalesOrderById(Context context, User user, int salesOrderId) {
        String result;
        try {
            result = (new SalesOrderDB(context, user)).deactiveSalesOrderById(salesOrderId);
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }

    public static void validateQuantityOrderedInSalesOrderLines(Context context, User user, ArrayList<SalesOrderLine> salesOrderLines) {
        if (salesOrderLines!=null && !salesOrderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (SalesOrderLine salesOrderLine : salesOrderLines) {
                Product product = productDB.getProductById(salesOrderLine.getProductId());
                //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                //pedida no es multiplo del empaque comercial del producto entonces se marca que
                //esa linea del pedido tiene error en la cantidad pedida.
                salesOrderLine.setQuantityOrderedInvalid((salesOrderLine.getQuantityOrdered() > product.getDefaultProductPriceAvailability().getAvailability())
                        || (salesOrderLine.getQuantityOrdered()%product.getProductCommercialPackage().getUnits()!=0));
            }
        }
    }

    public static String isValidQuantityOrderedInSalesOrderLines(Context context, User user, ArrayList<SalesOrderLine> salesOrderLines) {
        if (salesOrderLines!=null && !salesOrderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (SalesOrderLine salesOrderLine : salesOrderLines) {
                Product product = productDB.getProductById(salesOrderLine.getProductId());
                //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                //pedida no es multiplo del empaque comercial del producto entonces se marca que
                //esa linea del pedido tiene error en la cantidad pedida.
                if ((salesOrderLine.getQuantityOrdered() > product.getDefaultProductPriceAvailability().getAvailability())
                        || (salesOrderLine.getQuantityOrdered()%product.getProductCommercialPackage().getUnits()!=0)) {
                    return "Error en cantidad pedida del articulo "+product.getName();
                }
            }
        }
        return null;
    }
}

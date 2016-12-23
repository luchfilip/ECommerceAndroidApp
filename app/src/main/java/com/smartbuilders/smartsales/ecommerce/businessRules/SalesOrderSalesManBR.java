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
public class SalesOrderSalesManBR {

    public static double getSubTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double subTotal=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            subTotal += SalesOrderLineBR.getSubTotalLine(salesOrderLine, salesOrderLine.getProduct());
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(salesOrderLines));
    }

    public static double getTaxAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double tax=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            tax += SalesOrderLineBR.getTaxAmount(salesOrderLine, salesOrderLine.getProduct());
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(salesOrderLines));
    }

    public static double getTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double total=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            total += SalesOrderLineBR.getTotalLine(salesOrderLine, salesOrderLine.getProduct());
        }
        return new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTotalAmountStringFormat(ArrayList<SalesOrderLine> salesOrderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTotalAmount(salesOrderLines));
    }

    public static String createSalesOrderFromShoppingSale(Context context, User user,
                                                          Date validTo, int businessPartnerAddressId, ArrayList<SalesOrderLine> salesOrderLines){
        try {
            return createSalesOrderFromShoppingSale(context, user,
                    Utils.getAppCurrentBusinessPartnerId(context, user), validTo, businessPartnerAddressId, salesOrderLines);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String createSalesOrderFromShoppingSale(Context context, User user, Integer businessPartnerId,
                                                          Date validTo, int businessPartnerAddressId, ArrayList<SalesOrderLine> salesOrderLines){
        String result;
        try {
            result = new SalesOrderDB(context, user)
                    .createSalesOrderFromShoppingSale(businessPartnerId, validTo, businessPartnerAddressId,
                            getSubTotalAmount(salesOrderLines),
                            getTaxAmount(salesOrderLines),
                            getTotalAmount(salesOrderLines),
                            salesOrderLines);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    public static String deactivateSalesOrderById(Context context, User user, int salesOrderId) {
        String result;
        try {
            result = (new SalesOrderDB(context, user)).deactivateSalesOrderById(salesOrderId);
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
                if (product!=null) {
                    //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                    //pedida no es multiplo del empaque comercial del producto entonces se marca que
                    //esa linea del pedido tiene error en la cantidad pedida.
                    salesOrderLine.setQuantityOrderedInvalid((salesOrderLine.getQuantityOrdered() > product.getProductPriceAvailability().getAvailability())
                            || (salesOrderLine.getQuantityOrdered() % product.getProductCommercialPackage().getUnits() != 0));
                } else {
                    salesOrderLine.setQuantityOrderedInvalid(true);
                }
            }
        }
    }

    public static String isValidQuantityOrderedInSalesOrderLines(Context context, User user, ArrayList<SalesOrderLine> salesOrderLines) {
        if (salesOrderLines!=null && !salesOrderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (SalesOrderLine salesOrderLine : salesOrderLines) {
                Product product = productDB.getProductById(salesOrderLine.getProductId());
                if (product!=null) {
                    //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                    //pedida no es multiplo del empaque comercial del producto entonces se marca que
                    //esa linea del pedido tiene error en la cantidad pedida.
                    if ((salesOrderLine.getQuantityOrdered() > product.getProductPriceAvailability().getAvailability())
                            || (salesOrderLine.getQuantityOrdered() % product.getProductCommercialPackage().getUnits() != 0)) {
                        return "Error en cantidad pedida del articulo " + product.getName();
                    }
                } else {
                    if (salesOrderLine.getProduct()!=null) {
                        return "Error en cantidad pedida del artículo " + salesOrderLine.getProduct().getName();
                    }
                    return "Error en cantidad pedida.";
                }
            }
        }
        return null;
    }
}

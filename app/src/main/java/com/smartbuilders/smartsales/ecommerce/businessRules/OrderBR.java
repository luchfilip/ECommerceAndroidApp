package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.ProductDB;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;

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
            //subTotal += orderLine.getSubTotalLineAmount();
            subTotal += OrderLineBR.getSubTotalLine(orderLine, orderLine.getProduct());
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(orderLines));
    }

    public static double getTaxAmount(ArrayList<OrderLine> orderLines){
        double tax=0;
        for(OrderLine orderLine : orderLines){
            //tax += orderLine.getLineTaxAmount();
            tax += OrderLineBR.getTaxAmount(orderLine, orderLine.getProduct());
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(orderLines));
    }

    public static double getTotalAmount(ArrayList<OrderLine> orderLines){
        double total=0;
        for(OrderLine orderLine : orderLines){
            //total += orderLine.getTotalLineAmount();
            total += OrderLineBR.getTotalLine(orderLine, orderLine.getProduct());
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
                if (product!=null) {
                    //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                    //pedida no es multiplo del empaque comercial del producto entonces se marca que
                    //esa linea del pedido tiene error en la cantidad pedida.
                    orderLine.setQuantityOrderedInvalid((orderLine.getQuantityOrdered() > product.getProductPriceAvailability().getAvailability())
                            || (orderLine.getQuantityOrdered() % product.getProductCommercialPackage().getUnits() != 0));
                } else {
                    orderLine.setQuantityOrderedInvalid(true);
                }
            }
        }
    }

    public static String isValidQuantityOrderedInOrderLines(Context context, User user, ArrayList<OrderLine> orderLines) {
        if (orderLines!=null && !orderLines.isEmpty()) {
            ProductDB productDB = new ProductDB(context, user);
            for (OrderLine orderLine : orderLines) {
                Product product = productDB.getProductById(orderLine.getProductId());
                if (product!=null) {
                    //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
                    //pedida no es multiplo del empaque comercial del producto entonces se marca que
                    //esa linea del pedido tiene error en la cantidad pedida.
                    if ((orderLine.getQuantityOrdered() > product.getProductPriceAvailability().getAvailability())
                            || (orderLine.getQuantityOrdered() % product.getProductCommercialPackage().getUnits() != 0)) {
                        return "Error en cantidad pedida del artículo " + product.getName();
                    }
                } else {
                    if (orderLine.getProduct()!=null) {
                        return "Error en cantidad pedida del artículo " + orderLine.getProduct().getName();
                    }
                    return "Error en cantidad pedida.";
                }
            }
        }
        return null;
    }

    public static String createOrderFromOrderLines(Context context, User user, int salesOrderId,
                                                   int businessPartnerAddressId, ArrayList<OrderLine> orderLines, boolean insertOrderLinesInDB){
        String result;
        try {
            result = (new OrderDB(context, user)).createOrderFromOrderLines(salesOrderId,
                    businessPartnerAddressId, orderLines, insertOrderLinesInDB);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;
    }

    public static String deactivateOrderById(Context context, User user, int orderId) {
        String result;
        try {
            result = (new OrderDB(context, user)).deactivateOrderById(orderId);
        } catch (Exception e) {
            result = e.getMessage();
            e.printStackTrace();
        }
        return result;
    }
}

package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.ids.model.User;
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
            subTotal += orderLine.getQuantityOrdered() * orderLine.getPrice();
        }
        return new BigDecimal(subTotal).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getSubTotalAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getSubTotalAmount(orderLines));
    }

    public static double getTaxAmount(ArrayList<OrderLine> orderLines){
        double tax=0;
        for(OrderLine orderLine : orderLines){
            tax += orderLine.getQuantityOrdered() * orderLine.getPrice() * (orderLine.getTaxPercentage()/100);
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getTaxAmountStringFormat(ArrayList<OrderLine> orderLines){
        return String.format(new Locale("es", "VE"), "%,.2f", getTaxAmount(orderLines));
    }

    public static double getTotalAmount(ArrayList<OrderLine> orderLines){
        double total = getSubTotalAmount(orderLines) + getTaxAmount(orderLines);
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
}

package com.smartbuilders.smartsales.ecommerceandroidapp.businessRules;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

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
            tax += orderLine.getQuantityOrdered() * orderLine.getPrice() *
                    (orderLine.getProduct().getProductTax().getPercentage()/100);
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
}

package com.smartbuilders.smartsales.ecommerce.businessRules;

import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;

import java.math.BigDecimal;
import java.util.ArrayList;
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
}

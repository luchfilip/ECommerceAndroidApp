package com.smartbuilders.smartsales.ecommerceandroidapp.businessRules;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrderLine;

import java.math.BigDecimal;
import java.util.ArrayList;

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

    public static double getTaxAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double tax=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            tax += salesOrderLine.getQuantityOrdered() * salesOrderLine.getPrice() *
                    (salesOrderLine.getTaxPercentage()/100);
        }
        return new BigDecimal(tax).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double getTotalAmount(ArrayList<SalesOrderLine> salesOrderLines){
        double subTotal=0, tax=0, total=0;
        for(SalesOrderLine salesOrderLine : salesOrderLines){
            subTotal += salesOrderLine.getQuantityOrdered() * salesOrderLine.getPrice();
            tax += salesOrderLine.getQuantityOrdered() * salesOrderLine.getPrice() *
                    (salesOrderLine.getTaxPercentage()/100);
            total += subTotal + tax;
        }
        return new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}

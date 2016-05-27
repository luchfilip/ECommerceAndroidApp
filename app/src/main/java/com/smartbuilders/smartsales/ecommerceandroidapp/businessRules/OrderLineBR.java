package com.smartbuilders.smartsales.ecommerceandroidapp.businessRules;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;

/**
 * Created by stein on 27/5/2016.
 */
public class OrderLineBR {

    public static double getTotalLine(OrderLine orderLine){
        double totalLine = 0;
        try {
            totalLine = (orderLine.getPrice() * orderLine.getQuantityOrdered())
                    + (orderLine.getPrice() * orderLine.getQuantityOrdered() * (orderLine.getTaxPercentage()/100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalLine;
    }
}

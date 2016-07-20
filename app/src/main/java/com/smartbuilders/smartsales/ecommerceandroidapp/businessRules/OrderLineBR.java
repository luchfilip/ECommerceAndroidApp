package com.smartbuilders.smartsales.ecommerceandroidapp.businessRules;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.OrderLine;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

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

    public static void validateQuantityOrdered(OrderLine orderLine, Context context, User user) {
        if (orderLine!=null) {
            Product product = (new ProductDB(context, user)).getProductById(orderLine.getProductId());
            //si la cantidad pedida es mayor a la cantidad disponible del producto o si la cantidad
            //pedida no es multiplo del empaque comercial del producto entonces se marca que
            //esa linea del pedido tiene error en la cantidad pedida.
            orderLine.setQuantityOrderedInvalid((orderLine.getQuantityOrdered() > product.getDefaultProductPriceAvailability().getAvailability())
                || (orderLine.getQuantityOrdered()%product.getProductCommercialPackage().getUnits()!=0));
        }
    }
}

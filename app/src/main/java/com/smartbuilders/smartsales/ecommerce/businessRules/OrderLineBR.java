package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.OrderLine;
import com.smartbuilders.smartsales.ecommerce.model.Product;

/**
 * Created by stein on 27/5/2016.
 */
public class OrderLineBR {

    /**
     * Total de impuestos de la linea
     * @param orderLine
     * @return
     */
    private static double getTaxAmount(OrderLine orderLine, Product product){
        double taxAmount = 0;
        try {
            taxAmount = product.getDefaultProductPriceAvailability().getTax() * orderLine.getQuantityOrdered();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taxAmount;
    }

    /**
     * Total de la linea sin impuestos
     * @param orderLine
     * @return
     */
    private static double getSubTotalLine(OrderLine orderLine, Product product){
        double subTotalLine = 0;
        try {
            subTotalLine = product.getDefaultProductPriceAvailability().getPrice() * orderLine.getQuantityOrdered();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subTotalLine;
    }

    /**
     * Total de la linea con impuestos
     * @param orderLine
     * @return
     */
    private static double getTotalLine(OrderLine orderLine, Product product){
        double totalLine = 0;
        try {
            totalLine = product.getDefaultProductPriceAvailability().getTotalPrice() * orderLine.getQuantityOrdered();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalLine;
    }

    public static void validateQtyOrdered(Context context, int qtyOrdered, Product product) throws Exception {
        if (qtyOrdered<=0) {
            throw new Exception(context.getString(R.string.invalid_qty_requested));
        }
        if ((qtyOrdered % product.getProductCommercialPackage().getUnits())!=0) {
            throw new Exception(context.getString(R.string.invalid_commercial_package_qty_requested));
        }
        if (qtyOrdered > product.getDefaultProductPriceAvailability().getAvailability()) {
            throw new Exception(context.getString(R.string.invalid_availability_qty_requested));
        }
    }

    public static void fillOrderLine (int qtyOrdered, Product product, OrderLine orderLine) {
        orderLine.setProductId(product.getId());
        orderLine.setProduct(product);
        orderLine.setQuantityOrdered(qtyOrdered);
        orderLine.setProductPrice(product.getDefaultProductPriceAvailability().getPrice());
        orderLine.setProductTaxPercentage(product.getProductTax().getPercentage());
        orderLine.setLineTaxAmount(getTaxAmount(orderLine, product));
        orderLine.setSubTotalLineAmount(getSubTotalLine(orderLine, product));
        orderLine.setTotalLineAmount(getTotalLine(orderLine, product));
    }

}

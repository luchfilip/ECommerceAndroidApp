package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrderLine;

/**
 * Created by stein on 27/5/2016.
 */
public class SalesOrderLineBR {

    /**
     * Total de impuestos de la linea
     * @param salesOrderLine
     * @return
     */
    private static double getTaxAmount(SalesOrderLine salesOrderLine, Product product){
        double taxAmount = 0;
        try {
            taxAmount = product.getProductPriceAvailability().getTax() * salesOrderLine.getQuantityOrdered();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taxAmount;
    }

    /**
     * Total de la linea sin impuestos
     * @param salesOrderLine
     * @return
     */
    private static double getSubTotalLine(SalesOrderLine salesOrderLine, Product product){
        double subTotalLine = 0;
        try {
            subTotalLine = product.getProductPriceAvailability().getPrice() * salesOrderLine.getQuantityOrdered();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return subTotalLine;
    }

    /**
     * Total de la linea con impuestos
     * @param salesOrderLine
     * @return
     */
    private static double getTotalLine(SalesOrderLine salesOrderLine, Product product){
        double totalLine = 0;
        try {
            totalLine = product.getProductPriceAvailability().getTotalPrice() * salesOrderLine.getQuantityOrdered();
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
        if (qtyOrdered > product.getProductPriceAvailability().getAvailability()) {
            throw new Exception(context.getString(R.string.invalid_availability_qty_requested));
        }
    }

    public static void fillSalesOrderLine (int qtyOrdered, Product product, SalesOrderLine salesOrderLine) throws Exception {
        salesOrderLine.setProductId(product.getId());
        salesOrderLine.setProduct(product);
        salesOrderLine.setQuantityOrdered(qtyOrdered);
        salesOrderLine.setProductPrice(product.getProductPriceAvailability().getPrice());
        salesOrderLine.setProductTaxPercentage(product.getProductTax().getPercentage());
        salesOrderLine.setLineTaxAmount(getTaxAmount(salesOrderLine, product));
        salesOrderLine.setSubTotalLineAmount(getSubTotalLine(salesOrderLine, product));
        salesOrderLine.setTotalLineAmount(getTotalLine(salesOrderLine, product));
    }

}

package com.smartbuilders.smartsales.ecommerceandroidapp.model;

/**
 * Created by Alberto on 7/4/2016.
 */
public class InvoiceLine extends Model {

    private Product product;
    private int quantityInvoiced;
    private double price;
    private Currency currency;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantityInvoiced() {
        return quantityInvoiced;
    }

    public void setQuantityInvoiced(int quantityInvoiced) {
        this.quantityInvoiced = quantityInvoiced;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

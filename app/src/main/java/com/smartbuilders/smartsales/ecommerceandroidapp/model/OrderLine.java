package com.smartbuilders.smartsales.ecommerceandroidapp.model;

/**
 * Created by Alberto on 5/4/2016.
 */
public class OrderLine extends Model {

    private Product product;
    private int quantityOrdered;
    private double price;
    private Currency currency;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}

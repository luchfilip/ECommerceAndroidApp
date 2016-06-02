package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import java.util.ArrayList;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandsPromotionSection {

    private String name;
    private ArrayList<ProductBrand> productBrands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ProductBrand> getProductBrands() {
        return productBrands;
    }

    public void setProductBrands(ArrayList<ProductBrand> productBrands) {
        this.productBrands = productBrands;
    }
}

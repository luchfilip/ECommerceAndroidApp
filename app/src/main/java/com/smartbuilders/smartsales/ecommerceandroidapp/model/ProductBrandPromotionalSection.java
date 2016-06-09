package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import java.util.ArrayList;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionalSection {

    private ArrayList<ProductBrandPromotionalCard> productBrandPromotionalCards;

    public ProductBrandPromotionalSection(){
        productBrandPromotionalCards = new ArrayList<>();
    }

    public ArrayList<ProductBrandPromotionalCard> getProductBrandPromotionalCards() {
        return productBrandPromotionalCards;
    }

    public void setProductBrandPromotionalCards(ArrayList<ProductBrandPromotionalCard> productBrandPromotionalCards) {
        this.productBrandPromotionalCards = productBrandPromotionalCards;
    }
}

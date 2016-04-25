package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class UtilsMainPageProduct {

    private ArrayList<String> insert;

    public ArrayList<String> getInserts(){
        return insert;
    }

    public UtilsMainPageProduct(){
        insert = new ArrayList<>();
        insert.add("insert into MAINPAGE_PRODUCT (MAINPAGE_PRODUCT_ID, MAINPAGE_SECTION_ID, PRODUCT_ID, ISACTIVE) values (1, 1, 1, 'Y')");
    }
}

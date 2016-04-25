package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class UtilsMainPageSection {

    private ArrayList<String> insert;

    public ArrayList<String> getInserts(){
        return insert;
    }

    public UtilsMainPageSection(){
        insert = new ArrayList<>();
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, ISACTIVE) VALUES (1, '', '', 'Y') )");
    }
}

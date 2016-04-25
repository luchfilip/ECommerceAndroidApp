package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;

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
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (1, 'Productos Recientes', 'Productos Recientes', 1, 'Y')");
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (2, 'Destacados', 'Destacados', 2, 'Y')");
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (3, 'Ofertas', 'Ofertas', 3, 'Y')");
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (4, 'Lo más vendido', 'Lo más vendido', 4, 'Y')");
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (5, 'Recomendados', 'Recomendados', 5, 'Y')");
        insert.add("insert into MAINPAGE_SECTION (MAINPAGE_SECTION_ID, NAME, DESCRIPTION, PRIORITY, ISACTIVE) VALUES (6, 'Promociones', 'Promociones', 6, 'Y')");
    }
}

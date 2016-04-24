package com.smartbuilders.smartsales.ecommerceandroidapp.utils;

import java.util.ArrayList;

/**
 * Created by stein on 4/23/2016.
 */
public class UtilsCategory {

    private ArrayList<String> insert;

    public ArrayList<String> getInserts(){
        return insert;
    }

    public UtilsCategory(){
        insert = new ArrayList<>();
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000239, '0', 'HERRAMIENTAS MANUALES Y ELECTRICAS', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000240, '1', 'FERRETERIA AGRICOLA', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000241, '2', 'RECUBRIMIENTOS, ADHESIVOS Y ABRASIVOS', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000242, '3', 'SEGURIDAD INDUSTRIAL Y AUTOMOTRIZ', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000243, '5', 'SANITARIOS', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000244, '6', 'CERRADURAS Y CANDADOS', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000245, '7', 'ELECTRICIDAD E ILUMINACION', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000246, '8', 'GRIFERIAS Y VALVULAS', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000247, '4', 'MALLAS Y ALAMBRES', 'Y')");
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000248, '9', 'ARTICULOS PESADOS Y CONSTRUCCION', 'Y')");
    }
}

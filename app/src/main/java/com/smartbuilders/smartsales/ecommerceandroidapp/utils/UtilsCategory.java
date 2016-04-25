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
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000239, 'Grupo 0', 'HERRAMIENTAS MANUALES Y ELECTRICAS', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000240, 'Grupo 1', 'FERRETERIA AGRICOLA', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000241, 'Grupo 2', 'RECUBRIMIENTOS, ADHESIVOS Y ABRASIVOS', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000242, 'Grupo 3', 'SEGURIDAD INDUSTRIAL Y AUTOMOTRIZ', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000243, 'Grupo 5', 'SANITARIOS', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000244, 'Grupo 6', 'CERRADURAS Y CANDADOS', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000245, 'Grupo 7', 'ELECTRICIDAD E ILUMINACION', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000246, 'Grupo 8', 'GRIFERIAS Y VALVULAS', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000247, 'Grupo 4', 'MALLAS Y ALAMBRES', 'Y')".replaceAll("\\s+", " ").trim());
        insert.add("insert into CATEGORY (CATEGORY_ID, NAME, DESCRIPTION, ISACTIVE) values (1000248, 'Grupo 9', 'ARTICULOS PESADOS Y CONSTRUCCION', 'Y')".replaceAll("\\s+", " ").trim());
    }
}

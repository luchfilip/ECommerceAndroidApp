package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 22/4/2016.
 */
public class ProductDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;


    public ProductDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<Product> getProductsBySubCategoryId(int subCategoryId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IDARTICULO, IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, USO, " +
                " OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, UNIDADVENTA_COMERCIAL, " +
                " EMPAQUE_COMERCIAL " +
                " FROM ARTICULOS " +
                " WHERE IDPARTIDA = "+subCategoryId, null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByBrandId(int brandId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IDARTICULO, IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, USO, " +
                " OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, UNIDADVENTA_COMERCIAL, " +
                " EMPAQUE_COMERCIAL " +
                " FROM ARTICULOS " +
                " WHERE IDMARCA = "+brandId, null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IDARTICULO, IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, USO, " +
                " OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, UNIDADVENTA_COMERCIAL, " +
                " EMPAQUE_COMERCIAL " +
                " FROM ARTICULOS " +
                " WHERE NOMBRE LIKE '"+name+"%'", null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            products.add(p);
        }
        return products;
    }

    public Product getProductById(int id){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT IDPARTIDA, IDMARCA, NOMBRE, DESCRIPCION, USO, " +
                " OBSERVACIONES, IDREFERENCIA, NACIONALIDAD, CODVIEJO, UNIDADVENTA_COMERCIAL, " +
                " EMPAQUE_COMERCIAL " +
                " FROM ARTICULOS " +
                " WHERE IDARTICULO = "+id, null);
        if(c.moveToNext()){
            Product p = new Product();
            p.setId(id);
            p.setName(c.getString(2));
            p.setDescription(c.getString(3));
            p.setInternalCode(c.getString(8));
            return p;
        }
        return null;
    }
}

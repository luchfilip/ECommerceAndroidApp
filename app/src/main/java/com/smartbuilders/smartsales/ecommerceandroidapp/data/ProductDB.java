package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCommercialPackage;

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
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " WHERE IDPARTIDA = "+subCategoryId, null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByCategoryId(int categoryId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                " WHERE S.CATEGORY_ID = "+categoryId, null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByBrandId(int brandId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                " WHERE A.IDMARCA = "+brandId, null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " WHERE NOMBRE LIKE '"+name+"%'", null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setInternalCode(c.getString(9));
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            products.add(p);
        }
        return products;
    }

    public Product getProductById(int id){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION " +
                " FROM ARTICULOS A " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " WHERE IDARTICULO = "+id, null);
        if(c.moveToNext()){
            Product p = new Product();
            p.setId(id);
            p.setName(c.getString(2));
            p.setDescription(c.getString(3));
            p.setInternalCode(c.getString(8));
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(9), c.getString(10)));
            p.setProductBrand(new ProductBrand(c.getInt(1), c.getString(11), c.getString(12)));
            return p;
        }
        return null;
    }
}

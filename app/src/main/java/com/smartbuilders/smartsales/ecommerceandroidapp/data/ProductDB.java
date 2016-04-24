package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCommercialPackage;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

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
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION " +
                " FROM ARTICULOS A " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                    " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                    " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                " WHERE IDPARTIDA = "+subCategoryId + " ORDER BY A.NOMBRE ASC ", null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setImageFileName(c.getString(9)+".png");
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
            p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByCategoryId(int categoryId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                " WHERE S.CATEGORY_ID = "+categoryId + " ORDER BY A.NOMBRE ASC", null);

        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setImageFileName(c.getString(9)+".png");
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
            p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByBrandId(int brandId){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                " WHERE A.IDMARCA = "+brandId+" ORDER BY A.NOMBRE ASC", null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setImageFileName(c.getString(9)+".png");
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
            p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
            products.add(p);
        }
        return products;
    }

    public ArrayList<Product> getProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                " WHERE NOMBRE LIKE '"+name+"%' ORDER BY A.NOMBRE ASC", null);
        while(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setImageFileName(c.getString(9)+".png");
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
            p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
            products.add(p);
        }
        return products;
    }

    public Product getProductById(int id){
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION " +
                " FROM ARTICULOS A " +
                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                " WHERE IDARTICULO = "+id+" ORDER BY A.NOMBRE ASC", null);
        if(c.moveToNext()){
            Product p = new Product();
            p.setId(c.getInt(0));
            p.setName(c.getString(3));
            p.setDescription(c.getString(4));
            p.setImageFileName(c.getString(9)+".png");
            p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
            p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
            p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
            p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
            return p;
        }
        return null;
    }
}

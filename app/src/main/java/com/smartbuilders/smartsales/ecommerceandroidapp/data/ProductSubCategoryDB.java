package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductSubCategoryDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public ProductSubCategoryDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategories(){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT S.SUBCATEGORY_ID, S.CATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.SUBCATEGORY_ID) " +
                " FROM SUBCATEGORY S " +
                    " INNER JOIN ARTICULOS A ON A.IDPARTIDA = S.SUBCATEGORY_ID AND A.ACTIVO = 'V' " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                    " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' AND PA.AVAILABILITY>0 " +
                " WHERE S.ISACTIVE = 'Y' " +
                " GROUP BY S.SUBCATEGORY_ID, S.CATEGORY_ID, S.NAME, S.DESCRIPTION " +
                " ORDER BY S.NAME ASC", null);
        while(c.moveToNext()){
            ProductSubCategory productSubCategory = new ProductSubCategory();
            productSubCategory.setId(c.getInt(1));
            productSubCategory.setProductCategoryId(c.getInt(0));
            productSubCategory.setName(c.getString(2).replaceAll("\\s+", " ").trim());
            productSubCategory.setDescription(c.getString(3).replaceAll("\\s+", " ").trim());
            productSubCategory.setProductsActiveQty(c.getInt(4));
            categories.add(productSubCategory);
        }
        return categories;
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategoriesByCategoryId(int categoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.SUBCATEGORY_ID) " +
                " FROM SUBCATEGORY S " +
                    " INNER JOIN ARTICULOS A ON A.IDPARTIDA = S.SUBCATEGORY_ID AND A.ACTIVO = 'V' " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                    " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' AND PA.AVAILABILITY>0 " +
                " WHERE S.ISACTIVE = 'Y' AND S.CATEGORY_ID ="+categoryId +
                " GROUP BY S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION " +
                " ORDER BY S.NAME ASC", null);
        while(c.moveToNext()){
            ProductSubCategory productSubCategory = new ProductSubCategory();
            productSubCategory.setId(c.getInt(0));
            productSubCategory.setProductCategoryId(categoryId);
            productSubCategory.setName(c.getString(1).replaceAll("\\s+", " ").trim());
            productSubCategory.setDescription(c.getString(2).replaceAll("\\s+", " ").trim());
            productSubCategory.setProductsActiveQty(c.getInt(3));
            categories.add(productSubCategory);
        }
        return categories;
    }

    public ProductSubCategory getProductSubCategoryById(int subCategoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT S.CATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.CATEGORY_ID) " +
                " FROM SUBCATEGORY S " +
                    " INNER JOIN ARTICULOS A ON A.IDPARTIDA = S.SUBCATEGORY_ID AND A.ACTIVO = 'V' " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                    " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' AND PA.AVAILABILITY>0 " +
                " WHERE S.ISACTIVE = 'Y' AND S.SUB_CATEGORY_ID ="+subCategoryId +
                " GROUP BY S.CATEGORY_ID, S.NAME, S.DESCRIPTION " +
                " ORDER BY S.NAME ASC", null);
        if(c.moveToNext()){
            ProductSubCategory productSubCategory = new ProductSubCategory();
            productSubCategory.setId(subCategoryId);
            productSubCategory.setProductCategoryId(c.getInt(0));
            productSubCategory.setName(c.getString(1).replaceAll("\\s+", " ").trim());
            productSubCategory.setDescription(c.getString(2).replaceAll("\\s+", " ").trim());
            productSubCategory.setProductsActiveQty(c.getInt(3));
            return productSubCategory;
        }
        return null;
    }
}

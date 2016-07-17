package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductSubCategoryDB {

    private Context context;

    public ProductSubCategoryDB(Context context){
        this.context = context;
    }

    public ProductSubCategory getActiveProductSubCategoryById(int subCategoryId){
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT S.CATEGORY_ID, S.NAME, S.DESCRIPTION " +
                    " FROM SUBCATEGORY S " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                    " WHERE S.SUBCATEGORY_ID = ? AND S.IS_ACTIVE = ?",
                    new String[]{"Y", String.valueOf(subCategoryId), "Y"}, null);
            if (c!=null && c.moveToNext()){
                ProductSubCategory productSubCategory = new ProductSubCategory();
                productSubCategory.setId(subCategoryId);
                productSubCategory.setProductCategoryId(c.getInt(0));
                productSubCategory.setName(c.getString(1));
                productSubCategory.setDescription(c.getString(2));
                return productSubCategory;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c!=null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategoriesByCategoryId(int categoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.SUBCATEGORY_ID) " +
                    " FROM SUBCATEGORY S " +
                        " INNER JOIN PRODUCT P ON P.SUBCATEGORY_ID = S.SUBCATEGORY_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                    " WHERE S.CATEGORY_ID = ? AND S.IS_ACTIVE = ? " +
                    " GROUP BY S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION " +
                    " ORDER BY S.NAME ASC",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(categoryId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    ProductSubCategory productSubCategory = new ProductSubCategory();
                    productSubCategory.setId(c.getInt(0));
                    productSubCategory.setProductCategoryId(categoryId);
                    productSubCategory.setName(c.getString(1));
                    productSubCategory.setDescription(c.getString(2));
                    productSubCategory.setProductsActiveQty(c.getInt(3));
                    categories.add(productSubCategory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c!=null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return categories;
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductSubCategoryDB {

    private Context context;
    private User user;

    public ProductSubCategoryDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public ArrayList<ProductSubCategory> getActiveProductSubCategoriesByCategoryId(int categoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.SUBCATEGORY_ID) " +
                    " FROM SUBCATEGORY S " +
                        " INNER JOIN ARTICULOS A ON A.IDPARTIDA = S.SUBCATEGORY_ID AND A.ACTIVO = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                    " WHERE S.ISACTIVE = ? AND S.CATEGORY_ID = ? " +
                    " GROUP BY S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION " +
                    " ORDER BY S.NAME ASC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"V", "Y", "Y", "Y", String.valueOf(categoryId)}, null);
            while(c.moveToNext()){
                ProductSubCategory productSubCategory = new ProductSubCategory();
                productSubCategory.setId(c.getInt(0));
                productSubCategory.setProductCategoryId(categoryId);
                productSubCategory.setName(c.getString(1));
                productSubCategory.setDescription(c.getString(2));
                productSubCategory.setProductsActiveQty(c.getInt(3));
                categories.add(productSubCategory);
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

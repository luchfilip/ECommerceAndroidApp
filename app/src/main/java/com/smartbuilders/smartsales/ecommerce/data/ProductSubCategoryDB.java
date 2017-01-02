package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by stein on 4/24/2016.
 */
public class ProductSubCategoryDB {

    private Context mContext;
    private User mUser;
    private boolean mShowProductsWithoutAvailability;

    public ProductSubCategoryDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
        this.mShowProductsWithoutAvailability = Parameter.showProductsWithoutAvailability(context, user);
    }

    public ProductSubCategory getProductSubCategory(int subCategoryId){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
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

    public ArrayList<ProductSubCategory> getProductSubCategoriesByCategoryId(int categoryId){
        ArrayList<ProductSubCategory> categories = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, COUNT(S.SUBCATEGORY_ID) " +
                    " FROM SUBCATEGORY S " +
                        " INNER JOIN PRODUCT P ON P.SUBCATEGORY_ID = S.SUBCATEGORY_ID AND P.IS_ACTIVE = 'Y' " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' " +
                        (mShowProductsWithoutAvailability ? ""
                            : " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = (SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID="+Utils.getAppCurrentBusinessPartnerId(mContext, mUser)+" AND IS_ACTIVE='Y') " +
                                " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ") +
                    " WHERE S.CATEGORY_ID = ? AND S.IS_ACTIVE = 'Y' " +
                    " GROUP BY S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION " +
                    " ORDER BY S.NAME ASC",
                    new String[]{String.valueOf(categoryId)}, null);
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

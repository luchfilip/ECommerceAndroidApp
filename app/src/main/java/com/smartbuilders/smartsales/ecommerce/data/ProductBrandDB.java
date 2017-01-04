package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrand;

import java.util.ArrayList;

/**
 * Created by stein on 4/23/2016.
 */
public class ProductBrandDB {

    private Context mContext;
    private User mUser;
    private boolean mShowProductsWithoutAvailability;

    public ProductBrandDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
        this.mShowProductsWithoutAvailability = Parameter.showProductsWithoutAvailability(context, user);
    }

    public ArrayList<ProductBrand> getActiveProductBrands(){
        ArrayList<ProductBrand> productBrands = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT B.BRAND_ID, B.NAME, B.DESCRIPTION, COUNT(B.BRAND_ID) " +
                            " FROM BRAND B " +
                            " INNER JOIN PRODUCT P ON P.BRAND_ID = B.BRAND_ID AND P.IS_ACTIVE = 'Y' " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' " +
                            (mShowProductsWithoutAvailability ? ""
                                    : " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = (SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID=" + Utils.getAppCurrentBusinessPartnerId(mContext, mUser) + " AND IS_ACTIVE='Y') " +
                                    " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ") +
                            " WHERE B.IS_ACTIVE = 'Y' " +
                            " GROUP BY B.BRAND_ID, B.NAME, B.DESCRIPTION " +
                            " ORDER BY B.NAME ASC ",
                    null, null);
            if(c!=null){
                while(c.moveToNext()){
                    ProductBrand productBrand = new ProductBrand();
                    productBrand.setId(c.getInt(0));
                    productBrand.setName(c.getString(1).toUpperCase());
                    productBrand.setDescription(c.getString(2).toUpperCase());
                    productBrand.setProductsActiveQty(c.getInt(3));
                    productBrands.add(productBrand);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(c!=null){
                try {
                    c.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return productBrands;
    }
}

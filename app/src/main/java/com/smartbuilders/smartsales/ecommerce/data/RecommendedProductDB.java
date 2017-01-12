package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.util.ArrayList;

/**
 * Created by stein on 29/6/2016.
 */
public class RecommendedProductDB {

    private Context mContext;
    private User mUser;

    public RecommendedProductDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Product> getRecommendedProductsByBusinessPartnerId(int businessPartnerId){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM RECOMMENDED_PRODUCT RP " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = RP.PRODUCT_ID AND P.IS_ACTIVE = 'Y' " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = COALESCE((SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID=? AND IS_ACTIVE='Y'),0) " +
                            " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = 'Y' " +
                    " WHERE RP.BUSINESS_PARTNER_ID = ? AND RP.IS_ACTIVE = 'Y' " +
                    " ORDER BY RP.PRIORITY desc",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()),
                            OrderLineDB.WISH_LIST_DOC_TYPE, String.valueOf(businessPartnerId)}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillFullProductInfoFromCursor(product, c);
                    products.add(product);
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
        return products;
    }

    public int getRecommendedProductsCount(){
        Cursor c = null;
        try {
            String currentBusinessPartnerId = String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(R.PRODUCT_ID) FROM RECOMMENDED_PRODUCT R " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = R.PRODUCT_ID AND P.IS_ACTIVE = 'Y' " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = COALESCE((SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID=? AND IS_ACTIVE='Y'),0) " +
                            " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 " +
                    " WHERE R.BUSINESS_PARTNER_ID = ? AND R.IS_ACTIVE = 'Y' ",
                    new String[]{currentBusinessPartnerId, currentBusinessPartnerId}, null);
            if (c!=null && c.moveToNext()) {
                return c.getInt(0);
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
        return 0;
    }

}

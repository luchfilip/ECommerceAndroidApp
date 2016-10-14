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
                    "SELECT DISTINCT RP.PRODUCT_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, OL.PRODUCT_ID, P.INTERNAL_CODE " +
                    " FROM  RECOMMENDED_PRODUCT RP " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = RP.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.BUSINESS_PARTNER_ID = ? " +
                            " AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE RP.BUSINESS_PARTNER_ID = ? AND RP.IS_ACTIVE = ? " +
                    " ORDER BY RP.PRIORITY desc",
                    new String[]{"Y", "Y", "Y", "Y", "0", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                            "Y", String.valueOf(businessPartnerId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillLightProductInfoFromCursor(product, c);
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT COUNT(R.PRODUCT_ID) FROM RECOMMENDED_PRODUCT R " +
                            " INNER JOIN PRODUCT P ON P.PRODUCT_ID = R.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0" +
                        " WHERE R.BUSINESS_PARTNER_ID = ? AND R.IS_ACTIVE = ? ",
                    new String[]{"Y", "Y", "Y", "Y", "0", "Y",
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)), "Y"}, null);
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

package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 29/6/2016.
 */
public class ProductRecentlySeenDB {

    private Context mContext;
    private User mUser;
    private boolean mShowProductsWithoutAvailability;

    public ProductRecentlySeenDB(Context context, User user) {
        this.mContext = context;
        this.mUser = user;
        this.mShowProductsWithoutAvailability = Parameter.showProductsWithoutAvailability(context, user);
    }

    public void addProduct(int productId){
        try {
            //este dato no se envia en tiempo real al servidor por que la sentencia no es compatible (INSERT OR REPLACE INTO)
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "INSERT OR REPLACE INTO PRODUCT_RECENTLY_SEEN (PRODUCT_RECENTLY_SEEN_ID, BUSINESS_PARTNER_ID, " +
                            " USER_ID, PRODUCT_ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "PRODUCT_RECENTLY_SEEN")),
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)), String.valueOf(mUser.getServerUserId()),
                            String.valueOf(productId), DateFormat.getCurrentDateTimeSQLFormat(),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getProductsRecentlySeen(Integer limit){
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
                    " FROM PRODUCT_RECENTLY_SEEN PRS " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = PRS.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        (mShowProductsWithoutAvailability ? " LEFT " : " INNER ") + " JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = COALESCE((SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID=? AND IS_ACTIVE='Y'), 0) " +
                            " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE PRS.USER_ID = ? " +
                    " ORDER BY PRS.PRODUCT_RECENTLY_SEEN_ID desc " +
                    (limit!=null && limit>0 ? " LIMIT " + limit : ""),
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            "Y", "Y", "1", "Y", "Y", String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                            "Y", String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillFullProductInfoFromCursor(product, c);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsRecentlySeenByBusinessPartnerId(int businessPartnerId){
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
                    " FROM PRODUCT_RECENTLY_SEEN PRS " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = PRS.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        (mShowProductsWithoutAvailability ? " LEFT " : " INNER ") + " JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRICE_LIST_ID = COALESCE((SELECT PRICE_LIST_ID FROM BUSINESS_PARTNER WHERE BUSINESS_PARTNER_ID=? AND IS_ACTIVE='Y'),0) " +
                            " AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE PRS.BUSINESS_PARTNER_ID = ? AND PRS.USER_ID = ? " +
                    " ORDER BY PRS.PRODUCT_RECENTLY_SEEN_ID desc " +
                    " LIMIT 30",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            "Y", "Y", "1", "Y", "Y", String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                            "Y", String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillFullProductInfoFromCursor(product, c);
                    products.add(product);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return products;
    }
}

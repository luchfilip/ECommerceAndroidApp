package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.ids.model.User;
import com.smartbuilders.ids.providers.DataBaseContentProvider;
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

    public ProductRecentlySeenDB(Context context, User user) {
        this.mContext = context;
        this.mUser = user;
    }

    public void addProduct(int productId, int businessPartnerId){
        try {
            mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "INSERT OR REPLACE INTO PRODUCT_RECENTLY_SEEN (PRODUCT_RECENTLY_SEEN_ID, BUSINESS_PARTNER_ID, " +
                            " USER_ID, PRODUCT_ID, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    new String[]{String.valueOf(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "PRODUCT_RECENTLY_SEEN")), String.valueOf(businessPartnerId),
                            String.valueOf(mUser.getServerUserId()), String.valueOf(productId), DateFormat.getCurrentDateTimeSQLFormat(),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Product> getProductsRecentlySeenByBusinessPartnerId(int businessPartnerId){
        ArrayList<Product> products = new ArrayList<>();
        ArrayList<Integer> productsIds = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "SELECT PRS.PRODUCT_ID " +
                    " FROM PRODUCT_RECENTLY_SEEN PRS " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = PRS.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                    " WHERE PRS.BUSINESS_PARTNER_ID = ? AND PRS.USER_ID = ? " +
                    " ORDER BY PRS.PRODUCT_RECENTLY_SEEN_ID desc " +
                    " LIMIT 30",
                    new String[]{"Y", String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                while(c.moveToNext()){
                    productsIds.add(c.getInt(0));
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
        ProductDB productDB = new ProductDB(mContext, mUser);
        for (Integer productId : productsIds) {
            Product product = productDB.getProductById(productId);
            if(product!=null){
                products.add(product);
            }
        }
        return products;
    }
}

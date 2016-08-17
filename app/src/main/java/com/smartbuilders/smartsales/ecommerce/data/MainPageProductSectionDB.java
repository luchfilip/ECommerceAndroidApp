package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductSectionDB {

    private Context mContext;
    private User mUser;

    public MainPageProductSectionDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<MainPageProductSection> getActiveMainPageProductSections(){
        ArrayList<MainPageProductSection> mainPageProductSections = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT MS.MAINPAGE_PRODUCT_SECTION_ID, MS.NAME, MS.DESCRIPTION " +
                    " FROM MAINPAGE_PRODUCT_SECTION MS " +
                        " INNER JOIN MAINPAGE_PRODUCT MP ON MP.MAINPAGE_PRODUCT_SECTION_ID = MS.MAINPAGE_PRODUCT_SECTION_ID " +
                            " AND MP.IS_ACTIVE = ? " +
                    " WHERE MS.IS_ACTIVE = ? " +
                    " ORDER BY MS.PRIORITY ASC",
                    new String[] {"Y", "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    MainPageProductSection mainPageProductSection = new MainPageProductSection();
                    mainPageProductSection.setId(c.getInt(0));
                    mainPageProductSection.setName(c.getString(1));
                    mainPageProductSection.setDescription(c.getString(2));
                    mainPageProductSections.add(mainPageProductSection);
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

        if(!mainPageProductSections.isEmpty()) {
            for(MainPageProductSection mainPageProductSection : mainPageProductSections){
                mainPageProductSection.setProducts(getActiveProductsByMainPageProductSectionId(mainPageProductSection.getId()));
            }
        }
        return mainPageProductSections;
    }

    private ArrayList<Product> getActiveProductsByMainPageProductSectionId(int productSectionId){
        ArrayList<Product> productsByProductSectionId = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.NAME, PI.FILE_NAME, B.BRAND_ID, " +
                            " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                            " PA.PRICE, OL.PRODUCT_ID, P.INTERNAL_CODE " +
                    " FROM MAINPAGE_PRODUCT M " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = M.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.BUSINESS_PARTNER_ID = ? " +
                            " AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE M.MAINPAGE_PRODUCT_SECTION_ID = ? AND M.IS_ACTIVE = ? " +
                    " ORDER BY M.PRIORITY ASC",
                    new String[]{"Y", "Y", "Y", "Y", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(Utils.getAppCurrentBusinessPartnerId(mContext, mUser)),
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                            "Y", String.valueOf(productSectionId), "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillLightProductInfoFromCursor(product, c);
                    productsByProductSectionId.add(product);
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
        return productsByProductSectionId;
    }
}

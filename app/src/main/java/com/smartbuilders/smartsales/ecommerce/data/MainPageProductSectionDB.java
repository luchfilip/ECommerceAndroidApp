package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

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
                mainPageProductSection.setProducts(getActiveProductsByMainPageProductSectionId(mainPageProductSection.getId(), 25));
            }
        }
        return mainPageProductSections;
    }

    public MainPageProductSection getActiveMainPageProductSectionById(int mainPageProductSectionId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT NAME, DESCRIPTION FROM MAINPAGE_PRODUCT_SECTION WHERE MAINPAGE_PRODUCT_SECTION_ID=? AND IS_ACTIVE=?",
                    new String[] {String.valueOf(mainPageProductSectionId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                MainPageProductSection mainPageProductSection = new MainPageProductSection();
                mainPageProductSection.setId(mainPageProductSectionId);
                mainPageProductSection.setName(c.getString(0));
                mainPageProductSection.setDescription(c.getString(1));
                return mainPageProductSection;
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
        return null;
    }

    public ArrayList<Product> getActiveProductsByMainPageProductSectionId(int productSectionId, Integer limit){
        ArrayList<Product> productsByProductSectionId = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM MAINPAGE_PRODUCT M " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = M.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE M.MAINPAGE_PRODUCT_SECTION_ID = ? AND M.IS_ACTIVE = ? " +
                    " ORDER BY M.PRIORITY ASC " +
                    (limit!=null && limit>0 ? " LIMIT " + limit : ""),
                    new String[]{"Y", "Y", "Y", "Y", "0", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                            "Y", String.valueOf(productSectionId), "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Product product = new Product();
                    ProductDB.fillFullProductInfoFromCursor(product, c);
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

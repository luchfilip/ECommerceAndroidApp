package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.smartbuilders.smartsales.ecommerce.model.ProductSearchResult;
import com.smartbuilders.smartsales.ecommerce.session.Parameter;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Alberto on 22/4/2016.
 */
public class ProductDB {
    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    private Context mContext;
    private User mUser;
    private boolean mShowProductsWithoutAvailability;

    public ProductDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
        this.mShowProductsWithoutAvailability = Parameter.showProductsWithoutAvailability(context, user);
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = P.PRODUCT_ID AND R.PRODUCT_ID = ").append(productId).append(" ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.PRODUCT_ID <> ").append(productId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY R.TIMES DESC ")
                    .append((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
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

    public ArrayList<Product> getRelatedProductsBySubCategoryId(int subCategoryId, int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.SUBCATEGORY_ID = ").append(subCategoryId).append(" AND P.PRODUCT_ID <> ").append(productId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC ")
                    .append((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
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

    public ArrayList<Product> getRelatedProductsByBrandId(int brandId, int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.BRAND_ID = ").append(brandId).append(" AND P.PRODUCT_ID <> ").append(productId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC ")
                    .append(((limit!=null && limit>0) ? " LIMIT " + limit : ""));
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null, sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
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

    public ArrayList<Product> getProductsBySubCategoryId(int subCategoryId, String productName,
                                                         String productReference, String productPurpose){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.SUBCATEGORY_ID = ").append(subCategoryId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC ");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()) {
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
                }
                if (!TextUtils.isEmpty(productName)) {
                    ProductDBUtils.sortByProductNameContains(products, productName);
                } else if (!TextUtils.isEmpty(productReference)) {
                    ProductDBUtils.sortByProductReferenceContains(products, productReference);
                } else if (!TextUtils.isEmpty(productPurpose)) {
                    ProductDBUtils.sortByProductPurposeContains(products, productPurpose);
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

    public ArrayList<Product> getProductsByCategoryId(int categoryId){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE S.CATEGORY_ID = ").append(categoryId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC ");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
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

    public ArrayList<Product> getProductsByBrandId(int brandId){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.BRAND_ID = ").append(brandId).append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(), null, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
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

    public ArrayList<Product> getProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(name.length()>120
                || name.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        name = name.replace('ñ', '\001').replace('Ñ', '\002');
        name = Normalizer.normalize(name.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        name = name.replace('\001', 'ñ').replace('\002','Ñ');

        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ");

            //si es un numero
            if(name.length()<8 && !patternIsNotNumeric.matcher(name).matches()) {
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = 'Y' ")
                                        .append(" ORDER BY P.NAME ASC").toString(),
                                new String[]{name+"%"}, null);
            }else{
                StringBuilder aux = new StringBuilder();
                StringBuilder firstWord = new StringBuilder();
                for (String word : name.split(" ")){
                    firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                    aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
                }
                String firstReplace = name.contains("\"")
                        ? "replace(lower(P.NAME),'á','a')"
                        : "replace(replace(lower(P.NAME),'\"',''),'á','a')";
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                                        .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                                        .append(" AND P.IS_ACTIVE = 'Y' ")
                                        .append(" ORDER BY P.NAME ASC").toString(),
                                new String[]{firstWord.toString(), aux.toString()}, null);
            }
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
                }
                ProductDBUtils.sortByProductNameStartsWith(products, name);
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

    public ArrayList<ProductSearchResult> getLightProductsByName(String name){
        ArrayList<ProductSearchResult> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(name) || name.length()>120
                || name.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        name = name.replace('ñ', '\001').replace('Ñ', '\002');
        name = Normalizer.normalize(name.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        name = name.replace('\001', 'ñ').replace('\002', 'Ñ');

        boolean isNumeric = (name.length()<8 && !patternIsNotNumeric.matcher(name).matches());

        if(!isNumeric && (TextUtils.isEmpty(name) || name.length()<1)){
            return products;
        }

        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID,P.SUBCATEGORY_ID,UPPER(P.NAME),P.INTERNAL_CODE,S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND ")
                    .append(" PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ");

            if (isNumeric) {
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = 'Y' ")
                                        .append(" ORDER BY P.INTERNAL_CODE ASC LIMIT 30").toString(),
                                new String[]{name+"%"}, null);
            } else {
                StringBuilder aux = new StringBuilder();
                StringBuilder firstWord = new StringBuilder();
                for (String word : name.split(" ")){
                    firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                    aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
                }
                String firstReplace = name.contains("\"")
                        ? "replace(lower(P.NAME),'á','a')"
                        : "replace(replace(lower(P.NAME),'\"',''),'á','a')";
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                                        .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                                        .append(" AND P.IS_ACTIVE = 'Y' ORDER BY P.NAME ASC ")
                                        .append((name.length()>1 ? "" : " LIMIT 50")).toString(),
                                new String[]{firstWord.toString(), aux.toString()}, null);
            }

            if (c!=null) {
                while(c.moveToNext()){
                    ProductSearchResult p = new ProductSearchResult();
                    p.setProductId(c.getInt(0));
                    p.setProductName(c.getString(2));
                    p.setProductInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.setProductSubCategoryName(c.getString(4));
                    products.add(p);
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

    public ArrayList<Product> getProductsByReference(String reference){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(reference) || reference.length()>120
                || reference.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        reference = reference.replace('ñ', '\001').replace('Ñ', '\002');
        reference = Normalizer.normalize(reference.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        reference = reference.replace('\001', 'ñ').replace('\002', 'Ñ');

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : reference.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = reference.contains("\"")
                    ? "replace(lower(P.REFERENCE_ID),'á','a')"
                    : "replace(replace(lower(P.REFERENCE_ID),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(),
                            new String[]{firstWord.toString(), aux.toString()}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
                }
                ProductDBUtils.sortByProductReferenceStartsWith(products, reference);
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

    public ArrayList<ProductSearchResult> getLightProductsByReference(String reference){
        ArrayList<ProductSearchResult> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(reference) || reference.length()>120
                || reference.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        reference = reference.replace('ñ', '\001').replace('Ñ', '\002');
        reference = Normalizer.normalize(reference.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        reference = reference.replace('\001', 'ñ').replace('\002', 'Ñ');

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : reference.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = reference.contains("\"")
                    ? "replace(lower(P.REFERENCE_ID),'á','a')"
                    : "replace(replace(lower(P.REFERENCE_ID),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.REFERENCE_ID), P.INTERNAL_CODE, S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC ")
                    .append(reference.length()>1 ? "" : " LIMIT 50");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(),
                            new String[]{firstWord.toString(), aux.toString()}, null);
            if (c!=null) {
                productRepeated:
                while(c.moveToNext()){
                    for (ProductSearchResult product : products) {
                        if (product.getProductSubCategoryId() == c.getInt(1)
                                && product.getProductReference()!=null && c.getString(2)!=null
                                && product.getProductReference().equals(c.getString(2))) {
                            continue productRepeated;
                        }
                    }
                    ProductSearchResult p = new ProductSearchResult();
                    p.setProductId(c.getInt(0));
                    p.setProductReference(c.getString(2));
                    p.setProductInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.setProductSubCategoryName(c.getString(4));
                    products.add(p);
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

    public ArrayList<Product> getProductsByPurpose(String purpose){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(purpose) || purpose.length()>120
                || purpose.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        purpose = purpose.replace('ñ', '\001').replace('Ñ', '\002');
        purpose = Normalizer.normalize(purpose.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        purpose = purpose.replace('\001', 'ñ').replace('\002', 'Ñ');

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : purpose.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = purpose.contains("\"")
                    ? "replace(lower(P.PURPOSE),'á','a')"
                    : "replace(replace(lower(P.PURPOSE),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y'")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(),
                            new String[]{firstWord.toString(), aux.toString()}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillFullProductInfoFromCursor(p, c);
                    products.add(p);
                }
                ProductDBUtils.sortByProductPurposeStartsWith(products, purpose);
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

    public ArrayList<ProductSearchResult> getLightProductsByPurpose(String purpose){
        ArrayList<ProductSearchResult> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(purpose) || purpose.length()>120
                || purpose.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        purpose = purpose.replace('ñ', '\001').replace('Ñ', '\002');
        purpose = Normalizer.normalize(purpose.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]","");
        purpose = purpose.replace('\001', 'ñ').replace('\002', 'Ñ');

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : purpose.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = purpose.contains("\"")
                    ? "replace(lower(P.PURPOSE),'á','a')"
                    : "replace(replace(lower(P.PURPOSE),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.PURPOSE), P.INTERNAL_CODE, S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(mShowProductsWithoutAvailability ? " LEFT " : " INNER ").append(" JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ORDER BY P.NAME ASC ")
                    .append(purpose.length()>1 ? "" : " LIMIT 100");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                            sql.toString(), new String[]{firstWord.toString(), aux.toString()}, null);

            if (c!=null) {
                productRepeated:
                while(c.moveToNext()){
                    for (ProductSearchResult product : products) {
                        if (product.getProductSubCategoryId() == c.getInt(1)
                                && product.getProductPurpose()!=null && c.getString(2)!=null
                                && product.getProductPurpose().equals(c.getString(2))) {
                            continue productRepeated;
                        }
                    }
                    ProductSearchResult p = new ProductSearchResult();
                    p.setProductId(c.getInt(0));
                    p.setProductPurpose(c.getString(2));
                    p.setProductInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.setProductSubCategoryName(c.getString(4));
                    products.add(p);
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

    public Product getProductById(int id){
        if (id<0) {
            return null;
        }
        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, P.COMMERCIAL_PACKAGE, B.NAME, ")
                    .append(" B.DESCRIPTION, C.CATEGORY_ID, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ").append(mUser.getServerUserId()).append(" AND OL.DOC_TYPE = '").append(OrderLineDB.WISH_LIST_DOC_TYPE).append("' AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE P.PRODUCT_ID = ").append(id).append(" AND P.IS_ACTIVE = 'Y'");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(), null, null);
            if (c!=null && c.moveToNext()){
                Product p = new Product();
                fillFullProductInfoFromCursor(p, c);
                return p;
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

    /**
     * Carga el objeto Product que se pasa por parametro a partir de un cursor que posea las siguientes columnas:
     *     0) PRODUCT.PRODUCT_ID,
     *     1) PRODUCT.SUBCATEGORY_ID,
     *     2) PRODUCT.BRAND_ID,
     *     3) PRODUCT.NAME,
     *     4) PRODUCT.DESCRIPTION,
     *     5) PRODUCT.PURPOSE,
     *     6) PRODUCT.INTERNAL_CODE,
     *     7) PRODUCT.COMMERCIAL_PACKAGE_UNITS,
     *     8) PRODUCT.COMMERCIAL_PACKAGE,
     *     9) BRAND.NAME,
     *     10) BRAND.DESCRIPTION,
     *     11) CATEGORY.CATEGORY_ID,
     *     12) PRODUCT_PRICE_AVAILABILITY.AVAILABILITY,
     *     13) PRODUCT_IMAGE.FILE_NAME,
     *     14) PRODUCT_RATING.RATING,
     *     15) CURRENCY.CURRENCY_ID,
     *     16) CURRENCY.UNICODE_DECIMAL,
     *     17) PRODUCT_PRICE_AVAILABILITY.PRICE
     *     18) PRODUCT_PRICE_AVAILABILITY.TAX
     *     19) PRODUCT_PRICE_AVAILABILITY.TOTAL_PRICE
     *     20) PRODUCT_TAX.PRODUCT_TAX_ID,
     *     21) PRODUCT_TAX.PERCENTAGE
     *     22) ECOMMERCE_ORDER_LINE.PRODUCT_ID
     *     23) PRODUCT.REFERENCE_ID
     * @param product
     * @param cursor
     */
    public static void fillFullProductInfoFromCursor(Product product, Cursor cursor) {
        product.setId(cursor.getInt(0));
        product.setName(cursor.getString(3));
        product.setDescription(cursor.getString(4));
        product.setPurpose(cursor.getString(5));
        product.setInternalCode(cursor.getString(6));
        product.getProductCommercialPackage().setUnits(cursor.getInt(7));
        product.getProductCommercialPackage().setUnitDescription(cursor.getString(8));
        product.setProductBrandId(cursor.getInt(2));
        product.getProductBrand().setId(cursor.getInt(2));
        product.getProductBrand().setName(cursor.getString(9));
        product.getProductBrand().setDescription(cursor.getString(10));
        product.setProductCategoryId(cursor.getInt(11));
        product.setProductSubCategoryId(cursor.getInt(1));
        product.getProductPriceAvailability().setAvailability(cursor.getInt(12));
        product.setImageFileName(cursor.getString(13));
        product.setRating(cursor.getFloat(14));
        product.getProductPriceAvailability().setCurrencyId(cursor.getInt(15));
        product.getProductPriceAvailability().getCurrency().setId(cursor.getInt(15));
        product.getProductPriceAvailability().getCurrency().setUnicodeDecimal(cursor.getString(16));
        product.getProductPriceAvailability().setPrice(cursor.getFloat(17));
        product.getProductPriceAvailability().setTax(cursor.getFloat(18));
        product.getProductPriceAvailability().setTotalPrice(cursor.getFloat(19));
        product.setProductTaxId(cursor.getInt(20));
        product.getProductTax().setId(cursor.getInt(20));
        product.getProductTax().setPercentage(cursor.getFloat(21));
        product.setFavorite(cursor.getString(22)!=null);
        product.setReference(cursor.getString(23));
    }

    public Product getProductByInternalCode(String productCode) {
        return getProductById(getProductIdByInternalCode(productCode));
    }

    /**
     * Devuelve -1 si no se consigue el articulo
     * @param productCode
     * @return
     */
    public int getProductIdByInternalCode(String productCode) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT PRODUCT_ID FROM PRODUCT WHERE INTERNAL_CODE = ? AND IS_ACTIVE = 'Y'",
                    new String[]{productCode}, null);
            if (c!=null && c.moveToNext()){
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
        return -1;
    }

    /**
     * Devuelve -1 si no se consigue el articulo
     * @param barCode
     * @return
     */
    public int getProductIdByBarCode(String barCode) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT PRODUCT_ID FROM PRODUCT WHERE BAR_CODE = ? AND IS_ACTIVE = 'Y'",
                    new String[]{barCode}, null);
            if (c!=null && c.moveToNext()){
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
        return -1;
    }
}

/**
 *
 */
class ProductDBUtils {

    private static int incidencesByWord(String str, String[] words) {
        int i = words.length;
        for (String word : Arrays.asList(words)) {
            if (!str.contains(word)) {
                i--;
            }
        }
        return i;
    }

    private static int startsWith(String str, String[] words) {
        int i = 0;
        for (String word : Arrays.asList(words)) {
            i += str.indexOf(word);
        }
        return i;
    }

    static void sortByProductNameContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getName().toUpperCase(), aux)
                            - incidencesByWord(p1.getName().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    static void sortByProductReferenceContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getReference().toUpperCase(), aux)
                            - incidencesByWord(p1.getReference().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    static void sortByProductPurposeContains(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return incidencesByWord(p2.getPurpose().toUpperCase(), aux)
                            - incidencesByWord(p1.getPurpose().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    static void sortByProductNameStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getName().toUpperCase(), aux)
                            - startsWith(p2.getName().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    static void sortByProductReferenceStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getReference().toUpperCase(), aux)
                            - startsWith(p2.getReference().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    static void sortByProductPurposeStartsWith(List<Product> products, String searchPattern) {
        final String aux[] = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
        //Se ordena segun el parecido que tenga el producto con el texto buscado
        Collections.sort(products, new Comparator<Product>() {

            @Override
            public int compare(Product p1, Product p2) {
                try {
                    return startsWith(p1.getPurpose().toUpperCase(), aux)
                            - startsWith(p2.getPurpose().toUpperCase(), aux);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }
}
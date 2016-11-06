package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.text.Normalizer;
import java.util.ArrayList;
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

    public ProductDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, OL.PRODUCT_ID, P.INTERNAL_CODE, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = P.PRODUCT_ID AND R.PRODUCT_ID = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? "  +
                    " ORDER BY R.TIMES DESC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "0", "Y", String.valueOf(productId), "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillLightProductInfoFromCursor(p, c);
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
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, OL.PRODUCT_ID, P.INTERNAL_CODE, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "0", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(subCategoryId), String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillLightProductInfoFromCursor(p, c);
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
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, " +
                        " PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, OL.PRODUCT_ID, P.INTERNAL_CODE, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 "+
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.PRODUCT_ID <> ?  AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "0", "Y", "Y", "1", "Y", "Y",
                    String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                    String.valueOf(brandId), String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    fillLightProductInfoFromCursor(p, c);
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "0", "Y", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(subCategoryId), "Y"}, null);
            if (c!=null) {
                String[] words;
                String searchPattern = productName;
                int filterColumn = 3;
                if (productName!=null) {
                    searchPattern = productName;
                    filterColumn = 3;
                } else if (productReference!=null) {
                    searchPattern = productReference;
                    filterColumn = 27;
                } else if (productPurpose!=null) {
                    searchPattern = productPurpose;
                    filterColumn = 5;
                }
                try {
                    words = searchPattern == null ? null : searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
                } catch (Exception e) {
                    words = null;
                }
                whileStatement:
                while(c.moveToNext()){
                    if(searchPattern!=null && words!=null){
                        try {
                            for(String word : words){
                                if(!c.getString(filterColumn).toUpperCase().contains(word)){
                                    continue whileStatement;
                                }
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
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

    public ArrayList<Product> getProductsByCategoryId(int categoryId){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 "+
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE S.CATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "0", "Y", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(categoryId), "Y"}, null);
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? AND PA.AVAILABILITY > 0 "+
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC",
                    new String[]{"Y", "Y", "Y", "0", "Y", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(brandId), "Y"}, null);
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
        name = Normalizer.normalize(name.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, ")
                    .append(" P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, ")
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, ")
                    .append(" S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = 'Y' ");

            //si es un numero
            if(name.length()<8 && !patternIsNotNumeric.matcher(name).matches()) {
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = 'Y' ")
                                        .append(" ORDER BY P.NAME ASC").toString(),
                                new String[]{String.valueOf(mUser.getServerUserId()),
                                        OrderLineDB.WISH_LIST_DOC_TYPE, name+"%"}, null);
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
                                new String[]{String.valueOf(mUser.getServerUserId()),
                                        OrderLineDB.WISH_LIST_DOC_TYPE, firstWord.toString(), aux.toString()}, null);
            }
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

    public ArrayList<Product> getLightProductsByName(String searchPattern){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(searchPattern) || searchPattern.length()>120
                || searchPattern.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        searchPattern = Normalizer.normalize(searchPattern.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

        boolean isNumeric = (searchPattern.length()<8 && !patternIsNotNumeric.matcher(searchPattern).matches());

        if(!isNumeric && (TextUtils.isEmpty(searchPattern) || searchPattern.length()<1)){
            return products;
        }

        Cursor c = null;
        try {
            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID,P.SUBCATEGORY_ID,UPPER(P.NAME),P.INTERNAL_CODE,S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND ")
                    .append(" PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ");

            if (isNumeric) {
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = 'Y' ")
                                        .append(" ORDER BY P.INTERNAL_CODE ASC LIMIT 30").toString(),
                                new String[]{searchPattern+"%"}, null);
            } else {
                StringBuilder aux = new StringBuilder();
                StringBuilder firstWord = new StringBuilder();
                for (String word : searchPattern.split(" ")){
                    firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                    aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
                }
                String firstReplace = searchPattern.contains("\"")
                        ? "replace(lower(P.NAME),'á','a')"
                        : "replace(replace(lower(P.NAME),'\"',''),'á','a')";
                c = mContext.getContentResolver()
                        .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                                sql.append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                                        .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                                        .append(" AND P.IS_ACTIVE = 'Y' ORDER BY P.NAME ASC ")
                                        .append((searchPattern.length()>1 ? "" : " LIMIT 50")).toString(),
                                new String[]{firstWord.toString(), aux.toString()}, null);
            }

            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(2));
                    p.setInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.getProductSubCategory().setId(c.getInt(1));
                    p.getProductSubCategory().setName(c.getString(4));
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
        reference = Normalizer.normalize(reference.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

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
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, ")
                    .append(" S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(),
                            new String[]{String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                                    firstWord.toString(), aux.toString()}, null);
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

    public ArrayList<Product> getLightProductsByReference(String searchPattern){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(searchPattern) || searchPattern.length()>120
                || searchPattern.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        searchPattern = Normalizer.normalize(searchPattern.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : searchPattern.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = searchPattern.contains("\"")
                    ? "replace(lower(P.REFERENCE_ID),'á','a')"
                    : "replace(replace(lower(P.REFERENCE_ID),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.REFERENCE_ID), P.INTERNAL_CODE, S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = ? ")
                    .append(" ORDER BY P.NAME ASC ")
                    .append(searchPattern.length()>1 ? "" : " LIMIT 50");
            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                            sql.toString(), new String[]{firstWord.toString(), aux.toString(), "Y"}, null);
            if (c!=null) {
                productRepeated:
                while(c.moveToNext()){
                    for (Product product : products) {
                        if (product.getProductSubCategoryId() == c.getInt(1)
                                && product.getReference()!=null && c.getString(2)!=null
                                && product.getReference().equals(c.getString(2))) {
                            continue productRepeated;
                        }
                    }
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setReference(c.getString(2));
                    p.setInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.getProductSubCategory().setId(c.getInt(1));
                    p.getProductSubCategory().setName(c.getString(4));
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
        purpose = Normalizer.normalize(purpose.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

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
                    .append(" P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, ")
                    .append(" S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, ")
                    .append(" PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = 'Y'")
                    .append(" LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = 1 AND PI.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = 'Y' ")
                    .append(" LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = 'Y' ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ")
                    .append(" ORDER BY P.NAME ASC");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                            null, sql.toString(),
                            new String[]{ String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE,
                                    firstWord.toString(), aux.toString()}, null);
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

    public ArrayList<Product> getLightProductsByPurpose(String searchPattern){
        ArrayList<Product> products = new ArrayList<>();
        //Se valida que la busqueda no este vacia o no sea muy grande
        if(TextUtils.isEmpty(searchPattern) || searchPattern.length()>120
                || searchPattern.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        searchPattern = Normalizer.normalize(searchPattern.replaceAll("\\s+", " ").trim().toLowerCase(Locale.getDefault()),
                Normalizer.Form.NFD).replaceAll("[^a-zA-Z0-9 ]","");

        Cursor c = null;
        try {
            StringBuilder aux = new StringBuilder();
            StringBuilder firstWord = new StringBuilder();
            for (String word : searchPattern.split(" ")){
                firstWord.append(firstWord.length()==0 ? "% " : " ").append(word).append("%");
                aux.append(aux.length()==0 ? "" : " ").append(word).append("%");
            }
            String firstReplace = searchPattern.contains("\"")
                    ? "replace(lower(P.PURPOSE),'á','a')"
                    : "replace(replace(lower(P.PURPOSE),'\"',''),'á','a')";

            StringBuilder sql = new StringBuilder("SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.PURPOSE), P.INTERNAL_CODE, S.NAME ")
                    .append(" FROM PRODUCT P ")
                    .append(" INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = 'Y' ")
                    .append(" INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = 0 AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = 'Y' AND PA.AVAILABILITY > 0 ")
                    .append(" WHERE (replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? ")
                    .append(" OR replace(replace(replace(replace(").append(firstReplace).append(",'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ?) ")
                    .append(" AND P.IS_ACTIVE = 'Y' ORDER BY P.NAME ASC ")
                    .append(searchPattern.length()>1 ? "" : " LIMIT 100");

            c = mContext.getContentResolver()
                    .query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                            sql.toString(), new String[]{firstWord.toString(), aux.toString()}, null);

            if (c!=null) {
                productRepeated:
                while(c.moveToNext()){
                    for (Product product : products) {
                        if (product.getProductSubCategoryId() == c.getInt(1)
                                && product.getPurpose()!=null && c.getString(2)!=null
                                && product.getPurpose().equals(c.getString(2))) {
                            continue productRepeated;
                        }
                    }
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setPurpose(c.getString(2));
                    p.setInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.getProductSubCategory().setId(c.getInt(1));
                    p.getProductSubCategory().setName(c.getString(4));
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, P.COMMERCIAL_PACKAGE, B.NAME, " +
                        " B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PA.TAX, PA.TOTAL_PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE, OL.PRODUCT_ID, P.REFERENCE_ID " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_PRICE_ID = ? AND PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " LEFT JOIN ECOMMERCE_ORDER_LINE OL ON OL.PRODUCT_ID = P.PRODUCT_ID AND OL.USER_ID = ? AND OL.DOC_TYPE=? AND OL.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID = ? AND P.IS_ACTIVE = ?",
                    new String[]{"Y", "Y", "Y", "0", "Y", "Y", "Y", "1", "Y", "Y",
                            String.valueOf(mUser.getServerUserId()), OrderLineDB.WISH_LIST_DOC_TYPE, "Y",
                            String.valueOf(id), "Y"}, null);
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
     *     12) CATEGORY.NAME,
     *     13) CATEGORY.DESCRIPTION,
     *     14) SUBCATEGORY.NAME,
     *     15) SUBCATEGORY.DESCRIPTION,
     *     16) PRODUCT_PRICE_AVAILABILITY.AVAILABILITY,
     *     17) PRODUCT_IMAGE.FILE_NAME,
     *     18) PRODUCT_RATING.RATING,
     *     19) CURRENCY.CURRENCY_ID,
     *     20) CURRENCY.UNICODE_DECIMAL,
     *     21) PRODUCT_PRICE_AVAILABILITY.PRICE
     *     22) PRODUCT_PRICE_AVAILABILITY.TAX
     *     23) PRODUCT_PRICE_AVAILABILITY.TOTAL_PRICE
     *     24) PRODUCT_TAX.PRODUCT_TAX_ID,
     *     25) PRODUCT_TAX.PERCENTAGE
     *     26) ECOMMERCE_ORDER_LINE.PRODUCT_ID
     *     27) PRODUCT.REFERENCE_ID
     * @param product
     * @param cursor
     */
    private static void fillFullProductInfoFromCursor(Product product, Cursor cursor) {
        product.setRequireFullFill(false);
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
        product.getProductCategory().setId(cursor.getInt(11));
        product.getProductCategory().setName(cursor.getString(12));
        product.getProductCategory().setDescription(cursor.getString(13));
        product.setProductSubCategoryId(cursor.getInt(1));
        product.getProductSubCategory().setProductCategoryId(cursor.getInt(11));
        product.getProductSubCategory().setId(cursor.getInt(1));
        product.getProductSubCategory().setName(cursor.getString(14));
        product.getProductSubCategory().setDescription(cursor.getString(15));
        product.getDefaultProductPriceAvailability().setAvailability(cursor.getInt(16));
        product.setImageFileName(cursor.getString(17));
        product.setRating(cursor.getFloat(18));
        product.getDefaultProductPriceAvailability().setCurrencyId(cursor.getInt(19));
        product.getDefaultProductPriceAvailability().getCurrency().setId(cursor.getInt(19));
        product.getDefaultProductPriceAvailability().getCurrency().setUnicodeDecimal(cursor.getString(20));
        product.getDefaultProductPriceAvailability().setPrice(cursor.getFloat(21));
        product.getDefaultProductPriceAvailability().setTax(cursor.getFloat(22));
        product.getDefaultProductPriceAvailability().setTotalPrice(cursor.getFloat(23));
        product.setProductTaxId(cursor.getInt(24));
        product.getProductTax().setId(cursor.getInt(24));
        product.getProductTax().setPercentage(cursor.getFloat(25));
        product.setFavorite(cursor.getString(26)!=null);
        product.setReference(cursor.getString(27));
    }


    /**
     * Carga el objeto Product que se pasa por parametro a partir de un cursor que posea las siguientes columnas:
     * 0) PRODUCT.PRODUCT_ID
     * 1) PRODUCT.NAME
     * 2) PRODUCT.DESCRIPTION
     * 3) PRODUCT.PURPOSE
     * 4) PRODUCT_IMAGE.FILE_NAME
     * 5) BRAND.BRAND_ID
     * 6) BRAND.NAME
     * 7) BRAND.DESCRIPTION
     * 8) SUBCATEGORY.CATEGORY_ID
     * 9) SUBCATEGORY.SUBCATEGORY_ID
     * 10) SUBCATEGORY.NAME
     * 11) SUBCATEGORY.DESCRIPTION
     * 12) PRODUCT_PRICE_AVAILABILITY.AVAILABILITY
     * 13) PRODUCT_RATING.RATING
     * 14) CURRENCY.CURRENCY_ID
     * 15) CURRENCY.UNICODE_DECIMAL
     * 16) PRODUCT_PRICE_AVAILABILITY.PRICE
     * 17) PRODUCT_PRICE_AVAILABILITY.TAX
     * 18) PRODUCT_PRICE_AVAILABILITY.TOTAL_PRICE
     * 19) ECOMMERCE_ORDER_LINE.PRODUCT_ID
     * 20) PRODUCT.INTERNAL_CODE
     * 21) PRODUCT.REFERENCE_ID
     * @param product
     * @param cursor
     */
    public static void fillLightProductInfoFromCursor(Product product, Cursor cursor) {
        product.setRequireFullFill(true);
        product.setId(cursor.getInt(0));
        product.setName(cursor.getString(1));
        product.setDescription(cursor.getString(2));
        product.setPurpose(cursor.getString(3));
        product.setImageFileName(cursor.getString(4));
        product.setProductBrandId(cursor.getInt(5));
        product.getProductBrand().setId(cursor.getInt(5));
        product.getProductBrand().setName(cursor.getString(6));
        product.getProductBrand().setDescription(cursor.getString(7));
        product.setProductCategoryId(cursor.getInt(8));
        product.setProductSubCategoryId(cursor.getInt(9));
        product.getProductSubCategory().setProductCategoryId(cursor.getInt(8));
        product.getProductSubCategory().setId(cursor.getInt(9));
        product.getProductSubCategory().setName(cursor.getString(10));
        product.getProductSubCategory().setDescription(cursor.getString(11));
        product.getDefaultProductPriceAvailability().setAvailability(cursor.getInt(12));
        product.setRating(cursor.getFloat(13));
        product.getDefaultProductPriceAvailability().setCurrencyId(cursor.getInt(14));
        product.getDefaultProductPriceAvailability().getCurrency().setId(cursor.getInt(14));
        product.getDefaultProductPriceAvailability().getCurrency().setUnicodeDecimal(cursor.getString(15));
        product.getDefaultProductPriceAvailability().setPrice(cursor.getFloat(16));
        product.getDefaultProductPriceAvailability().setTax(cursor.getFloat(17));
        product.getDefaultProductPriceAvailability().setTotalPrice(cursor.getFloat(18));
        product.setFavorite(cursor.getString(19)!=null);
        product.setInternalCode(cursor.getString(20));
        product.setReference(cursor.getString(21));
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
                    "SELECT PRODUCT_ID FROM PRODUCT WHERE INTERNAL_CODE = ? AND IS_ACTIVE = ?",
                    new String[]{productCode, "Y",}, null);
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
                    "SELECT PRODUCT_ID FROM PRODUCT WHERE BAR_CODE = ? AND IS_ACTIVE = ?",
                    new String[]{barCode, "Y",}, null);
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

package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Alberto on 22/4/2016.
 */
public class ProductDB {
    // Regular expression in Java to check if String is number or not
    private static final Pattern patternIsNotNumeric = Pattern.compile(".*[^0-9].*");

    private Context mContext;
    private User mUser;
    private OrderLineDB mOrderLineDB;

    public ProductDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
        this.mOrderLineDB = new OrderLineDB(mContext, user);
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, PA.PRICE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = P.PRODUCT_ID AND R.PRODUCT_ID = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? "  +
                    " ORDER BY R.TIMES DESC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(productId), "Y",
                    String.valueOf(1), "Y", "Y", String.valueOf(productId), "Y"}, null);
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
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, PA.PRICE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
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
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, " +
                        " PA.AVAILABILITY, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, PA.PRICE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.PRODUCT_ID <> ?  AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    sql, new String[]{"Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
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

    public ArrayList<Product> getProductsBySubCategoryId(int subCategoryId, String searchPattern){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                        " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
                            String.valueOf(subCategoryId), "Y"}, null);
            if (c!=null) {
                String[] words;
                try {
                    words = searchPattern.toUpperCase().replaceAll("\\s+", " ").split(" ");
                } catch (Exception e) {
                    words = null;
                }
                whileStatement:
                while(c.moveToNext()){
                    if(searchPattern!=null && words!=null){
                        try {
                            for(String word : words){
                                if(!c.getString(3).toUpperCase().contains(word)){
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
                        " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE S.CATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", String.valueOf(categoryId), "Y"}, null);
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
                        " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC",
                    new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
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
        if(/*TextUtils.isEmpty(name) || */name.length()>120
                || name.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        name = name.replaceAll("\\s+", " ").trim().toUpperCase();

        Cursor c = null;
        try {
            //si es un numero
            if(name.length()<8 && !patternIsNotNumeric.matcher(name).matches()) {
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                        "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                            " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                            " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                            " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                            " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = ? " +
                        " ORDER BY P.NAME ASC",
                        new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", name+"%", "Y"}, null);
            }else{
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                        "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                            " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                            " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING, CU.CURRENCY_ID, CU.UNICODE_DECIMAL, " +
                            " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                            " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " WHERE (replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE " +
                                    " OR replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE) " +
                                " AND P.IS_ACTIVE = ? " +
                        " ORDER BY P.NAME ASC",
                        new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", name+"%", "% "+name+"%", "Y"}, null);
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
        searchPattern = searchPattern.replaceAll("\\s+", " ").trim().toUpperCase();

        boolean isNumeric = (searchPattern.length()<8 && !patternIsNotNumeric.matcher(searchPattern).matches());

        if(!isNumeric && (TextUtils.isEmpty(searchPattern) || searchPattern.length()<1)){
            return products;
        }

        Cursor c = null;
        try {
            if (isNumeric) {
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                        "SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.NAME), P.INTERNAL_CODE, S.NAME, S.DESCRIPTION " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = ? "  +
                        " ORDER BY P.INTERNAL_CODE ASC LIMIT 20",
                        new String[]{"Y", "Y", "Y", "Y", searchPattern+"%", "Y"}, null);
            } else {
                String sql = "SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.NAME), P.INTERNAL_CODE, S.NAME, S.DESCRIPTION " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " WHERE (replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE " +
                                " OR replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE) " +
                            " AND P.IS_ACTIVE = ? " +
                        " ORDER BY P.NAME ASC " +
                        (searchPattern.length()>1 ? "" : " LIMIT 100");
                String aux = null;
                String firstWord = "";
                for (String word : searchPattern.split(" ")){
                    if(aux==null){
                        if (searchPattern.length()>1 && searchPattern.split(" ").length==1) {
                            firstWord = "% "+word+"%";
                        }
                        aux = word+"%";
                    } else {
                        aux += " "+word+"%";
                    }
                }
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                        sql, new String[]{"Y", "Y", "Y", "Y", firstWord, aux, "Y"}, null);
            }

            if (c!=null) {
                boolean searchPatternIsOneWord = searchPattern.split(" ").length==1;
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    if (!isNumeric) {
                        if(searchPatternIsOneWord) {
                            for(String aux : c.getString(2).replaceAll("\\s+", " ").split(" ")){
                                if(aux.contains(searchPattern)){
                                    p.setName(aux);
                                    break;
                                }
                            }
                            if(p.getName()==null){
                                p.setName(c.getString(2));
                            }
                        } else {
                            p.setName(c.getString(2));
                        }
                    } else {
                        p.setName(c.getString(2));
                    }
                    p.setInternalCode(c.getString(3));
                    p.setProductSubCategoryId(c.getInt(1));
                    p.getProductSubCategory().setId(c.getInt(1));
                    if(!isNumeric && products.contains(p)){
                        continue;
                    }
                    p.getProductSubCategory().setName(c.getString(4));
                    p.getProductSubCategory().setDescription(c.getString(5));
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
                        " PA.PRICE, PT.PRODUCT_TAX_ID, PT.PERCENTAGE " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_TAX PT ON PT.PRODUCT_TAX_ID = P.PRODUCT_TAX_ID AND PT.IS_ACTIVE = ?" +
                        " LEFT JOIN PRODUCT_PRICE_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN CURRENCY CU ON CU.CURRENCY_ID = PA.CURRENCY_ID AND CU.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID = ? AND P.IS_ACTIVE = ?",
                    new String[]{"Y", "Y", "Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", String.valueOf(id), "Y"}, null);
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
     *     22) PRODUCT_TAX.PRODUCT_TAX_ID,
     *     23) PRODUCT_TAX.PERCENTAGE
     * @param product
     * @param cursor
     */
    private void fillFullProductInfoFromCursor(Product product, Cursor cursor) {
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
        product.setProductTaxId(cursor.getInt(22));
        product.getProductTax().setId(cursor.getInt(22));
        product.getProductTax().setPercentage(cursor.getInt(23));
        product.setFavorite(mOrderLineDB.isProductInWishList(product.getId()));
    }


    /**
     * Carga el objeto Product que se pasa por parametro a partir de un cursor que posea las siguientes columnas:
     * 0) PRODUCT.PRODUCT_ID,
     * 1) PRODUCT.NAME,
     * 2) PRODUCT_IMAGE.FILE_NAME,
     * 3) BRAND.BRAND_ID,
     * 4) BRAND.NAME,
     * 5) BRAND.DESCRIPTION,
     * 6) SUBCATEGORY.CATEGORY_ID,
     * 7) SUBCATEGORY.SUBCATEGORY_ID,
     * 8) SUBCATEGORY.NAME,
     * 9) SUBCATEGORY.DESCRIPTION,
     * 10) PRODUCT_PRICE_AVAILABILITY.AVAILABILITY,
     * 11) PRODUCT_RATING.RATING,
     * 12) CURRENCY.CURRENCY_ID,
     * 13) CURRENCY.UNICODE_DECIMAL,
     * 14) PRODUCT_PRICE_AVAILABILITY.PRICE
     * @param product
     * @param cursor
     */
    private void fillLightProductInfoFromCursor(Product product, Cursor cursor) {
        product.setId(cursor.getInt(0));
        product.setName(cursor.getString(1));
        product.setImageFileName(cursor.getString(2));
        product.setProductBrandId(cursor.getInt(3));
        product.getProductBrand().setId(cursor.getInt(3));
        product.getProductBrand().setName(cursor.getString(4));
        product.getProductBrand().setDescription(cursor.getString(5));
        product.setProductCategoryId(cursor.getInt(6));
        product.setProductSubCategoryId(cursor.getInt(7));
        product.getProductSubCategory().setProductCategoryId(cursor.getInt(6));
        product.getProductSubCategory().setId(cursor.getInt(7));
        product.getProductSubCategory().setName(cursor.getString(8));
        product.getProductSubCategory().setDescription(cursor.getString(9));
        product.getDefaultProductPriceAvailability().setAvailability(cursor.getInt(10));
        product.setRating(cursor.getFloat(11));
        product.getDefaultProductPriceAvailability().setCurrencyId(cursor.getInt(12));
        product.getDefaultProductPriceAvailability().getCurrency().setId(cursor.getInt(12));
        product.getDefaultProductPriceAvailability().getCurrency().setUnicodeDecimal(cursor.getString(13));
        product.getDefaultProductPriceAvailability().setPrice(cursor.getFloat(14));
        product.setFavorite(mOrderLineDB.isProductInWishList(product.getId()));
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
}

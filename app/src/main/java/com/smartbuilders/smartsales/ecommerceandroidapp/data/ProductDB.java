package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCommercialPackage;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Alberto on 22/4/2016.
 */
public class ProductDB {

    private Context mContext;
    private OrderLineDB mOrderLineDB;

    public ProductDB(Context context, User user){
        this.mContext = context;
        this.mOrderLineDB = new OrderLineDB(mContext, user);
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT DISTINCT P.PRODUCT_ID, P.NAME, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = P.PRODUCT_ID AND R.PRODUCT_ID = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? "  +
                    " ORDER BY R.TIMES DESC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(productId),
                    String.valueOf(1), "Y", "Y", String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setRating(c.getFloat(11));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
                        " S.DESCRIPTION, PA.AVAILABILITY, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.PRODUCT_ID <> ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
                            String.valueOf(subCategoryId), String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setRating(c.getFloat(11));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
                        " PA.AVAILABILITY, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.PRODUCT_ID <> ?  AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
                    String.valueOf(brandId), String.valueOf(productId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setRating(c.getFloat(11));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.SUBCATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
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
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    p.setInternalCode(c.getString(6));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(7), c.getString(8)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(9), c.getString(10)));
                    p.setProductCategory(new ProductCategory(c.getInt(11), c.getString(12), c.getString(13)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(11), c.getInt(1), c.getString(14), c.getString(15)));
                    p.setAvailability(c.getInt(16));
                    p.setImageFileName(c.getString(17));
                    p.setRating(c.getFloat(18));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE S.CATEGORY_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC ",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", String.valueOf(categoryId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    p.setInternalCode(c.getString(6));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(7), c.getString(8)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(9), c.getString(10)));
                    p.setProductCategory(new ProductCategory(c.getInt(11), c.getString(12), c.getString(13)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(11), c.getInt(1), c.getString(14), c.getString(15)));
                    p.setAvailability(c.getInt(16));
                    p.setImageFileName(c.getString(17));
                    p.setRating(c.getFloat(18));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                        " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.BRAND_ID = ? AND P.IS_ACTIVE = ? " +
                    " ORDER BY P.NAME ASC",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y",
                            String.valueOf(brandId), "Y"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    p.setInternalCode(c.getString(6));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(7), c.getString(8)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(9), c.getString(10)));
                    p.setProductCategory(new ProductCategory(c.getInt(11), c.getString(12), c.getString(13)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(11), c.getInt(1), c.getString(14), c.getString(15)));
                    p.setAvailability(c.getInt(16));
                    p.setImageFileName(c.getString(17));
                    p.setRating(c.getFloat(18));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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
        if(TextUtils.isEmpty(name) || name.length()>120
                || name.replaceAll("\\s+", " ").trim().split(" ").length>15){
            return products;
        }
        name = name.replaceAll("\\s+", " ").trim().toUpperCase();

        boolean isNumeric = false;
        // Regular expression in Java to check if String is number or not
        Pattern pattern = Pattern.compile(".*[^0-9].*");
        if(name.length()<8 && !pattern.matcher(name).matches()){
            isNumeric = true;
        }

        Cursor c = null;
        try {
            if(isNumeric) {
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                            " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                            " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = ? " +
                        " ORDER BY P.NAME ASC",
                        new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", name+"%", "Y"}, null);
            }else{
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                            " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, " +
                            " P.COMMERCIAL_PACKAGE, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID, PR.RATING " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                            " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                        " WHERE (replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE " +
                                    " OR replace(replace(replace(replace(replace(lower(P.NAME),'á','a'),'é','e'),'í','i'),'ó','o'),'ú','u') LIKE ? COLLATE NOCASE) " +
                                " AND P.IS_ACTIVE = ? " +
                        " ORDER BY P.NAME ASC",
                        new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", name+"%", "% "+name+"%", "Y"}, null);
            }
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5)) && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    p.setInternalCode(c.getString(6));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(7), c.getString(8)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(9), c.getString(10)));
                    p.setProductCategory(new ProductCategory(c.getInt(11), c.getString(12), c.getString(13)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(11), c.getInt(1), c.getString(14), c.getString(15)));
                    p.setAvailability(c.getInt(16));
                    p.setImageFileName(c.getString(17));
                    p.setRating(c.getFloat(18));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
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

        boolean isNumeric = false;
        // Regular expression in Java to check if String is number or not
        Pattern pattern = Pattern.compile(".*[^0-9].*");
        if(searchPattern.length()<8 && !pattern.matcher(searchPattern).matches()){
            isNumeric = true;
        }

        if(!isNumeric && (TextUtils.isEmpty(searchPattern) || searchPattern.length()<1)){
            return products;
        }

        Cursor c = null;
        try {
            if (isNumeric) {
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                        "SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.NAME), P.INTERNAL_CODE, S.NAME, S.DESCRIPTION " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
                        " WHERE P.INTERNAL_CODE LIKE ? AND P.IS_ACTIVE = ? "  +
                        " ORDER BY P.INTERNAL_CODE ASC LIMIT 20",
                        new String[]{"Y", "Y", "Y", "Y", searchPattern+"%", "Y"}, null);
            } else {
                String sql = "SELECT P.PRODUCT_ID, P.SUBCATEGORY_ID, UPPER(P.NAME), P.INTERNAL_CODE, S.NAME, S.DESCRIPTION " +
                        " FROM PRODUCT P " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? "+
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
                c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
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
                    p.setProductSubCategory(new ProductSubCategory(0, c.getInt(1), null, null));
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
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT DISTINCT P.PRODUCT_ID, P.SUBCATEGORY_ID, P.BRAND_ID, P.NAME, P.DESCRIPTION, P.PURPOSE, " +
                        " P.INTERNAL_CODE, P.COMMERCIAL_PACKAGE_UNITS, P.COMMERCIAL_PACKAGE, B.NAME, " +
                        " B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, PR.RATING " +
                    " FROM PRODUCT P " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = P.BRAND_ID AND B.IS_ACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = P.SUBCATEGORY_ID AND S.IS_ACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = P.PRODUCT_ID AND PA.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = P.PRODUCT_ID AND PI.PRIORITY = ? AND PI.IS_ACTIVE = ? " +
                        " LEFT JOIN PRODUCT_RATING PR ON PR.PRODUCT_ID = P.PRODUCT_ID AND PR.IS_ACTIVE = ? " +
                    " WHERE P.PRODUCT_ID = ? AND P.IS_ACTIVE = ?",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), "Y", "Y", String.valueOf(id), "Y"}, null);
            if (c!=null) {
                if(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    p.setInternalCode(c.getString(6));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(7), c.getString(8)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(9), c.getString(10)));
                    p.setProductCategory(new ProductCategory(c.getInt(11), c.getString(12), c.getString(13)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(11), c.getInt(1), c.getString(14), c.getString(15)));
                    p.setAvailability(c.getInt(16));
                    p.setImageFileName(c.getString(17));
                    p.setRating(c.getFloat(18));
                    p.setFavorite(mOrderLineDB.isProductInWishList(p.getId()));
                    return p;
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
        return null;
    }
}

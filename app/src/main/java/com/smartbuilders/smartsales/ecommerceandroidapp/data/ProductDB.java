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

    private Context context;
    private User user;

    public ProductDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "SELECT DISTINCT A.IDARTICULO, A.NOMBRE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, " +
                        " PA.AVAILABILITY, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = A.IDARTICULO AND R.PRODUCT_ID = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDARTICULO <> ? AND A.ACTIVO = ? "  +
                    " ORDER BY R.TIMES DESC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(productId),
                    String.valueOf(1), OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(productId), "V"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setFavorite(c.getInt(11)>0);
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
            String sql = "SELECT DISTINCT A.IDARTICULO, A.NOMBRE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, " +
                        " PA.AVAILABILITY, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDPARTIDA = ? AND A.IDARTICULO <> ? AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(subCategoryId), String.valueOf(productId), "V"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setFavorite(c.getInt(11)>0);
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
            String sql = "SELECT DISTINCT A.IDARTICULO, A.NOMBRE, PI.FILE_NAME, B.BRAND_ID, " +
                        " B.NAME, B.DESCRIPTION, S.CATEGORY_ID, S.SUBCATEGORY_ID, S.NAME, S.DESCRIPTION, " +
                        " PA.AVAILABILITY, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDMARCA = ? AND A.IDARTICULO <> ?  AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : "");
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(brandId), String.valueOf(productId), "V"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(1));
                    p.setImageFileName(c.getString(2));
                    p.setProductBrand(new ProductBrand(c.getInt(3), c.getString(4), c.getString(5)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(6), c.getInt(7), c.getString(8), c.getString(9)));
                    p.setAvailability(c.getInt(10));
                    p.setFavorite(c.getInt(11)>0);
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
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                        " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                        " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDPARTIDA = ? AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC ",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(subCategoryId), "V"}, null);
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
                            continue whileStatement;
                        }
                    }
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3)+" (Cod: "+c.getString(9)+")");
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    if(!TextUtils.isEmpty(c.getString(6))  && c.getString(6).length()>2) {
                        p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                    }
                    if(!TextUtils.isEmpty(c.getString(8))) {
                        p.setDescription(p.getDescription()+".\nOrigen: "+c.getString(8));
                    }
                    p.setImageFileName(c.getString(20));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                    p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                    p.setAvailability(c.getInt(19));
                    p.setFavorite(c.getInt(21)>0);
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
            String sql = "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                        " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                        " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE S.CATEGORY_ID = ? AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC ";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(categoryId), "V"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3)+" (Cod: "+c.getString(9)+")");
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    if(!TextUtils.isEmpty(c.getString(6))  && c.getString(6).length()>2) {
                        p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                    }
                    if(!TextUtils.isEmpty(c.getString(8))) {
                        p.setDescription(p.getDescription()+".\nOrigen: "+c.getString(8));
                    }
                    p.setImageFileName(c.getString(20));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                    p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                    p.setAvailability(c.getInt(19));
                    p.setFavorite(c.getInt(21)>0);
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
            String sql = "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                        " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                        " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDMARCA = ? AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(brandId), "V"}, null);
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3)+" (Cod: "+c.getString(9)+")");
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    if(!TextUtils.isEmpty(c.getString(6))  && c.getString(6).length()>2) {
                        p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                    }
                    if(!TextUtils.isEmpty(c.getString(8))) {
                        p.setDescription(p.getDescription()+".\nOrigen: "+c.getString(8));
                    }
                    p.setImageFileName(c.getString(20));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                    p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                    p.setAvailability(c.getInt(19));
                    p.setFavorite(c.getInt(21)>0);
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
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                                .build(), null,
                        "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                            " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                            " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                        " FROM ARTICULOS A " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                            " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                        " WHERE A.CODVIEJO LIKE ? AND A.ACTIVO = ? " +
                        " ORDER BY A.NOMBRE ASC",
                        new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), OrderLineDB.WISHLIST_DOCTYPE,
                                name+"%", "V"}, null);
            }else{
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null,
                        "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                            " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                            " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                        " FROM ARTICULOS A " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                            " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                        " WHERE (A.NOMBRE LIKE ? COLLATE NOCASE OR A.NOMBRE LIKE ? COLLATE NOCASE) AND A.ACTIVO = ? " +
                        " ORDER BY A.NOMBRE ASC",
                        new String[]{"Y", "Y", "Y", "Y", String.valueOf(1), OrderLineDB.WISHLIST_DOCTYPE,
                        name+"%", "% "+name+"%", "V"}, null);
            }
            if (c!=null) {
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3)+" (Cod: "+c.getString(9)+")");
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5)) && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    if(!TextUtils.isEmpty(c.getString(6)) && c.getString(6).length()>2) {
                        p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                    }
                    if(!TextUtils.isEmpty(c.getString(8))) {
                        p.setDescription(p.getDescription()+".\nOrigen: "+c.getString(8));
                    }
                    p.setImageFileName(c.getString(20));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                    p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                    p.setAvailability(c.getInt(19));
                    p.setFavorite(c.getInt(21)>0);
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
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null,
                        "SELECT A.IDARTICULO, A.IDPARTIDA, UPPER(A.NOMBRE), A.CODVIEJO, S.NAME, S.DESCRIPTION " +
                        " FROM ARTICULOS A " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " WHERE A.CODVIEJO LIKE ? AND A.ACTIVO = ? "  +
                        " ORDER BY A.CODVIEJO ASC LIMIT 20",
                        new String[]{"Y", "Y", "Y", "Y", searchPattern+"%", "V"}, null);
            } else {
                String sql = "SELECT A.IDARTICULO, A.IDPARTIDA, UPPER(A.NOMBRE), A.CODVIEJO, S.NAME, S.DESCRIPTION " +
                        " FROM ARTICULOS A " +
                            " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                            " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                            " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? "+
                        " WHERE (A.NOMBRE LIKE ? COLLATE NOCASE OR A.NOMBRE LIKE ? COLLATE NOCASE) AND A.ACTIVO = ? " +
                        " ORDER BY A.NOMBRE ASC " +
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
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                        .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                        .build(), null, sql, new String[]{"Y", "Y", "Y", "Y", firstWord, aux, "V"}, null);
            }

            if (c!=null) {
                boolean searchPatternIsOneWord = searchPattern.split(" ").length==1;
                whileStatement:
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
                        p.setName("Cod: "+c.getString(3)+" - " + c.getString(2));
                    }

                    p.setProductSubCategory(new ProductSubCategory(0, c.getInt(1), null, null));
                    if(!isNumeric && products.contains(p)){
                        continue whileStatement;
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

    public Product getProductById(int id, boolean useProductCode){
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                    .build(), null,
                    "SELECT DISTINCT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                        " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                        " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, PI.FILE_NAME, OL.PRODUCT_ID " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = ? " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = ? " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = ? " +
                        " LEFT JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = ? " +
                        " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = A.IDARTICULO AND PI.PRIORITY = ? " +
                        " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = ? " +
                    " WHERE A.IDARTICULO = ? AND A.ACTIVO = ? " +
                    " ORDER BY A.NOMBRE ASC",
                    new String[]{"Y", "Y", "Y", "Y", String.valueOf(1),
                    OrderLineDB.WISHLIST_DOCTYPE, String.valueOf(id), "V"}, null);
            if (c!=null) {
                if(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(0));
                    p.setName(c.getString(3) + (useProductCode ? " (Cod: "+c.getString(9)+")" : ""));
                    p.setDescription(c.getString(4));
                    if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                        p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                    }
                    if(!TextUtils.isEmpty(c.getString(6)) && c.getString(6).length()>2) {
                        p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                    }
                    if(!TextUtils.isEmpty(c.getString(8))) {
                        p.setDescription(p.getDescription()+".\nOrigen: "+c.getString(8));
                    }
                    p.setImageFileName(c.getString(20));
                    p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                    p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                    p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                    p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                    p.setAvailability(c.getInt(19));
                    p.setFavorite(c.getInt(21)>0);
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

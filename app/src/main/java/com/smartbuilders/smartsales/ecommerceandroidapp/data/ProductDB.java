package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
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
    private DatabaseHelper dbh;

    public ProductDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<Product> getRelatedShoppingProductsByProductId(int productId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                        " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                        " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                        " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' "+ //AND PA.AVAILABILITY>0 " +
                        " INNER JOIN PRODUCT_SHOPPING_RELATED R ON R.PRODUCT_RELATED_ID = A.IDARTICULO AND R.PRODUCT_ID = " + productId +
                    " WHERE A.IDARTICULO <> " + productId +
                    " ORDER BY R.TIMES DESC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : ""), null);
            while(c.moveToNext()){
                Product p = new Product();
                p.setId(c.getInt(0));
                p.setName(c.getString(3));
                p.setDescription(c.getString(4));
                if(!TextUtils.isEmpty(c.getString(5))  && c.getString(5).length()>2) {
                    p.setDescription(p.getDescription()+".\nUso: "+c.getString(5));
                }
                if(!TextUtils.isEmpty(c.getString(6))  && c.getString(6).length()>2) {
                    p.setDescription(p.getDescription()+".\nObservaciones: "+c.getString(6));
                }
                if(!TextUtils.isEmpty(c.getString(8))) {
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsBySubCategoryId(int subCategoryId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                    " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                    " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                    " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y'"+// AND PA.AVAILABILITY>0 " +
                    " WHERE A.IDPARTIDA = "+subCategoryId + " ORDER BY A.NOMBRE ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : ""), null);
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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsByBrandId(int brandId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                    " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                    " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                    " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                    " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                    " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                    " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                    " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y'"+
                    " WHERE B.BRAND_ID = "+brandId + " ORDER BY A.NOMBRE ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : ""), null);
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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsByCategoryId(int categoryId, Integer limit){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                    " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                    " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                    " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' "+// AND PA.AVAILABILITY>0 " +
                    " WHERE S.CATEGORY_ID = "+categoryId + " ORDER BY A.NOMBRE ASC " +
                    ((limit!=null && limit>0) ? " LIMIT " + limit : ""), null);

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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsByBrandId(int brandId){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                    " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                    " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                    " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' "+// AND PA.AVAILABILITY>0 " +
                    " WHERE A.IDMARCA = "+brandId+" ORDER BY A.NOMBRE ASC", null);
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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                    " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                    " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                    " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' " +// AND PA.AVAILABILITY>0 " +
                    " WHERE A.NOMBRE LIKE '"+name.replaceAll("\\s+", " ").trim()+"%' COLLATE NOCASE " +
                    " OR A.NOMBRE LIKE '% "+name.replaceAll("\\s+", " ").trim()+"%' COLLATE NOCASE " +
                    " ORDER BY A.NOMBRE ASC", null);
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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public ArrayList<Product> getLightProductsByName(String name){
        ArrayList<Product> products = new ArrayList<>();
        if(TextUtils.isEmpty(name)){
            return products;
        }
        name = name.replaceAll("\\s+", " ").trim().toUpperCase();

        boolean isNumeric = false;
        // Regular expression in Java to check if String is number or not
        Pattern pattern = Pattern.compile(".*[^0-9].*");
        if(name.length()<8 && !pattern.matcher(name).matches()){
            isNumeric = true;
        }

        if(!isNumeric && (TextUtils.isEmpty(name) || name.length()<1)){
            return products;
        }

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, UPPER(A.NOMBRE), A.CODVIEJO, S.NAME, S.DESCRIPTION " +
                    " FROM ARTICULOS A " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' "+// AND PA.AVAILABILITY>0 " +
                    (isNumeric ? " WHERE A.CODVIEJO LIKE '"+name+"%' "
                            : " WHERE A.NOMBRE LIKE '"+name+"%' COLLATE NOCASE " +
                            (name.length() > 1 ? " OR A.NOMBRE LIKE '% "+name+"%' COLLATE NOCASE " : "")) +
                    (isNumeric ? " ORDER BY A.CODVIEJO ASC LIMIT 15"
                            : (name.length()==1 ? " ORDER BY A.NOMBRE ASC LIMIT 50" : " ORDER BY A.NOMBRE ASC")), null);
            whileStatement:
            while(c.moveToNext()){
                Product p = new Product();
                p.setId(c.getInt(0));
                if (!isNumeric) {
                    for(String aux : c.getString(2).replaceAll("\\s+", " ").split(" ")){
                        p.setName(p.getName()==null ? aux : p.getName() + " " +  aux);
                        if(aux.contains(name)){
                            break;
                        }
                    }
                    if(p.getName()==null){
                        p.setName(c.getString(2));
                    }
                } else {
                    p.setName("Cod: "+c.getString(3)+" - " + c.getString(2));
                }

                p.setProductSubCategory(new ProductSubCategory(0, c.getInt(1), c.getString(4), c.getString(5)));
                if(!isNumeric) {
                    for(Product aux : products){
                        if(aux.equals(p)){
                            continue whileStatement;
                        }
                    }
                }
                products.add(p);
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return products;
    }

    public Product getProductById(int id, boolean useProductCode){
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT A.IDARTICULO, A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, " +
                            " A.OBSERVACIONES, A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, " +
                            " A.EMPAQUE_COMERCIAL, B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, " +
                            " S.DESCRIPTION, PA.AVAILABILITY, A.NOMBRE_ARCHIVO_IMAGEN " +
                            " FROM ARTICULOS A " +
                                " INNER JOIN BRAND B ON B.BRAND_ID = IDMARCA AND B.ISACTIVE = 'Y' " +
                                " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                                " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                                " LEFT JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO AND PA.ISACTIVE = 'Y' " +
                            " WHERE A.IDARTICULO = "+id+" ORDER BY A.NOMBRE ASC", null);
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
                    p.setDescription(p.getDescription()+".\nNacionalidad: "+c.getString(8));
                }
                p.setImageFileName(c.getString(20));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(10), c.getString(11)));
                p.setProductBrand(new ProductBrand(c.getInt(2), c.getString(12), c.getString(13)));
                p.setProductCategory(new ProductCategory(c.getInt(14), c.getString(15), c.getString(16)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(14), c.getInt(1), c.getString(17), c.getString(18)));
                p.setAvailability(c.getInt(19));
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
            if(db!=null){
                try {
                    db.close();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProduct;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrand;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductCommercialPackage;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductSubCategory;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductDB {

    private Context context;
    private User user;
    private DatabaseHelper dbh;

    public MainPageProductDB(Context context, User user){
        this.context = context;
        this.user = user;
        this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<MainPageProduct> getActiveMainPageProductsByMainPageSectionId(int mainPageSectionId){
        ArrayList<MainPageProduct> mainPageProducts = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = dbh.getReadableDatabase();
            c = db.rawQuery("SELECT M.MAINPAGE_PRODUCT_ID, M.MAINPAGE_SECTION_ID, M.PRODUCT_ID, " +
                        " A.IDPARTIDA, A.IDMARCA, A.NOMBRE, A.DESCRIPCION, A.USO, A.OBSERVACIONES, " +
                        " A.IDREFERENCIA, A.NACIONALIDAD, A.CODVIEJO, A.UNIDADVENTA_COMERCIAL, A.EMPAQUE_COMERCIAL, " +
                        " B.NAME, B.DESCRIPTION, C.CATEGORY_ID, C.NAME, C.DESCRIPTION, S.NAME, S.DESCRIPTION, A.NOMBRE_ARCHIVO_IMAGEN " +
                    " FROM MAINPAGE_PRODUCT M " +
                        " INNER JOIN ARTICULOS A ON A.IDARTICULO = M.PRODUCT_ID " +
                        " INNER JOIN BRAND B ON B.BRAND_ID = A.IDMARCA AND B.ISACTIVE = 'Y' " +
                        " INNER JOIN SUBCATEGORY S ON S.SUBCATEGORY_ID = A.IDPARTIDA AND S.ISACTIVE = 'Y' " +
                        " INNER JOIN CATEGORY C ON C.CATEGORY_ID = S.CATEGORY_ID AND C.ISACTIVE = 'Y' " +
                    " WHERE M.ISACTIVE = 'Y' AND M.MAINPAGE_SECTION_ID = " + mainPageSectionId +
                    " ORDER BY M.PRIORITY ASC", null);
            while(c.moveToNext()){
                MainPageProduct mainPageProduct = new MainPageProduct();
                mainPageProduct.setId(c.getInt(0));
                mainPageProduct.setMainPageSectionId(c.getInt(1));
                mainPageProduct.setProductId(c.getInt(2));
                Product p = new Product();
                p.setId(c.getInt(2));
                p.setName(c.getString(5));
                p.setDescription(c.getString(6));
                p.setImageFileName(c.getString(21));
                p.setProductCommercialPackage(new ProductCommercialPackage(c.getInt(12), c.getString(13)));
                p.setProductBrand(new ProductBrand(c.getInt(4), c.getString(14), c.getString(15)));
                p.setProductCategory(new ProductCategory(c.getInt(16), c.getString(17), c.getString(18)));
                p.setProductSubCategory(new ProductSubCategory(c.getInt(16), c.getInt(3), c.getString(19), c.getString(20)));
                mainPageProduct.setProduct(p);
                mainPageProducts.add(mainPageProduct);
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
        return mainPageProducts;
    }
}

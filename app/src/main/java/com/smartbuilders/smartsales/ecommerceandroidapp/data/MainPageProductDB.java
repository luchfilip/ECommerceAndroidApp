package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProduct;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductDB {

    private Context context;
    private User user;
    //private DatabaseHelper dbh;

    public MainPageProductDB(Context context, User user){
        this.context = context;
        this.user = user;
        //this.dbh = new DatabaseHelper(context, user);
    }

    public ArrayList<MainPageProduct> getActiveMainPageProductsByMainPageSectionId(int mainPageSectionId){
        ArrayList<MainPageProduct> mainPageProducts = new ArrayList<>();
        //SQLiteDatabase db = null;
        Cursor c = null;
        try {
            //db = dbh.getReadableDatabase();
            String sql = "SELECT DISTINCT M.MAINPAGE_PRODUCT_ID, M.MAINPAGE_SECTION_ID, M.PRODUCT_ID, " +
                    " A.NOMBRE, PI.FILE_NAME, PA.AVAILABILITY, OL.PRODUCT_ID " +
                    " FROM MAINPAGE_PRODUCT M " +
                    " INNER JOIN ARTICULOS A ON A.IDARTICULO = M.PRODUCT_ID " +
                    " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = M.PRODUCT_ID AND PI.PRIORITY = 1 " +
                    " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO " +
                    " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = '" + OrderLineDB.WISHLIST_DOCTYPE + "' " +
                    " WHERE M.ISACTIVE = 'Y' AND M.MAINPAGE_SECTION_ID = " + mainPageSectionId +
                    " ORDER BY M.PRIORITY ASC";
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                                                    .build(), null, sql, null, null);
            //c = db.rawQuery("SELECT DISTINCT M.MAINPAGE_PRODUCT_ID, M.MAINPAGE_SECTION_ID, M.PRODUCT_ID, " +
            //            " A.NOMBRE, PI.FILE_NAME, PA.AVAILABILITY, OL.PRODUCT_ID " +
            //        " FROM MAINPAGE_PRODUCT M " +
            //            " INNER JOIN ARTICULOS A ON A.IDARTICULO = M.PRODUCT_ID " +
            //            " LEFT JOIN PRODUCT_IMAGE PI ON PI.PRODUCT_ID = M.PRODUCT_ID AND PI.PRIORITY = 1 " +
            //            " INNER JOIN PRODUCT_AVAILABILITY PA ON PA.PRODUCT_ID = A.IDARTICULO " +
            //            " LEFT JOIN ECOMMERCE_ORDERLINE OL ON OL.PRODUCT_ID = A.IDARTICULO AND OL.DOC_TYPE = '" + OrderLineDB.WISHLIST_DOCTYPE + "' " +
            //        " WHERE M.ISACTIVE = 'Y' AND M.MAINPAGE_SECTION_ID = " + mainPageSectionId +
            //        " ORDER BY M.PRIORITY ASC", null);
            while(c.moveToNext()){
                MainPageProduct mainPageProduct = new MainPageProduct();
                mainPageProduct.setId(c.getInt(0));
                mainPageProduct.setMainPageSectionId(c.getInt(1));
                mainPageProduct.setProductId(c.getInt(2));
                Product p = new Product();
                p.setId(c.getInt(2));
                p.setName(c.getString(3));
                p.setImageFileName(c.getString(4));
                p.setAvailability(c.getInt(5));
                p.setFavorite(c.getInt(6)>0);
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
            //if(db!=null){
            //    try {
            //        db.close();
            //    } catch (Exception e){
            //        e.printStackTrace();
            //    }
            //}
        }
        return mainPageProducts;
    }
}

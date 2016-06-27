package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;

import java.util.ArrayList;

/**
 * Created by Alberto on 25/4/2016.
 */
public class MainPageProductSectionDB {

    private Context context;
    private User user;

    public MainPageProductSectionDB(Context context, User user){
        this.context = context;
        this.user = user;
    }

    public ArrayList<MainPageProductSection> getActiveMainPageProductSections(){
        ArrayList<MainPageProductSection> mainPageProductSections = new ArrayList<>();
        Cursor c = null;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
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
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT DISTINCT M.MAINPAGE_PRODUCT_ID, M.MAINPAGE_PRODUCT_SECTION_ID, M.PRODUCT_ID " +
                    " FROM MAINPAGE_PRODUCT M " +
                        " INNER JOIN PRODUCT P ON P.PRODUCT_ID = M.PRODUCT_ID AND P.IS_ACTIVE = ? " +
                    " WHERE M.MAINPAGE_PRODUCT_SECTION_ID = ? AND M.IS_ACTIVE = ? " +
                    " ORDER BY M.PRIORITY ASC",
                    new String[]{"Y", String.valueOf(productSectionId), "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Product p = new Product();
                    p.setId(c.getInt(2));
                    productsByProductSectionId.add(p);
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

        if(!productsByProductSectionId.isEmpty()){
            ProductDB productDB = new ProductDB(context, user);
            for (int i = 0; i < productsByProductSectionId.size(); i++){
                productsByProductSectionId.set(i, productDB.getProductById(productsByProductSectionId.get(i).getId()));
            }
        }
        return productsByProductSectionId;
    }
}

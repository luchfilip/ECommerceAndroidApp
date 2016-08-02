package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.ProductTax;

import java.util.ArrayList;

/**
 * Created by stein on 16/7/2016.
 */
public class ProductTaxDB {

    private Context mContext;
    private User mUser;

    public ProductTaxDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ProductTax getActiveTaxById(int taxId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT PERCENTAGE, NAME FROM PRODUCT_TAX WHERE PRODUCT_TAX_ID=? AND IS_ACTIVE=?",
                    new String[]{String.valueOf(taxId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                ProductTax productTax = new ProductTax();
                productTax.setId(taxId);
                productTax.setPercentage(c.getFloat(0));
                productTax.setName(c.getString(1));
                return productTax;
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

    public ArrayList<ProductTax> getActiveTaxesList() {
        ArrayList<ProductTax> activeProductTaxes = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "SELECT TAX_ID, PERCENTAGE, NAME FROM TAX WHERE IS_ACTIVE=?",
                    new String[]{}, null);
            if(c!=null) {
                while(c.moveToNext()) {
                    ProductTax productTax = new ProductTax();
                    productTax.setId(c.getInt(0));
                    productTax.setPercentage(c.getFloat(1));
                    productTax.setName(c.getString(2));
                    activeProductTaxes.add(productTax);
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
        return activeProductTaxes;
    }
}

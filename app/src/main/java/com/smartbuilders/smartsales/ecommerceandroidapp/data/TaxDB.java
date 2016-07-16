package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Tax;

import java.util.ArrayList;

/**
 * Created by stein on 16/7/2016.
 */
public class TaxDB {

    private Context mContext;

    public TaxDB(Context context){
        this.mContext = context;
    }

    public Tax getActiveTaxById(int taxId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT PERCENTAGE, NAME FROM TAX WHERE TAX_ID=? AND IS_ACTIVE=?",
                    new String[]{String.valueOf(taxId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                Tax tax = new Tax();
                tax.setId(taxId);
                tax.setPercentage(c.getFloat(0));
                tax.setName(c.getString(1));
                return tax;
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

    public ArrayList<Tax> getActiveTaxesList() {
        ArrayList<Tax> activeTaxes = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT TAX_ID, PERCENTAGE, NAME FROM TAX WHERE IS_ACTIVE=?",
                    new String[]{}, null);
            if(c!=null) {
                while(c.moveToNext()) {
                    Tax tax = new Tax();
                    tax.setId(c.getInt(0));
                    tax.setPercentage(c.getFloat(1));
                    tax.setName(c.getString(2));
                    activeTaxes.add(tax);
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
        return activeTaxes;
    }
}

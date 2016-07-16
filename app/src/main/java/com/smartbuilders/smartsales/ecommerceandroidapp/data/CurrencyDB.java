package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Currency;

import java.util.ArrayList;

/**
 * Created by stein on 16/7/2016.
 */
public class CurrencyDB {

    private Context mContext;

    public CurrencyDB(Context context){
        this.mContext = context;
    }

    public Currency getActiveCurrencyById(int taxId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT COUNTRY_NAME, CURRENCY_NAME, CODE, UNICODE_DECIMAL, UNICODE_HEX " +
                            " FROM CURRENCY WHERE CURRENCY_ID=? AND IS_ACTIVE=?",
                    new String[]{String.valueOf(taxId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                Currency currency = new Currency();
                currency.setId(taxId);
                currency.setCountryName(c.getString(0));
                currency.setCurrencyName(c.getString(1));
                currency.setInternationalCode(c.getString(2));
                currency.setUnicodeDecimal(c.getString(3));
                currency.setUnicodeHex(c.getString(4));
                return currency;
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

    public ArrayList<Currency> getActiveCurrenciesList() {
        ArrayList<Currency> activeCurrencies = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT CURRENCY_ID, COUNTRY_NAME, CURRENCY_NAME, CODE, UNICODE_DECIMAL, UNICODE_HEX " +
                            " FROM CURRENCY WHERE IS_ACTIVE=?",
                    new String[]{}, null);
            if(c!=null) {
                while (c.moveToNext()){
                    Currency currency = new Currency();
                    currency.setId(c.getInt(0));
                    currency.setCountryName(c.getString(1));
                    currency.setCurrencyName(c.getString(2));
                    currency.setInternationalCode(c.getString(3));
                    currency.setUnicodeDecimal(c.getString(4));
                    currency.setUnicodeHex(c.getString(5));
                    activeCurrencies.add(currency);
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
        return activeCurrencies;
    }
}

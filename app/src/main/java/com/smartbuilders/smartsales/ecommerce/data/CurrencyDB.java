package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Currency;

import java.util.ArrayList;

/**
 * Created by stein on 16/7/2016.
 */
public class CurrencyDB {

    private Context mContext;
    private User mUser;

    public CurrencyDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public Currency getActiveCurrencyById(int taxId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
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

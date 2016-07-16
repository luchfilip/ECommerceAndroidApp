package com.smartbuilders.smartsales.ecommerceandroidapp.model;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ParameterDB;

/**
 * Created by stein on 16/7/2016.
 */
public class Parameter {

    public static int getDefaultCurrencyId(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.DEFAULT_CURRENCY_ID_PARAM_ID);
    }

    public static int getDefaultTaxId(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.DEFAULT_TAX_ID_PARAM_ID);
    }
}

package com.smartbuilders.smartsales.ecommerceandroidapp.session;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ParameterDB;

/**
 * Created by stein on 16/7/2016.
 */
public class Parameter {

    /**
     * default 0
     * @param context
     * @param user
     * @return
     */
    public static int getDefaultCurrencyId(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.DEFAULT_CURRENCY_ID_PARAM_ID, 0);
    }

    /**
     * default 0
     * @param context
     * @param user
     * @return
     */
    public static int getDefaultTaxId(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.DEFAULT_TAX_ID_PARAM_ID, 0);
    }

    /**
     * default false
     * @param context
     * @param user
     * @return
     */
    public static boolean isManagePriceInOrder(Context context, User user){
        return ParameterDB.getParameterBooleanValue(context, user, ParameterDB.MANAGE_PRICE_IN_ORDER, false);
    }

    /**
     * default 1500
     * @param context
     * @param user
     * @return
     */
    public static int getConnectionTimeOutValue(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.CONNECTION_TIME_OUT_VALUE, 1500);
    }

    /**
     * default 9000
     * @param context
     * @param user
     * @return
     */
    public static int getBatchSizeForQueryResult(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.BATCH_SIZE_FOR_QUERY_RESULT, 9000);
    }
}

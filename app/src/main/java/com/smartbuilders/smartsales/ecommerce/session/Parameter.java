package com.smartbuilders.smartsales.ecommerce.session;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.data.ParameterDB;

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
     * default 30*1000
     * @param context
     * @param user
     * @return
     */
    public static int getConnectionTimeOutValue(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.CONNECTION_TIME_OUT_VALUE, 30*1000);
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

    /**
     * devuelve el tiempo por defecto, en segundos, que debe transcurrir entre cada sincronizacion,
     * default 3600 segundos = 60 minutos
     * @param context
     * @param user
     * @return
     */
    public static int getDefaultSyncPeriodicity(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.DEFAULT_SYNCHRONIZATION_PERIODICITY,
                Integer.valueOf(context.getString(R.string.sync_periodicity_default_value)));
    }

    /**
     * indica si se usará la base de datos de forma remota
     * @param context
     * @param user
     * @return
     */
    public static boolean isUseRemoteDataBase(Context context, User user){
        return user.getUserProfileId()== UserProfile.BUSINESS_PARTNER_PROFILE_ID;
    }

    /**
     * devuelve el numero de version actual de la aplicacion que se encuentra en produccion
     * @param context
     * @return
     */
    public static int getMarketAppVersion(Context context){
        return ParameterDB.getParameterIntValue(context, ParameterDB.APP_CURRENT_VERSION, 0);
    }

    /**
     * devuelve el numero de la ultima version de la aplicacion que se tiene como obligatoria
     * @param context
     * @return
     */
    public static int getLastMandatoryAppVersion(Context context){
        return ParameterDB.getParameterIntValue(context, ParameterDB.LAST_MANDATORY_APP_VERSION, 0);
    }

    /**
     * indica si se mostrara o no el ratingBar de los productos
     * @param context
     * @return
     */
    public static boolean showProductRatingBar(Context context, User user){
        return ParameterDB.getParameterBooleanValue(context, user, ParameterDB.SHOW_RATING_BAR, true);
    }

    /**
     * devuelve el texto que se colocara en el ratingBar
     * @param context
     * @return
     */
    public static String getProductRatingBarLabelText(Context context, User user){
        return ParameterDB.getParameterStringValue(context, user, ParameterDB.RATING_BAR_LABEL_TEXT, "");
    }
}

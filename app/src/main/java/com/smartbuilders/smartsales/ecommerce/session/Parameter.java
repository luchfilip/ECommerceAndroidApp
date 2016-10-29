package com.smartbuilders.smartsales.ecommerce.session;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
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
        return BuildConfig.IS_SALES_FORCE_SYSTEM ||
                ParameterDB.getParameterBooleanValue(context, user, ParameterDB.MANAGE_PRICE_IN_ORDER, false);
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
     * @param user
     * @return
     */
    public static int getMarketAppVersion(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.APP_CURRENT_VERSION, 0);
    }

    /**
     * devuelve el numero de la ultima version de la aplicacion que se tiene como obligatoria
     * @param context
     * @param user
     * @return
     */
    public static int getLastMandatoryAppVersion(Context context, User user){
        return ParameterDB.getParameterIntValue(context, user, ParameterDB.LAST_MANDATORY_APP_VERSION, 0);
    }

    /**
     * indica si se mostrara o no el ratingBar de los productos
     * @param context
     * @return
     */
    public static boolean showProductRatingBar(Context context, User user){
        return !BuildConfig.IS_SALES_FORCE_SYSTEM
                && ParameterDB.getParameterBooleanValue(context, user, ParameterDB.SHOW_RATING_BAR, true);
    }

    /**
     * devuelve el texto que se colocara en el ratingBar
     * @param context
     * @return
     */
    public static String getProductRatingBarLabelText(Context context, User user){
        return ParameterDB.getParameterStringValue(context, user, ParameterDB.RATING_BAR_LABEL_TEXT, "");
    }

    /**
     * devuelve el correo electronico que se usa cuando se reportan errores de la aplicacion
     * @param context
     * @return
     */
    public static String getReportErrorEmail(Context context, User user){
        return ParameterDB.getParameterStringValue(context, user, ParameterDB.REPORT_ERROR_EMAIL, "");
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductImages(Context context, User user) {
        return ParameterDB.getParameterBooleanValue(context, user, ParameterDB.SHOW_PRODUCT_IMAGES, true);
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean isActiveOrderTracking(Context context, User user) {
        return ParameterDB.getParameterBooleanValue(context, user, ParameterDB.IS_ACTIVE_ORDER_TRACKING, false);
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductTax(Context context, User user) {
        return false;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductPrice(Context context, User user) {
        return true;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductTotalPrice(Context context, User user) {
        return false;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductTaxInOrderLine(Context context, User user) {
        return false;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductPriceInOrderLine(Context context, User user) {
        return true;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showProductTotalPriceInOrderLine(Context context, User user) {
        return false;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showSubTotalLineAmountInOrderLine(Context context, User user) {
        return true;
    }

    /**
     *
     * @param context
     * @param user
     * @return
     */
    public static boolean showTotalLineAmountInOrderLine(Context context, User user) {
        return false;
    }
}

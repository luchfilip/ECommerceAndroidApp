package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by stein on 16/7/2016.
 */
public class ParameterDB {

    private static final String TEXT_VALUE_COLUMN_NAME = "TEXT_VALUE";
    private static final String INTEGER_VALUE_COLUMN_NAME = "INTEGER_VALUE";
    private static final String DOUBLE_VALUE_COLUMN_NAME = "DOUBLE_VALUE";
    private static final String BOOLEAN_VALUE_COLUMN_NAME = "BOOLEAN_VALUE";
    private static final String DATE_VALUE_COLUMN_NAME = "DATE_VALUE";
    private static final String DATETIME_VALUE_COLUMN_NAME = "DATETIME_VALUE";

    public static final int DEFAULT_CURRENCY_ID_PARAM_ID = 1;
    public static final int DEFAULT_TAX_ID_PARAM_ID = 2;
    public static final int MANAGE_PRICE_IN_ORDER = 3;
    public static final int CONNECTION_TIME_OUT_VALUE = 4;
    public static final int BATCH_SIZE_FOR_QUERY_RESULT = 5;
    public static final int DEFAULT_SYNCHRONIZATION_PERIODICITY = 6;
    public static final int APP_CURRENT_VERSION = 7;
    public static final int LAST_MANDATORY_APP_VERSION = 8;
    public static final int SHOW_RATING_BAR = 9;
    public static final int RATING_BAR_LABEL_TEXT = 10;
    public static final int REPORT_ERROR_EMAIL = 11;
    public static final int SHOW_PRODUCT_IMAGES = 12;
    public static final int IS_ACTIVE_ORDER_TRACKING = 13;
    public static final int THUMB_IMAGES_REQUIRED_DISK_SPACE = 14;
    public static final int ORIGINAL_IMAGES_REQUIRED_DISK_SPACE = 15;
    public static final int SHOW_PRICE_IN_SHARE_PRODUCT_CARD = 16;
    public static final int SHOW_PRICE_IN_WISH_LIST_PDF = 17;
    public static final int SHOW_PRICE_IN_RECOMMENDED_PRODUCTS_PDF = 18;
    public static final int SHOW_PRODUCTS_WITHOUT_AVAILABILITY = 19;
    public static final int SHOW_QUERIES_MENU = 20;
    public static final int IS_REQUEST_PRICE_AVAILABLE = 21;
    public static final int IS_DEACTIVE_ORDER_AVAILABLE = 22;

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static String getParameterStringValue(Context context, User user, int parameterId, String defaultValue) {
        try {
            return (String) getParameterValue(context, user, parameterId, TEXT_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static int getParameterIntValue(Context context, User user, int parameterId, int defaultValue) {
        try {
            return (int) getParameterValue(context, user, parameterId, INTEGER_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static double getParameterDoubleValue(Context context, User user, int parameterId, double defaultValue) {
        try {
            return (double) getParameterValue(context, user, parameterId, DOUBLE_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static boolean getParameterBooleanValue(Context context, User user, int parameterId, boolean defaultValue) {
        try {
            return (boolean) getParameterValue(context, user, parameterId, BOOLEAN_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static Date getParameterDateValue(Context context, User user, int parameterId, Date defaultValue) {
        try {
            return (Date) getParameterValue(context, user, parameterId, DATE_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Devuelve el valor del parametro segun la tabla USER_APP_PARAMETER o APP_PARAMETER
     * @param context
     * @param parameterId
     * @param defaultValue
     * @return
     */
    public static Timestamp getParameterTimestampValue(Context context, User user, int parameterId, Timestamp defaultValue) {
        try {
            return (Timestamp) getParameterValue(context, user, parameterId, DATETIME_VALUE_COLUMN_NAME);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static Object getParameterValue(Context context, User user, int parameterId,
                                            final String tableColumn) throws Exception {
        Cursor c = null;
        String result = null;
        boolean paramFound = false;
        try {
            c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                            .build(), null,
                    "SELECT "+tableColumn+" FROM USER_APP_PARAMETER WHERE APP_PARAMETER_ID=? AND USER_ID=? AND IS_ACTIVE=?" ,
                    new String[]{String.valueOf(parameterId), String.valueOf(user.getServerUserId()), "Y"}, null);
            if(c!=null && c.moveToNext()){
                result = c.getString(0);
                paramFound = true;
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
        if (!paramFound) {
            try {
                c = context.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                                .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, user.getUserId())
                                .build(), null,
                        "SELECT "+tableColumn+" FROM APP_PARAMETER WHERE APP_PARAMETER_ID=? AND IS_ACTIVE=?" ,
                        new String[]{String.valueOf(parameterId), "Y"}, null);
                if(c!=null && c.moveToNext()){
                    result = c.getString(0);
                    paramFound = true;
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
        }
        if(paramFound){
            switch (tableColumn) {
                case INTEGER_VALUE_COLUMN_NAME:
                    return Integer.valueOf(result);
                case DOUBLE_VALUE_COLUMN_NAME:
                    return Double.valueOf(result);
                case BOOLEAN_VALUE_COLUMN_NAME:
                    return String.valueOf(result).equalsIgnoreCase("Y");
                case DATE_VALUE_COLUMN_NAME:
                    try {
                        return (new SimpleDateFormat("yyyy-MM-dd")).parse(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DATETIME_VALUE_COLUMN_NAME:
                    try{
                        return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(result).getTime());
                    }catch(ParseException ex){
                        try {
                            return new Timestamp(new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSSSSS").parse(result).getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TEXT_VALUE_COLUMN_NAME:
                    return result;
            }
            return result;
        } else {
            throw new Exception("The parameter with the id: " + parameterId + " was not found.");
        }
    }

}

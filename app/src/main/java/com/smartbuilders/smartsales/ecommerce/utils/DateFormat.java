package com.smartbuilders.smartsales.ecommerce.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stein on 28/7/2016.
 */
public class DateFormat {

    public static String getCurrentDateTimeSQLFormat(){
        return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", new Locale("es","VE"))).format(new Date());
    }
}

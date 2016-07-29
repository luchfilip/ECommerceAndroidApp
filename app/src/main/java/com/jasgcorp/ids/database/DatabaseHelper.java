package com.jasgcorp.ids.database;

import com.jasgcorp.ids.logsync.LogSyncData;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 9;
	private static final String DATABASE_NAME = "IDS_DATABASE";
//    private static final int DB_NOT_FOUND = 0;
//    private static final int USING_INTERNAL_STORAGE = 1;
//    private static final int USING_EXTERNAL_STORAGE = 2;
    private String dataBaseName;

    private static final String CREATE_USER_TABLE 	=
            "CREATE TABLE IF NOT EXISTS IDS_USER (" +
                    "USER_ID INTEGER NOT NULL, " +
                    " SERVER_USER_ID INTEGER, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL DEFAULT 0, " +
                    " USER_PROFILE_ID INTEGER NOT NULL DEFAULT 0, " +
                    " AUTH_TOKEN TEXT, " +
                    " USER_NAME TEXT NOT NULL, " +
                    " SERVER_ADDRESS TEXT NOT NULL, " +
                    " USER_GROUP TEXT NOT NULL, " +
                    " GCM_ID TEXT, " +
                    " STATE VARCHAR(16), " +
                    " CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), " +
                    " CREATED_BY VARCHAR(255), " +
                    " UPDATE_TIME DATETIME, " +
                    " UPDATED_BY VARCHAR(255), " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY(USER_ID, USER_GROUP))";

    //private static final String CREATE_USER_PROFILE_TABLE 	=
    //        "CREATE TABLE IF NOT EXISTS USER_PROFILE (" +
    //                "USER_PROFILE_ID INTEGER NOT NULL, " +
    //                " NAME VARCHAR(255), " +
    //                " IS_ACTIVE CHAR(1) DEFAULT 'Y', "+
    //                " PRIMARY KEY(USER_PROFILE_ID))";

	public static final String CREATE_PRODUCT =
            "CREATE TABLE IF NOT EXISTS PRODUCT (" +
                    "PRODUCT_ID INTEGER DEFAULT NULL, " +
                    " INTERNAL_CODE VARCHAR(128) DEFAULT NULL, " +
                    " SUBCATEGORY_ID INTEGER DEFAULT NULL, " +
                    " BRAND_ID INTEGER DEFAULT NULL, " +
                    " NAME VARCHAR(255) DEFAULT NULL, " +
                    " DESCRIPTION CLOB DEFAULT NULL, " +
                    " PURPOSE CLOB DEFAULT NULL, " +
                    " OBSERVATION CLOB DEFAULT NULL, " +
                    " REFERENCE_ID VARCHAR(36) DEFAULT NULL, " +
                    " ORIGIN VARCHAR(55) DEFAULT NULL, " +
                    " COMMERCIAL_PACKAGE_UNITS INTEGER DEFAULT NULL," +
                    " COMMERCIAL_PACKAGE VARCHAR(20) DEFAULT NULL," +
                    " INVENTORY_PACKAGE_UNITS INTEGER DEFAULT NULL," +
                    " INVENTORY_PACKAGE VARCHAR(20) DEFAULT NULL," +
                    " LAST_RECEIVED_DATE DATE DEFAULT NULL, " +
                    " PRODUCT_TAX_ID INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_ID))";

	public static final String CREATE_PRODUCT_PRICE_AVAILABILITY =
            "CREATE TABLE IF NOT EXISTS PRODUCT_PRICE_AVAILABILITY (" +
                    "PRODUCT_ID INTEGER NOT NULL, " +
                    " PRODUCT_PRICE_ID INTEGER DEFAULT 0 NOT NULL, " +
                    " PRICE DECIMAL DEFAULT 0 NOT NULL, " +
                    " AVAILABILITY INTEGER DEFAULT 0 NOT NULL, " +
                    " CURRENCY_ID INTEGER DEFAULT 0 NOT NULL, " +
                    " PRIORITY INTEGER DEFAULT 1 NOT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_ID, PRODUCT_PRICE_ID))";

	public static final String CREATE_PRODUCT_IMAGE =
            "CREATE TABLE IF NOT EXISTS PRODUCT_IMAGE (" +
                    "PRODUCT_ID INTEGER NOT NULL, " +
                    " FILE_NAME VARCHAR(255) NOT NULL, " +
                    " PRIORITY INTEGER NOT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_ID, FILE_NAME))";

    public static final String CREATE_PRODUCT_RATING =
            "CREATE TABLE IF NOT EXISTS PRODUCT_RATING (" +
                    "PRODUCT_ID INTEGER NOT NULL, " +
                    " RATING DOUBLE NOT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_ID))";

	public static final String CREATE_PRODUCT_TAX =
            "CREATE TABLE IF NOT EXISTS PRODUCT_TAX (" +
                    " PRODUCT_TAX_ID INTEGER NOT NULL, " +
                    " PERCENTAGE DOUBLE NOT NULL, " +
                    " NAME VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_TAX_ID))";

    public static final String CREATE_CURRENCY =
            "CREATE TABLE IF NOT EXISTS CURRENCY (" +
                    "CURRENCY_ID INTEGER NOT NULL, " +
                    " COUNTRY_NAME VARCHAR(128) NOT NULL, " +
                    " CURRENCY_NAME VARCHAR(128) NOT NULL, " +
                    " CODE VARCHAR(3) DEFAULT 0 NOT NULL, " +
                    " UNICODE_DECIMAL VARCHAR(32), " +
                    " UNICODE_HEX VARCHAR(32), " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (CURRENCY_ID))";

	public static final String CREATE_PRODUCT_SHOPPING_RELATED =
            "CREATE TABLE IF NOT EXISTS PRODUCT_SHOPPING_RELATED (" +
                    "PRODUCT_ID INTEGER NOT NULL, " +
                    " PRODUCT_RELATED_ID INTEGER NOT NULL, " +
                    " TIMES INTEGER DEFAULT 0 NOT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (PRODUCT_ID, PRODUCT_RELATED_ID))";

	public static final String CREATE_BRAND =
            "CREATE TABLE IF NOT EXISTS BRAND (" +
                    "BRAND_ID INTEGER NOT NULL, " +
                    " NAME VARCHAR(255) DEFAULT NULL, " +
                    " DESCRIPTION TEXT DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (BRAND_ID))";

	public static final String CREATE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS CATEGORY (" +
                    "CATEGORY_ID INTEGER NOT NULL, " +
                    " NAME VARCHAR(255) DEFAULT NULL, " +
                    " DESCRIPTION TEXT DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (CATEGORY_ID))";

	public static final String CREATE_SUBCATEGORY =
            "CREATE TABLE IF NOT EXISTS SUBCATEGORY (" +
                    "SUBCATEGORY_ID INTEGER NOT NULL, " +
                    " CATEGORY_ID INTEGER NOT NULL, " +
                    " NAME VARCHAR(255) DEFAULT NULL, " +
                    " DESCRIPTION TEXT DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (SUBCATEGORY_ID))";

    public static final String CREATE_COMPANY =
            "CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "COMPANY_ID INTEGER NOT NULL, " +
                    " NAME TEXT DEFAULT NULL, " +
                    " COMMERCIAL_NAME TEXT DEFAULT NULL, " +
                    " TAX_ID VARCHAR(255) DEFAULT NULL, " +
                    " ADDRESS TEXT DEFAULT NULL, " +
                    " CONTACT_PERSON VARCHAR(255) DEFAULT NULL, " +
                    " EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, " +
                    " CONTACT_CENTER_PHONE_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " PHONE_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " FAX_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " WEB_PAGE VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (COMPANY_ID))";

	public static final String CREATE_MAINPAGE_PRODUCT_SECTION =
            "CREATE TABLE IF NOT EXISTS MAINPAGE_PRODUCT_SECTION (" +
                    "MAINPAGE_PRODUCT_SECTION_ID INTEGER NOT NULL, " +
                    " NAME VARCHAR(128) DEFAULT NULL, " +
                    " DESCRIPTION VARCHAR(255) DEFAULT NULL, " +
                    " PRIORITY INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (MAINPAGE_PRODUCT_SECTION_ID))";

	public static final String CREATE_MAINPAGE_PRODUCT =
            "CREATE TABLE IF NOT EXISTS MAINPAGE_PRODUCT (" +
                    "MAINPAGE_PRODUCT_ID INTEGER NOT NULL, " +
                    " MAINPAGE_PRODUCT_SECTION_ID INTEGER NOT NULL, " +
                    " PRODUCT_ID INTEGER NOT NULL, " +
                    " PRIORITY INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (MAINPAGE_PRODUCT_ID))";

	public static final String CREATE_BANNER =
            "CREATE TABLE IF NOT EXISTS BANNER (" +
                    "BANNER_ID INTEGER NOT NULL, " +
                    " PRODUCT_ID INTEGER DEFAULT NULL, " +
                    " BRAND_ID INTEGER DEFAULT NULL, " +
                    " SUBCATEGORY_ID INTEGER DEFAULT NULL, " +
                    " CATEGORY_ID INTEGER DEFAULT NULL, " +
                    " IMAGE_FILE_NAME VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (BANNER_ID))";

	public static final String CREATE_BRAND_PROMOTIONAL_CARD =
            "CREATE TABLE IF NOT EXISTS BRAND_PROMOTIONAL_CARD (" +
                    "BRAND_PROMOTIONAL_CARD_ID INTEGER NOT NULL, " +
                    " BRAND_ID INTEGER NOT NULL, " +
                    " IMAGE_FILE_NAME VARCHAR(255) DEFAULT NULL, " +
                    " PROMOTIONAL_TEXT TEXT DEFAULT NULL, " +
                    " BACKGROUND_R_COLOR INTEGER DEFAULT NULL, " +
                    " BACKGROUND_G_COLOR INTEGER DEFAULT NULL, " +
                    " BACKGROUND_B_COLOR INTEGER DEFAULT NULL, " +
                    " PROMOTIONAL_TEXT_R_COLOR INTEGER DEFAULT NULL, " +
                    " PROMOTIONAL_TEXT_G_COLOR INTEGER DEFAULT NULL, " +
                    " PROMOTIONAL_TEXT_B_COLOR INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (BRAND_PROMOTIONAL_CARD_ID))";

    public static final String CREATE_APP_PARAMETER =
            "CREATE TABLE IF NOT EXISTS APP_PARAMETER (" +
                    "APP_PARAMETER_ID INTEGER NOT NULL, " +
                    " PARAMETER_DESCRIPTION VARCHAR(255) DEFAULT NULL, " +
                    " TEXT_VALUE TEXT DEFAULT NULL, " +
                    " INTEGER_VALUE INTEGER DEFAULT NULL, " +
                    " DOUBLE_VALUE DOUBLE DEFAULT NULL, " +
                    " BOOLEAN_VALUE CHAR(1) DEFAULT NULL, " +
                    " DATE_VALUE DATE DEFAULT NULL, " +
                    " DATETIME_VALUE DATETIME DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (APP_PARAMETER_ID))";

    public static final String CREATE_SERVER_ADDRESS_BACKUP =
            "CREATE TABLE IF NOT EXISTS SERVER_ADDRESS_BACKUP (" +
                    "SERVER_ADDRESS VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (SERVER_ADDRESS))";

    /*********************************************************************************************************************/

    private static final String CREATE_LOG_TABLE 	=
            "CREATE TABLE IF NOT EXISTS IDS_SYNC_LOG (" +
                    "SYNC_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " LOG_TYPE TEXT NOT NULL, " +
                    " LOG_MESSAGE TEXT, " +
                    " LOG_MESSAGE_DETAIL TEXT, " +
                    " LOG_VISIBILITY NUMERIC DEFAULT "+LogSyncData.INVISIBLE+", " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " CREATE_TIME DATETIME DEFAULT (datetime('now','localtime'))) ";

    private static final String CREATE_SCHEDULER_TABLE 	=
            "CREATE TABLE IF NOT EXISTS IDS_SCHEDULER_SYNC (" +
                    "SCHEDULER_SYNC_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
                    " USER_ID INTEGER NOT NULL, " +
                    " HOUR INTEGER NOT NULL, " +
                    " MINUTE INTEGER NOT NULL, " +
                    " MONDAY CHAR, " +
                    " TUESDAY CHAR, " +
                    " WEDNESDAY CHAR, " +
                    " THURSDAY CHAR, " +
                    " FRIDAY CHAR, " +
                    " SATURDAY CHAR, " +
                    " SUNDAY CHAR, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')))";

    public static final String CREATE_BUSINESS_PARTNER =
            "CREATE TABLE IF NOT EXISTS BUSINESS_PARTNER (" +
                    "BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " INTERNAL_CODE VARCHAR(128) DEFAULT NULL, " +
                    " NAME TEXT DEFAULT NULL, " +
                    " COMMERCIAL_NAME TEXT DEFAULT NULL, " +
                    " TAX_ID VARCHAR(255) DEFAULT NULL, " +
                    " ADDRESS TEXT DEFAULT NULL, " +
                    " CONTACT_PERSON VARCHAR(255) DEFAULT NULL, " +
                    " EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, " +
                    " PHONE_NUMBER VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (BUSINESS_PARTNER_ID))";

    public static final String CREATE_USER_BUSINESS_PARTNER =
            "CREATE TABLE IF NOT EXISTS USER_BUSINESS_PARTNER (" +
                    "USER_BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " INTERNAL_CODE VARCHAR(128) DEFAULT NULL, " +
                    " NAME TEXT DEFAULT NULL, " +
                    " COMMERCIAL_NAME TEXT DEFAULT NULL, " +
                    " TAX_ID VARCHAR(255) DEFAULT NULL, " +
                    " ADDRESS TEXT DEFAULT NULL, " +
                    " CONTACT_PERSON VARCHAR(255) DEFAULT NULL, " +
                    " EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, " +
                    " PHONE_NUMBER VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (USER_BUSINESS_PARTNER_ID, USER_ID))";

    public static final String CREATE_USER_APP_PARAMETER =
            "CREATE TABLE IF NOT EXISTS USER_APP_PARAMETER (" +
                    "USER_ID INTEGER NOT NULL, " +
                    " APP_PARAMETER_ID INTEGER NOT NULL, " +
                    " PARAMETER_DESCRIPTION VARCHAR(255) DEFAULT NULL, " +
                    " TEXT_VALUE TEXT DEFAULT NULL, " +
                    " INTEGER_VALUE INTEGER DEFAULT NULL, " +
                    " DOUBLE_VALUE DOUBLE DEFAULT NULL, " +
                    " BOOLEAN_VALUE CHAR(1) DEFAULT NULL, " +
                    " DATE_VALUE DATE DEFAULT NULL, " +
                    " DATETIME_VALUE DATETIME DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (USER_ID, APP_PARAMETER_ID))";

    public static final String CREATE_ECOMMERCE_ORDER =
            "CREATE TABLE IF NOT EXISTS ECOMMERCE_ORDER (" +
                    "ECOMMERCE_ORDER_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " ECOMMERCE_SALES_ORDER_ID INTEGER DEFAULT NULL, " +
                    " LINES_NUMBER INTEGER DEFAULT 0, " +
                    " SUB_TOTAL DOUBLE DEFAULT 0, " +
                    " TAX DOUBLE DEFAULT 0, " +
                    " TOTAL DOUBLE DEFAULT 0, " +
                    " DOC_STATUS CHAR(2) DEFAULT NULL, " +
                    " DOC_TYPE CHAR(4) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (ECOMMERCE_ORDER_ID, USER_ID))";

    public static final String CREATE_ECOMMERCE_ORDER_LINE =
            "CREATE TABLE IF NOT EXISTS ECOMMERCE_ORDER_LINE (" +
                    "ECOMMERCE_ORDER_LINE_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " ECOMMERCE_ORDER_ID INTEGER DEFAULT NULL, " +
                    " PRODUCT_ID INTEGER NOT NULL, " +
                    " QTY_REQUESTED INTEGER NOT NULL, " +
                    " SALES_PRICE DOUBLE DEFAULT NULL, " +
                    " TAX_PERCENTAGE DOUBLE DEFAULT NULL, " +
                    " TOTAL_LINE DOUBLE DEFAULT NULL, " +
                    " DOC_TYPE CHAR(4) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (ECOMMERCE_ORDER_LINE_ID, USER_ID))";

    public static final String CREATE_ECOMMERCE_SALES_ORDER =
            "CREATE TABLE IF NOT EXISTS ECOMMERCE_SALES_ORDER (" +
                    "ECOMMERCE_SALES_ORDER_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " LINES_NUMBER INTEGER DEFAULT 0, " +
                    " SUB_TOTAL DOUBLE DEFAULT 0, " +
                    " TAX DOUBLE DEFAULT 0, " +
                    " TOTAL DOUBLE DEFAULT 0, " +
                    " VALID_TO DATE DEFAULT NULL, " +
                    " DOC_STATUS CHAR(2) DEFAULT NULL, " +
                    " DOC_TYPE CHAR(4) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (ECOMMERCE_SALES_ORDER_ID, USER_ID))";

    public static final String CREATE_ECOMMERCE_SALES_ORDER_LINE =
            "CREATE TABLE IF NOT EXISTS ECOMMERCE_SALES_ORDER_LINE (" +
                    "ECOMMERCE_SALES_ORDER_LINE_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " ECOMMERCE_SALES_ORDER_ID INTEGER DEFAULT NULL, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " PRODUCT_ID INTEGER NOT NULL, " +
                    " QTY_REQUESTED INTEGER NOT NULL, " +
                    " SALES_PRICE DOUBLE DEFAULT NULL, " +
                    " TAX_PERCENTAGE DOUBLE DEFAULT NULL, " +
                    " TOTAL_LINE DOUBLE DEFAULT NULL, " +
                    " DOC_TYPE CHAR(4) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (ECOMMERCE_SALES_ORDER_LINE_ID, USER_ID))";

    public static final String CREATE_RECENT_SEARCH =
            "CREATE TABLE IF NOT EXISTS RECENT_SEARCH (" +
                    "RECENT_SEARCH_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " TEXT_TO_SEARCH TEXT NOT NULL, " +
                    " PRODUCT_ID INTEGER DEFAULT NULL, " +
                    " SUBCATEGORY_ID INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (RECENT_SEARCH_ID, USER_ID))";

	public static final String CREATE_PRODUCT_RECENTLY_SEEN =
            "CREATE TABLE IF NOT EXISTS PRODUCT_RECENTLY_SEEN (" +
                    "PRODUCT_RECENTLY_SEEN_ID INTEGER NOT NULL, " +
                    " USER_ID INTEGER NOT NULL, " +
                    " BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " PRODUCT_ID INTEGER DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (USER_ID, BUSINESS_PARTNER_ID, PRODUCT_ID))";

    public static final String CREATE_RECOMMENDED_PRODUCT =
            "CREATE TABLE IF NOT EXISTS RECOMMENDED_PRODUCT (" +
                    "BUSINESS_PARTNER_ID INTEGER NOT NULL, " +
                    " PRODUCT_ID INTEGER NOT NULL, " +
                    " PRIORITY INTEGER NOT NULL DEFAULT 0, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (BUSINESS_PARTNER_ID, PRODUCT_ID))";

    public static final String CREATE_USER_COMPANY =
            "CREATE TABLE IF NOT EXISTS USER_COMPANY (" +
                    "USER_ID INTEGER NOT NULL, " +
                    " NAME TEXT DEFAULT NULL, " +
                    " COMMERCIAL_NAME TEXT DEFAULT NULL, " +
                    " TAX_ID VARCHAR(255) DEFAULT NULL, " +
                    " ADDRESS TEXT DEFAULT NULL, " +
                    " CONTACT_PERSON VARCHAR(255) DEFAULT NULL, " +
                    " EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, " +
                    " CONTACT_CENTER_PHONE_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " PHONE_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " FAX_NUMBER VARCHAR(128) DEFAULT NULL, " +
                    " WEB_PAGE VARCHAR(255) DEFAULT NULL, " +
                    " IS_ACTIVE CHAR(1) DEFAULT 'Y', " +
                    " CREATE_TIME DATETIME NOT NULL, " +
                    " UPDATE_TIME DATETIME DEFAULT NULL, " +
                    " APP_VERSION VARCHAR(128) NOT NULL, " +
                    " APP_USER_NAME VARCHAR(128) NOT NULL, " +
                    " DEVICE_MAC_ADDRESS VARCHAR(128) NOT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " PRIMARY KEY (USER_ID))";

    public static final String CREATE_FAILED_SYNC_DATA_WITH_SERVER =
            "CREATE TABLE IF NOT EXISTS FAILED_SYNC_DATA_WITH_SERVER (" +
                    " row_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " selection TEXT DEFAULT NULL, " +
                    " selectionArgs TEXT DEFAULT NULL, " +
                    " SEQUENCE_ID BIGINT UNSIGNED NOT NULL DEFAULT 0, "+
                    " columnCount INTEGER DEFAULT NULL)";

	/**
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.dataBaseName = DATABASE_NAME;
	}
	
	/**
	 * 
	 * @param context
	 * @param user
	 */
	public DatabaseHelper(Context context, User user){
		super(context, 
				user.isSaveDBInExternalCard()
					? context.getExternalFilesDir(null).getAbsolutePath()+ File.separator + ApplicationUtilities.getDatabaseNameByUser(user)
					: ApplicationUtilities.getDatabaseNameByUser(user), 
				null, 
				DATABASE_VERSION);
		this.dataBaseName = ApplicationUtilities.getDatabaseNameByUser(user);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		if(this.dataBaseName.equals(DATABASE_NAME)){
			db.execSQL(CREATE_USER_TABLE);
            //db.execSQL(CREATE_USER_PROFILE_TABLE);
            db.execSQL(CREATE_PRODUCT);
            db.execSQL(CREATE_PRODUCT_PRICE_AVAILABILITY);
            db.execSQL(CREATE_PRODUCT_IMAGE);
            db.execSQL("create index product_image_idx on product_image (product_id)");
            db.execSQL(CREATE_PRODUCT_RATING);
            db.execSQL("create index product_rating_idx on product_rating (product_id)");
            db.execSQL(CREATE_PRODUCT_TAX);
            db.execSQL(CREATE_CURRENCY);
            db.execSQL(CREATE_PRODUCT_SHOPPING_RELATED);
            db.execSQL("create index product_shopping_related_idx on product_shopping_related (product_id)");
            db.execSQL(CREATE_BRAND);
            db.execSQL(CREATE_CATEGORY);
            db.execSQL(CREATE_SUBCATEGORY);
            db.execSQL(CREATE_COMPANY);
            db.execSQL(CREATE_MAINPAGE_PRODUCT_SECTION);
            db.execSQL(CREATE_MAINPAGE_PRODUCT);
            db.execSQL(CREATE_BANNER);
            db.execSQL(CREATE_BRAND_PROMOTIONAL_CARD);
            db.execSQL(CREATE_APP_PARAMETER);
            db.execSQL(CREATE_SERVER_ADDRESS_BACKUP);
		}else{
            db.execSQL(CREATE_LOG_TABLE);
            db.execSQL(CREATE_SCHEDULER_TABLE);
            db.execSQL(CREATE_BUSINESS_PARTNER);
            db.execSQL(CREATE_USER_APP_PARAMETER);
            db.execSQL(CREATE_ECOMMERCE_ORDER);
            db.execSQL(CREATE_ECOMMERCE_ORDER_LINE);
            db.execSQL(CREATE_ECOMMERCE_SALES_ORDER);
            db.execSQL(CREATE_ECOMMERCE_SALES_ORDER_LINE);
            db.execSQL(CREATE_RECENT_SEARCH);
            db.execSQL(CREATE_PRODUCT_RECENTLY_SEEN);
            db.execSQL(CREATE_RECOMMENDED_PRODUCT);
            db.execSQL(CREATE_USER_BUSINESS_PARTNER);
            db.execSQL(CREATE_USER_COMPANY);
            db.execSQL(CREATE_FAILED_SYNC_DATA_WITH_SERVER);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(this.dataBaseName.equals(DATABASE_NAME)){
            if(oldVersion<8){
                db.execSQL(CREATE_SERVER_ADDRESS_BACKUP);
            }
        }else{
            if(oldVersion<7) {
                db.execSQL(CREATE_FAILED_SYNC_DATA_WITH_SERVER);
            }
        }

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

}

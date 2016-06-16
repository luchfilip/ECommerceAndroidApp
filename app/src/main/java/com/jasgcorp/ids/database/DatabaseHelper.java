package com.jasgcorp.ids.database;

import com.jasgcorp.ids.logsync.LogSyncData;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "IDS_DATABASE";
//    private static final int DB_NOT_FOUND = 0;
//    private static final int USING_INTERNAL_STORAGE = 1;
//    private static final int USING_EXTERNAL_STORAGE = 2;
	private static final String TAG = DatabaseHelper.class.getSimpleName();
    private String dataBaseName;
	private static final String CREATE_LOG_TABLE 	= "CREATE TABLE IF NOT EXISTS IDS_SYNC_LOG (" +
														"SYNC_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
														"USER_ID INTEGER NOT NULL, " +
														"LOG_TYPE TEXT NOT NULL, " +
														"LOG_MESSAGE TEXT, " +
														"LOG_MESSAGE_DETAIL TEXT, " +
														"LOG_VISIBILITY NUMERIC DEFAULT "+LogSyncData.INVISIBLE+", " +
														"CREATE_TIME DATETIME DEFAULT (datetime('now','localtime'))) ";
	
	private static final String CREATE_USER_TABLE 	= "CREATE TABLE IF NOT EXISTS IDS_USER (" +
														"USER_ID INTEGER NOT NULL, " +
														"SERVER_USER_ID INTEGER, " +
														"AUTH_TOKEN TEXT, " +
														"USER_NAME TEXT NOT NULL, " +
														"SERVER_ADDRESS TEXT NOT NULL, " +
														"USER_GROUP TEXT NOT NULL, " +
														"GCM_ID TEXT, " +
														"STATE VARCHAR(16), " +
														"CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), " +
														"CREATED_BY VARCHAR(255), " +
														"UPDATE_TIME DATETIME, " +
														"UPDATED_BY VARCHAR(255), " +
														"ISACTIVE CHAR(1), "+
														"PRIMARY KEY(USER_ID, USER_GROUP))";
	
	private static final String CREATE_SCHEDULER_TABLE 	= "CREATE TABLE IF NOT EXISTS IDS_SCHEDULER_SYNC (" +
															"SCHEDULER_SYNC_ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "+
															"USER_ID INTEGER NOT NULL, " +
															"HOUR INTEGER NOT NULL, " +
															"MINUTE INTEGER NOT NULL, " +
															"MONDAY CHAR, " +
															"TUESDAY CHAR, " +
															"WEDNESDAY CHAR, " +
															"THURSDAY CHAR, " +
															"FRIDAY CHAR, " +
															"SATURDAY CHAR, " +
															"SUNDAY CHAR, " +
															"ISACTIVE CHAR DEFAULT 'Y', " +
															"CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')))";
	
	public static final String CREATE_IDS_INCOMING_FILE_SYNC = 
								new StringBuffer("CREATE TABLE IF NOT EXISTS IDS_INCOMING_FILE_SYNC ")
													.append("(INCOMING_FILE_SYNC_ID INTEGER PRIMARY KEY AUTOINCREMENT, " )
													.append(" FILE_SYNC_ID BIGINT UNSIGNED NOT NULL, ")
													.append(" FOLDER_CLIENT_NAME VARCHAR(255) NOT NULL, ")
													.append(" FILE_NAME VARCHAR(255) NOT NULL, ")
													.append(" FILE_SIZE INTEGER NOT NULL, ")
													.append(" ERROR_MESSAGE TEXT, ")
													.append(" ISACTIVE CHAR DEFAULT 'Y', ")
													.append(" CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')))").toString();
	
	public static final String CREATE_IDS_OUTGOING_FILE_SYNC = 
								new StringBuffer("CREATE TABLE IF NOT EXISTS IDS_OUTGOING_FILE_SYNC ")
													.append("(OUTGOING_FILE_SYNC_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
													.append(" FOLDER_CLIENT_NAME VARCHAR(255) NOT NULL, ")
													.append(" FILE_PATH VARCHAR(512) NOT NULL, ")
													.append(" FILE_SIZE INTEGER NOT NULL, ")
													.append(" ISACTIVE CHAR DEFAULT 'Y', ")
													.append(" CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')))").toString();

	public static final String CREATE_ARTICULOS =
								new StringBuffer("CREATE TABLE IF NOT EXISTS ARTICULOS ")
											.append("(IDARTICULO INTEGER DEFAULT 0 NOT NULL, ")
											.append("IDPARTIDA INTEGER DEFAULT 0 NOT NULL, ")
											.append("IDMARCA INTEGER DEFAULT 0 NOT NULL, ")
											.append("NOMBRE VARCHAR(255) DEFAULT NULL, ")
                                            .append("DESCRIPCION CLOB DEFAULT NULL, ")
											.append("USO CLOB DEFAULT NULL, ")
											.append("OBSERVACIONES CLOB DEFAULT NULL, ")
											.append("IDREFERENCIA VARCHAR(36) DEFAULT NULL, ")
											.append("NACIONALIDAD VARCHAR(55) DEFAULT NULL, ")
											.append("ACTIVO CHAR(1) DEFAULT 'V', ")
											.append("CODVIEJO CHAR(7) DEFAULT NULL, ")
											.append("UNIDADVENTA_COMERCIAL INTEGER DEFAULT NULL,")
											.append("EMPAQUE_COMERCIAL VARCHAR(20) DEFAULT NULL,")
											.append("LAST_RECEIVED_DATE DATE DEFAULT NULL, ")
                                            .append("PRODUCT_TAX_ID INTEGER DEFAULT NULL, ")
											.append("PRIMARY KEY (IDARTICULO))").toString();

	public static final String CREATE_PRODUCT_AVAILABILITY =
									new StringBuffer("CREATE TABLE IF NOT EXISTS PRODUCT_AVAILABILITY ")
											.append("(PRODUCT_ID INTEGER NOT NULL, ")
											.append("AVAILABILITY INTEGER DEFAULT 0 NOT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("CREATE_TIME DATETIME DEFAULT NULL, ")
											.append("UPDATE_TIME DATETIME DEFAULT NULL, ")
											.append("PRIMARY KEY (PRODUCT_ID, ISACTIVE))").toString();

	public static final String CREATE_PRODUCT_IMAGE =
									new StringBuffer("CREATE TABLE IF NOT EXISTS PRODUCT_IMAGE ")
											.append("(PRODUCT_IMAGE_ID INTEGER PRIMARY KEY, ")
                                            .append("PRODUCT_ID INTEGER NOT NULL, ")
											.append("FILE_NAME VARCHAR(255) NOT NULL, ")
                                            .append("PRIORITY INTEGER NOT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y')").toString();

    public static final String CREATE_PRODUCT_RANKING =
                                    new StringBuffer("CREATE TABLE IF NOT EXISTS PRODUCT_RANKING ")
                                            .append("(PRODUCT_RANKING_ID INTEGER PRIMARY KEY, ")
                                            .append("PRODUCT_ID INTEGER NOT NULL, ")
                                            .append("RANKING DOUBLE NOT NULL, ")
                                            .append("ISACTIVE CHAR(1) DEFAULT 'Y')").toString();

	public static final String CREATE_PRODUCT_TAX =
									new StringBuffer("CREATE TABLE IF NOT EXISTS PRODUCT_TAX ")
											.append("(PRODUCT_TAX_ID INTEGER NOT NULL, ")
											.append("TAX_PERCENTAGE DOUBLE NOT NULL, ")
											.append("TAX_NAME VARCHAR(255) DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("PRIMARY KEY (PRODUCT_TAX_ID))").toString();

	public static final String CREATE_PRODUCT_SHOPPING_RELATED =
									new StringBuffer("CREATE TABLE IF NOT EXISTS PRODUCT_SHOPPING_RELATED ")
											.append("(PRODUCT_ID INTEGER NOT NULL, ")
											.append("PRODUCT_RELATED_ID INTEGER NOT NULL, ")
											.append("TIMES INTEGER DEFAULT 0 NOT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("PRIMARY KEY (PRODUCT_ID, PRODUCT_RELATED_ID))").toString();

	public static final String CREATE_BRAND =
								new StringBuffer("CREATE TABLE IF NOT EXISTS BRAND ")
											.append("(BRAND_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("PRIMARY KEY (BRAND_ID))").toString();

	public static final String CREATE_CATEGORY =
								new StringBuffer("CREATE TABLE IF NOT EXISTS CATEGORY ")
											.append("(CATEGORY_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("PRIMARY KEY (CATEGORY_ID))").toString();

	public static final String CREATE_SUBCATEGORY =
								new StringBuffer("CREATE TABLE IF NOT EXISTS SUBCATEGORY ")
											.append("(SUBCATEGORY_ID INTEGER NOT NULL, ")
											.append("CATEGORY_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
											.append("PRIMARY KEY (SUBCATEGORY_ID))").toString();

	public static final String CREATE_MAINPAGE_PRODUCT_SECTION =
								new StringBuffer("CREATE TABLE IF NOT EXISTS MAINPAGE_PRODUCT_SECTION ")
										.append("(MAINPAGE_PRODUCT_SECTION_ID INTEGER NOT NULL, ")
										.append("NAME VARCHAR(128) DEFAULT NULL, ")
										.append("DESCRIPTION VARCHAR(255) DEFAULT NULL, ")
										.append("PRIORITY INTEGER DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("PRIMARY KEY (MAINPAGE_PRODUCT_SECTION_ID))").toString();

	public static final String CREATE_MAINPAGE_PRODUCT =
								new StringBuffer("CREATE TABLE IF NOT EXISTS MAINPAGE_PRODUCT ")
										.append("(MAINPAGE_PRODUCT_ID INTEGER NOT NULL, ")
										.append("MAINPAGE_PRODUCT_SECTION_ID INTEGER NOT NULL, ")
										.append("PRODUCT_ID INTEGER NOT NULL, ")
										.append("PRIORITY INTEGER DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("PRIMARY KEY (MAINPAGE_PRODUCT_ID))").toString();

    public static final String CREATE_ECOMMERCE_ORDER =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS ECOMMERCE_ORDER ")
                                        .append("(ECOMMERCE_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
										.append("ECOMMERCE_SALES_ORDER_ID INTEGER DEFAULT NULL, ")
										.append("BUSINESS_PARTNER_ID INTEGER DEFAULT NULL, ")
                                        .append("LINES_NUMBER INTEGER DEFAULT 0, ")
                                        .append("SUB_TOTAL DOUBLE DEFAULT 0, ")
                                        .append("TAX DOUBLE DEFAULT 0, ")
                                        .append("TOTAL DOUBLE DEFAULT 0, ")
                                        .append("DOC_STATUS CHAR(2) DEFAULT NULL, ")
                                        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
                                        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
                                        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
                                        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
                                        .append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();

    public static final String CREATE_ECOMMERCE_ORDERLINE =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS ECOMMERCE_ORDERLINE ")
                                        .append("(ECOMMERCE_ORDERLINE_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                                        .append("ECOMMERCE_ORDER_ID INTEGER DEFAULT NULL, ")
                                        .append("PRODUCT_ID INTEGER NOT NULL, ")
                                        .append("QTY_REQUESTED INTEGER NOT NULL, ")
                                        .append("SALES_PRICE DOUBLE DEFAULT NULL, ")
										.append("TAX_PERCENTAGE DOUBLE DEFAULT NULL, ")
										.append("TOTAL_LINE DOUBLE DEFAULT NULL, ")
                                        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
                                        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
                                        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
                                        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
                                        .append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();

    public static final String CREATE_ECOMMERCE_SALES_ORDER =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS ECOMMERCE_SALES_ORDER ")
                                        .append("(ECOMMERCE_SALES_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                                        .append("BUSINESS_PARTNER_ID INTEGER DEFAULT NULL, ")
                                        .append("LINES_NUMBER INTEGER DEFAULT 0, ")
                                        .append("SUB_TOTAL DOUBLE DEFAULT 0, ")
                                        .append("TAX DOUBLE DEFAULT 0, ")
                                        .append("TOTAL DOUBLE DEFAULT 0, ")
                                        .append("DOC_STATUS CHAR(2) DEFAULT NULL, ")
                                        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
                                        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
                                        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
                                        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
                                        .append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();

    public static final String CREATE_ECOMMERCE_SALES_ORDERLINE =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS ECOMMERCE_SALES_ORDERLINE ")
                                        .append("(ECOMMERCE_SALES_ORDERLINE_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
                                        .append("ECOMMERCE_SALES_ORDER_ID INTEGER DEFAULT NULL, ")
                                        .append("BUSINESS_PARTNER_ID INTEGER NOT NULL, ")
                                        .append("PRODUCT_ID INTEGER NOT NULL, ")
                                        .append("QTY_REQUESTED INTEGER NOT NULL, ")
                                        .append("SALES_PRICE DOUBLE DEFAULT NULL, ")
                                        .append("TAX_PERCENTAGE DOUBLE DEFAULT NULL, ")
                                        .append("TOTAL_LINE DOUBLE DEFAULT NULL, ")
                                        .append("DOC_TYPE CHAR(2) DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT NULL, ")
                                        .append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
                                        .append("UPDATE_TIME DATETIME DEFAULT NULL, ")
                                        .append("APP_VERSION VARCHAR(128) NOT NULL, ")
                                        .append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();

	public static final String CREATE_RECENT_SEARCH =
								new StringBuffer("CREATE TABLE IF NOT EXISTS RECENT_SEARCH ")
										.append("(RECENT_SEARCH_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
										.append("TEXT_TO_SEARCH TEXT NOT NULL, ")
										.append("PRODUCT_ID INTEGER DEFAULT NULL, ")
										.append("SUBCATEGORY_ID INTEGER DEFAULT NULL, ")
										.append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')))").toString();

	public static final String CREATE_BUSINESS_PARTNER =
								new StringBuffer("CREATE TABLE IF NOT EXISTS BUSINESS_PARTNER ")
										.append("(BUSINESS_PARTNER_ID INTEGER PRIMARY KEY AUTOINCREMENT, ")
										.append("NAME TEXT DEFAULT NULL, ")
										.append("COMMERCIAL_NAME TEXT DEFAULT NULL, ")
										.append("TAX_ID VARCHAR(255) DEFAULT NULL, ")
                                        .append("ADDRESS TEXT DEFAULT NULL, ")
										.append("CONTACT_PERSON VARCHAR(255) DEFAULT NULL, ")
										.append("EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, ")
										.append("PHONE_NUMBER VARCHAR(255) DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
										.append("UPDATE_TIME DATETIME DEFAULT NULL, ")
										.append("APP_VERSION VARCHAR(128) NOT NULL, ")
										.append("APP_USER_NAME VARCHAR(128) NOT NULL)").toString();

	public static final String CREATE_COMPANY =
								new StringBuffer("CREATE TABLE IF NOT EXISTS COMPANY ")
										.append("(NAME TEXT DEFAULT NULL, ")
										.append("COMMERCIAL_NAME TEXT DEFAULT NULL, ")
										.append("TAX_ID VARCHAR(255) DEFAULT NULL, ")
										.append("ADDRESS TEXT DEFAULT NULL, ")
										.append("CONTACT_PERSON VARCHAR(255) DEFAULT NULL, ")
										.append("EMAIL_ADDRESS VARCHAR(255) DEFAULT NULL, ")
										.append("PHONE_NUMBER VARCHAR(255) DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("CREATE_TIME DATETIME DEFAULT (datetime('now','localtime')), ")
										.append("UPDATE_TIME DATETIME DEFAULT NULL, ")
										.append("APP_VERSION VARCHAR(128) NOT NULL, ")
										.append("APP_USER_NAME VARCHAR(128) NOT NULL, ")
										.append("PRIMARY KEY (APP_USER_NAME))").toString();

	public static final String CREATE_BANNER =
								new StringBuffer("CREATE TABLE IF NOT EXISTS BANNER ")
										.append("(BANNER_ID INTEGER NOT NULL, ")
                                        .append("PRODUCT_ID INTEGER DEFAULT NULL, ")
										.append("BRAND_ID INTEGER DEFAULT NULL, ")
                                        .append("SUBCATEGORY_ID INTEGER DEFAULT NULL, ")
                                        .append("CATEGORY_ID INTEGER DEFAULT NULL, ")
										.append("IMAGE_FILE_NAME VARCHAR(255) DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("PRIMARY KEY (BANNER_ID))").toString();

	public static final String CREATE_BRAND_PROMOTIONAL_CARD =
								new StringBuffer("CREATE TABLE IF NOT EXISTS BRAND_PROMOTIONAL_CARD ")
										.append("(BRAND_PROMOTIONAL_CARD_ID INTEGER NOT NULL, ")
										.append("BRAND_ID INTEGER NOT NULL, ")
                                        .append("IMAGE_FILE_NAME VARCHAR(255) DEFAULT NULL, ")
                                        .append("PROMOTIONAL_TEXT TEXT DEFAULT NULL, ")
                                        .append("BACKGROUND_R_COLOR INTEGER DEFAULT NULL, ")
                                        .append("BACKGROUND_G_COLOR INTEGER DEFAULT NULL, ")
                                        .append("BACKGROUND_B_COLOR INTEGER DEFAULT NULL, ")
                                        .append("PROMOTIONAL_TEXT_R_COLOR INTEGER DEFAULT NULL, ")
                                        .append("PROMOTIONAL_TEXT_G_COLOR INTEGER DEFAULT NULL, ")
                                        .append("PROMOTIONAL_TEXT_B_COLOR INTEGER DEFAULT NULL, ")
										.append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
										.append("PRIMARY KEY (BRAND_PROMOTIONAL_CARD_ID))").toString();

    public static final String CREATE_APP_PARAMETER =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS APP_PARAMETER ")
                                        .append("(APP_PARAMETER_ID INTEGER NOT NULL, ")
                                        .append("PARAMETER_DESCRIPTION VARCHAR(255) DEFAULT NULL, ")
                                        .append("TEXT_VALUE TEXT DEFAULT NULL, ")
                                        .append("INTEGER_VALUE INTEGER DEFAULT NULL, ")
                                        .append("DOUBLE_VALUE DOUBLE DEFAULT NULL, ")
                                        .append("BOOLEAN_VALUE CHAR(1) DEFAULT NULL, ")
                                        .append("DATE_VALUE DATE DEFAULT NULL, ")
                                        .append("DATETIME_VALUE DATETIME DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
                                        .append("PRIMARY KEY (APP_PARAMETER_ID))").toString();

    public static final String CREATE_USER_APP_PARAMETER =
                                new StringBuffer("CREATE TABLE IF NOT EXISTS USER_APP_PARAMETER ")
                                        .append("(USER_NAME INTEGER NOT NULL, ")
                                        .append("APP_PARAMETER_ID INTEGER NOT NULL, ")
                                        .append("TEXT_VALUE TEXT DEFAULT NULL, ")
                                        .append("INTEGER_VALUE INTEGER DEFAULT NULL, ")
                                        .append("DOUBLE_VALUE DOUBLE DEFAULT NULL, ")
                                        .append("BOOLEAN_VALUE CHAR(1) DEFAULT NULL, ")
                                        .append("DATE_VALUE DATE DEFAULT NULL, ")
                                        .append("DATETIME_VALUE DATETIME DEFAULT NULL, ")
                                        .append("ISACTIVE CHAR(1) DEFAULT 'Y', ")
                                        .append("PRIMARY KEY (USER_NAME, APP_PARAMETER_ID))").toString();

	/**
	 * 
	 * @param context
	 */
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//super(context, getDBDirectory(context, DATABASE_NAME)==DB_NOT_FOUND ? DATABASE_NAME 
		//	: (getDBDirectory(context, DATABASE_NAME)==USING_INTERNAL_STORAGE ? DATABASE_NAME : context.getExternalFilesDir(null).getAbsolutePath()+"/"+DATABASE_NAME)
		//	, null, DATABASE_VERSION);
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
					? context.getExternalFilesDir(null).getAbsolutePath()+"/"+ApplicationUtilities.getDatabaseNameByUser(user) 
					: ApplicationUtilities.getDatabaseNameByUser(user), 
				null, 
				DATABASE_VERSION);
		//super(context, getDBDirectory(context, dataBaseName)==DB_NOT_FOUND ? dataBaseName 
		//	: (getDBDirectory(context, dataBaseName)==USING_INTERNAL_STORAGE ? dataBaseName : context.getExternalFilesDir(null).getAbsolutePath()+"/"+dataBaseName)
		//	, null, DATABASE_VERSION);
		this.dataBaseName = ApplicationUtilities.getDatabaseNameByUser(user);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		if(this.dataBaseName.equals(DATABASE_NAME)){
			db.execSQL(CREATE_LOG_TABLE);
			db.execSQL(CREATE_USER_TABLE);
			db.execSQL(CREATE_SCHEDULER_TABLE);
		}else{
			db.execSQL(CREATE_IDS_INCOMING_FILE_SYNC);
			db.execSQL(CREATE_IDS_OUTGOING_FILE_SYNC);
            db.execSQL(CREATE_ARTICULOS);
            db.execSQL(CREATE_PRODUCT_IMAGE);
            db.execSQL(CREATE_PRODUCT_RANKING);
            db.execSQL(CREATE_PRODUCT_TAX);
			db.execSQL(CREATE_BRAND);
			db.execSQL(CREATE_CATEGORY);
			db.execSQL(CREATE_SUBCATEGORY);
			db.execSQL(CREATE_MAINPAGE_PRODUCT_SECTION);
			db.execSQL(CREATE_MAINPAGE_PRODUCT);
            db.execSQL(CREATE_ECOMMERCE_ORDER);
            db.execSQL(CREATE_ECOMMERCE_ORDERLINE);
            db.execSQL(CREATE_ECOMMERCE_SALES_ORDER);
            db.execSQL(CREATE_ECOMMERCE_SALES_ORDERLINE);
            db.execSQL(CREATE_PRODUCT_AVAILABILITY);
			db.execSQL(CREATE_PRODUCT_SHOPPING_RELATED);
			db.execSQL(CREATE_RECENT_SEARCH);
			db.execSQL(CREATE_BUSINESS_PARTNER);
			db.execSQL(CREATE_COMPANY);
            db.execSQL(CREATE_BANNER);
            db.execSQL(CREATE_BRAND_PROMOTIONAL_CARD);
            db.execSQL(CREATE_APP_PARAMETER);
            db.execSQL(CREATE_USER_APP_PARAMETER);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(!this.dataBaseName.equals(DATABASE_NAME) && newVersion<=3) {
            try {
                db.execSQL("ALTER TABLE ECOMMERCE_ORDER ADD COLUMN ECOMMERCE_SALES_ORDER_ID INTEGER DEFAULT NULL");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE ECOMMERCE_ORDER ADD COLUMN BUSINESS_PARTNER_ID INTEGER DEFAULT NULL");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE ARTICULOS ADD COLUMN PRODUCT_TAX_ID INTEGER DEFAULT NULL");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(CREATE_PRODUCT_RANKING);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(CREATE_PRODUCT_TAX);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(CREATE_APP_PARAMETER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL(CREATE_USER_APP_PARAMETER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
	
//	/**
//	 * 
//	 * @param context
//	 * @return
//	 */
//	private static int getDBDirectory(Context context, String dataBaseName){
//		Log.d(TAG, "getDBDirectory(Context context)");
//	    try {
//	    	File internalDBFile = context.getDatabasePath(dataBaseName);
//	    	File externalDBFile = null;
//	    	try{
//	    		externalDBFile = context.getDatabasePath(context.getExternalFilesDir(null).getAbsolutePath()+"/"+dataBaseName);
//	    	}catch(Exception e){ 
//	    		return USING_INTERNAL_STORAGE;
//	    	}
//	    	if(internalDBFile.exists() && externalDBFile.exists()){
//	    		if(internalDBFile.length()>externalDBFile.length()){
//	    			return USING_INTERNAL_STORAGE;
//	    		}
//	    		return USING_EXTERNAL_STORAGE;
//	    	}else if(internalDBFile.exists()){
//	    		return USING_INTERNAL_STORAGE;
//	    	}else if(externalDBFile.exists()){
//	    		return USING_EXTERNAL_STORAGE;
//	    	}
//	    } catch (Exception e) {
//	    	e.printStackTrace();
//	    }
//		return DB_NOT_FOUND;
//	}
}

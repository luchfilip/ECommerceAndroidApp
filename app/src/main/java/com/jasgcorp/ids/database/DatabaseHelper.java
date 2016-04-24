package com.jasgcorp.ids.database;

import com.jasgcorp.ids.logsync.LogSyncData;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsBrands;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsCategory;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup0;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup1;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup2;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup3;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup4;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup5;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup6;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup7;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup8;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsGroup9;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.UtilsSubCategory;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
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
								new StringBuffer("CREATE TABLE ARTICULOS ")
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
											.append("PRIMARY KEY (IDARTICULO))").toString();

	public static final String CREATE_BRAND =
								new StringBuffer("CREATE TABLE BRAND ")
											.append("(BRAND_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT NULL, ")
											.append("PRIMARY KEY (BRAND_ID))").toString();

	public static final String CREATE_CATEGORY =
								new StringBuffer("CREATE TABLE CATEGORY ")
											.append("(CATEGORY_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT NULL, ")
											.append("PRIMARY KEY (CATEGORY_ID))").toString();

	public static final String CREATE_SUBCATEGORY =
								new StringBuffer("CREATE TABLE SUBCATEGORY ")
											.append("(SUBCATEGORY_ID INTEGER NOT NULL, ")
											.append("CATEGORY_ID INTEGER NOT NULL, ")
											.append("NAME VARCHAR(255) DEFAULT NULL, ")
											.append("DESCRIPTION TEXT DEFAULT NULL, ")
											.append("ISACTIVE CHAR(1) DEFAULT NULL, ")
											.append("PRIMARY KEY (SUBCATEGORY_ID))").toString();

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
//    	Log.d(TAG, "DatabaseHelper("+DATABASE_NAME+")");
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
//    	Log.d(TAG, "DatabaseHelper("+user+", "+dataBaseName+")");
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
//		Log.d(TAG, "onCreate(SQLiteDatabase arg0)");
		if(this.dataBaseName.equals(DATABASE_NAME)){
//			Log.i(TAG, "db.execSQL(CREATE_LOG_TABLE);");
			db.execSQL(CREATE_LOG_TABLE);
//			Log.i(TAG, "db.execSQL(CREATE_USER_TABLE);");
			db.execSQL(CREATE_USER_TABLE);
//			Log.i(TAG, "db.execSQL(CREATE_SCHEDULER_TABLE);");
			db.execSQL(CREATE_SCHEDULER_TABLE);
		}else{
//			Log.i(TAG, "db.execSQL(CREATE_IDS_INCOMING_FILE_SYNC);");
			db.execSQL(CREATE_IDS_INCOMING_FILE_SYNC);
//			Log.i(TAG, "db.execSQL(CREATE_IDS_OUTGOING_FILE_SYNC);");
			db.execSQL(CREATE_IDS_OUTGOING_FILE_SYNC);
            db.execSQL(CREATE_ARTICULOS);
            for(String insert : (new UtilsGroup0()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup1()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup2()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup3()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup4()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup5()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup6()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup7()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup8()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            for(String insert : (new UtilsGroup9()).getInserts()){
                try{
                    db.execSQL(insert);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
			db.execSQL(CREATE_BRAND);
			for(String insert : (new UtilsBrands()).getInserts()){
				try{
					db.execSQL(insert);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			db.execSQL(CREATE_CATEGORY);
			for(String insert : (new UtilsCategory()).getInserts()){
				try{
					db.execSQL(insert);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			db.execSQL(CREATE_SUBCATEGORY);
			for(String insert : (new UtilsSubCategory()).getInserts()){
				try{
					db.execSQL(insert);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
//		Log.d(TAG, "onUpgrade(SQLiteDatabase arg0, int arg1, int arg2)");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
//		Log.d(TAG, "onOpen(SQLiteDatabase db)");
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

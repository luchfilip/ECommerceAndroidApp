package com.smartbuilders.synchronizer.ids.providers;

import java.util.LinkedHashMap;

import org.ksoap2.serialization.SoapPrimitive;

import com.smartbuilders.smartsales.ecommerce.BuildConfig;
import com.smartbuilders.synchronizer.ids.database.DatabaseHelper;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.utils.ApplicationUtilities;
import com.smartbuilders.synchronizer.ids.utils.ConsumeWebService;
import com.smartbuilders.synchronizer.ids.utils.DataBaseUtilities;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

/**
 * http://developer.android.com/guide/topics/providers/content-provider-creating.html
 * @author jsarco
 *
 */
public class DataBaseContentProvider extends ContentProvider implements OnAccountsUpdateListener {

    public static final String AUTHORITY = BuildConfig.DataBaseContentProvider_AUTHORITY;

    public static final String KEY_USER_ID = "DataBaseContentProvider.USER_ID";
    public static final String KEY_USER_DB_NAME = "DataBaseContentProvider.USER_DB_NAME";
    public static final String KEY_USER_SAVE_DB_EXTERNAL_CARD = "DataBaseContentProvider.USER_SAVE_DB_EXTERNAL_CARD";
    public static final String KEY_SEND_DATA_TO_SERVER = "DataBaseContentProvider.SEND_DATA_TO_SERVER";
    public static final String KEY_DATA_FROM_WS = "DataBaseContentProvider.KEY_DATA_FROM_WS";
    public static final String KEY_DATA_FROM_WS_VALID_SEQS_IDS = "DataBaseContentProvider.KEY_DATA_FROM_WS_VALID_SEQS_IDS";
    public static final String KEY_DATA_FROM_WS_TABLE_NAME = "DataBaseContentProvider.KEY_DATA_FROM_WS_TABLE_NAME";

    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri INTERNAL_DB_URI = Uri.withAppendedPath(CONTENT_URI, "internalDB");
    public static final Uri REMOTE_DB_URI = Uri.withAppendedPath(CONTENT_URI, "remoteDB");
    public static final Uri DROP_USER_DB_URI = Uri.withAppendedPath(CONTENT_URI, "dropUserDB");
    public static final Uri INSERT_UPDATE_DATA_FROM_WS_URI = Uri.withAppendedPath(CONTENT_URI, "insertUpdateDataFromWS");

    private static final int INTERNAL_DB = 1;
    private static final int REMOTE_DB = 2;
    private static final int DROP_USER_DB = 3;
    private static final int INSERT_UPDATE_DATA_FROM_WS = 4;

    private static final UriMatcher uriMatcher;

    private static DatabaseHelper dbHelper;
    private static SQLiteDatabase mUserReadableDB;
    private static SQLiteDatabase mUserWriteableDB;
    private static SQLiteDatabase mIDSReadableDB;
    private static SQLiteDatabase mIDSWriteableDB;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "internalDB", INTERNAL_DB);
        uriMatcher.addURI(AUTHORITY, "remoteDB", REMOTE_DB);
        uriMatcher.addURI(AUTHORITY, "dropUserDB", DROP_USER_DB);
        uriMatcher.addURI(AUTHORITY, "insertUpdateDataFromWS", INSERT_UPDATE_DATA_FROM_WS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri arg0) {
        return null;
    }

    /**
     * Utilizar el metodo Update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        //TODO: manejar permisos en el manifest para mejorar la seguridad
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(getContext());
        }
		AccountManager mAccountManager = AccountManager.get(getContext());
		mAccountManager.addOnAccountsUpdatedListener(this, null, false);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor response;
		switch (uriMatcher.match(uri)) {
	    	case INTERNAL_DB:
	    		if(uri.getQueryParameter(KEY_USER_ID)!=null){
                    if (mUserReadableDB==null) {
                        mUserReadableDB = new DatabaseHelper(getContext(), ApplicationUtilities
                                .getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID))).getReadableDatabase();
                    }
                    response = mUserReadableDB.rawQuery(selection, selectionArgs);
	    		}else{
                    if(mIDSReadableDB==null) {
                        mIDSReadableDB = dbHelper.getReadableDatabase();
                    }
                    response = mIDSReadableDB.rawQuery(selection, selectionArgs);
	    		}
			break;
	        case REMOTE_DB:
	        	response = execQueryRemoteDB(uri, selection);
	        break;
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return response;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int response = 0;
		switch (uriMatcher.match(uri)) {
	    	case INTERNAL_DB:
	    		if(uri.getQueryParameter(KEY_USER_ID)!=null){
					if(mUserWriteableDB==null){
                        mUserWriteableDB = new DatabaseHelper(getContext(), ApplicationUtilities
                                .getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID))).getReadableDatabase();
                    }
					if (selectionArgs!=null) {
                    	mUserWriteableDB.execSQL(selection, selectionArgs);
					} else {
						mUserWriteableDB.execSQL(selection);
					}
					//if(uri.getQueryParameter(KEY_SEND_DATA_TO_SERVER)!=null
					//		&& Boolean.valueOf(uri.getQueryParameter(KEY_SEND_DATA_TO_SERVER))){
					//	Intent syncDataIntent = new Intent(getContext(), SyncDataWithServer.class);
					//	syncDataIntent.putExtra(SyncDataWithServer.KEY_USER_ID, uri.getQueryParameter(KEY_USER_ID));
					//	syncDataIntent.putExtra(SyncDataWithServer.KEY_SQL_SELECTION, selection);
					//	syncDataIntent.putExtra(SyncDataWithServer.KEY_SQL_SELECTION_ARGS, selectionArgs);
					//	if (getContext()!=null) {
					//		getContext().startService(syncDataIntent);
					//	}
					//}
	    		}else{
                    if(mIDSWriteableDB==null){
                        mIDSWriteableDB = dbHelper.getWritableDatabase();
                    }
                    if (selectionArgs!=null) {
                        mIDSWriteableDB.execSQL(selection, selectionArgs);
                    } else {
                        mIDSWriteableDB.execSQL(selection);
                    }
	    		}
	    		response = 1;
			break;
	        case REMOTE_DB:
	        	response = execUpdateRemoteDB(uri, selection);
	        break;
	        case DROP_USER_DB:
	        	if(uri.getQueryParameter(KEY_USER_DB_NAME)!=null
					&& uri.getQueryParameter(KEY_USER_SAVE_DB_EXTERNAL_CARD)!=null){
                    if (getContext()!=null) {
	    			    getContext().deleteDatabase(uri.getQueryParameter(KEY_USER_DB_NAME));
                    }
	    		}else{
	    			throw new IllegalArgumentException("Incomplete URI " + uri);
	    		}
	        break;
			case INSERT_UPDATE_DATA_FROM_WS:
				insertUpdateDataFromWS(uri);
				break;
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri);
		}
		return response;
	}

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
	public void onAccountsUpdated(final Account[] accounts) {
	    //Removes content associated with accounts that are not currently in the device
	    ApplicationUtilities.cleanUsersData(getContext(), accounts);
	}
	
	/**
	 * 
	 * @param uri
	 * @param sql
	 * @return
	 */
	private Cursor execQueryRemoteDB(Uri uri, String sql) {
		Cursor cursor;
		if(uri.getQueryParameter(KEY_USER_ID)==null){
			throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("authToken", user.getAuthToken());
			parameters.put("userId", user.getServerUserId());
			parameters.put("sql", sql);
	    	ConsumeWebService a = new ConsumeWebService(getContext(),
	    												user.getServerAddress(), 
										    			"/IntelligentDataSynchronizer/services/ManageRemoteDBAccess?wsdl", 
										    			"executeQuery", 
										    			"urn:executeQuery", 
										    			parameters);
			try {
				Object result = a.getWSResponse();
				if(result instanceof SoapPrimitive){
					cursor = DataBaseUtilities.parseJsonCursorToCursor(result.toString());
				}else if (result !=null){
					throw new Exception("Error while executing execQueryRemoteDB("+user.getServerAddress()+", "+sql+"), ClassCastException.");
				}else{
					throw new Exception("Error while executing execQueryRemoteDB("+user.getServerAddress()+", "+sql+"), result is null.");
				}
			} catch(Exception e){
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}
		}
		return cursor;
	}
	
	/**
	 * 
	 * @param uri
	 * @param sql
	 * @return
	 */
	private int execUpdateRemoteDB(Uri uri, String sql){
		int rowsAffected;
		if(uri.getQueryParameter(KEY_USER_ID)==null){
			throw new IllegalArgumentException("No userId parameter found in the Uri passed.");
		}else{
			User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
			LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
			parameters.put("authToken", user.getAuthToken());
			parameters.put("userId", user.getServerUserId());
			parameters.put("sql", sql);
	    	ConsumeWebService a = new ConsumeWebService(getContext(),
														user.getServerAddress(), 
										    			"/IntelligentDataSynchronizer/services/ManageRemoteDBAccess?wsdl", 
										    			"executeUpdate", 
										    			"urn:executeUpdate", 
										    			parameters);
			try {
				Object result = a.getWSResponse();
				if(result instanceof SoapPrimitive){
					rowsAffected = Integer.valueOf(result.toString());
				}else if (result==null){
					throw new NullPointerException("Error while executing execUpdateRemoteDB("+user+", "+sql+"), result is null.");
				}else{
					throw new ClassCastException("Error while executing execUpdateRemoteDB("+user+", "+sql+"), ClassCastException.");
				}
			} catch(Exception e){
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}
		}
		return rowsAffected;
	}

	/**
	 *
	 * @param uri
	 * @throws SQLException
	 */
	private void insertUpdateDataFromWS(Uri uri) throws SQLException {
        try {
            User user = ApplicationUtilities.getUserByIdFromAccountManager(getContext(), uri.getQueryParameter(KEY_USER_ID));
            if (user==null) {
                if (mIDSWriteableDB == null) {
                    mIDSWriteableDB = dbHelper.getWritableDatabase();
                }
            } else if (mUserWriteableDB == null) {
                mUserWriteableDB = new DatabaseHelper(getContext(), user).getReadableDatabase();
            }
            DataBaseUtilities.insertDataFromWSResultData((user==null ? mIDSWriteableDB : mUserWriteableDB),
                    uri.getQueryParameter(KEY_DATA_FROM_WS), uri.getQueryParameter(KEY_DATA_FROM_WS_VALID_SEQS_IDS),
                    uri.getQueryParameter(KEY_DATA_FROM_WS_TABLE_NAME));
        } catch (Exception e) {
            throw new SQLiteException(e.getMessage());
        }
	}

}

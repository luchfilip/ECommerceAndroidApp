package com.jasgcorp.ids.datamanager;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.jasgcorp.ids.database.DatabaseHelper;
import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.utils.ApplicationUtilities;
import com.jasgcorp.ids.utils.ConsumeWebService;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import net.iharder.Base64;

public class TableDataReceiverFromServer extends Thread {
	
	private static final String TAG = TableDataReceiverFromServer.class.getSimpleName();

	private Context context;
	private boolean sync = true;
	private String exceptionMessage;
	private String exceptionClass;
	private float syncPercentage;
	private User mUser;
	
	public TableDataReceiverFromServer(User user, Context context) throws Exception{
		this.context = context;
		this.mUser = user;
	}
	/**
	 * detiene el hilo de sincronizacion
	 */
	public void stopSynchronization(){
		Log.d(TAG, "stopSynchronization()");
		sync = false;
	}
	
	public String getExceptionMessage(){
		return exceptionMessage;
	}
	
	public String getExceptionClass(){
		return exceptionClass;
	}
	
	@Override
	public void run() {
		Log.d(TAG, "run()");
		try {
			sync = Utils.appRequireInitialLoadOfGlobalData(context);
			if(sync){
				loadInitialGlobalDataFromWS(context);
			}
			sync = Utils.appRequireInitialLoadOfUserData(context, mUser);
			if(sync){
				loadInitialUserDataFromWS(context, mUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
			exceptionMessage = e.getMessage();
			exceptionClass = e.getClass().getName();
		}
		sync = false;
	}

	public void loadInitialGlobalDataFromWS(Context context) throws Exception {
		long initTime = System.currentTimeMillis();
        syncPercentage = 0;
        if(sync){
            execRemoteQueryAndInsert(context, null,
                    "select APP_PARAMETER_ID, PARAMETER_DESCRIPTION, TEXT_VALUE, INTEGER_VALUE, DOUBLE_VALUE, " +
                            " BOOLEAN_VALUE, DATE_VALUE, DATETIME_VALUE " +
                        " from APP_PARAMETER where IS_ACTIVE = 'Y'",
                    "INSERT OR REPLACE INTO APP_PARAMETER (APP_PARAMETER_ID, PARAMETER_DESCRIPTION, " +
                            " TEXT_VALUE, INTEGER_VALUE, DOUBLE_VALUE, BOOLEAN_VALUE, DATE_VALUE, DATETIME_VALUE) " +
                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            syncPercentage = 5;
        }
		if(sync){
			execRemoteQueryAndInsert(context, null,
					"select COMPANY_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, " +
                        " EMAIL_ADDRESS, CONTACT_CENTER_PHONE_NUMBER, PHONE_NUMBER, FAX_NUMBER, WEB_PAGE " +
							" from COMPANY where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO COMPANY (COMPANY_ID, NAME, COMMERCIAL_NAME, TAX_ID, " +
                        " ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, CONTACT_CENTER_PHONE_NUMBER, PHONE_NUMBER, FAX_NUMBER, WEB_PAGE) " +
							" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			syncPercentage = 10;
		}
		if(sync){
			execRemoteQueryAndInsert(context, null,
					"select PRODUCT_ID, SUBCATEGORY_ID, BRAND_ID, NAME, DESCRIPTION, PURPOSE, " +
						" OBSERVATION, REFERENCE_ID, ORIGIN, INTERNAL_CODE, COMMERCIAL_PACKAGE_UNITS, " +
						" COMMERCIAL_PACKAGE, INVENTORY_PACKAGE_UNITS, INVENTORY_PACKAGE, LAST_RECEIVED_DATE, PRODUCT_TAX_ID " +
					" from PRODUCT where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT (PRODUCT_ID, SUBCATEGORY_ID, BRAND_ID, NAME, " +
						" DESCRIPTION, PURPOSE, OBSERVATION, REFERENCE_ID, ORIGIN, INTERNAL_CODE, " +
						" COMMERCIAL_PACKAGE_UNITS, COMMERCIAL_PACKAGE, INVENTORY_PACKAGE_UNITS, " +
						" INVENTORY_PACKAGE, LAST_RECEIVED_DATE, PRODUCT_TAX_ID) " +
					" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            syncPercentage = 20;
		}
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select BRAND_ID, NAME, DESCRIPTION from BRAND where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BRAND (BRAND_ID, NAME, DESCRIPTION) VALUES (?, ?, ?)");
            syncPercentage = 25;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select CATEGORY_ID, NAME, DESCRIPTION from Category where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO CATEGORY (CATEGORY_ID, NAME, DESCRIPTION) VALUES (?, ?, ?)");
            syncPercentage = 30;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select MAINPAGE_PRODUCT_ID, MAINPAGE_PRODUCT_SECTION_ID, PRODUCT_ID, PRIORITY from MAINPAGE_PRODUCT where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO MAINPAGE_PRODUCT (MAINPAGE_PRODUCT_ID, MAINPAGE_PRODUCT_SECTION_ID, PRODUCT_ID, PRIORITY) VALUES (?, ?, ?, ?)");
            syncPercentage = 40;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select MAINPAGE_PRODUCT_SECTION_ID, NAME, DESCRIPTION, PRIORITY from MAINPAGE_PRODUCT_SECTION where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO MAINPAGE_PRODUCT_SECTION (MAINPAGE_PRODUCT_SECTION_ID, NAME, DESCRIPTION, PRIORITY) VALUES (?, ?, ?, ?)");
            syncPercentage = 50;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"SELECT PRODUCT_ID, AVAILABILITY FROM PRODUCT_AVAILABILITY WHERE IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_AVAILABILITY (PRODUCT_ID, AVAILABILITY) VALUES (?, ?)");
            syncPercentage = 60;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select PRODUCT_ID, FILE_NAME, PRIORITY from PRODUCT_IMAGE where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_IMAGE (PRODUCT_ID, FILE_NAME, PRIORITY) VALUES (?, ?, ?)");
            syncPercentage = 70;
        }
		if(sync){
			execRemoteQueryAndInsert(context, null,
					"select PRODUCT_ID, RATING from PRODUCT_RATING where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_RATING (PRODUCT_ID, RATING) VALUES (?, ?)");
			syncPercentage = 75;
		}
        if(sync){
            execRemoteQueryAndInsert(context, null,
                    "select PRODUCT_TAX_ID, TAX_PERCENTAGE, TAX_NAME from PRODUCT_TAX where IS_ACTIVE = 'Y'",
                    "INSERT OR REPLACE INTO PRODUCT_TAX (PRODUCT_TAX_ID, TAX_PERCENTAGE, TAX_NAME) VALUES (?, ?, ?)");
            syncPercentage = 80;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION from SUBCATEGORY where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO SUBCATEGORY (SUBCATEGORY_ID, CATEGORY_ID, NAME, DESCRIPTION) VALUES (?, ?, ?, ?)");
            syncPercentage = 85;
        }
        if(sync){
			execRemoteQueryAndInsert(context, null,
					"select PRODUCT_ID, PRODUCT_RELATED_ID, TIMES from PRODUCT_SHOPPING_RELATED where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO PRODUCT_SHOPPING_RELATED (PRODUCT_ID, PRODUCT_RELATED_ID, TIMES) VALUES (?, ?, ?)");
            syncPercentage = 90;
        }
		if(sync){
			execRemoteQueryAndInsert(context, null,
					"select BANNER_ID, PRODUCT_ID, BRAND_ID, SUBCATEGORY_ID, CATEGORY_ID, IMAGE_FILE_NAME from BANNER where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BANNER (BANNER_ID, PRODUCT_ID, BRAND_ID, SUBCATEGORY_ID, CATEGORY_ID, IMAGE_FILE_NAME) VALUES (?, ?, ?, ?, ?, ?)");
			syncPercentage = 95;
		}
		if(sync){
			execRemoteQueryAndInsert(context, null,
					"select BRAND_PROMOTIONAL_CARD_ID, BRAND_ID, IMAGE_FILE_NAME, PROMOTIONAL_TEXT, " +
							" BACKGROUND_R_COLOR, BACKGROUND_G_COLOR, BACKGROUND_B_COLOR, PROMOTIONAL_TEXT_R_COLOR, " +
							" PROMOTIONAL_TEXT_G_COLOR, PROMOTIONAL_TEXT_B_COLOR " +
						" from BRAND_PROMOTIONAL_CARD where IS_ACTIVE = 'Y'",
					"INSERT OR REPLACE INTO BRAND_PROMOTIONAL_CARD (BRAND_PROMOTIONAL_CARD_ID, BRAND_ID, " +
							" IMAGE_FILE_NAME, PROMOTIONAL_TEXT, BACKGROUND_R_COLOR, BACKGROUND_G_COLOR, " +
							" BACKGROUND_B_COLOR, PROMOTIONAL_TEXT_R_COLOR, PROMOTIONAL_TEXT_G_COLOR, " +
							" PROMOTIONAL_TEXT_B_COLOR) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			syncPercentage = 100;
		}
		Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
	}

	public void loadInitialUserDataFromWS(Context context, User user) throws Exception {
		long initTime = System.currentTimeMillis();
		syncPercentage = 0;
		if(sync){
			execRemoteQueryAndInsert(context, user,
					"select BUSINESS_PARTNER_ID, USER_ID, INTERNAL_CODE, NAME, COMMERCIAL_NAME, TAX_ID, " +
                        " ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from BUSINESS_PARTNER where USER_ID = "+user.getServerUserId()+" AND IS_ACTIVE = 'Y' ",
					"INSERT OR REPLACE INTO BUSINESS_PARTNER (BUSINESS_PARTNER_ID, USER_ID, INTERNAL_CODE, " +
                        " NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			syncPercentage = 100;
		}
		Log.d(TAG, "Total Load Time: "+(System.currentTimeMillis() - initTime)+"ms");
	}

	/**
	 *
	 * @param context
	 * @param user
	 * @param sql
	 * @param insertSentence
	 */
	private void execRemoteQueryAndInsert(Context context, User user, String sql,
                                          String insertSentence) throws Exception {
		LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
		parameters.put("authToken", mUser.getAuthToken());
		parameters.put("userId", mUser.getServerUserId());
		parameters.put("sql", sql);
		ConsumeWebService a =
                new ConsumeWebService(context,
                        mUser.getServerAddress(),
                        "/IntelligentDataSynchronizer/services/ManageRemoteDBAccess?wsdl",
                        "executeQuery",
                        "urn:executeQuery",
                        parameters);
        Object result = a.getWSResponse();
        if (result instanceof SoapPrimitive) {
            insertDataFromWSResultData(result.toString(), insertSentence, context, user);
        } else if(result instanceof Exception) {
            throw (Exception) result;
        } else if (result!=null) {
            throw new Exception("Error while executing execQueryRemoteDB("+mUser.getServerAddress()+", "+sql+"), ClassCastException.");
        } else {
            throw new Exception("Error while executing execQueryRemoteDB("+mUser.getServerAddress()+", "+sql+"), result is null.");
        }
	}

	/**
	 *
	 * @param data
	 * @param insertSentence
	 * @param context
	 * @throws Exception
	 */
	public void insertDataFromWSResultData(String data, String insertSentence, Context context, User user) throws Exception {
		int counterEntireCompressedData = 0;
		int counter = 0;
		JSONArray jsonArray = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(data, Base64.GZIP)));
		Iterator<?> keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
		if(keys.hasNext()){
			int columnCount = 0;
			JSONArray jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String)keys.next()), Base64.GZIP)));
			try{
				counter = 1;
				Iterator<?> keysTemp = jsonArray2.getJSONObject(counter).keys();
				while(keysTemp.hasNext()){
					keysTemp.next();
					columnCount++;
				}
			} catch (Exception e){
				e.printStackTrace();
			}

			int columnIndex;
			SQLiteDatabase db = null;
			SQLiteStatement statement = null;
			try {
                if(user == null){
                    db = (new DatabaseHelper(context)).getWritableDatabase();
                }else{
				    db = (new DatabaseHelper(context, user)).getWritableDatabase();
                }

				statement = db.compileStatement(insertSentence);
				db.beginTransaction();
				//Se itera a traves de la data
				while (counter <= jsonArray2.length()) {
					if (++counter >= jsonArray2.length()) {
						if (keys.hasNext()) {
							counter = 0;
							jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
							if (jsonArray2.length() < 1) {
								break;
							}
						} else {
							if (++counterEntireCompressedData >= jsonArray.length()) {
								break;
							} else {
								counter = 0;
								keys = jsonArray.getJSONObject(counterEntireCompressedData).keys();
								jsonArray2 = new JSONArray(ApplicationUtilities.ungzip(Base64.decode(jsonArray.getJSONObject(counterEntireCompressedData).getString((String) keys.next()), Base64.GZIP)));
								if (jsonArray2.length() < 1) {
									break;
								}
							}
						}
					}
					//Se prepara la data que se insertara
					statement.clearBindings();
					for (columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
						try {
							//data que se insertara
							statement.bindString(columnIndex, jsonArray2.getJSONObject(counter).getString(String.valueOf(columnIndex)));
						} catch (JSONException e) {
                            //Log.w(TAG, e.getMessage()!=null ? e.getMessage() : "insertDataFromWSResultData - JSONException");
                        } catch (Exception e) {
							e.printStackTrace();
						}
					}
					statement.execute();
					//Fin de preparacion de la data que se insertara
				}
				db.setTransactionSuccessful();
			} catch (Exception e) {
				e.printStackTrace();
                throw e;
			} finally {
				if(statement!=null) {
					try {
						statement.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (db!=null) {
					try {
						db.endTransaction();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						db.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public float getSyncPercentage() {
		return syncPercentage;
	}

}

package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Company;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Created by stein on 5/6/2016.
 */
public class CompanyDB {

//    private Context mContext;
//    private User mUser;
//
//    public CompanyDB(Context context, User user){
//        this.mContext = context;
//        this.mUser = user;
//    }
//
//    public String insertUpdateCompany(Company company) {
//        try {
//            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
//                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
//                    null,
//                    "insert or replace into company (NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, " +
//                            " EMAIL_ADDRESS, PHONE_NUMBER, APP_VERSION, APP_USER_NAME) " +
//                            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ",
//                    new String[]{company.getName(), company.getCommercialName(), company.getTaxId(),
//                            company.getAddress(), company.getContactPerson(), company.getEmailAddress(),
//                            company.getPhoneNumber(), Utils.getAppVersionName(mContext), mUser.getUserName()});
//            if (rowsAffected <= 0){
//                return "No se insertó o actualizó el registro en la base de datos.";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return e.getMessage();
//        }
//        return null;
//    }
//
//    public Company getCompany(){
//        Cursor c = null;
//        try {
//            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
//                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
//                    .build(), null,
//                    "select NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
//                        " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
//                        " from COMPANY where IS_ACTIVE = ?",
//                    new String[]{"Y"}, null);
//            if(c!=null && c.moveToNext()){
//                Company company = new Company();
//                company.setName(c.getString(0));
//                company.setCommercialName(c.getString(1));
//                company.setTaxId(c.getString(2));
//                company.setAddress(c.getString(3));
//                company.setContactPerson(c.getString(4));
//                company.setEmailAddress(c.getString(5));
//                company.setPhoneNumber(c.getString(6));
//                return company;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if(c!=null){
//                try {
//                    c.close();
//                } catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//    }

}

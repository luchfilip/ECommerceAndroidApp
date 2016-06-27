package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 30/5/2016.
 */
public class UserBusinessPartnerDB {

    private Context mContext;
    private User mCurrentUser;

    public UserBusinessPartnerDB(Context context, User user){
        this.mContext = context;
        this.mCurrentUser = user;
    }

    public ArrayList<BusinessPartner> getActiveUserBusinessPartners(){
        ArrayList<BusinessPartner> activeBusinessPartners = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "select USER_BUSINESS_PARTNER_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                    " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from USER_BUSINESS_PARTNER " +
                    " where IS_ACTIVE = ? " +
                    " order by USER_BUSINESS_PARTNER_ID desc";
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null, sql, new String[]{"Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    BusinessPartner businessPartner = new BusinessPartner();
                    businessPartner.setId(c.getInt(0));
                    businessPartner.setName(c.getString(1));
                    businessPartner.setCommercialName(c.getString(2));
                    businessPartner.setTaxId(c.getString(3));
                    businessPartner.setAddress(c.getString(4));
                    businessPartner.setContactPerson(c.getString(5));
                    businessPartner.setEmailAddress(c.getString(6));
                    businessPartner.setPhoneNumber(c.getString(7));
                    activeBusinessPartners.add(businessPartner);
                }
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
        return activeBusinessPartners;
    }

    public String registerUserBusinessPartner(BusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                    null,
                    "INSERT INTO USER_BUSINESS_PARTNER (NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, " +
                            " EMAIL_ADDRESS, PHONE_NUMBER, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                    new String[]{businessPartner.getName(), businessPartner.getCommercialName(), businessPartner.getTaxId(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), Utils.getAppVersionName(mContext),
                            mCurrentUser.getUserName(), Utils.getMacAddress(mContext)});
            if (rowsAffected <= 0){
                return "No se insertó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateUserBusinessPartner(BusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                    null,
                    "UPDATE USER_BUSINESS_PARTNER SET NAME = ?, COMMERCIAL_NAME = ?, ADDRESS = ?, CONTACT_PERSON = ?, " +
                            " EMAIL_ADDRESS = ?, PHONE_NUMBER = ?, APP_VERSION = ?, APP_USER_NAME = ?, UPDATE_TIME = ? " +
                            " where USER_BUSINESS_PARTNER_ID = ? ",
                    new String[]{businessPartner.getName(), businessPartner.getCommercialName(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), Utils.getAppVersionName(mContext),
                            mCurrentUser.getUserName(), "datetime('now')", String.valueOf(businessPartner.getId())});
            if (rowsAffected <= 0){
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deactivateUserBusinessPartner(int businessPartnerId){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId()).build(),
                    null,
                    "UPDATE USER_BUSINESS_PARTNER SET IS_ACTIVE = ? WHERE USER_BUSINESS_PARTNER_ID = ?",
                    new String[]{"N", String.valueOf(businessPartnerId)});
            if (rowsAffected <= 0){
                return "No se desactivó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public boolean isTaxIdRegistered(String taxID){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "select COUNT(TAX_ID) from USER_BUSINESS_PARTNER where TAX_ID = ? AND IS_ACTIVE = ? ",
                    new String[]{taxID, "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0)>0;
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
        return false;
    }

    public BusinessPartner getActiveUserBusinessPartnerById(int businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mCurrentUser.getUserId())
                    .build(), null,
                    "select NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from USER_BUSINESS_PARTNER " +
                    " where USER_BUSINESS_PARTNER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), "Y"}, null);
            if(c!=null && c.moveToNext()){
                BusinessPartner businessPartner = new BusinessPartner();
                businessPartner.setId(businessPartnerId);
                businessPartner.setName(c.getString(0));
                businessPartner.setCommercialName(c.getString(1));
                businessPartner.setTaxId(c.getString(2));
                businessPartner.setAddress(c.getString(3));
                businessPartner.setContactPerson(c.getString(4));
                businessPartner.setEmailAddress(c.getString(5));
                businessPartner.setPhoneNumber(c.getString(6));
                return businessPartner;
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
        return null;
    }

}

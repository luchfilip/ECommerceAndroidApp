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
public class BusinessPartnerDB {

    private Context mContext;
    private User mUser;

    public BusinessPartnerDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<BusinessPartner> getActiveBusinessPartners(){
        ArrayList<BusinessPartner> activeBusinessPartners = new ArrayList<>();
        Cursor c = null;
        try {
            String sql = "select BUSINESS_PARTNER_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                    " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from BUSINESS_PARTNER where ISACTIVE = ? order by BUSINESS_PARTNER_ID desc";
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null, sql, new String[]{"Y"}, null);
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

    public String registerBusinessPartner(BusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "INSERT INTO BUSINESS_PARTNER (NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, " +
                            " EMAIL_ADDRESS, PHONE_NUMBER, APP_VERSION, APP_USER_NAME) " +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                    new String[]{businessPartner.getName(), businessPartner.getCommercialName(), businessPartner.getTaxId(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), Utils.getAppVersionName(mContext), mUser.getUserName()});
            if (rowsAffected <= 0){
                return "No se insertó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateBusinessPartner(BusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE BUSINESS_PARTNER SET NAME = ?, COMMERCIAL_NAME = ?, ADDRESS = ?, CONTACT_PERSON = ?, " +
                            " EMAIL_ADDRESS = ?, PHONE_NUMBER = ?, APP_VERSION = ?, APP_USER_NAME = ?, UPDATE_TIME = ? " +
                            " where BUSINESS_PARTNER_ID = ? ",
                    new String[]{businessPartner.getName(), businessPartner.getCommercialName(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), Utils.getAppVersionName(mContext),
                            mUser.getUserName(), "datetime('now')", String.valueOf(businessPartner.getId())});
            if (rowsAffected <= 0){
                return "No se actualizó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String deactivateBusinessPartner(int businessPartnerId){
        try {
            int rowsAffected = mContext.getContentResolver().update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(),
                    null,
                    "UPDATE BUSINESS_PARTNER SET ISACTIVE = ? WHERE BUSINESS_PARTNER_ID = ?",
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
            String sql = "select COUNT(TAX_ID) from BUSINESS_PARTNER where ISACTIVE = ? AND TAX_ID = ?";
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null, sql, new String[]{taxID, "Y"}, null);
            if(c.moveToNext()){
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

    public BusinessPartner getActiveBusinessPartnerById(int businessPartnerId) {
        Cursor c = null;
        try {
            String sql = "select NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                    " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from BUSINESS_PARTNER where BUSINESS_PARTNER_ID = ? AND ISACTIVE = ?";
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null, sql, new String[]{String.valueOf(businessPartnerId), "Y"}, null);
            if(c.moveToNext()){
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

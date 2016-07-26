package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.model.User;
import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;

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
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select BUSINESS_PARTNER_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                        " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER, INTERNAL_CODE " +
                    " from BUSINESS_PARTNER " +
                    " where USER_ID = ? AND IS_ACTIVE = ? " +
                    " order by BUSINESS_PARTNER_ID desc",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y"}, null);
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
                    businessPartner.setInternalCode(c.getString(8));
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

    public BusinessPartner getActiveBusinessPartnerById(int businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                        " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER, INTERNAL_CODE " +
                    " from BUSINESS_PARTNER " +
                    " where BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()), "Y"},
                    null);
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
                businessPartner.setInternalCode(c.getString(7));
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

    public int getMaxActiveBusinessPartnerId(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select MAX(BUSINESS_PARTNER_ID) from BUSINESS_PARTNER where USER_ID = ? AND IS_ACTIVE = ? ",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y"}, null);
            if(c!=null && c.moveToNext()){
                return c.getInt(0);
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
        return 0;
    }

}

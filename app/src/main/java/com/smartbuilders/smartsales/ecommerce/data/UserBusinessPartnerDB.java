package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.BusinessPartnerAddress;
import com.smartbuilders.smartsales.ecommerce.model.UserBusinessPartner;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.utils.DateFormat;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

import java.util.ArrayList;

/**
 * Created by stein on 30/5/2016.
 */
public class UserBusinessPartnerDB {

    private Context mContext;
    private User mUser;

    public UserBusinessPartnerDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<UserBusinessPartner> getUserBusinessPartners(){
        ArrayList<UserBusinessPartner> businessPartners = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select USER_BUSINESS_PARTNER_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, " +
                        " CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from USER_BUSINESS_PARTNER " +
                    " where USER_ID = ? AND IS_ACTIVE = ? " +
                    " order by USER_BUSINESS_PARTNER_ID desc",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    UserBusinessPartner businessPartner = new UserBusinessPartner();
                    businessPartner.setId(c.getInt(0));
                    businessPartner.setName(c.getString(1));
                    businessPartner.setCommercialName(c.getString(2));
                    businessPartner.setTaxId(c.getString(3));
                    businessPartner.setAddress(c.getString(4));
                    businessPartner.setContactPerson(c.getString(5));
                    businessPartner.setEmailAddress(c.getString(6));
                    businessPartner.setPhoneNumber(c.getString(7));
                    businessPartners.add(businessPartner);
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
        UserBusinessPartnerAddressDB userBusinessPartnerAddressDB = new UserBusinessPartnerAddressDB(mContext, mUser);
        for (UserBusinessPartner businessPartner : businessPartners) {
            businessPartner.setAddresses(userBusinessPartnerAddressDB
                    .getUserBusinessPartnerAddresses(businessPartner.getId(), BusinessPartnerAddress.TYPE_DELIVERY_ADDRESS));
        }
        return businessPartners;
    }

    public String registerUserBusinessPartner(UserBusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "INSERT INTO USER_BUSINESS_PARTNER (USER_BUSINESS_PARTNER_ID, USER_ID, NAME, COMMERCIAL_NAME, TAX_ID, " +
                        " ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER, CREATE_TIME, APP_VERSION, APP_USER_NAME, DEVICE_MAC_ADDRESS) " +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ",
                    new String[]{String.valueOf(UserTableMaxIdDB.getNewIdForTable(mContext, mUser, "USER_BUSINESS_PARTNER")),
                            String.valueOf(mUser.getServerUserId()), businessPartner.getName(),
                            businessPartner.getCommercialName(), businessPartner.getTaxId(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), DateFormat.getCurrentDateTimeSQLFormat(),
                            Utils.getAppVersionName(mContext), mUser.getUserName(), Utils.getMacAddress(mContext)});
            if (rowsAffected <= 0){
                return "No se insertó el registro en la base de datos.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return null;
    }

    public String updateUserBusinessPartner(UserBusinessPartner businessPartner){
        try {
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                    null,
                    "UPDATE USER_BUSINESS_PARTNER SET NAME = ?, COMMERCIAL_NAME = ?, TAX_ID = ?, ADDRESS = ?, CONTACT_PERSON = ?, " +
                        " EMAIL_ADDRESS = ?, PHONE_NUMBER = ?, APP_VERSION = ?, APP_USER_NAME = ?, UPDATE_TIME = ?, SEQUENCE_ID = 0 " +
                    " where USER_BUSINESS_PARTNER_ID = ? AND USER_ID = ?",
                    new String[]{businessPartner.getName(), businessPartner.getCommercialName(), businessPartner.getTaxId(),
                            businessPartner.getAddress(), businessPartner.getContactPerson(), businessPartner.getEmailAddress(),
                            businessPartner.getPhoneNumber(), Utils.getAppVersionName(mContext),
                            mUser.getUserName(), DateFormat.getCurrentDateTimeSQLFormat(),
                            String.valueOf(businessPartner.getId()),
                            String.valueOf(mUser.getServerUserId())});
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
            int rowsAffected = mContext.getContentResolver()
                    .update(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .appendQueryParameter(DataBaseContentProvider.KEY_SEND_DATA_TO_SERVER, String.valueOf(Boolean.TRUE)).build(),
                            null,
                            "UPDATE USER_BUSINESS_PARTNER SET IS_ACTIVE = ?, SEQUENCE_ID = 0 WHERE USER_BUSINESS_PARTNER_ID = ? AND USER_ID = ?",
                            new String[]{"N", String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId())});
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
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select COUNT(TAX_ID) from USER_BUSINESS_PARTNER where TAX_ID = ? AND USER_ID = ? AND IS_ACTIVE = ? ",
                    new String[]{taxID, String.valueOf(mUser.getServerUserId()), "Y"}, null);
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

    public UserBusinessPartner getUserBusinessPartnerById(int businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, PHONE_NUMBER " +
                    " from USER_BUSINESS_PARTNER " +
                    " where USER_BUSINESS_PARTNER_ID = ? AND USER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(mUser.getServerUserId()), "Y"}, null);
            if(c!=null && c.moveToNext()){
                UserBusinessPartner businessPartner = new UserBusinessPartner();
                businessPartner.setId(businessPartnerId);
                businessPartner.setName(c.getString(0));
                businessPartner.setCommercialName(c.getString(1));
                businessPartner.setTaxId(c.getString(2));
                businessPartner.setAddress(c.getString(3));
                businessPartner.setContactPerson(c.getString(4));
                businessPartner.setEmailAddress(c.getString(5));
                businessPartner.setPhoneNumber(c.getString(6));
                c.close();
                businessPartner.setAddresses((new UserBusinessPartnerAddressDB(mContext, mUser))
                        .getUserBusinessPartnerAddresses(businessPartner.getId(), BusinessPartnerAddress.TYPE_DELIVERY_ADDRESS));
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

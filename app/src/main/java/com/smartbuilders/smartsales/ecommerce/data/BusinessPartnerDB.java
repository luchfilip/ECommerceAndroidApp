package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.BusinessPartnerAddress;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
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

    public ArrayList<BusinessPartner> getBusinessPartners(){
        ArrayList<BusinessPartner> activeBusinessPartners = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select bp.BUSINESS_PARTNER_ID, bp.NAME, bp.COMMERCIAL_NAME, bp.TAX_ID, bp.ADDRESS, " +
                        " bp.CONTACT_PERSON, bp.EMAIL_ADDRESS, bp.PHONE_NUMBER, bp.INTERNAL_CODE " +
                    " from BUSINESS_PARTNER bp " +
                        " inner join SALES_REP SR ON SR.USER_ID = ? AND SR.IS_ACTIVE = ? " +
                        " inner join USER_BUSINESS_PARTNERS ubp on ubp.business_partner_id = bp.business_partner_id and ubp.user_id = SR.sales_rep_id and ubp.is_active = ? " +
                    " where bp.IS_ACTIVE = ? " +
                    " order by bp.BUSINESS_PARTNER_ID desc",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y", "Y", "Y"}, null);
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
        BusinessPartnerAddressDB businessPartnerAddressDB = new BusinessPartnerAddressDB(mContext, mUser);
        for (BusinessPartner businessPartner : activeBusinessPartners) {
            businessPartner.setAddresses(businessPartnerAddressDB
                    .getBusinessPartnerAddresses(businessPartner.getId(), BusinessPartnerAddress.TYPE_DELIVERY_ADDRESS));
        }
        return activeBusinessPartners;
    }

    public BusinessPartner getBusinessPartnerById(int businessPartnerId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select bp.NAME, bp.COMMERCIAL_NAME, bp.TAX_ID, bp.ADDRESS, " +
                        " bp.CONTACT_PERSON, bp.EMAIL_ADDRESS, bp.PHONE_NUMBER, bp.INTERNAL_CODE " +
                    " from BUSINESS_PARTNER bp " +
                        " inner join SALES_REP SR ON SR.USER_ID = ? AND SR.IS_ACTIVE = ? " +
                        " inner join USER_BUSINESS_PARTNERS ubp on ubp.business_partner_id = bp.business_partner_id and ubp.user_id = SR.sales_rep_id and ubp.is_active = ? " +
                    " where bp.BUSINESS_PARTNER_ID = ? AND bp.IS_ACTIVE = ?",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y", "Y", String.valueOf(businessPartnerId), "Y"},
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
                c.close();
                businessPartner.setAddresses((new BusinessPartnerAddressDB(mContext, mUser))
                        .getBusinessPartnerAddresses(businessPartner.getId(), BusinessPartnerAddress.TYPE_DELIVERY_ADDRESS));
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

    public int getMaxActiveBusinessPartnerId() throws Exception{
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                    .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                    .build(), null,
                    "select MAX(bp.BUSINESS_PARTNER_ID) from BUSINESS_PARTNER bp " +
                        " inner join SALES_REP SR ON SR.USER_ID = ? AND SR.IS_ACTIVE = ? " +
                        " inner join USER_BUSINESS_PARTNERS ubp on ubp.business_partner_id = bp.business_partner_id and ubp.user_id = SR.sales_rep_id and ubp.is_active = ? " +
                    " where  bp.IS_ACTIVE = ? ",
                    new String[]{String.valueOf(mUser.getServerUserId()), "Y", "Y", "Y"}, null);
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
        throw new Exception("No se pudo obtener el id del cliente.");
    }

}

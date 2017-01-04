package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.BusinessPartnerAddress;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.util.ArrayList;

/**
 * Created by stein on 5/10/2016.
 */
public class BusinessPartnerAddressDB {

    final private Context mContext;
    final private User mUser;

    public BusinessPartnerAddressDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    /**
     *
     * @param businessPartnerId
     * @return
     */
    public ArrayList<BusinessPartnerAddress> getBusinessPartnerAddresses(int businessPartnerId) {
        ArrayList<BusinessPartnerAddress> businessPartnerAddresses = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select BUSINESS_PARTNER_ADDRESS_ID, ADDRESS_TYPE, ADDRESS " +
                        " from BUSINESS_PARTNER_ADDRESS " +
                        " where BUSINESS_PARTNER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), "Y"},
                    null);
            if(c!=null && c.moveToNext()){
                BusinessPartnerAddress businessPartnerAddress = new BusinessPartnerAddress();
                businessPartnerAddress.setId(c.getInt(0));
                businessPartnerAddress.setAddressType(c.getInt(1));
                businessPartnerAddress.setAddress(c.getString(2));
                businessPartnerAddresses.add(businessPartnerAddress);
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
        return businessPartnerAddresses;
    }

    /**
     *
     * @param businessPartnerId
     * @param addressType
     * @return
     */
    public ArrayList<BusinessPartnerAddress> getBusinessPartnerAddresses(int businessPartnerId, int addressType) {
        ArrayList<BusinessPartnerAddress> businessPartnerAddresses = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select BUSINESS_PARTNER_ADDRESS_ID, ADDRESS " +
                        " from BUSINESS_PARTNER_ADDRESS " +
                        " where BUSINESS_PARTNER_ID = ? AND ADDRESS_TYPE = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(businessPartnerId), String.valueOf(addressType), "Y"},
                    null);
            if(c!=null && c.moveToNext()){
                BusinessPartnerAddress businessPartnerAddress = new BusinessPartnerAddress();
                businessPartnerAddress.setId(c.getInt(0));
                businessPartnerAddress.setAddressType(addressType);
                businessPartnerAddress.setAddress(c.getString(1));
                businessPartnerAddresses.add(businessPartnerAddress);
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
        return businessPartnerAddresses;
    }
}

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
public class UserBusinessPartnerAddressDB {

    private Context mContext;
    private User mUser;

    public UserBusinessPartnerAddressDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    /**
     *
     * @param userBusinessPartnerId
     * @return
     */
    public ArrayList<BusinessPartnerAddress> getUserBusinessPartnerAddresses(int userBusinessPartnerId) {
        ArrayList<BusinessPartnerAddress> businessPartnerAddresses = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select USER_BUSINESS_PARTNER_ADDRESS_ID, ADDRESS_TYPE, ADDRESS " +
                            " from USER_BUSINESS_PARTNER_ADDRESS " +
                            " where USER_BUSINESS_PARTNER_ID = ? AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(userBusinessPartnerId), "Y"},
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
     * @param userBusinessPartnerId
     * @param addressType
     * @return
     */
    public ArrayList<BusinessPartnerAddress> getUserBusinessPartnerAddresses(int userBusinessPartnerId, int addressType) {
        ArrayList<BusinessPartnerAddress> businessPartnerAddresses = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select USER_BUSINESS_PARTNER_ADDRESS_ID, ADDRESS " +
                            " from USER_BUSINESS_PARTNER_ADDRESS " +
                            " where USER_BUSINESS_PARTNER_ID = ? AND ADDRESS_TYPE = ?  AND IS_ACTIVE = ?",
                    new String[]{String.valueOf(userBusinessPartnerId),  String.valueOf(addressType), "Y"},
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

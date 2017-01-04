package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Company;

/**
 * Created by stein on 5/6/2016.
 */
public class CompanyDB {

    final private Context mContext;
    final private User mUser;

    public CompanyDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public Company getCompany(){
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId()).build(), null,
                    "select COMPANY_ID, NAME, COMMERCIAL_NAME, TAX_ID, ADDRESS, CONTACT_PERSON, EMAIL_ADDRESS, " +
                        " PHONE_NUMBER, CONTACT_CENTER_PHONE_NUMBER, FAX_NUMBER, WEB_PAGE " +
                    " from COMPANY where IS_ACTIVE = ?",
                    new String[]{"Y"}, null);
            if(c!=null && c.moveToNext()){
                Company company = new Company();
                company.setId(c.getInt(0));
                company.setName(c.getString(1));
                company.setCommercialName(c.getString(2));
                company.setTaxId(c.getString(3));
                company.setAddress(c.getString(4));
                company.setContactPerson(c.getString(5));
                company.setEmailAddress(c.getString(6));
                company.setPhoneNumber(c.getString(7));
                company.setContactCenterPhoneNumber(c.getString(8));
                company.setFaxNumber(c.getString(9));
                company.setWebPage(c.getString(10));
                return company;
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

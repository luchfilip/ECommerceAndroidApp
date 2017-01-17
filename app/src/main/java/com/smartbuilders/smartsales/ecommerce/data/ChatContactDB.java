package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.providers.DataBaseContentProvider;

import java.util.ArrayList;

/**
 * Created by AlbertoSarco on 16/1/2017.
 */
public class ChatContactDB {

    final private Context mContext;
    final private User mUser;

    public ChatContactDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<ChatContact> getAvailableContacts() {
        ArrayList<ChatContact> chatContacts = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select bp.BUSINESS_PARTNER_ID, bp.NAME, bp.COMMERCIAL_NAME, bp.TAX_ID, bp.INTERNAL_CODE " +
                            " from BUSINESS_PARTNER bp " +
                            " inner join USER_BUSINESS_PARTNERS ubp on ubp.business_partner_id = bp.business_partner_id " +
                            " and ubp.user_id in (SELECT SALES_REP_ID FROM SALES_REP WHERE USER_ID = ? AND IS_ACTIVE = 'Y') and ubp.is_active = 'Y' " +
                            " where bp.IS_ACTIVE = 'Y' " +
                            " order by bp.NAME asc",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                while(c.moveToNext()){
                    ChatContact chatContact = new ChatContact();
                    chatContact.setId(c.getInt(0));
                    chatContact.setName(c.getString(1));
                    chatContact.setCommercialName(c.getString(2));
                    chatContact.setTaxId(c.getString(3));
                    chatContact.setInternalCode(c.getString(4));
                    chatContacts.add(chatContact);
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
        return chatContacts;
    }

    public ArrayList<ChatContact> getRecentContacts() {
        ArrayList<ChatContact> chatContacts = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "",
                    new String[]{String.valueOf(mUser.getServerUserId())}, null);
            if(c!=null){
                while(c.moveToNext()){
                    ChatContact chatContact = new ChatContact();
                    chatContact.setId(c.getInt(0));
                    chatContact.setName(c.getString(1));
                    chatContact.setCommercialName(c.getString(2));
                    chatContact.setTaxId(c.getString(3));
                    chatContact.setInternalCode(c.getString(4));
                    chatContacts.add(chatContact);
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
        return chatContacts;
    }

    public ChatContact getChatContactById(int chatContactId) {
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI.buildUpon()
                            .appendQueryParameter(DataBaseContentProvider.KEY_USER_ID, mUser.getUserId())
                            .build(), null,
                    "select bp.NAME, bp.COMMERCIAL_NAME, bp.TAX_ID, bp.INTERNAL_CODE " +
                    " from BUSINESS_PARTNER bp " +
                        " inner join USER_BUSINESS_PARTNERS ubp on ubp.business_partner_id = bp.business_partner_id " +
                            " and ubp.user_id in (SELECT SALES_REP_ID FROM SALES_REP WHERE USER_ID = ? AND IS_ACTIVE = 'Y') and ubp.is_active = 'Y' " +
                    " where bp.IS_ACTIVE = 'Y' and bp.BUSINESS_PARTNER_ID = ? " +
                    " order by bp.NAME asc",
                    new String[]{String.valueOf(mUser.getServerUserId()), String.valueOf(chatContactId)}, null);
            if(c!=null && c.moveToNext()){
                ChatContact chatContact = new ChatContact();
                chatContact.setId(chatContactId);
                chatContact.setName(c.getString(0));
                chatContact.setCommercialName(c.getString(1));
                chatContact.setTaxId(c.getString(2));
                chatContact.setInternalCode(c.getString(3));
                return chatContact;
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

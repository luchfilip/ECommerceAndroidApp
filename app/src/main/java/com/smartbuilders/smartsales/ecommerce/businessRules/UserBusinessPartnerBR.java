package com.smartbuilders.smartsales.ecommerce.businessRules;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;

/**
 * Created by stein on 4/6/2016.
 */
public class UserBusinessPartnerBR {

    public static String validateBusinessPartner(BusinessPartner businessPartner, Context context, User user) {
        if(businessPartner==null){
            return "businessPartner==null";
        }

        UserBusinessPartnerDB userBusinessPartnerDB = new UserBusinessPartnerDB(context, user);
        if(userBusinessPartnerDB.isTaxIdRegistered(businessPartner.getTaxId())){
            return "El RIF \""+businessPartner.getTaxId()+"\" ya se encuentra registrado.";
        }
        return null;
    }
}

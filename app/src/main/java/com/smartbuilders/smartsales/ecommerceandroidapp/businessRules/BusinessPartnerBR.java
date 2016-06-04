package com.smartbuilders.smartsales.ecommerceandroidapp.businessRules;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

/**
 * Created by stein on 4/6/2016.
 */
public class BusinessPartnerBR {

    public static String validateBusinessPartner(BusinessPartner businessPartner, Context context, User user) {
        if(businessPartner==null){
            return "businessPartner==null";
        }

        BusinessPartnerDB businessPartnerDB = new BusinessPartnerDB(context, user);
        if(businessPartnerDB.isTaxIdRegistered(businessPartner.getTaxId())){
            return "El RIF \""+businessPartner.getTaxId()+"\" ya se encuentra registrado.";
        }
        return null;
    }
}

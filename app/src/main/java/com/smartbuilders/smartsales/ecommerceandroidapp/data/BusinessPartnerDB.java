package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BusinessPartner;

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
        BusinessPartner businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 1");
        businessPartner.setTaxId("J00000001");
        activeBusinessPartners.add(businessPartner);

        businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 2");
        businessPartner.setTaxId("J00000002");
        activeBusinessPartners.add(businessPartner);

        businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 3");
        businessPartner.setTaxId("J00000003");
        activeBusinessPartners.add(businessPartner);

        businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 4");
        businessPartner.setTaxId("J00000004");
        activeBusinessPartners.add(businessPartner);

        businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 5");
        businessPartner.setTaxId("J00000005");
        activeBusinessPartners.add(businessPartner);

        businessPartner = new BusinessPartner();
        businessPartner.setName("Cliente 6");
        businessPartner.setTaxId("J00000006");
        activeBusinessPartners.add(businessPartner);
        return activeBusinessPartners;
    }

    public String registerBusinessPartner(String name, String commercialName, String taxId,
                                          String contactPerson, String emailAddress, String phoneNumber){

        return null;
    }

}

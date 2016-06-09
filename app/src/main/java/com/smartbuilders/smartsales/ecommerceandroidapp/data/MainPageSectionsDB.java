package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandPromotionalSection;

import java.util.ArrayList;

/**
 * Created by stein on 1/6/2016.
 */
public class MainPageSectionsDB {

    private Context mContext;
    private User mUser;

    public MainPageSectionsDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public ArrayList<Object> getActiveMainPageSections(){
        ArrayList<Object> mainPageList = new ArrayList<>();

        try {
            BannerSectionDB bannerSectionDB = new BannerSectionDB(mContext, mUser);
            BannerSection bannerSection = bannerSectionDB.getBannerSection();
            if (bannerSection!=null && bannerSection.getBanners()!=null
                    && !bannerSection.getBanners().isEmpty()) {
                mainPageList.add(bannerSection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ProductBrandPromotionSectionDB productBrandPromotionSectionDB =
                    new ProductBrandPromotionSectionDB(mContext, mUser);
            ProductBrandPromotionalSection productBrandPromotionalSection =
                    productBrandPromotionSectionDB.getProductBrandPromotionSection();
            if (productBrandPromotionalSection !=null
                    && productBrandPromotionalSection.getProductBrandPromotionalCards()!=null
                    && !productBrandPromotionalSection.getProductBrandPromotionalCards().isEmpty()) {
                mainPageList.add(productBrandPromotionalSection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mainPageList.addAll((new MainPageProductSectionDB(mContext, mUser)).getActiveMainPageProductSections());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainPageList;
    }
}

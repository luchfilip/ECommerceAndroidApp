package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.R;
import com.smartbuilders.smartsales.ecommerce.model.BannerSection;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.smartsales.ecommerce.model.MainPageProductSection;
import com.smartbuilders.smartsales.ecommerce.model.MainPageTitleSection;
import com.smartbuilders.smartsales.ecommerce.model.Product;
import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalSection;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

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
            BusinessPartner businessPartner = (new BusinessPartnerDB(mContext, mUser))
                    .getActiveBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
            if (businessPartner!=null && businessPartner.getCommercialName()!=null) {
                mainPageList.add(businessPartner.getCommercialName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            BannerSection bannerSection = (new BannerSectionDB(mContext, mUser)).getBannerSection();
            if (bannerSection!=null && bannerSection.getBanners()!=null
                    && !bannerSection.getBanners().isEmpty()) {
                mainPageList.add(bannerSection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ProductBrandPromotionalSection productBrandPromotionalSection =
                    (new ProductBrandPromotionSectionDB(mContext, mUser)).getProductBrandPromotionSection();
            if (productBrandPromotionalSection !=null
                    && productBrandPromotionalSection.getProductBrandPromotionalCards()!=null
                    && !productBrandPromotionalSection.getProductBrandPromotionalCards().isEmpty()) {
                mainPageList.add(productBrandPromotionalSection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<Product> products = (new ProductRecentlySeenDB(mContext, mUser))
                    .getProductsRecentlySeenByBusinessPartnerId(Utils.getAppCurrentBusinessPartnerId(mContext, mUser));
            if (products!=null && products.size()>12) {
                MainPageProductSection mainPageProductSection = new MainPageProductSection();
                mainPageProductSection.setName(mContext.getString(R.string.products_recently_seen));
                mainPageProductSection.setProducts(products);
                mainPageList.add(mainPageProductSection);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            ArrayList<MainPageProductSection> mainPageProductSections =
                    (new MainPageProductSectionDB(mContext, mUser)).getActiveMainPageProductSections();
            if (mainPageProductSections!=null && !mainPageProductSections.isEmpty()) {
                for (MainPageProductSection mainPageProductSection : mainPageProductSections) {
                    if(mainPageProductSection!=null && mainPageProductSection.getProducts()!=null
                            && !mainPageProductSection.getProducts().isEmpty()) {
                        mainPageList.add(new MainPageTitleSection(mainPageProductSection.getName()));
                        mainPageList.addAll(mainPageProductSection.getProducts());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mainPageList;
    }
}

package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.model.BannerSection;

/**
 * Created by stein on 1/6/2016.
 */
public class BannerSectionDB {

    private Context mContext;
    private User mUser;

    public BannerSectionDB(Context context, User user){
        this.mContext = context;
        this.mUser = user;
    }

    public BannerSection getBannerSection() {
        BannerSection bannerSection = new BannerSection();
        bannerSection.setBanners((new BannerDB(mContext, mUser)).getActiveBanners());
        return bannerSection;
    }
}

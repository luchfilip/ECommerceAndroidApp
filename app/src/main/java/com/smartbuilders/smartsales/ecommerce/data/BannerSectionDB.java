package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerce.model.BannerSection;

/**
 * Created by stein on 1/6/2016.
 */
public class BannerSectionDB {

    private Context mContext;

    public BannerSectionDB(Context context){
        this.mContext = context;
    }

    public BannerSection getBannerSection() {
        BannerSection bannerSection = new BannerSection();
        bannerSection.setBanners((new BannerDB(mContext)).getActiveBanners());
        return bannerSection;
    }
}

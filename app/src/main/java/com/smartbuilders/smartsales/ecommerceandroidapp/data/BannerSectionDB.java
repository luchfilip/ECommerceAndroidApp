package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import android.content.Context;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;

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

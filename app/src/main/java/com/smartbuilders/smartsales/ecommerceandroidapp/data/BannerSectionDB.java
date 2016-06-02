package com.smartbuilders.smartsales.ecommerceandroidapp.data;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Banner;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.BannerSection;

import java.util.ArrayList;

/**
 * Created by stein on 1/6/2016.
 */
public class BannerSectionDB {

    public static BannerSection getBannerSection() {
        BannerSection bannerSection = new BannerSection();

        ArrayList<Banner> banners = new ArrayList<>();
        Banner banner = new Banner();
        banner.setImageResId(R.drawable.banner);
        banners.add(banner);

        banner = new Banner();
        banner.setImageResId(R.drawable.banner0);
        banners.add(banner);

        banner = new Banner();
        banner.setImageResId(R.drawable.banner1);
        banners.add(banner);

        banner = new Banner();
        banner.setImageResId(R.drawable.banner2);
        banners.add(banner);

        banner = new Banner();
        banner.setImageResId(R.drawable.banner3);
        banners.add(banner);

        banner = new Banner();
        banner.setImageResId(R.drawable.banner4);
        banners.add(banner);

        bannerSection.setBanners(banners);

        return bannerSection;
    }
}

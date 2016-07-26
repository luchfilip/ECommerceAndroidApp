package com.smartbuilders.smartsales.ecommerce.data;

import android.content.Context;
import android.database.Cursor;

import com.jasgcorp.ids.providers.DataBaseContentProvider;
import com.smartbuilders.smartsales.ecommerce.model.Banner;

import java.util.ArrayList;

/**
 * Created by stein on 9/6/2016.
 */
public class BannerDB {

    private Context mContext;

    public BannerDB(Context context){
        this.mContext = context;
    }

    public ArrayList<Banner> getActiveBanners () {
        ArrayList<Banner> activeBanners = new ArrayList<>();
        Cursor c = null;
        try {
            c = mContext.getContentResolver().query(DataBaseContentProvider.INTERNAL_DB_URI, null,
                    "SELECT BANNER_ID, IMAGE_FILE_NAME, PRODUCT_ID, BRAND_ID, SUBCATEGORY_ID, CATEGORY_ID " +
                    " from BANNER where IS_ACTIVE = ?", new String[]{"Y"}, null);
            if(c!=null){
                while(c.moveToNext()){
                    Banner banner = new Banner();
                    banner.setId(c.getInt(0));
                    banner.setImageFileName(c.getString(1));
                    banner.setProductId(c.getInt(2));
                    banner.setProductBrandId(c.getInt(3));
                    banner.setProductSubCategoryId(c.getInt(4));
                    banner.setProductCategoryId(c.getInt(5));
                    activeBanners.add(banner);
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
        return activeBanners;
    }

}

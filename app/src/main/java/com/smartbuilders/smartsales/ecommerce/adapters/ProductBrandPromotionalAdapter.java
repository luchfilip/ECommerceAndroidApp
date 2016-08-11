package com.smartbuilders.smartsales.ecommerce.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.DisplayMetrics;

import com.smartbuilders.smartsales.ecommerce.model.ProductBrandPromotionalCard;
import com.smartbuilders.smartsales.ecommerce.view.ProductBrandPromotionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionalAdapter extends FragmentStatePagerAdapter {

    private List<ProductBrandPromotionalCard> mProductBrandPromotionalCards;
    private DisplayMetrics mDisplayMetrics;

    public ProductBrandPromotionalAdapter(FragmentManager fm, DisplayMetrics displayMetrics){
        super(fm);
        this.mDisplayMetrics = displayMetrics;
    }

    public void setData(ArrayList<ProductBrandPromotionalCard> productBrandPromotionalCards){
        this.mProductBrandPromotionalCards = productBrandPromotionalCards;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return ProductBrandPromotionFragment.getInstance(mProductBrandPromotionalCards.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        try {
            return mProductBrandPromotionalCards.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public float getPageWidth (int position) {
        if(mDisplayMetrics.widthPixels < mDisplayMetrics.heightPixels){
            return 0.90f;
        } else {
            return 0.45f;
        }
    }
}
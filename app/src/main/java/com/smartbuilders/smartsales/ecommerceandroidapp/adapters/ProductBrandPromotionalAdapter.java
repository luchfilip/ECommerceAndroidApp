package com.smartbuilders.smartsales.ecommerceandroidapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.smartbuilders.smartsales.ecommerceandroidapp.model.ProductBrandPromotionalCard;
import com.smartbuilders.smartsales.ecommerceandroidapp.view.ProductBrandPromotionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stein on 2/6/2016.
 */
public class ProductBrandPromotionalAdapter extends FragmentStatePagerAdapter {

    private List<ProductBrandPromotionalCard> mProductBrandPromotinalCards;

    public ProductBrandPromotionalAdapter(FragmentManager fm){
        super(fm);
    }

    public void setData(ArrayList<ProductBrandPromotionalCard> productBrandPromotionalCards){
        this.mProductBrandPromotinalCards = productBrandPromotionalCards;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            return ProductBrandPromotionFragment.getInstance(mProductBrandPromotinalCards.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        try {
            return mProductBrandPromotinalCards.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public float getPageWidth (int position) {
        return 0.90f;
    }
}
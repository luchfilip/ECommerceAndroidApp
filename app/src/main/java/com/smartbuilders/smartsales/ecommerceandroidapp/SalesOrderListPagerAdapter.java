package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by stein on 11/6/2016.
 */
public class SalesOrderListPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public SalesOrderListPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                SalesOrdersListFragment tab1 = new SalesOrdersListFragment();
                return tab1;
            case 1:
                SalesOrdersListFragment tab2 = new SalesOrdersListFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
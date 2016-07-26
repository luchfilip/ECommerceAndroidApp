package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
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
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putBoolean(SalesOrdersListFragment.KEY_LOAD_ORDERS_FROM_SALES_ORDERS, false);
                SalesOrdersListFragment tab1 = new SalesOrdersListFragment();
                tab1.setArguments(args);
                return tab1;
            case 1:
                args.putBoolean(SalesOrdersListFragment.KEY_LOAD_ORDERS_FROM_SALES_ORDERS, true);
                SalesOrdersListFragment tab2 = new SalesOrdersListFragment();
                tab2.setArguments(args);
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
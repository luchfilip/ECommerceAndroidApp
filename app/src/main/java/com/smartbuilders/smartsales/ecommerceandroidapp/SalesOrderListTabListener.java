package com.smartbuilders.smartsales.ecommerceandroidapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;

/**
 * Created by stein on 11/6/2016.
 */
public class SalesOrderListTabListener implements ActionBar.TabListener {

    private Fragment fragment;

    public SalesOrderListTabListener(Fragment fragment){
        this.fragment = fragment;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.replace(R.id.fragment_sales_order_list, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        ft.remove(fragment);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }
}

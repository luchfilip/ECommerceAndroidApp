package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Jesus Sarco on 16/01/2017.
 */
public class ChatContactsListPagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public ChatContactsListPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putBoolean(ChatContactsListFragment.KEY_LOAD_RECENT_CONVERSATIONS, true);
                ChatContactsListFragment tab1 = new ChatContactsListFragment();
                tab1.setArguments(args);
                return tab1;
            case 1:
                ChatContactsListFragment tab2 = new ChatContactsListFragment();
                tab2.setArguments(args);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
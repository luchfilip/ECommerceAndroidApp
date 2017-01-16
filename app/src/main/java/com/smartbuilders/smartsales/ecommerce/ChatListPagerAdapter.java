package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by stein on 11/6/2016.
 */
public class ChatListPagerAdapter extends FragmentStatePagerAdapter {
    private int numOfTabs;

    public ChatListPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putBoolean(ChatListFragment.KEY_LOAD_RECENT_CONVERSATIONS, true);
                ChatListFragment tab1 = new ChatListFragment();
                tab1.setArguments(args);
                return tab1;
            case 1:
                args.putBoolean(ChatListFragment.KEY_LOAD_ALL_CONTACTS, true);
                ChatListFragment tab2 = new ChatListFragment();
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
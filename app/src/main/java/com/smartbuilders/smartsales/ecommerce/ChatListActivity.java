package com.smartbuilders.smartsales.ecommerce;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.ViewPager;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 *
 */
public class ChatListActivity extends AppCompatActivity {

    public static final String STATE_CURRENT_TAB_SELECTED = "STATE_CURRENT_TAB_SELECTED";
    public static final String KEY_CURRENT_TAB_SELECTED = "KEY_CURRENT_TAB_SELECTED";

    private TabLayout mTabLayout;
    private int mCurrentTabSelected;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, false);
        setSupportActionBar(toolbar);

        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(STATE_CURRENT_TAB_SELECTED)){
                mCurrentTabSelected = savedInstanceState.getInt(STATE_CURRENT_TAB_SELECTED);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_TAB_SELECTED)){
                mCurrentTabSelected = getIntent().getExtras().getInt(KEY_CURRENT_TAB_SELECTED);
            }
        }

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.chat_list_tab0_name));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.chat_list_tab1_name));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ChatListPagerAdapter adapter = new ChatListPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentTabSelected = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
                //manageMenu();
                switch (tab.getPosition()) {
                    case 0:
                        if (fragments!=null && fragments.size()>0 && fragments.get(0)!=null
                                && fragments.get(0).getView()!=null) {
                            //showOrHideEmptyLayoutWallpaper(((ListView) fragments.get(0).getView()
                            //        .findViewById(R.id.sales_orders_list)), false);
                        }
                        break;
                    case 1:
                        if (fragments!=null && fragments.size()>1 && fragments.get(1)!=null
                                && fragments.get(1).getView()!=null) {
                            //showOrHideEmptyLayoutWallpaper(((ListView) fragments.get(1).getView()
                            //        .findViewById(R.id.sales_orders_list)), true);
                        }
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing
            }
        });
    }

}

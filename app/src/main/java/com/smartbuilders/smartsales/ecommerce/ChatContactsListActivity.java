package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.ViewPager;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 *
 */
public class ChatContactsListActivity extends AppCompatActivity implements ChatContactsListFragment.Callback {

    public static final String STATE_CURRENT_TAB_SELECTED = "STATE_CURRENT_TAB_SELECTED";
    public static final String KEY_CURRENT_TAB_SELECTED = "KEY_CURRENT_TAB_SELECTED";
    private static final String CHAT_DETAIL_FRAGMENT_TAG = "CHAT_DETAIL_FRAGMENT_TAG";

    private TabLayout mTabLayout;
    private int mCurrentTabSelected;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private boolean mThreePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_contacts_list);

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

        mThreePane = findViewById(R.id.chat_detail_container) != null
                && findViewById(R.id.chat_recent_detail_container) != null;

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.chat_list_tab0_name));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.chat_list_tab1_name));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ChatContactsListPagerAdapter adapter = new ChatContactsListPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
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
                            showOrHideEmptyLayoutWallpaper(((ListView) fragments.get(0).getView()
                                    .findViewById(R.id.chat_contacts_list)), true);
                        }
                        break;
                    case 1:
                        if (fragments!=null && fragments.size()>1 && fragments.get(1)!=null
                                && fragments.get(1).getView()!=null) {
                            showOrHideEmptyLayoutWallpaper(((ListView) fragments.get(1).getView()
                                    .findViewById(R.id.chat_contacts_list)), false);
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
        viewPager.setAllowSwap(true);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        fragments.add(fragment);
    }

    private void showOrHideEmptyLayoutWallpaper(ListView listView, boolean isRecentChats) {
        int index = isRecentChats ? 1 : 0;
        if (listView != null && listView.getAdapter()!=null && !listView.getAdapter().isEmpty()) {
            if (fragments.get(index)!=null && fragments.get(index).getView() != null) {
                if (fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper) != null) {
                    fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper).setVisibility(View.GONE);
                }
                if (fragments.get(index).getView().findViewById(R.id.chat_contacts_list) != null) {
                    fragments.get(index).getView().findViewById(R.id.chat_contacts_list).setVisibility(View.VISIBLE);
                }
            }

            /****************/
            if (isRecentChats) {
                if (findViewById(R.id.chat_recent_detail_container) != null) {
                    findViewById(R.id.chat_recent_detail_container).setVisibility(View.VISIBLE);
                }
                if (findViewById(R.id.chat_detail_container) != null) {
                    findViewById(R.id.chat_detail_container).setVisibility(View.GONE);
                }
            } else {
                if (findViewById(R.id.chat_detail_container) != null) {
                    findViewById(R.id.chat_detail_container).setVisibility(View.VISIBLE);
                }
                if (findViewById(R.id.chat_recent_detail_container) != null) {
                    findViewById(R.id.chat_recent_detail_container).setVisibility(View.GONE);
                }
            }
        }else{
            if (fragments.get(index) != null && fragments.get(index).getView() != null) {
                if (fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper) != null) {
                    fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper).setVisibility(View.VISIBLE);
                }
                if (fragments.get(index).getView().findViewById(R.id.chat_contacts_list) != null) {
                    fragments.get(index).getView().findViewById(R.id.chat_contacts_list).setVisibility(View.GONE);
                }
            }

            /****************/
            if (findViewById(R.id.chat_recent_detail_container) != null) {
                findViewById(R.id.chat_recent_detail_container).setVisibility(View.GONE);
            }
            if (findViewById(R.id.chat_detail_container) != null) {
                findViewById(R.id.chat_detail_container).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onItemSelected(ChatContact chatContact, boolean isRecentConversation) {
        if(mThreePane){
            Bundle args = new Bundle();
            args.putInt(ChatDetailsActivity.KEY_CHAT_CONTACT_ID, chatContact.getId());

            ChatDetailsFragment fragment = new ChatDetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(isRecentConversation ? R.id.chat_recent_detail_container : R.id.chat_detail_container,
                            fragment, CHAT_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, ChatDetailsActivity.class);
            intent.putExtra(ChatDetailsActivity.KEY_CHAT_CONTACT_ID, chatContact.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(ChatContact chatContact, ListView listView, User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_chat_question, chatContact.getName()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //String result = OrderBR.deactivateOrderById(SalesOrdersListActivity.this,
                        //        user, order.getId());
                        //if (result==null) {
                        //    reloadOrdersList(listView, user);
                        //} else {
                        //    Toast.makeText(SalesOrdersListActivity.this, result, Toast.LENGTH_SHORT).show();
                        //}
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onListIsLoaded(ListView listView, boolean isRecentConversation) {
        if (mThreePane) {
            if (listView != null && listView.getAdapter() != null && !listView.getAdapter().isEmpty()) {
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        if ((mCurrentTabSelected==0 && !isRecentConversation)
                || (mCurrentTabSelected==1 && isRecentConversation)) {
            showOrHideEmptyLayoutWallpaper(listView, isRecentConversation);
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex, ListView listView) {
        if (mThreePane) {
            if (listView!=null && listView.getAdapter()!=null && listView.getAdapter().getCount()>selectedIndex) {
                listView.setSelection(selectedIndex);
                listView.setItemChecked(selectedIndex, true);
            }
        }
    }
}

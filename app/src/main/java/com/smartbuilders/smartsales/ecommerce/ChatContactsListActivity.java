package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.adapters.ChatContactsListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.ChatContactDB;
import com.smartbuilders.smartsales.ecommerce.data.ChatMessageDB;
import com.smartbuilders.smartsales.ecommerce.model.ChatContact;
import com.smartbuilders.smartsales.ecommerce.view.ViewPager;
import com.smartbuilders.synchronizer.ids.model.User;

import java.util.ArrayList;

/**
 *
 */
public class ChatContactsListActivity extends AppCompatActivity
        implements ChatContactsListFragment.Callback, ChatMessagesFragment.Callback{

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

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setLogo(null);
            actionBar.setTitle(R.string.chat);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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
                manageMenu();
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
        int index = isRecentChats ? 0 : 1;
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
                    if (index == 1) {
                        if (fragments.get(index).getView().findViewById(R.id.empty_chat_list_textView) != null) {
                            ((TextView) fragments.get(index).getView().findViewById(R.id.empty_chat_list_textView))
                                    .setText(R.string.no_contacts_availabe);
                        }
                        if (fragments.get(index).getView().findViewById(R.id.empty_sales_order_list_imageView) != null) {
                            ((ImageView) fragments.get(index).getView().findViewById(R.id.empty_sales_order_list_imageView))
                                    .setImageResource(R.drawable.ic_supervisor_account_black_48dp);
                        }
                    }
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
            args.putInt(ChatMessagesActivity.KEY_CHAT_CONTACT_ID, chatContact.getId());

            ChatMessagesFragment fragment = new ChatMessagesFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(isRecentConversation ? R.id.chat_recent_detail_container : R.id.chat_detail_container,
                            fragment, CHAT_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, ChatMessagesActivity.class);
            intent.putExtra(ChatMessagesActivity.KEY_CHAT_CONTACT_ID, chatContact.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(final ChatContact chatContact, final ListView listView, final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_chat_question, chatContact.getName()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new ChatMessageDB(ChatContactsListActivity.this, user))
                                .deactiveConversationByContactId(chatContact.getId());
                        if (result==null) {
                            reloadChatContactsList(listView, user, true);
                        } else {
                            Toast.makeText(ChatContactsListActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void reloadChatContactsList(ListView listView, User user, boolean isRecentConversation) {
        if (listView!=null && listView.getAdapter()!=null) {
            int oldListSize = listView.getCount();
            int selectedIndex = listView.getCheckedItemPosition();
            ((ChatContactsListAdapter) listView.getAdapter())
                    .setData(new ChatContactDB(this, user).getContactsWithRecentConversations());

            if (mThreePane) {
                if (listView.getCount() < oldListSize && !listView.getAdapter().isEmpty()) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                } else if (listView.getCount() > selectedIndex) {
                    listView.setSelection(selectedIndex);
                    listView.setItemChecked(selectedIndex, true);
                }
            }
        }
        showOrHideEmptyLayoutWallpaper(listView, isRecentConversation);
    }

    @Override
    public void onListIsLoaded(ListView listView, boolean isRecentConversation) {
        if (mThreePane) {
            if (listView != null && listView.getAdapter() != null && !listView.getAdapter().isEmpty()) {
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        if ((mCurrentTabSelected==0 && isRecentConversation)
                || (mCurrentTabSelected==1 && !isRecentConversation)) {
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

    @Override
    public void chatMessagesDetailLoaded() {
        manageMenu();
    }

    private void manageMenu(){
        if(mThreePane && getSupportFragmentManager()!=null){
            //if(mTabLayout.getSelectedTabPosition()==0) {
                if(getSupportFragmentManager().findFragmentByTag(CHAT_DETAIL_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().findFragmentByTag(CHAT_DETAIL_FRAGMENT_TAG)
                            .setMenuVisibility(true);
                }
                //if(getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)!=null){
                //    getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)
                //            .setMenuVisibility(false);
                //}
            //}else{
            //    if(getSupportFragmentManager().findFragmentByTag(CHAT_DETAIL_FRAGMENT_TAG)!=null){
            //        getSupportFragmentManager().findFragmentByTag(CHAT_DETAIL_FRAGMENT_TAG)
            //                .setMenuVisibility(false);
            //    }
                //if(getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)!=null){
                //    getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)
                //            .setMenuVisibility(true);
                //}
            //}
        }
    }

    @Override
    public boolean isFragmentMenuVisible() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

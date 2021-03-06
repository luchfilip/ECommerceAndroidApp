package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartbuilders.smartsales.ecommerce.adapters.OrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.businessRules.OrderBR;
import com.smartbuilders.smartsales.ecommerce.businessRules.SalesOrderSalesManBR;
import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.OrderDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.adapters.SalesOrdersListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.smartsales.ecommerce.view.ViewPager;
import com.smartbuilders.synchronizer.ids.model.UserProfile;

import java.util.ArrayList;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SalesOrdersListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SalesOrdersListFragment.Callback,
        SalesOrderDetailFragment.Callback, OrderDetailFragment.Callback {

    private static final String SALES_ORDER_DETAIL_FRAGMENT_TAG = "SALES_ORDER_DETAIL_FRAGMENT_TAG";
    public static final String STATE_CURRENT_TAB_SELECTED = "STATE_CURRENT_TAB_SELECTED";
    public static final String KEY_CURRENT_TAB_SELECTED = "KEY_CURRENT_TAB_SELECTED";

    private boolean mThreePane;
    private TabLayout mTabLayout;
    private int mCurrentTabSelected;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_orders_list);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, mUser);

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

        mThreePane = findViewById(R.id.sales_order_detail_container) != null
                && findViewById(R.id.order_detail_container) != null;

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sales_orders_list_tab0_name));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sales_orders_list_tab1_name));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final SalesOrderListPagerAdapter adapter = new SalesOrderListPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount());
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
                                    .findViewById(R.id.sales_orders_list)), false);
                        }
                        break;
                    case 1:
                        if (fragments!=null && fragments.size()>1 && fragments.get(1)!=null
                                && fragments.get(1).getView()!=null) {
                            showOrHideEmptyLayoutWallpaper(((ListView) fragments.get(1).getView()
                                    .findViewById(R.id.sales_orders_list)), true);
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
        viewPager.setAllowSwap(!mThreePane);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        fragments.add(fragment);
    }

    @Override
    protected void onStart() {
        if(mTabLayout!=null && mTabLayout.getTabAt(mCurrentTabSelected)!=null){
            mTabLayout.getTabAt(mCurrentTabSelected).select();
        }
        if(findViewById(R.id.sales_order_detail_container)==null
                && (mUser!=null && (BuildConfig.IS_SALES_FORCE_SYSTEM
                || mUser.getUserProfileId()==UserProfile.SALES_MAN_PROFILE_ID))){
            try {
                BusinessPartner businessPartner = (new BusinessPartnerDB(this, mUser))
                        .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
                if(businessPartner!=null){
                    ((TextView) findViewById(R.id.business_partner_name)).setText(businessPartner.getName());
                    findViewById(R.id.business_partner_name_container).setVisibility(View.VISIBLE);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        Utils.manageNotificationOnDrawerLayout(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            Utils.loadNavigationViewBadge(getApplicationContext(), Utils.getCurrentUser(this),
                    (NavigationView) findViewById(R.id.nav_view));
        }
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sales_orders_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.search) {
            startActivity(new Intent(this, SearchResultsActivity.class));
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onItemSelected(SalesOrder salesOrder) {
        if(mThreePane){
            Bundle args = new Bundle();
            args.putInt(SalesOrderDetailActivity.KEY_SALES_ORDER_ID, salesOrder.getId());

            SalesOrderDetailFragment fragment = new SalesOrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sales_order_detail_container, fragment, SALES_ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, SalesOrderDetailActivity.class);
            intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER_ID, salesOrder.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(final SalesOrder salesOrder, final ListView listView, final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_sales_order_question, salesOrder.getSalesOrderNumber(),
                        salesOrder.getBusinessPartner().getName()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = SalesOrderSalesManBR.deactivateSalesOrderById(SalesOrdersListActivity.this,
                                user, salesOrder.getId());
                        if (result==null) {
                            reloadSalesOrdersList(listView, user);
                        } else {
                            Toast.makeText(SalesOrdersListActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onItemSelected(Order order) {
        if(mThreePane){
            Bundle args = new Bundle();
            args.putInt(OrderDetailActivity.KEY_ORDER_ID, order.getId());

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment, OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, order.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(final Order order, final ListView listView, final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_order_question, order.getOrderNumber()))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = OrderBR.deactivateOrderById(SalesOrdersListActivity.this,
                                user, order.getId());
                        if (result==null) {
                            reloadOrdersList(listView, user);
                        } else {
                            Toast.makeText(SalesOrdersListActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void reloadOrdersList(ListView listView, User user){
        if (listView!=null && listView.getAdapter()!=null) {
            int oldListSize = listView.getCount();
            int selectedIndex = listView.getCheckedItemPosition();
            ((OrdersListAdapter) listView.getAdapter())
                    .setData((new OrderDB(this, user)).getActiveOrdersFromSalesOrders());

            if (mThreePane) {
                if (listView.getCount() < oldListSize && !listView.getAdapter().isEmpty()) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                } else if (listView.getCount() > selectedIndex) {
                    listView.setSelection(selectedIndex);
                    listView.setItemChecked(selectedIndex, true);
                }
            }
        }
        showOrHideEmptyLayoutWallpaper(listView, mCurrentTabSelected==1);
    }

    @Override
    public void onListIsLoaded(ListView listView, boolean isOrdersFromSalesOrder) {
        if (mThreePane) {
            if (listView != null && listView.getAdapter() != null && !listView.getAdapter().isEmpty()) {
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        if ((mCurrentTabSelected==0 && !isOrdersFromSalesOrder)
                || (mCurrentTabSelected==1 && isOrdersFromSalesOrder)) {
            showOrHideEmptyLayoutWallpaper(listView, isOrdersFromSalesOrder);
        }
    }

    private void showOrHideEmptyLayoutWallpaper(ListView listView, boolean isOrdersFromSalesOrder) {
        int index = isOrdersFromSalesOrder ? 1 : 0;
        if (listView != null && listView.getAdapter()!=null && !listView.getAdapter().isEmpty()) {
            if (fragments.get(index)!=null && fragments.get(index).getView() != null) {
                if (fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper) != null) {
                    fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper).setVisibility(View.GONE);
                }
                if (fragments.get(index).getView().findViewById(R.id.main_layout) != null) {
                    fragments.get(index).getView().findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                }
            }

            /****************/
            if (isOrdersFromSalesOrder) {//Order
                if (findViewById(R.id.order_detail_container) != null) {
                    findViewById(R.id.order_detail_container).setVisibility(View.VISIBLE);
                }
                if (findViewById(R.id.sales_order_detail_container) != null) {
                    findViewById(R.id.sales_order_detail_container).setVisibility(View.GONE);
                }
            } else {//SalesOrder
                if (findViewById(R.id.sales_order_detail_container) != null) {
                    findViewById(R.id.sales_order_detail_container).setVisibility(View.VISIBLE);
                }
                if (findViewById(R.id.order_detail_container) != null) {
                    findViewById(R.id.order_detail_container).setVisibility(View.GONE);
                }
            }
        }else{
            if (fragments.get(index) != null && fragments.get(index).getView() != null) {
                if (fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper) != null) {
                    fragments.get(index).getView().findViewById(R.id.empty_layout_wallpaper).setVisibility(View.VISIBLE);
                }
                if (fragments.get(index).getView().findViewById(R.id.main_layout) != null) {
                    fragments.get(index).getView().findViewById(R.id.main_layout).setVisibility(View.GONE);
                }
            }

            /****************/
            if (findViewById(R.id.order_detail_container) != null) {
                findViewById(R.id.order_detail_container).setVisibility(View.GONE);
            }
            if (findViewById(R.id.sales_order_detail_container) != null) {
                findViewById(R.id.sales_order_detail_container).setVisibility(View.GONE);
            }
        }
    }

    private void reloadSalesOrdersList(ListView listView, User user){
        if (listView!=null && listView.getAdapter()!=null) {
            int oldListSize = listView.getCount();
            int selectedIndex = listView.getCheckedItemPosition();
            ((SalesOrdersListAdapter) listView.getAdapter())
                    .setData(new SalesOrderDB(this, user).getSalesOrderList());

            if (mThreePane) {
                if (listView.getCount() < oldListSize && !listView.getAdapter().isEmpty()) {
                    listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
                } else if (listView.getCount() > selectedIndex) {
                    listView.setSelection(selectedIndex);
                    listView.setItemChecked(selectedIndex, true);
                }
            }
        }
        showOrHideEmptyLayoutWallpaper(listView, mCurrentTabSelected==1);
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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_TAB_SELECTED, mCurrentTabSelected);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean isFragmentMenuVisible() {
        return false;
    }

    @Override
    public void salesOrderDetailLoaded() {
        manageMenu();
    }

    @Override
    public void orderDetailLoaded() {
        manageMenu();
    }

    private void manageMenu(){
        if(mThreePane && getSupportFragmentManager()!=null){
            if(mTabLayout.getSelectedTabPosition()==0) {
                if(getSupportFragmentManager().findFragmentByTag(SALES_ORDER_DETAIL_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().findFragmentByTag(SALES_ORDER_DETAIL_FRAGMENT_TAG)
                            .setMenuVisibility(true);
                }
                if(getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)
                            .setMenuVisibility(false);
                }
            }else{
                if(getSupportFragmentManager().findFragmentByTag(SALES_ORDER_DETAIL_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().findFragmentByTag(SALES_ORDER_DETAIL_FRAGMENT_TAG)
                            .setMenuVisibility(false);
                }
                if(getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)!=null){
                    getSupportFragmentManager().findFragmentByTag(OrdersListActivity.ORDER_DETAIL_FRAGMENT_TAG)
                            .setMenuVisibility(true);
                }
            }
        }
    }
}
package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SalesOrdersListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.view.ViewPager;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SalesOrdersListActivity extends AppCompatActivity
        implements SalesOrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener,
        SalesOrderDetailFragment.Callback, OrderDetailFragment.Callback {

    private static final String SALES_ORDER_DETAIL_FRAGMENT_TAG = "SALES_ORDER_DETAIL_FRAGMENT_TAG";
    public static final String STATE_CURRENT_TAB_SELECTED = "STATE_CURRENT_TAB_SELECTED";
    public static final String KEY_CURRENT_TAB_SELECTED = "KEY_CURRENT_TAB_SELECTED";

    private boolean mThreePane;
    private User mCurrentUser;
    private TabLayout mTabLayout;
    private int mCurrentTabSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_orders_list);

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

        mCurrentUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));

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
                if(mThreePane) {
                    manageMenu();
                    switch (tab.getPosition()) {
                        case 0:
                                findViewById(R.id.sales_order_detail_container).setVisibility(View.VISIBLE);
                                findViewById(R.id.order_detail_container).setVisibility(View.GONE);
                            break;
                        case 1:
                                findViewById(R.id.order_detail_container).setVisibility(View.VISIBLE);
                                findViewById(R.id.sales_order_detail_container).setVisibility(View.GONE);
                            break;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAllowSwap(!mThreePane);
    }

    @Override
    protected void onStart() {
        if(mTabLayout!=null && mTabLayout.getTabAt(mCurrentTabSelected)!=null){
            mTabLayout.getTabAt(mCurrentTabSelected).select();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sales_orders_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
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
        drawer.closeDrawer(GravityCompat.START);
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
    public void onItemLongSelected(final SalesOrder salesOrder, final ListView listView) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_sales_order_question, salesOrder.getSalesOrderNumber(),
                        salesOrder.getBusinessPartner().getCommercialName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new SalesOrderDB(SalesOrdersListActivity.this, mCurrentUser))
                                .deactiveSalesOrderById(salesOrder.getId());
                        if (result==null) {
                            reloadSalesOrdersList(listView);
                        } else {
                            Toast.makeText(SalesOrdersListActivity.this, result, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
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
    public void onListIsLoaded(ListView listView) {
        if (mThreePane) {
            if (listView != null && listView.getAdapter()!=null && listView.getAdapter().getCount()>0) {
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    private void reloadSalesOrdersList(ListView listView){
        if (listView!=null && listView.getAdapter()!=null) {
            int oldListSize = listView.getCount();
            int selectedIndex = listView.getCheckedItemPosition();
            ((SalesOrdersListAdapter) listView.getAdapter())
                    .setData(new SalesOrderDB(this, mCurrentUser).getActiveSalesOrders());

            if (listView.getCount()<oldListSize && listView.getCount()>0 && mThreePane) {
                listView.performItemClick(listView.getAdapter().getView(0, null, null), 0, 0);
            } else if (listView.getCount()>selectedIndex && mThreePane) {
                listView.setSelection(selectedIndex);
                listView.setItemChecked(selectedIndex, true);
            } else if (mThreePane) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.sales_order_detail_container, new SalesOrderDetailFragment(), SALES_ORDER_DETAIL_FRAGMENT_TAG)
                        .commit();
            }
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
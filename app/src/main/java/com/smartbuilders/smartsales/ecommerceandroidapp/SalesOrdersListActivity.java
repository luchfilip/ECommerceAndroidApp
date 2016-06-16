package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SalesOrdersListActivity extends AppCompatActivity
        implements SalesOrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private static final String SALES_ORDER_DETAIL_FRAGMENT_TAG = "SALES_ORDER_DETAIL_FRAGMENT_TAG";
    private static final String STATE_CURRENT_SELECTED_ITEM_POSITION = "STATE_CURRENT_SELECTED_ITEM_POSITION";

    private boolean mTwoPane;
    private User mCurrentUser;
    private int mCurrentSelectedItemPosition;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_orders_list);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_ITEM_POSITION)){
                mCurrentSelectedItemPosition = savedInstanceState.getInt(STATE_CURRENT_SELECTED_ITEM_POSITION);
            }
        }

        mCurrentUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));

        mTwoPane = findViewById(R.id.sales_order_detail_container) != null;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Cotizaciones"));
        tabLayout.addTab(tabLayout.newTab().setText("Cotizaciones hechas pedidos"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final SalesOrderListPagerAdapter adapter = new SalesOrderListPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setAllowSwap(!mTwoPane);

        mListView = (ListView) findViewById(R.id.sales_orders_list);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mTwoPane) {
            if (mListView != null && mListView.getAdapter()!=null
                    && mListView.getAdapter().getCount() > mCurrentSelectedItemPosition
                    && mCurrentSelectedItemPosition != ListView.INVALID_POSITION) {
                mListView.performItemClick(mListView.getAdapter().getView(mCurrentSelectedItemPosition, null, null),
                        mCurrentSelectedItemPosition, mCurrentSelectedItemPosition);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView lv = (ListView) findViewById(R.id.sales_orders_list);
        if(lv != null && (lv.getAdapter()==null || lv.getAdapter().getCount()==0)){
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            if(mTwoPane){
                findViewById(R.id.fragment_sales_order_list).setVisibility(View.GONE);
                findViewById(R.id.sales_order_detail_container).setVisibility(View.GONE);
            }else{
                findViewById(R.id.sales_orders_list).setVisibility(View.GONE);
            }
        }
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
    public void onItemSelected(SalesOrder salesOrder, int selectedItemPosition) {
        mCurrentSelectedItemPosition = selectedItemPosition;
        if(mTwoPane){
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
    public void onItemLongSelected(final SalesOrder salesOrder) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_sales_order_question, salesOrder.getSalesOrderNumber(),
                        salesOrder.getBusinessPartner().getCommercialName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new SalesOrderDB(SalesOrdersListActivity.this, mCurrentUser))
                                .deactiveSalesOrderById(salesOrder.getId());
                        if (result==null) {
                            if (mListView != null) {
                                ArrayList<SalesOrder> activeSalesOrders = (new SalesOrderDB(SalesOrdersListActivity.this, mCurrentUser))
                                        .getActiveSalesOrders();
                                if (mListView.getAdapter()!=null) {
                                    ((SalesOrdersListAdapter) mListView.getAdapter()).setData(activeSalesOrders);
                                } else {
                                    mListView.setAdapter(new SalesOrdersListAdapter(SalesOrdersListActivity.this, activeSalesOrders));
                                }
                                if (mTwoPane) {
                                    if(mListView.getAdapter().getCount()>0) {
                                        mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
                                    } else {

                                    }
                                }
                            }
                        } else {
                            Toast.makeText(SalesOrdersListActivity.this, result, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onItemSelected(Order order, int selectedItemPosition) {
        mCurrentSelectedItemPosition = selectedItemPosition;
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(OrderDetailActivity.KEY_ORDER_ID, order.getId());

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sales_order_detail_container, fragment, OrdersListActivity.ORDERDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, order.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTED_ITEM_POSITION, mCurrentSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }
}
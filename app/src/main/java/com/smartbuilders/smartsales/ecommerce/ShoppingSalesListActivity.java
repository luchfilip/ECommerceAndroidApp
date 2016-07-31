package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import com.jasgcorp.ids.model.UserProfile;
import com.smartbuilders.smartsales.ecommerce.adapters.ShoppingSalesListAdapter;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerce.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerce.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco, before 15.06.2016
 */
public class ShoppingSalesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShoppingSalesListFragment.Callback,
        ShoppingSaleFragment.Callback {

    public static final String SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG =
            "SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG";

    private boolean mTwoPane;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_sales_list);

        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                Utils.loadNavigationViewBadge(getApplicationContext(), user,
                        (NavigationView) findViewById(R.id.nav_view));
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && user!=null){
            if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, user.getUserName()));
        }

        mTwoPane = findViewById(R.id.shopping_sale_order_detail_container)!=null;
        mListView = (ListView) findViewById(R.id.shopping_sales_orders_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_sales_list, menu);
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
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartner().getId());

            ShoppingSaleFragment fragment = new ShoppingSaleFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shopping_sale_order_detail_container, fragment, SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            startActivity(new Intent(this, ShoppingSaleActivity.class)
                    .putExtra(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartner().getId()));
        }
    }

    @Override
    public void onItemLongSelected(final SalesOrder salesOrder, final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_shopping_sales_question,
                        salesOrder.getBusinessPartner().getCommercialName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new SalesOrderLineDB(ShoppingSalesListActivity.this, user))
                                .deactiveLinesFromShoppingSaleByBusinessPartnerId(salesOrder.getBusinessPartnerId());
                        if (result==null) {
                            reloadShoppingSalesList(user);
                        } else {
                            Toast.makeText(ShoppingSalesListActivity.this, result, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onListIsLoaded() {
        if (mListView != null && mListView.getAdapter()!=null && mListView.getAdapter().getCount()>0) {
            mListView.setVisibility(View.VISIBLE);
            if (mTwoPane) {
                findViewById(R.id.fragment_sales_order_list).setVisibility(View.VISIBLE);
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.VISIBLE);
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }else{
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
            }
            findViewById(R.id.company_logo_name).setVisibility(View.GONE);
        } else {
            if (mTwoPane) {
                findViewById(R.id.fragment_sales_order_list).setVisibility(View.GONE);
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.GONE);
            } else {
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
                findViewById(R.id.main_layout).setVisibility(View.GONE);
            }
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (mListView!=null && mListView.getAdapter()!=null
                && mListView.getAdapter().getCount()>selectedIndex) {
            if (mTwoPane) {
                mListView.setVisibility(View.VISIBLE);
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
                findViewById(R.id.fragment_sales_order_list).setVisibility(View.VISIBLE);
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.VISIBLE);
            }else{
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
            }
            findViewById(R.id.company_logo_name).setVisibility(View.GONE);
        } else {
            if (mTwoPane) {
                findViewById(R.id.fragment_sales_order_list).setVisibility(View.GONE);
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.GONE);
            } else {
                findViewById(R.id.progressContainer).setVisibility(View.GONE);
                findViewById(R.id.main_layout).setVisibility(View.GONE);
            }
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void reloadShoppingSalesList(User user) {
        if (mListView!=null && mListView.getAdapter()!=null) {
            int oldListSize = mListView.getCount();
            int selectedIndex = mListView.getCheckedItemPosition();
            ((ShoppingSalesListAdapter) mListView.getAdapter())
                    .setData(new SalesOrderDB(this, user).getActiveShoppingSalesOrders());

            if (mListView.getCount()<oldListSize && mListView.getCount()>0 && mTwoPane) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
                mListView.setVisibility(View.VISIBLE);
                findViewById(R.id.company_logo_name).setVisibility(View.GONE);
            } else if (mListView.getCount()>selectedIndex && mTwoPane) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
                mListView.setVisibility(View.VISIBLE);
                findViewById(R.id.company_logo_name).setVisibility(View.GONE);
            } else if(mListView.getCount()==0) { //se bloquea la pantalla
                findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
                mListView.setVisibility(View.GONE);
            }
        }
    }

}

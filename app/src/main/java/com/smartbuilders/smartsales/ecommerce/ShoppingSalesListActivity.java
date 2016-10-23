package com.smartbuilders.smartsales.ecommerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.synchronizer.ids.model.UserProfile;
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
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view); //must call super
                Utils.loadNavigationViewBadge(getApplicationContext(), user,
                        (NavigationView) findViewById(R.id.nav_view));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null && user!=null){
            if(BuildConfig.IS_SALES_FORCE_SYSTEM){
                navigationView.inflateMenu(R.menu.sales_force_system_drawer_menu);
            }else if(user.getUserProfileId() == UserProfile.BUSINESS_PARTNER_PROFILE_ID){
                navigationView.inflateMenu(R.menu.business_partner_drawer_menu);
            }else if(user.getUserProfileId() == UserProfile.SALES_MAN_PROFILE_ID){
                navigationView.inflateMenu(R.menu.sales_man_drawer_menu);
            }
            navigationView.setNavigationItemSelectedListener(this);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, user.getUserName()));
        }

        mListView = (ListView) findViewById(R.id.shopping_sales_orders_list);
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
        getMenuInflater().inflate(R.menu.menu_shopping_sales_list, menu);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onItemSelected(SalesOrder salesOrder) {
        if(findViewById(R.id.shopping_sale_order_detail_container) != null){
            Bundle args = new Bundle();
            args.putInt(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartnerId());

            ShoppingSaleFragment fragment = new ShoppingSaleFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shopping_sale_order_detail_container, fragment, SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            startActivity(new Intent(this, ShoppingSaleActivity.class)
                    .putExtra(ShoppingSaleActivity.KEY_USER_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartnerId()));
        }
    }

    @Override
    public void onItemLongSelected(final SalesOrder salesOrder, final User user) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_shopping_sales_question,
                        salesOrder.getBusinessPartner().getName()))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new SalesOrderLineDB(ShoppingSalesListActivity.this, user))
                                .deactiveLinesFromShoppingSaleByBusinessPartnerId(salesOrder.getBusinessPartnerId());
                        if (result==null) {
                            reloadShoppingSalesList(user);
                        } else {
                            Toast.makeText(ShoppingSalesListActivity.this, result, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onListIsLoaded() {
        if (findViewById(R.id.shopping_sale_order_detail_container) != null) {
            if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        showOrHideEmptyLayoutWallpaper();
    }

    private void showOrHideEmptyLayoutWallpaper() {
        if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
            if (findViewById(R.id.shopping_sale_order_detail_container) != null) {
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.company_logo_name) != null) {
                findViewById(R.id.company_logo_name).setVisibility(View.GONE);
            }
        } else {
            if (findViewById(R.id.company_logo_name) != null) {
                findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.shopping_sale_order_detail_container) != null) {
                findViewById(R.id.shopping_sale_order_detail_container).setVisibility(View.GONE);
            }
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (findViewById(R.id.shopping_sale_order_detail_container) != null) {
            if (mListView!=null && mListView.getAdapter()!=null
                    && mListView.getAdapter().getCount()>selectedIndex) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void reloadShoppingSalesList(User user) {
        if (mListView!=null && mListView.getAdapter()!=null) {
            int oldListSize = mListView.getCount();
            int selectedIndex = mListView.getCheckedItemPosition();
            ((ShoppingSalesListAdapter) mListView.getAdapter())
                    .setData(new SalesOrderDB(this, user).getShoppingSalesList());
            if (findViewById(R.id.shopping_sale_order_detail_container) != null) {
                if (mListView.getCount() < oldListSize && !mListView.getAdapter().isEmpty()){
                    mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
                } else if (mListView.getCount() > selectedIndex) {
                    mListView.setSelection(selectedIndex);
                    mListView.setItemChecked(selectedIndex, true);
                }
            }
        }
        showOrHideEmptyLayoutWallpaper();
    }

}

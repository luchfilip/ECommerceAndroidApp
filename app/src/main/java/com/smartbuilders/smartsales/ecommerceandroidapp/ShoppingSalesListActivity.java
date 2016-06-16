package com.smartbuilders.smartsales.ecommerceandroidapp;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.ShoppingSalesListAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.SalesOrderLineDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco, before 15.06.2016
 */
public class ShoppingSalesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShoppingSalesListFragment.Callback,
        ShoppingSaleFragment.Callback {

    public static final String SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG =
            "SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG";
    private static final int SHOW_SALES_ORDER_DETAIL_REQUEST = 1;

    private User mCurrentUser;
    private boolean mTwoPane;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_sales_list);

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
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartner().getId());

            ShoppingSaleFragment fragment = new ShoppingSaleFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.shopping_sale_order_detail_container, fragment, SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, ShoppingSaleActivity.class);
            intent.putExtra(ShoppingSaleActivity.KEY_BUSINESS_PARTNER_ID, salesOrder.getBusinessPartner().getId());
            startActivityForResult(intent, SHOW_SALES_ORDER_DETAIL_REQUEST);
        }
    }

    @Override
    public void onItemLongSelected(final SalesOrder salesOrder) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.delete_shopping_sales_question,
                        salesOrder.getBusinessPartner().getCommercialName()))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String result = (new SalesOrderLineDB(ShoppingSalesListActivity.this, mCurrentUser))
                                .deactiveLinesFromShoppingSaleByBusinessPartnerId(salesOrder.getBusinessPartnerId());
                        if (result==null) {
                            reloadShoppingSalesList();
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
        if (mTwoPane) {
            if (mListView != null && mListView.getAdapter()!=null && mListView.getAdapter().getCount()>0) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (mTwoPane) {
            if (mListView!=null && mListView.getAdapter()!=null && mListView.getAdapter().getCount()>selectedIndex) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void reloadShoppingSalesList() {
        if (mListView!=null && mListView.getAdapter()!=null) {
            int oldListSize = mListView.getCount();
            int selectedIndex = mListView.getCheckedItemPosition();
            ((ShoppingSalesListAdapter) mListView.getAdapter())
                    .setData(new SalesOrderDB(this, mCurrentUser).getActiveShoppingSalesOrders());
            if (mTwoPane) {
                if (mListView.getCount()<oldListSize && mListView.getCount()>0) {
                    mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
                } else {
                    mListView.setSelection(selectedIndex);
                    mListView.setItemChecked(selectedIndex, true);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SHOW_SALES_ORDER_DETAIL_REQUEST) {
            if (resultCode == RESULT_OK) {
                reloadShoppingSalesList();
            }
        }
    }
}

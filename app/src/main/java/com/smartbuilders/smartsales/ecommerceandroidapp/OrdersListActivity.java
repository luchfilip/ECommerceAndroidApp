package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.adapters.SearchResultAdapter;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.ProductDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Product;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

public class OrdersListActivity extends AppCompatActivity
        implements OrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private boolean mTwoPane;
    private static final String ORDERDETAIL_FRAGMENT_TAG = "ORDERDETAIL_FRAGMENT_TAG";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ProductDB productDB;
    private ListView mListViewSearchResults;
    private SearchResultAdapter mSearchResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        if(findViewById(R.id.title_textView) != null){
            ((TextView) findViewById(R.id.title_textView))
                    .setTypeface(Typeface.createFromAsset(getAssets(), "MyriadPro-Bold.otf"));
        }

        try {
            if(findViewById(R.id.toolbar) != null) {
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.app_name);
                Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, true);
                setSupportActionBar(toolbar);

                toolbar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(OrdersListActivity.this,
                                MainActivity.class).putExtra(MainActivity.KEY_CURRENT_USER, mCurrentUser));
                    }
                });

                if(findViewById(R.id.drawer_layout) != null) {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();
                }
            }
            if(findViewById(R.id.nav_view) != null) {
                NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);
                mNavigationView.setNavigationItemSelectedListener(this);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        if(findViewById(R.id.order_detail_container) != null){
            // If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.order_detail_container, new OrderDetailFragment(),
                                ORDERDETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
        }

        productDB = new ProductDB(this, mCurrentUser);

        mSearchResultAdapter = new SearchResultAdapter(this, new ArrayList<Product>(), mCurrentUser);

        mListViewSearchResults = (ListView) findViewById(R.id.search_result_list);
        mListViewSearchResults.setAdapter(mSearchResultAdapter);
    }

    @Override
    public void onItemSelected(Order order) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putInt(OrderDetailActivity.KEY_ORDER_ID, order.getId());
            args.putParcelable(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment, ORDERDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(OrdersListActivity.this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER_ID, order.getId());
            intent.putExtra(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_shopping_cart) {
            Intent intent = new Intent(this, ShoppingCartActivity.class);
            intent.putExtra(ShoppingCartActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else if (id == R.id.nav_whish_list) {
            Intent intent = new Intent(this, WishListActivity.class);
            intent.putExtra(WishListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else if (id == R.id.nav_orders) {
            Intent intent = new Intent(this, OrdersListActivity.class);
            intent.putExtra(OrdersListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else/* if (id == R.id.nav_invoices_list) {
            Intent intent = new Intent(MainActivity.this, InvoicesListActivity.class);
            intent.putExtra(InvoicesListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else if (id == R.id.nav_statement_of_account) {
            Intent intent = new Intent(MainActivity.this, StatementOfAccountActivity.class);
            intent.putExtra(StatementOfAccountActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else*/ if (id == R.id.nav_share) {
            try{
                Utils.showPromptShareApp(this);
            }catch(Throwable e){
                e.printStackTrace();
            }
        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_report_error) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders_list, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(OrdersListActivity.this, ProductsListActivity.class);
                intent.putExtra(ProductsListActivity.KEY_CURRENT_USER, mCurrentUser);
                intent.putExtra(ProductsListActivity.KEY_PRODUCT_NAME, s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Some code here
                //Log.d(TAG, "onQueryTextChange("+s+")");
                mSearchResultAdapter.setData(productDB.getLightProductsByName(s), OrdersListActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Some code here
                //Log.d(TAG, "onMenuItemActionExpand(...)");
                mListViewSearchResults.setVisibility(View.VISIBLE);
                findViewById(R.id.orders_list).setVisibility(View.GONE);
                if(findViewById(R.id.title_textView) != null) {
                    findViewById(R.id.title_textView).setVisibility(View.GONE);
                }
                mSearchResultAdapter.setData(new ArrayList<Product>(), OrdersListActivity.this);
                mSearchResultAdapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Some code here
                //Log.d(TAG, "onMenuItemActionCollapse(...)");
                mListViewSearchResults.setVisibility(View.GONE);
                findViewById(R.id.orders_list).setVisibility(View.VISIBLE);
                if(findViewById(R.id.title_textView) != null) {
                    findViewById(R.id.title_textView).setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }

}

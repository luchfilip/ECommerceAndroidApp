package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco, 12.05.2016
 */
public class SalesOrdersListActivity extends AppCompatActivity
        implements SalesOrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private static final String SALES_ORDERDETAIL_FRAGMENT_TAG = "SALES_ORDERDETAIL_FRAGMENT_TAG";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private static final String STATE_CURRENT_SELECTED_ITEM_POSITION = "STATE_CURRENT_SELECTED_ITEM_POSITION";

    private boolean mTwoPane;
    private User mCurrentUser;
    private int mCurrentSelectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_orders_list);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_CURRENT_SELECTED_ITEM_POSITION)){
                mCurrentSelectedItemPosition = savedInstanceState.getInt(STATE_CURRENT_SELECTED_ITEM_POSITION);
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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        if(findViewById(R.id.nav_view) != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            try{
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                        .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        if(findViewById(R.id.sales_order_detail_container) != null){
            // If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.sales_order_detail_container, new SalesOrderDetailFragment(),
                                SALES_ORDERDETAIL_FRAGMENT_TAG).commit();
            }
        }else{
            mTwoPane = false;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.sales_orders_list);
            if (lv != null && lv.getAdapter().getCount() > mCurrentSelectedItemPosition
                    && mCurrentSelectedItemPosition != ListView.INVALID_POSITION) {
                lv.performItemClick(lv.getAdapter().getView(mCurrentSelectedItemPosition, null, null),
                        mCurrentSelectedItemPosition, mCurrentSelectedItemPosition);
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
                startActivity(new Intent(this, SearchResultsActivity.class)
                        .putExtra(SearchResultsActivity.KEY_CURRENT_USER, mCurrentUser));
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
        Utils.navigationItemSelectedBehave(item.getItemId(), this, mCurrentUser);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(Order order, int selectedItemPosition) {
        mCurrentSelectedItemPosition = selectedItemPosition;
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(SalesOrderDetailActivity.KEY_SALES_ORDER, order);
            args.putParcelable(SalesOrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);

            SalesOrderDetailFragment fragment = new SalesOrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.sales_order_detail_container, fragment, SALES_ORDERDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(SalesOrdersListActivity.this, SalesOrderDetailActivity.class);
            intent.putExtra(SalesOrderDetailActivity.KEY_SALES_ORDER, order);
            intent.putExtra(SalesOrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        outState.putInt(STATE_CURRENT_SELECTED_ITEM_POSITION, mCurrentSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }
}
package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
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
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco
 */
public class OrdersListActivity extends AppCompatActivity
        implements OrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private boolean mTwoPane;
    public static final String ORDERDETAIL_FRAGMENT_TAG = "ORDERDETAIL_FRAGMENT_TAG";

    private static final String STATE_CURRENT_SELECTED_ITEM_POSITION = "STATE_CURRENT_SELECTED_ITEM_POSITION";
    private User mCurrentUser;
    private int mCurrentSelectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

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

        mTwoPane = findViewById(R.id.order_detail_container) != null;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.orders_list);
            if (lv != null && lv.getAdapter()!=null
                    && lv.getAdapter().getCount() > mCurrentSelectedItemPosition
                    && mCurrentSelectedItemPosition != ListView.INVALID_POSITION) {
                lv.performItemClick(lv.getAdapter().getView(mCurrentSelectedItemPosition, null, null),
                        mCurrentSelectedItemPosition, mCurrentSelectedItemPosition);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView lv = (ListView) findViewById(R.id.orders_list);
        if(lv != null && (lv.getAdapter()==null || lv.getAdapter().getCount()==0)){
            findViewById(R.id.company_logo_name).setVisibility(View.VISIBLE);
            if(mTwoPane){
                findViewById(R.id.fragment_order_list).setVisibility(View.GONE);
                findViewById(R.id.order_detail_container).setVisibility(View.GONE);
            }else{
                findViewById(R.id.orders_list).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders_list, menu);
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
    public void onItemSelected(Order order, int selectedItemPosition) {
        mCurrentSelectedItemPosition = selectedItemPosition;
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(OrderDetailActivity.KEY_ORDER, order);

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment, ORDERDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER, order);
            startActivity(intent);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTED_ITEM_POSITION, mCurrentSelectedItemPosition);
        super.onSaveInstanceState(outState);
    }
}
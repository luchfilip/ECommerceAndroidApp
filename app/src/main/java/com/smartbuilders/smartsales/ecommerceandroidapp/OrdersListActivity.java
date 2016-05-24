package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.data.OrderDB;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

import java.util.ArrayList;

/**
 * Jesus Sarco
 */
public class OrdersListActivity extends AppCompatActivity
        implements OrdersListFragment.Callback, NavigationView.OnNavigationItemSelectedListener {

    private boolean mTwoPane;
    private static final String ORDERDETAIL_FRAGMENT_TAG = "ORDERDETAIL_FRAGMENT_TAG";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private ArrayList<Order> activeOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        if(savedInstanceState != null) {
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

        if(activeOrders == null){
            activeOrders = (new OrderDB(this, mCurrentUser)).getActiveOrders();
        }

        if(findViewById(R.id.order_detail_container) != null){
            // If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if(savedInstanceState == null){
                Bundle args = new Bundle();
                args.putParcelable(OrderDetailActivity.KEY_ORDER, null);
                args.putParcelable(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);

                OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
                orderDetailFragment.setArguments(args);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.order_detail_container, orderDetailFragment, ORDERDETAIL_FRAGMENT_TAG)
                        .commit();
            }
        }else{
            mTwoPane = false;
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
                startActivity(new Intent(this, SearchResultsActivity.class)
                        .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(Order order) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(OrderDetailActivity.KEY_ORDER, order);
            args.putParcelable(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);

            OrderDetailFragment fragment = new OrderDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_detail_container, fragment, ORDERDETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(OrdersListActivity.this, OrderDetailActivity.class);
            intent.putExtra(OrderDetailActivity.KEY_ORDER, order);
            intent.putExtra(OrderDetailActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public ArrayList<Order> getActiveOrders(User user) {
        if(activeOrders==null){
            activeOrders = (new OrderDB(this, user)).getActiveOrders();
        }
        return activeOrders;
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }
}
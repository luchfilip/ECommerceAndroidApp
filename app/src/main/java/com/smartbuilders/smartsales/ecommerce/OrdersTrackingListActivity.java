package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.smartbuilders.smartsales.ecommerce.model.Order;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 * Created by Jesus Sarco, 19.10.2016
 */
public class OrdersTrackingListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OrdersTrackingListFragment.Callback {

    public static final String ORDER_TRACKING_DETAIL_FRAGMENT_TAG = "ORDER_TRACKING_DETAIL_FRAGMENT_TAG";

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_tracking_list);
        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, user);

        mListView = (ListView) findViewById(R.id.orders_tracking_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders_tracking_list, menu);
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
    public void onListIsLoaded() {
        if (findViewById(R.id.order_tracking_detail_container) != null) {
            if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
                mListView.performItemClick(mListView.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        showOrHideEmptyLayoutWallpaper();
    }

    private void showOrHideEmptyLayoutWallpaper() {
        if (mListView != null && mListView.getAdapter()!=null && !mListView.getAdapter().isEmpty()) {
            if (findViewById(R.id.order_tracking_detail_container) != null) {
                findViewById(R.id.order_tracking_detail_container).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
            }
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.GONE);
            }
        } else {
            if (findViewById(R.id.order_tracking_detail_container) != null) {
                findViewById(R.id.order_tracking_detail_container).setVisibility(View.GONE);
            }
            if (findViewById(R.id.main_layout) != null) {
                findViewById(R.id.main_layout).setVisibility(View.GONE);
            }
            if (findViewById(R.id.empty_layout_wallpaper) != null) {
                findViewById(R.id.empty_layout_wallpaper).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setSelectedIndex(int selectedIndex) {
        if (findViewById(R.id.order_tracking_detail_container) != null) {
            if (mListView!=null && mListView.getAdapter()!=null && mListView.getAdapter().getCount()>selectedIndex) {
                mListView.setSelection(selectedIndex);
                mListView.setItemChecked(selectedIndex, true);
            }
        }
    }

    @Override
    public void onItemSelected(Order order) {
        if(findViewById(R.id.order_tracking_detail_container) != null){
            Bundle args = new Bundle();
            args.putInt(OrderTrackingDetailActivity.KEY_ORDER_ID, order.getId());

            OrderTrackingDetailFragment fragment = new OrderTrackingDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.order_tracking_detail_container, fragment, ORDER_TRACKING_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, OrderTrackingDetailActivity.class);
            intent.putExtra(OrderTrackingDetailActivity.KEY_ORDER_ID, order.getId());
            startActivity(intent);
        }
    }
}

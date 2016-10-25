package com.smartbuilders.smartsales.salesforcesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.smartbuilders.smartsales.ecommerce.R;

import com.smartbuilders.smartsales.ecommerce.utils.Utils;
import com.smartbuilders.synchronizer.ids.model.User;

/**
 *
 */
public class PricesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, PricesListFragment.Callback {

    private static final String PRICES_LIST_DETAILS_FRAGMENT_TAG = "PRICES_LIST_DETAILS_FRAGMENT_TAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prices_list);
        final User user = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, user);

        mTwoPane = findViewById(R.id.price_list_detail_container) != null;
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
    public void onItemSelected(int productCategoryId) {
        if(mTwoPane){
            Bundle args = new Bundle();
            PriceListDetailActivityFragment fragment = new PriceListDetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.price_list_detail_container, fragment, PRICES_LIST_DETAILS_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this, PriceListDetailActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPricesListIsLoaded() {

    }

    @Override
    public void setSelectedIndex(int selectedIndex) {

    }
}

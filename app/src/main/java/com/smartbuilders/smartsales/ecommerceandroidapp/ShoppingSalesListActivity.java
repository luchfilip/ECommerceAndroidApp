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
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.febeca.R;
import com.smartbuilders.smartsales.ecommerceandroidapp.model.SalesOrder;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class ShoppingSalesListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShoppingSalesListFragment.Callback {

    public static final String SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG = "SHOPPING_SALES_ORDER_DETAIL_FRAGMENT_TAG";

    private User mCurrentUser;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_sales_list);

        if(findViewById(R.id.title_textView) != null){
            ((TextView) findViewById(R.id.title_textView))
                    .setTypeface(Typeface.createFromAsset(getAssets(), "MyriadPro-Bold.otf"));
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

        mTwoPane = findViewById(R.id.shopping_sale_order_detail_container)!=null;

    }

    @Override
    protected void onResume() {
        if (mTwoPane) {
            ListView lv = (ListView) findViewById(R.id.shopping_sales_orders_list);
            if (lv != null && lv.getAdapter()!=null && lv.getAdapter().getCount()>0) {
                lv.performItemClick(lv.getAdapter().getView(0, null, null), 0, 0);
            }
        }
        super.onResume();
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
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongSelected(SalesOrder salesOrder, int selectedItemPosition) {
        if(mTwoPane) {

        } else {

        }
    }
}

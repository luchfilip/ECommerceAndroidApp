package com.smartbuilders.smartsales.ecommerceandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jasgcorp.ids.model.User;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

public class FilterOptionsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    private static final String STATE_CURRENT_USER = "state_current_user";
    private User mCurrentUser;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_options);

        if( savedInstanceState != null ) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
        }

        ((Button) findViewById(R.id.search_by_category_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(FilterOptionsActivity.this,
                                CategoriesListActivity.class).putExtra(CategoriesListActivity.KEY_CURRENT_USER, mCurrentUser));
                        finish();
                    }
                });

        ((Button) findViewById(R.id.search_by_brand_button))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(FilterOptionsActivity.this,
                                BrandsListActivity.class).putExtra(BrandsListActivity.KEY_CURRENT_USER, mCurrentUser));
                        finish();
                    }
                });
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
        } /*else if (id == R.id.nav_orders) {
            Intent intent = new Intent(MainActivity.this, OrdersListActivity.class);
            intent.putExtra(OrdersListActivity.KEY_CURRENT_USER, mCurrentUser);
            startActivity(intent);
        } else if (id == R.id.nav_invoices_list) {
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_CURRENT_USER, mCurrentUser);
        super.onSaveInstanceState(outState);
    }

}

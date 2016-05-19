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
import com.smartbuilders.smartsales.ecommerceandroidapp.model.Order;
import com.smartbuilders.smartsales.ecommerceandroidapp.utils.Utils;

/**
 * Jesus Sarco, 12.05.2016
 */
public class OrderDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_ORDER = "key_order";
    public static final String KEY_CURRENT_USER = "KEY_CURRENT_USER";
    public static final String STATE_CURRENT_USER = "state_current_user";
    public static final String STATE_ORDER = "state_order";

    private User mCurrentUser;
    private Order mOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_CURRENT_USER)){
                mCurrentUser = savedInstanceState.getParcelable(STATE_CURRENT_USER);
            }
            if(savedInstanceState.containsKey(STATE_ORDER)){
                mOrder = savedInstanceState.getParcelable(STATE_ORDER);
            }
        }

        if(getIntent()!=null && getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey(KEY_CURRENT_USER)){
                mCurrentUser = getIntent().getExtras().getParcelable(KEY_CURRENT_USER);
            }
            if(getIntent().getExtras().containsKey(KEY_ORDER)){
                mOrder = getIntent().getExtras().getParcelable(KEY_ORDER);
            }
        }

        if(findViewById(R.id.title_textView) != null){
            ((TextView) findViewById(R.id.title_textView))
                    .setTypeface(Typeface.createFromAsset(getAssets(), "MyriadPro-Bold.otf"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.app_name);
        Utils.setCustomToolbarTitle(this, toolbar, mCurrentUser, true);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        try{
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name))
                    .setText(getString(R.string.welcome_user, mCurrentUser.getUserName()));
        }catch(Exception e){
            e.printStackTrace();
        }

        //if(findViewById(R.id.search_bar_linear_layout)!=null){
        //    findViewById(R.id.search_by_button).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(OrderDetailActivity.this, FilterOptionsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //
        //    findViewById(R.id.search_product_editText).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(OrderDetailActivity.this, SearchResultsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //
        //    findViewById(R.id.image_search_bar_layout).setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            startActivity(new Intent(OrderDetailActivity.this, SearchResultsActivity.class)
        //                    .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
        //        }
        //    });
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                goBack();
                return true;
            case R.id.search:
                startActivity(new Intent(this, SearchResultsActivity.class)
                        .putExtra(FilterOptionsActivity.KEY_CURRENT_USER, mCurrentUser));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goBack();
    }

    private void goBack(){
        Intent intent = new Intent(this, OrdersListActivity.class);
        intent.putExtra(OrdersListActivity.KEY_CURRENT_USER, mCurrentUser);
        startActivity(intent);
        finish();
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
        outState.putParcelable(STATE_ORDER, mOrder);
        super.onSaveInstanceState(outState);
    }
}
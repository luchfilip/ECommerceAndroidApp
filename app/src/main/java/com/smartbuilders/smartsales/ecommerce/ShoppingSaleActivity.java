package com.smartbuilders.smartsales.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smartbuilders.smartsales.ecommerce.data.BusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.data.UserBusinessPartnerDB;
import com.smartbuilders.smartsales.ecommerce.model.BusinessPartner;
import com.smartbuilders.synchronizer.ids.model.User;
import com.smartbuilders.smartsales.ecommerce.utils.Utils;

/**
 * Jesus Sarco 12.05.2016
 */
public class ShoppingSaleActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ShoppingSaleFragment.Callback {

    public static final String KEY_USER_BUSINESS_PARTNER_ID = "KEY_USER_BUSINESS_PARTNER_ID";
    private static final String STATE_USER_BUSINESS_PARTNER_ID = "STATE_USER_BUSINESS_PARTNER_ID";

    private int mUserBusinessPartnerId;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_sale);

        mUser = Utils.getCurrentUser(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Utils.setCustomToolbarTitle(this, toolbar, true);
        setSupportActionBar(toolbar);

        Utils.inflateNavigationView(this, this, toolbar, mUser);

        if (getIntent()!=null && getIntent().getExtras()!=null) {
            if (getIntent().getExtras().containsKey(KEY_USER_BUSINESS_PARTNER_ID)) {
                mUserBusinessPartnerId = getIntent().getExtras().getInt(KEY_USER_BUSINESS_PARTNER_ID);
            }
        }

        if (savedInstanceState!=null) {
            if (savedInstanceState.containsKey(STATE_USER_BUSINESS_PARTNER_ID)) {
                mUserBusinessPartnerId = savedInstanceState.getInt(STATE_USER_BUSINESS_PARTNER_ID);
            }
        }
    }

    @Override
    protected void onStart() {
        BusinessPartner businessPartner = null;
        if(mUserBusinessPartnerId !=0) {
            businessPartner = (new UserBusinessPartnerDB(this, mUser))
                    .getUserBusinessPartnerById(mUserBusinessPartnerId);
        } else {
            try {
                businessPartner = (new BusinessPartnerDB(this, mUser))
                        .getBusinessPartnerById(Utils.getAppCurrentBusinessPartnerId(this, mUser));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(businessPartner!=null){
            ((TextView) findViewById(R.id.business_partner_name)).setText(businessPartner.getName());
            findViewById(R.id.business_partner_name_container).setVisibility(View.VISIBLE);
        }
        super.onStart();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shopping_sale, menu);
        return true;
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer!=null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Utils.navigationItemSelectedBehave(item.getItemId(), this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawer!=null){
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void reloadShoppingSalesList(User user) {
        //do nothing
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putInt(STATE_USER_BUSINESS_PARTNER_ID, mUserBusinessPartnerId);
        super.onSaveInstanceState(outState, outPersistentState);
    }
}
